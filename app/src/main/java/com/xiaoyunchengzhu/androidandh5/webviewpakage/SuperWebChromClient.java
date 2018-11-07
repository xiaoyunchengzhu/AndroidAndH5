package com.xiaoyunchengzhu.androidandh5.webviewpakage;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SuperWebChromClient extends WebChromeClient {


    private WebViewActivity activity = null;
    private int FILECHOOSER_RESULTCODE = 0;
    private int REQUEST_SELECT_FILE = 0;
    private ValueCallback<Uri> mUploadMessage;
    public SuperWebChromClient(WebViewActivity activity, int FILECHOOSER_RESULTCODE, int REQUEST_SELECT_FILE) {

        this.activity = activity;
        this.FILECHOOSER_RESULTCODE = FILECHOOSER_RESULTCODE;
        this.REQUEST_SELECT_FILE = REQUEST_SELECT_FILE;
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog,
                                  boolean isUserGesture, Message resultMsg) {


        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(view, true);
        } else {
            cookieManager.setAcceptCookie(true);
        }
        CookieManager.setAcceptFileSchemeCookies(true);

        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(view);
        resultMsg.sendToTarget();
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);

    }

    @Override
    public void onCloseWindow(WebView window) {
        Log.v("TEST", "onCloseWindow");
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(final String origin,
                                                   final GeolocationPermissions.Callback callback) {
        // Always grant permissionSelectFile since the app itself requires location
        // permissionSelectFile and the user has therefore already granted it
        PermissionUtil.geoLocationPermission(activity, origin, callback);
    }

    // openFileChooser for Android 3.0+
    protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {

        Log.i("accepttype",acceptType);
        activity.mUploadMessage = uploadMsg;
        File imageStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES)
                , "AndroidExampleFolder");

        if (!imageStorageDir.exists()) {
            // Create AndroidExampleFolder at sdcard
            imageStorageDir.mkdirs();
        }

        // Create camera captured image file path and name
        File file = new File(
                imageStorageDir + File.separator + "IMG_"
                        + String.valueOf(System.currentTimeMillis())
                        + ".jpg");
        Log.d("File", "File: " + file);
        activity.mCapturedImageURI = Uri.fromFile(file);

        // Camera capture image intent
        final Intent captureIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, activity.mCapturedImageURI);

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");

        // Create file chooser intent
        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

        // Set camera intent to file chooser
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                , new Parcelable[]{captureIntent});

        // On select image call onActivityResult method of activity
        activity.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);


    }

    // For Lollipop 5.0+ Devices
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback,
                                     WebChromeClient.FileChooserParams fileChooserParams) {

        for (String type:fileChooserParams.getAcceptTypes()){
            Log.i("accepttype",type);

        }
        String type=fileChooserParams.getAcceptTypes()[0];
        if (activity.uploadMessage != null) {
            activity.uploadMessage.onReceiveValue(null);
            activity.uploadMessage = null;
        }
//        if ()

        activity.uploadMessage = filePathCallback;
        Intent takePictureIntent=null;
        if (type.contains("image")){
            takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        }else if (type.contains("video")){
            takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        }else {
            takePictureIntent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        }
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", activity.mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("TEST", "Unable to create Image File", ex);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                activity.mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType(fileChooserParams.getAcceptTypes()[0]);

        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, type+" Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        activity.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

        return true;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    // openFileChooser for Android < 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "");
    }

    //For Android 4.1 only
    protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg, acceptType);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return super.onJsAlert(view, url, message, result);
    }

}
