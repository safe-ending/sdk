package com.eningqu.aipen.qpen.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.eningqu.aipen.R;
import com.eningqu.aipen.activity.MainActivity;
import com.eningqu.aipen.bean.AuthBaseBean;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.db.model.BluetoothDevice;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.receiver.NotificationBroadcastReceiver;
import com.google.gson.Gson;

import nq.com.ahlibrary.BaseAHUtil;
import nq.com.ahlibrary.utils.AhUtil;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/5/29 14:20
 * desc   : 笔画书写后台服务
 * version: 1.0
 */
public class BgWriterService extends Service {

    public final static String TAG = BgWriterService.class.getSimpleName();
    private NotificationManager notificationManager;
    private NotificationCompat.Builder mBuilder;
    private Notification notification;
    private PowerManager.WakeLock mWakeLock;
    private final int NOTI_ID = 2;
    private final String channelID = "com.eningqu.aipen.1";
    private final String channelName = "channel_name1";

    private final int MSG_WHAT_GET_BATTERY = 2;//获取电量

    private int retryCount = 0;//获取授权重复次数

    private BgWriterBinder mBinder = new BgWriterBinder();

    private BaseAHUtil mBaseAhUtil;

    public class BgWriterBinder extends Binder {
        public BgWriterService getInstance() {
            return BgWriterService.this;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WHAT_GET_BATTERY:
//                    if (AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.CONNECTED) {
//                        AFPenClientCtrl.getInstance().requestBatInfo();
//                        mHandler.sendEmptyMessageDelayed(MSG_WHAT_GET_BATTERY, 180 * 1000);
//                    }
                    break;

            }
        }
    };

    /**
     * 在切换至后台或者退出的时候  保存
     *//*
    protected synchronized void save(final Context context, final int curPageNum) {
        NoteBookData noteBookData = AppCommon.selectNotebook(AppCommon.getCurrentNotebookId());
        if (null == noteBookData) {
            L.error(TAG, "save() noteBookData is null");
        } else {
            if (!noteBookData.isLock) {
                //保存数据
                final long start_time = System.currentTimeMillis();
                AppCommon.saveCurrentPage(context, curPageNum, new IQPenSaveCurPageListener() {
                    @Override
                    public void onSuccessful() {
                        L.info(TAG, "save page " + curPageNum + " cost time=" + (System.currentTimeMillis() - start_time));
                    }

                    @Override
                    public void onFail() {

                    }
                });

            } else {
                L.error(TAG, "save() noteBookData is locked");
            }
        }
    }*/
    @Override
    public void onCreate() {
        super.onCreate();
        //        mContext = this;
        L.debug(TAG, "onCreate");
        init();
        mHandler.sendEmptyMessageDelayed(MSG_WHAT_GET_BATTERY, 20 * 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.debug(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("测试方法","onDestroy Bgwrite");

        if (notificationManager != null){
            notificationManager.cancelAll();
        }
        L.debug(TAG, "onDestroy");
        unInit();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        L.info(TAG, "onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        L.info(TAG, "onTrimMemory level=" + level);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private void init() {
        L.info(TAG, "init");
        acquireWakeLock();
        //初始化智能笔afpensdk-SDK
        AFPenClientCtrl.getInstance().init(getApplicationContext());

        if (null == mBaseAhUtil) {
            mBaseAhUtil = new BaseAHUtil(getApplicationContext());
        }
    }

    private void unInit() {
        L.info(TAG, "unInit");
        releaseWakeLock();
        if (null != notificationManager) {
            notificationManager.cancelAll();
        }

//        IInkSdkManager.getInstance().unInit();
    }

    public static void start(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            context.getApplicationContext().startForegroundService(new Intent(context, BgWriterService.class));
        } else {
            context.getApplicationContext().startService(new Intent(context, BgWriterService.class));
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    private void acquireWakeLock() {
        if (this.mWakeLock == null) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            this.mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "myService");
            if (this.mWakeLock != null && !mWakeLock.isHeld()) {
                this.mWakeLock.acquire();
            }
        }
    }

    private void releaseWakeLock() {
        if (this.mWakeLock != null) {
            this.mWakeLock.release();
            this.mWakeLock = null;
        }
    }

    private void initNotif() {

        /**
         *  创建通知栏管理工具
         */
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        /**
         *  实例化通知栏构造器
         */
        mBuilder = new NotificationCompat.Builder(this);
        /**
         *  设置Builder
         */
        //设置标题
        mBuilder.setContentTitle(getString(R.string.app_name))
                //设置内容
                .setContentText(getString(R.string.running_in_the_background))
                //设置大图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                //设置通知时间
                .setWhen(System.currentTimeMillis())
                //首次进入时显示效果
                .setTicker(getString(R.string.app_name))
                //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
//                .setDefaults(Notification.DEFAULT_SOUND);
                .setOnlyAlertOnce(true);
        //发送通知请求
        //        notificationManager.notify(10, mBuilder.build());
        Intent intentClick = new Intent(this, MainActivity.class);
        intentClick.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntentClick = PendingIntent.getActivity(this, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);

        Intent intentCancel = new Intent(this, NotificationBroadcastReceiver.class);
        intentCancel.setAction("notification_cancelled");
        intentCancel.putExtra(NotificationBroadcastReceiver.TYPE, 1);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, 0, intentCancel, PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentIntent(pendingIntentClick);
        mBuilder.setDeleteIntent(pendingIntentCancel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_MIN);
            channel.setSound(null, null);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            //创建通知时指定channelID
            mBuilder.setChannelId(channelID);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);//统一消除声音和震动
/*            mBuilder.setContent(remoteViews);
            if (progress == 1) {
                mBuilder.setDefaults(Notification.DEFAULT_SOUND);
            }
            remoteViews.setImageViewResource(R.id.image, R.mipmap.timg);
            remoteViews.setTextViewText(R.id.title, "我是标题");
            remoteViews.setTextViewText(R.id.content, "我是内容");
            remoteViews.setProgressBar(R.id.pBar, 10, progress, false);
            remoteViews.setTextViewText(R.id.proNum, progress + "/10");
            notificationManager.notify(10, mBuilder.build());*/
//            notificationManager.notify(NOTI_ID, mBuilder.build());
            //            showNotification();
            notification = mBuilder.build();

        }
//        else {
        //            notification = new Notification();
        ////            notification.flags = 2;
        ////            notification.flags |= 32;
        ////            notification.flags |= 64;
        ////            notification.flags = Notification.FLAG_AUTO_CANCEL;
        ////            startForeground(1, notification);
        //            showNotification();
//        }
        notificationManager.notify(NOTI_ID, mBuilder.build());
    }

    public void showNotification() {
        initNotif();
        if (null != notification) {
            notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
            startForeground(NOTI_ID, notification);
        }
    }

    public void cancelNotification() {
        if (null != notification) {
            notificationManager.cancel(NOTI_ID);
            notificationManager.cancelAll();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    notificationManager.deleteNotificationChannel(channelID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void toCheckAuth() {

        String btAddr = AFPenClientCtrl.getInstance().getLastTryConnectAddr();

        if (TextUtils.isEmpty(btAddr)) {
            BluetoothDevice bluetoothData = AppCommon.loadBleInfo2();
            if (TextUtils.isEmpty(btAddr) || null == bluetoothData || TextUtils.isEmpty(bluetoothData.bleMac)) {
                L.error(TAG, "BLE mac is null");
                return;
            }
            btAddr = bluetoothData.bleMac;
        }
        //        mBinding.etHwrContent.setText(getString(R.string.tran_translation)+"...");
        final String mac = btAddr.contains("-") ? btAddr.substring(btAddr.indexOf("-") + 1) : btAddr;//60HW-C3:49:D4:23:BC:BC

        mBaseAhUtil.setImei("android123456789");
        mBaseAhUtil.ieAndBleLocal(mac, Constant.pkg, new AhUtil.AhGetListener() {
            @Override
            public void onFailure(final String s) {
                L.error(s);
                sendEventMsg(Constant.NQ_SER_AUTH_FAIL, "mac=" + mac);
                SpUtils.putInt(BgWriterService.this, Constant.SP_KEY_INIT_PEN, -2);
                if (retryCount < 3) {
                    retryCount++;
                    toCheckAuth();
                }
            }

            @Override
            public void onResponse(String s) {
                L.info(s);
                Gson gson = new Gson();
                try {
                    final AuthBaseBean authBaseBean = gson.fromJson(s, AuthBaseBean.class);
                    if (null != authBaseBean && authBaseBean.isSuccess()) {
                        SpUtils.putInt(BgWriterService.this, Constant.SP_KEY_INIT_PEN, 1);
                        SpUtils.putString(BgWriterService.this, Constant.SP_KEY_AUTH_PEN, mac);
                        SpUtils.putLong(BgWriterService.this, Constant.SP_KEY_AUTH_PEN_TIME, System.currentTimeMillis());

                        sendEventMsg(Constant.NQ_SER_AUTH_SUCCESS, mac);
//                        ThreadUtils.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtils.showShort("授权成功");
//                            }
//                        });
                    } else {
                        SpUtils.putInt(BgWriterService.this, Constant.SP_KEY_INIT_PEN, -2);
                        sendEventMsg(Constant.NQ_SER_AUTH_FAIL, "mac=" + mac);
                        SpUtils.putString(BgWriterService.this, Constant.SP_KEY_AUTH_PEN, "");
                    }
                    retryCount = 0;
                } catch (Exception e) {
                    L.error(e.getMessage());
                    if (retryCount < 3) {
                        retryCount++;
                        toCheckAuth();
                    }
                }
            }

            @Override
            public void onShowMate(String s) {
                L.error(s);
//                showToast("result:" + s);
                sendEventMsg(Constant.NQ_SER_AUTH_FAIL, s + "mac=" + mac);
            }
        });
    }

    private void sendEventMsg(int type, Object object) {
        EventBusCarrier eventBusCarrier = new EventBusCarrier();
        eventBusCarrier.setObject(object);
        eventBusCarrier.setEventType(type);
        EventBusUtil.postSticky(eventBusCarrier);
    }
}
