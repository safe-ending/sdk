package com.eningqu.aipen.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.eningqu.aipen.common.HwrEngineEnum;
import com.eningqu.aipen.common.bluetooth.BluetoothClient;
import com.eningqu.aipen.common.utils.LocationUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.SmartPenApp;
import com.eningqu.aipen.adapter.MainDrawerAdapter;
import com.eningqu.aipen.base.ActivityStackManager;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.base.ui.BaseFragment;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.CommonBusUtils;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.dialog.BaseDialog;
import com.eningqu.aipen.common.dialog.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.NetworkUtil;
import com.eningqu.aipen.common.utils.NingQuLog;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.common.utils.ToastUtils;
import com.eningqu.aipen.common.utils.UserManager;
import com.eningqu.aipen.common.utils.VibratorUtil;
import com.eningqu.aipen.common.utils.ZipUtil;
import com.eningqu.aipen.databinding.ActivityMainBinding;
import com.eningqu.aipen.db.model.AASUserInfoData;
import com.eningqu.aipen.db.model.BluetoothData;
import com.eningqu.aipen.db.model.BluetoothDevice;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.db.model.OseUserInfoData;
import com.eningqu.aipen.db.model.UserInfoData;
import com.eningqu.aipen.fragment.MainFragment;
import com.eningqu.aipen.p20.DotListenerService;
import com.eningqu.aipen.p20.StudentAppManager;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.IPenOfflineDataSyncListener;
import com.eningqu.aipen.qpen.PEN_CONN_STATUS;
import com.eningqu.aipen.qpen.PEN_SYNC_STATUS;
import com.eningqu.aipen.qpen.QPenManager;
import com.eningqu.aipen.qpen.SignatureView;
import com.eningqu.aipen.qpen.bean.CommandSize;
import com.eningqu.aipen.qpen.listener.IQPenOnActivityResult;
import com.eningqu.aipen.sdk.bean.device.NQBtDevice;
import com.eningqu.aipen.sdk.bean.device.NQDeviceBase;
import com.eningqu.aipen.sdk.comm.ConnectState;
import com.eningqu.aipen.sdk.comm.JsonTag;
import com.eningqu.aipen.sdk.comm.ScanListener;
import com.eningqu.aipen.sdk.listener.PenConnectListener;
import com.eningqu.aipen.view.OfflineDialog;
import com.myscript.iink.eningqu.IInkSdkManager;
import com.nq.edusaas.hps.PenSdkCtrl;
import com.nq.edusaas.hps.sdkcore.afpensdk.Const;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;
import nq.com.ahlibrary.utils.AuthManager;

public class MainActivity extends FragmentBaseActivity implements Toolbar.OnMenuItemClickListener, MainDrawerAdapter.OnItemClickListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mainBinding;
    /***侧滑菜单适配器*/
    private MainDrawerAdapter drawerAdapter;
    /*** 用于控制"点击两次退出程序*/
    private long mExitTime;
    private RxPermissions rxPermission;
    public static boolean isBuss = true;
    private final int MSG_WHAT_DISMISS_DIALOG_OFFLINE_SYNC = 3;
    private final int MSG_WHAT_CANCEL_PERMISSION = 4;
    private final int MSG_WHAT_MYSCRIPT_DIALOG = 998;
    private final int DEYED_TIME = 20000;
    private boolean createNotebook = false;
    private boolean syncFinish = false;//是否同步完成
    private boolean isCancelPermission = false;//是否取消授权
    private boolean isCancelBtEnable = false;//是否取消打开蓝牙
    private BluetoothDevice mBoundBtData;//绑定的设备
    private String strCostTime;//离线同步耗时
    private int dotSize;//点数量

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (MSG_WHAT_DISMISS_DIALOG_OFFLINE_SYNC == msg.what) {
                dismissDialog();
                if (offlineDialog != null && offlineDialog.isShowing()) {
                    offlineDialog.dismiss();
                }
                dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        dismissDialog();
                        AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.NONE);
                        syncFinish = true;
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
                        requestPermissions();
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
//                        SpUtils.putBoolean(MainActivity.this, Constant.SP_KEY_MYSCRIPT_INSTALL, false);
//                        gotoActivity(RecordLanguageActivity.class);
//                        dismissDialog();
//                    }
//
//                    @Override
//                    public void cancel() {
//                        SpUtils.putBoolean(MainActivity.this, Constant.SP_KEY_MYSCRIPT_INSTALL, false);
//                        dismissDialog();
//                        SpUtils.putString(mContext, Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);
//                    }
//                }, getString(R.string.recognize_download_resoures));
            }
        }
    };
    private SystemLocaleChangeReceiver systemLocaleChangeReceiver;
    private ActionBarDrawerToggle toggle;
    private BaseDialog dialogAgree;
    private UserInfoData userInfoData;
    public PenConnectListener penConnectListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            L.info("UI", "onCreate savedInstanceState=" + savedInstanceState.toString());
        }
        super.onCreate(savedInstanceState);
        userInfoData = UserManager.loadUserInfo();
        mContext = this;
        rxPermission = new RxPermissions(MainActivity.this);
        //        L.info(TAG, printKeyHash(this));
        //监听笔的广播
        IntentFilter filter = new IntentFilter(Const.Broadcast.ACTION_PEN_MESSAGE);
        filter.addAction(Const.Broadcast.ACTION_PEN_DOT);
        registerReceiver(mBroadcastReceiver, filter);
        //        EventBusUtil.register(this);

        //动态注册广播
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(Intent.ACTION_LOCALE_CHANGED);
        systemLocaleChangeReceiver = new SystemLocaleChangeReceiver();
        registerReceiver(systemLocaleChangeReceiver, filter1);

        if (userInfoData != null) {
            AppCommon.setUserInfo(userInfoData);
        }
        Log.e("测试方法","onCreate onConnectState---"+AFPenClientCtrl.getInstance().getConnStatus());

        if (penConnectListener == null) {
            penConnectListener = new PenConnectListener() {
                @Override
                public void onConnectState(int state) {
                    Log.e("测试方法","onConnectState---"+state);

                    if (state == ConnectState.CONN_STATE_CONNECTED) {
                        NQBtDevice device = (NQBtDevice) PenSdkCtrl.getInstance().getCurNQDev();
                        Intent intent = new Intent(Const.Broadcast.ACTION_PEN_MESSAGE);
                        intent.putExtra(Const.Broadcast.MESSAGE_TYPE, Const.PenMsgType.PEN_CONNECTION_SUCCESS);
                        sendBroadcast(intent);
                        DotListenerService.getInstance().releaseReConnect();

                    } else if (state == ConnectState.CONN_STATE_CLOSED) {
                        Intent intent = new Intent(Const.Broadcast.ACTION_PEN_MESSAGE);
                        intent.putExtra(Const.Broadcast.MESSAGE_TYPE, Const.PenMsgType.PEN_CONNECTION_FAILURE);
                        sendBroadcast(intent);

                    }
                }

                @Override
                public void onUsbDeviceAttached(UsbDevice usbDevice) {

                }

                @Override
                public void onUsbDeviceDetached(UsbDevice usbDevice) {

                }


                @Override
                public void onReceiveException(int error, String s) {
                    if (error == 211) {
                        Intent intent2 = new Intent(Const.Broadcast.ACTION_PEN_MESSAGE);
                        intent2.putExtra(Const.Broadcast.MESSAGE_TYPE, Const.PenMsgType.PEN_CONNECTION_FAILURE);
                        sendBroadcast(intent2);
                        //设备关机情况下连5次
//                        if (!AFPenClientCtrl.getInstance().isDisconnManual()) {
                        DotListenerService.getInstance().reConnectBle();
//                        }
                    }
                }


            };
            PenSdkCtrl.getInstance().setConnectListener(penConnectListener);
        }
        ;
