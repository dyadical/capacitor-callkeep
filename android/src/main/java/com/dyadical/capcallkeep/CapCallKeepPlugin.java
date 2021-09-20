package com.dyadical.capcallkeep;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.util.ArrayList;
import java.util.Map;

import static com.dyadical.capcallkeep.Constants.EXTRA_CALLER_NAME;
import static com.dyadical.capcallkeep.Constants.EXTRA_CALL_NUMBER;
import static com.dyadical.capcallkeep.Constants.EXTRA_CALL_UUID;

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


    private static final String TAG = "CapCallKeepPlugin";

    public static Bridge staticBridge = null;
    private CapCallKeep implementation;

    public void load() {
        Log.d(TAG, "load()");
        staticBridge = this.bridge;
//        implementation = new CapCallKeep(getContext());
        implementation = new CapCallKeep();
        // messagingService = new MessagingService();
    }

    // public RNCallKeepModule(ReactApplicationContext reactContext) {
    // super(reactContext);
    // Log.d(TAG, "[VoiceConnection] constructor");
    //
    // this.reactContext = reactContext;
    // }


    // @Override
    // public String getName() {
    // return REACT_NATIVE_MODULE_NAME;
    // }

    @PluginMethod
    public void setupAndroid(PluginCall call) {
        Log.d(TAG, "[VoiceConnection] setup");
        JSObject data = call.getData();
        implementation.setupAndroid(data, getContext().getApplicationContext());
        call.resolve();
    }

    @PluginMethod
    public void registerPhoneAccount(PluginCall call) {
        if (!implementation.isConnectionServiceAvailable()) {
            Log.w(TAG, "[VoiceConnection] registerPhoneAccount ignored due to no ConnectionService");
            return;
        }

        Log.d(TAG, "[VoiceConnection] registerPhoneAccount");

        implementation.registerPhoneAccount(getAppContext());
        call.resolve();
    }

    @PluginMethod
    public void registerEvents(PluginCall call) {
        implementation.registerEvents(getContext());
        call.resolve();
    }

    @PluginMethod
    public void displayIncomingCall(PluginCall call) {
        Log.d(TAG, "displayIncomingCall()");
        if (!validate(call, new String[] { "uuid", "number", "callerName" })) {
            return;
        }
        String uuid = call.getString("uuid");
        String number = call.getString("number");
        String callerName = call.getString("callerName");

        Boolean succeeded = implementation.displayIncomingCall(uuid, number, callerName);
        if (!succeeded) {
            call.reject("no ConnectionService or no phone account");
            return;
        }
        call.resolve();
    }

    @PluginMethod
    public void answerIncomingCall(PluginCall call) {
        if (!validate(call, new String[] { "uuid" })) {
            return;
        }
        String uuid = call.getString("uuid");
        Log.d(TAG, "[VoiceConnection] answerIncomingCall, uuid: " + uuid);
        if (!implementation.isConnectionServiceAvailable() || !implementation.hasPhoneAccount()) {
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
        if (!validate(call, new String[] { "uuid", "handle" })) {
            return;
        }
        String uuid = call.getString("uuid");
        String number = call.getString("handle");
        String callerName = call.getString("callerName");
        Log.d(TAG, "[VoiceConnection] startCall called, uuid: " + uuid + ", number: " + number + ", callerName: " + callerName);

        if (!implementation.isConnectionServiceAvailable() || !implementation.hasPhoneAccount() || !hasPermissions() || number == null) {
            Log.w(
                TAG,
                "[VoiceConnection] startCall ignored: " +
                implementation.isConnectionServiceAvailable() +
                ", " +
                implementation.hasPhoneAccount() +
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
        if (callerName != null) {
            callExtras.putString(EXTRA_CALLER_NAME, callerName);
        }
        callExtras.putString(EXTRA_CALL_UUID, uuid);
        callExtras.putString(EXTRA_CALL_NUMBER, number);

        extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, implementation.handle);
        extras.putParcelable(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, callExtras);

        Log.d(TAG, "[VoiceConnection] startCall, uuid: " + uuid);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            call.reject("no CALL_PHONE permissions");
            return;
        }
        implementation.telecomManager.placeCall(uri, extras);
        call.resolve();
    }

    @PluginMethod
    public void endCall(PluginCall call) {
        if (!validate(call, new String[] { "uuid" })) {
            return;
        }
        String uuid = call.getString("uuid");
        Log.d(TAG, "[VoiceConnection] endCall called, uuid: " + uuid);
        if (!implementation.isConnectionServiceAvailable() || !implementation.hasPhoneAccount()) {
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
    public void endAllCalls(PluginCall call) {
        Log.d(TAG, "[VoiceConnection] endAllCalls called");
        if (!implementation.isConnectionServiceAvailable() || !implementation.hasPhoneAccount()) {
            Log.w(TAG, "[VoiceConnection] endAllCalls ignored due to no ConnectionService or no phone account");
            return;
        }

        Map<String, VoiceConnection> currentConnections = VoiceConnectionService.currentConnections;
        for (Map.Entry<String, VoiceConnection> connectionEntry : currentConnections.entrySet()) {
            Connection connectionToEnd = connectionEntry.getValue();
            connectionToEnd.onDisconnect();
        }

        Log.d(TAG, "[VoiceConnection] endAllCalls executed");
        call.resolve();
    }

    @PermissionCallback
    public void checkPhoneAccountPermission(PluginCall call) {
        // ReadableArray optionalPermissions, Promise promise

        if (!implementation.isConnectionServiceAvailable()) {
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
        if (!implementation.isConnectionServiceAvailable() || !implementation.hasPhoneAccount()) {
            ret.put("value", true);
            call.resolve(ret);
            return;
        }

        if (!Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
            ret.put("value", true);
            call.resolve(ret);
            return;
        }

        boolean hasSim = implementation.telephonyManager.getSimState() != TelephonyManager.SIM_STATE_ABSENT;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            call.reject("no READ_PHONE_STATE permission");
            return;
        }
        boolean hasDefaultAccount = implementation.telecomManager.getDefaultOutgoingPhoneAccount("tel") != null;

        ret.put("value", !hasSim || hasDefaultAccount);
        call.resolve(ret);
    }

    @PluginMethod
    public void setOnHold(PluginCall call) {
        if (!validate(call, new String[] { "uuid", "shouldHold" })) {
            return;
        }
        String uuid = call.getString("uuid");
        Boolean shouldHold = call.getBoolean("shouldHold");
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
        call.resolve();
    }

    @PluginMethod
    public void reportEndCallWithUUID(PluginCall call) {
        if (!validate(call, new String[] { "uuid", "reason" })) {
            return;
        }
        String uuid = call.getString("uuid");
        int reason = call.getInt("reason");
        Log.d(TAG, "[VoiceConnection] reportEndCallWithUUID, uuid: " + uuid + ", reason: " + reason);
        if (!implementation.isConnectionServiceAvailable() || !implementation.hasPhoneAccount()) {
            return;
        }

        VoiceConnection conn = (VoiceConnection) VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            Log.w(TAG, "[VoiceConnection] reportEndCallWithUUID ignored because no connection found, uuid: " + uuid);
            return;
        }
        conn.reportDisconnect(reason);
        call.resolve();
    }

    @PluginMethod
    public void rejectCall(PluginCall call) {
        if (!validate(call, new String[] { "uuid" })) {
            return;
        }
        String uuid = call.getString("uuid");
        Log.d(TAG, "[VoiceConnection] rejectCall, uuid: " + uuid);
        if (!implementation.isConnectionServiceAvailable() || !implementation.hasPhoneAccount()) {
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
    public void setMutedCall(PluginCall call) {
        if (!validate(call, new String[] { "uuid", "shouldMute" })) {
            return;
        }
        String uuid = call.getString("uuid");
        Boolean shouldMute = call.getBoolean("shouldMute");
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
        call.resolve();
    }

    /**
     * toggle audio route for speaker via connection service function
     *
     * @param uuid
     * @param routeSpeaker
     */
    @PluginMethod
    public void toggleAudioRouteSpeaker(PluginCall call) {
        if (!validate(call, new String[] { "uuid", "routeSpeaker" })) {
            return;
        }
        String uuid = call.getString("uuid");
        Boolean routeSpeaker = call.getBoolean("routeSpeaker");
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
        call.resolve();
    }

    @PluginMethod
    public void setAudioRoute(PluginCall call) {
        if (!validate(call, new String[] { "uuid", "audioRoute" })) {
            return;
        }
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
    public void sendDTMF(PluginCall call) {
        if (!validate(call, new String[] { "uuid", "key" })) {
            return;
        }
        String uuid = call.getString("uuid");
        String key = call.getString("key");
        Log.d(TAG, "[VoiceConnection] sendDTMF, uuid: " + uuid + ", key: " + key);
        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            Log.w(TAG, "[VoiceConnection] sendDTMF ignored because no connection found, uuid: " + uuid);
            return;
        }
        char dtmf = key.charAt(0);
        conn.onPlayDtmfTone(dtmf);
        call.resolve();
    }

    @PluginMethod
    public void updateDisplay(PluginCall call) {
        if (!validate(call, new String[] { "uuid", "displayName", "handle" })) {
            return;
        }
        String uuid = call.getString("uuid");
        String displayName = call.getString("displayName");
        String uri = call.getString("handle");
        Log.d(TAG, "[VoiceConnection] updateDisplay, uuid: " + uuid + ", displayName: " + displayName + ", uri: " + uri);
        Connection conn = VoiceConnectionService.getConnection(uuid);
        if (conn == null) {
            Log.w(TAG, "[VoiceConnection] updateDisplay ignored because no connection found, uuid: " + uuid);
            return;
        }

        conn.setAddress(Uri.parse(uri), TelecomManager.PRESENTATION_ALLOWED);
        conn.setCallerDisplayName(displayName, TelecomManager.PRESENTATION_ALLOWED);
        call.resolve();
    }

    @PluginMethod
    public void hasPhoneAccount(PluginCall call) {
        if (implementation.telecomManager == null) {
            this.initializeTelecomManager(getAppContext());
        }

        resolveWith(call, implementation.hasPhoneAccount());
        notifyListeners("TODO", new JSObject());
    }

    @PluginMethod
    public void hasOutgoingCall(PluginCall call) {
        resolveWith(call, VoiceConnectionService.hasOutgoingCall);
    }

    @PluginMethod
    public void hasPermissions(PluginCall call) {
        resolveWith(call, hasPermissions());
    }

    private void resolveWith(PluginCall call, Object value) {
        JSObject ret = new JSObject();
        ret.put("value", value);
        call.resolve(ret);
    }

    @PluginMethod
    public void setAvailable(PluginCall call) {
        if (!validate(call, new String[] { "active" })) {
            return;
        }
        Boolean active = call.getBoolean("active");
        VoiceConnectionService.setAvailable(active);
        call.resolve();
    }

    @PluginMethod
    public void setForegroundServiceSettings(PluginCall call) {
        JSObject settings = call.getData();
        VoiceConnectionService.setSettings(settings);
        call.resolve();
    }

    @PluginMethod
    public void canMakeMultipleCalls(PluginCall call) {
        if (!validate(call, new String[] { "allow" })) {
            return;
        }
        Boolean allow = call.getBoolean("allow");
        VoiceConnectionService.setCanMakeMultipleCalls(allow);
        call.resolve();
    }

    @PluginMethod
    public void setReachable(PluginCall call) {
        VoiceConnectionService.setReachable();
        call.resolve();
    }

    @PluginMethod
    public void setCurrentCallActive(PluginCall call) {
        if (!validate(call, new String[] { "uuid" })) {
            return;
        }
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
    public void openPhoneAccounts(PluginCall call) {
        Log.d(TAG, "[VoiceConnection] openPhoneAccounts");
        if (!implementation.isConnectionServiceAvailable()) {
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
        call.resolve();
    }

    @PluginMethod
    public void openPhoneAccountSettings(PluginCall call) {
        Log.d(TAG, "[VoiceConnection] openPhoneAccountSettings");
        openPhoneAccountSettings();
        call.resolve();
    }

    @PluginMethod
    public void isConnectionServiceAvailable(PluginCall call) {
        resolveWith(call, implementation.isConnectionServiceAvailable());
    }

    @PluginMethod
    public void checkPhoneAccountEnabled(PluginCall call) {
        resolveWith(call, implementation.hasPhoneAccount());
    }

    @PluginMethod
    public void backToForeground(PluginCall call) {
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
        call.resolve();
    }


    private Context getAppContext() {
        return getContext().getApplicationContext();
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

    private boolean validate(PluginCall call, String[] keys) {
        for (String key : keys) {
            if (!call.hasOption(key)) {
                String message = String.format("Missing required key '%s'", key);
                Log.w(TAG, "rejecting with message " + message);
                call.reject(message);
                return false;
            }
        }
        return true;
    }

    public void openPhoneAccountSettings() {
        if (!implementation.isConnectionServiceAvailable()) {
            Log.w(TAG, "[VoiceConnection] openPhoneAccountSettings ignored due to no ConnectionService");
            return;
        }

        Intent intent = new Intent(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        this.getAppContext().startActivity(intent);
    }

    public Boolean hasPermissions() {
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


}
