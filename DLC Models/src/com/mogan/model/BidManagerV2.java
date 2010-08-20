package com.mogan.model;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.entity.CurrencyEntity;
import com.mogan.entity.ItemOrderEntity;
import com.mogan.entity.ItemTideEntity;
import com.mogan.entity.MemberEntity;
import com.mogan.entity.RemitEntity;
import com.mogan.entity.SellerAccountEntity;
import com.mogan.entity.SellerEntity;
import com.mogan.exception.MoganException;
import com.mogan.exception.NonPrivilegeException;
import com.mogan.exception.entity.EntityNotExistException;
import com.mogan.exception.netAgent.AccountNotExistException;
import com.mogan.exception.privilege.PrivilegeException;
import com.mogan.log.MoganLogger;
import com.mogan.model.netAgent.NetAgentYJ;
import com.mogan.model.netAgent.NetAgentYJV2;
import com.mogan.sys.DBConn;
import com.mogan.sys.SysCalendar;
import com.mogan.sys.SysKernel;
import com.mogan.sys.SysMath;
import com.mogan.sys.SysPrivilege;
import com.mogan.sys.log.SysLogger4j;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;

/**
 * 訂單管理第二版 20100401 開始
 * 
 * @author Dian
 */
public class BidManagerV2 extends ProtoModel implements ServiceModelFace {
	static final String YAHOO_JP_WEBSITE_ID = "SWD-2009-0001";

	private static Logger logger = Logger.getLogger(BidManagerV2.class.getName());
	final static private String CONN_ALIAS = "mogan-DB";
	final static private String PRIVILEGE_VIEW = "view";
	final static private String PRIVILEGE_UP = "up";
	final static private String PRIVILEGE_DEL = "del";
	final static private String PRIVILEGE_ADD = "add";
	final static private String PRIVILEGE_MODEL_TIDE_MONEY = "SF-201005-07"; // 費用維護
	final static private String PRIVILEGE_MODEL_TIDE_STATE = "SF-201004-17"; // 訂單狀態管理
	final static private String PRIVILEGE_MODEL_SEND_MSG = "SF-201004-16"; // 聯絡賣家
	final static private String PRIVILEGE_MODEL_TIDE_DATA = "SF-201005-08"; // 訂單備忘管理

