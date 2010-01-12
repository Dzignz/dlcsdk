package com.mogan.sys.model;

import java.util.Date;

public interface ScheduleModelFace extends ProtoModelFace {

	/**
	 * @return the executeDate
	 */
	Date getExecuteDate();
	
	/** 設定重覆執行間隔時間 */
	void setInterval(Object intervalMinute);
	/** 取得重覆執行間隔時間 */
	long getInterval();
	
	/** 設定是否重覆執行 */
	void setLoop(Object loop);
	/** 是否重覆執行 */
	boolean isLoop();
	
	/** 設定是否自動執行 */
	void setLOS(Object LOS);	
	/** 是否自動執行 */
	boolean isLOS();
	
	void setExecuteDate(Date executeDate);
}
