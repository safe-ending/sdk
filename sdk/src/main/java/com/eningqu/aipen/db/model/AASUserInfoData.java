package com.eningqu.aipen.db.model;

import com.eningqu.aipen.db.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
* @Author: Qiu.Li
* @Create Date: 2021/7/8 14:08
* @Description: 彩云用户登录信息
* @Email: liqiupost@163.com
*/
@Table(database = AppDataBase.class)
public class AASUserInfoData extends BaseModel {

    @PrimaryKey(autoincrement = true)
    public Long id;
    @Column
    public String error;
    @Column
    public String desc;
    @Column
    public String imspwd;
    @Column
    public String sbc;
    @Column
    public String domain;
    @Column
    public String svnlist;
    @Column
    public String svnuser;
    @Column
    public String svnpwd;
    @Column
    public String htslist;
    @Column
    public String userType;
    @Column
    public String userid;
    @Column
    public String loginid;
    @Column
    public String heartime;
    @Column
    public String funcId;
    @Column
    public String token;
    @Column
    public String expiretime;
    @Column
    public String authToken;
    @Column
    public String atExpiretime;
    @Column
    public String deviceid;
    @Column
    public String account;
    @Column
    public String expiryDate;
    @Column
    public String areaCode;
    @Column
    public String provCode;
    @Column
    public String srvInfoVer;
    @Column
    public String rifurl;
    @Column
    public String calURL;
    @Column
    public String testTermConnectURL;
    @Column
    public String passID;
    @Column
    public String isRegWeibo;
    @Column
    public String accessToken;

}
