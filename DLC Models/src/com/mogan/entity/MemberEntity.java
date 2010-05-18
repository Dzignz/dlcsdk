package com.mogan.entity;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.mogan.exception.MoganException;
import com.mogan.exception.entity.EntityNotExistException;
import com.mogan.sys.DBConn;

public class MemberEntity {
	private DBConn conn;
	final static private String CONN_ALIAS = "mogan-DB";
	static public Logger logger  =  Logger.getLogger("MOGAN");
	final static public String MEMBER_ID="MEMBER_ID";
	final static public String ITEM_ORDER_ID="ITEM_ORDER_ID";
	final static public String ITEM_TIDE_ID="ITEM_TIDE_ID";
	
	final static public String CNY="CNY";	//人民幣
	final static public String TWD="TWD";	//新台幣
	final static public String USD="USD";	//美金
	final static public String JPY="JPY";	//日幣
	
	private JSONObject memberObj;
	private String id;
	private String idType;
	/**
	 * 用memberId找出會員
	 * @param conn
	 * @param id 會員ID
	 * @param idType 
	 * @throws EntityNotExistException 
	 */
	public MemberEntity (DBConn conn,String id,String idType) throws EntityNotExistException{
		this.conn=conn;
		this.id=id;
		this.idType=idType;
		refreashData();
	}
	
	/**
	 * 重新讀取會員資料
	 * @throws EntityNotExistException
	 */
	public void refreashData() throws EntityNotExistException{
		JSONArray jArray;
		if (idType.equals(MEMBER_ID)){
			jArray=conn.queryJSONArray(CONN_ALIAS, "SELECT * FROM member_data WHERE member_id = '"+id+"' AND delete_flag=1");
		}else if (idType.equals(ITEM_ORDER_ID)){
			jArray=conn.queryJSONArray(CONN_ALIAS, "SELECT * FROM member_data WHERE member_id in (SELECT member_id FROM item_order WHERE item_order_id ='"+id+"'  AND  delete_flag=1  )  AND delete_flag=1 ");
		}else if (idType.equals(ITEM_ORDER_ID)){
			jArray=conn.queryJSONArray(CONN_ALIAS, "SELECT * FROM member_data WHERE member_id in (SELECT member_id FROM item_tide WHERE tide_id ='"+id+"'  AND  delete_flag=1  )  AND delete_flag=1 ");	
		}else{
			jArray=new JSONArray();
		}
		
		if (jArray.size()>0){
			memberObj=jArray.getJSONObject(0);
		}else{
			throw new EntityNotExistException("沒有此ID資料.("+id+" "+idType+")");
		}
	}
	
	/**
	 * 直接回傳會員資料
	 * @return
	 */
	public JSONObject getData(){
		return memberObj;
	}

	/**
	 * 取回帳戶餘額
	 * @param currency
	 * @return
	 */
	public String getMyMoney(String currency){
		String money="";
		money=memberObj.getString(CurrencyEntity.getCurrencyAccountColName(currency));
		return money;
	}
	
	/**
	 * 取回帳戶欠款
	 * @param currency
	 * @return
	 */
	public String getMyDebts(String currency){
		String debts="";
		debts=memberObj.getString(CurrencyEntity.getCurrencyAccountColName(currency));
		return debts;
	}
	
}
