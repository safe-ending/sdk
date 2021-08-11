package com.eningqu.aipen.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.BlueDeviceAdapter;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.DialogHelper;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.bluetooth.BluetoothClient;
import com.eningqu.aipen.common.dialog.BaseDialog;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.LocationUtils;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.databinding.ActivityBleDeviceBinding;
import com.eningqu.aipen.db.model.BluetoothDevice;
import com.eningqu.aipen.p20.DotListenerService;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.PEN_CONN_STATUS;
import com.eningqu.aipen.qpen.PEN_SYNC_STATUS;
import com.eningqu.aipen.qpen.QPenManager;
import com.eningqu.aipen.sdk.NQPenSDK;
import com.eningqu.aipen.sdk.bean.device.NQBtDevice;
import com.eningqu.aipen.sdk.bean.device.NQDeviceBase;
import com.eningqu.aipen.sdk.comm.ScanListener;
import com.nq.edusaas.hps.PenSdkCtrl;
import com.nq.edusaas.hps.popup.GpsTipsPopup;
import com.nq.edusaas.hps.popup.MessagePopup;
import com.nq.edusaas.hps.popup.PenProgressPopup;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author: Qiu.Li
 * @Create Date: 2019/8/21 14:51
 * @Description:
 * @Email: liqiupost@163.com
 */
public class BleDeviceActivity extends BaseActivity {

    private final static String TAG = BleDeviceActivity.class.getSimpleName();

    private ActivityBleDeviceBinding mBinding;
    public final static int REQUEST_CODE_BLUETOOTH_ENABLE = 101;

    private static final int MSG_BLE_NOT_FUND = 2;

