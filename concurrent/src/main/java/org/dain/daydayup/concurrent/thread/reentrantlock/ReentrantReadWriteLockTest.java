package org.dain.daydayup.concurrent.thread.reentrantlock;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * @Description
 *  实现读写分离的锁
 *  读读共享，读写互斥，写写互斥
 * @Author lideyin
 * @Date 2019/9/8 22:08
 * @Version 1.0
 */
public class ReentrantReadWriteLockTest {
    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private ReadLock readLock = rwLock.readLock();
    private WriteLock writeLock = rwLock.writeLock();

    private void read() {
        readLock.lock();
        try {
            System.out.println("当前线程："+Thread.currentThread().getName()+" 进入了读方法");
            Thread.sleep(2000);
            System.out.println("当前线程："+Thread.currentThread().getName()+" 退出了读方法");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }

    }

    private void write() {
        writeLock.lock();
        try {
            System.out.println("当前线程："+Thread.currentThread().getName()+" 进入了写方法");
            Thread.sleep(2000);
            System.out.println("当前线程："+Thread.currentThread().getName()+" 退出了写方法");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }
    
    public static void main(String[] args) {
        ReentrantReadWriteLockTest rwTest = new ReentrantReadWriteLockTest();
        Thread t1 = new Thread(()->{
            rwTest.read();//读
        },"t1");
        Thread t2 = new Thread(()->{
            rwTest.read();//读
        },"t2");
        Thread t3 = new Thread(()->{
            rwTest.write();//写
        },"t3");
        //分别才能不同的两组线程，会发现读读共享，读写互斥，写写互斥（懒得写）
        t1.start();
        //t2.start();
        t3.start();
    }
}
