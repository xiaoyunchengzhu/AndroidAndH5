package com.xiaoyunchengzhu.androidandh5.webviewpakage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;

import com.xiaoyunchengzhu.androidandh5.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static android.content.Context.CLIPBOARD_SERVICE;


/**
 * Created by TL on 2016/5/31.
 */
public class WebViewManger implements View.OnClickListener{

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
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setAllowFileAccess(true);
//        webView.getSettings().setAllowFileAccessFromFileURLs(true);
//        webView.getSettings().setSupportZoom(true);
//        //添加useragent
//        String ua = webView.getSettings().getUserAgentString();
//        webView.getSettings().setUserAgentString(ua + " " + "");


        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setGeolocationDatabasePath(activity.getFilesDir().getPath());
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setUserAgentString(webSettings.getUserAgentString()+" Android");
        if (false) {
            webSettings.setAppCacheEnabled(true);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }


        //阻塞加载图片
//        webView.getSettings().setBlockNetworkImage(true);
        webView.setWebChromeClient(new SuperWebChromClient(activity, WebViewActivity.FILECHOOSER_RESULTCODE, WebViewActivity.REQUEST_SELECT_FILE));
        webView.addJavascriptInterface(new AndroidToJs(activity, webView), "LocalNative");
        setCookie();
        imageInit();
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
    void setCookie(){
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        } else {
            cookieManager.setAcceptCookie(true);
        }
        CookieManager.setAcceptFileSchemeCookies(true);
        cookieManager.setCookie("www.chehngzhu.com","name:chengzhu;password:123456");
        Log.i("cookie",cookieManager.getCookie("www.chehngzhu.com"));
    }
    private String picUrl=null;
    private int x,y;
    private PopupWindow mPopWindow;
    private
    void imageInit(){
        mPopWindow=new PopupWindow(activity);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.pop_img, null);
        mPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        Button save= (Button) contentView.findViewById(R.id.btn_save);
        Button cp= (Button) contentView.findViewById(R.id.btn_cp);
        Button cancel= (Button) contentView.findViewById(R.id.btn_cancel);
        save.setOnClickListener(this);
        cp.setOnClickListener(this);
        cancel.setOnClickListener(this);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x= (int) event.getX();
                y= (int) event.getY();
                return false;
            }
        });

        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                 WebView.HitTestResult hitTestResult = webView.getHitTestResult();
                // 如果是图片类型或者是带有图片链接的类型
                if (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                        hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    picUrl = hitTestResult.getExtra();
                    mPopWindow.showAtLocation(webView, Gravity.TOP|Gravity.LEFT, x, y);
                    return true;
                }
                return false;
            }
        });
    }
    public void savePic(String url) {
        String[] str = picUrl.split("/");
        String fileName = str[str.length - 1];
        if (fileName.length()>20) {
            fileName=fileName.substring(fileName.length()-10,fileName.length());
        }
        OkhttpDownloadFile.getInstance(activity,new Handler(activity.getMainLooper())).downloadFile(url,fileName);


    }
    private void saveBitmap(Bitmap bitmap) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "com.xiaoyunchengzhu.androidandh5");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String[] str = picUrl.split("/");
        String fileName = str[str.length - 1];
        if (fileName.length()>20) {
            fileName=fileName.substring(fileName.length()-10,fileName.length());
        }
        File file = new File(appDir, fileName);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "保存成功", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "保存失败", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //保存本地
            case R.id.btn_save:

                savePic(picUrl);
                mPopWindow.dismiss();
                break;
                //复制粘贴板
            case R.id.btn_cp:
                ClipboardManager mClipboardManager =
                        (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
                ClipData mClipData;
                mClipData = ClipData.newPlainText("test", picUrl);
                mClipboardManager.setPrimaryClip(mClipData);
                Toast.makeText(activity, "复制成功", Toast.LENGTH_SHORT).show();
                mPopWindow.dismiss();
                break;
            case R.id.btn_cancel:
                if (mPopWindow!=null){
                    mPopWindow.dismiss();
                }
                break;
        }
    }

    public WebView getWebView() {
        return webView;
    }
}
