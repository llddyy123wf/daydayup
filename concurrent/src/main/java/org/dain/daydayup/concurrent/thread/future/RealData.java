package org.dain.daydayup.concurrent.thread.future;

import java.util.concurrent.CountDownLatch;

/**
 * @Description 真正处理数据的对象
 * @Author lideyin
 * @Date 2019/8/21 22:50
 * @Version 1.0
 */
public class RealData implements Data {

    private CountDownLatch countDownLatch;
    private String result;

    public RealData(CountDownLatch countDownLatch) {
        this.countDownLatch=countDownLatch;
    }

    @Override
    public String getData() {
        return result;
    }

    //模拟处理相关业务
    public void processData(String queryPara){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.result=queryPara+" 100 .";
        this.countDownLatch.countDown();
    }
}
