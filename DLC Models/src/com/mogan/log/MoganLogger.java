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
	
	final static Map<String,Object> objects=new HashMap<String,Object>();
	{
		Map<String,Object> actionCodeMap=new HashMap<String,Object>();
		actionCodeMap.put(DELETE, "");
		actionCodeMap.put(NEW, "");
		actionCodeMap.put(UPDATE, "");
		actionCodeMap.put(QUERY, "");
		objects.put(OBJECT_ITEM_TIDE, actionCodeMap);
	}
	
	/**
	 * 記錄LOG，varchar1不寫入COMMIT
	 * @param logDataMap
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	public void preLog(Map<String,Object> logDataMap) throws UnsupportedEncodingException, SQLException{
		Map<String,Object> logConditionMap=new HashMap<String,Object>();
		logConditionMap.put("log_id", logDataMap.get("log_id"));
		preLog(logDataMap,logConditionMap);
	}
	
	/**
	 * 記錄LOG，varchar1不寫入COMMIT
	 * @param logDataMap
	 * @param logConditionMap
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	public void preLog(Map<String,Object> logDataMap,Map<String,Object> logConditionMap) throws UnsupportedEncodingException, SQLException{
		conn.newData(CONN_ALIAS, LOG_TABLE, logConditionMap, logDataMap);
	}
	
	/**
	 * 記錄LOG，varchar1寫入COMMIT，確認動作完成
	 * @param logDataMap
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	public void commitLog(Map<String,Object> logDataMap) throws UnsupportedEncodingException, SQLException{
		Map<String,Object> logConditionMap=new HashMap<String,Object>();
		
		logConditionMap.put("log_id", logDataMap.get("log_id"));
		commitLog(logDataMap,logConditionMap);
	}
	
	/**
	 * 記錄LOG，varchar1寫入COMMIT，確認動作完成
	 * @param logDataMap
	 * @param logConditionMap
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	public void commitLog (Map<String,Object> logDataMap,Map<String,Object> logConditionMap) throws UnsupportedEncodingException, SQLException{
		logDataMap.put("varchar1", "commit");
		conn.newData(CONN_ALIAS, LOG_TABLE, logConditionMap, logDataMap);
	}
	
	/**
	 * 刪除ITEM ORDER資料，給User看的資料
	 * log_status=LR-9623
	 * @param logId
	 * @param itemOrderId
	 * @param money
	 * @param userId
	 * @param ip
	 * @param msg
	 * @return
	 */
	static public Map<String,Object> getItemOrderDel4User(String logId,String itemOrderId,String money,String userId,String memberId,String ip,String msg){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-9623");
		logDataMap.put("item_order_id", itemOrderId);
		logDataMap.put("money", money);
		logDataMap.put("time_at", new Date());
		logDataMap.put("varchar2", msg);
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		logDataMap.put("user_name", memberId);
		return logDataMap;
	}
	
	/**
	 *  刪除ITEM TIDE資料，給User看的資料
	 *  log_status=LR-9633
	 * @param logId
	 * @param tideId
	 * @param money
	 * @param userId
	 * @param memberId
	 * @param ip
	 * @param msg
	 * @return
	 */
	static public Map<String,Object> getItemTideDel4User(String logId,String tideId,String money,String userId,String memberId,String ip,String msg){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-9633");
		logDataMap.put("item_order_id", tideId);
		logDataMap.put("money", money);
		logDataMap.put("time_at", new Date());
		logDataMap.put("varchar2", msg);
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		logDataMap.put("user_name", memberId);
		return logDataMap;
	}
	
	/**
	 *  棄標ITEM TIDE資料，給User看的資料
	 *  log_status=LR-9626
	 * @param logId
	 * @param tideId
	 * @param userId
	 * @param memberId
	 * @param ip
	 * @param msg
	 * @return
	 */
	static public Map<String,Object> getItemTideGiveUp4User(String logId,String tideId,String userId,String memberId,String ip,String msg){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-9626");
		logDataMap.put("item_order_id", tideId);
		logDataMap.put("money", "0");
		logDataMap.put("time_at", new Date());
		logDataMap.put("varchar2", msg);
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		logDataMap.put("user_name", memberId);
		return logDataMap;
	}
	
	/**
	 * 棄標商品
	 * log_status=LR-8015
	 * @param logId
	 * @param tideId
	 * @param money
	 * @param userId
	 * @param memberId
	 * @param ip
	 * @return
	 */
	static public Map<String,Object> getItemOrderGiveup4User(String logId,String itemOrderId,String money,String userId,String memberId,String ip,String msg){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-8015");
		logDataMap.put("item_order_id", itemOrderId);
		logDataMap.put("money", money);
		logDataMap.put("time_at", new Date());
		logDataMap.put("varchar2", msg);
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		logDataMap.put("user_name", memberId);
		return logDataMap;
	}
	
	/**
	 * 	確認訂單金額
	 * 	log_status=LR-9634
	 * @param tideId
	 * @param moeny
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map<String,Object> getItemTideSubmitMoeny(String logId,String tideId,String money,String userId,String ip){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-9634");
		logDataMap.put("money", money);
		logDataMap.put("item_order_id", tideId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	
	/**
	 *	修改訂單資料 
	 *	log_status=LR-9627
	 * @param tideId
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map<String,Object> getItemTideSaveMoney(String logId,String tideId,String userId,String ip){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-9627");
		logDataMap.put("item_order_id", tideId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	

	
	/**
	 * 	移動訂單訊息
	 * 	log_status=LR-9636
	 * @param itemOrderId
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map<String,Object> getItemOrderMoveTide(String logId,String itemOrderId,String newTideId,String oldTideId,String userId,String ip){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-9636");
		logDataMap.put("item_order_id", itemOrderId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		logDataMap.put("varchar2", newTideId);
		logDataMap.put("varchar3", oldTideId);
		return logDataMap;
	}
	
	/**
	 * 建立新訂單
	 * log_status=LR-9630
	 * @param itemOrderId
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map<String,Object> getItemTideNew(String logId,String newTideId,String userId,String ip){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-9630");
		logDataMap.put("item_order_id", newTideId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	
	
	/**
	 * 修改訂單備忘資料 
	 * log_status=LR-9637
	 * @param tideId
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map<String,Object> getItemTideSaveAlert(String logId,String tideId,String userId,String ip){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-9637");
		logDataMap.put("item_order_id", tideId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	
	/**
	 * 退商品費用 
	 * log_status=LR-9638
	 * @param logId
	 * @param tideId 
	 * @param money
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map<String,Object> getItemOrderMoneyBak(String logId,String tideId,String money,String userId,String ip){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-9638");
		logDataMap.put("money", money);
		logDataMap.put("item_order_id", tideId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	
	/**
	 * 	退訂單費用
	 * log_status=LR-9632
	 * @param logId
	 * @param tideId
	 * @param money
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map<String,Object> getItemTideMoneyBak(String logId,String tideId,String money,String userId,String ip){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-9632");
		logDataMap.put("money", money);
		logDataMap.put("item_order_id", tideId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	
	
	
	///////////////////////////////////////////////////////////
	
	
	/**
	 * 	改變訂單item tide資料
	 * log_status=LR-8010
	 * @param logId
	 * @param tideId
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map<String,Object> getItemTideChange(String logId,String tideId,String userId,String ip){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-8010");
		logDataMap.put("item_order_id", tideId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	
	/**
	 * 	改變訂單item tide資料
	 *  log_status=LR-8011
	 * @param logId
	 * @param tideId
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map<String,Object> getMemberDataChange(String logId,String memberId,String userId,String ip){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-8011");
		logDataMap.put("item_order_id", memberId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	
	/**
	 * 會員帳戶改變
	 * log_status=LR-8012
	 * @param logId
	 * @param memberId
	 * @param money
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map<String,Object> getMemberMoneyChange(String logId,String memberId,String money,String userId,String ip){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-8012");
		logDataMap.put("item_order_id", memberId);
		logDataMap.put("money", money);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	
	/**
	 * 	改變訂單item tide資料
	 * log_status=LR-8013
	 * @param logId
	 * @param itemOrderId
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map<String,Object> getItemOrderChange(String logId,String itemOrderId,String userId,String ip){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-8013");
		logDataMap.put("item_order_id", itemOrderId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	
	/**
	 * 刪除ITEM ORDER資料
	 * log_status=LR-8014
	 * @param logId
	 * @param tideId
	 * @param money
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map<String,Object> getItemOrderDel(String logId,String tideId,String userId,String ip){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-8014");
		logDataMap.put("item_order_id", tideId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
	

	
	/**
	 * 	刪除訂單
	 * 	log_status=LR-8016
	 * @param tideId
	 * @param userId
	 * @param ip
	 * @return
	 */
	static public Map<String,Object> getItemTideDelete(String logId,String tideId,String userId,String ip){
		Map<String,Object> logDataMap =new HashMap<String,Object>();
		logDataMap.put("log_id", logId);
		logDataMap.put("log_status", "LR-8016");
		logDataMap.put("item_order_id", tideId);
		logDataMap.put("time_at", new Date());
		logDataMap.put("user_ip", ip);
		logDataMap.put("admin_name", userId);
		return logDataMap;
	}
}
