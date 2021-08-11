package com.eningqu.aipen.fragment;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.GetDataAdapter;
import com.eningqu.aipen.base.ui.BaseFragment;
import com.eningqu.aipen.bean.QpenDataBean;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.databinding.FragmentServiceListBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class FragmentServiceList extends BaseFragment {
    private FragmentServiceListBinding mBinding;
    private GetDataAdapter adapter;

    public static FragmentServiceList newInstance() {
        FragmentServiceList newFragment = new FragmentServiceList();
        return newFragment;
    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_service_list;
    }

    @Override
    protected void dataBindingLayout(ViewDataBinding viewDataBinding) {
        mBinding = (FragmentServiceListBinding) viewDataBinding;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    // 普通事件的处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier carrier) {
    }

    public void sendMigration(ArrayList<QpenDataBean> migrationdata) {
        try {
            mBinding.fragmentServiceTxt.setVisibility(View.GONE);
            mBinding.fragmentServiceLayout.setVisibility(View.VISIBLE);
            mBinding.fragmentServiceRecycler.setVisibility(View.VISIBLE);
            adapter = new GetDataAdapter(migrationdata);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            mBinding.fragmentServiceRecycler.setLayoutManager(layoutManager);
            mBinding.fragmentServiceRecycler.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDataInfo(String notebook_id, long length, int count) {
        adapter.setDataInfo(notebook_id, length, count);
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
    }

    public void setCurCount(int curCount) {
        adapter.setCurCount(curCount);
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

    }

    public void setDataStatus(String notebook_id) {
        adapter.setDataStatus(notebook_id);
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
    }

    public int getMapSize() {
        return adapter.getMap().size();
    }
}
