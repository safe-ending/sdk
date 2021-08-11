package com.eningqu.aipen.base;

import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/1/10 10:26
 */

public class BaseHandler<T> extends Handler{

    protected WeakReference<T> ref;

    public BaseHandler(T cls){
        ref = new WeakReference<T>(cls);
    }

    public T getRef(){
        return ref != null ? ref.get() : null;
    }


}
