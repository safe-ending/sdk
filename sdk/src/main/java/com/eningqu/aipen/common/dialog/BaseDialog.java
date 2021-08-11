package com.eningqu.aipen.common.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.support.v4.app.PDialogFragment;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.eningqu.aipen.R;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/3 17:33
 */

public class BaseDialog extends PDialogFragment {

    /*** 回调获得需要显示的dialog*/
    private OnCallDialog mOnCallDialog;

    /*** 透明度*/
    private float alpha;

    private int animation = -1;

    private boolean dialogMatchParent = true;
    private boolean dialogMatchParentHeight = true;

    public static BaseDialog newInstance(@NonNull OnCallDialog mOnCallDialog, @NonNull float alpha, @NonNull int animation) {
        BaseDialog dialog = new BaseDialog();
        dialog.mOnCallDialog = mOnCallDialog;
        dialog.alpha = alpha;
        dialog.animation = animation;
        return dialog;
    }


    /***
     * 一般用于创建替代传统的 Dialog 对话框的场景，UI 简单，功能单一
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (null == mOnCallDialog) {
            super.onCreate(savedInstanceState);
        }
        if(null!=mOnCallDialog){
            return mOnCallDialog.getDialog(getActivity());
        }
        return null;
    }

    /**
     * 一般用于创建复杂内容弹窗或全屏展示效果的场景，UI 复杂，功能复杂，一般有网络请求等异步操作
     *
     * @return
     */
    /*
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    */
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            // 在 5.0 以下的版本会出现白色背景边框，若在 5.0 以上设置则会造成文字部分的背景也变成透明
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                // 目前只有这两个 dialog 会出现边框
                if (dialog instanceof ProgressDialog || dialog instanceof DatePickerDialog) {
                    getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            }
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams windowParams = window.getAttributes();
            if (animation != -1) {
                //设置dialog的动画
                windowParams.windowAnimations = animation;
            }
            windowParams.dimAmount = alpha;
            super.onStart();
            window.setAttributes(windowParams);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null) {

            /*
             * 将对话框的大小按屏幕大小的百分比设置
             */
            Window window = getDialog().getWindow();

            if(null!=window){

                if (dialogMatchParent) {
                    window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                } else {
                    DisplayMetrics dm = new DisplayMetrics();
                    window.getWindowManager().getDefaultDisplay().getMetrics(dm);
                    WindowManager.LayoutParams lp = window.getAttributes(); // 获取对话框当前的参数值
                    lp.width = (int) (dm.widthPixels * 0.66); // 宽度设置为屏幕的0.6
                    if(!dialogMatchParentHeight){
                        lp.height = (int) (dm.heightPixels * 0.6); // 宽度设置为屏幕的0.6
                    }
                    window.setAttributes(lp);
                }
            }
        }

    }

    /*@Override
    public int show(FragmentTransaction transaction, String tag) {
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(this, tag).addToBackStack(null);
        return transaction.commitAllowingStateLoss();
    }*/

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public void setDialogMatchParent(boolean dialogMatchParent) {
        this.dialogMatchParent = dialogMatchParent;
    }

    public void setDialogMatchParentHeight(boolean dialogMatchParent) {
        this.dialogMatchParentHeight = dialogMatchParent;
    }

    public interface OnCallDialog {
        Dialog getDialog(Context context);
    }
}
