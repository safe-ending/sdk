package com.eningqu.aipen.db.model;

import com.eningqu.aipen.db.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
* @Author: Qiu.Li
* @Create Date: 2019/4/12 21:05
* @Description:
* @Email: liqiupost@163.com
*/
@Table(database = AppDataBase.class)
public class PageLabelData extends BaseModel {

    //标签ID，唯一，主键
    @PrimaryKey
    public String labelId;
    //页面的唯一ID，唯一，主键
    @Column
    public String noteBookId;
    //所属笔记本ID
    @Column
    public String pageId;
    //标签名称
    @Column
    public String labelName;
    //用户所属ID
    @Column
    public String userUid;

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public String getNoteBookId() {
        return noteBookId;
    }

    public void setNoteBookId(String noteBookId) {
        this.noteBookId = noteBookId;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