//        StudentAppManager.getInstance().init(this.getApplicationContext());
//        bindService();
//        receiver = new MyReceiver();
//        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        registerReceiver(receiver, homeFilter);

        DotListenerService.getInstance().reConnectBle();

    }


    /**
     * BroadcastReceiver
     **/
    public class SystemLocaleChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(Intent.ACTION_LOCALE_CHANGED)) {
                    SmartPenApp.forceExit = true;
                    finish();
                }
            }
        }
    }

    @Override
    protected void setLayout() {
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
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
        if (SmartPenApp.forceExit) {
            finish();
            return;
        }

        int initPen = SpUtils.getInt(this, Constant.SP_KEY_INIT_PEN, 0);
        if (initPen != 1 && AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.CONNECTED ) {
            //连接成功去获取授权
            QPenManager.getInstance().toAuth();
        } else if (initPen == 1) {
            AuthManager.getInstance().setbAuthStatus(true);
            long authTime = SpUtils.getLong(this, Constant.SP_KEY_AUTH_PEN_TIME, 0);
            //距离上次授权大于15天再次请求授权//15 * 24 * 3600 * 1000
            if (AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.CONNECTED && System.currentTimeMillis() - authTime > 15 * 24 * 3600 * 1000) {
                //连接成功去获取授权
                QPenManager.getInstance().toAuth();
            }
        }

        userInfoData = AppCommon.loadUserInfo();
        if (null != userInfoData) {
            int size = SpUtils.getInt(this, AppCommon.getUserUID() + "_" + "size", 1);
            int color = SpUtils.getInt(this, AppCommon.getUserUID() + "_" + "color", ContextCompat.getColor(app, R.color.colors_menu_black));
            QPenManager.getInstance().setPaintColor(color);
            QPenManager.getInstance().setPaintSize(CommandSize.getSizeByLevel(size));
        }

        //更新蓝牙连接状态图标
        updateBleStateIcon();
        NQBtDevice btDevice = (NQBtDevice) PenSdkCtrl.getInstance().getConnectedDevice();


//        //获取绑定的设备
//        mBoundBtData = AppCommon.loadBleInfo2();
//        mainBinding.includeMainTopTips.rlRoot.post(new Runnable() {
//            @Override
//            public void run() {
//                if (mBoundBtData != null && !TextUtils.isEmpty(mBoundBtData.bleName) && (!mBoundBtData.bleName.startsWith(AppCommon.PEN_QPEN)) &&
//                        AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.DISCONNECTED) {
//                    if (BluetoothClient.getBle().getBlueToothStatus()) {
//                        //开启定位
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            if (!LocationUtils.isLocationEnabled(app)) {
//                                dialog = DialogHelper.showGpsTips(getSupportFragmentManager(), new ConfirmListener() {
//                                    @Override
//                                    public void confirm(View view) {
//                                        dismissDialog();
//                                        LocationUtils.openLocationSettings(app);
//                                    }
//
//                                    @Override
//                                    public void cancel() {
//                                        dismissDialog();
//                                    }
//                                });
//                                return;
//                            }
//                        }
//                        //启动搜索
//                        PenSdkCtrl.getInstance().startScanDevice();
//                        //搜索结果监听
//                        PenSdkCtrl.getInstance().setScanListener(new ScanListener() {
//                            @Override
//                            public void onScanStart() {
//
//                            }
//
//                            @Override
//                            public void onScanStop() {
//
//                            }
//
//                            @Override
//                            public void onReceiveException(int i, String s) {
//                            }
//
//                            @Override
//                            public void onScanResult(NQDeviceBase nqDeviceBase) {
//                                if (mBoundBtData.bleMac.equals(((NQBtDevice) nqDeviceBase).mac)) {
//                                    NQBtDevice btDevice = new NQBtDevice();
//                                    btDevice.name = mBoundBtData.bleName;
//                                    btDevice.mac = mBoundBtData.bleMac;
//                                    PenSdkCtrl.getInstance().setCurNQDev(btDevice);
//                                    AFPenClientCtrl.getInstance().lastTryConnectAddr = mBoundBtData.bleMac;
//                                    AFPenClientCtrl.getInstance().lastTryConnectName = mBoundBtData.bleName;
//                                    PenSdkCtrl.getInstance().stopScan();
//                                    PenSdkCtrl.getInstance().connect(btDevice);
//
//                                }
//                            }
//                        });
//                    } else {
//                        BluetoothClient.getBle().openBlueTooth(MainActivity.this, REQUEST_CODE_BLUETOOTH_ENABLE);
//                    }
//
//                }
//            }
//        });

        //显示离线同步的等待框
        if (AFPenClientCtrl.getInstance().getSyncStatus() == PEN_SYNC_STATUS.SYNCHRONIZING) {
            Log.e("测试方法","AFPenClientCtrl SYNCHRONIZING");
            int progress = SpUtils.getInt(MainActivity.this, Constant.SP_KEY_OUTLINE_PROGRESS, 0);
            dialog = DialogHelper.showProgress(getSupportFragmentManager(), String.format(getString(R.string.ble_history_data_receive), progress), false);
            syncFinish = false;
            mHandler.sendEmptyMessageDelayed(MSG_WHAT_DISMISS_DIALOG_OFFLINE_SYNC, DEYED_TIME);
        }

        //动态获取安卓系统授权
        if (!isCancelPermission) {
            requestPermissions();
        } else if (!AppCommon.checkLogin() && !SmartPenApp.forceExit) {
            //检查登录状态
            gotoActivity(LoginActivity.class, true);
            finish();
        }

        updateTopToolbar("");

        if (AFPenClientCtrl.getInstance().isbSyncBackground()) {
            AFPenClientCtrl.getInstance().setbSyncBackground(false);
            dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                @Override
                public void confirm(View view) {
                    Intent intent = new Intent(mContext, NotebookDisplayVerticalActivity.class);
                    intent.putExtra(NotebookDisplayVerticalActivity.FUN_TYPE, NotebookDisplayVerticalActivity.FUN_TYPE_OFFLINE_DATA);
                    mContext.startActivity(intent);
                }

                @Override
                public void cancel() {

                }
            }, getString(R.string.str_sync_finish));
        }

