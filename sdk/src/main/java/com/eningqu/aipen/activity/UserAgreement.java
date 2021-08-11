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
    TextView textView;

    TextView tv_title;
    @Override
    protected void setLayout() {
        setContentView(R.layout.activity_user_agreement);
    }

    @Override
    protected void initView() {
        textView = findViewById(R.id.tv_user);
        tv_title = findViewById(R.id.tv_title);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

}
