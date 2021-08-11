package com.eningqu.aipen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.db.model.PageData;
import com.eningqu.aipen.db.model.PageData_Table;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.adapter.CollectPageAdapter;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/6 15:34
 */

public class CollectPageShowActivity extends BaseActivity{

    private final static String TAG = CollectPageShowActivity.class.getSimpleName();

    TextView mNoteName;
    ViewPager mNotePage;
    TextView pageNum;
    TextView createTime;
    TextView labels;

    private String noteName;
    private String noteBookId;

    private CollectPageAdapter collectPageAdapter;
    private List<PageData> dataList;
    private boolean isFist;

    @Override
    protected void setLayout() {
        setContentView(R.layout.activity_collect_page);
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mNoteName = findViewById(R.id.tv_title);
        mNotePage = findViewById(R.id.vp_note_page);
        pageNum = findViewById(R.id.tv_page_num);
        createTime = findViewById(R.id.tv_create_time);
        labels = findViewById(R.id.tv_page_labels);

        collectPageAdapter = new CollectPageAdapter(this, dataList);
        mNotePage.setAdapter(collectPageAdapter);
        mNotePage.setOffscreenPageLimit(3);
        int pagerWidth = (int) (getResources().getDisplayMetrics().widthPixels * 3.0f / 5.0f);
        ViewGroup.LayoutParams lp = mNotePage.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(pagerWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp.width = pagerWidth;
        }
        mNotePage.setLayoutParams(lp);
        mNotePage.setPageMargin(-30);
        mNotePage.setPageTransformer(true, new MyTransformation());
        mNotePage.addOnPageChangeListener(new MyOnPageChangeListener());

        mNoteName.setText(noteName);
        if (dataList.size() > 0) {
            setText(0);
        }

        mNoteName.setText(noteName);
    }

    @Override
    protected void initData() {
        isFist = true;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        noteName = bundle.getString("noteName");
        noteBookId = bundle.getString("noteBookId", "");
        dataList = SQLite.select(PageData_Table.id,
                PageData_Table.pageNum,
                PageData_Table.noteBookId,
                PageData_Table.noteType,
                PageData_Table.isLock,
                PageData_Table.lastModifyTime,
                PageData_Table.picUrl,
                PageData_Table.userUid)
                .from(PageData.class)
                .where(PageData_Table.noteBookId.eq(noteBookId),
                        PageData_Table.isLock.eq(true),
                        PageData_Table.userUid.eq(AppCommon.getUserUID()))
                .orderBy(PageData_Table.pageNum, true)
                .queryList();
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFist){
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            noteName = bundle.getString("noteName");
            noteBookId = bundle.getString("noteBookId", "");
            dataList = SQLite.select(PageData_Table.id,
                    PageData_Table.pageNum,
                    PageData_Table.noteBookId,
                    PageData_Table.noteType,
                    PageData_Table.isLock,
                    PageData_Table.lastModifyTime,
                    PageData_Table.picUrl,
                    PageData_Table.userUid)
                    .from(PageData.class)
                    .where(PageData_Table.noteBookId.eq(noteBookId),
                            PageData_Table.isLock.eq(true),
                            PageData_Table.userUid.eq(AppCommon.getUserUID()))
                    .orderBy(PageData_Table.pageNum, true)
                    .queryList();

            mNoteName.setText(noteName);
            if (dataList.size() > 0) {
                setText(0);
            }

            mNoteName.setText(noteName);
            collectPageAdapter.notifyDataSetChanged();
        }
        isFist = false;
    }

    private void setText(int position){
        pageNum.setText(String.valueOf(dataList.get(position).pageNum));
        createTime.setText(dataList.get(position).lastModifyTime);
        StringBuilder sb = new StringBuilder("");
//        for (PageLabelData label : dataList.get(position).getLabels()){
//            recoResultSB.append(label.getLabelName()).append(",");
//        }
        labels.setText(sb.toString().contains(",") ?sb.toString().substring(0, sb.toString().lastIndexOf(",")) : sb.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListner(final Message message) {
        switch (message.what) {
            case Constant.PAGE_LABEL_CLICK_CODE:
                Bundle bundle = message.getData();
                String pageId = bundle.getString(Constant.PAGE_NUM_ID);
                PageData pageData = SQLite.select().from(PageData.class).where(PageData_Table.noteBookId.eq(pageId)).querySingle();
                if (pageData != null&&!CollectDrawActivity.isOpen) {
                    gotoActivity(CollectDrawActivity.class, bundle);
                }
                break;
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            setText(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    }

    private static class MyTransformation implements ViewPager.PageTransformer {

        private static final float MIN_SCALE = 0.8f;
        private static final float MIN_ALPHA = 0.5f;
        private static final float MAX_ROTATE = 30;

        @Override
        public void transformPage(View page, float position) {
            float centerX = page.getWidth() / 2;
            float centerY = page.getHeight() / 2;
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float rotate = 20 * Math.abs(position);
            if (position < -1) {

            } else if (position < 0) {
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setRotationY(rotate);
            } else if (position >= 0 && position < 1) {
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setRotationY(-rotate);
            } else if (position >= 1) {
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
                page.setRotationY(-rotate);
            }
        }
    }
}
