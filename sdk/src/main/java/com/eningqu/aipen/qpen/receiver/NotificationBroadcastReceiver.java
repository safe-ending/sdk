package com.eningqu.aipen.qpen.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eningqu.aipen.activity.MainActivity;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/3/28 15:42
 * desc   :
 * version: 1.0
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = NotificationBroadcastReceiver.class.getSimpleName();
    public static final String TYPE = "type";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int type = intent.getIntExtra(TYPE, -1);
        if (type != -1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(type);
        }

        if (action.equals("notification_clicked")) {
            //处理点击事件
            Intent startMainAct = new Intent(context, MainActivity.class);
            context.startActivity(startMainAct);
        } else if (action.equals("notification_cancelled")) {
            //处理滑动清除和点击删除事件
        }
    }
}
