package com.mogan.bean;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.sys.DBConn;
import com.mogan.sys.SysCalendar;
import com.mogan.sys.SysKernel;

public class Statistics {
	/**
	 * 得標
	 */
	final public int DATA_TYPE_WON_BID = 1;

	/**
	 * 最高標
	 */
	final public int DATA_TYPE_HIGHEST_BID = 2;

	/**
	 * 競標中
	 */
	final public int DATA_TYPE_BID = 3;

	final public int DAY = 1;
	final public int WEEK = 2;
	final public int MONTH = 3;
	final public int SESSION = 4;
	final public int YEAR = 5;

	/**
	 * 
	 */
	private DBConn conn;
	private JSONArray dataArray;

	/**
	 * 建構子
	 */
	public Statistics() {
		conn = SysKernel.getConn();

	}

	/**
	 * 讀取資料
	 */
	public void loadData() {
		loadWonBid();
	}

	private void loadWonBid() {
		String sql = "";
		SysCalendar sc = new SysCalendar();
		String groupKey="";
		
		if (this.range == this.DAY) {
			sc.addDay(-rangeValue);
			groupKey="DATE";
		}else if (this.range==this.MONTH){
			sc.addMonth(-rangeValue);
			groupKey="WEEK";
		}
		
		sql = "SELECT COUNT(create_date) AS data,"+groupKey+"(create_date) AS data_date FROM system_alert WHERE alert LIKE '%YAHOO_JP_WON_BID_MAIL%'";
		sql += " AND create_date>'"
				+ sc.getFormatDate(SysCalendar.yyyy_MM_dd) + "'";
		sql += " GROUP BY "+groupKey+"(create_date) ORDER BY "+groupKey+"(create_date) ";
		dataArray = conn.queryJSONArray((String) SysKernel.getApplicationAttr(SysKernel.MAIN_DB), sql);
	}

	public String printData() {
		StringBuffer output = new StringBuffer();
		float avg = 0;
		for (int i = 0; i < dataArray.size(); i++) {
			JSONObject jObj = dataArray.getJSONObject(i);
			output.append("data.setValue(" + i + ", 0, '"
					+ jObj.getString("data_date") + "');\n");
			output.append("data.setValue(" + i + ", 1, "
					+ jObj.getString("data") + ");\n");
			avg = (avg + jObj.getInt("data")) / 2;
			output.append("data.setValue(" + i + ", 2, " + avg + ");\n");
		}
		return output.toString();
	}

	public int getDataSize() {
		return dataArray.size();
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the dataType
	 */
	public int getDataType() {
		return dataType;
	}

	/**
	 * @param range
	 *            the range to set
	 */
	public void setRange(int range) {
		this.range = range;
	}

	/**
	 * @return the range
	 */
	public int getRange() {
		return range;
	}

	/**
	 * @param rangeValue
	 *            the rangeValue to set
	 */
	public void setRangeValue(int rangeValue) {
		this.rangeValue = rangeValue;
	}

	/**
	 * @return the rangeValue
	 */
	public int getRangeValue() {
		return rangeValue;
	}

	/**
	 * 資料類型
	 */
	private int dataType = 0;

	/**
	 * 時間單位 day week month session year
	 */
	private int range = 0;

	/**
	 * 單位範圍 1,2,3...
	 */
	private int rangeValue = 0;

}
