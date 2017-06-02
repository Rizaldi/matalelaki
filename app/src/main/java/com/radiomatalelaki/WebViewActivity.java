package com.radiomatalelaki;

/**
 * Created by rianpradana on 5/26/17.
 */


import com.radiomatalelaki.util.Global;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * The type Web view activity.
 */
public class WebViewActivity extends Activity {

    // set to false if you don't want to show ads
    private boolean showAd = false;

    // Progress bar indicating page loading
    private ProgressBar progressBar;

    /* Webview and webclient */
    private WebView webView;
    private WebViewClient client;

    // view title
    private TextView title;

    /* Banner AD View */
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // view title
        title = (TextView) findViewById(R.id.barTitle);

        // loading indicator
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // web view and client
        webView = (WebView) findViewById(R.id.webview);
        client = new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted (WebView view, String url, Bitmap favicon) {
                // shows progressbar
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished (WebView view, String url) {

                // hides progress bar
                progressBar.setVisibility(View.GONE);

                // set view title
                if (view.getTitle()!=null) {
                    title.setText(view.getTitle());
                }
            }
        };

        // init webview settings
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(client);

        // get url from intent and add the http prefix
        String url = getIntent().getStringExtra(Global.EXTRA_URL_WEBVIEW);
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://"+url;
        }
        webView.loadUrl(url);

        // bak button
        ImageButton backButton = (ImageButton) findViewById(R.id.buttonBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

		/* Set the banner ad view */
        mAdView = (AdView) findViewById(R.id.adView);
        if (showAd) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

}
