package com.xiaoyunchengzhu.androidandh5.webviewpakage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.xiaoyunchengzhu.androidandh5.R;


/**
 * Created by TL on 2016/5/31.
 */
public class WebViewActivity extends Activity {

    public ValueCallback<Uri> mUploadMessage;
    public final static int FILECHOOSER_RESULTCODE = 1;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    protected WebViewManger webViewManger;
    private View showError;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        showError = this.findViewById(R.id.WebViewActivityShowError);
        webView = (WebView) this.findViewById(R.id.WebViewActivityWebView);
        webViewManger = new WebViewManger(this, webView);
        webView.setWebViewClient(new CustomWebViewClient(showError,this));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webViewManger.canGoBack()) {
                webViewManger.goBack();
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {


        Log.i("filechooser", requestCode + "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != this.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else {
            Toast.makeText(this.getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
        }
        Log.i("filechooser", requestCode + "结束");
    }
}
