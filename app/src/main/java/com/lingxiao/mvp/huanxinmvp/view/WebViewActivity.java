package com.lingxiao.mvp.huanxinmvp.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lingxiao.mvp.huanxinmvp.R;
import com.lingxiao.mvp.huanxinmvp.global.ContentValue;
import com.lingxiao.mvp.huanxinmvp.widget.NoAdWebViewClient;

import java.util.HashMap;

public class WebViewActivity extends BaseActivity {

    private WebView wv_code;
    private ProgressBar pb_code;
    private String url,findUrl,title;
    private TextView tv_title_bar;
    private Toolbar tb_web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initView();
        initToolbar();

        url = getIntent().getStringExtra("url");
        findUrl = getIntent().getStringExtra("findUrl");
        title = getIntent().getStringExtra("title");
        tv_title_bar.setText(title);

        WebSettings settings = wv_code.getSettings();
        settings.setBuiltInZoomControls(true); //显示缩放按钮
        settings.setUseWideViewPort(true); 	//支持双击缩放
        settings.setJavaScriptEnabled(true); 	//支持js渲染

        if (url != null){
            wv_code.loadUrl(url);
        }else {
            wv_code.loadUrl(findUrl);
        }

        wv_code.setWebViewClient(new NoAdWebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pb_code.setVisibility(View.VISIBLE);
                //tv_title_bar.setText("Loading...");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pb_code.setVisibility(View.GONE);
                //tv_title_bar.setText("Complete");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //强制在本地浏览器打开
                view.loadUrl(url);
                return true;
            }
        });
        wv_code.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                pb_code.setProgress(newProgress);
            }
        });

    }

    private void initToolbar() {
        tb_web.setTitle("");
        setSupportActionBar(tb_web);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        wv_code = (WebView) findViewById(R.id.wv_code);
        pb_code = (ProgressBar) findViewById(R.id.pb_code);
        tv_title_bar = (TextView) findViewById(R.id.tv_title_bar);
        tb_web = (Toolbar) findViewById(R.id.tb_web);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
