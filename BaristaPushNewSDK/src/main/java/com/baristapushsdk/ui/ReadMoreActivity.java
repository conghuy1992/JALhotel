package com.baristapushsdk.ui;

import android.os.Bundle;

import com.baristapushsdk.BaseActivity;
import com.baristapushsdk.R;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;

public class ReadMoreActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        setContentView(R.layout.activity_read_more);

        WebView myWebView = (WebView) findViewById(R.id.webview_readmore);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        myWebView.loadUrl(getIntent().getStringExtra("start_Url"));
    }
}

