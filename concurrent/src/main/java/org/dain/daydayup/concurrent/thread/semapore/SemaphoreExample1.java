package org.dain.daydayup.concurrent.thread.semapore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class SemaphoreExample1 {
	// 定义启动的线程数量
	private static final int threadCount = 5;
	// 定义信号量的大小
	private static Semaphore semaphore = new Semaphore(4);

	// 定义信号量的大小后，配合acquire()来使用，即一次最多只能执行这定义数量的线程
//	private static final Semaphore semaphore = new Semaphore(10);
	public static void main(String[] args) throws InterruptedException {
		// 定义固定大小为100的线程池
		ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
		for (int i = 0; i < threadCount; i++) {
			final int threadNumber = i;

			threadPool.execute(() -> {
				try {
					test(threadNumber);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

		}
		threadPool.shutdown();
	}

	private static void test(int threadNumber) throws InterruptedException {
		try {
			semaphore.acquire(2);
			System.out.println("test thread " + threadNumber + " started." + System.currentTimeMillis());

			System.out.println("the left semaphore is availablePermits= " + semaphore.availablePermits()
					+ " semaphore.getQueueLength= " + semaphore.getQueueLength() + " drainPermits = "
					+ semaphore.drainPermits());

			Thread.sleep(2000);

			System.err.println("test thread " + threadNumber + " end." + System.currentTimeMillis());
		} finally {
			semaphore.release(2);
		}
	}

}
