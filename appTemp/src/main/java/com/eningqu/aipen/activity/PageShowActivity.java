package com.eningqu.aipen.activity;

import android.content.Intent;
import android.os.Message;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.dialog.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.thread.ThreadPoolUtils;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.db.model.NoteBookData_Table;
import com.eningqu.aipen.db.model.PageData;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.adapter.PageViewPagerAdapter;
import com.eningqu.aipen.db.model.PageData_Table;
import com.eningqu.aipen.db.model.PageLabelData;
import com.eningqu.aipen.db.model.PageLabelData_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/3 11:03
 */
@Deprecated
public class PageShowActivity extends BaseActivity {

    private final static String TAG = PageShowActivity.class.getSimpleName();
    @BindView(R.id.tv_title)
    TextView mNoteName;
    @BindView(R.id.tv_page_num)
    TextView pageNumText;
    @BindView(R.id.tv_create_time)
    TextView createTime;
    @BindView(R.id.tv_page_labels)
    TextView labelsText;
    @BindView(R.id.vp_note_page)
    ViewPager mNotePage;
   /* @BindView(R.id.vp_note_page)
    RecyclerView mNotePage;*/


    int mDistances = 0;
    private int itemWidth;
    boolean mNoNeedToScroll = false;
    boolean mDragging = false;
    boolean mIdle = false;
    private int itemCount;
    int padding = 15;
    int left_right = 10;
    int mCurrentPosition = 0;

    private PageViewPagerAdapter mPageViewPagerAdapter;
    private List<PageData> dataList = Collections.emptyList();
    private List<PageLabelData> labelsList = Collections.emptyList();

    private String noteName;
    private int noteType;

    /**
     * 当前viewpager选中展示的第几个下标  默认是第一个
     */
    //private int currentSelectPosition = 0;
    @Override
    protected void setLayout() {
        setContentView(R.layout.activity_page_show);
    }

