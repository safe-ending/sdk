package com.eningqu.aipen.common.utils;

import android.content.Context;
import android.util.Base64;

import com.eningqu.aipen.BuildConfig;
import com.eningqu.aipen.common.network.LoggingInterceptor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 说明：
 * 作者：WangYabin
 * 邮箱：wyb@eningqu.com
 * 时间：15:35
 */
public class HttpUtils {
    private static OkHttpClient client = null;

    private static Context mContext;

    private HttpUtils() {
    }

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public static OkHttpClient getInstance() {
        if (client == null) {
            synchronized (HttpUtils.class) {
                if (client == null) {
                    client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
//                            .addInterceptor(new LoggingInterceptor(mContext))
                            .addInterceptor(new HttpLoggingInterceptor()
                            .setLevel(BuildConfig.DEBUG ?
                                    HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                            .build();
                }
            }
        }
        return client;
    }

    /**
     * Get请求
     *
     * @param url
     * @param callback
     */
    public static void doGet(String url, String token, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("auth_token", token)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    public static void doGet(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    /**
     * Post请求发送键值对数据
     *
     * @param url
     * @param mapParams
     * @param callback
     */
    public static void doPost(String url, Map<String, String> mapParams, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : mapParams.keySet()) {
            builder.add(key, mapParams.get(key));
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    /**
     * Post请求发送JSON数据
     *
     * @param url
     * @param jsonParams
     * @param callback
     */
    public static void doPost(String url, String jsonParams, Callback callback) {
        Map<String, String> map = new HashMap<>();
        map.put("dataJson", jsonParams);
        //处理参数
        FormBody.Builder builder = new FormBody.Builder();
        addParams(builder, map);
        FormBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    private static void addParams(FormBody.Builder builder, Map<String, String> params) {
        if (params != null) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
    }

    private static void addHeaderParams(Request.Builder builder, Map<String, String> params) {
        if (params != null) {
            for (String key : params.keySet()) {
                builder.addHeader(key, params.get(key));
            }
        }
    }

    /**
     * 上传文件
     *
     * @param url
     * @param pathName
     * @param fileName
     * @param callback
     */
    public static void doFile(String url, String pathName, String fileName, String token, String bookNo, Callback callback) {
        //判断文件类型
        //        bookNo = bookNo.replace(" ","").replace("-", "").replace(":", "");
        MediaType MEDIA_TYPE = MediaType.parse(judgeType(pathName));
        Map<String, String> map = new HashMap<>();
        //        map.put("hashValue", getFileMD5(new File(pathName)));
        map.put("bookName", fileName);
        map.put("bookNo", bookNo);
        File file = new File(pathName);

        MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        multipartBody.addFormDataPart("zipFile", fileName, RequestBody.create(MEDIA_TYPE, file));
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (String key : map.keySet()) {
                multipartBody.addFormDataPart(key, map.get(key));
            }
        }
        //发出请求参数
        Request request = new Request.Builder()
                .header("auth_token", token)
                .url(url)
                .post(multipartBody.build())
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    /**
     * 根据文件路径判断MediaType
     *
     * @param path
     * @return
     */
    public static String judgeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 下载文件
     *
     * @param url
     */
    public static void downFile(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    public interface DownloadListener {
        void onProgress(long count, int progress);

        void onFailure(String message);
    }

    /**
     * 下载文件
     *
     * @param url
     */
    public static void downFile(String url, String fileDir, String fileName, DownloadListener downloadListener) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                if (null != downloadListener) {
                    downloadListener.onFailure("download failure message:" + e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[512];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(fileDir, fileName);
                    fos = new FileOutputStream(file);
                    //---增加的代码---
                    //计算进度
                    long totalSize = response.body().contentLength();
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        sum += len;
                        //progress就是进度值
                        int progress = (int) (sum * 1.0f / totalSize * 100);
                        //---增加的代码---
                        fos.write(buf, 0, len);

                        if (null != downloadListener) {
                            downloadListener.onProgress(sum, progress);
                        }
                    }
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (null != downloadListener) {
                        downloadListener.onFailure("download failure, Please retry it!");
                    }
                } finally {
                    if (is != null) is.close();
                    if (fos != null) fos.close();
                }
            }
        });
    }

    public static void doPut(String jsonParams, Callback callback) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonParams);

        Request request = new Request.Builder()
                .addHeader("Ocp-Apim-Subscription-Key", "4954e82f0439414493c357ceb7952135")
                .url("https://position-ocr.cognitiveservices.azure.com/inkrecognizer/v1.0-preview/recognize")
                .put(body).build();
        getInstance().newCall(request)
                .enqueue(callback);
    }

    public static void doHanvonPost(String lang, String jsonParams, Callback callback) {
        MediaType JSON = MediaType.parse("application/octet-stream; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonParams);

        String code = "d4b92957-78ed-4c52-a004-ac3928b054b5";

        if (lang.equals("chns")) {
            code = "d4b92957-78ed-4c52-a004-ac3928b054b5";
        } else if (lang.equals("chnt")) {
            code = "05a7d172-ad21-4749-be0f-bfa4166d4da0";
        } else if (lang.equals("en")) {
            code = "f01d64a2-bd96-4554-8bcc-81d221f314a4";
        } else if (lang.equals("ja")) {
            code = "de1303e1-7656-45e9-80dd-6e4c225a81d7";
        }

        String url = "https://api.hanvon.com/rt/ws/v1/hand/line?key="
                + "03663e70-d78f-4bf6-ba8e-69b2088af321"
                + "&code=" + code;
        Request request = new Request.Builder()
//                .addHeader("key", "03663e70-d78f-4bf6-ba8e-69b2088af321")
//                .addHeader("code", code)
                .url(url)
                .post(body).build();
        getInstance().newCall(request)
                .enqueue(callback);
    }

    public static void doNingQuPost(String jsonParams, Callback callback) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonParams);

        String url = "http://admin.eningqu.com/hwr/recognize";
        Request request = new Request.Builder()
                .url(url)
                .post(body).build();
        getInstance().newCall(request)
                .enqueue(callback);
    }


