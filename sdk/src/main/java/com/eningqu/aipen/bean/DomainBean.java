package com.eningqu.aipen.bean;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/7/6 14:37
 * desc   :
 * version: 1.0
 */
public class DomainBean {

    /**
     * domain : speech1.dstsoft.net
     * type : 2
     * version : 3
     */

    private String domain;
    private int type;
    private int version;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
