package com.eningqu.aipen.adapter;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/20 15:04
 */

public class DemoAdatper /*extends BaseAdapter*/ {
    /*private Context mContext;

    private List<BluetoohDevice> datas = new ArrayList<>();

    public DemoAdatper(Context mContext) {
        this.mContext = mContext;
        initData();
    }

    private void initData(){
        BluetoohDevice bean = null;
        for (int i = 1; i <= 10; i++) {
            bean = new BluetoohDevice();
            bean.setDeviceName("蓝牙设备"+ i + "号");
            datas.add(bean);
        }
    }

    @Override
    public int getCount() {
        return datas.size();
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
        final BluetoohDevice bluetoohDevice = datas.get(i);
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_search_blue, null, false);
            holder = new ViewHolder(view);
            // 通过setTag将ViewHolder和View绑定
            view.setTag(holder);
        } else {
            // 获取，通过ViewHolder找到相应的控件
            holder = (ViewHolder) view.getTag();
        }

        holder.deviceName.setText(bluetoohDevice.getDeviceName());

        return view;
    }

    static class ViewHolder {

        @Nullable
        @BindView(R.id.blue_device_name)
        TextView deviceName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }*/
}
