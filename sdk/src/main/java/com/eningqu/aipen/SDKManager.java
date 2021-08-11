package com.eningqu.aipen;

import android.app.Application;
import android.util.Log;
import android.view.Gravity;

import com.blankj.utilcode.util.Utils;
import com.eningqu.aipen.base.ActivityStackManager;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.common.bluetooth.BluetoothClient;
import com.eningqu.aipen.common.network.RetrofitHelper;
import com.eningqu.aipen.common.thread.ThreadPoolUtils;
import com.eningqu.aipen.common.utils.CrashHandler;
import com.eningqu.aipen.common.utils.HttpUtils;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.SoundPlayUtils;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.db.model.UserInfoData;
import com.eningqu.aipen.myscript.RecognizeCommon;
import com.eningqu.aipen.p20.StudentAppManager;
import com.eningqu.aipen.qpen.QPenManager;
import com.mob.MobSDK;
import com.myscript.iink.eningqu.IInkSdkManager;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;

import cn.sharesdk.framework.Platform;

/**
 * 说明：初始化一些三方库，包括 SDK，
 * 作者：yt
 * 邮箱：yt@eningqu.com
 * 时间：2021/8/10 16:07
 */

public class SDKManager {
    private static SDKManager instance;
    private boolean isInit;
    private static Application sdkApplication;
    private String pkgName;

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public static SDKManager getInstance() {
        if (null == instance) {
            synchronized (SDKManager.class) {
                if (null == instance) {
                    instance = new SDKManager();
                }
            }
        }
        return instance;
    }

    public Application getApplication() {
        return sdkApplication;
    }


    public void init(Application application,String packageName,String name) {

        if(isInit){
            Log.i("caiyunsdk","sdkmanager already init!");
            return;
        }
        sdkApplication = application;

        this.pkgName = packageName;
        AppCommon.APP_NAME = name;
        appInit(application);


    }


    private void appInit(Application application) {
        isInit = true;
        //工具初始化
        Utils.init(application);
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(application);
        StudentAppManager.getInstance().init(application);
        //数据库初始化
        FlowManager.init(application);
        //初始化MObSDK
        MobSDK.init(application);
        //初始化网络请求上下文
        RetrofitHelper.init(application);
        HttpUtils.init(application);
        //蓝牙初始化
        BluetoothClient.init(application);
        //启动智能笔监听service服务
        QPenManager.getInstance().init(application);
        //加载用户信息
        AppCommon.setUserInfo(AppCommon.loadUserInfo());
        //初始化腾讯Bugly异常捕获
//        CrashReport.initCrashReport(getApplicationContext(), BuildConfig.buglyId, false);
        //设置toast
        ToastUtils.setBgResource(R.drawable.shape_toast_bg);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        // 声音播放工具类初始化
        SoundPlayUtils.init(application);
        //清除5天前的日志
        ThreadPoolUtils.getThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                L.cleanLogFile(-5);

            }
        });

        ActivityStackManager.getInstance();
        com.myscript.iink.eningqu.FileUtils.copyFileOrDir(application, "conf", com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS);
        com.myscript.iink.eningqu.FileUtils.copyFileOrDir(application, "resources", com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS);
        try {
            IInkSdkManager.getInstance().init(application, new IInkSdkManager.IInkSdkInitCallback() {
                @Override
                public void onSuccess() {
                    IInkSdkManager.getInstance().editorClean();
                    String lang = SpUtils.getString(application, Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);
                    IInkSdkManager.getInstance().setLanguage(application, lang);//zh_CN  en_US
                }

                @Override
                public void onFailure() {
                }
            }, RecognizeCommon.getAppName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initUserInfo(null);

    }

    private void initUserInfo(Platform platform) {
        if (platform == null) {
            //获取用户资料
            String userId = "123";//获取用户账号
            String userName = getApplication().getString(R.string.tourist);//获取用户名字
//            String userIcon = "http://img2.imgtn.bdimg.com/it/u=1813493607,361824557&fm=26&gp=0.jpg";//获取用户头像
            String userGender = "m"; //获取用户性别，m = 男, f = 女，如果微信没有设置性别,默认返回null
            userGender = getApplication().getResources().getString(R.string.man);

            //先删除用户
            Delete.tables(UserInfoData.class);

            UserInfoData userInfoData = new UserInfoData();
            userInfoData.id = 1L;
            userInfoData.userUid = userId;
            userInfoData.userName = userName;
            userInfoData.userIcon = "";
            userInfoData.userSex = userGender;
            userInfoData.save();
            SpUtils.putString(getApplication(), SpUtils.LOGIN_INFO, userInfoData.toString());
            AppCommon.setUserInfo(userInfoData);
        } else {
            //获取用户资料
            String userId = platform.getDb().getUserId();//获取用户账号
            String userName = platform.getDb().getUserName();//获取用户名字
            String userIcon = platform.getDb().getUserIcon();//获取用户头像
            String userGender = platform.getDb().getUserGender(); //获取用户性别，m = 男, f = 女，如果微信没有设置性别,默认返回null
            userGender = (userGender == null ? "" : userGender).equals("m") ? getApplication().getResources().getString(R.string.man) : getApplication().getResources().getString(R.string.woman);

            //先删除用户
            Delete.tables(UserInfoData.class);

            UserInfoData userInfoData = new UserInfoData();
            userInfoData.id = 1L;
            userInfoData.userUid = userId;
            userInfoData.userName = userName;
            userInfoData.userIcon = userIcon;
            userInfoData.userSex = userGender;
            userInfoData.save();
            SpUtils.putString(getApplication(), SpUtils.LOGIN_INFO, userInfoData.toString());
            AppCommon.setUserInfo(userInfoData);
        }
    }
}
