package com.eningqu.aipen.activity;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.eningqu.aipen.R;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.PEN_CONN_STATUS;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.databinding.ActivitySettingBinding;
import com.eningqu.aipen.fragment.LogoutDialogFragment;
import com.nq.edusaas.hps.PenSdkCtrl;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/6 19:30
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = SettingActivity.class.getSimpleName();

    //    @BindView(R.id.tv_title)
    //    TextView title;
    private ActivitySettingBinding mBinding;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        //        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void initView() {
        mBinding.layoutTitle.tvTitle.setText(R.string.drawer_setting);
        mBinding.layoutTitle.ivBack.setOnClickListener(this);
        mBinding.layoutUpdate.setOnClickListener(this);
        mBinding.layoutPenSetting.setOnClickListener(this);
        mBinding.layoutLogout.setOnClickListener(this);
        mBinding.layoutUserAgreement.setOnClickListener(this);
        mBinding.layoutUserAgreement2.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.layout_pen_setting) {
            gotoActivity(PenSettingActivity.class);
        } else if (id == R.id.layout_update) {//                updateVersion();
//                if (NetworkUtil.isNetWorkAvailable(this)) {
            gotoActivity(VersionActivity.class);
//                } else {
//                    Toast.makeText(this, R.string.network_error_tip, Toast.LENGTH_SHORT).show();
//                }
        } else if (id == R.id.layout_user_agreement) {
            Bundle bundle = new Bundle();
            bundle.putInt(WebViewActivity.TYPE_KEY, WebViewActivity.WEB_VIEW_TYPE_PRO);
            gotoActivity(WebViewActivity.class, bundle);
        } else if (id == R.id.layout_logout) {
            LogoutDialogFragment.newInstance().show(getSupportFragmentManager(), LogoutDialogFragment.class.getName());
        } else if (id == R.id.layout_user_agreement2) {
            Bundle bundle = new Bundle();
            bundle.putInt(WebViewActivity.TYPE_KEY, WebViewActivity.WEB_VIEW_TYPE_PRO2);
            gotoActivity(WebViewActivity.class, bundle);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutEventListener(EventBusCarrier carrier) {
        if (null == carrier) {
            return;
        }
        //        EventBusCarrier eventBusCarrier = new EventBusCarrier();
        int eventType = carrier.getEventType();
        switch (eventType) {
            case Constant.USER_LOGOUT:
                AppCommon.logoutReset(1);
                try {//退出后断开设备，让用户重新连接，避免偶现的连接状态错乱
                    PenSdkCtrl.getInstance().disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AFPenClientCtrl.getInstance().setConnStatus(PEN_CONN_STATUS.DISCONNECTED);

                SpUtils.putString(this, SpUtils.LOGIN_TOKEN, "");
                SpUtils.putString(this, SpUtils.LOGIN_INFO, "");
                AppCommon.setNotebooksChange(true);
                //                SmartPenApp.getApp().exit();
                gotoActivity(LoginActivity.class, true);
                break;
        }
    }
}
