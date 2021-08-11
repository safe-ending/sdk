package com.eningqu.aipen.common.observer;

import com.eningqu.aipen.common.response.HttpResponse;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/17 19:56
 */

public abstract class HttpObserver<T> implements Observer<HttpResponse<T>> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(HttpResponse<T> httpResponse) {
        //做一些回调后需统一处理的事情
        //如请求回来后，先判断token是否失效
        //如果失效则直接跳转登录页面
        //...

        //如果没失效，则正常回调
        onSuccess(httpResponse.getData());
    }

    @Override
    public void onError(Throwable e) {
        onFail(e.getMessage());
    }

    @Override
    public void onComplete() {

    }

    //具体实现下面两个方法，便可从中得到更直接详细的信息
    public abstract void onSuccess(T t);

    public abstract void onFail(String error);
}