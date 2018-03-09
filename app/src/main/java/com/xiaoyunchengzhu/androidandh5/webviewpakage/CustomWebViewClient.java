package com.xiaoyunchengzhu.androidandh5.webviewpakage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by TL on 2016/5/31.
 */
public class CustomWebViewClient extends WebViewClient {

    private View showError;
    private Context context;
    public CustomWebViewClient(View showError, Context context) {
        this.showError = showError;
        this.context=context;
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
        // 处理自定义scheme http开头，包含http ,https
        if (!url.startsWith("http")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("no","noactivity");
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        view.getSettings().setBlockNetworkImage(false);
        if (!view.getSettings().getLoadsImagesAutomatically()) {
            view.getSettings().setLoadsImagesAutomatically(true);
        }
        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        view.loadUrl("");
        showError.setVisibility(View.VISIBLE);
    }
}
