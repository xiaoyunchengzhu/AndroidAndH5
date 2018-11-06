package com.xiaoyunchengzhu.androidandh5.module;

import android.app.Activity;

/**
 * Created by TL on 2016/6/1.
 */
public abstract class ActionService<T> {

      protected Activity activity;
      public ActionService(Activity activity)
      {
            this.activity=activity;
      }
      public  abstract void execute(T param,CallBackJs callBackJs);
      public abstract   T fromJson(String params);

}
