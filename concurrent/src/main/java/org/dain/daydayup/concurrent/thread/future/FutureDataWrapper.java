package org.dain.daydayup.concurrent.thread.future;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * 获取future数据包装类
 * 
 * @author lideyin
 * @date 2019年8月19日 下午11:25:31
 * @description
 */
public class FutureDataWrapper {

	private CountDownLatch cdl = new CountDownLatch(1);

	public FutureResultData getResponse(int id) {
		FutureResultData result =new FutureResultData(cdl);
		new Thread(() -> {
			result.processData(id);
		}).start();
		result.setInvoked(true);
		return result;
	}

}
