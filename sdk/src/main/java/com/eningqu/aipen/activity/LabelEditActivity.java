package com.eningqu.aipen.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.PageLabelItemRVAdapter;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.utils.SpacesItemDecoration;
import com.eningqu.aipen.databinding.ActivityLabelEditBinding;
import com.eningqu.aipen.db.model.PageData;
import com.eningqu.aipen.qpen.QPenManager;

import java.util.List;

/**
 * @Author: Qiu.Li
 * @Create Date: 2019/4/25 15:56
 * @Description: 页签编辑
 * @Email: liqiupost@163.com
 */
public class LabelEditActivity extends BaseActivity implements PageLabelItemRVAdapter.OnRecyclerViewItemClickListener {

    private final static String TAG = LabelEditActivity.class.getSimpleName();

    private List<PageData> dataList;
    private PageLabelItemRVAdapter itemAdapter;
    private ActivityLabelEditBinding mBinding;
    private int curPage;
    private String label;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_label_edit);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String notebookId = bundle.getString(BaseActivity.NOTEBOOK_ID);
        label = bundle.getString(BaseActivity.PAGE_LABEL_NAME);
        curPage = bundle.getInt(BaseActivity.PAGE_NUM);
        if(null!=dataList){
            dataList.clear();
        }
        dataList = AppCommon.loadPageDataList(notebookId, true);
    }

    @Override
    protected void initView() {

        itemAdapter = new PageLabelItemRVAdapter(this);
        itemAdapter.setList(dataList, PageLabelItemRVAdapter.VIEW_TYPE_PAGELABEL);
        StaggeredGridLayoutManager staggered = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mBinding.recyclerView.setLayoutManager(staggered);
        int spacingInPixels = 15;
        mBinding.recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mBinding.recyclerView.setAdapter(itemAdapter);
        itemAdapter.setOnItemClickListener(this);
        mBinding.includeTopBar.tvRight.setText(R.string.save);
        mBinding.includeTopBar.includeNotebookNameEdit.etName.setHint(R.string.dialog_edit_tips);
        if(null!= mBinding.includeTopBar.includeNotebookNameEdit.etName && null!=label){
            mBinding.includeTopBar.includeNotebookNameEdit.etName.setText(label);
        }
    }

    @Override
    protected void initEvent() {
    }

    public void onViewClick(View view) {
        EditText editText = mBinding.includeTopBar.includeNotebookNameEdit.etName;
        String labelName = editText.getText().toString().trim();
        int id = view.getId();
        if (id == R.id.iv_back) {/*if(TextUtils.isEmpty(labelName)){
                    ToastUtils.showShort(R.string.label_is_empty);
                    return;
                }*/
            finish();
        } else if (id == R.id.ll_delete) {
            if (AppCommon.isCurrentNotebookLocked()) {
                ToastUtils.showShort(R.string.collected_canot_modif);
                return;
            }
            editText.setText("");
        } else if (id == R.id.tv_right) {
            if (AppCommon.isCurrentNotebookLocked()) {
                ToastUtils.showShort(R.string.collected_canot_modif);
                return;
            }
            if (TextUtils.isEmpty(labelName)) {
                ToastUtils.showShort(R.string.label_is_empty);
                return;
            }
            Intent intent = getIntent();
            if (AppCommon.saveLabel(AppCommon.getCurrentNotebookId(), curPage, AppCommon.getCurrentNoteType(),
                    labelName, QPenManager.getInstance().getCurrentBitmap())) {
                intent.putExtra("label", labelName);
            } else {
                ToastUtils.showShort(R.string.save_fail);
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(View view, int data, Object tag) {
        if (AppCommon.isCurrentNotebookLocked()) {
            Toast.makeText(this, R.string.collected_canot_modif, Toast.LENGTH_SHORT).show();
            return;
        }
        PageData pageData = dataList.get(data);
        if(view.getId()==R.id.main){
            AppCommon.saveLabel(AppCommon.getCurrentNotebookId(), curPage,  AppCommon.getCurrentNoteType(),
                    pageData.name, QPenManager.getInstance().getCurrentBitmap());

            Intent intent = getIntent();
            intent.putExtra("label",pageData.name);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
