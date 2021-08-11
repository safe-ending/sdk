package com.eningqu.aipen.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.db.model.BluetoothDevice;
import com.eningqu.aipen.p20.DotListenerService;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.Const;
import com.eningqu.aipen.qpen.IPenOfflineDataSyncListener;
import com.eningqu.aipen.qpen.PEN_CONN_STATUS;
import com.eningqu.aipen.qpen.PEN_SYNC_STATUS;
import com.eningqu.aipen.qpen.SignatureView;
import com.eningqu.aipen.qpen.bean.CommandSize;
import com.eningqu.aipen.base.ui.BaseFragment;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.CommonBusUtils;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.common.utils.VibratorUtil;
import com.eningqu.aipen.databinding.ActivityDrawPageBinding;
import com.eningqu.aipen.db.model.BluetoothData;
import com.eningqu.aipen.db.model.UserInfoData;
import com.eningqu.aipen.fragment.PageDrawFragment;
import com.eningqu.aipen.qpen.listener.IQPenOnActivityResult;
import com.eningqu.aipen.qpen.QPenManager;
import com.eningqu.aipen.sdk.bean.device.NQBtDevice;
import com.eningqu.aipen.sdk.comm.JsonTag;
import com.nq.edusaas.hps.PenSdkCtrl;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DrawPageActivity extends FragmentBaseActivity implements Toolbar.OnMenuItemClickListener {

    private final static String TAG = DrawPageActivity.class.getSimpleName();

    private ActivityDrawPageBinding mainBinding;
    public static boolean isBuss = true;
    private final int MSG_WHAT_DISMISS_DIALOG_OFFLINE_SYNC = 3;
    private final int MSG_WHAT_CANCEL_PERMISSION = 4;
    private final int MSG_WHAT_MYSCRIPT_DIALOG = 998;
    private final int DEYED_TIME = 180000;
    private boolean syncFinish = false;//是否同步完成
    private boolean isCancelPermission = false;//是否取消授权
    private boolean isCancelBtEnable = false;//是否取消打开蓝牙
    private BluetoothDevice mBoundBtData;//绑定的设备
    private UserInfoData userInfo;
    private String strCostTime;//离线同步耗时
    private int dotSize;//点数量

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (MSG_WHAT_DISMISS_DIALOG_OFFLINE_SYNC == msg.what) {
                dismissDialog();
                dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        dismissDialog();
                        //同步离线数据
                        DotListenerService.getInstance().requestOfflineDataWithRange(penOfflineDataSync,dotSize);
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                        AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.NONE);
                        syncFinish = true;
                    }
                }, R.string.dialog_title, R.string.dialog_msg_synchronized_offline_data_timeout, R.string.title_retry, R.string.dialog_cancel_text);
            } else if (MSG_WHAT_CANCEL_PERMISSION == msg.what) {
                dismissDialog();
                //拒绝授权
                dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        //继续获取授权
                        isCancelPermission = false;
                        dismissDialog();
                    }

                    @Override
                    public void cancel() {
                        isCancelPermission = true;
                        dismissDialog();
                    }
                }, R.string.dialog_title, R.string.dialog_permission_tips, R.string.title_retry, R.string.dialog_cancel_text);
            } else if (MSG_WHAT_MYSCRIPT_DIALOG == msg.what) {
//                dismissDialog();
//                dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
//                    @Override
//                    public void confirm(View view) {
//                        SpUtils.putBoolean(DrawPageActivity.this, Constant.SP_KEY_MYSCRIPT_INSTALL, false);
//                        gotoActivity(RecordLanguageActivity.class);
//                        dismissDialog();
//                    }
//
//                    @Override
//                    public void cancel() {
//                        SpUtils.putBoolean(DrawPageActivity.this, Constant.SP_KEY_MYSCRIPT_INSTALL, false);
//                        dismissDialog();
//                        SpUtils.putString(DrawPageActivity.this, Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);
//                    }
//                }, getString(R.string.recognize_download_resoures));
            }
        }
    };
    private MyReceiver receiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            L.info("UI", "onCreate savedInstanceState=" + savedInstanceState.toString());
        }
        super.onCreate(savedInstanceState);
        Boolean sync = SpUtils.getBoolean(this, Constant.SP_KEY_SYNC, true);
        if (sync) {
            //请求离线数据
            PenSdkCtrl.getInstance().requestOfflineDataLength();
        }

        //监听笔的广播
        IntentFilter filter = new IntentFilter(Const.Broadcast.ACTION_PEN_MESSAGE);
        filter.addAction(Const.Broadcast.ACTION_PEN_DOT);
        registerReceiver(mBroadcastReceiver, filter);

