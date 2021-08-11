package com.eningqu.aipen.fragment;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.activity.DrawPageActivity;
import com.eningqu.aipen.activity.MainActivity;
import com.eningqu.aipen.activity.NotebookDisplayHorizonActivity;
import com.eningqu.aipen.qpen.PAGE_OPEN_STATUS;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.dialog.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.enums.NoteTypeEnum;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.databinding.FragmentMainBinding;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.db.model.PageData;
import com.eningqu.aipen.qpen.listener.IQPenOnActivityResult;
import com.eningqu.aipen.qpen.listener.IQPenRenameNotebookListener;
import com.eningqu.aipen.qpen.QPenManager;
import com.eningqu.aipen.manager.SpinNotebookManager;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.listener.IQPenCreateNotebookListener;
import com.hitomi.smlibrary.OnMenuLongClickListener;
import com.hitomi.smlibrary.OnMenuSelectedListener;
import com.hitomi.smlibrary.OnSpinMenuStateChangeListener;
import com.hitomi.smlibrary.OnSpinSelectedListener;
import com.hitomi.smlibrary.SMFragmentAdapter;
import com.hitomi.smlibrary.SMItemLayout;
import com.hitomi.smlibrary.SpinMenu;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.eningqu.aipen.base.ui.BaseActivity.REQUEST_CODE_DEL_NOTEBOOK;
import static com.eningqu.aipen.common.AppCommon.getUserUID;

/**
 * @Author: Qiu.Li
 * @Create Date: 2019/8/6 10:10
 * @Description: 选择笔记本界面
 * @Email: liqiupost@163.com
 */
public class MainFragment extends DrawBaseFragment {

    public final static String TAG = MainFragment.class.getSimpleName();
    private SMFragmentAdapter mFragmentPagerAdapter;
    private SpinMenu mSpinMenu;

    private int mIndexInAllBooks = 0;
    private boolean isOpenNotebookHorView = false;//是否笔记本水平展示界面
    private boolean createNotebook = false;

    private FragmentMainBinding mBinding;

