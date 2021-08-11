package com.eningqu.aipen.common.utils;

import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.eningqu.aipen.common.AppCommon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
* @Author: Qiu.Li
* @Create Date: 2021/7/8 9:58
* @Description: 日志管理
* @Email: liqiupost@163.com
*/
public class L {

    private L() {}

    public static boolean isDebug = true;//TODO  是否需要打印lug，正式版本需要为false

    private static final String DEFAULT_MESSAGE = "execute";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final int JSON_INDENT = 4;
    private static String CACHE_DIR = AppCommon.NQ_SAVE_ROOT_PATH_LOG_DIR;// 缓存文件夹

    private static final String TAG = L.class.getSimpleName();
    private static final int V = 0x1;
    private static final int D = 0x2;
    private static final int I = 0x3;
    private static final int W = 0x4;
    private static final int E = 0x5;
    private static final int A = 0x6;
    private static final int JSON = 0x7;

    public static void init(boolean debug) {
        isDebug = debug;
    }

    public static void setLogSwitch(boolean debug){
        isDebug = debug;
    }

    public static void setDir(String dir){
        CACHE_DIR = dir;
    }

    public static String getDir(){
        return CACHE_DIR;
    }

    // 下面四个是默认tag的函数
    public static void info(String msg) {
        printLog(I, TAG, msg);
    }

    public static void debug(String msg) {
        printLog(D, TAG, msg);
    }

    public static void error(String msg) {
        printLog(E, TAG, msg);
    }

    public static void warn(String msg){
        printLog(W, TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void info(String tag, String msg) {
        printLog(I, tag, msg);
    }

    public static void debug(String tag, String msg) {
        printLog(D, tag, msg);
    }

    public static void error(String tag, String msg) {
        printLog(E, tag, msg);
    }

    public static void warn(String tag, String msg) {
        printLog(W, tag, msg);
    }

    private static void printLog(int type, String tagStr, Object objectMsg) {
        String msg;
        if (!isDebug) {
            return;
        }

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        int index = 4;
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        String tag = "NingQu";//(tagStr == null ? className : tagStr);
        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#").append(methodName).append(" ] ");

        if (objectMsg == null) {
            msg = TAG;//"Log with null Object";
        } else {
            msg = objectMsg.toString();
        }
        if (msg != null && type != JSON) {
            stringBuilder.append(msg);
        }

        String logStr = stringBuilder.toString();
        LogUtils.file(logStr);

        switch (type) {
            case V:
                Log.v(tag, logStr);
                break;
            case D:
                Log.d(tag, logStr);
                break;
            case I:
                Log.i(tag, logStr);
                break;
            case W:
                Log.w(tag, logStr);
                break;
            case E:
                Log.e(tag, logStr);
                break;
            case A:
                Log.wtf(tag, logStr);
                break;
            case JSON: {

                if (TextUtils.isEmpty(msg)) {
                    Log.d(tag, "Empty or Null json content");
                    return;
                }

                String message = null;

                try {
                    if (msg.startsWith("{")) {
                        JSONObject jsonObject = new JSONObject(msg);
                        message = jsonObject.toString(JSON_INDENT);
                    } else if (msg.startsWith("[")) {
                        JSONArray jsonArray = new JSONArray(msg);
                        message = jsonArray.toString(JSON_INDENT);
                    }
                } catch (JSONException e) {
                    error(tag, e.getCause().getMessage() + "\n" + msg);
                    return;
                }

                printLine(tag, true);
                message = logStr + LINE_SEPARATOR + message;
                String[] lines = message.split(LINE_SEPARATOR);
                StringBuilder jsonContent = new StringBuilder();
                for (String line : lines) {
                    jsonContent.append("║ ").append(line).append(LINE_SEPARATOR);
                }
                //Log.i(tag, jsonContent.toString());

                if (jsonContent.toString().length() > 3200) {
                    Log.w(tag, "jsonContent.length = " + jsonContent.toString().length());
                    int chunkCount = jsonContent.toString().length() / 3200;
                    for (int i = 0; i <= chunkCount; i++) {
                        int max = 3200 * (i + 1);
                        if (max >= jsonContent.toString().length()) {

                            Log.w(tag, jsonContent.toString().substring(3200 * i));

                        } else {

                            Log.w(tag, jsonContent.toString().substring(3200 * i, max));

                        }

                    }

                } else {
                    Log.w(tag, jsonContent.toString());

                }
                printLine(tag, false);
            }
            break;
        }

    }

    private static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.w(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.w(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }

    /**
     * 清空*天前的日志
     * @param daysBefore 比如:-5 ，5天前的日志
     */
    public static void cleanLogFile(int daysBefore){
        List<File> fileList = FileUtils.listFilesInDir(AppCommon.NQ_SAVE_ROOT_PATH_LOG_DIR);
        if(null!=fileList && fileList.size()>0){
            Date date = TimeUtil.getSomeDay(new Date(), daysBefore);
            long delTime = date.getTime();

            for(File file:fileList){
                long lastModified = file.lastModified();
                if(lastModified<delTime){
                    file.delete();
                }
            }
        }
    }
}
