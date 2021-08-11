package com.eningqu.aipen.bean.caiyun;

import java.io.Serializable;
import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/11 13:14
 * desc   : 笔记摘要
 * version: 1.0
 */

public class NoteSummaryRsp implements Serializable {


    /**
     * noteList : [{"summary":"3","createTime":"1574322996151","shareStatus":0,"updateTime":"1574322996151","noteId":"f1858a31a4bf572917e00f85572cbd4f","type":"TEXT","title":"测试3","notebook":"默认笔记本","archived":0,"remindTime":"","thumbUrl":"","attachAmount":0,"topmost":0,"remindType":0},{"summary":"222222222","createTime":"1574322984019","shareStatus":0,"updateTime":"1574322984019","noteId":"cf366868902ced58a340022835c41b7c","type":"TEXT","title":"测试2","notebook":"默认笔记本","archived":0,"remindTime":"","thumbUrl":"","attachAmount":0,"topmost":0,"remindType":0}]
     * filterNotes : [{"summary":"3","createTime":"1574322996151","shareStatus":0,"updateTime":"1574322996151","noteId":"f1858a31a4bf572917e00f85572cbd4f","type":"TEXT","title":"测试3","notebook":"默认笔记本","archived":0,"remindTime":"","thumbUrl":"","attachAmount":0,"topmost":0,"remindType":0}]
     * totalCount : 3
     */

    public Integer totalCount;
    public List<NoteListBean> noteList;
    public List<NoteListBean> filterNotes;

    
    public static class NoteListBean implements Serializable {
        /**
         * summary : 3
         * createTime : 1574322996151
         * shareStatus : 0
         * updateTime : 1574322996151
         * noteId : f1858a31a4bf572917e00f85572cbd4f
         * type : TEXT
         * title : 测试3
         * notebook : 默认笔记本
         * archived : 0
         * remindTime : 
         * thumbUrl : 
         * attachAmount : 0
         * topmost : 0
         * remindType : 0
         */

        public String summary;
        public String createTime;
        public Integer shareStatus;
        public String updateTime;
        public String noteId;
        public String type;
        public String title;
        public String notebook;
        public Integer archived;
        public String remindTime;
        public String thumbUrl;
        public Integer attachAmount;
        public Integer topmost;
        public Integer remindType;
    }
}
