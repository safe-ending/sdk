package com.eningqu.aipen.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.eningqu.aipen.bean.caiyun.MCloudNoteData;
import com.eningqu.aipen.bean.caiyun.NoteDetailReq;
import com.eningqu.aipen.bean.caiyun.NoteDetailRsp;
import com.eningqu.aipen.bean.caiyun.NoteSummaryReq;
import com.eningqu.aipen.bean.caiyun.NoteSummaryRsp;
import com.eningqu.aipen.bean.caiyun.UploadFileRsp;
import com.eningqu.aipen.bean.caiyun.CaiyunBaseBean;
import com.eningqu.aipen.bean.caiyun.LoginCallBack;
import com.eningqu.aipen.bean.caiyun.NoteAllBean;
import com.eningqu.aipen.bean.caiyun.NoteBean;
import com.eningqu.aipen.bean.caiyun.NotebookAllBean;
import com.eningqu.aipen.bean.caiyun.NotebookBean;
import com.eningqu.aipen.bean.caiyun.GetUploadFileUrlRsp;
import com.eningqu.aipen.common.utils.BitmapUtil;
import com.eningqu.aipen.common.utils.EventBusUtil;
import com.eningqu.aipen.common.utils.FileUtils;
import com.eningqu.aipen.common.utils.GeneratorUtil;
import com.eningqu.aipen.common.utils.HttpUtils;
import com.eningqu.aipen.common.utils.SpUtils;
import com.eningqu.aipen.common.utils.xml.Xml2Obj;
import com.eningqu.aipen.db.model.AASUserInfoData;
import com.eningqu.aipen.db.model.OseUserInfoData;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * 说明：
 * 作者：WangYabin
 * 邮箱：wyb@eningqu.com
 * 时间：14:25
 */
