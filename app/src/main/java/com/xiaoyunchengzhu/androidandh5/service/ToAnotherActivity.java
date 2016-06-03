package com.xiaoyunchengzhu.androidandh5.service;

import android.app.Activity;
import android.content.Intent;

import com.xiaoyunchengzhu.androidandh5.SecondActivity;
import com.xiaoyunchengzhu.androidandh5.module.ActionService;
import com.xiaoyunchengzhu.androidandh5.module.CallBackJs;

/**
 * Created by TL on 2016/6/3.
 *
 */
public class ToAnotherActivity extends ActionService<String> {
    public ToAnotherActivity(Activity activity) {
        super(activity);
    }

    @Override
    public void execute(String param, CallBackJs callBackJs) {
        Intent intent=new Intent(activity, SecondActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 可以不转化为对象，直接使用传来的字符串。
     * @param params
     * @return
     */
    @Override
    public String fromJson(String params) {
        return params;
    }
}
