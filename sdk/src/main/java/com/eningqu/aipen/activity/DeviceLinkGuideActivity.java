package com.eningqu.aipen.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.databinding.DataBindingUtil;

import android.os.Message;
import android.view.View;

import com.eningqu.aipen.R;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.db.model.BluetoothDevice;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.Const;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.DialogHelper;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.databinding.ActivityDeviceLinkGuideBinding;
import com.eningqu.aipen.db.model.BluetoothData;
import com.eningqu.aipen.qpen.PEN_CONN_STATUS;
import com.eningqu.aipen.qpen.PEN_SYNC_STATUS;
import com.eningqu.aipen.qpen.QPenManager;
import com.eningqu.aipen.sdk.bean.device.NQBtDevice;
import com.nq.edusaas.hps.PenSdkCtrl;

/**
 * @Author: Qiu.Li
 * @Create Date: 2019/5/9 12:54
 * @Description: 搜索蓝牙引导
 * @Email: liqiupost@163.com
 */
public class DeviceLinkGuideActivity extends BaseActivity implements View.OnClickListener {
    private ActivityDeviceLinkGuideBinding mBinding;

    boolean registerBR = false;
    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_device_link_guide);
//        setContentView(R.layout.activity_device_link_guide);
    }

    @Override
    protected void initView() {
        mBinding.deviceLinkGuideNext.setOnClickListener(this);
        mBinding.layoutTitle.ivBack.setOnClickListener(this);

        BluetoothDevice bluetoothData = AppCommon.loadBleInfo2();
        if (null != bluetoothData) {
            //监听笔的广播
            IntentFilter filter = new IntentFilter(Const.Broadcast.ACTION_PEN_MESSAGE);
            filter.addAction(Const.Broadcast.ACTION_PEN_DOT);
            registerReceiver(mBroadcastReceiver, filter);
            registerBR = true;
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.device_link_guide_next) {
            gotoActivity(BleDeviceActivity.class, true);
        } else if (id == R.id.iv_back) {
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(registerBR){
                unregisterReceiver(mBroadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.ACTION_PEN_MESSAGE.equals(action)) {

                int penMsgType = intent.getIntExtra(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.MESSAGE_TYPE, 0);

                switch (penMsgType) {
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_CONNECTION_SUCCESS:
                        /*BluetoothData bluetoothData = SQLite.select().from(BluetoothData.class).querySingle();
                        if (ble != null && bluetoothData != null) {
                            ble.connect(bluetoothData.bleMac.split("-")[1]);
                        }*/
                        Message message = new Message();
                        message.what = Constant.BLE_CONNECT_SUCCESS_CODE;
                        EventBusUtil.post(message);
                        NQBtDevice btDevice = (NQBtDevice) PenSdkCtrl.getInstance().getConnectedDevice();
                        if (btDevice != null) {
                            PenSdkCtrl.getInstance().setCurNQDev(btDevice);
                            AFPenClientCtrl.getInstance().lastTryConnectName = btDevice.name;
                            AFPenClientCtrl.getInstance().lastTryConnectAddr = btDevice.mac;

                        }

                        AFPenClientCtrl.getInstance().setConnStatus(PEN_CONN_STATUS.CONNECTED);
                        AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.NONE);

                        EventBusCarrier tcpState = new EventBusCarrier();
                        tcpState.setEventType(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Constant.BIND_STATE);
                        tcpState.setObject(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.PEN_CONNECT);
                        EventBusUtil.post(tcpState);
                        //连接成功去获取授权
                        QPenManager.getInstance().toAuth();
                        dismissDialogConn();
                        finish();
                        showToast(getResources().getString(R.string.blue_connect_success));
                        break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_CONNECTION_FAILURE:
                        dismissDialogConn();
                        showToast(getResources().getString(R.string.str_pen_unbind));
                        break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_DISCONNECTED:
                        dismissDialogConn();
                        showToast(getResources().getString(R.string.blue_connect_discontinue));
                        break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_CONNECTION_TRY:
                        if (null == mDialogConn) {
                            mDialogConn = DialogHelper.showProgress(getSupportFragmentManager(), String.format(getString(R.string.blue_connecting), AFPenClientCtrl.getInstance().getLastTryConnectName().toUpperCase()), false);
                        }
                        L.error("dialog=" + mDialogConn);
                        break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_CONNECTION_TIMEOUT:
                        dismissDialogConn();
                        showToast(getResources().getString(R.string.bt_connect_timeout));
                        break;
                }
            }
        }
    };
}
