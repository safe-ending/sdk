package com.eningqu.aipen.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.db.model.PageData;
import com.eningqu.aipen.db.model.PageLabelData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/8 11:48
 */

public class PageLabelAdapter extends BaseAdapter implements View.OnClickListener {

    private Context mContext;

    private List<PageData> datas;

    public PageLabelAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setDatas(List<PageData> dataList) {
        this.datas = dataList;
        notifyDataSetChanged();
    }

    public interface IAdapterClickListener{
        void onItemClick(String notebookId, int page);
    }

    private IAdapterClickListener onItemClick;

    public void setOnItemClick(IAdapterClickListener onItemClick) {
        this.onItemClick = onItemClick;
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
        final PageData pageLabelData = datas.get(i);
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_page_label, null, false);
            holder = new ViewHolder();
            holder.labelName = view.findViewById(R.id.tv_label_name);
            // 通过setTag将ViewHolder和View绑定
            view.setTag(holder);
        } else {
            // 获取，通过ViewHolder找到相应的控件
            holder = (PageLabelAdapter.ViewHolder) view.getTag();
        }

        holder.labelName.setText(pageLabelData.name);
        holder.labelName.setTag(i);
//        holder.labelName.setOnClickListener(this);

        return view;
    }

    private long time;

    @Override
    public void onClick(View view) {
        if (System.currentTimeMillis() - time < 500) {
            time = System.currentTimeMillis();
            return;
        } else {
            time = System.currentTimeMillis();
            int position = (int)view.getTag();
            onItemClick.onItemClick(datas.get(position).noteBookId, datas.get(position).pageNum);
        }
    }

    static class ViewHolder {
        TextView labelName;
    }
}
