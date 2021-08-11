package com.nq.edusaas.hps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.os.IBinder;
import android.util.Log;

import com.eningqu.aipen.sdk.NQPenSDK;
import com.eningqu.aipen.sdk.bean.device.NQDeviceBase;
import com.eningqu.aipen.sdk.comm.ScanListener;
import com.eningqu.aipen.sdk.comm.utils.BytesConvert;
import com.eningqu.aipen.sdk.listener.PenConnectListener;
import com.eningqu.aipen.sdk.listener.PenDotListener;
import com.eningqu.aipen.sdk.listener.PenMsgListener;
import com.eningqu.aipen.sdk.listener.PenOfflineDataListener;
import com.nq.edusaas.hps.model.command.CommandBase;
import com.nq.edusaas.hps.model.command.CommandColor;
import com.nq.edusaas.hps.model.command.CommandRecord;
import com.nq.edusaas.hps.model.command.CommandSound;
import com.nq.edusaas.hps.model.enummodel.PEN_CONN_STATUS;
import com.nq.edusaas.hps.model.enummodel.PEN_RECO_STATUS;
import com.nq.edusaas.hps.model.enummodel.PEN_SYNC_STATUS;
import com.nq.edusaas.hps.model.enummodel.POLL_SWITCH_STATUS;
import com.nq.edusaas.hps.sdkcore.IPenSdkCtrl;
import com.nq.edusaas.hps.sdkcore.nqpensdk.NQPenClientCtrl;
//import com.nq.edusaas.hps.service.BgWriterService;
import com.nq.edusaas.hps.utils.PenNQLog;

/**
 * @Author: Qiu.Li
 * @Create Date: 2021/1/9 10:45
 * @Description: 智能笔SDK管理类
 * @Email: liqiupost@163.com
 */
public class PenSdkCtrl implements IPenSdkCtrl {

    public static final String TAG = PenSdkCtrl.class.getSimpleName();

    private PEN_CONN_STATUS connStatus = PEN_CONN_STATUS.DISCONNECTED;
    private PEN_SYNC_STATUS syncStatus = PEN_SYNC_STATUS.NONE;
    private PEN_RECO_STATUS recoStatus = PEN_RECO_STATUS.NONE;

    private String lastTryConnectAddr = "";
    private String lastTryConnectName = "";
    private boolean clickMenu = false;
    private boolean isCanDraw = false;//true;
    private boolean bActualDrawReady = false;//画板初始化完成再进行发送实时数据
    private boolean bSyncBackground = false;//是否后台同步完成
    private boolean disconnManual = false;//是否手动断开
    private boolean isDrawNow = false;
    private boolean isSaveImage;
    private int lastPower = 10;
    public static float PX2CODE_P20 = 1.388f;//像素与码点坐标的换算系数

    private static PenSdkCtrl instance;

    public static PenSdkCtrl getInstance() {
        if (null == instance) {
            synchronized (PenSdkCtrl.class) {
                if (null == instance) {
                    instance = new PenSdkCtrl();
                }
            }
        }
        return instance;
    }

    private IPenSdkCtrl iPenCtrl;

    private boolean isNQAuth = true;//false;//是否已设备授权

    private Context mContext;

    private boolean isBindService = false;

    public void init(Context context) {
        this.mContext = context.getApplicationContext();
//        bindService(mContext);

        //默认NQPenSDK,可以根据不同的笔设置
        setPenCtrl(NQPenClientCtrl.getInstance());
        if (null != iPenCtrl) {
            this.iPenCtrl.init(context, NQPenSDK.CONN_TYPE.BLE);
//            this.iPenCtrl.setConnectListener(penConnectListener);
        }
        //给NQPenSDK设置远程通信接口
//        NQPenSDK.getInstance().setIRemoteCommunicate(NettyClient.getInstance());
    }

    public void unInit() {
//        if (isBindService) {
//            unbindService(mContext);
//        }
        this.iPenCtrl.release();
    }

    public void setPenCtrl(IPenSdkCtrl penCtrl) {
        this.iPenCtrl = penCtrl;
    }

//    /**
//     * 后台服务
//     */
//    protected BgWriterService.BgWriterBinder bgWriterBinder;

//    protected ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            bgWriterBinder = (BgWriterService.BgWriterBinder) service;
//
//            isBindService = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            isBindService = false;
//            if (null != bgWriterBinder) {
//                bgWriterBinder.getInstance().cancelNotification();
//                bgWriterBinder = null;
//            }
////            PenSdkCtrl.getInstance().disconnect();
//            if (null != iPenCtrl) {
//                iPenCtrl.disconnect();
//            }
//        }
//    };
//
//    public void showNotification() {
//        if (null != bgWriterBinder) {
//            bgWriterBinder.getInstance().showNotification();
//        }
//    }
//
//    public void cancelNotification() {
//        if (null != bgWriterBinder) {
//            bgWriterBinder.getInstance().cancelNotification();
//        }
//    }

