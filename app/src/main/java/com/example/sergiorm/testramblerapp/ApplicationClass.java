package com.example.sergiorm.testramblerapp;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.onesignal.OneSignal;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;

import org.json.JSONObject;

/**
 *  ApplicationClass
 *  Registers the device with the OneSignal push notification plugin and sets custom NotificationOpenedHandler
 *  Example Project: https://github.com/OneSignal/OneSignal-Android-SDK/blob/master/Examples/AndroidStudio/app/src/main/java/com/onesignal/example/ExampleApplication.java
 *
 *  NotificationOpenHandler = custom NotificationOpenedHandler that passes launchURL to MainActivity
 *
 *  Created by Brendan Thompson, Sergio Ramirez Martin, and Kelsey Clater Winter 2018
 */

public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // OneSignal Initialization: https://documentation.onesignal.com/docs/android-sdk-setup#section-android-studio
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new NotificationOpenHandler()) // set custom NotificationOpenedHandler
                .init();
    }

    // Handles what happens when a notification is fired (tapped on)
    class NotificationOpenHandler implements OneSignal.NotificationOpenedHandler {
        private static final String TAG = "NotificationOpenHandler";

        @Override
        public void notificationOpened(OSNotificationOpenResult result) {

            // Get Data passed with Notification
//            OSNotificationAction.ActionType actionType = result.action.type;
//            JSONObject data = result.notification.payload.additionalData;
//            String dataFromNotification;

            // Get LaunchURL
            String launchURL = result.notification.payload.launchURL;
            Log.d(TAG, launchURL);

            // Create Intent
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("launchURL", launchURL); // Add launchURL as extra
            startActivity(intent);
        }
    }
}