//        boolean myscriptInstall = SpUtils.getBoolean(MainActivity.this, Constant.SP_KEY_MYSCRIPT_INSTALL, true);
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
//                    SpUtils.putString(mContext, Constant.SP_KEY_RECO_LANGUAGE, "en_US");
//                }
//            } else {
//                SpUtils.putString(mContext, Constant.SP_KEY_RECO_LANGUAGE, "en_US");
//            }
//        }

        requestOffline();
        showUserAgree();
    }

    private void requestOffline() {
        mainBinding.includeMainTopTips.rlRoot.setVisibility(View.GONE);
        Boolean sync = SpUtils.getBoolean(MainActivity.this, Constant.SP_KEY_SYNC, true);
        if (sync && !SmartPenApp.isSync) {
            //请求离线数据
            String lastName = AFPenClientCtrl.getInstance().getLastTryConnectName();
            if (!TextUtils.isEmpty(lastName)) {
                PenSdkCtrl.getInstance().requestOfflineDataLength();
            }
        }
    }

    private void showUserAgree() {
        Boolean isAgree = SpUtils.getBoolean(this, Constant.SP_KEY_USER_AGREE, false);
        if (!isAgree) {
            SpannableStringBuilder ss = new SpannableStringBuilder(getString(R.string.str_agree_content1));
            String string = getString(R.string.str_agree_sure);
            SpannableString sAgreeSure1 = new SpannableString(string);
            sAgreeSure1.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(WebViewActivity.TYPE_KEY, WebViewActivity.WEB_VIEW_TYPE_PRO);
                    gotoActivity(WebViewActivity.class, bundle);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ContextCompat.getColor(MainActivity.this, R.color.app_click_text_green));
                    ds.setUnderlineText(false);    //去除超链接的下划线
                }

            }, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.append(sAgreeSure1);
            SpannableStringBuilder sAnd = new SpannableStringBuilder(getString(R.string.str_add));
            ss.append(sAnd);
            String string2 = getString(R.string.str_agree_sure2);
            SpannableString sAgreeSure2 = new SpannableString(string2);
            sAgreeSure2.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(WebViewActivity.TYPE_KEY, WebViewActivity.WEB_VIEW_TYPE_PRO2);
                    gotoActivity(WebViewActivity.class, bundle);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ContextCompat.getColor(MainActivity.this, R.color.app_click_text_green));
                    ds.setUnderlineText(false);    //去除超链接的下划线
                }

            }, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.append(sAgreeSure2);

            SpannableStringBuilder ss2 = new SpannableStringBuilder(getString(R.string.str_agree_content2));
            ss.append(ss2);

            dialogAgree = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                @Override
                public void confirm(View view) {
                    if (dialogAgree != null) {
                        dialogAgree.dismiss();
                        dialogAgree = null;
                    }
                    SpUtils.putBoolean(MainActivity.this, Constant.SP_KEY_USER_AGREE, true);
                }

                @Override
                public void cancel() {
                    if (dialogAgree != null) {
                        dialogAgree.dismiss();
                        dialogAgree = null;
                    }
                    SmartPenApp.forceExit = true;
                    finish();
                }
            }, R.string.user_agreement_title_all, ss, R.string.str_agree, R.string.str_unagree, false);
        }
    }

    private void unzipDb() {
        File file = new File(AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + "aipendb.zip");
        if (file.exists()) {
            try {
                ZipUtil.unzip(file.getAbsolutePath(), AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + "aipendb");
                FileUtils.delete(file);

                File db = new File(AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + "aipendb" + File.separator + "AiPenDB.db");
                BufferedReader br = null;
                Reader read = null;
                read = new FileReader(db);
                br = new BufferedReader(read);
                String content = "";
                while ((content = br.readLine()) != null) {
                    Log.w("test", "content = " + content);
                }

                //得到数据库的输入流
                InputStream is = new FileInputStream(AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + "aipendb" + File.separator + "AiPenDB.db");
                //用输出流写到SDcard上面
                FileOutputStream fos = new FileOutputStream("/data/data/com.eningqu.aipen/databases/AiPenDB.db");
                //创建byte数组  用于1KB写一次
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                //最后关闭就可以了
                fos.flush();
                fos.close();
                is.close();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                FileUtils.delete(AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + "aipendb");
            }

        }
    }

