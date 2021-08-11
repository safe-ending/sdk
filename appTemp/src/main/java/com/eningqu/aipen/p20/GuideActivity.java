//package com.eningqu.aipen.p20;
//
//import android.Manifest;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//
//import androidx.annotation.Nullable;
//import androidx.databinding.DataBindingUtil;
//
//import com.eningqu.aipen.R;
//import com.nq.edusaas.hps.activity.BaseActivity;
//
///**
// * @Author: Qiu.Li
// * @Create Date: 2021/1/8 11:14
// * @Description: 引导连接设备
// * @Email: liqiupost@163.com
// */
//public class GuideActivity extends BaseActivity {
//
//    ActivityGuideForP20Binding binding;
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_guide_for_p20);
//    }
//
//    @Override
//    protected void doNext() {
//
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        baseCheckPermission(this, new String[]{Manifest.permission.BLUETOOTH,
//                Manifest.permission.ACCESS_FINE_LOCATION});
//    }
//
//    public void onViewClick(View v) {
//
//        if (v.getId() == R.id.iv_back) {
//            finish();
//        } else if(v.getId() == R.id.btn_scan){
//            Intent intent = new Intent(this, DeviceListActivity.class);
//            this.startActivity(intent);
//            finish();
//        }
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//    }
//
//    /*@Override
//    public int contentViewLayoutId() {
//        return R.layout.activity_guide;
//    }
//
//    @Override
//    protected void onInitView(View decorView) {
//        mViewBinding.deviceLinkGuideNext.setOnClickListener(this);
//        mViewBinding.ivBack.setOnClickListener(this);
//
//        BluetoothData bluetoothData = BleInfoManager.loadBleInfo();
//        if (null == bluetoothData) {
//            //监听笔的广播
//            IntentFilter filter = new IntentFilter(Const.Broadcast.ACTION_PEN_MESSAGE);
//            filter.addAction(Const.Broadcast.ACTION_PEN_DOT);
//            registerReceiver(mBroadcastReceiver, filter);
//        }
//
//        mViewBinding.deviceLinkGuideNext.setOnClickListener(new PerfectClickListener() {
//            @Override
//            protected void onNoDoubleClick(View v) {
//                ActivityLauncher.start(self(), new Intent(self(), DeviceListActivity.class));
//                finish();
//            }
//        });
//    }
//
//
//
//
//
//    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//
//            if (Const.Broadcast.ACTION_PEN_MESSAGE.equals(action)) {
//
//                int penMsgType = intent.getIntExtra(Const.Broadcast.MESSAGE_TYPE, 0);
//
//                switch (penMsgType) {
//                    case Const.PenMsgType.PEN_CONNECTION_SUCCESS:
//                        String mac = PenSdkCtrl.getInstance().getLastTryConnectName().toUpperCase();
//                        String[] split = mac.split("-");
//                        if (split.length > 1) {
//                            mac = split[1];
//                        }
//
//
//                        dismissDialogConn();
//                        finish();
//                        UIHelper.showToastShort(R.string.blue_connect_success);
//                        break;
//                    case Const.PenMsgType.PEN_CONNECTION_FAILURE:
//                        dismissDialogConn();
//                        UIHelper.showToastShort(R.string.blue_connect_fail);
//                        break;
//                    case Const.PenMsgType.PEN_DISCONNECTED:
//                        dismissDialogConn();
//                        UIHelper.showToastShort(R.string.blue_connect_discontinue);
//                        break;
//                    case Const.PenMsgType.PEN_CONNECTION_TRY:
//                        if (mProgressPopup == null) {
//                            mProgressPopup = new ProgressPopup(DeviceLinkGuideActivity.this,
//                                    String.format(getString(R.string.blue_connecting), PenSdkCtrl.getInstance().getLastTryConnectName().toUpperCase()));
//                        }
//                        mProgressPopup.showPopupWindow();
////                        if (null == mDialogConn) {
////                            mDialogConn = DialogHelper.showProgress(getSupportFragmentManager(), String.format(getString(R.string.blue_connecting), AFPenClientCtrl.getInstance().getLastTryConnectName().toUpperCase()), false);
////                        }
//                        break;
//                    case Const.PenMsgType.PEN_CONNECTION_TIMEOUT:
//                        dismissDialogConn();
//                        UIHelper.showToastShort(R.string.bt_connect_timeout);
//                        break;
//                }
//            }
//        }
//    };
//
//    private ProgressPopup mProgressPopup;
//
//    public void dismissDialogConn() {
//        if (!isFinishing() && !isDestroyed()) {
//            if (mProgressPopup != null && mProgressPopup.isShowing()) {
//                mProgressPopup.dismiss(false);
//            }
//        }
//    }*/
//}
