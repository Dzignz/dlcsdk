package com.mogan.model.netAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.httpclient.Cookie;
import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.exception.netAgent.AccountNotExistException;
import com.mogan.face.BidFace;
import com.mogan.face.NetAgentModel;
import com.mogan.sys.DBConn;
import com.mogan.sys.SysCalendar;
import com.mogan.sys.SysKernel;
import com.mogan.sys.log.SysLogger4j;
import com.mogan.sys.mail.MailSenderInfo;
import com.mogan.sys.mail.SimpleMailSender;

/**
 * 日本雅虎專用，新版DB專用，
 * 
 * @author Dian
 */
public class NetAgentYJV2 extends NetAgentModel implements BidFace {

	private final String loginURL = "https://login.yahoo.co.jp/config/login";
	private final String bidURL = "https://login.yahoo.co.jp/config/login";

	/** 留言版 */
	private final String ITEM_CONTACT_MSG_URL = "http://page.auctions.yahoo.co.jp/show/contact?aID=$YAHOO_ITEM_ID#message";
	/** 揭示版 */
	private final String ITEM_DISCUSSION_MSG_URL = "http://$YAHOO_PAGE.auctions.yahoo.co.jp/jp/show/discussion?aID=$YAHOO_ITEM_ID";

	private final String ITEM_DATA_URL = "http://page.auctions.yahoo.co.jp/jp/auction/$YAHOO_ITEM_ID";

	private final String contactDetailUrl = "http://page.auctions.yahoo.co.jp";

	private final String previewMsgURL = "http://page.auctions.yahoo.co.jp/jp/show/contact_preview#message";

	private final String discussPreviewURL = "http://page2.auctions.yahoo.co.jp/jp/show/discuss_preview";

	/** 上架網址-1分類篩選 */
	private final String SELL_URL_A = "http://auctions.yahoo.co.jp/jp/show/topsubmit";
	/** 上架網址-2商品資料 */
	private final String SELL_URL_B = "http://list.auctions.yahoo.co.jp/jp/show/submit";
	/** 上架網址-3商品資料預覽 */
	private final String SELL_URL_C = "http://list.auctions.yahoo.co.jp/jp/show/preview";

	// private final String SELL_URL_D = "http://list3.auctions.yahoo.co.jp/jp/show/back"; // 上架網址-4商品資料
	/** 重新上架網址 */
	private final String RESELL_URL_A = "http://page18.auctions.yahoo.co.jp/jp/show/resubmit?aID=$YAHOO_ITEM_ID";
	/** 商品下架網址 */
	private final String UNSELL_URL_A = "http://page.auctions.yahoo.co.jp/jp/show/closeauction?aID=$YAHOO_ITEM_ID";
	/** 商品下架網址確認網址 */
	private final String UNSELL_URL_B = "http://edit.auctions.yahoo.co.jp/jp/config/closeauction";

	/** 商品發問網址 */
	private final String QUEST_URL_A = "http://page.auctions.yahoo.co.jp/jp/show/qanda?aID=$YAHOO_ITEM_ID";
	/** 商品發問預覽網址 */
	private final String QUEST_URL_B = "http://page.auctions.yahoo.co.jp/jp/show/qanda_preview";
	/** 送出商品發問網址 */
	private final String QUEST_URL_C = "http://edit.auctions.yahoo.co.jp/jp/config/qanda_submit";

	/** 商品出價履歷網址 */
	private final String BID_HIST_URL_A = "http://page.auctions.yahoo.co.jp/jp/show/bid_hist?aID=$YAHOO_ITEM_ID&apg=$PAGE";

	/** 商品出價詳細履歷網址 */
	private final String BID_HIST_URL_B = "http://page.auctions.yahoo.co.jp/jp/show/bid_hist?aID=$YAHOO_ITEM_ID&apg=$PAGE&typ=log";

	/** 商品ORDER FORM 網址 */
	private final String ORDER_FORM_URL_A = "https://order.auctions.yahoo.co.jp/jp/show/orderform?yahooID=$YAHOO_BIDDER_ACCOUNT&aid=$YAHOO_ITEM_ID&seller=$YAHOO_SELLER_ACCOUNT";

	/** 商品預覽ORDER FORM 網址 */
	private final String ORDER_FORM_URL_B = "https://order.auctions.yahoo.co.jp/jp/confirm/orderform?preview=order_form";

	/** 商品送出ORDER FORM 網址 */
	private final String ORDER_FORM_URL_C = "https://order.auctions.yahoo.co.jp/jp/config/orderform";

	private final String IMG_UPLOAD_URL = "/f/imgup/index";// 上傳圖片畫面

	private final String msgFrom_KeyWord = "投稿者：";// 訊息來源KEY WORD
	private final String msgContactCSS_KeyWord = "line-height:110%; word-break: break-all;";// 訊息內容CSS KEY WORD

	public final static String DISCUSSION_BOARD = "discussion";// 揭示版

	/**
	 * 留言版
	 */
	public final static String CONTACT_BOARD = "取引ナビで連絡";// 訊息來源KEY WORD
	public final static String CONTACT_FINISH = "投稿が完了しました。";// 
	public final static String DISCUSS_FINISH = "送信完了しました。";//
	/** 資料來源為資料庫 */
	public final static String DATA_SOURCE_DB = "DB";
	/** 資料來源為網頁 */
	public final static String DATA_SOURCE_WEB = "WEB";
	/** 日本yahoo網頁使用編碼 */
	private final String charSet = "euc-jp";

	private static Logger logger = Logger.getLogger(NetAgentYJV2.class.getName());

	/** 訊息來源-資料庫 */
	final static public int MSG_SOURCE_DB = 0;
	/** 訊息來源-網頁 */
	final static public int MSG_SOURCE_WEB = 1;

	final static private String CONN_ALIAS = "mogan-DB";

	public NetAgentYJV2(ServletContext servletContext, String appId) {
		super();
		this.setModelServletContext(servletContext);
		this.setAppId(appId);
	}