public class NetCommon {
    /**
     * 获得验证码
     *
     * @param number
     */
    public static void getCode(final String number) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils.doGet(AppCommon.BASE_URL + "api/captcha/" + number, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                });
            }
        }).start();
    }

    /**
     * 手机号码注册
     *
     * @param maps
     */
    public static void regester(final Map<String, String> maps) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils.doPost(AppCommon.BASE_URL + "api/mobile/register", maps, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message = new Message();
                        message.obj = response.body().string();
                        message.what = 80001;
                        EventBusUtil.post(message);
                    }
                });
            }
        }).start();
    }

    /**
     * 重置密码
     *
     * @param maps
     */
    public static void forgetpwd(final Map<String, String> maps) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils.doPost(AppCommon.BASE_URL + "api/forgetPwd", maps, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message = new Message();
                        message.obj = response.body().string();
                        message.what = 80002;
                        EventBusUtil.post(message);
                    }
                });
            }
        }).start();
    }

    /**
     * 登陆
     *
     * @param maps
     */
    public static void login(final Map<String, String> maps) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils.doPost(AppCommon.BASE_URL + "api/login/mobile", maps, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message = new Message();
                        message.obj = response.body().string();
                        message.what = 80003;
                        EventBusUtil.post(message);
                    }
                });
            }
        }).start();
    }

    /**
     * 检查手机号码是否已经注册
     *
     * @param number
     */
    public static void checkNumber(final String number) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils.doGet(AppCommon.BASE_URL + "api/check/mobile/" + number, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                boolean isBindOauth = data.getBoolean("isBindOauth");
                                if (isBindOauth) {//手机号未注册
                                    Message message = new Message();
                                    message.obj = "手机号码已经注册和绑定";
                                    message.what = 80006;
                                    EventBusUtil.post(message);
                                } else {//手机号码已注册但未绑定
                                    Message message = new Message();
                                    message.obj = "手机号码已经注册和绑定";
                                    message.what = 80005;
                                    EventBusUtil.post(message);
                                }
                            } else {
                                Message message = new Message();
                                message.obj = "手机号码已经注册和绑定";
                                message.what = 80004;
                                EventBusUtil.post(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 手机号未注册
     *
     * @param context
     * @param maps
     */
    public static void bindingHasResNumber(final Context context, final Map<String, String> maps, int flag) {
        String path = null;
        if (flag == 1) {
            path = "/api/bind/registerMobile";
        } else {
            path = "/api/bind/mobile";
        }
        final String finalPath = path;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils.doPost(AppCommon.BASE_URL + finalPath, maps, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(body);
                            int code = jsonObject.getInt("code");
                            boolean success = jsonObject.getBoolean("success");
                            if (code == 1 && success) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String token = data.getString("token");
                                SpUtils.putString(context, SpUtils.LOGIN_TOKEN, token);
                                Message message = new Message();
                                message.obj = jsonObject.getString("msg");
                                message.what = 80007;
                                EventBusUtil.post(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }


    /**
     * 第三方授权登录接口
     *
     * @param maps
     */
    public static void oauth(final Map<String, String> maps) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils.doPost(AppCommon.BASE_URL + "api/login/oauth", maps, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e("NetCommon", response.toString());
/*                        Message message = new Message();
                        message.obj = response.body().string();
                        message.what = 80003;
                        EventBusUtil.post(message);*/
                    }
                });
            }
        }).start();
    }

    /**
     * 发送错误提示
     *
     * @param msg
     */
    private static void sendUploadFailureMsg(String msg) {
        EventBusCarrier eventBusCarrier = new EventBusCarrier();
        eventBusCarrier.setEventType(80015);
        eventBusCarrier.setObject(msg);
        EventBusUtil.post(eventBusCarrier);
    }

    /**
     * 获取彩云http通用头
     *
     * @return
     */
    private static Map<String, String> getAASHeaders() {
        Map<String, String> headers = new HashMap<>();
//        headers.put("x-UserAgent", MCloudConf.X_USER_AGENT);
        headers.put("x-huawei-channelSrc", MCloudConf.X_HUAWEI_CHANNELSRC);
        return headers;
    }

    /**
     * 加密
     *
     * @param data
     */
    public static String Base64encode(String data) {
        try {
            if (null == data) {
                return null;
            }
            return new String(android.util.Base64.encode(data.getBytes("utf-8"), android.util.Base64.DEFAULT), "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(String.format("字符串：%s，加密异常", data), e.getMessage());
        }

        return null;
    }

    /**
     * 获取彩云http通用头2
     *
     * @return
     */
    private static Map<String, String> getMCloudHeaders2() {
        OseUserInfoData oseUserInfoData = AppCommon.loadOseUserInfo();

        Map<String, String> headers = getAASHeaders();

//        String key = Base64encode(aasUserInfoData.account + ":" + aasUserInfoData.token);
//        headers.put("Authorization", "" + key.replaceAll("\r|\n", ""));//用户手机号码
        headers.put("APP_NUMBER", "" + oseUserInfoData.getAPP_NUMBER());//用户手机号码
        headers.put("APP_AUTH", "" + oseUserInfoData.getAPP_AUTH());//鉴权后的字符串
        headers.put("NOTE_TOKEN", "" + oseUserInfoData.getNOTE_TOKEN());//便签令牌
        headers.put("APP_CP", "android");//所属渠道
        headers.put("CP_VERSION", "1.0");//渠道版本号
        headers.put("call_id", System.currentTimeMillis() + "");//序列号，使用当前的微秒时间作为序列号
        return headers;
    }

    private static String maps2xml(Map<String, String> maps) {
        StringBuilder sb = new StringBuilder();
        sb.append("<root>");
        Iterator<Map.Entry<String, String>> it = maps.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append("<" + key + ">").append(value).append("</" + key + ">");
        }
        sb.append("</root>");

        return sb.toString();
    }

    /**
     * 和彩云AAS短信验证码
     *
     * @param maps
     */
    public static void aasGetVeriCode(String url, final Map<String, String> maps) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpUtils.doPostXml(url, getAASHeaders(), maps2xml(maps), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                        sendUploadFailureMsg("验证码发送失败，请稍后重试 1-029");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        if (null != response) {

                            EventBusCarrier eventBusCarrier = new EventBusCarrier();
                            eventBusCarrier.setEventType(80012);
                            eventBusCarrier.setObject(response.body().string());
                            EventBusUtil.post(eventBusCarrier);
                        } else {
                            sendUploadFailureMsg("验证码发送失败，请稍后重试 1-030");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 和彩云AAS授权
     *
     * @param maps
     */
    public static void aasLogin(final Map<String, String> maps) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String xml = maps2xml(maps);

                HttpUtils.doPostOctetStream(MCloudConf.MCLOUD_AAS_URL + "tellin/thirdlogin.do", getAASHeaders(), xml, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                        sendUploadFailureMsg("绑定失败 1-027");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (null != response) {

                            EventBusCarrier eventBusCarrier = new EventBusCarrier();
                            eventBusCarrier.setEventType(80013);
                            eventBusCarrier.setObject(response.body().string());
                            EventBusUtil.post(eventBusCarrier);
                        } else {
                            sendUploadFailureMsg("绑定失败 1-028");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 和彩云授权2
     *
     * @param mCloudUserInfoData
     */
    public static void mCloudAuthLogin(AASUserInfoData mCloudUserInfoData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> maps = new HashMap<>();
                maps.put("username", mCloudUserInfoData.account + "");//用户手机号码
                maps.put("password", "286759");//用户密码
                maps.put("type", "0");//登陆方式：0：通行证短信密码登陆，1：通行证密码登陆；默认0

                HttpUtils.doPostJson(MCloudConf.MCLOUD_OSE_URL + "/noteServer/api/thirdlogin.do", getMCloudHeaders2(), new Gson().toJson(maps), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();
                            Log.e("彩云数据", body + "");
                            CaiyunBaseBean caiyunBaseBean = new Gson().fromJson(body, CaiyunBaseBean.class);
                        }
                    }
                });
            }
        }).start();
    }


    /**
     * 和彩云获取文件上传地址
     *
     * @param noteBean
     */
    public static void mCloudGetUploadPath(NoteDetailRsp noteDetail, NoteBean noteBean, String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> maps = new HashMap<>();
                Map<String, String> map = new HashMap<>();
                try {
                    map.put("contentSize", String.valueOf(FileUtils.getFileSize(new File(noteBean.filePath))));//内容大小
                    map.put("digest", FileUtils.getMD5Checksum(noteBean.filePath));//待上传内容的MD5摘要值
                } catch (Exception e) {
                    sendUploadFailureMsg("上传失败 1-010");
                    e.printStackTrace();
                }
                final AASUserInfoData aasUserInfoData = AppCommon.loadAASUserInfo();
                maps.put("filename", noteBean.fileName);//文件名称
                maps.put("noteid", noteBean.noteId);//笔记Id
                maps.put("client", "android");//客户端类型，传android、iphone、或者pc
                maps.put("uploadContentInfo", new Gson().toJson(map));//上传内容信息，v2接口必传
                HttpUtils.doPostJson(MCloudConf.MCLOUD_OSE_URL + "/noteServer/api/getUploadFileURLV2.do", getMCloudHeaders2(), new Gson().toJson(maps), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                        sendUploadFailureMsg("上传失败 1-011");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();
                            GetUploadFileUrlRsp uploadFileUrlRsp = new Gson().fromJson(body, GetUploadFileUrlRsp.class);
                            if (null != uploadFileUrlRsp) {
                                noteBean.catalogId = uploadFileUrlRsp.catalogId;
                                if ("1".equals(uploadFileUrlRsp.isNeedUpload)) {
                                    mCloudUploadFile(noteDetail, noteBean, uploadFileUrlRsp, maps, type);
                                } else {
                                    MCloudNoteData mCloudNoteData = new MCloudNoteData();


                                    if (null != noteDetail) {
                                        mCloudNoteData.version = noteDetail.version;//描述文件版本号，传1.00
                                        mCloudNoteData.createtime = noteDetail.createtime;
                                        mCloudNoteData.revision = noteDetail.revision + 1;//修订版本号，创建时候传1，每次更新需要+1
                                        mCloudNoteData.attachmentdir = noteDetail.attachmentdir;//附件目录
                                        mCloudNoteData.notestatus = 1;//[笔记状态：新建0 | 修改1 | 删除2 | 已同步3 | 临时4 | 回收5]
                                        mCloudNoteData.contentid = noteDetail.contentid;//笔记网盘上的ID
                                    } else {
                                        mCloudNoteData.version = "1.00";//描述文件版本号，传1.00
                                        mCloudNoteData.createtime = System.currentTimeMillis();
                                        mCloudNoteData.revision = 1;//修订版本号，创建时候传1，每次更新需要+1
                                        mCloudNoteData.notestatus = 0;//[笔记状态：新建0 | 修改1 | 删除2 | 已同步3 | 临时4 | 回收5]
                                        mCloudNoteData.contentid = uploadFileUrlRsp.contentId;//笔记网盘上的ID
                                    }

                                    mCloudNoteData.updatetime = System.currentTimeMillis();
                                    mCloudNoteData.title = noteBean.title;
                                    mCloudNoteData.attachmentdirid = noteBean.catalogId;

                                    mCloudNoteData.system = "android";
                                    mCloudNoteData.description = "qpen demo";
                                    mCloudNoteData.location = "";//位置信息，没有就传null
                                    mCloudNoteData.latlng = "";//置经纬度，没有就传null
                                    mCloudNoteData.visitTime = System.currentTimeMillis();
                                    mCloudNoteData.userphone = aasUserInfoData.account;
                                    //tags
                                    //contents
                                    mCloudNoteData.contents = new ArrayList<>();
                                    MCloudNoteData.ContentsBean contentsBean = new MCloudNoteData.ContentsBean();
                                    if (null != noteBean && !TextUtils.isEmpty(noteBean.catalogId)) {
                                        mCloudNoteData.attachmentdirid = noteBean.catalogId;//附件id，端侧生成，和笔记Id生成规则一样，每个附件的附件id必须保持唯一
                                    } else {
                                        mCloudNoteData.attachmentdirid = GeneratorUtil.randomSequence(32);//附件id，端侧生成，和笔记Id生成规则一样，每个附件的附件id必须保持唯一
                                    }

                                    //attachments
                                    mCloudNoteData.attachments = new ArrayList<>();
                                    MCloudNoteData.AttachmentsBean attachmentsBean = new MCloudNoteData.AttachmentsBean();
                                    if (null != noteDetail && null != noteDetail.attachments && noteDetail.attachments.size() > 0) {
                                        attachmentsBean.attachmentid = noteDetail.attachments.get(0).attachmentid;//附件id，端侧生成，和笔记Id生成规则一样，每个附件的附件id必须保持唯一
                                    } else {
                                        attachmentsBean.attachmentid = GeneratorUtil.randomSequence(32);//附件id，端侧生成，和笔记Id生成规则一样，每个附件的附件id必须保持唯一
                                    }
//                                    attachmentsBean.noteId = noteBean.noteId;
                                    attachmentsBean.rsid = uploadFileUrlRsp.contentId;//资源ID，无论create和update都要传
                                    attachmentsBean.relativepath = noteBean.filePath;//本地的相对路径，可不传
                                    attachmentsBean.filename = noteBean.fileName;
                                    attachmentsBean.type = "IMAGE";
                                    attachmentsBean.thumbnailURL = "";//缩略图的真实URL地址，可不传
                                    attachmentsBean.isMixAttach = true;//
                                    mCloudNoteData.attachments.add(attachmentsBean);
                                    //landMark
                                    mCloudNoteData.cp = "android";
                                    mCloudNoteData.archived = "0";//是否归档，普通笔记传0，加密柜笔记传1
                                    mCloudNoteData.topmost = "0";//是否置顶，0 非置顶，1 置顶，创建时候一般传0
                                    mCloudNoteData.remindtype = 0;//短信发送周期,0：无提醒，1：仅一次，2：每天，3：工作日，4：每周，5：每月，6：每年
                                    mCloudNoteData.remindtime = "";

                                    String[] text = noteBean.content.split("\n");
                                    StringBuffer stringBuffer = new StringBuffer();
                                    for (String str : text) {
                                        stringBuffer.append("<div>");
                                        stringBuffer.append(str);
                                        stringBuffer.append("</div>");
                                    }
                                    stringBuffer.append("<br>");
                                    stringBuffer.append("<br>");
                                    stringBuffer.append("<br>");
                                    //<div><img class=\"attach_image\" data-media-type=\"image\" id=\"d3d870fa7b2a4536940b6d77123e32c4\" src=\"227e0f04b376171cbcc5e21cce909b8ed3d870fa7b2a4536940b6d77123e32c4\" alt=\"笔记附件_MobileNoteSDK_20210712_092604.JPEG\"></div>
//                               <img class=\"attach_image\" data-media-type=\"image\" id=\"d3d870fa7b2a4536940b6d77123e32c4\" src=\"227e0f04b376171cbcc5e21cce909b8ed3d870fa7b2a4536940b6d77123e32c4\" alt=\"笔记附件_MobileNoteSDK_20210712_092604.JPEG\">
//                               <img class=\"attach_image\" data-media-type=\"image\" id=\"0i11dIqd70jU16920210712101736nse\" src=\"227e0f04b376171cbcc5e21cce909b8e0i11dIqd70jU16920210712101736nse\" alt=\"1626056894121.jpg\" />
                                    stringBuffer.append("<div>");
                                    stringBuffer.append("<img class=\"attach_image\" data-media-type=\"image\" id=\"");
                                    stringBuffer.append(attachmentsBean.attachmentid);
                                    stringBuffer.append("\" src=\"227e0f04b376171cbcc5e21cce909b8e");
                                    stringBuffer.append(attachmentsBean.attachmentid);
                                    stringBuffer.append("\" alt=\"");
                                    stringBuffer.append(attachmentsBean.filename);
                                    stringBuffer.append("\" >");
                                    stringBuffer.append("</div>");

                                    contentsBean.data = stringBuffer.toString();
                                    contentsBean.sortOrder = "0";
                                    contentsBean.type = "RICHTEXT";
                                    contentsBean.contentid = 0;
                                    contentsBean.noteId = noteBean.noteId;
                                    mCloudNoteData.contents.add(contentsBean);

                                    MCloudNoteData.ContentsBean contentsImgBean = new MCloudNoteData.ContentsBean();
                                    contentsImgBean.data = attachmentsBean.attachmentid;
                                    contentsImgBean.noteId = noteBean.noteId;
                                    contentsImgBean.sortOrder = "0";
                                    contentsImgBean.contentid = 0;
                                    contentsImgBean.type = "IMAGE";
                                    mCloudNoteData.contents.add(contentsImgBean);
                                    //expands
                                    //audioInfo


                                    if ("add".equals(type)) {
//                                          mCloudCreateNote(noteBean);
                                        mCloudNoteData.noteid = GeneratorUtil.randomSequence(32);
                                        mCloudNoteData.notestatus = 0;//[笔记状态：新建0 | 修改1 | 删除2 | 已同步3 | 临时4 | 回收5]
                                        mCloudOperCreateNote(mCloudNoteData);
                                    } else {
                                        mCloudNoteData.notestatus = 1;//[笔记状态：新建0 | 修改1 | 删除2 | 已同步3 | 临时4 | 回收5]
//                                          mCloudUpdateNote(noteBean);
                                        mCloudNoteData.noteid = noteBean.noteId;
                                        mCloudOperUpdateNote(mCloudNoteData);
                                    }
                                }
                            } else {
                                //响应头
                                Headers rspHeaders = response.headers();
                                //遍历Header数组，并打印出来
                                Set<String> rspNames = rspHeaders.names();
                                String error = "";
                                for (String name : rspNames) {
                                    if ("ERRORCODE".equals(name)) {
                                        error = rspHeaders.get(name);
                                        break;
                                    }
                                }

                                sendUploadFailureMsg("上传失败 1-013 " + error);
                            }
                        } else {
                            sendUploadFailureMsg("上传失败 1-012");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 和彩云上传附件
     *
     * @param uploadFileUrlRsp 为彩云第一次上传获取到的结果中拿取
     * @param params
     */
    public static void mCloudUploadFile(NoteDetailRsp noteDetail, NoteBean noteBean, GetUploadFileUrlRsp uploadFileUrlRsp, Map<String, String> params, String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String size = "";
                try {
                    size = String.valueOf(FileUtils.getFileSize(new File(noteBean.filePath)));
                } catch (Exception e) {
                    e.printStackTrace();
                    sendUploadFailureMsg("上传失败 1-025");
                }

                //读取图片数据
                byte[] first = new byte[0];
                byte[] second = new byte[0];
                try {
                    first = FileUtils.readStream(noteBean.filePath);
                    Bitmap thumbnail = BitmapUtil.getImageThumbnail(first, 30);
                    second = BitmapUtil.bitmap2Bytes(thumbnail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                byte[] result = Arrays.copyOf(first, first.length + second.length);
                System.arraycopy(second, 0, result, first.length, second.length);

//                OseUserInfoData oseUserInfoData = AppCommon.loadOseUserInfo();
                //组装参数
                Map<String, String> maps = new HashMap<>();
                final AASUserInfoData aasUserInfoData = AppCommon.loadAASUserInfo();
                String key = "Basic " + Base64encode(aasUserInfoData.account + ":" + uploadFileUrlRsp.uploadtaskID).replaceAll("\r|\n", "");
                maps.put("Authorization", key);//标准HTTP头域，遵照HTTP定义 起始地址指的是上次上传文件时的中断位置
                maps.put("Content-Type", "image/jpeg;name=" + noteBean.fileName);//
                maps.put("Range", "bytes=0-" + (Integer.parseInt(size) - 1));//标准HTTP头域，遵照HTTP定义起始地址指的是上次上传文件时的中断位置
//                maps.put("rangeType", "1");//
//                maps.put("x-NameCoding", "urlencoding");//

                maps.put("contentSize", size);//文件内容大小，字节为单位。当rangeType=1，contentSize填写本次文件分片的大小；当rangeType=2，contentSize填写为完整文件的大小。
                maps.put("UploadtaskID", uploadFileUrlRsp.uploadtaskID);//上传任务ID，标识本次上传
//                maps.put("Content-Length", (size) + "");//消息包体的长度

                HttpUtils.uploadFileBinaryStream(uploadFileUrlRsp.uploadFileUrl, maps, noteBean.fileName, first, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        sendUploadFailureMsg("图片上传失败 1-023");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String body = response.body().string();
                            UploadFileRsp obj = Xml2Obj.fromXml(body, UploadFileRsp.class);
                            if (null == obj) {
                                sendUploadFailureMsg("上传失败 1-024-0 ");
                            }
                            if ("0".equals(obj.getResultCode())) {
                                MCloudNoteData mCloudNoteData = new MCloudNoteData();
                                if (null != noteDetail) {
                                    mCloudNoteData.version = noteDetail.version;//描述文件版本号，传1.00
                                    mCloudNoteData.createtime = noteDetail.createtime;
                                    mCloudNoteData.revision = noteDetail.revision + 1;//修订版本号，创建时候传1，每次更新需要+1
                                    mCloudNoteData.attachmentdir = noteDetail.attachmentdir;//附件目录
                                    mCloudNoteData.notestatus = 1;//[笔记状态：新建0 | 修改1 | 删除2 | 已同步3 | 临时4 | 回收5]
                                    mCloudNoteData.contentid = noteDetail.contentid;//笔记网盘上的ID
                                } else {
                                    mCloudNoteData.version = "1.00";//描述文件版本号，传1.00
                                    mCloudNoteData.createtime = System.currentTimeMillis();
                                    mCloudNoteData.revision = 1;//修订版本号，创建时候传1，每次更新需要+1
                                    mCloudNoteData.notestatus = 0;//[笔记状态：新建0 | 修改1 | 删除2 | 已同步3 | 临时4 | 回收5]
                                    mCloudNoteData.contentid = uploadFileUrlRsp.contentId;//笔记网盘上的ID
                                }
                                mCloudNoteData.contentid = uploadFileUrlRsp.contentId;
                                MCloudNoteData.TagsBean tagsBean = new MCloudNoteData.TagsBean();
                                tagsBean.id = noteBean.notebookId;
                                tagsBean.orderIndex = 0;
                                tagsBean.text = noteBean.notebook;
                                mCloudNoteData.tags = new ArrayList<>();
                                mCloudNoteData.tags.add(tagsBean);

                                mCloudNoteData.updatetime = System.currentTimeMillis();
                                mCloudNoteData.title = noteBean.title;

//                                mCloudNoteData.attachmentdirid = noteBean.attachmentDirId;
                                mCloudNoteData.system = "android";
                                mCloudNoteData.description = "qpen demo";
                                mCloudNoteData.location = "";//位置信息，没有就传null
                                mCloudNoteData.latlng = "";//置经纬度，没有就传null
                                mCloudNoteData.visitTime = System.currentTimeMillis();
                                mCloudNoteData.userphone = aasUserInfoData.account;
                                //tags
                                //contents
                                mCloudNoteData.contents = new ArrayList<>();
                                MCloudNoteData.ContentsBean contentsBean = new MCloudNoteData.ContentsBean();
//                                contentsBean.noteId = noteBean.noteId;
//                                if (null != noteBean && !TextUtils.isEmpty(noteBean.catalogId)) {
//                                    mCloudNoteData.attachmentdirid = noteBean.catalogId;//附件id，端侧生成，和笔记Id生成规则一样，每个附件的附件id必须保持唯一
//                                } else {
                                    mCloudNoteData.attachmentdirid = GeneratorUtil.randomSequence(32);//附件id，端侧生成，和笔记Id生成规则一样，每个附件的附件id必须保持唯一
//                                }

                                //attachments
                                mCloudNoteData.attachments = new ArrayList<>();
                                MCloudNoteData.AttachmentsBean attachmentsBean = new MCloudNoteData.AttachmentsBean();
//                                if(null!=noteDetail && null!=noteDetail.attachments && noteDetail.attachments.size()>0){
//                                    attachmentsBean.attachmentid = noteDetail.attachments.get(0).attachmentid;//附件id，端侧生成，和笔记Id生成规则一样，每个附件的附件id必须保持唯一
//                                } else {
                                attachmentsBean.attachmentid = GeneratorUtil.randomSequence(32);//附件id，端侧生成，和笔记Id生成规则一样，每个附件的附件id必须保持唯一
//                                }
                                attachmentsBean.noteId = noteBean.noteId;
                                attachmentsBean.rsid = uploadFileUrlRsp.contentId;//资源ID，无论create和update都要传
                                attachmentsBean.relativepath = noteBean.filePath;//本地的相对路径，可不传
                                attachmentsBean.filename = noteBean.fileName;
                                attachmentsBean.type = "IMAGE";//https://cdn3-banquan.ituchong.com/weili/l/907924913558651006.webp
                                attachmentsBean.thumbnailURL = "";//缩略图的真实URL地址，可不传
                                attachmentsBean.isMixAttach = true;//
                                mCloudNoteData.attachments.add(attachmentsBean);
                                //landMark
                                mCloudNoteData.cp = "android";
                                mCloudNoteData.archived = "0";//是否归档，普通笔记传0，加密柜笔记传1
                                mCloudNoteData.topmost = "0";//是否置顶，0 非置顶，1 置顶，创建时候一般传0
                                mCloudNoteData.remindtype = 0;//短信发送周期,0：无提醒，1：仅一次，2：每天，3：工作日，4：每周，5：每月，6：每年
                                mCloudNoteData.remindtime = "";
                                String[] text = noteBean.content.split("\n");
                                StringBuffer stringBuffer = new StringBuffer();
                                for (String str : text) {
                                    stringBuffer.append("<div>");
                                    stringBuffer.append(str);
                                    stringBuffer.append("</div>");
                                }
                                stringBuffer.append("<br>");
                                stringBuffer.append("<br>");
                                stringBuffer.append("<br>");
                                //<div><img class=\"attach_image\" data-media-type=\"image\" id=\"d3d870fa7b2a4536940b6d77123e32c4\" src=\"227e0f04b376171cbcc5e21cce909b8ed3d870fa7b2a4536940b6d77123e32c4\" alt=\"笔记附件_MobileNoteSDK_20210712_092604.JPEG\"></div>
//                               <img class=\"attach_image\" data-media-type=\"image\" id=\"d3d870fa7b2a4536940b6d77123e32c4\" src=\"227e0f04b376171cbcc5e21cce909b8ed3d870fa7b2a4536940b6d77123e32c4\" alt=\"笔记附件_MobileNoteSDK_20210712_092604.JPEG\">
//                               <img class=\"attach_image\" data-media-type=\"image\" id=\"Ps4abPjwt2eq6Q7JR2ecc44cjHS6O0rU\" src=\"227e0f04b376171cbcc5e21cce909b8ePs4abPjwt2eq6Q7JR2ecc44cjHS6O0rU\" alt=\"1626058925849.jpg\" />
                                stringBuffer.append("<div>");
                                stringBuffer.append("<img class=\"attach_image\" data-media-type=\"image\" id=\"");
                                stringBuffer.append(attachmentsBean.attachmentid);
                                stringBuffer.append("\" src=\"227e0f04b376171cbcc5e21cce909b8e");
                                stringBuffer.append(attachmentsBean.attachmentid);
                                stringBuffer.append("\" alt=\"");
                                stringBuffer.append(noteBean.fileName);
                                stringBuffer.append("\" >");
                                stringBuffer.append("</div>");

                                contentsBean.data = stringBuffer.toString();
                                contentsBean.sortOrder = "0";
                                contentsBean.noteId = noteBean.noteId;
                                contentsBean.type = "TEXT";
//                                contentsBean.contentid = 0;
                                mCloudNoteData.contents.add(contentsBean);

                                MCloudNoteData.ContentsBean contentsImgBean = new MCloudNoteData.ContentsBean();
                                contentsImgBean.data = attachmentsBean.attachmentid;
                                contentsImgBean.noteId = noteBean.noteId;
                                contentsImgBean.sortOrder = "0";
//                                contentsImgBean.contentid = 0;
                                contentsImgBean.type = "IMAGE";
                                mCloudNoteData.contents.add(contentsImgBean);
                                //expands
                                //audioInfo


                                if ("add".equals(type)) {
                                    mCloudNoteData.noteid = GeneratorUtil.randomSequence(32);
//                                    mCloudCreateNote(noteBean);
                                    mCloudNoteData.notestatus = 0;//[笔记状态：新建0 | 修改1 | 删除2 | 已同步3 | 临时4 | 回收5]
                                    mCloudOperCreateNote(mCloudNoteData);
                                } else {
                                    mCloudNoteData.noteid = noteBean.noteId;
                                    mCloudNoteData.notestatus = 1;//[笔记状态：新建0 | 修改1 | 删除2 | 已同步3 | 临时4 | 回收5]
//                                    mCloudUpdateNote(noteBean);
                                    mCloudOperUpdateNote(mCloudNoteData);
                                }
                            } else {
                                sendUploadFailureMsg("上传失败 1-024-1 " + obj.getResultCode());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            sendUploadFailureMsg("上传失败 1-024");
                        }

                    }
                });
            }
        }).start();
    }

    /**
     * 和彩云获取笔记本列表
     */
    public static void mCloudGetNotebookList(NoteBean noteBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> maps = new HashMap<>();
                HttpUtils.doPostJson(MCloudConf.MCLOUD_OSE_URL + "/noteServer/openapi/notebook/getAll.do", getMCloudHeaders2(), new Gson().toJson(""), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                        sendUploadFailureMsg("上传失败 1-001");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();
                            NotebookAllBean notebookAllBean = new Gson().fromJson(body, NotebookAllBean.class);
                            if (notebookAllBean != null) {
                                List<NotebookBean> notebookBeans = notebookAllBean.notebooks;
                                if (notebookBeans == null) {
                                    mCloudCreateNoteBook(noteBean);
                                } else {
                                    boolean haveBook = false;
                                    for (NotebookBean bean : notebookBeans) {
                                        if (noteBean.notebook.equals(bean.text)) {
                                            haveBook = true;
                                            noteBean.notebookId = bean.notebookId;
                                            break;
                                        }
                                    }
                                    if (!haveBook) {
                                        // 没有笔记本时先创建笔记本
                                        mCloudCreateNoteBook(noteBean);
                                    } else {
                                        // 有笔记本时先获取该笔记本的笔记摘要
                                        // mCloudGetNoteList(noteBean);
                                        NoteSummaryReq summaryReq = new NoteSummaryReq();
                                        summaryReq.pageIndex = 1;//从第一条开始
                                        summaryReq.pageSize = 100;//每页请求100
                                        summaryReq.sortType = 0;//按实际排序
                                        summaryReq.sort = 0;//倒序
                                        mCloudGetNoteSummaryList(noteBean, summaryReq);
                                    }
                                }
                            } else {
                                sendUploadFailureMsg("上传失败 1-003");
                            }
                        } else {
                            sendUploadFailureMsg("上传失败 1-002");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 和彩云获取笔记列表
     */
    public static void mCloudGetNoteList(NoteBean noteBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> maps = new HashMap<>();
                HttpUtils.doPostJson(MCloudConf.MCLOUD_OSE_URL + "/noteServer/openapi/note/getList.do", getMCloudHeaders2(), new Gson().toJson(maps), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                        sendUploadFailureMsg("上传失败 1-007");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();
                            NoteAllBean noteAllBean = new Gson().fromJson(body, NoteAllBean.class);
                            if (noteAllBean != null) {
                                List<NoteBean> noteBeans = noteAllBean.normalList;
                                if (noteBeans == null) {
                                    mCloudGetUploadPath(null, noteBean, "add");
//                                    mCloudcreateNote(noteBean);
                                } else {
                                    boolean haveBook = false;
                                    for (NoteBean bean : noteBeans) {
                                        if (noteBean.title.equals(bean.title)) {
                                            haveBook = true;
                                            noteBean.notebookId = bean.notebookId;
                                            noteBean.noteId = bean.noteId;
                                            mCloudGetUploadPath(null, noteBean, "update");
//                                            mCloudupdateNote(noteBean);
                                            break;
                                        }
                                    }
                                    if (!haveBook) {
                                        mCloudGetUploadPath(null, noteBean, "add");
//                                        mCloudcreateNote(noteBean);
                                    }
                                }
                            } else {
                                sendUploadFailureMsg("上传失败 1-009");
                            }

                        } else {
                            sendUploadFailureMsg("上传失败 1-008");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 和彩云获取笔记摘要列表
     */
    public static void mCloudGetNoteSummaryList(NoteBean noteBean, NoteSummaryReq summaryReq) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpUtils.doPostJson(MCloudConf.MCLOUD_OSE_URL + "/noteServer/api/getNoteList.do", getMCloudHeaders2(), new Gson().toJson(summaryReq), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                        sendUploadFailureMsg("上传失败 1-007");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();
                            NoteSummaryRsp summaryRsp = new Gson().fromJson(body, NoteSummaryRsp.class);
                            if (null != summaryRsp) {
                                if (summaryRsp.totalCount > 0 && null != summaryRsp.noteList && summaryRsp.noteList.size() > 0) {
                                    boolean haveNote = false;
                                    for (NoteSummaryRsp.NoteListBean bean : summaryRsp.noteList) {
                                        if (noteBean.notebook.equals(bean.notebook) && noteBean.title.equals(bean.title)) {
                                            haveNote = true;
//                                            noteBean.notebookId = bean.notebookId;
                                            noteBean.noteId = bean.noteId;
                                            break;
                                        }
                                    }
                                    if (!haveNote) {
                                        // 没有找到笔记记录，创建新记录
                                        // 先获取上传地址
                                        mCloudGetUploadPath(null, noteBean, "add");
//                                        mCloudcreateNote(noteBean);
                                    } else {
                                        // 更新对应的记录
                                        // 先获取上传地址
//                                        mCloudGetUploadPath(noteBean, "update");
//                                      mCloudupdateNote(noteBean);

                                        NoteDetailReq detailReq = new NoteDetailReq();
                                        detailReq.noteId = noteBean.noteId;
                                        mCloudGetNoteDetail(noteBean, detailReq);
                                    }
                                } else {
                                    // 没有找到笔记记录，创建新记录
                                    // 先获取上传地址
                                    mCloudGetUploadPath(null, noteBean, "add");
//                                    mCloudcreateNote(noteBean);
                                }
                            } else {
                                sendUploadFailureMsg("上传失败 1-009");
                            }
                            /*if (noteAllBean != null) {
                                List<NoteBean> noteBeans = noteAllBean.normalList;
                                if (noteBeans == null) {
                                    mCloudGetUploadPath(noteBean, "add");
//                                    mCloudcreateNote(noteBean);
                                } else {
                                    boolean haveBook = false;
                                    for (NoteBean bean : noteBeans) {
                                        if (noteBean.title.equals(bean.title)) {
                                            haveBook = true;
                                            noteBean.notebookId = bean.notebookId;
                                            noteBean.noteId = bean.noteId;
                                            mCloudGetUploadPath(noteBean, "update");
//                                            mCloudupdateNote(noteBean);
                                            break;
                                        }
                                    }
                                    if (!haveBook) {
                                        mCloudGetUploadPath(noteBean, "add");
//                                        mCloudcreateNote(noteBean);
                                    }
                                }
                            } else {
                                sendUploadFailureMsg("上传失败 1-009");
                            }*/
                        } else {
                            sendUploadFailureMsg("上传失败 1-008");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 和彩云获取笔记详情
     */
    public static void mCloudGetNoteDetail(NoteBean noteBean, NoteDetailReq detailReq) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpUtils.doPostJson(MCloudConf.MCLOUD_OSE_URL + "/noteServer/api/getNoteV2.do", getMCloudHeaders2(), new Gson().toJson(detailReq), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                        sendUploadFailureMsg("上传失败 1-007");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();
                            NoteDetailRsp detailRsp = new Gson().fromJson(body, NoteDetailRsp.class);

                            if (detailRsp != null) {
                                List<NoteDetailRsp.ContentsBean> contentsBeans = detailRsp.contents;
                                List<NoteDetailRsp.AttachmentsBean> attachmentsBeans = detailRsp.attachments;
                                noteBean.attachmentDirId = detailRsp.attachmentdirid;
                                if (contentsBeans == null || contentsBeans.size() == 0 ||
                                        attachmentsBeans == null || attachmentsBeans.size() == 0) {
                                    mCloudGetUploadPath(detailRsp, noteBean, "add");
                                } else {
                                    mCloudGetUploadPath(detailRsp, noteBean, "update");
                                }
                            } else {
                                sendUploadFailureMsg("上传失败 1-009");
                            }
                        } else {
                            sendUploadFailureMsg("上传失败 1-008");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 和彩云新建笔记
     */
    public static void mCloudCreateNote(NoteBean noteBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> maps = new HashMap<>();
                maps.put("notebook", "" + noteBean.notebook);//笔记所属笔记本名称
                maps.put("title", "" + noteBean.title);
                maps.put("content", "" + noteBean.content);
                maps.put("topmost", noteBean.topmost);
                maps.put("archived", noteBean.archived);
                maps.put("latlng", "");
                maps.put("location", "");
                maps.put("remindType", noteBean.remindType);
                maps.put("remindTime", "");
                maps.put("attachmentDirId", noteBean.catalogId);//附件目录id
                HttpUtils.doPostJson(MCloudConf.MCLOUD_OSE_URL + "/noteServer/openapi/note/create.do", getMCloudHeaders2(), new Gson().toJson(maps), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                        sendUploadFailureMsg("上传失败 1-020");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();
                            CaiyunBaseBean caiyunBaseBean = new Gson().fromJson(body, CaiyunBaseBean.class);
                            if (caiyunBaseBean != null && caiyunBaseBean.result == 0) {

                                //TODO 跳转彩云app
                                EventBusCarrier eventBusCarrier = new EventBusCarrier();
                                eventBusCarrier.setEventType(80014);
                                eventBusCarrier.setObject(response.body().string());
                                EventBusUtil.post(eventBusCarrier);
                            } else {
                                sendUploadFailureMsg("上传失败 1-022");
                            }
                        } else {
                            sendUploadFailureMsg("上传失败 1-021");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 和彩云操作接口：新建笔记
     */
    public static void mCloudOperCreateNote(MCloudNoteData noteBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpUtils.doPostJson(MCloudConf.MCLOUD_OSE_URL + "/noteServer/api/createNote.do", getMCloudHeaders2(), new Gson().toJson(noteBean), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                        sendUploadFailureMsg("上传失败 1-020");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();
                            CaiyunBaseBean caiyunBaseBean = new Gson().fromJson(body, CaiyunBaseBean.class);
                            if (caiyunBaseBean != null && caiyunBaseBean.result == 0) {

                                //TODO 跳转彩云app
                                EventBusCarrier eventBusCarrier = new EventBusCarrier();
                                eventBusCarrier.setEventType(80014);
//                                eventBusCarrier.setObject(response.body().string());
                                EventBusUtil.post(eventBusCarrier);
                            } else {
                                sendUploadFailureMsg("上传失败 1-022");
                            }
                        } else {
                            sendUploadFailureMsg("上传失败 1-021");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 和彩云操作接口：更新笔记
     */
    public static void mCloudOperUpdateNote(MCloudNoteData noteBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils.doPostJson(MCloudConf.MCLOUD_OSE_URL + "/noteServer/api/updateNote.do", getMCloudHeaders2(), new Gson().toJson(noteBean), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                        sendUploadFailureMsg("上传失败 1-020");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();
                            CaiyunBaseBean caiyunBaseBean = new Gson().fromJson(body, CaiyunBaseBean.class);
                            if (caiyunBaseBean != null && caiyunBaseBean.result == 0) {

                                //TODO 跳转彩云app
                                EventBusCarrier eventBusCarrier = new EventBusCarrier();
                                eventBusCarrier.setEventType(80014);
                                eventBusCarrier.setObject("");
                                EventBusUtil.post(eventBusCarrier);
                            } else {
                                sendUploadFailureMsg("上传失败 1-022");
                            }
                        } else {
                            sendUploadFailureMsg("上传失败 1-021");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 和彩云更新笔记
     */
    public static void mCloudUpdateNote(NoteBean noteBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> maps = new HashMap<>();
                maps.put("noteId", "" + noteBean.noteId);//笔记所属笔记本名称
                maps.put("notebook", "" + noteBean.notebook);//笔记所属笔记本名称
                maps.put("title", "" + noteBean.title);
                maps.put("content", "" + noteBean.content);
                maps.put("topmost", noteBean.topmost);
                maps.put("archived", noteBean.archived);
                maps.put("latlng", "");
                maps.put("location", "");
                maps.put("remindType", "0");
                maps.put("remindTime", "");
                maps.put("attachmentDirId", noteBean.catalogId);//附件目录id
                HttpUtils.doPostJson(MCloudConf.MCLOUD_OSE_URL + "/noteServer/openapi/note/update.do", getMCloudHeaders2(), new Gson().toJson(maps), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                        sendUploadFailureMsg("上传失败 1-014");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();
                            CaiyunBaseBean caiyunBaseBean = new Gson().fromJson(body, CaiyunBaseBean.class);
                            if (caiyunBaseBean != null && caiyunBaseBean.result == 0) {
                                //TODO 跳转彩云app
                                EventBusCarrier eventBusCarrier = new EventBusCarrier();
                                eventBusCarrier.setEventType(80014);
                                eventBusCarrier.setObject(response.body().string());
                                EventBusUtil.post(eventBusCarrier);
                            } else {
                                sendUploadFailureMsg("上传失败 1-016");
                            }
                        } else {
                            sendUploadFailureMsg("上传失败 1-015");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 和彩云新建笔记本
     */
    public static void mCloudCreateNoteBook(NoteBean noteBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> maps = new HashMap<>();
                maps.put("text", "" + noteBean.notebook);//笔记本名称
                maps.put("isDefault", 0);//0-普通笔记本，1-默认笔记本
                maps.put("parentName", "");//笔记本组名称
                HttpUtils.doPostJson(MCloudConf.MCLOUD_OSE_URL + "/noteServer/openapi/notebook/add.do", getMCloudHeaders2(), new Gson().toJson(maps), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                        sendUploadFailureMsg("上传失败 1-004");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();
                            CaiyunBaseBean caiyunBaseBean = new Gson().fromJson(body, CaiyunBaseBean.class);
                            if (caiyunBaseBean != null && caiyunBaseBean.result == 0) {
                                //创建笔记本成功，去获取笔记列表
//                                mCloudGetNoteList(noteBean);
                                NoteSummaryReq summaryReq = new NoteSummaryReq();
                                summaryReq.pageIndex = 1;//从第一条开始
                                summaryReq.pageSize = 100;//每页请求100
                                summaryReq.sortType = 0;//按实际排序
                                summaryReq.sort = 0;//倒序
                                mCloudGetNoteSummaryList(noteBean, summaryReq);
                            } else {
                                sendUploadFailureMsg("上传失败 1-006");
                            }
                        } else {
                            sendUploadFailureMsg("上传失败 1-005");
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 和彩云新token
     */
    public static void mCloudGetToken(OseUserInfoData oseUserInfoData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> headers = new HashMap<>();
                headers.put("APP_NUMBER", "" + oseUserInfoData.getAPP_NUMBER());//用户手机号码
                headers.put("NOTE_TOKEN", "" + oseUserInfoData.getAPP_AUTH());//鉴权后的字符串
                headers.put("APP_CP", "android");//所属渠道
                headers.put("CP_VERSION", "1.0");//渠道版本号
                HttpUtils.doPostJson(MCloudConf.MCLOUD_OSE_URL + "/noteServer/api/updateNoteToken.do", getMCloudHeaders2(), new Gson().toJson(headers), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {
                            String body = response.body().string();
                            CaiyunBaseBean caiyunBaseBean = new Gson().fromJson(body, CaiyunBaseBean.class);
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 和彩云新token
     */
    public static void mCloudResetToken(AASUserInfoData aasUserInfoData, LoginCallBack loginCallBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> maps = new HashMap<>();
                maps.put("userPhone", "" + aasUserInfoData.account);//用户手机号码
                maps.put("authToken", "" + aasUserInfoData.token);//和彩云token
                HttpUtils.doPostJson(MCloudConf.MCLOUD_OSE_URL + "/noteServer/api/authTokenRefresh.do", getMCloudHeaders2(), new Gson().toJson(maps), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("NetCommon", e.getMessage());
                        sendUploadFailureMsg("上传失败 1-017");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() != null) {

                            String body = response.body().string();
                            CaiyunBaseBean caiyunBaseBean = new Gson().fromJson(body, CaiyunBaseBean.class);
                            if (caiyunBaseBean != null && caiyunBaseBean.result == 0) {
                                //响应头
                                Headers rspHeaders = response.headers();
                                //遍历Header数组，并打印出来
                                Set<String> rspNames = rspHeaders.names();
//                            L.info("Http Rsp Header:");
                                OseUserInfoData oseUserInfoData = new OseUserInfoData();
                                for (String name : rspNames) {
                                    String value = rspHeaders.get(name);
                                    if ("APP_NUMBER".equals(name)) {
                                        oseUserInfoData.setAPP_NUMBER(value);
                                    } else if ("ERRORCODE".equals(name)) {
                                        oseUserInfoData.setERRORCODE(value);
                                    } else if ("isSwitch".equals(name)) {
                                        oseUserInfoData.setIsSwitch(value);
                                    } else if ("NOTE_TOKEN".equals(name)) {
                                        oseUserInfoData.setNOTE_TOKEN(value);
                                    } else if ("APP_AUTH".equals(name)) {
                                        oseUserInfoData.setAPP_AUTH(value);
                                    }
                                }
                                AppCommon.saveOseUserInfo(oseUserInfoData);

                                loginCallBack.success();
                            } else {
                                sendUploadFailureMsg("上传失败 1-019");
                            }
                        } else {
                            sendUploadFailureMsg("上传失败 1-018");
                        }
                    }
                });
            }
        }).start();
    }
}
