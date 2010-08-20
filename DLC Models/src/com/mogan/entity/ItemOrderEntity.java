package com.mogan.entity;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.exception.entity.EntityNotExistException;
import com.mogan.exception.netAgent.AccountNotExistException;
import com.mogan.log.MoganLogger;
import com.mogan.log.MoganMessage;
import com.mogan.model.netAgent.NetAgentYJV2;
import com.mogan.sys.DBConn;
import com.mogan.sys.SysCalendar;
import com.mogan.sys.SysKernel;
import com.mogan.sys.SysMath;

public class ItemOrderEntity extends EntityService {
	static final String entityName="ITEM ORDER";
	// static final String ITEM_CONTACT_TYPE="o_varchar02";
	/**
	 * order form填寫狀況 <li>0=不用填寫</li> <li>1=要填寫</li> <li>2=已填寫完(未確認)</li>
	 */
	static final String ITEM_HAS_ORDER_FORM = "o_varchar02";

	/**
	 * 是否被取消過 <li>0=沒有取消記錄</li> <li>1=競標中被取消</li> <li>2=得標後被取消</li>
	 */
	static final String ITEM_CANCELD_FLAG = "flag_01";
	private Logger logger = Logger.getLogger(ItemOrderEntity.class.getName());

	private String id;

	/**
	 * @param conn
	 * @param id
	 * @param idType
	 * @throws EntityNotExistException
	 */
	public ItemOrderEntity(ServletContext servletContext, HttpSession session,
			String id) throws EntityNotExistException {
		super(servletContext, session);
		this.id = id;
		refreashData();
	}

	@Override
	void refreashData() throws EntityNotExistException {
		JSONArray jArray;
		jArray = conn.queryJSONArray(this.CONN_ALIAS, "SELECT * FROM view_bid_item_order_v1 WHERE item_order_id = '"
				+ id + "' AND delete_flag=1");
		if (jArray.size() == 1) {
			this.setMainObj(jArray.getJSONObject(0));
			logger.info(entityName+" 資料讀取完成.(" + this.getMainObj().getString("item_order_id")
					+ ")");
			/**
			 * 回舊資料庫帶資料
			 */
		} else if (jArray.size() > 1) {
			throw new EntityNotExistException("此ID對應多筆資料.(" + id + ")");
		} else if (jArray.size() == 0) {
			throw new EntityNotExistException("沒有此ID資料.(" + id + ")");
		}
	}

	/**
	 * 儲存資料
	 */
	@Override
	public void saveEntity(String msg) throws UnsupportedEncodingException,
			SQLException {
		String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
		Map log_dataMap = MoganLogger.getItemOrderChange(log_Id, this.getMainObj().getString("item_order_id"), this.getOpenUser(), this.getOpenUserIP());
		log_dataMap.put("varchar2", msg);
		mLogger.preLog(log_dataMap);

		Map conditionMap = new HashMap();
		Map dataMap = new HashMap();
		conditionMap.put("item_order_id", this.getMainObj().getString("item_order_id"));
		dataMap.putAll(this.getMainObj());
		logger.debug(entityName+" SAVE ... " + this.getMainObj());
		logger.info(entityName+ " SAVE ....");
		conn.update(CONN_ALIAS, "item_order", conditionMap, dataMap);

		mLogger.commitLog(log_dataMap);
	}

	/**
	 * 取得此訂單總費用 ，商品價格 X 數量
	 */
	public double getItemTotalCost() {
		String cost = this.getMainObj().getString("buy_price");
		String qty = this.getMainObj().getString("buy_unit");
		double totalCost = SysMath.mul(cost, qty);
		logger.info(ItemOrderEntity.entityName+" 取得商品總費用 ... "+totalCost);
		return totalCost;
	}

