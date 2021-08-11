package com.eningqu.aipen.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.common.utils.ScreenUtils;
import com.eningqu.aipen.qpen.QPenManager;

import java.util.ArrayList;
import java.util.List;

/**
* @Author: Qiu.Li
* @Create Date: 2019/9/24 16:34
* @Description: 颜色选择器
* @Email: liqiupost@163.com
*/
public class ColorSelectorAdapter extends RecyclerView.Adapter<ColorSelectorAdapter.DrawerViewHolder> {
    List<String> arraylist = new ArrayList<>();
    Context mContext;
    ColorItemClickListener drawerItemClickListener;
    public ColorSelectorAdapter(Context context, List<String> arraylist) {
        mContext = context;
        this.arraylist = arraylist;
    }

    public ColorSelectorAdapter(Context context, String[] array) {
        mContext = context;
        for(String str : array){
            this.arraylist.add(str);
        }
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_color_selector, parent, false);
        return new DrawerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DrawerViewHolder holder, final int position) {
        String strColor = arraylist.get(position);
//        holder.textView.setText(strColor);
        //#FFB6C1转成Int
        final int color = Color.parseColor(strColor);
        holder.textView.setBackgroundColor(color);
        ViewGroup.LayoutParams layoutParams = holder.textView.getLayoutParams();
        final int curColor = QPenManager.getInstance().getPaintColor();
        if(curColor == color){
            holder.imageView.setVisibility(View.VISIBLE);
            if(strColor.equals("#FFFFFF")||strColor.equals("#FCFF00")||
                    strColor.equals("#E4E63C")||strColor.equals("#EDEE77")||
                    strColor.equals("#F6F7A9")||strColor.equals("#A0FE7F")||
                    strColor.equals("#D0D0D0")||strColor.equals("#C2FAAF")||
                    strColor.equals("#00FF00")){
                holder.imageView.setImageResource(R.drawable.icon_selected_gray);
            }else {
                holder.imageView.setImageResource(R.drawable.icon_selected_white);
            }
        }else {
            holder.imageView.setVisibility(View.INVISIBLE);
        }

        layoutParams.width = ScreenUtils.dip2px(mContext, 45);
        layoutParams.height = ScreenUtils.dip2px(mContext, 60);

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
//        @BindView(R.id.text1)
        TextView textView;
//        @BindView(R.id.iv_select_state)
        ImageView imageView;
        public DrawerViewHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(this, itemView);
            textView = itemView.findViewById(R.id.text1);
            imageView = itemView.findViewById(R.id.iv_select_state);
        }
    }

    public void setOnItemClickListener(ColorItemClickListener drawerItemClickListener) {
        this.drawerItemClickListener = drawerItemClickListener;
    }

    public interface ColorItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);

        void onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }
}
