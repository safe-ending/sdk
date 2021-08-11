package com.eningqu.aipen.bean.caiyun;

public class NoteBean {
    public String noteId;//笔记Id
    public String notebookId;//笔记本Id
    public String notebook;//笔记所属笔记本名称
    public String title;//笔记标题
    public String content;//笔记标题
    public String summary;//笔记摘要
    public String createTime;//创建时间
    public String updateTime;//更新时间
    public String thumbUrl;//（图片/非图片）附件缩略图地址
    public int attachAmount;//附件数量
    public int topmost;//置顶 0-不置顶，1-置顶
    public String attachmentDirId;//附件目录id

    //    上传用
    public int archived;//
    public int remindType;//
    public String filePath;
    public String fileName;
    public String catalogId;
}