	/**
	 * 刪除訂單 付款狀態不改
	 * 
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	@Override
	public void delEntity(String msg) throws UnsupportedEncodingException,
			SQLException {
		String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
		Map log_dataMap = MoganLogger.getItemOrderDel(log_Id, this.getMainObj().getString("item_order_id"), this.getOpenUser(), this.getOpenUserIP());
		log_dataMap.put("varchar2", msg);
		mLogger.preLog(log_dataMap);
		Map conditionMap = new HashMap();
		Map dataMap = new HashMap();
		conditionMap.put("item_order_id", this.getMainObj().getString("item_order_id"));
		dataMap.put("delete_flag", "0");
		conn.update(CONN_ALIAS, "item_order", conditionMap, dataMap);
		logger.info(ItemOrderEntity.entityName+" 刪除訂單 ... ");
		mLogger.commitLog(log_dataMap);
	}

	/**
	 * 變更訂單狀態
	 * 
	 * @param status
	 * @param autoSave
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	public void changeStatus(String status, boolean autoSave)
			throws UnsupportedEncodingException, SQLException {
		this.setAttribute("order_status", status);
		if (autoSave) {
			logger.info(ItemOrderEntity.entityName+" 修改下標商品狀態 ... "+this.getAttribute("item_order_id")
					+ " to " + status);
			this.saveEntity();
		}
	}

	/**
	 * 變更所屬同賣家訂單，
	 * 
	 * @param newTideId
	 * @param autoSave
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	public void changeTide(String newTideId, boolean autoSave)
			throws UnsupportedEncodingException, SQLException {
		String log_Id = mLogger.getNewLogId();
		Map log_dataMap = MoganLogger.getItemOrderMoveTide(log_Id, (String) this.getAttribute("item_order_id"), newTideId, (String) this.getAttribute("tide_id"), this.getOpenUser(), this.getOpenUserIP());
		logger.info(ItemOrderEntity.entityName+" 修改下標商品所屬同賣家訂單 ... "+ this.getAttribute("item_order_id")
				+this.getAttribute("tide_id")+ " to " + newTideId+" ("+autoSave+")");
		this.setAttribute("tide_id", newTideId);
		if (autoSave) {
			mLogger.preLog(log_dataMap);
			this.saveEntity();
			mLogger.commitLog(log_dataMap);
		}
	}

	
	/**
	 * 讀取此商品的訊息
	 * @return
	 */
	public JSONArray loadMsg(){
		String orderBy="msg_date";
		String orderType="desc"; 
		JSONArray jArray = conn.queryJSONArray("mogan-DB", "SELECT * FROM view_item_contact_record_v1 WHERE "
				+ " item_order_id = '" + this.getAttribute("item_order_id") + "'  ORDER BY " + orderBy + // 排序欄位
				" " + orderType);// 排序方法
		return jArray;
	}
	
