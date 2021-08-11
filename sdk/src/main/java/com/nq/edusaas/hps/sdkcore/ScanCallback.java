package com.nq.edusaas.hps.sdkcore;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/2/25 17:33
 * desc   : 扫描回调
 * version: 1.0
 */
public interface ScanCallback {
    void onScanResult(final String name, final String mac);
}
