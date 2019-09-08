package org.dain.daydayup.concurrent.thread.blockingqueue;

import javax.swing.plaf.TableHeaderUI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @Description 使用delayQueue模拟10秒钟后，该睡觉了
 * @Author lideyin
 * @Date 2019/8/24 23:35
 * @Version 1.0
 */
public class DelayQueueTest {

    private static CountDownLatch cdl = new CountDownLatch(3);
    public static void main(String[] args) throws InterruptedException {
        ClockWorker worker = new ClockWorker();
        worker.start();
        Thread t1 = new Thread(() -> {
            //5秒后，叫醒dain1
            DelayClockEntity clock1 = new DelayClockEntity("dain1", System.currentTimeMillis() + 1000 * 5L);
            worker.addCustomClock(clock1);
            cdl.countDown();
        });
        Thread t2 = new Thread(() -> {
            //10秒后，叫醒dain2
            DelayClockEntity clock2 = new DelayClockEntity("dain2", System.currentTimeMillis() + 1000 * 10L);
            worker.addCustomClock(clock2);
            cdl.countDown();
        });
        Thread t3 = new Thread(() -> {
            //15秒后，叫醒dain3
            DelayClockEntity clock3 = new DelayClockEntity("dain3", System.currentTimeMillis() + 1000 * 15L);
            worker.addCustomClock(clock3);
            cdl.countDown();
        });
        t1.start();
        t2.start();
        t3.start();

        cdl.await();
        System.out.println("已成功添加进队列,time=" + System.currentTimeMillis());

        System.out.println("当前队列数量为："+worker.getDelayQueue().size());
        DelayClockEntity entity = worker.getDelayQueue().poll();
        //试试看能不能拿到队列的数量
        if (entity==null){
            System.out.println("是否能立即取到队列的数据：NO");
        }

    }

}

class ClockWorker extends Thread {
    private DelayQueue<DelayClockEntity> delayQueue = new DelayQueue();

    public void addCustomClock(DelayClockEntity delayClock) {
        this.delayQueue.put(delayClock);
        System.out.println("now delayQueue.size=" + delayQueue.size());
    }

    public DelayQueue<DelayClockEntity>  getDelayQueue(){
        return delayQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("队列数量剩余："+delayQueue.size());
                //如果没有取到数据，则等待
                DelayClockEntity pollClock = delayQueue.take();
                System.out.println("dear " + pollClock.getName() + ",it's time to go to bed now. time=" +
                        System.currentTimeMillis());
                if (delayQueue.size() == 0) {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class DelayClockEntity implements Delayed {
    //姓名
    private String name;
    //闹钟响起的时间
    private Long ringTime;

    public DelayClockEntity() {
    }

    public DelayClockEntity(String name, Long ringTime) {
        this.name = name;
        this.ringTime = ringTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRingTime() {
        return ringTime;
    }

    public void setRingTime(Long ringTime) {
        this.ringTime = ringTime;
    }

    //判断是否到了指定延迟的时间
    @Override
    public long getDelay(TimeUnit unit) {
        return ringTime - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        DelayClockEntity dc1 = (DelayClockEntity) o;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        return this.getDelay(timeUnit) - dc1.getDelay(timeUnit) > 0 ? 0 : 1;
    }
}

