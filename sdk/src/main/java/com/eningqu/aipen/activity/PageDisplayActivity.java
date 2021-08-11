package com.eningqu.aipen.activity;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.eningqu.aipen.qpen.bean.CommandSize;
import com.eningqu.aipen.sdk.bean.NQDot;
import com.eningqu.aipen.sdk.bean.DotType;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.PageItemRVAdapter;
import com.eningqu.aipen.qpen.CanvasFrame;
import com.eningqu.aipen.qpen.GestureListener;
import com.eningqu.aipen.qpen.SDKUtil;
import com.eningqu.aipen.qpen.SignatureView;
import com.eningqu.aipen.qpen.StrokesUtilForQpen;
import com.eningqu.aipen.qpen.TouchListener;
import com.eningqu.aipen.qpen.bean.PageStrokesCacheBean;
import com.eningqu.aipen.qpen.bean.StrokesBean;
import com.eningqu.aipen.bean.OfflinePageItemData;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.enums.NoteTypeEnum;
import com.eningqu.aipen.common.utils.FileUtils;
import com.eningqu.aipen.databinding.ActivityPageDataDisplayBinding;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.db.model.PageData;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/4/25 14:58
 * desc   :数据预览
 * version: 1.0
 */
public class PageDisplayActivity extends DrawBaseActivity {
    ActivityPageDataDisplayBinding mBinding;

    /**
     * 已选择的页码
     */
    private List<Integer> mNBSelectedList = new ArrayList<>();
    /**
     * 水平滚动显示封面适配器
     */
    private PageItemRVAdapter recyAdapter;

    // 根据坐标点的页码变化统计页笔画
    private volatile List<OfflinePageItemData> mOfflinePageDatas = new ArrayList<>();

    private PageData mCurPageData;
    private PageStrokesCacheBean mCurPageStrokesCache;
    private Bitmap mCurrentBitmap;
    private List<NQDot> mOfflineDots;//离线数据
    //    private Context mContext;
    private SignatureView mPreViewStrokeView;//
    //    private CanvasFrame mPreViewCanvasFrame;

    private float DEFAULT_PAINT_SIZE = 2f;
    private int DEFAULT_PAINT_COLOR = 0;
    private final String mCurNotebookId = "nq123";
    private int mCurPageNum = 0;

