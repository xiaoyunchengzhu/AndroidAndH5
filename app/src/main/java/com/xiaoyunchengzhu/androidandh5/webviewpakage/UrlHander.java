package com.xiaoyunchengzhu.androidandh5.webviewpakage;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.webkit.URLUtil;
import android.widget.Toast;


/**
 * Created by dragank on 10/1/2016.
 */
 public class UrlHander {




    private static void phoneLink(Activity mActivity, String url) {
        if (PermissionUtil.isPermissionAllowed(mActivity, Manifest.permission.CALL_PHONE)) {
            call(mActivity, url);
        } else {
            PermissionUtil.requestPermission(mActivity, Manifest.permission.CALL_PHONE, PermissionUtil.MY_PERMISSIONS_REQUEST_CALL);
        }
    }

    public static void call(Activity mActivity, String url) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(url));
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mActivity.startActivity(callIntent);
    }

    private static void smsLink(Activity mActivity, String url) {
        if (PermissionUtil.isPermissionAllowed(mActivity, Manifest.permission.SEND_SMS)) {
            sms(mActivity, url);
        } else {
            PermissionUtil.requestPermission(mActivity, Manifest.permission.SEND_SMS, PermissionUtil.MY_PERMISSIONS_REQUEST_SMS);
        }
    }

    public static void sms(Activity mActivity, String url) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.setData(Uri.parse(url));
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mActivity.startActivity(smsIntent);
    }

    private static void email(Activity mActivity, String url) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(url));
        mActivity.startActivity(emailIntent);
    }

    public static void map(Activity mActivity, String url) {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setData(Uri.parse(url));
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            mActivity.startActivity(mapIntent);
        }
    }


    private static void openGooglePlay(Activity mActivity, String url) {
//        url = url.replace("http://play.google.com/store/apps/", "market://");
        Intent googlePlayIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (googlePlayIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            mActivity.startActivity(googlePlayIntent);
        }
    }

    private static void openYoutube(Activity mActivity, String url) {
        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW);
        youtubeIntent.setData(Uri.parse(url));
        if (youtubeIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            mActivity.startActivity(youtubeIntent);
        }
    }

    public static void download(Activity mActivity, String url, String contentDisposition, String mimetype) {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //To notify the Client that the file is being downloaded
        Toast.makeText(mActivity, "下载", Toast.LENGTH_LONG).show();
        final String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        DownloadManager dm = (DownloadManager) mActivity.getSystemService(Activity.DOWNLOAD_SERVICE);
        dm.enqueue(request);
    }

    public static void downloadLink(Activity mActivity, String url, String contentDisposition, String mimetype) {
        if (PermissionUtil.isPermissionAllowed(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            download(mActivity, url, contentDisposition, mimetype);
        } else  {
            PermissionUtil.requestPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionUtil.MY_PERMISSIONS_REQUEST_DOWNLOAD);
        }
    }
}
