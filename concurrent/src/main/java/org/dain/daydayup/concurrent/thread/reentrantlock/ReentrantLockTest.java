package org.dain.daydayup.concurrent.thread.reentrantlock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 对ReentryLock及及相关知识的测试了解，顺便好好研究一下核心的AQS...
 * 1.可重入锁，同一线程可以多次(n次)获取锁，所以也必须多次(n次)释放
 * 2.支持两种锁：公平锁和非公平锁。针对获取锁的顺序而言的
 * a.公平锁（FairSync）：锁的获取顺序符合请求上的绝对时间顺序，满足FIFO
 * b.非公平锁(NonFairSync)：锁的获取顺序不一定和请求顺序一致
 * 3.AbstractQueuedSynchronizer(AQS) ：抽象的队列式同步器
 * a.AQS定义了一套多线程访问共享资源的同步器框架，许多同步类的实现都依赖于它，如：ReentryLock/Semaphore/CountDownLatch
 * b.它维护了一个volatile int state(代表共享资源)
 * AQS
 * @Author lideyin
 * @Date 2019/8/30 8:54
 * @Version 1.0
 */
public class ReentrantLockTest {
    private ReentrantLock reentrantLock = new ReentrantLock();

    public void method() {
        reentrantLock.lock();
        try {
            System.out.println("线程"+Thread.currentThread().getName()+"进入...");
            Thread.sleep(2000);
            System.out.println("线程"+Thread.currentThread().getName()+"退出...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }

    public static void main(String[] args) {
    	ReentrantLockTest test =new ReentrantLockTest();
        Thread t1 = new Thread(()->{
            test.method();
        });
        Thread t2 = new Thread(()->{
            test.method();
        });
        t1.start();
        t2.start();
    }
}
