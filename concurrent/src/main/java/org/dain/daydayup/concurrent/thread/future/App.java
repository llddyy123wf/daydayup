package org.dain.daydayup.concurrent.thread.future;

public class App {
	public static void main(String[] args) {
		FutureClient client = new FutureClient();
		//提交待处理的请求数据，异步处理请求
		Data data = client.submit("this is my request param.");

		//模拟主线程处理其它任务
		System.out.println("主钱和开始休眠1秒，do something else."+",curTime="+System.currentTimeMillis());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//在主线程业务做完后，再来调future的返回值，
		// 有可能早已准备好，也有可能还需要继续等待一段时间才返回
		String result =data.getData();
		System.out.println("总共2秒后，得到最终的响应结果:"+result+",curTime="+System.currentTimeMillis());
	}
}
