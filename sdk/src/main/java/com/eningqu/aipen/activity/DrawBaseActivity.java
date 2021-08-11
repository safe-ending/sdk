package com.eningqu.aipen.activity;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.eningqu.aipen.db.model.BluetoothDevice;
import com.eningqu.aipen.sdk.bean.NQDot;

import com.eningqu.aipen.sdk.bean.DotType;
import com.eningqu.aipen.R;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.CanvasFrame;
import com.eningqu.aipen.qpen.PEN_CONN_STATUS;
import com.eningqu.aipen.qpen.SDKUtil;
import com.eningqu.aipen.qpen.SignatureView;
import com.eningqu.aipen.qpen.bean.CommandBase;
import com.eningqu.aipen.qpen.bean.CommandSize;
import com.eningqu.aipen.qpen.bean.PageStrokesCacheBean;
import com.eningqu.aipen.qpen.bean.StrokesBean;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.bluetooth.BluetoothClient;
import com.eningqu.aipen.common.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.enums.NoteTypeEnum;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.LocationUtils;
import com.eningqu.aipen.db.model.BluetoothData;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.qpen.listener.IQPenSaveCurPageListener;
import com.eningqu.aipen.qpen.QPenManager;
import com.eningqu.aipen.sdk.bean.NQDot;
import com.eningqu.aipen.view.CustomPopWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/5/10 9:21
 * desc   : 首页和画图页的base activity
 * version: 1.0
 */
public abstract class DrawBaseActivity extends BaseActivity {
    private final static String TAG = DrawBaseActivity.class.getSimpleName();

    protected SignatureView mStrokeView;
    protected CanvasFrame canvasFrame;
    private boolean showBindingTips = true;//显示绑定提示
    //    private boolean notebookNotSelected = false;
    protected CustomPopWindow mCustomPopWindow;

