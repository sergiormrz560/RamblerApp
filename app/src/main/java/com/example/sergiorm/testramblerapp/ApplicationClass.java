package com.example.sergiorm.testramblerapp;

import android.app.Application;
import com.onesignal.OneSignal;

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
                .setNotificationOpenedHandler(new OneSignalNotificationHandler(this))
                .init();
    }
    //end https://documentation.onesignal.com/docs/android-sdk-setup#section-android-studio
}
