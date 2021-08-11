package com.eningqu.aipen.activity;

import androidx.databinding.DataBindingUtil;

import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.CalendarPageShowAdapter;
import com.eningqu.aipen.adapter.HistoryBookAdapter;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.databinding.ActivityCalendarSearchBinding;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.db.model.NoteBookData_Table;
import com.eningqu.aipen.db.model.PageData;
import com.eningqu.aipen.db.model.PageData_Table;
import com.eningqu.aipen.db.model.PageLabelData_Table;
import com.eningqu.aipen.view.CalendarView;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/13 10:03
 */

public class CalendarSearchActivity extends BaseActivity implements CalendarView.onCalendarClickListener {

    private final static String TAG = CalendarSearchActivity.class.getSimpleName();
    HistoryBookAdapter adapter;

    //    @BindView(R.id.calendar)
    //    CalendarView calendarView;
    //    @BindView(R.id.tv_title)
    //    TextView title;

    private Set<String> dateSet = new HashSet<>();
    private ActivityCalendarSearchBinding mBinding;
    private ArrayList<CalendarPageShowAdapter.CalendarPage> dataList = new ArrayList<>();

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_calendar_search);
        //        setContentView(R.layout.activity_calendar_search);
    }

    @Override
    protected void initView() {
        mBinding.layoutTitle.tvTitle.setText(R.string.calendar_text);
        mBinding.calendar.setOnClickListener(this);
        mBinding.calendar.setSelectDate(dateSet);
        mBinding.layoutTitle.ivBack.setOnClickListener(listener);
        mBinding.calendarRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new HistoryBookAdapter(this);
        mBinding.calendarRecycler.setAdapter(adapter);

        onDayClick(0, getMonthStr());
    }

    @Override
    protected void initData() {
        List<PageData> pageDataList = SQLite.select()
                .from(PageData.class)
                .where(PageData_Table.userUid.eq(AppCommon.getUserUID()))
                .queryList();
        String date;
        for (PageData page : pageDataList) {
            date = TimeUtils.date2String(TimeUtils.string2Date(page.lastModifyTime, new SimpleDateFormat("yyyy-MM-dd")), new SimpleDateFormat("yyyy-MM-dd"));
            dateSet.add(date);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void initEvent() {

    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.iv_back) {
                finish();
            }
        }
    };

    @Override
    public void onLeftRowClick() {
        mBinding.calendar.monthChange(-1);
    }

    @Override
    public void onRightRowClick() {
        mBinding.calendar.monthChange(1);
    }

    @Override
    public void onDayClick(int day, String dayStr) {
        for (String date : dateSet) {
            if (StringUtils.equals(date, dayStr)) {
                mBinding.historyLayout.setVisibility(View.VISIBLE);
                getHistoryBooks(dayStr);
                adapter.setArrayList(dataList, dayStr);
                adapter.notifyDataSetChanged();
                //                Bundle bundle = new Bundle();
                //                bundle.putString("date", dayStr);
                //                gotoActivity(CalendarPageShowActivity.class, bundle);
                break;
            } else {
                mBinding.historyLayout.setVisibility(View.GONE);
                //                ToastUtils.showLong(R.string.no_page);
            }
        }
    }

    /**
     * 获取月份标题
     */
    private String getMonthStr() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(System.currentTimeMillis());
    }


    protected void getHistoryBooks(String date) {
        dataList.clear();
        List<PageData> pageDataList =
                SQLite.select(PageData_Table.id,
                        PageData_Table.pageNum,
                        PageData_Table.noteBookId,
                        PageData_Table.noteType,
                        PageData_Table.isLock,
                        PageData_Table.lastModifyTime,
                        PageData_Table.picUrl,
                        PageData_Table.userUid)
                        .from(PageData.class)
                        .where(PageData_Table.lastModifyTime.like(date + "%"),
                                PageData_Table.userUid.eq(AppCommon.getUserUID()))
                        .orderBy(PageData_Table.noteBookId, true).groupBy(PageLabelData_Table.noteBookId)
                        .queryList();

        List<NoteBookData> noteBookDataList = SQLite.select()
                .from(NoteBookData.class)
                .where(NoteBookData_Table.userUid.eq(AppCommon.getUserUID()))
                .queryList();

        CalendarPageShowAdapter.CalendarPage calendarPage = null;

        boolean exist = false;
        for (NoteBookData noteBookData : noteBookDataList) {

            for (PageData pageData : pageDataList) {
                if (noteBookData.notebookId.equals(pageData.noteBookId)) {
                    exist = true;

                    calendarPage = new CalendarPageShowAdapter.CalendarPage();
                    calendarPage.setId(pageData.noteBookId);
                    calendarPage.setCreateTime(pageData.lastModifyTime);
                    calendarPage.setLock(pageData.isLock);
                    calendarPage.setPageNum(pageData.pageNum);
                    if (TextUtils.isEmpty(noteBookData.noteName)) {
                        calendarPage.setNoteName("Notebook " + noteBookData.createTime);
                    } else {
                        calendarPage.setNoteName(noteBookData.noteName);
                    }
                    break;
                }
            }
            if (exist) {
                dataList.add(calendarPage);
                exist = false;
            }
        }
        /*for (PageData pageData : pageDataList) {
            calendarPage = new CalendarPageShowAdapter.CalendarPage();
            calendarPage.setId(pageData.noteBookId);
            calendarPage.setCreateTime(pageData.lastModifyTime);
            calendarPage.setLock(pageData.isLock);
            calendarPage.setPageNum(pageData.pageNum);
            if (calendarPage.isLock()) {  //被收藏
                for (NoteBookData noteBookData : noteBookDataList) {
                    if (noteBookData.notebookId.equals(pageData.noteBookId)) {
                        if(TextUtils.isEmpty(noteBookData.noteName)){
                            calendarPage.setNoteName("Notebook "+noteBookData.createTime);
                        }else {
                            calendarPage.setNoteName(noteBookData.noteName);
                        }
                        break;
                    }
                }
            } else { //没有收藏
                for (NoteBookData noteBookData : noteBookDataList) {
                    if (!noteBookData.isLock && noteBookData.notebookId.equals(pageData.noteBookId)) {
                        if(TextUtils.isEmpty(noteBookData.noteName)){

                            calendarPage.setNoteName("Notebook "+noteBookData.createTime);
                        }else {
                            calendarPage.setNoteName(noteBookData.noteName);
                        }
                        dataList.add(calendarPage);
                        break;
                    }
                }
            }

        }*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusCarrier carrier) {
        if(null!=carrier){
            switch (carrier.getEventType()) {
                case Constant.OPEN_NOTEBOOK_BY_SEARCH_CODE:
                    CalendarSearchActivity.this.finish();
                    break;
            }
        }
    }
}
