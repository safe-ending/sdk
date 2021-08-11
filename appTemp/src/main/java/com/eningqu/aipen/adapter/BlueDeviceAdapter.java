package com.eningqu.aipen.adapter;

import android.content.Context;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.qpen.bean.BtDeviceBean;
import com.eningqu.aipen.sdk.bean.device.NQBtDevice;
import com.eningqu.aipen.sdk.bean.device.NQDeviceBase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 说明：蓝牙适配器
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/18 21:51
 */

public class BlueDeviceAdapter extends BaseAdapter {

    private Context mContext;

    //    private List<BluetoothDevice> datas = new ArrayList<>();
    private List<NQBtDevice> datas = new ArrayList<>();

    public BlueDeviceAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(NQBtDevice bluetoothDevice) {
        if (datas.size() > 0) {
            for (NQBtDevice item : datas) {
                if (bluetoothDevice.getName().equals(item.getName())
                        && bluetoothDevice.getMac().equals(item.getMac())) {
                    return;
                }
            }
        }
        datas.add(bluetoothDevice);
        notifyDataSetChanged();
    }

    public void clearDatas() {
        datas.clear();
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
        final NQBtDevice bluetoothDevice = datas.get(i);
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_bluetooth_search, viewGroup, false);
            holder = new ViewHolder(view);
            // 通过setTag将ViewHolder和View绑定
            view.setTag(holder);
        } else {
            // 获取，通过ViewHolder找到相应的控件
            holder = (ViewHolder) view.getTag();
        }
        holder.bluetoothName.setText(bluetoothDevice.getName() + "\n" + bluetoothDevice.getMac().toUpperCase());
        return view;
    }

    static class ViewHolder {

        @Nullable
        @BindView(R.id.bluetooth_name)
        TextView bluetoothName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * ----------事件监听-----------
     */

    public BleDeviceItemClickListener listener;

    public void setClickListener(BleDeviceItemClickListener listener) {
        this.listener = listener;
    }

    public interface BleDeviceItemClickListener {
        void onItemClick(BtDeviceBean bluetoothDevice);
    }
}
