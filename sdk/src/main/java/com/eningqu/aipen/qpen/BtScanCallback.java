package com.eningqu.aipen.qpen;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/2/25 17:33
 * desc   :
 * version: 1.0
 */
public interface BtScanCallback {
    void onScanResult(final String name, final String mac);
}
