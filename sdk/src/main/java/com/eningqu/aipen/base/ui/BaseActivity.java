package com.eningqu.aipen.base.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.eningqu.aipen.R;
import com.eningqu.aipen.activity.BaseRecycleActivity;
import com.eningqu.aipen.base.ActivityStackManager;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.DialogHelper;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.common.bluetooth.BlueToothLeClass;
import com.eningqu.aipen.common.bluetooth.BluetoothClient;
import com.eningqu.aipen.common.dialog.BaseDialog;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.enums.LifeCycleEvent;
import com.eningqu.aipen.common.utils.AudioUtil;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.VibratorUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.subjects.PublishSubject;


/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/17 19:06
 */

public abstract class BaseActivity extends BaseRecycleActivity {

    public final static int REQUEST_CODE_BLUETOOTH_ENABLE = 101;
    public final static int REQUEST_CODE_LOCATION_SETTINGS = 102;
    public final static int REQUEST_CODE_DEL_NOTEBOOK = 103;

    public static final String NOTEBOOK_ID = "notebook_id";
    public static final String PAGE_NUM = "page_num";
    public static final String NOTE_NAME = "note_name";
    public static final String NOTE_TYPE = "note_type";
    public static final String PAGE_LABEL_NAME = "page_label_name";
    public static final String OFFLINE_STATUS = "offline_status";

    private Unbinder unbinder;

    //    private ActivityStackManager mStackManager;

    //用于控制retrofit的生命周期，以便在destroy或其他状态时终止网络请求
    public PublishSubject<LifeCycleEvent> lifecycleSubject = PublishSubject.create();

    protected BlueToothLeClass ble = BluetoothClient.getBle();

    protected BaseDialog dialog;
    protected BaseDialog mDialogOfflineData;
    protected BaseDialog mDialogConn;

    protected Context mContext;

    protected String curRecordFileName;
    protected int statusBarHeight;
    protected BaseDialog initDialog;

    //获取状态栏高度
    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        L.info("UI", "onCreate " + this.getLocalClassName());
//        SmartPenApp.getApp().addActivity(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        //竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        statusBarHeight = getStatusBarHeight(this);
        boolean oppoScreen = getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
//        boolean featureSupportInVIVO = isFeatureSupportInVIVO(this, NOTCH_IN_SCREEN_VOIO_MARK);
        if (oppoScreen)
            getWindow().getDecorView().setPadding(0, statusBarHeight, 0, 0);