	/**
	 * 發問問題
	 * 
	 * @param bidId
	 * @param question
	 * @return JSONArray 1-成功，0-失敗
	 * @throws AccountNotExistException
	 */
	public JSONArray askQuestion(String agentAccount, String yahooItemId,
			String question) throws AccountNotExistException {
		JSONArray jArray = new JSONArray();
		String returnStr = "0";
		autoLogin(agentAccount);
		NetAgent nAgent = new NetAgent();
		Cookie[] cookies = getLoginSessionCookie(getAppId(), agentAccount);
		nAgent.getState().addCookies(cookies);
		nAgent.getDataWithGet(QUEST_URL_A.replaceAll("\\$YAHOO_ITEM_ID", yahooItemId));
		Map postMap = new HashMap();
		postMap.put("comment", question);
		try {
			NodeList nodes = nAgent.filterInputItem();
			nAgent.setParserNodesToPostDataMap(nodes);
			nAgent.putDataInPostDataMap(postMap);
			nAgent.postMaptoData();
			nAgent.getDataWithPost(QUEST_URL_B, charSet);

			postMap.put("UEComment", question);
			nodes = nAgent.filterInputItem();
			nAgent.setParserNodesToPostDataMap(nodes);
			nAgent.putDataInPostDataMap(postMap);

			nAgent.postMaptoData();
			nodes = nAgent.filterItem(new HTMLNodeFilter(".auctions.yahoo.co.jp/jp/config/qanda_submit"));
			String submitURL = nAgent.getHttp(nodes.toHtml());
			nAgent.getDataWithPost(submitURL, charSet);
			returnStr = "1";
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jArray.add(returnStr);
		return jArray;
	}

	/**
	 * 取得最高出價者的帳號
	 * 
	 * @param bidId
	 * @param yahooItemId
	 * @return 最高出價者帳號
	 * @throws AccountNotExistException
	 */
	public JSONArray getHighPriceAccount(String bidId, String yahooItemId,
			String page) throws AccountNotExistException {
		JSONArray jArray = new JSONArray();
		autoLogin(bidId);
		NetAgent nAgent = new NetAgent();
		Cookie[] cookies = getLoginSessionCookie(getAppId(), bidId);
		nAgent.getState().addCookies(cookies);
		nAgent.getDataWithGetiPhone(BID_HIST_URL_A.replaceAll("\\$YAHOO_ITEM_ID", yahooItemId).replaceAll("\\$PAGE", page));
		this.outputTofile(nAgent.getResponseBody(), "getHighPriceAccount");
		String highPriceAccount = "";
		try {
			NodeList nodes = nAgent.filterItem(new OrFilter(new HasChildFilter(new HTMLNodeFilter("最高額入札者")), new HasChildFilter(new HTMLNodeFilter("落札者"))));
			Map infoMap = new HashMap();

			for (int i = 0; i < nodes.size(); i++) {
				highPriceAccount = nodes.elementAt(i).toPlainTextString().split(" / 評価")[0];
				infoMap.put("ACCOUNT", nodes.elementAt(i).toPlainTextString().split(" / 評価")[0]); // 帳號
				infoMap.put("PRICE", nodes.elementAt(i).getParent().getChildren().elementAt(3).toPlainTextString().replaceAll(" |円|,", "")); // 價錢
				infoMap.put("UNIT", nodes.elementAt(i).getParent().getChildren().elementAt(5).toPlainTextString().replaceAll(",", "")); // 數量
				infoMap.put("DATE", nodes.elementAt(i).getParent().getChildren().elementAt(7).toPlainTextString()); // 時間
				// jArray.add(highPriceAccount);
				jArray.add(infoMap);
			}
			nodes = nAgent.filterItem(new HTMLNodeFilter("div id=\"modCtgSearchResult\""));
			updateActiveDate(bidId);
		} catch (ParserException e) {
			logger.info(e.getMessage(), e);
		}
		return jArray;
	}

	/**
	 * @param itemId
	 *            商品ID
	 * @param buyerAccount
	 *            下標帳號
	 * @param itemServer
	 *            商品資料SERVR
	 * @param sellerId
	 *            賣家帳號
	 * @return
	 * @throws AccountNotExistException
	 */
	public JSONArray getItemContactMsg(String itemId, String buyerAccount,
			String itemServer, String sellerId) throws AccountNotExistException {
		autoLogin(buyerAccount);

		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), buyerAccount);
		NetAgent nAgent = new NetAgent();
		nAgent.getState().addCookies(cookies);
		/*
		 * String itemServer = orderMap.get("item_url").split("http://")[1].split("\\.")[0]; String itemId = orderMap.get("item_id"); String memberId = orderMap.get("member_id"); String itemOrderId = orderMap.get("item_order_id"); String sellerId = orderMap.get("seller_id");
		 */
		JSONArray jArray = new JSONArray();
		JSONObject jObj = new JSONObject();
		NodeList nodes;
		nAgent.getDataWithGet(ITEM_DISCUSSION_MSG_URL.replaceAll("\\$YAHOO_ITEM_ID", itemId).replaceAll("\\$YAHOO_PAGE", itemServer));

		try {
			jObj = new JSONObject();
			NodeList discussionNodes = nAgent.filterItem(new HTMLNodeFilter("id=\"modBlbdForm\"")); // 揭示版
			if (discussionNodes.size() > 0 // 是否有揭示版
					&& discussionNodes.elementAt(0).getChildren().size() > 5// 揭示版是否有內容
			) {
				this.outputTofile(nAgent.getResponseBody(), itemId
						+ "_discussion");
				if (!discussionNodes.toHtml().contains(".*※投稿できるのは、出品者と落札者のみです。.*")) {
					// 子項必需超過5個才可判斷為有內容
					discussionNodes = discussionNodes.elementAt(0).getChildren();

					discussionNodes = discussionNodes.elementAt(5).getChildren();

					for (int i = 0; i < discussionNodes.size(); i++) {

						if (discussionNodes.elementAt(i).toPlainTextString().matches("^(\\s投稿|\\s答え)(.|\\s)*")) {
							// 標題
							String msgId = discussionNodes.elementAt(i).toPlainTextString().split("\\n")[1];
							String msgFrom = discussionNodes.elementAt(i).toPlainTextString().split("\\n")[2].split("(\\(|\\（)")[0];
							for (int d = 3; d < discussionNodes.elementAt(i).toPlainTextString().split("\\n").length; d++) {
								if (discussionNodes.elementAt(i).toPlainTextString().split("\\n")[d].indexOf("月") > 0) {
									String msgDate = discussionNodes.elementAt(i).toPlainTextString().split("\\n")[d];
									msgDate = fixYJDate(msgDate);// 修正留言時間為資料庫可以接受的時間格式
									jObj.put("msg_date", msgDate);
								}
							}
							jObj.put("msg_id", msgId);
							jObj.put("msg_title", msgId);
							jObj.put("msg_category", "連絡掲示板");
							jObj.put("msg_from", msgFrom);

						} else if (discussionNodes.elementAt(i).toPlainTextString().matches("^\\s*")) {
							// 空白

						} else {
							// 內文
							jObj.put("msg_contact", discussionNodes.elementAt(i).toHtml());
							jArray.add(jObj);
						}
					}
				}
			}
			// /揭示版結束
			/**
			 * 再讀取留言版
			 */
			nAgent.getDataWithGet(ITEM_CONTACT_MSG_URL.replaceAll("\\$YAHOO_ITEM_ID", itemId));

			HTMLNodeFilter aNf = new HTMLNodeFilter("a");
			HTMLNodeFilter crumbNf = new HTMLNodeFilter(".crumb");// 20091026確定
			nodes = nAgent.filterItem(new AndFilter(aNf, crumbNf));
			String linkURL = "";

			String msgId = ""; // 訊息SEQ
			String msgTitle = ""; // 訊息標題
			String msgFrom = ""; // 訊息由誰發出
			String msgContact = ""; // 訊息內容
			String msgDate = ""; // 訊息發出時間

			for (int i = 0; i < nodes.size(); i++) {
				jObj.clear();
				linkURL = nodes.elementAt(i).getText().split("\"")[1].split("\"")[0];
				msgId = linkURL.split("&no=")[1].split("&")[0].replaceAll("&no=", "");

				// 開啟留言畫面
				nAgent.getDataWithGet(contactDetailUrl + linkURL);//

				// 取得留言內容
				HTMLNodeFilter fromNf = new HTMLNodeFilter(msgFrom_KeyWord);
				HTMLNodeFilter cssNf = new HTMLNodeFilter(msgContactCSS_KeyWord);
				NodeList contactNodes = nAgent.filterItem(cssNf);
				msgContact = contactNodes.elementAt(0).toHtml();
				msgContact = msgContact.replaceAll("(<small>|</small>)", "");

				// 找到"投稿者："的位置，再找出投稿者與留言時間的資料
				NodeList tableFromNodes = nAgent.filterItem(fromNf).elementAt(0).getParent().getParent().getParent().getChildren();

				msgTitle = tableFromNodes.elementAt(1).getChildren().elementAt(1).toPlainTextString();// 留言標題
				msgFrom = tableFromNodes.elementAt(3).getChildren().elementAt(1).toPlainTextString();// 留言者
				msgDate = tableFromNodes.elementAt(3).getChildren().elementAt(3).toPlainTextString();// 留言時間

				// 修正留言者的留言內容
				msgFrom = msgFrom.replaceAll(msgFrom_KeyWord, "");

				msgDate = fixYJDate(msgDate);// 修正留言時間為資料庫可以接受的時間格式

				/*
				 * dataMap.put("seller_id", sellerId); dataMap.put("member_id", memberId); dataMap.put("bid_id", bidId); dataMap.put("item_order_id", itemOrderId); dataMap.put("item_id", itemId);
				 */
				jObj.put("msg_id", msgId);
				jObj.put("msg_title", msgTitle);
				jObj.put("msg_from", msgFrom);
				jObj.put("msg_contact", msgContact);
				jObj.put("msg_date", msgDate);
				jObj.put("msg_category", "取引ナビ");

				jArray.add(jObj);
			}
			updateActiveDate(buyerAccount);
		} catch (ParserException e) {
			logger.info(e.getMessage(), e);
		}

