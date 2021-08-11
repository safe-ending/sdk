package com.eningqu.aipen.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eningqu.aipen.R;
import com.eningqu.aipen.activity.CalendarPageShowActivity;
import com.eningqu.aipen.base.ui.BaseActivity;
import com.eningqu.aipen.databinding.ItemHistoryBookBinding;

import java.util.ArrayList;

/**
 * @author Zhenglijia
 * @filename HistoryBookAdapter
 * @date 2019/4/11
 * @email zlj@eningqu.com
 **/
public class HistoryBookAdapter extends RecyclerView.Adapter<HistoryBookAdapter.HistoryViewHolder> {

    ArrayList<CalendarPageShowAdapter.CalendarPage> arrayList = new ArrayList<>();
    String dayStr;
    Context mContext;

    public HistoryBookAdapter(Context context) {
        mContext = context;
    }

    public void setArrayList(ArrayList<CalendarPageShowAdapter.CalendarPage> arrayList, String dayStr) {
        this.arrayList = arrayList;
        this.dayStr = dayStr;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemHistoryBookBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_history_book, parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        final CalendarPageShowAdapter.CalendarPage calendarPage = arrayList.get(position);
        holder.binding.historyLayoutText.setText(TextUtils.isEmpty(calendarPage.getNoteName()) ? "null" : calendarPage.getNoteName());
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CalendarPageShowActivity.class);
//                intent.putExtra("date", dayStr);
//                mContext.startActivity(intent);
                Bundle bundle = new Bundle();
                bundle.putString(BaseActivity.NOTEBOOK_ID, calendarPage.getId());
                bundle.putString(BaseActivity.NOTE_NAME,calendarPage.getNoteName());
//                bundle.putInt(BaseActivity.PAGE_NUM, calendarPage.getPageNum());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        protected final ItemHistoryBookBinding binding;

        public HistoryViewHolder(ItemHistoryBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}
