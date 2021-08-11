package com.eningqu.aipen.db.model;

import com.eningqu.aipen.db.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/10 14:42
 */
@Table(database = AppDataBase.class)
public class BluetoothData extends BaseModel {

    @PrimaryKey(autoincrement = true)
    public Long id;
    //蓝牙名称
    @Column
    public String bleName;
    //蓝牙地址
    @Column
    public String bleMac;
    //所属用户
    @Column
    public String userUid;

}
