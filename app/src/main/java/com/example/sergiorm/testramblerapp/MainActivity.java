package com.example.sergiorm.testramblerapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;


public class MainActivity extends Activity {
    private WebView myWebView;
    private static final String TAG = "MainActivity";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String launchURL;
        if (getIntent().getStringExtra("launchURL") != null) {
            launchURL = getIntent().getStringExtra("launchURL");
        } else {
            launchURL = "https://www.transyrambler.com/";
        }
        Log.d(TAG, launchURL);

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

            myWebView.setWebViewClient(new WebViewClient(){
                public boolean shouldOverrideUrlLoading(WebView paramWebView, String paramString)
                {
                    paramWebView.loadUrl(paramString);
                    return true;
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
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        myWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        myWebView.restoreState(savedInstanceState);
    }


}
