package com.mogan.entity;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.mogan.exception.MoganException;
import com.mogan.exception.entity.EntityNotExistException;
import com.mogan.log.MoganLogger;
import com.mogan.sys.DBConn;
import com.mogan.sys.SysMath;
import com.mogan.sys.log.SysLogger4j;

public class MemberEntity extends EntityService {
	private Logger logger  =  Logger.getLogger(MemberEntity.class.getName());
	
	final static public String MEMBER_ID="MEMBER_ID";
	final static public String ITEM_ORDER_ID="ITEM_ORDER_ID";
	final static public String ITEM_TIDE_ID="ITEM_TIDE_ID";

	private String id;
	private String idType;
	
	/**
	 * 可接受不同的ID，使用不同的方法找出會員資料
	 * @param conn
	 * @param id ID
	 * @param idType ID類型
	 * @throws EntityNotExistException 
	 */
	public MemberEntity(ServletContext servletContext,HttpSession session,String id,String idType) throws EntityNotExistException{
		super(servletContext, session);
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
			jArray=conn.queryJSONArray(CONN_ALIAS, "SELECT * FROM member_data WHERE member_id = '"+this.id+"' AND delete_flag=1");
		}else if (idType.equals(ITEM_ORDER_ID)){
			jArray=conn.queryJSONArray(CONN_ALIAS, "SELECT * FROM member_data WHERE member_id in (SELECT member_id FROM item_order WHERE item_order_id ='"+id+"'  AND  delete_flag=1  )  AND delete_flag=1 ");
		}else if (idType.equals(ITEM_TIDE_ID)){
			jArray=conn.queryJSONArray(CONN_ALIAS, "SELECT * FROM member_data WHERE member_id in (SELECT member_id FROM item_tide WHERE tide_id ='"+id+"'  AND  delete_flag=1  )  AND delete_flag=1 ");	
		}else{
			jArray=new JSONArray();
		}
		
		if (jArray.size()==1){
			this.setMainObj(jArray.getJSONObject(0));
			
			
			/**
			 * 回舊資料庫帶資料 
			 */
			JSONArray oldData=conn.queryJSONArray("mogan-tw", "SELECT ntd,debts FROM web_member WHERE name = '"+this.getAttribute("name")+"' ");
			
			this.setAttribute("sum_ntd", oldData.getJSONObject(0).getString("ntd"));
			this.setAttribute("debts_ntd", oldData.getJSONObject(0).getString("debts"));
			logger.info("會員資料讀取完成.("+this.getAttribute("name")+") "+this.getAttribute("sum_ntd"));
		}else if (jArray.size()>1){
			throw new EntityNotExistException("此ID對應多筆資料.("+id+" "+idType+")");
		}else if (jArray.size()==0){
			throw new EntityNotExistException("沒有此ID資料.("+id+" "+idType+")");
		}
	}
	

	/**
	 * 調整台幣帳戶餘額
	 * @param money 金額，可傳入正數或負數
	 * @param autoSave 是否自已儲存
	 * @return
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 * @throws EntityNotExistException 
	 */
	public String addMoney(String money,boolean autoSave) throws UnsupportedEncodingException, SQLException, EntityNotExistException{
		return addMoney(money,CurrencyEntity.TWD,autoSave);
	}
	
	/**
	 * 調整台幣帳戶餘額
	 * @param money 金額，可傳入正數或負數
	 * @param currency 帳戶類型
	 * @param autoSave 是否自動儲存
	 * @return
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 * @throws EntityNotExistException 
	 */
	public String addMoney(String money,String currency,boolean autoSave) throws UnsupportedEncodingException, SQLException, EntityNotExistException{
		
		String baseMoneny=(String) this.getAttribute(CurrencyEntity.getCurrencyAccountColName(currency));	//取得原本餘額
		//判斷傳入金額是否正確
		try{
			Double.parseDouble(money);
		}catch(Exception ex){
			logger.error(ex.getMessage(),ex);
			return baseMoneny;
		}
		
		String sumValue= Double.toString(SysMath.add(baseMoneny, money));								//將付入金額+原本餘額
		logger.info("會員調整餘額  "+baseMoneny+" + "+money+" = "+sumValue);
		this.setAttribute(CurrencyEntity.getCurrencyAccountColName(currency), sumValue);
		if (autoSave){
			String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
			Map<String,Object> log_dataMap = mLogger.getMemberMoneyChange(log_Id, (String) this.getAttribute("member_id"), money, this.getOpenUser() , this.getOpenUserIP());
			log_dataMap.put("varchar2", "new db");
			mLogger.preLog(log_dataMap);
			Map dataMap=new HashMap();
			Map conditionMap=new HashMap();
			conditionMap.put("member_id", this.getAttribute("member_id"));
			dataMap.put(CurrencyEntity.getCurrencyAccountColName(currency), sumValue);
			conn.update(CONN_ALIAS, "member_data", conditionMap, dataMap);

			log_dataMap.put("sum_money", this.getMyMoney(currency));	//帳戶餘額
			log_dataMap.put("debts", this.getMyDebts(currency));		//帳戶欠款
			mLogger.commitLog(log_dataMap);
			///////////////////////////////////分隔線//////////////////
			
			//舊版更新
			log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
			log_dataMap = mLogger.getMemberMoneyChange(log_Id, (String) this.getAttribute("member_id"), money, this.getOpenUser() , this.getOpenUserIP());
			log_dataMap.put("varchar2", "old db");
			mLogger.preLog(log_dataMap);
			conditionMap=new HashMap();
			dataMap=new HashMap();
			conditionMap.put("name", this.getAttribute("name"));
			dataMap.put("ntd", sumValue);
			conn.update("mogan-tw", "web_member", conditionMap, dataMap);
			mLogger.commitLog(log_dataMap);
			this.refreashData();
		}
		logger.info(this.getAttribute("first_name")+" "+this.getAttribute("last_name")+"會員餘額調整.... 金額："+money+" ... 帳戶："+currency+" ... 自動儲存"+autoSave+"...("+this.getAttribute("member_id")+")");
		return sumValue;
	}
	
	/**
	 * 取回帳戶餘額
	 * @param currency
	 * @return
	 */
	public String getMyMoney(String currency){
		String money="";
		money=(String) this.getAttribute(CurrencyEntity.getCurrencyAccountColName(currency));
		return money;
	}
	
	/**
	 * 取回帳戶欠款
	 * @param currency
	 * @return
	 */
	public String getMyDebts(String currency){
		String debts="";
		debts=(String) this.getAttribute(CurrencyEntity.getCurrencyDebtsColName(currency));
		return debts;
	}

	/**
	 * 取得會員ID
	 * @return
	 */
	public String getUserId(){
		return (String) this.getAttribute("member_id");
	}
	

	@Override
	public void delEntity(String msg) throws UnsupportedEncodingException, SQLException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void saveEntity(String msg) throws UnsupportedEncodingException, SQLException {
		// TODO Auto-generated method stub

	}



	@Override
	protected String create() {
		// TODO Auto-generated method stub
		return "";
	}



	@Override
	public EntityService cloneEty() throws UnsupportedEncodingException,
			SQLException, EntityNotExistException {
		// TODO Auto-generated method stub
		return null;
	}


	
	
}
