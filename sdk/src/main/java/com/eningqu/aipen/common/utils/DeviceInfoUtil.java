package com.eningqu.aipen.common.utils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/1/8 9:39
 * desc   : 设备信息
 * version: 1.0
 */
public class DeviceInfoUtil {

    /**
     * Android 7.0之后获取Mac地址
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
     *
     * @return
     */
    public static byte[] getMacFromHardware() {
        try {
            Enumeration<NetworkInterface> all = NetworkInterface.getNetworkInterfaces();

            if (all != null) {
                while (all.hasMoreElements()) {

                    NetworkInterface nif = all.nextElement();
                    if (!nif.getName().equals("wlan0"))
                        continue;
                    byte macBytes[] = nif.getHardwareAddress();
                    if (macBytes == null)
                        return null;
//                    StringBuilder res1 = new StringBuilder();
//                    for (Byte b : macBytes) {
//                        res1.append(String.format("%02X:", b));
//                    }
//                    if (!TextUtils.isEmpty(res1)) {
//                        res1.deleteCharAt(res1.length - 1)
//                    }
                    return macBytes;
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }
}
