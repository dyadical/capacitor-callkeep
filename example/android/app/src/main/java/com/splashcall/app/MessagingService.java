package com.splashcall.app;

import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.dyadical.capcallkeep.CapCallKeepPlugin;
import com.getcapacitor.JSObject;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "App MessagingService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i(TAG, "onMessageReceived()");
        super.onMessageReceived(remoteMessage);
        String type = remoteMessage.getData().get("type");
        if (type != null && type.equals("ring")) {
            CapCallKeepPlugin cckp = CapCallKeepPlugin.getCapCallKeepInstance();
            if (cckp != null) {
                Log.i(TAG, "gonna try to display it");
                cckp.displayIncomingCall("aaaa", "12345", "Wes");
                //                PushNotificationsPlugin.sendRemoteMessage(remoteMessage);
            } else {
                Log.e(TAG, "no CapCallKeepPlugin instance found");
                cckp = new CapCallKeepPlugin();
                JSObject androidData = new JSObject();
                androidData.put("selfManaged", false);
                androidData.put("imageName", "imageNameIdk");
                JSObject foregroundService = new JSObject();
                foregroundService.put("channelId", "hmm");
                foregroundService.put("channelName", "whatisthis");
                foregroundService.put("notificationTitle", "titlehereidk");
                androidData.put("foregroundService", foregroundService);
                Log.i(TAG, "about to call cckp.setupAndroid");
                //                PluginCall pc = new PluginCall();
                //                cckp.setupAndroid(); // TODO: is it just this simple?

                cckp.setupAndroid(androidData, getApplicationContext());
                cckp.displayIncomingCall("aaaa", "12345", "Wes");
            }
        } else {
            Log.i(TAG, "forwarding notification to @capacitor/push-notifications");
            //            PushNotificationsPlugin.sendRemoteMessage(remoteMessage);
        }
    }
}
