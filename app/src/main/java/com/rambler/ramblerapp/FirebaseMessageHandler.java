package com.rambler.ramblerapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 *  Class for handling the reception of push notifications from Firebase
 *  May or may not be being used for the OneSignal push notification plugin
 */

public class FirebaseMessageHandler extends FirebaseMessagingService {

    private static final String TAG = "MessageHandlerFirebase";
    private static final int REQUEST_CODE = 1;
    private static final int NOTIFICATION_ID = 6578;

    // Constructor
    public FirebaseMessageHandler() {

    }

    // Handle Messages
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload
        if (remoteMessage.getData().size() > 0){
            Log.d(TAG, "Message Data Payload: " + remoteMessage.getData());

//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }
        }

        // Check if message contains Notification payload
        if (remoteMessage.getNotification() != null){
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            // Send Notification
            final String title = remoteMessage.getData().get("title");
            final String message = remoteMessage.getData().get("body");
            showNotifications(title, message);
        }
    }

    void showNotifications(String title, String msg){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentText(msg)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
    }

}
