package com.eningqu.aipen.db.model;

import com.eningqu.aipen.db.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
* @Description: 彩云业务信息头
*/
@Table(database = AppDataBase.class)
public class OseUserInfoData extends BaseModel {

    @PrimaryKey(autoincrement = true)
    public Long id;
    @Column
    public String APP_NUMBER;
    @Column
    public String ERRORCODE;
    @Column
    public String isSwitch;
    @Column
    public String NOTE_TOKEN;

    @Column
    public String APP_AUTH;

    public String getAPP_NUMBER() {
        return APP_NUMBER;
    }

    public void setAPP_NUMBER(String APP_NUMBER) {
        this.APP_NUMBER = APP_NUMBER;
    }

    public String getERRORCODE() {
        return ERRORCODE;
    }

    public void setERRORCODE(String ERRORCODE) {
        this.ERRORCODE = ERRORCODE;
    }

    public String getIsSwitch() {
        return isSwitch;
    }

    public void setIsSwitch(String isSwitch) {
        this.isSwitch = isSwitch;
    }

    public String getNOTE_TOKEN() {
        return NOTE_TOKEN;
    }

    public void setNOTE_TOKEN(String NOTE_TOKEN) {
        this.NOTE_TOKEN = NOTE_TOKEN;
    }

    public String getAPP_AUTH() {
        return APP_AUTH;
    }

    public void setAPP_AUTH(String APP_AUTH) {
        this.APP_AUTH = APP_AUTH;
    }
}
