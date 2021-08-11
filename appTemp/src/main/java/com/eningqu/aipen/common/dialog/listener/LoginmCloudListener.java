package com.eningqu.aipen.common.dialog.listener;

import android.view.View;

public interface LoginmCloudListener {

    void getVeriCode(String mobile, View view);
    void login(String mobile, String veriCode, View view);
    void OnDismissListener();
}
