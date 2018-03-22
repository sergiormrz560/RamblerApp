package com.example.sergiorm.testramblerapp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//  FirebaseDeviceRegistration class for registering the device for Push Notifications
//
//  Automatically registers the device with Firebase (super.onTokenRefresh())
//
//  Attempting to register the device with the Push Notification WordPress Plugin
//  Plugin: Push Notifications for Wordpress Lite
//  Documentation: https://www.delitestudio.com/wordpress/push-notifications-for-wordpress/documentation/

public class FirebaseDeviceRegistration extends FirebaseInstanceIdService {

    private static final String TAG = "Device Registration";

    String urlAdress = "https://www.transyrambler.com/pnfw/register/";

    // onTokenRefresh = Get Updated InstanceID Token
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed Token: " + refreshedToken);

        // Register with the Push Notification Service
        registerForPushNotifications(refreshedToken);
    }

    // Sends the HTTP Post Request to register with the WordPress Plugin
    private void registerForPushNotifications(final String token) {

        Log.d(TAG, "Attempting to register with the WP PN Plugin");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set Headers
                    URL url = new URL(urlAdress);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    // Create JSON Object
                    JSONObject jsonParam = createRegistrationMessage(token);
                    Log.i(TAG, "JSON Data: " + jsonParam.toString());

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());
                    os.flush();
                    os.close();

                    Log.i(TAG, "STATUS:" + String.valueOf(conn.getResponseCode()));
                    Log.i(TAG , "MSG: " + conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    Log.d(TAG, "Failed to register with the WP PN Plugin");
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    // createRegistrationMessage = Create the JSONObject to register for Push Notifications For Wordpress
    private JSONObject createRegistrationMessage(String token){
        JSONObject obj = new JSONObject();
        try {
            obj.put("token", token);
            obj.put("os", "Android");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
