package com.eningqu.aipen.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.WindowManager;

import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.BuildConfig;
import com.eningqu.aipen.R;
import com.eningqu.aipen.SmartPenApp;
import com.eningqu.aipen.base.ActivityStackManager;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.dialog.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.common.utils.HttpUtils;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.RSAKit;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.databinding.ActivityLoginBinding;
import com.eningqu.aipen.db.model.UserInfoData;
import com.eningqu.aipen.qpen.QPenManager;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.Delete;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/9 15:06
 */
public class LoginActivity extends BaseActivity implements PlatformActionListener, View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private final static int AUTHORIZE_COMPLETE = 1;
    private final static int AUTHORIZE_ERROR = 2;
    private final static int AUTHORIZE_CANCEL = 3;
    private final static int AUTHORIZE_SUCCESS = 4;
    private ActivityLoginBinding mBinding;

    private Context context;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
    }

    @Override
    protected void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //username.setHint(getResources().getString(R.string.phone_num));
        //password.setHint(getResources().getString(R.string.input_password));
        //username.setError(getResources().getString(R.string.phone_num_error));
        //password.setError(getResources().getString(R.string.phone_num_error));
        //        int[] widthAndheight = GetDeviceWidthAndHeight.getDeviceWidthAndHeight(this);
        //        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
        //                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置控件的位置
        //        params.setMargins((int) widthAndheight[0]/3, 0, 0, 0);//左上右下
        //        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        //        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        //        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        //        String local = Locale.getDefault().getLanguage();
        //        ll_qq.setLayoutParams(params);
        //        ll_weichat.setLayoutParams(params);
        //        ll_twitter.setLayoutParams(params);
        //        ll_facebook.setLayoutParams(params);
        //        if (local.equals("zh")) {
        //            layout_qq_login.setVisibility(View.VISIBLE);
        //            layout_weixin_login.setVisibility(View.VISIBLE);
        //            layout_twitter_login.setVisibility(View.GONE);
        //            layout_facebook_login.setVisibility(View.GONE);
        //        }else {
        //            layout_qq_login.setVisibility(View.GONE);
        //            layout_weixin_login.setVisibility(View.GONE);
        //            layout_twitter_login.setVisibility(View.VISIBLE);
        //            layout_facebook_login.setVisibility(View.VISIBLE);
        //        }

        mBinding.layoutQqLogin.setOnClickListener(this);
        mBinding.layoutFacebookLogin.setOnClickListener(this);
        mBinding.layoutTwitterLogin.setOnClickListener(this);
        mBinding.layoutWeixinLogin.setOnClickListener(this);
        mBinding.jump.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mBinding.jump.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Delete.tables(UserInfoData.class);
        context = this;
    }

    @Override
    protected void onResume() {
        super.onResume();

        showUserAgree();
    }

    private void showUserAgree() {
        Boolean isAgree = SpUtils.getBoolean(this, Constant.SP_KEY_USER_AGREE, false);
        if (!isAgree) {
            SpannableStringBuilder ss = new SpannableStringBuilder(getString(R.string.str_agree_content1));
            String string = getString(R.string.str_agree_sure);
            SpannableString sAgreeSure1 = new SpannableString(string);
            sAgreeSure1.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(WebViewActivity.TYPE_KEY, WebViewActivity.WEB_VIEW_TYPE_PRO);
                    gotoActivity(WebViewActivity.class, bundle);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ContextCompat.getColor(LoginActivity.this, R.color.app_click_text_green));
                    ds.setUnderlineText(false);    //去除超链接的下划线
                }

            }, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.append(sAgreeSure1);
            SpannableStringBuilder sAnd = new SpannableStringBuilder(getString(R.string.str_add));
            ss.append(sAnd);
            String string2 = getString(R.string.str_agree_sure2);
            SpannableString sAgreeSure2 = new SpannableString(string2);
            sAgreeSure2.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(WebViewActivity.TYPE_KEY, WebViewActivity.WEB_VIEW_TYPE_PRO2);
                    gotoActivity(WebViewActivity.class, bundle);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ContextCompat.getColor(LoginActivity.this, R.color.app_click_text_green));
                    ds.setUnderlineText(false);    //去除超链接的下划线
                }

            }, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.append(sAgreeSure2);


            SpannableStringBuilder ss2 = new SpannableStringBuilder(getString(R.string.str_agree_content2));
            ss.append(ss2);

            dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                @Override
                public void confirm(View view) {
                    dismissDialog();
                    SpUtils.putBoolean(LoginActivity.this, Constant.SP_KEY_USER_AGREE, true);
                }

                @Override
                public void cancel() {
                    dismissDialog();
                    SmartPenApp.forceExit = true;
                    finish();
                }
            }, R.string.user_agreement_title_all, ss, R.string.str_agree, R.string.str_unagree, false);
        }
    }

    @Override
    protected void initEvent() {
        //设置返回键点击监听
        setOnKeyListener(new OnKeyClickListener() {
            @Override
            public void clickBack() {
                SmartPenApp.forceExit = true;
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        String loginIfo = SpUtils.getString(getApplicationContext(), SpUtils.LOGIN_INFO, "");
        if (SmartPenApp.forceExit) {
            QPenManager.getInstance().unInit();
            ActivityStackManager.getInstance().exitApplication();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    /*@Override
    protected void onResume() {
        *//*if (dialog != null) {
            boolean b = dialog.isAdded();
        }
        if (dialog != null && dialog.isAdded()) {
            dialog.dismiss();
        }*//*
//        super.onResume();
    }*/

    //    @OnClick({R.id.layout_qq_login, R.id.layout_weixin_login,R.id.layout_twitter_login,R.id.layout_facebook_login})
    //    public void onLoginViewClick(View v) {
    //        LoginApi loginApi = new LoginApi();
    //        switch (v.getId()) {
    //            case R.id.layout_qq_login:
    //                Platform qq = ShareSDK.getPlatform(QQ.NAME);
    //                qq.setPlatformActionListener(LoginActivity.this);
    //                boolean qqclientValid = qq.isClientValid();
    //                if(!qqclientValid) {
    //                    ToastUtils.showShort(R.string.app_QQ);
    //                    return;
    //                } else {
    //                    qq.SSOSetting(false);
    //                    authorize(qq, R.string.authorize_qq);
    ////                    layout_facebook_login.setClickable(false);
    ////                    layout_twitter_login.setClickable(false);
    ////                    layout_qq_login.setClickable(false);
    ////                    layout_weixin_login.setClickable(false);
    //                }
    //
    //                break;
    //            case R.id.layout_weixin_login:
    //                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
    //                wechat.setPlatformActionListener(LoginActivity.this);
    //                boolean clientValid = wechat.isClientValid();
    //                if(!clientValid) {
    //                    ToastUtils.showShort(R.string.app_WeChat);
    //                    return;
    //                } else {
    //                    wechat.SSOSetting(false);
    //                    authorize(wechat, R.string.authorize_weixin);
    ////                    layout_facebook_login.setClickable(false);
    ////                    layout_twitter_login.setClickable(false);
    ////                    layout_qq_login.setClickable(false);
    ////                    layout_weixin_login.setClickable(false);
    //                }
    //                break;
    //            case R.id.layout_twitter_login:
    //                Platform twitter = ShareSDK.getPlatform(Twitter.NAME);
    //                twitter.setPlatformActionListener(LoginActivity.this);
    ////                boolean twClientValid = twitter.isClientValid();
    ////                if(!twClientValid) {
    ////                    ToastUtils.showShort(R.string.app_Twitter);
    ////                    return;
    ////                } else {
    //                    twitter.SSOSetting(false);
    //                    authorize(twitter, R.string.authorize_twitter);
    ////                layout_facebook_login.setClickable(false);
    ////                layout_twitter_login.setClickable(false);
    ////                layout_qq_login.setClickable(false);
    ////                layout_weixin_login.setClickable(false);
    ////                }
    //
    //                break;
    //            case R.id.layout_facebook_login:
    //
    //                Platform facebook = ShareSDK.getPlatform(Facebook.NAME);
    //                facebook.setPlatformActionListener(LoginActivity.this);
    //                boolean fbClientValid = facebook.isClientValid();
    //                if(!fbClientValid) {
    //                    ToastUtils.showShort(R.string.app_Facebook);
    //                    return;
    //                } else {
    //                    facebook.SSOSetting(false);
    //                    authorize(facebook, R.string.authorize_facebook);
    ////                    layout_facebook_login.setClickable(false);
    ////                    layout_twitter_login.setClickable(false);
    ////                    layout_qq_login.setClickable(false);
    ////                    layout_weixin_login.setClickable(false);
    //                }
    //                break;
    //            /*case R.id.img_SinaWeibo:
    //                Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
    //                sina.setPlatformActionListener(this);
    //                sina.SSOSetting(false);
    //                authorize(sina, 3);
    //                break;*/
    //            default:
    //                break;
    //        }
    //    }

    /*private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }*/

    //    BaseDialog mDialog;
    //授权
    private void authorize(Platform plat, int msg) {
//        dialog = DialogHelper.showProgress(getSupportFragmentManager(), msg);
        dialog = DialogHelper.showProgress(getSupportFragmentManager(), msg, false);
        ShareSDK.setActivity(this);//抖音登录适配安卓9.0
        if (plat.isAuthValid()) { //如果授权就删除授权资料
            plat.removeAccount(true);
        }
        plat.showUser(null);//授权并获取用户信息
        plat.removeAccount(true);
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
        L.error("onComplete " + hashMap.toString());
        Message msg = new Message();
        msg.obj = platform;
        msg.what = AUTHORIZE_COMPLETE;
        EventBusUtil.post(msg);
    }

    @Override
    public void onError(Platform platform, int action, Throwable throwable) {
        L.error("onError " + action);
        Message msg = new Message();
        msg.obj = platform;
        msg.what = AUTHORIZE_ERROR;
        EventBusUtil.post(msg);
        ToastUtils.showShort(R.string.authorize_fail);
    }

    @Override
    public void onCancel(Platform platform, int action) {
        //        dialog = DialogHelper.showProgress(getSupportFragmentManager(), R.string.authorize_success);
        L.error("onCancel " + action);
        Message msg = new Message();
        msg.obj = platform;
        msg.what = AUTHORIZE_CANCEL;
        EventBusUtil.post(msg);
        ToastUtils.showShort(R.string.authorize_cancel);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //    private void login(String platformName) {
    //        LoginApi api = new LoginApi();
    //        //设置登陆的平台后执行登陆的方法
    //        api.setPlatform(platformName);
    //        api.setOnLoginListener(new OnLoginListener() {
    //            public boolean onLogin(String platform, HashMap<String, Object> res) {
    //                // 在这个方法填写尝试的代码，返回true表示还不能登录，需要注册
    //                // 此处全部给回需要注册
    //                Platform platform1 = ShareSDK.getPlatform(platform);
    //                return true;
    //            }
    //
    //            public boolean onRegister(UserInfo info) {
    //                // 填写处理注册信息的代码，返回true表示数据合法，注册页面可以关闭
    //                return true;
    //            }
    //        });
    //        api.login(this);
    //    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListner(final Message message) {
        switch (message.what) {
            case AUTHORIZE_COMPLETE:
                long startTime = System.currentTimeMillis();
                Platform platform = (Platform) message.obj;
                if (platform != null) {
                    initUserInfo(platform);

                    long endTime = System.currentTimeMillis();
                    L.error(TAG, "所用时间：" + (endTime - startTime));
//                    dismissDialog();
//                    //获取后台授权
//                    oauth();
//                    dialog = DialogHelper.showProgress(getSupportFragmentManager(), authTips);

                    Message msg = new Message();
                    msg.what = AUTHORIZE_SUCCESS;
                    EventBusUtil.post(msg);
                    ToastUtils.showShort(R.string.login_success);
                } else {
                    dismissDialog();
                    ToastUtils.showShort(R.string.authorize_fail);
                }
                break;
            case AUTHORIZE_ERROR:
                dismissDialog();
//                dismissMyDialog();
                if (BuildConfig.DEBUG) {
                    //debug状态下设置预设用户便于开发测试
                    initUserInfo(null);
                    dismissDialog();
                    Intent intent = new Intent(this, MainActivity.class);
                    this.startActivity(intent);
                    finish();
                    SmartPenApp.isFirst = false;
                }
                //                layout_facebook_login.setClickable(true);
                //                layout_twitter_login.setClickable(true);
                //                layout_qq_login.setClickable(true);
                //                layout_weixin_login.setClickable(true);
                break;
            case AUTHORIZE_CANCEL:
                dismissDialog();
//                dismissMyDialog();
                //                layout_facebook_login.setClickable(true);
                //                layout_twitter_login.setClickable(true);
                //                layout_qq_login.setClickable(true);
                //                layout_weixin_login.setClickable(true);
                break;
            case AUTHORIZE_SUCCESS:
                dismissDialog();
                gotoActivity(MainActivity.class);
                SmartPenApp.isFirst = false;
                SmartPenApp.forceExit = false;
//                dismissMyDialog();
                finish();
                //                layout_facebook_login.setClickable(true);
                //                layout_twitter_login.setClickable(true);
                //                layout_qq_login.setClickable(true);
                //                layout_weixin_login.setClickable(true);
                break;
        }
    }

//    private void dismissMyDialog(){
//        if(null!=mDialog){
//            mDialog.dismiss();
//            mDialog = null;
//        }
//    }

    /**
     * 判断 用户是否安装微信客户端
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断 用户是否安装QQ客户端
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_qq_login:
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                qq.setPlatformActionListener(this);
                boolean qqclientValid = qq.isClientValid();
                if (!qqclientValid) {
                    ToastUtils.showShort(R.string.app_QQ);
                    return;
                } else {
                    qq.SSOSetting(false);
                    authorize(qq, R.string.authorize_qq);
                }
                break;
            case R.id.layout_weixin_login:
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                wechat.setPlatformActionListener(this);
                boolean clientValid = wechat.isClientValid();
                if (!clientValid) {
                    ToastUtils.showShort(R.string.app_WeChat);
                    return;
                } else {
                    wechat.SSOSetting(false);
                    authorize(wechat, R.string.authorize_weixin);
                }
                break;
            case R.id.layout_twitter_login:
                Platform twitter = ShareSDK.getPlatform(Twitter.NAME);
                twitter.setPlatformActionListener(this);
                twitter.SSOSetting(false);
                authorize(twitter, R.string.authorize_twitter);
                break;
            case R.id.layout_facebook_login:
                Platform facebook = ShareSDK.getPlatform(Facebook.NAME);
                facebook.setPlatformActionListener(this);
                boolean fbClientValid = facebook.isClientValid();
                if (!fbClientValid) {
                    ToastUtils.showShort(R.string.app_Facebook);
                    return;
                } else {
                    facebook.SSOSetting(false);
                    authorize(facebook, R.string.authorize_facebook);
                }
                break;
            case R.id.jump:
                initUserInfo(null);
                dismissDialog();
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                finish();
                SmartPenApp.isFirst = false;
                break;
            default:
                break;
        }
    }

    private void initUserInfo(Platform platform) {
        if (platform == null) {
            //获取用户资料
            String userId = "123";//获取用户账号
            String userName = getString(R.string.tourist);//获取用户名字
//            String userIcon = "http://img2.imgtn.bdimg.com/it/u=1813493607,361824557&fm=26&gp=0.jpg";//获取用户头像
            String userGender = "m"; //获取用户性别，m = 男, f = 女，如果微信没有设置性别,默认返回null
            userGender = getResources().getString(R.string.man);

            //先删除用户
            Delete.tables(UserInfoData.class);

            UserInfoData userInfoData = new UserInfoData();
            userInfoData.id = 1L;
            userInfoData.userUid = userId;
            userInfoData.userName = userName;
            userInfoData.userIcon = "";
            userInfoData.userSex = userGender;
            userInfoData.save();
            SpUtils.putString(this.getApplicationContext(), SpUtils.LOGIN_INFO, userInfoData.toString());
            AppCommon.setUserInfo(userInfoData);
        } else {
            //获取用户资料
            String userId = platform.getDb().getUserId();//获取用户账号
            String userName = platform.getDb().getUserName();//获取用户名字
            String userIcon = platform.getDb().getUserIcon();//获取用户头像
            String userGender = platform.getDb().getUserGender(); //获取用户性别，m = 男, f = 女，如果微信没有设置性别,默认返回null
            userGender = (userGender == null ? "" : userGender).equals("m") ? getResources().getString(R.string.man) : getResources().getString(R.string.woman);

            //先删除用户
            Delete.tables(UserInfoData.class);

            UserInfoData userInfoData = new UserInfoData();
            userInfoData.id = 1L;
            userInfoData.userUid = userId;
            userInfoData.userName = userName;
            userInfoData.userIcon = userIcon;
            userInfoData.userSex = userGender;
            userInfoData.save();
            SpUtils.putString(this.getApplicationContext(), SpUtils.LOGIN_INFO, userInfoData.toString());
            AppCommon.setUserInfo(userInfoData);
        }
    }

//    private void oauth() {
////        long endTime = System.currentTimeMillis();
////        L.error(TAG, "所用时间：" + (endTime - startTime));
////        String token = SpUtils.getString(this, SpUtils.LOGIN_INFO);
//        UserInfoData userInfoData = AppCommon.getUserInfo();
//        User user = new User();
//        user.openId = userInfoData.userUid;
//        user.nickName = userInfoData.userName;
//        user.headImg = userInfoData.userIcon;
//        user.sex = (userInfoData.userSex.endsWith(getResources().getString(R.string.man)) ? "m" : "f");
//        user.pkgName = BuildConfig.APPLICATION_ID;
//        user.userType = 1;//1 android 2 iOS
//        Gson gson = new Gson();
//        String userStr = gson.toJson(user);
////        String userStr = "{\"openId\":\"D503013A5125662840623E409E1C784D\",\"nickName\":\"Chad\",\"headImg\":\"http://thirdqq.qlogo.cn/g?b\\u003doidb\\u0026k\\u003d0eITSBAVpeBiaudQrh57XjQ\\u0026s\\u003d100\",\"sex\":\"m\",\"userType\":1,\"pkgName\":\"com.eningqu.aipen\"}";
//        String rsaLogin = null;
//        try {
//            //                        rsaLogin = String.valueOf(RSAHelper.encryptByPublicKey(userStr.getBytes(),RSAHelper.RSA.getBytes()));
//            //                        PublicKey key = RSAHelper.keyStrToPublicKey(userStr);
//            L.error("abcd", "userStr: " + userStr);
////            rsaLogin = Base64Utils.encode(RSAUtils.encryptByPublicKey(userStr.getBytes(), RSAUtils.RSA));
////            L.error("abcd", "rsaLogin 1: "+rsaLogin);
//            rsaLogin = new RSAKit(RSAKit.RSA).encrypt(userStr);
//            L.error("abcd", "rsaLogin 2: " + rsaLogin);
//
//            HttpUtils.doPost(AppCommon.BASE_URL + "api/login/oauth", rsaLogin, new Callback() {
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//
//                    L.error("返回：" + response.body().string());
//                    Message msg = new Message();
//                    msg.what = AUTHORIZE_SUCCESS;
//                    EventBusUtil.post(msg);
//                    ToastUtils.showShort(R.string.login_success);
//                    /*Gson gson = new Gson();
//                    ResponseMsg responseMsg = gson.fromJson(response.body().string(), ResponseMsg.class);
//                    if (responseMsg.success && responseMsg.code == 1) {
//                        SpUtils.putString(LoginActivity.this, SpUtils.LOGIN_TOKEN, responseMsg.getDataX().getToken());
//                        Message msg = new Message();
//                        msg.what = AUTHORIZE_SUCCESS;
//                        EventBusUtil.post(msg);
//                                         L.error("登录成功");
//                        ToastUtils.showShort(R.string.login_success);
//                    } else {
//                        dismissDialog();
//                        ToastUtils.showShort(R.string.login_faild);
//                    }*/
//                }
//
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    L.error("登录失败");
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
