package com.eningqu.aipen.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.NotebookItemRVAdapter;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.enums.NoteTypeEnum;
import com.eningqu.aipen.common.utils.SpacesItemDecoration;
import com.eningqu.aipen.databinding.ActivityNotebookDisplayVerticalBinding;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.qpen.AFPenClientCtrl;
import com.eningqu.aipen.qpen.listener.IQPenCreateNotebookListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.eningqu.aipen.activity.OfflinePageDisplayActivity.REQUEST_CODE_OFFLINE;
import static com.eningqu.aipen.common.AppCommon.getUserUID;


/**
 * @Author: Qiu.Li
 * @Create Date: 2019/4/25 15:56
 * @Description: 笔记本列表
 * @Email: liqiupost@163.com
 */
public class NotebookDisplayVerticalActivity extends BaseActivity implements NotebookItemRVAdapter.OnRecyclerViewItemClickListener {

    private final static String TAG = NotebookDisplayVerticalActivity.class.getSimpleName();

    public static final String FUN_TYPE = "fun_type";
    public static final int FUN_TYPE_OFFLINE_DATA = 1;
    private int funType = -1;
    private List<NoteBookData> dataList;
    private NotebookItemRVAdapter itemAdapter;
    private ActivityNotebookDisplayVerticalBinding mBinding;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notebook_display_vertical);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        funType = intent.getIntExtra(FUN_TYPE, -1);

        if (funType == FUN_TYPE_OFFLINE_DATA) {
            dataList = AppCommon.loadNoteBookData(0);
        } else {
            dataList = AppCommon.loadNoteBookData(2);
        }
    }

    @Override
    protected void initView() {

        itemAdapter = new NotebookItemRVAdapter(this);
        itemAdapter.setList(dataList, NotebookItemRVAdapter.VIEW_TYPE_NOTEBOOK_VERTICAL);
        StaggeredGridLayoutManager staggered = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mBinding.recyclerView.setLayoutManager(staggered);
        int spacingInPixels = 15;
        mBinding.recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mBinding.recyclerView.setAdapter(itemAdapter);
        itemAdapter.setOnItemClickListener(this);
        mBinding.includeTopBar.tvTitle.setText(R.string.label_text);
        if (funType == FUN_TYPE_OFFLINE_DATA) {
            mBinding.includeTopBar.tvTitle.setText(R.string.drawer_home);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(String.format(getString(R.string.str_offline_page_num), AFPenClientCtrl.getInstance().getSet().size()));
            mBinding.tvTips.setText(stringBuffer);
        } else {
            mBinding.tvTips.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initEvent() {
    }

    public void onViewClick(View view) {
        if (view.getId() == R.id.iv_back) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (funType == FUN_TYPE_OFFLINE_DATA) {
            if (null == dataList || dataList.size() == 0 && AFPenClientCtrl.getInstance().getOfflineDataDots().size() > 0) {
                dialog = DialogHelper.showConfirm(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        dismissDialog();
                        addBook(0);
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                    }
                }, R.string.confirm_create_notebook, 0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空缓存的离线数据
        if (null != AFPenClientCtrl.getInstance().getOfflineDataDots()) {
            AFPenClientCtrl.getInstance().getOfflineDataDots().clear();
        }
    }

    @Override
    public void onItemClick(View view, int data, Object tag) {
        NoteBookData noteBookData = dataList.get(data);
        Bundle bundle = new Bundle();
        bundle.putString(BaseActivity.NOTEBOOK_ID, noteBookData.notebookId);
        bundle.putString(BaseActivity.NOTE_NAME, noteBookData.noteName);
        if (funType == FUN_TYPE_OFFLINE_DATA) {
            //            gotoActivity(OfflinePageDisplayActivity.class, bundle);
            if (null == AFPenClientCtrl.getInstance().getOfflineDataDots() ||
                    AFPenClientCtrl.getInstance().getOfflineDataDots().size() == 0) {
                ToastUtils.showShort(getString(R.string.offline_data) + getString(R.string.empty));
                return;
            }

            Intent intent = new Intent(this, OfflinePageDisplayActivity.class);
            intent.putExtras(bundle);
            this.startActivityForResult(intent, REQUEST_CODE_OFFLINE);
        } else {
            gotoActivity(LabelSearchActivity.class, bundle);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_OFFLINE == requestCode) {

            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    /**
     * 添加笔记本
     *
     * @param position
     */
    private void addBook(final int position) {
        String bookName = getString(R.string.new_notebook_name);
        if (dataList == null) {
            dataList = new ArrayList<>();
        }

        //在数据块创建一个笔记本
        AppCommon.createNoteBook(NoteTypeEnum.NOTE_TYPE_A5.getNoeType(), String.valueOf(position), getUserUID(), bookName, new IQPenCreateNotebookListener() {
            @Override
            public void onSuccessful(final NoteBookData noteBookData) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (null != noteBookData) {
                            NoteBookData book = new NoteBookData();
                            book.notebookId = noteBookData.notebookId;
                            book.createTime = noteBookData.createTime;
                            book.noteName = noteBookData.noteName;
                            book.noteType = noteBookData.noteType;
                            book.userUid = noteBookData.userUid;
                            int coverIndex = position % Constant.BOOK_COVERS.length;//取余可循环使用封皮
                            book.noteCover = String.valueOf(coverIndex);
                            dataList.add(book);
                            itemAdapter.setList(dataList, NotebookItemRVAdapter.VIEW_TYPE_NOTEBOOK_VERTICAL);

                            AppCommon.setNotebooksChange(true);
                        }
                        itemAdapter.notifyDataSetChanged();
                    }
                });

            }

            @Override
            public void onFail() {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusCarrier carrier) {
        if (null != carrier) {
            switch (carrier.getEventType()) {
                case Constant.OPEN_NOTEBOOK_BY_SEARCH_CODE:
                    finish();
                    break;
            }
        }
    }
}
