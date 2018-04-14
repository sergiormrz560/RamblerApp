package com.rambler.ramblerapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.CalendarContract.CalendarCache.URI;

/**
 *  MainActivity for Rambler Mobile Application
 *
 *  Manages the WebView
 *
 *  Created by Kelsey Clater, Sergio Ramirez Martin, Brendan Thompson, Winter 2018
 */

public class MainActivity extends Activity {
    private WebView myWebView, webView2;  //webView2 wil be used to load a blank url when internet connection is lost
   private static final String TAG = "MainActivity"; //for debuging
    private String launchURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        if (getIntent().getStringExtra("launchURL") != null) {
            // launchURL from Notification:
            // https://github.com/OneSignal/OneSignal-Android-SDK/blob/master/Examples/AndroidStudio/app/src/main/java/com/onesignal/example/GreenActivity.java
            launchURL = getIntent().getStringExtra("launchURL");
           // num = 3;
        } else {
            launchURL = "https://www.transyrambler.com/"; // Official Site URL
//            myWebView.loadUrl("http://ggt.bf8.myftpupload.com/"); // Staging Site URL, but not working
        }
       //Log.d(TAG, launchURL); //for debugging

        myWebView = findViewById(R.id.webView);
        webView2 = findViewById(R.id.webView2);

        setWebSettings();
        setWebViewClient();
        setWebCromeClient();

        //get "RELOAD" button for when internet connection is lost
        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reloading = true;
                myWebView.loadUrl(launchURL); // when "RELOAD" button is pressed
            }
        });

        myWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        //do not reload when phone rotates
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
            firstLoad = true;
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


    public void setWebCromeClient() {
        myWebView.setWebChromeClient(new WebChromeClient(){
            private View mCustomView;
            private WebChromeClient.CustomViewCallback mCustomViewCallback;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;

            public Bitmap getDefaultVideoPoster() {
                return BitmapFactory.decodeResource(MainActivity.this.getApplicationContext().getResources(), 2130837573);
            }
            public void onHideCustomView() {
                ((FrameLayout)MainActivity.this.getWindow().getDecorView()).removeView(this.mCustomView);
                this.mCustomView = null;
                MainActivity.this.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                MainActivity.this.setRequestedOrientation(this.mOriginalOrientation);
                this.mCustomViewCallback.onCustomViewHidden();
                this.mCustomViewCallback = null;
            }

            public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
            {
                if (this.mCustomView != null) {
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
    }

    public void setWebViewClient() {
        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String scheme = Uri.parse(url).getScheme();
                if(scheme.equals("spotify")||scheme.equals("whatsapp")||scheme.equals("intent")||scheme.equals("twitter")||scheme.equals("facebook")|scheme.equals("soundcloud")||(!url.startsWith("https://www."))) {
                     try {
                      /*  startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;*/
                        Intent intent = new Intent(Intent.ACTION_VIEW, URI);
                        intent.setData(Uri.parse(url));
                        Activity host = (Activity) view.getContext();
                        host.startActivity(intent);
                        return true;
                     }
                    catch (ActivityNotFoundException e) {
                        // Google Play app is not installed, you may want to open the app store link
                       // Uri uri = Uri.parse(url);

                        if(!getPackageName().equals("invalid")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, URI);
                            intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + getPackage(url)));

                            Activity host = (Activity) view.getContext();
                            host.startActivity(intent);
                            onBackPressed();
                        }


                       // view.loadUrl("http://play.google.com/store/apps/" + uri.getHost() + "?" + uri.getQuery());
                        //http://play.google.com/store/apps/details?id=<package_name>
                        //market://details?id=<package_name>
                      //  view.loadUrl("market://details?id=" + view.getContext());
                        return false;
                    }
                }
                return false;
            }

            /*
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getScheme().equals("spotify")||url.startsWith("intent://")||Uri.parse(url).getScheme().equals("soundcloud")) {
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
            }*/

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                launchURL = failingUrl;

                linkBroke = true;
                webView2.loadUrl("about:blank");
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
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setWebSettings() {
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(false);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
        webSettings.setDatabaseEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
    }


    static boolean firstLoad = true;
    static boolean reloading = false;
    static boolean linkBroke = false;
    //the next two functions take care of hiding or showing the necessary components of the Layout
    public void visible(){
        if(firstLoad) {
            WebView webview =  findViewById(R.id.webView);
            WebView webview2 =  findViewById(R.id.webView2);
            ImageView logo =  findViewById(R.id.imageView);
            ProgressBar bar =  findViewById(R.id.progressBar);
            TextView version = findViewById(R.id.textView);
            TextView broken = findViewById(R.id.broken);
            Button button = findViewById(R.id.button);
            webview.setVisibility(View.GONE);
            webview2.setVisibility(View.GONE);
            logo.setVisibility(View.VISIBLE);
            bar.setVisibility(View.VISIBLE);
            version.setVisibility(View.VISIBLE);
            button.setVisibility(View.GONE);
            broken.setVisibility(View.GONE);
            firstLoad = false;
        }
        else if(reloading) {
            WebView webview =  findViewById(R.id.webView);
            WebView webview2 =  findViewById(R.id.webView2);
            ImageView logo =  findViewById(R.id.imageView);
            ProgressBar bar =  findViewById(R.id.progressBar);
            TextView version = findViewById(R.id.textView);
            TextView broken = findViewById(R.id.broken);
            Button button = findViewById(R.id.button);
            webview.setVisibility(View.GONE);
            webview2.setVisibility(View.GONE);
            logo.setVisibility(View.GONE);
            bar.setVisibility(View.VISIBLE);
            version.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            broken.setVisibility(View.GONE);
            reloading = false;
        }
    }


    public void unvisible(){
        if(!linkBroke) {
            WebView webview =  findViewById(R.id.webView);
            WebView webview2 =  findViewById(R.id.webView2);
            ImageView logo =  findViewById(R.id.imageView);
            ProgressBar bar =  findViewById(R.id.progressBar);
            TextView version = findViewById(R.id.textView);
            TextView broken = findViewById(R.id.broken);
            Button button = findViewById(R.id.button);
            webview.setVisibility(View.VISIBLE);
            webview2.setVisibility(View.GONE);
            logo.setVisibility(View.GONE);
            bar.setVisibility(View.GONE);
            version.setVisibility(View.GONE);
            broken.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }
        else {
            WebView webview =  findViewById(R.id.webView);
            WebView webview2 =  findViewById(R.id.webView2);
            ImageView logo =  findViewById(R.id.imageView);
            ProgressBar bar =  findViewById(R.id.progressBar);
            TextView version = findViewById(R.id.textView);
            TextView broken = findViewById(R.id.broken);
            Button button = findViewById(R.id.button);
            webview.setVisibility(View.GONE);
            webview2.setVisibility(View.VISIBLE);
            logo.setVisibility(View.GONE);
            bar.setVisibility(View.GONE);
            version.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
            broken.setVisibility(View.VISIBLE);
            linkBroke = false;
        }
    }

    public String getPackage(String url) {
        String match = "uninitialized";
        Matcher m = Pattern.compile(
                Pattern.quote("package=")
                        + "(.*?)"
                        + Pattern.quote(";")
        ).matcher(url);
        while(m.find()){
            match = m.group(1);
        }
        if(!match.equals("uninitialized")) {

            return match;
        }
        else {
            return "invalid";
        }
    }
}