//        //动态注册广播
//        IntentFilter filter1 = new IntentFilter();
//        filter1.addAction(Intent.ACTION_LOCALE_CHANGED);
//        systemLocaleChangeReceiver = new SystemLocaleChangeReceiver();
//        registerReceiver(systemLocaleChangeReceiver, filter1);

        receiver = new MyReceiver();
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(receiver, homeFilter);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return true;
    }

    @Override
    protected void setLayout() {
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_draw_page);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //当界面被回收后，会触发这里，需要重新init，生成对象
        AppCommon.setNotebooksChange(true);
        QPenManager.getInstance().setNeedInit(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AFPenClientCtrl.getInstance().setClickMenu(false);
        userInfo = AppCommon.loadUserInfo();
        if (null != userInfo) {
            int size = SpUtils.getInt(this, AppCommon.getUserUID() + "_" + "size", 1);
            int color = SpUtils.getInt(this, AppCommon.getUserUID() + "_" + "color", ContextCompat.getColor(mContext, R.color.colors_menu_black));
            QPenManager.getInstance().setPaintColor(color);
            QPenManager.getInstance().setPenSizeType(size);
            QPenManager.getInstance().setPaintSize(CommandSize.getSizeByLevel(size));
        }

        //更新蓝牙连接状态图标
        updateBleStateIcon();

        //获取绑定的设备
        mBoundBtData = AppCommon.loadBleInfo2();
        //显示离线同步的等待框
        if (AFPenClientCtrl.getInstance().getSyncStatus() == PEN_SYNC_STATUS.SYNCHRONIZING) {
            int progress = SpUtils.getInt(DrawPageActivity.this, Constant.SP_KEY_OUTLINE_PROGRESS, 0);
            dialog = DialogHelper.showProgress(getSupportFragmentManager(), String.format(getString(R.string.ble_history_data_receive), progress), false);
            syncFinish = false;
            mHandler.sendEmptyMessageDelayed(MSG_WHAT_DISMISS_DIALOG_OFFLINE_SYNC, DEYED_TIME);
        }

        updateTopToolbar("");

        if (AFPenClientCtrl.getInstance().isbSyncBackground()) {
            AFPenClientCtrl.getInstance().setbSyncBackground(false);
            dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                @Override
                public void confirm(View view) {
                    Intent intent = new Intent(DrawPageActivity.this, NotebookDisplayVerticalActivity.class);
                    intent.putExtra(NotebookDisplayVerticalActivity.FUN_TYPE, NotebookDisplayVerticalActivity.FUN_TYPE_OFFLINE_DATA);
                    startActivity(intent);
                }

                @Override
                public void cancel() {

                }
            }, getString(R.string.str_sync_finish));
        }

