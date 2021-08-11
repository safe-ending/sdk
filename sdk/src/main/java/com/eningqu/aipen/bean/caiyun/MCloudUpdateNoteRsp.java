package com.eningqu.aipen.bean.caiyun;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/10 16:24
 * desc   :
 * version: 1.0
 */
public class MCloudUpdateNoteRsp implements Serializable {


    /**
     * noteid : 便签id
     * version : 描述文件版本号
     * createtime : 创建时间
     * updatetime : 更新时间
     * revision : 修订版本号
     * title : 标题
     * attachmentdir : 附件目录
     * attachmentdirid : 附件目录在网盘上的ID
     * contentid : 便签网盘上的ID
     * notestatus : 状态
     * system : 来源平台
     * description : 来源平台描述
     * location : 位置信息
     * latlng : 位置经纬度
     * visitTime : 最后访问时间
     * userphone : 所属用户标识
     * tags : [{"id":"标签ID","text":"标签内容","orderIndex":"排序下标 "}]
     * contents : [{"type":"类型","data":"笔记对应的数据","noteId":"便签id","sortOrder":"图文混排时的排序字段"}]
     * attachments : [{"attachmentid":"附件id","noteId":"便签id","rsid":"资源ID","relativepath":"本地的相对路径","filename":"附件的文件名","type":"笔记的类型","thumbnailURL":"缩略图的真实URL地址"}]
     * landMark : [{"noteId":"便签id","longitude":"经度","latitude":"纬度","address":"位置信息","createTime":"地标创建时间"}]
     * remindtime : 提醒时间
     * cp : 创建平台
     * archived : 是否归档
     * topmost : 是否置顶
     * ”remindtype” : ”周期提醒类型”
     */

    public String noteid;
    public String version;
    public String createtime;
    public String updatetime;
    public String revision;
    public String title;
    public String attachmentdir;
    public String attachmentdirid;
    public String contentid;
    public String notestatus;
    public String system;
    public String description;
    public String location;
    public String latlng;
    public long visitTime;
    public String userphone;
    public String remindtime;
    public String cp;
    public String archived;
    public String topmost;
    public String remindtype; // FIXME check this code
    public List<TagsBean> tags;
    public List<ContentsBean> contents;
    public List<AttachmentsBean> attachments;
    public List<LandMarkBean> landMark;


    public static class TagsBean implements Serializable {
        /**
         * id : 标签ID
         * text : 标签内容
         * orderIndex : 排序下标
         */

        public String id;
        public String text;
        public String orderIndex;
    }

    public static class ContentsBean implements Serializable {
        /**
         * type : 类型
         * data : 笔记对应的数据
         * noteId : 便签id
         * sortOrder : 图文混排时的排序字段
         */

        public String type;
        public String data;
        public String noteId;
        public String sortOrder;
    }


    public static class AttachmentsBean implements Serializable {
        /**
         * attachmentid : 附件id
         * noteId : 便签id
         * rsid : 资源ID
         * relativepath : 本地的相对路径
         * filename : 附件的文件名
         * type : 笔记的类型
         * thumbnailURL : 缩略图的真实URL地址
         */

        public String attachmentid;
        public String noteId;
        public String rsid;
        public String relativepath;
        public String filename;
        public String type;
        public String thumbnailURL;
    }


    public static class LandMarkBean implements Serializable {
        /**
         * noteId : 便签id
         * longitude : 经度
         * latitude : 纬度
         * address : 位置信息
         * createTime : 地标创建时间
         */

        public String noteId;
        public String longitude;
        public String latitude;
        public String address;
        public String createTime;
    }
}
