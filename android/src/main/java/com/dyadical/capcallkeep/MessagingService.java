package com.dyadical.capcallkeep;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.cloudmessaging.CloudMessagingReceiver;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.concurrent.atomic.AtomicInteger;

import static com.dyadical.capcallkeep.CapCallKeepPlugin.getCCKInstance;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "CCK MessagingService";
    private static final AtomicInteger requestCodeProvider;
    Intent receivedIntent;
//    Context context;

//    public MessagingService(Context cont) {
//    public MessagingService(Context cont) {
//        super();
////        context = cont;
//    }

//    @MainThread
//    @Override
//    private Task<Void> processIntent(Intent intent) {
//        Log.i(TAG, "processIntent");
//    }

    @Override
    public void handleIntent(Intent intent) {
        Log.i(TAG, "handleIntent()");
        receivedIntent = intent;
        super.handleIntent(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        getPushNotificationsInstance().onMessageReceived(remoteMessage);
//        plugin.onMessageReceived(remoteMessage);
        Log.i(TAG, "onMessageReceived");

//        Context context = this.bridge.getPlugin("CapCallKeep").getInstance();
//        Context context = getCCKInstance().getContext();
        createMessagingPendingIntent(this, receivedIntent);
        // if not a call:
        // change the name and send again to capacitor
        // super.handleIntent(intent);
//
//        Intent intent = new Intent(this, );
//        EditText editText = (EditText) findViewById(R.id.editTextTextPersonName);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
        // HERE: FIRE NEW INTE
        //        PushNotificationsPlugin.sendRemoteMessage(remoteMessage);
    }

//    @Override
//    public void onNewToken(@NonNull String s) {
//        super.onNewToken(s);
//        Log.i(TAG, "onNewToken");
//                PushNotificationsPlugin.onNewToken(s);
//    }

    static {
        AtomicInteger var0 = new AtomicInteger((int) SystemClock.elapsedRealtime());
        requestCodeProvider = var0;
    }


    private static void createMessagingPendingIntent(Context context, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                generatePendingIntentRequestCode(),
                new Intent("com.dyadical.capcallkeep.FORWARD_MESSAGING_EVENT")
                        .setComponent(
                                new ComponentName(context, "com.google.firebase.iid.FirebaseInstanceIdReceiver"))
                        .putExtra(CloudMessagingReceiver.IntentKeys.WRAPPED_INTENT, intent),
                PendingIntent.FLAG_ONE_SHOT
        );
//        context.sendBroadcast(pendingIntent);

//                        .setComponent(
//                                new ComponentName(context, "com.google.firebase.iid.FirebaseInstanceIdReceiver"))
//                getPendingIntentFlags(PendingIntent.FLAG_ONE_SHOT));
    }

    private static int generatePendingIntentRequestCode() {
        return requestCodeProvider.incrementAndGet();
    }

}
