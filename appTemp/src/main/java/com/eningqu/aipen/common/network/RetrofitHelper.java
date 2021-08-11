package com.eningqu.aipen.common.network;

import android.content.Context;

import com.eningqu.aipen.common.enums.LifeCycleEvent;
import com.eningqu.aipen.common.utils.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 说明：网络请求封装
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/4 15:05
 */

public class RetrofitHelper {

    private static Context mContext;

    //请求超时时长，单位秒
    private static final int DEFAULT_TIMEOUT = 10;

    private static Retrofit singleton;

    //初始化设置设置上下文
    public static void init(Context context) {
        mContext = context;
    }

    public static ApiService getApiService() {
        if (singleton == null) {
            synchronized (RetrofitHelper.class) {
                if (singleton == null) {

                    OkHttpClient.Builder client = new OkHttpClient.Builder();
                    //设置请求超时时长
                    client.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                    //启用Log日志
                    client.addInterceptor(getHttpLoggingInterceptor());
                    //设置缓存方式、时长、地址
                    client.addNetworkInterceptor(getCacheInterceptor());
                    client.addInterceptor(getCacheInterceptor());
                    client.cache(getCache());

                    singleton = new Retrofit.Builder()
                            .baseUrl(ApiService.API_SERVER_URL)
                            .addConverterFactory(GsonConverterFactory.create())//设置远程地址
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create().create())
                            .client(client.build())
                            .build();
                }
            }
        }
        return singleton.create(ApiService.class);
    }


    //提供Log日志插值器
    public static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    //提供缓存插值器
    public static Interceptor getCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                boolean netState = NetworkUtil.isMobileConnected(mContext) || NetworkUtil.isWifiConnected(mContext);

                //对request的设置是用来指定有网/无网下所走的方式

                //对response的设置是用来指定有网/无网下的缓存时长
                Request request = chain.request();
                if (!netState) {
                    //无网络下强制使用缓存，无论缓存是否过期,此时该请求实际上不会被发送出去。
                    //有网络时则根据缓存时长来决定是否发出请求
                    request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                }

                Response response = chain.proceed(request);
                if (netState) {
                    //有网络情况下，根据请求接口的设置，配置缓存。
                    //String cacheControl = request.cacheControl().toString();
                    //有网络情况下，超过1分钟，则重新请求，否则直接使用缓存数据
                    int maxAge = 60; //缓存一分钟
                    String cacheControl = "public,max-age=" + maxAge;
                    //当然如果你想在有网络的情况下都直接走网络，那么只需要
                    //将其超时时间maxAge设为0即可
                    return response.newBuilder().header("Cache-Control", cacheControl).removeHeader("Pragma").build();
                } else {
                    //无网络时直接取缓存数据，该缓存数据保存1周
                    int maxStale = 60 * 60 * 24 * 7 * 1;  //1周
                    return response.newBuilder().header("Cache-Control", "public,only-if-cached,max-stale=" + maxStale).removeHeader("Pragma").build();
                }
            }
        };
    }

    public static Interceptor getHeaderInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder builder = originalRequest.newBuilder();

                builder.header("timestamp", System.currentTimeMillis() + "");

                Request.Builder requestBuilder = builder.method(originalRequest.method(), originalRequest.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
    }

    //配置缓存
    public static Cache getCache() {
        File cacheFile = new File(mContext.getExternalCacheDir(), "HttpCache");//缓存地址
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); //大小50Mb
        return cache;
    }

    /**
     * 对observable进行统一转换（用于非文件下载请求）
     *
     * @param observable       被订阅者
     * @param observer         订阅者
     * @param lifecycleSubject 生命周期事件发射者
     */
    public static void composeToSubscribe(Observable observable, Observer observer, PublishSubject<LifeCycleEvent> lifecycleSubject) {
        //默认在进入DESTROY状态时发射一个事件来终止网络请求
        composeToSubscribe(observable, observer, LifeCycleEvent.DESTROY, lifecycleSubject);
    }

    /**
     * 对observable进行统一转换（用于非文件下载请求）
     *
     * @param observable       被订阅者
     * @param observer         订阅者
     * @param event            生命周期中的某一个状态，比如传入DESTROY，则表示在进入destroy状态时lifecycleSubject会发射一个事件从而终止请求
     * @param lifecycleSubject 生命周期事件发射者
     */
    public static void composeToSubscribe(Observable observable, Observer observer, LifeCycleEvent event, PublishSubject<LifeCycleEvent> lifecycleSubject) {
        observable.compose(getTransformer(event, lifecycleSubject)).subscribe(observer);
    }


    /**
     * 获取统一转换用的Transformer（用于非文件下载请求）
     *
     * @param event            生命周期中的某一个状态，比如传入DESTROY，则表示在进入destroy状态时
     *                         lifecycleSubject会发射一个事件从而终止请求
     * @param lifecycleSubject 生命周期事件发射者
     */
    public static <T> ObservableTransformer<T, T> getTransformer(final LifeCycleEvent event, final PublishSubject<LifeCycleEvent> lifecycleSubject) {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {

                //当lifecycleObservable发射事件时，终止操作。
                //统一在请求时切入io线程，回调后进入ui线程
                //加入失败重试机制（延迟3秒开始重试，重试3次）
                return upstream
                        .takeUntil(getLifeCycleObservable(event, lifecycleSubject))
                        .retryWhen(new RetryFunction(3, 3))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    //获取用于控制声明周期的Observable
    public static Observable<LifeCycleEvent> getLifeCycleObservable(final LifeCycleEvent event, PublishSubject<LifeCycleEvent> lifecycleSubject) {
        return lifecycleSubject.filter(new Predicate<LifeCycleEvent>() {
            @Override
            public boolean test(LifeCycleEvent lifeCycleEvent) throws Exception {
                //当生命周期为event状态时，发射事件
                return lifeCycleEvent.equals(event);
            }
        }).take(1);
    }


    //请求失败重试机制
    public static class RetryFunction implements Function<Observable<Throwable>, ObservableSource<?>> {

        private int retryDelaySeconds;//延迟重试的时间
        private int retryCount;//记录当前重试次数
        private int retryCountMax;//最大重试次数

        public RetryFunction(int retryDelaySeconds, int retryCountMax) {
            this.retryDelaySeconds = retryDelaySeconds;
            this.retryCountMax = retryCountMax;
        }

        @Override
        public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
            //使用全局变量来控制重试次数，重试3次后不再重试，通过代码显式回调onError结束请求
            return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                @Override
                public ObservableSource<?> apply(Throwable throwable) throws Exception {
                    //如果失败的原因是UnknownHostException（DNS解析失败，当前无网络），则没必要重试，直接回调error结束请求即可
                    if (throwable instanceof UnknownHostException) {
                        return Observable.error(throwable);
                    }
                    //没超过最大重试次数的话则进行重试
                    if (++retryCount <= retryCountMax) {
                        //延迟retryDelaySeconds后开始重试
                        return Observable.timer(retryDelaySeconds, TimeUnit.SECONDS);
                    }

                    return Observable.error(throwable);
                }
            });
        }
    }
}
