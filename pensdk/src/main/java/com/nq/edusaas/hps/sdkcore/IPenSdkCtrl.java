package com.nq.edusaas.hps.sdkcore;

import android.content.Context;

import com.eningqu.aipen.sdk.IPenCtrl;
import com.eningqu.aipen.sdk.NQPenSDK;
import com.eningqu.aipen.sdk.bean.device.NQDeviceBase;
import com.eningqu.aipen.sdk.comm.ScanListener;
import com.eningqu.aipen.sdk.listener.PenConnectListener;
import com.eningqu.aipen.sdk.listener.PenMsgListener;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/1/9 11:15
 * desc   :
 * version: 1.0
 */
public interface IPenSdkCtrl extends IPenCtrl {

    /**
     * 设置扫描监听
     * @param scanListener
     */
    void setScanListener(ScanListener scanListener);

    void setConnectListener(PenConnectListener listener);
    /**
     * 设置笔信息监听
     * @param penMsgListener
     */
    void setPenMsgListener(PenMsgListener penMsgListener);

    /**
     * 获取连接状态
     * @return
     */
    int getConnectState();

    NQDeviceBase getCurNQDev();

    void setCurNQDev(NQDeviceBase curNQDev);
    NQPenSDK.CONN_TYPE getConnType();
    void init(Context context, NQPenSDK.CONN_TYPE connType);
    void release();
    void parseByLocalLib(byte[] data, byte version);
}
