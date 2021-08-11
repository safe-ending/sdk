package com.nq.edusaas.hps.utils;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

/**
 * 说明：定位权限工具类
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/27 14:41
 */

public class PenLocationUtils {
    private PenLocationUtils() {
        throw new UnsupportedOperationException("can't instantiate ...");
    }


    /**
     * 判断定位是否可用
     */
    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (networkProvider || gpsProvider) return true;
        return false;
    }

    /**
     * 打开定位设置界面
     */
    public static void openLocationSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}