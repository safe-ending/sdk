package com.eningqu.aipen.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;

import com.eningqu.aipen.bean.caiyun.AASLoginRsp;
import com.eningqu.aipen.bean.caiyun.GetVeriCodeRsp;
import com.eningqu.aipen.bean.caiyun.LoginCallBack;
import com.eningqu.aipen.bean.caiyun.NoteBean;
import com.eningqu.aipen.common.MCloudConf;
import com.eningqu.aipen.common.NetCommon;
import com.eningqu.aipen.common.dialog.listener.LoginmCloudListener;
import com.eningqu.aipen.common.utils.MCloudAESUtil;
import com.eningqu.aipen.common.utils.DeviceInfoUtil;
import com.eningqu.aipen.common.utils.GeneratorUtil;
import com.eningqu.aipen.common.utils.NingQuLog;
import com.eningqu.aipen.common.utils.RegexUtils;
import com.eningqu.aipen.common.utils.StartAppUtil;
import com.eningqu.aipen.common.utils.xml.Xml2Obj;
import com.eningqu.aipen.db.model.AASUserInfoData;
import com.eningqu.aipen.db.model.OseUserInfoData;
import com.eningqu.aipen.db.model.RecognizeBean;
import com.eningqu.aipen.db.model.RecognizeData;
import com.eningqu.aipen.myscript.RecognizeCallback;
import com.eningqu.aipen.myscript.RecognizeCommon;
import com.eningqu.aipen.myscript.RecognizeDBmanager;
import com.eningqu.aipen.qpen.SignatureView;
import com.eningqu.aipen.qpen.bean.PageStrokesCacheBean;
import com.eningqu.aipen.qpen.bean.StrokesBean;
import com.eningqu.aipen.sdk.bean.NQDot;
import com.eningqu.aipen.sdk.bean.DotType;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.StringUtils;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.SmartPenApp;
import com.eningqu.aipen.activity.DrawPageActivity;
import com.eningqu.aipen.activity.FragmentBaseActivity;
import com.eningqu.aipen.activity.HwrRecognizeActivity;
import com.eningqu.aipen.activity.LabelEditActivity;
import com.eningqu.aipen.activity.RecordListActivity;
import com.eningqu.aipen.adapter.ColorSelectorAdapter;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.CanvasFrame;
import com.eningqu.aipen.qpen.GestureListener;
import com.eningqu.aipen.qpen.IPageDrawView;
import com.eningqu.aipen.qpen.PAGE_OPEN_STATUS;
import com.eningqu.aipen.qpen.PEN_SYNC_STATUS;
import com.eningqu.aipen.qpen.SDKUtil;
import com.eningqu.aipen.qpen.StrokesUtilForQpen;
import com.eningqu.aipen.qpen.TouchListener;
import com.eningqu.aipen.qpen.bean.AFStrokeAndPaint;
import com.eningqu.aipen.qpen.bean.CommandBase;
import com.eningqu.aipen.qpen.bean.CommandColor;
import com.eningqu.aipen.qpen.bean.CommandRecord;
import com.eningqu.aipen.qpen.bean.CommandSize;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.dialog.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.dialog.listener.SelectFileFormatListener;
import com.eningqu.aipen.common.dialog.listener.ShareListener;
import com.eningqu.aipen.common.thread.ThreadPoolUtils;
import com.eningqu.aipen.common.utils.AppInfoUtil;
import com.eningqu.aipen.common.utils.AudioUtil;
import com.eningqu.aipen.common.utils.BitmapUtil;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.common.utils.FileUtils;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.PdfUtil;
import com.eningqu.aipen.common.utils.ScreenUtils;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.common.utils.TimeUtil;
import com.eningqu.aipen.databinding.FragmentPageDrawBinding;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.db.model.PageData;
import com.eningqu.aipen.db.model.UserInfoData;
import com.eningqu.aipen.qpen.listener.IQPenOnActivityResult;
import com.eningqu.aipen.qpen.QPenManager;
import com.eningqu.aipen.sdk.comm.utils.BytesConvert;
import com.eningqu.aipen.view.CustomPopWindow;
import com.myscript.iink.eningqu.IInkSdkManager;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.whatsapp.WhatsApp;
import io.reactivex.functions.Consumer;

import static com.eningqu.aipen.base.ui.BaseActivity.NOTEBOOK_ID;
import static com.eningqu.aipen.base.ui.BaseActivity.PAGE_NUM;

import cn.sharesdk.system.email.Email;
import nq.com.ahlibrary.utils.MD5;

/**
 * @Author: Qiu.Li
 * @Create Date: 2019/8/6 17:47
 * @Description: 画图界面
 * @Email: liqiupost@163.com
 */
public class PageDrawFragment extends DrawBaseFragment implements PopupMenu.OnMenuItemClickListener {

    public final static String TAG = PageDrawFragment.class.getSimpleName();
    private final static int REQUEST_CODE_EDIT_LABEL = 2;

    private boolean isFirst = true;//首次进入
    private boolean isShowTag = false;
    private boolean isStopRecord = true;

    private String mLabel;
    private long curSeconds = 0;
    protected SignatureView mStrokeView;
    protected CanvasFrame mCanvasFrame;
    protected CustomPopWindow mCustomPopWindow;
    private RxPermissions rxPermission;
    protected List<String> files;
    protected String curRecordFileName;
    private boolean isShowBottomMenu = true;

    int curPageNum = 1;
    String bookId = "";

    SmartPenApp app;

    private UserInfoData userInfo;

    FragmentPageDrawBinding mBinding;

    private ColorSelectorAdapter colorSelectorAdapter;
    private boolean isOver;
    //    private String recognFile;
    private boolean isNeedInitHwr = true;
    private UIHandler mHandler;

