package org.dain.daydayup.concurrent.thread.masterworker;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class MyWorker implements Runnable {

	private ConcurrentLinkedQueue<Task> taskQueue;
	private ConcurrentHashMap<String, Object> workerResultMap;
	private CountDownLatch ctl;

	@Override
	public void run() {
		while (true) {
			Task task = taskQueue.poll();
			if (task == null) {
				break;
			}
			Object res =handleTask(task);
			workerResultMap.put(Integer.toString(task.getId()), res);
			this.ctl.countDown();
			System.out.println("完成一个活，ctl.count="+ctl.getCount());
		}

	}

	Random rdm = new Random();

	private Object handleTask(Task task) {
		// 模拟执行时间
		try {
			Thread.sleep(100 * rdm.nextInt(10));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return task.getCount();
		
	}

	public void setTaskQueue(ConcurrentLinkedQueue<Task> taskQueue) {
		this.taskQueue = taskQueue;
	}

	public void setWorkerResult(ConcurrentHashMap<String, Object> workerResultMap) {
		this.workerResultMap = workerResultMap;
	}

	public void setCountDownLatch(CountDownLatch ctl) {
		this.ctl=ctl;
		
	}

}
