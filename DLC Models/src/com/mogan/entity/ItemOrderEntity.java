package com.mogan.entity;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.exception.entity.EntityNotExistException;
import com.mogan.log.MoganLogger;
import com.mogan.sys.SysMath;

public class ItemOrderEntity extends EntityService {
	private Logger logger  =  Logger.getLogger(ItemOrderEntity.class.getName());
	
	private String id;
	
	/**
	 * 
	 * @param conn
	 * @param id
	 * @param idType
	 * @throws EntityNotExistException
	 */
	public ItemOrderEntity (ServletContext servletContext,HttpSession session,String id) throws EntityNotExistException{
		super(servletContext, session);
		this.id=id;
		refreashData();
	}

	@Override
	void refreashData() throws EntityNotExistException {
		JSONArray jArray;
		jArray=conn.queryJSONArray(this.CONN_ALIAS,"SELECT * FROM view_bid_item_order_v1 WHERE item_order_id = '"+id+"' AND delete_flag=1");
		if (jArray.size()==1){
			this.setMainObj(jArray.getJSONObject(0));
			logger.info("會員資料讀取完成.("+this.getMainObj().getString("tide_id")+")");
			/**
			 * 回舊資料庫帶資料 
			 */
		}else if (jArray.size()>1){
			throw new EntityNotExistException("此ID對應多筆資料.("+id+")");
		}else if (jArray.size()==0){
			throw new EntityNotExistException("沒有此ID資料.("+id+")");
		}
	}
	
	/**
	 * 儲存資料
	 */
	@Override
	public void saveEntity(String msg) throws UnsupportedEncodingException, SQLException {
		String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
		Map log_dataMap = MoganLogger.getItemOrderChange(log_Id,this.getMainObj().getString("item_order_id"),  this.getOpenUser() , this.getOpenUserIP());
		log_dataMap.put("varchar2", msg);
		mLogger.preLog(log_dataMap);
		
		Map conditionMap=new HashMap();
		Map dataMap=new HashMap();
		conditionMap.put("item_order_id", this.getMainObj().getString("item_order_id"));
		dataMap.putAll(this.getMainObj());
		logger.info("getMainObj::"+this.getMainObj());	
		logger.info("dataMap::"+dataMap);
		conn.update(CONN_ALIAS, "item_order", conditionMap, dataMap);
		
		mLogger.commitLog(log_dataMap);
	}
	
	/**
	 * 取得此訂單總費用 ，商品價格 X 數量
	 */
	public double getItemTotalCost(){
		String cost=this.getMainObj().getString("buy_price");
		String qty=this.getMainObj().getString("buy_unit");
		double totalCost=SysMath.mul(cost, qty);
		return totalCost;
	}
	

	/**
	 * 刪除訂單
	 * 付款狀態不改 
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 */
	@Override
	public void delEntity(String msg) throws UnsupportedEncodingException, SQLException {
		String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
		Map log_dataMap = MoganLogger.getItemOrderDel(log_Id,this.getMainObj().getString("item_order_id"),  this.getOpenUser() , this.getOpenUserIP());
		log_dataMap.put("varchar2", msg);
		mLogger.preLog(log_dataMap);
		Map conditionMap=new HashMap();
		Map dataMap=new HashMap();
		conditionMap.put("item_order_id", this.getMainObj().getString("item_order_id"));
		dataMap.put("delete_flag", "0");
		conn.update(CONN_ALIAS, "item_order", conditionMap, dataMap);
		mLogger.commitLog(log_dataMap);
	}
	
	/**
	 * 變更訂單狀態
	 * @param status
	 * @param autoSave
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	public void changeStatus(String status, boolean autoSave) throws UnsupportedEncodingException, SQLException{
		this.setAttribute("order_status", status);
		this.saveEntity();
		logger.info("修改下標商品狀態. "+this.getAttribute("item_order_id")+" to "+status);
	}

	@Override
	String create(JSONObject etyObj) {
		// TODO Auto-generated method stub
		return null;
	}
}
