package com.eningqu.aipen.manager;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.SmartPenApp;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.dialog.BaseDialog;
import com.eningqu.aipen.common.dialog.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ShareListener;
import com.eningqu.aipen.common.utils.AppInfoUtil;
import com.eningqu.aipen.common.utils.L;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.youtube.Youtube;
import io.reactivex.functions.Consumer;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/7/8 14:29
 * desc   : 分享管理类
 * version: 1.0
 */
public class ShareManager {

    public final static String TAG = ShareManager.class.getSimpleName();

    private static ShareManager instance;
//    private Activity activity;
    private FragmentManager fragmentManager;
    private RxPermissions rxPermission;

    public static ShareManager getInstance() {
        if (null == instance) {
            synchronized (ShareManager.class) {
                if (null == instance) {
                    instance = new ShareManager();
                }
            }
        }
        return instance;
    }


    private IShareCallback iShareCallback;

    public void init(@NonNull Activity activity, @NonNull IShareCallback iShareCallback) {
//        this.activity = activity;
        this.iShareCallback = iShareCallback;
        this.rxPermission = new RxPermissions(activity);
    }

    public void unInit() {
//        activity = null;
        iShareCallback = null;
        rxPermission = null;
    }

    public enum SHARE_TYPE {
        FILE_PHOTO(0), FILE_PDF(1), TEXT(2);

        private int type;

        SHARE_TYPE(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public String toString() {
            return String.valueOf(type);
        }
    }

    public interface IShareCallback {
        void onComplete(int i);

        void onError(int i);

        void onCancel(int i);
    }


    private OnekeyShare oks;
    private SHARE_TYPE format;
    private String platform;
    private String content;
    private String filePath;
    BaseDialog baseDialog;

    /***
     * 显示分享对话框
     */
    public void showShare(@NonNull FragmentManager fragmentManager, @NonNull final SHARE_TYPE format, final String msg, final String path) {
        AppCommon.isSharePageDraw = true;
        this.content = msg;
        this.filePath = path;
        toShare(platform, format, content, filePath);
    }

