package com.xiaoyunchengzhu.androidandh5.webviewpakage;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiaoyunchengzhu.androidandh5.R;


/**
 * Created by TL on 2016/5/31.
 */
public class WebViewActivity extends Activity implements DownloadListener, SwipeRefreshLayout.OnRefreshListener{

    public ValueCallback<Uri> mUploadMessage;
    public final static int FILECHOOSER_RESULTCODE = 1;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    protected WebViewManger webViewManger;
    private WebView webView;
    private SwipeRefreshLayout mSwipeToRefresh;
    private ImageView mImageViewSplash;
    public boolean show_content=true;
    private String urlData, currentUrl, contentDisposition, mimeType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        webView = (WebView) this.findViewById(R.id.WebViewActivityWebView);
        mImageViewSplash = (ImageView) findViewById(R.id.image_splash);
        webViewManger = new WebViewManger(this, webView);
        webView.setWebViewClient(new CustomWebViewClient(this));
        mSwipeToRefresh= (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        if (mSwipeToRefresh != null) {
            mSwipeToRefresh.setOnRefreshListener(this);
        }

        if (savedInstanceState != null) {
            showContent();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showContent();
                }
            }, 5000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
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

    @Override
    public void onRefresh() {
        webView.reload();
        mSwipeToRefresh.setRefreshing(false);
    }
    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == PermissionUtil.MY_PERMISSIONS_REQUEST_CALL) {
            //If permissionSelectFile is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                UrlHander.call(this, urlData);
            }
        } else if (requestCode == PermissionUtil.MY_PERMISSIONS_REQUEST_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                UrlHander.sms(this, urlData);
            }
        } else if (requestCode == PermissionUtil.MY_PERMISSIONS_REQUEST_DOWNLOAD) {
            UrlHander.download(this, urlData, contentDisposition, mimeType);
        } else if (requestCode == PermissionUtil.MY_PERMISSIONS_REQUEST_GEOLOCATION) {

        }
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long l) {
        this.contentDisposition = contentDisposition;
        this.mimeType = mimeType;
        UrlHander.downloadLink(this, url, contentDisposition, mimeType);
    }
    public void showContent() {
        if (show_content) {
            PermissionUtil.checkPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.CALL_PHONE,
                    android.Manifest.permission.SEND_SMS,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.INTERNET
            });
            show_content=false;
            mImageViewSplash.setVisibility(View.GONE);
            ProgressDialogHelper.dismissProgress();
        }
    }
    public void hideStatusBar() {
        if (!TextUtils.isEmpty(getString(R.string.hide_status_bar))) {
            if (Build.VERSION.SDK_INT < 16) {
                getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                ActionBar actionBar = getActionBar();
                if (actionBar != null) {
                    actionBar.hide();
                }
            }
        }
    }
//    public void setToolbarButtonColor() {
//        if (false) {
//            if (webView.canGoBack()) {
//                mBack.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
//            } else {
//                mBack.setColorFilter(ContextCompat.getColor(this, R.color.gray));
//            }
//            if (mWebview.canGoForward()) {
//                mForward.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
//            } else {
//                mForward.setColorFilter(ContextCompat.getColor(this, R.color.gray));
//            }
//        }
//    }
}
