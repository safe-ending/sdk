package com.eningqu.aipen.activity;

import androidx.databinding.DataBindingUtil;
import android.view.View;

import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.databinding.ActivityMigrationSelectBinding;
import com.eningqu.aipen.db.model.NoteBookData;

import java.util.List;

public class MigrationDataActivity extends BaseActivity {
    ActivityMigrationSelectBinding mBinding;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_migration_select);
        mBinding.includeTopBar.tvTitle.setText(R.string.drawer_data_migration);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initEvent() {

    }

    public void onViewClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.migration_client) {
            List<NoteBookData> noteBookData = AppCommon.loadNoteBookData(2);
            if (noteBookData == null || noteBookData.size() == 0) {
                ToastUtils.showShort(R.string.str_data_null);
                return;
            }
            gotoActivity(ClientActivity.class);
        } else if (id == R.id.migration_server) {
            gotoActivity(ServiceActivity.class);
        }
    }
}
