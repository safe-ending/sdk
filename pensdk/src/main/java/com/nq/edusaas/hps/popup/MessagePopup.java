package com.nq.edusaas.hps.popup;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.nq.edusaas.pensdk.R;

import androidx.annotation.NonNull;
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
public class MessagePopup extends BasePopupWindow {

    public MessagePopup(Context context, String content, boolean touchDismiss) {
        super(context);
        bindEvent(content);
//        setOverlayStatusbar(true);
        setPopupWindowFullScreen(true);
        setBlurBackgroundEnable(false);
        setOutSideTouchable(touchDismiss);
        setOutSideDismiss(touchDismiss);
        setBackgroundColor(Color.parseColor("#4c000000"));
        setBackPressEnable(touchDismiss);
    }

    @Override
    public void onViewCreated(@NonNull View contentView) {
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.dialog_message);
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

    private void bindEvent(String content) {
        TextView introduction = findViewById(R.id.dialog_content);
        introduction.setText(content);
        findViewById(R.id.dialog_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onItemClick();
            }
        });
    }

    private OnClick click;
    public interface OnClick {
        void onItemClick();
    }

    public void setClick(OnClick click) {
        this.click = click;
    }
}
