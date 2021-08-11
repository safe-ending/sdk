package com.eningqu.aipen.bean.caiyun;

/**
 * 获取上传地址成功
 */
public class GetUploadFileUrlRsp {
    public String uploadFileUrl;//上传文件地址
    public String uploadtaskID;//上传任务ID，标识一次上传。
    public String contentId;//请求上传文件的内容ID
    public String catalogId;//上传文件所在目录ID
    public String isNeedUpload;
}
