package com.cnbs.gisdemo;

import android.app.Application;

/**
 * author: zuo
 * date: 2017/11/30 14:34
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
       new Utils().saveAssetsToSD(this);
    }
}
