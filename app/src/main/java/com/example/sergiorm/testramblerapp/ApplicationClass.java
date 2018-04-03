package com.example.sergiorm.testramblerapp;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OneSignal;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;

import org.json.JSONObject;

/**
 * Registers the device with the OneSignal push notification plugin
 */

public class ApplicationClass extends Application {
    //begin https://documentation.onesignal.com/docs/android-sdk-setup#section-android-studio
    @Override
    public void onCreate() {
        super.onCreate();
        // TODO: Add OneSignal initialization here
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new NotificationOpenHandler())
                //.setNotificationOpenedHandler(new MyNotificationOpenedHandler(this))
                .init();
    }
    //end https://documentation.onesignal.com/docs/android-sdk-setup#section-android-studio

    class NotificationOpenHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        private static final String TAG = "NotificationOpenHandler";

        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String dataFromNotification;

            //String stationName = data.optString("stationName");
            //String timestamp = data.optString("timestamp");
            //String filename = data.optString("filename");
//            String url = getString(R.string.callResourceUrl) + filename;
            //Log.d(TAG, data.toString());

            // Get Data from notification
            if (data != null) {
                dataFromNotification = data.optString("dataFromNotification", null);
                if (dataFromNotification != null){
                    Log.d(TAG, "dataFromNotification set with value: " + dataFromNotification);

                    // Add data as extra to intent https://stackoverflow.com/questions/29866450/how-to-send-data-back-to-mainactivity
//                    intent.putExtra("dataFromNotification", dataFromNotification);
                } else {
                    Log.d(TAG, "No dataFromNotification");
                }
            } else {
                Log.d(TAG, "No Data");
            }

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            //intent.putExtra("stationName", stationName);
            //intent.putExtra("time", timestamp);
//            intent.putExtra("url", url);
//            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}





/*

public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {

    private Application application;

    public MyNotificationOpenedHandler(Application application) {
        this.application = application;
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {

        // Get custom datas from notification
        JSONObject data = result.notification.payload.additionalData;
        if (data != null) {
            String myCustomData = data.optString("key", null);
        }

        // React to button pressed
        OSNotificationAction.ActionType actionType = result.action.type;
        if (actionType == OSNotificationAction.ActionType.ActionTaken)
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

        // Launch new activity using Application object
        startApp();
    }

    private void startApp() {
        Intent intent = new Intent(application, MyActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(intent);
    }
}

*/