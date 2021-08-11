package com.eningqu.aipen.common.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wtj
 * @filename SingleThreadExecutorUtils
 * @date 2019/9/23
 * @email wtj@eningqu.com
 **/
public class SingleThreadExecutorUtils {

    private final static ExecutorService singleThreadPool;

    static {
        singleThreadPool = Executors.newSingleThreadExecutor();
    }

    public static ExecutorService getThreadPool(){
        return singleThreadPool;
    }



}
