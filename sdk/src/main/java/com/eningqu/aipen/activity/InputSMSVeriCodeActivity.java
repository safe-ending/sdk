package com.eningqu.aipen.activity;

import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.eningqu.aipen.R;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.MCloudConf;
import com.eningqu.aipen.common.NetCommon;
import com.eningqu.aipen.common.utils.GeneratorUtil;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.databinding.ActivityInputSmsVeriCodeBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.databinding.DataBindingUtil;

/**
* @Author: Qiu.Li
* @Create Date: 2021/7/6 15:14
* @Description: 校验短信验证码
* @Email: liqiupost@163.com
*/
public class InputSMSVeriCodeActivity extends BaseActivity implements View.OnClickListener{

    ActivityInputSmsVeriCodeBinding binding;

    private String mobile;
    @Override
    protected void setLayout() {
//        setContentView(R.layout.login_common_activity);
        binding = DataBindingUtil.setContentView(InputSMSVeriCodeActivity.this, R.layout.activity_input_sms_veri_code);
    }

    @Override
    protected void initView() {
        String local = Locale.getDefault().getLanguage();
        binding.includeTopBar.tvTitle.setText("和彩云登录");
        binding.loginNameNum.setText(mobile);
    }

    @Override
    protected void initData() {

        mobile = getIntent().getExtras().getString("mobile");
    }

    @Override
    protected void initEvent() {
        binding.includeTopBar.ivBack.setOnClickListener(this);
        binding.btnVeriCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            InputSMSVeriCodeActivity.this.finish();
        } else if (id == R.id.btn_veri_code) {
            String mobile = binding.loginNameNum.getText().toString();
            if (TextUtils.isEmpty(mobile)) {
                Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            Map<String, String> maps = new HashMap<>();
            maps.put("random", GeneratorUtil.randomSequence(32));
            maps.put("mode", "0");
            maps.put("reqType", "3");
            maps.put("msisdn", mobile);
            maps.put("lang", "zh_CN");
            maps.put("clientType", "737");

            NetCommon.aasGetVeriCode(MCloudConf.MCLOUD_AAS_URL + "tellin/getDyncPasswd.do", maps);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListner(final Message message) {
        switch (message.what) {
            case 80012: {
                String body = (String) message.obj;
                L.debug("body="+body);
                try {
                    JSONObject jsonObject = new JSONObject(body);
//                    Toast.makeText(this, jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
 /*                   if (jsonObject.getBoolean("success")) {
                        SpUtils.putString(this, SpUtils.LOGIN_TOKEN, jsonObject.getString("data"));
                        gotoActivity(MainActivity.class);
                        SmartPenApp.isFirst = false;
                        finish();
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

        }

    }

}
