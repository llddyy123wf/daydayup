package org.dain.daydayup.concurrent.thread.future;

import java.util.concurrent.CountDownLatch;

/**
 * 最终返回的结果实体
 * @author lideyin
 * @date 2019年8月19日 下午11:32:22
 * @description
 */
public class FutureResultData {

	private boolean invoked;
	private CountDownLatch cdl;
	private String result;
	
	public FutureResultData(CountDownLatch cdl) {
		this.cdl = cdl;
	}

	public boolean isInvoked() {
		return invoked;
	}

	public void setInvoked(boolean invoked) {
		this.invoked = invoked;
	}

	public void processData(int id) {
		
		try {
			Thread.sleep(5000);
			this.result= "the Result:"+id;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			cdl.notifyAll();
		}
		
		
	}
	
	public String getData() {
		try {
			cdl.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.result;
	}
}
