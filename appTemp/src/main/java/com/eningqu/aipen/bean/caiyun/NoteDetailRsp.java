package com.eningqu.aipen.bean.caiyun;

import java.io.Serializable;
import java.util.List;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/11 14:11
 * desc   :
 * version: 1.0
 */


public class NoteDetailRsp implements Serializable {


    /**
     * attachmentdirid : 1611TWM8Y2t605520210712111140zsm
     * notestatus : 3
     * topmost : 0
     * attachments : [{"filename":"笔记附件_IMG_20210711_230450.jpg","relativepath":"/storage/emulated/0/DCIM/Camera","rsid":"1611TWM8Y2t605820210712115959imy","noteId":"SMmEOFz281Soo79pYX1qdtzvLaYCdQEO","attachmentid":"d92b25dcea794552bd4d06e3b2275ca2","type":"IMAGE","thumbnailURL":"http://download.caiyun.feixin.10086.cn:80/storageWeb/servlet/GetFileByURLServlet?root=/mnt/wfs133&fileid=KBb832a7938a2fd9ac0eec918633ff0a55.jpg&ct=1&type=0&code=47CCD67CADFF1AD7E31768779BE58768EE3DA5F37077443B75E65CC0A3EFA0AA&account=MTM0MTc1MjQzMzU=&p=0&ui=1611TWM8Y2t6&ci=1611TWM8Y2t605820210712115959imy&cn=%E7%AC%94%E8%AE%B0%E9%99%84%E4%BB%B6_IMG_20210711_23...&oprChannel=10000000&dom=D961"}]
     * description : qpen demo
     * noteid : SMmEOFz281Soo79pYX1qdtzvLaYCdQEO
     * title : 页签29
     * remindtype : 0
     * archived : 0
     * audioInfo : {}
     * landmark : []
     * createtime : 1626059499320
     * attachmentdir : 
     * userphone : 13417524335
     * contentid : 1611TWM8Y2t605420210712111140xm3
     * expands : {"noteType":0}
     * version : 1.00
     * cp : 
     * revision : 3
     * tags : [{"userphone":"13417524335","params2":{"id":"4a75ce586b52cb5b2b0d9cc26df61328","text":"新建笔记本1","userphone":"13417524335","orderIndex":"1"},"orderIndex":"1","id":"4a75ce586b52cb5b2b0d9cc26df61328","text":"新建笔记本1"}]
     * visitTime : 1626061777032
     * system : android
     * sharecount : 0
     * contents : [{"txtcontent":"和彩云","data":"<div>\n  和彩云\n<\/div>\n<br />\n<br />\n<br />\n<div>\n <br />\n <div>\n  <img src=\"227e0f04b376171cbcc5e21cce909b8ed92b25dcea794552bd4d06e3b2275ca2\"  class=\"attach_image\" data-media-type=\"image\" id=\"d92b25dcea794552bd4d06e3b2275ca2\" alt=\"笔记附件_IMG_20210711_230450.jpg\" />\n <\/div>\n <br />\n <br />\n<\/div>","sortOrder":0,"contentid":234627748,"noteId":"SMmEOFz281Soo79pYX1qdtzvLaYCdQEO","type":"RICHTEXT"},{"txtcontent":"","data":"d92b25dcea794552bd4d06e3b2275ca2","sortOrder":0,"contentid":234627747,"noteId":"SMmEOFz281Soo79pYX1qdtzvLaYCdQEO","type":"IMAGE"}]
     * sharestatus : 0
     * location : 
     * updatetime : 1626062388083
     * remindtime : 0
     * latlng : 
     */

    public String attachmentdirid;
    public Integer notestatus;
    public String topmost;
    public String description;
    public String noteid;
    public String title;
    public Integer remindtype;
    public Integer archived;
    public AudioInfoBean audioInfo;
    public long createtime;
    public String attachmentdir;
    public String userphone;
    public String contentid;
    public ExpandsBean expands;
    public String version;
    public String cp;
    public int revision;
    public long visitTime;
    public String system;
    public String sharecount;
    public String sharestatus;
    public String location;
    public String updatetime;
    public String remindtime;
    public String latlng;
    public List<AttachmentsBean> attachments;
    public List<?> landmark;
    public List<TagsBean> tags;
    public List<ContentsBean> contents;

    
    public static class AudioInfoBean implements Serializable {
    }

    
    public static class ExpandsBean implements Serializable {
        /**
         * noteType : 0
         */

        public Integer noteType;
    }

    
    public static class AttachmentsBean implements Serializable {
        /**
         * filename : 笔记附件_IMG_20210711_230450.jpg
         * relativepath : /storage/emulated/0/DCIM/Camera
         * rsid : 1611TWM8Y2t605820210712115959imy
         * noteId : SMmEOFz281Soo79pYX1qdtzvLaYCdQEO
         * attachmentid : d92b25dcea794552bd4d06e3b2275ca2
         * type : IMAGE
         * thumbnailURL : http://download.caiyun.feixin.10086.cn:80/storageWeb/servlet/GetFileByURLServlet?root=/mnt/wfs133&fileid=KBb832a7938a2fd9ac0eec918633ff0a55.jpg&ct=1&type=0&code=47CCD67CADFF1AD7E31768779BE58768EE3DA5F37077443B75E65CC0A3EFA0AA&account=MTM0MTc1MjQzMzU=&p=0&ui=1611TWM8Y2t6&ci=1611TWM8Y2t605820210712115959imy&cn=%E7%AC%94%E8%AE%B0%E9%99%84%E4%BB%B6_IMG_20210711_23...&oprChannel=10000000&dom=D961
         */

        public String filename;
        public String relativepath;
        public String rsid;
        public String noteId;
        public String attachmentid;
        public String type;
        public String thumbnailURL;
    }

    
    public static class TagsBean implements Serializable {
        /**
         * userphone : 13417524335
         * params2 : {"id":"4a75ce586b52cb5b2b0d9cc26df61328","text":"新建笔记本1","userphone":"13417524335","orderIndex":"1"}
         * orderIndex : 1
         * id : 4a75ce586b52cb5b2b0d9cc26df61328
         * text : 新建笔记本1
         */

        public String userphone;
        public Params2Bean params2;
        public String orderIndex;
        public String id;
        public String text;

        
        public static class Params2Bean implements Serializable {
            /**
             * id : 4a75ce586b52cb5b2b0d9cc26df61328
             * text : 新建笔记本1
             * userphone : 13417524335
             * orderIndex : 1
             */

            public String id;
            public String text;
            public String userphone;
            public String orderIndex;
        }
    }

    
    public static class ContentsBean implements Serializable {
        /**
         * txtcontent : 和彩云
         * data : <div>
         和彩云
         </div>
         <br />
         <br />
         <br />
         <div>
         <br />
         <div>
         <img src="227e0f04b376171cbcc5e21cce909b8ed92b25dcea794552bd4d06e3b2275ca2"  class="attach_image" data-media-type="image" id="d92b25dcea794552bd4d06e3b2275ca2" alt="笔记附件_IMG_20210711_230450.jpg" />
         </div>
         <br />
         <br />
         </div>
         * sortOrder : 0
         * contentid : 234627748
         * noteId : SMmEOFz281Soo79pYX1qdtzvLaYCdQEO
         * type : RICHTEXT
         */

        public String txtcontent;
        public String data;
        public Integer sortOrder;
        public Integer contentid;
        public String noteId;
        public String type;
    }
}
