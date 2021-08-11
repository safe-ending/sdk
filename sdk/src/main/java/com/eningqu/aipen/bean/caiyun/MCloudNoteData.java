package com.eningqu.aipen.bean.caiyun;

import java.io.Serializable;
import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/10 15:38
 * desc   : 彩云笔记对象JSON
 * version: 1.0
 */

public class MCloudNoteData implements Serializable {

    /**
     * noteid : 笔记id，端侧自行生成uuid（去掉-）
     * version : 描述文件版本号，传1.00
     * createtime : 创建时间，端侧传时间戳（ms）
     * updatetime : 更新时间，端侧传时间戳（ms）
     * revision : 修订版本号，创建时候传1，每次更新需要+1
     * title : 标题
     * attachmentdir : 附件目录
     * attachmentdirid : 附件目录在网盘上的ID
     * contentid : 笔记网盘上的ID
     * notestatus : [笔记状态：新建0 | 修改1 | 删除2 | 已同步3 | 临时4 | 回收5]
     * system : 来源平台，传android、iphone或者pc，按需传
     * description : 来源平台描述，透传保存数据库
     * location : 位置信息，没有就传null
     * latlng : 位置经纬度，没有就传null
     * visitTime : 最后访问时间，传null
     * userphone : 所属用户标识,传手机号码
     * tags : [{"id":"标签ID","text":"标签内容","orderIndex":"排序下标 "}]
     * contents : [{"type":"类型","data":"笔记对应的数据","noteId":"笔记Id","sortOrder":"图文混排时的排序字段"}]
     * attachments : [{"attachmentid":"附件id，端侧生成，和笔记Id生成规则一样，每个附件的附件id必须保持唯一","noteId":"笔记Id","rsid":"资源ID，无论create和update都要传","relativepath":"本地的相对路径","filename":"附件的文件名","type":"笔记的类型","thumbnailURL":"缩略图的真实URL地址","isMixAttach":"附件是否插入正文，boolean类型"}]
     * landMark : [{"noteId":"笔记Id","longitude":"经度","latitude":"纬度","address":"位置信息","createTime":"地标创建时间"}]
     * remindtime : 提醒时间，时间戳(ms)，不设置传null
     * cp : 创建平台，传pc、android or iphone
     * archived : 是否归档，普通笔记传0，加密柜笔记传1
     * topmost : 是否置顶，0 非置顶，1 置顶，创建时候一般传0
     * remindtype : 短信发送周期,0：无提醒，1：仅一次，2：每天，3：工作日，4：每周，5：每月，6：每年
     * expands : {"noteType":"int类型，0 或者空 普通类型  1 语音类型"}
     * audioInfo : {"audioStatus":"语音转换状态,int 类型，0 待转换 1 转换中 2 转换完成 -1 转换失败","audioCTime":"语音笔记创建时间，时间戳，字符串","audioUpTime":"语音内容更新时间，时间戳，字符串","audioName":"录音名称，字符串","audioDuration":"录音时长，long类型，单位为ms","audioSize":"录音大小，long类型，单位b","audioContent":"语音内容，离线转换语音后的内容，服务端解析后的内容","originalAudioContent":"原始语音内容，咪咕平台回调后的原始json字符串"}
     */

    public String noteid;
    public String version;
    public long createtime;
    public long updatetime;
    public long revision;
    public String title;
    public String attachmentdir;
    public String attachmentdirid;
    public String contentid;
    public int notestatus;
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
    public int remindtype;
    public ExpandsBean expands;
    public AudioInfoBean audioInfo;
    public List<TagsBean> tags;
    public List<ContentsBean> contents;
    public List<AttachmentsBean> attachments;
    public List<LandMarkBean> landMark;


    public static class ExpandsBean implements Serializable {
        /**
         * noteType : int类型，0 或者空 普通类型  1 语音类型
         */

        public String noteType;
    }


    public static class AudioInfoBean implements Serializable {
        /**
         * audioStatus : 语音转换状态,int 类型，0 待转换 1 转换中 2 转换完成 -1 转换失败
         * audioCTime : 语音笔记创建时间，时间戳，字符串
         * audioUpTime : 语音内容更新时间，时间戳，字符串
         * audioName : 录音名称，字符串
         * audioDuration : 录音时长，long类型，单位为ms
         * audioSize : 录音大小，long类型，单位b
         * audioContent : 语音内容，离线转换语音后的内容，服务端解析后的内容
         * originalAudioContent : 原始语音内容，咪咕平台回调后的原始json字符串
         */

        public String audioStatus;
        public String audioCTime;
        public String audioUpTime;
        public String audioName;
        public String audioDuration;
        public String audioSize;
        public String audioContent;
        public String originalAudioContent;
    }


    public static class TagsBean implements Serializable {
        /**
         * id : 标签ID
         * text : 标签内容
         * orderIndex : 排序下标
         */

        public String id;
        public String text;
        public int orderIndex;
    }

    public static class ContentsBean implements Serializable {
        /**
         * type : 类型
         * data : 笔记对应的数据
         * noteId : 笔记Id
         * sortOrder : 图文混排时的排序字段
         */

        public String type;
        public String data;
        public String noteId;
        public String sortOrder;
        public long contentid;
    }


    public static class AttachmentsBean implements Serializable {
        /**
         * attachmentid : 附件id，端侧生成，和笔记Id生成规则一样，每个附件的附件id必须保持唯一
         * noteId : 笔记Id
         * rsid : 资源ID，无论create和update都要传
         * relativepath : 本地的相对路径
         * filename : 附件的文件名
         * type : 笔记的类型
         * thumbnailURL : 缩略图的真实URL地址
         * isMixAttach : 附件是否插入正文，boolean类型
         */

        public String attachmentid;
        public String noteId;
        public String rsid;
        public String relativepath;
        public String filename;
        public String type;
        public String thumbnailURL;
        public boolean isMixAttach;
    }


    public static class LandMarkBean implements Serializable {
        /**
         * noteId : 笔记Id
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
