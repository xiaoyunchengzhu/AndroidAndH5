package com.xiaoyunchengzhu.androidandh5.webviewpakage;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.xiaoyunchengzhu.androidandh5.module.ConvertCore;


/**
 * Created by TL on 2016/5/31.
 */
public class AndroidToJs {

    private Activity activity;
    private WebView webView;
    private ConvertCore convert;

    public AndroidToJs(Activity activity, WebView webView) {
        this.activity = activity;
        this.webView = webView;
        convert = new ConvertCore(webView);
    }

    @JavascriptInterface
    public void goBack() {
        webView.post(new Runnable() {
            @Override
            public void run() {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });

    }

    //{"action":"'+action+'","params":"'+param+'"}
    @JavascriptInterface
    public void execute(String json) {
        convert.convert(json, activity);
    }



}
