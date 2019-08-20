package org.dain.daydayup.concurrent.thread.wait;

public class Add {
	private String lock;

	public Add(String lock) {
		this.lock = lock;
	}
	public void add() {
		synchronized (lock) {
			System.out.println("threadName:"+Thread.currentThread().getName()+" start add");
			Constant.list.add("test");
			lock.notifyAll();
			System.out.println("threadName:"+Thread.currentThread().getName()+" end add and notify all");
		}
	}
}
