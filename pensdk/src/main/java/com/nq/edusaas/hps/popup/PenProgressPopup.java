package com.nq.edusaas.hps.popup;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.nq.edusaas.pensdk.R;
import com.nq.edusaas.pensdk.databinding.PopupLoadingBinding;

import razerdp.basepopup.BasePopupWindow;
import razerdp.util.animation.AnimationHelper;
import razerdp.util.animation.ScaleConfig;

/**
 * author : Sivan
 * e-mail : hsy@eningqu.com
 * date   : 2020/7/23
 * desc   :
 * version: 1.0
 */
public class PenProgressPopup extends BasePopupWindow {

    private AnimationDrawable mAnimationDrawable;
    private PopupLoadingBinding bind;

    public PenProgressPopup(AppCompatActivity context, String content) {
        super(context);

        setBackgroundColor(Color.parseColor("#00000000"));
        setPopupGravity(Gravity.CENTER);

        setOutSideTouchable(false);
        setOutSideDismiss(false);

        bindEvent(content);
    }

    @Override
    public void onViewCreated(@NonNull View contentView) {
        bind = DataBindingUtil.bind(contentView);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_loading);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return AnimationHelper.asAnimation()
                .withScale(ScaleConfig.CENTER)
                .toShow();
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return AnimationHelper.asAnimation()
                .withScale(ScaleConfig.CENTER)
                .toDismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        // 停止动画
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
    }

    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
        // 开启动画
        if (mAnimationDrawable != null) {
            if (!mAnimationDrawable.isRunning()) {
                mAnimationDrawable.start();
            }
        } else {
            mAnimationDrawable = (AnimationDrawable) bind.imgProgressLoading.getDrawable();
            if (!mAnimationDrawable.isRunning()) {
                mAnimationDrawable.start();
            }
        }
    }

    private void bindEvent(String content) {
        bind.tvTipLoading.setText(content);
        // 加载动画
        mAnimationDrawable = (AnimationDrawable)  bind.imgProgressLoading.getDrawable();
        // 默认进入页面就开启动画
        if (!mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
    }
}
