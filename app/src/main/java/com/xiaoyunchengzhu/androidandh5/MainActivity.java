package com.xiaoyunchengzhu.androidandh5;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xiaoyunchengzhu.androidandh5.webviewpakage.WebViewActivity;

public class MainActivity extends WebViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webViewManger.load("file:///android_asset/test.html");
    }
}
