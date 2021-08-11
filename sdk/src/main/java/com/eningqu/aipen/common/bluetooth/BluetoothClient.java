package com.eningqu.aipen.common.bluetooth;

import android.content.Context;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/24 19:26
 */

public class BluetoothClient {

    private static BlueToothLeClass myBle;

    public static void init(Context context){
        if(myBle == null){
            synchronized (BluetoothClient.class) {
                if(myBle == null){
                    myBle = new BlueToothLeClass(context);
                }
            }
        }
    }

    public static BlueToothLeClass getBle(){
        return myBle;
    }

}
