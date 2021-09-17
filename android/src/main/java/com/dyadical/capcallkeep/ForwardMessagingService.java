package com.dyadical.capcallkeep;


import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ForwardMessagingService extends FirebaseMessagingService {

    private static final String TAG = "CCK ForwardMessage";

    public ForwardMessagingService() {
        super();
        Log.i(TAG, "instantiated ForwardMessagingService");
    }

    @Override
    public void handleIntent(Intent intent) {
        Log.i(TAG, "forwarded handleIntent()");
        super.handleIntent(intent);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i(TAG, "forwarded onMessageReceive()");
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        Log.i(TAG, "forwarded onNewToken()");
        super.onNewToken(s);
    }


}