    private Handler mHandler = new Handler();
    /*private static MainFragment fragment;

    public static MainFragment newInstance() {
        if (null == fragment) {
            synchronized (MainFragment.class) {
                if (null == fragment) {
                    fragment = new MainFragment();
                }
            }
        }
        return fragment;
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void onChangePage() {
        //没有笔记本则创建
        if (!createNotebook && SpinNotebookManager.getInstance().getAllNoteBookDatas().size() == 0) {
            addBook(getContext(), 0);
            return;
        }
        if (createNotebook) {
            return;
        }
    }

    @Override
    protected void onError(int error) {
        //        switch (error) {
        //            case Constant.ERROR_NONE_SELECT_NOTEBOOK:
        //                break;
        //        }
    }

    @Override
    public void onResume() {
        super.onResume();
        L.info(TAG, "onResume isHidden=" + isHidden);
        if (!isHidden) {
            //可见状态时
            ((MainActivity) getActivity()).setQPenOnActivityResult(new IQPenOnActivityResult() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, Intent data) {

                    updateNotebook(resultCode);
                }
            });
            isOpenNotebookHorView = false;
            updateWhenSHow();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        L.info(TAG, "onHiddenChanged hidden=" + hidden);
        isHidden = hidden;
        if (!isHidden) {
            //可见状态时
            AppCommon.setCurrentPage(-1);
            AppCommon.setDrawOpenState(PAGE_OPEN_STATUS.CLOSE);
            isOpenNotebookHorView = false;
            updateWhenSHow();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_main;
    }

    @Override
    protected void dataBindingLayout(ViewDataBinding viewDataBinding) {
        mBinding = (FragmentMainBinding) viewDataBinding;
    }

    @Override
    protected void initView() {
        isHidden = false;
        mBinding.etNoteName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String name = v.getText().toString().trim().replace(" ", "");

                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showShort(R.string.dialog_rename_tips);
                    return false;
                }

                AppCommon.renameNoteBook(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), name, new IQPenRenameNotebookListener() {
                    @Override
                    public void onSuccessful() {
                        ToastUtils.showShort(R.string.rename_success);
                        updateNotebookList();
                    }

                    @Override
                    public void onFail() {

                    }
                });
                return false;
            }
        });

        if (QPenManager.getInstance().isNeedInit()) {
            //延时刷新
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (null != mSpinMenu) {
                        try {
                            mSpinMenu.pagerDestroyItem(mFragmentPagerAdapter);
                            //设置适配器
                            mSpinMenu.setFragmentAdapter(mFragmentPagerAdapter);
                        } catch (IllegalStateException e) {
                            L.error(TAG, "java.lang.IllegalStateException:");
                        }
                        mFragmentPagerAdapter.notifyDataSetChanged();
                        initDotView(0);
                    }
                    QPenManager.getInstance().setNeedInit(false);
                }
            }, 300);
        }
    }

    @Override
    protected void initData() {
        updateNotebookList();
        SpinNotebookManager.getInstance().addBookCovers(SpinNotebookManager.getInstance().getAllNoteBookDatas());
        LogUtils.e("初始化首页控件0");
        if (QPenManager.getInstance().isNeedInit()) {
            LogUtils.e("初始化首页控件1");
            //初始化笔记本旋转view
            initBookSpinMenuView();
            //            QPenManager.getInstance().setNeedInit(false);
        }

        //更新圆点
        if (SpinNotebookManager.getInstance().getAdapterNoteBookDatas().size() == 0) {
            if (null != mBinding.llDotContainer &&
                    mBinding.llDotContainer.getChildCount() > 0) {
                mBinding.llDotContainer.removeAllViews();
            }
        }
    }

    private void updateWhenSHow() {
        ((MainActivity) getActivity()).updateTopToolbar("");

        if (AppCommon.isNotebooksChange()) {
            AppCommon.setNotebooksChange(false);
            updateNotebookList();
            if (SpinNotebookManager.getInstance().getAdapterNoteBookDatas().size() == 0) {
                //添加封面，当总笔记本少于等于10个时，默认显示所有笔记本和一个空白封面，
                // 当多于10个时，显示11个笔记本和一个空白封面
                if (SpinNotebookManager.getInstance().getAllNoteBookDatas().size() > 10) {
                    initBookCovers(SpinNotebookManager.getInstance().getAllNoteBookDatas().subList(0, 11));
                } else {
                    initBookCovers(SpinNotebookManager.getInstance().getAllNoteBookDatas());
                }
                mSpinMenu.scroll(0, 0, 0, 0);
            } else {
                // 当多于10个时，显示11个笔记本和一个空白封面
                if (SpinNotebookManager.getInstance().getAllNoteBookDatas().size() > 10) {
                    initBookCovers(SpinNotebookManager.getInstance().getAllNoteBookDatas().subList(0, 11));
                } else {
                    initBookCovers(SpinNotebookManager.getInstance().getAllNoteBookDatas());
                }
                mSpinMenu.scroll(0, 0, 0, 0);
            }
        }

        //设置当前笔记本
        NoteBookData noteBookData = SpinNotebookManager.getInstance().getCurNotebookData();

        AppCommon.setCurrentNoteBookData(noteBookData);
        if (null != noteBookData) {
            AppCommon.setCurrentNotebookId(noteBookData.notebookId);
            if (!TextUtils.isEmpty(noteBookData.noteName)) {
                if (null != mBinding.etNoteName) {
                    mBinding.etNoteName.setText(noteBookData.noteName);
                }
            }
        } else {
            AppCommon.setCurrentNotebookId("");
        }
    }


    public void updateNotebook(int resultCode) {
        if (resultCode == 1) {
            SpinNotebookManager.getInstance().reset();
            updateNotebookList();
            if (SpinNotebookManager.getInstance().getAllNoteBookDatas().size() > 0) {
                NoteBookData curNoteBookData = SpinNotebookManager.getInstance().getAllNoteBookDatas().get(0);
                AppCommon.setCurrentNoteBookData(curNoteBookData);
                AppCommon.setCurrentNotebookId(curNoteBookData.notebookId);
            }
            //添加封面，当总笔记本少于等于10个时，默认显示所有笔记本和一个空白封面，当多于10个时，显示11个笔记本
            if (SpinNotebookManager.getInstance().getAllNoteBookDatas().size() > 10) {
                initBookCovers(SpinNotebookManager.getInstance().getAllNoteBookDatas().subList(0, 11));
            } else {
                initBookCovers(SpinNotebookManager.getInstance().getAllNoteBookDatas());
            }
            mSpinMenu.scroll(0, 0, 0, 0);
        }
    }

    /**
     * 初始化笔记本旋转选择view
     */
    private void initBookSpinMenuView() {
        // 设置启动手势开启菜单
        mSpinMenu = mBinding.spinMenu;
        mSpinMenu.setEnableGesture(true);
        // 设置页面适配器
        mFragmentPagerAdapter = new SMFragmentAdapter(getChildFragmentManager(), SpinNotebookManager.getInstance().getAdapterBookCovers());
        //设置适配器
        mSpinMenu.setFragmentAdapter(mFragmentPagerAdapter);
        // 设置菜单状态改变时的监听器
        mSpinMenu.setOnSpinMenuStateChangeListener(new OnSpinMenuStateChangeListener() {
            @Override
            public void onMenuOpened() {
            }

            @Override
            public void onMenuClosed() {
            }
        });

        //设置选择监听
        mSpinMenu.setSpinSelectedListener(spinSelectedListener);
        mSpinMenu.setMenuSelectedListener(onMenuSelectedListener);
        mSpinMenu.setOnMenuLongClickListener(onMenuLongClickListener);
    }

