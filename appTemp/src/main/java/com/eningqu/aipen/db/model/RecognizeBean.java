package com.eningqu.aipen.db.model;

import com.eningqu.aipen.db.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Objects;

@Table(database = AppDataBase.class)
public class RecognizeBean extends BaseModel {
    //标签ID，唯一，主键
    @PrimaryKey(autoincrement = true)
    public Long recognizeId;
    //页面的唯一ID，唯一，主键
    @Column
    public String noteBookId;
    //所属笔记本ID
    @Column
    public int pageId;
    //用户所属ID
    @Column
    public String userUid;
    //识别结果
    @Column
    public String recognizeResult;
    //识别时间
    @Column
    public long timestamp;
    //识别区域
    @Column
    public double x;
    @Column
    public double y;
    @Column
    public double w;
    @Column
    public double h;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecognizeBean that = (RecognizeBean) o;
        return Objects.equals(recognizeId, that.recognizeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recognizeId);
    }

}