    private String mVeriCode;
    /**
     * 和彩云登录用户信息
     */
    private AASUserInfoData mCloudUserInfoData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirst = true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_page_draw;
    }

    @Override
    protected void dataBindingLayout(ViewDataBinding viewDataBinding) {
        mBinding = (FragmentPageDrawBinding) viewDataBinding;
    }

    @Override
    protected void initData() {
        app = SmartPenApp.getApp();
        userInfo = AppCommon.loadUserInfo();
        mHandler = new UIHandler(this);

        rxPermission = new RxPermissions(getActivity());

        Bundle bundle = getArguments();
        initNotebookData(bundle);

        final List<PageData> pageDatas = AppCommon.loadPageDataList(AppCommon.getCurrentNotebookId(), false);
        if (AppCommon.getCurrentPage() == -1 && null != pageDatas && pageDatas.size() > 0) {
            L.info(TAG, "loadPage() pageDatas size=" + pageDatas.size());
            for (int i = 0; i < pageDatas.size(); i++) {
                if (pageDatas.get(i).pageNum != -1) {
                    AppCommon.setCurrentPage(pageDatas.get(i).pageNum);
                    break;
                }
            }
        }

    }

    public RecognizeCallback recognizeCallback = new RecognizeCallback() {
        @Override
        public void getResult(String result) {
            isRecognizeNow = false;
            NingQuLog.error("myscript in pageDrawwFragment:", "getResult4" + result);

        }

        @Override
        public void onError(String error) {
            isRecognizeNow = false;
            NingQuLog.error("myscript in pageDrawwFragment:", "getResult3" + error);
        }
    };


    /**
     * 更新标题和页码
     *
     * @param bookId
     * @param page
     */
    private void updateTopTitleAndPageLabel(String bookId, int page) {
        //更新标题
        PageData pageData = AppCommon.loadPageData(bookId, page);
        if (null != pageData) {
            mLabel = pageData.name;
            if (TextUtils.isEmpty(mLabel)) {
                mLabel = getString(R.string.label_text) + AppCommon.getCurrentPage();
            }
            updateTopToolbar(mLabel);
        } else {
            mLabel = getString(R.string.label_text) + AppCommon.getCurrentPage();
            updateTopToolbar(mLabel);
        }
        if (page != 261 && page != 262 && page != 263 && page != 264) {
            //更新页码
            mBinding.tvPageNum.setText("" + page);
        } else {
            mBinding.tvPageNum.setText(getString(R.string.test_page));
        }
    }

    /**
     * 根据bundle的数据初始化笔记本信息和页码信息
     *
     * @param bundle
     */
    private void initNotebookData(Bundle bundle) {
        if (bundle != null) {
            curPageNum = bundle.getInt(PAGE_NUM, 1);
            bookId = bundle.getString(NOTEBOOK_ID);
        }

        NoteBookData curNoteBookData = AppCommon.selectNotebook(bookId);
        AppCommon.setCurrentPage(curPageNum);
        AppCommon.setCurrentNotebookId(bookId);
        AppCommon.setCurrentNoteBookData(curNoteBookData);
    }

    @Override
    protected void initView() {
        //初始化画板
        initDrawBroad();
        //更新标题和页码
        updateTopTitleAndPageLabel(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
        //初始化颜色选择器
        initColorSelectorView();
        isHidden = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        L.info(TAG, "onResume isHidden=" + isHidden);
        if (!isHidden) {
            AppCommon.setDrawOpenState(PAGE_OPEN_STATUS.OPEN);
            updateWhenSHow();
            if (isNeedInitHwr) {
                String okMac = SpUtils.getString(getActivity(), Constant.SP_KEY_AUTH_PEN);
                if (!TextUtils.isEmpty(okMac)) {
                    isNeedInitHwr = false;
                }
            }
        } else {
            AppCommon.setDrawOpenState(PAGE_OPEN_STATUS.CLOSE);
            AppCommon.setDrawOpenState(PAGE_OPEN_STATUS.CLOSE);
        }
    }

    /**
     * 页码切换
     */
    @Override
    protected void onChangePage() {
        //通过书写获取到页码时，需要更新一下bundle的页码值
        Bundle bundle = getArguments();
        bundle.putInt(PAGE_NUM, AppCommon.getCurrentPage());
        initNotebookData(bundle);
        //更新标题和页码
        updateTopTitleAndPageLabel(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
        //更新录音文件数量提示
        updateRecordsTips();
        if(null!=getHostActivity()){
            getHostActivity().toShowPage(AppCommon.getCurrentPage(), true);
        }
    }

    @Override
    protected void onError(int error) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        L.info(TAG, "onHiddenChanged hidden=" + hidden);
        isHidden = hidden;
        if (isHidden) {
            if (null != mStrokeView) {
                mStrokeView.clearPaint();
            }
            AppCommon.setDrawOpenState(PAGE_OPEN_STATUS.CLOSE);
        } else {
            updateWhenSHow();
            AppCommon.setDrawOpenState(PAGE_OPEN_STATUS.OPEN);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Bundle bundle = getArguments();
        bundle.putInt(PAGE_NUM, AppCommon.getCurrentPage());

        AFPenClientCtrl.getInstance().setDrawNow(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mCustomPopWindow != null)
            mCustomPopWindow = null;
        if (null != mStrokeView) {
            mStrokeView.setDrawing(false);
            mStrokeView.removeCallback();
            mStrokeView = null;
        }
        mBinding.llDrawBoard.removeView(mCanvasFrame);
        mCanvasFrame.bDrawl = null;
        mCanvasFrame = null;
        mBinding = null;
        colorSelectorAdapter = null;
        recordListener = null;
        app = null;
        userInfo = null;
        secondLeft = 0;
        timer.cancel();
        //停止获取缓存的点
        //        AFPenClientCtrl.getInstance().setPollSwitchStatus(false);
        //设置笔记本关闭状态
        //        AppCommon.setDrawOpenState(PAGE_OPEN_STATUS.CLOSE);
        AppCommon.getCurPageStrokesCache().getStrokesBeans().clear();
        //        List<NQDot> list = AFPenClientCtrl.getInstance().getCacheDots();
        //        if (null != list) {//清理上一页的缓存点
        //            list.clear();
        //        }

        AppCommon.setCurrentPage(-1);
        //        unregisterReceiver(receiver);
        if (AudioUtil.getInstance().getRecStatus() == AudioUtil.REC_STATUS.STATUS_START
                || AudioUtil.getInstance().getRecStatus() == AudioUtil.REC_STATUS.STATUS_PAUSE) {
            CommandRecord command = new CommandRecord(CommandRecord.RECORD_STOP);
            QPenManager.getInstance().onCommand(command);
            AudioUtil.getInstance().stopRecord();
        }
    }

    private void updateWhenSHow() {
        Bundle bundle = getArguments();
        initNotebookData(bundle);

        //根据录音状态显示图标
        onRecordStatus(AudioUtil.getInstance().getRecStatus());
        //根据笔的颜色大小显示图标
        updatePenSizeColorStatus();
        AudioUtil.getInstance().setRecordListener(recordListener);
        //更新录音文件数量提示
        updateRecordsTips();
        //更新标题和页码
        updateTopTitleAndPageLabel(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
        if(null!=getHostActivity()){

            getHostActivity().setQPenOnActivityResult(new IQPenOnActivityResult() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, Intent data) {

                }
            });
        }

        ThreadPoolUtils.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //延时加载当前页内容
                if (null != mStrokeView && !isFirst) {
                    loadPage();
                }
            }
        });
    }

    private DrawPageActivity getHostActivity() {
        if (null == getActivity()) {
            return null;//throw new NullPointerException("get MainActivity is null");
        }
        return ((DrawPageActivity) getActivity());
    }

    private void updateTopToolbar(String name) {
        if(null!=getHostActivity()){

            getHostActivity().updateTopToolbar(name);
        }
    }

    private void setSignatureView(SignatureView signatureView) {
        if(null!=getHostActivity()){

            getHostActivity().setSignatureView(signatureView);
        }
    }

    private void showPage(final int pageNum, final boolean clean) {
        if(null!=getHostActivity()){

            getHostActivity().toShowPage(pageNum, clean);
        }
    }

    private void switchPage(final int pageNum) {
        ThreadPoolUtils.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if(null!=getHostActivity()){

                    getHostActivity().toSwitchPage(pageNum);
                }
                mLabel = "";
                L.info("left change page " + AppCommon.getCurrentPage());

                if(null!=getHostActivity()){

                    getHostActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //更新标题和页码
                            updateTopTitleAndPageLabel(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
                            //更新录音文件数量提示
                            updateRecordsTips();
                            setIInkSDK();
                            L.info("updateRecordsTips end  ");
                        }
                    });
                }
            }
        });
    }

    private void setIInkSDK() {
        try {
            /*String recoLang = SpUtils.getString(getActivity(), Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);
            IInkSdkManager.getInstance().editorClean();
            IInkSdkManager.getInstance().setPackageName(AppCommon.getCurrentNotebookId() + "_" + AppCommon.getCurrentPage() + ".iink");
            IInkSdkManager.getInstance().setLanguage(getActivity(), recoLang);*/
            L.error("switch page to " + AppCommon.getCurrentPage() + " editor clean ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save(final int pageNum) {
        if(null!=getHostActivity()){

            getHostActivity().toSave(pageNum);
        }
    }

    private void showTopToolbar(int visibility) {
        if(null!=getHostActivity()){

            getHostActivity().showTopToolbar(visibility);
        }
    }

    /**
     * 初始化画板
     */
    private void initDrawBroad() {
        mBinding.llDrawBoard.post(new Runnable() {
            public void run() {
                L.info(TAG, "initDrawBroad()");
                if (null == mCanvasFrame) {
                    mCanvasFrame = new CanvasFrame(getContext());
                }
                mStrokeView = mCanvasFrame.bDrawl;
                if (null != mStrokeView) {
                    setSignatureView(mStrokeView);
                    mStrokeView.setPageDrawView(iPageDrawView);
                    // mStrokeView.getHolder().setFormat(PixelFormat.TRANSPARENT);//设置背景透明
                    mStrokeView.setZOrderOnTop(true); // 在最顶层，会遮挡一切view
                    mStrokeView.setZOrderMediaOverlay(true);// 如已绘制SurfaceView则在surfaceView上一层绘制。
                    int defaultBg = AppCommon.getCurrentNoteBookBG();
                    setPageBackground(defaultBg);
                }
                if (null != userInfo) {
                    int leavel = SpUtils.getInt(getContext(), AppCommon.getUserUID() + "_" + "size", 1);
                    int color = SpUtils.getInt(getContext(), AppCommon.getUserUID() + "_" + "color", ContextCompat.getColor(app, R.color.colors_menu_black));
                    QPenManager.getInstance().setPaintColor(color);
                    QPenManager.getInstance().setPenSizeType(leavel);
                    QPenManager.getInstance().setPaintSize(CommandSize.getSizeByLevel(leavel));
                    mStrokeView.setPenColor(QPenManager.getInstance().getPaintCacheColor());
                    mStrokeView.setPenSize(CommandSize.getSizeByLevel(leavel));
                }

                ViewGroup.LayoutParams layoutParams = mStrokeView.getLayoutParams();
                layoutParams.width = ScreenUtils.getDisplayMetrics(getHostActivity()).widthPixels;
                layoutParams.height = (int) (((float) 211 / 148) * layoutParams.width);
                mStrokeView.setLayoutParams(layoutParams);

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mStrokeView.getLayoutParams();
                lp.topMargin = (ScreenUtils.getDisplayMetrics(getHostActivity()).heightPixels - layoutParams.height) / 2;
                mStrokeView.setLayoutParams(lp);
                //设置页面背景图
//                Bitmap bgBitmap;
                /*if (AppCommon.getCurrentNoteType() == NoteTypeEnum.NOTE_TYPE_A4.getNoeType()) {
                    bgBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.a53);
                } else if (AppCommon.getCurrentNoteType() == NoteTypeEnum.NOTE_TYPE_A3.getNoeType()) {
                    bgBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.a53);
                } else {
                }*/
//                bgBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.page_bg1);
//                mStrokeView.setSignatureBitmap(bgBitmap);

                AppCommon.setDrawBroadWidthHeight(mBinding.llDrawBoard.getWidth(), mBinding.llDrawBoard.getHeight());

                mCanvasFrame.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                mCanvasFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                //                                canvasFrame.bDrawl.setOnTouchListener(new TouchListener(canvasFrame, mBinding.llDrawBoard.getWidth(),
                                //                                        mBinding.llDrawBoard.getHeight(), canvasFrame.getMeasuredWidth(), canvasFrame.getMeasuredHeight(), gestureListener));
                                mBinding.llDrawBoard.setOnTouchListener(new TouchListener(mCanvasFrame, mBinding.llDrawBoard.getWidth(),
                                        mBinding.llDrawBoard.getHeight(), mCanvasFrame.getMeasuredWidth(), mCanvasFrame.getMeasuredHeight(), gestureListener));

                                loadPage();
                            }
                        });

                mBinding.llDrawBoard.removeAllViews();
                mBinding.llDrawBoard.addView(mCanvasFrame);
                updatePenSizeColorStatus();
            }
        });
    }

    private IPageDrawView iPageDrawView = new IPageDrawView() {

        @Override
        public void onCreated(SurfaceHolder holder) {
            L.info(TAG, "drawView onCreated");
        }

        @Override
        public void onChanged(SurfaceHolder holder, int format, int width, int height) {
            L.info(TAG, "drawView onChanged");
        }

        @Override
        public void onDestroyed(SurfaceHolder holder) {
            L.info(TAG, "drawView onDestroyed");
            AppCommon.setDrawOpenState(PAGE_OPEN_STATUS.CLOSE);
        }
    };

    private synchronized void loadPage() {
        L.info(TAG, "loadPage isFirst=" + isFirst);
        if (isFirst) {
            final List<PageData> pageDatas = AppCommon.loadPageDataList(AppCommon.getCurrentNotebookId(), false);
            if (AppCommon.getCurrentPage() < 1 && null != pageDatas && pageDatas.size() > 0) {
                L.info(TAG, "loadPage() pageDatas size=" + pageDatas.size());
                for (int i = 0; i < pageDatas.size(); i++) {
                    if (pageDatas.get(i).pageNum > 0) {
                        AppCommon.setCurrentPage(pageDatas.get(i).pageNum);
                        break;
                    }
                }
            }
            showPage(AppCommon.getCurrentPage(), true);
            isFirst = false;
        } else {
            showPage(AppCommon.getCurrentPage(), true);
        }
        //打开获取缓存的笔画点的开关
//        AFPenClientCtrl.getInstance().setPollSwitchStatus(POLL_SWITCH_STATUS.OPEN);//落笔会飞笔相关，先注释掉这句试试
        //有时候标签没有显示的问题
        /*PageData curPageData = AppCommon.loadPageData(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
        if (null != curPageData && null != curPageData.name) {
            updateTopToolbar(curPageData.name);
        } else {
            updateTopToolbar(getString(R.string.label_text) + AppCommon.getCurrentPage());
        }*/
    }

    AudioUtil.IRecordListener recordListener = new AudioUtil.IRecordListener() {

        @Override
        public void onRecordStart() {
            onRecordStatus(AudioUtil.getInstance().getRecStatus());
            if(null!=getHostActivity()){

                getHostActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(R.string.recording);
                    }
                });
            }
        }

        @Override
        public void onRecordReStart() {
            onRecordStatus(AudioUtil.getInstance().getRecStatus());
            if(null!=getHostActivity()){

                getHostActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(R.string.record_restart);
                    }
                });
            }
        }

        @Override
        public void onRecordPause() {
            onRecordStatus(AudioUtil.getInstance().getRecStatus());
            if(null!=getHostActivity()){

                getHostActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(R.string.record_pause);
                    }
                });
            }
        }

        @Override
        public void onRecordStop() {
            onRecordStatus(AudioUtil.getInstance().getRecStatus());
            if(null!=getHostActivity()){

                getHostActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(R.string.record_stop);
                    }
                });
            }
        }

        @Override
        public void onRecordProgress(final long milliseconds) {
            if(null!=getHostActivity()){

                getHostActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String strTime = TimeUtil.recordTime(milliseconds);
                            mBinding.includeDrawerBottomMenu.includeRecordStatus.tvRecordTime.setText(strTime);
                            curSeconds = milliseconds;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }
    };

    GestureListener gestureListener = new GestureListener() {
        @Override
        public void leftDirect() {
            final int curPage = AppCommon.getCurrentPage();
            L.info(TAG, "left slide curPage=" + curPage);

            Bundle bundle = getArguments();
            bundle.putInt(PAGE_NUM, AppCommon.getCurrentPage());

            final List<PageData> pageDatas = AppCommon.loadPageDataList(AppCommon.getCurrentNotebookId(), false);

            if (curPage > 0 && curPage <= 300) {
                if(null!=getHostActivity()){

                    getHostActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int i = 0;
                            for (PageData pageData : pageDatas) {
                                if (pageData.pageNum == curPage) {
                                    //找到当前页在页签列表的序号i
                                    if (((i + 1) < pageDatas.size()) &&
                                            pageDatas.get(i + 1).pageNum == curPage) {
                                        i++;
                                    }
                                    break;
                                }
                                i++;
                            }
                            if ((i + 1) >= pageDatas.size()) {
                                showToast(R.string.it_is_last_page);
                                return;
                            }
                            //切换到下一页
                            switchPage(pageDatas.get(i + 1).pageNum);
//                        mLabel = "";
//                        L.info("left change page " + AppCommon.getCurrentPage());
//                        //更新标题和页码
//                        updateTopTitleAndPageLabel(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
//                        //更新录音文件数量提示
//                        updateRecordsTips();
//                        L.info("updateRecordsTips end  ");
                        }
                    });
                }
            } else {
                L.info("page num is invalid page=" + curPage);

                int i = 0;
                for (PageData pageData : pageDatas) {
                    if (pageData.pageNum > 0) {
                        //取第一个大于0的页码
                        break;
                    }
                    i++;
                }
                int page = pageDatas.get(i).pageNum;
                if (page > 0) {
                    //切换到下一页
                    AppCommon.setCurrentPage(page);
                    switchPage(page);
//                    mLabel = "";
//                    L.info("left change page " + AppCommon.getCurrentPage());
//                    //更新标题和页码
//                    updateTopTitleAndPageLabel(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
//                    //更新录音文件数量提示
//                    updateRecordsTips();
//                    L.info("updateRecordsTips end  ");
                }
            }
        }

        @Override
        public void rightDirect() {
            final int curPage = AppCommon.getCurrentPage();
            L.info(TAG, "right slide curPage=" + curPage);

            Bundle bundle = getArguments();
            bundle.putInt(PAGE_NUM, AppCommon.getCurrentPage());

            final List<PageData> pageDatas = AppCommon.loadPageDataList(AppCommon.getCurrentNotebookId(), false);

            int i = 0;
            for (PageData pageData : pageDatas) {
                if (pageData.pageNum == curPage) {
                    //找到当前页在列表中的索引值
                    break;
                }
                i++;
            }
            if ((i - 1) > -1) {
                int page = pageDatas.get(i - 1).pageNum;
                if (page < 1) {
                    //如果上一页的页码小于1则提示已是第一页
                    L.info(TAG, "Last page num=" + page);
                    showToast(R.string.it_is_first_page);
                    return;
                }
            }

            if (curPage > 1) {
                if(null!=getHostActivity()){

                    getHostActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int i = 0;
                            for (PageData pageData : pageDatas) {
                                if (pageData.pageNum == curPage) {
                                    break;
                                }
                                i++;
                            }
                            if ((i - 1) < 0) {
                                showToast(R.string.it_is_first_page);
                                return;
                            }

                            //                            switchPage(curPage - 1);
                            switchPage(pageDatas.get(i - 1).pageNum);
//                        mLabel = "";
//                        L.info("right change page " + AppCommon.getCurrentPage());
//                        //更新标题和页码
//                        updateTopTitleAndPageLabel(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
//                        mLabel = "";
//                        //更新录音文件数量提示
//                        updateRecordsTips();
//                        L.info("updateRecordsTips end  ");
                        }
                    });
                }
            }
            {
                if (curPage == 1) {
                    showToast(R.string.it_is_first_page);
                    return;
                }
            }
        }
    };

    /**
     * 点击多任务退出程序的时候来保存数据
     */
    class InnerReceiver extends BroadcastReceiver {

        final String SYSTEM_DIALOG_REASON_KEY = "reason";

        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        if (null != mStrokeView) {
                            mStrokeView.getDrawingCache();
                        }
                        save(1);
                    }
                }
            }
        }
    }

    /**
     * 显示底部菜单
     *
     * @param res
     */
    private void showBottomMenu(int res, boolean show) {
        if (isStopRecord) {
            mBinding.includeDrawerBottomMenu.includeRecordStatus.llRoot.setVisibility(View.INVISIBLE);
            mBinding.includeDrawerBottomMenu.includeRecordStop.llRoot.setVisibility(View.INVISIBLE);
        }

        if (show) {
            switch (res) {
                case R.id.fl_mic:
                    isStopRecord = false;
                    mBinding.includeDrawerBottomMenu.includeRecordStatus.llRoot.setVisibility(View.VISIBLE);
                    mBinding.includeDrawerBottomMenu.flMic.setVisibility(View.INVISIBLE);
                    toShowTools(res, false);
                    break;
                case R.id.ll_colors_plate:
//                    isShowTag = (Boolean) mBinding.includeRightColors.clRoot.getTag();
                    isShowTag = (Boolean) mBinding.includeBmColors.rlRoot.getTag();
                    toShowTools(res, !isShowTag);
                    break;
                case R.id.ll_pen_size:
                    isShowTag = (Boolean) mBinding.includeBmPenSize.rlRoot.getTag();
                    toShowTools(res, !isShowTag);
                    break;
                case R.id.ll_book_bg_type:
                    isShowTag = (Boolean) mBinding.includeBmBookBgType.rlRoot.getTag();
                    toShowTools(res, !isShowTag);
                    break;
            }
        } else {
            toShowTools(res, false);
        }
    }

    /**
     * view 上的点击事件
     *
     * @param view
     */
    public void onViewClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fl_mic:
                if (null != AppCommon.getCurrentNoteBookData() && !AppCommon.getCurrentNoteBookData().isLock) {
                    showBottomMenu(id, true);
                    //开始录音
                    CommandRecord command = new CommandRecord(CommandRecord.RECORD_START);
                    QPenManager.getInstance().onCommand(command);
                    String strTime = TimeUtil.recordTime(0);
                    mBinding.includeDrawerBottomMenu.includeRecordStatus.tvRecordTime.setText(strTime);
                } else {
                    ToastUtils.showShort(R.string.collected_canot_modif);
                }
                break;
            case R.id.ll_colors_plate:
                if (null != AppCommon.getCurrentNoteBookData() && !AppCommon.getCurrentNoteBookData().isLock) {
                    showBottomMenu(id, true);
                } else {
                    ToastUtils.showShort(R.string.collected_canot_modif);
                }
                break;
            case R.id.ll_pen_size:
                if (null != AppCommon.getCurrentNoteBookData() && !AppCommon.getCurrentNoteBookData().isLock) {
                    showBottomMenu(id, true);
                } else {
                    ToastUtils.showShort(R.string.collected_canot_modif);
                }
                break;
            case R.id.ll_book_bg_type:
                if (null != AppCommon.getCurrentNoteBookData() && !AppCommon.getCurrentNoteBookData().isLock) {
                    showBottomMenu(id, true);
                } else {
                    ToastUtils.showShort(R.string.collected_canot_modif);
                }
                break;
            case R.id.tv_page_label:
                PageData pageData = AppCommon.loadPageData(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
                if (null != pageData) {
                    mLabel = pageData.name;
                }
                Intent intent = new Intent(getActivity(), LabelEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(NOTEBOOK_ID, AppCommon.getCurrentNotebookId());
                bundle.putString(BaseActivity.PAGE_LABEL_NAME, mLabel);
                bundle.putInt(PAGE_NUM, AppCommon.getCurrentPage());
                intent.putExtras(bundle);
                this.startActivityForResult(intent, REQUEST_CODE_EDIT_LABEL);
                break;

            case R.id.ll_draw_board:
                L.info("click draw board show bottom menu = " + !isShowBottomMenu);
                if (isShowBottomMenu) {
                    mBinding.includeDrawerBottomMenu.llDrawerBottomMenu.setVisibility(View.GONE);
                    showTopToolbar(View.INVISIBLE);
                    showBottomMenu(0, false);
                    isShowBottomMenu = false;
                } else {
                    mBinding.includeDrawerBottomMenu.llDrawerBottomMenu.setVisibility(View.VISIBLE);
                    showTopToolbar(View.VISIBLE);
                    isShowBottomMenu = true;
                }
                break;
            case R.id.iv_more:
                showPopMenu();
                break;
            //笔的颜色
/*            case R.id.fl_colors_red:
                setPaintColor(ContextCompat.getColor(app, R.color.colors_menu_red));
                setColorsStatus(R.color.colors_menu_red);
                break;
            case R.id.fl_colors_green:
                setPaintColor(ContextCompat.getColor(app, R.color.colors_menu_green));
                setColorsStatus(R.color.colors_menu_green);
                break;
            case R.id.fl_colors_blue:
                setPaintColor(ContextCompat.getColor(app, R.color.colors_menu_blue));
                setColorsStatus(R.color.colors_menu_blue);
                break;
            case R.id.fl_colors_black:
                setPaintColor(ContextCompat.getColor(app, R.color.colors_menu_black));
                setColorsStatus(R.color.colors_menu_black);
                break;*/
            //笔的大小
            case R.id.fl_pen_size_thin:
                setPenType(CommandSize.PEN_SIZE_ONE);
                setPenStatusStatus(CommandSize.PEN_SIZE_ONE);
                break;
            case R.id.fl_pen_size_mid:
                setPenType(CommandSize.PEN_SIZE_TWO);
                setPenStatusStatus(CommandSize.PEN_SIZE_TWO);
                break;
            case R.id.fl_pen_size_thick:
                setPenType(CommandSize.PEN_SIZE_THREE);
                setPenStatusStatus(CommandSize.PEN_SIZE_THREE);
                break;
            //笔记本背景图
            case R.id.fl_book_bg_line:
                setPageBackground(1);
                setPageBgTypeStatus(1);
                break;
            case R.id.fl_book_bg_dot:
                setPageBackground(2);
                setPageBgTypeStatus(2);
                break;
            case R.id.fl_book_bg_empty:
                setPageBackground(3);
                setPageBgTypeStatus(3);
                break;
            case R.id.fl_record_pause:
                //录音暂停按键
                if (null != AppCommon.getCurrentNoteBookData() && !AppCommon.getCurrentNoteBookData().isLock) {
                    CommandRecord command = new CommandRecord(CommandRecord.RECORD_PAUSE);
                    QPenManager.getInstance().onCommand(command);
                } else {
                    ToastUtils.showShort(R.string.collected_canot_modif);
                }
                break;
            case R.id.fl_record_start:
                //录音开始按键
                if (null != AppCommon.getCurrentNoteBookData() && !AppCommon.getCurrentNoteBookData().isLock) {
                    CommandRecord command = new CommandRecord(CommandRecord.RECORD_PAUSE);
                    QPenManager.getInstance().onCommand(command);
                } else {
                    ToastUtils.showShort(R.string.collected_canot_modif);
                }
                break;
            case R.id.fl_record_stop:
                //录音停止按键
                isStopRecord = true;
                CommandRecord command = new CommandRecord(CommandRecord.RECORD_STOP);
                QPenManager.getInstance().onCommand(command);
                break;
            case R.id.layout_revoke:
                String strokesPath = AppCommon.getStrokesPath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
                StrokesUtilForQpen.deleteStrokes(strokesPath);
                loadPage();
                break;
        }
    }

    /**
     * toolbar右侧菜单监听事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_records:
                startActivity(new Intent(getActivity(), RecordListActivity.class));
                break;
            case R.id.menu_share:
                dialog = DialogHelper.showSelectFileFormatDialog(getChildFragmentManager(), selectFileFormatListener);
                break;
        }
        return true;
    }

    private SelectFileFormatListener selectFileFormatListener = new SelectFileFormatListener() {
        @Override
        public void onClick(View view, int format) {
            dismissDialog();
            showShare(format);
        }

        @Override
        public void onCancel() {
            dismissDialog();
        }
    };

    private ConfirmListener notesCleanConfirmListener = new ConfirmListener() {
        @Override
        public void confirm(View view) {
            dismissDialog();

            AppCommon.cleanPageData(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
            try {
                RecognizeDBmanager.getInstance().deleteRecognizeListByPage(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
                RecognizeDBmanager.getInstance().deleteRecognizeDataByPage(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());

                IInkSdkManager.getInstance().saveRecogn(AppCommon.getHwrFilePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage()), "");
                String recoLang = SpUtils.getString(getActivity(), Constant.SP_KEY_RECO_LANGUAGE, Constant.DEF_SHORT_NAME);
                /*IInkSdkManager.getInstance().editorClean();
                IInkSdkManager.getInstance().setPackageName(AppCommon.getCurrentNotebookId() + "_" + AppCommon.getCurrentPage() + ".iink");
                IInkSdkManager.getInstance().setLanguage(getActivity(), recoLang);*/

                L.error("clean page to " + AppCommon.getCurrentPage() + " editor clean ");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (null != mStrokeView) {
                mStrokeView.clearPaint();
                mStrokeView.invalidate();
            }
        }

        @Override
        public void cancel() {
            dismissDialog();
        }
    };

    /**
     * 右上角的弹出菜单
     */
    private void showPopMenu() {

        final int screenWidth = ScreenUtils.getDisplayMetrics(getHostActivity()).widthPixels;
        final int screenHeight = ScreenUtils.getDisplayMetrics(getHostActivity()).heightPixels;

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.pop_menu_draw, null);
        //处理popWindow 显示内容
        LinearLayout llHWR = contentView.findViewById(R.id.ll_hand_reco);
        LinearLayout llRecords = contentView.findViewById(R.id.ll_record_play);
        LinearLayout llShare = contentView.findViewById(R.id.ll_share);
        LinearLayout llmCloud = contentView.findViewById(R.id.ll_upload_mcloud);
        LinearLayout llClean = contentView.findViewById(R.id.ll_notes_clean);

        //        llHWR.setVisibility(View.GONE);
        llHWR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    toHwRecognition();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        llRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getHostActivity(), RecordListActivity.class));
                mCustomPopWindow.dissmiss();
            }
        });
        llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                dialog = DialogHelper.showSelectFileFormatDialog(getChildFragmentManager(), selectFileFormatListener);
                mCustomPopWindow.dissmiss();
            }
        });

        llmCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                mCustomPopWindow.dissmiss();