    /**
     * 初始化点
     *
     * @param currentIndex
     */
    private void initDotView(int currentIndex) {
        int i = 0;
        if (null != mBinding.llDotContainer &&
                mBinding.llDotContainer.getChildCount() > 0) {
            mBinding.llDotContainer.removeAllViews();
        }

        boolean addLeftArrow = false;
        boolean addRightArrow = false;
        //10个为一组
        for (NoteBookData noteBookData : SpinNotebookManager.getInstance().getAdapterNoteBookDatas()) {
            ImageView dot = new ImageView(getContext());
            if (i == currentIndex) {
                dot.setImageResource(R.drawable.shape_dot_green);//设置当前页的圆点
            } else {
                dot.setImageResource(R.drawable.shape_dot_gray);//其余页的圆点
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                params.leftMargin = 30;//设置圆点边距
            }
            params.width = 20;
            params.height = 20;
            dot.setLayoutParams(params);
            mBinding.llDotContainer.addView(dot);
            i++;
        }

        int curRound = SpinNotebookManager.getInstance().getCurRound();

        //多于10个本子
        if ((SpinNotebookManager.getInstance().getAllNoteBookDatas().size() > 10)) {

            if (curRound > 0) {
                addLeftArrow = true;
            }

            if (curRound == 0) {
                if (SpinNotebookManager.getInstance().getAllNoteBookDatas().size() > 10) {
                    addRightArrow = true;
                }
            } else {
                if (SpinNotebookManager.getInstance().getAllNoteBookDatas().size() > (curRound * 10 + 10)) {
                    addRightArrow = true;
                }
            }

            if (addLeftArrow) {
                ImageView arrow = (ImageView) mBinding.llDotContainer.getChildAt(0);
                arrow.setImageResource(R.drawable.arrow_left);
            }
            if (addRightArrow) {
                int count = mBinding.llDotContainer.getChildCount();
                ImageView arrow = (ImageView) mBinding.llDotContainer.getChildAt(count - 1);
                arrow.setImageResource(R.drawable.arrow_right);
            }
        }
        mBinding.llDotContainer.requestLayout();
    }

