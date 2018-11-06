package com.xiaoyunchengzhu.androidandh5.module;

import android.app.Activity;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by TL on 2016/6/1.
 */
public class ConvertCore implements CallBackJs{
    
    private WebView webView;
    public ConvertCore(WebView webView)
    {
        this.webView=webView;
    }
    public void convert(String param, Activity a) {
        String actoin = null;
        String paramContent = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(param);
            actoin = jsonObject.getString("action");
            paramContent = jsonObject.getString("params");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            Class<?> aClass = Class.forName("com.xiaoyunchengzhu.androidandh5.service." + actoin);
            Constructor<?>[] constructors = aClass.getConstructors();
            ActionService actionParam = (ActionService) constructors[0].newInstance(a);
            actionParam.execute(actionParam.fromJson(paramContent),this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void callback(final String json) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:Local.call('"+json+"')");
            }
        });

    }
}
