package com.nq.edusaas.hps.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.SpanUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.eningqu.aipen.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

/**
 * author : Sivan
 * e-mail : hsy@eningqu.com
 * date   : 2020/5/14
 * desc   :
 * version: 1.0
 */
public class PenUIHelper {

    private PenUIHelper() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void showToastShort(@NonNull Context context, final String message) {
        resetToast();
        ToastUtils.setBgResource(R.drawable.shape_toast_bg);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        ToastUtils.showShort(new SpanUtils()
                .appendSpace(20)
                .append(message)
                .setFontSize(16, true)
                .setForegroundColor(context.getResources().getColor(R.color.app_bg_white))
                .appendSpace(20)
                .create());
    }

    public static void showToastShort(@NonNull Context context, @StringRes final int message) {
        resetToast();
        ToastUtils.setBgResource(R.drawable.shape_toast_bg);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        ToastUtils.showShort(new SpanUtils()
                .appendSpace(20)
                .append(context.getResources().getString(message))
                .setFontSize(16, true)
                .setForegroundColor(context.getResources().getColor(R.color.app_bg_white))
                .appendSpace(20)
                .create());
    }

    public static void showToastLong(@NonNull Context context, final String message) {
        resetToast();
        ToastUtils.setBgResource(R.drawable.shape_toast_bg);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        ToastUtils.showShort(new SpanUtils()
                .appendSpace(20)
                .append(message)
                .setFontSize(16, true)
                .setForegroundColor(context.getResources().getColor(R.color.app_bg_white))
                .appendSpace(20)
                .create());
    }

    public static void showToastLong(@NonNull Context context, @StringRes final int message) {
        resetToast();
        ToastUtils.setBgResource(R.drawable.shape_toast_bg);
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        ToastUtils.showShort(new SpanUtils()
                .appendSpace(20)
                .append(context.getResources().getString(message))
                .setFontSize(16, true)
                .setForegroundColor(context.getResources().getColor(R.color.app_bg_white))
                .appendSpace(20)
                .create());
    }

    private static void resetToast() {
        ToastUtils.setMsgColor(-0x1000001);
        ToastUtils.setBgColor(-0x1000001);
        ToastUtils.setBgResource(-1);
        ToastUtils.setGravity(-1, -1, -1);
    }


    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    /**
     * 显示软键盘
     */
    public static void openKeyboard(View view) {
        if (view == null) return;
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 显示软键盘
     */
    public static void openKeyboard(Context context) {
        if (context==null)return;
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void closeKeyboard(AppCompatActivity activity) {
        if (activity == null) {
            return;
        }
        View view = activity.getWindow().getDecorView().getRootView();
        try {
            InputMethodManager imm = (InputMethodManager) view.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void closeKeyboard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) view.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isOpenKeyboard(Context context) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                return imm.isActive();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将Toolbar高度填充到状态栏
     */
    public static void initFullBar(Toolbar toolbar, AppCompatActivity activity) {
        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
        params.height = PenToolUtil.getStatusHeight(activity) + getSystemActionBarSize(activity);
        toolbar.setLayoutParams(params);
        toolbar.setPadding(
                toolbar.getLeft(),
                toolbar.getTop() + PenToolUtil.getStatusHeight(activity),
                toolbar.getRight(),
                toolbar.getBottom()
        );
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    public static void initImageView(ImageView toolbar, AppCompatActivity activity) {
        CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
        params.topMargin = PenToolUtil.getStatusHeight(activity);
        toolbar.setLayoutParams(params);
    }

    public static void initFullBar(Toolbar toolbar, ConstraintLayout child, AppCompatActivity activity, boolean expanded) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        Toolbar.LayoutParams childParams = (Toolbar.LayoutParams) child.getLayoutParams();
        if (expanded) {
            params.height = getSystemActionBarSize(activity);
            toolbar.setLayoutParams(params);
            childParams.setMargins(0, 0, 0, 0);
        } else {
            params.height = PenToolUtil.getStatusHeight(activity) + getSystemActionBarSize(activity);
            toolbar.setLayoutParams(params);
            childParams.setMargins(0, PenToolUtil.getStatusHeight(activity), 0, 0);
        }
    }

    public static void initFullBar(ConstraintLayout toolbar, AppCompatActivity activity) {
        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
        params.height = PenToolUtil.getStatusHeight(activity) + params.height/*getSystemActionBarSize(activity)*/;
        toolbar.setLayoutParams(params);
        toolbar.setPadding(
                toolbar.getLeft(),
                toolbar.getTop() + PenToolUtil.getStatusHeight(activity),
                toolbar.getRight(),
                toolbar.getBottom()
        );
    }

    private static int getSystemActionBarSize(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        } else {
            return PenToolUtil.dip2px(context, 48);
        }
    }
}