    /**
     * view 上的点击事件
     *
     * @param view
     */
    public void onViewClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_edit_del:
                mBinding.etNoteName.setText("");
                break;
        }
    }

    // 普通事件的处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier carrier) {
        super.handleEvent(carrier);
        if (null == carrier) {
            return;
        }

        int eventType = carrier.getEventType();
        switch (eventType) {
            case Constant.USER_LOGOUT:
                //                firstInit = true;
                break;
            case Constant.USER_LOGIN:
                //                firstInit = true;
                break;
            case Constant.ERROR_NONE_SELECT_NOTEBOOK:
                ToastUtils.showShort(R.string.pls_select_notebook_tips);
            case Constant.ERROR_NONE_NOTEBOOK:
                AFPenClientCtrl.getInstance().cleanQueenDatas();
                AppCommon.setCurrentPage(-1);
                AppCommon.setCurrentNoteBookData(null);
                AppCommon.setCurrentNotebookId("");
                break;
        }
    }

    /**
     * 转动监听
     */
    private OnSpinSelectedListener spinSelectedListener = new OnSpinSelectedListener() {

        @Override
        public void onSpinSelected(final int pos) {

            int curIndex = SpinNotebookManager.getInstance().getCurPosition();
            int curRound = SpinNotebookManager.getInstance().getCurRound();
            List<NoteBookData> allNoteBookDatas = SpinNotebookManager.getInstance().getAllNoteBookDatas();
            List<NoteBookData> adapterNoteBookDatas = SpinNotebookManager.getInstance().getAdapterNoteBookDatas();
            List<Fragment> adapterBookCovers = SpinNotebookManager.getInstance().getAdapterBookCovers();

            FragmentBook fragmentBook = null;
            boolean isEmptyNotebook = false;//当前是否空白封面
            if (adapterBookCovers.size() > pos) {
                fragmentBook = (FragmentBook) adapterBookCovers.get(pos);
            }
            if (null != fragmentBook && fragmentBook.isEmpty()) {
                //如果当前页是空白封面则把圆点隐藏，否则显示
                mBinding.llDotContainer.setVisibility(View.INVISIBLE);
                isEmptyNotebook = true;
            } else {
                mBinding.llDotContainer.setVisibility(View.VISIBLE);
            }
            //            int position = pos % 10;

            if ((adapterNoteBookDatas.size() > 10 || curRound > 0)) {
                //如果多于10个
                if (curIndex < pos) {
                    //向右
                    //第一轮，第11个
                    //大于第一轮，第12个
                    if ((curRound == 0 && pos >= 10) || (curRound > 0 && pos >= 11 && (pos == 11 && null != fragmentBook && !fragmentBook.isEmpty()))
                            || (pos == 12 && null != fragmentBook && fragmentBook.isEmpty())
                            || (null == fragmentBook && pos == 10)) {
                        /*if(SpinNotebookManager.getInstance().getAllNoteBookDatas().size()>((curRound+1)*10)){
                        }else {
                            curIndex = pos;
                            mIndexInAllBooks = curRound * 10 + pos;
                        }*/
                        //标记为下一轮
                        SpinNotebookManager.getInstance().setCurRound(++curRound);
                        //下一轮的笔记本
                        List<NoteBookData> list;
                        if (allNoteBookDatas.size() > (curRound * 10 + 10)) {
                            list = allNoteBookDatas.subList(curRound * 10 - 1, curRound * 10 + 11);
                        } else {
                            list = allNoteBookDatas.subList(curRound * 10 - 1, SpinNotebookManager.getInstance().getAllNoteBookDatas().size());
                        }
                        initBookCovers(list);
                        curIndex = pos;
                        mIndexInAllBooks = curRound * 10 + pos;
                        SpinNotebookManager.getInstance().setCurPosition(1);
                        mSpinMenu.scroll(0, 0, 1, 0);
                    } else {
                        /*if(mCurRound==0){
                            mIndexInAdapter = position;
                            mIndexInAllBooks = mCurRound * 10 + position;
                        }*/
                        curIndex = pos;
                        mIndexInAllBooks = curRound * 10 + pos - 1;
                        SpinNotebookManager.getInstance().setCurPosition(curIndex);
                    }

                } else if (curIndex > pos) {
                    //向左
                    if (pos == 0 && curRound > 0) {

                        SpinNotebookManager.getInstance().setCurRound(--curRound);

                        //上一轮的笔记本
                        List<NoteBookData> list = null;
                        if (curRound > 0) {
                            if (allNoteBookDatas.size() > ((curRound + 1) * 10)) {
                                list = allNoteBookDatas.subList(curRound * 10 + pos - 1, curRound * 10 + 11);
                            } else {
                                list = allNoteBookDatas.subList(curRound * 10 + pos - 1, curRound * 10 + 10);
                            }
                            curIndex = 10;
                            initBookCovers(list);
                            mIndexInAllBooks = curRound * 10 + curIndex - 1;
                            mSpinMenu.scroll(0, 0, 10, 0);
                        } else {
                            curIndex = 9;
                            try {
                                list = allNoteBookDatas.subList(0, allNoteBookDatas.size() > 10 ? 11 : allNoteBookDatas.size());
                            } catch (Exception e) {
                                e.printStackTrace();
                                list = allNoteBookDatas;
                            }
                            initBookCovers(list);
                            mIndexInAllBooks = curRound * 10 + curIndex - 1;
                            mSpinMenu.scroll(0, 0, 9, 0);
                        }


                    } else {
                        curIndex = pos;
                        mIndexInAllBooks = curRound * 10 + pos - 1;
                    }
                    SpinNotebookManager.getInstance().setCurPosition(curIndex);
                } else {

                    if (isNextRound) {
                        //标记为下一轮
                        SpinNotebookManager.getInstance().setCurRound(++curRound);

                        //下一轮的笔记本
                        List<NoteBookData> list;
                        if (allNoteBookDatas.size() > (curRound * 10 + 10)) {
                            list = allNoteBookDatas.subList(curRound * 10 - 1, curRound * 10 + 11);
                        } else {
                            list = allNoteBookDatas.subList(curRound * 10 - 1, SpinNotebookManager.getInstance().getAllNoteBookDatas().size());
                        }
                        initBookCovers(list);
                        curIndex = 1;
                        mIndexInAllBooks = curRound * 10 + curIndex;
                        mSpinMenu.scroll(0, 0, 1, 0);
                    } else {
                        initBookCovers(adapterNoteBookDatas);
                        curIndex = pos;
                        mIndexInAllBooks = curRound * 10 + pos - 1;
                    }
                    SpinNotebookManager.getInstance().setCurPosition(curIndex);
                    isNextRound = false;
                }
            } else {
                //                initBookCovers(adapterNoteBookDatas);
                if (isNextRound) {
                    //标记为下一轮
                    SpinNotebookManager.getInstance().setCurRound(++curRound);

                    //下一轮的笔记本
                    List<NoteBookData> list;
                    if (allNoteBookDatas.size() > (curRound * 10 + 10)) {
                        list = allNoteBookDatas.subList(curRound * 10 - 1, curRound * 10 + 11);
                    } else {
                        list = allNoteBookDatas.subList(curRound * 10 - 1, SpinNotebookManager.getInstance().getAllNoteBookDatas().size());
                    }
                    initBookCovers(list);
                    curIndex = 1;
                    mIndexInAllBooks = curRound * 10 + curIndex;
                    mSpinMenu.scroll(0, 0, 1, 0);
                } else {
                    initBookCovers(adapterNoteBookDatas);
                    curIndex = pos;
                    mIndexInAllBooks = curRound * 10 + pos - 1;
                }
                SpinNotebookManager.getInstance().setCurPosition(curIndex);
                isNextRound = false;
            }

            mBinding.etNoteName.setText("");
            //选择笔记本
            int position = SpinNotebookManager.getInstance().getCurPosition();
            if (position < adapterNoteBookDatas.size()) {
                //不是最后一个
                NoteBookData curNoteBookData = SpinNotebookManager.getInstance().getAllNoteBookDatas().get(curRound * 10 + position - (curRound == 0 ? 0 : 1));
                AppCommon.setCurrentNoteBookData(curNoteBookData);
                AppCommon.setCurrentNotebookId(curNoteBookData.notebookId);
                if (TextUtils.isEmpty(curNoteBookData.noteName)) {
                    mBinding.etNoteName.setText("");
                } else {
                    mBinding.etNoteName.setText(curNoteBookData.noteName);
                }
            } else {
                AppCommon.setCurrentNotebookId("");
                AppCommon.setCurrentPage(-1);
                AppCommon.setCurrentNoteBookData(null);
            }

            initDotView(position);
        }
    };

    private boolean isNextRound = false;
    private OnMenuSelectedListener onMenuSelectedListener = new OnMenuSelectedListener() {

        @Override
        public void onMenuSelected(SMItemLayout smItemLayout, int pos) {

            int curIndex = SpinNotebookManager.getInstance().getCurPosition();
            int curRound = SpinNotebookManager.getInstance().getCurRound();
            List<NoteBookData> allNoteBookDatas = SpinNotebookManager.getInstance().getAllNoteBookDatas();
            List<NoteBookData> adapterNoteBookDatas = SpinNotebookManager.getInstance().getAdapterNoteBookDatas();
            List<Fragment> adapterBookCovers = SpinNotebookManager.getInstance().getAdapterBookCovers();

            FragmentBook fragmentBook = null;
            if (adapterBookCovers.size() > pos) {
                fragmentBook = (FragmentBook) adapterBookCovers.get(pos);
            }
            int position = pos;
            if (null != fragmentBook && fragmentBook.isEmpty()) {
                //点击最后一页(空白页)
                if ((curRound == 0 && adapterNoteBookDatas.size() > 9) ||
                        (curRound > 0 && adapterNoteBookDatas.size() > 10)) {
                    adapterNoteBookDatas.clear();
                    adapterBookCovers.clear();
                    addBook(getContext(), 0);
                    isNextRound = true;
                } else {
                    addBook(getContext(), position);
                }
            } else {
                //不是空白页
                if (position < adapterNoteBookDatas.size()) {

                    NoteBookData curNoteBookData = adapterNoteBookDatas.get(position);
                    AppCommon.setCurrentNoteBookData(curNoteBookData);
                    if (!TextUtils.isEmpty(curNoteBookData.notebookId)) {
                        AppCommon.setCurrentNotebookId(curNoteBookData.notebookId);
                        List<PageData> pageDatas = AppCommon.loadPageDataList(curNoteBookData.notebookId, false);
                        if (null != pageDatas && pageDatas.size() > 0) {
                            int i = 0;
                            for (PageData pageData : pageDatas) {
                                if (pageData.pageNum > 0) {
                                    //取第一个大于0的页码
                                    break;
                                }
                                i++;
                            }

                            Bundle bundle = new Bundle();
                            bundle.putString(BaseActivity.NOTEBOOK_ID, curNoteBookData.notebookId);
                            bundle.putInt(BaseActivity.PAGE_NUM, pageDatas.get(i).pageNum);
                            bundle.putString(BaseActivity.NOTE_NAME, curNoteBookData.noteName);
                            bundle.putInt(BaseActivity.NOTE_TYPE, curNoteBookData.noteType);
                            AppCommon.setCurrentPage(pageDatas.get(i).pageNum);

                            Intent intent = new Intent(getActivity(), DrawPageActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    AFPenClientCtrl.getInstance().drawDotQueue();
                                }
                            }, 10);

                        } else {
                            dismissDialog();
                            dialog = DialogHelper.showMessage(getChildFragmentManager(), new ConfirmListener() {
                                @Override
                                public void confirm(View view) {
                                    dismissDialog();
                                }

                                @Override
                                public void cancel() {
                                    dismissDialog();
                                }
                            }, R.string.connect_pen_tips, R.string.notebook_empty);
                        }
                    }
                }
            }
        }
    };

    /**
     * 长按监听
     */
    private OnMenuLongClickListener onMenuLongClickListener = new OnMenuLongClickListener() {
        @Override
        public void onMenuLongClick(int position) {
            if (SpinNotebookManager.getInstance().getAdapterNoteBookDatas().size() == 0) {
                return;
            }
            if (!isOpenNotebookHorView) {
                isOpenNotebookHorView = true;
                Bundle bundle = new Bundle();
                bundle.putInt(NotebookDisplayHorizonActivity.VIEW_TYPE, NotebookDisplayHorizonActivity.NOTEBOOK_LOCK_DEL);
                if (SpinNotebookManager.getInstance().getCurRound() == 0) {
                    bundle.putInt(NotebookDisplayHorizonActivity.VIEW_POSITION, position);
                } else {
                    int pos = SpinNotebookManager.getInstance().getCurRound() * 10 + position - 1;
                    bundle.putInt(NotebookDisplayHorizonActivity.VIEW_POSITION, pos);
                }

                Intent intent = new Intent(getActivity(), NotebookDisplayHorizonActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CODE_DEL_NOTEBOOK);
            }
        }
    };

    /**
     * 添加笔记本
     *
     * @param position
     */
    public synchronized void addBook(Context context, int position) {
        String bookName = "";
        //在数据块创建一个笔记本
        createNotebook = true;
        String etNotebookName = mBinding.etNoteName.getText().toString().replace(" ", "");
        if (TextUtils.isEmpty(etNotebookName) || etNotebookName.equals(getString(R.string.please_write_tips))) {
            bookName = context.getString(R.string.new_notebook_name);
        } else {
            bookName = etNotebookName;
        }
        final int size = SpinNotebookManager.getInstance().getNotebookUnlockList().size();
        AppCommon.createNoteBook(NoteTypeEnum.NOTE_TYPE_A5.getNoeType(), String.valueOf(size), getUserUID(), bookName, new IQPenCreateNotebookListener() {
            @Override
            public void onSuccessful(final NoteBookData noteBookData) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NoteBookData book = new NoteBookData();
                        book.notebookId = noteBookData.notebookId;
                        book.createTime = noteBookData.createTime;
                        book.noteName = noteBookData.noteName;
                        book.noteType = noteBookData.noteType;
                        book.userUid = noteBookData.userUid;
                        int coverIndex = size % Constant.BOOK_COVERS.length;//取余可循环使用封皮
                        book.noteCover = String.valueOf(coverIndex);
                        AppCommon.setCurrentNoteBookData(book);
                        AppCommon.setCurrentNotebookId(book.notebookId);
                        SpinNotebookManager.getInstance().addNotebook(book);
                        initBookCovers(SpinNotebookManager.getInstance().getAdapterNoteBookDatas());
                        SpinNotebookManager.getInstance().getNotebookUnlockList();
                        //创建完成
                        createNotebook = false;
                    }
                });

            }

            @Override
            public void onFail() {
                //创建完成
                createNotebook = false;
            }
        });
    }

    /**
     * 根据笔记本记录生成可转动的动画画面
     *
     * @param list
     */
    private void initBookCovers(List<NoteBookData> list) {
        if (mBinding.spinMenu != null) {
            mBinding.spinMenu.pagerDestroyItem(mFragmentPagerAdapter);
            SpinNotebookManager.getInstance().addBookCovers(list);
            mBinding.spinMenu.setFragmentAdapter(mFragmentPagerAdapter);
            mFragmentPagerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 更新笔记本列表信息
     */
    private void updateNotebookList() {
        SpinNotebookManager.getInstance().getNotebookUnlockList();
    }

    /**
     * 设置当前笔记本
     *
     * @param index
     */
    private void setCurrentNotebook(int index) {
        if (SpinNotebookManager.getInstance().getAllNoteBookDatas().size() > index) {

            NoteBookData curNoteBookData = SpinNotebookManager.getInstance().getAllNoteBookDatas().get(index);
            AppCommon.setCurrentNoteBookData(curNoteBookData);
            AppCommon.setCurrentNotebookId(curNoteBookData.notebookId);
        }
    }
}

