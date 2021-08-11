package com.eningqu.aipen.common.exception;


import com.eningqu.aipen.R;

/**
 * 蓝牙授权服务器接口，返回code 定义
 */
public enum BleStatusCode {
    SUCCESS(0, R.string.server_status_tips0, "设备可用"),
    UNAVAILABLE(1, R.string.server_status_tips1, "设备不可用"),
    PARAM_ERROR(2, R.string.server_status_tips2, "参数错误"),
    BINDING(3, R.string.server_status_tips3, "设备已绑定"),
    NOT_EXIST(4, R.string.server_status_tips4, "设备不存在"),
    NETWORK_ERROR(5, R.string.network_error_tip, "网络错误");

    private int code;
    private int tipsResID;
    private String msg;

    BleStatusCode(int code, int tipsResID, String msg) {
        this.code = code;
        this.tipsResID = tipsResID;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public int getTipsResID() {
        return tipsResID;
    }

    public String getMsg() {
        return msg;
    }
}
