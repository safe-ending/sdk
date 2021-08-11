package com.eningqu.aipen.bean;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/7/6 11:52
 * desc   :
 * version: 1.0
 */
public class TransResultBean extends ApiBaseBean{

    /**
     * msg : 翻译成功
     * code : 20000000
     * data : Have a good time.

     * success : true
     */
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
