package com.eningqu.aipen.p20;

import android.content.Context;

import com.nq.edusaas.hps.PenSdkCtrl;


public class StudentAppManager {

    private static StudentAppManager sInstance;

    public static StudentAppManager getInstance() {
        if (null == sInstance) {
            synchronized (StudentAppManager.class) {
                if (null == sInstance) {
                    sInstance = new StudentAppManager();
                }
            }
        }
        return sInstance;
    }



    public void init(Context context) {
        //初始化智能笔SDK
        PenSdkCtrl.getInstance().init(context);
    }

    public void unInit() {
        PenSdkCtrl.getInstance().unInit();
    }

}
