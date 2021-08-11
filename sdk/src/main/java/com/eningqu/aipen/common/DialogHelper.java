package com.eningqu.aipen.common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.eningqu.aipen.R;
import com.eningqu.aipen.common.dialog.BaseDialog;
import com.eningqu.aipen.common.dialog.listener.ConfirmListener;
import com.eningqu.aipen.common.dialog.listener.DeleteListener;
import com.eningqu.aipen.common.dialog.listener.HistoryOperateListener;
import com.eningqu.aipen.common.dialog.listener.LoginmCloudListener;
import com.eningqu.aipen.common.dialog.listener.NoteOpertingListener;
import com.eningqu.aipen.common.dialog.listener.SelectFileFormatListener;
import com.eningqu.aipen.common.dialog.listener.ShareListener;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/3 16:58
 */

public class DialogHelper {

    private final static String TAG = DialogHelper.class.getSimpleName();

    public static BaseDialog showMessage(final FragmentManager fragmentManager, final int msg) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
                final TextView content = view.findViewById(R.id.dialog_content);
                content.setText(msg);
                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);
                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(true);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.show(fragmentManager, TAG + ":message");


        return baseDialog;
    }

    public static BaseDialog showMessage(final FragmentManager fragmentManager, final String msg) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
                final TextView content = view.findViewById(R.id.dialog_content);
                content.setText(msg);
                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);
                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(true);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.show(fragmentManager, TAG + ":message");

        return baseDialog;
    }

    public static BaseDialog showMessage(final FragmentManager fragmentManager, final ConfirmListener listener, final int dialogTitle, final int msg) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
                final TextView title = view.findViewById(R.id.dialog_title);
                final TextView content = view.findViewById(R.id.dialog_content);
                final TextView positive = view.findViewById(R.id.dialog_positive);

                title.setText(dialogTitle);
                content.setText(msg);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(v);
                    }
                });

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(false);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(false);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(false);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.show(fragmentManager, TAG + ":message");
        return baseDialog;
    }

    public static BaseDialog showMessage(final FragmentManager fragmentManager, final ConfirmListener listener, final int msg) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
                final TextView content = view.findViewById(R.id.dialog_content);
                final TextView positive = view.findViewById(R.id.dialog_positive);

                content.setText(msg);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(v);
                    }
                });

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(false);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(false);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(false);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.show(fragmentManager, TAG + ":message");
        return baseDialog;
    }

    public static BaseDialog showMessage(final FragmentManager fragmentManager, final ConfirmListener listener, final String msg) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
                final TextView content = view.findViewById(R.id.dialog_content);
                final TextView positive = view.findViewById(R.id.dialog_positive);

                content.setText(msg);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(v);
                    }
                });

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(false);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(false);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(false);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.show(fragmentManager, TAG + ":message");
        return baseDialog;
    }

    public static BaseDialog showErrorMessage(final FragmentManager fragmentManager, final ConfirmListener listener, final String msg) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
                final TextView content = view.findViewById(R.id.dialog_content);
                final TextView positive = view.findViewById(R.id.dialog_positive);

                content.setText(msg);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(v);
                    }
                });

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(true);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.show(fragmentManager, TAG + ":message");
        return baseDialog;
    }

    public static BaseDialog showProgress(FragmentManager fragmentManager, final int msg, final boolean cancelable) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
                final TextView content = view.findViewById(R.id.dialog_content);
                content.setText(msg);

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(cancelable);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(false);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(cancelable);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.show(fragmentManager, TAG + ":progress");
        return baseDialog;
    }

    public static BaseDialog showProgress(FragmentManager fragmentManager, final String msg, final boolean cancelable) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
                final TextView content = view.findViewById(R.id.dialog_content);
                content.setText(msg);

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(cancelable);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(false);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(cancelable);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.show(fragmentManager, TAG + ":progress");
        return baseDialog;
    }

    public static void setProgressText(BaseDialog baseDialog, String msg) {
        if (null != baseDialog) {
            if (null != baseDialog.getDialog()) {
                final TextView content = baseDialog.getDialog().findViewById(R.id.dialog_content);
                if (null != content) {
                    content.setText(msg);
                }
            }
        }
    }

    public static BaseDialog showConfirm(final FragmentManager fragmentManager, final ConfirmListener listener,
                                         final int dialogTitle, final int dialogContent, final int positiveTitle, final int negativeTitle) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null);

                TextView title = view.findViewById(R.id.dialog_title);
                TextView negative = view.findViewById(R.id.dialog_negative);
                TextView positive = view.findViewById(R.id.dialog_positive);
                title.setText(dialogTitle);
                positive.setText(positiveTitle);
                negative.setText(negativeTitle);

                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(v);
                    }
                });

                TextView content = view.findViewById(R.id.dialog_content);
                content.setText(dialogContent);


                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.setCancelable(true);
        baseDialog.show(fragmentManager, TAG + ":confirm");
        return baseDialog;
    }

    public static BaseDialog showConfirm(FragmentManager fragmentManager, final ConfirmListener listener,
                                         final int dialogTitle, final String dialogContent, final int positiveTitle,
                                         final int negativeTitle, final boolean canceledOnTouchOutside) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null);

                TextView title = view.findViewById(R.id.dialog_title);
                TextView negative = view.findViewById(R.id.dialog_negative);
                TextView positive = view.findViewById(R.id.dialog_positive);
                title.setText(dialogTitle);
                positive.setText(positiveTitle);
                negative.setText(negativeTitle);

                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(v);
                    }
                });

                TextView content = view.findViewById(R.id.dialog_content);
                content.setText(dialogContent);


                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(true);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.show(fragmentManager, TAG + ":confirm");
        return baseDialog;
    }

    public static BaseDialog showConfirm(FragmentManager fragmentManager, final ConfirmListener listener,
                                         final String dialogTitle, final String dialogContent, final String positiveTitle,
                                         final String negativeTitle, final boolean canceledOnTouchOutside) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null);

                TextView title = view.findViewById(R.id.dialog_title);
                TextView negative = view.findViewById(R.id.dialog_negative);
                TextView positive = view.findViewById(R.id.dialog_positive);
                title.setText(dialogTitle);
                positive.setText(positiveTitle);
                negative.setText(negativeTitle);

                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(v);
                    }
                });

                TextView content = view.findViewById(R.id.dialog_content);
                content.setText(dialogContent);


                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(true);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.show(fragmentManager, TAG + ":confirm");
        return baseDialog;
    }

    public static BaseDialog showConfirm(FragmentManager fragmentManager, final ConfirmListener listener,
                                         final int dialogTitle, final SpannableStringBuilder dialogContent, final int positiveTitle,
                                         final int negativeTitle, final boolean canceledOnTouchOutside) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_agree, null);

                TextView title = view.findViewById(R.id.dialog_title);
                TextView negative = view.findViewById(R.id.dialog_negative);
                TextView positive = view.findViewById(R.id.dialog_positive);
                title.setText(dialogTitle);
                positive.setText(positiveTitle);
                negative.setText(negativeTitle);

                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(v);
                    }
                });

                TextView content = view.findViewById(R.id.dialog_content);
                content.setText(dialogContent);
                content.setMovementMethod(LinkMovementMethod.getInstance());


                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(false);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(false);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.show(fragmentManager, TAG + ":confirm");
        return baseDialog;
    }

    public static BaseDialog showConfirm(FragmentManager fragmentManager, final ConfirmListener listener, final int msg, final int positiveStr) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null);

                TextView negative = view.findViewById(R.id.dialog_negative);
                TextView positive = view.findViewById(R.id.dialog_positive);
                if (positiveStr > 0) {
                    positive.setText(positiveStr);
                }

                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(v);
                    }
                });

                TextView content = view.findViewById(R.id.dialog_content);
                content.setText(msg);


                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(false);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(false);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(false);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.show(fragmentManager, TAG + ":confirm");
        return baseDialog;
    }

    public static BaseDialog showConfirm(FragmentManager fragmentManager, final ConfirmListener listener, final String msg) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null);

                TextView negative = view.findViewById(R.id.dialog_negative);
                TextView positive = view.findViewById(R.id.dialog_positive);

                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(v);
                    }
                });

                TextView content = view.findViewById(R.id.dialog_content);
                content.setText(msg);

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(true);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.show(fragmentManager, TAG + ":confirm");
        return baseDialog;
    }

    public static BaseDialog showLabel(FragmentManager fragmentManager, final ConfirmListener listener) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_label, null);

                final Button negative = view.findViewById(R.id.dialog_negative);
                final Button positive = view.findViewById(R.id.dialog_positive);
                final EditText content = view.findViewById(R.id.dialog_label);

                content.setFocusable(true);
                content.setFocusableInTouchMode(true);
                content.requestFocus();

                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(content);
                    }
                });


                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(true);
        baseDialog.show(fragmentManager, TAG + ":label");
        return baseDialog;
    }


    public static BaseDialog showNoteRename(FragmentManager fragmentManager, final ConfirmListener listener) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_note_rename, null);

                final Button negative = view.findViewById(R.id.dialog_negative);
                final Button positive = view.findViewById(R.id.dialog_positive);
                final EditText content = view.findViewById(R.id.dialog_label);

                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(content);
                    }
                });

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(true);
        baseDialog.show(fragmentManager, TAG + ":rename");
        return baseDialog;
    }

    public static BaseDialog showHistory(FragmentManager fragmentManager, final HistoryOperateListener listener) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_ble_msg, null);

                final Button delete = view.findViewById(R.id.dialog_negative);
                final Button receive = view.findViewById(R.id.dialog_positive);
                final Button ignore = view.findViewById(R.id.dialog_ignore);


                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.delete();
                    }
                });
                receive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.receive();
                    }
                });
                ignore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.ignore();
                    }
                });


                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(false);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(false);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(false);
        baseDialog.show(fragmentManager, TAG + ":history");
        return baseDialog;
    }

    public static BaseDialog showOperating(FragmentManager fragmentManager, final NoteOpertingListener listener) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_note_book, null);

                TextView renameTV = view.findViewById(R.id.tv_note_rename);
                TextView collectTV = view.findViewById(R.id.tv_note_collect);
                TextView deleteTV = view.findViewById(R.id.tv_note_delete);
                TextView cancelTV = view.findViewById(R.id.tv_cancel);

                renameTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.rename();
                    }
                });
                collectTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.collect();
                    }
                });
                deleteTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.delete();
                    }
                });
                cancelTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.BottomDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
                dialog.getWindow().setAttributes(params);
                return dialog;
            }
        }, .4f, R.style.dialog_bottom_animation);
        baseDialog.show(fragmentManager, TAG + ":operating");
        return baseDialog;
    }

    public static BaseDialog showShare(final int format, FragmentManager fragmentManager, final ShareListener listener) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_share, null);

                LinearLayout wechat = view.findViewById(R.id.wechat);
                LinearLayout wechatmoments = view.findViewById(R.id.wechatmoments);
                LinearLayout qq = view.findViewById(R.id.qq);
                LinearLayout qzone = view.findViewById(R.id.qzone);
                LinearLayout sinaweibo = view.findViewById(R.id.sinaweibo);
                LinearLayout facebook = view.findViewById(R.id.facebook);
                LinearLayout twitter = view.findViewById(R.id.twitter);
                //***！这里布局的资源用的sharesdk的 适配ui用了不同的布局
                RelativeLayout whatapp = view.findViewById(R.id.whatapp);
                RelativeLayout email = view.findViewById(R.id.email);

                LinearLayout ll2 = view.findViewById(R.id.ll2);
                LinearLayout ll3 = view.findViewById(R.id.ll3);