    //    private UsbManager usbManager;
//    private UsbDevice usbDevice;
//    private USBDeviceAdapter deviceAdapter;
    private List<NQDeviceBase> deviceList;
    private boolean bDevExist = false;
    public BlueDeviceAdapter blueDeviceAdapter;
    public boolean isRefresh = false;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_ble_device);
    }

    @Override
    protected void initView() {
        DotListenerService.getInstance().releaseReConnect();

        //        mBinding.includeTopBar.tvTitle.setText(R.string.drawer_ble_scan);
        mBinding.llBtList.setVisibility(View.GONE);
        mBinding.llBtSearch.setVisibility(View.VISIBLE);
        populateList();


        mBinding.swipeRefresh.setEnabled(true);

        mBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.swipeRefresh.setRefreshing(false);
                refresh();
            }
        });
        refresh();

    }

    @Override
    protected void initData() {
    }


    private void refresh() {
        if (BluetoothClient.getBle().getBlueToothStatus()) {
            deviceList.clear();
            blueDeviceAdapter.notifyDataSetChanged();
            searchCanGetResult = false;
            PenSdkCtrl.getInstance().stopScan();
            startScan();
        } else {
            BluetoothClient.getBle().openBlueTooth(this, REQUEST_CODE_BLUETOOTH_ENABLE);
        }
    }

    protected BaseDialog dialog, connectDialog;

    private void startScan() {
        //开启定位
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!LocationUtils.isLocationEnabled(this)) {
                dialog = DialogHelper.showGpsTips(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        dismissDialog();
                        LocationUtils.openLocationSettings(BleDeviceActivity.this);
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                    }
                });
                return;
            }
        }

        PenSdkCtrl.getInstance().setScanListener(scanListener);
        PenSdkCtrl.getInstance().startScanDevice();
    }


    protected void initEvent() {
        //监听笔的广播
        IntentFilter filter = new IntentFilter(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.ACTION_PEN_MESSAGE);
        filter.addAction(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.ACTION_PEN_DOT);

        filter.addAction(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.ACTION_FIND_START);
        filter.addAction(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.ACTION_FIND_STOP);
        filter.addAction(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.ACTION_FIND_DEVICE);
        filter.addAction(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.ACTION_FIND_ERROR);
        filter.addAction(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.ACTION_USB_DEVICE_ATTACHED);

        registerReceiver(mBroadcastReceiver, filter);
    }

    /**
     * 初始化设备列表
     */
    private void populateList() {
        mHandler = new UIHandler(this);

        deviceList = new ArrayList<>();
        blueDeviceAdapter = new BlueDeviceAdapter(this);
        mBinding.lvBluetooth.setAdapter(blueDeviceAdapter);
        mBinding.lvBluetooth.setOnItemClickListener(mDeviceClickListener);
    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void onViewClick(View view) {
        int id = view.getId();

        if (id == R.id.iv_back) {
            if (connectDialog != null) {
                connectDialog.dismiss();
            }
            dismissDialog();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListner(Message message) {
        switch (message.what) {
            case com.nq.edusaas.hps.sdkcore.afpensdk.Const.Constant.BLE_FOUND_DEVICE: {
                //                if (dialog != null) {
                //                    dialog.dismiss();
                //                }
                break;
            }
            case com.nq.edusaas.hps.sdkcore.afpensdk.Const.Constant.BLE_START_FOUND_DEVICE: {
                if (mPenProgressPopup2 == null) {
                    mPenProgressPopup2 = new PenProgressPopup(BleDeviceActivity.this, "Update");
                }
                mPenProgressPopup2.showPopupWindow();

                Message msg = new Message();
                msg.what = 10;
                mHandler.sendMessageDelayed(msg, 3000);
                break;
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (NQPenSDK.getInstance().isScanning()) {
            PenSdkCtrl.getInstance().stopScan();
        }
        unregisterReceiver(mBroadcastReceiver);

        mHandler.removeCallbacksAndMessages(null);
//        if (isRegisterUsbReceiver) {
//            unregisterReceiver(mUsbPermissionReceiver);
//        }
        if (AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.DISCONNECTED) {
            DotListenerService.getInstance().reConnectBle();
        }

    }

    private boolean searchCanGetResult = false;
    ScanListener scanListener = new ScanListener() {
        @Override
        public void onScanStart() {
            showToast(getResources().getString(R.string.ble_refresh));

        }

        @Override
        public void onScanStop() {
//            showToast("Scan stop!");
            if (!searchCanGetResult && deviceList.size() == 0) {
                showToast("附近暂时没有搜索到蓝牙笔");
            } else if (deviceList.size() == 0) {
                showToast("蓝牙搜索频繁，请稍后再试，若多次失败，请重启手机系统蓝牙");
            }
        }

        @Override
        public void onReceiveException(int i, String s) {
//            showToast("Scan exception, error=" + i + ", message=" + s);
        }

        @Override
        public void onScanResult(NQDeviceBase nqDeviceBase) {
//            NQBtDevice btDevice = (NQBtDevice) PenSdkCtrl.getInstance().getCurNQDev();
//            NQBtDevice scanDevice = (NQBtDevice) nqDeviceBase;
//            if (btDevice != null && btDevice.mac.equals(scanDevice.mac)) {
//                PenSdkCtrl.getInstance().connect(nqDeviceBase);
//            }
            searchCanGetResult = true;
            addDevice(nqDeviceBase);

        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.ACTION_PEN_MESSAGE.equals(action)) {

                int penMsgType = intent.getIntExtra(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.MESSAGE_TYPE, 0);

                switch (penMsgType) {
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_CONNECTION_SUCCESS: {
                        Message message = new Message();
                        message.what = Constant.BLE_CONNECT_SUCCESS_CODE;
                        EventBusUtil.post(message);

                        AFPenClientCtrl.getInstance().setConnStatus(PEN_CONN_STATUS.CONNECTED);
                        AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.NONE);

                        EventBusCarrier penStateEbCarrier = new EventBusCarrier();
                        penStateEbCarrier.setEventType(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Constant.BIND_STATE);
                        penStateEbCarrier.setObject(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.PEN_CONNECT);
                        EventBusUtil.post(penStateEbCarrier);

                        NQBtDevice btDevice = (NQBtDevice) PenSdkCtrl.getInstance().getConnectedDevice();
                        if (btDevice != null) {
                            PenSdkCtrl.getInstance().setCurNQDev(btDevice);
                            AFPenClientCtrl.getInstance().lastTryConnectName = btDevice.name;
                            AFPenClientCtrl.getInstance().lastTryConnectAddr = btDevice.mac;

                        }
                        L.debug("测试,", "连接成功");
                        mBinding.llBtList.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dismissDialogConn();
                                if (connectDialog != null) {
                                    connectDialog.dismiss();
                                }
//                              Log.e("测试,", "连接成功关闭页面");
                                if (btDevice != null) {
                                    BluetoothDevice bluetoothDevice = new BluetoothDevice();
                                    bluetoothDevice.bleMac = btDevice.getMac();
                                    bluetoothDevice.bleName = btDevice.getName();
                                    bluetoothDevice.userUid = AppCommon.getUserUID();
                                    bluetoothDevice.save();
                                    //连接成功去获取授权
                                    QPenManager.getInstance().toAuth();
                                }
                                finish();
                            }
                        }, 1000);
                    }
                    break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_CONNECTION_FAILURE: {
                        if (connectDialog != null) {
                            connectDialog.dismiss();
                        }
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        dismissDialogConn();
                        showToast(getResources().getString(R.string.str_pen_unbind));
                    }
                    break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_DISCONNECTED:
                        if (connectDialog != null) {
                            connectDialog.dismiss();
                        }
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        dismissDialogConn();
                        showToast(getResources().getString(R.string.blue_connect_discontinue));
                        break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_CONNECTION_TRY:
//                        if (mPenProgressPopup == null) {
//                            mPenProgressPopup = new PenProgressPopup(DeviceListActivity.this,
//                                    String.format("Retry to connect %s", AFPenClientCtrl.getInstance().getLastTryConnectName().toUpperCase()));
//                        }
//                        mPenProgressPopup.showPopupWindow();

//                        if (null == mDialogConn) {
//                            mDialogConn = DialogHelper.showProgress(getSupportFragmentManager(), String.format(getString(R.string.blue_connecting), AFPenClientCtrl.getInstance().getLastTryConnectName().toUpperCase()), false);
//                        }
                        break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_CONNECTION_TIMEOUT:
                        dismissDialogConn();
                        showToast(getResources().getString(R.string.bt_connect_timeout));
                        break;
                }
            } else if (com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                if (null != blueDeviceAdapter) {
//                    blueDeviceAdapter.clearDatas();
                    blueDeviceAdapter.notifyDataSetChanged();
                }
                PenSdkCtrl.getInstance().startScanDevice();
            } else if (com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (null != blueDeviceAdapter) {
//                    blueDeviceAdapter.clearDatas();
                    blueDeviceAdapter.notifyDataSetChanged();
                }
                PenSdkCtrl.getInstance().stopScan();
            }
        }
    };

    private MessagePopup mMessagePopup;
    private PenProgressPopup mPenProgressPopup;
    private GpsTipsPopup mGpsTipsPopup;
    private PenProgressPopup mPenProgressPopup2;

    public void dismissDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing() && !isDestroyed()) {
                    if (mMessagePopup != null && mMessagePopup.isShowing()) {
                        mMessagePopup.dismiss(false);
                    }
                    if (mGpsTipsPopup != null && mGpsTipsPopup.isShowing()) {
                        mGpsTipsPopup.dismiss(false);
                    }
                    if (mPenProgressPopup2 != null && mPenProgressPopup2.isShowing()) {
                        mPenProgressPopup2.dismiss(false);
                    }
                }
            }
        });
    }

    public void dismissDialogConn() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing() && !isDestroyed()) {
                    if (mPenProgressPopup != null && mPenProgressPopup.isShowing()) {
                        mPenProgressPopup.dismiss(false);
                    }
                }
            }
        });
    }

    private UIHandler mHandler;

    private static class UIHandler extends Handler {
        WeakReference<BleDeviceActivity> softReference;

        UIHandler(BleDeviceActivity activity) {
            softReference = new WeakReference<BleDeviceActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BleDeviceActivity activity = softReference.get();
            if (null != activity) {
                if (10 == msg.what) {
                    activity.dismissDialog();
                } else if (MSG_BLE_NOT_FUND == msg.what) {
                    activity.dismissDialog();
                    if (activity.mMessagePopup == null) {
                        activity.mMessagePopup = new MessagePopup(activity, "Device not fund", false);
                        activity.mMessagePopup.setClick(new MessagePopup.OnClick() {
                            @Override
                            public void onItemClick() {
                                activity.mMessagePopup.dismiss();
                                activity.finish();
                            }
                        });
                    }
                    activity.mMessagePopup.showPopupWindow();
                }
            }
        }
    }


    private void initUSB() {
//        isSupportUsbHost = supportUsbHost();
//        if (!supportUsbHost()) {
//            PenNQLog.error("This device is not support Usb host");
//            return;
//        }
//        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Intent result = new Intent();
            PenSdkCtrl.getInstance().stopScan();
            if (position >= 0 && position < deviceList.size()) {
                NQDeviceBase deviceInfo = deviceList.get(position);
                NQBtDevice btDevice = (NQBtDevice) deviceInfo;
//                if (checkUsbDevicePermission()) {
//                    finish();
//                }

                dialog = DialogHelper.showBle(getSupportFragmentManager(), getResources().getString(R.string.str_linking));
                AFPenClientCtrl.getInstance().setConnStatus(PEN_CONN_STATUS.DISCONNECTED);
                AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.NONE);
                EventBusCarrier tcpState = new EventBusCarrier();
                tcpState.setEventType(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Constant.BIND_STATE);
                tcpState.setObject(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.PEN_UNCONNECT);
                EventBusUtil.post(tcpState);

                if (PenSdkCtrl.getInstance().getConnectedDevice() != null) {
                    PenSdkCtrl.getInstance().disconnect();
                    PenSdkCtrl.getInstance().setCurNQDev(null);
                }
                AFPenClientCtrl.getInstance().cleanBluetoothInfo();
                AFPenClientCtrl.getInstance().setDisconnManual(true);
                SpUtils.putInt(BleDeviceActivity.this, Constant.SP_KEY_INIT_PEN, -2);
                L.debug("测试,", "开始连接1");
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            PenSdkCtrl.getInstance().startScanDevice();
                PenSdkCtrl.getInstance().connect(btDevice);
//                            PenSdkCtrl.getInstance().stopScan();
//                            L.debug("测试,", "开始连接2");
//                        }
//                    }, 1000);


            }
        }
    };

    /**
     * 添加设备
     */
    private void addDevice(NQDeviceBase nqDeviceBase) {
        if (null == nqDeviceBase)
            return;

        if ("bt".equals(nqDeviceBase.getType())) {

            NQBtDevice nqBtDevice = (NQBtDevice) nqDeviceBase;
            bDevExist = false;
            if (null != nqBtDevice) {
                for (NQDeviceBase deviceBase : deviceList) {
                    NQBtDevice btDevice = (NQBtDevice) deviceBase;
                    if (null != btDevice && btDevice.getMac().equals(nqBtDevice.getMac())) {
                        bDevExist = true;
                    }
                }
            }
            if (bDevExist) {
                return;
            }

            try {

                NQBtDevice btDevice = (NQBtDevice) nqDeviceBase;
                if (null != btDevice && null != btDevice.name) {
                    String name = btDevice.name.toUpperCase();
                    if (name.startsWith(AppCommon.PEN_P20) ||
                            name.startsWith(AppCommon.PEN_P20_TEMP) ||
                            name.startsWith(AppCommon.PEN_QPE)) {
                        deviceList.add(nqDeviceBase);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.llBtList.setVisibility(View.VISIBLE);
                                mBinding.llBtSearch.setVisibility(View.GONE);
                                dismissDialog();
                                blueDeviceAdapter.setData(btDevice);
                                blueDeviceAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            } catch (ClassCastException e) {
                L.error("class cast exception ");
            }
        }

    }

    //判断usb设备是否已获取了授权
    /*private boolean checkUsbDevicePermission() {
        if (null != usbManager && null != usbDevice && !usbManager.hasPermission(usbDevice)) {
            PenNQLog.debug("usb auth"*//*"usb设备未授权，弹出对话框请求授权"*//*);
            PenUIHelper.showToastShort(context, "请授予访问权限");
            IntentFilter filter2 = new IntentFilter(ACTION_USB_PERMISSION);
            registerReceiver(mUsbPermissionReceiver, filter2);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0,
                    new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_UPDATE_CURRENT);
            usbManager.requestPermission(usbDevice, pi);

            isRegisterUsbReceiver = true;
            return false;
        }
        return true;
    }*/

    /*private BroadcastReceiver mUsbPermissionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                if (granted) {
                    PenNQLog.debug("get usb auth"*//*"获得了usb使用权限，去初始化设备"*//*);
                    PenUIHelper.showToastShort(context, "授权成功");
                    //获得了usb使用权限，去初始化设备
//                    usbDeviceInit(device);
                    finish();
                } else {
                    PenNQLog.error("device get permission granted is false");
                    PenUIHelper.showToastShort(context, "授权失败");
                }
            }
        }
    };*/

    /**
     * 是否支持USB HOST
     *
     * @return
     */
    public boolean supportUsbHost() {
        return getPackageManager().hasSystemFeature("android.hardware.usb.host");
    }
}
