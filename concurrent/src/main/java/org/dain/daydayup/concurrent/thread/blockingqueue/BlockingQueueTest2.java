package org.dain.daydayup.concurrent.thread.blockingqueue;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
/**
 *  .测试往队列中插入数据，优先执行
 * @author lideyin
 * @date 2019年4月24日 下午6:45:05
 * @description
 */
public class BlockingQueueTest2 {
	static class Productor implements Runnable {
		private BlockingQueue<Integer> blockingQueue;

		Productor(BlockingQueue<Integer> blockingQueue) {
			this.blockingQueue = blockingQueue;
		}

		private volatile int i;

		public void run() {
			for (i = 0; i < Constants.dbList.size(); i++) {
				try {
					if (i > 9) {						
						break;
					}
					if(i==9 || i==Constants.dbList.size()-1) {
						//数据造完,重置状态
						Constants.isProducted=false;
					}
					// 当前队列数
//					System.out.println("当前队列数：" + Constants.blockingQueue.size());
					// 将给定元素在给定的时间内设置到队列中，如果设置成功返回true, 否则返回false.
					boolean isOfferedSuccess = blockingQueue.offer(Constants.dbList.get(i), 10, TimeUnit.SECONDS);
					if (!isOfferedSuccess) {
						System.out.println("当前时间：" + (new Date()).toLocaleString() + ",生产超时准备退出");
						break;
					} else {
						System.out.println("productor producted a object:" + Constants.dbList.get(i));
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	static class Consumer implements Runnable {
		private BlockingQueue<Integer> blockingQueue;
		private long startTime = System.currentTimeMillis();

		Consumer(BlockingQueue blockingQueue) {
			this.blockingQueue = blockingQueue;
		}

		public void run() {
			while (true) {
				String tName = Thread.currentThread().getName();
				Integer queueData;
				try {
					System.out.println();
					Thread.currentThread().sleep(1000);
					/*
					 * // 从队列中获取值，如果队列中没有值，线程会一直阻塞，直到队列中有值，并且该方法取得了该值。 queueData =
					 * blockingQueue.take();
					 */

					// 在给定的时间里，从队列中获取值，时间到了直接调用普通的poll方法，为null则直接返回null。
					queueData = blockingQueue.poll(3, TimeUnit.SECONDS);

					if (queueData == null) {
						System.out.println("超过3秒没取到数据 ，准备退出线程，当前时间：" + (new Date()).toLocaleString());
						// 3秒没取到数据，则结束进程
						break;
					} else {
						System.out.println("当前时间：" + (new Date()).toLocaleString());
					}
					// 用一条,删除一条
					Constants.dbList.removeIf(p -> p.equals(queueData));

					System.out.println("消费者：" + tName + " 消费了产品：" + queueData);
					System.out.println("还剩：" + blockingQueue.size() + " 条数据待消费");
					int blockingSize = blockingQueue.size();

					// 小于两条数据时，马上再生产数据
					if (blockingSize == 0) {
						if (Constants.isProducted == false) {
//							synchronized (this) {
								System.out.println("告诉生产者生产数据");
								new Thread(new Productor(Constants.blockingQueue)).start();
								Constants.isProducted = true;
//							}

						}

					}
//					if (blockingQueue.size() == 0) {
//						long endTime = System.currentTimeMillis();
//						System.out.println("当前队列处理完成共计耗时：" + (endTime - startTime) + "ms");
//					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	public static void main(String[] args) {
		Productor productor = new Productor(Constants.blockingQueue);
		Consumer consumer = new Consumer(Constants.blockingQueue);
		Thread tt = new Thread(productor);

		Thread t1 = new Thread(consumer);
		Thread t2 = new Thread(consumer);
		Thread t3 = new Thread(consumer);
		Thread t4 = new Thread(consumer);
		tt.start();
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		try {
			Thread.currentThread().sleep(8000);
			Constants.blockingQueue.offer(888);
			Constants.blockingQueue.offer(999);
			System.out.println("8秒生产者又造了888和999");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
