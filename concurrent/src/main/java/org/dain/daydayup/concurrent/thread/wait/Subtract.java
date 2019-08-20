package org.dain.daydayup.concurrent.thread.wait;

public class Subtract {
	private String lock;

	public Subtract(String lock) {
		this.lock = lock;
	}

	public void subtract() {
		try {
			synchronized (lock) {
				if (Constant.list.size() == 0) {
					System.out.println("wait begin ,threadName=" + Thread.currentThread().getName());
					lock.wait();
					System.out.println("wait end ,threadName=" + Thread.currentThread().getName());
				}
				Constant.list.remove(0);
				System.out.println("list.size=" + Constant.list.size());
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
