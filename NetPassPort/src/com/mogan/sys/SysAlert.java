package com.mogan.sys;

import java.util.Date;
import java.util.TimerTask;

import org.apache.log4j.Logger;

/**
 * 固定時間顯示系統訊息，如果有狀況則通知管理者
 * @author Dian
 *
 */
public class SysAlert extends TimerTask {
	static int i=0;
	static private Logger logger=Logger.getLogger(SysAlert.class.getName()); 
	@Override
	public void run() {
		logger.info("[INTO]\tSysAlert\t"+new Date());
		logger.info("\tThreads\t"+Thread.activeCount());
        logger.info("\tTotal memory\t"+ Runtime.getRuntime().totalMemory()+"byte");
        logger.info("\tFree memory\t" + Runtime.getRuntime().freeMemory()+"byte");
        logger.info("\tcount i\t" + i++);
        logger.info("====================");
	}

}
