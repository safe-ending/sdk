package com.eningqu.aipen.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eningqu.aipen.R;

import androidx.appcompat.app.AppCompatDialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Zhenglijia
 * @filename SureBaseDialogFragment
 * @date 2018/12/26
 * @email zlj@eningqu.com
 **/
public class SureBaseDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    @BindView(R.id.fragment_msg)
    TextView fragmentMsg;
    @BindView(R.id.fragment_sure)
    TextView fragmentSure;
    @BindView(R.id.fragment_cancel)
    TextView fragmentCancel;

    SureBaseListener sureBaseListener;
    private String msg;

    public SureBaseDialogFragment() {
    }

    public static SureBaseDialogFragment newInstance(String msg) {
        SureBaseDialogFragment newFragment = new SureBaseDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            msg = args.getString("msg");
        }

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    public void setSureBaseListener(SureBaseListener sureBaseListener) {
        this.sureBaseListener = sureBaseListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取布局中的控件
        View root = inflater.inflate(R.layout.fragment_sure_base, container, false);
        ButterKnife.bind(this, root);

        if (!TextUtils.isEmpty(msg)) {
            fragmentMsg.setText(msg);
        }
        fragmentSure.setOnClickListener(this);
        fragmentCancel.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.fragment_sure:
                if (sureBaseListener != null) {
                    sureBaseListener.clickSure();
                }
                break;
            case R.id.fragment_cancel:
                if (sureBaseListener != null) {
                    sureBaseListener.clickCancel();
                }
                break;
        }
    }

    public interface SureBaseListener {
        void clickSure();

        void clickCancel();
    }


}
