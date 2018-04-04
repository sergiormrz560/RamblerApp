package com.example.sergiorm.testramblerapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static android.provider.CalendarContract.CalendarCache.URI;

/**
 *  MainActivity for Rambler Mobile Application
 *
 *  Manages the WebView
 *
 *  Created by Brendan Thompson, Sergio Ramirez Martin, and Kelsey Clater Winter 2018
 */

public class MainActivity extends Activity {
    private WebView myWebView;
    private static final String TAG = "MainActivity";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get URL to open in WebView
        String launchURL;
        if (getIntent().getStringExtra("launchURL") != null) {
            // launchURL from Notification: https://github.com/OneSignal/OneSignal-Android-SDK/blob/master/Examples/AndroidStudio/app/src/main/java/com/onesignal/example/GreenActivity.java
            launchURL = getIntent().getStringExtra("launchURL");
        } else {
            launchURL = "https://www.transyrambler.com/"; // Official Site URL
//            myWebView.loadUrl("http://ggt.bf8.myftpupload.com/"); // Staging Site URL
        }
        Log.d(TAG, launchURL);

        // Use same instance (fixed issue where rotation re-set URL)
        if (savedInstanceState == null) {
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
            //

            myWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                   if (Uri.parse(url).getScheme().equals("spotify")||Uri.parse(url).getScheme().equals("market")) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, URI);
                            intent.setData(Uri.parse(url));
                            Activity host = (Activity) view.getContext();
                            host.startActivity(intent);
                            return true;
                        }
                        catch (ActivityNotFoundException e) {
                            // Google Play app is not installed, you may want to open the app store link
                            Uri uri = Uri.parse(url);
                            view.loadUrl("http://play.google.com/store/apps/" + uri.getHost() + "?" + uri.getQuery());
                            return false;
                        }
                    }
                    return false;
                }
            });

            myWebView.setWebChromeClient(new WebChromeClient(){
                private View mCustomView;
                private WebChromeClient.CustomViewCallback mCustomViewCallback;
                private int mOriginalOrientation;
                private int mOriginalSystemUiVisibility;

                public Bitmap getDefaultVideoPoster()
                {
                    return BitmapFactory.decodeResource(MainActivity.this.getApplicationContext().getResources(), 2130837573);
                }

                public void onHideCustomView()
                {
                    ((FrameLayout)MainActivity.this.getWindow().getDecorView()).removeView(this.mCustomView);
                    this.mCustomView = null;
                    MainActivity.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                    MainActivity.this.setRequestedOrientation(this.mOriginalOrientation);
                    this.mCustomViewCallback.onCustomViewHidden();
                    this.mCustomViewCallback = null;
                }

                public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
                {
                    if (this.mCustomView != null)
                    {
                        onHideCustomView();
                        return;
                    }
                    this.mCustomView = paramView;
                    this.mOriginalSystemUiVisibility = MainActivity.this.getWindow().getDecorView().getSystemUiVisibility();
                    this.mOriginalOrientation = MainActivity.this.getRequestedOrientation();
                    this.mCustomViewCallback = paramCustomViewCallback;
                    ((FrameLayout)MainActivity.this.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                    MainActivity.this.getWindow().getDecorView().setSystemUiVisibility(3846);
                }
            });
//            myWebView.loadUrl("http://ggt.bf8.myftpupload.com/");
//            myWebView.loadUrl("https://www.transyrambler.com/");
            myWebView.loadUrl(launchURL);
        }
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


    @Override
    protected void onSaveInstanceState(Bundle outState ) {
        super.onSaveInstanceState(outState);
        myWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        myWebView.restoreState(savedInstanceState);
    }
}
