package com.eningqu.aipen.activity;

import android.Manifest;
import android.os.Build;
import android.view.View;

import com.blankj.utilcode.util.SPUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.SmartPenApp;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.databinding.ActivityPermissionBinding;
import com.tbruyelle.rxpermissions2.RxPermissions;

import androidx.databinding.DataBindingUtil;
import io.reactivex.functions.Consumer;

public class PermissionActivity extends BaseActivity {

    private ActivityPermissionBinding mBinding;
    private RxPermissions rxPermission;
    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_permission);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        rxPermission = new RxPermissions(this);
    }

    @Override
    protected void initEvent() {

    }

    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.permission_btn:
                requestPermissions();
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
                        SPUtils.getInstance().put(Constant.SP_KEY_PERMISSION, true);
//                QPenManager.getInstance().setNeedInit(true);
                        gotoActivity(LoginActivity.class, true);
                    } else {
//                        SPUtils.getInstance().put(Constant.SP_KEY_PERMISSION, true);
//                QPenManager.getInstance().setNeedInit(true);
                        gotoActivity(LoginActivity.class, true);
                    }
                }
            });
        } else {
//            SmartPenApp.getApp().appInit();
        }
    }
}