//    private void unzipBooks() {
//        File file = new File(AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + userInfo.userUid + ".zip");
//        if (file.exists()) {
//            try {
//                ZipUtil.unzip(file.getAbsolutePath(), AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + userInfo.userUid);
//                FileUtils.deleteFile(file);
//                File zipFiles = new File(AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + userInfo.userUid);
//                File[] files = zipFiles.listFiles();
//                for (File f : files) {
//                    String name = f.getName();
//                    File dbFile = new File(AppCommon.NQ_SAVE_ROOT_PATH + File.separator + userInfo.userUid + File.separator + name);
//                    if (dbFile.exists()) {
//                        FileUtils.deleteDir(dbFile);
//                    }
////                    FileUtils.createOrExistsDir(dbFile);
//                    boolean b = FileUtils.moveDir(f, dbFile, new FileUtils.OnReplaceListener() {
//                        @Override
//                        public boolean onReplace() {
//                            return false;
//                        }
//                    });
//                }
//                FileUtils.deleteDir(zipFiles);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    @Override
    protected void onPause() {
        super.onPause();
        mainBinding.drawerLayout.closeDrawer(GravityCompat.START);

    }

    public void bindService() {
        if (isConnected) {
            return;
        }
//        L.debug("测试,", "bindService");
        SmartPenApp.isSync = false;
        //系统参数配置完成，启动服务，定时获取轮询数据
        final Intent intent = new Intent(this, DotListenerService.class);
        isConnected = bindService(intent, serviceConn, BIND_AUTO_CREATE);
        mainBinding.mainContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestOffline();
            }
        }, 2000);
    }

    //定义一个全局变量用来标记
    private boolean isConnected = false;
    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != outState) {
            L.info("UI", "onSaveInstanceState outState=" + outState.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("测试方法","onDestroy");

        if (offlineDialog != null && offlineDialog.isShowing()) {
            offlineDialog.dismiss();
        }
        AFPenClientCtrl.getInstance().setConnStatus(PEN_CONN_STATUS.DISCONNECTED);
        AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.NONE);
        PenSdkCtrl.getInstance().disconnect();

        if (isConnected) {
            unbindService(serviceConn);//conn表示ServiceConnection 对象
            isConnected = false;
        }
        mStrokeView = null;
        drawerAdapter = null;
        AppCommon.logoutReset(1);
        unregisterReceiver(mBroadcastReceiver);
        unregisterReceiver(systemLocaleChangeReceiver);
        //        EventBusUtil.unregister(this);
//        if (SmartPenApp.forceExit) {
        Log.e("测试方法","onDestroy forceExit");

        StudentAppManager.getInstance().unInit();
        QPenManager.getInstance().unInit();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 300);
