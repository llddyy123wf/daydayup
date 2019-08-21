package org.dain.daydayup.concurrent.thread.future;

import org.dain.daydayup.concurrent.thread.countdowlatch.CountDownLatchExample1;

import java.util.concurrent.CountDownLatch;

/**
 * @Description 模拟Future数据对象
 * @Author lideyin
 * @Date 2019/8/21 22:48
 * @Version 1.0
 */
public class FutureData implements Data{
    private CountDownLatch countDownLatch;
    private RealData realData;

    public FutureData(CountDownLatch countDownLatch) {
        this.countDownLatch=countDownLatch;
    }

    @Override
    public String getData() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return realData.getData();
    }

    public void setRealData(RealData realData) {
        this.realData=realData;
    }
}
