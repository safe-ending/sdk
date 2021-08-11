package com.eningqu.aipen.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.SmartPenApp;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.dialog.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.utils.CommandExecution;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.db.model.UserInfoData;
import com.eningqu.aipen.qpen.QPenManager;
import com.eningqu.aipen.recyle.AppStatus;
import com.eningqu.aipen.recyle.AppStatusManager;
import com.raizlabs.android.dbflow.sql.language.Delete;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.OnClick;


/**
 * 说明：
 * 作者：WangYabin
 * 邮箱：wyb@eningqu.com
 * 时间：9:41
 */
public class WelcomeActivity extends BaseActivity {
    private static final String TAG = WelcomeActivity.class.getSimpleName();
    private final int JUMPOVER = 0;
    private boolean readed = false;
    @BindView(R.id.jump)
    TextView jumpView;
    @BindView(R.id.welcome_img)
    ImageView welcome_img;

    @Override
    protected void setLayout() {
        setContentView(R.layout.activity_welcome);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case JUMPOVER: {
                    showUserAgree();
                    break;
                }
            }
        }

    };

    @Override
    protected void initView() {
        Message msg = new Message();
        msg.what = JUMPOVER;
//        mHandler.sendMessageDelayed(msg, 3000);

//        Boolean recoSetting = SpUtils.getBoolean(this, Constant.SP_KEY_RECO_SETTING, false);
//        if (!recoSetting) {
//            String locale = Locale.getDefault().toString();
//
//            switch (locale) {
//                case "zh_CN":
//                case "ja_JP":
//                    SpUtils.putString(this, Constant.SP_KEY_RECO_LANGUAGE, locale);
//                    break;
//                case "ja_CN":
//                    SpUtils.putString(this, Constant.SP_KEY_RECO_LANGUAGE, "ja_JP");
//                    break;
//                default:
//                    SpUtils.putString(this, Constant.SP_KEY_RECO_LANGUAGE, "en_US");
//                    break;
//            }
//        }
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initEvent() {
    }

    @OnClick({R.id.jump, R.id.welcome_img})
    public void goOut(View view) {
        switch (view.getId()) {
            case R.id.jump: {
                Boolean isAgree = SpUtils.getBoolean(this, Constant.SP_KEY_USER_AGREE, false);
                if(isAgree){
                    mHandler.removeMessages(JUMPOVER);
                    SmartPenApp.getApp().appInit();
                    startLoginOrMain();
                    finish();
                    L.error("广告", "关闭");
                } else {
                    showUserAgree();
                }
                break;
            }
            case R.id.welcome_img: {
                L.error("广告", "广告");
//                ToastUtils.showShort("点到广告啦");
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        mHandler.removeMessages(JUMPOVER);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppCommon.setNotebooksChange(true);
        QPenManager.getInstance().setNeedInit(true);
        if(readed){
            Message msg = new Message();
            msg.what = JUMPOVER;
            mHandler.sendMessageDelayed(msg, 100);
        } else {
            Message msg = new Message();
            msg.what = JUMPOVER;
            mHandler.sendMessageDelayed(msg, 3000);
        }
    }

    /**
     * 检查登录和跳转主页面
     */
    private void startLoginOrMain() {
        if (!AppCommon.checkLogin()) {
            CommandExecution.CommandResult commandResult = CommandExecution.execCommand("getprop ro.boot.serialno", false);
            if (commandResult != null) {
                if (commandResult.successMsg.equals("21V1ALH200541") || commandResult.successMsg.equals("21V1ALH200208") || commandResult.successMsg.equals("21V2ALH201162")
                        || commandResult.successMsg.equals("21V1ALH200097")) {
                    setTmpUser();
                    gotoActivity(MainActivity.class, true);
                    return;
                }
            }
            boolean bpermission = SPUtils.getInstance().getBoolean(Constant.SP_KEY_PERMISSION, false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !bpermission) {
                gotoActivity(PermissionActivity.class, true);
            } else {
                gotoActivity(LoginActivity.class, true);
            }
        } else {
            gotoActivity(MainActivity.class, true);
        }
        finish();
    }

    private void setTmpUser() {
        //获取用户资料
        String userId = "123";//获取用户账号
        String userName = "123";//获取用户名字
        String userIcon = "http://img2.imgtn.bdimg.com/it/u=1813493607,361824557&fm=26&gp=0.jpg";//获取用户头像
        String userGender = "m"; //获取用户性别，m = 男, f = 女，如果微信没有设置性别,默认返回null
        userGender = getResources().getString(R.string.man);

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

    private void showUserAgree() {
        Boolean isAgree = SpUtils.getBoolean(this, Constant.SP_KEY_USER_AGREE, false);
        if (!isAgree) {
            SpannableStringBuilder ss = new SpannableStringBuilder(getString(R.string.str_agree_content1));
            String string = getString(R.string.str_agree_sure);
            SpannableString sAgreeSure1 = new SpannableString(string);
            sAgreeSure1.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    readed = true;
                    Bundle bundle = new Bundle();
                    bundle.putInt(WebViewActivity.TYPE_KEY, WebViewActivity.WEB_VIEW_TYPE_PRO);
                    gotoActivity(WebViewActivity.class, bundle);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ContextCompat.getColor(WelcomeActivity.this, R.color.app_click_text_green));
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
                    readed = true;
                    Bundle bundle = new Bundle();
                    bundle.putInt(WebViewActivity.TYPE_KEY, WebViewActivity.WEB_VIEW_TYPE_PRO2);
                    gotoActivity(WebViewActivity.class, bundle);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(ContextCompat.getColor(WelcomeActivity.this, R.color.app_click_text_green));
                    ds.setUnderlineText(false);    //去除超链接的下划线
                }

            }, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.append(sAgreeSure2);


            SpannableStringBuilder ss2 = new SpannableStringBuilder(getString(R.string.str_agree_content2));
            ss.append(ss2);

            if(null!=dialog && !dialog.isCancelable()){
                return;
            }
            dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                @Override
                public void confirm(View view) {
                    dismissDialog();
                    SpUtils.putBoolean(WelcomeActivity.this, Constant.SP_KEY_USER_AGREE, true);
                    SmartPenApp.getApp().appInit();
                    startLoginOrMain();
                }

                @Override
                public void cancel() {
                    dismissDialog();
                    SmartPenApp.forceExit = true;
                    finish();
                }
            }, R.string.user_agreement_title_all, ss, R.string.str_agree, R.string.str_unagree, false);
        } else {
            SmartPenApp.getApp().appInit();
            startLoginOrMain();
        }
    }
}