    public int getLastPower() {
        return lastPower;
    }

    public void setLastPower(int lastPower) {
        this.lastPower = lastPower;
    }



    public boolean isNQAuth() {
        return isNQAuth;
    }

    private void setNQAuth(boolean NQAuth) {
        isNQAuth = NQAuth;
    }

//    /**
//     * 绑定后台服务
//     */
//    protected void bindService(Context context) {
//        Intent intent = new Intent(context, BgWriterService.class);
//        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//    }

    /**
     * 解绑后台服务
     */
//    protected void unbindService(Context context) {
//        context.unbindService(serviceConnection);
//    }

//    public void setScanListener(ScanListener listener){
//        iPenCtrl.setScanListener(listener);
//    }

    PenConnectListener penConnectListener = new PenConnectListener() {

        @Override
        public void onScanStart() {
            Log.i(TAG, "onScanStart");
        }

        @Override
        public void onScanStop() {
            Log.i(TAG, "onScanStop");
        }

        @Override
        public void onReceiveException(int i, String s) {
            Log.i(TAG, "onReceiveException s=" + s);
        }

        @Override
        public void onScanResult(NQDeviceBase nqDeviceBase) {
            Log.i(TAG, "onScanResult");
        }

        @Override
        public void onConnectState(int i) {
            Log.i(TAG, "onConnectState state=" + i);
            setConnStatus(PEN_CONN_STATUS.getState(i));

        }

        @Override
        public void onUsbDeviceAttached(UsbDevice usbDevice) {
            Log.i(TAG, "onUsbDeviceAttached");
        }

        @Override
        public void onUsbDeviceDetached(UsbDevice usbDevice) {
            Log.i(TAG, "onUsbDeviceDetached");
        }
    };

    @Override
    public void setScanListener(ScanListener scanListener) {
        if (null != iPenCtrl) {
            iPenCtrl.setScanListener(scanListener);
        }
    }

    @Override
    public void setConnectListener(PenConnectListener listener) {
        if (null != iPenCtrl) {
            iPenCtrl.setConnectListener(listener);
        }
    }

    @Override
    public void setPenMsgListener(PenMsgListener penMsgListener) {
        if (null != iPenCtrl) {
            iPenCtrl.setPenMsgListener(penMsgListener);
        }
    }

    @Override
    public int getConnectState() {
        if (null != iPenCtrl) {
            return iPenCtrl.getConnectState();
        }
        return 0;
    }

    @Override
    public NQDeviceBase getCurNQDev() {
        if (null != iPenCtrl) {
            return iPenCtrl.getCurNQDev();
        }
        return null;
    }

    @Override
    public void setCurNQDev(NQDeviceBase curNQDev) {
        if (null != iPenCtrl) {
            iPenCtrl.setCurNQDev(curNQDev);
        }
    }

    @Override
    public NQPenSDK.CONN_TYPE getConnType() {
        if (null != iPenCtrl) {
            return iPenCtrl.getConnType();
        }
        return null;
    }

    @Override
    public void init(Context context, NQPenSDK.CONN_TYPE connType) {
        if (null != iPenCtrl) {
            iPenCtrl.init(context, connType);
        }
    }

    @Override
    public void release() {
        if (null != iPenCtrl) {
            iPenCtrl.release();
        }
    }

    @Override
    public int startScanDevice() {
        if (null != iPenCtrl) {
            return iPenCtrl.startScanDevice();
        }
        return 0;
    }

    @Override
    public void stopScan() {
        if (null != iPenCtrl) {
            iPenCtrl.stopScan();
        }
    }

    @Override
    public void connect(NQDeviceBase device) {

        if (null != iPenCtrl) {
            iPenCtrl.connect(device);
        }
    }

    @Override
    public void disconnect() {
        if (null != iPenCtrl) {
            iPenCtrl.disconnect();
        }
    }

    @Override
    public NQDeviceBase getConnectedDevice() {
        if (null != iPenCtrl) {
            return iPenCtrl.getConnectedDevice();
        }
        return null;
    }

    @Override
    public void setDotListener(PenDotListener listener) {
        if (null != iPenCtrl) {
            iPenCtrl.setDotListener(listener);
        }
    }


