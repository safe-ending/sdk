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
* @Create Date: 2019/4/12 21:07
* @Description:
* @Email: liqiupost@163.com
*/
@Table(database = AppDataBase.class)
public class PageData extends BaseModel {

    @PrimaryKey(autoincrement = true)
    public Long id;
    @Column
    public String noteBookId;
    //所属笔记本ID
    @Column
    public String pageId;
    //笔记本类型
    @Column
    public int noteType;
    //页码数
    @Column
    public int pageNum;
    //页码数据  二进制数据
    @Column
    public byte[] data;
    //是否被锁  未锁可修改 已锁不可修改
    @Column
    public boolean isLock = false;
    //最近一次修改时间
    @Column
    public String lastModifyTime;
    //缩略图存放路径
    @Column
    public String picUrl;
    //用户所属ID
    @Column
    public String userUid;
    //同步状态
    @Column
    public int syncState;
    //当前页面的版本号(每次同步成功后，版本号+1。客户端根据版本号判断本地是否是最新版本)，注：同步接口请求后，服务端把最新版本号返回给客户端。
    @Column
    public int syncVersion;
    //页签名称
    @Column
    public String name;
    /*
    * 	notebookId:String     所属笔记本的ID，唯一，主键
	pageId:String         页面的唯一ID，唯一，主键
	pageNumber:Int        页码，唯一，非零正整数
	type:Int              笔记本类型
	lastModifyTime:String 上次修改时间
	isLocked:Bool         是否上锁，上锁不可修改
	picUrl:String         笔记本页面在服务器上的地址(例如https://xxx.com/note.jpg   本地存储文件名则截取”note.jpg“保存，本地存储路径自定义)
	audioUrls:[String]    录音文件在服务器上的地址列表（保存规则同上。文件名截取最后的文件名，本地存储路径客户端自定义）
	syncState:Int         同步状态
	syncVersion:Int       当前页面的版本号。(每次同步成功后，版本号+1。客户端根据版本号判断本地是否是最新版本)，注：同步接口请求后，服务端把最新版本号返回给客户端。
	labels:[String]       当前页面的标签列表
    * */
    PageLabelData label;
    @OneToMany(methods = {OneToMany.Method.LOAD}, variableName = "label")
    public PageLabelData getLabel(){
        if (label == null || label.labelName.isEmpty()) {
            label = SQLite.select()
                    .from(PageLabelData.class)
                    .where(PageLabelData_Table.pageId.eq(pageId),
                            PageLabelData_Table.noteBookId.eq(noteBookId),
                            PageLabelData_Table.userUid.eq(userUid))
                    .querySingle();
        }
        return label;
    }

    //备注：DBFlow会根据你的类名自动生成一个表名，以此为例：
    //这个类对应的表名为：PageData_Table
}
