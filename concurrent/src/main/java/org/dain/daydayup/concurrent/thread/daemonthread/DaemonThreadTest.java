package org.dain.daydayup.concurrent.thread.daemonthread;

/**
 * @Description 守护线程的特征：如果所有的用户线程挂了，则守护线程也立马over.
 * @Author lideyin
 * @Date 2019/9/10 23:07
 * @Version 1.0
 */
public class DaemonThreadTest {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    System.out.println((i + 1) + Thread.currentThread().getName() + ": I'm free,I do nothing.Just see see.");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }, "t1");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    System.err.println((i + 1) + Thread.currentThread().getName() + ": I'm very busy...");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t2");
        //设置t1为守护线程,尝试改为非守护，则有不同效果
        t1.setDaemon(true);
        t1.start();
//        t2.setDaemon(true);
        t2.start();
        System.out.println("main thread is over." );
    }
}
