package org.dain.daydayup.concurrent.thread.masterworker;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * boss
 * 
 * @author lideyin
 * @date 2019年8月13日 上午7:14:55
 * @description
 */
public class ComputeMaster {

	private int id;
	// 盛放所有需要执行的任务队列
	ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<>();
	// 存储需要执行工作线程
	private HashMap<String, Thread> workerThreadMap = new HashMap<>();
	// 存储需要各个线程执行的子线程的任务的结果
	private ConcurrentHashMap<String, Object> workerResultMap = new ConcurrentHashMap<>();

//	worker的数量，默认为java虚拟机的处理器数量
//	private int workerCount =Runtime.getRuntime().availableProcessors();
	// 构造函数初始化worker及其数量
	public ComputeMaster(CountDownLatch ctl, MyWorker worker, int workerCount) {
		// worker里添加所有执行任务队列的引用
		worker.setTaskQueue(taskQueue);
		// worker里添加任务执行结果Map的引用
		worker.setWorkerResult(workerResultMap);
		// worker里添加
		worker.setCountDownLatch(ctl);
		for (int i = 0; i < workerCount; i++) {
			//将worker放入线程列表中
			workerThreadMap.put("worker" + i, new Thread(worker));

		}
	}

	/**
	 * 添加需要执行任务
	 * 
	 * @param 任务
	 * @return
	 */
	public void addTask(Task task) {
		taskQueue.add(task);
	}

	/**
	 * 执行计算任务，肯定是要拆分成多个子任务来执行 一直监听，有新任务则执行
	 */
	public void execute() {
		for (Entry<String, Thread> s : workerThreadMap.entrySet()) {
			Thread t = s.getValue();
			t.start();
			System.out.println("工人：" + s.getKey() + "开始干活了");
		}
	}

	/**
	 * 返回最终的执行结果
	 * 
	 * @return
	 */
	public int getResut() {
		int sum = 0;
		for (Entry<String, Object> s : workerResultMap.entrySet()) {
			sum += (Integer) s.getValue();
		}
		return sum;
	}

}
