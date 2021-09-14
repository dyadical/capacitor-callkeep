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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CapacitorPlugin(name = "CapCallKeep")
public class CapCallKeepPlugin extends Plugin {

    private CapCallKeep implementation = new CapCallKeep();

    public static final int REQUEST_READ_PHONE_STATE = 1337;
    public static final int REQUEST_REGISTER_CALL_PROVIDER = 394859;

    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
    private static final String REACT_NATIVE_MODULE_NAME = "RNCallKeep";
    private static String[] permissions = {
        Build.VERSION.SDK_INT < 30 ? Manifest.permission.READ_PHONE_STATE : Manifest.permission.READ_PHONE_NUMBERS,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.RECORD_AUDIO
    };

    private static final String TAG = "RNCallKeep";
    private static TelecomManager telecomManager;
    private static TelephonyManager telephonyManager;
    public static PhoneAccountHandle handle;
    private boolean isReceiverRegistered = false;

    private VoiceBroadcastReceiver voiceBroadcastReceiver;
    private JSObject _settings;
    public static Bridge staticBridge = null;

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    public void load() {
        staticBridge = this.bridge;
        //        messagingService = new MessagingService();
    }

    //    public RNCallKeepModule(ReactApplicationContext reactContext) {
    //        super(reactContext);
    //        Log.d(TAG, "[VoiceConnection] constructor");
    //
    //        this.reactContext = reactContext;
    //    }

    private boolean isSelfManaged() {
        try {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && _settings.has("selfManaged") && _settings.getBoolean("selfManaged");
        } catch (Exception e) {
            return false;
        }
    }

    //    @Override
    //    public String getName() {
    //        return REACT_NATIVE_MODULE_NAME;
    //    }