//                gotoActivity(LoginSMSActivity.class, null);
                createMCloud();
            }
        });

        llClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppCommon.isCurrentNotebookLocked()) {
                    ToastUtils.showLong(R.string.collected_canot_modif);
                    return;
                }
                dismissDialog();
                dialog = DialogHelper.showDelete(getChildFragmentManager(), notesCleanConfirmListener, R.string.confirm_delete_text, 0);
                mCustomPopWindow.dissmiss();
            }
        });

        //创建并显示popWindow
        mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(getHostActivity())
                .setView(contentView)
                .size(screenWidth / 3, 0)
                .setAnimationStyle(R.anim.slide_right_in)
                .create();
        //        int xOff = mCustomPopWindow.getWidth() / 2 - view.getWidth() / 2;
        //                mCustomPopWindow.showAsDropDown(icon_bg, -xOff, view.getHeight());

        final int viewWidth = contentView.getMeasuredWidth();
        final int viewHeight = contentView.getMeasuredHeight();

        Toolbar toolbar = getHostActivity().getTopToolbar();
        final int toolbarHeight = toolbar.getHeight();
        mCustomPopWindow.showAtLocation(toolbar, Gravity.START | Gravity.TOP, screenWidth, toolbarHeight);

    }

    /**
     * 创建彩云笔记
     */
    private void createMCloud() {
        PageStrokesCacheBean mCurPageStrokesCache = StrokesUtilForQpen.getStrokes(new File(AppCommon.getStrokesPath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage())));

        if (mCurPageStrokesCache != null) {
            AASUserInfoData aasUserInfoData = AppCommon.loadAASUserInfo();

            if (null != aasUserInfoData && !TextUtils.isEmpty(aasUserInfoData.authToken)) {
                //获取彩云业务请求头
                OseUserInfoData oseUserInfoData = AppCommon.loadOseUserInfo();
                if (oseUserInfoData == null || TextUtils.isEmpty(oseUserInfoData.NOTE_TOKEN)) {
                    NetCommon.mCloudResetToken(aasUserInfoData, new LoginCallBack() {
                        @Override
                        public void success() {
                            uploadData(mCurPageStrokesCache);
                        }
                    });
                } else {
                    uploadData(mCurPageStrokesCache);
                }
            } else {
                showConfirmDialogToLoginMCloud();
            }


        } else {
            ToastUtils.showShort("当前没有笔记");
            return;
        }
    }

    /**
     * 上传笔记到彩云笔记
     *
     * @param mCurPageStrokesCache
     */
    void uploadData(PageStrokesCacheBean mCurPageStrokesCache) {

        dialog = DialogHelper.showBle(getFragmentManager(), "正在生成笔记...");
        //上传数据
        String noteName = AppCommon.getCurrentNoteBookData().noteName;
        PageData pageData = AppCommon.loadPageData(bookId, AppCommon.getCurrentPage());
        if (null != pageData) {
            mLabel = pageData.name;
            if (TextUtils.isEmpty(mLabel)) {
                mLabel = getString(R.string.label_text) + AppCommon.getCurrentPage();
            }
        } else {
            mLabel = getString(R.string.label_text) + AppCommon.getCurrentPage();
        }
        String noteTitle = mLabel;

        //整理笔记及识别   先全部识别
        List<RecognizeBean> recognizeBeans = RecognizeDBmanager.getInstance().getRecognizeListByPage(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
        List<StrokesBean> recognizeList = new ArrayList<>();
        if (null != recognizeBeans && recognizeBeans.size() > 0) {
            for (StrokesBean strokesBean : mCurPageStrokesCache.getStrokesBeans()) {
                if (strokesBean.getCreateTime() > recognizeBeans.get(recognizeBeans.size() - 1).timestamp) {
                    recognizeList.add(strokesBean);//1624431493759    1624431490568
                }
            }
        } else if (null != mCurPageStrokesCache.getStrokesBeans()) {

            recognizeList.addAll(mCurPageStrokesCache.getStrokesBeans());
        }

        if (recognizeList.size() > 0) {
            RecognizeCommon.getInstance().recognize(new RecognizeCallback() {
                @Override
                public void getResult(String result) {
                    toUploadResource(noteName, noteTitle);
                }

                @Override
                public void onError(String error) {
                    toUploadResource(noteName, noteTitle);
                }
            }, AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), recognizeList, true);
        } else {
            toUploadResource(noteName, noteTitle);
        }
    }

    private NoteBean mNoteBean;

    /**
     * 等到当页的识别完成后，开始整合上传需要的资源
     *
     * @param notebookName
     * @param pageName
     */
    public void toUploadResource(String notebookName, String pageName) {
        RecognizeData recognizeData = RecognizeDBmanager.getInstance().getRecognizeData(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage());
        if (null == recognizeData) {
            dismissDialog();
            showToast("无笔记数据");
            return;
        }
        long timestamp = System.currentTimeMillis();
        String path = AppCommon.NQ_SAVE_ROOT_PATH + AppCommon.FILE_SEPARATOR + AppCommon.getUserUID() + AppCommon.FILE_SEPARATOR + AppCommon.getCurrentNotebookId() + AppCommon.FILE_SEPARATOR + AppCommon.getCurrentPage() + AppCommon.FILE_SEPARATOR + timestamp + AppCommon.SUFFIX_NAME_JPG;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        BitmapUtil.bitmap2File(mStrokeView.getSignatureBitmap(), path, 1);
        String content = recognizeData.getResultList();
        mNoteBean = new NoteBean();
//        content = content.replaceAll("/\n", "\\n");
//        content = content.replaceAll("/\r", "\\r");
        mNoteBean.noteId = GeneratorUtil.randomSequence(32);
        mNoteBean.content = content;
        mNoteBean.notebook = notebookName;
        mNoteBean.title = pageName;
        mNoteBean.topmost = 1;
        mNoteBean.remindType = 0;
        mNoteBean.archived = -1;
        mNoteBean.filePath = path;
        mNoteBean.fileName = timestamp + AppCommon.SUFFIX_NAME_JPG;
        NetCommon.mCloudGetNotebookList(mNoteBean);
    }

    /**
     * 显示对话框确认是否登录和彩云
     */
    private void showConfirmDialogToLoginMCloud() {
        dialog = DialogHelper.showLoginMCloud(getFragmentManager(), new LoginmCloudListener() {
            @Override
            public void getVeriCode(String mobile, View view) {
                //获取验证码
//                        EditText editText = (EditText) view.findViewById(R.id.et_mobile);
//                        String mobile = editText.getText().toString();
                if (!RegexUtils.checkPhoneNumber(mobile)) {
                    showToast("请输入正确的手机号");
                    return;
                }

                Button button1 = (Button) view;//.findViewById(R.id.btn_veri_code);
                if (null != button1) {
//                    button1.setText("重新获取");
                    button1.setClickable(false);
                    createTimerTask(button1);
                    try{

                        if(null!=timer){
                            timer.schedule(task, 1000, 1000);
                        }
                    } catch (Exception e){

                    }

                    getSMSVeriCode(mobile);
                }
            }

            @Override
            public void login(String mobile, String veriCode, View view) {
                //登录
//                        EditText editText = (EditText) view.findViewById(R.id.et_mobile);
//                        String mobile = editText.getText().toString();
                if (!RegexUtils.checkPhoneNumber(mobile)) {
                    showToast("请输入正确的手机号");
                    return;
                }
//                        EditText etVeriCode = (EditText) view.findViewById(R.id.et_veri_code);
//                        String veriCode = etVeriCode.getText().toString().trim();
                if (TextUtils.isEmpty(veriCode)) {
                    showToast("短信验证码不能为空");
                    return;
                }
                mVeriCode = veriCode;
                aasLogin(mobile, veriCode);
            }

            @Override
            public void OnDismissListener() {
                secondLeft = 0;
            }
        });
    }

    /**
     * 显示对话框确认是否拉起和彩云
     */
    private void showConfirmDialogToStartMCloudApp() {
        dialog = DialogHelper.showConfirm(getFragmentManager(), new ConfirmListener() {
            @Override
            public void confirm(View view) {
                startMCloudApp();
                dismissDialog();

                if(null!=getHostActivity()){

                    getHostActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(null!=mStrokeView){
                                mStrokeView.invalidate();
                            }
                        }
                    });
                }
            }

            @Override
            public void cancel() {
                dismissDialog();
            }
        }, "笔记生成成功", "和彩云笔记生成成功，您可以前往 【和彩云APP-云笔记】 查看已生成的笔记", "前往和彩云", "取消", true);
    }

    /**
     * 根据颜色设置色盘选择状态
     *
     * @param color
     */
    private void setColorsStatus(final int color) {
        getHostActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*mBinding.includeBmColors.ivSelectedRed.setVisibility(View.INVISIBLE);
                mBinding.includeBmColors.ivSelectedGreen.setVisibility(View.INVISIBLE);
                mBinding.includeBmColors.ivSelectedBlue.setVisibility(View.INVISIBLE);
                mBinding.includeBmColors.ivSelectedBlack.setVisibility(View.INVISIBLE);
                switch (color) {
                    case R.color.colors_menu_red:
                        mBinding.includeBmColors.ivSelectedRed.setVisibility(View.VISIBLE);
                        break;
                    case R.color.colors_menu_green:
                        mBinding.includeBmColors.ivSelectedGreen.setVisibility(View.VISIBLE);
                        break;
                    case R.color.colors_menu_blue:
                        mBinding.includeBmColors.ivSelectedBlue.setVisibility(View.VISIBLE);
                        break;
                    case R.color.colors_menu_black:
                        mBinding.includeBmColors.ivSelectedBlack.setVisibility(View.VISIBLE);
                        break;
                    default:
                        mBinding.includeBmColors.ivSelectedBlack.setVisibility(View.VISIBLE);
                }*/
            }
        });
    }

    /**
     * 设置note type status
     *
     * @param bgType
     */
    private void setPageBgTypeStatus(final int bgType) {
        getHostActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.includeBmBookBgType.ivSelected1.setVisibility(View.INVISIBLE);
                mBinding.includeBmBookBgType.ivSelected2.setVisibility(View.INVISIBLE);
                mBinding.includeBmBookBgType.ivSelected3.setVisibility(View.INVISIBLE);

                switch (bgType) {
                    case 2:
                        mBinding.includeBmBookBgType.ivSelected2.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        mBinding.includeBmBookBgType.ivSelected3.setVisibility(View.VISIBLE);
                        break;
                    default:
                        mBinding.includeBmBookBgType.ivSelected1.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    /**
     * 设置pen size status
     *
     * @param sizeType
     */
    private void setPenStatusStatus(final int sizeType) {
        getHostActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.includeBmPenSize.flPenSizeThin.setBackground(null);
                mBinding.includeBmPenSize.flPenSizeMid.setBackground(null);
                mBinding.includeBmPenSize.flPenSizeThick.setBackground(null);

                switch (sizeType) {
                    case CommandSize.PEN_SIZE_ONE:
                        mBinding.includeBmPenSize.flPenSizeThin.setBackgroundResource(R.drawable.pen_size_bg_clk);
                        break;
                    case CommandSize.PEN_SIZE_TWO:
                        mBinding.includeBmPenSize.flPenSizeMid.setBackgroundResource(R.drawable.pen_size_bg_clk);
                        break;
                    case CommandSize.PEN_SIZE_THREE:
                        mBinding.includeBmPenSize.flPenSizeThick.setBackgroundResource(R.drawable.pen_size_bg_clk);
                        break;
                    default:
                        mBinding.includeBmPenSize.flPenSizeThin.setBackgroundResource(R.drawable.pen_size_bg_clk);
                }
            }
        });
    }

    private void setPaintColor(int colorId) {
        QPenManager.getInstance().setPaintColor(colorId);
        if (null != AppCommon.getUserUID()) {
            SpUtils.putInt(getContext(), AppCommon.getUserUID() + "_" + "color", colorId);
        }
        if (null != mStrokeView) {
            mStrokeView.setPenColor(colorId);
        }
    }

    private void setPenType(int leavel) {
        QPenManager.getInstance().setPenSizeType(leavel);
        QPenManager.getInstance().setPaintSize(CommandSize.getSizeByLevel(leavel));

        if (null != AppCommon.getUserUID()) {
            SpUtils.putInt(getContext(), AppCommon.getUserUID() + "_" + "size", leavel);
        }

        if (null != mStrokeView) {
            mStrokeView.setPenSize(CommandSize.getSizeByLevel(leavel));
        }
    }

    private void setPageBackground(int bgType) {
        AppCommon.setCurrentNoteBookBG(bgType);
        int resBg = 0;
        if (null != mStrokeView) {
            switch (bgType) {
                case 2:
                    resBg = R.drawable.dot_main_bg;
                    break;
                case 3:
                    resBg = R.drawable.page_bg1;
                    break;
                default:
                    resBg = R.drawable.page_bg;
            }
            Bitmap bmp = null;
            if(resBg!=0){
                bmp = BitmapFactory.decodeResource(getContext().getResources(), resBg);
            }
            if(null!=bmp){
                mStrokeView.setSignatureBitmap(bmp);
            }
        }
    }

    private void setLabel(String labelName) {
        Message mes = new Message();
        mes.what = 11100;
        mes.obj = labelName;
        EventBusUtil.post(mes);
    }

    /***
     * 分享
     */
    private void showShare(final int format) {
        AppCommon.isSharePageDraw = true;
        dialog = DialogHelper.showShare(format, getChildFragmentManager(), new ShareListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.wechat:
                        if (format == 0) {
                            share(Wechat.NAME, format);
                        } else {
                            //                            AppCommon.getShareFilePath(mStrokeView.getSignatureBitmap());

                            BitmapUtil.bitmap2File(mStrokeView.getSignatureBitmap(), AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_JPG), 1);

                            PdfUtil.pdfModel(mStrokeView, mStrokeView.getWidth(), mStrokeView.getHeight(), 1, AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_PDF));

                            Platform.ShareParams sp = new Platform.ShareParams();
                            sp.setTitle(getString(R.string.menu_share));
                            sp.setImagePath(AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_JPG));
                            sp.setFilePath(AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_PDF));
                            sp.setText(getString(R.string.menu_share));
                            sp.setShareType(Platform.SHARE_FILE);
                            Platform plat = ShareSDK.getPlatform(Wechat.NAME);
                            plat.setPlatformActionListener(sharePlatformActionListener);
                            plat.share(sp);
                        }
                        break;
                    case R.id.wechatmoments:
                        if (format == 0) {
                            share(WechatMoments.NAME, format);
                        } else {
                            ToastUtils.showShort(R.string.share_to_wechat_not_support);
                        }
                        //                        nativeToShare(1, format);
                        break;
                    case R.id.qq:
                        if (format == 0) {
                            share(QQ.NAME, format);
                        } else {
                            if (AppInfoUtil.isQQClientAvailable(getContext())) {
                                nativeToShare(2, format);
                            } else {
                                ToastUtils.showShort(R.string.qq_version_not_install);
                            }
                        }
                        //                        share(QQ.NAME, format);
                        break;
                    case R.id.qzone:
                        //                        nativeToShare(3, format);
                        if (format == 0) {
                            share(QZone.NAME, format);
                        } else {
                            ToastUtils.showShort(R.string.share_to_qzone_not_support);
                        }
                        break;
                    case R.id.sinaweibo:
                        if (format == 0) {
                            share(SinaWeibo.NAME, format);
                        } else {
                            ToastUtils.showShort(R.string.share_to_weibo_not_support);
                        }
                        break;
                    case R.id.facebook:
                        if (format == 1) {
                            ToastUtils.showShort(R.string.share_facebook_not_support);
                        } else {
                            share(Facebook.NAME, format);
                        }
                        break;
                    case R.id.twitter:
                        if (format == 1) {
                            ToastUtils.showShort(R.string.share_twitter_not_support);
                        } else {
                            share(Twitter.NAME, format);
                        }
                        break;
                    case R.id.email:
                        if (format == 1) {
                            ToastUtils.showShort(R.string.share_facebook_not_support);
                        } else {
                            share(Email.NAME, format);
                        }
                        break;
                    case R.id.whatapp:
                        if (format == 1) {
                            ToastUtils.showShort(R.string.share_twitter_not_support);
                        } else {
                            share(WhatsApp.NAME, format);
                        }
                        break;
                }
            }

            @Override
            public void onCancel() {
                dismissDialog();
            }
        });
    }

    private void nativeToShare(int name, int format) {
        String path = "";
        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        switch (name) {
            case 0: {
                ComponentName cop = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                imageIntent.setComponent(cop);
                break;
            }
            case 1: {
                ComponentName cop = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                imageIntent.setComponent(cop);
                break;
            }
            case 2: {
                ComponentName cop = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
                imageIntent.setComponent(cop);
                break;
            }
            case 3: {
                ComponentName cop = new ComponentName("com.qzone", "com.qzonex.module.operation.ui.QZonePublishMoodActivity");
                imageIntent.setComponent(cop);
                break;
            }
        }

        if (format == 0) {
            path = AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_JPG);
            BitmapUtil.bitmap2File(mStrokeView.getSignatureBitmap(), path, 1);
            imageIntent.setType("image/jpeg");
        } else {
            path = AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_PDF);
            PdfUtil.pdfModel(mStrokeView, mStrokeView.getWidth(), mStrokeView.getHeight(), 1, path);
            imageIntent.setType("*/*");
        }
        Uri uriForFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uriForFile = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".FileProvider", new File(path));
        } else {
            uriForFile = Uri.parse(path);
        }
        imageIntent.putExtra(Intent.EXTRA_STREAM, uriForFile);
        startActivity(Intent.createChooser(imageIntent, "分享"));
    }

    OnekeyShare oks;
    int format;
    String platform;

    private void share(final String platform, final int format) {
        this.format = format;
        this.platform = platform;
        rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    //                    toShare();
                    new ShareTask().execute(platform, "" + format);
                } else {
                    //拒绝授权
                }
            }
        });
    }

    private void toShare(String platform, String format) {
        if (null == oks) {
            oks = new OnekeyShare();
        }

        if (!StringUtils.isEmpty(platform)) {
            oks.setPlatform(platform);
        }
        oks.setSite(getString(R.string.app_name));
        oks.setCallback(sharePlatformActionListener);
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        if ("0".equals(format)) {
            //            AppCommon.getShareFilePath(mStrokeView.getSignatureBitmap());
            oks.setImagePath(AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_JPG));

        } else {
            //            PdfUtil.pdfModel(mStrokeView, mStrokeView.getWidth(), mStrokeView.getHeight(), 1, Constant.SHARE_PATH_PDF);
            oks.setFilePath(AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_PDF));
        }
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams shareParams) {
                if (SinaWeibo.NAME.equals(platform.getName())) {
                    shareParams.setShareType(Platform.SHARE_IMAGE);
                    shareParams.setImageData(ImageUtils.getBitmap(AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_JPG)));

                }
            }
        });
        // 启动分享GUI
        oks.show(getContext());

