package com.xiaoyunchengzhu.androidandh5.service;

import android.app.Activity;

import com.google.gson.Gson;

import com.xiaoyunchengzhu.androidandh5.bean.TestBean;
import com.xiaoyunchengzhu.androidandh5.module.ActionService;
import com.xiaoyunchengzhu.androidandh5.module.CallBackJs;

/**
 * Created by TL on 2016/6/1.
 */
public class Test extends ActionService<TestBean> {


    public Test(Activity activity ) {
        super(activity);
    }

    /**
     * 执行处理
     * @param param
     * @param callBackJs
     */
    @Override
    public void execute(TestBean param, CallBackJs callBackJs) {
               //对传来的参数 param 对象处理。处理doing something
               String calls=new Gson().toJson(param);
               //回调
                callBackJs.callback(calls);
    }

    /**
     * 对传来的json字符串转化为对象。
     * @param params
     * @return
     */
    @Override
    public TestBean fromJson(String params) {

        return new Gson().fromJson(params, TestBean.class);
    }
}
