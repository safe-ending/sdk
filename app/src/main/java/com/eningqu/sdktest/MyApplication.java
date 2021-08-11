package com.eningqu.sdktest;

import android.app.Application;

import com.eningqu.aipen.SDKManager;

public class MyApplication extends Application {
    public static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SDKManager.getInstance().init(this,"com.eningqu.aipen","sdkTest");
    }


}