/*        AppCommon.getShareFilePath(mStrokeView.getSignatureBitmap());
        PdfUtil.pdfModel(mStrokeView, mStrokeView.getWidth(), mStrokeView.getHeight(), 1, Constant.SHARE_PATH_PDF);
        Platform.ShareParams sp = new Platform.ShareParams();
//        sp.setText("http://image.eningqu.com/aipen_share.pdf");
        sp.setTitle(getString(R.string.menu_share));
//        sp.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
//        sp.setImagePath(Constant.SHARE_PATH_JPG);
        sp.setFilePath(Constant.SHARE_PATH_PDF);
//        sp.setUrl("http://image.eningqu.com/aipen_share.pdf");
//        sp.setFilePath("http://image.eningqu.com/aipen_share.pdf");
        sp.setShareType(Platform.SHARE_FILE);
        Platform plat = ShareSDK.getPlatform(platform);
        plat.setPlatformActionListener(sharePlatformActionListener);
        plat.share(sp);*/
    }

    PlatformActionListener sharePlatformActionListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            L.info(TAG, "-----------onComplete----------");
            AppCommon.isSharePageDraw = false;
            if (i != 9)

                ToastUtils.showShort(getString(R.string.menu_share) + getString(R.string.success));
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            L.error(TAG, "-----------onError----------");
            AppCommon.isSharePageDraw = false;
            ToastUtils.showShort(getString(R.string.menu_share) + getString(R.string.fail) + throwable.getMessage());
        }

        @Override
        public void onCancel(Platform platform, int i) {
            L.info(TAG, "-----------onCancel----------");
            AppCommon.isSharePageDraw = false;
            ToastUtils.showShort(getString(R.string.dialog_cancel_text) + getString(R.string.menu_share));
        }
    };

    private void updatePenSizeColorStatus() {
        mBinding.includeBmColors.rlRoot.setTag(isShowTag);
        mBinding.includeRightColors.clRoot.setTag(isShowTag);
        mBinding.includeBmPenSize.rlRoot.setTag(isShowTag);
        mBinding.includeBmBookBgType.rlRoot.setTag(isShowTag);

        int defaultColor = QPenManager.getInstance().getPaintCacheColor();

        int red = ContextCompat.getColor(app, R.color.colors_menu_red);
        int green = ContextCompat.getColor(app, R.color.colors_menu_green);
        int blue = ContextCompat.getColor(app, R.color.colors_menu_blue);

        if (defaultColor == red) {
            setColorsStatus(R.color.colors_menu_red);
        } else if (defaultColor == green) {
            setColorsStatus(R.color.colors_menu_green);
        } else if (defaultColor == blue) {
            setColorsStatus(R.color.colors_menu_blue);
        } else {
            setColorsStatus(R.color.colors_menu_black);
        }

        int defaultBg = AppCommon.getCurrentNoteBookBG();
        setPageBgTypeStatus(defaultBg);

        int penSizeType = QPenManager.getInstance().getPenSizeType();
        setPenStatusStatus(penSizeType);
    }

    private List<String> searchFiles(List<String> list) {
        //        records
        String[] ext = {"pcm"};
        //        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/" + AudioUtil.folderName);
        File dir = new File(AppCommon.getAudioPathDir(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), ""));

        return FileUtils.search(list, dir, ext);
    }

    /**
     * 更新录音的UI状态
     *
     * @param status
     */
    protected void onRecordStatus(AudioUtil.REC_STATUS status) {
        if (status == AudioUtil.REC_STATUS.STATUS_PAUSE) {
            //录音暂停按键
            getHostActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.includeDrawerBottomMenu.includeRecordStatus.llRoot.setVisibility(View.INVISIBLE);
                    mBinding.includeDrawerBottomMenu.includeRecordStop.llRoot.setVisibility(View.VISIBLE);
                    mBinding.includeDrawerBottomMenu.flMic.setVisibility(View.INVISIBLE);
                }
            });

        } else if (status == AudioUtil.REC_STATUS.STATUS_START) {
            isStopRecord = false;
            //录音开始按键
            getHostActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.includeDrawerBottomMenu.includeRecordStatus.llRoot.setVisibility(View.VISIBLE);
                    mBinding.includeDrawerBottomMenu.includeRecordStop.llRoot.setVisibility(View.INVISIBLE);
                    mBinding.includeDrawerBottomMenu.flMic.setVisibility(View.INVISIBLE);
                }
            });
        } else if (status == AudioUtil.REC_STATUS.STATUS_STOP ||
                status == AudioUtil.REC_STATUS.STATUS_NO_READY) {
            isStopRecord = true;
            //录音停止按键
            getHostActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        try {
                            mBinding.includeDrawerBottomMenu.includeRecordStatus.llRoot.setVisibility(View.INVISIBLE);
                            mBinding.includeDrawerBottomMenu.includeRecordStop.llRoot.setVisibility(View.INVISIBLE);
                            mBinding.includeDrawerBottomMenu.flMic.setVisibility(View.VISIBLE);
                            updateRecordsTips();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        }
    }

    /**
     * 更新界面的录音数量
     */
    private void updateRecordsTips() {
        //搜索录音文件
        files = searchFiles(new ArrayList<String>());
        if (null != files) {
            if (files.size() == 0) {
                mBinding.includeDrawerBottomMenu.tvRecordsCounter.setText("");
            } else {
                mBinding.includeDrawerBottomMenu.tvRecordsCounter.setText(String.valueOf(files.size()));
            }
        }
    }


    class ShareTask extends AsyncTask<String, Void, String> {
        String platform;
        String format;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            platform = params[0];
            format = params[1];

            if(null!=getHostActivity()){

                getHostActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if ("0".equals(format)) {
                            BitmapUtil.bitmap2File(mStrokeView.getSignatureBitmap(), AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_JPG), 1);
                        } else if ("1".equals(format)) {
                            PdfUtil.pdfModel(mStrokeView, mStrokeView.getWidth(), mStrokeView.getHeight(), 1, AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_PDF));
                        }

                        toShare(platform, format);
                    }
                });
            }
            //            toShare2();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    NQDot firstDot = null;
    private Queue<NQDot> mDotQueueForBroadcast = new ConcurrentLinkedQueue<>();

    public boolean isRecognizeNow = false;

    /**
     * 实时处理收到的坐标点
     *
     * @param dot
     */
    public void drawDot(final NQDot dot) {
        //添加到队列
        mDotQueueForBroadcast.add(dot);
        if (!isOver) {
            //检查是否命令区域的点
            CommandBase commandBase1 = null;
            if (firstDot == null) {
                firstDot = dot;
            }
            commandBase1 = SDKUtil.calculateADot(firstDot.page, firstDot.type, firstDot.x, firstDot.y);
            if (commandBase1 != null) {//落笔在功能区
                CommandBase commandBase2 = SDKUtil.calculateADot(dot.page, dot.type, dot.x, dot.y);
                if (commandBase2 == null || commandBase1.getSizeLevel() != commandBase2.getSizeLevel() ||
                        commandBase1.getCode() != commandBase2.getCode()) {
                    // 不在功能区或不是同一个功能区
                    // 绘制
                    if (commandBase2 != null) {
                        L.info("base1 type = " + commandBase1.getSizeLevel() + ": 2type = " + commandBase2.getSizeLevel());
                        L.info("base1 code = " + commandBase1.getCode() + ": 2code = " + commandBase2.getCode());
                    }
                    L.error("handle dot 离开功能区 dot type = " + dot.type + ", x = " + dot.x + ", y = " + dot.y);

                    if(null!=getHostActivity()){

                        getHostActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //绘制画布
                                if (null != mStrokeView) {
                                    isOver = true;
                                    NQDot poll = mDotQueueForBroadcast.poll();
                                    while (poll != null && AFPenClientCtrl.getInstance().getSyncStatus() == PEN_SYNC_STATUS.NONE) {
                                        if (!AFPenClientCtrl.getInstance().isClickMenu()) {
                                            if (AFPenClientCtrl.getInstance().getSyncStatus() != PEN_SYNC_STATUS.NONE)
                                                mStrokeView.addDot2(poll, true);
                                            else {
                                                poll.type = 2;
                                                mStrokeView.addDot2(poll, true);
                                            }
                                        }
                                        poll = mDotQueueForBroadcast.poll();
                                    }
                                } else {
                                    L.error(TAG, "mStrokeView is null");
                                }
                            }
                        });
                    }
                } else {
                    resetFirstDot(dot);
                }
            } else {
                //落笔不在功能区
                //落笔不在功能区
                drawDotQueueForBroadcast(dot);
            }
        } else {
            drawDotQueueForBroadcast(dot);
        }
    }

    private void resetFirstDot(NQDot dot) {
        //落笔在功能区，且直到抬笔都没有离开该功能区
        if (dot.type == DotType.PEN_ACTION_UP) {
            isOver = false;
            //抬笔时
//            L.error(TAG, "落笔和抬笔都在功能区  handle dot.UP");
            //                    functionListener.onCommand(commandBase1);
            // 清空队列
            mDotQueueForBroadcast.clear();
            firstDot = null;
        }
    }

    private NQDot lastPoint;
    private long lastUpTime = 0;

    private void drawDotQueueForBroadcast(NQDot dot) {
        //落笔不在功能区
        if(null==getHostActivity()){
            return;
        }
        getHostActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //绘制画布
                if (null != mStrokeView) {
                    NQDot poll = mDotQueueForBroadcast.poll();
                    while (poll != null && AFPenClientCtrl.getInstance().getSyncStatus() == PEN_SYNC_STATUS.NONE) {
                        if (!AFPenClientCtrl.getInstance().isClickMenu()) {
                            mStrokeView.addDot2(poll, true);
                            if (AFPenClientCtrl.getInstance().getSyncStatus() != PEN_SYNC_STATUS.NONE) {
                                poll.type = 2;
                                mStrokeView.addDot2(poll, true);
                            }
                            if (lastPoint != null) {
                                double a = Math.pow(Math.abs(poll.x - lastPoint.x), 2);
                                double b = Math.pow(Math.abs(poll.y - lastPoint.y), 2);
                                double dist = Math.sqrt(a + b);
                                lastUpTime = System.currentTimeMillis();
                                //暂时移除自动识别
//                                if (poll.type == 2) {
//                                    mHandler.removeMessages(1);
//                                    mHandler.sendEmptyMessageDelayed(1, 2000);
//                                }
                                //暂时移除换行识别
//                                if ((dist > 300 && lastPoint.page == poll.page)) {
//                                    mHandler.sendEmptyMessageDelayed(2, 200);
//                                }

                            }


                            lastPoint = poll;
                        }

                        poll = mDotQueueForBroadcast.poll();

                    }
                } else {
                    L.error(TAG, "mStrokeView is null");
                }
            }
        });

        resetFirstDot(dot);
    }

    private static class UIHandler extends Handler {
        WeakReference<PageDrawFragment> softReference;

        UIHandler(PageDrawFragment fragment) {
            softReference = new WeakReference<PageDrawFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PageDrawFragment drawFragment = softReference.get();

            if (msg.what == 1) {
                if (null != drawFragment) {
                    if (!drawFragment.isRecognizeNow) {
                        drawFragment.isRecognizeNow = true;
                        NingQuLog.error("myscript in pageDrawwFragment:", "start");
                        RecognizeCommon.getInstance().recognizeAfterChange(drawFragment.recognizeCallback, true);
                    }
                }
            } else if (msg.what == 2) {
                if (null != drawFragment) {
                    if (!drawFragment.isRecognizeNow) {
                        drawFragment.isRecognizeNow = true;
                        NingQuLog.error("myscript in pageDrawwFragment:", "start");
                        RecognizeCommon.getInstance().recognizeAfterChange(drawFragment.recognizeCallback, true);
                    }
                }
            }

        }
    }

    public void clickMenuDraw() {
        if (mStrokeView != null) {
            NQDot strokeLastDot = mStrokeView.getStrokeLastDot();
            if (strokeLastDot != null) {
                strokeLastDot.type = 2;
                AFPenClientCtrl.getInstance().setClickMenu(true);
                mStrokeView.addDot2(strokeLastDot, true);
            }
        }
    }

    /**
     * 进入手写识别页面
     */
    private void toHwRecognition() {

        if (AppCommon.getCurrentPage() < 1) {
            ToastUtils.showShort("page number is error");
            return;
        }

        if (null != mStrokeView) {
            List<AFStrokeAndPaint> list = mStrokeView.getStrokeAndPaints();
            List<AFStrokeAndPaint> list2 = mStrokeView.getStrokeAndPaints2();
            if (null != list && list.size() == 0 && null != list2 && list2.size() == 0) {
                ToastUtils.showShort(R.string.empty);
                return;
            }
            isNeedInitHwr = true;
//        IInkSdkManager.getInstance().unInit();
            BitmapUtil.bitmap2File(mStrokeView.getSignatureBitmap(), AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_JPG), 1);
            Bundle bundle = new Bundle();
            bundle.putString(NOTEBOOK_ID, AppCommon.getCurrentNotebookId());
            bundle.putInt(PAGE_NUM, AppCommon.getCurrentPage());
            gotoActivity(HwrRecognizeActivity.class, bundle);
            if (null != mCustomPopWindow) {
                mCustomPopWindow.dissmiss();
            }
        }
    }

    private void gotoActivity(Class<?> clz, Bundle bundle) {
        ((FragmentBaseActivity) getActivity()).toGotoActivity(clz, bundle);
    }

    // 普通事件的处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier carrier) {
        super.handleEvent(carrier);
        if (null == carrier) {
            return;
        }

        switch (carrier.getEventType()) {
            case Constant.FUNCTION_COMMAND_CODE:
                CommandBase commandBase = (CommandBase) carrier.getObject();
                dealWithCommand(commandBase);
                break;
            case Constant.ERROR_LOCKED:
                showToast(R.string.collected_canot_modif);
                break;
            case Constant.DRAW_CODE:
                if (AppCommon.getDrawOpenState() == PAGE_OPEN_STATUS.OPEN) {
                    NQDot dot = (NQDot) carrier.getObject();
                    drawDot(dot);
                }
                break;

            case 80012: {
                String body = (String) carrier.getObject();
                ;
                L.debug("body=" + body);
                try {
                    GetVeriCodeRsp obj = Xml2Obj.fromXml(body, GetVeriCodeRsp.class);
                    if (null != obj) {
                        if ("0".equals(obj.getError())) {
                            showToast("短信已经发送，请查收");
                        } else {
                            showToast("验证码发送失败，请稍后重试");
                            secondLeft = 0;
                        }
                    }
                    //xml消息体经过AES加密，密钥md5（md5（动态密码）+约定编号）取前16位
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case 80013: {
                String body = (String) carrier.getObject();

                L.debug("body=" + body);
                String xml = "";
                try {
                    xml = MCloudAESUtil.decode(body, MCloudConf.SECRET, mVeriCode, 5);
                    L.debug("解密body=" + xml);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (TextUtils.isEmpty(xml)) {
                        xml = body;
                    }
                    AASLoginRsp obj = Xml2Obj.fromXml(xml, AASLoginRsp.class);
                    if (null != obj) {
                        if ("0".equals(obj.getError())) {
                            showToast("绑定成功");
                            secondLeft = 0;
                            dismissDialog();
                            if (AppCommon.saveMCloudUserInfo(obj)) {
                                createMCloud();
                            } else {
                                showToast("绑定失败，请稍后重试");
                            }
                        } else {
                            if("9441".equals(obj.getError())){
                                showToast("验证码错误，请重新输入");
                            } else if("9442".equals(obj.getError())){
                                showToast("验证码已失效，请稍后重试");
                            }  else if("200059505".equals(obj.getError())){
                                showToast("多次登录失败，当前用户已被锁住");
                            } else {
                                showToast("绑定失败：" + obj.getError() + " " + obj.getDesc());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case 80014:
                dismissDialog();
                showConfirmDialogToStartMCloudApp();
                break;
            case 80015:
                dismissDialog();
                String message = (String) carrier.getObject();
                showToast(message == null ? "" : message);
                break;
        }
    }

    /**
     * 界面只对功能命令做出状态显示，具体操作在AFPenClientCtrl类
     *
     * @param commandBase
     */
    private void dealWithCommand(CommandBase commandBase) {
        if (null != commandBase) {
            switch (commandBase.getSizeLevel()) {
                case CommandBase.COMMAND_TYPE_RECORD:
                    CommandRecord commandRecord = (CommandRecord) commandBase;
                    switch (commandRecord.getCode()) {
                        case CommandRecord.RECORD_START:
                            //                            onRecordStatus(AudioUtil.getInstance().getRecStatus());
                            if ((AudioUtil.getInstance().getLastRecStatus() != AudioUtil.REC_STATUS.STATUS_PAUSE ||
                                    AudioUtil.getInstance().getRecStatus() != AudioUtil.REC_STATUS.STATUS_START) &&
                                    (AudioUtil.getInstance().getLastRecStatus() != AudioUtil.REC_STATUS.STATUS_START ||
                                            AudioUtil.getInstance().getRecStatus() != AudioUtil.REC_STATUS.STATUS_PAUSE)) {
                                String strTime = TimeUtil.recordTime(0);
                                mBinding.includeDrawerBottomMenu.includeRecordStatus.tvRecordTime.setText(strTime);
                            }
                            break;
                        case CommandRecord.RECORD_PAUSE:
                            break;
                        case CommandRecord.RECORD_STOP:
                            break;
                    }
                    break;
                case CommandBase.COMMAND_TYPE_COLOR:
                    //颜色选择
                    CommandColor commandColor = (CommandColor) commandBase;
                    final String[] colors = getResources().getStringArray(R.array.colors_selector);
                    int color = Color.parseColor(colors[colors.length - 5]);
                    switch (commandColor.getCode()) {
                        case CommandColor.PEN_COLOR_RED:
//                            setPaintColor(ContextCompat.getColor(app, R.color.colors_menu_red));
//                            setColorsStatus(R.color.colors_menu_red);
                            showToast(R.string.paint_selected_red);
                            color = Color.parseColor(colors[colors.length - 2]);
                            setPaintColor(color);
                            break;
                        case CommandColor.PEN_COLOR_GREEN:
                            setPaintColor(ContextCompat.getColor(app, R.color.colors_menu_green));
//                            setColorsStatus(R.color.colors_menu_green);
                            showToast(R.string.paint_selected_green);
                            color = Color.parseColor(colors[colors.length - 3]);
                            setPaintColor(color);
                            break;
                        case CommandColor.PEN_COLOR_BLUE:
                            setPaintColor(ContextCompat.getColor(app, R.color.colors_menu_blue));
//                            setColorsStatus(R.color.colors_menu_blue);
                            showToast(R.string.paint_selected_blue);
                            color = Color.parseColor(colors[colors.length - 1]);
                            setPaintColor(color);
                            break;
                        case CommandColor.PEN_COLOR_BLACK:
                            setPaintColor(ContextCompat.getColor(app, R.color.colors_menu_black));
//                            setColorsStatus(R.color.colors_menu_black);
                            showToast(R.string.paint_selected_black);
                            color = Color.parseColor(colors[colors.length - 5]);
                            setPaintColor(color);
                            break;
                    }
                    getHostActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            colorSelectorAdapter.notifyDataSetChanged();
                        }
                    });

                    break;
                case CommandBase.COMMAND_TYPE_SIZE:
                    CommandSize command = (CommandSize) commandBase;
                    setPenStatusStatus(command.getCode());
                    QPenManager.getInstance().setPenSizeType(command.getCode());
                    switch (command.getCode()) {
                        case CommandSize.PEN_SIZE_ONE:
                            if (null != mStrokeView) {
                                setPenType(CommandSize.PEN_SIZE_ONE);
                                //                                        mStrokeView.setPenSize(2.0f);
                                //                                        QPenManager.getInstance().setPaintSize(2.0f);
                                showToast(R.string.paint_selected_thin_line);
                            }
                            break;
                        case CommandSize.PEN_SIZE_TWO:
                            if (null != mStrokeView) {
                                setPenType(CommandSize.PEN_SIZE_TWO);
                                //                                        mStrokeView.setPenSize(4.0f);
                                //                                        QPenManager.getInstance().setPaintSize(4.0f);
                                showToast(R.string.paint_selected_medium_line);
                            }
                            break;
                        case CommandSize.PEN_SIZE_THREE:
                            if (null != mStrokeView) {
                                setPenType(CommandSize.PEN_SIZE_THREE);
                                //                                        mStrokeView.setPenSize(6.0f);
                                //                                        QPenManager.getInstance().setPaintSize(6.0f);
                                showToast(R.string.paint_selected_thick_line);
                            }
                            break;
                    }
                    break;
            }
        }
    }

    /**
     * 初始化右侧色彩选择器
     */
    private void initColorSelectorView() {
        final String[] colors = getResources().getStringArray(R.array.colors_selector);
        colorSelectorAdapter = new ColorSelectorAdapter(getHostActivity(), colors);
//        colorBottomSelectorAdapter = new ColorBottomSelectorAdapter(getHostActivity(), colors);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getHostActivity());
/*        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.includeRightColors.rvColorSelector.setLayoutManager(layoutManager);
//        mBinding.includeRightColors.clRoot.setVisibility(View.VISIBLE);
        mBinding.includeRightColors.rvColorSelector.setAdapter(colorSelectorAdapter);
        mBinding.includeRightColors.rvColorSelector.scrollToPosition(0);
        mBinding.includeRightColors.clRoot.setVisibility(View.GONE);
        mBinding.includeRightColors.clRoot.setTag(isShowTag);*/

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.includeBmColors.rvColorBottomSelector.setLayoutManager(layoutManager);
//        mBinding.includeRightColors.clRoot.setVisibility(View.VISIBLE);
        mBinding.includeBmColors.rvColorBottomSelector.setAdapter(colorSelectorAdapter);
        mBinding.includeBmColors.rvColorBottomSelector.scrollToPosition(0);
        mBinding.includeBmColors.rlRoot.setVisibility(View.GONE);
        mBinding.includeBmColors.rlRoot.setTag(isShowTag);

        colorSelectorAdapter.setOnItemClickListener(new ColorSelectorAdapter.ColorItemClickListener() {

            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                int color = Color.parseColor(colors[position]);
                setPaintColor(color);
                colorSelectorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {

            }
        });
    }

    /**
     * 切换显示画板界面的工具
     *
     * @param res
     * @param show
     */
    private void toShowTools(int res, boolean show) {
        mBinding.includeBmColors.rlRoot.setVisibility(View.INVISIBLE);
        mBinding.includeRightColors.clRoot.setVisibility(View.INVISIBLE);
        mBinding.includeBmPenSize.rlRoot.setVisibility(View.INVISIBLE);
        mBinding.includeBmBookBgType.rlRoot.setVisibility(View.INVISIBLE);

        mBinding.includeBmColors.rlRoot.setTag(false);
        mBinding.includeRightColors.clRoot.setTag(false);
        mBinding.includeBmPenSize.rlRoot.setTag(false);
        mBinding.includeBmBookBgType.rlRoot.setTag(false);

        switch (res) {
            case R.id.fl_mic:

                break;
            case R.id.ll_colors_plate:
                if (show) {
//                    mBinding.includeRightColors.clRoot.setVisibility(View.VISIBLE);
                    mBinding.includeBmColors.rlRoot.setVisibility(View.VISIBLE);
                    int position = 0;
                    final int curColor = QPenManager.getInstance().getPaintColor();
                    final String[] colors = getResources().getStringArray(R.array.colors_selector);
                    for (String strColor : colors) {
                        final int color = Color.parseColor(strColor);
                        if (curColor == color) {
                            break;
                        }
                        position++;
                    }
                    mBinding.includeBmColors.rvColorBottomSelector.scrollToPosition(position);
                } else {
//                    mBinding.includeRightColors.clRoot.setVisibility(View.INVISIBLE);
                    mBinding.includeBmColors.rlRoot.setVisibility(View.INVISIBLE);
                }
//                mBinding.includeRightColors.clRoot.setTag(show);
                mBinding.includeBmColors.rlRoot.setTag(show);
                break;
            case R.id.ll_pen_size:
                if (show) {
                    mBinding.includeBmPenSize.rlRoot.setVisibility(View.VISIBLE);
                } else {
                    mBinding.includeBmPenSize.rlRoot.setVisibility(View.INVISIBLE);
                }
                mBinding.includeBmPenSize.rlRoot.setTag(show);
                break;
            case R.id.ll_book_bg_type:
                if (show) {
                    mBinding.includeBmBookBgType.rlRoot.setVisibility(View.VISIBLE);
                } else {
                    mBinding.includeBmBookBgType.rlRoot.setVisibility(View.INVISIBLE);
                }
                mBinding.includeBmBookBgType.rlRoot.setTag(show);
                break;
        }

    }

    private String getAESSecret(String veriCode) {
        String vcMD5 = "";
        String secretMD5 = "";
        try {
            vcMD5 = MD5.getMD5(veriCode);
            secretMD5 = MD5.getMD5(vcMD5 + MCloudConf.SECRET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (null != secretMD5 && secretMD5.length() > 16) {
            secretMD5 = secretMD5.substring(0, 16);
        }
        return secretMD5;
    }

    private void getSMSVeriCode(String mobile) {

        Map<String, String> maps = new HashMap<>();
        maps.put("random", GeneratorUtil.randomSequence(32));//随机字符串32位
        maps.put("mode", "0");//0：发送验证码到手机
        maps.put("reqType", "3");//3：彩云用户用手机号码登录并自动开户时获取短信验证码流程
        maps.put("msisdn", "+86" + mobile);//用户手机号码，与用户账号二者取其一，不可同时存在。需携带国家码
        maps.put("lang", "zh_CN");//语言
        maps.put("clientType", MCloudConf.CLIENT_TYPE);//客户端类型，3位字符串。由AAS分配

        NetCommon.aasGetVeriCode(MCloudConf.MCLOUD_AAS_URL + "tellin/getDyncPasswd.do", maps);
    }

    private void aasLogin(String mobile, String veriCode) {

        byte[] mac = DeviceInfoUtil.getMacFromHardware();
        Map<String, String> maps = new HashMap<>();
        maps.put("msisdn", mobile);//用户手机号码，与用户账号二者取其一，不可同时存在。需携带国家码
//        maps.put("random", GeneratorUtil.randomSequence(32));//随机字符串32位
//        maps.put("secinfo", "1.0");//登录系统的139号码、密码加密后的字符串，密文
        maps.put("version", "1.0");//客户端版本号，数字.数字，如12.27
        maps.put("clientType", MCloudConf.CLIENT_TYPE);//客户端类型，3位字符串。由AAS分配
        maps.put("pintype", "5");// 5：彩云短信动态密码登录
        maps.put("dycpwd", veriCode);//当pintype为5、6时，此字段填写用户从RCS系统获取的短信验证码
        maps.put("cpid", MCloudConf.CPID);//CPID，由AAS分配
        maps.put("mac", BytesConvert.bcdToString(mac));//mac

        StringBuilder sbExtInfo = new StringBuilder();
        sbExtInfo.append("<![CDATA[")
                .append("<ifOpenAccount>").append("1")
                .append("</ifOpenAccount>")
                .append("]]>");

        maps.put("extInfo", sbExtInfo.toString());//登录成功后，是否自动创建用户的标志：0：否 1：是, 如果不携带，则默认为是

        NetCommon.aasLogin(maps);
    }


    private Timer timer = new Timer();
    TimerTask task;
    private int secondLeft = 60;

    private void createTimerTask(final Button button) {
        task = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if(null!=getHostActivity()){

                    getHostActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            secondLeft--;
                            if (null != button) {
                                button.setText(secondLeft + "重新获取");
                                if (secondLeft < 0) {
                                    button.setClickable(true);
                                    button.setText("获取验证码");
                                    timer.cancel();
                                }
                            }
                        }
                    });
                }
            }
        };
    }

    /**
     * 拉起彩云App，进入笔记列表界面
     */
    private void startMCloudApp() {
        Context context = getActivity();
        if (StartAppUtil.getInstance().exist(context, MCloudConf.MCLOUD_APP_PACKAGE_NAME)) {
            StartAppUtil.getInstance().startByUri(context, MCloudConf.MCLOUD_APP_START_URI);
        } else {
            StartAppUtil.getInstance().startWebView(context, MCloudConf.MCLOUD_APP_DOWNLOAD_URL);
        }
    }
}

