package com.mogan.log;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mogan.model.BidManagerV2;
import com.mogan.sys.DBConn;
import com.mogan.sys.log.SysLogger4j;

 public class MoganLogger {	
	static public Logger logger  =  Logger.getLogger("MOGAN");
	final static private String CONN_ALIAS = "mogan-DB";
	final static private String LOG_TABLE = "log_record";
	private DBConn conn;
	final static public String DELETE="DEL";
	final static public String NEW="NEW";
	final static public String UPDATE="UPD";
	final static public String QUERY="QUE";
	
	final static public String OBJECT_ITEM_TIDE="ITEM_TIDE";
	
	public MoganLogger (DBConn conn){
		this.conn=conn;
	}
	
	final static Map<String,Map<String,String>> objects=new HashMap();
	{
		Map actionCodeMap=new HashMap();
		actionCodeMap.put(DELETE, "");
		actionCodeMap.put(NEW, "");
		actionCodeMap.put(UPDATE, "");
		actionCodeMap.put(QUERY, "");
		objects.put(OBJECT_ITEM_TIDE, actionCodeMap);
	}
	
	public void preLog(Map logDataMap,Map logConditionMap) throws UnsupportedEncodingException, SQLException{
		conn.newData(CONN_ALIAS, LOG_TABLE, logConditionMap, logDataMap);
	}
	
	public void commitLog (Map logDataMap,Map logConditionMap) throws UnsupportedEncodingException, SQLException{
		logDataMap.put("varchar1", "commit");
		conn.newData(CONN_ALIAS, LOG_TABLE, logConditionMap, logDataMap);
	}
	
	/**
	 * 確認訂單金額
	 * @param tideId
	 * @param moeny
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map getItemTideSubmitMoeny(String logId,String tideId,String money,String userId,String ip){
		Map logDataMap =new HashMap();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-8002");
		logDataMap.put("money", money);
		logDataMap.put("item_order_id", tideId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	

	
	/**
	 *	修改訂單資料 
	 * @param tideId
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map getItemTideSaveMoney(String logId,String tideId,String userId,String ip){
		Map logDataMap =new HashMap();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-9627");
		logDataMap.put("item_order_id", tideId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	
	/**
	 * 刪除訂單
	 * @param tideId
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map getItemTideDelete(String tideId,String userId,String ip){
		Map logDataMap =new HashMap();
		logDataMap.put("log_status", "LR-9623");
		logDataMap.put("item_order_id", tideId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	
	/**
	 * 移動訂單訊息
	 * @param itemOrderId
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map getItemOrderMoveTide(String logId,String itemOrderId,String newTideId,String oldTideId,String userId,String ip){
		Map logDataMap =new HashMap();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-8004");
		logDataMap.put("item_order_id", itemOrderId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		logDataMap.put("varchar2", newTideId);
		logDataMap.put("varchar3", oldTideId);
		return logDataMap;
	}
	
	/**
	 * 
	 * @param itemOrderId
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map getItemTideNew(String logId,String newTideId,String userId,String ip){
		Map logDataMap =new HashMap();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-8005");
		logDataMap.put("item_order_id", newTideId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	
	/**
	 * 訂單移動記錄
	 * @param logId
	 * @param itemOrderId
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map getItemOrderMove(String logId,String itemOrderId,String userId,String ip){
		Map logDataMap =new HashMap();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-8004");
		logDataMap.put("item_order_id", itemOrderId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	
	/**
	 *	修改訂單備忘資料 
	 * @param tideId
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map getItemTideSaveAlert(String logId,String tideId,String userId,String ip){
		Map logDataMap =new HashMap();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-8006");
		logDataMap.put("item_order_id", tideId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	
}
