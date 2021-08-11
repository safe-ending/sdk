package com.eningqu.aipen.activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.databinding.DataBindingUtil;
import androidx.core.content.ContextCompat;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.eningqu.aipen.common.utils.SoundPlayUtils;
import com.eningqu.aipen.common.utils.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.SmartPenApp;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.common.utils.VibratorUtil;
import com.eningqu.aipen.db.model.BluetoothDevice;
import com.eningqu.aipen.p20.DotListenerService;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.QPenManager;
import com.eningqu.aipen.sdk.bean.device.NQBtDevice;
import com.eningqu.aipen.sdk.comm.JsonTag;
import com.nq.edusaas.hps.sdkcore.afpensdk.Const;
import com.eningqu.aipen.qpen.PEN_CONN_STATUS;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.CommonBusUtils;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.bluetooth.BlueToothLeClass;
import com.eningqu.aipen.common.dialog.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.thread.ThreadPoolUtils;
import com.eningqu.aipen.common.utils.HexUtil;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.databinding.ActivityPenSettingBinding;
import com.eningqu.aipen.db.model.BluetoothData;
import com.eningqu.aipen.qpen.PEN_SYNC_STATUS;
import com.nq.edusaas.hps.PenSdkCtrl;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nq.com.ahlibrary.utils.AhUtil;
import nq.com.ahlibrary.utils.HttpUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/8 19:13
 */

public class PenSettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private final static String TAG = PenSettingActivity.class.getSimpleName();

    private BluetoothDevice bluetoothData;
    private ActivityPenSettingBinding mBinding;
    int showBatt = 10;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_pen_setting);
    }

    @Override
    protected void initView() {
        mBinding.layoutTitle.tvTitle.setText(R.string.pen_setting);
        if (bluetoothData != null && !TextUtils.isEmpty(bluetoothData.bleName)) {
            mBinding.bleName.setText(bluetoothData.bleName.toUpperCase() + bluetoothData.bleMac);
            mBinding.switchUnbind.setChecked(true);
            mBinding.battery.setText(PenSdkCtrl.getInstance().getLastPower() + "0%");
            PenSdkCtrl.getInstance().requestBatInfo();
        } else {
            mBinding.switchUnbind.setEnabled(false);
            L.error("bluetooth data is null");
        }
        mBinding.switchUnbind.setOnCheckedChangeListener(this);
        mBinding.layoutTitle.ivBack.setOnClickListener(this);
        mBinding.tvOfflineDataClean.setOnClickListener(this);
        mBinding.llReportLog.setOnClickListener(this);
        mBinding.tvReport.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        bluetoothData = AppCommon.loadBleInfo2();
        if (null != bluetoothData) {
            if (TextUtils.isEmpty(bluetoothData.bleName)) {
                bluetoothData.bleName = AFPenClientCtrl.getInstance().getLastTryConnectName();
                bluetoothData.bleMac = AFPenClientCtrl.getInstance().getLastTryConnectAddr();
                bluetoothData.update();
            }
        }
    }

    @Override
    protected void initEvent() {
    }

    //    @OnClick({R.id.iv_back, R.id.switch_unbind})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.switch_unbind:
                //                ble.disconnect(1);
                break;
            case R.id.tv_offline_data_clean:
                if (AFPenClientCtrl.getInstance().getConnStatus() != PEN_CONN_STATUS.CONNECTED) {
                    showToast(R.string.str_pen_unbind);
                } else {
                    dialog = DialogHelper.showDelete(getSupportFragmentManager(), new ConfirmListener() {
                        @Override
                        public void confirm(View view) {
                            SmartPenApp.isSync = true;
//                            AFPenClientCtrl.getInstance().requestDeleteOfflineData();
                            PenSdkCtrl.getInstance().requestDeleteOfflineData();
                            ToastUtils.showShort(getResources().getString(R.string.dialog_clear_offline_yes));
                            dismissDialog();
                        }

                        @Override
                        public void cancel() {
                            dismissDialog();
                        }
                    }, R.string.str_clean_content, R.string.offline_data_clean);
                }
                break;
            case R.id.ll_report_log:
            case R.id.tv_report:
                if (mBinding.tvReport.getText().toString().contains("...")) {
                    return;
                }
                dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {

                    @Override
                    public void confirm(View view) {
                        dismissDialog();
                        final List<File> fileList = FileUtils.listFilesInDir(AppCommon.NQ_SAVE_ROOT_PATH_LOG_DIR);
                        if (null != fileList && fileList.size() > 0) {
                            ThreadPoolUtils.getThreadPool().execute(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mBinding.tvReport.setText(getString(R.string.report) + "...");
                                        }
                                    });
                                    for (int i = 0; i < fileList.size(); i++) {

                                        uploadLog(fileList.get(i).getPath());
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        } else {
                            showToast(getString(R.string.empty));
                        }
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                    }
                }, R.string.report_tips, 0);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //监听笔的广播
        IntentFilter filter = new IntentFilter(Const.Broadcast.ACTION_PEN_MESSAGE);
        filter.addAction(Const.Broadcast.ACTION_PEN_DOT);
        registerReceiver(mBroadcastReceiver, filter);
        //获取电量
