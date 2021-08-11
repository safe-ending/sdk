package com.eningqu.aipen.common.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 说明：
 * 作者：Yanghuangping
 * 邮箱：yhp@eningqu.com
 * 时间：2018/3/20 14:08
 */

public class ThreadPoolUtils {

    private final static ExecutorService fixedThreadPool;

    static {
        fixedThreadPool = Executors.newFixedThreadPool(5);
    }

    public static ExecutorService getThreadPool(){
        return fixedThreadPool;
    }
}