//        }
    }

    @Override
    protected void initData() {
        mainFragment = (MainFragment) getFragment(MainFragment.class);

        userInfoData = AppCommon.loadUserInfo();
        if (null != userInfoData) {
//            unzipBooks();
//            unzipDb();
        }
    }

    @Override
    protected void initView() {
        init();
    }

    @Override
    protected BaseFragment getFirstFragment() {
        mainFragment = (MainFragment) getFragment(MainFragment.class);
        return mainFragment;
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

    public void toHideDrawer() {
        if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * 初始化
     */
    private void init() {
        setLeftDrawLayout();

        drawerAdapter = new MainDrawerAdapter(this, AppCommon.loadUserInfo());
        drawerAdapter.setOnItemClickListener(this);
        mainBinding.navDrawer.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.navDrawer.setAdapter(drawerAdapter);
        //默认设置断开连接的图标
        mainBinding.includeMainTopToolbar.ivBtState.setImageResource(R.drawable.icon_bt_disconnected);

        mainBinding.includeMainTopTips.rlRoot.setVisibility(View.GONE);

        //        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        //        layoutParams.topMargin = statusBarHeight;
        //        mainBinding.drawerLayout.setLayoutParams(layoutParams);
    }

    private void setLeftDrawLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //将侧边栏顶部延伸至status bar
            mainBinding.drawerLayout.setFitsSystemWindows(true);
            //将主页面顶部延伸至status bar;虽默认为false,但经测试,DrawerLayout需显示设置
            mainBinding.drawerLayout.setClipToPadding(false);
        }
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

        //创建返回键，并实现打开关/闭监听
        toggle = new ActionBarDrawerToggle(this,
                mainBinding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        toggle.syncState();
        //设置home图标
        toolbar.setNavigationIcon(R.drawable.icon_menu);
        mainBinding.drawerLayout.addDrawerListener(toggle);
    }

    @Override
    protected void initEvent() {
        //        //设置返回键点击监听
        setOnKeyListener(new OnKeyClickListener() {
            @Override
            public void clickBack() {
                //两秒内点击两次则退出程序
                if (System.currentTimeMillis() - mExitTime > 2000) {
                    showToast(R.string.exit_tips);
                    mExitTime = System.currentTimeMillis();

                    if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                    }
                } else {
                    SmartPenApp.forceExit = true;
//                    finish();
                    ActivityUtils.finishAllActivities();
                    System.exit(0);
                    //                    ActivityStackManager asm = getActivityStackManager();
                    //                    asm.exitAllActivityExceptCurrent(null);
                    //                    asm.exitApplication();
                }
            }
        });
    }

    /**
     * 按返回键时，如果侧滑菜单处于打开状态则先关闭侧滑菜单
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        if (KeyEvent.KEYCODE_BACK == keyCode) {

            if (null != currentFragment) {
                if (currentFragment != mainFragment) {
                    switchFragment(mainFragment);
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

//    private class MyReceiver extends BroadcastReceiver {
//
//        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
//        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
//        private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
//
//                if (reason == null)
//                    return;
//
//                // Home键
//                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
//                }
//
//                // 最近任务列表键
//                if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
//                    pageDrawFragment.clickMenuDraw();
//                }
//            }
//        }
//    }


    /**
     * 创建toolbar菜单（右侧菜单）
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(, menu);
        return false;
    }

    /**
     * 在onCreateOptionsMenu执行后，菜单被显示前调用；如果菜单已经被创建，则在菜单显示前被调用。 同样的，
     * 返回true则显示该menu,false 则不显示; （可以通过此方法动态的改变菜单的状态，比如加载不同的菜单等）
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * 每次菜单被关闭时调用.
     * 菜单被关闭有三种情形:
     * 1.展开menu的按钮被再次点击
     * 2.back按钮被点击
     * 3.用户选择了某一个菜单项
     */
    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    /**
     * toolbar右侧菜单监听事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return true;
    }


    /**
     * 侧滑菜单点击事件
     *
     * @param ResId
     */
    @Override
    public void itemClick(int ResId) {
        switch (ResId) {
            case R.string.drawer_home:
                SmartPenApp.isFirst = false;
                mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                //                pushFragment(MainFragment.newInstance());
                switchFragment(mainFragment);
                break;
            case R.string.drawer_note_collect:
                gotoActivity(CollectActivity.class);
                SmartPenApp.isFirst = false;
                break;
            case R.string.calendar_text:
                gotoActivity(CalendarSearchActivity.class);
                SmartPenApp.isFirst = false;
                break;
            case R.string.label_text:
                gotoActivity(NotebookDisplayVerticalActivity.class);
                SmartPenApp.isFirst = false;
                break;
            case R.string.drawer_search:
                gotoActivity(NoteSearchActivity.class);
                SmartPenApp.isFirst = false;
                break;
            case R.string.drawer_ble_scan:
                if (!ble.connectStatus) {
                    gotoActivity(DeviceLinkGuideActivity.class);
                    SmartPenApp.isFirst = false;
                } else {
                    showToast(R.string.collect_success_msg);
                }
                break;
            case R.string.drawer_setting:
                gotoActivity(SettingActivity.class);
                SmartPenApp.isFirst = false;
                break;
            case R.string.drawer_operating: {
                Bundle bundle = new Bundle();
                bundle.putInt(WebViewActivity.TYPE_KEY, WebViewActivity.WEB_VIEW_TYPE_INT);
                gotoActivity(WebViewActivity.class, bundle);
                SmartPenApp.isFirst = false;
            }
            break;
            case R.string.drawer_cloud_backups:
                showToast(R.string.developing);
                mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.string.user_agreement_title2: {
                Bundle bundle = new Bundle();
                bundle.putInt(WebViewActivity.TYPE_KEY, WebViewActivity.WEB_VIEW_TYPE_PRO2);
                gotoActivity(WebViewActivity.class, bundle);
                SmartPenApp.isFirst = false;
            }
            break;
            case R.string.drawer_data_migration:
                gotoActivity(MigrationDataActivity.class);
                SmartPenApp.isFirst = false;
                break;
        }
    }

    /**
     * 动态权限
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSSIONS);*/
            rxPermission.request(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            ).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if (aBoolean) {
                        com.myscript.iink.eningqu.FileUtils.copyFileOrDir(mContext, "conf", com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS);
                        com.myscript.iink.eningqu.FileUtils.copyFileOrDir(mContext, "resources", com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS);
                        isCancelPermission = true;
                        //完成授权
                        SmartPenApp.getApp().appInit();
                        //先初始化再打开蓝牙
                        //开始扫描蓝牙设备，自动连接
                        if (!isCancelBtEnable) {
                            openBluetoothAndLocation(MainActivity.this);
                        }

                        if (QPenManager.getInstance().getHwrEngineEnum() == HwrEngineEnum.MY_SCRIPT) {
                            IInkSdkManager.getInstance().copyRecoRes(mContext);
                        }
                    } else {
                        isCancelPermission = true;
                        mHandler.sendEmptyMessage(MSG_WHAT_CANCEL_PERMISSION);
                    }
                }
            });
        } else {
            SmartPenApp.getApp().appInit();
            //先初始化再打开蓝牙
            openBluetoothAndLocation(MainActivity.this);

            if (QPenManager.getInstance().getHwrEngineEnum() == HwrEngineEnum.MY_SCRIPT) {
                IInkSdkManager.getInstance().copyRecoRes(mContext);
            }
        }
    }

    public void updateTopToolbar(String label) {
        mainBinding.includeMainTopToolbar.toolbar.setVisibility(View.VISIBLE);
        mainBinding.includeMainTopToolbar.rlToolbarSearch.setVisibility(View.VISIBLE);
        mainBinding.includeMainTopToolbar.rlToolbarLabel.setVisibility(View.GONE);
        mainBinding.includeMainTopToolbar.ivMore.setVisibility(View.GONE);
        mainBinding.includeMainTopToolbar.tvPageLabel.setText("");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusCarrier carrier) {
        if (null != carrier) {
            switch (carrier.getEventType()) {
                case Constant.OPEN_NOTEBOOK_BY_SEARCH_CODE:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Bundle bundle = (Bundle) carrier.getObject();
                            gotoPageDrawFragment(bundle);
                        }
                    });
                    break;
                case Constant.USER_LOGOUT:
                    logout();

                    break;
                case Constant.NQ_SER_AUTH_FAIL:
                    int anInt = SpUtils.getInt(this, Constant.SP_KEY_INIT_PEN, 0);
                    if (anInt == -1) {
                        ToastUtils.showShort(SmartPenApp.getApp().getResources().getString(R.string.pen_error));
                    } else if (anInt == -2) {
                        ToastUtils.showShort(SmartPenApp.getApp().getResources().getString(R.string.str_network_error2));
                    }

                    break;

                case com.nq.edusaas.hps.sdkcore.afpensdk.Const.Constant.BIND_STATE:
                    if (carrier.getObject() == com.nq.edusaas.hps.sdkcore.afpensdk.Const.Broadcast.PEN_CONNECT) {
//                        StudentAppManager.getInstance().init(this.getApplicationContext());
                        bindService();
                        mainBinding.includeMainTopToolbar.ivBtState.setImageResource(AppCommon.getPower(PenSdkCtrl.getInstance().getLastPower()));

                    } else {
//                        StudentAppManager.getInstance().unInit();
                        if (isConnected) {
                            unbindService(serviceConn);//conn表示ServiceConnection 对象
                            isConnected = false;
                        }
                        mainBinding.includeMainTopToolbar.ivBtState.setImageDrawable(null);
                        mainBinding.includeMainTopToolbar.ivBtState.setImageResource(R.drawable.icon_bt_disconnected);
                        mainBinding.includeMainTopTips.rlRoot.setVisibility(View.GONE);
                    }
                    break;

                case Constant.POWER_CODE:
                    mainBinding.includeMainTopToolbar.ivBtState.setImageResource(AppCommon.getPower((Integer) carrier.getObject()));
                    break;
            }
        }
    }

    private void logout() {
        QPenManager.getInstance().setNeedInit(true);
        PenSdkCtrl.getInstance().setCurNQDev(null);
        PenSdkCtrl.getInstance().disconnect();
        UserManager.exitUser();
        try{

            Delete.tables(UserInfoData.class);
            Delete.tables(AASUserInfoData.class);
            Delete.tables(OseUserInfoData.class);
        } catch (Exception e){
            e.printStackTrace();
        }
//        ActivityStackManager.getInstance().exitApplication();
//        finish();
    }

    /**
     * 跳转到书写页面
     *
     * @param noteBookData
     */
    public void gotoPageDrawFragment(@NonNull NoteBookData noteBookData) {
        Bundle bundle = new Bundle();
        bundle.putString(BaseActivity.NOTEBOOK_ID, noteBookData.notebookId);
        bundle.putInt(BaseActivity.PAGE_NUM, AppCommon.getCurrentPage());
        bundle.putString(BaseActivity.NOTE_NAME, noteBookData.noteName);
        bundle.putInt(BaseActivity.NOTE_TYPE, noteBookData.noteType);
        //        addFragment(PageDrawFragment.newInstance());
        L.error("switchFragment pageDrawFragment page = " + AppCommon.getCurrentPage());
        mainFragment.dismissDialog();
        gotoActivity(DrawPageActivity.class, bundle);
    }

    public void gotoPageDrawFragment(@NonNull Bundle bundle) {
//        Bundle bundle1 = new Bundle();
//            bundle1.putString(BaseActivity.NOTEBOOK_ID, bundle.getString(BaseActivity.NOTEBOOK_ID));
//            bundle1.putInt(BaseActivity.PAGE_NUM, bundle.getInt(BaseActivity.PAGE_NUM));
//            bundle1.putString(BaseActivity.NOTE_NAME, bundle.getString(BaseActivity.NOTE_NAME));
//            bundle1.putInt(BaseActivity.NOTE_TYPE, bundle.getInt(BaseActivity.NOTE_TYPE));
        gotoActivity(DrawPageActivity.class, bundle);
        ActivityStackManager.getInstance().exitAllActivityExceptCurrent(MainActivity.class);
    }

    IQPenOnActivityResult onActivityResult;

    public void setQPenOnActivityResult(IQPenOnActivityResult callback) {
        this.onActivityResult = callback;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //蓝牙开启
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BLUETOOTH_ENABLE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
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
        switch (view.getId()) {
            case R.id.tv_search_note_name: {
                //搜索笔记本
                gotoActivity(NoteSearchActivity.class);
            }
            break;
            case R.id.iv_bt_state: {
                //蓝牙连接状态图标
                actionBtStateClick(MainActivity.this);
            }
            break;
            case R.id.iv_close: {
                SmartPenApp.isSync = true;
                mainBinding.includeMainTopTips.rlRoot.setVisibility(View.GONE);
            }
            break;
            case R.id.rl_top_tips: {
                mainBinding.includeMainTopTips.rlRoot.setVisibility(View.GONE);
                //同步离线数据
                showOfflineDataSyncConfirmDialog(strCostTime);
            }
            break;
            default: {
                mainFragment.onViewClick(view);
            }
        }
    }

