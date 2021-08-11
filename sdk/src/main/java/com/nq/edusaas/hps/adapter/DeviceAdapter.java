package com.nq.edusaas.hps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.eningqu.aipen.R;
import com.eningqu.aipen.sdk.bean.device.NQBtDevice;
import com.eningqu.aipen.sdk.bean.device.NQDeviceBase;
import com.eningqu.aipen.sdk.bean.device.NQUsbDevice;


import java.util.List;



public class DeviceAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;
    List<NQDeviceBase> devices;

    public DeviceAdapter(Context context, List<NQDeviceBase> devices) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.devices = devices;
    }


    @Override
    public int getCount() {
        return devices == null ? 0 : devices.size();
    }

    @Override
    public Object getItem(int i) {
        return devices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup vg;

        if (convertView != null) {
            vg = (ViewGroup) convertView;
        } else {
            vg = (ViewGroup) inflater.inflate(R.layout.device_list_element, null);
        }

        NQDeviceBase device = devices.get(position);
        final TextView tvadd = ((TextView) vg.findViewById(R.id.address));
        final TextView tvname = ((TextView) vg.findViewById(R.id.name));
        final TextView tvpaired = (TextView) vg.findViewById(R.id.paired);
        //final TextView tvrssi = (TextView) vg.findViewById(R.id.rssi);
        if("usb".equals(device.getType())){
            NQUsbDevice nqUsbDevice = (NQUsbDevice) device;

            String pid = Integer.toHexString(nqUsbDevice.getDevice().getProductId());
            String vid = Integer.toHexString(nqUsbDevice.getDevice().getVendorId());

            pid = pid.length()==1?"000"+pid.toUpperCase():pid.toUpperCase();
            vid = vid.length()==1?"000"+vid.toUpperCase():vid.toUpperCase();

            pid = pid.length()==2?"00"+pid.toUpperCase():pid.toUpperCase();
            vid = vid.length()==2?"00"+vid.toUpperCase():vid.toUpperCase();

            pid = pid.length()==3?"0"+pid.toUpperCase():pid.toUpperCase();
            vid = vid.length()==3?"0"+vid.toUpperCase():vid.toUpperCase();

            tvname.setText("PID: "+pid);
            tvadd.setText("VID: "+vid);
            tvpaired.setText("");
        } else if("bt".equals(device.getType())){
            NQBtDevice nqBleDevice = (NQBtDevice) device;

            tvname.setText(nqBleDevice.getName());
            tvadd.setText(nqBleDevice.getMac());
            tvpaired.setText("");
        }
        //tvrssi.setText("Rssi = ");
        return vg;
    }

//    static class ViewHolder {
//
//        ItemBluetoothSearchBinding bind;
//
//        public ViewHolder(View view) {
//            bind = DataBindingUtil.bind(view);
//        }
//    }

    /**
     * ----------事件监听-----------
     */

    public ItemClickListener listener;

    public void setClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(NQBtDevice bluetoothDevice);
    }
}
