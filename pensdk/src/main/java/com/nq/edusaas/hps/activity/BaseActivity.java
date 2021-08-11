package com.nq.edusaas.hps.activity;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.blankj.utilcode.util.ToastUtils;
import com.nq.edusaas.hps.activity.base.StatusBarHelper;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/1/8 15:41
 * desc   :
 * version: 1.0
 */
public abstract class BaseActivity extends AppCompatActivity {

    // 获得授权
    public static final int PERMISSION_GRANTED = 0;
    // 未获授权
    public static final int PERMISSION_DENIED = -1;

    private final int PERMISSION_REQUEST_CODE = 2;

    private int refusedPermissionIndex = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarHelper.setStatusBarColor(this, Color.TRANSPARENT);
    }

    protected abstract void doNext();

    protected void baseCheckPermission(@NonNull Context context, @NonNull String[] permissions) {
        if (hasPermission(this, permissions)) {
            // 执行拍照的逻辑
            doNext();
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (hasPermission(this, permissions)) {
            // 执行拍照的逻辑
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[refusedPermissionIndex])) {
                // 向用户展示申请权限的理由
            } else {
                // 引用用户去开启权限
            }
        }
    }

    private boolean hasPermission(@NonNull Context context, @NonNull String[] permissions) {
        refusedPermissionIndex = 0;
        for(String permission:permissions){
            if (ActivityCompat.checkSelfPermission(context, permission) != PermissionChecker.PERMISSION_GRANTED
                    || PermissionChecker.checkSelfPermission(context, permission) != PermissionChecker.PERMISSION_GRANTED) {
                return false;
            }
            refusedPermissionIndex++;
        }

        return true;
    }

    protected void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(message);
            }
        });
    }

    protected void showToast(final int message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(getResources().getString(message));
            }
        });
    }
}
