package com.eningqu.aipen.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.RvHorizonBookItemAdapter;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.dialog.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.databinding.ActivityNotebookDisplayHorizonBinding;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.db.model.PageData;
import com.eningqu.aipen.qpen.listener.IQPenCollectNotebookListener;
import com.eningqu.aipen.qpen.listener.IQPenDeleteNotebookListener;
import com.eningqu.aipen.manager.SpinNotebookManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/4/25 14:58
 * desc   : 水平展示笔记本
 * version: 1.0
 */
public class NotebookDisplayHorizonActivity extends BaseActivity {
    ActivityNotebookDisplayHorizonBinding mBinding;

    /**
     * 已选择的笔记本
     */
    private List<String> mNBSelectedList = new ArrayList<>();
    /**
     * 水平滚动显示封面适配器
     */
    private RvHorizonBookItemAdapter recyAdapter;
    /**
     * 查询到的未锁定笔记本
     */
    private List<NoteBookData> noteBookDatas;

    public static final int NOTEBOOK_LOCK_DEL = 1;
    public static final int NOTEBOOK_LOCK = 2;
    public static final String VIEW_TYPE = "view_type";
    public static final String VIEW_POSITION = "view_position";
    private int type = NOTEBOOK_LOCK_DEL;
    private int position = 0;
    int backresultCode = 0;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notebook_display_horizon);
    }

    @Override
    protected void initData() {
        noteBookDatas = AppCommon.loadNoteBookData(0);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            type = bundle.getInt(VIEW_TYPE);
            position = bundle.getInt(VIEW_POSITION);
        }
    }

    @Override
    protected void initView() {
        initRecycleView();
        updateSelectCount(0);

        if (type == NOTEBOOK_LOCK_DEL) {
            mBinding.includeTopMenuLockAndDel.llRoot.setVisibility(View.VISIBLE);
            mBinding.includeTopMenuLock.llRoot.setVisibility(View.GONE);
        } else {
            mBinding.includeTopMenuLockAndDel.llRoot.setVisibility(View.GONE);
            mBinding.includeTopMenuLock.llRoot.setVisibility(View.VISIBLE);
            mBinding.includeTopMenuLock.tvRight.setText(R.string.collect);
        }
    }

    @Override
    protected void initEvent() {

    }

    // 普通事件的处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier carrier) {
        if (null == carrier) {
            return;
        }

        switch (carrier.getEventType()) {
            case Constant.OPEN_NOTEBOOK_CODE:
            case Constant.DRAW_CODE:
//                ActivityStackManager.getInstance().exitAllActivityExceptCurrent(MainActivity.class);
                break;
        }
    }

    /**
     * 点击事件
     *
     * @param view
     */
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.ll_share: {
                //分享笔记本
                ToastUtils.showShort("developing");
            }
            break;
            case R.id.tv_right:
            case R.id.ll_lock: {
                AppCommon.setNotebooksChange(true);

                if (mNBSelectedList.size() == 0) {
                    ToastUtils.showShort(R.string.pls_select_notebook_tips);
                    return;
                }

                if (existEmptyNotebook(mNBSelectedList)) {
                    ToastUtils.showShort(R.string.collect_fail_empty);
                    return;
                }

                //锁住笔记本
                dismissDialog();
                dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {

                    @Override
                    public void confirm(View view) {
                        dismissDialog();
                        AppCommon.collectNoteBook(AppCommon.getUserUID(), mNBSelectedList, true, new IQPenCollectNotebookListener() {

                            @Override
                            public void onSuccessful() {
                                backresultCode = 1;
                                ToastUtils.showShort(R.string.collect_success);
                                updateNotebookList();
                                SpinNotebookManager.getInstance().getNotebookUnlockList();
                                recyAdapter.notifyDataSetChanged();
                                mNBSelectedList.clear();
                                updateSelectCount(mNBSelectedList.size());
                                mBinding.tvNoteName.setText("");
                                AppCommon.setNotebooksChange(true);
                                SpinNotebookManager.getInstance().setCurRound(0);
                                SpinNotebookManager.getInstance().setCurPosition(0);
                                setResult(backresultCode);
                                finish();
                            }

                            @Override
                            public void onFail() {
                                ToastUtils.showShort(R.string.collect_fail);
                            }
                        });
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                    }
                }, R.string.dialog_title_lock_notebook, R.string.confirm_collect_text, R.string.dialog_confirm_text, R.string.dialog_cancel_text);

            }
            break;
            case R.id.ll_delete: {
                AppCommon.setNotebooksChange(true);
                //删除笔记本
                dismissDialog();
                dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {

                    @Override
                    public void confirm(View view) {
                        dismissDialog();
                        if (mNBSelectedList.size() > 0) {
                            AppCommon.deleteNoteBook(AppCommon.getUserUID(), mNBSelectedList, new IQPenDeleteNotebookListener() {
                                @Override
                                public void onSuccessful() {
                                    backresultCode = 1;
                                    ToastUtils.showShort(R.string.delete_success);
                                    updateNotebookList();
                                    SpinNotebookManager.getInstance().getNotebookUnlockList().size();
                                    recyAdapter.notifyDataSetChanged();
                                    mNBSelectedList.clear();
                                    updateSelectCount(mNBSelectedList.size());
                                    mBinding.tvNoteName.setText("");
                                    AppCommon.setNotebooksChange(true);
                                    SpinNotebookManager.getInstance().setCurRound(0);
                                    SpinNotebookManager.getInstance().setCurPosition(0);
                                    setResult(backresultCode);
                                    finish();
                                }

                                @Override
                                public void onFail() {
                                    ToastUtils.showShort(R.string.delete_fail);
                                }
                            });
                        } else {
                            ToastUtils.showShort(R.string.pls_select_notebook_tips);
                        }
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                    }
                }, R.string.dialog_title_delete_notebook, R.string.confirm_delete_text, R.string.dialog_delete_text, R.string.dialog_cancel_text);

            }
            break;
            case R.id.ll_bg:
            case R.id.iv_back:
                setResult(backresultCode);
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                setResult(backresultCode);
                finish();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化水平滚动显示笔记本的rv
     */
    private void initRecycleView() {
        recyAdapter = new RvHorizonBookItemAdapter(this, noteBookDatas);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.rvNoteBook.setLayoutManager(layoutManager);
        recyAdapter.setOnItemClickListener(new RvHorizonBookItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int tag, boolean lock) {
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_select_state);
                String notebookId = noteBookDatas.get(tag).notebookId;
                removeObjById(notebookId);
                if (lock) {
                    imageView.setImageResource(R.drawable.selected);
                    mNBSelectedList.add(notebookId);
                    if (null != mBinding.tvNoteName) {
                        mBinding.tvNoteName.setText(noteBookDatas.get(tag).noteName);
                    }
                } else {
                    imageView.setImageResource(R.drawable.selected_none);
                }

                updateSelectCount(mNBSelectedList.size());
            }
        });
        mBinding.rvNoteBook.setAdapter(recyAdapter);
        mBinding.rvNoteBook.scrollToPosition(position);
    }

    /**
     * 更新已选择笔记本信息
     */
    private void updateSelectCount(int size) {
        String s = String.format(getString(R.string.selected_books), size);
        mBinding.tvSelectCount.setText(s);

        if (mNBSelectedList.size() > 0) {
            if (null != mBinding.includeTopMenuLock.tvRight) {
                mBinding.includeTopMenuLock.tvRight.setTextColor(getResources().getColor(R.color.text_color_green));
            }
        } else {
            if (null != mBinding.includeTopMenuLock.tvRight) {
                mBinding.includeTopMenuLock.tvRight.setTextColor(getResources().getColor(R.color.text_color_gray));
            }
        }
    }

    /**
     * 根据笔记本ID，去掉已选的笔记本
     *
     * @param id
     */
    private synchronized void removeObjById(String id) {
        if (null != mNBSelectedList && mNBSelectedList.size() > 0) {

            for (String s : mNBSelectedList) {
                if (s.equals(id)) {
                    mNBSelectedList.remove(s);
                    return;
                }
            }
        }
    }

    /**
     * 更新笔记本列表信息
     */
    private void updateNotebookList() {
        if (null != noteBookDatas && noteBookDatas.size() > 0) {
            noteBookDatas.clear();
        } else if (null == noteBookDatas) {
            noteBookDatas = new ArrayList<>();
        }

        //获取未锁定的笔记本
        List<NoteBookData> list = AppCommon.loadNoteBookData(0);

        if (null == list || list.size() == 0) {
            AppCommon.resetCurrentData();
            return;
        }
        if (null != list) {
            for (NoteBookData bookData : list) {
                noteBookDatas.add(bookData);
            }
        }
    }

    /**
     * 是否存在空笔记本
     *
     * @param notebookIdList
     * @return
     */
    private boolean existEmptyNotebook(List<String> notebookIdList) {
        boolean exist = false;
        if (null != notebookIdList && notebookIdList.size() > 0) {

            for (String notebookId : notebookIdList) {

                List<PageData> pageDatas = AppCommon.loadPageDataList(notebookId, false);
                if (null == pageDatas || pageDatas.size() == 0) {
                    exist = true;
                    break;
                }
            }
        }
        return exist;
    }
}
