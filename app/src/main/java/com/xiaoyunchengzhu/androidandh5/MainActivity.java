package com.xiaoyunchengzhu.androidandh5;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xiaoyunchengzhu.androidandh5.webviewpakage.WebViewActivity;

public class MainActivity extends WebViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webViewManger.load("file:///android_asset/test.html");
//        webViewManger.load("http://www.6vhao.tv/");
//        webViewManger.load("http://hanyxmpt09.cloud31.49host.com");
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webViewManger.getWebView().saveState(outState);
    }
}
