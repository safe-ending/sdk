package com.eningqu.aipen.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.eningqu.aipen.activity.MainActivity;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.qpen.QPenManager;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/8/31 15:35
 * desc   :
 * version: 1.0
 */
public class SimpleActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    private boolean isForeground = false;//应用是否处于前端

    private Queue<String> activityNames = new ConcurrentLinkedQueue<>();
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        activityNames.add(activity.getLocalClassName());
        if(activity instanceof MainActivity){
//            isForeground = true;
            QPenManager.getInstance().cancelNotification();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

        activityNames.poll();

        if(activity instanceof MainActivity){
            QPenManager.getInstance().showNotification();
//            isForeground = false;
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public boolean isForeground() {
        if(activityNames.size()>0){
            return true;
        }
        return false;
    }
}
