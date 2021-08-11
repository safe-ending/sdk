package com.eningqu.aipen.activity;

import android.content.Intent;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.eningqu.aipen.sdk.bean.NQDot;
import com.eningqu.aipen.sdk.bean.DotType;
import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.PageItemRVAdapter;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.CanvasFrame;
import com.eningqu.aipen.qpen.bean.CommandSize;
import com.nq.edusaas.hps.sdkcore.afpensdk.Const;
import com.eningqu.aipen.qpen.GestureListener;
import com.eningqu.aipen.qpen.SDKUtil;
import com.eningqu.aipen.qpen.SignatureView;
import com.eningqu.aipen.qpen.StrokesUtilForQpen;
import com.eningqu.aipen.qpen.TouchListener;
import com.eningqu.aipen.qpen.bean.PageStrokesCacheBean;
import com.eningqu.aipen.qpen.bean.StrokesBean;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.bean.OfflinePageItemData;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.dialog.DialogHelper;
import com.eningqu.aipen.common.enums.NoteTypeEnum;
import com.eningqu.aipen.common.thread.ThreadPoolUtils;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.databinding.ActivityOfflinePageDataDisplayBinding;
import com.eningqu.aipen.db.model.NoteBookData;
import com.nq.edusaas.hps.PenSdkCtrl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/4/25 14:58
 * desc   :离线数据预览
 * version: 1.0
 */
public class OfflinePageDisplayActivity extends DrawBaseActivity {
    ActivityOfflinePageDataDisplayBinding mBinding;

    public final static String TAG = OfflinePageDisplayActivity.class.getSimpleName();
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

    private PageStrokesCacheBean mCurPageStrokesCache;
    private List<NQDot> mOfflineDots;//离线数据

    private float DEFAULT_PAINT_SIZE = 2f;
    private int DEFAULT_PAINT_COLOR = 0;
    private String mCurNotebookId;
    private int mCurPageNum = 0;

    private NoteBookData mNotebookData;
    private boolean firstInit = true;//首次进入界面加载
    private int mFirstVisible;//recycleView   第一个可见的item
    private int mLastVisible;//recycleView  最后一个可见的item

