package org.dain.daydayup.concurrent.thread.interrupt;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class InterruptException {
	private static ReentrantLock lock = new ReentrantLock();
	private static Condition condition = lock.newCondition();

	public static void main(String[] args) {
		Thread t1 = new Thread(() -> {
			try {
				lock.lock();
				System.out.println("begin wait.");
				condition.awaitUninterruptibly();
//				condition.await();
			} /*catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} */finally {
				lock.unlock();
				System.out.println("unlock");
			}
		});
//		t1.setDaemon(true);
		t1.start();
		try {
			System.out.println("111");
			Thread.sleep(3000);
			System.out.println("222");
			t1.interrupt();
			lock.lock();
			condition.signalAll();
			lock.unlock();
			System.out.println("333");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
