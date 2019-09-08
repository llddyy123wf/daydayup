package org.dain.daydayup.concurrent.thread.blockingqueue;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import org.omg.PortableServer.THREAD_POLICY_ID;

public class BlockingQueueTest {
	static class Productor implements Runnable {
		private BlockingQueue<Integer> blockingQueue;

		Productor(BlockingQueue<Integer> blockingQueue) {
			this.blockingQueue = blockingQueue;
		}

		public void run() {
			for (int i = 0; i < 20; i++) {
				try {
					// 将元素设置到队列中，如果队列中没有多余的空间，该方法会一直阻塞，直到队列中有多余的空间。
					blockingQueue.put(i);
					// 将给定元素在给定的时间内设置到队列中，如果设置成功返回true, 否则返回false.
//					blockingQueue.offer(i,10,TimeUnit.SECONDS);	
					
					System.out.println("productor producted a object:" + i);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
//			for(int i=0;i<20;i++) {//测试多线程用
//				if(totalTicket>0) {
//					System.out.println("余票还有："+totalTicket--);
//				}
//				
//			}
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
					/*这里是drainTo的测试
					 * List<Integer> iList = new ArrayList<Integer>();
					blockingQueue.drainTo(iList, 10);
					int i=0;
					if(!iList.isEmpty()) {
						for(Integer p:iList) {
							i++;
							System.out.println("list【"+i+"】="+p);
						}
					}
					if(1==1) return;*/
					System.out.println();
					Thread.currentThread().sleep(1000);
					/*// 从队列中获取值，如果队列中没有值，线程会一直阻塞，直到队列中有值，并且该方法取得了该值。
					queueData = blockingQueue.take();*/
					
					// 在给定的时间里，从队列中获取值，时间到了直接调用普通的poll方法，为null则直接返回null。
					queueData = blockingQueue.poll(3,TimeUnit.SECONDS);
					
					if(queueData==null) {
						System.out.println("超过3秒没取到数据 ，准备退出线程，当前时间："+(new Date()).toLocaleString());
						//3秒没取到数据，则结束进程
						break;
					}else {
						System.out.println("当前时间："+(new Date()).toLocaleString());
					}
					System.out.println("消费者：" + tName + " 消费了产品：" + queueData);
					System.out.println("还剩：" + blockingQueue.size() + " 条数据待消费");
					if (blockingQueue.size() == 0) {
						long endTime = System.currentTimeMillis();
						System.out.println("当前队列处理完成共计耗时：" + (endTime - startTime) + "ms");
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	/**
	 * 多线程继承Thred的话，无法共享资源，而runnable却可以。
	 * start和run的区别。调start其实是资源准备就绪，不会马上执行，而调用run会马上执行。所以必须调用start.
	 * 
	 * @author lideyin
	 * @date 2019年3月21日 上午11:33:14
	 * @description
	 */
	static class ProductorThread extends Thread {
		private BlockingQueue<Integer> blockingQueue;
		private int totalTicket = 10;

		ProductorThread(BlockingQueue blockingQueue) {
			this.blockingQueue = blockingQueue;
		}

		public void run() {
//			for (int i = 0; i < 11; i++) {
//				try {
//					blockingQueue.put(i);
//					System.out.println("productor producted a object:" + i);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
			for (int i = 0; i < 20; i++) {
				if (totalTicket > 0) {
					System.out.println("余票还有：" + totalTicket--);
				}

			}
		}

	}

	public static void main(String[] args) {
//		method1();
//		if (2==2) return;
//		BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<Integer>(10);
//		BlockingQueue queue = new LinkedBlockingQueue();
//		ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 6, 10, TimeUnit.SECONDS,queue);
		
		Productor productor = new Productor(Constants.blockingQueue);
//		ProductorThread t1 = new ProductorThread(blockingQueue);
//		ProductorThread t2 = new ProductorThread(blockingQueue);
		Consumer consumer = new Consumer(Constants.blockingQueue);
//		executor.execute(productor);
//		executor.execute(consumer);
//		executor.execute(consumer);
//		executor.execute(consumer);
//		executor.shutdown();
//		if(1==1) return;
		
//		Consumer consumer2 = new Consumer(blockingQueue);
//		t1.start();
//		t2.start();
		Thread tt = new Thread(productor);
		
//		new Thread(productor).start();
		Thread t1 =new  Thread(consumer);
		Thread t2 =new  Thread(consumer);
		System.out.println("此时还未启动线程");
		System.out.println("tt.getState()="+tt.getState());
		System.out.println("t1.getState()="+t1.getState());
		System.out.println("t2.getState()="+t2.getState());
		tt.start();
		t1.start();
		t2.start();
		System.out.println("此时已启动线程.....");
		System.out.println("tt.getState()="+tt.getState());
		System.out.println("t1.getState()="+t1.getState());
		System.out.println("t2.getState()="+t2.getState());
		try {
			Thread.currentThread().sleep(16000);
			Constants.blockingQueue.offer(888);
			Constants.blockingQueue.offer(999);
			System.out.println("23秒生产者又造了888和999");
			System.out.println("tt.getState()="+tt.getState());
			System.out.println("t1.getState()="+t1.getState());
			System.out.println("t2.getState()="+t2.getState());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	  public static void method1() {
          BlockingQueue queue = new LinkedBlockingQueue();
          ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 6, 1, TimeUnit.SECONDS, queue);
          for ( int i = 0; i < 20; i++) {
              executor.execute( new Runnable() {
                  public void run() {
                      try {
                          System. out.println( this.hashCode()/1000);
                            for ( int j = 0; j < 10; j++) {
                               System. out.println( this.hashCode() + ":" + j);
                               Thread. sleep(this.hashCode()%2);
                          } 
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                      System. out.println(String. format("thread %d finished", this.hashCode()));
                  }
              });
          }         
          executor.shutdown();
      }
}
