package com.dyadical.capcallkeep;

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

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.getcapacitor.Bridge;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginHandle;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.google.firebase.messaging.RemoteMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
@CapacitorPlugin(
    name = "CapCallKeep",
    permissions = {
        @Permission(alias = "readPhoneNumbers", strings = { Manifest.permission.READ_PHONE_NUMBERS }),
        @Permission(alias = "readPhoneState", strings = { Manifest.permission.READ_PHONE_STATE }),
        @Permission(alias = "callPhone", strings = { Manifest.permission.CALL_PHONE }),
        @Permission(alias = "recordAudio", strings = { Manifest.permission.RECORD_AUDIO }),
        @Permission(alias = "manageOwnCalls", strings = { Manifest.permission.MANAGE_OWN_CALLS })
    }
)
public class CapCallKeepPlugin extends Plugin {

    private String[] permissions = new String[] { "readPhoneNumbers", "readPhoneState", "manageOwnCalls", "callPhone", "recordAudio" };

    public static final int REQUEST_READ_PHONE_STATE = 1337;
    public static final int REQUEST_REGISTER_CALL_PROVIDER = 394859;

    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
    private static final String REACT_NATIVE_MODULE_NAME = "CapCallKeep";

    private static final String TAG = "CapCallKeep";
    private static TelecomManager telecomManager;
    private static TelephonyManager telephonyManager;
    public static PhoneAccountHandle handle;
    private boolean isReceiverRegistered = false;

    private VoiceBroadcastReceiver voiceBroadcastReceiver;
    private JSObject _settings;
    public static Bridge staticBridge = null;

    public void load() {
        Log.d(TAG, "load()");
        staticBridge = this.bridge;
        // messagingService = new MessagingService();
    }

    // public RNCallKeepModule(ReactApplicationContext reactContext) {
    // super(reactContext);
    // Log.d(TAG, "[VoiceConnection] constructor");
    //
    // this.reactContext = reactContext;
    // }

    private boolean isSelfManaged() {
        try {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && _settings.has("selfManaged") && _settings.getBoolean("selfManaged");
        } catch (Exception e) {
            return false;
        }
    }

    // @Override
    // public String getName() {
    // return REACT_NATIVE_MODULE_NAME;
    // }

    @PluginMethod
    public void setupAndroid(PluginCall call) {
        Log.d(TAG, "[VoiceConnection] setup");
        JSObject data = call.getData();
        setupAndroid(data, getContext().getApplicationContext());
        call.resolve();
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

    @PluginMethod
    public void registerPhoneAccount(PluginCall call) {
        if (!isConnectionServiceAvailable()) {
            Log.w(TAG, "[VoiceConnection] registerPhoneAccount ignored due to no ConnectionService");
            return;
        }

        Log.d(TAG, "[VoiceConnection] registerPhoneAccount");

        registerPhoneAccount(getAppContext());
        call.resolve();
    }

    @PluginMethod
    public void registerEvents() {
        registerEvents(getContext());
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

        voiceBroadcastReceiver = new VoiceBroadcastReceiver();
        registerReceiver(context);
        VoiceConnectionService.setPhoneAccountHandle(handle);
    }

    @PluginMethod
    public void displayIncomingCall(PluginCall call) {
        Log.d(TAG, "displayIncomingCall()");
        if (!call.hasOption("uuid")) {
            call.reject("missing key 'uuid'");
            return;
        }
        String uuid = call.getString("uuid");
        if (!call.hasOption("number")) {
            call.reject("missing key 'number'");
            return;
        }
        String number = call.getString("number");
        if (!call.hasOption("callerName")) {
            call.reject("missing key 'callerName'");
            return;
        }
        String callerName = call.getString("callerName");

        Boolean succeeded = displayIncomingCall(uuid, number, callerName);
        if (!succeeded) {
            call.reject("no ConnectionService or no phone account");
            return;
        }
        call.resolve();
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

    @PluginMethod
    public void answerIncomingCall(PluginCall call) {
        String uuid = call.getString("uuid");
        Log.d(TAG, "[VoiceConnection] answerIncomingCall, uuid: " + uuid);
        if (!isConnectionServiceAvailable() || !hasPhoneAccount()) {
            Log.w(TAG, "[VoiceConnection] answerIncomingCall ignored due to no ConnectionService or no phone account");
            return;
        }

        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            Log.w(TAG, "[VoiceConnection] answerIncomingCall ignored because no connection found, uuid: " + uuid);
            return;
        }

        conn.onAnswer();
        call.resolve();
    }

    @PluginMethod
    public void startCall(PluginCall call) {
        String uuid = call.getString("uuid");
        String number = call.getString("number");
        String callerName = call.getString("callerName");
        Log.d(TAG, "[VoiceConnection] startCall called, uuid: " + uuid + ", number: " + number + ", callerName: " + callerName);

        if (!isConnectionServiceAvailable() || !hasPhoneAccount() || !hasPermissions() || number == null) {
            Log.w(
                TAG,
                "[VoiceConnection] startCall ignored: " +
                isConnectionServiceAvailable() +
                ", " +
                hasPhoneAccount() +
                ", " +
                hasPermissions() +
                ", " +
                number
            );
            return;
        }

        Bundle extras = new Bundle();
        Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, number, null);

        Bundle callExtras = new Bundle();
        callExtras.putString(EXTRA_CALLER_NAME, callerName);
        callExtras.putString(EXTRA_CALL_UUID, uuid);
        callExtras.putString(EXTRA_CALL_NUMBER, number);

        extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, handle);
        extras.putParcelable(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, callExtras);

