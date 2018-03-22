package com.example.sergiorm.testramblerapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends Activity {
    private WebView myWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        myWebView = findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();

        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);


        //added code
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
        webSettings.setDatabaseEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);


        myWebView.setWebChromeClient(new WebChromeClient());
        //

        myWebView.setWebViewClient(new WebViewClient());

        myWebView.loadUrl("https://www.transyrambler.com/");

        testPushNotifications();
    }

    @Override
    public void onBackPressed() {
        if(myWebView.canGoBack()) {
            myWebView.goBack();
        }
        else {
           super.onBackPressed();
        }
    }

    // Function testing the process of registering for the push notification server
    private void testPushNotifications(){

        // Variables
        final String TAG = "Registration Test";
        final String urlAdress = "https://www.transyrambler.com/pnfw/register/";
        final String token = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "Attempting to register with the WP PN Plugin");
        Log.d(TAG, "Device ID: " + token);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Prepare to Connect
                    URL url = new URL(urlAdress);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    // Create JSON Data Object
                    JSONObject jsonData = createRegistrationMessage(token);
                    Log.i(TAG, "JSON Data: " + jsonData.toString());

                    // Output the Data
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonData.toString());
                    os.flush();
                    os.close();
                    conn.disconnect();

                    // Log Response
                    Log.i(TAG, "STATUS:" + String.valueOf(conn.getResponseCode()));
                    Log.i(TAG , "MSG: " + conn.getResponseMessage());
                } catch (Exception e) {
                    Log.d(TAG, "Failed to register with the WP PN Plugin");
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    // Returns the JSONObject to register for Push Notifications For Wordpress
    private JSONObject createRegistrationMessage(String token){
        JSONObject obj = new JSONObject();
        try {
            obj.put("token", token);
            // obj.put("prevToken", prevToken); // Optional
            obj.put("os", "Android");
            // obj.put("email", userEmail); // Optional
            // obj.put("userCategory", userCategory); // Optional
            // obj.put("lang", "en"); // Optional
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
