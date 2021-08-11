package com.eningqu.aipen.fragment;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import com.eningqu.aipen.R;
import com.eningqu.aipen.adapter.SentDataAdapter;
import com.eningqu.aipen.base.ui.BaseFragment;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.databinding.FragmentClientBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FragmentClient extends BaseFragment implements SentDataAdapter.ISentDataListener {
    FragmentClientBinding mBinding;
    private SentDataAdapter adapter;

    @Override
    protected int setLayout() {
        return R.layout.fragment_client;
    }

    @Override
    protected void dataBindingLayout(ViewDataBinding viewDataBinding) {
        mBinding = (FragmentClientBinding) viewDataBinding;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mBinding.clientRecycler.setLayoutManager(manager);
        adapter = new SentDataAdapter(this);
        mBinding.clientRecycler.setAdapter(adapter);
        selectChanged();
    }

    @Override
    public void selectChanged() {
        int size = adapter.getSelectedList().size();
        mBinding.clientSent.setText(getString(R.string.str_send) + "(" + size + ")");
    }

    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.client_sent_layout:
                if (adapter.getSelectedList().size() > 0) {
                    EventBusCarrier eventBusCarrier = new EventBusCarrier();
                    eventBusCarrier.setEventType(Constant.MIGRATION_SENT);
                    EventBusUtil.post(eventBusCarrier);
                }
                break;
            case R.id.client_select_all:
                adapter.selectAll();
                adapter.notifyDataSetChanged();
                break;
        }
    }

    // 普通事件的处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier carrier) {
    }

    public SentDataAdapter getAdapter() {
        return adapter;
    }
}
