package com.eningqu.aipen.db.model;

import com.eningqu.aipen.db.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = AppDataBase.class)
public class RecognizeData extends BaseModel {


    //标签ID，唯一，主键
    @PrimaryKey
    public Long id;
    //页面的唯一ID，唯一，主键
    @Column
    public String noteBookId;
    //所属笔记本ID
    @Column
    public int pageId;
    //用户所属ID
    @Column
    public String userUid;
    //识别的拼接结果 为gson转换的字符串数组
    @Column
    public String resultList;

    public String getNoteBookId() {
        return noteBookId;
    }

    public void setNoteBookId(String noteBookId) {
        this.noteBookId = noteBookId;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getResultList() {
        return resultList;
    }

    public void setResultList(String resultList) {
        this.resultList = resultList;
    }
}
