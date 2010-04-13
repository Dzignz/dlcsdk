package com.mogan.face;

import java.util.Map;

import net.sf.json.JSONArray;

public interface MigrationFace {
	
	/**
	 * 開始Migration資料
	 */
	boolean startMigr();
	
	/**
	 * 停止整合資料 
	 */
	boolean stopMigr();
	
	/**
	 * 取得進度百分比
	 * @return
	 */
	int getMigrRate();
	
	/**
	 * 取得資料總筆數
	 * @return
	 */
	int getDataCount();
	
	/**
	 * 取得已處理筆數
	 * @return
	 */
	int getMigrCount();
}
