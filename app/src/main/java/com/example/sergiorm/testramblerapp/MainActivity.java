package com.example.sergiorm.testramblerapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
//import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Handler;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Get URL to open in WebView
        final String launchURL;
        if (getIntent().getStringExtra("launchURL") != null) {
            // launchURL from Notification: https://github.com/OneSignal/OneSignal-Android-SDK/blob/master/Examples/AndroidStudio/app/src/main/java/com/onesignal/example/GreenActivity.java
            launchURL = getIntent().getStringExtra("launchURL");
        } else {
            launchURL = "https://www.transyrambler.com/"; // Official Site URL
//            myWebView.loadUrl("http://ggt.bf8.myftpupload.com/"); // Staging Site URL
        }
        Log.d(TAG, launchURL);

        // Use same instance (fixed issue where rotation re-set URL)
        //if (savedInstanceState == null) {
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

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myWebView.loadUrl(launchURL);
            }
        });

            myWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                   if (Uri.parse(url).getScheme().equals("spotify")||Uri.parse(url).getScheme().equals("market")||url.startsWith("intent://")) {
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

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    // TODO Auto-generated method stub
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    myWebView.loadUrl("about:blank");
                    unconnected();
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    visible();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    unvisible();
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

            myWebView.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent,
                                            String contentDisposition, String mimetype,
                                            long contentLength) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                }
            });
        if (savedInstanceState == null) {
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
    /*
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.button) {
            if (!mbErrorOccured) {
                return;
            }

            mbReloadPressed = true;
            mWebView.reload();
            mbErrorOccured = false;
        }
    }*/

    static int num = 0;
    public void visible(){
        if(num == 0) {
            WebView webview = findViewById(R.id.webView);
            ImageView logo = findViewById(R.id.imageView);
            ProgressBar bar = findViewById(R.id.progressBar);
            TextView version = findViewById(R.id.textView);
            TextView broken = findViewById(R.id.broken);
            Button button = findViewById(R.id.button);
            webview.setVisibility(View.GONE);
            logo.setVisibility(View.VISIBLE);
            bar.setVisibility(View.VISIBLE);
            version.setVisibility(View.VISIBLE);
            button.setVisibility(View.INVISIBLE);
            broken.setVisibility(View.INVISIBLE);
            num = num+1;
        }
    }

    public void unvisible(){
        WebView webview =  findViewById(R.id.webView);
        ImageView logo =  findViewById(R.id.imageView);
        ProgressBar bar =  findViewById(R.id.progressBar);
        TextView version = findViewById(R.id.textView);
        webview.setVisibility(View.VISIBLE);
        logo.setVisibility(View.GONE);
        bar.setVisibility(View.GONE);
        version.setVisibility(View.GONE);
    }

    public void unconnected(){
        WebView webview =  findViewById(R.id.webView);
        ImageView logo =  findViewById(R.id.imageView);
        ProgressBar bar =  findViewById(R.id.progressBar);
        TextView version = findViewById(R.id.textView);
        TextView broken = findViewById(R.id.broken);
        Button button = findViewById(R.id.button);
        webview.setVisibility(View.VISIBLE);
        logo.setVisibility(View.GONE);
        bar.setVisibility(View.GONE);
        version.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);
        broken.setVisibility(View.VISIBLE);
       /// Log.d(TAG, "Unconnected");
        num = 0;
    }
}
