package com.eningqu.aipen.bean.caiyun;

import java.io.Serializable;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/11 13:18
 * desc   :
 * version: 1.0
 */
public class NoteSummaryReq implements Serializable {


    /**
     * pageIndex : 1 页码，默认为1
     * pageSize : 10 每页记录数，默认为20
     * sortType : 0 排序类型，0 是按照更新时间排序 不传时候默认为0
     * sort : 0 排序顺序，0 倒序 1 正序，不传时候默认为0
     * filterTime : 1545706378589 过滤时间，格式为时间戳（精确到毫秒），若传此字段，后端将会过滤掉比该时间戳更新的数据
     * recycleFlag : false 是否查询回收站的笔记，传true时候为是，不传或者传false时候为否
     */

    public Integer pageIndex;
    public Integer pageSize;
    public Integer sortType;
    public Integer sort;
    public String filterTime;
    public boolean recycleFlag;
}