    /**
     * 调用系统的分享接口
     *
     * @param name
     * @param format
     */
    private void nativeToShare(int name, SHARE_TYPE format) {
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

        if (format == SHARE_TYPE.FILE_PHOTO) {
            path = AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_JPG);
//            BitmapUtil.bitmap2File(mStrokeView.getSignatureBitmap(), path, 1);
            imageIntent.setType("image/jpeg");
        } else if (format == SHARE_TYPE.FILE_PDF) {
            path = AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_PDF);
//            PdfUtil.pdfModel(mStrokeView, mStrokeView.getWidth(), mStrokeView.getHeight(), 1, path);
            imageIntent.setType("*/*");
        }
        Uri uriForFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uriForFile = FileProvider.getUriForFile(SmartPenApp.getApp(), SmartPenApp.getApp().getPackageName() +".FileProvider", new File(path));
        } else {
            uriForFile = Uri.parse(path);
        }
        imageIntent.putExtra(Intent.EXTRA_STREAM, uriForFile);


        SmartPenApp.getApp().startActivity(Intent.createChooser(imageIntent, "分享"));
    }

    /**
     * 检测是否有内存空间访问权限
     *
     * @param platform
     * @param format
     */
    private void checkPermission(final String platform, final SHARE_TYPE format) {
        this.format = format;
        this.platform = platform;
        rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    //                    toShare();
                    new CreateShareFileTask().execute(platform, format.toString());
                } else {
                    //拒绝授权
                }
            }
        });
    }

    /**
     * 异步生成文件
     */
    class CreateShareFileTask extends AsyncTask<String, Void, String> {
        String platform;
        SHARE_TYPE type;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            platform = params[0];
            String format = params[1];
            /*if ("0".equals(format)) {

                BitmapUtil.bitmap2File(mStrokeView.getSignatureBitmap(), AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_JPG), 1);
            } else if ("1".equals(format)) {

                PdfUtil.pdfModel(mStrokeView, mStrokeView.getWidth(), mStrokeView.getHeight(), 1, AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_PDF));
            }*/
            if ("0".equals(format)) {
                type = SHARE_TYPE.FILE_PHOTO;
            } else if ("1".equals(format)) {
                type = SHARE_TYPE.FILE_PDF;
            } else if ("2".equals(format)) {
                type = SHARE_TYPE.TEXT;
            }
            if (platform.equals(QQ.NAME)) {
                if (AppInfoUtil.isQQClientAvailable(SmartPenApp.getApp())) {
                    nativeToShare(2, type);
                } else {
                    Toast.makeText(SmartPenApp.getApp(), R.string.share_qq_version_too_low
                            , Toast.LENGTH_SHORT).show();
                }
            } else {

                toShare(platform, type, "", filePath);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }

    /**
     * 启动分享
     */
    private void toShare(String platform, final SHARE_TYPE format, final String content, final String filePath) {
//        if (activity == null) {
//            return;
//        }
        if (null == oks) {
            oks = new OnekeyShare();

//            // 构造一个图标
//            Bitmap enableLogo = BitmapFactory.decodeResource(SmartPenApp.getApp().getResources(), R.drawable.ssdk_oks_classic_email);
//            String label = "E-Mail";
//            View.OnClickListener listener = new View.OnClickListener() {
//                public void onClick(View v) {
//                    Intent data = new Intent(Intent.ACTION_SENDTO);
//                    data.setData(Uri.parse("mailto: "));
//                    data.putExtra(Intent.EXTRA_SUBJECT, SmartPenApp.getApp().getString(R.string.email_title));
//                    data.putExtra(Intent.EXTRA_TEXT, content);
//                    data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    if (null != SmartPenApp.getApp()) {
//                        try {
//                            SmartPenApp.getApp().startActivity(data);
//                        } catch (ActivityNotFoundException e) {
//                            ToastUtils.showShort(R.string.str_no_email);
//                        }
//                    }
//                }
//            };
//            oks.setCustomerLogo(enableLogo, label, listener);
        }


        if (!StringUtils.isEmpty(platform)) {
            oks.setPlatform(platform);
        }
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams shareParams) {
                if (SHARE_TYPE.TEXT.getType() == format.getType() && Facebook.NAME.equals(platform.getName())){
                    shareParams.setHashtag(content);
                    shareParams.setText(content);
                    shareParams.setShareType(Platform.SHARE_IMAGE);//不能缺失
                    shareParams.setUrl(content+"");
//                    shareParams.setImageUrl("");
                }else if (Youtube.NAME.equals(platform.getName())){
                    ToastUtils.showShort(R.string.share_to_youtube_not_support);
                }
            }

        });

        oks.setSite(SmartPenApp.getApp().getString(R.string.app_name));
        if (!TextUtils.isEmpty(content)) {
            oks.setText(content);
        }

        oks.setCallback(sharePlatformActionListener);
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        if (SHARE_TYPE.FILE_PHOTO == format) {
            //            AppCommon.getShareFilePath(mStrokeView.getSignatureBitmap());
            if (!TextUtils.isEmpty(filePath)) {
                oks.setImagePath(filePath);
            }
        } else if (SHARE_TYPE.FILE_PDF == format) {
            //            PdfUtil.pdfModel(mStrokeView, mStrokeView.getWidth(), mStrokeView.getHeight(), 1, Constant.SHARE_PATH_PDF);
            if (!TextUtils.isEmpty(filePath)) {
                oks.setFilePath(filePath);
            }
        }

        // 启动分享GUI
        oks.show(SmartPenApp.getApp());

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
        plat.checkPermission(sp);*/
    }

    PlatformActionListener sharePlatformActionListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            L.error(TAG, "-----------onComplete----------");
            AppCommon.isSharePageDraw = false;
//            Toast.makeText(DrawNqActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
            if (null != iShareCallback) {
                iShareCallback.onComplete(i);
            }
            if (null != baseDialog) {
                baseDialog.dismiss();
            }
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            L.error(TAG, "-----------onError----------");
            AppCommon.isSharePageDraw = false;
//            Toast.makeText(DrawNqActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
            if (null != iShareCallback) {
                iShareCallback.onError(i);
            }
            if (null != baseDialog) {
                baseDialog.dismiss();
            }
        }

        @Override
        public void onCancel(Platform platform, int i) {
            L.error(TAG, "-----------onCancel----------");
            AppCommon.isSharePageDraw = false;
//            Toast.makeText(DrawNqActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
            if (null != iShareCallback) {
                iShareCallback.onCancel(i);
            }
            if (null != baseDialog) {
                baseDialog.dismiss();
            }
        }
    };
}
