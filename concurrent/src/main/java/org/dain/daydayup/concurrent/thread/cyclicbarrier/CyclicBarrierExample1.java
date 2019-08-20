package org.dain.daydayup.concurrent.thread.cyclicbarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierExample1 {
	private static final int threadCount = 10;
//需要同步的线程数量
	private static CyclicBarrier cyclicBarrier = new CyclicBarrier(5);

	public static void main(String[] args) throws InterruptedException {
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		for (int i = 0; i < threadCount; i++) {
			final int threadNum = i;
			Thread.sleep(1000);
			threadPool.execute(() -> {
				try {
					test(threadNum);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
		threadPool.shutdown();

	}

	public static void test(int threadNum) throws InterruptedException, BrokenBarrierException {
		System.out.println("threadNum:" + threadNum + " is ready");
		try {
//			cyclicBarrier.await();
			cyclicBarrier.await(10000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			System.out.println("-----CyclicBarrierException------");
		}

		System.out.println("threadNum:" + threadNum + " is finish");
	}
}
