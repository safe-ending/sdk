package com.eningqu.aipen.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eningqu.aipen.R;
import com.eningqu.aipen.common.utils.ImageUtil;

/**
 * Created by peijiadi on 16/1/18.
 */
public class FragmentBook extends Fragment {
    private View rootView;
    private int mBgRes = -1;
    private String mUrl;
    private boolean isEmpty = false;

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frament_book, container, false);
        //        rootView.setBackgroundResource(mBgRes);
        ImageView imageView = rootView.findViewById(R.id.iv_root_bg);
        if (mBgRes != -1) {
            imageView.setBackgroundResource(mBgRes);
        } else if (!TextUtils.isEmpty(mUrl) && mUrl.startsWith("http")) {
            ImageUtil.load(getActivity(), mUrl, imageView);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setBackground(int res) {
        if (null != rootView) {
            ImageView imageView = rootView.findViewById(R.id.iv_root_bg);
            imageView.setBackgroundResource(res);
            imageView.invalidate();
        }
        mBgRes = res;
    }

    public void setBackground(String url) {
        if (null != rootView) {
            ImageView imageView = rootView.findViewById(R.id.iv_root_bg);
            ImageUtil.load(getActivity(), url, imageView);
        }
        mUrl = url;
    }

    public static FragmentBook newInstance() {
        FragmentBook fragment8 = new FragmentBook();
        return fragment8;
    }

    @Override
    public void onDestroy() {
        rootView = null;
        super.onDestroy();
    }
}