        Log.d(TAG, "[VoiceConnection] startCall, uuid: " + uuid);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            call.reject("no CALL_PHONE permissions");
            return;
        }
        telecomManager.placeCall(uri, extras);
        call.resolve();
    }

    @PluginMethod
    public void endCall(PluginCall call) {
        String uuid = call.getString("uuid");
        Log.d(TAG, "[VoiceConnection] endCall called, uuid: " + uuid);
        if (!isConnectionServiceAvailable() || !hasPhoneAccount()) {
            Log.w(TAG, "[VoiceConnection] endCall ignored due to no ConnectionService or no phone account");
            return;
        }

        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            Log.w(TAG, "[VoiceConnection] endCall ignored because no connection found, uuid: " + uuid);
            return;
        }
        conn.onDisconnect();

        Log.d(TAG, "[VoiceConnection] endCall executed, uuid: " + uuid);
        call.resolve();
    }

    @PluginMethod
    public void endAllCalls() {
        Log.d(TAG, "[VoiceConnection] endAllCalls called");
        if (!isConnectionServiceAvailable() || !hasPhoneAccount()) {
            Log.w(TAG, "[VoiceConnection] endAllCalls ignored due to no ConnectionService or no phone account");
            return;
        }

        Map<String, VoiceConnection> currentConnections = VoiceConnectionService.currentConnections;
        for (Map.Entry<String, VoiceConnection> connectionEntry : currentConnections.entrySet()) {
            Connection connectionToEnd = connectionEntry.getValue();
            connectionToEnd.onDisconnect();
        }

        Log.d(TAG, "[VoiceConnection] endAllCalls executed");
    }

    @PermissionCallback
    public void checkPhoneAccountPermission(PluginCall call) {
        // ReadableArray optionalPermissions, Promise promise

        if (!isConnectionServiceAvailable()) {
            call.reject("ConnectionService not available for this version of Android.");
            return;
        }
        if (getActivity() == null) {
            call.reject("Activity doesn't exist");
            return;
        }
        for (String permission : permissions) {
            if (getPermissionState(permission) != PermissionState.GRANTED) {
                call.reject(String.format("permission not granted for %s", permission));
                return;
            }
        }
        call.resolve();
    }

    @PluginMethod
    public void checkDefaultPhoneAccount(PluginCall call) {
        JSObject ret = new JSObject();
        if (!isConnectionServiceAvailable() || !hasPhoneAccount()) {
            ret.put("value", true);
            call.resolve(ret);
            return;
        }

        if (!Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
            ret.put("value", true);
            call.resolve(ret);
            return;
        }

        boolean hasSim = telephonyManager.getSimState() != TelephonyManager.SIM_STATE_ABSENT;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            call.reject("no READ_PHONE_STATE permission");
            return;
        }
        boolean hasDefaultAccount = telecomManager.getDefaultOutgoingPhoneAccount("tel") != null;

        ret.put("value", !hasSim || hasDefaultAccount);
        call.resolve(ret);
    }

    @PluginMethod
    public void setOnHold(String uuid, boolean shouldHold) {
        Log.d(TAG, "[VoiceConnection] setOnHold, uuid: " + uuid + ", shouldHold: " + (shouldHold ? "true" : "false"));

        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            Log.w(TAG, "[VoiceConnection] setOnHold ignored because no connection found, uuid: " + uuid);
            return;
        }

        if (shouldHold == true) {
            conn.onHold();
        } else {
            conn.onUnhold();
        }
    }

    @PluginMethod
    public void reportEndCallWithUUID(String uuid, int reason) {
        Log.d(TAG, "[VoiceConnection] reportEndCallWithUUID, uuid: " + uuid + ", reason: " + reason);
        if (!isConnectionServiceAvailable() || !hasPhoneAccount()) {
            return;
        }

        VoiceConnection conn = (VoiceConnection) VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            Log.w(TAG, "[VoiceConnection] reportEndCallWithUUID ignored because no connection found, uuid: " + uuid);
            return;
        }
        conn.reportDisconnect(reason);
    }

    @PluginMethod
    public void rejectCall(PluginCall call) {
        String uuid = call.getString("uuid");
        Log.d(TAG, "[VoiceConnection] rejectCall, uuid: " + uuid);
        if (!isConnectionServiceAvailable() || !hasPhoneAccount()) {
            Log.w(TAG, "[VoiceConnection] endAllCalls ignored due to no ConnectionService or no phone account");
            return;
        }

        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            Log.w(TAG, "[VoiceConnection] rejectCall ignored because no connection found, uuid: " + uuid);
            return;
        }

        conn.onReject();
        call.resolve();
    }

    @PluginMethod
    public void setMutedCall(String uuid, boolean shouldMute) {
        Log.d(TAG, "[VoiceConnection] setMutedCall, uuid: " + uuid + ", shouldMute: " + (shouldMute ? "true" : "false"));
        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            Log.w(TAG, "[VoiceConnection] setMutedCall ignored because no connection found, uuid: " + uuid);
            return;
        }

        CallAudioState newAudioState = null;
        // if the requester wants to mute, do that. otherwise unmute
        if (shouldMute) {
            newAudioState = new CallAudioState(true, conn.getCallAudioState().getRoute(), conn.getCallAudioState().getSupportedRouteMask());
        } else {
            newAudioState =
                new CallAudioState(false, conn.getCallAudioState().getRoute(), conn.getCallAudioState().getSupportedRouteMask());
        }
        conn.onCallAudioStateChanged(newAudioState);
    }

    /**
     * toggle audio route for speaker via connection service function
     *
     * @param uuid
     * @param routeSpeaker
     */
    @PluginMethod
    public void toggleAudioRouteSpeaker(String uuid, boolean routeSpeaker) {
        Log.d(TAG, "[VoiceConnection] toggleAudioRouteSpeaker, uuid: " + uuid + ", routeSpeaker: " + (routeSpeaker ? "true" : "false"));
        VoiceConnection conn = (VoiceConnection) VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            Log.w(TAG, "[VoiceConnection] toggleAudioRouteSpeaker ignored because no connection found, uuid: " + uuid);
            return;
        }
        if (routeSpeaker) {
            conn.setAudioRoute(CallAudioState.ROUTE_SPEAKER);
        } else {
            conn.setAudioRoute(CallAudioState.ROUTE_EARPIECE);
        }
    }

    @PluginMethod
    public void setAudioRoute(PluginCall call) {
        String uuid = call.getString("uuid");
        String audioRoute = call.getString("audioRoute");
        try {
            VoiceConnection conn = (VoiceConnection) VoiceConnectionService.getConnection(uuid);
            if (conn == null) {
                return;
            }
            if (audioRoute.equals("Bluetooth")) {
                Log.d(TAG, "[VoiceConnection] setting audio route: Bluetooth");
                conn.setAudioRoute(CallAudioState.ROUTE_BLUETOOTH);
                call.resolve();
                return;
            }
            if (audioRoute.equals("Headset")) {
                Log.d(TAG, "[VoiceConnection] setting audio route: Headset");
                conn.setAudioRoute(CallAudioState.ROUTE_WIRED_HEADSET);
                call.resolve();
                return;
            }
            if (audioRoute.equals("Speaker")) {
                Log.d(TAG, "[VoiceConnection] setting audio route: Speaker");
                conn.setAudioRoute(CallAudioState.ROUTE_SPEAKER);
                call.resolve();
                return;
            }
            Log.d(TAG, "[VoiceConnection] setting audio route: Wired/Earpiece");
            conn.setAudioRoute(CallAudioState.ROUTE_WIRED_OR_EARPIECE);
            call.resolve();
        } catch (Exception e) {
            call.reject("SetAudioRoute", e.getMessage());
        }
    }

    @PluginMethod
    public void getAudioRoutes(PluginCall call) {
        try {
            Context context = this.getAppContext();
            AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
            JSArray devices = new JSArray();
            ArrayList<String> typeChecker = new ArrayList<>();
            AudioDeviceInfo[] audioDeviceInfo = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS + AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo device : audioDeviceInfo) {
                String type = getAudioRouteType(device.getType());
                if (type != null && !typeChecker.contains(type)) {
                    JSObject deviceInfo = new JSObject();
                    deviceInfo.put("name", type);
                    deviceInfo.put("type", type);
                    typeChecker.add(type);
                    devices.put(deviceInfo);
                }
            }
            JSObject ret = new JSObject();
            ret.put("devices", devices);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("GetAudioRoutes Error", e.getMessage());
        }
    }

    private String getAudioRouteType(int type) {
        switch (type) {
            case (AudioDeviceInfo.TYPE_BLUETOOTH_A2DP):
            case (AudioDeviceInfo.TYPE_BLUETOOTH_SCO):
                return "Bluetooth";
            case (AudioDeviceInfo.TYPE_WIRED_HEADPHONES):
            case (AudioDeviceInfo.TYPE_WIRED_HEADSET):
                return "Headset";
            case (AudioDeviceInfo.TYPE_BUILTIN_MIC):
                return "Phone";
            case (AudioDeviceInfo.TYPE_BUILTIN_SPEAKER):
                return "Speaker";
            default:
                return null;
        }
    }

    @PluginMethod
    public void sendDTMF(String uuid, String key) {
        Log.d(TAG, "[VoiceConnection] sendDTMF, uuid: " + uuid + ", key: " + key);
        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            Log.w(TAG, "[VoiceConnection] sendDTMF ignored because no connection found, uuid: " + uuid);
            return;
        }
        char dtmf = key.charAt(0);
        conn.onPlayDtmfTone(dtmf);
    }

    @PluginMethod
    public void updateDisplay(String uuid, String displayName, String uri) {
        Log.d(TAG, "[VoiceConnection] updateDisplay, uuid: " + uuid + ", displayName: " + displayName + ", uri: " + uri);
        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            Log.w(TAG, "[VoiceConnection] updateDisplay ignored because no connection found, uuid: " + uuid);
            return;
        }

        conn.setAddress(Uri.parse(uri), TelecomManager.PRESENTATION_ALLOWED);
        conn.setCallerDisplayName(displayName, TelecomManager.PRESENTATION_ALLOWED);
    }

    @PluginMethod
    public void hasPhoneAccount(PluginCall call) {
        if (telecomManager == null) {
            this.initializeTelecomManager(getAppContext());
        }

        resolveWith(call, hasPhoneAccount());
    }

    @PluginMethod
    public void hasOutgoingCall(PluginCall call) {
        resolveWith(call, VoiceConnectionService.hasOutgoingCall);
    }

    @PluginMethod
    public void hasPermissions(PluginCall call) {
        resolveWith(call, this.hasPermissions());
    }

    private void resolveWith(PluginCall call, Object value) {
        JSObject ret = new JSObject();
        ret.put("value", value);
        call.resolve(ret);
    }

    @PluginMethod
    public void setAvailable(Boolean active) {
        VoiceConnectionService.setAvailable(active);
    }

    @PluginMethod
    public void setForegroundServiceSettings(JSObject settings) {
        VoiceConnectionService.setSettings(settings);
    }

    @PluginMethod
    public void canMakeMultipleCalls(Boolean allow) {
        VoiceConnectionService.setCanMakeMultipleCalls(allow);
    }

    @PluginMethod
    public void setReachable() {
        VoiceConnectionService.setReachable();
    }

    @PluginMethod
    public void setCurrentCallActive(PluginCall call) {
        String uuid = call.getString("uuid");
        Log.d(TAG, "[VoiceConnection] setCurrentCallActive, uuid: " + uuid);
        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            Log.w(TAG, "[VoiceConnection] setCurrentCallActive ignored because no connection found, uuid: " + uuid);
            return;
        }

        conn.setConnectionCapabilities(conn.getConnectionCapabilities() | Connection.CAPABILITY_HOLD);
        conn.setActive();
        call.resolve();
    }

    @PluginMethod
    public void openPhoneAccounts() {
        Log.d(TAG, "[VoiceConnection] openPhoneAccounts");
        if (!isConnectionServiceAvailable()) {
            Log.w(TAG, "[VoiceConnection] openPhoneAccounts ignored due to no ConnectionService");
            return;
        }

        if (Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.setComponent(
                new ComponentName("com.android.server.telecom", "com.android.server.telecom.settings.EnableAccountPreferenceActivity")
            );

            this.getAppContext().startActivity(intent);
            return;
        }

        openPhoneAccountSettings();
    }

    @PluginMethod
    public void openPhoneAccountSettings() {
        Log.d(TAG, "[VoiceConnection] openPhoneAccountSettings");
        if (!isConnectionServiceAvailable()) {
            Log.w(TAG, "[VoiceConnection] openPhoneAccountSettings ignored due to no ConnectionService");
            return;
        }

        Intent intent = new Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        this.getAppContext().startActivity(intent);
    }

    public static Boolean isConnectionServiceAvailable() {
        // PhoneAccount is available since api level 23
        return Build.VERSION.SDK_INT >= 23;
    }

    @PluginMethod
    public void isConnectionServiceAvailable(PluginCall call) {
        resolveWith(call, isConnectionServiceAvailable());
    }

    @PluginMethod
    public void checkPhoneAccountEnabled(PluginCall call) {
        resolveWith(call, hasPhoneAccount());
    }

    @PluginMethod
    public void backToForeground() {
        Context context = getAppContext();
        String packageName = context.getApplicationContext().getPackageName();
        Intent focusIntent = context.getPackageManager().getLaunchIntentForPackage(packageName).cloneFilter();
        Activity activity = getActivity();
        boolean isOpened = activity != null;
        Log.d(TAG, "[VoiceConnection] backToForeground, app isOpened ?" + (isOpened ? "true" : "false"));

        if (isOpened) {
            focusIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity.startActivity(focusIntent);
        } else {
            focusIntent.addFlags( // TODO
                Intent.FLAG_ACTIVITY_NEW_TASK +
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            );

            getContext().startActivity(focusIntent);
        }
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

    private Boolean hasPermissions() {
        Activity currentActivity = getActivity();

        if (currentActivity == null) {
            return false;
        }

        for (String permission : permissions) {
            if (getPermissionState(permission) != PermissionState.GRANTED) {
                return false;
            }
        }

        return true;
    }

    private static boolean hasPhoneAccount() {
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

    private Context getAppContext() {
        return getContext().getApplicationContext();
    }

    private class VoiceBroadcastReceiver extends BroadcastReceiver {

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

    public static void sendRemoteMessage(RemoteMessage remoteMessage) {
        CapCallKeepPlugin cckPlugin = CapCallKeepPlugin.getCapCallKeepInstance();
        if (cckPlugin != null) {
            Log.d(TAG, "received remote message" + remoteMessage.toString());
        } else {
            Log.e(TAG, "received remote message but no  " + remoteMessage.toString());
        }
    }

    public static void onNewToken(String newToken) {
        CapCallKeepPlugin cckPlugin = CapCallKeepPlugin.getCapCallKeepInstance();
        if (cckPlugin != null) {
            Log.d(TAG, "onNewToken received");
        } else {
            Log.e(TAG, "onNewToken but no plugin instance");
        }
    }

    public static CapCallKeepPlugin getCapCallKeepInstance() {
        if (staticBridge != null && staticBridge.getWebView() != null) {
            PluginHandle handle = staticBridge.getPlugin("CapCallKeep");
            if (handle == null) {
                return null;
            }
            return (CapCallKeepPlugin) handle.getInstance();
        }
        return null;
    }
}
