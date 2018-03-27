package com.example.sergiorm.testramblerapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


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
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setInstanceFollowRedirects(true);
                conn.setChunkedStreamingMode(0);

                // Create JSON Data Object for parameters
                JSONObject jsonData = createRegistrationMessage(token);
                Log.i(TAG, "JSON Data: " + jsonData.toString());
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept","application/json");

                // Do the HTTP POST
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeUTF("msg=" + URLEncoder.encode (jsonData.toString (), "UTF-8"));
                os.flush();
                os.close();

                // Log Response
                Log.d(TAG, "STATUS:" + String.valueOf(conn.getResponseCode()));
                Log.d(TAG , "MSG: " + conn.getResponseMessage());

                // Parse JSON Data returned in response
                if ((200 < conn.getResponseCode()) && (conn.getResponseCode() < 299)){
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null){
                        sb.append(output);
                    }
                    Log.d(TAG, "RESPONSE: " + sb.toString());
                }
                else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null){
                        sb.append(output);
                    }
                    Log.d(TAG, "RESPONSE: " + sb.toString());
                }

                // Shutdown Connection
                conn.disconnect();

            } catch (Exception e) {
                Log.d(TAG, "Failed to register with the WP PN Plugin");
                e.printStackTrace();
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Push Notification Failure", Toast.LENGTH_SHORT).show();
                    }
                });
            }}
        });

        thread.start();
    }

    // Returns the JSONObject to register for Push Notifications For Wordpress
    private JSONObject createRegistrationMessage(String token){
        JSONObject obj = new JSONObject();
        try {
            obj.put("token", token);
            obj.put("os", "Android");
            // obj.put("prevToken", prevToken); // Optional
            // obj.put("email", userEmail); // Optional
            // obj.put("userCategory", userCategory); // Optional
            // obj.put("lang", "en"); // Optional
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
