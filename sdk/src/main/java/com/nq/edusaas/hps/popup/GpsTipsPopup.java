package com.nq.edusaas.hps.popup;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;


import androidx.annotation.NonNull;

import com.eningqu.aipen.R;

import razerdp.basepopup.BasePopupWindow;
import razerdp.util.animation.AnimationHelper;
import razerdp.util.animation.ScaleConfig;

/**
 * author : Sivan
 * e-mail : hsy@eningqu.com
 * date   : 2020/5/12
 * desc   :
 * version: 1.0
 */
public class GpsTipsPopup extends BasePopupWindow {

    public GpsTipsPopup(Context context) {
        super(context);
        bindEvent();
//        setOverlayStatusbar(true);
        setPopupWindowFullScreen(true);
        setBlurBackgroundEnable(false);
        setOutSideTouchable(true);
        setOutSideDismiss(true);
        setBackgroundColor(Color.parseColor("#4c000000"));
        setBackPressEnable(true);
    }

    @Override
    public void onViewCreated(@NonNull View contentView) {
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.dialog_location_tips);
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

    private void bindEvent() {
        findViewById(R.id.dialog_negative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.cancel();
            }
        });
        findViewById(R.id.dialog_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.confirm(v);
            }
        });
    }

    private OnClick click;
    public interface OnClick {
        void confirm(View view);
        void cancel();
    }

    public void setClick(OnClick click) {
        this.click = click;
    }
}
