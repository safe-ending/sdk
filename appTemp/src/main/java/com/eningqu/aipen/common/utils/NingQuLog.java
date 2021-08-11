package com.eningqu.aipen.common.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * author : Sivan
 * e-mail : hsy@eningqu.com
 * date   : 2020/5/9
 * desc   : 统一日志管理
 * version: 1.0
 */
public class NingQuLog {

    private static boolean IS_SHOW_LOG = true;

    private static final String DEFAULT_MESSAGE = "execute";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final int JSON_INDENT = 4;

    private static final String TAG = "NingQu";
    private static String CACHE_DIR = SDCardHelper.getRootPath() + "/log/";// 缓存文件夹
    private static final int V = 0x1;
    private static final int D = 0x2;
    private static final int I = 0x3;
    private static final int W = 0x4;
    private static final int E = 0x5;
    private static final int A = 0x6;
    private static final int JSON = 0x7;

    public static void init(boolean isShowLog) {
        IS_SHOW_LOG = isShowLog;
        setDir();
    }

    public static void setLogSwitch(boolean debug){
        IS_SHOW_LOG = debug;
    }

    private static void setDir(){
        CACHE_DIR = SDCardHelper.getRootPath() + "/log/";
    }
    public static String getDir(){
        return CACHE_DIR;
    }

    public static boolean isDebug(){
//        LogUtils.Config config = LogUtils.getConfig();
//        setDir();
//        if(IS_SHOW_LOG){
//            config.setLog2FileSwitch(true);
//        }else {
//            config.setLog2FileSwitch(false);
//        }
        return IS_SHOW_LOG;
    }

    public static void v() {
        printLog(V, null, DEFAULT_MESSAGE);
    }

    public static void v(Object msg) {
        printLog(V, TAG, msg);
    }

    public static void v(String tag, String msg) {
        printLog(V, tag, msg);
    }

    public static void debug() {
        printLog(D, null, DEFAULT_MESSAGE);
    }

    public static void debug(Object msg) {
        printLog(D, TAG, msg);
    }

    public static void debug(String tag, Object msg) {
        printLog(D, tag, msg);
    }

    public static void info() {
        printLog(I, null, DEFAULT_MESSAGE);
    }

    public static void info(Object msg) {
        printLog(I, TAG, msg);
    }

    public static void info(String tag, Object msg) {
        printLog(I, tag, msg);
    }

    public static void warn() {
        printLog(W, null, DEFAULT_MESSAGE);
    }

    public static void warn(Object msg) {
        printLog(W, TAG, msg);
    }

    public static void warn(String tag, Object msg) {
        printLog(W, tag, msg);
    }

    public static void error() {
        printLog(E, null, DEFAULT_MESSAGE);
    }

    public static void error(Object msg) {
        printLog(E, TAG, msg);
    }

    public static void error(String tag, Object msg) {
        printLog(E, tag, msg);
    }

    public static void a() {
        printLog(A, null, DEFAULT_MESSAGE);
    }

    public static void a(Object msg) {
        printLog(A, TAG, msg);
    }

    public static void a(String tag, Object msg) {
        printLog(A, tag, msg);
    }

    public static void json(String jsonFormat) {
        printLog(JSON, TAG, jsonFormat);
    }

    public static void json(String tag, String jsonFormat) {
        printLog(JSON, tag, jsonFormat);
    }

    private static void printLog(int type, String tagStr, Object objectMsg) {
        String msg;
        if (!IS_SHOW_LOG) {
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

//    /**
//     * 清空*天前的日志
//     * @param daysBefore 比如:-5 ，5天前的日志
//     */
//    public static void cleanLogFile(int daysBefore){
//        List<File> fileList = FileUtils.listFilesInDir(SDCardHelper.getRootPath() + "/log/");
//        if(null!=fileList && fileList.size()>0){
//            Date date = TimeUtil.getSomeDay(new Date(), daysBefore);
//            long delTime = date.getTime();
//
//            for(File file:fileList){
//                long lastModified = file.lastModified();
//                if(lastModified<delTime){
//                    file.delete();
//                }
//            }
//        }
//    }
}
