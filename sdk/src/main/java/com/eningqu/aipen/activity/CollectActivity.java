package com.eningqu.aipen.activity;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.eningqu.aipen.common.ToastUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.NotebookItemRVAdapter;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.DialogHelper;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.dialog.listener.DeleteListener;
import com.eningqu.aipen.common.thread.ThreadPoolUtils;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.common.utils.HttpUtils;
import com.eningqu.aipen.common.utils.L;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.common.utils.SpacesItemDecoration;
import com.eningqu.aipen.databinding.ActivityCollectBinding;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.db.model.NoteBookData_Table;
import com.eningqu.aipen.db.model.PageData;
import com.eningqu.aipen.db.model.PageData_Table;
import com.eningqu.aipen.qpen.listener.IQPenCollectNotebookListener;
import com.eningqu.aipen.view.SwipeItemLayout;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @Author: Qiu.Li
 * @Create Date: 2019/4/24 17:59
 * @Description: 收藏柜
 * @Email: liqiupost@163.com
 */
public class CollectActivity extends BaseActivity implements NotebookItemRVAdapter.OnRecyclerViewItemClickListener,
        NotebookItemRVAdapter.OnRecyclerItemLongListener, View.OnClickListener {

    private List<NoteBookData> dataList;
    private NotebookItemRVAdapter itemAdapter;
    private ActivityCollectBinding mBinding;

    @Override
    protected void setLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_collect);
        //        setContentView(R.layout.activity_collect);
    }

    @Override
    protected void initView() {
        mBinding.layoutTitle.tvTitle.setText(R.string.title_my_note_collect);

        //添加适配器，这里适配器刚刚装入了数据
        itemAdapter = new NotebookItemRVAdapter(this);
        itemAdapter.setList(dataList, NotebookItemRVAdapter.VIEW_TYPE_NOTEBOOK_COLLECT);
        StaggeredGridLayoutManager staggered = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mBinding.recyclerView.setLayoutManager(staggered);
        int spacingInPixels = 15;
        mBinding.recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mBinding.recyclerView.setAdapter(itemAdapter);
        mBinding.recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));//右滑监听
        itemAdapter.setOnItemClickListener(this);
        itemAdapter.setOnItemLongClickListener(this);
        mBinding.layoutTitle.ivBack.setOnClickListener(this);
        mBinding.layoutTitle.ivRight.setOnClickListener(this);
        mBinding.layoutTitle.ivRight.setVisibility(View.VISIBLE);

    }

    @Override
    protected void initData() {
        searchNote();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        searchNote();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemAdapter.setList(dataList, NotebookItemRVAdapter.VIEW_TYPE_NOTEBOOK_COLLECT);
                itemAdapter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListener(final Message message) {
        switch (message.what) {
            case Constant.NOTE_BOOK_LONG_DELETE_CODE:
                Bundle bundle = new Bundle();
                bundle = (Bundle) message.obj;
                final String notebookId = bundle.getString("note_id");
                final String note_name = bundle.getString("note_name");
                final String note_time = bundle.getString("note_time");
                final int note_type = bundle.getInt("note_type");
                final int position = bundle.getInt("position");
                //                final Long id = (Long) message.obj;

                //                bundle.putString("note_name",note_name);
                //                bundle.putLong("note_id", (Long) view.getTag(R.string.note_book_id));
                //                bundle.putInt("note_type", (Integer) view.getTag(R.string.note_type));
                //                bundle.putInt("position", position);
                dismissDialog();
                dialog = DialogHelper.showDelete(getSupportFragmentManager(), new DeleteListener() {
                    @Override
                    public void rename() {
                        Message msg = new Message();
                        msg.obj = notebookId;
                        msg.what = Constant.NOTE_RENAME_CODE_COLLECT;
                        msg.arg1 = position;
                        EventBusUtil.post(msg);
                    }

                    @Override
                    public void delete() {
                        dismissDialog();
                        dialog = DialogHelper.showDelete(getSupportFragmentManager(), new ConfirmListener() {
                            @Override
                            public void confirm(View view) {
                                dismissDialog();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String token = SpUtils.getString(CollectActivity.this, SpUtils.LOGIN_TOKEN);
                                        String noteTime = note_time.replace(" ", "")
                                                .replace("-", "").replace(":", "");
                                        Date date = TimeUtils.string2Date(note_time);
                                        long currentMillis = TimeUtils.date2Millis(date);
                                        HttpUtils.doGet(AppCommon.BASE_URL + "api/pen-data/delete/" + currentMillis + "_" + note_type,
                                                token, new Callback() {
                                                    @Override
                                                    public void onFailure(Call call, IOException e) {

                                                    }

                                                    @Override
                                                    public void onResponse(Call call, Response response) throws IOException {
                                                        L.error("删除成功", response.body().string());
                                                    }
                                                });
                                    }
                                }).start();
                                deleteNoteBook(notebookId, position, note_type);
                            }

                            @Override
                            public void cancel() {
                                dismissDialog();
                            }
                        }, R.string.confirm_delete_text,0);
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                    }
                });
                break;
            case Constant.NOTE_RENAME_CODE_COLLECT:
                dismissDialog();
                final Long idew = (Long) message.obj;
                final int positionew = message.arg1;
                dialog = DialogHelper.showNoteRename(getSupportFragmentManager(), new ConfirmListener() {
                    @Override
                    public void confirm(View view) {
                        EditText text = (EditText) view;
                        if (!StringUtils.isEmpty(text.getText())) {
                            dismissDialog();
                            renameNoteBook(idew, text.getText().toString(), positionew);
                        } else {
                            ToastUtils.showLong(R.string.dialog_rename_tips);
                        }
                    }

                    @Override
                    public void cancel() {
                        dismissDialog();
                    }
                });
                break;

            case Constant.REFRESH_DATA: {
                dataList = SQLite.select()
                        .from(NoteBookData.class)
                        .where(NoteBookData_Table.isLock.eq(true),
                                NoteBookData_Table.userUid.eq(AppCommon.getUserUID()))
                        .queryList();
                if (itemAdapter != null) {
                    itemAdapter = new NotebookItemRVAdapter(this);
                    itemAdapter.setList(dataList, NotebookItemRVAdapter.VIEW_TYPE_NOTEBOOK_COLLECT);
                    StaggeredGridLayoutManager staggered = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    mBinding.recyclerView.setLayoutManager(staggered);
                    int spacingInPixels = 15;
                    mBinding.recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
                    mBinding.recyclerView.setAdapter(itemAdapter);
                    itemAdapter.setOnItemClickListener(this);
                    itemAdapter.setOnItemLongClickListener(this);
                }
                break;
            }
        }
    }


    /**
     * 重命名笔记本
     */
    private void renameNoteBook(Long id, String newNoteName, int position) {
        SQLite.update(NoteBookData.class)
                .set(NoteBookData_Table.noteName.eq(newNoteName))
                .where(NoteBookData_Table.isLock.eq(true),
                        NoteBookData_Table.id.eq(id),
                        NoteBookData_Table.userUid.eq(AppCommon.getUserUID()))
                .query();
        //        dataList = SQLite.select().from(NoteBookData.class).where(NoteBookData_Table.isLock.eq(true)).queryList();
        dataList.get(position).noteName = newNoteName;
        itemAdapter.notifyItemChanged(position);

        ToastUtils.showLong(R.string.rename_success);
    }

    /**
     * 删除收藏的笔记本
     *
     * @para
     */
    private void deleteNoteBook(String notebookId, int position, final int noteType) {

        final NoteBookData noteBook = SQLite.select().
                from(NoteBookData.class)
                .where(NoteBookData_Table.noteType.eq(noteType),
                        NoteBookData_Table.isLock.eq(true),
                        NoteBookData_Table.userUid.eq(AppCommon.getUserUID()))
                .querySingle();
        SQLite.delete()
                .from(NoteBookData.class)
                .where(NoteBookData_Table.notebookId.eq(notebookId),
                        NoteBookData_Table.userUid.eq(AppCommon.getUserUID()))
                .query();

        SQLite.delete()
                .from(PageData.class)
                .where(PageData_Table.noteBookId.eq(notebookId),
                        PageData_Table.userUid.eq(AppCommon.getUserUID()))
                .query();

        //        dataList = SQLite.select().from(NoteBookData.class).where(NoteBookData_Table.isLock.eq(true)).queryList();
        //        dataList.remove(position);
        itemAdapter.removeItem(position);
        itemAdapter.notifyDataSetChanged();
        //删除本地笔记本下的所有图片
        ThreadPoolUtils.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                sb.append(AppCommon.NQ_SAVE_ROOT_PATH)
                        .append(AppCommon.FILE_SEPARATOR)
                        .append(TimeUtils.date2Millis(TimeUtils.string2Date(noteBook.createTime)) / 1000 + "")
                        .append("_")
                        .append(noteType);
                FileUtils.delete(sb.toString());
            }
        });
        ToastUtils.showLong(R.string.delete_success);
    }

    private long time;

    @Override
    public void onItemClick(View view, int data, Object tag) {
        if ((System.currentTimeMillis() - time) < 300) {
            time = System.currentTimeMillis();
            return;
        }
        NoteBookData noteBookData = dataList.get(data);
        if (null != noteBookData)
            if (view.getId() == R.id.tv_unlock) {
                AppCommon.collectNoteBook(AppCommon.getUserUID(), noteBookData.notebookId, false, new IQPenCollectNotebookListener() {

                    @Override
                    public void onSuccessful() {
                        AppCommon.setNotebooksChange(true);
                        ToastUtils.showShort(R.string.unlock_success);
                        searchNote();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                itemAdapter.setList(dataList, NotebookItemRVAdapter.VIEW_TYPE_NOTEBOOK_COLLECT);
                                itemAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onFail() {
                        ToastUtils.showShort(R.string.unlock_fail);
                    }
                });
            } else {
                List<PageData> pageDataList = AppCommon.loadPageDataList(noteBookData.notebookId, false);
                Bundle bundle = new Bundle();
                bundle.putString(BaseActivity.NOTEBOOK_ID, noteBookData.notebookId);
                if (null != pageDataList && pageDataList.size() > 0) {
                    bundle.putInt(BaseActivity.PAGE_NUM, pageDataList.get(0).pageNum);
                    bundle.putString(BaseActivity.NOTE_NAME, noteBookData.noteName);
                    bundle.putInt(BaseActivity.NOTE_TYPE, noteBookData.noteType);
                    gotoActivity(LabelSearchActivity.class, bundle);
                } else {
                    ToastUtils.showShort(R.string.empty);
                }

                finish();
            }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Message message = new Message();
        message.what = Constant.NOTE_BOOK_LONG_DELETE_CODE;
        //        message.obj = (Long) view.getTag(R.string.note_book_id);
        //        message.arg1 = position;
        //        message.arg2 = (int) view.getTag(R.string.note_type);
        String note_name = (String) view.getTag(R.string.note_name);
        Bundle bundle = new Bundle();
        bundle.putString("note_name", note_name);
        bundle.putString("note_time", (String) view.getTag(R.string.note_time));
        bundle.putLong("note_id", (Long) view.getTag(R.string.note_book_id));
        bundle.putInt("note_type", (Integer) view.getTag(R.string.note_type));
        bundle.putInt("position", position);
        message.obj = bundle;
        EventBusUtil.post(message);
    }

    public void zipUpdate() {
        Message message = new Message();
        message.what = Constant.ZIP_UPDATE;
        message.obj = dataList;
        EventBusUtil.post(message);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_right) {
            Bundle bundle = new Bundle();
            bundle.putInt(NotebookDisplayHorizonActivity.VIEW_TYPE, NotebookDisplayHorizonActivity.NOTEBOOK_LOCK);
            gotoActivity(NotebookDisplayHorizonActivity.class, bundle);
        } else if (id == R.id.iv_back) {
            finish();
        }
    }

    private void searchNote() {
        if (null != dataList) {
            dataList.clear();
        }
        dataList = SQLite.select()
                .from(NoteBookData.class)
                .where(NoteBookData_Table.isLock.eq(true),
                        NoteBookData_Table.userUid.eq(AppCommon.getUserUID()))
                .queryList();
    }
}