//                String locale = Locale.getDefault().getLanguage();

//                Locale locale = AppInfoUtil.getSystemDefaultLocale(context);

//                if (locale.getCountry().contains("zh")||locale.getCountry().contains("CN")) {
                if (format == 0) {
                    ll2.setVisibility(View.VISIBLE);
                    ll3.setVisibility(View.VISIBLE);
                    wechat.setVisibility(View.VISIBLE);
                    wechatmoments.setVisibility(View.VISIBLE);
                    qq.setVisibility(View.VISIBLE);
                    qzone.setVisibility(View.VISIBLE);
                    sinaweibo.setVisibility(View.VISIBLE);
                    facebook.setVisibility(View.VISIBLE);
                    twitter.setVisibility(View.VISIBLE);
                    whatapp.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                } else {
                    ll2.setVisibility(View.GONE);
                    ll3.setVisibility(View.GONE);

                    wechat.setVisibility(View.VISIBLE);
                    qq.setVisibility(View.VISIBLE);

                    wechatmoments.setVisibility(View.INVISIBLE);
                    qzone.setVisibility(View.INVISIBLE);
                    sinaweibo.setVisibility(View.INVISIBLE);
                    facebook.setVisibility(View.INVISIBLE);
                    twitter.setVisibility(View.INVISIBLE);
                    whatapp.setVisibility(View.INVISIBLE);
                    email.setVisibility(View.INVISIBLE);
                }


                wechat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v);
                    }
                });
                wechatmoments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v);
                    }
                });
                qq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v);
                    }
                });
                qzone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v);
                    }
                });
                sinaweibo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v);
                    }
                });
                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v);
                    }
                });

                sinaweibo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v);
                    }
                });
                twitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v);
                    }
                });
                email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v);
                    }
                });
                whatapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v);
                    }
                });
                /*}else {
                    ll_zn.setVisibility(View.GONE);
                    sinaweibo.setVisibility(View.GONE);
                    facebook.setVisibility(View.VISIBLE);
                    twitter.setVisibility(View.VISIBLE);
                    ll_fa.setVisibility(View.GONE);

                    facebook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onClick(v);
                        }
                    });
                    twitter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onClick(v);
                        }
                    });
                }*/
                TextView cancelTV = view.findViewById(R.id.tv_cancel);
                cancelTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onCancel();
                    }
                });

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.BottomDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
                dialog.getWindow().setAttributes(params);
                return dialog;
            }
        }, .4f, R.style.dialog_bottom_animation);
        baseDialog.show(fragmentManager, TAG + ":share");
        return baseDialog;
    }
    public static BaseDialog showSelectFileFormatDialogBottom(FragmentManager fragmentManager, final SelectFileFormatListener listener) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_share_format, null);
                view.setMinimumWidth(ScreenUtils.getScreenWidth());
                LinearLayout llJpg = view.findViewById(R.id.ll_jpg);
                LinearLayout llPdf = view.findViewById(R.id.ll_pdf);
                llJpg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v, 0);
                    }
                });
                llPdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v, 1);
                    }
                });

                TextView cancelTV = view.findViewById(R.id.tv_cancel);
                cancelTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onCancel();
                    }
                });

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.MyDialog)
                        .setView(view)
                        .create();
                dialog.show();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, 0.4f, -1);
        baseDialog.show(fragmentManager, TAG + ":share_format");
        return baseDialog;
    }

    public static BaseDialog showSelectFileFormatDialog(FragmentManager fragmentManager, final SelectFileFormatListener listener) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_share_format, null);

                LinearLayout llJpg = view.findViewById(R.id.ll_jpg);
                LinearLayout llPdf = view.findViewById(R.id.ll_pdf);


                llJpg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v, 0);
                    }
                });
                llPdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v, 1);
                    }
                });

                TextView cancelTV = view.findViewById(R.id.tv_cancel);
                cancelTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onCancel();
                    }
                });

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.BottomDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
                dialog.getWindow().setAttributes(params);
                return dialog;
            }
        }, .4f, R.style.dialog_bottom_animation);
        baseDialog.show(fragmentManager, TAG + ":share_format");
        return baseDialog;
    }

    public static BaseDialog showDelete(FragmentManager fragmentManager, final DeleteListener listener) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_delete, null);

                TextView renameTV = view.findViewById(R.id.tv_note_rename);
                TextView deleteTV = view.findViewById(R.id.tv_note_delete);
                TextView cancelTV = view.findViewById(R.id.tv_cancel);

                renameTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.rename();
                    }
                });
                deleteTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.delete();
                    }
                });
                cancelTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.BottomDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
                dialog.getWindow().setAttributes(params);
                return dialog;
            }
        }, .4f, R.style.dialog_bottom_animation);
        baseDialog.show(fragmentManager, TAG + ":delete");
        return baseDialog;
    }

    public static BaseDialog showUpdateVersion(FragmentManager fragmentManager, final ConfirmListener listener, final String tips) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_update_version, null);

                final Button negative = view.findViewById(R.id.dialog_negative);
                final Button positive = view.findViewById(R.id.dialog_positive);
                final TextView content = view.findViewById(R.id.dialog_content);

                content.setText(tips);

                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(v);
                    }
                });

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(true);
        baseDialog.show(fragmentManager, TAG + ":updateVersion");
        return baseDialog;
    }

    public static BaseDialog showGpsTips(FragmentManager fragmentManager, final ConfirmListener listener) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_location_tips, null);

                final Button negative = view.findViewById(R.id.dialog_negative);
                final Button positive = view.findViewById(R.id.dialog_positive);

                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(v);
                    }
                });

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(true);
        baseDialog.show(fragmentManager, TAG + ":location");
        return baseDialog;
    }

    public static BaseDialog showLoginMCloud(FragmentManager fragmentManager, final LoginmCloudListener listener) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_login_mcloud, null);

                final Button btnVeriCode = view.findViewById(R.id.tv_veri_code);
                final Button btnLogin = view.findViewById(R.id.tv_login);

                btnVeriCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = (EditText) view.findViewById(R.id.et_mobile);
                        String mobile = editText.getText().toString();
                        listener.getVeriCode(mobile, v);
                    }
                });
                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = (EditText) view.findViewById(R.id.et_mobile);
                        String mobile = editText.getText().toString();

                        EditText etVeriCode = (EditText) view.findViewById(R.id.et_veri_code);
                        String veriCode = etVeriCode.getText().toString().trim();
                        listener.login(mobile, veriCode, v);
                    }
                });

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        listener.OnDismissListener();
                    }
                });
                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(true);
        baseDialog.show(fragmentManager, TAG + ":login");
        return baseDialog;
    }

    public static BaseDialog showBle(FragmentManager fragmentManager, final String msg) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
                final TextView content = view.findViewById(R.id.dialog_content);
                content.setText(msg);

                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(false);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(false);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(true);
        baseDialog.show(fragmentManager, TAG + ":progress");
        return baseDialog;
    }

    public static BaseDialog showListViewDialog(@Nullable FragmentManager fragmentManager, final int title,
                                                @Nullable final RecyclerView.Adapter adapter, final int position) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
