package com.mogan.model;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import com.mogan.dataBean.BidItemOrderBean;
import com.mogan.exception.netAgent.AccountNotExistException;
import com.mogan.io.FileIO;
import com.mogan.model.netAgent.NetAgentYJ;
import com.mogan.sys.DBConn;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;

/**
 * <div style="background-color:#FBB117"> WEB_SITE_ID清單：<br /> 1=日本雅虎 </div> Model name=BidManager
 * 
 * @author user
 */
public class BidManager extends ProtoModel implements ServiceModelFace {
	static final String YAHOO_JP_WEBSITE_ID = "SWD-2009-0001";

	/**
	 * <p>
	 * <font size=7 color=red>取得出價履歷，ACTION = GET_BID_LIST</font>
	 * </p>
	 * 
	 * @param webSiteId
	 *            <font color=red>WEB_SITE_ID </font> 網站ID
	 * @param bidAccount
	 *            <font color=red>BID_ACCOUNT</font> 下標或上架帳號
	 * @param itemId
	 *            <font color=red>ITEM_ID</font> 網站商品ID
	 * @param listType
	 *            <font color=red>LIST_TYPE</font> 清單類型
	 *            <ul>
	 *            TOP_LIST 簡易清單
	 *            </ul>
	 *            <ul>
	 *            LOG_LIST完整清單
	 *            </ul>
	 * @param page
	 *            <font color=red>PAGE</font> 頁數
	 * @return
	 * @throws Exception
	 */
	private JSONArray getBidList(String webSiteId, String bidAccount,
			String itemId, String listType, String page) throws Exception {
		JSONArray jArray = new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
					this.getAppId());
			jArray = agentYJ.getBidList(bidAccount, itemId, listType, page);
		}
		return jArray;
	}

	/**
	 * <p>
	 * <font size=7 color=red>取得高最出價者的帳號，ACTION = GET_HIGH_PRICE_ACCOUNT</font>
	 * </p>
	 * 
	 * @param webSiteId
	 *            <font color=red>WEB_SITE_ID </font> 網站ID
	 * @param bidAccount
	 *            <font color=red>BID_ACCOUNT</font> 下標帳號
	 * @param itemId
	 *            <font color=red>ITEM_ID</font> 網站商品ID
	 * @param page
	 *            <font color=red>PAGE</font> 頁數
	 * @return 最高出價者ID
	 * @throws Exception
	 */
	private JSONArray getHighPriceAccount(String webSiteId, String bidAccount,
			String itemId, String page) throws Exception {
		// TODO 商品發問
		JSONArray jArray = new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
					this.getAppId());
			jArray = agentYJ.getHighPriceAccount(bidAccount, itemId, page);
		}
		return jArray;
	}

	/**
	 * <p>
	 * <font size=7 color=red>對商品發問，ACTION = ASK_QUESTION</font>
	 * </p>
	 * 
	 * @param webSiteId
	 *            <font color=red>WEB_SITE_ID</font> 網站ID
	 * @param questAccount
	 *            <font color=red>AGENT_ACCOUNT</font> 發問的帳號
	 * @param itemId
	 *            <font color=red>ITEM_ID</font> 網站商品ID
	 * @param question
	 *            <font color=red></font> QUESTION問題
	 * @return
	 * @throws Exception
	 */
	private JSONArray askQusetion(String webSiteId, String agentAccount,
			String itemId, String question) throws Exception {
		// TODO 商品發問
		JSONArray jArray = new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
					this.getAppId());
			jArray = agentYJ.askQuestion(agentAccount, itemId, question);
		}
		return jArray;
	}

	/**
	 * <P>
	 * <font size=7 color=red>代拍下架，ACTION =UN_POST_ITEM</font>
	 * </P>
	 * 
	 * @param itemOrderId
	 *            <font color=red>[ITEM_ORDER_ID]</font>
	 * @return 0-失敗，1-成功
	 * @throws Exception
	 */
	private JSONArray unpostItem(String itemOrderId) throws Exception {
		// TODO 重刊商品
		JSONArray jArray = new JSONArray();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		ArrayList itemDatas = conn.query("mogan-DB",
				"SELECT * FROM view_sell_item_order WHERE item_order_id='"
						+ itemOrderId + "'");
		if (itemDatas.size() > 0) {
			Map itemMap = (Map) itemDatas.get(0);
			String webSiteId = (String) itemMap.get("website_id");
			if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {// 日本雅虎
				NetAgentYJ agentYJ = new NetAgentYJ(this
						.getModelServletContext(), this.getAppId());
				agentYJ.setProperties(this.getProperties());
				jArray = agentYJ.unsellItem(itemOrderId);
			}
		}
		return jArray;
	}

	/**
	 * <P>
	 * <font size=7 color=red>重新上刊商品，針對已上刊過的商品，再次上刊，ACTION = RE_POST_ITEM</font>
	 * </P>
	 * 
	 * @param itemOrderId
	 *            <font color=red>[ITEM_ORDER_ID]</font>
	 * @return 0-失敗，其他數字為yahoo 商品 ID
	 * @throws Exception
	 */
	private JSONArray repostItem(String itemOrderId) throws Exception {
		// TODO 重刊商品
		JSONArray jArray = new JSONArray();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		ArrayList itemDatas = conn.query("mogan-DB",
				"SELECT * FROM view_sell_item_order WHERE item_order_id='"
						+ itemOrderId + "'");
		if (itemDatas.size() > 0) {
			Map itemMap = (Map) itemDatas.get(0);
			String webSiteId = (String) itemMap.get("website_id");
			if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {// 日本雅虎
				NetAgentYJ agentYJ = new NetAgentYJ(this
						.getModelServletContext(), this.getAppId());
				agentYJ.setProperties(this.getProperties());
				jArray = agentYJ.resellItem(itemOrderId);
			}
		}
		return jArray;
	}

	/**
	 * <P>
	 * <font size=7 color=red>上刊商品，ACTION = POST_ITEM</font>
	 * </P>
	 * 
	 * @param itemOrderId
	 *            <font color=red>[ITEM_ORDER_ID]</font>
	 * @return 0-失敗，其他數字為yahoo 商品 ID
	 * @throws Exception
	 */
	private JSONArray postItem(String itemOrderId) throws Exception {
		// TODO 重刊商品
		JSONArray jArray = new JSONArray();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		ArrayList itemDatas = conn.query("mogan-DB",
				"SELECT * FROM view_sell_item_order WHERE item_order_id='"
						+ itemOrderId + "'");
		if (itemDatas.size() > 0) {
			Map itemMap = (Map) itemDatas.get(0);
			String webSiteId = (String) itemMap.get("website_id");
			if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {// 日本雅虎
				NetAgentYJ agentYJ = new NetAgentYJ(this
						.getModelServletContext(), this.getAppId());
				agentYJ.setProperties(this.getProperties());
				jArray = agentYJ.sellItem(itemOrderId);
			}
		}
		return jArray;
	}

	/**
	 * @param webSiteId
	 * @param orderId
	 * @param jObj
	 * @return
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 */
	public JSONArray saveOrderInfo(String webSiteId, String orderId,
			JSONObject jObj) throws UnsupportedEncodingException, SQLException {
		JSONArray jArray = new JSONArray();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");

		StringBuffer dataBuffer = new StringBuffer();
		Iterator it = jObj.keys();
		for (; it.hasNext();) {
			String tempKey = (String) it.next();
			if (dataBuffer.length() > 0) {
				dataBuffer.append(" , ");
			}
			dataBuffer.append(tempKey + "='" + jObj.getString(tempKey) + "'");
		}
		String sql = "UPDATE web_won SET " + dataBuffer + " WHERE ID="
				+ orderId;
		conn.executSql("mogan-tw", sql);
		return jArray;
	}

	/**
	 * <P>
	 * <font size=7 color=red>查詢資料，ACTION = LOAD_ITEMS</font>
	 * </P>
	 * 
	 * @param dataClass
	 *            資料類型
	 * @param startIndex
	 *            開始筆數
	 * @param pageSize
	 *            查詢筆數
	 * @param statusCondition
	 *            狀態條件
	 * @param condition
	 *            篩選條件
	 * @param orderBy
	 *            排序欄位
	 * @param dir
	 *            排序方法
	 * @return
	 */
	public JSONArray loadItems(String dataClass, int startIndex, int pageSize,
			String statusCondition, String condition, String orderBy, String dir) {
		JSONArray jArray = new JSONArray();
		JSONArray dataArray = new JSONArray();
		JSONObject jObj = new JSONObject();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		// 得標會員帳號 user_name
		// 摩根得標編號 no
		// 結標日 end_date
		// 商品ID item_id
		// 商品名 item
		// 賣家帳號 sell_name
		// 賣家mail
		// 消費稅 tax
		// 落札価格 costed
		// 運費(日本,日$) locally
		// 匯款費用(日$) remittance
		// 合計
		// 匯款帳戶
		// 匯款進度 status 0已得標未處理 1已取得賣家連絡資料 2完成匯款 3日本收貨完成 4日本出貨完成 5台灣收貨完成 6會員結帳完成 7台灣出貨完成
		// 下標帳號 jyahooid

		
		// String sql = "SELECT user_name,item,item_id ,no, end_date,sell_name,costed,status,jyahooid FROM web_bidding ";
		String sql = "";
		if (dataClass.equals("SELL_ITEM")) {
			sql = "SELECT * FROM view_sell_item_order";
		} else if (dataClass.equals("BUY_ITEM")) {
			sql = "SELECT id,user_name,item,item_id ,item_order_id, end_date,sell_name,tax,costed,locally,remittance,status,agent_account,contact_type,title,renote,total_item,total_unpay,total_unship,order_form_status,realname FROM view_bid_item_order_v1 ";
		}

		// 判斷要顯示的狀態
		boolean statusConditionFlag = true;

		for (int i = 0; i < statusCondition.length() - 1; i++) {
			// String aNum=statusCondition.substring(i,1);
			int aNum = Integer.parseInt(statusCondition.substring(i, i + 1));
			int bNum = Integer
					.parseInt(statusCondition.substring(i + 1, i + 2));
			if (bNum != aNum + 1) {
				statusConditionFlag = false;
			}
		}

		// 轉換為SQL語法
		String whereSql = "";
		if (statusCondition.length() == 0) {
			// 沒有statusCondition，在前就應該擋下，不該傳入
			// whereSql = " status is null";
		} else {
			// 有statusCondition，判斷statusConditionFlag(是否為連號)
			if (statusConditionFlag) {
				// 屬於連續號碼，直接用<>來找出目標
				whereSql = " status >= "
						+ statusCondition.substring(0, 1)
						+ " and status<= "
						+ statusCondition
								.substring(statusCondition.length() - 1);
			} else {
				// 屬非連續號碼，使用 or 串接條件
				for (int i = 0; i < statusCondition.length(); i++) {
					int aNum = Integer.parseInt(statusCondition.substring(i,
							i + 1));
					if (whereSql.length() > 0) {
						whereSql += " or ";
					}
					whereSql += " status = " + aNum;
				}
			}
		}

		if (condition.length() == 0) {

		} else {
			whereSql += " item_name like '%" + condition
			
			+ "%' OR realname like '%" + condition
					+ "%' OR e_varchar06 like '%" + condition
					+ "%' OR e_int04 like '%" + condition
					+ "%' OR e_int07 '%" + condition
					+ "%' OR e_int02 like '%" + condition
					+ "%' OR e_varchar12 like '%" + condition
					+ "%' OR e_varchar13 like '%" + condition
					+ "%' OR e_varchar14 like '%" + condition
					+ "%' OR bid_id like '%" + condition + "%' ";
			// id,, ,, ,,tax,costed,locally,,status,,,,
		}

		if (whereSql.length() > 0) {
			sql = sql + " WHERE " + whereSql;
		}
		// sql = sql + " WHERE " + whereSql + " ORDER BY " + orderBy + " " + dir;
		dataArray = conn.queryJSONArrayWithPage("mogan-DB", sql, startIndex,
				pageSize);

		jObj.put("Datas", dataArray);
		jObj.put("Records", conn.getQueryDataSize("mogan-DB", sql));
		jArray.add(jObj);
		return jArray;
	}

	/**
	 * <P>
	 * <font size=7 color=red>ACTION = LOAD_BID_ITEMS</font>
	 * </P>
	 * 讀取得標商品清單
	 * 
	 * @param startIndex
	 *            <font color=red>[START_INDEX]</font> - 起始INDEX
	 * @param pageSize
	 *            <font color=red>[PAGE_SIZE]</font> - 取回總數
	 * @param statusCondition
	 *            <font color=red>[STATUS_CONDITION]</font> - 狀態條件0-6
	 * @param condition
	 *            <font color=red>[CONDITION]</font> - 篩選條件
	 * @param orderBy
	 *            <font color=red>[ORDER_BY]</font> - 排序條件
	 * @param dir
	 *            <font color=red>[DIR]</font> - 排序方法
	 * @return
	 */
	public JSONArray loadBidItems(int startIndex, int pageSize,
			String statusCondition, String condition, String orderBy, String dir) {
		JSONArray jArray = new JSONArray();
		JSONArray dataArray = new JSONArray();
		JSONObject jObj = new JSONObject();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		// 得標會員帳號 user_name
		// 摩根得標編號 no
		// 結標日 end_date
		// 商品ID item_id
		// 商品名 item
		// 賣家帳號 sell_name
		// 賣家mail
		// 消費稅 tax
		// 落札価格 costed
		// 運費(日本,日$) locally
		// 匯款費用(日$) remittance
		// 合計
		// 匯款帳戶
		// 匯款進度 status 0已得標未處理 1已取得賣家連絡資料 2完成匯款 3日本收貨完成 4日本出貨完成 5台灣收貨完成 6會員結帳完成 7台灣出貨完成
		// 下標帳號 jyahooid
		System.out.println("[DEBUG] loadItems::"+condition);
		// String sql = "SELECT user_name,item,item_id ,no, end_date,sell_name,costed,status,jyahooid FROM web_bidding ";
		String sql = "SELECT id,user_name,item,item_id ,item_order_id, end_date,sell_name,tax,costed,locally,remittance,status,agent_account ,contact_type,title,renote,total_item,total_unpay,total_unship,order_form_status,realname FROM view_bid_item_order_v1 ";

		// 判斷要顯示的狀態
		boolean statusConditionFlag = true;

		for (int i = 0; i < statusCondition.length() - 1; i++) {
			// String aNum=statusCondition.substring(i,1);
			int aNum = Integer.parseInt(statusCondition.substring(i, i + 1));
			int bNum = Integer
					.parseInt(statusCondition.substring(i + 1, i + 2));
			if (bNum != aNum + 1) {
				statusConditionFlag = false;
			}
		}

		// 轉換為SQL語法
		String whereSql = "";
		if (statusCondition.length() == 0) {
			// 沒有statusCondition，在前就應該擋下，不該傳入
			whereSql = " status is null";
		} else {
			// 有statusCondition，判斷statusConditionFlag(是否為連號)
			if (statusConditionFlag) {
				// 屬於連續號碼，直接用<>來找出目標
				whereSql = " status >= "
						+ statusCondition.substring(0, 1)
						+ " and status<= "
						+ statusCondition
								.substring(statusCondition.length() - 1);
			} else {
				// 屬非連續號碼，使用 or 串接條件
				for (int i = 0; i < statusCondition.length(); i++) {
					int aNum = Integer.parseInt(statusCondition.substring(i,
							i + 1));
					if (whereSql.length() > 0) {
						whereSql += " or ";
					}
					whereSql += " status = " + aNum;
				}
			}
		}
		
		
		if (condition!=null && condition.length()>0){
			JSONObject conditionJObj = JSONObject.fromObject(condition);	
		
			if (conditionJObj.has("SEARCH_KEY")
					&& conditionJObj.getString("SEARCH_KEY").length() > 0) {
				// 有設定篩選值
				String searchKey=conditionJObj.getString("SEARCH_KEY");
				whereSql += " AND (user_name like '%"
						+ conditionJObj.getString("SEARCH_KEY")
						+ "%' OR item like '%"
						+ conditionJObj.getString("SEARCH_KEY")
						+ "%' OR item_id like '%"
						+ conditionJObj.getString("SEARCH_KEY")
						+ "%' OR item_order_id like '%"
						+ conditionJObj.getString("SEARCH_KEY")
						+ "%' OR end_date like '%"
						+ conditionJObj.getString("SEARCH_KEY")
						+ "%' OR sell_name like '%"
						+ conditionJObj.getString("SEARCH_KEY")
						+ "%' OR remittance like '%"
						+ conditionJObj.getString("SEARCH_KEY")
						+ "%' OR agent_account like '%"
						+ conditionJObj.getString("SEARCH_KEY")
						+ "%' OR contact_type like '%"
						+ conditionJObj.getString("SEARCH_KEY")
						+ "%' OR title like '%"
						+ conditionJObj.getString("SEARCH_KEY")
						+ "%' OR realname like '%"
						+ conditionJObj.getString("SEARCH_KEY")
						+ "%' OR renote like '%"
						+ conditionJObj.getString("SEARCH_KEY") + "%' )";
				
			}
			if (conditionJObj.has("ACCOUNT")
					&& conditionJObj.getString("ACCOUNT").length() > 0
					&& !conditionJObj.getString("ACCOUNT").equals("-")) {
				// 有設定篩選帳號 舊版
				whereSql += " AND agent_account like '"
						+ conditionJObj.getString("ACCOUNT").replaceAll(
								"-YAHOO JP", "") + "' ";
			}
		}



		sql = sql + " WHERE `show` = 1 AND (" + whereSql + ") ORDER BY "
				+ orderBy + " " + dir;

		System.out.println("[DEBUG] startIndex:"+startIndex+" pageSize:"+pageSize);
		dataArray = conn.queryJSONArrayWithPage("mogan-tw", sql, startIndex,
				pageSize);

		System.out.println("[DEBUG] sql::" + sql);
		jObj.put("Datas", dataArray);
		jObj.put("Records", conn.getQueryDataSize("mogan-tw", sql));
		jArray.add(jObj);
		return jArray;
	}

	/**
	 * <P>
	 * <font size=7 color=red>讀取商品連絡資料，ACTION = GET_ITEM_CONTACT_DATA</font>
	 * </P>
	 * 
	 * @param webSiteId
	 * @param bidAccount
	 * @param itemId
	 * @param transactionId
	 * @param sellerId
	 * @param memberAccount
	 * @return
	 * @throws Exception 
	 */
	public JSONArray getItemContactData(String webSiteId, String bidAccount,
			String itemId, String transactionId, String sellerId,
			String memberAccount, String dataSource)
			throws Exception {
		JSONArray jArray = new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
					this.getAppId());
			
			jArray = agentYJ.getItemContactMsg(bidAccount, itemId,
					transactionId, sellerId, memberAccount, dataSource);
		}
		return jArray;
	}
	
	/**
	 * action=UPDATE_ITEM_CONTACT_DATA 
	 * 更新商品聯絡資料
	 * @param itemOrderId
	 * @return
	 * @throws Exception 
	 */
	public JSONArray updateItemContactData(String itemOrderId)
			throws Exception {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
		"DBConn");
		BidItemOrderBean bidItemOrderBean= new BidItemOrderBean(conn,itemOrderId);
		Map dataMap=bidItemOrderBean.getDataMap();
		JSONArray jArray = new JSONArray();
		if (dataMap.get("website_id").equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
					this.getAppId());
			agentYJ.getItemContactMsg(itemOrderId);
			
			jArray = agentYJ.getItemContactMsgFromDB("","",itemOrderId);
		}
		return jArray;
	}

	/**
	 * <P>
	 * <font size=7 color=red>讀取網頁上商品資料，ACTION = GET_ITEM_DATA</font>
	 * </P>
	 * 
	 * @param webSiteId
	 * @param bidAccount
	 * @param itemId
	 * @return
	 * @throws AccountNotExistException
	 */
	public JSONArray getItemData(String webSiteId, String bidAccount,
			String itemId) throws AccountNotExistException {
		JSONArray jArray = new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
					this.getAppId());
			jArray = agentYJ.getItemData(bidAccount, itemId);
		}
		return jArray;
	}

	/**
	 * <P>
	 * <font size=7 color=red>讀取網頁上商品資料，ACTION = GET_ITEM_DATA</font>
	 * </P>
	 * 
	 * @param webSiteId
	 * @param bidAccount
	 * @param itemId
	 * @param won_id
	 *            資料庫ID
	 * @return
	 * @throws AccountNotExistException
	 */
	public JSONArray getItemData(String webSiteId, String bidAccount,
			String itemId, String won_id) throws AccountNotExistException {
		JSONArray jArray = new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
					this.getAppId());
			jArray = agentYJ.getItemData(bidAccount, itemId, won_id);
		}
		return jArray;
	}
	
	/**
	 * 取得商品的order form
	 * @param webSiteId
	 * @param bidAccount
	 * @param itemId
	 * @return
	 * @throws AccountNotExistException 
	 */
	public JSONArray getItemOrderForm(String webSiteId, String bidAccount,
			String itemId,String sellerAccount) throws AccountNotExistException{
		JSONArray jArray = new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
					this.getAppId());
			jArray=agentYJ.getItemOrderForm(bidAccount,itemId,sellerAccount);
		}
		return jArray;
	}

	/**
	 * <P>
	 * <font size=7 color=red>發送得標聯絡訊息，ACTION = SEND_WON_MESSAGE</font>
	 * </P>
	 * 不指定發送訊息方法，優先權為留言版>e-mail>揭示版 回傳使用那種連絡方式，失敗或成功
	 * 
	 * @param webSiteId
	 *            <font color=red>[WEB_SITE_ID]</font> YAHOO_JP_WEBSITE_ID=1
	 * @param bidAccount
	 *            <font color=red>[BID_ACCOUNT]</font> 下標帳號
	 * @param itemId
	 *            <font color=red>[ITEM_ID]</font> 下標商品編號
	 * @param subject
	 *            <font color=red>[SUBJECT]</font> e-mail標題
	 * @param msg
	 *            <font color=red>[MSG]</font> 訊息內容
	 * @return CONTACT_TYPE,CONTACT_RESULTS<BR />CONTACT_TYPE 0=留言版，1=E-MAIL，2=揭示版 <BR />CONTACT_RESULTS 1=成功 0=失敗
	 * @throws AccountNotExistException
	 */
	public JSONArray sendWonMsg(String webSiteId, String bidAccount,
			String itemId, String subject, String msg)
			throws AccountNotExistException {
		JSONArray jArray = new JSONArray();
		JSONObject jObj = new JSONObject();

		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			String sendMethod = "2";
			String contactType = "";
			NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
					this.getAppId());

			// 取得連絡方法
			contactType = agentYJ.getItemContactType(bidAccount, itemId);

			if (contactType
					.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")) {
				sendMethod = "1";
			} else {
				if (contactType.equals(NetAgentYJ.CONTACT_BOARD)) {
					sendMethod = "0";
					subject = "1";
				} else {
					sendMethod = "2";
					subject = "no";
				}
			}
			
			agentYJ.setMailSenderName(this.getProperty("mailSenderName"));
			agentYJ.setMailSenderAddress(this.getProperty("mailSenderAddress"));
			agentYJ.setMailCC(this.getProperty("mailCC"));
			
			jObj.put("CONTACT_TYPE", sendMethod);
			jObj.put("CONTACT_RESULTS", agentYJ.sendMsg(bidAccount, itemId,
					sendMethod, subject, msg, contactType).getString(0));
					
		}
		// 讀取商品頁面
		jArray.add(jObj);
		return jArray;
	}

	/**
	 * <P>
	 * <font size=7 color=red>發送訊息，ACTION = SEND_MESSAGE</font>
	 * </P>
	 * 指定訊息發送方法
	 * 
	 * @param webSiteId
	 *            <font color=red>[WEB_SITE_ID] </font> -YAHOO_JP_WEBSITE_ID=1
	 * @param bidAccount
	 *            <font color=red>[BID_ACCOUNT]</font> - 下標帳號
	 * @param itemId
	 *            <font color=red>[ITEM_ID]</font> - 下標商品編號
	 * @param sendMethod
	 *            <font color=red>[SEND_METHOD]</font> - 訊息傳送方法 0=留言版,1=e-mail 2=揭示版
	 * @param subject
	 *            <font color=red>[SUBJECT_A]</font> - 訊息標題或EMAIL標題<br>
	 *            留言版
	 *            <ul>
	 *            1=送付先住所、支払い、発送などについて
	 *            </ul>
	 *            <ul>
	 *            2=支払いが完了しました
	 *            </ul>
	 *            <ul>
	 *            3=商品を受け取りました
	 *            </ul>
	 *            <ul>
	 *            4=その他
	 *            </ul>
	 *            </br> 揭示版
	 *            <ul>
	 *            no=公開しない
	 *            </ul>
	 *            <ul>
	 *            yes=公開する
	 *            </ul>
	 * @param msg
	 *            <font color=red>[MSG]</font> - 訊息內容
	 * @return
	 * @throws AccountNotExistException
	 */
	public JSONArray snedMsg(String webSiteId, String bidAccount,
			String itemId, String sendMethod, String subject, String msg)
			throws AccountNotExistException {

		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
					this.getAppId());
			agentYJ.sendMsg(bidAccount, itemId, sendMethod, subject, msg);
		}
		JSONArray jArray = new JSONArray();
		return jArray;
	}
	
	
	/**
	 * 
	 * @param itemOrderId
	 * @param sendMethod
	 * @param subject
	 * @param msg
	 * @return
	 * @throws AccountNotExistException 
	 */
	public JSONArray snedMsg(String itemOrderId, String sendMethod, String subject, String msg) throws AccountNotExistException{
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
		"DBConn");
		BidItemOrderBean bidItemOrderBean= new BidItemOrderBean(conn,itemOrderId);
		
		JSONArray jArray = new JSONArray();
		Map<String,String> dataMap=bidItemOrderBean.getDataMap();
		String webSiteId=dataMap.get("website_id");
		String bidAccount=dataMap.get("agent_account");
		String itemId=dataMap.get("item_id");
		snedMsg( webSiteId,  bidAccount, itemId,  sendMethod,  subject,  msg);
		
		
		return jArray;
	}
			

	/**
	 * <p>
	 * <font size=7 color=red>登入，ACTION = LOGIN</font>
	 * </p>
	 * <p>
	 * 傳入webSiteId帳號和密碼進行登入動作,webSiteId [1=yahoo jp]
	 * </p>
	 * 
	 * @param webSiteId
	 *            <font color=red>[WEB_SITE_ID]</font>
	 * @param uId
	 *            <font color=red>[UID]</font>
	 * @param pwd
	 *            <font color=red>[PWD]</font>
	 * @return 回傳數字<br /> 0-未登入 <br /> 1-登入成功 <br /> 2-登入失敗 <br /> 3-帳號已登入
	 * @throws Exception
	 */
	public JSONArray login(String webSiteId, String uId, String pwd)
			throws Exception {
		JSONArray jArray = new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
					this.getAppId());
			jArray = agentYJ.login(uId, pwd);
		}
		return jArray;
	}

	/**
	 * <font size=7 color=red>下標商品，ACTION = BID_ITEM</font>
	 * 
	 * @param webSiteId
	 *            <font color=red>[WEB_SITE_ID]</font>
	 * @param uId
	 *            <font color=red>[UID]</font> - 下標帳號
	 * @param pwd
	 *            <font color=red>[PWD]</font> - 密碼
	 * @param itemURL
	 *            <font color=red>[ITEM_URL]</font> - 商品網址
	 * @param price
	 *            <font color=red>[PRICE]</font> - 下標價
	 * @param qty
	 *            <font color=red>[QTY]</font> - 下標數量
	 * @return 下標結果，以數字表示 <br /> 0 - 無法判斷<br /> 1 - 下標成功，非最高出價<br /> 2 - 下標成功，已得標<br/> 3 - 下標成功，最高出價<br /> 4 - 無法下標<br /> - 下標失敗，錯誤出價(未完成)<br /> -
	 *         下標失敗，已結標(未完成)<br /> - 下標失敗，無下標連結(未完成)<br /> - 下標失敗，評價不足(未完成)<br /> - 下標失敗，密碼錯誤(未完成)<br /> - 下標失敗，下標價過低(未完成)<br /> - 下標失敗，數量錯誤(未完成)<br
	 *         /> - 下標失敗，無法下標(未完成)<br />
	 * @throws Exception
	 */
	public JSONArray bidItem(String webSiteId, String uId, String pwd,
			String itemURL, String price, String qty) throws Exception {
		JSONArray jArray = new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
					this.getAppId());
			jArray = agentYJ.bidItem(uId, pwd, itemURL, price, qty);
		}
		return jArray;
	}

	/**
	 * <font size=7 color=red>直購商品，ACTION = BUY_ITEM</font>
	 * 
	 * @param webSiteId
	 *            <font color=red>[WEB_SITE_ID]</font>
	 * @param uId
	 *            <font color=red>[UID]</font> - 下標帳號
	 * @param pwd
	 *            <font color=red>[PWD]</font> - 密碼
	 * @param itemURL
	 *            <font color=red>[ITEM_URL]</font> - 商品網址
	 * @param qty
	 *            <font color=red>[QTY]</font> - 下標數量
	 * @return 下標結果，以數字表示 <br /> 0 - 無法判斷<br /> 1 - 下標成功，非最高出價<br /> 2 - 下標成功，已得標<br/> 3 - 下標成功，最高出價<br /> 4 - 無法下標<br /> - 下標失敗，錯誤出價(未完成)<br /> -
	 *         下標失敗，已結標(未完成)<br /> - 下標失敗，無下標連結(未完成)<br /> - 下標失敗，評價不足(未完成)<br /> - 下標失敗，密碼錯誤(未完成)<br /> - 下標失敗，下標價過低(未完成)<br /> - 下標失敗，數量錯誤(未完成)<br
	 *         /> - 下標失敗，無法下標(未完成)<br />
	 * @throws AccountNotExistException
	 * @throws Exception
	 */
	public JSONArray buyItem(String webSiteId, String uId, String pwd,
			java.lang.String itemURL, String qty)
			throws AccountNotExistException {
		JSONArray jArray = new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
					this.getAppId());
			jArray = agentYJ.buyItem(uId, pwd, itemURL, qty);
		}
		return jArray;
	}

	/**
	 * <p>
	 * <font size=7 color=red>判斷競標狀況，ACTION = IS_MY_BID</font>
	 * </p>
	 * <p>
	 * 開啟商品頁面判斷狀態
	 * </p>
	 * 
	 * @param webSiteId
	 *            <font color=red> [WEB_SITE_ID]</font>
	 * @param uId
	 *            <font color=red>[UID] </font>
	 * @param itemURL
	 *            <font color=red> [ITEM_URL]</font>
	 * @param price
	 *            <font color=red> [PRICE]</font> - 下標價
	 * @return 回傳數字<br /> 0 - 未得標<br /> 1 - 最高出價者<br /> 2 - 已得標<br /> 3 - 出價被取消，未結標(未完成)<br /> 4 - 出價被取消，已結標(未完成)<br /> 5 - 出價被超過(未完成)<br /> 6 -
	 *         流標(未完成)<br />
	 * @throws Exception
	 */
	public JSONArray isMyBid(String webSiteId, String uId, String itemURL,
			String price) throws Exception {
		JSONArray jArray = new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(),
					this.getAppId());
			jArray = agentYJ.isMyBid(uId, itemURL, price);
		}
		return jArray;
	}

	/**
	 * <p>
	 * <font size=7 color=red>取得已登入列表，ACTION = GET_LOGIN_LIST</font>
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	public JSONArray getLoginList() throws Exception {
		JSONArray jArray = new JSONArray();
		NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(), this
				.getAppId());
		jArray = agentYJ.getLoginList();
		return jArray;
	}

	/**
	 * 將訊息設為已讀
	 * 
	 * @param contactId
	 * @return
	 * @throws Exception
	 */
	public JSONArray setTransactionMsgReaded(String contactId) throws Exception {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		Map conditionMap = new HashMap();
		conditionMap.put("contact_id", contactId);
		Map dataMap = new HashMap();
		dataMap.put("is_read", "1");
		dataMap.put("read_date", new Date());
		conn.update("mogan-DB", "item_contact_record", conditionMap, dataMap);
		JSONArray jArray = new JSONArray();
		NetAgentYJ agentYJ = new NetAgentYJ(this.getModelServletContext(), this
				.getAppId());
		return conn.queryJSONArray("mogan-DB",
				"SELECT * FROM view_item_contact_record WHERE contact_id='"
						+ contactId + "'");
	}
	
	/**
	 * 讀取範本內容
	 * @param templetName
	 * @return
	 */
	public JSONArray loadTemplate(String templetName){
		FileIO fio =new FileIO();
		JSONArray jArray=new JSONArray();
		JSONObject jObj=new JSONObject();
		//jObj.put(templetName, fio.loadTxtFile(null, this.getModelName(), templetName));
		jObj.put("fileName", templetName);
		jObj.put("fileContent", fio.loadTxtFile(null, this.getModelName(), templetName).toString());
		jArray.add(jObj);
		//fio.loadTxtFile(null, this.getModelName(), templetName);
		return jArray;
	}
	
	/**
	 * 儲存範本檔案
	 * @param templetName
	 * @param templetText
	 * @return
	 */
	public JSONArray saveTemplate(String templetName,String templetText){
		FileIO fio =new FileIO();
		JSONArray jArray=new JSONArray();
		jArray.add(fio.saveTxtFile(null,this.getModelName(),templetName,templetText));
		return jArray;
	}
	
	/**
	 * 取得範本列表
	 * @return
	 */
	public JSONArray getTemplateList(){
		FileIO fio =new FileIO();
		fio.getTxtFileList(null,this.getModelName());
		return null;
	}
	
	public JSONArray delTemplate(String templetName){
		FileIO fio =new FileIO();
		JSONArray jArray=new JSONArray();
		jArray.add(fio.delTxtFile(null,this.getModelName(),templetName));
		return jArray;
	}
	
	/**
	 * 讀取個人設定
	 * @param ptyName
	 * @return
	 */
	public JSONArray loadUserPty(String ptyName){
		FileIO fio =new FileIO();
		JSONArray jArray=new JSONArray();
		Properties p = fio.loadPtyFile(null, this.getModelName());
		jArray=JSONArray.fromObject(p.get(ptyName));
		return null;
	}
	
	/**
	 * 將個人設定保存起來
	 * @param jTrnsCodeList
	 * @return
	 */
	public JSONArray saveUserPty(JSONArray jTrnsCodeList){
		FileIO fio =new FileIO();
		JSONArray jArray=new JSONArray();
		Properties p = new Properties();
		p.put("TRNS_CODE_LIST", jTrnsCodeList.toString());
		jArray.add(fio.savePtyFile(null, this.getModelName(), p, "")) ;
		return jArray;
	}
	
	/**
	 * 
	 */
	public JSONArray doAction(Map parameterMap) throws Exception {
		JSONArray jArray = new JSONArray();
		System.out.println("[INFO]BidManager ACTION start. " + this.getAct());

		if (this.getAct().equals("DEL_TEMPLATE")){
			String templetName= (String) parameterMap.get("TEMPLATE_NAME");
			jArray=delTemplate(templetName);
		}else if (this.getAct().equals("LOAD_TEMPLATE")){
			String templetName= (String) parameterMap.get("TEMPLATE_NAME");
			jArray=loadTemplate(templetName);
		}else if (this.getAct().equals("SAVE_TEMPLATE")){
			String templateName= (String) parameterMap.get("TEMPLATE_NAME");
			String templateText= (String) parameterMap.get("TEMPLATE_TEXT");
			jArray = saveTemplate(templateName,templateText);
		}else if (this.getAct().equals("SAVE_TRNS_CODE_LIST")){
			String trnsCodeList= (String) parameterMap.get("TRNS_CODE_LIST");
			JSONArray jTrnsCodeList=JSONArray.fromObject(trnsCodeList);
			jArray = saveUserPty(jTrnsCodeList);
		}else if (this.getAct().equals("LOAD_TRNS_CODE_LIST")){
			//jArray = saveUserPty(jTrnsCodeList);
		}else if (this.getAct().equals("UPDATE_ITEM_CONTACT_DATA")) {//更新商品聯絡資料
			String itemOrderId = (String) parameterMap.get("ITEM_ORDER_ID");
			jArray = updateItemContactData(itemOrderId);
		}else if (this.getAct().equals("UN_POST_ITEM")) {
			String itemOrderId = (String) parameterMap.get("ITEM_ORDER_ID");
			jArray = unpostItem(itemOrderId);
		} else if (this.getAct().equals("READ_TRANSACTION_MSG")) {
			String contactId = (String) parameterMap.get("CONTACT_ID");
			jArray = setTransactionMsgReaded(contactId);
		} else if (this.getAct().equals("GET_ITEM_DATA")) {
			String webSiteId = (String) parameterMap.get("WEB_SITE_ID");
			String bidAccount = (String) parameterMap.get("BID_ACCOUNT");
			String itemId = (String) parameterMap.get("ITEM_ID");
			jArray = getItemData(webSiteId, bidAccount, itemId);
		} else if (this.getAct().equals("GET_BID_LIST")) {
			String webSiteId = (String) parameterMap.get("WEB_SITE_ID");
			String bidAccount = (String) parameterMap.get("BID_ACCOUNT");
			String itemId = (String) parameterMap.get("ITEM_ID");
			String listType = (String) parameterMap.get("LIST_TYPE");
			String page = (String) parameterMap.get("PAGE");
			jArray = getBidList(webSiteId, bidAccount, itemId, listType, page);
		} else if (this.getAct().equals("GET_HIGH_PRICE_ACCOUNT")) {
			String webSiteId = (String) parameterMap.get("WEB_SITE_ID");
			String bidAccount = (String) parameterMap.get("BID_ACCOUNT");
			String itemId = (String) parameterMap.get("ITEM_ID");
			String page = (String) parameterMap.get("PAGE");
			jArray = getHighPriceAccount(webSiteId, bidAccount, itemId, page);
		} else if (this.getAct().equals("SEND_QUESTION")) {
			String webSiteId = (String) parameterMap.get("WEB_SITE_ID");
			String questAccount = (String) parameterMap.get("QUEST_ACCOUNT");
			String itemId = (String) parameterMap.get("ITEM_ID");
			String question = (String) parameterMap.get("QUESTION");
			jArray = askQusetion(webSiteId, questAccount, itemId, question);
		} else if (this.getAct().equals("REPOST_ITEM")) {
			String itemOrderId = (String) parameterMap.get("ITEM_ORDER_ID");
			jArray = repostItem(itemOrderId);
		} else if (this.getAct().equals("POST_ITEM")) {
			String itemOrderId = (String) parameterMap.get("ITEM_ORDER_ID");
			jArray = postItem(itemOrderId);
		} else if (this.getAct().equals("LOAD_ITEMS")) {
			int startIndex = Integer.parseInt((String) parameterMap
					.get("START_INDEX"));
			int pageSize = Integer.parseInt((String) parameterMap
					.get("PAGE_SIZE"));
			String statusCondition = (String) parameterMap
					.get("STATUS_CONDITION");
			String dataClass = (String) parameterMap.get("DATA_CLASS");
			String condition = (String) parameterMap.get("CONDITION_KEY");
			String orderBy = (String) parameterMap.get("ORDER_BY");
			String dir = (String) parameterMap.get("DIR");
			if (dir == null) {
				dir = "";
			}
			jArray = loadItems(dataClass, startIndex, pageSize,
					statusCondition, condition, orderBy, dir);
		} else if (this.getAct().equals("LOAD_BID_ITEMS")) {
			int startIndex = Integer.parseInt((String) parameterMap
					.get("START_INDEX"));
			int pageSize = Integer.parseInt((String) parameterMap
					.get("PAGE_SIZE"));
			String statusCondition = (String) parameterMap
					.get("STATUS_CONDITION");
			String condition = (String) parameterMap.get("CONDITION_KEY");
			String orderBy = (String) parameterMap.get("ORDER_BY");
			String dir = (String) parameterMap.get("DIR");
			if (dir == null) {
				dir = "";
			}
			
			jArray = loadBidItems(startIndex, pageSize, statusCondition,
					condition, orderBy, dir);
		} else if (this.getAct().equals("SEND_ITEM_ORDER_FORM")) {
			System.out.println("[DEBUG] SEND_ITEM_ORDER_FORM::"+parameterMap);
		} else if (this.getAct().equals("GET_ITEM_ORDER_FORM")) {
			String webSiteId = parameterMap.get("WEB_SITE_ID").toString();
			String uId = (String) parameterMap.get("UID");
			String itemId = (String) parameterMap.get("ITEM_ID");
			String sellerAccount = (String) parameterMap.get("SELLER_ACCOUNT");
			jArray = getItemOrderForm(webSiteId,uId,itemId,sellerAccount);
		} else if (this.getAct().equals("LOGIN")) {
			String webSiteId = parameterMap.get("WEB_SITE_ID").toString();
			String uId = (String) parameterMap.get("UID");
			String pwd = (String) parameterMap.get("PWD");
			jArray = login(webSiteId, uId, pwd);
		} else if (this.getAct().equals("BID_ITEM")) {
			String webSiteId = (String) parameterMap.get("WEB_SITE_ID");
			String uId = (String) parameterMap.get("UID");
			String pwd = (String) parameterMap.get("PWD");
			String itemURL = (String) parameterMap.get("ITEM_URL");
			String price = (String) parameterMap.get("PRICE");
			String qty = (String) parameterMap.get("QTY");
			jArray = bidItem(webSiteId, uId, pwd, itemURL, price, qty);
		} else if (this.getAct().equals("BUY_ITEM")) {
			String webSiteId = (String) parameterMap.get("WEB_SITE_ID");
			String uId = (String) parameterMap.get("UID");
			String pwd = (String) parameterMap.get("PWD");
			String itemURL = (String) parameterMap.get("ITEM_URL");
			String qty = (String) parameterMap.get("QTY");
			jArray = buyItem(webSiteId, uId, pwd, itemURL, qty);
		} else if (this.getAct().equals("IS_MY_BID")) {
			String webSiteId = (String) parameterMap.get("WEB_SITE_ID");
			String uId = (String) parameterMap.get("UID");
			String itemURL = (String) parameterMap.get("ITEM_URL");
			String price = (String) parameterMap.get("QTY");
			jArray = isMyBid(webSiteId, uId, itemURL, price);
		} else if (this.getAct().equals("GET_LOGIN_LIST")) {
			jArray = getLoginList();
		} else if (this.getAct().equals("LOAD_TRANSACTION_DATA")) {

			String bidAccount = (String) parameterMap.get("BID_ACCOUNT");
			String itemId = (String) parameterMap.get("ITEM_ID");
			String webSiteId = (String) parameterMap.get("WEB_SITE_ID");
			String transactionId = (String) parameterMap.get("ITEM_ORDER_ID");
			String sellerId = (String) parameterMap.get("SELLER_ID");
			String memberAccount = (String) parameterMap.get("MEMBER_ACCOUNT");
			String contactType = (String) parameterMap.get("CONTACT_TYPE");
			String wonId = (String) parameterMap.get("WON_ID");
			String dataSource = (String) parameterMap.get("DATA_SOURCE");

			JSONObject jItemObj = new JSONObject();
			JSONObject jMsgObj = new JSONObject();

			jMsgObj
					.put("Datas", getItemContactData(webSiteId, bidAccount,
							itemId, transactionId, sellerId, memberAccount,
							dataSource));

			jMsgObj.put("Records", jMsgObj.getJSONArray("Datas").size());
			jItemObj.put("CONTACT_MSG", jMsgObj);
			// if (contactType == null) {
			// 未取得連絡方法
			jMsgObj.put("Datas", getItemData(webSiteId, bidAccount, itemId,
					wonId));
			jMsgObj.put("Records", jMsgObj.getJSONArray("Datas").size());
			jItemObj.put("ITEM_DATA", jMsgObj);
			// }
			jArray.add(jItemObj);

		} else if (this.getAct().equals("SEND_MESSAGE")) {
			String webSiteId = (String) parameterMap.get("WEB_SITE_ID");
			String bidAccount = (String) parameterMap.get("BID_ACCOUNT");
			String itemId = (String) parameterMap.get("ITEM_ID");
			String itemOrderId = (String) parameterMap.get("ITEM_ORDER_ID");

			String sendMethod = (String) parameterMap.get("SEND_METHOD");
			String subject = (String) parameterMap.get("SUBJECT_A");
			String msg = (String) parameterMap.get("MSG");
			if (sendMethod.equals("1")) {
				subject = (String) parameterMap.get("SUBJECT_B");
			} else {
				subject = (String) parameterMap.get("SUBJECT_A");
			}
			jArray = snedMsg(itemOrderId, sendMethod, subject, msg);
			/*
			jArray = snedMsg(webSiteId, bidAccount, itemId, sendMethod,
					subject, msg);
					*/
//			sendWonMsg(webSiteId, bidAccount, itemId, subject, msg);
		} else if (this.getAct().equals("SEND_WON_MESSAGE")) {
			String webSiteId = (String) parameterMap.get("WEB_SITE_ID");
			String bidAccount = (String) parameterMap.get("BID_ACCOUNT");
			String itemId = (String) parameterMap.get("ITEM_ID");
			String subject = (String) parameterMap.get("SUBJECT");
			String msg = (String) parameterMap.get("MSG");
			jArray = sendWonMsg(webSiteId, bidAccount, itemId, subject, msg);
		} else if (this.getAct().equals("SAVE_ORDER_INFO")) {
			String webSiteId = (String) parameterMap.get("WEB_SITE_ID");
			String bidAccount = (String) parameterMap.get("BID_ACCOUNT");
			String orderId = (String) parameterMap.get("ORDER_ID");
			String updateInfo = (String) parameterMap.get("UPDATE_INFO");// json
			JSONObject jObj = JSONObject.fromObject(updateInfo);
			jArray = saveOrderInfo(webSiteId, orderId, jObj);
		}
		System.out.println("[INFO]BidManager ACTION end.");
		// TODO Auto-generated method stub
		return jArray;
	}

}
