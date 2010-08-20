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

import com.mogan.exception.entity.EntityNotExistException;

/**
 * 2010/05/31 賣家資料
 * @author Dian
 *
 */
public class SellerEntity extends EntityService {
	private Logger logger  =  Logger.getLogger(SellerEntity.class.getName());
	private String id;
	private JSONObject sellerMainObj;
	private JSONArray sellerAccounts;
	
	public SellerEntity(ServletContext servletContext, HttpSession session,String id) throws EntityNotExistException {
		super(servletContext, session);
		this.id=id;
		refreashData();
	}

	@Override
	public void delEntity(String msg) throws UnsupportedEncodingException,
			SQLException {
		// TODO 刪除資料

	}

	@Override
	void refreashData() throws EntityNotExistException {
		// TODO Auto-generated method stub
		JSONArray jArray=conn.queryJSONArray(this.CONN_ALIAS,"SELECT * FROM item_seller WHERE seller_id = '"+id+"'");
		if (jArray.size()==1){
			setSellerMainObj(jArray.getJSONObject(0));
			logger.info("會員資料讀取完成.("+getSellerMainObj().getString("account")+" "+getSellerMainObj().getString("seller_id")+")");
			sellerAccounts=conn.queryJSONArray(this.CONN_ALIAS, "SELECT * FROM view_item_seller_account_v1 WHERE seller_id='"+id+"'");
			/**
			 * 回舊資料庫帶資料 
			 */
			//JSONArray oldData=conn.queryJSONArray("mogan-tw", "SELECT ntd,debts FROM web_member WHERE name = '"+sellerMainObj.getString("name")+"' ");
			//sellerMainObj.put("sum_ntd", oldData.getJSONObject(0).getString("ntd"));
			//sellerMainObj.put("debts_ntd", oldData.getJSONObject(0).getString("debts"));
		}else if (jArray.size()>1){
			throw new EntityNotExistException("此ID對應多筆資料.("+id+")");
		}else if (jArray.size()==0){
			throw new EntityNotExistException("沒有此ID資料.("+id+")");
		}
	}

	@Override
	public void saveEntity(String msg) throws UnsupportedEncodingException,
			SQLException {
		// TODO 儲存資料
		Map conditionMap=new HashMap();
		conditionMap.put("seller_id", this.getSellerMainObj().getString("seller_id"));
		conn.update(this.CONN_ALIAS, "item_seller", conditionMap, this.getSellerMainObj());
		for (int i=0;i<this.getSellerAccounts().size();i++){
			JSONObject tempObj=this.getSellerAccounts().getJSONObject(i);
			conditionMap=new HashMap();
			conditionMap.put("account_id", tempObj.getString("account_id"));
			conn.update(this.CONN_ALIAS, "item_seller_account", conditionMap, tempObj);	
		}
		logger.info("saveEntity success.");
	}

	/**
	 * @param sellerMainObj the sellerMainObj to set
	 */
	public void setSellerMainObj(JSONObject sellerMainObj) {
		this.sellerMainObj=sellerMainObj;
		logger.info("setSellerMainObj="+sellerMainObj);
	}
	

	/**
	 * @return the sellerMainObj
	 */
	public JSONObject getSellerMainObj() {
		return sellerMainObj;
	}

	/**
	 * 指定更新那筆資料,用傳入sellerAccountData的 account_id來尋找對應資料
	 * @param accountId
	 * @param sellerAccountData
	 */
	public void setSellerAccount(JSONObject sellerAccountData) {
		for (int i=0;i<sellerAccounts.size();i++){
			JSONObject tempObj=sellerAccounts.getJSONObject(i);
			if (tempObj.getString("account_id").equals(sellerAccountData.getString("account_id"))){
				tempObj.putAll(sellerAccountData);
				break;
			}
		}
	}
	
	/**
	 * 指定某筆資料ID、欄位及資料
	 * @param accountId 資料ID
	 * @param key 欄位
	 * @param value	資料
	 */
	public void setSellerAccount(String accountId,String key,String value) {
		for (int i=0;i<sellerAccounts.size();i++){
			JSONObject tempObj=sellerAccounts.getJSONObject(i);
			if (tempObj.getString("account_id").equals(accountId)){
				tempObj.put(key, value);
			}
		}
	}
	
	/**
	 * @param sellerAccounts the sellerAccounts to set
	 */
	public void setSellerAccounts(JSONArray sellerAccounts) {
		this.sellerAccounts = sellerAccounts;
	}

	/**
	 * @return the sellerAccounts
	 */
	public JSONArray getSellerAccounts() {
		return sellerAccounts;
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
