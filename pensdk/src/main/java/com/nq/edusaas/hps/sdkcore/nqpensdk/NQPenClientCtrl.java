package com.nq.edusaas.hps.sdkcore.nqpensdk;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.eningqu.aipen.sdk.NQPenSDK;
import com.eningqu.aipen.sdk.bean.device.NQBtDevice;
import com.eningqu.aipen.sdk.bean.device.NQDeviceBase;
import com.eningqu.aipen.sdk.comm.ConnectState;
import com.eningqu.aipen.sdk.comm.ScanListener;
import com.eningqu.aipen.sdk.comm.utils.LogUtils;
import com.eningqu.aipen.sdk.listener.InitListener;
import com.eningqu.aipen.sdk.listener.PenConnectListener;
import com.eningqu.aipen.sdk.listener.PenDotListener;
import com.eningqu.aipen.sdk.listener.PenMsgListener;
import com.eningqu.aipen.sdk.listener.PenOfflineDataListener;
import com.eningqu.aipen.sdk.listener.TestListener;
import com.nq.edusaas.hps.PenSdkCtrl;
import com.nq.edusaas.hps.model.enummodel.NoteTypeEnum;
import com.nq.edusaas.hps.model.enummodel.PEN_CONN_STATUS;
import com.nq.edusaas.hps.sdkcore.IPenSdkCtrl;
import com.nq.edusaas.hps.sdkcore.afpensdk.Const;
import com.nq.edusaas.hps.utils.PenNQLog;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;


public class NQPenClientCtrl implements IPenSdkCtrl {
    private static NQPenClientCtrl instance;
    private Context mContext;
    PowerManager.WakeLock mWakeLock;
    /**
     * 连接回调
     */
    private PenConnectListener mConnectListener;
    /**
     * 扫描回调
     */
    private ScanListener mScanListener;
    /**
     * 当前连接的设备
     */
    private NQDeviceBase mCurNQDev;
    /**
     * 默认的连接方式
     */
    NQPenSDK.CONN_TYPE mConnType = NQPenSDK.CONN_TYPE.USB;

    /**
     * 连接状态
     */
    int mConnState = ConnectState.CONN_STATE_CLOSED;

    /**
     * 页面尺寸列表
     */
//    List<PageSize> mPageList = new ArrayList<>();
    public static NQPenClientCtrl getInstance() {

        if (null == instance) {
            synchronized (NQPenClientCtrl.class) {

                if (null == instance) {
                    instance = new NQPenClientCtrl();
                }
            }
        }

        return instance;
    }

    /**
     * SDK初始化
     *
     * @param context
     */
    @Override
    public void init(@NonNull Context context, NQPenSDK.CONN_TYPE type) {
        mContext = context;
        mConnType = type;
        //初始化
        NQPenSDK.getInstance().init(context, type, initListener);
        //设置原点在左下角
//        NQPenSDK.getInstance().setCoordinateOrigin(NQPenSDK.ORIGIN_POSITION.LEFT_BOTTOM);
        //初始化页面尺寸
        setDefaultPageSize();
        //获取电源锁，保持CPU运转，保持屏幕常亮(亮度低)
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, NQPenClientCtrl.class.getName());

        if (null != mWakeLock) {

            mWakeLock.acquire();
        }

