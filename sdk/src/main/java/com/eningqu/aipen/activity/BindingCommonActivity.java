//package com.eningqu.aipen.activity;
//
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.eningqu.aipen.R;
//import com.eningqu.aipen.base.ui.BaseActivity;
//import com.eningqu.aipen.common.NetCommon;
//
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import butterknife.BindView;
//
///**
// * 说明：
// * 作者：WangYabin
// * 邮箱：wyb@eningqu.com
// * 时间：10:58
// */
//public class BindingCommonActivity extends BaseActivity implements View.OnClickListener {
//    @BindView(R.id.imageView)
//    ImageView imageView;
//    @BindView(R.id.login_name_num)
//    EditText login_name_num;
//    @BindView(R.id.login_name_pwd)
//    EditText login_name_pwd;
//    @BindView(R.id.login_name_code)
//    EditText login_name_code;
//    @BindView(R.id.get_code_common)
//    Button get_code_common;
//    @BindView(R.id.sure)
//    ImageView sure;
//    @BindView(R.id.login_has)
//    TextView login_has;
//    private int type = 0;
//    private String openId;
//    private int T = 60;
//    private MyHanlder mHandler;
//    private Runnable myRunnable;
//    private Thread myThred;
//
//    @Override
//    protected void setLayout() {
//        setContentView(R.layout.binding_common_activity);
//    }
//
//    @Override
//    protected void initView() {
//
//    }
//
//    @Override
//    protected void initData() {
//        mHandler = new MyHanlder();
//        openId = getIntent().getStringExtra("openId");
//        imageView.setOnClickListener(this);
//        login_has.setOnClickListener(this);
//        sure.setOnClickListener(this);
//        get_code_common.setOnClickListener(this);
//    }
//
//    @Override
//    protected void initEvent() {
//
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.get_code_common: {
//                String login_name = login_name_num.getText().toString();
//                NetAppCommon.getCode(login_name);
//                NetAppCommon.checkNumber(login_name);
//                myRunnable = new MyCountDownTimer();
//                //开始执行
//                myThred = new Thread(myRunnable);
//                myThred.start();
//                break;
//            }
//            case R.id.sure: {
//                String login_name = login_name_num.getText().toString();
//                String login_pwd = login_name_pwd.getText().toString();
//                String login_code = login_name_code.getText().toString();
//                if (TextUtils.isEmpty(login_name)) {
//                    Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                if (TextUtils.isEmpty(login_pwd)) {
//                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                if (TextUtils.isEmpty(login_code)) {
//                    Toast.makeText(this, "验证码不能为空", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                if (type == 0) {
//                    Toast.makeText(this, "号码已经注册且已绑定第三方账号", Toast.LENGTH_LONG).show();
//                }
//                if (type == 1) {//手机号码已注册但未绑定
//                    Map<String, String> maps = new HashMap<>();
//                    maps.put("mobile", login_name);
//                    maps.put("openId", openId);
//                    maps.put("code", login_code);
//                    maps.put("pkgName", "com.eningqu.com");
//                    NetAppCommon.bindingHasResNumber(this, maps, 1);
//                }
//                if (type == 2) {//手机号未注册
//                    Map<String, String> maps = new HashMap<>();
//                    maps.put("mobile", login_name);
//                    maps.put("password", login_pwd);
//                    maps.put("openId", openId);
//                    maps.put("code", login_code);
//                    maps.put("pkgName", "com.eningqu.com");
//                    NetAppCommon.bindingHasResNumber(this, maps, 2);
//                }
//                break;
//            }
//            case R.id.imageView: {
//                finish();
//                break;
//            }
//            case R.id.login_has: {
//                Intent intent = new Intent(this, LoginActivity.class);
//                startActivity(intent);
//                finish();
//                break;
//            }
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventListner(final Message message) {
//        switch (message.what) {
//            case 80004: {
//                type = 0;
//                break;
//            }
//            case 80005: {//手机号码已注册但未绑定
//                type = 1;
//                break;
//            }
//            case 80006: {//手机号未注册
//                type = 2;
//                break;
//            }
//            case 80007: {
//                String msg = (String) message.obj;
//                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }
//    }
//
//    class MyHanlder extends Handler {
//
//    }
//
//    class MyCountDownTimer implements Runnable {
//
//        @Override
//        public void run() {
//
//            //倒计时开始，循环
//            while (T > 0) {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (null != get_code_common) {
//                            get_code_common.setClickable(false);
//                            get_code_common.setText(T + "秒后重新获取");
//                        }
//                    }
//                });
//                try {
//                    Thread.sleep(1000); //强制线程休眠1秒，就是设置倒计时的间隔时间为1秒。
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                T--;
//            }
//
//            //倒计时结束，也就是循环结束
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (null != get_code_common) {
//                        get_code_common.setClickable(true);
//                        get_code_common.setText("获取验证码");
//                    }
//                }
//            });
//            T = 10; //最后再恢复倒计时时长
//        }
//    }
//
//}