    @Override
    protected void initView() {
        int pagerWidth = (int) (getResources().getDisplayMetrics().widthPixels * 3.0f / 5.0f);
        mPageViewPagerAdapter = new PageViewPagerAdapter(this, dataList);
        mNotePage.setAdapter(mPageViewPagerAdapter);

        ViewGroup.LayoutParams lp = mNotePage.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(pagerWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp.width = pagerWidth;
        }
        mNotePage.setOffscreenPageLimit(3);
        mNotePage.setLayoutParams(lp);
        mNotePage.setPageMargin(-30);
        mNotePage.setPageTransformer(true, new MyTransformation());
        mNotePage.addOnPageChangeListener(new MyOnPageChangeListener());
        mNoteName.setText(noteName);
        if (dataList.size() > 0) {
            setText(0);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        refreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        noteName = intent.getStringExtra("noteName");
        noteType = intent.getIntExtra("noteType", -1);

        loadData();
    }

    @Override
    protected void initEvent() {

    }

    @OnClick({R.id.iv_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListner(final Message message) {
        switch (message.what) {
            case Constant.PAGE_LONG_DELETE_CODE:
                dialog = DialogHelper.showDelete(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        String pageId = (String) message.obj;
                        int noteType = message.arg1;
                        deletePage(pageId, noteType);
                        dismissDialog();
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                    }
                }, R.string.confirm_delete_page_text, 0);
                break;

            case Constant.PAGE_CLICK_CODE://TODO
                if (!isFirst) {
                    isFirst = true;
                    //                    AppCommon.switchPageData(message.arg1, message.arg2);
                    AppCommon.setCurrentPage(message.arg2);
                    //                    AppCommon.loadPageData(message.arg1, message.arg2);
                    //                    Intent intent = new Intent(this, DrawNqActivity.class);
                    //                    startActivity(intent);
                }
                break;
        }
    }

    public static boolean isFirst = false;

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

    /**
     * 加载数据 和 对应标签
     */
    private void loadData() {
        dataList = SQLite
                .select(PageData_Table.id,
                        PageData_Table.pageNum,
                        PageData_Table.noteBookId,
                        PageData_Table.noteType,
                        PageData_Table.isLock,
                        PageData_Table.lastModifyTime,
                        PageData_Table.picUrl,
                        PageData_Table.userUid)
                .from(PageData.class)
                .where(PageData_Table.noteType.eq(noteType),
                        PageData_Table.isLock.eq(false),
                        PageData_Table.userUid.eq(AppCommon.getUserUID()))
                // true为'ASC'正序, false为'DESC'反序
                .orderBy(PageData_Table.pageNum, true)
                .queryList();

        labelsList = SQLite.select()
                .from(PageLabelData.class)
                .queryList();
    }

    private void refreshData() {
        //若笔记本类型相同 则重载数据
        if (noteType == AppCommon.getCurrentNoteType()) {
            //重新加载数据
            loadData();
            int changePosition = -1;
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).pageNum == AppCommon.getCurrentPage()) {
                    changePosition = i;
                    break;
                }
            }
            mPageViewPagerAdapter = new PageViewPagerAdapter(this, dataList);
            mNotePage.setAdapter(mPageViewPagerAdapter);
            mNotePage.setPageTransformer(true, new MyTransformation());
            mNotePage.setCurrentItem(changePosition);
            setText(changePosition == -1 ? 0 : changePosition);
        }
    }

    /**
     * 删除当前页
     */
    private void deletePage(final String pageId, final int noteType) {

        //查询未收藏的笔记本
        final NoteBookData noteBook = SQLite.select().
                from(NoteBookData.class)
                .where(NoteBookData_Table.noteType.eq(noteType),
                        NoteBookData_Table.isLock.eq(false),
                        NoteBookData_Table.userUid.eq(AppCommon.getUserUID()))
                .querySingle();

        //删除当前页
        SQLite.delete()
                .from(PageData.class)
                .where(PageData_Table.pageId.eq(pageId),
                        PageData_Table.userUid.eq(AppCommon.getUserUID()))
                .query();

        //删除当前页所属的页签
        SQLite.delete()
                .from(PageLabelData.class)
                .where(PageLabelData_Table.pageId.eq(pageId)).query();

        //若当前笔记本只有1页数据时，则删除笔记本
        if (dataList.size() == 1) {
            SQLite.delete(NoteBookData.class)
                    .where(NoteBookData_Table.noteType.eq(noteType),
                            NoteBookData_Table.isLock.eq(false),
                            NoteBookData_Table.userUid.eq(AppCommon.getUserUID()))
                    .query();

            //删除本地笔记本下的所有图片
            ThreadPoolUtils.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    StringBuilder sb = new StringBuilder();
                    sb.append(AppCommon.NQ_SAVE_ROOT_PATH)
                            .append(AppCommon.FILE_SEPARATOR)
                            .append(TimeUtils.date2Millis(TimeUtils.string2Date(noteBook.createTime)) / 1000 + "")
                            .append("_")
                            .append(noteType);
                    FileUtils.delete(sb.toString());
                }
            });

            //重置当前书写页
            AppCommon.drawReset();

            finish();
            return;
        }

        int currentSelectPosition = 0;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).pageId.equals(pageId)) {
                currentSelectPosition = i;
                break;
            }
        }

        //删除当前页的缩略图
        if (noteBook != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(AppCommon.NQ_SAVE_ROOT_PATH)
                    .append(AppCommon.FILE_SEPARATOR)
                    .append(TimeUtils.date2Millis(TimeUtils.string2Date(noteBook.createTime)) / 1000 + "")
                    .append("_")
                    .append(noteType)
                    .append(AppCommon.FILE_SEPARATOR)
                    .append(dataList.get(currentSelectPosition).pageNum)
                    .append(AppCommon.SUFFIX_NAME_JPG);
            final String path = sb.toString();
            ThreadPoolUtils.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    FileUtils.delete(path);
                }
            });
        }

        //重置当前书写页
        AppCommon.drawReset();
        //刷新数据
        resetData(pageId, currentSelectPosition);


    }

    /***
     * 删除以后刷新数据
     * @param pageId
     */
    public void resetData(final String pageId, int currentSelectPosition) {

        Iterator<PageData> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            PageData item = iterator.next();
            if (item.pageId.equals(pageId)) {
                iterator.remove();
                break;
            }
        }

        int size = dataList.size();
        if (currentSelectPosition == (size + 1)) {
            currentSelectPosition = size;
        } else if (currentSelectPosition == size) {
            currentSelectPosition = currentSelectPosition - 1;
        }

        mPageViewPagerAdapter = new PageViewPagerAdapter(this, dataList);
        mNotePage.setAdapter(mPageViewPagerAdapter);
        mNotePage.setPageTransformer(true, new MyTransformation());
        mNotePage.setCurrentItem(currentSelectPosition);

        ToastUtils.showShort(R.string.delete_success);

        setText(currentSelectPosition == -1 ? 0 : currentSelectPosition);
    }

    /**
     * 设置文本显示
     *
     * @param position
     */
    private void setText(int position) {
        PageData pageData = dataList.get(position);
        pageNumText.setText(String.valueOf(pageData.pageNum));
        createTime.setText(pageData.lastModifyTime);
        StringBuilder sb = new StringBuilder("");
        for (PageLabelData label : labelsList) {
            if (label.pageId.equals(pageData.pageId)) {
                sb.append(label.getLabelName()).append(",");
            }
        }
        labelsText.setText(sb.toString().indexOf(",") > -1 ? sb.toString().substring(0, sb.toString().lastIndexOf(",")) : sb.toString());
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
