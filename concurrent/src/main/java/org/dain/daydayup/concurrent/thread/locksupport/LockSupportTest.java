package org.dain.daydayup.concurrent.thread.locksupport;

import java.util.concurrent.locks.LockSupport;

/**
 * @Description 测试LockSupport功能
 * LockSupport优点：
 * 1.不需要写在同步代码块中(即不需要加synchronized),也不需要维护一个共享的同步变量
 * 2.unpark函数可以先于park方法，即加锁和解锁方法没有先后顺序之分。
 * LockSupport与ReentryLock使用场景区分：
 * 简单的单个线程之间可以直接用LockSupport
 * 如果需要重入或需要根据condition条件加解多个锁时，可以考虑使用ReentryLock
 * @Author lideyin
 * @Date 2019/8/28 0:21
 * @Version 1.0
 */
public class LockSupportTest {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(()->{
            int sum=0;
            for (int i=0;i<10;i++){
                sum++;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LockSupport.park();
            System.out.println("最终结果："+sum);
        });
        t1.start();
        Thread.sleep(3000);
        //unpark需要指定具体的线程,unpark比park先执行或后执行结果都一样
        LockSupport.unpark(t1);
    }
}
