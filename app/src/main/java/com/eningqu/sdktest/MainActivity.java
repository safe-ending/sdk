package com.eningqu.sdktest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.FileUtils;
import com.eningqu.aipen.SDKManager;
import com.eningqu.aipen.common.HwrEngineEnum;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.p20.DotListenerService;
import com.eningqu.aipen.qpen.QPenManager;
import com.github.kongpf8848.tkpermission.MultiplePermissionsListener;
import com.github.kongpf8848.tkpermission.PermissionUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.myscript.iink.eningqu.IInkSdkManager;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Arrays;
import java.util.List;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    List<String> permissionList = Arrays.asList(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(permissionList);

            }
        });
    }

    private void startGo() {
        startActivity(new Intent(MainActivity.this, com.eningqu.aipen.activity.MainActivity.class));
    }

    /**
     * 动态权限
     */
    @SuppressLint("CheckResult")
    private void requestPermissions(List<String> ls) {
        PermissionUtils.INSTANCE.requestMultiplePermissions(MainActivity.this, ls, new MultiplePermissionsListener() {
            /**
             * 多个权限全部被允许时回调
             */
            @Override
            public void allGranted() {
                startGo();
            }

            /**
             * 被拒绝的权限列表
             */
            @Override
            public void denied(List<String> list) {
                requestPermissions(list);
                ToastUtils.showShort("请先获取权限");

            }
        });

    }
}