package com.mogan.sys;

import java.util.Date;
import java.util.TimerTask;

/**
 * 固定時間顯示系統訊息，如果有狀況則通知管理者
 * @author Dian
 *
 */
public class SysAlert extends TimerTask {
	static int i=0;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("[INTO]\tSysAlert\t"+new Date());
		System.out.println("\tThreads\t"+Thread.activeCount());
        long totalMem = Runtime.getRuntime().totalMemory();
        long freeMem = Runtime.getRuntime().freeMemory();
        System.out.println("\tTotal memory\t"+ totalMem+"byte");
        System.out.println("\tFree memory\t" + freeMem+"byte");
        System.out.println("\tcount i\t" + i++);
		System.out.println("====================");
	}

}