    public static final int REQUEST_CODE_OFFLINE = 2;
    long lastTime = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 2) {

                if (mOfflinePageDatas.size() >= 1 && mLastVisible < mOfflinePageDatas.size()) {
                    L.info(TAG, "mOfflinePageDatas.size=" + mOfflinePageDatas.size());
                    for (int i = mFirstVisible; i <= mLastVisible; i++) {
                        mStrokeView.clearPaint();
                        mStrokeView.invalidate();
                        OfflinePageItemData itemData = mOfflinePageDatas.get(i);
                        if (null == itemData.getDrawable()) {
                            loadStrokes(mStrokeView, itemData);
                            mStrokeView.invalidate();
                            WeakReference<Bitmap> bitmapWeakReference = new WeakReference<>(mStrokeView.getSignatureBitmap());
                            itemData.setDrawable(bitmapWeakReference);
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
                        WeakReference<Bitmap> bitmapWeakReference = new WeakReference<>(mStrokeView.getSignatureBitmap());
                        itemData.setDrawable(bitmapWeakReference);
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_offline_page_data_display);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
            mCurNotebookId = bundle.getString(BaseActivity.NOTEBOOK_ID);
        if (null != mCurNotebookId && !"".equals(mCurNotebookId)) {
            mNotebookData = AppCommon.selectNotebook(mCurNotebookId);
        }
        visibleText();
        //离线数据默认的笔画大小和颜色
        DEFAULT_PAINT_SIZE = 2f;
        DEFAULT_PAINT_COLOR = ContextCompat.getColor(this, R.color.colors_menu_black);
        //加载当前页的笔画
        mCurPageStrokesCache = StrokesUtilForQpen.getStrokes(new File(AppCommon.getStrokesPath(mCurNotebookId, mCurPageNum)));
        // 获取离线数据
        if (null != mOfflinePageDatas) {
            mOfflinePageDatas.clear();
        }
        mOfflineDots = AFPenClientCtrl.getInstance().getOfflineDataDots();

        /*
        //测试数据
        for(int i=1;i<9;i++){
            PageStrokesCacheBean bean = StrokesUtil.getStrokes(new File(Common.getStrokesPath(mCurNotebookId, i)));
            if(null!=bean){

                OfflinePageItemData offlinePageItemData = new OfflinePageItemData(mContext, Common.getUserUID(), mCurNotebookId, i, 1, "1");
                offlinePageItemData.getStrokesBeans().addAll(bean.getStrokesBeans());
                mOfflinePageDatas.add(offlinePageItemData);
            }
        }*/

        //归类每页的离线数据
        getOfflinePageDatas(mOfflineDots);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView() {
        //        mBinding.includeMainTopMenu.tvTitle.setText(R.string.offline_data_preview);
        if (mNotebookData != null && !TextUtils.isEmpty(mNotebookData.noteName)) {
            mBinding.includeMainTopMenu.tvTitle.setText(mNotebookData.noteName);
        }
        mBinding.includeMainTopMenu.tvRight.setText(R.string.sync);
        mBinding.includeMainTopMenu.tvRight.setTextColor(getResources().getColor(R.color.text_color_gray));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mOfflinePageDatas) {
            for (OfflinePageItemData data : mOfflinePageDatas) {
                if (data.getDrawable() != null) {
                    data.getDrawable().recycle();
                }
            }

            mOfflinePageDatas.clear();
            mOfflinePageDatas = null;
        }
        mCurPageStrokesCache = null;
        mOfflineDots = null;
        mNotebookData = null;
        recyAdapter = null;
        mStrokeView = null;
        mCustomPopWindow = null;

        System.gc();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mOfflinePageDatas.size() == 0) {
                setResult(RESULT_OK);
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (mOfflinePageDatas.size() == 0) {
                    setResult(RESULT_OK);
                } else {
                    setResult(RESULT_CANCELED);
                }
                finish();
                break;
            case R.id.tv_right:
                saveStrokes();
                visibleText();
                break;
            case R.id.tv_cancel:
                mNBSelectedList.clear();
                mBinding.includeMainTopMenu.tvRight.setTextColor(getResources().getColor(R.color.text_color_gray));
                for (OfflinePageItemData itemData : mOfflinePageDatas) {
                    itemData.setCheck(false);
                }
                recyAdapter.notifyDataSetChanged();
                mCurPageNum = -1;
                //重绘
                if (null != mStrokeView) {
                    mStrokeView.clearPaint();
                    mStrokeView.invalidate();
                }
                visibleText();
                break;
            case R.id.tv_all:
                if (mNBSelectedList.size() == mOfflinePageDatas.size()){
                    return;
                }
                mNBSelectedList.clear();
                int i = 0;
                for (OfflinePageItemData itemData : mOfflinePageDatas) {
//                    if (itemData.getPage() == mCurPageNum) {
                    itemData.setCheck(true);
                    mNBSelectedList.add(i);
//                    }
                    i++;
                }
                recyAdapter.notifyDataSetChanged();
                updateView();
                break;
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
                                        loadStrokes(mStrokeView, mCurPageStrokesCache);
                                    }
                                }
                            });
                        } else {
//                            if (pageNum != mCurPageNum) {
//
//                                //不是已选的页码
//                                offlinePageItemData.setCheck(!state);
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        int page = mCurPageNum;
//                                        showToast(String.format(getString(R.string.slecte_page_to_sync), page));
//                                        recyAdapter.notifyDataSetChanged();
//                                    }
//                                });
//                                return;
//                            } else {
//                                //是已选的页码，则保存该页的索引值
//                                int size = mNBSelectedList.size();
//                                for (int i = 0; i < size; i++) {
//                                    if (position == mNBSelectedList.get(i)) {
//                                        mNBSelectedList.remove(i);
//                                        break;
//                                    }
//                                }
//                                mNBSelectedList.add(position);
//                            }
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
                                        loadStrokes(mStrokeView, mCurPageStrokesCache);
                                    }
                                }
                            });
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
                            //加载当前页的笔画
                            if (null != mCurPageStrokesCache && null != mCurPageStrokesCache.getStrokesBeans()) {
                                mCurPageStrokesCache.getStrokesBeans().clear();
                            }
                            mCurPageNum = mOfflinePageDatas.get(mNBSelectedList.get(mNBSelectedList.size() - 1)).getPage();
                            mCurPageStrokesCache = StrokesUtilForQpen.getStrokes(new File(AppCommon.getStrokesPath(mCurNotebookId, mCurPageNum)));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (null != mStrokeView) {
                                        mStrokeView.clearPaint();
                                        mStrokeView.invalidate();
                                        loadStrokes(mStrokeView, mCurPageStrokesCache);
                                    }
                                }
                            });
                        }
                    }

                    visibleText();
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
                L.info(TAG, "updateView() ");
                if (size > 0) {
                    mBinding.includeMainTopMenu.tvRight.setTextColor(getResources().getColor(R.color.text_color_green));
                    loadStrokes(mStrokeView, mCurPageStrokesCache);
                } else {
                    mBinding.includeMainTopMenu.tvRight.setTextColor(getResources().getColor(R.color.text_color_gray));
                }
                if (size > 0) {
                    int position = mNBSelectedList.get(mNBSelectedList.size() - 1);
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
                canvasFrame = new CanvasFrame(OfflinePageDisplayActivity.this);
                mStrokeView = canvasFrame.bDrawl;
                mStrokeView.setOffline();
                if (null != mStrokeView) {
                    mStrokeView.setZOrderOnTop(true); // 在最顶层，会遮挡一切view
                    mStrokeView.setZOrderMediaOverlay(true);// 如已绘制SurfaceView则在surfaceView上一层绘制。
                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.page_bg1);
                    mStrokeView.setSignatureBitmap(bmp);
                    //                if (null != Common.getCurrentBitmap()) {
                    //                    mStrokeView.setSignatureBitmap(Common.getCurrentBitmap());
                    //                }
                }
                mBinding.llDrawBoard.addView(canvasFrame);

                //                Common.setStrokeView(mStrokeView);
                //                Common.setDrawBroadWidthHeight(mBinding.llDrawBoard.getWidth(), mBinding.llDrawBoard.getHeight());
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
            //            int curPage = Common.getCurrentPage();
            //            switchPageByPageNum(curPage + 1);
        }

        @Override
        public void rightDirect() {
            //            int curPage = Common.getCurrentPage();
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
                    //离线同步的笔迹颜色
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

    /**
     * 通过离线点坐标归类每页的离线数据
     *
     * @param dots
     */
    private void getOfflinePageDatas(final List<NQDot> dots) {
        if (null != dots && dots.size() > 0) {
            ThreadPoolUtils.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog = DialogHelper.showProgress(getSupportFragmentManager(), getString(R.string.processing_offline_data), true);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int page = 1;
                    OfflinePageItemData pageBean = null;
                    LinkedHashMap<String, OfflinePageItemData> pageBeans = new LinkedHashMap<>();
                    // 根据坐标点的页码变化统计页笔画
                    mOfflinePageDatas.clear();
                    if (lastTime == 0) {
                        lastTime = System.currentTimeMillis() - dots.size() - 100;
                    }
                    for (NQDot dot : dots) {
                        if (dot.page > 0 && dot.page <= Const.Page.PAGE_MAX_A5) {

                            if (pageBeans.containsKey(dot.page + "")) {
                                pageBean = pageBeans.get(dot.page + "");
                            } else {
                                pageBean = new OfflinePageItemData(OfflinePageDisplayActivity.this, AppCommon.getUserUID(), mCurNotebookId, dot.page, 1, "1");
                            }
//                            if (page != dot.page) {
//                                //如果不是连续的页码，创建新页
//                                page = dot.page;
//                                pageBean = new OfflinePageItemData(OfflinePageDisplayActivity.this, AppCommon.getUserUID(), mCurNotebookId, page, 1, "1");
//                                mOfflinePageDatas.add(pageBean);
//                            }
//                            if (null == pageBean) {
//                                pageBean = new OfflinePageItemData(OfflinePageDisplayActivity.this, AppCommon.getUserUID(), mCurNotebookId, page, 1, "1");
//                                mOfflinePageDatas.add(pageBean);
//                            }
                            //当前页添加点的数据
                            pageBean.addStrokes(new Point(dot.x, dot.y), dot.type, DEFAULT_PAINT_SIZE, DEFAULT_PAINT_COLOR, lastTime++);
                            pageBeans.put(dot.page + "", pageBean);
                        }
                    }
                    List<Map.Entry<String, OfflinePageItemData>> infoIds = new ArrayList<Map.Entry<String, OfflinePageItemData>>(pageBeans.entrySet());
                    //排序
                    Collections.sort(infoIds, new Comparator<Map.Entry<String, OfflinePageItemData>>() {
                        public int compare(Map.Entry<String, OfflinePageItemData> o1, Map.Entry<String, OfflinePageItemData> o2) {
                            String p1 = o1.getKey();
                            String p2 = o2.getKey();
                            ;
                            return Integer.parseInt(p1) - Integer.parseInt(p2);//如果要升序， 改为return Integer.valueOf(p1)-Integer.valueOf(p2);
                        }
                    });
                    //转换成新map输出
                    LinkedHashMap<String, OfflinePageItemData> newMap = new LinkedHashMap<String, OfflinePageItemData>();

                    for (Map.Entry<String, OfflinePageItemData> entity : infoIds) {
                        newMap.put(entity.getKey(), entity.getValue());
                    }
                    pageBeans = newMap;
                    mOfflinePageDatas = new ArrayList<>(pageBeans.values());
//                    try {
//                        //用旧JDK 版本的排序
//                        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//                        //排序
//                        Collections.sort(mOfflinePageDatas, new SortByPageNum());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    L.info("init data mOfflinePageDatas.size=" + mOfflinePageDatas.size());
                    //                    mHandler.sendEmptyMessage(2);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //更新UI
                            dismissDialog();
                            if (null != recyAdapter) {
                                recyAdapter.setList(mOfflinePageDatas, PageItemRVAdapter.VIEW_TYPE_PAGE);
                            }
                            mHandler.sendEmptyMessage(2);
                        }
                    });
                }
            });
        } else {
            if (dots != null) {
                L.warn("get offline page datas=" + dots.size());
            } else {
                L.warn("get offline page datas is null");
            }
        }
    }

    /**
     * 升序排序
     */
    class SortByPageNum implements Comparator {
        public int compare(Object o1, Object o2) {
            OfflinePageItemData s1 = (OfflinePageItemData) o1;
            OfflinePageItemData s2 = (OfflinePageItemData) o2;
            if (s1.getPage() > s2.getPage())
                return 1;
            return -1;
        }
    }

    /**
     * 保存已选择的离线数据
     */
    private void saveStrokes() {
        int size = mNBSelectedList.size();
        List<Integer> integers = new ArrayList<>();
        if (size > 0) {
            for (int i=0;i< mNBSelectedList.size() ;i++) {
                int mCurPageNum = mOfflinePageDatas.get(mNBSelectedList.get(i)).getPage();
                integers.add(mCurPageNum);
                PageStrokesCacheBean mCurPageStrokesCache = new PageStrokesCacheBean(AppCommon.getUserUID(), mCurNotebookId, mCurPageNum, 1, "1");
                List<PageStrokesCacheBean> temp = new ArrayList<>();
                final PageStrokesCacheBean strokesCache = mOfflinePageDatas.get(mNBSelectedList.get(i));
                //离线同步的笔迹颜色
                for (StrokesBean strokesBean : strokesCache.getStrokesBeans()) {
                    strokesBean.setColor(ContextCompat.getColor(mContext, R.color.colors_menu_black));
                }
                temp.add(strokesCache);
                //加载选择页的离线笔画
                mCurPageStrokesCache.getStrokesBeans().addAll(strokesCache.getStrokesBeans());

                if (AppCommon.createPageData(this, mCurNotebookId, NoteTypeEnum.NOTE_TYPE_A5.getNoeType(), mCurPageNum)) {
                    //保存笔迹
                    StrokesUtilForQpen.saveStrokes(mCurPageStrokesCache, AppCommon.getStrokesPath(mCurNotebookId, mCurPageNum));

                    if (i == size - 1) {
                        for (int j = 0;j< integers.size();j++) {
                            for (OfflinePageItemData data : mOfflinePageDatas){
                                if (integers.get(j) == data.getPage()){
                                    mOfflinePageDatas.remove(data);
                                    break;
                                }
                            }
                        }
                        mNBSelectedList.clear();
                        recyAdapter.setList(mOfflinePageDatas, PageItemRVAdapter.VIEW_TYPE_PAGE);
                        recyAdapter.notifyDataSetChanged();
                        //重绘
                        if (null != mStrokeView) {
                            mStrokeView.clearPaint();
                            mStrokeView.invalidate();
                        }

                        showToast(R.string.save_success);
                        if (mOfflinePageDatas.size() == 0) {
//                    AFPenClientCtrl.getInstance().requestDeleteOfflineData();
                            PenSdkCtrl.getInstance().requestDeleteOfflineData();
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                } else {
                    showToast(R.string.save_fail);
                }
            }


        } else {
            showToast(R.string.pls_select_offline_data);
        }
    }

    PageItemRVAdapter.OnGlobalLayoutListener onGlobalLayoutListener = new PageItemRVAdapter.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout(View view, final int position) {

            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyAdapter.notifyItemChanged(position);
                }
            });*/
        }
    };

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

    private void visibleText() {
        if (mNBSelectedList.size() == 0) {
//            mBinding.tvAll.setVisibility(View.GONE);
//            mBinding.tvCancel.setVisibility(View.INVISIBLE);
        } else {
//            mBinding.tvCancel.setVisibility(View.VISIBLE);
            int i = 0;
            for (OfflinePageItemData itemData : mOfflinePageDatas) {
                if (itemData.getPage() == mCurPageNum) {
                    i++;
                }
            }
//            if (i > 1) {
//                mBinding.tvAll.setVisibility(View.VISIBLE);
//            } else {
//                mBinding.tvAll.setVisibility(View.GONE);
//            }

        }
    }
}
