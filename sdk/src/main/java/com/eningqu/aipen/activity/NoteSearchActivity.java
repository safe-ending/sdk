package com.eningqu.aipen.activity;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.blankj.utilcode.util.StringUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.NotebookItemRVAdapter;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.utils.SpacesItemDecoration;
import com.eningqu.aipen.databinding.ActivityNoteSearchBinding;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.db.model.NoteBookData_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
* @Author: Qiu.Li
* @Create Date: 2019/4/25 15:56
* @Description: 笔记本搜索
* @Email: liqiupost@163.com
*/
public class NoteSearchActivity extends BaseActivity implements NotebookItemRVAdapter.OnRecyclerViewItemClickListener{

    private final static String TAG = NoteSearchActivity.class.getSimpleName();

    private List<NoteBookData> dataList;
    private NotebookItemRVAdapter itemAdapter;
    private ActivityNoteSearchBinding mBinding;
    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_note_search);
    }

    @Override
    protected void initData() {
        dataList = AppCommon.loadNoteBookData(2);
    }

    @Override
    protected void initView() {

        itemAdapter = new NotebookItemRVAdapter(this);
        itemAdapter.setList(dataList, NotebookItemRVAdapter.VIEW_TYPE_NOTEBOOK_SEARCH);
        StaggeredGridLayoutManager staggered = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        mBinding.recyclerView.setLayoutManager(staggered);
        int spacingInPixels = 15;
        mBinding.recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mBinding.recyclerView.setAdapter(itemAdapter);
        itemAdapter.setOnItemClickListener(this);

    }

    @Override
    protected void initEvent() {
    }

    public void onViewClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.ll_search) {
            searchNote();
        }
    }

    private void searchNote() {
        String noteName = mBinding.includeTopBar.includeNotebookNameEdit.etSearchNoteName.getText().toString();
        if(StringUtils.isEmpty(noteName)){
            dataList = SQLite.select().from(NoteBookData.class).where().queryList();
        }else{
            dataList = SQLite.select().from(NoteBookData.class).where(NoteBookData_Table.noteName.like("%"+noteName + "%")).queryList();
        }
        itemAdapter.setList(dataList, NotebookItemRVAdapter.VIEW_TYPE_NOTEBOOK_SEARCH);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(View view, int data, Object tag) {
//        Bundle bundle = new Bundle();
//        bundle.putString(BaseActivity.NOTEBOOK_ID, (String) view.getTag(R.string.note_book_id));
//        bundle.putInt(BaseActivity.PAGE_NUM, 1);
//        bundle.putString(BaseActivity.NOTE_NAME, (String) view.getTag(R.string.note_name));
//        bundle.putInt(BaseActivity.NOTE_TYPE, (int) view.getTag(R.string.note_type));
//        gotoActivity(DrawNqActivity.class, bundle);

/*        Bundle bundle = PageDrawFragment.newInstance().getArguments();
        if(null==bundle){
            bundle = new Bundle();
            PageDrawFragment.newInstance().setArguments(bundle);
        }
        bundle.putString(BaseActivity.NOTEBOOK_ID, (String) view.getTag(R.string.note_book_id));
        bundle.putInt(BaseActivity.PAGE_NUM, 1);
        bundle.putString(BaseActivity.NOTE_NAME, (String) view.getTag(R.string.note_name));
        bundle.putInt(BaseActivity.NOTE_TYPE, (int) view.getTag(R.string.note_type));
        EventBusCarrier carrier = new EventBusCarrier();
        carrier.setObject(bundle);
        carrier.setEventType(Constant.OPEN_NOTEBOOK_BY_SEARCH_CODE);
        EventBusUtil.post(carrier);*/

        NoteBookData noteBookData = dataList.get(data);
        Bundle bundle = new Bundle();
        bundle.putString(BaseActivity.NOTEBOOK_ID, noteBookData.notebookId);
        bundle.putString(BaseActivity.NOTE_NAME, noteBookData.noteName);
        gotoActivity(LabelSearchActivity.class, bundle);
//        getActivityStackManager().exitAllActivityExceptCurrent(MainActivity.class);
    }

}
