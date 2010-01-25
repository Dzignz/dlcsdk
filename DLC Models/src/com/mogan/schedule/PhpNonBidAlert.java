package com.mogan.schedule;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.mogan.model.netAgent.NetAgent;
import com.mogan.schedule.PhpTask.PhpB2BLink;
import com.mogan.sys.model.ScheduleModelAdapter;

/**
 * 定時清除未得標案件
 * @author Dian
 *
 */
public class PhpNonBidAlert extends ScheduleModelAdapter {
	/** 呼叫PHP處理未得標資料  */
	static final String PHP_NON_BID_ALERT_URL = "PHP_NON_BID_ALERT_URL";
	public void run() {
		super.run();
		NetAgent nAgent = new NetAgent();
		
		nAgent.getDataWithGet(this.getProperty(PHP_NON_BID_ALERT_URL));
	}
}