//    /**
//     * 处理弹出显示内容、点击事件等逻辑
//     *
//     * @param contentView
//     */
//    private void handleLogic(View contentView) {
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mCustomPopWindow != null) {
//                    mCustomPopWindow.dissmiss();
//                }
//                String showContent = "";
//                switch (v.getId()) {
//                    case R.id.tv_buss:
//                        isBuss = true;
//                        showContent = getResources().getString(R.string.tishi);
//                        Toast.makeText(mContext, showContent, Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.tv_fallow:
//                        isBuss = false;
//                        showContent = getResources().getString(R.string.tishi1);
//                        Toast.makeText(mContext, showContent, Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.view: {
//                        if (mCustomPopWindow != null) {
//                            mCustomPopWindow.dissmiss();
//                        }
//                        break;
//                    }
//                }
//            }
//        };
//        contentView.findViewById(R.id.tv_buss).setOnClickListener(listener);
//        contentView.findViewById(R.id.tv_fallow).setOnClickListener(listener);
//        contentView.findViewById(R.id.view).setOnClickListener(listener);
//    }

    /*public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            L.info(TAG, "Package Name=" + context.getApplicationContext().getPackageName());

            MessageDigest md;
            for (Signature signature : packageInfo.signatures) {
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                L.info(TAG, "Key Hash=" + key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            L.error(TAG, "Name not found " + e1.toString());
        } catch (NoSuchAlgorithmException e) {
            L.error(TAG, "No such an algorithm " + e.toString());
        } catch (Exception e) {
            L.error(TAG, "Exception" + e.toString());
        }

        return key;
    }*/

    /**
     * 更新BLE蓝牙连接图标
     */
    private void updateBleStateIcon() {
        if (AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.CONNECTED) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainBinding.includeMainTopToolbar.ivBtState.setImageResource(AppCommon.getPower(PenSdkCtrl.getInstance().getLastPower()));
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

            if (Const.Broadcast.ACTION_PEN_MESSAGE.equals(action)) {

                int penMsgType = intent.getIntExtra(Const.Broadcast.MESSAGE_TYPE, 0);

                switch (penMsgType) {
                    case Const.PenMsgType.FIND_DEVICE:
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
                    case Const.PenMsgType.PEN_CONNECTION_SUCCESS:
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
                        L.debug("测试,", "PEN_CONNECTION_SUCCESS");

                        EventBusCarrier tcpState = new EventBusCarrier();
                        tcpState.setEventType(Const.Constant.BIND_STATE);
                        tcpState.setObject(Const.Broadcast.PEN_CONNECT);
                        EventBusUtil.post(tcpState);

//                        AFPenClientCtrl.getInstance().setDisconnManual(false);

                        int initPen = SpUtils.getInt(MainActivity.this, Constant.SP_KEY_INIT_PEN, 0);
                        if (initPen != 1 && AFPenClientCtrl.getInstance().getConnStatus() == PEN_CONN_STATUS.CONNECTED) {
                            //连接成功没授权的笔去获取授权
                            QPenManager.getInstance().toAuth();
                        }
                        break;
                    case Const.PenMsgType.PEN_CONNECTION_FAILURE:

                        AFPenClientCtrl.getInstance().setConnStatus(PEN_CONN_STATUS.DISCONNECTED);
                        AFPenClientCtrl.getInstance().setSyncStatus(PEN_SYNC_STATUS.NONE);
                        dismissDialog();
                        showToast(R.string.str_pen_unbind);
                        updateBleStateIcon();

//                        AFPenClientCtrl.getInstance().setDisconnManual(false);
                        break;
                    case Const.PenMsgType.PEN_DISCONNECTED:
                        dismissDialogOfflineData();
                        dismissDialog();
                        showToast(R.string.blue_connect_discontinue);
                        updateBleStateIcon();
                        mainBinding.includeMainTopTips.rlRoot.setVisibility(View.GONE);
                        break;

                    case Const.PenMsgType.PEN_CONNECTION_TRY:
                        //                        dismissDialog();
                        dismissDialogOfflineData();
                        L.error("dialog=" + dialog);
                        break;
                    case Const.PenMsgType.PEN_CONNECTION_TIMEOUT:
                        dismissDialog();
                        showToast(R.string.bt_connect_timeout);
                        break;
                    case Const.PenMsgType.PEN_CUR_MEMOFFSET:
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
                    case Const.PenMsgType.PEN_CUR_BATT:
                        final int battery = intent.getIntExtra(JsonTag.INT_BATT_VAL, 0);
                        // intent.putExtra(JsonTag.INT_BATT_VAL, obj.getInt(JsonTag.INT_BATT_VAL));
                        final int showBat = CommonBusUtils.bat2Percent(battery);
                        if (showBat <= Constant.PEN_BATTERY_ALARM_VALUE) {
                            VibratorUtil.Vibrate(MainActivity.this, 2000);
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
                                    }, String.format(getString(R.string.batt_alarm), showBat));
                                }
                            });
                        }
                        break;
                    case Const.PenMsgType.PEN_DELETE_OFFLINEDATA_FINISHED:
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


    private OfflineDialog offlineDialog;

    /**
     * 同步离线数据提示
     */
    private IPenOfflineDataSyncListener penOfflineDataSync = new IPenOfflineDataSyncListener() {

        @Override
        public void onSyncBegin() {
            L.error("start offline data sync");
            dismissDialog();
//            dialog = DialogHelper.showProgress(getSupportFragmentManager(), String.format(getString(R.string.ble_history_data_receive), 0), false);
            syncFinish = false;
            mHandler.sendEmptyMessageDelayed(MSG_WHAT_DISMISS_DIALOG_OFFLINE_SYNC, DEYED_TIME);
            try {
                if (offlineDialog == null) {
                    offlineDialog = new OfflineDialog(mContext);
                }
                if (!offlineDialog.isShowing()) {
                    offlineDialog.show();
                }
                offlineDialog.setContent(String.format(getString(R.string.ble_history_data_receive), 0));
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onSyncProgress(int progress) {
            L.error("onSyncProgress offline progress = " + progress);
            final String msg = String.format(getString(R.string.ble_history_data_receive), progress);
            if (offlineDialog == null) {
                offlineDialog = new OfflineDialog(mContext);
            }
            if (!offlineDialog.isShowing()) {
                offlineDialog.show();
            }
            offlineDialog.setContent(msg);

//            if (dialog != null) {
//                Dialog dialog1 = dialog.getDialog();
//                if (dialog1 != null){
//                    final TextView content = dialog1.findViewById(R.id.dialog_content);
//                    if (null != content) {
//                        content.setText(msg);
//                    }
//                }else{
//                    dismissDialog();
//                    dialog = DialogHelper.showProgress(getSupportFragmentManager(),msg, false);
//
//                }
//            }
            SpUtils.putInt(MainActivity.this, Constant.SP_KEY_OUTLINE_PROGRESS, progress);
            mHandler.removeMessages(MSG_WHAT_DISMISS_DIALOG_OFFLINE_SYNC);
            mHandler.sendEmptyMessageDelayed(MSG_WHAT_DISMISS_DIALOG_OFFLINE_SYNC, DEYED_TIME);
        }

        @Override
        public void onSyncEnd() {
            L.error("onSyncEnd offline ");
            offlineDialog.dismiss();
            mHandler.removeMessages(MSG_WHAT_DISMISS_DIALOG_OFFLINE_SYNC);
            mainBinding.includeMainTopTips.rlRoot.setVisibility(View.GONE);
            dismissDialog();
            //同步完成 ,提示选择笔记本，进入预览页面
            if (!syncFinish) {
                if (AppUtils.isAppForeground()) {
                    Intent intent = new Intent(mContext, NotebookDisplayVerticalActivity.class);
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