    @Override
    public void setPenOfflineDataListener(PenOfflineDataListener listener) {
        if (null != iPenCtrl) {
            iPenCtrl.setPenOfflineDataListener(listener);
        }
    }

    @Override
    public void setPenConnectListener(PenConnectListener listener) {
        if (null != iPenCtrl) {
            iPenCtrl.setPenConnectListener(listener);
        }
    }

    @Override
    public void requestFirWareVersion() {
        if (null != iPenCtrl) {
            iPenCtrl.requestFirWareVersion();
        }
    }

    @Override
    public String requestSdkVersion() {
        if (null != iPenCtrl) {
            return iPenCtrl.requestSdkVersion();
        }
        return null;
    }

    @Override
    public String requestMcuVersion() {
        if (null != iPenCtrl) {
            return iPenCtrl.requestMcuVersion();
        }
        return null;
    }

    @Override
    public void requestBatInfo() {
        if (null != iPenCtrl) {
            iPenCtrl.requestBatInfo();
        }
    }

    @Override
    public void requestOfflineDataLength() {
        if (null != iPenCtrl) {
            iPenCtrl.requestOfflineDataLength();
        }
    }

    @Override
    public void requestOfflineDataWithRange() {
        if (null != iPenCtrl) {
            iPenCtrl.requestOfflineDataWithRange();
        }
    }

    @Override
    public void requestDeleteOfflineData() {
        if (null != iPenCtrl) {
            iPenCtrl.requestDeleteOfflineData();
        }
    }

    @Override
    public void editBleDeviceName(String name) {
        if (null != iPenCtrl) {
            iPenCtrl.editBleDeviceName(name);
        }
    }

//    @Override
    public void readDeviceSerialNumber() {

    }

//
//    public void readDeviceSerialNumber() {
//        if (null != iPenCtrl) {
//            iPenCtrl.readDeviceSerialNumber();
//        }
//    }


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

    @Override
    public void parseByLocalLib(byte[] data, byte version) {
//        PenNQLog.debug("开始解析 "+ BytesConvert.bytesToHex(data));
        if (null != iPenCtrl) {
            iPenCtrl.parseByLocalLib(data, version);
        }
    }

    public PEN_SYNC_STATUS getSyncStatus() {
        return this.syncStatus;
    }

    public void setSyncStatus(PEN_SYNC_STATUS syncStatus) {
        this.syncStatus = syncStatus;
    }

    public PEN_RECO_STATUS getRecoStatus() {
        return recoStatus;
    }

    public void setRecoStatus(PEN_RECO_STATUS recoStatus) {
        this.recoStatus = recoStatus;
    }

    public PEN_CONN_STATUS getConnStatus() {
        return connStatus;
    }

    public void setConnStatus(PEN_CONN_STATUS connStatus) {
        this.connStatus = connStatus;
    }

    /////////////////////////////////////
    public boolean isCanDraw() {
        return isCanDraw;
    }

    public void setCanDraw(boolean canDraw) {
        this.isCanDraw = canDraw;
    }


    public boolean isbActualDrawReady() {
        return bActualDrawReady;
    }

    public void setbActualDrawReady(boolean bActualDrawReady) {
        this.bActualDrawReady = bActualDrawReady;
    }

    public boolean isbSyncBackground() {
        return bSyncBackground;
    }

    public void setbSyncBackground(boolean bSyncBackground) {
        this.bSyncBackground = bSyncBackground;
    }

    public boolean isDisconnManual() {
        return disconnManual;
    }

    public void setDisconnManual(boolean disconnManual) {
        this.disconnManual = disconnManual;
    }

    public boolean isClickMenu() {
        return clickMenu;
    }

    public void setClickMenu(boolean clickMenu) {
        this.clickMenu = clickMenu;
    }

    public boolean isSaveImage() {
        return isSaveImage;
    }

    public void setSaveImage(boolean saveImage) {
        isSaveImage = saveImage;
    }

    public boolean isDrawNow() {
        return isDrawNow;
    }

    public void setDrawNow(boolean drawNow) {
        isDrawNow = drawNow;
    }

    public String getLastTryConnectAddr() {
//        if (null == lastTryConnectAddr || TextUtils.isEmpty(lastTryConnectAddr)) {
//            return defaultConnectAddr;
//        }
        return lastTryConnectAddr;
    }

    public void setLastTryConnectAddr(String addr) {
        this.lastTryConnectAddr = addr;
    }

    public String getLastTryConnectName() {
        return lastTryConnectName;
    }

    public void setLastTryConnectName(String name) {
        this.lastTryConnectName = name;
    }
}
