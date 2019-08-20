package org.dain.daydayup.concurrent.thread.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 验证schedule与scheduleAtFixedRate方法的追赶性实验
 * 
 * @author lideyin
 * @date 2019年7月28日 下午4:04:45
 * @description
 */
public class Run2 {
	private static Timer timer = new Timer();

	static public class MyTask2 extends TimerTask {

		@Override
		public void run() {
			System.out.println("begin run...time is " + new Date());
			System.out.println("end run...time is " + new Date());
		}

	}
	public static void main(String[] args) {
		try {
		MyTask2 tk2 =new MyTask2();
		String planTime = "2019-07-28 16:15:00";
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date datePlan = sdf.parse(planTime);
		timer.scheduleAtFixedRate(tk2, datePlan, 20000);
		}catch(ParseException e) {
			e.printStackTrace();
		}
		}
}
