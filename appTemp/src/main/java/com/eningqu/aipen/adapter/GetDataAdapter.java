package com.eningqu.aipen.adapter;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eningqu.aipen.R;
import com.eningqu.aipen.bean.QpenDataBean;
import com.eningqu.aipen.databinding.ItemMigrationGetBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetDataAdapter extends RecyclerView.Adapter<GetDataAdapter.DataViewHolder> {

    List<QpenDataBean> dataBeans;
    Map<String, Boolean> map = new HashMap<>();
    long length;
    int count;
    int curCount;

    public GetDataAdapter(ArrayList<QpenDataBean> list) {
        dataBeans = list;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemMigrationGetBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.item_migration_get, viewGroup, false);
        return new DataViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder dataViewHolder, int i) {
        QpenDataBean qpenDataBean = dataBeans.get(i);
        dataViewHolder.binding.itemGetDataName.setText(qpenDataBean.getNotebook_name());

        if (map.containsKey(qpenDataBean.getNotebook_id())) {
            Boolean aBoolean = map.get(qpenDataBean.getNotebook_id());
            if (aBoolean) {
                dataViewHolder.binding.itemGetDataIcon.setVisibility(View.VISIBLE);
                dataViewHolder.binding.itemGetDataProgress.setVisibility(View.GONE);
                dataViewHolder.binding.itemGetDataIcon.setImageResource(R.drawable.icon_data_success);
                dataViewHolder.binding.itemGetDataLength.setText(R.string.str_competed);
            } else {
                dataViewHolder.binding.itemGetDataIcon.setVisibility(View.GONE);
                dataViewHolder.binding.itemGetDataProgress.setVisibility(View.VISIBLE);
                int progress = curCount * 100 / count;
                dataViewHolder.binding.itemGetDataLength.setText(progress + "%");
                dataViewHolder.binding.itemGetDataProgress.setProgress(progress);
            }
        } else {
            dataViewHolder.binding.itemGetDataIcon.setVisibility(View.VISIBLE);
            dataViewHolder.binding.itemGetDataProgress.setVisibility(View.GONE);
            dataViewHolder.binding.itemGetDataIcon.setImageResource(R.drawable.icon_data_wait);
            dataViewHolder.binding.itemGetDataLength.setText(R.string.str_wait);
        }
    }

    @Override
    public int getItemCount() {
        return dataBeans == null ? 0 : dataBeans.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        protected ItemMigrationGetBinding binding;

        public DataViewHolder(ItemMigrationGetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setDataInfo(String notebook_id, long length, int count) {
        if (!map.containsKey(notebook_id)) {
            map.put(notebook_id, false);
        }
        this.length = length;
        this.count = count;
        curCount = 0;
    }

    public void setCurCount(int count) {
        this.curCount = count;
    }

    public void setDataStatus(String notebook_id) {
        map.remove(notebook_id);
        map.put(notebook_id, true);
    }

    public Map<String, Boolean> getMap() {
        return map;
    }
}
