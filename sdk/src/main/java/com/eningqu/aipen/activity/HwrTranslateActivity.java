package com.eningqu.aipen.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.eningqu.aipen.BuildConfig;
import com.eningqu.aipen.R;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.bean.AuthBaseBean;
import com.eningqu.aipen.bean.TransResultBean;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.databinding.ActivityHwrTranslateBinding;
import com.eningqu.aipen.manager.ShareManager;
import com.google.gson.Gson;

import nq.com.ahlibrary.BaseAHUtil;
import nq.com.ahlibrary.utils.AhUtil;
import nq.com.ahlibrary.utils.NQSpeechUtils;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/7/5 17:09
 * desc   :
 * version: 1.0
 */
@Deprecated
public class HwrTranslateActivity extends BaseActivity {

    private ActivityHwrTranslateBinding mBinding;

    public static final String INTENT_KEY_CONTENT = "intent_key_content";
    public static final String INTENT_KEY_LANGUAGE = "intent_key_language";
    public static final String INTENT_KEY_TO_LANGUAGE = "intent_key_to_language";
    private String content;
    private String language;
    private String toLanguage;
    private BaseAHUtil mBaseAhUtil;

    @Override
    protected void setLayout() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);// 设置默认键盘不弹出
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_hwr_translate);
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            content = bundle.getString(INTENT_KEY_CONTENT);
            language = bundle.getString(INTENT_KEY_LANGUAGE);
            toLanguage = bundle.getString(INTENT_KEY_TO_LANGUAGE);
        }

        mBaseAhUtil = new BaseAHUtil(this);
    }

    @Override
    protected void initView() {
        mBinding.etTranContent.setText(getString(R.string.hand_reco_tran) + "...");
        mBinding.layoutTitle.tvTitle.setText(R.string.hand_reco_tran);
    }

    @Override
    protected void initEvent() {

    }

    public void onViewClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.iv_tran_edit) {
            mBinding.etTranContent.setEnabled(true);
        } else if (id == R.id.iv_tran_share) {//                ShareManager.getInstance().toShare("", ShareManager.SHARE_TYPE.TEXT, mBinding.etTranContent.getText().toString());
            ShareManager.getInstance().showShare(getSupportFragmentManager(), ShareManager.SHARE_TYPE.TEXT,
                    mBinding.etTranContent.getText().toString(), "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toAuth();
        ShareManager.getInstance().init(this, iShareCallback);
    }

    private void toAuth() {
        String btAddr = AFPenClientCtrl.getInstance().lastTryConnectAddr;
        final String mac = btAddr.contains("-") ? btAddr.substring(btAddr.indexOf("-") + 1) : btAddr;//60HW-C3:49:D4:23:BC:BC

        mBaseAhUtil.ieAndBleLocal(mac, BuildConfig.APPLICATION_ID, new AhUtil.AhGetListener() {
            @Override
            public void onFailure(String s) {
                L.error(s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.etTranContent.setText(getString(R.string.authorize_fail) + " MAC " + mac);
                    }
                });
            }

            @Override
            public void onResponse(String s) {
                L.info(s);
                Gson gson = new Gson();
                try {
                    final AuthBaseBean authBaseBean = gson.fromJson(s, AuthBaseBean.class);
                    if (null != authBaseBean && authBaseBean.isSuccess()) {
                        toTranslate(content, mac);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.etTranContent.setText(getString(R.string.authorize_fail) + " MAC " + mac);
                            }
                        });
                    }
                } catch (Exception e) {
                    L.error(e.getMessage());
                }
            }

            @Override
            public void onShowMate(String s) {
                L.error(s);
                showToast("result:" + s);
            }
        });
    }

    private void toTranslate(String content, String mac) {
        String APP_KEY_AI = "";
        try {
            ApplicationInfo ai = getApplicationContext().getPackageManager().getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai.metaData) {
                APP_KEY_AI = ai.metaData.getString("NQ_APPSIGN");
            }
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }
        NQSpeechUtils.getTrans(content, language, toLanguage, mac, APP_KEY_AI, "1", new NQSpeechUtils.ITransCallback() {
            @Override
            public void onSuccess(final String s) {
                //                L.debug(s);
                Gson gson = new Gson();
                try {
                    final TransResultBean resultBean = gson.fromJson(s, TransResultBean.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(null!=mBinding.etTranContent && null!=resultBean &&  !TextUtils.isEmpty(resultBean.getData())){
                                mBinding.etTranContent.setText(resultBean.getData());
                            }

                        }
                    });
                } catch (Exception e) {
                    L.error(e.getMessage());
                }
            }

            @Override
            public void onFailed(final String s, final int i, final String s3) {
                L.error(s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.etTranContent.setText(getString(R.string.error_tran) + " error:" + i);
                        if(null!=mBinding.etTranContent){
                            mBinding.etTranContent.setText(getString(R.string.error_tran) + " error:" + i);
                        }
                    }
                });
            }
        });
    }

    ShareManager.IShareCallback iShareCallback = new ShareManager.IShareCallback() {

        @Override
        public void onComplete(int i) {
            if (i != 9)
                showToast(R.string.ssdk_oks_share_completed);
        }

        @Override
        public void onError(int i) {
            showToast(R.string.ssdk_oks_share_failed);
        }

        @Override
        public void onCancel(int i) {
            showToast(R.string.ssdk_oks_share_canceled);
        }
    };
}
