package com.nq.edusaas.hps.utils;

import android.os.Environment;

import com.nq.edusaas.hps.PenSdkCtrl;
import com.nq.edusaas.hps.model.enummodel.PAGE_OPEN_STATUS;
import com.nq.edusaas.hps.sdkcore.afpensdk.Const;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * author : Sivan
 * e-mail : hsy@eningqu.com
 * date   : 2020/7/23
 * desc   :
 * version: 1.0
 */
public class Common {

    /**
     * 用户声网ID，目前是demo，学生对应id为100001，老师对应id为100002
     */
    private static String mUserId;
    private static int mRoleType;

    /**
     * 开始绘图/播放时创建的时间
     */
    private static long mCreateTime = 0;
    /**
     * 绘图/播放开始时间
     */
    private static long mStartTime = 0;
    /**
     * 绘图/播放暂停了多少时间
     */
    private static long mPauseTime = 0;

    private static String qCode = "";


    public static final String NQ_SAVE_ROOT_PATH = PenSDCardHelper.getRootPath();
    public static final String FILE_SEPARATOR = File.separator;

    public static final String SUFFIX_NAME_JPG = ".jpg";
    public static final String SUFFIX_NAME_PCM = ".pcm";
    public static final String SUFFIX_NAME_TXT = ".txt";
    public static final String SUFFIX_NAME_PDF = ".pdf";
    public static final String SUFFIX_NAME_MP4 = ".mp4";
    public static final String SUFFIX_NAME_WAV = ".wav";
    /**
     * 画板长宽
     */
    private static int mDrawBroadHeight;
    private static int mDrawBroadWidth;

    /**
     * 当前书写的ID
     */
    private static String mCurrentNotebookId;
    /**
     * 绘图界面打开状态0关闭 1正在打开 2已打开
     */
    private static volatile PAGE_OPEN_STATUS mDrawOpenState = PAGE_OPEN_STATUS.CLOSE;


    public static int getRoleType() {
        return mRoleType;
    }

    public static void setRoleType(int mRoleType) {
        Common.mRoleType = mRoleType;
    }

    /**
     * 获取当前笔记本ID
     */
    public static String getCurrentNotebookId() {
        return mCurrentNotebookId;
    }

    /**
     * 缓存当前笔记本ID
     */
    public static void setCurrentNotebookId(String currentNotebookId) {
        mCurrentNotebookId = currentNotebookId;
    }

    /**
     * 是否打开绘图界面
     */
    public static PAGE_OPEN_STATUS getDrawOpenState() {
        return mDrawOpenState;
    }

    public static void setDrawOpenState(PAGE_OPEN_STATUS state) {
        mDrawOpenState = state;
    }

    public static void setDrawBroadWidthHeight(int w, int h) {
        mDrawBroadWidth = w;
        mDrawBroadHeight = h;
    }

    public static int getDrawBroadWidth() {
        return mDrawBroadWidth;
    }

    public static int getDrawBroadHeight() {
        return mDrawBroadHeight;
    }


    public static String getUserId() {
        return mUserId;
    }

    public static void setUserId(String userId) {
        mUserId = userId;
    }


    public static long getCreateTime() {
        return mCreateTime;
    }

    public static void setCreateTime(long mCreateTime) {
        Common.mCreateTime = mCreateTime;
    }

    public static String getqCode() {
        return qCode;
    }

    public static void setqCode(String qCode) {
        Common.qCode = qCode;
    }

    public static long getStartTime() {
        return mStartTime;
    }

    public static void setStartTime(long startTime) {
        Common.mStartTime = startTime;
    }

    public static long getPauseTime() {
        return mPauseTime;
    }

    public static void setPauseTime(long mPauseTime) {
        Common.mPauseTime = mPauseTime;
    }


    /**
     * 一个用户uid对应一个文件夹，uid文件夹下保存多本书的文件夹
     *
     * @param userId
     * @param notebookId
     * @param fileType
     * @return
     */
    public static String getFileSavePath(String userId, String notebookId, String fileType) {
        String currentSavePath = userId + FILE_SEPARATOR + notebookId;
        if (SUFFIX_NAME_JPG.equals(fileType)) {
            return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentSavePath + FILE_SEPARATOR + "_jpg";
        } else if (SUFFIX_NAME_PCM.equals(fileType)) {
            return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentSavePath + FILE_SEPARATOR + "_audio";
        } else if (SUFFIX_NAME_PDF.equals(fileType)) {
            return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentSavePath + FILE_SEPARATOR + "_pdf";
        } else if (SUFFIX_NAME_MP4.equals(fileType)) {
            return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentSavePath + FILE_SEPARATOR + "_video";
        } else {
            return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentSavePath + FILE_SEPARATOR + "_txt";
        }
    }


    public static String getPDFLongSharePath(String name) {
        String storeLocation = Environment.getExternalStorageDirectory().getAbsolutePath();
        return storeLocation + FILE_SEPARATOR + "aipen" + FILE_SEPARATOR + "_pdf" + FILE_SEPARATOR + name + SUFFIX_NAME_PDF;
    }


    public static String getStrokesPath(String noteBookId, int page) {
        return getFileSavePath(mUserId, noteBookId, SUFFIX_NAME_TXT) +
                FILE_SEPARATOR + page + SUFFIX_NAME_TXT;

    }

    public static String getTotalFileSavePath() {
        String currentSavePath = getUserId() + FILE_SEPARATOR + getCreateTime();
        return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentSavePath;
    }

    public static String getFileSavePath() {
        return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + getUserId() + FILE_SEPARATOR + "question" + FILE_SEPARATOR + getqCode();
    }

    public static String getQcodeSavePath(String qCode) {
        return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + getUserId() + FILE_SEPARATOR + "question" + FILE_SEPARATOR + qCode;
    }

    public static String getAudioPathDir() {
        return getFileSavePath();
    }

    public static String getAudioPath() {
        return getFileSavePath() + FILE_SEPARATOR + "answer_audio" + SUFFIX_NAME_PCM;
    }
    public static String getPathPath() {
        return getFileSavePath() + FILE_SEPARATOR + "answer_stroke.txt";
    }

    public static String getZipPath() {
        String fileName = "answer.zip";
        return getFileSavePath() + FILE_SEPARATOR + fileName;
    }

    //用历史记录再传的时候
    public static String getPathPathDirByQcode(String qCode) {
        return getQcodeSavePath(qCode) + FILE_SEPARATOR + "answer_stroke.txt";
    }

    public static String getAudioPathByQcode(String qCode) {
        return getQcodeSavePath(qCode) + FILE_SEPARATOR + "answer_audio" + SUFFIX_NAME_PCM;
    }

    public static String getZipPathByQcode(String qCode) {
        String fileName = "answer.zip";
        return getQcodeSavePath(qCode) + FILE_SEPARATOR + fileName;
    }

    public static String getDownloadPath(String key) {
        return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + getUserId() + FILE_SEPARATOR + key + ".zip";
    }

    public static String getUnZipPath(String key) {
        return NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + getUserId() + FILE_SEPARATOR + key;
    }

    public static String getUnZipAudioPath(String key, String fileName) {
        return getUnZipPath(key) + FILE_SEPARATOR + fileName + SUFFIX_NAME_WAV;//SUFFIX_NAME_PCM;//
    }

    public static String getUnZipStrokesPath(String key, String fileName) {
        return getUnZipPath(key) + FILE_SEPARATOR + fileName + SUFFIX_NAME_TXT;
    }


}
