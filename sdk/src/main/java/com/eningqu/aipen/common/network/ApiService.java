package com.eningqu.aipen.common.network;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 说明：网络请求接口定义
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/4 15:04
 */

public interface ApiService{

    String API_SERVER_URL = "https://api.douban.com/v2/";

    //@Headers("")
    @GET("book/search")
    Observable<String> getSearchBooks(@Query("q") String name,
                                      @Query("tag") String tag,
                                      @Query("start") int start,
                                      @Query("count") int count);

}