//        long bat_time = SpUtils.getLong(this, "bat_time");
//        if (System.currentTimeMillis() - bat_time > 4 * 60 * 1000) {
//            AFPenClientCtrl.getInstance().requestBatInfo();
//        }

        if (AFPenClientCtrl.getInstance().getConnStatus() != PEN_CONN_STATUS.CONNECTED) {
            if (null != bluetoothData && !TextUtils.isEmpty(bluetoothData.bleName)) {
                mBinding.bleName.setText("(" + bluetoothData.bleName.toUpperCase() + ")" + getString(R.string.ble_tips));
            } else {
                mBinding.bleName.setText(R.string.ble_tips);
            }
            mBinding.battery.setText(R.string.ble_tips);
            mBinding.tvOfflineDataClean.setTextColor(getResources().getColor(R.color.app_hint_text));
            mBinding.tvOfflineDataClean.setText(R.string.ble_tips);
        } else {
            if (null != bluetoothData && !TextUtils.isEmpty(bluetoothData.bleName)) {
                mBinding.bleName.setText(bluetoothData.bleName.toUpperCase() + bluetoothData.bleMac);
            }
            mBinding.tvOfflineDataClean.setTextColor(getResources().getColor(R.color.app_hwr_tran_bottom));
            mBinding.tvOfflineDataClean.setText(R.string.offline_data_clean);
        }

        Boolean sync = SpUtils.getBoolean(this, Constant.SP_KEY_SYNC, true);
        mBinding.switchSync.setChecked(sync);
        mBinding.switchSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpUtils.putBoolean(PenSettingActivity.this, Constant.SP_KEY_SYNC, isChecked);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            //选中时
            L.error(TAG, "----------选中时----------");
        } else {
            //非选中时
            L.error(TAG, "----------非选中时----------");

            //批量插入
            //同步事务
            /*FlowManager.getDatabase(YourDatabase.class)
                    .executeTransaction(new ProcessModelTransaction.Builder<YourModel>(
                            BaseModel::insert
                    ).addAll(YourModeList).build());
            //异步事务
            FlowManager.getDatabase(YourDatabase.class)
                    .beginTransactionAsync(new ProcessModelTransaction.Builder<YourModel>(
                            BaseModel::insert
                    ).addAll(YourModelList).build())
                    .error(your log)
                    .success(your log)
                    .build()
                    .execute();*/
            dismissDialog();
            dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                @Override
                public void confirm(View view) {
                    dismissDialog();
                    int anInt = SpUtils.getInt(PenSettingActivity.this, Constant.SP_KEY_INIT_PEN, -2);
                    if (anInt == 1) {
                        Map<String, String> map = new HashMap<>();
                        final String mac = bluetoothData.bleMac.contains("-") ? bluetoothData.bleMac.substring(bluetoothData.bleMac.indexOf("-") + 1) : bluetoothData.bleMac;//60HW-C3:49:D4:23:BC:BC
                        map.put("mac", mac);
                        map.put("imei", "android123456789");
                        map.put("pkgName", Constant.pkg);
                        HttpUtils.doPost(AppCommon.UNBIND_URL, map, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String str = response.body().string();
                                try {
                                    final JSONObject jsonObject = new JSONObject(str);
                                    final String msg = jsonObject.getString("msg");
                                    if (jsonObject.has("success")) {
                                        boolean success = jsonObject.getBoolean("success");
                                        if (success) {
                                            unbind();
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showToast(msg);
                                                    unbind();
                                                }
                                            });
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        unbind();
                    }
                }

                @Override
                public void cancel() {
                    dismissDialog();
                    mBinding.switchUnbind.setChecked(true);
                }
            }, R.string.dialog_confirm_unbund_ble, 0);

        }
    }

    private void unbind() {
        if (null != bluetoothData && bluetoothData.delete()) {
            //                    SQLite.delete().from(BluetoothData.class).execute();
            showToast(getString(R.string.str_unbinded));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.switchUnbind.setEnabled(false);
                    mBinding.bleName.setText(R.string.ble_tips);
                    mBinding.battery.setText(R.string.ble_tips);
                    mBinding.switchSyncLayout.setVisibility(View.GONE);
                    mBinding.tvOfflineDataClean.setTextColor(ContextCompat.getColor(PenSettingActivity.this, R.color.app_hint_text));
                }
            });
            //断开时添加声音和震动提示
            SoundPlayUtils.play(R.raw.sk_stop);
            VibratorUtil.Vibrate(mContext, 1000);
            AFPenClientCtrl.getInstance().setConnStatus(PEN_CONN_STATUS.DISCONNECTED);
            AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.NONE);
            EventBusCarrier tcpState = new EventBusCarrier();
            tcpState.setEventType(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Constant.BIND_STATE);
            tcpState.setObject(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.PEN_UNCONNECT);
            EventBusUtil.post(tcpState);

            DotListenerService.getInstance().releaseReConnect();

            PenSdkCtrl.getInstance().disconnect();
            PenSdkCtrl.getInstance().setCurNQDev(null);
            AFPenClientCtrl.getInstance().cleanBluetoothInfo();
            AFPenClientCtrl.getInstance().setDisconnManual(true);
            if (ble != null) {
                ble.disconnect(1);
            }

        } else {
            showToast(getString(R.string.unbind) + getString(R.string.fail));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusCarrier carrier) {
        if (null != carrier) {
            switch (carrier.getEventType()) {
                case Constant.POWER_CODE:
                    mBinding.battery.setText(carrier.getObject() + "0%");
                    break;

            }
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Const.Broadcast.ACTION_PEN_MESSAGE.equals(action)) {
                int penMsgType = intent.getIntExtra(Const.Broadcast.MESSAGE_TYPE, 0);
                //                handleMsg(penMsgType, intent);
                L.error(TAG, "action:" + action + ", penMsgType:" + penMsgType);
                switch (penMsgType) {
                    case Const.PenMsgType.PEN_CONNECTION_FAILURE: {
                        AFPenClientCtrl.getInstance().setConnStatus(PEN_CONN_STATUS.DISCONNECTED);
                        AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.NONE);
                        dismissDialogConn();
                        showToast(R.string.str_pen_unbind);

                        if (null != bluetoothData && !TextUtils.isEmpty(bluetoothData.bleName)) {
                            mBinding.bleName.setText("(" + bluetoothData.bleName.toUpperCase() + ")" + getString(R.string.ble_tips));
                        } else {
                            mBinding.bleName.setText(R.string.ble_tips);
                        }
                        mBinding.battery.setText(R.string.ble_tips);
                        mBinding.tvOfflineDataClean.setTextColor(getResources().getColor(R.color.app_hint_text));
                        mBinding.tvOfflineDataClean.setText(R.string.ble_tips);
                    }
                    break;

                    case Const.PenMsgType.PEN_CONNECTION_SUCCESS:

                        /*BluetoothData bluetoothData = SQLite.select().from(BluetoothData.class).querySingle();
                        if (ble != null && bluetoothData != null) {
                            ble.connect(bluetoothData.bleMac.split("-")[1]);
                        }*/
                        showToast(R.string.blue_connect_success);
                        //                        updateBleStateIcon();
                        //请求离线数据
                        //                        AFPenClientCtrl.getInstance().requestOfflineDataInfo();
                        mBinding.tvOfflineDataClean.setTextColor(getResources().getColor(R.color.app_hwr_tran_bottom));
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
                        //连接成功去获取授权
                        QPenManager.getInstance().toAuth();
                        EventBusCarrier tcpState = new EventBusCarrier();
                        tcpState.setEventType(Const.Constant.BIND_STATE);
                        tcpState.setObject(Const.Broadcast.PEN_CONNECT);
                        EventBusUtil.post(tcpState);

                        if (null != bluetoothData && !TextUtils.isEmpty(bluetoothData.bleName)) {
                            mBinding.bleName.setText(bluetoothData.bleName.toUpperCase() + bluetoothData.bleMac);
                        }
                        mBinding.tvOfflineDataClean.setTextColor(getResources().getColor(R.color.app_hwr_tran_bottom));
                        mBinding.tvOfflineDataClean.setText(R.string.offline_data_clean);
                        PenSdkCtrl.getInstance().requestBatInfo();

                        break;
                    //                    case Const.PenMsgType.PEN_CONNECTION_FAILURE:
                    //                        showToast(R.string.blue_connect_fail);
                    //                        updateBleStateIcon();
                    //                        break;
                    case Const.PenMsgType.PEN_DISCONNECTED:
                        showToast(R.string.blue_connect_discontinue);
                        if (AFPenClientCtrl.getInstance().getConnStatus() != PEN_CONN_STATUS.CONNECTED) {
                            mBinding.bleName.setText(R.string.ble_tips);
                            mBinding.battery.setText(R.string.ble_tips);
                            mBinding.tvOfflineDataClean.setTextColor(getResources().getColor(R.color.app_hint_text));
                            mBinding.tvOfflineDataClean.setText(R.string.ble_tips);

                        }
                        break;
                    case Const.PenMsgType.PEN_CUR_MEMOFFSET:
                        break;
                    case Const.PenMsgType.PEN_CUR_BATT:
                        final int battery = intent.getIntExtra(JsonTag.INT_BATT_VAL, 0);

                        showBatt = CommonBusUtils.bat2Percent(battery);

                        mBinding.battery.setText(showBatt + "%");

                        if (battery == 32767) {
                            mBinding.battery.setText(R.string.charging);
                        }

                        if (showBatt <= Constant.PEN_BATTERY_ALARM_VALUE) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissDialog();
                                    dialog = DialogHelper.showMessage(getSupportFragmentManager(), new ConfirmListener() {
                                        @Override
                                        public void confirm(View view) {
                                            dismissDialog();
                                        }

                                        @Override
                                        public void cancel() {
                                            dismissDialog();
                                        }
                                    }, String.format(getString(R.string.batt_alarm), showBatt));
                                }
                            });
                        }
                        break;

                }
            }
        }
    };


    private void uploadLog(String path) {
        Map<String, String> mapParater = new HashMap<>();
        mapParater.put("pkgName", Constant.pkg);
        String imei = new AhUtil().getIMEI(this);
        mapParater.put("imei", imei == null ? "" : imei);
        mapParater.put("taskId", String.valueOf(System.currentTimeMillis()));
        mapParater.put("osType", "android");
        String versionName = AppUtils.getAppVersionName();
        mapParater.put("softVersion", versionName == null ? "" : versionName);
        L.info(TAG, mapParater.toString() + " , path:" + path);
        File file = new File(path);
        if (!file.exists()) {
            L.error(TAG, "log.zip is none");
        }
        HttpUtils.postFile("http://admin.eningqu.com/logger/upload", path, mapParater, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(getString(R.string.report_log) + getString(R.string.fail));
                        mBinding.tvReport.setText(getString(R.string.report));
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String string = response.body().string();
                            Log.d("uploadLog", "@@@@ response: " + string);
                            JSONObject jsonObject = new JSONObject(string);
                            int code = jsonObject.optInt("code");
                            if (code == 1) {
                                showToast(getString(R.string.report_log) + getString(R.string.success));
                            } else {
                                showToast(getString(R.string.report_log) + getString(R.string.fail) + ", error code=" + code);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mBinding.tvReport.setText(getString(R.string.report));
                    }
                });
            }
        });
    }
}
