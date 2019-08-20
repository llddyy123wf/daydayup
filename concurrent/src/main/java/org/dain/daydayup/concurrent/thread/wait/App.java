package org.dain.daydayup.concurrent.thread.wait;

public class App {
	public static void main(String[] args) throws InterruptedException {
		String lock = new String("");
		Add add = new Add(lock);
		Subtract subtract=new Subtract(lock);
		for(int i=0;i<2;i++) {
			Thread t1=new Thread(()->{
				subtract.subtract();
			}) ;
			t1.setName("subtract_t"+i);
			t1.start();
		}
		Thread.sleep(1000);
		for(int i=0;i<1;i++) {
			Thread t1=new Thread(()->{
				add.add();
			});
			t1.setName("add_t"+i);
			t1.start();
		}
	}
}
