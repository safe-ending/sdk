package com.eningqu.aipen.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.eningqu.aipen.R;


/**
 * Created by yt on 2021 5 17
 */
public class OfflineDialog extends AlertDialog{

    private Context mContext;
    private String mContent;
    private TextView mContentTxt;

    public OfflineDialog(Context context) {
        super(context, R.style.MyDialog);
        mContext = context;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, null);
        mContentTxt = view.findViewById(R.id.dialog_content);
        mContentTxt.setText(mContent);

        setCanceledOnTouchOutside(false);
        setCancelable(false);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
//        lp.width = (int) (dm.widthPixels * 0.66);
        lp.width = SizeUtils.dp2px(260);

        window.setAttributes(lp);
        window.setWindowAnimations(R.style.MyDialog);
        setContentView(view);
    }

    public void setContent(String versionContent) {
        mContent = versionContent;
        mContentTxt.setText(mContent);
    }


}