    public static void doPostOctetStream(String url, Map<String, String> headers, String xmlStr, Callback callback) {
        //处理参数
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), xmlStr);
        Request.Builder builder = new Request.Builder();
        headers.put("Content-Type", "application/octet-stream");
        addHeaderParams(builder, headers);
        Request request = builder
//                .addHeader("Content-Type", "application/octet-stream")
                .url(url)
                .post(body).build();
        getInstance().newCall(request)
                .enqueue(callback);
    }

    /**
     * 字节流上传文件
     *
     * @param url
     * @param headers
     * @param fileName
     * @param fileBytes
     * @param callback
     */
    public static void uploadFileBinaryStream(String url, Map<String, String> headers, String fileName, byte[] fileBytes, Callback callback) {
        //创建File
//        File file = new File(filePath);
        //创建RequestBody
        RequestBody body = null;
        try {
            body = RequestBody.create(MediaType.parse("application/octet-stream;name=" + fileName + ""), fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Request.Builder builder = new Request.Builder();
        //处理参数
//        headers.put("Content-Type", "binary/octet-stream");
        addHeaderParams(builder, headers);
        //创建Request
        Request request = builder
//                .addHeader("Content-Type", "binary/octet-stream")
                .url(url)
                .post(body).build();
        getInstance().newCall(request)
                .enqueue(callback);
    }

    public static void doPostXml(String url, Map<String, String> headers, String xmlStr, Callback callback) {
        //处理参数
        RequestBody body = RequestBody.create(MediaType.parse("application/xml"), xmlStr);
        Request.Builder builder = new Request.Builder();
        headers.put("Content-Type", "text/xml;charset=utf-8");
        addHeaderParams(builder, headers);
        Request request = builder.url(url).post(body).build();
        getInstance().newCall(request)
                .enqueue(callback);
    }

    public static void doPostJson(String url, Map<String, String> headers, String jsonStr, Callback callback) {
        //处理参数
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonStr);
        Request.Builder builder = new Request.Builder();
        headers.put("Content-Type", "text/json;charset=utf-8");
        addHeaderParams(builder, headers);
        Request request = builder.url(url).post(body).build();
        getInstance().newCall(request)
                .enqueue(callback);
    }

    /**
     * 上传文件
     *
     * @param url
     * @param pathName
     * @param fileName
     * @param callback
     */
    public static void doFileXml(String url, String pathName, String fileName, Map<String, String> map, Callback callback) {
        //判断文件类型
        MediaType MEDIA_TYPE = MediaType.parse(judgeType(pathName));
        File file = new File(pathName);

        MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        multipartBody.addFormDataPart("", fileName, RequestBody.create(MEDIA_TYPE, file));
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (String key : map.keySet()) {
                multipartBody.addFormDataPart(key, map.get(key));
            }
        }
        //发出请求参数
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody.build())
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }


}
