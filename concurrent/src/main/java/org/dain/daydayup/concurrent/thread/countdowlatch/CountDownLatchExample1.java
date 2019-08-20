package org.dain.daydayup.concurrent.thread.countdowlatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchExample1 {
	private static final int threadCount = 550;

	public static void main(String[] args) throws InterruptedException {
		ExecutorService threadPool = Executors.newFixedThreadPool(300);
		final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		for (int i = 0; i < threadCount; i++) {
			final int threadNum = i;
			threadPool.execute(()->{
				try {
					test(threadNum);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					countDownLatch.countDown();
				}
			});
		}
		countDownLatch.await();
		threadPool.shutdown();
		System.out.println("finish.");
	}
	public static void test(int threadNum) throws InterruptedException {
		Thread.sleep(1000);
		System.out.println("threadNum:"+threadNum);
		Thread.sleep(1000);
	}
}
