package com.eningqu.aipen.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.CalendarPageShowAdapter;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.databinding.ActivityCalendarPageShowBinding;
import com.eningqu.aipen.db.model.PageData;
import com.eningqu.aipen.db.model.PageData_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/13 18:43
 */

public class CalendarPageShowActivity extends BaseActivity implements View.OnClickListener ,AdapterView.OnItemClickListener {

    private final static String TAG = CalendarPageShowActivity.class.getSimpleName();

    //    @BindView(R.id.tv_title)
    //    TextView title;
    //    @BindView(R.id.lv_page_show)
    //    ListView listView;

    private CalendarPageShowAdapter calendarPageShowAdapter;

    private ActivityCalendarPageShowBinding mBinding;
    private String notebookId;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_calendar_page_show);
        //        setContentView(R.layout.activity_calendar_page_show);
    }

    @Override
    protected void initView() {
//        mBinding.layoutTitle.tvTitle.setText(R.string.calendar_text);
        calendarPageShowAdapter = new CalendarPageShowAdapter(this, labelDataList);
        mBinding.lvPageShow.setAdapter(calendarPageShowAdapter);
        mBinding.layoutTitle.ivBack.setOnClickListener(this);
        mBinding.lvPageShow.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(null!=bundle){
            notebookId = bundle.getString(BaseActivity.NOTEBOOK_ID);
            String string = bundle.getString(BaseActivity.NOTE_NAME);
            mBinding.layoutTitle.tvTitle.setText(string);
        }
        searchLabel(notebookId);
    }

    private List<PageData> labelDataList = new ArrayList<>();

    private void searchLabel(String notebookId) {

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

    @Override
    protected void initEvent() {

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListener(final Message message) {
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
    }
}
