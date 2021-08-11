package com.eningqu.aipen.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.PageLabelAdapter;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.databinding.ActivityLabelSearchBinding;
import com.eningqu.aipen.db.model.PageData;
import com.eningqu.aipen.db.model.PageData_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @Author: Qiu.Li
 * @Create Date: 2019/4/25 15:53
 * @Description: 页签搜索
 * @Email: liqiupost@163.com
 */
public class LabelSearchActivity extends BaseActivity implements View.OnClickListener {


    private final static String TAG = LabelSearchActivity.class.getSimpleName();

    private PageLabelAdapter pageLabelAdapter;
    private List<PageData> labelDataList;
    private ActivityLabelSearchBinding mBinding;
    private String notebookId;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_label_search);
    }

    @Override
    protected void initView() {
        pageLabelAdapter = new PageLabelAdapter(this);
        if (pageLabelAdapter.getCount() == 0) {
            mBinding.lvLabel.setEmptyView(mBinding.emptyLayout);
        }

        mBinding.lvLabel.setAdapter(pageLabelAdapter);
        mBinding.lvLabel.setOnItemClickListener(onItemClickListener);
        mBinding.ivBack.setOnClickListener(this);
        mBinding.btnSearch.setOnClickListener(this);

        pageLabelAdapter.setDatas(labelDataList);
        if (pageLabelAdapter.getCount() == 0) {
            mBinding.lvLabel.setEmptyView(mBinding.emptyLayout);
        }
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            notebookId = bundle.getString(BaseActivity.NOTEBOOK_ID);
        }

        /*List<PageData> pageDataList = SQLite.select()
                .from(PageData.class)
                .where(PageData_Table.userUid.eq(AppCommon.getUserUID()),
                        PageData_Table.noteBookId.eq(notebookId))
                .queryList();*/
        /*List<String> ids = new ArrayList<>();
        for (PageData pageData : pageDataList) {
            ids.add(pageData.noteBookId);
        }*/
        searchLabel(notebookId);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.btn_search) {
            searchLabel(notebookId);
            pageLabelAdapter.setDatas(labelDataList);
            if (pageLabelAdapter.getCount() == 0) {
                mBinding.lvLabel.setEmptyView(mBinding.emptyLayout);
            }
        }
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Bundle bundle = PageDrawFragment.newInstance().getArguments();
//            if(null==bundle){
//                bundle = new Bundle();
//            }
            Bundle bundle = new Bundle();
            bundle.putString(BaseActivity.NOTEBOOK_ID, labelDataList.get(position).noteBookId);
            bundle.putInt(BaseActivity.PAGE_NUM, labelDataList.get(position).pageNum);
            bundle.putString(BaseActivity.NOTE_NAME, labelDataList.get(position).name);
            bundle.putInt(BaseActivity.NOTE_TYPE, labelDataList.get(position).noteType);
            EventBusCarrier carrier = new EventBusCarrier();
            carrier.setObject(bundle);
            carrier.setEventType(Constant.OPEN_NOTEBOOK_BY_SEARCH_CODE);
            EventBusUtil.postSticky(carrier);
            finish();
//            getActivityStackManager().exitAllActivityExceptCurrent(MainActivity.class);
        }
    };

    private PageLabelAdapter.IAdapterClickListener adapterClickListener = new PageLabelAdapter.IAdapterClickListener() {
        @Override
        public void onItemClick(String notebookId, int page) {
//            AppCommon.setCurrentNotebookId(notebookId);

        }
    };

    private void searchLabel(String notebookId) {
        String labelName = mBinding.labelInput.getText().toString();
        if (!TextUtils.isEmpty(labelName)) {
            if (!TextUtils.isEmpty(notebookId)) {
                labelDataList = SQLite.select().from(PageData.class).
                        where(PageData_Table.userUid.eq(AppCommon.getUserUID()),
                                PageData_Table.noteBookId.eq(notebookId),
                                PageData_Table.name.like("%" + labelName + "%")).queryList();
            } else {
                labelDataList = SQLite.select().from(PageData.class).
                        where(PageData_Table.userUid.eq(AppCommon.getUserUID()),
                                PageData_Table.name.like("%" + labelName + "%")).queryList();
            }
        } else {
            if (TextUtils.isEmpty(notebookId)) {
                labelDataList = SQLite.select().from(PageData.class).
                        where(PageData_Table.userUid.eq(AppCommon.getUserUID()),
                                PageData_Table.name.notEq("")).queryList();
            } else {
                labelDataList = SQLite.select().from(PageData.class).
                        where(PageData_Table.userUid.eq(AppCommon.getUserUID()),
                                PageData_Table.noteBookId.eq(notebookId),
                                PageData_Table.name.notEq("")).queryList();
            }
        }

        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = labelDataList.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        labelDataList.clear();
        labelDataList.addAll(newList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListner(final Message message) {
        switch (message.what) {
            case Constant.PAGE_LABEL_CLICK_CODE:
                Bundle bundle = message.getData();
                String pageId = bundle.getString(Constant.PAGE_NUM_ID);

                PageData pageData = SQLite.select()
                        .from(PageData.class)
                        .where(PageData_Table.noteBookId.eq(pageId),
                                PageData_Table.userUid.eq(AppCommon.getUserUID()))
                        .querySingle();

                if (pageData != null) {
                    if (pageData.isLock) { //当前页是否被收藏过
                        gotoActivity(CollectDrawActivity.class, bundle);
                    } else {
                        AppCommon.switchPageData(pageData.noteType, pageData.pageNum);
                    }
                }
                break;
        }
    }
}