//                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null);
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_language, null);
                final TextView titleView = view.findViewById(R.id.tv_title);
                titleView.setText(title);
                titleView.setTextSize(18);
                RecyclerView recyclerView = view.findViewById(R.id.rcv_select_language);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(position);
                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(true);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(true);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

//                Window window = getWindow();
                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//                WindowManager.LayoutParams attributes = window.getAttributes();
                /*if (dialog.getWindow().getDecorView().getHeight() >= (int) (displayMetrics.heightPixels * 0.7)) {
                }
                if (dialog.getWindow().getDecorView().getWidth() >= (int) (displayMetrics.widthPixels * 0.5)) {
                    params.width = (int) (displayMetrics.widthPixels * 0.5);
                }*/
                params.height = 200;//(int) (displayMetrics.heightPixels * 0.4);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(true);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(false);
        baseDialog.show(fragmentManager, TAG + ":listView");
        return baseDialog;
    }

    public static BaseDialog showDelete(FragmentManager fragmentManager, final ConfirmListener listener, final int msg, final int title) {
        final BaseDialog baseDialog = BaseDialog.newInstance(new BaseDialog.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                //加载自定义布局文件
                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_delete2, null);

                TextView negative = view.findViewById(R.id.dialog_negative);
                TextView positive = view.findViewById(R.id.dialog_positive);
                TextView titleView = view.findViewById(R.id.dialog_title);
                if (title > 0) {
                    titleView.setText(title);
                }

                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                });
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.confirm(v);
                    }
                });

                TextView content = view.findViewById(R.id.dialog_content);
                content.setText(msg);


                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.CenterDialogTheme)
                        .setView(view)
                        .create();
                /**是否可以按“返回键”消失*/
                dialog.setCancelable(false);
                /**点击加载框以外的区域*/
                dialog.setCanceledOnTouchOutside(false);

                //设置无标题
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setAttributes(params);

                return dialog;
            }
        }, .4f, -1);
        baseDialog.setCancelable(false);
        baseDialog.setDialogMatchParent(false);
        baseDialog.setDialogMatchParentHeight(true);
        baseDialog.show(fragmentManager, TAG + ":delete");
        return baseDialog;
    }
}
