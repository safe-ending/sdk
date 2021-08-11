package com.myscript.iink.eningqu;

import android.os.Environment;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/4/9 11:01
 */

public class SDCardHelper {

    private final static String TAG = SDCardHelper.class.getSimpleName();

    private SDCardHelper() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 外部存储是否可用
     * @return
     */
    public static boolean isSDCardEnable(){
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable();
    }

    /***
     * 获取文件根路径
     */
    public static String getRootPath(){
        if(!isSDCardEnable()){
            LogUtils.eTag(TAG, "-----------外部存储不可用");
        }
        return Utils.getApp().getExternalFilesDir(null).getAbsolutePath();
    }

    /**
     * 获取外部存储卡的根目录
     * @return
     */
    public static String getSDCardPath(){
        if(!isSDCardEnable()){
            LogUtils.eTag(TAG, "-----------外部存储不可用");
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath();//获取根目录
    }
}