    private NoteBookData mNotebookData;
    private boolean firstInit = true;//首次进入界面加载
    private int mFirstVisible;//recycleView   第一个可见的item
    private int mLastVisible;//recycleView  最后一个可见的item

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 2) {
                if (mOfflinePageDatas.size() > 1 && mLastVisible < mOfflinePageDatas.size()) {

                    for (int i = mFirstVisible; i <= mLastVisible; i++) {
                        mStrokeView.clearPaint();
                        mStrokeView.invalidate();
                        OfflinePageItemData itemData = mOfflinePageDatas.get(i);
                        if (null == itemData.getDrawable()) {
                            loadStrokes(mStrokeView, itemData);
                            mStrokeView.invalidate();
                            if (null != mStrokeView.getSignatureBitmap()) {
                                WeakReference<Bitmap> bitmapWeakReference = new WeakReference<>(mStrokeView.getSignatureBitmap());
                                itemData.setDrawable(bitmapWeakReference);
                            }
                        }
                    }
                    recyAdapter.notifyDataSetChanged();
                    updateView();
                } else if (mOfflinePageDatas.size() == 1) {
                    mStrokeView.clearPaint();
                    mStrokeView.invalidate();
                    OfflinePageItemData itemData = mOfflinePageDatas.get(0);
                    if (null == itemData.getDrawable()) {
                        loadStrokes(mStrokeView, itemData);
                        mStrokeView.invalidate();
                        if (null != mStrokeView.getSignatureBitmap()) {
                            WeakReference<Bitmap> bitmapWeakReference = new WeakReference<>(mStrokeView.getSignatureBitmap());
                            itemData.setDrawable(bitmapWeakReference);
                        }
                    }
                    updateView();
                    mHandler.sendEmptyMessageDelayed(3, 300);
                }

            } else if (msg.what == 3) {
                recyAdapter.notifyItemChanged(0);
            }
        }
    };

    @Override
    protected void setLayout() {
        mContext = this;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_page_data_display);
    }

    @Override
    protected void initData() {
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        if (bundle != null)
//            mCurNotebookId = bundle.getString(BaseActivity.NOTEBOOK_ID);
//        if (null != mCurNotebookId && !"".equals(mCurNotebookId)) {
//            mNotebookData = AppCommon.selectNotebook(mCurNotebookId);
//        }
        //离线数据默认的笔画大小和颜色
        DEFAULT_PAINT_SIZE = 2f;
        DEFAULT_PAINT_COLOR = ContextCompat.getColor(mContext, R.color.color_000000);
        //加载当前页的笔画
        mCurPageStrokesCache = StrokesUtilForQpen.getStrokes(new File(AppCommon.getStrokesPath(mCurNotebookId, mCurPageNum)));
        // 获取离线数据
        if (null != mOfflinePageDatas) {
            mOfflinePageDatas.clear();
        }

        //加载指定目录下的所有数据

        File dir = new File(AppCommon.NQ_SAVE_SDCARD_PATH + "/" + mCurNotebookId + "/");
        if (null != dir && dir.exists() && dir.isDirectory()) {

            List<File> files = FileUtils.getAllFiles(dir);
            if (null != files && files.size() > 0) {

                for (File file : files) {

                    PageStrokesCacheBean bean = StrokesUtilForQpen.getStrokes(file);
                    if (null != bean) {

                        OfflinePageItemData offlinePageItemData = new OfflinePageItemData(mContext, AppCommon.getUserUID(), mCurNotebookId, bean.getPage(), 1, "1");
                        offlinePageItemData.getStrokesBeans().addAll(bean.getStrokesBeans());
                        mOfflinePageDatas.add(offlinePageItemData);
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView() {
        //        mBinding.includeMainTopMenu.tvTitle.setText(R.string.offline_data_preview);
//        mBinding.includeMainTopMenu.tvRight.setText(R.string.sync);
        // 获取颜色资源文件
//        int myColor = getResources().getColor(R.color.text_color_green);
//        mBinding.includeMainTopMenu.tvRight.setTextColor(myColor);
        initRecycleView();
        initDrawBroad();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void onViewClick(View view) {
        if (view.getId() == R.id.iv_back) {
            finish();
        }
    }

    /**
     * 初始化水平滚动显示笔记本的rv
     */
    private void initRecycleView() {
        recyAdapter = new PageItemRVAdapter(this);
        recyAdapter.setList(mOfflinePageDatas, PageItemRVAdapter.VIEW_TYPE_PAGE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.rvNoteBook.setLayoutManager(layoutManager);
        mBinding.rvNoteBook.addOnScrollListener(recyclerScrollListener);

        recyAdapter.setOnItemClickListener(new PageItemRVAdapter.OnRecyclerViewItemClickListener() {

            @Override
            public void onItemClick(View view, int position, Object check) {
                //                L.info("onItemClick() position=" + position + ", check=" + check);
                //                ImageView imageView = (ImageView) view.findViewById(R.id.iv_select_state);
                if (null != mOfflinePageDatas && mOfflinePageDatas.size() > 0 && position < mOfflinePageDatas.size() && position >= 0) {

                    boolean state = (boolean) check;

                    if (state) {
                        //选择状态
                        final OfflinePageItemData offlinePageItemData = mOfflinePageDatas.get(position);
                        final int pageNum = offlinePageItemData.getPage();

                        //第一次选择
                        if (mNBSelectedList.size() == 0) {
                            //加载当前页的笔画
                            if (null != mCurPageStrokesCache && null != mCurPageStrokesCache.getStrokesBeans()) {
                                mCurPageStrokesCache.getStrokesBeans().clear();
                            }
                            mCurPageNum = pageNum;
                            mNBSelectedList.add(position);
                            mCurPageStrokesCache = StrokesUtilForQpen.getStrokes(new File(AppCommon.getStrokesPath(mCurNotebookId, mCurPageNum)));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (null != mStrokeView) {
                                        mStrokeView.clearPaint();
                                        mStrokeView.invalidate();
                                    }
                                    loadStrokes(mStrokeView, mCurPageStrokesCache);
                                }
                            });
                        } else {
                            if (pageNum != mCurPageNum) {

                                //不是已选的页码
                                offlinePageItemData.setCheck(!state);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int page = mCurPageNum;
                                        ToastUtils.showShort(String.format(getString(R.string.slecte_page_to_sync), page));
                                        recyAdapter.notifyDataSetChanged();
                                    }
                                });
                                return;
                            } else {
                                //是已选的页码，则保存该页的索引值
                                int size = mNBSelectedList.size();
                                for (int i = 0; i < size; i++) {
                                    if (position == mNBSelectedList.get(i)) {
                                        mNBSelectedList.remove(i);
                                        break;
                                    }
                                }
                                mNBSelectedList.add(position);
                            }
                        }

                    } else {
                        // 非选择状态
                        //如果已选的列表中只有一页
                        if (mNBSelectedList.size() == 1) {
                            mNBSelectedList.clear();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (null != mStrokeView) {
                                        mStrokeView.clearPaint();
                                        mStrokeView.invalidate();
                                    }
                                    recyAdapter.notifyDataSetChanged();
                                }
                            });

                            mCurPageNum = 0;
                        } else {

                            int size = mNBSelectedList.size();
                            for (int i = 0; i < size; i++) {
                                if (position == mNBSelectedList.get(i)) {
                                    mNBSelectedList.remove(i);
                                    break;
                                }
                            }
                        }
                    }

                    updateView();
                }
            }
        });
        mBinding.rvNoteBook.setAdapter(recyAdapter);
    }

    private void updateView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyAdapter.notifyDataSetChanged();
                int size = mNBSelectedList.size();
                //重绘
                if (null != mStrokeView) {
                    mStrokeView.clearPaint();
                    mStrokeView.invalidate();
                }
                if (size > 0) {
                    loadStrokes(mStrokeView, mCurPageStrokesCache);
                }
                for (int i = 0; i < size; i++) {
                    int position = mNBSelectedList.get(i);
                    final PageStrokesCacheBean strokesCache = mOfflinePageDatas.get(position);
                    //加载选择页的离线笔画
                    loadStrokes(mStrokeView, strokesCache);
                }
            }
        });
    }

    private void initDrawBroad() {
        mBinding.llDrawBoard.post(new Runnable() {
            public void run() {
                //                L.info("initDrawBroad()");
                canvasFrame = new CanvasFrame(mContext);
                mStrokeView = canvasFrame.bDrawl;
                mStrokeView.setZOrderOnTop(true); // 在最顶层，会遮挡一切view
                mStrokeView.setZOrderMediaOverlay(true);// 如已绘制SurfaceView则在surfaceView上一层绘制。
                Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.page_bg1);
                mStrokeView.setSignatureBitmap(bmp);
                //                if (null != AppCommon.getCurrentBitmap()) {
                //                    mStrokeView.setSignatureBitmap(AppCommon.getCurrentBitmap());
                //                }
                mBinding.llDrawBoard.addView(canvasFrame);

                //                AppCommon.setStrokeView(mStrokeView);
                //                AppCommon.setDrawBroadWidthHeight(mBinding.llDrawBoard.getWidth(), mBinding.llDrawBoard.getHeight());
                canvasFrame.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                canvasFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                mBinding.llDrawBoard.setOnTouchListener(new TouchListener(canvasFrame, mBinding.llDrawBoard.getWidth(),
                                        mBinding.llDrawBoard.getHeight(), canvasFrame.getMeasuredWidth(), canvasFrame.getMeasuredHeight(), gestureListener));
                            }
                        });
                //                L.error("loadPageData()");
                //                if(null!=mCurPageStrokesCache){
                //                    paintCurPageStrokes(mCurPageStrokesCache);
                //                }
            }
        });
    }

    GestureListener gestureListener = new GestureListener() {
        @Override
        public void leftDirect() {
            //            int curPage = AppCommon.getCurrentPage();
            //            switchPageByPageNum(curPage + 1);
        }

        @Override
        public void rightDirect() {
            //            int curPage = AppCommon.getCurrentPage();
            //            if (curPage > 1) {
            //                switchPageByPageNum(curPage - 1);
            //            }
        }
    };

    protected void loadStrokes(SignatureView strokeView, PageStrokesCacheBean pageStrokesCache) {

        if (null != pageStrokesCache && pageStrokesCache.getStrokesBeans().size() > 0) {
            //            L.error("paintCurPageStrokes() begin");
            int i;
            //一笔一笔转换，包括每一笔的颜色和粗细大小、坐标
            int book_height = null != mNotebookData ? mNotebookData.yMax : (int) SDKUtil.PAGER_HEIGHT_A5;
            int book_width = null != mNotebookData ? mNotebookData.xMax : (int) SDKUtil.PAGER_WIDTH_A5;
            int book_no = null != mNotebookData ? mNotebookData.noteType : NoteTypeEnum.NOTE_TYPE_A5.getNoeType();
            for (StrokesBean strokesBean : pageStrokesCache.getStrokesBeans()) {
                i = 0;
                List<Point> points = strokesBean.getDots();
                if (null != strokeView) {
                    strokeView.setPenSize(CommandSize.getSizeByLevel(strokesBean.getSizeLevel()));
                    strokeView.setPenColor(strokesBean.getColor());
                }

                for (int j = 0; j < points.size(); j++) {
                    NQDot afDot = new NQDot();
                    if (j == 0) {
                        afDot.type = DotType.PEN_ACTION_DOWN;
                    } else if (j == points.size() - 1) {
                        afDot.type = DotType.PEN_ACTION_UP;
                    } else {
                        afDot.type = DotType.PEN_ACTION_MOVE;
                    }
                    afDot.x = points.get(j).x;
                    afDot.y = points.get(j).y;
                    afDot.book_height = (book_height == 0 ? (int) SDKUtil.PAGER_HEIGHT_A5 : book_height);
                    afDot.book_width = (book_width == 0 ? (int) SDKUtil.PAGER_WIDTH_A5 : book_width);
                    afDot.bookNum = book_no;
                    afDot.page = pageStrokesCache.getPage();

                    if (null != strokeView) {
                        strokeView.addDot(afDot, false);
                    }
                }

            }
            //            L.error("paintCurPageStrokes() end");
        }
    }

    private RecyclerView.OnScrollListener recyclerScrollListener = new RecyclerView.OnScrollListener() {
        //RecyclerVew
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                mHandler.sendEmptyMessage(2);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null) {
                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                int visibleItemCount = lastVisible - firstVisible;
                mFirstVisible = firstVisible;
                mLastVisible = lastVisible;

                if (lastVisible == 0) {
                    visibleItemCount = 0;
                }
                if (visibleItemCount != 0) {
                    //                    dealScrollEvent(firstVisible, lastVisible);
                    /*for (int i = firstVisible; i <= lastVisible; i++) {
                        OfflinePageItemData itemData = mOfflinePageDatas.get(i);
                        loadStrokes(mStrokeView, itemData);
                        mStrokeView.invalidate();
                        itemData.setDrawable(BitmapUtil.bitmap2Drawable(mContext, mStrokeView.getSignatureBitmap()));
                        mStrokeView.clearPaint();
                    }*/

                    if (mOfflinePageDatas.size() > 10) {

                        for (int j = 0; j < firstVisible; j++) {
                            OfflinePageItemData itemData = mOfflinePageDatas.get(j);
                            itemData.setDrawable(null);
                        }

                        if (lastVisible < mOfflinePageDatas.size()) {

                            for (int j = lastVisible + 1; j < mOfflinePageDatas.size(); j++) {
                                OfflinePageItemData itemData = mOfflinePageDatas.get(j);
                                itemData.setDrawable(null);
                            }
                        }
                    }
                    if (firstInit) {
                        //                        L.error("scroll");
                        mHandler.sendEmptyMessage(2);
                        firstInit = false;
                    }
                } else if (mOfflinePageDatas.size() == 1) {
                    if (firstInit) {
                        //                        L.error("scroll");
                        mHandler.sendEmptyMessage(2);
                        firstInit = false;
                    }
                }
            }
        }
    };
}
