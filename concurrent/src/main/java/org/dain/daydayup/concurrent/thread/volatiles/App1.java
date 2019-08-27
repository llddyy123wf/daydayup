package org.dain.daydayup.concurrent.thread.volatiles;

public class App1 {
	public static void main(String[] args) throws InterruptedException {
		MyTest t = new MyTest();
		Thread t1 = new Thread(new Task1(t));
		Thread t2 = new Thread(new Task2(t));

		//1 先启动t2,让循环走起来
		t2.start();
		Thread.sleep(1000);
		//2 然后启动t2线程，改变boolean变量b,以便停止循环
		t1.start();
	}
}

class Task1 implements Runnable {
	private MyTest mt;

	public Task1(MyTest mt) {
		this.mt = mt;
	}

	@Override
	public void run() {
		mt.method1();

	}

}

class Task2 implements Runnable {
	private MyTest mt;

	public Task2(MyTest mt) {
		this.mt = mt;
	}

	@Override
	public void run() {
		mt.method2();

	}

}

class MyTest {
	private int a = 0;
	// 如果变量b不定义为volatile，则线程t2使用线程内的工作内存中的值，
	// 不从主存中取最新的b的值，导致循环一直不结束
	private volatile boolean b = false;

	public void method1() {
		a = 1;// 1
		b = true;// 2
		System.out.println("method1--Thread:" + 
		Thread.currentThread().getName() + ".a=" + a +" b="+b);
	}

	public void method2() {
		while (!b) {// 3
		}

	}
}
