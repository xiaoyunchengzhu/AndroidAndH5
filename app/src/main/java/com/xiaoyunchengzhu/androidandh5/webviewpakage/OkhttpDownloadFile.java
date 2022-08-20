package com.xiaoyunchengzhu.androidandh5.webviewpakage;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class OkhttpDownloadFile {
    /**
     * Created by Jane on 2019/2/15.
     */
        private final String TAG = "OkhttpDownloadFile";
        private String mSDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        private static OkhttpDownloadFile okhttpDownloadFile;
        private static Context mContext;
        private static Handler handler;

        /**
         * 设置单例模式
         */
        public static OkhttpDownloadFile getInstance(Context context, Handler mHandle) {
            if (okhttpDownloadFile == null) {
                okhttpDownloadFile = new OkhttpDownloadFile();
            }
            handler = mHandle;
            return okhttpDownloadFile;
        }

        /**
         * 下载文件
         *
         * @param url      文件下载路径
         * @param fileName 文件名
         **/
        public void downloadFile(final String url, final String fileName) {
            //下载路径，如果路径无效了，可换成你的下载路径
            final long startTime = System.currentTimeMillis();
            Log.i(TAG, "startTime=" + startTime);
            Request request = new Request.Builder().url(url).build();
            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(TAG, "fail===cause==" + e.getCause().getMessage());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    Sink sink = null;
                    BufferedSink bufferedSink = null;
                    try {
                        File dest = new File(mSDCardPath, fileName);
                        sink = Okio.sink(dest);
                        bufferedSink = Okio.buffer(sink);
                        bufferedSink.writeAll(response.body().source());
                        bufferedSink.close();
                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("fileName", Constants.FILE_EXT_NAME);
//                        jsonObject.put("filePath", mSDCardPath + "/" + Constants.FILE_EXT_NAME);
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        message.obj = jsonObject.toString();
                        handler.sendMessage(message);
                        Log.i(TAG, "totalTime=" + (System.currentTimeMillis() - startTime));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, "download failed");
                    } finally {
                        if (bufferedSink != null) {
                            bufferedSink.close();
                        }
                    }
                }
            });
        }

}
