package com.nq.edusaas.hps.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.blankj.utilcode.util.Utils;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/4/9 11:01
 */

public class PenSDCardHelper {

    private final static String TAG = PenSDCardHelper.class.getSimpleName();

    private PenSDCardHelper() {
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
            PenNQLog.debug(TAG, "-----------外部存储不可用");
        }
        return Utils.getApp().getExternalFilesDir(null).getAbsolutePath();
    }

    /**
     * 获取外部存储卡的根目录
     * @return
     */
    public static String getSDCardPath(){
        if(!isSDCardEnable()){
            PenNQLog.debug(TAG, "-----------外部存储不可用");
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath();//获取根目录
    }

    // uri转绝对路径
    public static String getFilePathFromContentUri(Uri selectedVideoUri, ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
//      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
}
