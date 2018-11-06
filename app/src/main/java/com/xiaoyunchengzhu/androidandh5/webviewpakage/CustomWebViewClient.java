package com.xiaoyunchengzhu.androidandh5.webviewpakage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by TL on 2016/5/31.
 */
public class CustomWebViewClient extends WebViewClient {

    private Activity context;
    public CustomWebViewClient(Activity context) {
        this.context=context;
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        if (event.getAction() == KeyEvent.KEYCODE_BACK) {

        }
        return super.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // 处理自定义scheme http开头，包含http ,https
        Log.i("url :",url);
        if (!url.startsWith("http")) {

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                if (url.startsWith("d2k")||url.startsWith("magnet")||url.startsWith("thunder")||url.startsWith("flashget")||
                        url.startsWith("qqdl")){
                    Toast.makeText(context,"请安装迅雷",0).show();

                }else if (url.startsWith("baiduyun")){
                    Toast.makeText(context,"请安装百度云盘",0).show();
                }
                Log.i("no","noactivity");
            }
            return true;
        }else {

//            view.loadUrl(url);
        }
        view.getSettings().setBlockNetworkImage(true);
        if (!((WebViewActivity)context).show_content) {
            ProgressDialogHelper.showProgress(context);
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
        ((WebViewActivity)context).showContent();
        ProgressDialogHelper.dismissProgress();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);

        Log.i("error :",view.getUrl()+","+request.getUrl()+","+error.getDescription().toString());
        if (!NetworkHandler.isNetworkAvailable(view.getContext())) {
            view.loadUrl("file:///android_asset/NoInternet.html");
            return;
        }
        if (view.getUrl().equals(request.getUrl())) {
            view.loadUrl("file:///android_asset/error.html");
        }
        ProgressDialogHelper.dismissProgress();
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
        if (!NetworkHandler.isNetworkAvailable(view.getContext())) {
            view.loadUrl("file:///android_asset/NoInternet.html");
            return;
        }
        handler.proceed();
        ProgressDialogHelper.dismissProgress();
    }
}
