package com.eningqu.aipen.activity;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.RecordListAdapter;
import com.eningqu.aipen.qpen.bean.AudioRecordBean;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.dialog.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.utils.AudioUtil;
import com.eningqu.aipen.common.utils.FileUtils;
import com.eningqu.aipen.databinding.ActivityRecordListBinding;
import com.eningqu.aipen.qpen.service.MediaService;
import com.eningqu.aipen.view.SwipeItemLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Qiu.Li
 * @Create Date: 2019/5/10 21:45
 * @Description: 录音列表
 * @Email: liqiupost@163.com
 */
public class RecordListActivity extends DrawBaseActivity implements View.OnClickListener {
    private ActivityRecordListBinding mBinding;
    RecordListAdapter adapter;
    private List<AudioRecordBean> records;
    protected List<String> files;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_record_list);
    }

    @Override
    protected void initView() {
        mBinding.layoutTitle.ivBack.setOnClickListener(this);
        mBinding.layoutTitle.tvTitle.setText(R.string.str_record);
    }

    @Override
    protected void initData() {
        mContext = this;
        adapter = new RecordListAdapter(this);
        adapter.setRecords(records);
        adapter.setDeleteListener(deleteListener);
        mBinding.rvRecordList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvRecordList.setAdapter(adapter);
        //右滑监听
        mBinding.rvRecordList.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));

        if (records == null)
            records = new ArrayList<>();
        else
            records.clear();

        files = searchFiles(new ArrayList<String>());

        if (files.size() > 0) {
            for (int i = 0; i < files.size(); i++) {
                String str = files.get(i);
                AudioRecordBean bean = new AudioRecordBean();
                bean.filePath = str;
                File file = new File(str);
                long time = file.length() / 2 / 16000;
                String substring = str.substring(str.lastIndexOf("/") + 1, str.lastIndexOf("."));
                //音频文件文件名格式: 时间长度毫秒_创建时间戳
                String[] split = substring.split("_");
                if (split.length > 1) {
                    bean.name = split[1];
                    bean.createTime = Long.valueOf(bean.name);
                    bean.duration = time * 1000;
                } else {
                    bean.name = substring;
                    bean.createTime = Long.valueOf(bean.name);
                    bean.duration = time * 1000;
                }
                bean.postion = files.size() - i;
                records.add(bean);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private List<String> searchFiles(List<String> list) {
        String[] ext = {"pcm"};
        File dir = new File(AppCommon.getAudioPathDir(AppCommon.getUserUID(), AppCommon.getCurrentNotebookId(), AppCommon.getCurrentPage(), ""));
        return FileUtils.search(list, dir, ext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setRecords(records);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.setCurIndex(-1);
        AudioUtil.getInstance().stopPlay();
        MediaService.getInstance().resetMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaService.getInstance().stop();
        if (adapter != null) {
            adapter.stopHandler();
            adapter.cleanHandler();
        }
    }

    private RecordListAdapter.RecordListDeleteListener deleteListener = new RecordListAdapter.RecordListDeleteListener() {
        @Override
        public void onDelete(final int position) {
            if (position >= records.size()) {
                return;
            }
            if (null != AppCommon.getCurrentNoteBookData() && !AppCommon.getCurrentNoteBookData().isLock) {
                final AudioRecordBean bean = records.get(position);
                dismissDialog();
                dialog = DialogHelper.showDelete(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        dismissDialog();
                        if (adapter.getCurIndex() == position) {
                            MediaService.getInstance().stop();
                            adapter.stopHandler();
                            adapter.setCurIndex(-1);
                        } else if (adapter.getCurIndex() > position) {
                            adapter.setCurIndex(adapter.getCurIndex() - 1);
                        }
                        if (!TextUtils.isEmpty(bean.filePath)) {
                            FileUtils.deleteFile(bean.filePath);
                            records.remove(position);
                            adapter.setRecords(records);
                            adapter.notifyDataSetChanged();
                            ToastUtils.showShort(mContext.getString(R.string.delete_success));
                        }
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                    }
                }, R.string.confirm_delete_record, R.string.str_delete_record_title);
            } else {
                ToastUtils.showShort(R.string.collected_canot_modif);
            }
        }
    };
}
