package org.dain.daydayup.concurrent.thread.future;

import java.util.concurrent.CountDownLatch;

/**
 * @Description 模拟Future的客户端
 * @Author lideyin
 * @Date 2019/8/21 22:50
 * @Version 1.0
 */
public class FutureClient {
    //使用countDownLatch对象来实现异步等待通知
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    //模拟Future的提交方法
    public Data submit(final String queryParam){
        //定义需要立即返回的future对象
        FutureData futureData = new FutureData(countDownLatch);
        //启子线程处理需要真实操作的业务
        new Thread(()->{
            RealData realData = new RealData(countDownLatch);
            //调用真实对象处理相关业务
            realData.processData(queryParam);
            //将真实对象的引用添加到future对象中
            futureData.setRealData(realData);
        }).start();
        return  futureData;
    }
}
