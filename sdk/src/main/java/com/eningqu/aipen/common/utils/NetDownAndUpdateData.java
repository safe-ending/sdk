//package com.eningqu.aipen.common.utils;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.os.Message;
//
//import com.blankj.utilcode.util.FileUtils;
//import com.blankj.utilcode.util.TimeUtils;
//import com.eningqu.aipen.common.AppCommon;
//import com.eningqu.aipen.common.Constant;
//import com.eningqu.aipen.db.model.NoteBookData;
//import com.eningqu.aipen.db.model.NoteBookData_Table;
//import com.eningqu.aipen.db.model.PageData;
//import com.eningqu.aipen.db.model.PageData_Table;
//import com.eningqu.aipen.db.model.PageLabelData;
//import com.google.gson.Gson;
//import com.raizlabs.android.dbflow.sql.language.SQLite;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Response;
//
//import static com.eningqu.aipen.common.AppCommon.DATE_FORMAT;
//import static com.eningqu.aipen.common.AppCommon.FILE_SEPARATOR;
//import static com.eningqu.aipen.common.AppCommon.NQ_SAVE_ROOT_PATH;
//
///**
// * 说明：
// * 作者：WangYabin
// * 邮箱：wyb@eningqu.com
// * 时间：15:09
// */
//public class NetDownAndUpdateData {
//    //TODO 数据结构需要修改，解析存在问题
//    /**
//     * 上传数据
//     * @param message 消息参数，里面存着一个bundle和一个dataList
//     */
//    public static void updateZip(Message message, final Context context) {
//        List<NoteBookData> dataList = (List<NoteBookData>) message.obj;
//        for (final NoteBookData bookData :dataList){
//            if (bookData.syncState == 0) {
//                final String noteBookId = bookData.notebookId;
//                final String creatTime = bookData.createTime;
//                List<PageData> pageData = SQLite.select(PageData_Table.id,
//                        PageData_Table.pageNum,
//                        PageData_Table.noteBookId,
//                        PageData_Table.noteType,
//                        PageData_Table.isLock,
//                        PageData_Table.lastModifyTime,
//                        PageData_Table.picUrl,
//                        PageData_Table.userUid)
//                        .from(PageData.class)
//                        .where(PageData_Table.noteBookId.eq(noteBookId),
//                                PageData_Table.isLock.eq(true),
//                                PageData_Table.userUid.eq(AppCommon.getUserUID()))
//                        .orderBy(PageData_Table.pageNum, true)
//                        .queryList();
//                if (pageData.size() > 0) {
//                    String path = pageData.get(0).picUrl;
//                    int index = path.lastIndexOf(FILE_SEPARATOR);
//                    final String paths = path.substring(0, index);
//                    int index2 = paths.lastIndexOf(FILE_SEPARATOR);
//                    final String paths2 = paths.substring(0, index2);
//                    int index3 = paths2.lastIndexOf(FILE_SEPARATOR);
//                    final String paths3 = paths.substring(0, index3);
//                    int index4 = paths3.lastIndexOf(FILE_SEPARATOR);
//                    final String name = paths.substring(index3, paths2.length() + 1);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                ZipUtil.zip(paths, paths2 + name + "jpg.zip");
//                                ZipUtil.zip(paths2 + FILE_SEPARATOR + "_txt", paths2 + name + "txt.zip");
//                                ZipUtil.zip(paths2 + name, paths2 + name.substring(1, name.length() - 1) + ".zip");
//                                String token = SpUtils.getString(context, SpUtils.LOGIN_TOKEN);
//                                Date date = TimeUtils.string2Date(creatTime);
//                                long currentMillis = TimeUtils.date2Millis(date);
//                                HttpUtils.doFile(AppCommon.BASE_URL + "api/pen-data/upload",
//                                        paths2 + name.substring(1, name.length() - 1) + ".zip",
//                                        name.substring(1, name.length() - 1)+"_"+bookData.noteName + ".zip",
//                                        token,currentMillis+"_"+bookData.noteType, new Callback() {
//                                            @Override
//                                            public void onFailure(Call call, IOException e) {
//                                                L.error("aaaaaaa", "上传失败");
//                                            }
//
//                                            @Override
//                                            public void onResponse(Call call, Response response) throws IOException {
////                                        L.error("aaaaaaa",response.body().string());
//                                                L.error("aaaaaaa", "上传成功");
//                                                ResponseMsg responseMsg = new ResponseMsg();
//                                                Gson gson = new Gson();
//                                                responseMsg = gson.fromJson(response.body().string(), ResponseMsg.class);
//                                                if (responseMsg.success&&response.code() == 1) {
//                                                    File fileJpg = new File(paths2 + name + "jpg.zip");
//                                                    File fileTxt = new File(paths2 + name + "txt.zip");
//                                                    File fileBase = new File(paths2 + name.substring(1, name.length() - 1) + ".zip");
//                                                    if (fileJpg.exists()){
//                                                        fileJpg.delete();
//                                                    }
//                                                    if (fileTxt.exists()){
//                                                        fileTxt.delete();
//                                                    }
//                                                    if (fileBase.exists()){
//                                                        fileBase.delete();
//                                                    }
//                                                    Message message1 = new Message();
//                                                    message1.what = Constant.ZIP_UPDATE_SUCCESS;
//                                                    Bundle bundle = new Bundle();
//                                                    bundle.putString("noteid", noteBookId);
//                                                    bundle.putString("notename", bookData.noteName);
//                                                    message1.obj = bundle;
//                                                    EventBusUtil.post(message1);
//                                                }
//                                            }
//                                        });
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
//                }
//            }
//        }
//    }
//    /**
//     * 下载数据，并且处理
//     * @param message 存着下载的信息
//     */
//    public static  void downZipFile(Message message) {
//        List<ResponseDownMsg.DataBean> list = new ArrayList<>();
//        list = (List<ResponseDownMsg.DataBean>) message.obj;
//        for (ResponseDownMsg.DataBean dataBean : list){
//            String createTimeAndType = dataBean.getBookNo();
//            String[] createTimeAndTypes = createTimeAndType.split("_");
//            String creteTime = createTimeAndTypes[0];
//            final String type = createTimeAndTypes[1];
//            creteTime = TimeUtils.millis2String(Long.parseLong(creteTime));
//            NoteBookData noteBookData = SQLite.select()
//                    .from(NoteBookData.class)
//                    .where(NoteBookData_Table.createTime.eq(creteTime))
//                    .querySingle();
//            if (noteBookData == null){
//                int index = dataBean.getBookName().lastIndexOf("_");
//                String noteName = dataBean.getBookName().substring(index);
//                int dotIndex = noteName.lastIndexOf(".");
//                String name = noteName.substring(1, dotIndex);
////                        String[] names = name.split(".");
////                        name = names[0];
//                noteBookData = new NoteBookData();
//                noteBookData.noteType = Integer.parseInt(type);
//                noteBookData.createTime = creteTime;
//                noteBookData.syncState=1;
//                noteBookData.isLock = true;
//                noteBookData.noteName = name;
//                noteBookData.userUid = AppCommon.getUserUID();
//                noteBookData.insert();
//
//                Date date = TimeUtils.string2Date(creteTime);
//                final String currentPath = TimeUtils.date2String(date, DATE_FORMAT) +"_"+ type;
//                FileUtils.createOrExistsDir(NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentPath+FILE_SEPARATOR+"_jpg");
//                final String finalCreteTime = creteTime;
//                HttpUtils.downFile(dataBean.getDownloadUrl(),new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                    }
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        InputStream is = null;
//                        byte[] buf = new byte[2048];
//                        int len = 0;
//                        FileOutputStream fos = null;
//                        try {
//                            is = response.body().byteStream();
//                            File file = new File(NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentPath+FILE_SEPARATOR+"_jpg", currentPath+".zip");
//                            fos = new FileOutputStream(file);
//                            while ((len = is.read(buf)) != -1) {
//                                fos.write(buf, 0, len);
//                            }
//                            fos.flush();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } finally {
//                            if (is != null) is.close();
//                            if (fos != null) fos.close();
//                        }
//                        inset2DataBase(currentPath, type, finalCreteTime);
//                    }
//
//                });
//
//            }
//        }
//    }
//
//    /**
//     * 下载完成之后解压，并且把需要的东西存入到数据库，下载的zip删除
//     * @param currentPath 本地保存的地址
//     * @param type 笔记本的类型
//     * @param finalCreteTime 笔记本创建的时间
//     */
//    private static void inset2DataBase(String currentPath, String type, String finalCreteTime) {
//        try {
//            ZipUtil.unzip(NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentPath+FILE_SEPARATOR+"_jpg"+FILE_SEPARATOR+
//                    currentPath+".zip", NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentPath+FILE_SEPARATOR+"_jpg");
//            PageData currentPageData = SQLite.select()
//                    .from(PageData.class)
//                    .where(PageData_Table.isLock.eq(true),
//                            PageData_Table.noteType.eq(Integer.parseInt(type)),
//                            PageData_Table.userUid.eq(AppCommon.getUserUID()),
//                            PageData_Table.picUrl.eq(NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentPath+FILE_SEPARATOR+"_jpg"))
//                    .querySingle();
//            //如果本地没有这个本的内容则创建一个
//            if (currentPageData ==null){
//                NoteBookData noteBookData1 = SQLite.select()
//                        .from(NoteBookData.class)
//                        .where(NoteBookData_Table.createTime.eq(finalCreteTime))
//                        .querySingle();
//                File file = new File(NQ_SAVE_ROOT_PATH + FILE_SEPARATOR + currentPath+FILE_SEPARATOR+"_jpg");
//                currentPageData = new PageData();
//                currentPageData.isLock = true;
//                currentPageData.noteBookId = noteBookData1.notebookId;
//                currentPageData.noteType = Integer.parseInt(type);
//                currentPageData.syncState = 1;
//                currentPageData.userUid = AppCommon.getUserUID();
//
//                if (file.isDirectory()){
//                    File[] files = file.listFiles();
//                    for (File fileItem : files){
//                        String fileName = fileItem.getName();
//                        if (fileName.contains("_")&&fileName.contains(".jpg")){
//                            currentPageData.picUrl = file.getPath() +FILE_SEPARATOR+ fileName;
//                            String[] numAndLables = fileName.split("_");
//                            String namePage = numAndLables[0];
//                            String labels = numAndLables[1];
//                            String[] names = namePage.split("\\.");
//                            String num = names[0];
//                            Bitmap bitmap = BitmapUtil.file2Bitmap(file.getPath() + FILE_SEPARATOR + fileName);
//                            currentPageData.data = BitmapUtil.bitmap2Bytes(bitmap);
//                            currentPageData.pageNum = Integer.parseInt(num);
//                            currentPageData.insert();
//                            bitmap.recycle();
//                            if (labels!=null){
//                                PageLabelData pageLabel = new PageLabelData();
//                                String pageid= currentPageData.pageId;
//                                pageLabel.setPageId(pageid);
//                                pageLabel.setLabelName(labels);
//                                pageLabel.insert();
//                            }
//
//                        }else if (fileName.contains(".jpg")){
//                            currentPageData.picUrl = file.getPath() +FILE_SEPARATOR+ fileName;
//                            String[] names = fileName.split("\\.");
//                            String num = names[0];
//                            Bitmap bitmap = BitmapUtil.file2Bitmap(file.getPath() + FILE_SEPARATOR + fileName);
//                            currentPageData.data = BitmapUtil.bitmap2Bytes(bitmap);
//                            currentPageData.pageNum = Integer.parseInt(num);
//                            currentPageData.insert();
//                            bitmap.recycle();
//                        }else if (fileName.contains(".zip")){
//                            File fileZip = new File(file.getPath() + FILE_SEPARATOR + fileName);
//                            if (fileZip.exists()){
//                                fileZip.delete();
//                            }
//                        }
//                        Message message = new Message();
//                        message.what = Constant.REFRESH_DATA;
//                        EventBusUtil.post(message);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
