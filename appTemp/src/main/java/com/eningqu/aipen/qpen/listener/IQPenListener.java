package com.eningqu.aipen.qpen.listener;

import com.eningqu.aipen.sdk.bean.NQDot;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/5/29 21:34
 * desc   :
 * version: 1.0
 */
public interface IQPenListener {
    void pageChange(NQDot dot);
    void handleDot(NQDot dot);
    void onError(NQDot dot, int error);
}
