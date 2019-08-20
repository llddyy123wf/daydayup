package org.dain.daydayup.concurrent.thread.future;

public class App {
	public static void main(String[] args) {
		FutureDataWrapper futureWrapper = new FutureDataWrapper();
		FutureResultData response = futureWrapper.getResponse(1);
		System.out.println("主钱和开始休眠1秒，do something else."+",curTime="+System.currentTimeMillis());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("总共5秒后，得到最终的响应结果:"+response.getData()+",curTime="+System.currentTimeMillis());
	}
}
