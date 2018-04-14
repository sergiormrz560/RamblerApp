package com.rambler.ramblerapp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 *  Class for registering the device with Firebase
 *  May or may not be being used by the OneSignal push notification plugin
 *
 *  registerForPushNotifications() was for the plugin "Push Notifications for Wordpress (Lite)" by Delite Studios
 *  The plugin server always returned an error message saying "missing mandatory parameter os", even when testing the HTTP Post w/ Postman
 *  We have since switched to "OneSignal" for the push notification plugin
 */

public class FirebaseDeviceRegistration extends FirebaseInstanceIdService {

    private static final String TAG = "Registering";

    // onTokenRefresh = Get Updated InstanceID Token
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed Token: " + refreshedToken);

//        registerForPushNotifications(refreshedToken);
    }

    // Sends the HTTP Post Request to register for push notifications
    // This was for the plugin "Push Notifications for Wordpress (Lite)" by Delite Studios
    // The plugin server always returned an error message saying "missing mandatory parameter os", even when testing the HTTP Post w/ Postman
//    private void registerForPushNotifications(final String token) {
//
//        String urlAdress = "http://dk5.151.myftpupload.com/pnfw/register/"; // URL of user registration for the staging site
//
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    // Set Headers
//                    URL url = new URL(urlAdress);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("POST");
//                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
//                    conn.setRequestProperty("Accept","application/json");
//                    conn.setDoOutput(true);
//                    conn.setDoInput(true);
//
//                    // Create JSON Object
//                    JSONObject jsonParam = createRegistrationMessage(token);
//
//                    Log.i("JSON", jsonParam.toString());
//                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
//                    os.writeBytes(jsonParam.toString());
//
//                    os.flush();
//                    os.close();
//
//                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
//                    Log.i("MSG" , conn.getResponseMessage());
//
//                    conn.disconnect();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        thread.start();
//    }

    // createRegistrationMessage = Create the JSONObject to register for Push Notifications For Wordpress
//    private JSONObject createRegistrationMessage(String token){
//        JSONObject obj = new JSONObject();
//        try {
//            obj.put("token", token);
//            obj.put("os", "Android");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return obj;
//    }


}