    @PluginMethod
    public void setup(PluginCall call) {
        Log.d(TAG, "[VoiceConnection] setup");
        VoiceConnectionService.setAvailable(false);
        VoiceConnectionService.setInitialized(true);
        this._settings = call.getData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isSelfManaged()) {
                Log.d(TAG, "[VoiceConnection] API Version supports self managed, and is enabled in setup");
            } else {
                Log.d(TAG, "[VoiceConnection] API Version supports self managed, but it is not enabled in setup");
            }
        }

        // If we're running in self managed mode we need fewer permissions.
        if (isSelfManaged()) {
            Log.d(TAG, "[VoiceConnection] setup, adding RECORD_AUDIO in permissions in self managed");
            permissions = new String[] { Manifest.permission.RECORD_AUDIO };
        }

        if (isConnectionServiceAvailable()) {
            this.registerPhoneAccount();
            this.registerEvents();
            VoiceConnectionService.setAvailable(true);
        }

        VoiceConnectionService.setSettings(this._settings);
    }

    @PluginMethod
    public void registerPhoneAccount() {
        if (!isConnectionServiceAvailable()) {
            Log.w(TAG, "[VoiceConnection] registerPhoneAccount ignored due to no ConnectionService");
            return;
        }

        Log.d(TAG, "[VoiceConnection] registerPhoneAccount");

        this.registerPhoneAccount(this.getAppContext());
    }

    @PluginMethod
    public void registerEvents() {
        if (!isConnectionServiceAvailable()) {
            Log.w(TAG, "[VoiceConnection] registerEvents ignored due to no ConnectionService");
            return;
        }

        Log.d(TAG, "[VoiceConnection] registerEvents");

        voiceBroadcastReceiver = new VoiceBroadcastReceiver();
        registerReceiver();
        VoiceConnectionService.setPhoneAccountHandle(handle);
    }

    @PluginMethod
    public void displayIncomingCall(PluginCall call) {
        String uuid = call.getString("uuid");
        String number = call.getString("number");
        String callerName = call.getString("callerName");
        if (!isConnectionServiceAvailable() || !hasPhoneAccount()) {
            Log.w(TAG, "[VoiceConnection] displayIncomingCall ignored due to no ConnectionService or no phone account");
            return;
        }

        Log.d(TAG, "[VoiceConnection] displayIncomingCall, uuid: " + uuid + ", number: " + number + ", callerName: " + callerName);

        Bundle extras = new Bundle();
        Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, number, null);

        extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri);
        extras.putString(EXTRA_CALLER_NAME, callerName);
        extras.putString(EXTRA_CALL_UUID, uuid);

        telecomManager.addNewIncomingCall(handle, extras);
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

        telecomManager.placeCall(uri, extras);
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

    @PluginMethod
    public void checkPhoneAccountPermission() {
        // ReadableArray optionalPermissions, Promise promise
        // String[]
        Activity currentActivity = this.getCurrentActivity();

        if (!isConnectionServiceAvailable()) {
            String error = "ConnectionService not available for this version of Android.";
            Log.w(TAG, "[VoiceConnection] checkPhoneAccountPermission error " + error);
            promise.reject(E_ACTIVITY_DOES_NOT_EXIST, error);
            return;
        }
        if (currentActivity == null) {
            String error = "Activity doesn't exist";
            Log.w(TAG, "[VoiceConnection] checkPhoneAccountPermission error " + error);
            promise.reject(E_ACTIVITY_DOES_NOT_EXIST, error);
            return;
        }
        String[] optionalPermsArr = new String[optionalPermissions.size()];
        for (int i = 0; i < optionalPermissions.size(); i++) {
            optionalPermsArr[i] = optionalPermissions.getString(i);
        }

        final String[] allPermissions = Arrays.copyOf(permissions, permissions.length + optionalPermsArr.length);
        System.arraycopy(optionalPermsArr, 0, allPermissions, permissions.length, optionalPermsArr.length);

        hasPhoneAccountPromise = promise;

        if (!this.hasPermissions()) {
            WritableArray allPermissionaw = Arguments.createArray();
            for (String allPermission : allPermissions) {
                allPermissionaw.pushString(allPermission);
            }

            getReactApplicationContext()
                .getNativeModule(PermissionsModule.class)
                .requestMultiplePermissions(
                    allPermissionaw,
                    new Promise() {
                        @Override
                        public void resolve(@Nullable Object value) {
                            WritableMap grantedPermission = (WritableMap) value;
                            int[] grantedResult = new int[allPermissions.length];
                            for (int i = 0; i < allPermissions.length; ++i) {
                                String perm = allPermissions[i];
                                grantedResult[i] =
                                    grantedPermission.getString(perm).equals("granted")
                                        ? PackageManager.PERMISSION_GRANTED
                                        : PackageManager.PERMISSION_DENIED;
                            }
                            RNCallKeepModule.onRequestPermissionsResult(REQUEST_READ_PHONE_STATE, allPermissions, grantedResult);
                        }

                        @Override
                        public void reject(String code, String message) {
                            hasPhoneAccountPromise.resolve(false);
                        }

                        @Override
                        public void reject(String code, Throwable throwable) {
                            hasPhoneAccountPromise.resolve(false);
                        }

                        @Override
                        public void reject(String code, String message, Throwable throwable) {
                            hasPhoneAccountPromise.resolve(false);
                        }

                        @Override
                        public void reject(Throwable throwable) {
                            hasPhoneAccountPromise.resolve(false);
                        }

                        @Override
                        public void reject(Throwable throwable, WritableMap userInfo) {
                            hasPhoneAccountPromise.resolve(false);
                        }

                        @Override
                        public void reject(String code, @NonNull WritableMap userInfo) {
                            hasPhoneAccountPromise.resolve(false);
                        }

                        @Override
                        public void reject(String code, Throwable throwable, WritableMap userInfo) {
                            hasPhoneAccountPromise.resolve(false);
                        }

                        @Override
                        public void reject(String code, String message, @NonNull WritableMap userInfo) {
                            hasPhoneAccountPromise.resolve(false);
                        }

                        @Override
                        public void reject(String code, String message, Throwable throwable, WritableMap userInfo) {
                            hasPhoneAccountPromise.resolve(false);
                        }

                        @Override
                        public void reject(String message) {
                            hasPhoneAccountPromise.resolve(false);
                        }
                    }
                );
            return;
        }

        promise.resolve(!hasPhoneAccount());
    }

    @PluginMethod
    public void checkDefaultPhoneAccount(Promise promise) {
        if (!isConnectionServiceAvailable() || !hasPhoneAccount()) {
            promise.resolve(true);
            return;
        }

        if (!Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
            promise.resolve(true);
            return;
        }

        boolean hasSim = telephonyManager.getSimState() != TelephonyManager.SIM_STATE_ABSENT;
        boolean hasDefaultAccount = telecomManager.getDefaultOutgoingPhoneAccount("tel") != null;

        promise.resolve(!hasSim || hasDefaultAccount);
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
        //if the requester wants to mute, do that. otherwise unmute
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
    public void setAudioRoute(String uuid, String audioRoute, Promise promise) {
        try {
            VoiceConnection conn = (VoiceConnection) VoiceConnectionService.getConnection(uuid);
            if (conn == null) {
                return;
            }
            if (audioRoute.equals("Bluetooth")) {
                Log.d(TAG, "[VoiceConnection] setting audio route: Bluetooth");
                conn.setAudioRoute(CallAudioState.ROUTE_BLUETOOTH);
                promise.resolve(true);
                return;
            }
            if (audioRoute.equals("Headset")) {
                Log.d(TAG, "[VoiceConnection] setting audio route: Headset");
                conn.setAudioRoute(CallAudioState.ROUTE_WIRED_HEADSET);
                promise.resolve(true);
                return;
            }
            if (audioRoute.equals("Speaker")) {
                Log.d(TAG, "[VoiceConnection] setting audio route: Speaker");
                conn.setAudioRoute(CallAudioState.ROUTE_SPEAKER);
                promise.resolve(true);
                return;
            }
            Log.d(TAG, "[VoiceConnection] setting audio route: Wired/Earpiece");
            conn.setAudioRoute(CallAudioState.ROUTE_WIRED_OR_EARPIECE);
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("SetAudioRoute", e.getMessage());
        }
    }

    @PluginMethod
    public void getAudioRoutes(Promise promise) {
        try {
            Context context = this.getAppContext();
            AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
            WritableArray devices = Arguments.createArray();
            ArrayList<String> typeChecker = new ArrayList<>();
            AudioDeviceInfo[] audioDeviceInfo = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS + AudioManager.GET_DEVICES_OUTPUTS);
            for (AudioDeviceInfo device : audioDeviceInfo) {
                String type = getAudioRouteType(device.getType());
                if (type != null && !typeChecker.contains(type)) {
                    WritableMap deviceInfo = Arguments.createMap();
                    deviceInfo.putString("name", type);
                    deviceInfo.putString("type", type);
                    typeChecker.add(type);
                    devices.pushMap(deviceInfo);
                }
            }
            promise.resolve(devices);
        } catch (Exception e) {
            promise.reject("GetAudioRoutes Error", e.getMessage());
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
    public void hasPhoneAccount(Promise promise) {
        if (telecomManager == null) {
            this.initializeTelecomManager();
        }

        promise.resolve(hasPhoneAccount());
    }

    @PluginMethod
    public void hasOutgoingCall(Promise promise) {
        promise.resolve(VoiceConnectionService.hasOutgoingCall);
    }

    @PluginMethod
    public void hasPermissions(Promise promise) {
        promise.resolve(this.hasPermissions());
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
    public void isConnectionServiceAvailable(Promise promise) {
        promise.resolve(isConnectionServiceAvailable());
    }

    @PluginMethod
    public void checkPhoneAccountEnabled(Promise promise) {
        promise.resolve(hasPhoneAccount());
    }

    @PluginMethod
    public void backToForeground() {
        Context context = getAppContext();
        String packageName = context.getApplicationContext().getPackageName();
        Intent focusIntent = context.getPackageManager().getLaunchIntentForPackage(packageName).cloneFilter();
        Activity activity = getCurrentActivity();
        boolean isOpened = activity != null;
        Log.d(TAG, "[VoiceConnection] backToForeground, app isOpened ?" + (isOpened ? "true" : "false"));

        if (isOpened) {
            focusIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity.startActivity(focusIntent);
        } else {
            focusIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK +
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            );

            getReactApplicationContext().startActivity(focusIntent);
        }
    }

    private void initializeTelecomManager() {
        Context context = this.getAppContext();
        ComponentName cName = new ComponentName(context, VoiceConnectionService.class);
        String appName = this.getApplicationName(context);

        handle = new PhoneAccountHandle(cName, appName);
        telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
    }

    private void registerPhoneAccount(Context appContext) {
        if (!isConnectionServiceAvailable()) {
            Log.w(TAG, "[VoiceConnection] registerPhoneAccount ignored due to no ConnectionService");
            return;
        }

        this.initializeTelecomManager();
        String appName = this.getApplicationName(this.getAppContext());

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

        telephonyManager = (TelephonyManager) this.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);

        telecomManager.registerPhoneAccount(account);
    }

    private void sendEventToJS(String eventName, @Nullable WritableMap params) {
        Log.v(
            TAG,
            "[VoiceConnection] sendEventToJS, eventName :" + eventName + ", args : " + (params != null ? params.toString() : "null")
        );
        this.reactContext.getJSModule(RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    private String getApplicationName(Context appContext) {
        ApplicationInfo applicationInfo = appContext.getApplicationInfo();
        int stringId = applicationInfo.labelRes;

        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : appContext.getString(stringId);
    }

    private Boolean hasPermissions() {
        Activity currentActivity = this.getCurrentActivity();

        if (currentActivity == null) {
            return false;
        }

        boolean hasPermissions = true;
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(currentActivity, permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                hasPermissions = false;
            }
        }

        return hasPermissions;
    }

    private static boolean hasPhoneAccount() {
        return (
            isConnectionServiceAvailable() &&
            telecomManager != null &&
            telecomManager.getPhoneAccount(handle) != null &&
            telecomManager.getPhoneAccount(handle).isEnabled()
        );
    }

    private void registerReceiver() {
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

            LocalBroadcastManager.getInstance(this.reactContext).registerReceiver(voiceBroadcastReceiver, intentFilter);
            isReceiverRegistered = true;
        }
    }

    private Context getAppContext() {
        return this.reactContext.getApplicationContext();
    }

    public static void onRequestPermissionsResult(int requestCode, String[] grantedPermissions, int[] grantResults) {
        int permissionsIndex = 0;
        List<String> permsList = Arrays.asList(permissions);
        for (int result : grantResults) {
            if (permsList.contains(grantedPermissions[permissionsIndex]) && result != PackageManager.PERMISSION_GRANTED) {
                hasPhoneAccountPromise.resolve(false);
                return;
            }
            permissionsIndex++;
        }
        hasPhoneAccountPromise.resolve(true);
    }

    private class VoiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            WritableMap args = Arguments.createMap();
            HashMap<String, String> attributeMap = (HashMap<String, String>) intent.getSerializableExtra("attributeMap");

            switch (intent.getAction()) {
                case ACTION_END_CALL:
                    args.putString("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    sendEventToJS("RNCallKeepPerformEndCallAction", args);
                    break;
                case ACTION_ANSWER_CALL:
                    args.putString("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    sendEventToJS("RNCallKeepPerformAnswerCallAction", args);
                    break;
                case ACTION_HOLD_CALL:
                    args.putBoolean("hold", true);
                    args.putString("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    sendEventToJS("RNCallKeepDidToggleHoldAction", args);
                    break;
                case ACTION_UNHOLD_CALL:
                    args.putBoolean("hold", false);
                    args.putString("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    sendEventToJS("RNCallKeepDidToggleHoldAction", args);
                    break;
                case ACTION_MUTE_CALL:
                    args.putBoolean("muted", true);
                    args.putString("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    sendEventToJS("RNCallKeepDidPerformSetMutedCallAction", args);
                    break;
                case ACTION_UNMUTE_CALL:
                    args.putBoolean("muted", false);
                    args.putString("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    sendEventToJS("RNCallKeepDidPerformSetMutedCallAction", args);
                    break;
                case ACTION_DTMF_TONE:
                    args.putString("digits", attributeMap.get("DTMF"));
                    args.putString("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    sendEventToJS("RNCallKeepDidPerformDTMFAction", args);
                    break;
                case ACTION_ONGOING_CALL:
                    args.putString("handle", attributeMap.get(EXTRA_CALL_NUMBER));
                    args.putString("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    args.putString("name", attributeMap.get(EXTRA_CALLER_NAME));
                    sendEventToJS("RNCallKeepDidReceiveStartCallAction", args);
                    break;
                case ACTION_AUDIO_SESSION:
                    sendEventToJS("RNCallKeepDidActivateAudioSession", null);
                    break;
                case ACTION_CHECK_REACHABILITY:
                    sendEventToJS("RNCallKeepCheckReachability", null);
                    break;
                case ACTION_SHOW_INCOMING_CALL_UI:
                    args.putString("handle", attributeMap.get(EXTRA_CALL_NUMBER));
                    args.putString("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    args.putString("name", attributeMap.get(EXTRA_CALLER_NAME));
                    sendEventToJS("RNCallKeepShowIncomingCallUi", args);
                    break;
                case ACTION_WAKE_APP:
                    Intent headlessIntent = new Intent(reactContext, RNCallKeepBackgroundMessagingService.class);
                    headlessIntent.putExtra("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    headlessIntent.putExtra("name", attributeMap.get(EXTRA_CALLER_NAME));
                    headlessIntent.putExtra("handle", attributeMap.get(EXTRA_CALL_NUMBER));
                    Log.d(
                        TAG,
                        "[VoiceConnection] wakeUpApplication: " +
                        attributeMap.get(EXTRA_CALL_UUID) +
                        ", number : " +
                        attributeMap.get(EXTRA_CALL_NUMBER) +
                        ", displayName:" +
                        attributeMap.get(EXTRA_CALLER_NAME)
                    );

                    ComponentName name = reactContext.startService(headlessIntent);
                    if (name != null) {
                        HeadlessJsTaskService.acquireWakeLockNow(reactContext);
                    }
                    break;
                case ACTION_ON_SILENCE_INCOMING_CALL:
                    args.putString("handle", attributeMap.get(EXTRA_CALL_NUMBER));
                    args.putString("callUUID", attributeMap.get(EXTRA_CALL_UUID));
                    args.putString("name", attributeMap.get(EXTRA_CALLER_NAME));
                    sendEventToJS("RNCallKeepOnSilenceIncomingCall", args);
                    break;
            }
        }
    }
}
