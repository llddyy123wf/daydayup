package org.dain.daydayup.concurrent.thread.homework;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Description 线程工具类
 * @Author lideyin
 * @Date 2019/9/9 23:08
 * @Version 1.0
 */
public class ThreadUtil {
    //设置最大线程数
    private static final int maxThreadCount;
    //等待队列
    private static final ArrayBlockingQueue arrayBlockingQueue;

    static {
        maxThreadCount=Runtime.getRuntime().availableProcessors();
        arrayBlockingQueue=new ArrayBlockingQueue(maxThreadCount);
    }
    public static SimpleThreadPoolExecutor createThreadPool(int coreThreadSize, int maxThreadSize){
//        ThreadPoolExecutor
        return null;
    }


}
