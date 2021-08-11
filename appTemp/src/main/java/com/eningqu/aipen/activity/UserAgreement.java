package com.eningqu.aipen.activity;

import android.view.View;
import android.widget.TextView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.base.ui.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 说明：
 * 作者：WangYabin
 * 邮箱：wyb@eningqu.com
 * 时间：10:44
 */
public class UserAgreement extends BaseActivity {
    @BindView(R.id.tv_user)
    TextView textView;

    @BindView(R.id.tv_title)
    TextView tv_title;
    @Override
    protected void setLayout() {
        setContentView(R.layout.activity_user_agreement);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        textView.setText(getResources().getString(R.string.user_agreement_title)
                .replace("_","\n")
                .replace("*","\u3000"));
        tv_title.setText(R.string.user_agreement_title);
    }

    @Override
    protected void initEvent() {

    }

    @OnClick({R.id.iv_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back: {
                finish();
                break;
            }
        }
    }
}
