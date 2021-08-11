package com.eningqu.aipen.fragment;

import android.content.Context;
import androidx.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.eningqu.aipen.R;
import com.eningqu.aipen.base.ui.BaseFragment;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.utils.ZXingUtils;
import com.eningqu.aipen.databinding.FragmentServiceBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FragmentServer extends BaseFragment {

    private FragmentServiceBinding mBinding;

    @Override
    protected int setLayout() {
        return R.layout.fragment_service;
    }

    @Override
    protected void dataBindingLayout(ViewDataBinding viewDataBinding) {
        mBinding = (FragmentServiceBinding) viewDataBinding;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        String ip = getlocalip();

        Bitmap appQRCode = ZXingUtils.createQRImage("QPen_" + ip, 200, 200);
        mBinding.ivQrCode.setImageBitmap(appQRCode);
    }

    /**
     * 或取本机的ip地址
     */
    private String getlocalip() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        //  Log.d(Tag, "int ip "+ipAddress);
        if (ipAddress == 0) return null;
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

    // 普通事件的处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier carrier) {
    }
}
