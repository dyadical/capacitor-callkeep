package com.dyadical.capcallkeep;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;

import java.util.HashMap;
import java.util.function.BiFunction;

import static com.dyadical.capcallkeep.Constants.ACTION_ANSWER_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_AUDIO_SESSION;
import static com.dyadical.capcallkeep.Constants.ACTION_CHECK_REACHABILITY;
import static com.dyadical.capcallkeep.Constants.ACTION_DTMF_TONE;
import static com.dyadical.capcallkeep.Constants.ACTION_END_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_HOLD_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_MUTE_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_ONGOING_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_ON_SILENCE_INCOMING_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_SHOW_INCOMING_CALL_UI;
import static com.dyadical.capcallkeep.Constants.ACTION_UNHOLD_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_UNMUTE_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_WAKE_APP;
import static com.dyadical.capcallkeep.Constants.EXTRA_CALLER_NAME;
import static com.dyadical.capcallkeep.Constants.EXTRA_CALL_NUMBER;
import static com.dyadical.capcallkeep.Constants.EXTRA_CALL_UUID;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CapCallKeep {
    private JSObject _settings;
    private boolean isReceiverRegistered = false;
    public static final int REQUEST_READ_PHONE_STATE = 1337;
    public static final int REQUEST_REGISTER_CALL_PROVIDER = 394859;

    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
    private static final String REACT_NATIVE_MODULE_NAME = "CapCallKeep";
    private VoiceBroadcastReceiver voiceBroadcastReceiver;
    private static final String TAG = "CapCallKeepPlugin";
    public static TelecomManager telecomManager;
    public static TelephonyManager telephonyManager;
    public static PhoneAccountHandle handle;

    private boolean isSelfManaged() {
        try {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && _settings.has("selfManaged") && _settings.getBoolean("selfManaged");
        } catch (Exception e) {
            return false;
        }
    }

    public void setupAndroid(JSObject data, Context context) {
        VoiceConnectionService.setAvailable(false);
        VoiceConnectionService.setInitialized(true);
        this._settings = data;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isSelfManaged()) {
                Log.d(TAG, "[VoiceConnection] API Version supports self managed, and is enabled in setup");
            } else {
                Log.d(TAG, "[VoiceConnection] API Version supports self managed, but it is not enabled in setup");
            }
        }

        // If we're running in self managed mode we need fewer permissions.
        // TODO
        // if (isSelfManaged()) {
        // Log.d(TAG, "[VoiceConnection] setup, adding RECORD_AUDIO in permissions in
        // self managed");
        // permissions = new String[] { Manifest.permission.RECORD_AUDIO };
        // }

        if (isConnectionServiceAvailable()) {
            Log.i(TAG, "registering phone account");
            this.registerPhoneAccount(context);
            this.registerEvents(context);
            VoiceConnectionService.setAvailable(true);
            Log.d(TAG, "isEnabled:" + telecomManager.getPhoneAccount(handle).isEnabled());
        } else {
            Log.w(TAG, "connection service not available");
        }

        VoiceConnectionService.setSettings(this._settings);
    }

    public void registerEvents(Context context) {
        // TODO: test this function.
        //  registerEvents may expect/need the getContext() instead of getContext().getApplicationContext() which it currently sometimes receives.
        //  see commit https://github.com/dyadical/capacitor-callkeep/pull/4/commits/4e8f4acb43698decd5442d607fd36a5d4761272b
        if (!isConnectionServiceAvailable()) {
            Log.w(TAG, "[VoiceConnection] registerEvents ignored due to no ConnectionService");
            return;
        }

        Log.d(TAG, "[VoiceConnection] registerEvents");

        voiceBroadcastReceiver = new CapCallKeep.VoiceBroadcastReceiver();
        registerReceiver(context);
        VoiceConnectionService.setPhoneAccountHandle(handle);
    }

    public Boolean displayIncomingCall(String uuid, String number, String callerName) {
        Log.i(TAG, "displayIncomingCall()");
        if (!isConnectionServiceAvailable() || !hasPhoneAccount()) {
            Log.w(TAG, "doDisplayIncomingCall ignored due to no ConnectionService or no phone account");
            Boolean succeeded = false;
            return succeeded;
        }
        Bundle extras = new Bundle();
        Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, number, null);

        extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri);
        extras.putString(EXTRA_CALLER_NAME, callerName);
        extras.putString(EXTRA_CALL_UUID, uuid);

        telecomManager.addNewIncomingCall(handle, extras);
        Boolean succeeded = true;
        return succeeded;
    }


    public static Boolean isConnectionServiceAvailable() {
        // PhoneAccount is available since api level 23
        return Build.VERSION.SDK_INT >= 23;
    }

    private void initializeTelecomManager(Context context) {
        ComponentName cName = new ComponentName(context, VoiceConnectionService.class);
        String appName = this.getApplicationName(context);

        handle = new PhoneAccountHandle(cName, appName);
        telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
    }

    public void registerPhoneAccount(Context appContext) {
        if (!isConnectionServiceAvailable()) {
            Log.w(TAG, "doRegisterPhoneAccount ignored due to no ConnectionService");
            return;
        }

        this.initializeTelecomManager(appContext);
        String appName = this.getApplicationName(appContext);

        PhoneAccount.Builder builder = new PhoneAccount.Builder(handle, appName);
        if (isSelfManaged()) {
            builder.setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED);
        } else {
            builder.setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER);
        }

        if (_settings != null && _settings.has("imageName")) {
            int identifier = appContext
                    .getResources()
                    .getIdentifier(_settings.getString("imageName"), "drawable", appContext.getPackageName());
            Icon icon = Icon.createWithResource(appContext, identifier);
            builder.setIcon(icon);
        }

        PhoneAccount account = builder.build();

        telephonyManager = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);

        telecomManager.registerPhoneAccount(account);
    }

    private String getApplicationName(Context appContext) {
        ApplicationInfo applicationInfo = appContext.getApplicationInfo();
        int stringId = applicationInfo.labelRes;

        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : appContext.getString(stringId);
    }


    public static boolean hasPhoneAccount() {
        return (
                isConnectionServiceAvailable() &&
                        telecomManager != null &&
                        telecomManager.getPhoneAccount(handle) != null &&
                        telecomManager.getPhoneAccount(handle).isEnabled()
        );
    }

    private void registerReceiver(Context context) {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_END_CALL);
            intentFilter.addAction(ACTION_ANSWER_CALL);
            intentFilter.addAction(ACTION_MUTE_CALL);
            intentFilter.addAction(ACTION_UNMUTE_CALL);
            intentFilter.addAction(ACTION_DTMF_TONE);
            intentFilter.addAction(ACTION_UNHOLD_CALL);
            intentFilter.addAction(ACTION_HOLD_CALL);
            intentFilter.addAction(ACTION_ONGOING_CALL);
            intentFilter.addAction(ACTION_AUDIO_SESSION);
            intentFilter.addAction(ACTION_CHECK_REACHABILITY);
            intentFilter.addAction(ACTION_SHOW_INCOMING_CALL_UI);
            intentFilter.addAction(ACTION_ON_SILENCE_INCOMING_CALL);

            LocalBroadcastManager.getInstance(context).registerReceiver(voiceBroadcastReceiver, intentFilter);
            isReceiverRegistered = true;
        }
    }

    private class VoiceBroadcastReceiver extends BroadcastReceiver {

        private BiFunction<String, JSObject, Void> notifyListeners;
        public VoiceBroadcastReceiver(BiFunction<String, JSObject, Void> notifyListeners) {
            this.notifyListeners = notifyListeners;
        }
        private void notifyListeners(String s, JSObject j) {
            this.notifyListeners.apply(s, j);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            JSObject args = new JSObject();
            HashMap<String, String> attributeMap = (HashMap<String, String>) intent.getSerializableExtra("attributeMap");

            switch (intent.getAction()) {
                case ACTION_END_CALL:
                    args.put("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    notifyListeners("endCall", args);
                    break;
                case ACTION_ANSWER_CALL:
                    args.put("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    notifyListeners("answerCall", args);
                    break;
                case ACTION_HOLD_CALL:
                    args.put("hold", true);
                    args.put("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    notifyListeners("toggleHold", args);
                    break;
                case ACTION_UNHOLD_CALL:
                    args.put("hold", false);
                    args.put("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    notifyListeners("toggleHold", args);
                    break;
                case ACTION_MUTE_CALL:
                    args.put("muted", true);
                    args.put("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    notifyListeners("setMutedCall", args);
                    break;
                case ACTION_UNMUTE_CALL:
                    args.put("muted", false);
                    args.put("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    notifyListeners("setMutedCall", args);
                    break;
                case ACTION_DTMF_TONE:
                    args.put("digits", attributeMap.get("DTMF"));
                    args.put("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    notifyListeners("DTMFAction", args);
                    break;
                case ACTION_ONGOING_CALL:
                    args.put("handle", attributeMap.get(EXTRA_CALL_NUMBER));
                    args.put("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    args.put("name", attributeMap.get(EXTRA_CALLER_NAME));
                    notifyListeners("startCall", args);
                    break;
                case ACTION_AUDIO_SESSION:
                    notifyListeners("activateAudioSession", null);
                    break;
                case ACTION_CHECK_REACHABILITY:
                    notifyListeners("checkReachability", null);
                    break;
                case ACTION_SHOW_INCOMING_CALL_UI:
                    args.put("handle", attributeMap.get(EXTRA_CALL_NUMBER));
                    args.put("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    args.put("name", attributeMap.get(EXTRA_CALLER_NAME));
                    notifyListeners("showIncomingCallUi", args);
                    break;
                case ACTION_WAKE_APP:
                    Log.w(TAG, "ACTION_WAKE_UP NOT IMPLEMENTED");
                    // TODO
                    // Intent headlessIntent = new Intent(getContext(),
                    // CallKeepBackgroundMessagingService.class);
                    // headlessIntent.putExtra("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    // headlessIntent.putExtra("name", attributeMap.get(EXTRA_CALLER_NAME));
                    // headlessIntent.putExtra("handle", attributeMap.get(EXTRA_CALL_NUMBER));
                    // Log.d(
                    // TAG,
                    // "[VoiceConnection] wakeUpApplication: " +
                    // attributeMap.get(EXTRA_CALL_UUID) +
                    // ", number : " +
                    // attributeMap.get(EXTRA_CALL_NUMBER) +
                    // ", displayName:" +
                    // attributeMap.get(EXTRA_CALLER_NAME)
                    // );
                    //
                    // ComponentName name = getContext().startService(headlessIntent);
                    // if (name != null) {
                    // // TODO:
                    // // HeadlessJsTaskService.acquireWakeLockNow(getContext());
                    // }
                    break;
                case ACTION_ON_SILENCE_INCOMING_CALL:
                    args.put("handle", attributeMap.get(EXTRA_CALL_NUMBER));
                    args.put("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    args.put("name", attributeMap.get(EXTRA_CALLER_NAME));
                    notifyListeners("silenceIncomingCall", args);
                    break;
            }
        }
    }

}
