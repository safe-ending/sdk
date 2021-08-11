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
import com.eningqu.aipen.activity.PageShowActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/8 19:01
 */

public class NoteSearchAdapter extends BaseAdapter implements View.OnClickListener{

    private Context mContext;

    private List<NoteBookData> datas;

    public NoteSearchAdapter(Context mContext) {
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
        CollectAdapter.ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_collect_note, null, false);
            holder = new CollectAdapter.ViewHolder(view);
            // 通过setTag将ViewHolder和View绑定
            view.setTag(holder);
        } else {
            // 获取，通过ViewHolder找到相应的控件
            holder = (CollectAdapter.ViewHolder) view.getTag();
        }

        view.setTag(R.string.note_book_id, noteBook.id);
        view.setTag(R.string.note_name, noteBook.noteName);
        view.setTag(R.string.note_type, noteBook.noteType);
        view.setTag(R.string.is_lock, noteBook.isLock);

        view.setOnClickListener(this);
        holder.noteName.setText(noteBook.noteName);
        return view;
    }

    @Override
    public void onClick(View v) {
        boolean isLock = (boolean) v.getTag(R.string.is_lock);
        Intent intent = null;
        if(isLock){ //被收藏
            intent = new Intent(mContext, CollectPageShowActivity.class);
            intent.putExtra("noteBookId", (Long) v.getTag(R.string.note_book_id));
            intent.putExtra("noteName", (String) v.getTag(R.string.note_name));
        }else{
            intent = new Intent(mContext, PageShowActivity.class);
            intent.putExtra("noteName", (String) v.getTag(R.string.note_name));
            intent.putExtra("noteType", (int) v.getTag(R.string.note_type));
        }
        mContext.startActivity(intent);
    }


}
