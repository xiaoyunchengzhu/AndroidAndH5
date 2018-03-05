package com.xiaoyunchengzhu.androidandh5.webviewpakage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


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

        webView.setWebChromeClient(new CustomWebChromClient(activity, WebViewActivity.FILECHOOSER_RESULTCODE, WebViewActivity.REQUEST_SELECT_FILE));
        webView.addJavascriptInterface(new AndroidToJs(activity, webView), "LocalNative");
        setCookie();
        cpImage();
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
        cookieManager.setCookie("www.baidu.com","name:chengzhu;password:123456");
        Log.i("cookie",cookieManager.getCookie("www.baidu.com"));
    }
    private String picUrl=null;
    void cpImage(){
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                 WebView.HitTestResult hitTestResult = webView.getHitTestResult();
                // 如果是图片类型或者是带有图片链接的类型
                if (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                        hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    // 弹出保存图片的对话框
                    picUrl = hitTestResult.getExtra();
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("提示");
                    builder.setMessage("保存图片"+picUrl+"到本地");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           //获取图片链接
                            //保存图片到相册
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    url2bitmap(picUrl);
                                }
                            }).start();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        // 自动dismiss
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
                return false;//保持长按可以复制文字
            }
        });
    }
    public void url2bitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;
            int length = http.getContentLength();
            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
            if (bm != null) {
                save2Album(bm);
            }
        } catch (Exception e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "保存失败", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
    }
    private void save2Album(Bitmap bitmap) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "相册名称");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String[] str = picUrl.split("/");
        String fileName = str[str.length - 1];
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
            onSaveSuccess(file);
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

    private void onSaveSuccess(final File file) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                Toast.makeText(activity, "保存成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
