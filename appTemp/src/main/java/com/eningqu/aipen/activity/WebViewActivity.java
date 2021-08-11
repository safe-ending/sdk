package com.eningqu.aipen.activity;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.eningqu.aipen.R;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.databinding.ActivityWebviewBinding;

import java.util.Locale;

import butterknife.OnClick;

/**
 * @Author: Qiu.Li
 * @Create Date: 2019/4/30 14:13
 * @Description: web view load
 * @Email: liqiupost@163.com
 */
public class WebViewActivity extends BaseActivity {

    private ActivityWebviewBinding mBinding;
    //声明WebSettings子类WEB_VIEW_TYPE_PRO
    WebSettings mWebSettings;
    private final static String URL_INTRODUCE_ZH = "file:///android_asset/introduce_zh.html";
    private final static String URL_INTRODUCE_ZH_TW = "file:///android_asset/introduce_zh_tw.html";
    private final static String URL_INTRODUCE_EN = "file:///android_asset/introduce_en.html";

    private final static String URL_PROTOCOL_ZH = "file:///android_asset/protocol_zh.html";
    private final static String URL_PROTOCOL_ZH_TW = "file:///android_asset/protocol_zh_tw.html";
    private final static String URL_PROTOCOL_EN = "file:///android_asset/protocol_en.html";

    private final static String URL_PRIVACY_ZH = "file:///android_asset/privacy_zh.html";
    private final static String URL_PRIVACY_ZH_TW = "file:///android_asset/privacy_zh_tw.html";
    private final static String URL_PRIVACY_EN = "file:///android_asset/privacy_en.html";

    public static final int WEB_VIEW_TYPE_INT = 1;
    public static final int WEB_VIEW_TYPE_PRO = 2;
    public static final int WEB_VIEW_TYPE_PRO2 = 3;
    public static final String TYPE_KEY = "type_key";

    private int webType = 1;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
    }

    @Override
    protected void initView() {
        if (webType == WEB_VIEW_TYPE_INT) {
            mBinding.includeTopBar.tvTitle.setText(R.string.user_guide);
        } else if (webType == WEB_VIEW_TYPE_PRO) {
            mBinding.includeTopBar.tvTitle.setText(R.string.user_agreement_title);
        } else if (webType == WEB_VIEW_TYPE_PRO2) {
            mBinding.includeTopBar.tvTitle.setText(R.string.user_agreement_title2);
        }
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        webType = bundle.getInt(TYPE_KEY, 1);
        mWebSettings = mBinding.webView1.getSettings();
        initWebSetting(mWebSettings);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        String lan = Locale.getDefault().getLanguage();
        String local = getResources().getConfiguration().locale.getCountry();
        if (lan.equalsIgnoreCase("zh")) {
            if (local.equalsIgnoreCase("TW") ||
                    local.equalsIgnoreCase("HK")) {

                if (webType == WEB_VIEW_TYPE_INT) {
                    load(URL_INTRODUCE_ZH_TW);
                } else if (webType == WEB_VIEW_TYPE_PRO) {
                    load(URL_PROTOCOL_ZH_TW);
                } else {
                    load(URL_PRIVACY_ZH_TW);
                }
            } else {

                if (webType == WEB_VIEW_TYPE_INT) {
                    load(URL_INTRODUCE_ZH);
                } else if (webType == WEB_VIEW_TYPE_PRO) {
                    load(URL_PROTOCOL_ZH);
                } else {
                    load(URL_PRIVACY_ZH);
                }
            }

        } else if (lan.equalsIgnoreCase("UK") ||
                lan.equalsIgnoreCase("US")) {
            if (webType == WEB_VIEW_TYPE_INT) {
                load(URL_INTRODUCE_EN);
            } else if (webType == WEB_VIEW_TYPE_PRO) {
                load(URL_PROTOCOL_EN);
            } else {
                load(URL_PRIVACY_EN);
            }
        } else {
            if (webType == WEB_VIEW_TYPE_INT) {
                load(URL_INTRODUCE_EN);
            } else if (webType == WEB_VIEW_TYPE_PRO) {
                load(URL_PROTOCOL_EN);
            } else {
                load(URL_PRIVACY_EN);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mBinding.webView1 != null) {
            mBinding.webView1.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mBinding.webView1.clearHistory();

            ((ViewGroup) mBinding.webView1.getParent()).removeView(mBinding.webView1);
            mBinding.webView1.destroy();
        }
        super.onDestroy();
    }

    private void initWebSetting(WebSettings webSettings) {
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可
        //支持插件
//        webSettings.setPluginsEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
//        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

    }

    private void load(String url) {
        //步骤2. 选择加载方式
        //方式1. 加载一个网页：
//        mBinding.webView1.loadUrl("http://www.google.com/");
        //方式2：加载apk包中的html页面"file:///android_asset/test.html"
        mBinding.webView1.loadUrl(url);
        //方式3：加载手机本地的html页面
//        mBinding.webView1.loadUrl("content://com.android.htmlfileprovider/sdcard/test.html");
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
