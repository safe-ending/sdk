package com.eningqu.aipen.activity;

import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.eningqu.aipen.R;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.bean.caiyun.AASLoginRsp;
import com.eningqu.aipen.bean.caiyun.GetVeriCodeRsp;
import com.eningqu.aipen.common.MCloudConf;
import com.eningqu.aipen.common.NetCommon;
import com.eningqu.aipen.common.utils.GeneratorUtil;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.xml.Xml2Obj;
import com.eningqu.aipen.databinding.ActivityLoginSmsBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.databinding.DataBindingUtil;
import nq.com.ahlibrary.utils.MD5;

/**
 * @Author: Qiu.Li
 * @Create Date: 2021/7/6 15:14
 * @Description: 获取短信验证码
 * @Email: liqiupost@163.com
 */
public class LoginSMSActivity extends BaseActivity implements View.OnClickListener {

    ActivityLoginSmsBinding binding;
    private String mobile;

    private Timer timer = new Timer();
    TimerTask task;
    private int secondLeft = 60;

    @Override
    protected void setLayout() {
//        setContentView(R.layout.login_common_activity);
        binding = DataBindingUtil.setContentView(LoginSMSActivity.this, R.layout.activity_login_sms);
    }

    @Override
    protected void initView() {
        String local = Locale.getDefault().getLanguage();

        binding.includeTopBar.tvTitle.setText("和彩云登录");
    }

    @Override
    protected void initData() {
        createTimerTask();
    }

    @Override
    protected void initEvent() {
        binding.includeTopBar.ivBack.setOnClickListener(this);
        binding.tvGetVeriCode.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                LoginSMSActivity.this.finish();
                break;
            case R.id.tv_get_veri_code:
                getSMSVeriCode();
                break;
            case R.id.btn_login:
                mCloudLogin();
                break;
        }
    }

    private void getSMSVeriCode() {
        mobile = binding.loginNameNum.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        binding.tvGetVeriCode.setText("60重新获取");
        binding.tvGetVeriCode.setClickable(false);
        createTimerTask();
        timer.schedule(task, 1000, 1000);
        Map<String, String> maps = new HashMap<>();
        maps.put("random", GeneratorUtil.randomSequence(32));//随机字符串32位
        maps.put("mode", "0");//0：发送验证码到手机
        maps.put("reqType", "3");//3：彩云用户用手机号码登录并自动开户时获取短信验证码流程
        maps.put("msisdn", "+86" + mobile);//用户手机号码，与用户账号二者取其一，不可同时存在。需携带国家码
        maps.put("lang", "zh_CN");//语音
        maps.put("clientType", MCloudConf.CLIENT_TYPE);//客户端类型，3位字符串。由AAS分配

        NetCommon.aasGetVeriCode(MCloudConf.MCLOUD_AAS_URL + "tellin/getDyncPasswd.do", maps);
    }

    private void mCloudLogin() {

        mobile = binding.loginNameNum.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        String veriCode = binding.loginVeriCode.getText().toString().trim();
        if (TextUtils.isEmpty(veriCode)) {
            Toast.makeText(this, "短信验证码不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, String> maps = new HashMap<>();
        maps.put("msisdn", "+86" + mobile);//用户手机号码，与用户账号二者取其一，不可同时存在。需携带国家码
//        maps.put("random", GeneratorUtil.randomSequence(32));//随机字符串32位
//        maps.put("secinfo", "1.0");//登录系统的139号码、密码加密后的字符串，密文
        maps.put("version", "1.0");//客户端版本号，数字.数字，如12.27
        maps.put("clientType", MCloudConf.CLIENT_TYPE);//客户端类型，3位字符串。由AAS分配
        maps.put("pintype", "5");// 5：彩云短信动态密码登录
        maps.put("dycpwd", veriCode);//当pintype为5、6时，此字段填写用户从RCS系统获取的短信验证码
        maps.put("cpid", MCloudConf.CPID);//CPID，由AAS分配

        NetCommon.aasLogin(maps);
    }

    private String getAESSecret(String veriCode) {
        String no = "BiJi@rF02(hK5_cag#%78uL01";
        String vcMD5 = "";
        String secretMD5 = "";
        try {
            vcMD5 = MD5.getMD5(veriCode);
            secretMD5 = MD5.getMD5(vcMD5 + no);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (null != secretMD5 && secretMD5.length() > 16) {
            secretMD5 = secretMD5.substring(0, 16);
        }
        return secretMD5;
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
                L.debug("body=" + body);
                try {
                    GetVeriCodeRsp obj  = Xml2Obj.fromXml(body, GetVeriCodeRsp.class);

                    if (null != obj) {
                        if ("0".equals(obj.getError())) {
                            showToast("短信已经发送，请查收");
                        } else {
                            showToast("失败：" + obj.getError());
                        }
                    }
                    //xml消息体经过AES加密，密钥md5（md5（动态密码）+约定编号）取前16位
                } catch (Exception e) {
                    e.printStackTrace();
                }
                secondLeft = 0;
                break;
            }
            case 80013: {
                String body = (String) message.obj;
                L.debug("body=" + body);
                try {
                    AASLoginRsp obj = Xml2Obj.fromXml(body, AASLoginRsp.class);
                    if (null != obj) {
                        if("0".equals(obj.getError())){
                            showToast("登录成功");
                        } else {
                            showToast("登录失败："+obj.getError() + " "+obj.getDesc());
                        }
                    }
                    //xml消息体经过AES加密，密钥md5（md5（动态密码）+约定编号）取前16位
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                secondLeft = 0;
                break;
            }
        }

    }

    private void createTimerTask() {
        task = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        secondLeft--;
                        binding.tvGetVeriCode.setText(secondLeft + "重新获取");
                        if (secondLeft < 0) {
                            timer.cancel();
                            binding.tvGetVeriCode.setClickable(true);
                            binding.tvGetVeriCode.setText("获取验证码");
                        }
                    }
                });
            }
        };
    }
}
