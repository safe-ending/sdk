package com.eningqu.aipen.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Zhenglijia
 * @filename DrawerAdapter
 * @date 2019/2/28
 * @email zlj@eningqu.com
 **/
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {
    List<String> arraylist = new ArrayList<>();
    Context mContext;
    DrawerItemClickListener drawerItemClickListener;

    public DrawerAdapter(Context context, List<String> arraylist) {
        mContext = context;
        this.arraylist = arraylist;
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new DrawerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DrawerViewHolder holder, final int position) {
        holder.textView.setText(arraylist.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerItemClickListener != null) {
                    drawerItemClickListener.onItemClick(v, holder, position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (drawerItemClickListener != null) {
                    drawerItemClickListener.onItemLongClick(v, holder, position);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    class DrawerViewHolder extends RecyclerView.ViewHolder {
        @BindView(android.R.id.text1)
        TextView textView;

        public DrawerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnItemClickListener(DrawerItemClickListener drawerItemClickListener) {
        this.drawerItemClickListener = drawerItemClickListener;
    }

    public interface DrawerItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);

        void onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }
}
