package com.eningqu.aipen.db.model;

import com.eningqu.aipen.db.AppDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
* @Author: Qiu.Li
* @Create Date: 2019/4/12 21:12
* @Description:
* @Email: liqiupost@163.com
*/
@Table(database = AppDataBase.class)
public class NoteBookData extends BaseModel {

    @PrimaryKey(autoincrement = true)
    public Long id;
    //笔记本的唯一ID。主键
    @Column
    public String notebookId;
    //笔记本类型
    //规格A3=4 A4=2 A5=1 A6=3
    @Column
    public int noteType;
    //笔记本名称  可修改
    @Column
    public String noteName;
    //笔记本是否被收藏  默认未收藏
    @Column
    public boolean isLock = false;
    //笔记本创建时间
    @Column
    public String createTime;
    //笔记本上次打开时间
    @Column
    public String lastOpenTime;
    //笔记本上锁时间
    @Column
    public String lockedTime;
    //所属用户
    @Column
    public String userUid;
    //同步状态
    @Column
    public int syncState;
    //排序索引
    @Column
    public int sortIndex;
    //封面
    @Column
    public String noteCover;
    //笔记本的X坐标最大值
    @Column
    public int xMax;
    //笔记本的Y坐标最大值
    @Column
    public int yMax;

    List<PageLabelData> labels;
    @OneToMany(methods = {OneToMany.Method.LOAD}, variableName = "labels")
    public List<PageLabelData> getLabels(){
        if (labels == null || labels.isEmpty()) {
            labels = SQLite.select()
                    .from(PageLabelData.class)
                    .where(PageLabelData_Table.noteBookId.eq(notebookId),
                            PageLabelData_Table.userUid.eq(userUid))
                    .queryList();
        }
        return labels;
    }
}
