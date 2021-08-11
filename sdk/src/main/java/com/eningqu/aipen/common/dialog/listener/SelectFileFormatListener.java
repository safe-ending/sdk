package com.eningqu.aipen.common.dialog.listener;

import android.view.View;

/**
 * @Author: Qiu.Li
 * @Create Date: 2019/5/8 21:09
 * @Description: 选择图片格式
 * @Email: liqiupost@163.com
 */

public interface SelectFileFormatListener {

    /**
     *
     * @param view
     * @param format 0 jpg,1 pdf
     */
    void onClick(View view, int format);
    void onCancel();
}
