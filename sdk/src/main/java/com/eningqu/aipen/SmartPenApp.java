//package com.eningqu.aipen;
//
//import android.app.Application;
//import android.os.Build;
//import android.view.Gravity;
//
//import com.blankj.utilcode.util.FileUtils;
//import com.blankj.utilcode.util.TimeUtils;
//import com.eningqu.aipen.common.ToastUtils;
//import com.blankj.utilcode.util.Utils;
//import com.eningqu.aipen.base.ActivityStackManager;
//import com.eningqu.aipen.common.AppCommon;
//import com.eningqu.aipen.common.Constant;
//import com.eningqu.aipen.common.SimpleActivityLifecycle;
//import com.eningqu.aipen.common.bluetooth.BluetoothClient;
//import com.eningqu.aipen.common.network.RetrofitHelper;
//import com.eningqu.aipen.common.thread.ThreadPoolUtils;
//import com.eningqu.aipen.common.utils.CrashHandler;
//import com.eningqu.aipen.common.utils.HttpUtils;
//import com.eningqu.aipen.common.utils.L;
//import com.eningqu.aipen.common.utils.SoundPlayUtils;
//import com.eningqu.aipen.common.utils.SpUtils;
//import com.eningqu.aipen.common.utils.SystemUtil;
//import com.eningqu.aipen.db.model.NoteBookData;
//import com.eningqu.aipen.db.model.NoteBookData_Table;
//import com.eningqu.aipen.db.model.PageData;
//import com.eningqu.aipen.db.model.PageData_Table;
//import com.eningqu.aipen.db.model.UserInfoData;
//import com.eningqu.aipen.qpen.QPenManager;
//import com.eningqu.aipen.myscript.RecognizeCommon;
//import com.eningqu.aipen.p20.StudentAppManager;
//import com.mob.MobSDK;
//import com.myscript.iink.eningqu.IInkSdkManager;
//import com.raizlabs.android.dbflow.config.FlowManager;
//import com.raizlabs.android.dbflow.sql.language.SQLite;
//import com.tencent.bugly.crashreport.CrashReport;
//
//import java.io.File;
//import java.util.Date;
//
//import static com.eningqu.aipen.common.AppCommon.NQ_SAVE_ROOT_PATH;
//
///**
// * 说明：整个应用的入口，一些你希望在应用一跑起来就立即完成的工作（比如初始化一些三方库，包括 SDK），可以写入它的 onCreate() 方法
// * 切记不要用 instance = new MyApp() 一类的赋值去获取实例，这样你得到的只是一个普通的 Java 类，不会具备任何 Application 的功能 ！！！
// * <p>
// * 作者：Yanghuangping
// * 邮箱：yhp@eningqu.com
// * 时间：2018/1/4 16:07
// */
//
//public class SmartPenApp extends Application {
//    //    private List<Activity> activityList = new LinkedList<>();
//    public static boolean isFirst = true;
//    public static boolean forceExit = false;
//    private SimpleActivityLifecycle lifecycle;
//    public static boolean isSync = false;
//    private boolean isInit = false;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        app = this;
//        //工具初始化
//        Utils.init(this);
////        appInit();
//    }
//
//    public void appInit() {
//        if(isInit){
//            return;
//        }
//        Utils.init(this);
//        isInit = true;
//        CrashHandler handler = CrashHandler.getInstance();
//        handler.init(getApplicationContext());
//        StudentAppManager.getInstance().init(this);
//        //数据库初始化
//        FlowManager.init(this);
//        //初始化MObSDK
//        MobSDK.init(this);
//        //初始化网络请求上下文
//        RetrofitHelper.init(this);
//        HttpUtils.init(this);
//        //蓝牙初始化
//        BluetoothClient.init(this);
//        //启动智能笔监听service服务
//        QPenManager.getInstance().init(this);
//        //加载用户信息
//        AppCommon.setUserInfo(AppCommon.loadUserInfo());
//        //初始化腾讯Bugly异常捕获
////        CrashReport.initCrashReport(getApplicationContext(), BuildConfig.buglyId, false);
//        //设置toast
//        ToastUtils.setBgResource(R.drawable.shape_toast_bg);
//        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
//        // 声音播放工具类初始化
//        SoundPlayUtils.init(this);
//        //版本4.0以上，可以使用ActivityLifecycle, 在Application的onCreate()当中：
//        if (Build.VERSION.SDK_INT >= 14) {
//            lifecycle = new SimpleActivityLifecycle();
//            registerActivityLifecycleCallbacks(lifecycle);
//        }
//        //清除5天前的日志
//        ThreadPoolUtils.getThreadPool().submit(new Runnable() {
//            @Override
//            public void run() {
//                L.cleanLogFile(-5);
//
//            }
//        });
//
//        ActivityStackManager.getInstance();
//        com.myscript.iink.eningqu.FileUtils.copyFileOrDir(this, "conf", com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS);
//        com.myscript.iink.eningqu.FileUtils.copyFileOrDir(this, "resources", com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS);
//        try {
//            IInkSdkManager.getInstance().init(this, new IInkSdkManager.IInkSdkInitCallback() {
//                @Override
//                public void onSuccess() {
//                    IInkSdkManager.getInstance().editorClean();
//                    String lang = SpUtils.getString(getApp(), Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);
//                    IInkSdkManager.getInstance().setLanguage(getApp(), lang);//zh_CN  en_US
//                }
//
//                @Override
//                public void onFailure() {
//                }
//            }, RecognizeCommon.getAppName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
////        //连接成功去获取授权
////        QPenManager.getInstance().toAuth();
//
////        UserInfoData userInfo = AppCommon.loadUserInfo();
////        if (null != userInfo) {
////            try {
////                ZipUtil.zip(AppCommon.NQ_SAVE_ROOT_PATH + File.separator + userInfo.userUid,
////                        AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + userInfo.userUid + ".zip");
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
//
////        try {
////            ZipUtil.zip(filePath,
////                    AppCommon.NQ_SAVE_SDCARD_PATH + File.separator + "aipendb.zip");
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//        UserInfoData userInfo = AppCommon.loadUserInfo();
//        if (null != userInfo) {
//            fileDataChange(userInfo);
//        }
//    }
//
//    private void fileDataChange(UserInfoData userInfo) {
//        File file = new File(NQ_SAVE_ROOT_PATH + File.separator + userInfo.userUid);
//        if (file.exists()) {
//            File[] files = file.listFiles();
//            for (File f : files) {
//                String name = f.getName();
//                if (name.startsWith("2020")) {//老数据改成ios一致
//                    String txtPath = f.getAbsolutePath() + File.separator + "_txt";
//                    File txtFile = new File(txtPath);
//                    File[] files1 = txtFile.listFiles();//找到txt的笔迹内容
//                    if (files1 != null)
//                        for (File f1 : files1) {//txt文件迁移
//                            String newFileName = com.eningqu.aipen.common.utils.FileUtils.changeStr2Zero(f1.getName().replace(".txt", ""));
//                            String bookTxt = f.getAbsolutePath() + File.separator + newFileName;
//                            FileUtils.createOrExistsDir(bookTxt);
//                            FileUtils.moveFile(new File(f1.getAbsolutePath()), new File(bookTxt + File.separator + f1.getName()), new FileUtils.OnReplaceListener() {
//                                @Override
//                                public boolean onReplace(File srcFile, File destFile) {
//                                    return false;
//                                }
//                            });
//                        }
//
//                    File audioFile = new File(f.getAbsolutePath() + File.separator + "_audio");
//                    File[] files2 = audioFile.listFiles();//音频pcm格式内容
//                    if (files2 != null)
//                        for (File file1 : files2) {
//                            File ff = new File(f, com.eningqu.aipen.common.utils.FileUtils.changeStr2Zero(file1.getName()));
//                            FileUtils.createOrExistsDir(ff);
//                            File[] files3 = file1.listFiles();
//                            if (files3 != null)
//                                for (File file2 : files3) {
//                                    if (file2.getName().contains(".wav")) {
//                                        FileUtils.delete(file2);
//                                    } else if (file2.getName().contains("_") && file2.getName().contains(".pcm")) {
//                                        String s = file2.getName().split("_")[1];
//                                        String replace = s.replace(".pcm", "");
//                                        s = replace.substring(0, 10) + ".pcm";
//                                        FileUtils.rename(file2.getAbsolutePath(), s);
//
//                                        FileUtils.moveFile(new File(file2.getParent() + File.separator + s),new File( ff + File.separator + s), new FileUtils.OnReplaceListener() {
//                                            @Override
//                                            public boolean onReplace(File srcFile, File destFile) {
//                                                return false;
//                                            }
//
//
//                                        });
//                                    }
//                                }
//                        }
//
//                    FileUtils.delete(f + File.separator + "_audio");
//                    FileUtils.delete(f + File.separator + "_jpg");
//                    FileUtils.delete(f + File.separator + "_pdf");
//                    FileUtils.delete(f + File.separator + "_txt");
//                    FileUtils.delete(f + File.separator + "_hwr");
//
//                    Date date = TimeUtils.string2Date(name, AppCommon.DATE_FORMAT);
//                    long longTime = TimeUtils.date2Millis(date) / 1000;
//                    boolean rename = FileUtils.rename(f, longTime + "");
//
//                    SQLite.update(PageData.class).set(PageData_Table.noteBookId.eq(longTime + ""))
//                            .where(PageData_Table.noteBookId.eq(name)).query();
//                    SQLite.update(NoteBookData.class).set(NoteBookData_Table.notebookId.eq(longTime + ""))
//                            .where(NoteBookData_Table.notebookId.eq(name)).query();
//                }
//            }
//        }
//    }
//
//    private static SmartPenApp app;
//
//    public static SmartPenApp getApp() {
//        return app;
//    }
//
//    /**
//     * 是否处于前台
//     *
//     * @return
//     */
//    public boolean isForeground() {
//        if (Build.VERSION.SDK_INT >= 14) {
//            return lifecycle.isForeground();
//        } else {
//            return SystemUtil.isForeground(this);
//        }
//    }
//
//    /*public void exit() {
//        for (Activity activity : activityList) {
//            if (activity != null)
//                activity.finish();
//        }
//    }
//
//    public void addActivity(Activity activity) {
//        activityList.add(activity);
//    }*/
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        L.error("low memory !!!!!");
//    }
//}
