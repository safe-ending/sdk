package com.eningqu.aipen.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import androidx.annotation.Nullable;
import com.eningqu.aipen.common.utils.L;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.dialog.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.dialog.listener.ShareListener;
import com.eningqu.aipen.common.utils.BitmapUtil;
import com.eningqu.aipen.db.model.PageData;
import com.eningqu.aipen.db.model.PageData_Table;
import com.eningqu.aipen.db.model.PageLabelData;
import com.eningqu.aipen.db.model.PageLabelData_Table;
import com.eningqu.aipen.view.TouchImageView;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import io.reactivex.functions.Consumer;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/8 15:21
 */

public class CollectDrawActivity extends BaseActivity implements PlatformActionListener {

    private static final String TAG = CollectDrawActivity.class.getSimpleName();

    @BindView(R.id.layout_label)
    LinearLayout layoutLabel;

    @BindView(R.id.ll_top)
    RelativeLayout layoutTop;

    @BindView(R.id.iv_draw_board)
    TouchImageView imageView;

    @BindView(R.id.tv_page_num)
    TextView pageTV;

    @BindView(R.id.tv_label)
    TextView labelTV;

    private boolean isShowLabel = false;

    private String pageId;
    private int page;
    private Bitmap bitmap;

    private List<PageLabelData> labelDataList;
    private RxPermissions rxPermission;
    public static boolean isOpen = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        isOpen = true;
    }

    @Override
    protected void setLayout() {
        setContentView(R.layout.activity_collect_draw);
    }

    @Override
    protected void initView() {
        rxPermission = new RxPermissions(CollectDrawActivity.this);
        labelTV = findViewById(R.id.tv_label);
        imageView.setMaxZoom(2.0f);
        imageView.setImageBitmap(bitmap);
        pageTV.setText(page + "");
        loadLabel();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        pageId = bundle.getString(Constant.PAGE_NUM_ID);
        PageData pageData = SQLite.select()
                .from(PageData.class)
                .where(PageData_Table.pageId.eq(pageId),
                        PageData_Table.userUid.eq(AppCommon.getUserUID()))
                .querySingle();
        if (pageData != null) {
            page = pageData.pageNum;
            bitmap = BitmapFactory.decodeByteArray(pageData.data, 0, pageData.data.length);
//            labelDataList = pageData.getLabels();
        }else{
            finish();
        }
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fileList();
        isOpen =false;
    }

    @OnClick({R.id.iv_back,
            R.id.iv_label_add,
            R.id.iv_draw_board,
            R.id.iv_share})
    public void onViewClick(View view) {
        switch (view.getId()) {

            case R.id.iv_back:
                finish();
                break;

            case R.id.iv_label_add:
                dialog = DialogHelper.showLabel(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        EditText text = (EditText) view;
                        if (!StringUtils.isEmpty(text.getText())) {
                            dismissDialog();
                            saveLabel(text.getText().toString());
                        } else {
                            ToastUtils.showLong(R.string.dialog_edit_tips);
                        }
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                    }
                });
                break;

            case R.id.iv_draw_board:

                if (isShowLabel) {
                    layoutLabel.setVisibility(View.GONE);
                    layoutTop.setVisibility(View.INVISIBLE);
                    isShowLabel = false;
                } else {
                    layoutLabel.setVisibility(View.VISIBLE);
                    layoutTop.setVisibility(View.VISIBLE);
                    isShowLabel = true;
                }

                break;

            case R.id.iv_share:
                showShare();
                break;
        }
    }

    /**
     * 设置页签显示
     * @param labelName
     */
    private void setLabel(String labelName) {
        if(!StringUtils.isEmpty(labelName)){
            labelTV.setText(labelName);
        }else{
            labelTV.setText(R.string.label_empty_text);
        }
    }

    /**
     * 加载页签
     */
    private void loadLabel() {
        if (labelDataList != null && labelDataList.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (PageLabelData pageLabelData : labelDataList) {
                sb.append(pageLabelData.getLabelName()).append(",");
            }
            setLabel(sb.toString().contains(",") ? sb.toString().substring(0, sb.toString().length() - 1) : sb.toString());
        } else {
            setLabel(null);
        }
    }

    /**
     * 保存页签
     * @param labelName
     */
    private void saveLabel(String labelName) {
        PageLabelData pageLabel = SQLite.select().from(PageLabelData.class).where(PageLabelData_Table.pageId.eq(pageId)).querySingle();
        if(pageLabel == null){
            pageLabel = new PageLabelData();
            pageLabel.setPageId(pageId);
            pageLabel.setLabelName(labelName);
            pageLabel.insert();
        }else{
            SQLite.update(PageLabelData.class)
                    .set(PageLabelData_Table.labelName.eq(labelName))
                    .where(PageLabelData_Table.pageId.eq(pageId))
                    .query();
        }
        setLabel(labelName);
        ToastUtils.showShort(R.string.label_save_success);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Message message) {
        switch (message.what) {

        }
    }

    /***
     * 分享
     */
    private void showShare() {
        dialog = DialogHelper.showShare(0 , getSupportFragmentManager(), new ShareListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.wechat:
                        share(Wechat.NAME);
                        break;
                    case R.id.wechatmoments:
                        share(WechatMoments.NAME);
                        break;
                    case R.id.qq:
                        share(QQ.NAME);
                        break;
                    case R.id.qzone:
                        share(QZone.NAME);
                        break;
                    case R.id.sinaweibo:
                        share(SinaWeibo.NAME);
                        break;
                    case R.id.facebook:
                        share(Facebook.NAME);
                        break;
                    case R.id.twitter:
                        share(Twitter.NAME);
                        break;
                }
            }

            @Override
            public void onCancel() {
                dismissDialog();
            }
        });
    }


    private void share(final String platform){
        rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
//                    BitmapUtil.bitmap2File(BitmapUtil.getBitmapByView(imageView), Constant.SHARE_PATH_JPG, 0);
                    String path = AppCommon.getSharePath(AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), AppCommon.SUFFIX_NAME_JPG);
                    BitmapUtil.bitmap2File(BitmapUtil.getBitmapByView(imageView), path, 1);
                    OnekeyShare oks = new OnekeyShare();
                    if(!StringUtils.isEmpty(platform)){
                        oks.setPlatform(platform);
                    }
                    //关闭sso授权
                    oks.disableSSOWhenAuthorize();
                    oks.setImagePath(path);
                    // 启动分享GUI
                    oks.show(CollectDrawActivity.this);
                } else {
                    //拒绝授权
                }
            }
        });
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        L.error(TAG, "-----------onComplete----------");
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        L.error(TAG, "-----------onError----------");
    }

    @Override
    public void onCancel(Platform platform, int i) {
        L.error(TAG, "-----------onCancel----------");
    }
}