	/**
	 * 檢驗權限
	 * 
	 * @param action
	 *            動作
	 * @param modelId
	 *            權限ID
	 * @return true/false 是否擁有該權限
	 */
	@SuppressWarnings("finally")
	private boolean checkPrivilege(String action, String privilegeId) {
		try {
			if (this.getAppId().equals("26b782eb04abbd54efba0dcf854b158d")) {
				return true;
			}
			JSONObject privObj = (JSONObject) this.getSession().getAttribute("USER_PRIVILEGE");
			logger.debug((String) this.getSession().getAttribute("USER_ID")
					+ " : " + privObj.getJSONObject(privilegeId));
			logger.info((String) this.getSession().getAttribute("USER_ID")
					+ " : " + privilegeId + " : "
					+ privObj.getJSONObject(privilegeId).getBoolean(action));
			return privObj.getJSONObject(privilegeId).getBoolean(action);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		return false;
	}

	/**
	 * 程式進入點
	 */
	@Override
	public JSONArray doAction(Map<String, String> parameterMap)
			throws Exception {
		// TODO Auto-generated method stub
		if (this.getSession().isNew()
				|| this.getSession().getAttribute("USER_ID") == null) {
			logger.error("SESSION 已過期.");
			throw new Exception("SESSION 已過期.請重登入或重整畫面");
		}

		JSONArray jArray = new JSONArray();
		if (this.getAct().equals("LOAD_BID_ITEM_ORDERS")) {
			/**
			 * 讀取代標清單
			 */
			String page = parameterMap.get("START_INDEX");
			String size = parameterMap.get("PAGE_SIZE");
			String conditionKey = parameterMap.get("CONDITION_KEY");
			String statusCondit = parameterMap.get("STATUS_CONDITION");
			String checkSameSeller = parameterMap.get("CHECK_SAME_SELLER");
			String dir = parameterMap.get("DIR");
			String orderBy = parameterMap.get("ORDER_BY");
			jArray = loadBidIOs(page, size, conditionKey, statusCondit, checkSameSeller, dir, orderBy);
		} else if (this.getAct().equals("LOAD_TRADE_ORDER_DATA")) {
			String tideId = parameterMap.get("TIDE_ID");// 交易訂單號碼
			jArray = loadTideOder(tideId);
		} else if (this.getAct().equals("SAVE_TRADE_ORDER_DATA")) {
			JSONObject orderData = JSONObject.fromObject(parameterMap.get("ORDER_DATA"));
			saveTideOrder(orderData);
			jArray = loadTideOder(orderData.getString("tide_id"));
		} else if (this.getAct().equals("LOAD_MOVEABLE_ORDER")) {
			String sellerId = parameterMap.get("SELLER_ID");
			String memberId = parameterMap.get("MEMBER_ID");
			String tideId = parameterMap.get("TIDE_ID");
			jArray = getTideOrdersBySeller(sellerId, memberId, tideId);
		} else if (this.getAct().equals("MOVE_ITEM_2_ORDER")) {
			String itemOrderId = parameterMap.get("ITEM_ORDER_ID");
			String toTideId = parameterMap.get("TO_TIDE_ID");
			String fromTideId = parameterMap.get("FROM_TIDE_ID");
			String type = parameterMap.get("TYPE");
			jArray = moveItem2Order(itemOrderId, toTideId, fromTideId, type);
		} else if (this.getAct().equals("MOVE_ITEM_2_NEW_ORDER")) {
			String itemOrderId = parameterMap.get("ITEM_ORDER_ID");
			String fromTideId = parameterMap.get("FROM_TIDE_ID");
			jArray = moveItem2NewOrder(itemOrderId, fromTideId);
		} else if (this.getAct().equals("REFRESH_CONTACT_DATA")) {
			String itemOrderIds = parameterMap.get("ITEM_ORDER_IDS");
			String tideId = parameterMap.get("TIDE_ID");
			refreshContactData(itemOrderIds);
			jArray = getContactData(tideId, itemOrderIds);
		} else if (this.getAct().equals("SUBMIT_TRADE_ORDER_MONEY")) {
			JSONObject orderData = JSONObject.fromObject(parameterMap.get("ORDER_DATA"));
			String money = parameterMap.get("COMMIT_MONEY");
			saveTideOrder(orderData);
			submitOrderMoney(orderData, money);
		} else if (this.getAct().equals("SAVE_ALERT_DATA")) {
			JSONObject orderData = JSONObject.fromObject(parameterMap.get("ALERT_DATA"));
			String tideId = parameterMap.get("TIDE_ID");
			jArray = saveAlertData(tideId, orderData);
		} else if (this.getAct().equals("SEND_MESSAGE")) {
			jArray = sendMsg(JSONObject.fromObject(parameterMap.get("MSG_DATAS")));
		} else if (this.getAct().equals("DEL_TIDE")) {
			String delType = parameterMap.get("DEL_TYPE");
			String tideId = parameterMap.get("TIDE_ID");
			String msg = parameterMap.get("MSG");
			delTide(tideId, delType, msg);
			jArray = loadTideOder(tideId);
		} else if (this.getAct().equals("ADD_SELLER_ACCOUNT")) {
			String accountData = parameterMap.get("ACCOUNT_DATA");
			JSONObject jAccountData = JSONObject.fromObject(accountData);
			addSellerAccount(jAccountData);
			jArray = loadSellerData(jAccountData.getString("seller_id"));
		} else if (this.getAct().equals("LOAD_REMIT_TYPE")) {
			jArray = loadSellerAccount(parameterMap.get("SELLER_ID"));
		} else if (this.getAct().equals("SAVE_SELLER_DATA")) {
			JSONObject sellerData = JSONObject.fromObject(parameterMap.get("SELLER_DATA"));
			JSONArray sellerAcccounts = JSONArray.fromObject(parameterMap.get("ACCOUNT_DATA"));
			jArray.add(saveSellerData(sellerData, sellerAcccounts));
			jArray.add(loadSellerData(sellerData.getString("seller_id")));
			// jArray=loadSellerData(sellerData.getString("seller_id"));
		} else if (this.getAct().equals("READ_TRANSACTION_MSG")) {
			String contactId = (String) parameterMap.get("CONTACT_ID");
			String itemOrderId = (String) parameterMap.get("ITEM_ORDER_ID");
			// String tideId = (String) parameterMap.get("TIDE_ID");
			jArray = readMsg(itemOrderId, contactId);

			// jArray = setTransactionMsgReaded(itemOrderId,contactId);
		} else if (this.getAct().equals("BID_INFO")){
			String dateCondition=parameterMap.get("DATAS");
			jArray=getBidInfoByDate(dateCondition);
		}else if (this.getAct().equals("MSG_INFO")){
			String dateCondition=parameterMap.get("DATAS");
			jArray=getMsgInfoByDate(dateCondition);
		}
		return jArray;
	}
	
	/**
	 * 讀取訊息讀取狀況
	 * @param dateCondition
	 * @return
	 */
	private JSONArray getMsgInfoByDate(String dateCondition){
		JSONArray jArray = new JSONArray();
		DBConn conn = SysKernel.getConn();
		String sql="SELECT tmp.*,item_data.item_id from " +
				" (SELECT  io.item_order_id  , io.item_data_id, " +
				" SUM(IF(icr.is_read IS NOT NULL,1,0)) as total_count, " +
				" SUM(IF(icr.is_read=0,1,0)) AS msg_unread_count " +
				" FROM " +
				" item_order io LEFT JOIN item_contact_record icr on io.item_order_id = icr.item_order_id " +
				" WHERE " +
				" io.time_at_04 LIKE  '"+dateCondition+"%' AND io.order_status like '3%' group by  io.item_order_id ) tmp LEFT JOIN item_data on tmp.item_data_id = item_data.item_data_id";
		jArray=conn.queryJSONArray((String) SysKernel.getApplicationAttr(SysKernel.MAIN_DB), sql);
//		jArray.add(conn.queryJSONArray((String) SysKernel.getApplicationAttr(SysKernel.MAIN_DB), sql));
		return jArray;
	}
	
	/**
	 * 取得指定日期的競標資料
	 * @param dateCondition
	 * @return
	 */
	private JSONArray getBidInfoByDate(String dateCondition) {
		JSONArray jArray = new JSONArray();
		DBConn conn = SysKernel.getConn();
		String sql="SELECT DATE_FORMAT(time_at_04,'%Y_%m_%d') AS date_str," +
		" count(1) AS item_count"
		+ " FROM item_order "
		+ " WHERE item_order.time_at_04 LIKE  '"+dateCondition+"%' AND order_status like '3%' group by DAYOFMONTH(time_at_04)";
		jArray.add(conn.queryJSONArray((String) SysKernel.getApplicationAttr(SysKernel.MAIN_DB), sql));
		sql="SELECT  DATE_FORMAT(time_at_04,'%Y_%m_%d') AS date_str," +
				" SUM(IF(icr.is_read=1,1,0)) AS msg_count," +
				" SUM(IF(icr.is_read=0,1,0)) AS msg_unread_count" +
				" FROM " +
				" item_order io LEFT JOIN item_contact_record icr on io.item_order_id = icr.item_order_id" +
				" WHERE " +
				" io.time_at_04 LIKE  '"+dateCondition+"%' AND io.order_status like '3%' group by  DAYOFMONTH(time_at_04) ";
		jArray.add(conn.queryJSONArray((String) SysKernel.getApplicationAttr(SysKernel.MAIN_DB), sql));
		return jArray;
	}

	/**
	 * 將訊息設為已讀
	 * 
	 * @param contactId
	 * @return
	 * @throws Exception
	 */
	private JSONArray readMsg(String itemOrderId, String contactId)
			throws Exception {
		ItemOrderEntity ioEty = new ItemOrderEntity(this.getModelServletContext(), this.getSession(), itemOrderId);
		ioEty.readMsg(contactId);
		return ioEty.getItemOrdersMsg();
	}

	/**
	 * @param sellerId
	 * @return
	 * @throws EntityNotExistException
	 */
	private JSONArray loadSellerData(String sellerId)
			throws EntityNotExistException {
		JSONArray jArray = new JSONArray();
		SellerEntity sellEty = new SellerEntity(this.getModelServletContext(), this.getSession(), sellerId);
		jArray.add(sellEty.getSellerMainObj());
		jArray.add(sellEty.getSellerAccounts());
		return jArray;
	}

	/**
	 * 更新賣家資料
	 * 
	 * @param sellerDataStr
	 * @param sellerAcccountsStr
	 * @return
	 * @throws EntityNotExistException
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	private JSONObject saveSellerData(JSONObject sellerData,
			JSONArray sellerAcccounts) throws EntityNotExistException,
			UnsupportedEncodingException, SQLException {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
		SellerEntity sellEty = new SellerEntity(this.getModelServletContext(), this.getSession(), sellerData.getString("seller_id"));
		sellEty.getSellerMainObj().putAll(sellerData);
		JSONObject warnTide = new JSONObject(); // 被影響的訂單
		for (int i = 0; i < sellerAcccounts.size(); i++) {
			JSONObject tempObj = sellerAcccounts.getJSONObject(i);
			JSONArray tempWarnTide = new JSONArray();
			if (tempObj.getString("is_active").equals("1")) {
				tempWarnTide.addAll(conn.queryJSONArray(BidManagerV2.CONN_ALIAS, "SELECT tide_id FROM item_tide WHERE tide_status = '3-03' AND EXISTS (SELECT remit_id FROM remit_list WHERE remit_to = '"
						+ tempObj.getString("account_id")
						+ "' AND item_tide.remit_id=remit_list.remit_id )"));
			}
			warnTide.put(SellerAccountEntity.payTypeMap.get(tempObj.getString("remit_type"))
					+ " - "
					+ tempObj.getString("bank_name")
					+ " "
					+ tempObj.getString("account_no"), tempWarnTide);
			/**
			 * 將被影響的訂單找出，狀態在3-03的訂單會被影響，因為暫停對該帳戶匯帳
			 */
			sellEty.setSellerAccount(tempObj);
		}
		MoganLogger mLogger = new MoganLogger(conn);
		String logId = mLogger.getNewLogId();
		Map logDataMap = MoganLogger.getSellerDataSave(logId, sellerData.getString("seller_id"), (String) this.getSession().getAttribute("USER_ID"), (String) this.getSession().getAttribute("CLIENT_IP"));
		mLogger.preLog(logDataMap);
		sellEty.saveEntity();
		mLogger.commitLog(logDataMap);
		return warnTide;
	}

	/**
	 * 讀取賣家收款方式
	 * 
	 * @param sellerId
	 * @return
	 * @throws EntityNotExistException
	 */
	private JSONArray loadSellerAccount(String sellerId)
			throws EntityNotExistException {
		JSONArray jArray = new JSONArray();
		SellerEntity sellerEty = new SellerEntity(this.getModelServletContext(), this.getSession(), sellerId);
		jArray = sellerEty.getSellerAccounts();
		logger.info("讀取賣家收款方式");
		/*
		 * DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn"); //TODO 測試 jArray = conn.queryJSONArray(BidManagerV2.CONN_ALIAS, "SELECT account_id,remit_type,bank_name,branch_name,account_no,remit_value FROM view_item_seller_account_v1 WHERE seller_id='" + sellerId + "' AND is_active='0' "); // jObj.put("RemitCate", conn.queryJSONArray(BidManagerV2.CONN_ALIAS, "SELECT account_id,CONCAT(remit_type,CONCAT('-',bank_name)) as remit_value FROM item_seller_account WHERE seller_id='"+orderArray.getJSONObject(0).getString("seller_id")+"' AND is_active='0' "));
		 */
		return jArray;
	}

	/**
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 * @throws EntityNotExistException
	 */
	private JSONArray addSellerAccount(JSONObject jAccountData)
			throws UnsupportedEncodingException, SQLException,
			EntityNotExistException {
		JSONArray jArray = new JSONArray();

		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
		Map conditionMap = new HashMap();
		// SellerEntity sellerEty=new SellerEntity(this.getModelServletContext(),this.getSession(),(String) jAccountData.get("seller_id"));

		conditionMap.put("seller_id", jAccountData.get("seller_id")); // 賣家ID
		conditionMap.put("bank_name", jAccountData.get("bank_name")); // 銀行名稱
		// conditionMap.put("note", jAccountData.get("note")); //備註
		// conditionMap.put("account_name", jAccountData.get("account_name")); //帳戶名稱
		conditionMap.put("account_no", jAccountData.get("account_no")); // 帳戶編號
		// conditionMap.put("branch_name", jAccountData.get("branch_name")); //分行名稱
		// conditionMap.put("remit_type", jAccountData.get("remit_type")); //付款方法

		if (conn.queryJSONArray(CONN_ALIAS, "item_seller_account", conditionMap).size() > 0) {
			jArray.add("duplicate");
		} else {
			String logId = conn.getAutoNumber(CONN_ALIAS, "ISA-ID-01");
			Map dataMap = new HashMap();
			dataMap.put("account_id", logId); // 銀行名稱
			dataMap.put("bank_name", jAccountData.get("bank_name")); // 銀行名稱
			dataMap.put("note", jAccountData.get("note")); // 備註
			dataMap.put("account_name", jAccountData.get("account_name")); // 帳戶名稱
			dataMap.put("account_no", jAccountData.get("account_no")); // 帳戶編號
			dataMap.put("branch_name", jAccountData.get("branch_name")); // 分行名稱
			dataMap.put("remit_type", jAccountData.get("remit_type")); // 付款方法
			dataMap.put("is_active", "0"); // 付款方法
			dataMap.put("seller_id", jAccountData.get("seller_id")); // 賣家ID
			conn.newData(CONN_ALIAS, "item_seller_account", dataMap);
			jArray.add(logId);
		}

		return jArray;
	}

	/**
	 * 刪單或棄標操作,如果訂單已經進入已付款狀態，就無法刪除訂單或棄標
	 * 
	 * @param tideId
	 * @param delType
	 *            0-刪單 1-棄標
	 * @return
	 * @throws PrivilegeException
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 * @throws EntityNotExistException
	 */
	private JSONArray delTide(String tideId, String delType, String msg)
			throws PrivilegeException, UnsupportedEncodingException,
			SQLException, EntityNotExistException {
		if (!checkPrivilege(PRIVILEGE_UP, PRIVILEGE_MODEL_TIDE_STATE)
				|| !checkPrivilege(PRIVILEGE_DEL, PRIVILEGE_MODEL_TIDE_STATE)) {
			throw new PrivilegeException("無調整訂單狀態權限 - [" + PRIVILEGE_UP + ","
					+ PRIVILEGE_DEL + "]");
		}

		JSONArray jArray = new JSONArray();
		ItemTideEntity iTEty = new ItemTideEntity(this.getModelServletContext(), this.getSession(), tideId, ItemTideEntity.ITEM_TIDE_ID);
		/*
		 * if (!((String) iTEty.getAttribute("tide_status")).matches("3-0[123]")) { throw new PrivilegeException("訂單已付款，無法刪單或棄標!!"); }
		 */
		if (delType.equals("0")) { // 刪單
			iTEty.delTide(msg);
		} else if (delType.equals("1")) { // 棄標退還商品84.75%費用 訂單費用不退
			iTEty.giveupTide(msg);
		}
		return jArray;
	}

	/**
	 * 發送訊息給賣家
	 * 
	 * @param msgData
	 * @return
	 * @throws AccountNotExistException
	 * @throws PrivilegeException
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 * @throws EntityNotExistException
	 */
	private JSONArray sendMsg(JSONObject msgData)
			throws AccountNotExistException, PrivilegeException,
			UnsupportedEncodingException, SQLException, EntityNotExistException {
		if (!checkPrivilege(PRIVILEGE_ADD, PRIVILEGE_MODEL_SEND_MSG)) {
			throw new PrivilegeException("無發送訊息權限 - [" + PRIVILEGE_ADD + "]");
		}
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
		JSONArray jArray = new JSONArray();
		NetAgentYJV2 na = new NetAgentYJV2(this.getModelServletContext(), this.getAppId());
		int sendMethod = msgData.getInt("SEND_METHOD");
		boolean results = false;
		MoganLogger mLogger = new MoganLogger(conn);
		String logId = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
		Map logDataMap = MoganLogger.getSendMsg(logId, msgData.getString("ITEM_ORDER_ID"), sendMethod, (String) this.getSession().getAttribute("USER_ID"), (String) this.getSession().getAttribute("CLIENT_IP"));
		mLogger.preLog(logDataMap);

		ItemOrderEntity ioEty = new ItemOrderEntity(this.getModelServletContext(), this.getSession(), msgData.getString("ITEM_ORDER_ID"));

		if (sendMethod == 1) {
			results = na.sendMsg((String) ioEty.getAttribute("flag_02"), msgData.getString("SUBJECT_B"), msgData.getString("MSG"), sendMethod);
		} else {
			results = na.sendMsg(msgData.getString("ITEM_ORDER_ID"), msgData.getString("SUBJECT_A"), msgData.getString("MSG"), sendMethod);
		}

		logDataMap.put("varchar3", results);
		mLogger.commitLog(logDataMap);
		jArray.add(results);
		return jArray;
	}

	/**
	 * 儲存備忘資料
	 * 
	 * @param tideId
	 * @param alertData
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 * @throws PrivilegeException
	 * @throws EntityNotExistException
	 */
	private JSONArray saveAlertData(String tideId, JSONObject alertData)
			throws UnsupportedEncodingException, SQLException,
			PrivilegeException, EntityNotExistException {
		if (!checkPrivilege(PRIVILEGE_UP, PRIVILEGE_MODEL_TIDE_DATA)) {
			throw new PrivilegeException("無儲存備忘資料權限 - [" + PRIVILEGE_UP + "]");
		}
		JSONArray jArray = new JSONArray();
		ItemTideEntity itEty = new ItemTideEntity(this.getModelServletContext(), this.getSession(), tideId, ItemTideEntity.ITEM_TIDE_ID);
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
		String logId = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
		MoganLogger mLogger = new MoganLogger(conn);
		Map logDataMap = MoganLogger.getItemTideSaveAlert(logId, tideId, (String) this.getSession().getAttribute("USER_ID"), (String) this.getSession().getAttribute("CLIENT_IP"));
		mLogger.preLog(logDataMap);
		itEty.setAttribute(alertData.getString("alert_type"), alertData.getString("alert_text"));
		itEty.saveEntity();
		mLogger.commitLog(logDataMap);
		jArray.add(loadTideOder(tideId).getJSONObject(0).getJSONObject("Datas"));
		return jArray;
	}

	/**
	 * TODO 更新聯絡資料
	 * 
	 * @param itemOrderIds
	 *            String
	 * @return
	 */
	private JSONArray refreshContactData(String itemOrderIds) {
		JSONArray jArray = new JSONArray();
		JSONArray ios = JSONArray.fromObject(itemOrderIds);
		for (int i = 0; i < ios.size(); i++) {
			String itemOrderId = ios.getString(i);
			logger.info("refreshContactData : " + itemOrderId + " start.");
			try {
				ItemOrderEntity ioEty = new ItemOrderEntity(this.getModelServletContext(), this.getSession(), itemOrderId);
				ioEty.updateItemContactMsg();
				ioEty.updateItemData();
			} catch (EntityNotExistException e) {
				logger.error(e.getMessage(), e);
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
			logger.info("refreshContactData : " + itemOrderId + " end.");
		}
		return jArray;
	}

	/**
	 * 更新聯絡資料
	 * 
	 * @param itemOrderIds
	 *            JSONArray
	 * @return
	 */
	private JSONArray refreshContactData(JSONArray itemOrderIds) {
		JSONArray ids = new JSONArray();
		for (int i = 0; i < itemOrderIds.size(); i++) {
			ids.add(itemOrderIds.getJSONObject(i).get("item_order_id"));
		}
		return refreshContactData(ids.toString());
	}

	/**
	 * 取得聯絡資料
	 * 
	 * @param tideId
	 * @param itemOrderIds
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 * @throws EntityNotExistException
	 */
	@Deprecated
	private JSONArray getContactData(String tideId, String itemOrderIds)
			throws EntityNotExistException, UnsupportedEncodingException,
			SQLException {
		JSONArray jArray = new JSONArray();
		JSONObject jObj = new JSONObject();
		ItemTideEntity itEty = new ItemTideEntity(this.getModelServletContext(), this.getSession(), tideId, ItemTideEntity.ITEM_TIDE_ID);
		jObj.put("TideId", tideId);
		jObj.put("Datas", itEty.getItemOrdersMsg(JSONArray.fromObject(itemOrderIds)));
		// jObj.put("Datas", na.getItemContactMsgFromDB(JSONArray.fromObject(itemOrderIds)));
		jObj.put("Records", jObj.getJSONArray("Datas").size());
		jArray.add(jObj);
		return jArray;
	}

	/**
	 * 取得聯絡資料
	 * 
	 * @param tideId
	 * @param itemOrderIds
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 * @throws EntityNotExistException
	 */
	@Deprecated
	private JSONArray getContactData(String tideId, JSONArray itemOrderIds)
			throws EntityNotExistException, UnsupportedEncodingException,
			SQLException {
		JSONArray ids = new JSONArray();
		for (int i = 0; i < itemOrderIds.size(); i++) {
			ids.add(itemOrderIds.getJSONObject(i).get("item_order_id"));
		}
		return getContactData(tideId, ids.toString());
	}

	/**
	 * 訂單金額確定， 1. 將訂單金額寫入remit 2. 將訂單狀態切換到待匯款3-03
	 * 
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 * @throws PrivilegeException
	 * @throws EntityNotExistException
	 */
	private JSONArray submitOrderMoney(JSONObject orderData, String money)
			throws UnsupportedEncodingException, SQLException,
			PrivilegeException, EntityNotExistException {
		if (!checkPrivilege(PRIVILEGE_UP, PRIVILEGE_MODEL_TIDE_STATE)) {
			throw new PrivilegeException("無改變訂單狀態權限 - [" + PRIVILEGE_UP + "]");
		}
		JSONArray jArray = new JSONArray();
		RemitEntity rmtEny = new RemitEntity(this.getModelServletContext(), this.getSession(), orderData.getString("tide_id"), RemitEntity.TIDE_ID);
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");

		long tatolPrice = orderData.getLong("item_total_price");
		tatolPrice += orderData.getLong("cost_3");
		tatolPrice += orderData.getLong("cost_4");
		tatolPrice += orderData.getLong("cost_6");

		rmtEny.setAttribute("remit_classify", "RL-901");
		rmtEny.setAttribute("note", tatolPrice);
		rmtEny.setAttribute("money", money);
		rmtEny.setAttribute("remit_to", orderData.getString("remit_to"));
		rmtEny.setAttribute("creator", this.getSession().getAttribute("USER_ID"));

		// 建立log資料
		String logId = conn.getAutoNumber(BidManagerV2.CONN_ALIAS, "LR-ID-01");
		MoganLogger mLogger = new MoganLogger(conn);
		Map logDataMap = MoganLogger.getItemTideSubmitMoeny(logId, orderData.getString("tide_id"), String.valueOf(tatolPrice), (String) this.getSession().getAttribute("USER_ID"), (String) this.getSession().getAttribute("CLIENT_IP"));
		mLogger.preLog(logDataMap); // 建立log資料 開始修改資料
		rmtEny.saveEntity();

		ItemTideEntity itEty = new ItemTideEntity(this.getModelServletContext(), this.getSession(), orderData.getString("tide_id"), ItemTideEntity.ITEM_TIDE_ID);
		if (rmtEny.getAttribute("remit_type").equals("RL-802")) {
			itEty.changeStatus(ItemTideEntity.STATUS_3_04, true);
		} else {
			itEty.changeStatus(ItemTideEntity.STATUS_3_03, true);
		}

		mLogger.commitLog(logDataMap); // 建立log資料 資料修改完成

		return jArray;
	}

	/**
	 * 刪除多餘訂單
	 * 
	 * @param tideId
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 * @throws PrivilegeException
	 * @throws EntityNotExistException
	 */
	private void deleteOrder(String tideId)
			throws UnsupportedEncodingException, SQLException,
			PrivilegeException, EntityNotExistException {
		if (!checkPrivilege(PRIVILEGE_DEL, PRIVILEGE_MODEL_TIDE_STATE)) {
			throw new PrivilegeException("無刪除訂單權限 - [" + PRIVILEGE_DEL + "]");
		}
		ItemTideEntity itEty = new ItemTideEntity(this.getModelServletContext(), this.getSession(), tideId, ItemTideEntity.ITEM_TIDE_ID);
		if (itEty.getAttribute("items_count").equals("0")) {
			itEty.delEntity();
		}
	}

	/**
	 * 將得標商品轉換到新訂單
	 * 
	 * @param itemOrderId
	 * @param fromTideId
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 * @throws PrivilegeException
	 * @throws EntityNotExistException
	 */
	private JSONArray moveItem2NewOrder(String itemOrderId, String fromTideId)
			throws UnsupportedEncodingException, SQLException,
			PrivilegeException, EntityNotExistException {
		if (!checkPrivilege(PRIVILEGE_DEL, PRIVILEGE_MODEL_TIDE_STATE)
				|| !checkPrivilege(PRIVILEGE_UP, PRIVILEGE_MODEL_TIDE_STATE)
				|| !checkPrivilege(PRIVILEGE_ADD, PRIVILEGE_MODEL_TIDE_STATE)) {
			throw new PrivilegeException("無調整訂單權限 - [" + PRIVILEGE_DEL + ","
					+ PRIVILEGE_UP + "," + PRIVILEGE_ADD + "]");
		}
		JSONArray jArray = new JSONArray();

		ItemTideEntity oldItEty = new ItemTideEntity(this.getModelServletContext(), this.getSession(), fromTideId, ItemTideEntity.ITEM_TIDE_ID);
		ItemTideEntity newItEty = oldItEty.cloneEty();

		ItemOrderEntity ioEty = new ItemOrderEntity(this.getModelServletContext(), this.getSession(), itemOrderId);
		ioEty.changeTide((String) newItEty.getAttribute("tide_id"), true);
		newItEty.setAttribute("item_order_id", itemOrderId);
		newItEty.saveEntity("更新 建立新訂單");
		oldItEty.saveEntity("商品移出同訂單");
		newItEty = null;
		oldItEty = null;
		deleteOrder(fromTideId);

		return jArray;
	}

	/**
	 * 移動訂單
	 * 
	 * @param itemOrderId
	 * @param tideId
	 *            新item_tide
	 * @param fromTideId
	 *            舊item_tide
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 * @throws PrivilegeException
	 * @throws EntityNotExistException
	 */
	private JSONArray moveItem2Order(String itemOrderId, String tideId,
			String fromTideId, String type)
			throws UnsupportedEncodingException, SQLException,
			PrivilegeException, EntityNotExistException {
		if (!checkPrivilege(PRIVILEGE_DEL, PRIVILEGE_MODEL_TIDE_STATE)
				|| !checkPrivilege(PRIVILEGE_UP, PRIVILEGE_MODEL_TIDE_STATE)) {
			throw new PrivilegeException("無調整訂單權限 - [" + PRIVILEGE_DEL + ","
					+ PRIVILEGE_UP + "]");
		}
		JSONArray jArray = new JSONArray();
		logger.info("moveItem2Order...." + type);
		if (type.equals("tide")) {
			ItemTideEntity itEty = new ItemTideEntity(this.getModelServletContext(), this.getSession(), fromTideId, ItemTideEntity.ITEM_TIDE_ID);
			JSONArray ioArray = itEty.getItemOrders();
			for (int i = 0; i < ioArray.size(); i++) {
				moveItem2Order(ioArray.getJSONObject(i).getString("item_order_id"), tideId, fromTideId, "item");
			}
		} else {
			ItemOrderEntity ioEty = new ItemOrderEntity(this.getModelServletContext(), this.getSession(), itemOrderId);
			ioEty.changeTide(tideId, true);
			ItemTideEntity itEty = new ItemTideEntity(this.getModelServletContext(), this.getSession(), tideId, ItemTideEntity.ITEM_TIDE_ID);
			itEty.saveEntity("商品移入訂單");
			itEty = new ItemTideEntity(this.getModelServletContext(), this.getSession(), fromTideId, ItemTideEntity.ITEM_TIDE_ID);
			itEty.saveEntity("商品移出訂單");
			itEty = null;
		}

		deleteOrder(fromTideId);
		jArray.add("1");
		return jArray;
	}

	/**
	 * 取得同賣家尚未關閉的訂單
	 * 
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 * @throws EntityNotExistException
	 */
	private JSONArray getTideOrdersBySeller(String sellerId, String memberId,
			String tideId) throws EntityNotExistException,
			UnsupportedEncodingException, SQLException {
		JSONArray jArray = new JSONArray();
		ItemTideEntity itEty = new ItemTideEntity(this.getModelServletContext(), this.getSession(), tideId, ItemTideEntity.ITEM_TIDE_ID);
		// 取得同賣家及同會員的訂單
		jArray = itEty.getUnCloseTide(ItemTideEntity.SAME_MEMBER
				+ ItemTideEntity.SAME_SELLER);
		return jArray;
	}

	/**
	 * 儲存訂單資料，主要為各項費用，付款方式
	 * 
	 * @param orderObj
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 * @throws PrivilegeException
	 * @throws EntityNotExistException
	 */
	private JSONArray saveTideOrder(JSONObject orderObj)
			throws UnsupportedEncodingException, SQLException,
			PrivilegeException, EntityNotExistException {
		if (!checkPrivilege(PRIVILEGE_UP, PRIVILEGE_MODEL_TIDE_MONEY)) {
			throw new PrivilegeException("無調整訂單費用權限 - [" + PRIVILEGE_UP + "]");
		}
		JSONArray jArray = new JSONArray();

		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");

		MoganLogger mLogger = new MoganLogger(conn);
		String logId = conn.getAutoNumber(BidManagerV2.CONN_ALIAS, "LR-ID-01");
		Map logDataMap = MoganLogger.getItemTideSaveMoney(logId, orderObj.getString("tide_id"), (String) this.getSession().getAttribute("USER_ID"), (String) this.getSession().getAttribute("CLIENT_IP"));
		mLogger.preLog(logDataMap);

		ItemTideEntity itEty = new ItemTideEntity(this.getModelServletContext(), this.getSession(), orderObj.getString("tide_id"), ItemTideEntity.ITEM_TIDE_ID);

		RemitEntity rmEty = itEty.getRemitEntity();
		rmEty.setAttribute("remit_to", orderObj.getString("remit_to"));

		rmEty.saveEntity();

		itEty.setAttribute("cost_1", orderObj.optDouble("cost_1", 0.0));
		itEty.setAttribute("cost_1", orderObj.optDouble("cost_1", 0.0)); // 手續費(日)
		itEty.setAttribute("cost_2", orderObj.optDouble("cost_2", 0.0)); // 匯費
		itEty.setAttribute("cost_3", orderObj.optDouble("cost_3", 0.0)); // 稅金
		itEty.setAttribute("cost_4", orderObj.optDouble("cost_4", 0.0)); // 當地運費
		// itEty.setAttribute("cost_5", orderObj.optDouble("cost_5", 0.0)); // 國際運費(日)
		itEty.setAttribute("cost_6", orderObj.optDouble("cost_6", 0.0)); // 其他費用
		// itEty.setAttribute("cost_7", orderObj.optDouble("cost_7", 0.0)); // 包裝費用
		// itEty.setAttribute("cost_8", orderObj.optDouble("cost_8", 0.0)); // 手續費(台)
		// itEty.setAttribute("cost_9", orderObj.optDouble("cost_9", 0.0)); // 國際運費(台)
		itEty.setAttribute("cost_10", orderObj.optDouble("cost_10", 0.0));
		itEty.setAttribute("money_alert", orderObj.getString("money_alert"));// 金流備註
		itEty.setAttribute("ship_type", orderObj.optString("ship_type"));// 結清狀態

		itEty.saveEntity();
		mLogger.commitLog(logDataMap);

		jArray.add("1");
		return jArray;
	}

	/**
	 * 讀取訂單資料
	 * 
	 * @param tideId
	 * @return
	 * @throws EntityNotExistException
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	private JSONArray loadTideOder(String tideId)
			throws EntityNotExistException, UnsupportedEncodingException,
			SQLException {

		JSONArray jArray = new JSONArray();
		JSONObject jObj = new JSONObject();
		ItemTideEntity itEty = new ItemTideEntity(this.getModelServletContext(), this.getSession(), tideId, ItemTideEntity.ITEM_TIDE_ID);
		jObj.put("Datas", itEty.getMainObj());
		jObj.put("ItemList", itEty.getItemOrders());
		jObj.put("Records", jObj.getJSONArray("ItemList").size());
		jObj.put("Msgs", itEty.getItemOrdersMsg());
		jObj.put("MsgRecords", jObj.getJSONArray("Msgs").size());
		Map conditionMap = new HashMap();
		conditionMap.put("member_id", itEty.getAttribute("member_id"));
		conditionMap.put("seller_id", itEty.getAttribute("seller_id"));
		try {
			jObj.put("TideCount", itEty.getTideCount(conditionMap));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		SellerEntity sellEty = new SellerEntity(this.getModelServletContext(), this.getSession(), (String) itEty.getAttribute("seller_id"));
		jObj.put("SellerData", sellEty.getSellerMainObj());
		jObj.put("SellerAccounts", sellEty.getSellerAccounts());
		jObj.put("Logs", itEty.getlogs(1));
		jArray.add(jObj);
		return jArray;
	}

	/**
	 * 讀取代標清單
	 * 
	 * @param page
	 *            資料頁數
	 * @param size
	 *            資料筆數
	 * @param conditionKey
	 *            包含SEARCH_KEY-關鍵字，ACCOUNT_ID-下標帳號
	 * @param statusCondit
	 *            篩選訂單狀態
	 * @param checkSameSeller
	 *            是否顯示同賣家商品
	 * @param dir
	 *            排序方法
	 * @param orderBy
	 *            排序欄位
	 * @return
	 * @throws NonPrivilegeException
	 */
	private JSONArray loadBidIOs(String page, String size, String conditionKey,
			String statusCondit, String checkSameSeller, String dir,
			String orderBy) throws NonPrivilegeException {

		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
		logger.info(conditionKey);
		JSONArray jArray = new JSONArray();
		JSONObject conditObj = JSONObject.fromObject(conditionKey);
		JSONObject jObj = new JSONObject();
		int startIndex = Integer.parseInt(page);
		int pageSize = Integer.parseInt(size);
		JSONArray statusJArray = JSONArray.fromObject(statusCondit);

		boolean isPowerSearch = conditObj.getBoolean("IS_POWER_SEARCH");
		String sql = "SELECT " +
		// "o_varchar02,buy_price,seller_account,item_name,item_id,time_at_04,item_order_id,buyer_account,full_name,name,tide_status,tide_id,o_varchar01,website_id,new_msg" +
				" * " + " FROM view_bid_item_order_v2";
		if (sql.endsWith("view_bid_item_order_v2")) {
			sql += " WHERE ";
		}
		if (!isPowerSearch) {
			sql += " delete_flag = 1 ";
		} else {
			sql += " (delete_flag = 1 or delete_flag = 0) ";
		}

		String serchKey = null;
		if (conditObj.containsKey("SEARCH_KEY")) {
			serchKey = conditObj.getString("SEARCH_KEY");
		}

		if (serchKey != null && serchKey.length() > 0) {
			/*
			 * 商品ID * 商品名稱 * 賣家名稱 * 聯絡訊息 訂單ID 下標帳號 * 會員帳號 商品所在地
			 */
			StringBuffer keywordSql = new StringBuffer();
			keywordSql.append(" item_id LIKE '%" + serchKey + "%' ");
			keywordSql.append(" OR ");
			keywordSql.append(" item_name LIKE '%" + serchKey + "%' ");
			keywordSql.append(" OR ");
			keywordSql.append(" seller_account LIKE '%" + serchKey + "%' ");
			keywordSql.append(" OR ");
			// keywordSql.append(" e_varchar05 LIKE '%" + serchKey + "%' ");
			// keywordSql.append(" OR ");
			keywordSql.append(" tide_id LIKE '%" + serchKey + "%' ");
			keywordSql.append(" OR ");
			// keywordSql.append(" buyer_account LIKE '%" + serchKey + "%' ");
			// keywordSql.append(" OR ");
			keywordSql.append(" full_name LIKE '%" + serchKey + "%' ");
			keywordSql.append(" OR ");
			keywordSql.append(" item_order_id LIKE '%" + serchKey + "%' ");
			sql += " AND ( " + keywordSql + " )";
		}

		if (conditObj.containsKey("ACCOUNT_ID")
				&& conditObj.getString("ACCOUNT_ID").length() > 0) {
			sql += " AND ( bid_id = '" + conditObj.getString("ACCOUNT_ID")
					+ "' )";
		}

		if (!isPowerSearch && statusJArray != null && statusJArray.size() > 0) {
			StringBuffer statusSql = new StringBuffer();
			// statusKey
			for (int i = 0; i < statusJArray.size(); i++) {
				if (statusJArray.getJSONObject(i).getBoolean("value")) {
					if (statusSql.length() > 0) {
						statusSql.append(" OR ");
					}
					statusSql.append(" tide_status LIKE '"
							+ statusJArray.getJSONObject(i).getString("key")
							+ "' ");
					// statusSql.append(" order_status LIKE '"+ statusJArray.getJSONObject(i).getString("key")+ "' ");
				}
			}
			sql += " AND ( " + statusSql + " )";
		}

		if (!isPowerSearch) {
			if (orderBy != null) {
				sql += " ORDER BY " + orderBy;
			}
			if (dir != null) {
				sql += " " + dir;
			}
		}

		logger.info(sql);
		jObj.put("Datas", conn.queryJSONArrayWithPage(BidManagerV2.CONN_ALIAS, sql, startIndex, pageSize));
		jObj.put("Records", conn.getQueryDataSize(BidManagerV2.CONN_ALIAS, sql));
		fixDataColor(jObj.getJSONArray("Datas"));

		jArray.add(jObj);
		return jArray;
	}

	/**
	 * 修正顯示顏色
	 * 
	 * @param jArray
	 * @return
	 */
	private JSONArray fixDataColor(JSONArray jArray) {
		int sellerColorIndex = 0;
		int memberColorIndex = 0;
		int orderColorIndex = 0;
		JSONObject dataObj = new JSONObject();
		Map sellerCooloryMap = new HashMap();
		Map memberCooloryMap = new HashMap();
		Map orderCooloryMap = new HashMap();

		for (int i = 0; i < jArray.size(); i++) {
			dataObj = jArray.getJSONObject(i);
			String sellerKey = dataObj.getString("seller_account") + "_"
					+ dataObj.getString("website_id");
			String memberKey = dataObj.getString("member_id") + "_"
					+ dataObj.getString("website_id");
			String orderKey = dataObj.getString("tide_id");

			if (sellerCooloryMap.containsKey(sellerKey)) {
			} else {
				sellerCooloryMap.put(sellerKey, ++sellerColorIndex);
			}

			if (memberCooloryMap.containsKey(memberKey)) {
			} else {
				memberCooloryMap.put(memberKey, ++memberColorIndex);
			}

			if (orderCooloryMap.containsKey(orderKey)) {
			} else {
				orderCooloryMap.put(orderKey, ++orderColorIndex);
			}
			dataObj.put("sellerColor", sellerCooloryMap.get(sellerKey));
			dataObj.put("memberColor", memberCooloryMap.get(memberKey));
			dataObj.put("orderColor", orderCooloryMap.get(orderKey));
		}
		return jArray;
	}
}
