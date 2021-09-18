package com.splashcall.app;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.capacitorjs.plugins.pushnotifications.PushNotificationsPlugin;
import com.dyadical.capcallkeep.CapCallKeepPlugin;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "App MessagingService";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i(TAG, "onMessageReceived()");
        super.onMessageReceived(remoteMessage);
        PushNotificationsPlugin.sendRemoteMessage(remoteMessage);
        CapCallKeepPlugin.sendRemoteMessage(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        Log.i(TAG, "onNewToken()");
        super.onNewToken(s);
        PushNotificationsPlugin.onNewToken(s);
        CapCallKeepPlugin.onNewToken(s);
    }
}
