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
//import org.json.JSONException;
//import org.json.JSONObject;
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
// * 时间：18:47
// */
//public class RegesterCommonActivity extends BaseActivity implements View.OnClickListener {
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
//    private MyHanlder mHandler;
//    private int T = 60;
//
//    @Override
//    protected void setLayout() {
//        setContentView(R.layout.regester_common_activity);
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
//                new Thread(new MyCountDownTimer()).start();//开始执行
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
//                Map<String, String> maps = new HashMap<>();
//                maps.put("mobile", login_name);
//                maps.put("password", login_pwd);
//                maps.put("code", login_code);
//                maps.put("pkgName", "com.eningqu.aipen");
//                NetAppCommon.regester(maps);
//                break;
//            }
//            case R.id.imageView: {
//                finish();
//                break;
//            }
//            case R.id.login_has: {
//                Intent intent = new Intent(this, LoginCommonActivity.class);
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
//            case 80001: {
//                String body = (String) message.obj;
//                try {
//                    JSONObject jsonObject = new JSONObject(body);
//                    Toast.makeText(this, jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
//                    gotoActivity(LoginCommonActivity.class);
//                    finish();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
//
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