//        boolean myscriptInstall = SpUtils.getBoolean(DrawPageActivity.this, Constant.SP_KEY_MYSCRIPT_INSTALL, true);
//        if (myscriptInstall) {
//            String locale = Locale.getDefault().toString();
//            if (!locale.equals("en_US")) {
//                if (locale.startsWith("af_") || locale.startsWith("sq_") || locale.startsWith("hy_") || locale.startsWith("az_") || locale.startsWith("eu_")
//                        || locale.startsWith("be_") || locale.startsWith("bg_") || locale.startsWith("ca_") || locale.startsWith("zh_") || locale.startsWith("hr_")
//                        || locale.startsWith("cs_") || locale.startsWith("da_") || locale.startsWith("nl_") || locale.startsWith("en_") || locale.startsWith("et_")
//                        || locale.startsWith("fi_") || locale.startsWith("fr_") || locale.startsWith("gl_") || locale.startsWith("ka_") || locale.startsWith("de_")
//                        || locale.startsWith("el_") || locale.startsWith("hu_") || locale.startsWith("is_") || locale.startsWith("id_") || locale.startsWith("ga_")
//                        || locale.startsWith("it_") || locale.startsWith("ja_") || locale.startsWith("kk_") || locale.startsWith("ko_") || locale.startsWith("lv_")
//                        || locale.startsWith("lt_") || locale.startsWith("mk_") || locale.startsWith("ms_") || locale.startsWith("mn_") || locale.startsWith("mo_")
//                        || locale.startsWith("pl_") || locale.startsWith("pt_") || locale.startsWith("ro_") || locale.startsWith("ru_") || locale.startsWith("sr_")
//                        || locale.startsWith("sk_") || locale.startsWith("sl_") || locale.startsWith("es_") || locale.startsWith("sv_") || locale.startsWith("tt_")
//                        || locale.startsWith("tr_") || locale.startsWith("uk_") || locale.startsWith("vi_")) {
//                    //可下载语种，提示
//                    mHandler.sendEmptyMessageDelayed(MSG_WHAT_MYSCRIPT_DIALOG, 300);
//                } else {
//                    SpUtils.putString(this, Constant.SP_KEY_RECO_LANGUAGE, "en_US");
//                }
//            } else {
//                SpUtils.putString(this, Constant.SP_KEY_RECO_LANGUAGE, "en_US");
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainBinding = null;
        canvasFrame = null;
        mStrokeView = null;
        mContext = null;
        pageDrawFragment = null;
        mBoundBtData = null;
        userInfo = null;

        unregisterReceiver(mBroadcastReceiver);
