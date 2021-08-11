package com.eningqu.aipen.bean.caiyun;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/6 17:32
 * desc   :
 * version: 1.0
 */
public class GetVeriCodeRsp {

    String error;
    String mode;
    String desc;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
