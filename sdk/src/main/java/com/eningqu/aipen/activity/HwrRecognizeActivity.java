package com.eningqu.aipen.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.eningqu.aipen.common.utils.NingQuLog;
import com.eningqu.aipen.db.model.RecognizeBean;
import com.eningqu.aipen.myscript.RecognizeCommon;
import com.eningqu.aipen.common.dialog.listener.SelectFileFormatListener;
import com.eningqu.aipen.common.utils.PdfUtil;
import com.eningqu.aipen.common.utils.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.db.model.RecognizeData;
import com.eningqu.aipen.myscript.RecognizeCallback;
import com.eningqu.aipen.myscript.RecognizeDBmanager;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.myscript.iink.eningqu.IInkSdkManager;
import com.nq.edusaas.hps.sdkcore.afpensdk.Const;
import com.eningqu.aipen.qpen.PEN_RECO_STATUS;
import com.eningqu.aipen.qpen.StrokesUtilForQpen;
import com.eningqu.aipen.qpen.bean.PageStrokesCacheBean;
import com.eningqu.aipen.qpen.bean.StrokesBean;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.bean.HanvonRequest;
import com.eningqu.aipen.bean.HanvonResponse;
import com.eningqu.aipen.bean.LanguageBean;
import com.eningqu.aipen.bean.MSBean;
import com.eningqu.aipen.bean.MsHwrResultBean;
import com.eningqu.aipen.bean.NingQuHWRRequest;
import com.eningqu.aipen.bean.TransResultBean;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.HwrEngineEnum;
import com.eningqu.aipen.common.LanguageNQEnum;
import com.eningqu.aipen.common.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.thread.ThreadPoolUtils;
import com.eningqu.aipen.common.utils.HttpUtils;
import com.eningqu.aipen.common.utils.ImageUtil;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.NetworkUtil;
import com.eningqu.aipen.common.utils.ScreenUtils;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.common.utils.SystemUtil;
import com.eningqu.aipen.databinding.ActivityHwrRecoBinding;
import com.eningqu.aipen.qpen.QPenManager;
import com.eningqu.aipen.manager.ShareManager;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nq.com.ahlibrary.utils.NQSpeechUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//import com.myscript.iink.PointerType;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/7/3 17:09
 * desc   : 手写识别分屏显示
 * version: 1.0
 */
public class HwrRecognizeActivity extends BaseActivity implements View.OnClickListener {

    public final static String TAG = HwrRecognizeActivity.class.getSimpleName();

    private ActivityHwrRecoBinding mBinding;
    private String notebookId;
    private int pageNum;
    private String jpgFilePath;
    private List<LanguageBean> mTranLangList = new ArrayList<>();
    //    private List<RecoLanguageBean> mRecoLangList = new ArrayList<>();
    private final long NO_TIMESTAMP = -1;
    private final float NO_PRESSURE = 0.0f;
    private final int NO_POINTER_ID = -1;
    private int pointerId;

    private String contentHwr;//识别的内容
    private String contentTran;//翻译的内容
    private int tranSrcLanguage = 0;//翻译语言原文语种代码
    private int tranDesLanguage = 0;//翻译语言译文语种代码
    private boolean clkAction;//手动触发授权

    StringBuilder recoResultSB = new StringBuilder();

    private final int recoEngine = 1;//手写识别引擎 0 是MyScript，1是微软
    private final String HW_KEY = "03663e70-d78f-4bf6-ba8e-69b2088af321";
    //    private HWCloudManager hwCloudManagerHandSingle;
    PageStrokesCacheBean pageStrokesCache;
    private final String HW_LAN_CODE_CHNS = "chns";//汉王中文简体
    private final String HW_LAN_CODE_CHNT = "chnt";//汉王中文繁体
    private final String HW_LAN_CODE_EN = "en";//汉王英文
    private final String HW_LAN_CODE_JA = "ja";//汉王日文

    private final int MSG_TIME_OUT = 22;
    private final int MSG_SHOW_DIALOG = 23;
    private final int MSG_REQUEST_FAIL = 24;


    private final int REQUEST_CODE_SEL_LANG = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_TIME_OUT:
                    dismissDialog();
                    dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                        @Override
                        public void confirm(View view) {
                            dismissDialog();
                            SpUtils.putLong(HwrRecognizeActivity.this, AppCommon.getCurPageKey("SP_LAST_RECOGNIZE_TIME"), 0);
                            mHandler.sendEmptyMessageDelayed(MSG_SHOW_DIALOG, 100);
                        }

                        @Override
                        public void cancel() {
                            dismissDialog();
                        }
                    }, R.string.hwr_reco_timeout, R.string.str_try_again, R.string.title_retry, R.string.dialog_cancel_text);
                    L.error(TAG, getString(R.string.hwr_reco_timeout));
                    AFPenClientCtrl.getInstance().setRecoStatus(PEN_RECO_STATUS.NONE);
                    break;
                case MSG_SHOW_DIALOG:
                    dismissDialog();
                    dialog = DialogHelper.showProgress(getSupportFragmentManager(), R.string.processing_hw_recognition, true);
                    ThreadPoolUtils.getThreadPool().execute(new RunnableRecognize());
                    break;
                case MSG_REQUEST_FAIL:
                    dismissDialog();
                    dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                        @Override
                        public void confirm(View view) {
                            dismissDialog();
                            SpUtils.putLong(HwrRecognizeActivity.this, AppCommon.getCurPageKey("SP_LAST_RECOGNIZE_TIME"), 0);
                            mHandler.sendEmptyMessageDelayed(MSG_SHOW_DIALOG, 100);
                        }

                        @Override
                        public void cancel() {
                            dismissDialog();
                        }
                    }, R.string.hwr_reco_fail, R.string.str_try_again, R.string.title_retry, R.string.dialog_cancel_text);
                    L.error(TAG, getString(R.string.hwr_reco_fail));
                    AFPenClientCtrl.getInstance().setRecoStatus(PEN_RECO_STATUS.NONE);
                    break;
            }

        }
    };
    private String lastRecoLang;
    private int curId;

    private class RecoLanguageBean {

        String name;//识别语种名称
        String code;//识别语种code
        boolean selected;//是否已选择

        public RecoLanguageBean(String name, String code, boolean selected) {
            this.name = name;
            this.code = code;
            this.selected = selected;
        }
    }

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_hwr_reco);
    }

    @Override
    protected void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);// 设置默认键盘不弹出
        if (null != mBinding) {
            mBinding.layoutTitle.tvTitle.setText(R.string.menu_hand_reco);
            mBinding.layoutTitle.ivRight.setImageResource(R.drawable.icon_hwr_lang);
        }
        setSwitchTextColor(R.id.tv_tran_original);

        String okMac = SpUtils.getString(this, Constant.SP_KEY_AUTH_PEN);
        if (!TextUtils.isEmpty(okMac)) {
            toInitHwrSdk();
        } else {
            toAuth();
        }
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        notebookId = intent.getStringExtra(BaseActivity.NOTEBOOK_ID);
        pageNum = intent.getIntExtra(BaseActivity.PAGE_NUM, 1);
        jpgFilePath = AppCommon.getSharePath(notebookId, pageNum, AppCommon.SUFFIX_NAME_JPG);

        pointerId = 1;
        mTranLangList.clear();
        /*mRecoLangList.clear();
        // 初始化识别语种列表
        mRecoLangList.add(new RecoLanguageBean(getString(R.string.zh_CN), "zh_CN", false));//中文简体
        mRecoLangList.add(new RecoLanguageBean(getString(R.string.zh_TW), "zh_TW", false));//中文繁体
        mRecoLangList.add(new RecoLanguageBean(getString(R.string.ja_JP), "ja_JP", false));//日语
        mRecoLangList.add(new RecoLanguageBean(getString(R.string.en_US), "en_US", false));//英文*/
