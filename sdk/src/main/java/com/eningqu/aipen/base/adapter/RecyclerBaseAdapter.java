package com.eningqu.aipen.base.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/19 20:02
 */

public abstract class RecyclerBaseAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {

    Context mContext;

    List<T> datas;

    protected OnItemClickListener mOnItemClickListener;

    public RecyclerBaseAdapter(Context mContext, List<T> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public int getItemViewType(int position) {
        return getViewType(position);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return getViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        covert(holder, datas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    protected abstract int getContentView(int viewType);

    protected abstract void covert(final RecyclerViewHolder holder, final T data, final int position);

    protected abstract RecyclerViewHolder getViewHolder(ViewGroup parent, int viewType);

    protected abstract int getViewType(int position);

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
