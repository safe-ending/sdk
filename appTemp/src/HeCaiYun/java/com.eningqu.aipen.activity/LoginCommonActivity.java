package com.eningqu.aipen.activity;

import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.SmartPenApp;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.NetCommon;
import com.eningqu.aipen.common.dialog.DialogHelper;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.common.utils.HttpUtils;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.databinding.LoginCommonActivityBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.databinding.DataBindingUtil;
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
* @Author: Qiu.Li
* @Create Date: 2021/7/6 15:14
* @Description: 通用登录
* @Email: liqiupost@163.com
*/
public class LoginCommonActivity extends BaseActivity implements View.OnClickListener, PlatformActionListener {

    private final static int AUTHORIZE_COMPLETE = 1;
    private final static int AUTHORIZE_ERROR = 2;
    private final static int AUTHORIZE_CANCEL = 3;
    private final static int AUTHORIZE_CANCEL_SUCCESS = 4;
    private String unionid;

    LoginCommonActivityBinding binding;

    @Override
    protected void setLayout() {
        binding = DataBindingUtil.setContentView(LoginCommonActivity.this, R.layout.login_common_activity);
    }

    @Override
    protected void initView() {
        String local = Locale.getDefault().getLanguage();
        if (local.equals("zh")) {
            binding.llQq.setVisibility(View.VISIBLE);
            binding.llWeichat.setVisibility(View.VISIBLE);
            binding.llFacebook.setVisibility(View.GONE);
            binding.llTwitter.setVisibility(View.GONE);
        } else {
            binding.llQq.setVisibility(View.GONE);
            binding.llWeichat.setVisibility(View.GONE);
            binding.llFacebook.setVisibility(View.VISIBLE);
            binding.llTwitter.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {
        binding.llFacebook.setOnClickListener(this);
        binding.llQq.setOnClickListener(this);
        binding.llTwitter.setOnClickListener(this);
        binding.llWeichat.setOnClickListener(this);
        binding.login.setOnClickListener(this);
        binding.regesterCommon.setOnClickListener(this);
        binding.forgetPwdCommon.setOnClickListener(this);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login: {
                String login_name = binding.loginNameNum.getText().toString();
                String login_pwd = binding.loginNamePwd.getText().toString();
                if (TextUtils.isEmpty(login_name)) {
                    Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(login_pwd)) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                Map<String, String> maps = new HashMap<>();
                maps.put("mobile", login_name);
                maps.put("password", login_pwd);
//                NetCommon.mCloudLogin(maps);
                break;
            }
            case R.id.exit:
                LoginCommonActivity.this.finish();
                break;
            case R.id.ll_facebook: {
                Platform facebook = ShareSDK.getPlatform(Facebook.NAME);
                facebook.setPlatformActionListener(LoginCommonActivity.this);
                boolean fbClientValid = facebook.isClientValid();
                if (!fbClientValid) {
                    ToastUtils.showShort(R.string.app_Facebook);
                    return;
                } else {
                    facebook.SSOSetting(false);
                    authorize(facebook, R.string.authorize_facebook);
//                    ll_qq.setClickable(false);
//                    ll_twitter.setClickable(false);
//                    ll_weichat.setClickable(false);
//                    ll_facebook.setClickable(false);
                }
                break;
            }
            case R.id.ll_qq: {
                final Platform qq = ShareSDK.getPlatform(QQ.NAME);
                qq.setPlatformActionListener(LoginCommonActivity.this);
                boolean qqclientValid = qq.isClientValid();
                isQQorWeichat = 2;
                String exportData = qq.getDb().exportData();
                if (!qqclientValid) {
                    ToastUtils.showShort(R.string.app_QQ);
                    return;
                } else {
                    qq.SSOSetting(false);
                    authorize(qq, R.string.authorize_qq);
//                    ll_qq.setClickable(false);
//                    ll_twitter.setClickable(false);
//                    ll_weichat.setClickable(false);
//                    ll_facebook.setClickable(false);
                }
                break;
            }
            case R.id.ll_twitter: {
                Platform twitter = ShareSDK.getPlatform(Twitter.NAME);
                twitter.setPlatformActionListener(LoginCommonActivity.this);
//                boolean twClientValid = twitter.isClientValid();
//                if(!twClientValid) {
//                    ToastUtils.showShort(R.string.app_Twitter);
//                    return;
//                } else {
                twitter.SSOSetting(false);
                authorize(twitter, R.string.authorize_twitter);
//                ll_qq.setClickable(false);
//                ll_twitter.setClickable(false);
//                ll_weichat.setClickable(false);
//                ll_facebook.setClickable(false);
//                }
                break;
            }
            case R.id.ll_weichat: {
                isQQorWeichat = 1;

                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                wechat.setPlatformActionListener(LoginCommonActivity.this);
                boolean clientValid = wechat.isClientValid();

                if (!clientValid) {
                    ToastUtils.showShort(R.string.app_WeChat);
                    return;
                } else {
                    wechat.SSOSetting(false);
                    authorize(wechat, R.string.authorize_weixin);
//                    ll_qq.setClickable(false);
//                    ll_twitter.setClickable(false);
//                    ll_weichat.setClickable(false);
//                    ll_facebook.setClickable(false);
                }
                break;
            }
            case R.id.regester_common: {
//                Intent intent = new Intent(this, RegesterCommonActivity.class);
//                startActivity(intent);
                break;
            }
            case R.id.forget_pwd_common: {
//                Intent intent = new Intent(this, ForgetCommonActivity.class);
//                startActivity(intent);
                break;
            }
        }
    }

    private int isQQorWeichat = 0;
    //授权
    private void authorize(Platform plat, int msg) {
        dialog = DialogHelper.showProgress(getSupportFragmentManager(), msg, false);
        if (plat.isAuthValid()) { //如果授权就删除授权资料
            plat.removeAccount(true);
        }
        plat.showUser(null);//授权并获取用户信息
    }

    @Override
    public void onComplete(final Platform platform, int action, HashMap<String, Object> hashMap) {

        if (isQQorWeichat == 1) {
            unionid = platform.getDb().get("unionid");
            Message msg = new Message();
            msg.obj = platform;
            msg.what = AUTHORIZE_COMPLETE;
            EventBusUtil.post(msg);
        } else if (isQQorWeichat == 2) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpUtils.doGet("https://graph.qq.com/oauth2.0/me?access_token=" + platform.getDb().getToken() + "&unionid=1", new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String s = response.body().string();
                            String[] split = s.split(":");
                            s = split[split.length - 1];
                            split = s.split("\"");
                            s = split[1];
                            unionid = s;
                            Message msg = new Message();
                            msg.obj = platform;
                            msg.what = AUTHORIZE_COMPLETE;
                            EventBusUtil.post(msg);
                        }
                    });
                }
            }).start();
        }else{
            Message msg = new Message();
            msg.obj = platform;
            msg.what = AUTHORIZE_COMPLETE;
            EventBusUtil.post(msg);
        }
    }

    @Override
    public void onError(Platform platform, int action, Throwable throwable) {
        Message msg = new Message();
        msg.obj = platform;
        msg.what = AUTHORIZE_ERROR;
        EventBusUtil.post(msg);
        ToastUtils.showShort(R.string.authorize_fail);
    }

    @Override
    public void onCancel(Platform platform, int action) {
//        dialog = DialogHelper.showProgress(getSupportFragmentManager(), R.string.authorize_success);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListner(final Message message) {
        switch (message.what) {
            case AUTHORIZE_COMPLETE:
                long startTime = System.currentTimeMillis();
                Platform platform = (Platform) message.obj;
                if (platform != null) {
/*
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
                    SpUtils.putString(LoginCommonActivity.this, SpUtils.LOGIN_INFO, userInfoData.toString());
                    AppCommon.setUserInfo(userInfoData);
                    dismissDialog();

//                    long endTime = System.currentTimeMillis();
//                    L.error("", "所用时间：" + (endTime - startTime));

//                    dismissDialog();
//                    Intent intent = new Intent(this, MainActivity.class);
//                    this.startActivity(intent);
//                    finish();
                    SmartPenApp.isFirst = false;
                    long endTime = System.currentTimeMillis();
                    L.error("", "所用时间：" + (endTime - startTime));
                    String token = SpUtils.getString(this, SpUtils.LOGIN_INFO);
                    User user = new User();
                    if (isQQorWeichat != 0) {
                        user.openId = unionid;
                    } else {
                        user.openId = userId;
                    }
                    user.nickName = userName;
                    user.headImg = userIcon;
                    user.pkgName = "com.eningqu.aipen";
                    user.userType = 1;
                    if ("女".equals(userGender)) {
                        user.sex = "F";
                    } else {
                        user.sex = "M";
                    }
                    Gson gson = new Gson();
                    String userStr = gson.toJson(user);
                    String rsaLogin = null;
                    try {
//                        rsaLogin = String.valueOf(RSAHelper.encryptByPublicKey(userStr.getBytes(),RSAHelper.RSA.getBytes()));
//                        PublicKey key = RSAHelper.keyStrToPublicKey(userStr);
                        rsaLogin = Base64Utils.encode(RSAUtils.encryptByPublicKey(userStr.getBytes(), RSAUtils.RSA));
                        L.error("abcd", rsaLogin);
                        HttpUtils.doPost(Common.BASE_URL + "api/login/oauth", rsaLogin, new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                L.error("登录成功");
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    int code = jsonObject.getInt("code");
                                    boolean success = jsonObject.getBoolean("success");
                                    if (code == 1 && success) {

                                        JSONObject data = jsonObject.getJSONObject("data");
                                        if (data.has("isBindMobile")) {
//                                            String openId = data.getString("openId");
                                            String openId = data.getString("token");
                                            Intent intent = new Intent(LoginCommonActivity.this,BindingCommonActivity.class);
                                            intent.putExtra("openId", openId);
                                            startActivity(intent);
                                        }else{
                                            SpUtils.putString(LoginCommonActivity.this, SpUtils.LOGIN_TOKEN, data.getString("token"));
                                            Message msg = new Message();
                                            msg.what = AUTHORIZE_CANCEL_SUCCESS;
                                            EventBusUtil.post(msg);
                                            ToastUtils.showShort(R.string.login_success);
                                        }

                                    } else {
                                        dismissDialog();
                                        ToastUtils.showShort(R.string.login_faild);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call call, IOException e) {
                                L.error("登录失败");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                } else {
                    dismissDialog();
                    ToastUtils.showShort(R.string.authorize_fail);
                }
                break;
            case AUTHORIZE_ERROR:
            case AUTHORIZE_CANCEL:
                dismissDialog();
                binding.llQq.setClickable(true);
                binding.llTwitter.setClickable(true);
                binding.llWeichat.setClickable(true);
                binding.llFacebook.setClickable(true);
                break;
            case AUTHORIZE_CANCEL_SUCCESS:
                dismissDialog();
                gotoActivity(MainActivity.class);
                SmartPenApp.isFirst = false;
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                finish();
                binding.llQq.setClickable(true);
                binding.llTwitter.setClickable(true);
                binding.llWeichat.setClickable(true);
                binding.llFacebook.setClickable(true);
                break;
            case 80003: {
                String body = (String) message.obj;
                try {
                    JSONObject jsonObject = new JSONObject(body);
                    Toast.makeText(this, jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                    if (jsonObject.getBoolean("success")) {
                        SpUtils.putString(this, SpUtils.LOGIN_TOKEN, jsonObject.getString("data"));
                        gotoActivity(MainActivity.class);
                        SmartPenApp.isFirst = false;
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

        }

    }

}
