package com.eningqu.aipen.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.baidu.speech.utils.LogUtil;
import com.blankj.utilcode.util.ActivityUtils;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wtj
 * @filename CrashHandler
 * @date 2019/9/25
 * @email wtj@eningqu.com
 **/
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    // CrashHandler 实例
    private static CrashHandler INSTANCE = new CrashHandler();

    // 程序的 Context 对象
    private Context mContext;

    // 系统默认的 UncaughtException 处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    /**
     * 保证只有一个 CrashHandler 实例
     */
    private CrashHandler() {
    }

    /**
     * 获取 CrashHandler 实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的 UncaughtException 处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该 CrashHandler 为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当 UncaughtException 发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            LogUtil.i("wepa mDefaultHandler beg");
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                LogUtil.e("error : ", e);
            }
            LogUtil.i("wepa killProcess beg");
            // 退出程序
            // 清除栈
            //            List<Activity> openedActivity = ((WepaApplication) mContext)
            //                    .getOpenedActivity();
            //            for (Activity activity : openedActivity) {
            //                if (activity != null) {
            //                    LogUtil.d("activity getComponentName : "
            //                            + activity.getComponentName());
            //                    activity.finish();
            //                }
            //            }
            ActivityUtils.finishAllActivities();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);

            /*
             * // 重新启动程序，注释上面的退出程序 Intent intent = new Intent();
             * intent.setClass(mContext, MainPageActivity.class);
             * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             * mContext.startActivity(intent);
             * android.os.Process.killProcess(android.os.Process.myPid());
             */
        }
    }

    /**
     * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
     *
     * @param ex
     * @return true：如果处理了该异常信息；否则返回 false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        LogUtil.i("wepa handleException beg");
//        new Thread() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                Toast.makeText(mContext, "很抱歉，程序遇到异常，即将退出", Toast.LENGTH_SHORT)
//                        .show();
//                Looper.loop();
//            }
//        }.start();
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        L.error("crash info : " + new Gson().toJson(infos));

        L.error(ex.getMessage());
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] trace = ex.getStackTrace();
        for (StackTraceElement traceElement : trace)
            sb.append("\nat ").append(traceElement);

        for (Throwable se : ex.getSuppressed()) {
            StackTraceElement[] trace1 = se.getStackTrace();
            for (StackTraceElement traceElement : trace1)
                sb.append("\nat ").append(traceElement);
        }

        Throwable cause = ex.getCause();
        if (cause != null) {
            StackTraceElement[] trace1 = cause.getStackTrace();
            for (StackTraceElement traceElement : trace1)
                sb.append("\nat ").append(traceElement);
        }
        L.error(sb.toString());
        ActivityUtils.finishAllActivities();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
//        mContext.startActivity(new Intent(mContext, WelcomeActivity.class));

        return false;//需要把错误提供给第三方sdk上报，所以必须返回false
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);

            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e("an error occured when collect package info", e);
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                LogUtil.d(field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                LogUtil.e("an error occured when collect crash info", e);
            }
        }
    }

}
