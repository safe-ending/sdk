package com.eningqu.aipen.qpen.bean;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2019/4/24 15:11
 * desc   : sdk返回的离线信息
 * version: 1.0
 */
public class OfflineDataInfo {

    /**
     * readCnt : 2148
     * readedCnt : 5348
     * totalCnt : 5348
     */

    private int readCnt;
    private int readedCnt;
    private int totalCnt;

    public int getReadCnt() {
        return readCnt;
    }

    public void setReadCnt(int readCnt) {
        this.readCnt = readCnt;
    }

    public int getReadedCnt() {
        return readedCnt;
    }

    public void setReadedCnt(int readedCnt) {
        this.readedCnt = readedCnt;
    }

    public int getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(int totalCnt) {
        this.totalCnt = totalCnt;
    }
}
