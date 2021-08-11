package com.eningqu.aipen.qpen;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/4/24 14:46
 * desc   : 同步离线数据回调
 * version: 1.0
 */
public interface IPenOfflineDataSyncListener {
    void onSyncBegin();
    void onSyncProgress(int progress);
    void onSyncEnd();
}