        setLayout();
        //        if (SmartPenApp.isFirst){
        EventBusUtil.register(this);
        //        }
        unbinder = ButterKnife.bind(this);
        init();
        initData();
        initView();
        initEvent();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //        getNotchParams();
    }

    @Override
    protected void onStart() {
        //        EventBusUtil.register(this);
        super.onStart();
        L.info("UI", "onStart " + this.getLocalClassName());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        L.info("UI", "onRestart " + this.getLocalClassName());
    }

    @Override
    protected void onResume() {
        dismissDialog();
        super.onResume();
        L.info("UI", "onResume " + this.getLocalClassName());
    }

    @Override
    protected void onPause() {
        lifecycleSubject.onNext(LifeCycleEvent.PAUSE);
        if (initDialog != null)
            initDialog.dismiss();
        super.onPause();
        L.info("UI", "onPause " + this.getLocalClassName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        L.info("UI", "onSaveInstanceState " + this.getLocalClassName());
    }

    @Override
    protected void onStop() {
        //        EventBusUtil.unregister(this);
        lifecycleSubject.onNext(LifeCycleEvent.STOP);
        super.onStop();
        L.info("UI", "onStop " + this.getLocalClassName());
    }

    @Override
    protected void onDestroy() {
        dismissDialog();
        dismissDialogConn();
        dismissDialogOfflineData();
        EventBusUtil.unregister(this);
        unbinder.unbind();
        //此处需要注释掉popOneActivity，否则影响其他页面在书写时调到书写界面
        //        ActivityStackManager.getInstance().popOneActivity(this);
        lifecycleSubject.onNext(LifeCycleEvent.DESTROY);
        mContext = null;
        ble = null;
        super.onDestroy();
        lifecycleSubject = null;
        ActivityStackManager.getInstance().exitActivity(this);
        L.info("UI", "onDestroy " + this.getLocalClassName());
    }

    private void init() {
        ActivityStackManager.getInstance().pushOneActivity(this);
    }

    /***--------- 返回键返回事件 --------*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mOnKeyClickListener != null) {//如果没有设置返回事件的监听，则默认finish页面。
                    mOnKeyClickListener.clickBack();
                    return true;
                } else {
                    finish();
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long mExitTime;
    /**
     * ----------按键监听-------------
     */
    OnKeyClickListener mOnKeyClickListener;

    public void setOnKeyListener(OnKeyClickListener onKeyClickListener) {
        this.mOnKeyClickListener = onKeyClickListener;
    }

    public interface OnKeyClickListener {
        /**
         * 点击了返回键
         */
        void clickBack();
    }

    /**
     * 打开一个Activity 默认不关闭当前activity
     */
    protected void gotoActivity(Class<?> clz) {
        gotoActivity(clz, false);
    }

    protected void gotoActivity(Class<?> clz, boolean isCloseCurrentActivity) {
        gotoActivity(clz, isCloseCurrentActivity, null);
    }

    protected void gotoActivity(Class<?> clz, Bundle bundle) {
        gotoActivity(clz, false, bundle);
    }

    protected void gotoActivity(Class<?> clz, boolean isCloseCurrentActivity, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if (isCloseCurrentActivity) {
            finish();
        }
    }

    //    public ActivityStackManager getActivityStackManager() {
    //        return ActivityStackManager.getInstance();
    //    }

    public void dismissDialog() {
        L.error("dialog=" + dialog);
        if (dialog != null && !isFinishing() && !isDestroyed()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public void dismissDialogOfflineData() {
        L.error("dialog=" + mDialogOfflineData);
        if (mDialogOfflineData != null && !isFinishing() && !isDestroyed()) {
            mDialogOfflineData.dismiss();
            mDialogOfflineData = null;
        }
    }

    public void dismissDialogConn() {
        L.error("dialog=" + mDialogConn);
        if (mDialogConn != null && !isFinishing() && !isDestroyed()) {
            mDialogConn.dismiss();
            mDialogConn = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBaseEventListener(Message message) {
        switch (message.what) {
            case Constant.BLE_INTERRUPT_CODE:
                VibratorUtil.Vibrate(this, new long[]{10, 500, 100, 500, 100, 500}, false);
                dismissDialog();
                dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        dismissDialog();
                        /*BluetoothData bluetoothData = SQLite.select().from(BluetoothData.class).querySingle();
                        if (ble != null && bluetoothData != null) {
                            ble.connect(bluetoothData.bleMac.split("-")[1]);
                        }*/
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                        /*Message msg = new Message();
                        msg.what = Constant.CLOSE_DIALOG_CODE;
                        EventBusUtil.post(msg);*/
                    }
                }, R.string.ble_interrupted_text, 0);
                break;

            case Constant.BLE_CONNECT_SUCCESS_CODE:
                //                VibratorUtil.Vibrate(this, 2000);
                break;

            //            case Constant.HAS_HISTORY_CODE:
            //                dismissDialog();
            //                dialog = DialogHelper.showHistory(getSupportFragmentManager(), new HistoryOperateListener() {
            //                    @Override
            //                    public void receive() {
            //                        AppCommon.setHistoryData(true);
            //                        ble.startHistoryData();
            //                        dismissDialog();
            //                        gotoActivity(DrawNqActivity.class);
            //                    }
            //
            //                    @Override
            //                    public void ignore() {
            //                        ble.startRealData();
            //                        AppCommon.setHistoryData(false);
            //                        dismissDialog();
            //                    }
            //
            //                    @Override
            //                    public void delete() {
            //                        ble.clearHistoryData();
            //                        AppCommon.setHistoryData(false);
            //                        dismissDialog();
            //                        dialog = DialogHelper.showProgress(getSupportFragmentManager(), R.string.ble_history_data_delete);
            //                    }
            //                });
            //                break;

            case Constant.RECEIVE_HISTORY_FINISH_CODE:
                dismissDialog();
                break;

            case Constant.DELETE_HISTORY_FINISH_CODE:
                dismissDialog();
                break;
            case Constant.CLOSE_DIALOG_CODE:
                dismissDialog();
                break;

        }
    }

    protected abstract void setLayout();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initEvent();

    //    protected abstract void drawDot(NQDot dot);

    //    protected abstract void recordStop();

    protected void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(message);
            }
        });
    }

    protected void showToast(final int message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(message);
            }
        });
    }

    protected void startRecord() {
        //        curRecordFileName = System.currentTimeMillis() + ".pcm";
        //        AudioUtil.getInstance().stopRecord();
        //        AudioUtil.getInstance().createDefaultAudio();
        //        AudioUtil.getInstance().startRecord(this, AppCommon.getAudioPathDir(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), ""), curRecordFileName);//AudioUtil.folderName
        showToast(R.string.recording);
    }

    protected void pauseStopRecord() {
        //        AudioUtil.getInstance().pauseRecord();
        if (AudioUtil.getInstance().getRecStatus() == AudioUtil.REC_STATUS.STATUS_PAUSE) {
            showToast(R.string.record_restart);
        } else if (AudioUtil.getInstance().getRecStatus() == AudioUtil.REC_STATUS.STATUS_START) {
            showToast(R.string.record_pause);
        }
    }

    protected void stopRecord() {
        //        AudioUtil.getInstance().stopRecord();
        //降序排列，最新的一个录音加在第一个位置，如果searchFiles方法里面改为升序排列，就直接加在队列尾部
        //        String path = AppCommon.getAudioPathDir(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), curRecordFileName);
        //        if(null!=path){
        //            files.add(0, path);
        //        }
        showToast(R.string.record_stop);
    }

    @TargetApi(28)
    public void getNotchParams() {
        final View decorView = getWindow().getDecorView();

        decorView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    DisplayCutout displayCutout = decorView.getRootWindowInsets().getDisplayCutout();
                    Log.e("TAG", "安全区域距离屏幕左边的距离 SafeInsetLeft:" + displayCutout.getSafeInsetLeft());
                    Log.e("TAG", "安全区域距离屏幕右部的距离 SafeInsetRight:" + displayCutout.getSafeInsetRight());
                    Log.e("TAG", "安全区域距离屏幕顶部的距离 SafeInsetTop:" + displayCutout.getSafeInsetTop());
                    Log.e("TAG", "安全区域距离屏幕底部的距离 SafeInsetBottom:" + displayCutout.getSafeInsetBottom());

                    List<Rect> rects = displayCutout.getBoundingRects();
                    if (rects == null || rects.size() == 0) {
                        Log.e("TAG", "不是刘海屏");
                    } else {
                        Log.e("TAG", "刘海屏数量:" + rects.size());
                        for (Rect rect : rects) {
                            Log.e("TAG", "刘海屏区域：" + rect);
                        }
                    }
                } catch (NoSuchMethodError e) {
                    L.error(e.getMessage());
                }
            }
        });
    }

    public final int NOTCH_IN_SCREEN_VOIO_MARK = 0x00000020;//是否有凹槽

    public boolean isFeatureSupportInVIVO(Context context, int mark) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class ftFeature = cl.loadClass("android.util.FtFeature");
            Method get = ftFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) get.invoke(ftFeature, mark);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return ret;
        }
    }
}