		return jArray;
	}

	/**
	 * 取得聯絡資料新版 TODO 正要開始做
	 * 
	 * @version 2.0
	 * @param itemOrderId
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public boolean updateItemContactMsg(JSONArray itemOrderIds) {
		boolean results = false;
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
		ArrayList orderList = conn.query(CONN_ALIAS, "SELECT item_order_id,item_id,bid_id,item_url,item_id,buyer_account,member_id,seller_id FROM view_bid_item_order_v1 WHERE item_order_id IN ("
				+ itemOrderIds.toString().replaceAll("\"", "'").replaceAll("\\[|\\]", "")
				+ ") AND delete_flag = 1 ");
		for (int orderIndex = 0; orderIndex < orderList.size(); orderIndex++) {
			Map<String, String> orderMap = (Map) orderList.get(orderIndex);
			String bidId = orderMap.get("buyer_account");
			String bidAccount = orderMap.get("buyer_account");
			try {
				autoLogin(bidId);
			} catch (AccountNotExistException e) {
				// TODO Auto-generated catch block
				logger.error("帳號不存在", e);
			}
			Cookie[] cookies = getLoginSessionCookie(this.getAppId(), bidAccount);
			NetAgent nAgent = new NetAgent();
			nAgent.getState().addCookies(cookies);

			String itemServer = orderMap.get("item_url").split("http://")[1].split("\\.")[0];
			String itemId = orderMap.get("item_id");
			String memberId = orderMap.get("member_id");
			String itemOrderId = orderMap.get("item_order_id");
			String sellerId = orderMap.get("seller_id");

			JSONArray jDataArray = getItemContactMsgFromDB(itemOrderId);
			Map contactMap = new HashMap();
			for (int i = 0; i < jDataArray.size(); i++) {
				contactMap.put(jDataArray.getJSONObject(i).get("msg_id"), jDataArray.getJSONObject(i));
			}

			try {
				/**
				 * 更新order from 狀態
				 */
				nAgent.getDataWithGet(ITEM_DATA_URL.replaceAll("\\$YAHOO_ITEM_ID", itemId));
				int hasOrderForm = 1;
				NodeList nodes = nAgent.filterItem(new HTMLNodeFilter("http://i.yimg.jp/images/auct/template/ui/auc_mod/bg_step_01_02.gif"));
				if (nodes.size() > 0) {
					hasOrderForm = 2;
				}

				Map sellerConditionMap = new HashMap();
				sellerConditionMap.put("seller_id", sellerId);
				Map sellerDataMap = new HashMap();
				sellerDataMap.put("attribute_1", hasOrderForm);
				conn.update(CONN_ALIAS, "item_seller", sellerConditionMap, sellerDataMap);

				/**
				 * 先讀取揭示版 但如果商品未結標，揭示版不會出來需要避過
				 */
				nAgent.getDataWithGet(ITEM_DISCUSSION_MSG_URL.replaceAll("\\$YAHOO_ITEM_ID", itemId).replaceAll("\\$YAHOO_PAGE", itemServer));
				NodeList discussionNodes = nAgent.filterItem(new HTMLNodeFilter("id=\"modBlbdForm\""));// 揭示版
				if (discussionNodes.size() > 0 // 是否有揭示版
						&& discussionNodes.elementAt(0).getChildren().size() > 5// 揭示版是否有內容
				) {

					if (discussionNodes.toHtml().contains(".*※投稿できるのは、出品者と落札者のみです。.*")) {
						break;
					}
					// 子項必需超過5個才可判斷為有內容
					discussionNodes = discussionNodes.elementAt(0).getChildren();

					discussionNodes = discussionNodes.elementAt(5).getChildren();

					Map discussMap = new HashMap();
					discussMap.put("member_account", memberId);
					discussMap.put("bid_account", bidAccount);
					discussMap.put("transaction_id", itemOrderId);
					discussMap.put("item_id", itemId);
					discussMap.put("seller_id", sellerId);

					for (int i = 0; i < discussionNodes.size(); i++) {

						if (discussionNodes.elementAt(i).toPlainTextString().matches("^(\\s投稿|\\s答え)(.|\\s)*")) {

							// 標題
							String msgId = discussionNodes.elementAt(i).toPlainTextString().split("\\n")[1];
							String msgFrom = discussionNodes.elementAt(i).toPlainTextString().split("\\n")[2].split("(\\(|\\（)")[0];
							logger.info("[DEBUG] 有揭示版." + msgId);
							for (int d = 3; d < discussionNodes.elementAt(i).toPlainTextString().split("\\n").length; d++) {
								if (discussionNodes.elementAt(i).toPlainTextString().split("\\n")[d].indexOf("月") > 0) {
									String msgDate = discussionNodes.elementAt(i).toPlainTextString().split("\\n")[d];
									msgDate = fixYJDate(msgDate);// 修正留言時間為資料庫可以接受的時間格式
									discussMap.put("msg_date", msgDate);
								}
							}
							discussMap.put("msg_id", msgId);
							discussMap.put("msg_title", msgId);
							discussMap.put("msg_category", "連絡掲示板");
							discussMap.put("msg_from", msgFrom);

						} else if (discussionNodes.elementAt(i).toPlainTextString().matches("^\\s*")) {
							// 空白

						} else {
							// 內文
							discussMap.put("msg_contact", discussionNodes.elementAt(i).toHtml());

							discussMap.put("member_id", memberId);
							/** bid_account 將被修改為 bid_id */
							discussMap.put("bid_id", bidAccount);
							discussMap.put("item_order_id", itemOrderId);
							discussMap.put("item_id", itemId);

							/**
							 * 2010 02 10 修正資料更新方式
							 */
							Map conditionMap = new HashMap();
							conditionMap.put("item_order_id", itemOrderId);
							conditionMap.put("msg_id", discussMap.get("msg_id"));
							this.outputTofile(nAgent.getResponseBody(), "discussMap");
							conn.newData("mogan-DB", "item_contact_record", conditionMap, discussMap);

							Map itemOrderConditionMap = new HashMap();
							itemOrderConditionMap.put("item_order_id", itemOrderId);
							Map itemOrderDataMap = new HashMap();
							itemOrderDataMap.put("time_at_16", new Date());
							conn.update("mogan-DB", "item_order", itemOrderConditionMap, itemOrderDataMap);
						}
					}
				}

				/**
				 * 再讀取留言版
				 */
				nAgent.getDataWithGet(ITEM_CONTACT_MSG_URL.replaceAll("\\$YAHOO_ITEM_ID", itemId));
				HTMLNodeFilter aNf = new HTMLNodeFilter("a");
				HTMLNodeFilter crumbNf = new HTMLNodeFilter(".crumb");// 20091026確定
				nodes = nAgent.filterItem(new AndFilter(aNf, crumbNf));
				String linkURL = "";

				String msgId = ""; // 訊息SEQ
				String msgTitle = ""; // 訊息標題
				String msgFrom = ""; // 訊息由誰發出
				String msgContact = ""; // 訊息內容
				String msgDate = ""; // 訊息發出時間

				for (int i = 0; i < nodes.size(); i++) {

					linkURL = nodes.elementAt(i).getText().split("\"")[1].split("\"")[0];
					msgId = linkURL.split("&no=")[1].split("&")[0].replaceAll("&no=", "");

					if (!contactMap.containsKey(msgId)) {

						// 開啟留言畫面
						nAgent.getDataWithGet(contactDetailUrl + linkURL);//

						// 取得留言內容
						HTMLNodeFilter fromNf = new HTMLNodeFilter(msgFrom_KeyWord);
						HTMLNodeFilter cssNf = new HTMLNodeFilter(msgContactCSS_KeyWord);
						NodeList contactNodes = nAgent.filterItem(cssNf);
						msgContact = contactNodes.elementAt(0).toHtml();
						msgContact = msgContact.replaceAll("(<small>|</small>)", "");

						// 找到"投稿者："的位置，再找出投稿者與留言時間的資料
						NodeList tableFromNodes = nAgent.filterItem(fromNf).elementAt(0).getParent().getParent().getParent().getChildren();

						msgTitle = tableFromNodes.elementAt(1).getChildren().elementAt(1).toPlainTextString();// 留言標題
						msgFrom = tableFromNodes.elementAt(3).getChildren().elementAt(1).toPlainTextString();// 留言者
						msgDate = tableFromNodes.elementAt(3).getChildren().elementAt(3).toPlainTextString();// 留言時間

						// 修正留言者的留言內容
						msgFrom = msgFrom.replaceAll(msgFrom_KeyWord, "");

						msgDate = fixYJDate(msgDate);// 修正留言時間為資料庫可以接受的時間格式

						Map dataMap = new HashMap();
						dataMap.put("seller_id", sellerId);
						dataMap.put("member_id", memberId);
						dataMap.put("bid_id", bidId);
						dataMap.put("item_order_id", itemOrderId);
						dataMap.put("item_id", itemId);
						dataMap.put("msg_id", msgId);
						dataMap.put("msg_title", msgTitle);
						dataMap.put("msg_from", msgFrom);
						dataMap.put("msg_contact", msgContact);
						dataMap.put("msg_date", msgDate);
						dataMap.put("msg_category", "取引ナビ");

						/**
						 * 2010 02 10 修正資料更新方式
						 */
						Map conditionMap = new HashMap();
						conditionMap.put("item_order_id", itemOrderId);
						conditionMap.put("msg_id", msgId);

						conn.newData("mogan-DB", "item_contact_record", conditionMap, dataMap);
					}
				}
				Map itemOrderConditionMap = new HashMap();
				itemOrderConditionMap.put("item_order_id", itemOrderId);
				Map itemOrderDataMap = new HashMap();
				itemOrderDataMap.put("time_at_16", new Date());
				conn.update("mogan-DB", "item_order", itemOrderConditionMap, itemOrderDataMap);
				results = true;
			} catch (ParserException e) {
				logger.error(e.getMessage(), e);
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}

		}

		return results;
	}

	/**
	 * 直接抓目前資料庫中所儲存的連絡資料
	 * 
	 * @version 2.0
	 * @param bidAccount
	 *            下標帳號
	 * @param itemId
	 *            商品ID
	 * @param transactionId
	 *            交易ID
	 * @return
	 */
	public JSONArray getItemContactMsgFromDB(String itemOrderId) {
		JSONArray jArray = new JSONArray();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
		// 排序方式
		// 排序欄位
		String orderBy = "msg_date";
		String orderType = "DESC";
		jArray = conn.queryJSONArray("mogan-DB", "SELECT * FROM view_item_contact_record_v1 WHERE "
				+ " item_order_id='" + itemOrderId + "' ORDER BY " + orderBy + // 排序欄位
				" " + orderType);// 排序方法
		return jArray;
	}

	/**
	 * 讀取訊息，可指定訊息來源
	 * 
	 * @param itemOrderIds
	 * @param msgSource
	 * @return
	 */
	@Deprecated
	public JSONArray getItemContactMsg(JSONArray itemOrderIds, int msgSource) {
		JSONArray jArray = new JSONArray();
		switch (msgSource) {
		case MSG_SOURCE_DB:
			DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
			String orderBy = "msg_date"; // 排序欄位
			String orderType = "DESC"; // 排序方式
			jArray = conn.queryJSONArray("mogan-DB", "SELECT * FROM view_item_contact_record_v1 WHERE "
					+ " item_order_id IN ("
					+ itemOrderIds.toString().replaceAll("\"", "'").replaceAll("\\[|\\]", "")
					+ ")  ORDER BY " + orderBy + // 排序欄位
					" " + orderType);// 排序方法
			break;
		case MSG_SOURCE_WEB:
			this.updateItemContactMsg(itemOrderIds);// 先由網頁更新資料庫訊息
			jArray = getItemContactMsg(itemOrderIds, MSG_SOURCE_DB);// 再由資料庫抓訊息
			break;
		}
		return jArray;
	}

	/**
	 * 取得絡聯內容
	 * 
	 * @param itemOrderIds
	 * @return
	 */
	public JSONArray getItemContactMsgFromDB(JSONArray itemOrderIds) {
		JSONArray jArray = new JSONArray();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
		// 排序方式
		// 排序欄位
		String orderBy = "msg_date";
		String orderType = "DESC";
		jArray = conn.queryJSONArray("mogan-DB", "SELECT * FROM view_item_contact_record_v1 WHERE "
				+ " item_order_id IN ("
				+ itemOrderIds.toString().replaceAll("\"", "'").replaceAll("\\[|\\]", "")
				+ ")  ORDER BY " + orderBy + // 排序欄位
				" " + orderType);// 排序方法
		return jArray;
	}

	/**
	 * 取得商品聯絡方法 BM2
	 * 
	 * @param bidAccount
	 * @param itemId
	 * @param orderId
	 *            -系統id
	 * @return
	 * @throws AccountNotExistException
	 */
	@Deprecated
	public String getItemContactType(String bidAccount, String itemId,
			String orderId) throws AccountNotExistException {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");

		NetAgent nAgent = new NetAgent();

		autoLogin(bidAccount);
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), bidAccount);
		nAgent.getState().addCookies(cookies);
		logger.info("ITEM_DATA_URL :: "
				+ ITEM_DATA_URL.replaceAll("\\$YAHOO_ITEM_ID", itemId));
		nAgent.getDataWithGet(ITEM_DATA_URL.replaceAll("\\$YAHOO_ITEM_ID", itemId));

		HTMLNodeFilter hrefNf = new HTMLNodeFilter("href");// href
		HasParentFilter parnetFilter = new HasParentFilter(new HTMLNodeFilter("decBg01 decBg05"));// decBg01 decBg05

		AndFilter andFilter = new AndFilter(hrefNf, parnetFilter);

		HTMLNodeFilter EvaluationNf = new HTMLNodeFilter("rating");// 評價
		HTMLNodeFilter pointNf = new HTMLNodeFilter("/points.yahoo.co.jp");
		andFilter = new AndFilter(andFilter, new NotFilter(EvaluationNf));
		andFilter = new AndFilter(andFilter, new NotFilter(pointNf));
		NodeList nodes;
		String contactType = null;
		// orderForm status 是否要填寫order form 0=不必 1=需要
		int hasOrderForm = 0;
		try {
			nodes = nAgent.filterItem(andFilter);
			for (int n = 0; n < nodes.size(); n++) {
				contactType = nodes.elementAt(n).toPlainTextString();
			}
			nodes = nAgent.filterItem(new HTMLNodeFilter("オーダーフォーム"));
			if (nodes.size() > 0) {
				hasOrderForm = 1;
			}

			Map conditionMap = new HashMap();
			conditionMap.put("buyer_account", bidAccount);
			conditionMap.put("item_id", itemId);
			ArrayList dataList = conn.queryWithMap("mogan-DB", "view_bid_item_order_v1", conditionMap);
			orderId = (String) ((Map) dataList.get(0)).get("item_order_id");

			// TODO 新版須修改，因資料表結構改變
			conditionMap = new HashMap();
			conditionMap.put("no", orderId);
			Map dataMap = new HashMap();
			dataMap.put("contact_type", contactType);
			dataMap.put("order_form_status", hasOrderForm);
			logger.info("getItemContactType" + orderId + " " + dataMap + " "
					+ hasOrderForm);
		} catch (ParserException e) {
			logger.error(e.getMessage(), e);
		}
		return contactType;
	}

	/**
	 * 傳入HTML原始碼判斷連絡方式
	 * 
	 * @param html
	 * @return
	 */
	public String getItemContactType(String html) {
		String contactType = null;
		NodeList nodes;
		NetAgent nAgent = new NetAgent();
		nAgent.setResponseBody(html);
		HTMLNodeFilter hrefNf = new HTMLNodeFilter("href");// href
		HasParentFilter parnetFilter = new HasParentFilter(new HTMLNodeFilter("decBg01 decBg05"));// decBg01 decBg05
		HTMLNodeFilter EvaluationNf = new HTMLNodeFilter("rating");// 評價
		HTMLNodeFilter pointNf = new HTMLNodeFilter("/points.yahoo.co.jp");
		AndFilter andFilter = new AndFilter(new NodeFilter[] { hrefNf,
				parnetFilter, new NotFilter(EvaluationNf),
				new NotFilter(pointNf) });
		try {
			nodes = nAgent.filterItem(andFilter);
			for (int n = 0; n < nodes.size(); n++) {
				contactType = nodes.elementAt(n).toPlainTextString();
			}
		} catch (ParserException e) {
			logger.error(e.getMessage(), e);
		}
		return contactType;
	}

	/**
	 * 傳入HTML原始碼判斷是否必需填寫Order Form
	 * 
	 * @param html
	 * @return true=要填寫，false=不用填寫
	 */
	public boolean hasItemOrderForm(String html) {
		NetAgent nAgent = new NetAgent();
		nAgent.setResponseBody(html);
		boolean hasOrderForm = false;
		NodeList nodes;
		try {
			nodes = nAgent.filterItem(new HTMLNodeFilter("http://i.yimg.jp/images/auct/template/ui/auc_mod/bg_step_01_02.gif"));
			if (nodes.size() > 0) {
				hasOrderForm = true;
			}
		} catch (ParserException e) {
			logger.error(e.getMessage(), e);
		}
		return hasOrderForm;
	}

	/**
	 * 傳入HTML原始碼判斷是否必需填寫Order Form
	 * 
	 * @param bidAccount
	 * @param itemId
	 * @return true=要填寫，false=不用填寫
	 * @throws AccountNotExistException
	 */
	public boolean hasItemOrderForm(String bidAccount, String itemId)
			throws AccountNotExistException {
		boolean result = false;
		NetAgent nAgent = new NetAgent();
		autoLogin(bidAccount);
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), bidAccount);
		nAgent.getState().addCookies(cookies);
		logger.info("Item has Order Form :: "
				+ ITEM_DATA_URL.replaceAll("\\$YAHOO_ITEM_ID", itemId));
		nAgent.getDataWithGet(ITEM_DATA_URL.replaceAll("\\$YAHOO_ITEM_ID", itemId));
		result = hasItemOrderForm(nAgent.getResponseBody());
		return result;
	}

	/**
	 * 單純回傳聯絡方式，不寫入資料庫 BM2
	 * 
	 * @param bidAccount
	 * @param itemId
	 * @return
	 * @throws AccountNotExistException
	 */
	public String getItemContactType(String bidAccount, String itemId)
			throws AccountNotExistException {
		NetAgent nAgent = new NetAgent();
		autoLogin(bidAccount);
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), bidAccount);
		nAgent.getState().addCookies(cookies);
		logger.info("Get Item Contact Type URL :: "
				+ ITEM_DATA_URL.replaceAll("\\$YAHOO_ITEM_ID", itemId));

		nAgent.getDataWithGet(ITEM_DATA_URL.replaceAll("\\$YAHOO_ITEM_ID", itemId));
		updateActiveDate(bidAccount);
		String contactType = getItemContactType(nAgent.getResponseBody());
		return contactType;
	}

	/**
	 * 取得商品頁面
	 * 
	 * @param bidAccount
	 * @param itemId
	 * @return
	 * @throws AccountNotExistException
	 */
	public String getItemPage(String bidAccount, String itemId)
			throws AccountNotExistException {
		NetAgent nAgent = new NetAgent();
		autoLogin(bidAccount);
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), bidAccount);
		nAgent.getState().addCookies(cookies);
		logger.info("Get Item Contact Type URL :: "
				+ ITEM_DATA_URL.replaceAll("\\$YAHOO_ITEM_ID", itemId));

		nAgent.getDataWithGet(ITEM_DATA_URL.replaceAll("\\$YAHOO_ITEM_ID", itemId));
		return nAgent.getResponseBody();
	}

	/**
	 * @param receiver
	 * @param mailSubject
	 * @param mailMsg
	 * @return
	 */
	public boolean mailMsg(String receiver, String mailSubject, String mailMsg) {
		boolean results = false;
		SimpleMailSender sms = new SimpleMailSender();
		logger.info("[DEBUG] sendMail-1");

		mailMsg = "<html><body>" + mailMsg.replaceAll("(\r|\n|\r\n)", "<br />")
				+ "</body></html>";
		sms.setMailSubject(mailSubject);
		sms.setMailContent(mailMsg);
		sms.setMailServerHost("mail.mogan.com.tw");
		sms.setMailServerPort("25");
		sms.setAccount("");
		sms.setPwd("");

		sms.setFromName(System.getProperty("MOGAN_BID_MAIL_SENDER_NAME"));
		sms.setFromAddress(System.getProperty("MOGAN_BID_MAIL_SENDER_ADDRESS"));

		ArrayList dataList = new ArrayList();
		logger.info("[DEBUG] sendMail-2. " + receiver + " ... " + mailSubject);
		sms.setTargetList(dataList);

		/* 寄送mail開始 */
		MailSenderInfo mail = sms.getMailInfo("", receiver, "", System.getProperty("MOGAN_BID_MAIL_BCC"));
		results = sms.sendHtmlMail(mail);
		logger.info("[DEBUG] sendMail-3");
		return results;
	}

	/**
	 * 新版送訊息，與新版資料庫整合 BM2
	 * 
	 * @param itemOrderId
	 * @param msgTitle
	 * @param msg
	 * @param sendMethod
	 * @return
	 * @throws AccountNotExistException
	 */
	public boolean sendMsg(String itemOrderId, String msgTitle, String msg,
			int sendMethod) throws AccountNotExistException {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
		/**
		 * 先判斷是發送方式，如果是1(mail)呼叫另一個api
		 */
		switch (sendMethod) {
		case 1:
			return mailMsg(itemOrderId, msgTitle, msg);
		}
		ArrayList<Map> orders = conn.query("mogan-DB", "SELECT buyer_account,item_id,bid_id FROM view_bid_item_order_v1 WHERE item_order_id ='"
				+ itemOrderId + "'");
		String buyerAccount = (String) orders.get(0).get("buyer_account");
		String itemId = (String) orders.get(0).get("item_id");
		return sendMsg(buyerAccount, itemId, sendMethod, msgTitle, msg);
	}

	/**
	 * 傳送訊息 BM2
	 * 
	 * @param bidAccount
	 *            下標帳號
	 * @param itemId
	 *            商品ID
	 * @param sendMethod
	 *            聯絡方法 0-留言版，1-Email，2-揭示版
	 * @param msgTitle
	 *            訊息標題
	 * @param msgContact
	 *            訊息內容
	 * @param toAddress
	 *            收件人Email
	 * @return
	 * @throws AccountNotExistException
	 */
	public boolean sendMsg(String bidAccount, String itemId, int sendMethod,
			String subject, String msg) throws AccountNotExistException {
		logger.info("sendMsgold-0#" + sendMethod);

		switch (sendMethod) {
		/**
		 * 先判斷是發送方式，如果是1(mail)呼叫另一個api
		 */
		case 1:
			return mailMsg(itemId, subject, msg);
		}

		NetAgent nAgent = new NetAgent();

		NodeList nodes;
		String postMsgURL;
		NodeFilter[] nodefilter;
		boolean results = false;
		autoLogin(bidAccount);
		Cookie[] cookies = getLoginSessionCookie(getAppId(), bidAccount);
		nAgent.getState().addCookies(cookies);
		Map postDataMap = new HashMap(); // 送出資料
		try {
			switch (sendMethod) {
			case 0: // 留言版
				postDataMap.put("aID", itemId);
				postDataMap.put("subject", subject);
				postDataMap.put("comment", "msgContact");// 使用簡單的訊息避過第一次的字數簡查
				nAgent.setPostDataMap(postDataMap);
				nAgent.postMaptoData();
				nAgent.getDataWithPost(previewMsgURL); // 預覽送出
				logger.info("postDataMap ::" + postDataMap);
				logger.info("previewMsgURL ::" + previewMsgURL);
				this.outputTofile(nAgent.getResponseBody(), "previewMsgURL");

				postDataMap = new HashMap();// 清空準備確認送出
				nodefilter = new NodeFilter[3];
				nodefilter[0] = new HTMLNodeFilter("INPUT");
				nodefilter[1] = new HasParentFilter(new HTMLNodeFilter("contact_submit"));
				nodefilter[2] = new HTMLNodeFilter("NAME");
				nodes = nAgent.filterItem(new AndFilter(nodefilter));
				for (int n = 0; n < nodes.size(); n++) {
					postDataMap.put(nodes.elementAt(n).toHtml().split("NAME=\"")[1].split("\"")[0], nodes.elementAt(n).toHtml().split("VALUE=\"")[1].split("\"")[0]);
				}
				postDataMap.put("comment", msg);
				nAgent.setPostDataMap(postDataMap);
				nAgent.postMaptoData();
				// 取得送出網址
				nodes = nAgent.filterItem(new AndFilter(new HTMLNodeFilter("METHOD"), new HTMLNodeFilter("disabledSubmit")));
				this.outputTofile(nAgent.getResponseBody(), "sendmsg");
				logger.info("TRY ::" + nodes.toHtml());
				postMsgURL = "";
				for (int n = 0; n < nodes.size(); n++) {
					postMsgURL = "http"
							+ nodes.elementAt(n).toHtml().split("\"http")[1].split("\"")[0];
				}
				nAgent.getDataWithPost(postMsgURL, charSet);
				if (nAgent.filterItem(new HTMLNodeFilter(this.CONTACT_FINISH)).size() > 0) {
					results = true;
				}
				break;
			case 2: // 揭示版
				postDataMap.put("aID", itemId);
				postDataMap.put("cc", "jp");
				postDataMap.put("showdiscuss", subject);
				postDataMap.put("comment", msg);
				nAgent.setPostDataMap(postDataMap);
				nAgent.postMaptoData();
				nAgent.getDataWithPost(discussPreviewURL); // 預覽送出

				nodefilter = new NodeFilter[3];
				nodefilter[0] = new HTMLNodeFilter("INPUT");
				nodefilter[1] = new HasParentFilter(new HTMLNodeFilter("ACTION=\"http"));
				nodefilter[2] = new HTMLNodeFilter("name");

				nodes = nAgent.filterItem(new AndFilter(nodefilter));
				for (int n = 0; n < nodes.size(); n++) {
					logger.info("[DEBUG]" + nodes.elementAt(n).toHtml());
					postDataMap.put(nodes.elementAt(n).toHtml().split("name=")[1].split(" ")[0], nodes.elementAt(n).toHtml().split("VALUE=\"")[1].split("\"")[0]);
				}
				postDataMap.put("comment", msg);
				nAgent.setPostDataMap(postDataMap);
				nAgent.postMaptoData();

				nodes = nAgent.filterItem(new AndFilter(new HTMLNodeFilter("METHOD"), new HTMLNodeFilter("discussion_submit")));
				postMsgURL = "";
				for (int n = 0; n < nodes.size(); n++) {
					postMsgURL = "http"
							+ nodes.elementAt(n).toHtml().split("\"http")[1].split("\"")[0];
				}
				nAgent.getDataWithPost(postMsgURL, charSet);

				if (nAgent.filterItem(new HTMLNodeFilter(this.DISCUSS_FINISH)).size() > 0) {
					results = true;
				}
				break;
			}
			updateActiveDate(bidAccount);
			
		} catch (ParserException e) {
			logger.error(e.getMessage(), e);
		}

		return results;
	}

	/**
	 * @param
	 */
	@Override
	public boolean autoLogin(String account, String pwd) {
		logger.info(account + " cookies:"
				+ this.getLoginSessionCookie(this.getAppId(), account).length);
		if (this.getLoginSessionCookie(this.getAppId(), account).length > 1) {
			NetAgent nAgent = new NetAgent();
			Cookie[] cookies = getLoginSessionCookie(this.getAppId(), account);
			nAgent.getState().addCookies(cookies);
			nAgent.getDataWithGetiPhone("https://lh.login.yahoo.co.jp/");
			try {
				if (nAgent.filterItem(new HTMLNodeFilter("履歴 - Yahoo! JAPAN")).size() > 0) {
					return true;
				} else {
					return false;
				}
			} catch (ParserException e) {
				logger.error(e.getMessage(), e);
			}
			return false;
		} else {

			login(account, pwd);

		}
		return false;
	}

	@Override
	public JSONArray bidItem(String account, String pwd, String itemURL,
			String price, String qty) throws AccountNotExistException {
		JSONArray jArray = new JSONArray();
		long l0 = System.currentTimeMillis();
		autoLogin(account);

		String bidItemMsg = "0";
		NetAgent nAgent = new NetAgent();
		nAgent.getParams().setParameter("http.socket.timeout", 5000);
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), account);
		nAgent.getState().addCookies(cookies);

		String itemId = itemURL.split("\\/")[itemURL.split("\\/").length - 1];
		NodeList nodes;

		try {

			Map tempMap = new HashMap();

			tempMap.put(NetAgent.YAHOO_JP_BID, price);// 價格
			tempMap.put("ItemID", itemId);// ItemID
			tempMap.put(NetAgent.YAHOO_JP_QUANTITY, qty);// 數量
			tempMap.put("bidType", "1000");// bidtype

			tempMap.put("cc", "jp");// cc
			tempMap.put("lastquantity", "1");// cc
			tempMap.put("login", account);// cc
			tempMap.put("setPrice", price);// cc

			nAgent.putDataInPostDataMap(tempMap);

			/** 取得回傳網址 */
			nodes = nAgent.filterItem(new HTMLNodeFilter("id=\"frmbb1\""));
			String bidPreviewUrl = "http://$PAGE.auctions.yahoo.co.jp/jp/show/bid_preview";

			bidPreviewUrl = bidPreviewUrl.replaceAll("\\$PAGE", "page"
					+ itemURL.split("page")[1].split("\\.")[0]);
			logger.debug("bidPreviewUrl:" + bidPreviewUrl);

			nAgent.postMaptoData();
			nAgent.getDataWithPostIPhone(bidPreviewUrl);
			nodes = nAgent.filterInputItem();
			nAgent.setParserNodesToPostDataMap(nodes);
			nAgent.postMaptoData();

			String bidUrl = "http://$BID.auctions.yahoo.co.jp/jp/config/placebid";

			bidUrl = bidUrl.replaceAll("\\$BID", "bid"
					+ itemURL.split("page")[1].split("\\.")[0]);

			logger.debug("bidUrl:" + bidUrl);
			nAgent.setResponseBody("");
			nAgent.getDataWithPostIPhone(bidUrl);

			if (nAgent.getResponseBody().length() == 0) {
				switch (this.isMyBid(account, itemURL).getInt(0)) {
				case 0:// 0 - 未得標
					bidItemMsg = "1";
					break;
				case 1:// 1 - 最高出價者
					bidItemMsg = "3";
					break;
				case 2:// 2 - 已得標
					bidItemMsg = "2";
					break;
				}
			} else {
				bidItemMsg = this.checkBidResult(nAgent.getResponseBody(), 1);
			}
			//this.outputTofile(nAgent.getResponseBody(), System.currentTimeMillis()+ "_" + itemId);
			updateActiveDate(account);
		} catch (ParserException e) {
			logger.info(e.getMessage(), e);
			bidItemMsg = "0";
		} catch (IllegalArgumentException e) {
			logger.info(e.getMessage(), e);
			bidItemMsg = "4";
		}
		long l1 = System.currentTimeMillis();
		logger.info("bidItem:" + itemURL + ":" + account + ":" + price + ":"
				+ bidItemMsg + ":" + (l1 - l0));
		jArray.add(bidItemMsg);
		return jArray;
	}

	/**
	 * 只接收帳號，自動到資料庫找密碼
	 * 
	 * @param uId
	 * @return
	 * @throws AccountNotExistException
	 *             帳號不存在
	 */
	public boolean autoLogin(String uId) throws AccountNotExistException {
		String pwd = "";
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
		JSONArray jArray = conn.queryJSONArray("mogan-DB", "SELECT * FROM system_bid_id WHERE delete_flag='1' and account='"
				+ uId + "'");
		if (jArray.size() > 0) {
			return autoLogin(jArray.getJSONObject(0).getString("account"), jArray.getJSONObject(0).getString("bid_password"));
		} else {
			throw new AccountNotExistException("此帳號不存在(" + uId + ")");
		}
	}

	/**
	 * 判斷下標結果
	 * 
	 * @param html
	 *            html內容
	 * @param type
	 *            網頁類型
	 * @return 回傳數字<br />
	 *         0 - 無法判斷<br />
	 *         1 - 下標成功，非最高出價<br />
	 *         2 - 下標成功，已得標<br />
	 *         3 - 下標成功，最高出價<br />
	 *         4 - 無法下標
	 */
	private String checkBidResult(String html, int type) {
		String resultMsg = "0";
		NetAgent nAgent = new NetAgent();
		nAgent.setResponseBody(html);
		this.outputTofile(nAgent.getResponseBody(), "checkBidResult");
		HTMLNodeFilter untnf = new HTMLNodeFilter("div class=\"untLeadBox\""); // 被超標結果訊息
		HTMLNodeFilter decRednf = new HTMLNodeFilter("p class=\"decRedBold\""); // 最高出價者或得標結果訊息
		try {
			if (nAgent.filterItem(untnf).size() == 1) {
				resultMsg = "1";
			} else if (nAgent.filterItem(new AndFilter(new HasParentFilter(decRednf), new HTMLNodeFilter("おめでとうございます!!"))).size() == 1) {
				resultMsg = "2";
			} else if (nAgent.filterItem(new AndFilter(new HasParentFilter(decRednf), new HTMLNodeFilter("あなたが現在の最高額入札者です。"))).size() == 1) {
				resultMsg = "3";
			} else if (nAgent.filterItem(new HTMLNodeFilter("li class=\"modErrorIcon\"")).size() == 1) {
				resultMsg = "4";
			}
		} catch (ParserException e) {
			logger.error(e.getMessage(), e);
		}
		return resultMsg;
	}

	@Override
	@Deprecated
	public JSONArray buyItem(String uId, String pwd, String itemURL, String qty)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONArray checkBidHistory(String webSiteURL, String uId,
			String itemURL) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBidURL() {
		return bidURL;
	}

	@Override
	public String getLoginURL() {
		return loginURL;
	}

	/**
	 * 090923<br />
	 * 判斷網頁中是否有id=modTradeStep的div標籤，有代表已得標 100527修改
	 * 
	 * @return 回傳數字<br />
	 *         0 - 未得標<br />
	 *         1 - 最高出價者<br />
	 *         2 - 已得標<br />
	 * @throws AccountNotExistException
	 */
	@Override
	public JSONArray isMyBid(String uId, String itemURL)
			throws AccountNotExistException {
		JSONArray jArray = new JSONArray();
		autoLogin(uId);
		NetAgent nAgent = new NetAgent();
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), uId);
		nAgent.getState().addCookies(cookies);
		nAgent.getDataWithGet(itemURL);
		String resultMsg = "0";
		try {

			HTMLNodeFilter divNf = new HTMLNodeFilter("div");
			NodeList nodes;
			boolean flag = false;
			for (int i = 1; i <= 2; i++) {
				if (flag) {
					break;
				}
				switch (i) {
				case 1:// 最高出價者
					nodes = nAgent.filterItem(new HTMLNodeFilter("id=\"modMsgBox\""));
					if (nodes.size() > 0) {
						resultMsg = "1";
						flag = true;
						break;
					}
					continue;
				case 2:// 已得標
					nodes = nAgent.filterItem(new AndFilter(divNf, new HTMLNodeFilter("id=\"modTradeStep\""))); // 已得標
					if (nodes.size() > 0) {
						resultMsg = "2";
						flag = true;
						break;
					}
					continue;
				}
			}
			updateActiveDate(uId);
			logger.info("isMyBid");
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jArray.add(resultMsg);
		return jArray;
	}



	@Override
	public JSONArray login(String uId, String pwd) {
		JSONArray jArray = new JSONArray();
		NetAgent nAgent = new NetAgent();
		String loginMsg = "0";// 未登入
		NodeList nodes;
		try {
			nAgent.getDataWithGetiPhone(this.getLoginURL());// 開啟登入畫面
			nodes = nAgent.filterInputItem();// 取得input項目
			nAgent.setParserNodesToPostDataMap(nodes);// 將nodes設入要post項目
			Map tempMap = new HashMap();// 設定帳號及密碼
			tempMap.put(nAgent.YAHOO_JP_ACCOUNT, uId);
			tempMap.put(nAgent.YAHOO_JP_PWD, pwd);
			nAgent.putDataInPostDataMap(tempMap);// //將Map設入要post項目
			nAgent.postMaptoData();// 將postMap轉成postData

			nodes = nAgent.filterFormLoginHref();// 過濾登入項目

			setWebSiteURL(nodes.elementAt(0).getText());
			setWebSiteURL(nAgent.getUrl(getWebSiteURL()));
			nAgent.getDataWithPostIPhone(getWebSiteURL());
			if (nAgent.getResponseCookies().length == 0) {
				loginMsg = "2";// 登入失敗
			} else {
				this.setLoginCookieMap(this.getWebSiteName(), uId, pwd, nAgent.getResponseCookies());
				updateLoginDate(uId);
				loginMsg = "1";// 登入成功
			}
		} catch (ParserException e) {
			logger.error(e.getMessage(), e);
			loginMsg = "2";// 登入失敗
		}
		logger.info("YAHOO JP LOGIN loginMsg::" + loginMsg + " account:" + uId
				+ ":" + pwd);
		jArray.add(loginMsg);
		return jArray;
	}

	/**
	 * 更新登入時間
	 */
	private void updateLoginDate(String uId){
		DBConn conn=SysKernel.getConn();
		try {
			conn.executSql((String) SysKernel.getApplicationAttr(SysKernel.MAIN_DB), "UPDATE system_bid_id SET last_login_date=NOW() WHERE account='"+uId+"' AND website_id='SWD-2009-0001' ");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 更新操作時間
	 */
	private void updateActiveDate(String uId){
		DBConn conn=SysKernel.getConn();
		try {
			conn.executSql((String) SysKernel.getApplicationAttr(SysKernel.MAIN_DB), "UPDATE system_bid_id SET last_active_date=NOW() WHERE account='"+uId+"' AND website_id='SWD-2009-0001' ");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 修正留言時間為資料庫可以接受的時間格式，日雅專用
	 * 
	 * @param msgDate
	 * @return
	 */
	private String fixYJDate(String msgDate) {
		Calendar rightNow = Calendar.getInstance();
		rightNow.set(Calendar.MONTH, Integer.parseInt(msgDate.split("月")[0]) - 1);
		rightNow.set(Calendar.DATE, Integer.parseInt(msgDate.split("月")[1].split("日")[0].trim()));
		rightNow.set(Calendar.HOUR_OF_DAY, Integer.parseInt(msgDate.split("日")[1].split("時")[0].trim()));
		rightNow.set(Calendar.MINUTE, Integer.parseInt(msgDate.split("時")[1].split("分")[0].trim()));
		SysCalendar calendar = new SysCalendar();
		msgDate = calendar.getFormatDate(rightNow.getTime(), SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql);
		calendar = null;
		return msgDate;
	}

}
