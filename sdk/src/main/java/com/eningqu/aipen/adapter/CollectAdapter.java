package com.eningqu.aipen.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.db.model.NoteBookData;
import com.eningqu.aipen.activity.CollectPageShowActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/6 14:45
 */

public class CollectAdapter extends BaseAdapter implements View.OnClickListener, View.OnLongClickListener{

    private Context mContext;

    private List<NoteBookData> datas;

    public CollectAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setDatas(List<NoteBookData> dataList) {
        this.datas = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final NoteBookData noteBook = datas.get(i);
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_collect_note, null, false);
            holder = new ViewHolder(view);
            // 通过setTag将ViewHolder和View绑定
            view.setTag(holder);
        } else {
            // 获取，通过ViewHolder找到相应的控件
            holder = (ViewHolder) view.getTag();
        }
        view.setTag(R.string.note_time, noteBook.createTime);
        view.setTag(R.string.note_book_id, noteBook.id);
        view.setTag(R.string.note_name, noteBook.noteName);
        view.setTag(R.string.note_type,noteBook.noteType);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        holder.noteName.setText(noteBook.noteName);
        return view;
    }

    @Override
    public void onClick(View v) {
        Long id = (Long) v.getTag(R.string.note_book_id);
        Intent intent = new Intent(mContext, CollectPageShowActivity.class);
        intent.putExtra("noteBookId", id);
        intent.putExtra("noteName", (String) v.getTag(R.string.note_name));
        mContext.startActivity(intent);
    }

    @Override
    public boolean onLongClick(View view) {
//        Message message = new Message();
//        message.what = Constant.NOTE_BOOK_LONG_DELETE_CODE;
//        message.obj = (Long) view.getTag(R.string.note_book_id);
//        message.arg1 = position;
//        message.arg2 = (int) view.getTag(R.string.note_type);
//        String note_name = (String) view.getTag(R.string.note_name);
//        Bundle bundle = new Bundle();
//        bundle.putString("note_name",note_name);
//        bundle.putString("note_time",(String) view.getTag(R.string.note_time));
//        bundle.putLong("note_id", (Long) view.getTag(R.string.note_book_id));
//        bundle.putInt("note_type", (Integer) view.getTag(R.string.note_type));
//        message.obj = bundle;
//        EventBusUtil.post(message);
        return true;
    }

    static class ViewHolder {

        @Nullable
        TextView noteName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            noteName = view.findViewById(R.id.et_note_name);
        }
    }
}