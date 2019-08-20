package org.dain.daydayup.concurrent.thread.countdowlatch;

import java.util.concurrent.CountDownLatch;
class Foo {
    private CountDownLatch countDownLatch2=new CountDownLatch(1);
    private CountDownLatch countDownLatch3=new CountDownLatch(1);
    public Foo() {
        
    }

    public void first(Runnable printFirst) throws InterruptedException {
        
        // printFirst.run() outputs "first". Do not change or remove this line.
        printFirst.run();
        countDownLatch2.countDown();
    }

    public void second(Runnable printSecond) throws InterruptedException {
        
        countDownLatch2.await();
        // printSecond.run() outputs "second". Do not change or remove this line.
        printSecond.run();
        countDownLatch3.countDown();
    }

    public void third(Runnable printThird) throws InterruptedException {
        
        countDownLatch3.await();
        // printThird.run() outputs "third". Do not change or remove this line.
        printThird.run();
        
        
    }

}

