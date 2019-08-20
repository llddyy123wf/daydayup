package org.dain.daydayup.concurrent.thread.masterworker;

import java.util.concurrent.CountDownLatch;

public class MainTest {
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Runtime.getRuntime().availableProcessors()=" + Runtime.getRuntime().availableProcessors());
		int loopSize=5;
		CountDownLatch ctl = new CountDownLatch(loopSize);
		ComputeMaster master = new ComputeMaster(ctl,new MyWorker(), 
				Runtime.getRuntime().availableProcessors());
		for (int i = 0; i < loopSize; i++) {
			master.addTask(new Task(i, i));
		}
		master.execute();
		System.out.println("开始等待。。。");
		ctl.await();
		System.out.println("over,result ="+master.getResut());
	}
}
