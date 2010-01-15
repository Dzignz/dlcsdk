package com.mogan.schedule;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.mogan.model.netAgent.NetAgent;
import com.mogan.sys.model.ScheduleModelAdapter;

public class PhpTask extends ScheduleModelAdapter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PhpTask phpTask = new PhpTask();
		phpTask.run();
	}

	public static class PhpB2BLink implements Runnable {
		private int type;

		public PhpB2BLink(int type) {
			this.type = type;
		}

		public void run() {
			NetAgent nAgent = new NetAgent();
			nAgent
					.getDataWithGet("http://wwwx.mogan.com.tw/web_atm/Myb2bDataLink.php?type="
							+ this.type);
			System.out.println("[DEBUG] PHP TASK("+new Date()+"):"+nAgent.getResponseBody());
			
		}
	}

	public void run() {
		super.run();
		Executor tp = Executors.newFixedThreadPool(2);
		tp.execute(new PhpB2BLink(1));
		tp.execute(new PhpB2BLink(2));

	}

}