//        unregisterReceiver(systemLocaleChangeReceiver);
        unregisterReceiver(receiver);
        mHandler = null;
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();

        pageDrawFragment = (PageDrawFragment) getFragment(PageDrawFragment.class);
        pageDrawFragment.setArguments(bundle);
    }

    @Override
    protected void initView() {
        init();
    }

    @Override
    protected BaseFragment getFirstFragment() {
        if (pageDrawFragment == null)
            pageDrawFragment = (PageDrawFragment) getFragment(PageDrawFragment.class);
        return pageDrawFragment;
    }

    @Override
    protected int getFragmentContainerId() {
        return mainBinding.mainContainer.getId();
    }

    public SignatureView getSignatureView() {
        return this.mStrokeView;
    }

    public void setSignatureView(SignatureView signatureView) {
        this.mStrokeView = signatureView;
    }

    public void toShowPage(final int pageNum, final boolean clean) {
        showPage(pageNum, clean);
    }

    public void toSwitchPage(int pageNum) {
        switchPage(pageNum);
    }

    public void toSave(int pageNum) {
        save(pageNum);
    }

    public void showTopToolbar(int visibility) {
        mainBinding.includeMainTopToolbar.toolbar.setVisibility(visibility);
    }

    public void toStartRecord() {
        startRecord();
    }

    public void toPauseStopRecord() {
        pauseStopRecord();
    }

    public void toStopRecord() {
        stopRecord();
    }

    public Toolbar getTopToolbar() {
        return mainBinding.includeMainTopToolbar.toolbar;
    }

    /**
     * 初始化
     */
    private void init() {
        setLeftDrawLayout();
        //默认设置断开连接的图标
        mainBinding.includeMainTopToolbar.ivBtState.setImageResource(R.drawable.icon_bt_disconnected);
        mainBinding.includeMainTopTips.rlRoot.setVisibility(View.GONE);
    }

    private void setLeftDrawLayout() {
        Toolbar toolbar = mainBinding.includeMainTopToolbar.toolbar;
        //设置主标题和副标题
        toolbar.setTitle(R.string.drawer_home);
        setSupportActionBar(toolbar);

        // Navigation Icon 要設定在 setSupoortActionBar 才有作用 否則會出現 back button
        toolbar.setTitle("");
        toolbar.setLogo(null);
        // Menu item click 的监听事件一樣要设定在 setSupportActionBar 才有作用
        toolbar.setOnMenuItemClickListener(this);

        if (getSupportActionBar() != null) {
            //一定要在setSupportActionBar之后调用
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //设置home图标
        toolbar.setNavigationIcon(R.drawable.back);
    }

    @Override
    protected void initEvent() {
    }

    private class MyReceiver extends BroadcastReceiver {

        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

                if (reason == null)
                    return;

                // Home键
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                }

                // 最近任务列表键
                if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                    pageDrawFragment.clickMenuDraw();
                }
            }
        }
    }

    public void updateTopToolbar(String label) {
        mainBinding.includeMainTopToolbar.rlToolbarSearch.setVisibility(View.GONE);
        mainBinding.includeMainTopToolbar.rlToolbarLabel.setVisibility(View.VISIBLE);
        mainBinding.includeMainTopToolbar.ivMore.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(label)) {
            mainBinding.includeMainTopToolbar.tvPageLabel.setText(label);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusCarrier carrier) {
        if (null != carrier) {
            switch (carrier.getEventType()) {
                case Constant.USER_LOGOUT:
                    QPenManager.getInstance().setNeedInit(true);
                    finish();
                    break;
            }
        }
    }

    IQPenOnActivityResult onActivityResult;

    public void setQPenOnActivityResult(IQPenOnActivityResult callback) {
        this.onActivityResult = callback;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //蓝牙开启
        if (requestCode == REQUEST_CODE_BLUETOOTH_ENABLE) {
            if (resultCode == Activity.RESULT_OK) {
                gotoActivity(DeviceLinkGuideActivity.class);
            } else {
                isCancelBtEnable = true;
            }
        }

        if (null != onActivityResult) {
            onActivityResult.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void onViewClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_search_note_name) {//搜索笔记本
            gotoActivity(NoteSearchActivity.class);
        } else if (id == R.id.iv_bt_state) {//蓝牙连接状态图标
            actionBtStateClick(DrawPageActivity.this);
        } else if (id == R.id.iv_close) {
            mainBinding.includeMainTopTips.rlRoot.setVisibility(View.GONE);
        } else if (id == R.id.rl_top_tips) {
            mainBinding.includeMainTopTips.rlRoot.setVisibility(View.GONE);
            //同步离线数据
            showOfflineDataSyncConfirmDialog(strCostTime);
        } else {
            pageDrawFragment.onViewClick(view);
        }
    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     *
     * @param contentView
     */
    private void handleLogic(View contentView) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomPopWindow != null) {
                    mCustomPopWindow.dissmiss();
                }
                String showContent = "";
                int id = v.getId();
                if (id == R.id.tv_buss) {
                    isBuss = true;
                    showContent = getResources().getString(R.string.tishi);
                    Toast.makeText(DrawPageActivity.this, showContent, Toast.LENGTH_SHORT).show();
                } else if (id == R.id.tv_fallow) {
                    isBuss = false;
                    showContent = getResources().getString(R.string.tishi1);
                    Toast.makeText(DrawPageActivity.this, showContent, Toast.LENGTH_SHORT).show();
                } else if (id == R.id.view) {
                    if (mCustomPopWindow != null) {
                        mCustomPopWindow.dissmiss();
                    }
                }
            }
        };
        contentView.findViewById(R.id.tv_buss).setOnClickListener(listener);
        contentView.findViewById(R.id.tv_fallow).setOnClickListener(listener);
        contentView.findViewById(R.id.view).setOnClickListener(listener);
    }

    /**
     * 更新BLE蓝牙连接图标
     */
    private void updateBleStateIcon() {
        if (AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.CONNECTED) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainBinding.includeMainTopToolbar.ivBtState.setImageResource(R.drawable.icon_bt_connected);
                }
            });
        } else if (AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.DISCONNECTED) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainBinding.includeMainTopToolbar.ivBtState.setImageDrawable(null);
                    mainBinding.includeMainTopToolbar.ivBtState.setImageResource(R.drawable.icon_bt_disconnected);
                    mainBinding.includeMainTopTips.rlRoot.setVisibility(View.GONE);
                }
            });
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.ACTION_PEN_MESSAGE.equals(action)) {

                int penMsgType = intent.getIntExtra(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.MESSAGE_TYPE, 0);

                switch (penMsgType) {
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.FIND_DEVICE:
                        String penAddress = intent.getStringExtra(JsonTag.STRING_PEN_MAC_ADDRESS);
                        String penName = intent.getStringExtra(JsonTag.STRING_DEVICE_NAME);
                        if (null != mBoundBtData && !TextUtils.isEmpty(penAddress)) {
                            if (penAddress.equals(mBoundBtData.bleMac) &&
                                    AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.DISCONNECTED) {
//                                AFPenClientCtrl.getInstance().btStopSearchPeripheralsList();
//                                AFPenClientCtrl.getInstance().connect(penName, penAddress);
                            }
                        }
                        break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_CONNECTION_SUCCESS:
                        /*BluetoothData bluetoothData = SQLite.select().from(BluetoothData.class).querySingle();
                        if (ble != null && bluetoothData != null) {
                            ble.connect(bluetoothData.bleMac.split("-")[1]);
                        }*/
                        dismissDialog();
                        showToast(R.string.blue_connect_success);
                        updateBleStateIcon();
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
                        com.eningqu.aipen.qpen.QPenManager.getInstance().toAuth();
                        EventBusCarrier tcpState = new EventBusCarrier();
                        tcpState.setEventType(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Constant.BIND_STATE);
                        tcpState.setObject(com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.PEN_CONNECT);
                        EventBusUtil.post(tcpState);
                        break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_CONNECTION_FAILURE:
//                        if (!AFPenClientCtrl.getInstance().isDisconnManual()) {
                        AFPenClientCtrl.getInstance().setConnStatus(PEN_CONN_STATUS.DISCONNECTED);
                        AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.NONE);
                        dismissDialog();
                        showToast(R.string.str_pen_unbind);
                        updateBleStateIcon();

//                        }
//                        AFPenClientCtrl.getInstance().setDisconnManual(false);
                        break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_DISCONNECTED:
                        dismissDialogOfflineData();
                        dismissDialog();
                        showToast(R.string.blue_connect_discontinue);
                        updateBleStateIcon();
                        mainBinding.includeMainTopTips.rlRoot.setVisibility(View.GONE);
                        break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_CONNECTION_TRY:
                        //                        dismissDialog();
                        dismissDialogOfflineData();
                        L.error("dialog=" + dialog);
                        break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_CONNECTION_TIMEOUT:
                        dismissDialog();
                        showToast(R.string.bt_connect_timeout);
                        break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_CUR_MEMOFFSET:
                        int lastDotsCount = intent.getIntExtra(JsonTag.INT_DOTS_MEMORY_OFFSET, 0);
                        if (lastDotsCount > 0) {

                            float cost_time;
                            if (lastDotsCount <= 4800) {
                                cost_time = 10;//秒
                            } else {
                                cost_time = (lastDotsCount / 1650);//秒
                            }
                            dotSize = lastDotsCount * 10;

                            strCostTime = String.format("%1.1f", cost_time);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mainBinding.includeMainTopTips.rlRoot.setVisibility(View.VISIBLE);
                                    //                                    String message = String.format(getString(R.string.history_data_text), "" + strCostTime);
                                    if (null != mainBinding.includeMainTopTips.tvTipsMessage) {
                                        mainBinding.includeMainTopTips.tvTipsMessage.setText(getString(R.string.dialog_title_offline_data_discover) + "...");
                                    }
                                }
                            });
                        }
                        break;
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_CUR_BATT:
                        final int battery = intent.getIntExtra(JsonTag.INT_BATT_VAL, 0);
                        // intent.putExtra(JsonTag.INT_BATT_VAL, obj.getInt(JsonTag.INT_BATT_VAL));
                        final int showBatt = CommonBusUtils.bat2Percent(battery);
                        if (showBatt <= Constant.PEN_BATTERY_ALARM_VALUE) {
                            VibratorUtil.Vibrate(DrawPageActivity.this, 2000);
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
                    case com.nq.edusaas.hps.sdkcore.afpensdk.Const.PenMsgType.PEN_DELETE_OFFLINEDATA_FINISHED:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                showToast(R.string.offline_data_clean_finish);
                                mainBinding.includeMainTopTips.rlRoot.setVisibility(View.GONE);
                            }
                        });
                        break;
                }
            }
        }
    };

    /**
     * 显示离线数据同步确认对话框
     *
     * @param strCostTime
     */
    private void showOfflineDataSyncConfirmDialog(String strCostTime) {
        dismissDialogOfflineData();
        mDialogOfflineData = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        //                                                    dismissDialog();
                        dismissDialogOfflineData();
                        //同步离线数据
                        DotListenerService.getInstance().requestOfflineDataWithRange(penOfflineDataSync,dotSize);
                    }

                    @Override
                    public void cancel() {
                        //                                                    dismissDialog();
                        dismissDialogOfflineData();
                    }
                }, R.string.dialog_title_offline_data_discover,
                String.format(getString(R.string.history_data_text), "" + strCostTime),
                R.string.sync_right_now, R.string.sync_next_time, false);
    }


    /**
     * 同步离线数据提示
     */
    private IPenOfflineDataSyncListener penOfflineDataSync = new IPenOfflineDataSyncListener() {

        @Override
        public void onSyncBegin() {
            L.error("start offline data sync");
            dismissDialog();
            dialog = DialogHelper.showProgress(getSupportFragmentManager(), String.format(getString(R.string.ble_history_data_receive), 0), false);
            syncFinish = false;
            mHandler.sendEmptyMessageDelayed(MSG_WHAT_DISMISS_DIALOG_OFFLINE_SYNC, DEYED_TIME);
        }

        @Override
        public void onSyncProgress(int progress) {
            L.error("onSyncProgress offline progress = " + progress);
            final String msg = String.format(getString(R.string.ble_history_data_receive), progress);
            if (null != dialog) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogHelper.setProgressText(dialog, msg);
                    }
                });
            }
            SpUtils.putInt(DrawPageActivity.this, Constant.SP_KEY_OUTLINE_PROGRESS, progress);
            mHandler.removeMessages(MSG_WHAT_DISMISS_DIALOG_OFFLINE_SYNC);
            mHandler.sendEmptyMessageDelayed(MSG_WHAT_DISMISS_DIALOG_OFFLINE_SYNC, DEYED_TIME);
        }

        @Override
        public void onSyncEnd() {
            L.error("onSyncEnd offline ");
            mHandler.removeMessages(MSG_WHAT_DISMISS_DIALOG_OFFLINE_SYNC);
            mainBinding.includeMainTopTips.rlRoot.setVisibility(View.GONE);
            dismissDialog();
            //同步完成 ,提示选择笔记本，进入预览页面
            if (!syncFinish) {
                if (AppUtils.isAppForeground()) {
                    Intent intent = new Intent(DrawPageActivity.this, NotebookDisplayVerticalActivity.class);
                    intent.putExtra(NotebookDisplayVerticalActivity.FUN_TYPE, NotebookDisplayVerticalActivity.FUN_TYPE_OFFLINE_DATA);
                    startActivity(intent);
                } else {
                    AFPenClientCtrl.getInstance().setbSyncBackground(true);
                }
                syncFinish = true;
            }
        }
    };

}