package com.libi.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.libi.R;

/**
 * Created by surface on 2018/9/16.
 */

public class WebActivty extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "网页";
    private WebView webView;
    private TextView backView;
    private TextView webTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        findView();
        setLisenter();
        init();

    }

    private void init() {
        final String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setDomStorageEnabled(true);
        WebChromeClient chromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.w(TAG, "标题:" + title);
                webTitle.setText(title);
            }
        };
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(url);
                return true;
            }
        };
        webView.setWebChromeClient(chromeClient);
        webView.setWebViewClient(webViewClient);
    }

    private void setLisenter() {
        backView.setOnClickListener(this);
    }

    private void findView() {
        webView = findViewById(R.id.web_view);
        webTitle = findViewById(R.id.web_title);
        backView = findViewById(R.id.web_back);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.web_back:
                finish();
                break;
            default:
                break;
        }
    }
}