	/**
	 * 更新競標狀態為被取消
	 * 
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	public void canceled() throws UnsupportedEncodingException, SQLException {
		if (((String) this.getAttribute("order_status")).startsWith("3-")) {
			// 已得標
			this.setAttribute(ITEM_CANCELD_FLAG, "2");
		} else {
			// 競標中
			this.setAttribute(ITEM_CANCELD_FLAG, "1");
		}
		//MoganMessage.sendMsg("ICR-801", (String) SysKernel.getApplicationAttr(SysKernel.MOGAN_SWEET_ID), (String) SysKernel.getApplicationAttr(SysKernel.BID_ALERT_GROUP_ID),true, "商品出價取消","商品出價取消["+(String) this.getAttribute("item_order_id")+"]",(String) this.getAttribute("item_order_id"));
		logger.info(ItemOrderEntity.entityName+" 更新競標狀態為被取消 ... "+ this.getAttribute("item_order_id")
				+" ... "+this.getAttribute(ITEM_CANCELD_FLAG));
		this.saveEntity("order cancel");
	}

	/**
	 * 更新商品絡聯資訊
	 */
	public void updateItemContactMsg() {
		NetAgentYJV2 netAgentYJ = new NetAgentYJV2(this.getModelServletContext(), this.getAppId());
		try {
			JSONArray jArray = netAgentYJ.getItemContactMsg((String) this.getAttribute("item_id"), (String) this.getAttribute("buyer_account"), ((String) this.getAttribute("item_url")).split("http://")[1].split("\\.")[0], (String) this.getAttribute("seller_id"));
			for (int i = 0; i < jArray.size(); i++) {
				
				JSONObject jObj = jArray.getJSONObject(i);
				jObj.put("item_order_id", this.getAttribute("item_order_id"));
				Map conditionMap = new HashMap();
				conditionMap.put("item_order_id", this.getAttribute("item_order_id"));
				conditionMap.put("msg_id", jObj.getString("msg_id"));
				conn.newData(CONN_ALIAS, "item_contact_record", conditionMap, (Map) JSONObject.toBean(jObj, java.util.HashMap.class));
			}
			
			this.setAttribute("time_at_16", SysCalendar.getFormatDate(new Date(), SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql));
			
			logger.info(ItemOrderEntity.entityName+" 更新商品聯絡資訊 ... "+ this.getAttribute("item_order_id")+" ... "+this.getAttribute("time_at_16"));
			this.saveEntity("更新商品聯絡資訊");
		} catch (AccountNotExistException e) {
			logger.error(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 取得此訂單的聯絡記錄
	 * 
	 * @return
	 */
	public JSONArray getItemOrdersMsg() {
		String orderBy = "msg_date"; // 排序欄位
		String orderType = "DESC"; // 排序方式
		JSONArray jArray = conn.queryJSONArray("mogan-DB", "SELECT * FROM view_item_contact_record_v1 WHERE "
				+ " item_order_id = '"
				+ this.getAttribute("item_order_id")
				+ "' ORDER BY " + orderBy + // 排序欄位
				" " + orderType);// 排序方法
		logger.info(ItemOrderEntity.entityName+" 讀取此訂單的聯絡記錄... "+ this.getAttribute("item_order_id"));
		return jArray;
	}
	
	/**
	 * 訊息設為已讀
	 * @param msgId
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	public boolean readMsg(String msgId) throws UnsupportedEncodingException, SQLException{
		Map conditionMap = new HashMap();
		conditionMap.put("contact_id", msgId);
		conditionMap.put("item_order_id", this.getAttribute("item_order_id"));
		Map dataMap = new HashMap();
		dataMap.put("is_read", "1");
		dataMap.put("read_date", new Date());
		dataMap.put("note", this.getOpenUser());
		conn.update(CONN_ALIAS, "item_contact_record", conditionMap, dataMap);
		logger.info(ItemOrderEntity.entityName+" 將訊息設為已讀 ... "+ this.getAttribute("item_order_id")+"..." + msgId);
		return true;
	}

	/**
	 * 增加一筆連絡記錄
	 * 
	 * @param msgObj
	 * @return
	 */
	public boolean addContactMsg(JSONObject msgObj) {
		boolean result = false;

		return result;
	}

	/**
	 * 更新商品是否有Order Form,自動儲存
	 * 
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	public void updateItemData() throws UnsupportedEncodingException,
			SQLException {
		NetAgentYJV2 netAgentYJ = new NetAgentYJV2(this.getModelServletContext(), this.getAppId());
		try {
			String html = netAgentYJ.getItemPage((String) this.getAttribute("buyer_account"), (String) this.getAttribute("item_id"));
			// this.setAttribute(ITEM_CONTACT_TYPE, netAgentYJ.getItemContactType(html));
			if (netAgentYJ.hasItemOrderForm(html)) {
				this.setAttribute(ITEM_HAS_ORDER_FORM, "2");
			} else {
				this.setAttribute(ITEM_HAS_ORDER_FORM, "1");
			}
			String contactType=netAgentYJ.getItemContactType(html);
			if (contactType
					.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")) {
				this.setAttribute("flag_02", contactType);
				this.setAttribute("concat_type", "1");//email
			} else {
				if (contactType.equals(NetAgentYJV2.CONTACT_BOARD)) {
					this.setAttribute("concat_type", "0");// 留言版
				} else {
					this.setAttribute("concat_type", "2");//揭示版
				}
			}
			logger.info(ItemOrderEntity.entityName+" 更新商品是否有Order Form ... "+ this.getAttribute("item_order_id"));
			// 1=沒有order form
			// 2=有order form
			// 3=填完ORDER FORM 
			this.saveEntity("update order forem & contact type.");
		} catch (AccountNotExistException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 新版不用建立entity才能更新資料
	 * 
	 * @param itemOrderId
	 * @param itemId
	 * @param buyerAccount
	 */
	public static void updateItemData(String itemOrderId, String itemId,
			String buyerAccount) {
		/*
		 * NetAgentYJV2 netAgentYJ = new NetAgentYJV2(this.getModelServletContext(), this.getAppId());
		 */
		SysKernel.getConn();
	}

	/**
	 * 發送訊息給賣家
	 */
	public void sendMsg() {

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
