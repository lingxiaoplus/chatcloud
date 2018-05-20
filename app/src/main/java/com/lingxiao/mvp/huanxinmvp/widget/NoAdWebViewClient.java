package com.lingxiao.mvp.huanxinmvp.widget;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lingxiao.mvp.huanxinmvp.utils.StringUtils;

public class NoAdWebViewClient extends WebViewClient{
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        String js = StringUtils.getClearAdDivJs();
        view.loadUrl(js);
    }
}
