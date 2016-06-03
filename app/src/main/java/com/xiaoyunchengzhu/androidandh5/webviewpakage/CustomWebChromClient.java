package com.xiaoyunchengzhu.androidandh5.webviewpakage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Created by TL on 2016/5/31.
 */
public class CustomWebChromClient extends WebChromeClient {

    private WebViewActivity activity = null;
    private int FILECHOOSER_RESULTCODE = 0;
    private int REQUEST_SELECT_FILE = 0;
    private ValueCallback<Uri> mUploadMessage;

    public CustomWebChromClient(WebViewActivity activity, int FILECHOOSER_RESULTCODE, int REQUEST_SELECT_FILE) {

        this.activity = activity;
        this.FILECHOOSER_RESULTCODE = FILECHOOSER_RESULTCODE;
        this.REQUEST_SELECT_FILE = REQUEST_SELECT_FILE;
    }

    /**
     * 文件选择
     * @param uploadMsg
     */
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {

        ((WebViewActivity) activity).mUploadMessage = uploadMsg;

        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        activity.startActivityForResult(Intent.createChooser(i, "文件选择"), FILECHOOSER_RESULTCODE);

    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {

        ((WebViewActivity) activity).mUploadMessage = uploadMsg;

        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        activity.startActivityForResult(
                Intent.createChooser(i, "文件选择"),
                FILECHOOSER_RESULTCODE);
    }

    //For Android 4.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        ((WebViewActivity) activity).mUploadMessage = uploadMsg;

        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        activity.startActivityForResult(Intent.createChooser(i, "文件选择"), FILECHOOSER_RESULTCODE);

    }
    // For >= Lollipop 5.0

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

        Log.i("filechooser", REQUEST_SELECT_FILE + "启动");
        if (((WebViewActivity) activity).uploadMessage != null) {
            ((WebViewActivity) activity).uploadMessage.onReceiveValue(null);
            ((WebViewActivity) activity).uploadMessage = null;
        }
        ((WebViewActivity) activity).uploadMessage = filePathCallback;

        Log.i("filechooser", filePathCallback.toString());
        ;
        Intent intent = fileChooserParams.createIntent();

        try {
            activity.startActivityForResult(intent, REQUEST_SELECT_FILE);
        } catch (ActivityNotFoundException e) {
            ((WebViewActivity) activity).uploadMessage = null;
            Toast.makeText(activity.getApplicationContext(), "不能打开文件选择器", Toast.LENGTH_LONG).show();
            return false;
        }
//        openFileChooserImplForAndroid5(filePathCallback);
        return true;
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        ((WebViewActivity) activity).uploadMessage = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("*/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        activity.startActivityForResult(chooserIntent, REQUEST_SELECT_FILE);
    }
}
