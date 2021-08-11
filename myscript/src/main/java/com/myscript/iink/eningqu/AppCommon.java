package com.myscript.iink.eningqu;

import android.text.TextUtils;

import java.io.File;

public class AppCommon {

    public static final String NQ_SAVE_ROOT_PATH = SDCardHelper.getRootPath();
    public static final String NQ_SAVE_SDCARD_PATH = SDCardHelper.getSDCardPath();
    public static final String SUFFIX_NAME_HWR = ".hwr";
    public static final String FILE_SEPARATOR = File.separator;

    private static String userId;

    public static String getUserUID() {
        if (TextUtils.isEmpty(userId)) {
            return "";
        }
        return userId;
    }


    public static final String NQ_SAVE_SDCARD_PATH_ROOT = SDCardHelper.getRootPath() + "/aipen";
    public static final String NQ_SAVE_SDCARD_PATH_ASSETS = SDCardHelper.getRootPath() + "/aipen" + "/assets";

    public static String getHwrFilePath(String noteBookId, int page) {
        String path = getFileSavePath(getUserUID(), noteBookId) +
                FILE_SEPARATOR + FileUtils.changeStr2Zero(page + "") +
                FILE_SEPARATOR + page + SUFFIX_NAME_HWR;
        return path;
    }

    public static String getHwrFilePath() {
        String path;
//        if (TextUtils.isEmpty(getUserUID())) {//这里暂时不要  英语周报识别完没保存
        path = NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + SUFFIX_NAME_HWR;
//        } else {
//            path = NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + getUserUID() + FILE_SEPARATOR + SUFFIX_NAME_HWR;
//        }
        return path;
    }

    /**
     * 一个用户uid对应一个文件夹，uid文件夹下保存多本书的文件夹
     *
     * @param userId
     * @param notebookId
     * @return
     */
    public static String getFileSavePath(String userId, String notebookId) {
        String currentSavePath = userId + FILE_SEPARATOR + notebookId;
        return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentSavePath;
    }
}
