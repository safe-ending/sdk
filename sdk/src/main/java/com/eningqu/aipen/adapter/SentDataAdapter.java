package com.eningqu.aipen.adapter;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eningqu.aipen.R;
import com.eningqu.aipen.common.AppCommon;
import com.eningqu.aipen.databinding.ItemSentDataBinding;
import com.eningqu.aipen.db.model.NoteBookData;

import java.util.ArrayList;
import java.util.List;

public class SentDataAdapter extends RecyclerView.Adapter<SentDataAdapter.DataViewHolder> {

    private List<NoteBookData> dataList;
    List<Integer> selectedList = new ArrayList<>();
    ISentDataListener listener;

    public SentDataAdapter(ISentDataListener listener) {
        dataList = AppCommon.loadNoteBookData(2);
        this.listener = listener;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemSentDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.item_sent_data, viewGroup, false);
        return new DataViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder dataViewHolder, int i) {
        dataViewHolder.binding.itemSentDataName.setText(dataList.get(i).noteName);
        dataViewHolder.binding.itemSentDataIcon.setTag(i);
        if (selectedList.contains(i)) {
            dataViewHolder.binding.itemSentDataIcon.setImageResource(R.drawable.icon_data_selected);
        } else {
            dataViewHolder.binding.itemSentDataIcon.setImageResource(R.drawable.iocn_data_empty);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ItemSentDataBinding binding;

        public DataViewHolder(ItemSentDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.itemSentDataIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();
            if (selectedList.contains(tag)) {
                selectedList.remove((Integer) tag);
            } else {
                selectedList.add(tag);
            }
            notifyItemChanged(tag);
            listener.selectChanged();
        }
    }

    public interface ISentDataListener {
        public void selectChanged();
    }

    public List<Integer> getSelectedList() {
        return selectedList;
    }

    public List<NoteBookData> getDataList() {
        return dataList;
    }

    public void selectAll() {
        for (int i = 0; i < dataList.size(); i++) {
            if (!selectedList.contains(i)) {
                selectedList.add(i);
            }
        }
    }
}
