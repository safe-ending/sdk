package com.eningqu.aipen.fragment;

import android.annotation.TargetApi;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eningqu.aipen.R;
import com.eningqu.aipen.common.Constant;
import com.eningqu.aipen.common.EventBusCarrier;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.databinding.FragmentLogoutBinding;

/**
 * @author Zhenglijia
 * @filename LogoutDialogFragment
 * @date 2019/4/10
 * @email zlj@eningqu.com
 **/
public class LogoutDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private FragmentLogoutBinding dataBinding;

    public static LogoutDialogFragment newInstance() {
        LogoutDialogFragment newFragment = new LogoutDialogFragment();
        return newFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取布局中的控件
        View root = inflater.inflate(R.layout.fragment_logout, container, false);

        dataBinding = DataBindingUtil.bind(root);

        dataBinding.fragmentLogout.setOnClickListener(this);
        dataBinding.fragmentCancel.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_logout:
                dismiss();
//                EventBus.getDefault().post("logout");
                EventBusCarrier eventBusCarrier = new EventBusCarrier();
                eventBusCarrier.setEventType(Constant.USER_LOGOUT);
                EventBusUtil.postSticky(eventBusCarrier);
                break;
            case R.id.fragment_cancel:
                dismiss();
                break;
        }
    }
}