    private boolean paintThreadWorking = false;
    private int paintPage;
    private final int MSG_WHAT_SHOW_PAGE = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WHAT_SHOW_PAGE:

                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        canvasFrame = null;
        super.onDestroy();
    }

    /**
     * 在切换至后台或者退出的时候  保存
     */
    protected synchronized void save(final Context context, final int curPageNum) {
        NoteBookData noteBookData = AppCommon.selectNotebook(AppCommon.getCurrentNotebookId());
        if (null == noteBookData) {
            L.error(TAG, "save() noteBookData is null");
        } else {
            if (!noteBookData.isLock) {
                //未锁住状态
                if (null != mStrokeView) {
                    //                    QPenManager.getInstance().setCurrentBitmap(mStrokeView.getSignatureBitmap());
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
                    L.error(TAG, "save() mStrokeView is null");
                }
            } else {
                L.error(TAG, "save() noteBookData is locked");
            }
        }
    }

    protected void save(int i) {
        //保存数据
        AppCommon.saveCurrentPage(i, mStrokeView.getSignatureBitmap());
    }

    /**
     * 切换页面
     *
     * @param pageNum 目标页码
     */
    protected void switchPage(int pageNum) {
        //        if(haveNewStroke){
        //            save(mContext, Common.getCurrentPage());//不同页码保存
        //        }
        L.info(TAG, this.getLocalClassName());
        AppCommon.setCurrentPage(pageNum);//切换页码
        showPage(pageNum, true);
    }


    /**
     * 显示某页
     *
     * @param pageNum
     * @param clean
     */
    protected void showPage(final int pageNum, final boolean clean) {
        if (null != mStrokeView && pageNum != -1) {
            L.info(TAG, "showPage pageNum=" + pageNum + " clean=" + clean);
            if (clean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStrokeView.clearPaint();
                    }
                });
                final long start_time = System.currentTimeMillis();
                AppCommon.switchPageData(AppCommon.getCurrentNoteType(), pageNum);
                L.info(TAG, "loadPageData cost time(ms)=" + (System.currentTimeMillis() - start_time));
                long start_time2 = System.currentTimeMillis();
                if (paintThreadWorking && paintPage != pageNum) {
                    paintThreadWorking = true;
                    paintCurPageStrokes();
                    paintThreadWorking = false;
                } else if (!paintThreadWorking) {
                    paintThreadWorking = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                paintCurPageStrokes();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    paintThreadWorking = false;
                }
                L.info(TAG, "showPage() mStrokeView=" + mStrokeView + ",pageNum=" + pageNum + ", clean=" + clean +
                        ", paint strokes cost time=" + (System.currentTimeMillis() - start_time2));
            }
        } else {
            L.error(TAG, "mStrokeView=null");
        }
    }

    /**
     * 画当前页缓存的笔画
     */
    protected void paintCurPageStrokes() {
        paintPage = AppCommon.getCurrentPage();
        PageStrokesCacheBean pageStrokesCacheBean = AppCommon.getCurPageStrokesCache();
        if (null != pageStrokesCacheBean && null != pageStrokesCacheBean.getStrokesBeans() && pageStrokesCacheBean.getStrokesBeans().size() > 0) {
            L.info(TAG, "pageStrokesCacheBean paintPage=" + paintPage + " strokes size=" + pageStrokesCacheBean.getStrokesBeans().size() + ", mStrokeView=" + mStrokeView);
            int i;
            //一笔一笔转换，包括每一笔的颜色和粗细大小、坐标
            List<NQDot> list = new ArrayList<>();
            List<Point> points;
            NQDot afDot;
            long time;

            for (StrokesBean strokesBean : pageStrokesCacheBean.getStrokesBeans()) {
                if (strokesBean == null) {
                    continue;
                }
//                list.clear();
//                time = strokesBean.getCreateTime();
                i = 0;
                points = strokesBean.getDots();

                if (null != points && points.size() > 1) {
                    try {
                        if (null != mStrokeView) {
                            mStrokeView.setPenSize(CommandSize.getSizeByLevel(strokesBean.getSizeLevel()));
                            mStrokeView.setPenColor(strokesBean.getColor());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    CommandBase commandBase = null;
                    NQDot lastDot = null;
                    boolean operateArea = true;
                    for (int k = 0; k < points.size(); k++) {
                        if (!operateArea) {
                            break;
                        }
                        Point point = points.get(k);
                        afDot = new NQDot();
                        afDot.x = point.x;
                        afDot.y = point.y;
                        if (k == 0) {
                            commandBase = SDKUtil.calculateADot(afDot.page, DotType.PEN_ACTION_DOWN, afDot.x, afDot.y);
                        }
                        if (commandBase == null) {
                            operateArea = false;
                        }

                        if (commandBase != null) {
                            CommandBase commandBase2 = SDKUtil.calculateADot(afDot.page, afDot.type, afDot.x, afDot.y);
                            if (commandBase2 == null || commandBase.getSizeLevel() != commandBase2.getSizeLevel() ||
                                    commandBase.getCode() != commandBase2.getCode()) {//不在功能区或不是同一个功能区
                                operateArea = false;
                            }
                        }
                    }

                    if (!operateArea) {

                        for (int j = 0; j < points.size(); j++) {
                            afDot = new NQDot();
                            afDot.x = points.get(j).x;
                            afDot.y = points.get(j).y;
                            if (j  == 0) {
                                afDot.type = DotType.PEN_ACTION_DOWN;
                            } else if (j == points.size() - 1){
                                afDot.type = DotType.PEN_ACTION_UP;
                            }else{
                                afDot.type = DotType.PEN_ACTION_MOVE;
                            }
                            if (lastDot != null) {//用上一个点和当前点比较，排除掉飞笔的点
                                int length = lastDot.x - afDot.x;
                                int lengthH = lastDot.y - afDot.y;
                                if (Math.abs(length) > 500 || Math.abs(lengthH) > 500) {
                                    afDot.x = lastDot.x;
                                    afDot.y = lastDot.y;
                                }
                            }
                            if (afDot.type == DotType.PEN_ACTION_DOWN || afDot.type == DotType.PEN_ACTION_MOVE)
                                lastDot = afDot;
                            else
                                lastDot = null;//换一笔，清除上一个点

                            afDot.bookNum = AppCommon.getCurrentNoteType();
                            if (afDot.bookNum == NoteTypeEnum.NOTE_TYPE_A5.getNoeType()) {
                                afDot.book_width = (int) SDKUtil.PAGER_WIDTH_A5;
                                afDot.book_height = (int) SDKUtil.PAGER_HEIGHT_A5;
                            } else {
                                afDot.bookNum = NoteTypeEnum.NOTE_TYPE_A5.getNoeType();
                                afDot.book_width = (int) SDKUtil.PAGER_WIDTH_A5;
                                afDot.book_height = (int) SDKUtil.PAGER_HEIGHT_A5;
                            }
                            afDot.page = pageStrokesCacheBean.getPage();
                            //                    list.add(afDot);
                            mStrokeView.addDot(afDot, false);
                        }
                    }

                }
            }

            try {
                mStrokeView.setPenColor(QPenManager.getInstance().getPaintCacheColor());
                mStrokeView.setPenSize(QPenManager.getInstance().getPaintSize());
                mStrokeView.invalidate();
                L.info(TAG, "paint strokes finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    protected void actionBtStateClick(AppCompatActivity activity) {
        if (AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.CONNECTED) {
            //连接状态
            gotoActivity(PenSettingActivity.class);
        } else {
            //未连接状态
            if (BluetoothClient.getBle().getBlueToothStatus()) {
//                gotoActivity(BleDeviceActivity.class);
                gotoActivity(DeviceLinkGuideActivity.class);
            } else {
                openBluetoothAndLocation(activity);
            }
        }
    }

    /**
     * 打开蓝牙，如系统版本高于23 则需打开定位功能
     */
    protected void openBluetoothAndLocation(AppCompatActivity activity) {
        //开启蓝牙
        if(null==ble){
//            appInit();
            return;
        }
        if (!ble.isSupportBlutooth()) {
            showToast(getResources().getString(R.string.bluetooth_not_support));
            return;
        } else {
            if (!ble.getBlueToothStatus()) {
                ble.openBlueTooth(activity, REQUEST_CODE_BLUETOOTH_ENABLE);
                return;
            }
        }

        //开启定位
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!LocationUtils.isLocationEnabled(mContext)) {
                dialog = DialogHelper.showGpsTips(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        dismissDialog();
                        LocationUtils.openLocationSettings(mContext);
                        startBleScan();
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                    }
                });
            } else {
                startBleScan();
            }
        } else {
            startBleScan();
        }
    }

    /**
     * 开启蓝牙设备扫描
     */
    private void startBleScan() {
        //restart次数
        //        if(isRestartTimes < 2){
        BluetoothDevice bluetoothData = AppCommon.loadBleInfo2();
        /*if (bluetoothData != null && !ble.connectStatus) {
            ble.startLeScan(Constant.BLE_SCAN_DUARTION);
            return;
        } else if (bluetoothData == null && !ble.connectStatus) {  // 提示蓝牙绑定
            showBindingBleTips();
        }*/
        //        }
//        AFPenClientCtrl.getInstance().btStopSearchPeripheralsList();
//        AFPenClientCtrl.getInstance().btStartForPeripheralsList();

        if (null != bluetoothData) {
            showBindingTips = false;
            //            if (AFPenClientCtrl.getInstance().getConnStatus() != PEN_CONN_STATUS.CONNECTED) {
            //                PenSdkCtrl.getInstance().connect(bluetoothData.bleName, bluetoothData.bleMac);
            //            }
        } else if (AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.DISCONNECTED) {
//            boolean myscriptInstall = SpUtils.getBoolean(this, Constant.SP_KEY_MYSCRIPT_INSTALL, true);
//            if (!myscriptInstall)
            showBindingBleTips();
        }
    }

    /**
     * 绑定BLE提示
     */
    private void showBindingBleTips() {
        if (showBindingTips) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissDialog();
                    dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                        @Override
                        public void confirm(View view) {
                            dismissDialog();
                            gotoActivity(DeviceLinkGuideActivity.class);
                        }

                        @Override
                        public void cancel() {
                            showBindingTips = false;
                            dismissDialog();
                        }
                    }, R.string.bind_ble_text, R.string.str_bind);
                }
            }, 1500);
        }
    }

}
