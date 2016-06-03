package com.xiaoyunchengzhu.androidandh5.webviewpakage;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by TL on 2016/5/31.
 */
public class CustomWebViewClient extends WebViewClient {

    private View showError;

    public CustomWebViewClient(View showError) {
        this.showError = showError;
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        if (event.getAction() == KeyEvent.KEYCODE_BACK) {
            showError.setVisibility(View.GONE);
        }
        return super.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        view.loadUrl("");
        showError.setVisibility(View.VISIBLE);
    }
}
