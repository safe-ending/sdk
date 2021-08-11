package com.eningqu.aipen.base.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/19 20:43
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder{

    View mView;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public View getView(){
        return mView;
    }
}
