package com.mogan.sys.model;

import java.util.Date;

public interface ScheduleModelFace extends ProtoModelFace {

	/**
	 * run()會執行的程式，主要的的程式編寫內容
	 */
	void exeSchedule();
	
	
	/**
	 * 設定最後執行時間
	 * @param executeDate
	 */
	void setLastExecuteDate(Date executeDate);
	
	/**
	 * 取得最後執行時間
	 * @return the executeDate
	 */
	Date getLastExecuteDate();
	
	/**
	 * 更新排程啟動時間
	 * @return
	 */
	void setStartScheduleDate(Date startExeDate);
	
	/**
	 * 取得排程啟動時間
	 * @return
	 */
	Date getStartScheduleDate();
	
	/**
	 * 更新預計下次執行時間
	 * @return
	 */
	void setNextExeDate(Date nextExeDate);
	
	/**
	 * 取得下次執行時間
	 * @return
	 */
	Date getNextExeDate();
	

	
	/** 設定重覆執行間隔時間 */
	void setInterval(Object intervalMinute);
	/** 取得重覆執行間隔時間 */
	long getInterval();
	
	/** 設定是否重覆執行 */
	void setLoop(Object loop);
	/** 是否重覆執行 */
	boolean isLoop();
	

	
	
	/** 設定指定次執行時間格式 */
	void setRunTimeSpec(String dateStr);
	
	/** 取得第一次執行時間 */
	String getRunTimeSpec();
	
	/**
	 * 設定執行次數
	 * @param i
	 */
	void setRemainTime(int i);
	
	/**
	 * 取得剩餘次數
	 * @return
	 */
	Integer getRemainTime();
	
	/** 設定是否自動執行 */
	void setLOS(Object LOS);	
	/** 是否自動執行 */
	boolean isLOS();
	
	

	
}
