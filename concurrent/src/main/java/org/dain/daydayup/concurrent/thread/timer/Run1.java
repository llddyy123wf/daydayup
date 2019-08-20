package org.dain.daydayup.concurrent.thread.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Run1 {
	private static Timer timer = new Timer();
//	private static Timer timer = new Timer(true);

	static public class MyTask extends TimerTask {

		@Override
		public void run() {
			System.out.println("Timer task running...current time is :" + new Date());
		}

	}

	public static void main(String[] args) {
		try {
			MyTask task = new MyTask();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String planTime = "2019-07-28 13:31:59";
			Date datePlan = sdf.parse(planTime);

			System.out.println(
					"plan time:" + datePlan.toLocaleString() + ", current time :" + new Date().toLocaleString());
			
			timer.schedule(task, datePlan);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
