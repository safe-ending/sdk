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
public class PageAudioData extends BaseModel {

    @PrimaryKey(autoincrement = true)
    public Long id;
    //所属页码的ID  唯一标识
    //页面的唯一ID，唯一，主键
    @Column
    public String noteBookId;
    //所属笔记本ID
    @Column
    public String pageId;
    //音频资源url
    @Column
    public String audioUrl;
    //用户所属ID
    @Column
    public String userUid;
    //创建时间
    @Column
    public String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
