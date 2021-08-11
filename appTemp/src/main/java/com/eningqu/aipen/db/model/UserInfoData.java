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
 * 时间：2018/3/10 11:37
 */
@Table(database = AppDataBase.class)
public class UserInfoData extends BaseModel {

    @PrimaryKey(autoincrement = true)
    public Long id;
    //用户唯一标识UID
    @Column
    public String userUid;
    //用户昵称
    @Column
    public String userName;
    //用户头像
    @Column
    public String userIcon;
    //用户性别
    @Column
    public String userSex;
    @Override
    public String toString() {
        return "UserInfoData{" +
                "id=" + id +
                ", userUid='" + userUid + '\'' +
                ", userName='" + userName + '\'' +
                ", userIcon='" + userIcon + '\'' +
                ", userSex='" + userSex + '\'' +
                '}';
    }
}
