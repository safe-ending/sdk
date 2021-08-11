package com.eningqu.aipen.common.utils;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

/**
 * 说明：定位权限工具类
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/27 14:41
 */

public class LocationUtils {
    private LocationUtils() {
        throw new UnsupportedOperationException("can't instantiate ...");
    }


    /**
     * 判断定位是否可用
     */
    public static boolean isLocationEnabled(Context context) {
        Log.e("caiyunsdk",(context != null) ? "context not null" : "context is null" +"");
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