//        mRecoLangList.add(new RecoLanguageBean(getString(SystemUtil.getResId(mContext, "math")), "math", false));//英文

        // 初始化翻译语种列表
        for (LanguageNQEnum languageNQEnum : LanguageNQEnum.values()) {
            LanguageBean languageBean = new LanguageBean();
            languageBean.setCode(languageNQEnum.getCode());
            languageBean.setName(languageNQEnum.getName());
            languageBean.setName0(languageNQEnum.getName0());
            languageBean.setFlag(SystemUtil.getFlagDrawable(this, languageNQEnum.getPng()));
            mTranLangList.add(languageBean);
        }

        //获取上次选择的识别语种
        int srcLang = SpUtils.getInt(this, Constant.SP_KEY_LANGUAGE, 0);
        if (srcLang == 0) {
            tranSrcLanguage = LanguageNQEnum.CN_ZH.getCode();
        } else {
            tranSrcLanguage = srcLang;
        }
        //获取上次选择的翻译语种
        tranDesLanguage = SpUtils.getInt(this, Constant.SP_KEY_TO_LANGUAGE, 0);
        if (srcLang == 0) {
            tranSrcLanguage = LanguageNQEnum.CN_ZH.getCode();
        }

        if (QPenManager.getInstance().getHwrEngineEnum() == HwrEngineEnum.MY_SCRIPT) {
            IInkSdkManager.getInstance().copyRecoRes(mContext);
        }

        File file = new File(jpgFilePath);
        ImageUtil.load(this, file, mBinding.ivHwContent);
    }

    @Override
    protected void initEvent() {
        //        mBinding.layoutTitle.ivBack.setOnClickListener(this);
        mBinding.etHwrContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                IInkSdkManager.getInstance().saveRecogn(AppCommon.getHwrFilePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage()), s.toString());
                if (haveEdit) {
                    List<RecognizeBean> list = RecognizeDBmanager.getInstance().getRecognizeListByPage(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    long lastime = list.get(list.size() - 1).timestamp;
                    RecognizeDBmanager.getInstance().deleteRecognizeDataByPage(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
                    RecognizeDBmanager.getInstance().deleteRecognizeListByPage(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());

                    //一旦编辑过将整体放在左上角
                    RecognizeDBmanager.getInstance().addRecognizeBean(AppCommon.getUserUID(),
                            AppCommon.getCurrentNotebookId(),
                            AppCommon.getCurrentPage(),
                            lastime,
                            mBinding.etHwrContent.getText().toString(),
                            0,
                            0,
                            0,
                            0,true);

                    RecognizeData recognizeData = RecognizeDBmanager.getInstance().getRecognizeData(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
                    if (recognizeData != null) {
                        RecognizeDBmanager.getInstance().updateRecognizeData(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), mBinding.etHwrContent.getText().toString());
                    } else {
                        RecognizeDBmanager.getInstance().addRecognizeData(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), mBinding.etHwrContent.getText().toString());
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        //        switch (v.getId()){
        //            case R.id.iv_back:
        //                finish();
        //                break;
        //        }
    }

    private boolean haveEdit = false;

    public void onViewClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.fl_hwr_edit) {
            haveEdit = true;
            if (mBinding.etHwrContent.getVisibility() == View.VISIBLE) {
                mBinding.etHwrContent.setEnabled(true);
                mBinding.etHwrContent.setCursorVisible(true);//显示光标
                mBinding.etHwrContentTran.clearFocus();
                mBinding.etHwrContent.requestFocus();
            } else {
                mBinding.etHwrContentTran.setEnabled(true);
                mBinding.etHwrContentTran.setCursorVisible(true);//显示光标
                mBinding.etHwrContent.clearFocus();
                mBinding.etHwrContentTran.requestFocus();
            }
        } else if (id == R.id.fl_hwr_share) {
            if (mBinding.etHwrContent.getVisibility() == View.VISIBLE) {
                String string = mBinding.etHwrContent.getText().toString();
                if (!TextUtils.isEmpty(string))
                    ShareManager.getInstance().showShare(getSupportFragmentManager(), ShareManager.SHARE_TYPE.TEXT,
                            string, "");
                else
                    ToastUtils.showShort(R.string.str_record_null);
            } else {
                String string = mBinding.etHwrContentTran.getText().toString();
                if (!TextUtils.isEmpty(string))
                    ShareManager.getInstance().showShare(getSupportFragmentManager(), ShareManager.SHARE_TYPE.TEXT,
                            string, "");
                else
                    ToastUtils.showShort(R.string.str_record_null);
            }
        } else if (id == R.id.fl_hwr_tran) {
            String string = mBinding.etHwrContent.getText().toString();
            if (!TextUtils.isEmpty(string))
                toSelectLanguage();
            else
                showToast(getResources().getString(R.string.str_record_null));
        } else if (id == R.id.tv_tran_original) {
            setSwitchTextColor(id);
            //                mBinding.etHwrContent.setText(contentHwr);
        } else if (id == R.id.tv_tran_translation) {
            setSwitchTextColor(id);
            String string = mBinding.etHwrContent.getText().toString();
            dismissDialog();
            if (NetworkUtil.isNetWorkAvailable(this)) {
                if (!TextUtils.isEmpty(string)) {
                    if (TextUtils.isEmpty(contentTran)) {
                        String okMac = SpUtils.getString(this, Constant.SP_KEY_AUTH_PEN);
                        if (!TextUtils.isEmpty(okMac)) {
                            toTranslate(okMac);
                        } else {
                            toAuth();
                        }
                    }
                } else
                    showToast(getResources().getString(R.string.str_record_null));
            } else {
                showToast(getResources().getString(R.string.network_error_tip));
            }
        } else if (id == R.id.iv_right) {//                lastRecoLang = SpUtils.getString(this, Constant.SP_KEY_RECO_LANGUAGE, "en_US");
            toSelectRecoLanguage();
        }
    }

    private void setSwitchTextColor(int id) {
        if (id == R.id.tv_tran_original) {
            mBinding.tvTranOriginal.setTextColor(getResources().getColor(R.color.white_F5F5F5));
            mBinding.tvTranTranslation.setTextColor(getResources().getColor(R.color.word_gray));
            mBinding.tvTranOriginal.setBackgroundResource(R.drawable.shape_switch_btn_green_left);
            mBinding.tvTranTranslation.setBackgroundResource(R.drawable.shape_switch_btn_white_right);

            mBinding.etHwrContent.setVisibility(View.VISIBLE);
            mBinding.etHwrContentTran.setVisibility(View.GONE);
        } else if (id == R.id.tv_tran_translation) {
            mBinding.tvTranOriginal.setTextColor(getResources().getColor(R.color.word_gray));
            mBinding.tvTranTranslation.setTextColor(getResources().getColor(R.color.white_F5F5F5));
            mBinding.tvTranOriginal.setBackgroundResource(R.drawable.shape_switch_btn_white_left);
            mBinding.tvTranTranslation.setBackgroundResource(R.drawable.shape_switch_btn_green_right);

            mBinding.etHwrContent.setVisibility(View.GONE);
            mBinding.etHwrContentTran.setVisibility(View.VISIBLE);
        }
        curId = id;
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemUtil.hideSoftKeyboard(this);

        ShareManager.getInstance().init(this, iShareCallback);

        if (0 != tranDesLanguage) {
            //如果已经选择了翻译语种，则修改图标
            if (null != LanguageNQEnum.get(tranDesLanguage)) {
                mBinding.ivHwrTran.setBackground(SystemUtil.getFlagDrawable(this, LanguageNQEnum.get(tranDesLanguage).getPng()));
            } else {
                mBinding.ivHwrTran.setBackgroundResource(R.drawable.icon_hwr_tran);
                SpUtils.putString(this, Constant.SP_KEY_RECO_LANGUAGE, "");
            }
        } else {
            mBinding.ivHwrTran.setBackgroundResource(R.drawable.icon_hwr_tran);
        }

        if (curId != 0) {
            setSwitchTextColor(curId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.info(TAG, "onDestroy()");
        mTranLangList.clear();
//        mRecoLangList.clear();
        mHandler.removeMessages(MSG_TIME_OUT);
        mHandler = null;
        mTranLangList = null;
//        mRecoLangList = null;
        mBinding = null;
        recoResultSB = null;
//        IInkSdkManager.getInstance().editorClean();
        ShareManager.getInstance().unInit();
        AFPenClientCtrl.getInstance().setDrawNow(false);
    }

    //    ArrayList<PointerEvent> events = new ArrayList<PointerEvent>();

    class RunnableRecognize implements Runnable {

        @Override
        public void run() {
            mHandler.sendEmptyMessageDelayed(MSG_TIME_OUT, 90 * 1000);
            AFPenClientCtrl.getInstance().setRecoStatus(PEN_RECO_STATUS.RECOGNIZING);

            pageStrokesCache = StrokesUtilForQpen.getStrokes(new File(AppCommon.getStrokesPath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage())));

            if (QPenManager.getInstance().getHwrEngineEnum() == HwrEngineEnum.MY_SCRIPT) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RecognizeData recognizeData = RecognizeDBmanager.getInstance().getRecognizeData(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
                        if (recognizeData != null) {
                            dismissDialog();
                            AFPenClientCtrl.getInstance().setRecoStatus(PEN_RECO_STATUS.NONE);
                            mHandler.removeMessages(MSG_TIME_OUT);
                            contentHwr = recognizeData.getResultList();
                            if (!TextUtils.isEmpty(contentHwr)) {
                                contentHwr = contentHwr.replaceAll("\u00AD", "\n");
                            }
                            mBinding.etHwrContent.setText(contentHwr);
                            RecognizeCommon.getInstance().recognizeAfterChange(recognizeCallback,false);
                        } else {
                            RecognizeCommon.getInstance().recognizeAll(recognizeCallback);
                        }
                    }
                });

            } else {

                final String recognFile = IInkSdkManager.getInstance().getRecognFile(AppCommon.getHwrFilePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage()));
                if (!TextUtils.isEmpty(recognFile)) {

                    AFPenClientCtrl.getInstance().setRecoStatus(PEN_RECO_STATUS.NONE);
                    mHandler.removeMessages(MSG_TIME_OUT);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recoResultSB = new StringBuilder();
                            L.info(TAG, "file iHwRecognition:" + recognFile);
                            recoResultSB.append(recognFile.trim());
                            contentHwr = recoResultSB.toString();
                            mBinding.etHwrContent.setText(contentHwr);
                            if (recoResultSB.length() > 1)
                                recoResultSB.delete(0, recoResultSB.length() - 1);
                            setSwitchTextColor(R.id.tv_tran_original);
                        }
                    });

                    long lastTime = SpUtils.getLong(HwrRecognizeActivity.this, AppCommon.getCurPageKey("SP_LAST_RECOGNIZE_TIME"), 0);

                    if (null != pageStrokesCache) {
                        List<StrokesBean> strokesBeans = pageStrokesCache.getStrokesBeans();
                        if (null != strokesBeans && strokesBeans.size() > 0) {
                            StrokesBean strokesBean = strokesBeans.get(strokesBeans.size() - 1);
                            if (strokesBean.getCreateTime() > lastTime) {
                                if (QPenManager.getInstance().getHwrEngineEnum() == HwrEngineEnum.MS) {
                                    //微软识别
                                    recognizeStrokesByMSApi();
                                } else {
                                    //汉王识别
                                    recognizeStrokesByHanvon();
                                }
                                return;
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissDialog();
                                    }
                                });
                            }
                        }
                    }
                } else {
                    SpUtils.putLong(HwrRecognizeActivity.this, AppCommon.getCurPageKey("SP_LAST_RECOGNIZE_TIME"), 0);

                    if (QPenManager.getInstance().getHwrEngineEnum() == HwrEngineEnum.MS) {
                        //微软识别
                        recognizeStrokesByMSApi();
                    } else {
                        //汉王识别
                        recognizeStrokesByHanvon();
                    }
                }

            }
        }
    }


    /**
     * 识别笔画 通过微软接口
     */
    private void recognizeStrokesByMSApi() {
        boolean isReRecogine = false;
        //从保存的笔画文件中加载当前页的笔画
        ArrayList<MSBean.StrokesBean> list = new ArrayList<>();

        float x = ScreenUtils.getDisplayMetrics(this).widthPixels;
        float y = ScreenUtils.getDisplayMetrics(this).heightPixels;

        int w = Const.PageFormat.PAGE_A5.getWidth();
        int h = Const.PageFormat.PAGE_A5.getHeight();

        float scaleX = x / w;
        float scaleY = y / h;

        MSBean msBean = new MSBean();
        String recoLang = SpUtils.getString(this, Constant.SP_KEY_RECO_LANGUAGE, "");
        // 注意，MyScript的识别语种代码用的是下划线"_"，而微软的是横线"-"
        if (QPenManager.getInstance().getHwrEngineEnum() == HwrEngineEnum.MS) {
            recoLang = recoLang.replace("_", "-");
            if (TextUtils.isEmpty(recoLang)) {
                msBean.setLanguage("zh-CN");
            } else {
                msBean.setLanguage(recoLang);
            }
            msBean.setVersion(1);
        }

        PageStrokesCacheBean pageStrokesCache = StrokesUtilForQpen.getStrokes(new File(AppCommon.getStrokesPath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage())));
        if (null != pageStrokesCache) {
            List<StrokesBean> strokesBeans = pageStrokesCache.getStrokesBeans();
            if (null != strokesBeans && strokesBeans.size() > 0) {
                MSBean.StrokesBean strokesBean = null;
                StringBuffer sb = null;
                Point pointLast = null;
                StrokesBean strokesBean1 = null;
                long lastTime = SpUtils.getLong(this, AppCommon.getCurPageKey("SP_LAST_RECOGNIZE_TIME"), 0);
                if (lastTime == 0) {
                    isReRecogine = true;
                } else {
                    isReRecogine = false;
                }
                for (int i = 0; i < strokesBeans.size(); i++) {
                    if (list.size() >= 1000) {//微软手写识别接口请求对象中，list最大为1000
                        break;
                    }

                    strokesBean1 = strokesBeans.get(i);
                    if (lastTime >= strokesBean1.getCreateTime()) {
                        continue;
                    }

                    lastTime = strokesBean1.getCreateTime();
                    List<Point> dots = strokesBean1.getDots();

                    if (null != dots && dots.size() > 2) {
                        if (pointLast == null) {
                            pointLast = dots.get(0);
                            strokesBean = new MSBean.StrokesBean();
                            sb = new StringBuffer();
                            strokesBean.setId(i + 1);
                        } else {
                            if (Math.abs(pointLast.x - dots.get(0).x) > 400 ||
                                    Math.abs(pointLast.y - dots.get(0).y) > 400) {
                                if (list.size() > 800) {
                                    break;
                                }
                            }

                            if (Math.abs(pointLast.x - dots.get(0).x) > 50 ||
                                    Math.abs(pointLast.y - dots.get(0).y) > 50) {
                                //当和上一笔的落笔点相差比较大时，添加之前的数据，重新创捷一个对象；否则就直接添加到一起
                                if (!TextUtils.isEmpty(sb.toString())) {
                                    strokesBean.setPoints(sb.substring(0, sb.length() - 1));
                                    list.add(strokesBean);
                                }
                                pointLast = dots.get(0);
                                strokesBean = new MSBean.StrokesBean();
                                sb = new StringBuffer();
                                strokesBean.setId(i + 1);
                            }
                        }

                        for (Point point : dots) {
                            sb.append((float) point.x * scaleX)
                                    .append(",").append((float) point.y * scaleY).append(",");
                        }
                    }
                }
                if (sb != null && !TextUtils.isEmpty(sb.toString())) {
                    SpUtils.putLong(this, AppCommon.getCurPageKey("SP_LAST_RECOGNIZE_TIME"), lastTime);
                    strokesBean.setPoints(sb.substring(0, sb.length() - 1));
                    list.add(strokesBean);
                }
            }
            msBean.setStrokes(list);
        }

        if (list.size() > 0) {
            final boolean finalIsReRecogine = isReRecogine;
            HttpUtils.doPut(new Gson().toJson(msBean), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    dismissDialog();
                    try {
                        mHandler.removeMessages(MSG_TIME_OUT);
                        if (NetworkUtil.isNetWorkAvailable(HwrRecognizeActivity.this)) {
                            mHandler.sendEmptyMessage(MSG_TIME_OUT);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(R.string.network_error_tip);
                                }
                            });
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String string = response.body().string();
                    recoResultSB = new StringBuilder();
                    try {
//                MSResultBean msResultBean = new Gson().fromJson(string, MSResultBean.class);
                        MsHwrResultBean msResultBean = new Gson().fromJson(string, MsHwrResultBean.class);
                        if (msResultBean != null) {
                            List<MsHwrResultBean.RecognitionUnitsBean> recognitionUnits = msResultBean.getRecognitionUnits();

                            if (recognitionUnits != null) {
                                Collections.sort(recognitionUnits);
                                for (MsHwrResultBean.RecognitionUnitsBean unitsBean : recognitionUnits) {
                                    if (unitsBean.getCategory().equals("line")) {
                                        recoResultSB.append(unitsBean.getRecognizedText()).append("\n");
                                    }
                                }
                            }
                        }
                        AFPenClientCtrl.getInstance().setRecoStatus(PEN_RECO_STATUS.NONE);
                        if (mHandler != null) {
                            mHandler.removeMessages(MSG_TIME_OUT);
                        }
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finalIsReRecogine) {
                                    mBinding.etHwrContent.setText("");
                                }

                                String oldStr = mBinding.etHwrContent.getText().toString();
                                if (!TextUtils.isEmpty(recoResultSB)) {
                                    contentHwr = oldStr + "\n" + recoResultSB.toString();
                                    mBinding.etHwrContent.setText(contentHwr);
                                    if (recoResultSB.length() > 1)
                                        recoResultSB.delete(0, recoResultSB.length() - 1);
                                    setSwitchTextColor(R.id.tv_tran_original);
                                    IInkSdkManager.getInstance().saveRecogn(AppCommon.getHwrFilePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage()), contentHwr);
                                } else {
                                    if (TextUtils.isEmpty(oldStr)) {
                                        showToast(R.string.str_no_recogine);
                                    }
                                }
                                dismissDialog();
                            }
                        });
                        recognizeStrokesByMSApi();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            dismissDialog();
            AFPenClientCtrl.getInstance().setRecoStatus(PEN_RECO_STATUS.NONE);
            mHandler.removeMessages(MSG_TIME_OUT);
        }
    }

    /**
     * 识别笔画 通过汉王接口
     */
    private void recognizeStrokesByHanvon() {
        boolean isReRecogine = false;
        //从保存的笔画文件中加载当前页的笔画
        ArrayList<StrokesBean> list = new ArrayList<>();

        float x = ScreenUtils.getDisplayMetrics(this).widthPixels;
        float y = ScreenUtils.getDisplayMetrics(this).heightPixels;

        int w = Const.PageFormat.PAGE_A5.getWidth();
        int h = Const.PageFormat.PAGE_A5.getHeight();

        float scaleX = x / w;
        float scaleY = y / h;

//        HanvonRequest requestBean = new HanvonRequest();
        NingQuHWRRequest requestBean = new NingQuHWRRequest();
        String recoLang = SpUtils.getString(this, Constant.SP_KEY_RECO_LANGUAGE, "");

        //设置语种
        if ("zh_CN".equals(recoLang)) {
            requestBean.language = HW_LAN_CODE_CHNS;
        } else if ("CN_HK".equals(recoLang)) {
            requestBean.language = HW_LAN_CODE_CHNT;
        } else if ("ja_JP".equals(recoLang)) {
            requestBean.language = HW_LAN_CODE_JA;
        } else if ("en_US".equals(recoLang)) {
            requestBean.language = HW_LAN_CODE_EN;
        } else {
            requestBean.language = HW_LAN_CODE_CHNS;
        }

        //读取笔画数据
//        PageStrokesCacheBean pageStrokesCache = StrokesUtilForQpen.getStrokes(new File(Common.getStrokesPath(Common.getCurrentNotebookId(), Common.getCurrentPage())));

        if (null != pageStrokesCache) {

            List<StrokesBean> strokesBeans = pageStrokesCache.getStrokesBeans();

            if (null != strokesBeans && strokesBeans.size() > 0) {

                StringBuffer sb = new StringBuffer();
                Point lastUpPoint = null;//上一个抬笔点
                StrokesBean strokesBean1 = null;
                long lastTime = SpUtils.getLong(this, AppCommon.getCurPageKey("SP_LAST_RECOGNIZE_TIME"), 0);
                if (lastTime == 0) {
                    isReRecogine = true;
                } else {
                    isReRecogine = false;
                }
                for (int i = 0; i < strokesBeans.size(); i++) {
                    //遍历笔画
/*                    if(requestBean.lang.equals(HW_LAN_CODE_EN)){
                        if (list.size() >= 48) {
                            break;
                        }
                    } else {
                        if (list.size() >= 160) {
                            break;
                        }
                    }*/

                    strokesBean1 = strokesBeans.get(i);
                    if (lastTime >= strokesBean1.getCreateTime()) {
                        continue;
                    }

                    lastTime = strokesBean1.getCreateTime();
                    List<Point> dots = strokesBean1.getDots();

                    if (null != dots && dots.size() > 2) {

                        //当和上一笔的抬笔点相差比较大时，认为换行
                        if (null != lastUpPoint) {
                                /*Math.abs(pointLastUp.x - dots.get(0).x) > 50 ||
                                        Math.abs(pointLastUp.y - dots.get(0).y) > 50*/
                            float absX = Math.abs(dots.get(0).x - lastUpPoint.x);
                            float absY = Math.abs(dots.get(0).y - lastUpPoint.y);
                            double dis = Math.sqrt(Math.pow(absX, 2) + Math.pow(absY, 2));
                            if (dis > 500) {
                                //236=600dpi*0.3937inch=600dpi*1cm
                                if (!TextUtils.isEmpty(sb.toString())) {
                                    //结束当前笔画遍历，进行手写识别
                                    break;
                                }
                            }
                        }

                        Point lastPoint = null;

                        for (Point point : dots) {
                            //过滤无效点
                            if (null != lastPoint) {

                                if (Math.abs(lastPoint.x - dots.get(0).x) > 400 ||
                                        Math.abs(lastPoint.y - dots.get(0).y) > 400) {
                                    if (list.size() > 800) {
                                        continue;
                                    }
                                }
                            }
                            //遍历笔画的点
                            sb.append((float) point.x * scaleX)
                                    .append(",").append((float) point.y * scaleY).append(",");
                            lastPoint = point;
                        }

                        if (dots.size() > 2) {
                            lastUpPoint = dots.get(dots.size() - 1);
                        }

                        //拼接当前笔结束字符-1,0
                        sb.append("-1").append(",").append("0").append(",");
                        list.add(strokesBean1);
                    }
                }
                if (sb != null && !TextUtils.isEmpty(sb.toString())) {
                    SpUtils.putLong(this, AppCommon.getCurPageKey("SP_LAST_RECOGNIZE_TIME"), lastTime);

                    //拼接当前识别笔画结束字符-1,-1
                    sb.append("-1").append(",").append("-1");
                    requestBean.handLineData = sb.toString();
                }
            }
        }

        if (list.size() > 0) {

            final boolean finalIsReRecogine = isReRecogine;
            requestBean.key = HW_KEY;

            HttpUtils.doNingQuPost(new Gson().toJson(requestBean), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog();
                        }
                    });
                    try {
                        mHandler.removeMessages(MSG_TIME_OUT);
                        if (NetworkUtil.isNetWorkAvailable(HwrRecognizeActivity.this)) {
                            mHandler.sendEmptyMessage(MSG_TIME_OUT);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(R.string.network_error_tip);
                                }
                            });
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String string = response.body().string();
                    recoResultSB = new StringBuilder();
                    try {
//                        final String json = new String(Base64Utils.decode(string));

                        final HanvonResponse hwRsp = new Gson().fromJson(string, HanvonResponse.class);
                        if (hwRsp != null) {

                            if (hwRsp.code.equals("0") && !TextUtils.isEmpty(hwRsp.result)) {

                                String[] ucodes = hwRsp.result.split(",");

                                for (String ucode : ucodes) {
                                    if ("0".equals(ucode)) {
                                        break;
                                    }
                                    char ch = (char) Integer.valueOf(ucode).intValue();
                                    recoResultSB.append(ch);
                                }

                            } else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinding.etHwrContent.setText(string);
                                    }
                                });
                                mHandler.sendEmptyMessage(MSG_REQUEST_FAIL);
                                mHandler.removeMessages(MSG_TIME_OUT);
                                return;
                            }

                        }
                        AFPenClientCtrl.getInstance().setRecoStatus(PEN_RECO_STATUS.NONE);
                        if (mHandler != null) {
                            mHandler.removeMessages(MSG_TIME_OUT);
                        }
                        if (isFinishing() || isDestroyed()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissDialog();
                                }
                            });
                            return;
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String oldStr = mBinding.etHwrContent.getText().toString();
                                if (!TextUtils.isEmpty(recoResultSB)) {
                                    contentHwr = oldStr + "\n" + recoResultSB.toString();
                                    mBinding.etHwrContent.setText(contentHwr);
                                    if (recoResultSB.length() > 1)
                                        recoResultSB.delete(0, recoResultSB.length() - 1);
                                    setSwitchTextColor(R.id.tv_tran_original);
                                    IInkSdkManager.getInstance().saveRecogn(AppCommon.getHwrFilePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage()), contentHwr);
                                } else {
                                    if (TextUtils.isEmpty(oldStr)) {
                                        showToast(R.string.str_no_recogine);
                                    }
                                }
                                dismissDialog();
                            }
                        });

                        recognizeStrokesByHanvon();
                    } catch (Exception e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(MSG_REQUEST_FAIL);
                        mHandler.removeMessages(MSG_TIME_OUT);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissDialog();
                                mBinding.etHwrContent.setText(R.string.hwr_reco_fail);
                            }
                        });
                    }
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissDialog();
                }
            });
            AFPenClientCtrl.getInstance().setRecoStatus(PEN_RECO_STATUS.NONE);
            mHandler.removeMessages(MSG_TIME_OUT);
        }
    }

    /**
     * 识别笔画 通过汉王接口
     */
    private void recognizeStrokesByHanvonSDK() {
        boolean isReRecogine = false;
        //从保存的笔画文件中加载当前页的笔画
        ArrayList<StrokesBean> list = new ArrayList<>();

        float x = ScreenUtils.getDisplayMetrics(this).widthPixels;
        float y = ScreenUtils.getDisplayMetrics(this).heightPixels;

        int w = Const.PageFormat.PAGE_A5.getWidth();
        int h = Const.PageFormat.PAGE_A5.getHeight();

        float scaleX = x / w;
        float scaleY = y / h;

        HanvonRequest requestBean = new HanvonRequest();
        String recoLang = SpUtils.getString(this, Constant.SP_KEY_RECO_LANGUAGE, "");

        //设置语种
        if ("zh_CN".equals(recoLang)) {
            requestBean.lang = HW_LAN_CODE_CHNS;
        } else if ("CN_HK".equals(recoLang)) {
            requestBean.lang = HW_LAN_CODE_CHNT;
        } else if ("ja_JP".equals(recoLang)) {
            requestBean.lang = HW_LAN_CODE_JA;
        } else if ("en_US".equals(recoLang)) {
            requestBean.lang = HW_LAN_CODE_EN;
        } else {
            requestBean.lang = HW_LAN_CODE_CHNS;
        }

        //读取笔画数据
        PageStrokesCacheBean pageStrokesCache = StrokesUtilForQpen.getStrokes(new File(AppCommon.getStrokesPath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage())));

        if (null != pageStrokesCache) {

            final List<StrokesBean> strokesBeans = pageStrokesCache.getStrokesBeans();

            if (null != strokesBeans && strokesBeans.size() > 0) {
                StringBuffer sb = null;
                Point pointLast = null;
                StrokesBean strokesBean1 = null;
                long lastTime = SpUtils.getLong(this, AppCommon.getCurPageKey("SP_LAST_RECOGNIZE_TIME"), 0);
                if (lastTime == 0) {
                    isReRecogine = true;
                } else {
                    isReRecogine = false;
                }
                for (int i = 0; i < strokesBeans.size(); i++) {
                    //遍历笔画
                    if (list.size() >= 1000) {//微软手写识别接口请求对象中，list最大为1000
                        break;
                    }

                    strokesBean1 = strokesBeans.get(i);
                    if (lastTime >= strokesBean1.getCreateTime()) {
                        //上次识别最后一笔点时间戳前的不再识别
                        continue;
                    }

                    lastTime = strokesBean1.getCreateTime();
                    List<Point> dots = strokesBean1.getDots();

                    if (null != dots && dots.size() > 2) {
                        if (pointLast == null) {
                            pointLast = dots.get(0);
                            sb = new StringBuffer();
                        }

                        for (Point point : dots) {
                            //遍历笔画的点
                            sb.append((float) point.x * scaleX)
                                    .append(",").append((float) point.y * scaleY).append(",");
//                            sb.append(point.x)
//                                    .append(",").append(point.y ).append(",");
                        }

                        sb.append("-1").append(",").append("0").append(",");
                    }
                }
                if (sb != null && !TextUtils.isEmpty(sb.toString())) {
                    SpUtils.putLong(this, AppCommon.getCurPageKey("SP_LAST_RECOGNIZE_TIME"), lastTime);
                    sb.append("-1").append(",").append("-1");
                    list.add(strokesBean1);

                    requestBean.data = sb.toString();
                }
            }

            final int listSize = list.size();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("strokesBeans.size=" + strokesBeans.size() + ", list.size=" + listSize);
                }
            });

        }


        if (list.size() > 0) {

            final boolean finalIsReRecogine = isReRecogine;

            // handRepeatLanguage4Https(String language, String handLineData) 叠写
            // hwCloudManagerHandSingle

            final String content = "";//hwCloudManagerHandSingle.handRepeatLanguage4Https(requestBean.lang, requestBean.data);
//            final String content = hwCloudManagerHandSingle.handLineLanguage4Https(requestBean.lang, requestBean.data);

            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (finalIsReRecogine) {
                        mBinding.etHwrContent.setText(content);
                    }
                    dismissDialog();
                }
            });*/

            final HanvonResponse hwRsp = new Gson().fromJson(content, HanvonResponse.class);
            if (hwRsp != null) {

                if (hwRsp.code.equals("0") && !TextUtils.isEmpty(hwRsp.result)) {

                    String[] ucodes = hwRsp.result.split(",");

                    for (String ucode : ucodes) {
//                        if("0".equals(ucode)){
//                            break;
//                        }
                        char ch = (char) Integer.valueOf(ucode).intValue();
                        recoResultSB.append(ch);
                    }

                } else {
                    SpUtils.putLong(this, AppCommon.getCurPageKey("SP_LAST_RECOGNIZE_TIME"), 0);
                    mHandler.sendEmptyMessage(MSG_REQUEST_FAIL);
                    mHandler.removeMessages(MSG_TIME_OUT);
                    return;
                }

            }
            AFPenClientCtrl.getInstance().setRecoStatus(PEN_RECO_STATUS.NONE);
            mHandler.removeMessages(MSG_TIME_OUT);
            if (isFinishing() || isDestroyed()) {
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String oldStr = mBinding.etHwrContent.getText().toString();
                    if (!TextUtils.isEmpty(recoResultSB)) {
                        contentHwr = oldStr + "\n" + recoResultSB.toString();
                        mBinding.etHwrContent.setText(contentHwr);
                        if (recoResultSB.length() > 1)
                            recoResultSB.delete(0, recoResultSB.length() - 1);
                        setSwitchTextColor(R.id.tv_tran_original);
                        IInkSdkManager.getInstance().saveRecogn(AppCommon.getHwrFilePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage()), contentHwr);
                    } else {
                        if (TextUtils.isEmpty(oldStr)) {
                            showToast(R.string.str_no_recogine);
                        }
                    }
                    dismissDialog();
                }
            });

            recognizeStrokesByHanvonSDK();
        } else {
            dismissDialog();
            AFPenClientCtrl.getInstance().setRecoStatus(PEN_RECO_STATUS.NONE);
            mHandler.removeMessages(MSG_TIME_OUT);
        }
    }

    /**
     * MyScript 手写识别回调
     */
    RecognizeCallback recognizeCallback = new RecognizeCallback() {
        @Override
        public void getResult(final String result) {
            dismissDialog();
            NingQuLog.error("myscript in Activity:", "getResult1"+result);
            if (isFinishing()){
                return;
            }
            setResult(RESULT_OK);
            AFPenClientCtrl.getInstance().setRecoStatus(PEN_RECO_STATUS.NONE);
            if (mHandler != null) {
                mHandler.removeMessages(MSG_TIME_OUT);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    L.info(TAG, "back iHwRecognition:" + result);
                    if (!TextUtils.isEmpty(result)) {
                        mBinding.etHwrContent.setText(result);
//                        recoResultSB.delete(0, recoResultSB.length() - 1);
                    }
                    setSwitchTextColor(R.id.tv_tran_original);
                    dismissDialog();
                }
            });
            IInkSdkManager.getInstance().saveRecogn(AppCommon.getHwrFilePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage()), contentHwr);
        }

        @Override
        public void onError(String error) {
            dismissDialog();
            NingQuLog.error("myscript in Activity:", "getResult2"+error);
            if (isFinishing()){
                return;
            }
            setResult(RESULT_OK);
            AFPenClientCtrl.getInstance().setRecoStatus(PEN_RECO_STATUS.NONE);
            if (mHandler != null) {
                mHandler.removeMessages(MSG_TIME_OUT);
            }
            contentHwr = "";

            if (error.startsWith("1") && "MY_SCRIPT".equals(HwrEngineEnum.MY_SCRIPT.toString())) {
                dialog = DialogHelper.showErrorMessage(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        dismissDialog();
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                    }
                }, getString(R.string.hwr_reco_fail) + "(" + error + ",mac=" + AFPenClientCtrl.getInstance().getLastTryConnectAddr() + ")");
            } else if (!"MY_SCRIPT".equals(HwrEngineEnum.MY_SCRIPT.toString())){
                dialog = DialogHelper.showErrorMessage(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        dismissDialog();
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                    }
                }, getString(R.string.hwr_reco_fail) + "(" + error + ",mac=" + AFPenClientCtrl.getInstance().getLastTryConnectAddr() + ")");
            }
            if (error.contains("IO_FAILURE: error: AddResource")) {//识别资源出错，删除错误资源，改为识别语种为默认的英语，如需要再次进入资源界面下载
                String lang = SpUtils.getString(HwrRecognizeActivity.this, Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);
                File file = new File(com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS + "/conf", lang + ".conf");
                FileUtils.delete(file);
                File file1 = new File(com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS + "/resources/" + lang);
                FileUtils.delete(file1);

                SpUtils.putString(HwrRecognizeActivity.this, Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);
                tranSrcLanguage = 9;
                SpUtils.putInt(HwrRecognizeActivity.this, Constant.SP_KEY_LANGUAGE, tranSrcLanguage);
                IInkSdkManager.getInstance().saveRecogn(AppCommon.getHwrFilePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage()), "");
            }
        }
    };


    /**
     * 选择要翻译的目标语言
     */
    private void toSelectLanguage() {

        int position = 0;
        for (LanguageBean languageBean : mTranLangList) {
            if (languageBean.getCode() == tranDesLanguage) {
                break;
            }
            position++;
        }

        dismissDialog();
        dialog = DialogHelper.showListViewDialog(getSupportFragmentManager(), R.string.select_lang_title,
                new CommonAdapter<LanguageBean>(this, R.layout.item_select_language, mTranLangList) {
                    @Override
                    protected void convert(ViewHolder holder, LanguageBean languageBean, final int position) {
                        final LanguageBean bean = mTranLangList.get(position);
                        holder.setText(R.id.item_tv_language_name, getString(SystemUtil.getResId(HwrRecognizeActivity.this, bean.getName())));
                        //                holder.setText(R.id.item_tv_language_name_0, bean.getName0());
                        TextView textView = holder.getView(R.id.item_tv_language_name);
                        if (languageBean.getCode() == tranDesLanguage) {
                            Drawable drawable = getResources().getDrawable(R.drawable.selected);
                            textView.setCompoundDrawablesWithIntrinsicBounds(null,
                                    null, drawable, null);
                            textView.setCompoundDrawablePadding(4);
                        } else {
                            textView.setCompoundDrawablesWithIntrinsicBounds(null,
                                    null, null, null);
                            textView.setCompoundDrawablePadding(4);
                        }

                        holder.setImageDrawable(R.id.item_ic_language, bean.getFlag());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (recoResultSB.length() > 0) {

                                    recoResultSB.delete(0, recoResultSB.length() - 1);
                                }
                                tranDesLanguage = bean.getCode();
//                                mBinding.ivHwrTran.setBackground(bean.getFlag());
                                SpUtils.putInt(HwrRecognizeActivity.this, Constant.SP_KEY_TO_LANGUAGE, bean.getCode());
                                dismissDialog();
                                if (NetworkUtil.isNetWorkAvailable(HwrRecognizeActivity.this)) {
                                    String okMac = SpUtils.getString(HwrRecognizeActivity.this, Constant.SP_KEY_AUTH_PEN);
                                    if (!TextUtils.isEmpty(okMac)) {
                                        toTranslate(okMac);
                                    } else {
                                        toAuth();
                                    }
                                } else {
                                    showToast(R.string.network_error_tip);
                                }
                            }
                        });
                    }
                }, position);
    }

    /**
     * 弹出选择识别语言的对话框
     */
    private void toSelectRecoLanguage() {
/*        List<File> files = IInkSdkManager.getInstance().getLanguages(this.getApplicationContext());
        if(null!=files){
            for(File file:files){
                String name = file.getName();
                if(name.contains("en_US")||name.contains("zh_CN")||name.contains("ja_JP")){
                    mRecoLangList.add(name.replace(".conf","").replace("-", "_"));
                }
            }
        }*/
        int anInt = SpUtils.getInt(mContext, Constant.SP_KEY_INIT_PEN, 0);
        if (anInt != 1){
            ToastUtils.showShort(R.string.server_status_tips5);
            return;
        }
        startActivityForResult(new Intent(this, RecogLanguageActivity.class), REQUEST_CODE_SEL_LANG);
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

    /**
     * 去获取设备授权
     */
    private void toAuth() {
        clkAction = true;
        int initState = SpUtils.getInt(this, Constant.SP_KEY_INIT_PEN, -1);

        String okMac = SpUtils.getString(this, Constant.SP_KEY_AUTH_PEN);
        if (TextUtils.isEmpty(okMac)) {
            dismissDialog();
            if (-2 == initState) {
                //授权失败
                showToast(getResources().getString(R.string.server_status_tips5));
            } else {
                dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        dismissDialog();
                        gotoActivity(DeviceLinkGuideActivity.class);
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                    }
                }, R.string.str_linked_title, R.string.str_linked_content, R.string.dialog_confirm_text, R.string.dialog_cancel_text);
            }

        } else {
            QPenManager.getInstance().toAuth();
        }
    }

    /**
     * 翻译内容
     *
     * @param mac
     */
    private void toTranslate(final String mac) {

        final String strHwr = mBinding.etHwrContent.getText().toString();
        if (TextUtils.isEmpty(strHwr)) {
            showToast(R.string.empty);
            return;
        }
        L.error("tranSrcLanguage = " + tranSrcLanguage + " ; tranDesLanguage = " + tranDesLanguage);

        if (tranSrcLanguage == 0) {
            toSelectRecoLanguage();
            return;
        }

        if (tranDesLanguage == 0) {
            toSelectLanguage();
            return;
        }

        dismissDialog();
        dialog = DialogHelper.showProgress(getSupportFragmentManager(), getString(R.string.str_translateing), false);
        String[] split = mac.split("-");
        String ss = mac;
        if (split.length > 1) {
            ss = split[1];
        }

        String APP_KEY_AI = "";
        try {
            ApplicationInfo ai = getApplicationContext().getPackageManager().getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai.metaData) {
                APP_KEY_AI = ai.metaData.getString("NQ_APPSIGN");
            }
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }

        NQSpeechUtils.getTrans(strHwr, tranSrcLanguage + "", tranDesLanguage + "", ss, APP_KEY_AI, "1", new NQSpeechUtils.ITransCallback() {
            @Override
            public void onSuccess(final String s) {
                dismissDialog();
                //                L.debug(s);
                Gson gson = new Gson();
                try {
                    final TransResultBean resultBean = gson.fromJson(s, TransResultBean.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setSwitchTextColor(R.id.tv_tran_translation);
                            if (resultBean.isSuccess()) {
                                contentTran = resultBean.getData();
                                mBinding.etHwrContentTran.setText(contentTran);
                            } else {
                                contentTran = "";
//                                mBinding.etHwrContentTran.setText(getString(R.string.str_network_error)
//                                        + " (" + resultBean.getCode() + " mac=" + mac + ")");
                                showToast(R.string.str_no_trans);
                            }
                        }
                    });
                } catch (Exception e) {
                    L.error(e.getMessage());
                }
            }

            @Override
            public void onFailed(final String s, final int i, final String s3) {
                dismissDialog();
                L.error(s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSwitchTextColor(R.id.tv_tran_original);
                        dismissDialog();
                        dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                            @Override
                            public void confirm(View view) {
                                dismissDialog();
                                toTranslate(mac);
                            }

                            @Override
                            public void cancel() {
                                dismissDialog();
                            }
                        }, R.string.str_trans_timeout, R.string.str_try_again, R.string.title_retry, R.string.dialog_cancel_text);
                    }
                });
            }
        });
    }


    // 普通事件的处理
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void handleEvent(final EventBusCarrier carrier) {
        if (null == carrier) {
            return;
        }

        switch (carrier.getEventType()) {
            case Constant.OPEN_NOTEBOOK_CODE:
                finish();
                break;
            case Constant.GET_RECOGNIZE_RESULT:
                dismissDialog();
                String text = (String) carrier.getObject();
                if (!TextUtils.isEmpty(text)) {
                    text = text.replaceAll("\u00AD", "\n");
                    mBinding.etHwrContent.setText(text);
                }
                break;

            case Constant.NQ_SER_AUTH_SUCCESS:
                //AuthBaseBean
                String mac = "";
                if (null != carrier.getObject()) {
                    mac = (String) carrier.getObject();
                }
/*                if (!TextUtils.isEmpty(mac) && clkAction) {
                    toTranslate(mac);
                } else {
                    L.error("mac is null");
                }*/
                clkAction = false;
                toInitHwrSdk();
                break;
            case Constant.NQ_SER_AUTH_FAIL:
                //AuthBaseBean
                clkAction = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissDialog();
                        String msg = "";
                        if (null != carrier.getObject()) {
                            msg = (String) carrier.getObject();
                        }
                        dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                            @Override
                            public void confirm(View view) {
                                dismissDialog();
                            }

                            @Override
                            public void cancel() {
                                dismissDialog();
                            }
                        }, R.string.authorize_fail, getString(R.string.authorize_fail) + "\n " + msg, R.string.title_retry, R.string.dialog_cancel_text, true);
                    }
                });
                break;
        }
    }

    /**
     * 去初始化手写识别SDK
     */
    private void toInitHwrSdk() {
        //init IInkSdk
        if (QPenManager.getInstance().getHwrEngineEnum() == HwrEngineEnum.MY_SCRIPT) {
            //如果是MyScript手写识别则去初始化
            if (!IInkSdkManager.getInstance().isInitSuccess()) {
                IInkSdkManager.getInstance().init(getApplicationContext(), new IInkSdkManager.IInkSdkInitCallback() {
                    @Override
                    public void onSuccess() {
                        toRecoHwr();
                    }

                    @Override
                    public void onFailure() {
                        dismissDialog();
                    }
                }, RecognizeCommon.getAppName());
            } else {
                toRecoHwr();
            }
        } else if (QPenManager.getInstance().getHwrEngineEnum() == HwrEngineEnum.HANVON) {
            //如果手写识别是汉王，则初始化
            try {
                //sdk 需要read_phone_state权限
//                hwCloudManagerHandSingle = new HWCloudManager(this, HW_KEY);
            } catch (Exception e) {
                LogUtils.e("init hanvon hwr exception");
            }
            toRecoHwr();
        } else {
            //微软识别，已停用
            QPenManager.getInstance().setHwrEngineEnum(HwrEngineEnum.MS);
            toRecoHwr();
        }
    }

    /**
     * 去识别手写内容
     */
    private void toRecoHwr() {
        String recoLang = SpUtils.getString(this, Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);
        if (TextUtils.isEmpty(recoLang)) {
            //如果没有选择默认的识别语种则弹出列表
            toSelectRecoLanguage();
        } else {
            if (QPenManager.getInstance().getHwrEngineEnum() == HwrEngineEnum.MY_SCRIPT) {
                if (IInkSdkManager.getInstance().isInitSuccess()) {
                    //判断当前识别语种是否有资源，如果没有则跳到语音列表界面
                    String filePath = com.myscript.iink.eningqu.AppCommon.NQ_SAVE_SDCARD_PATH_ASSETS + "/conf/" + recoLang + ".conf";
                    if (FileUtils.isFileExists(filePath)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissDialog();
                                dialog = DialogHelper.showProgress(getSupportFragmentManager(), R.string.processing_hw_recognition, true);
                            }
                        });
                        IInkSdkManager.getInstance().editorClean();
                        IInkSdkManager.getInstance().setLanguage(this, recoLang);
                        dismissDialog();
                        mHandler.sendEmptyMessageDelayed(MSG_SHOW_DIALOG, 100);
                    } else {
                        ToastUtils.showShort(R.string.recognize_undownload);
                        toSelectRecoLanguage();
                    }
                } else {
                    toInitHwrSdk();
                }
            } else {
                if (NetworkUtil.isNetWorkAvailable(this)) {
                    dismissDialog();
                    mHandler.sendEmptyMessageDelayed(MSG_SHOW_DIALOG, 100);
                } else {
                    showToast(R.string.network_error_tip);
                }
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEL_LANG) {
            if (resultCode == 1) {
                if (data != null) {
                    tranSrcLanguage = data.getIntExtra("tranLanguage", 0);
                    String lang = SpUtils.getString(this, Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);
                    SpUtils.putInt(this, Constant.SP_KEY_LANGUAGE, tranSrcLanguage);
                    IInkSdkManager.getInstance().saveRecogn(AppCommon.getHwrFilePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage()), "");
                    SpUtils.putLong(this, AppCommon.getCurPageKey("SP_LAST_RECOGNIZE_TIME"), 0);
                    if (null != recoResultSB) {
                        recoResultSB.delete(0, recoResultSB.length());
                    }
                    contentHwr = "";
                    mBinding.etHwrContent.setText("");
                    RecognizeDBmanager.getInstance().deleteRecognizeDataByPage(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
                    RecognizeDBmanager.getInstance().deleteRecognizeListByPage(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
                    IInkSdkManager.getInstance().setLanguage(this, lang);
                    toRecoHwr();
                }
            } else if (resultCode == 2) {
                tranSrcLanguage = 9;
                SpUtils.putInt(this, Constant.SP_KEY_LANGUAGE, tranSrcLanguage);
                IInkSdkManager.getInstance().saveRecogn(AppCommon.getHwrFilePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage()), "");
            }
        }
    }
}
