package com.eningqu.aipen.common.network;

import android.content.Context;
import android.util.Log;

import com.eningqu.aipen.common.utils.L;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http2.Header;
import okio.Buffer;

/**
 * author : LiQiu
 * e-mail : lq@eningqu.com
 * date   : 2021/7/8 10:11
 * desc   :
 * version: 1.0
 */
public class LoggingInterceptor implements Interceptor {
    Context context;

    public LoggingInterceptor(Context mcontext) {

        this.context = mcontext;
    }

    public LoggingInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        //处理请求报文打印
        L.debug(request.toString());
        String method = request.method();
        //获取请求头，并用Header数组接收
        Headers reqHeaders = request.headers();
        MediaType contentType = null;
        if(null!=request.body()){
            contentType = request.body().contentType();
        }
        //遍历Header数组，并打印出来
        Set<String> names = reqHeaders.names();
        L.info("Http Request Headers:");
        for (String name : names) {
            String value = reqHeaders.get(name);
            L.info(name + "=" + value);
        }
        L.info("Http Request Body:");
        //重点部分----------针对post请求做处理-----------------------
        if ("POST".equals(method)) {//post请求需要拼接
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < body.size(); i++) {
                    sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                }
                sb.delete(sb.length() - 1, sb.length());
                L.info("请求报文： RequestParams:{" + sb.toString() + "}");
            } else if (request.body() instanceof RequestBody) {
                RequestBody requestBody = (RequestBody) request.body();
                Buffer bufferedSink = new Buffer();
                requestBody.writeTo(bufferedSink);
                Charset charset = Charset.forName("utf-8");
                String str = bufferedSink.readString(charset);

                if(null!=contentType && (!contentType.toString().contains("octet-stream") &&
                        !contentType.toString().contains("zip"))){

                    L.info("请求报文： RequestParams:" + str);
                }
            }
        } else {//get请求直接打印url
            if(null!=request.body()){

                L.info("request params==" + request.url() + "\n 参数==" + request.body().toString());
            }
        }

        long startTime = System.currentTimeMillis();
        Response response = chain.proceed(request);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        L.debug("----------END---------" + duration + "毫秒----");

        //处理响应报文打印
        contentType = null;
        String bodyString = null;
        if (response.body() != null) {
            contentType = response.body().contentType();
            bodyString = response.body().string();
        }


        //响应头
        Headers rspHeaders = response.headers();

        //遍历Header数组，并打印出来
        Set<String> rspNames = rspHeaders.names();
        L.info("Http Response Header:");
        for (String name : rspNames) {
            String value = rspHeaders.get(name);
            L.info(name + "=" + value);
        }

        if(null!=contentType && (!contentType.toString().contains("octet-stream") &&
                !contentType.toString().contains("zip"))){

            L.debug("Http Response Body: " + response.code() + "\n" + bodyString);
        }
        if (response.body() != null) {// 深坑！打印body后原ResponseBody会被清空，需要重新设置body
            ResponseBody body = ResponseBody.create(contentType, bodyString);
            return response.newBuilder().body(body).build();
        } else {
            return response;
        }
    }
}
