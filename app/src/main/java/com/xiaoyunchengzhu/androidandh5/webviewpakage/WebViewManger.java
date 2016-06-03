package com.xiaoyunchengzhu.androidandh5.webviewpakage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;


/**
 * Created by TL on 2016/5/31.
 */
public class WebViewManger {

    private WebView webView;
    private WebViewActivity activity;

    public WebViewManger(WebViewActivity activity, WebView webView) {
        this.webView = webView;
        this.activity = activity;
        inite();
    }

    //{"app":"TongLian","systemVersion":"8.0","systemPlatform":"android"}          user-agent
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void inite() {
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        //添加useragent
        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + " " + "");
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        } else {
            cookieManager.setAcceptCookie(true);
        }
        CookieManager.setAcceptFileSchemeCookies(true);
        webView.setWebChromeClient(new CustomWebChromClient(activity, WebViewActivity.FILECHOOSER_RESULTCODE, WebViewActivity.REQUEST_SELECT_FILE));
        webView.addJavascriptInterface(new AndroidToJs(activity, webView), "LocalNative");
    }

    public void load(String url) {
        webView.loadUrl(url);
    }

    public void goBack() {
        webView.goBack();
    }

    public boolean canGoBack() {
        return webView.canGoBack();
    }

}
