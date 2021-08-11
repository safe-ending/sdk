package com.eningqu.aipen.activity;

import android.view.View;
import android.widget.TextView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.base.ui.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/6 18:10
 */
@Deprecated
public class SearchActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView title;

    @Override
    protected void setLayout() {
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void initView() {
        title.setText(R.string.drawer_search);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @OnClick({R.id.iv_back, R.id.layout_label_search, R.id.layout_note_search, R.id.layout_calendar_search})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.layout_label_search:
                gotoActivity(LabelSearchActivity.class);
                break;
            case R.id.layout_note_search:
                gotoActivity(NoteSearchActivity.class);
                break;
            case R.id.layout_calendar_search:
                gotoActivity(CalendarSearchActivity.class);
                break;
        }
    }
}