        //设置远程解析
//        NQPenSDK.getInstance().setRemoteParse(false);
    }

    @Override
    public void release() {
        setCurNQDev(null);
        //释放SDK资源
        NQPenSDK.getInstance().release();
        //释放电源锁
        if (null != mWakeLock && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    @Override
    public void parseByLocalLib(byte[] data, byte version) {
        NQPenSDK.getInstance().parseByLocalLib(data, version);
    }

    @Override
    public NQDeviceBase getCurNQDev() {
        return mCurNQDev;
    }

    @Override
    public void setCurNQDev(NQDeviceBase curNQDev) {
        this.mCurNQDev = curNQDev;
    }

    /**
     * SDK初始化回调
     */
    InitListener initListener = new InitListener() {
        @Override
        public void success(NQPenSDK.CONN_TYPE var1) {
            PenNQLog.debug("init sdk success...");
            //设置扫描连接监听
//            PenSdkCtrl.getInstance().setPenConnectListener(connectListener);
            //查找设备
//            startScanDevice();
        }

        @Override
        public void failure(int error, String message) {
            PenNQLog.error("init sdk failure error=" + error + ", message=" + message);
        }
    };

    /**
     * 设置默认码点页面
     */
    public void addPageSizeByPaperSize(float w, float h, int bookNo, int pageFrom, int pageTo){
        //设置bookNum对应的页面尺寸和页码范围
        //此处以设置A4 bookNum=1和A5 bookNum=2为例
        NQPenSDK.getInstance().addPageSizeByPaperSize(w, h, bookNo, pageFrom, pageTo);
//        NQPenSDK.getInstance().addPageSize(210, 285, 1, 0, 30000);
    }

    /**
     * 设置默认码点页面
     */
    public void setDefaultPageSize() {
        //设置bookNum对应的页面尺寸和页码范围
        //此处以设置A4 bookNum=1和A5 bookNum=2为例
//        NQPenSDK.getInstance().addPageSize(144, 211, 1, 0, 300);
        NQPenSDK.getInstance().addPageSizeByPaperSize(144, 211, 1, 0, 300);

//        NQPenSDK.getInstance().addPageSize(210, 285, 1, 0, 30000);

    }

    /**
     * 设置0-300页适配A4纸张
     */
    public void setPageSizeForA4() {
//        NQPenSDK.getInstance().addPageSize(210, 297, Const.PageSizeType.BOOK_NUM_A4, 0, 300);
    }

    /**
     * 设置0-300页适配A5纸张
     */
    public void setPageSizeForA5() {
//        NQPenSDK.getInstance().addPageSize(148, 210, NoteTypeEnum.NOTE_TYPE_A5.getNoeType(), 0, 300);
    }

    @Override
    public NQPenSDK.CONN_TYPE getConnType() {
        return NQPenSDK.getInstance().getConnType();
    }

    @Override
    public int getConnectState() {
        return this.mConnState;
    }


    /**
     * 搜索设备
     *
     * @return
     */
    @Override
    public int startScanDevice() {
        return NQPenSDK.getInstance().startScanDevice();
    }

    /**
     * 停止搜索
     */
    @Override
    public void stopScan() {
        NQPenSDK.getInstance().stopScan();
    }

    /**
     * 连接设备
     *
     * @param deviceBase
     */
    @Override
    public void connect(NQDeviceBase deviceBase) {
        NQPenSDK.getInstance().connect(deviceBase);
    }

    /**
     * 断开连接
     */
    @Override
    public void disconnect() {
        NQPenSDK.getInstance().disconnect();
    }

    /**
     * 返回当前连接的设备
     *
     * @return
     */
    @Override
    public NQDeviceBase getConnectedDevice() {
        return NQPenSDK.getInstance().getConnectedDevice();
    }

    /**
     * 获取固件版本
     */
    @Override
    public void requestFirWareVersion() {
        NQPenSDK.getInstance().requestFirWareVersion();
    }

    /**
     * 获取sdk版本
     */
    @Override
    public String requestSdkVersion() {
        return NQPenSDK.getInstance().requestSdkVersion();
    }

    @Override
    public String requestMcuVersion() {
        return NQPenSDK.getInstance().requestMcuVersion();
    }

    /**
     * 获取电量信息
     */
    @Override
    public void requestBatInfo() {
        NQPenSDK.getInstance().requestBatInfo();
    }

    @Override
    public void requestOfflineDataLength() {
        NQPenSDK.getInstance().requestOfflineDataLength();
    }


    /**
     * 获取离线数据
     */
    @Override
    public void requestOfflineDataWithRange() {
        NQPenSDK.getInstance().requestOfflineDataWithRange();
    }

    /**
     * 删除离线数据
     */
    @Override
    public void requestDeleteOfflineData() {
        NQPenSDK.getInstance().requestDeleteOfflineData();
    }

    /**
     * 修改蓝牙设备名称
     *
     * @param name
     */
    @Override
    public void editBleDeviceName(String name) {
        NQPenSDK.getInstance().editBleDeviceName(name);
    }


    public void readDeviceSerialNumber() {
//        NQPenSDK.getInstance().readDeviceSerialNumber();
    }


    public void getSerialDevId() {

    }


    public void getSerialDevFlash() {

    }


    public void setSerialDevFlash(byte[] bytes) {

    }


    public void setSerialDevId(byte[] bytes) {

    }


    public void getSerialDevVer() {

    }

    /**
     * 设置蓝牙搜索名称过滤关键字
     *
     * @param value 格式："name1|name2"
     */
    public void setBtScannerFilterValue(String value) {
        NQPenSDK.getInstance().setBtScannerFilterValue(value);
    }

    /**
     * 设置实时书写数据监听
     *
     * @param listener
     */
    @Override
    public void setDotListener(PenDotListener listener) {
        NQPenSDK.getInstance().setDotListener(listener);
    }

    @Override
    public void setPenOfflineDataListener(PenOfflineDataListener listener) {
        NQPenSDK.getInstance().setPenOfflineDataListener(listener);
    }

    @Override
    public void setPenConnectListener(PenConnectListener listener) {
        //设置扫描连接监听
        NQPenSDK.getInstance().setPenConnectListener(listener);
    }

    /**
     * 设置连接监听
     *
     * @param connectListener
     */
    @Override
    public void setConnectListener(PenConnectListener connectListener) {
        NQPenSDK.getInstance().setPenConnectListener(connectListener);
    }

    public void setScanListener(ScanListener scanListener) {
        NQPenSDK.getInstance().setScanListener(scanListener);
    }

    public void setPenMsgListener(PenMsgListener penMsgListener) {
        NQPenSDK.getInstance().setPenMsgListener(penMsgListener);
    }

    public void setTestListener(TestListener testListener) {
        NQPenSDK.getInstance().setTestListener(testListener);
    }

    public void saveBmp() {
        NQPenSDK.getInstance().saveBmp();
    }
}
