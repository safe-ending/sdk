//package com.nq.edusaas.hps.service;
//
//import android.annotation.SuppressLint;
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Binder;
//import android.os.Build;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.os.PowerManager;
//
//import com.nq.edusaas.hps.PenSdkCtrl;
//import com.nq.edusaas.hps.model.enummodel.PEN_CONN_STATUS;
//import com.nq.edusaas.hps.utils.PenNQLog;
//
//import java.lang.ref.WeakReference;
//
//import androidx.annotation.Nullable;
//import androidx.core.app.NotificationCompat;
//import nq.com.ahlibrary.BaseAHUtil;
//
///**
// * author : LiQiu
// * e-mail : lq@eningqu.com
// * date   : 2019/5/29 14:20
// * desc   : 笔画书写后台服务
// * version: 1.0
// */
//public abstract class BgWriterService extends Service {
//
//    public final static String TAG = BgWriterService.class.getSimpleName();
//    public NotificationManager notificationManager;
//    public NotificationCompat.Builder mBuilder;
//    public Notification notification;
//    private PowerManager.WakeLock mWakeLock;
//    public final int NOTI_ID = 2;
//    public final String channelID = "com.nq.edusaas.hps";
//
//    private final int MSG_WHAT_GET_BATTERY = 2;//获取电量
//    private final int MSG_WHAT_GET_AUTH = 3;//获取授权
//
//    private int retryCount = 0;//获取授权重复次数
//
//    private BgWriterBinder mBinder = new BgWriterBinder();
//
//    private BaseAHUtil mBaseAhUtil;
//
//    public class BgWriterBinder extends Binder {
//        public BgWriterService getInstance() {
//            return BgWriterService.this;
//        }
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        PenNQLog.debug(TAG, "onCreate");
//        init();
//        mHandler.sendEmptyMessageDelayed(MSG_WHAT_GET_BATTERY, 20 * 1000);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        PenNQLog.debug(TAG, "onStartCommand");
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        PenNQLog.debug(TAG, "onDestroy");
//        unInit();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        PenNQLog.info(TAG, "onLowMemory");
//    }
//
//    @Override
//    public void onTrimMemory(int level) {
//        super.onTrimMemory(level);
//        PenNQLog.info(TAG, "onTrimMemory level=" + level);
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return mBinder;
//    }
//
//    private void init() {
//        PenNQLog.info(TAG, "init");
//        mHandler = new UIHandler(this);
//        acquireWakeLock();
//        if (null == mBaseAhUtil) {
//            mBaseAhUtil = new BaseAHUtil(getApplicationContext());
//        }
//    }
//
//    private void unInit() {
//        PenNQLog.info(TAG, "unInit");
//        mHandler.removeCallbacksAndMessages(null);
//        releaseWakeLock();
//        if (null != notificationManager) {
//            notificationManager.cancelAll();
//        }
//    }
//
//    public static void start(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.getApplicationContext().startForegroundService(new Intent(context, BgWriterService.class));
//        } else {
//            context.getApplicationContext().startService(new Intent(context, BgWriterService.class));
//        }
//    }
//
//    @SuppressLint("InvalidWakeLockTag")
//    private void acquireWakeLock() {
//        if (this.mWakeLock == null) {
//            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//            assert pm != null;
//            this.mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "myService");
//            if (this.mWakeLock != null && !mWakeLock.isHeld()) {
//                this.mWakeLock.acquire(/*10*60*1000L*/ /*10 minutes*/);
//            }
//        }
//    }
//
//    private void releaseWakeLock() {
//        if (this.mWakeLock != null) {
//            this.mWakeLock.release();
//            this.mWakeLock = null;
//        }
//    }
//
//    public abstract void initNotif();
//
//    public void showNotification() {
//        initNotif();
//        if (null != notification) {
//            notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
//            startForeground(NOTI_ID, notification);
//        }
//    }
//
//    public void cancelNotification() {
//        if (null != notification) {
//            notificationManager.cancel(NOTI_ID);
//            notificationManager.cancelAll();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                notificationManager.deleteNotificationChannel(channelID);
//            }
//        }
//    }
//
//    public void toCheckAuth() {
//
//        /*String btAddr = PenSdkCtrl.getInstance().getLastTryConnectAddr();
//
//        BluetoothData bluetoothData = DatabaseOperate.getBluetooth();
//        if (TextUtils.isEmpty(btAddr) || null == bluetoothData || TextUtils.isEmpty(bluetoothData.bleMac)) {
//            NingQuLog.error(TAG, "BLE mac is null");
//            return;
//        }
//
//        if (TextUtils.isEmpty(btAddr)) {
//            btAddr = bluetoothData.bleMac;
//        }
//        final String mac = btAddr.contains("-") ? btAddr.substring(btAddr.indexOf("-") + 1) : btAddr;//60HW-C3:49:D4:23:BC:BC
//
//        mBaseAhUtil.setImei("android123456789");
//        //授权暂时隐藏
//        SPUtils.putInt(SPUtils.SP_KEY_INIT_PEN, 1, false);
//        mBaseAhUtil.ieAndBleLocal(mac, BuildConfig.APPLICATION_ID, new AhUtil.AhGetListener() {
//            @Override
//            public void onFailure(final String s) {
//                NingQuLog.error(s);
//                sendEventMsg(Const.Constant.NQ_SER_AUTH_FAIL, "mac=" + mac);
//                SPUtils.putInt(SPUtils.SP_KEY_INIT_PEN, -2, false);
//            }
//
//            @Override
//            public void onResponse(String s) {
//                NingQuLog.info(s);
//                Gson gson = new Gson();
//                try {
//                    final AuthBaseBean authBaseBean = gson.fromJson(s, AuthBaseBean.class);
//                    if (null != authBaseBean && authBaseBean.isSuccess()) {
//                        SPUtils.putInt(SPUtils.SP_KEY_INIT_PEN, 1, false);
//                        sendEventMsg(Const.Constant.NQ_SER_AUTH_SUCCESS, mac);
//                    } else {
//                        SPUtils.putInt(SPUtils.SP_KEY_INIT_PEN, -1, false);
//                        sendEventMsg(Const.Constant.NQ_SER_AUTH_FAIL, "mac=" + mac);
//                        retryCount = 0;
//                    }
//                } catch (Exception e) {
//                    NingQuLog.error(e.getMessage());
//                }
//            }
//
//            @Override
//            public void onShowMate(String s) {
//                NingQuLog.error(s);
//                sendEventMsg(Const.Constant.NQ_SER_AUTH_FAIL, s + "mac=" + mac);
//            }
//        });*/
//    }
//
//    private void sendEventMsg(int type, Object object) {
////        EventBusCarrier eventBusCarrier = new EventBusCarrier();
////        eventBusCarrier.setObject(object);
////        eventBusCarrier.setEventType(type);
////        EventBusUtil.postSticky(eventBusCarrier);
//    }
//
////    @Subscribe(threadMode = ThreadMode.MAIN)
////    public void onBaseEventListener(EventBusCarrier carrier) {
////        if (carrier != null && carrier.getEventType() == ACTION_START_MICRO_LESSON) {
////            Intent intent = new Intent(ActivityUtils.getTopActivity(), MicroLessonChatActivity.class);
////            intent.putExtra(Const.IntentData.CHANNEL_ID, (String) carrier.getObject());
////            ActivityLauncher.start(ActivityUtils.getTopActivity(), intent);
////        }
////    }
//
//
//    private UIHandler mHandler;
//
//    private static class UIHandler extends Handler {
//        WeakReference<BgWriterService> softReference;
//
//        UIHandler(BgWriterService activity) {
//            softReference = new WeakReference<BgWriterService>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            BgWriterService service = softReference.get();
//            if (null != service) {
//                if (service.MSG_WHAT_GET_BATTERY == msg.what) {
//                    if (PenSdkCtrl.getInstance().getConnectState() == PEN_CONN_STATUS.CONNECTED.getState()) {
//                        PenSdkCtrl.getInstance().requestBatInfo();
//                        service.mHandler.sendEmptyMessageDelayed(service.MSG_WHAT_GET_BATTERY, 180 * 1000);
//                    }
//                } else if (service.MSG_WHAT_GET_AUTH == msg.what) {
//                    service.toCheckAuth();
//                }
//            }
//        }
//    }
//}
