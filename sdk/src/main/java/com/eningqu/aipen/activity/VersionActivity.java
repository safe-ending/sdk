package com.eningqu.aipen.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.BuildConfig;
import com.eningqu.aipen.R;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.NetworkUtil;
import com.eningqu.aipen.databinding.ActivityVersionBinding;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Qiu.Li
 * @Create Date: 2019/5/22 15:22
 * @Description: 版本信息
 * @Email: liqiupost@163.com
 */
public class VersionActivity extends BaseActivity {
    private String path = Environment.getExternalStorageDirectory() + "/Download/";
    private String filename = "qpen.apk";

    private final String TAG = VersionActivity.class.getSimpleName();

    private ActivityVersionBinding mBinding;

    private int clkCount1 = 0;
    private int clkCount2 = 0;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_version);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initEvent() {
        progressDialog = new ProgressDialog(this);
    }

    public void onViewClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.rl_version_update) {
            if (NetworkUtil.isNetWorkAvailable(this)) {
            } else {
                Toast.makeText(this, R.string.network_error_tip, Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.tv_log_switch) {
            clkCount1++;
            if (clkCount1 == 4) {
                //打开log开关
                L.setLogSwitch(!L.isDebug);
                if (L.isDebug) {
                    showToast("Log Switch On");
                } else {
                    showToast("Log Switch Off");
                }
                clkCount1 = 0;
            } else if (clkCount1 == 3) {
                ToastUtils.showShort("Click 4 times to set log switch");
            }
        } else if (id == R.id.tv_display_page) {
            clkCount2++;
            if (clkCount2 == 4) {
                gotoActivity(PageDisplayActivity.class);
                clkCount2 = 0;
            } else if (clkCount2 == 3) {
                ToastUtils.showShort("Click 4 times to check page data");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clkCount1 = 0;
        clkCount2 = 0;
    }

    private ProgressDialog progressDialog;

    private void showProgressDialog(int progress) {
        progressDialog.setMessage(getString(R.string.version_update) + "...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false); // 填false表示是明确显示进度的 填true表示不是明确显示进度的
        progressDialog.setProgress(progress);
        progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.seek_bar_bg));
        progressDialog.setMax(100);

        progressDialog.show();
    }
}
