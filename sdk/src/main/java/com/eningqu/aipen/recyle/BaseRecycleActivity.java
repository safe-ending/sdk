package com.eningqu.aipen.recyle;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class BaseRecycleActivity extends AppCompatActivity {
	@Override
    protected synchronized void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.e("测试崩溃","当前页面"+this.getClass().getSimpleName() +"->"+AppStatusManager.getInstance().getAppStatus());
//
//        //判断app状态
//        if (AppStatusManager.getInstance().getAppStatus() == AppStatus.STATUS_RECYCLE){
//            //被回收，跳转到启动页面
//            ActivityUtils.finishAllActivities();
//
//            if (AppStatusManager.getInstance().getAppStatus() == AppStatus.STATUS_RECYCLE) {
//                Intent intent = new Intent(this, WelcomeActivity.class);
//                startActivity(intent);
//            }
//            //app状态改为正常
//            AppStatusManager.getInstance().setAppStatus(AppStatus.STATUS_NORMAL);
//            Log.e("测试崩溃","to WelcomeActivity"+"时"+AppStatusManager.getInstance().getAppStatus());
//        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

    }
}