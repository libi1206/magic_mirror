package com.libi.ui;

import android.os.Build;
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
        webView.getSettings().setDomStorageEnabled(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setUseWideViewPort(true); // 关键点
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setUseWideViewPort(true);
//        webView.addJavascriptInterface(new MyJavaScript(), "JsUtils");

//        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
//        webSettings.setBuiltInZoomControls(false);//设置是否支持缩放
////        webSettings.addJavascriptInterface(obj,str);//向html页面注入java对象
//        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
//        webSettings.setLoadWithOverviewMode(true);// 页面支持缩放：
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setBuiltInZoomControls(true);
////        webUrl.requestFocusFromTouch(); //如果webView中需要用户手动输入用户名、密码或其他，则webview必须设置支持获取手势焦点。
//        webSettings.setJavaScriptEnabled(true);  //支持js
//        webSettings.setUseWideViewPort(false);  //将图片调整到适合webview的大小
//        webSettings.setSupportZoom(true);  //支持缩放    webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
//        webSettings.supportMultipleWindows();  //多窗口
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //关闭webview中缓存
//        webSettings.setAllowFileAccess(true);  //设置可以访问文件
//        webSettings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
//        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//        webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片

//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDisplayZoomControls(false);
//        webSettings.setSupportZoom(true);
//
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setDatabaseEnabled(true);
// 全屏显示
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setUseWideViewPort(true);


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

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null)
            webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
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
