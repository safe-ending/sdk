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
    TextView title;

    @Override
    protected void setLayout() {
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void initView() {
        title = findViewById(R.id.tv_title);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        findViewById(R.id.layout_label_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(LabelSearchActivity.class);

            }
        });
        findViewById(R.id.layout_note_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(NoteSearchActivity.class);

            }
        });
        findViewById(R.id.layout_calendar_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(CalendarSearchActivity.class);

            }
        });

        title.setText(R.string.drawer_search);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }


}
