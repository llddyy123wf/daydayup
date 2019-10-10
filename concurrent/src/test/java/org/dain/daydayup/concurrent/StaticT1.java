package org.dain.daydayup.concurrent;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description TODO
 * @Author lideyin
 * @Date 2019/9/12 9:55
 * @Version 1.0
 */
public class StaticT1 {
    public static void main(String[] args) throws InterruptedException {
    	System.err.println("OriginalError:"+StaticHolder.getError());
        System.out.println("OriginalSuccess:"+StaticHolder.getSuccess());
    	new Thread(()->{
    	    StaticHolder.increaseError();
    	    StaticHolder.increaseSuccess();
        }).start();
        new Thread(()->{
            StaticHolder.increaseError();
            StaticHolder.increaseSuccess();
        }).start();
        Thread.sleep(1000);
        System.err.println("add2:"+StaticHolder.getError());
        System.out.println("add2:"+StaticHolder.getSuccess());
        new Thread(()->{
            StaticHolder.decreaseError();
            StaticHolder.decreaseSuccess();
        }).start();
        Thread.sleep(1000);
        System.err.println("minus1:"+StaticHolder.getError());
        System.out.println("minus1:"+StaticHolder.getSuccess());
//        ThreadPoolExecutor
    }
}
