package com.example.sergiorm.testramblerapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 *  Handles opening of a Push Notification from the OneSignal plugin
 *  https://documentation.onesignal.com/docs/android-native-sdk#section--notificationopenedhandler-
 *
 *  TODO: Safely get Context to this non-Activity notification handler
 *  I did it by passing the context from the ApplicationClass when constructing this handler which may be a memory leak
 *  StackOverflow answer: https://stackoverflow.com/questions/36452568/android-get-context-on-a-non-activity-class-that-is-result-of-a-push-message-ope
 *  setNotificationReceivedHandler: https://documentation.onesignal.com/docs/android-native-sdk#section--setnotificationopenedhandler-
 *  A better StackOverflow answer: https://stackoverflow.com/questions/41863680/onesignal-android-notificationopenedhandler-start-activity
 *
 *  TODO: Start Activity from within this non-Activity Notification Handler
 *
 *  TODO: User intent extra data within MainActivity to set the URL
 */

public class OneSignalNotificationHandler implements OneSignal.NotificationOpenedHandler {

    Context mContext; // This may be a memory leak

    // Constructor
    OneSignalNotificationHandler(Context context){
        mContext = context; // This may be a memory leak
    }

    // Fires when a push notification is opened by tapping on it
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String dataFromNotification;

        // Start building intent to start MainActivity
        Intent intentToOpenMainActivity = new Intent(mContext, MainActivity.class);
        intentToOpenMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);

        // Get Data from notification
        if (data != null) {
            dataFromNotification = data.optString("dataFromNotification", null);
            if (dataFromNotification != null)
                Log.i("OneSignalExample", "dataFromNotification set with value: " + dataFromNotification);

                // Add data as extra to intent https://stackoverflow.com/questions/29866450/how-to-send-data-back-to-mainactivity
                intentToOpenMainActivity.putExtra("dataFromNotification", dataFromNotification);
        }

        // Recognize button clicked on Notification
        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
        }

        // Start intent to start MainActivity
//         startActivity(intentToOpenMainActivity);
    }
}
