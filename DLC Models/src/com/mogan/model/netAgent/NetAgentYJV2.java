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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.htmlparser.Node;
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

import com.mogan.exception.MoganException;
import com.mogan.exception.netAgent.AccountNotExistException;
import com.mogan.face.BidFace;
import com.mogan.face.NetAgentModel;
import com.mogan.sys.DBConn;
import com.mogan.sys.SysCalendar;
import com.mogan.sys.SysEnCoding;
import com.mogan.sys.log.SysLogger4j;
import com.mogan.sys.mail.MailSenderInfo;
import com.mogan.sys.mail.SimpleMailSender;

/**
 * 日本雅虎專用，新版DB專用，
 * 
 * @author Dian
 */
public class NetAgentYJV2 extends NetAgentModel implements BidFace {
	final static private String CONN_ALIAS = "mogan-DB";
	
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
	public final static String CONTACT_BOARD = "取引ナビで連絡";// 訊息來源KEY WORD
	public final static String CONTACT_FINISH = "投稿が完了しました。";// 
	public final static String DISCUSS_FINISH = "送信完了しました。";//
	/** 資料來源為資料庫 */
	public final static String DATA_SOURCE_DB = "DB";
	/** 資料來源為網頁 */
	public final static String DATA_SOURCE_WEB = "WEB";
	private final String charSet = "euc-jp";

	/** 發出mail時寄件人的名字 */
	private String mailSenderName = "";
	/** 發出mail時寄件人的信箱 */
	private String mailSenderAddress = "";
	/** 發出mail時要通知道的密件副本 */
	private String mailCC = "";

	public NetAgentYJV2(ServletContext servletContext, String appId) {
		super();
		this.setModelServletContext(servletContext);
		this.setAppId(appId);
	}

	/**
	 * 取得出價記錄
	 * 
	 * @param bidId
	 *            - 下標或上架帳號
	 * @param yahooItemId
	 *            - 商品ID
	 * @param listType
	 *            - 清單類型 TOP_LIST 簡易清單、LOG_LIST完整清單
	 * @param page
	 *            - 頁數50第一頁
	 * @return 回傳清單內容
	 * @throws AccountNotExistException
	 */
	public JSONArray getBidList(String bidId, String yahooItemId,
			String listType, String page) throws AccountNotExistException {

		JSONArray jArray = new JSONArray();
		autoLogin(bidId);
		NetAgent nAgent = new NetAgent();
		Cookie[] cookies = getLoginSessionCookie(getAppId(), bidId);
		nAgent.getState().addCookies(cookies);
		String topDivKey = "";// 清單資訊DIV
		String listDivKey = "";// 清單資料DIV
		String listHtml = "";
		if (listType.equals("TOP_LIST")) {
			nAgent.getDataWithGet(BID_HIST_URL_A.replaceAll("\\$YAHOO_ITEM_ID",
					yahooItemId).replaceAll("\\$PAGE", page));
			topDivKey = "div class=\"modPgCnt\"";
			listDivKey = "div id=\"modCtgSearchResult\"";
			System.out.println(BID_HIST_URL_A.replaceAll("\\$YAHOO_ITEM_ID",
					yahooItemId).replaceAll("\\$PAGE", page));
		} else if (listType.equals("LOG_LIST")) {
			topDivKey = "div class=\"modPgCntTop\"";
			listDivKey = "div id=\"modCtgSearchResult\"";
			nAgent.getDataWithGet(BID_HIST_URL_B.replaceAll("\\$YAHOO_ITEM_ID",
					yahooItemId).replaceAll("\\$PAGE", page));
			System.out.println(BID_HIST_URL_B.replaceAll("\\$YAHOO_ITEM_ID",
					yahooItemId).replaceAll("\\$PAGE", page));
		}
		this.outputTofile(nAgent.getResponseBody());
		try {
			// 取得清單資訊內容
			NodeList nodes = nAgent.filterItem(new HTMLNodeFilter(topDivKey));
			listHtml += nodes.toHtml();
			// 取得清個資料
			nodes = nAgent.filterItem(new HTMLNodeFilter(listDivKey));
			listHtml += nodes.toHtml();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jArray.add(listHtml);
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
		nAgent.getDataWithGet(BID_HIST_URL_A.replaceAll("\\$YAHOO_ITEM_ID",
				yahooItemId).replaceAll("\\$PAGE", page));

		String highPriceAccount = "";
		try {
			NodeList nodes = nAgent.filterItem(new OrFilter(new HasChildFilter(
					new HTMLNodeFilter("最高額入札者")), new HasChildFilter(
					new HTMLNodeFilter("落札者"))));
			Map infoMap = new HashMap();
			for (int i = 0; i < nodes.size(); i++) {
				highPriceAccount = nodes.elementAt(i).toPlainTextString()
						.split(" / 評価")[0];
				infoMap.put("ACCOUNT", nodes.elementAt(i).toPlainTextString()
						.split(" / 評価")[0]); // 帳號
				infoMap.put("PRICE", nodes.elementAt(i).getParent()
						.getChildren().elementAt(3).toPlainTextString()
						.replaceAll(" |円|,", "")); // 價錢
				infoMap.put("UNIT", nodes.elementAt(i).getParent()
						.getChildren().elementAt(5).toPlainTextString()
						.replaceAll(",", "")); // 數量
				infoMap.put("DATE", nodes.elementAt(i).getParent()
						.getChildren().elementAt(7).toPlainTextString()); // 時間
				// jArray.add(highPriceAccount);
				jArray.add(infoMap);
			}
			nodes = nAgent.filterItem(new HTMLNodeFilter(
					"div id=\"modCtgSearchResult\""));

		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jArray;
	}

	/**
	 * 回答問題
	 * 
	 * @param agentAccount
	 * @param itemId
	 * @param msg
	 * @return
	 */
	private JSONArray ansQuestion(String agentAccount, String itemId, String msg) {
		JSONArray jArray = new JSONArray();
		return jArray;
	}

	/**
	 * 發問問題
	 * 
	 * @param bidId
	 * @param question
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
		nAgent.getDataWithGet(QUEST_URL_A.replaceAll("\\$YAHOO_ITEM_ID",
				yahooItemId));
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
			nodes = nAgent.filterItem(new HTMLNodeFilter(
					".auctions.yahoo.co.jp/jp/config/qanda_submit"));
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
	 * 傳送訊息
	 * 
	 * @param bidAccount
	 * @param itemId
	 * @param sendMethod
	 * @param msgTitle
	 * @param msgContact
	 * @return
	 * @throws AccountNotExistException
	 */
	public JSONArray sendMsg(String bidAccount, String itemId,
			String sendMethod, String msgTitle, String msgContact)
			throws AccountNotExistException {
		return sendMsg(bidAccount, itemId, sendMethod, msgTitle, msgContact, "");
	}

	/**
	 * 取得商品賣家ID
	 * 
	 * @param itemId
	 * @return 查詢資料
	 */
	private HashMap getSellItemData(String itemId) {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String sql = "SELECT * FROM view_sell_item_order WHERE item_order_id ='"
				+ itemId + "'";

		ArrayList<Map> data = conn.query("mogan-DB", sql);
		if (data.size() > 0) {
			return (HashMap) data.get(0);
		} else {
			return new HashMap();
		}

	}

	/**
	 * 下載遠端網站的檔案
	 * 
	 * @param urlString
	 * @return
	 */
	private File loadUrlFile(String urlString, String tempFileName) {
		// 
		SysCalendar sc = new SysCalendar();
		sc.setDateFormat("dd");
		File f = new File("/tempFile_" + sc.getFormatDate() + "/"
				+ tempFileName);
		f.getParentFile().mkdirs();
		try {
			// 構造URL
			URL url = new URL(urlString);
			// 打開連接
			URLConnection con = url.openConnection();
			// 輸入流
			InputStream is = con.getInputStream();
			// 1K的數據緩衝
			byte[] bs = new byte[2048];
			// 讀取到的數據長度
			int len;
			// 輸出的文件流
			OutputStream os;

			os = new FileOutputStream(f);
			// 開始讀取
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
			// 完畢，關閉所有鏈接
			os.close();
			is.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return f;
	}

	/**
	 * 商品下架
	 * 
	 * @param itemOrderId
	 * @return
	 * @throws AccountNotExistException
	 */
	public JSONArray unsellItem(String itemOrderId)
			throws AccountNotExistException {
		JSONArray jArray = new JSONArray();
		String returnStr = "0";
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		Map sellItemDataMap = getSellItemData(itemOrderId);
		NetAgent nAgent = new NetAgent();
		String sellAccount = (String) sellItemDataMap.get("account");// 賣家帳號

		autoLogin(sellAccount);
		Cookie[] cookies = getLoginSessionCookie(getAppId(), sellAccount);
		nAgent.getState().addCookies(cookies);

		String yahooBidId = (String) sellItemDataMap.get("item_id");
		nAgent.getDataWithGet(this.UNSELL_URL_A.replaceAll("\\$YAHOO_ITEM_ID",
				yahooBidId));

		NodeList nodes;
		try {
			nodes = nAgent.filterInputItem();
			nAgent.setParserNodesToPostDataMap(nodes);
			nAgent.postMaptoData();
			String unSellUrl = nAgent.getUrl(nAgent.filterFormHttpHref()
					.elementAt(0).getText());
			nAgent.getDataWithPost(unSellUrl);
			returnStr = "1";
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		jArray.add(returnStr);
		return jArray;
	}

	/**
	 * 商品重新上刊
	 * 
	 * @param itemOrderId
	 * @return 0-失敗，其他數字為yahoo 商品 ID
	 * @throws AccountNotExistException
	 */
	public JSONArray resellItem(String itemOrderId)
			throws AccountNotExistException {
		JSONArray jArray = new JSONArray();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		Map sellItemDataMap = getSellItemData(itemOrderId);
		NetAgent nAgent = new NetAgent();
		String sellAccount = (String) sellItemDataMap.get("account");// 賣家帳號

		autoLogin(sellAccount);
		Cookie[] cookies = getLoginSessionCookie(getAppId(), sellAccount);
		nAgent.getState().addCookies(cookies);

		String yahooBidId = (String) sellItemDataMap.get("item_id");
		String returnStr = "0";
		try {
			// 取得重新上刊畫面
			nAgent.getDataWithGet(this.RESELL_URL_A.replaceAll(
					"\\$YAHOO_ITEM_ID", yahooBidId));

			NodeList nodes = nAgent.filterInputItem();
			nAgent.setParserNodesToPostDataMap(nodes);
			String itemDesc = nAgent.filterItem(
					new HasParentFilter(
							new HTMLNodeFilter("id=\"Description\""))).toHtml();
			// 修正商品描述
			Map postMap = nAgent.getPostDataMap();
			postMap.put("UEDescription", itemDesc); // 商品描述
			postMap.put("Description_plain", itemDesc); // 商品描述
			postMap.put("Description_plain_work", ""); // 商品描述
			postMap.put("Description_rte", itemDesc.replaceAll("\"", "%22")); // 商品描述
			postMap = fixSellItemPostData(postMap);
			nAgent.setPostDataMap(postMap);
			nAgent.postMaptoData();
			String previewUrl = "http://" + postMap.get("auction_server")
					+ "/show/preview";
			// 取得預覽畫面
			nAgent.getDataWithPost(previewUrl, charSet);

			nodes = nAgent.filterItem(new HTMLNodeFilter("auc_preview_submit"));
			String submitURL = nAgent.getHttp(nodes.elementAt(0).toHtml());
			nodes = nAgent.filterItem(new AndFilter(new HasParentFilter(
					new HTMLNodeFilter("id=\"modFormSbt\"")),
					new HTMLNodeFilter("input")));
			nAgent.setParserNodesToPostDataMap(nodes);
			postMap = fixSellItemPostData(postMap);
			nAgent.putAllPostDataMap(postMap);
			nAgent.postMaptoData();
			// 重新上刊
			nAgent.getDataWithPost(submitURL, charSet);

			nodes = nAgent.filterItem(new AndFilter(
					new HTMLNodeFilter("a href"), new HTMLNodeFilter("aID")));
			String aId = nodes.elementAt(0).toHtml().split("aID=")[1]
					.split("\"")[0];
			String updateItemBidId = "UPDATE item_data set item_id='" + aId
					+ "' where item_data_id='"
					+ sellItemDataMap.get("item_data_id") + "'";

			conn.executSql("mogan-DB", updateItemBidId);

			returnStr = aId;
			// http://page18.auctions.yahoo.co.jp/jp/show/resubmit?aID=w45993880
		} catch (ParserException e) {
			SysLogger4j.error("NetAgent Yahoo JP",e);
		} catch (UnsupportedEncodingException e) {
			SysLogger4j.error("NetAgent Yahoo JP",e);
		} catch (SQLException e) {
			SysLogger4j.error("NetAgent Yahoo JP",e);
		}
		jArray.add(returnStr);
		return jArray;
		// http://page18.auctions.yahoo.co.jp/jp/show/resubmit?aID=w45993880
	}

	/**
	 * 將商品上架
	 * 
	 * @param itemOrderId
	 *            MOGAN 訂單ID
	 * @return 0-失敗，其他數字為yahoo 商品 ID
	 * @throws Exception
	 */
	public JSONArray sellItem(String itemOrderId) throws Exception {
		JSONArray jArray = new JSONArray();
		NetAgent nAgent = new NetAgent();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		Map postMap = new HashMap();
		Map sellItemDataMap = getSellItemData(itemOrderId);
		String sellAccount = (String) sellItemDataMap.get("account");// 賣家帳號
		autoLogin(sellAccount);
		String returnStr = "0";

		try {

			Cookie[] cookies = getLoginSessionCookie(getAppId(), sellAccount);
			nAgent.getState().addCookies(cookies);

			// 開啟分類篩選頁面
			nAgent.getDataWithGet(this.SELL_URL_A);

			// 開啟商品資料填寫頁面
			NodeList nodes = nAgent.filterInputItem();
			nAgent.setParserNodesToPostDataMap(nodes);

			postMap.put("category", sellItemDataMap.get("e_varchar01"));
			// TODO 分類為可填寫
			nAgent.putAllPostDataMap(postMap);
			nAgent.postMaptoData();

			nAgent.getDataWithPost(this.SELL_URL_B);

			AndFilter andFilter = new AndFilter(new HasParentFilter(
					new HTMLNodeFilter("id=\"auction\"")), new HTMLNodeFilter(
					"input"));

			nodes = nAgent.filterItem(andFilter);

			nAgent.setParserNodesToPostDataMap(nodes);

			postMap = getSellItemPostData(sellItemDataMap, nAgent
					.getPostDataMap());

			nAgent.putAllPostDataMap(postMap);
			nAgent.postMaptoData();

			// 設定上傳圖片
			// 上傳圖片 1
			if (sellItemDataMap.get("main_image") != null
					&& ((String) sellItemDataMap.get("main_image")).length() > 0) {
				nAgent.addPostFileItem("ImageFile1", loadUrlFile(this
						.getProperty("imgServerPath")
						+ sellItemDataMap.get("main_image"), "ImageFile1"));
			}

			// 上傳圖片 2
			if (sellItemDataMap.get("e_varchar02") != null
					&& ((String) sellItemDataMap.get("e_varchar02")).length() > 0) {
				nAgent.addPostFileItem("ImageFile2", loadUrlFile(this
						.getProperty("imgServerPath")
						+ sellItemDataMap.get("e_varchar02"), "ImageFile2"));
			}

			// 上傳圖片 3
			if (sellItemDataMap.get("e_varchar03") != null
					&& ((String) sellItemDataMap.get("e_varchar03")).length() > 0) {
				nAgent.addPostFileItem("ImageFile3", loadUrlFile(this
						.getProperty("imgServerPath")
						+ sellItemDataMap.get("e_varchar03"), "ImageFile3"));
			}

			nAgent.getDateWithPostFile("http://"
					+ nAgent.getPostDataMap().get("uploadserver")
					+ IMG_UPLOAD_URL);

			// 開啟預灠
			nodes = nAgent.filterInputItem();

			nAgent.setParserNodesToPostDataMap(nodes);
			postMap = getSellItemPostData(sellItemDataMap, nAgent
					.getPostDataMap());
			nAgent.setPostDataMap(postMap);

			nAgent.postMaptoData();
			String previewUrl = "http://" + postMap.get("auction_server")
					+ "/show/preview";

			nAgent.getDataWithPost(previewUrl, charSet);

			// 送出
			nodes = nAgent.filterItem(new HTMLNodeFilter("auc_preview_submit"));

			String submitURL = nAgent.getHttp(nodes.elementAt(0).toHtml());
			andFilter = new AndFilter(new HasParentFilter(new HTMLNodeFilter(
					"id=\"modFormSbt\"")), new HTMLNodeFilter("input"));
			nodes = nAgent.filterItem(andFilter);
			nAgent.setParserNodesToPostDataMap(nodes);
			postMap = getSellItemPostData(sellItemDataMap, nAgent
					.getPostDataMap());
			nAgent.putAllPostDataMap(postMap);
			nAgent.postMaptoData();
			nAgent.getDataWithPost(submitURL, charSet);

			// 取得商品拍賣ID
			andFilter = new AndFilter(new HTMLNodeFilter("a href"),
					new HTMLNodeFilter("aID"));
			nodes = nAgent.filterItem(andFilter);
			String aId = nodes.elementAt(0).toHtml().split("aID=")[1]
					.split("\"")[0];
			String updateItemBidId = "UPDATE item_data set item_id='" + aId
					+ "' where item_data_id='"
					+ sellItemDataMap.get("item_data_id") + "'";

			conn.executSql("mogan-DB", updateItemBidId);

			returnStr = aId;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("上刊失敗");
		} finally {
			this.outputTofile(nAgent.getResponseBody());
		}

		jArray.add(returnStr);
		return jArray;
	}

	/**
	 * 修正要傳送的內容
	 * 
	 * @param postMap
	 * @return
	 */
	private Map fixSellItemPostData(Map postMap) {
		postMap.put("loc_cd", "48");
		postMap.remove("doDraft");
		postMap.remove("aspj4");
		postMap.remove("aspj3");
		postMap.remove("p");
		postMap.remove("Description_rte");
		postMap.remove("aspj14");
		postMap.remove("safetydelivery");
		postMap.remove("affiliate");
		postMap.remove("Description");
		postMap.remove("wrappingSelected");
		postMap.remove("ypmOK");
		postMap.remove("sd_anonymous");
		postMap.remove("Offer");
		postMap.remove("city");
		postMap.remove("showCase1");
		postMap.remove("showCase3");
		postMap.remove("showCase4");
		postMap.remove("boldSelected");
		postMap.remove("highlightlistingSelected");
		postMap.remove("starclub");// 無料（開始価格を1円にしてください）

		if (postMap.get("BidOrBuyPrice").equals("0")) {
			postMap.put("BidOrBuyPrice", "");
		}

		postMap.remove("highlightlistingSelected");
		return postMap;
	}

	/**
	 * 去除多餘的參數，保留必須的參數
	 * 
	 * @param sellItemDataMap
	 *            - 資料庫資料
	 * @param inputItemMap
	 *            - 網頁原有參數
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private Map getSellItemPostData(Map sellItemDataMap, Map inputItemMap)
			throws UnsupportedEncodingException {
		// 商品狀態
		Map postMap = new HashMap();
		NetAgent nAgent = new NetAgent();

		nAgent.getDataWithGet(this.getProperty("templetServerPath")
				+ sellItemDataMap.get("e_int11"));
		String itemDesc = nAgent.getResponseBody();

		itemDesc = itemDesc.replaceAll("%item_description%",
				(String) sellItemDataMap.get("e_text02"));// 商品描述

		itemDesc = itemDesc.replaceAll("%height%", (String) sellItemDataMap
				.get("item_high"));// 高
		itemDesc = itemDesc.replaceAll("%length%", (String) sellItemDataMap
				.get("item_long"));// 長
		itemDesc = itemDesc.replaceAll("%width%", (String) sellItemDataMap
				.get("item_wide"));// 寬
		itemDesc = itemDesc.replaceAll("%weight%", (String) sellItemDataMap
				.get("item_kg"));// 重量

		postMap.putAll(inputItemMap);
		postMap.put("Title", sellItemDataMap.get("e_varchar06")); // 商品名稱
		postMap.put("category", sellItemDataMap.get("e_varchar01")); // 商品分類

		postMap.put("UEDescription", itemDesc); // 商品描述

		postMap.put("Description_plain", itemDesc); // 商品描述
		postMap.put("Description_plain_work", ""); // 商品描述

		postMap.put("Description_rte", itemDesc.replaceAll("\"", "%22")); // 商品描述

		// postMap.put("Description_rte", "說明"); // 商品描述
		postMap.put("Description_rte_work", ""); // 商品描述

		postMap.put("Quantity", sellItemDataMap.get("unit")); // 商品數量

		if (sellItemDataMap.get("main_image") != null
				&& ((String) sellItemDataMap.get("main_image")).length() > 0) {
			postMap.put("image_comment1", sellItemDataMap.get("e_varchar12")); // 圖片說明-1
		}
		if (sellItemDataMap.get("main_image") != null
				&& ((String) sellItemDataMap.get("main_image")).length() > 0) {
			postMap.put("image_comment2", sellItemDataMap.get("e_varchar13")); // 圖片說明-2
		}
		if (sellItemDataMap.get("main_image") != null
				&& ((String) sellItemDataMap.get("main_image")).length() > 0) {
			postMap.put("image_comment3", sellItemDataMap.get("e_varchar14")); // 圖片說明-3
		}

		if (sellItemDataMap.get("e_int04") != null
				&& Integer.parseInt((String) sellItemDataMap.get("e_int04")) > 0) {
			postMap.put("BidOrBuyPrice", sellItemDataMap.get("e_int04")); // 直購價
		}
		postMap.put("StartPrice", sellItemDataMap.get("e_int02")); // 起標價

		postMap.put("Duration", sellItemDataMap.get("e_int08")); // 拍賣天數
		postMap.put("ClosingDate", sellItemDataMap.get("e_int08")); // 拍賣天數
		postMap.put("ClosingTime", sellItemDataMap.get("e_int09")); // 結標時間
		postMap.put("AutoExtension", "yes"); // 自動延長
		postMap.put("CloseEarly", "yes"); // 提早結標 早期終了

		if (Integer.parseInt((String) sellItemDataMap.get("e_int06")) > 0) {
			postMap.put("ReservePrice", sellItemDataMap.get("e_int07")); // 低標
		}

		postMap.put("istatus", sellItemDataMap.get("e_varchar04")); // 商品狀態分類 used(中古)、new(新品)、other(その他)
		postMap.put("istatus_comment", sellItemDataMap.get("e_varchar08")); // 商品狀態描述

		if (Integer.parseInt((String) sellItemDataMap.get("e_int13")) > 0) {
			if (Integer.parseInt((String) sellItemDataMap.get("e_int25")) > 1) {
				postMap.put("giftSelected", sellItemDataMap.get("e_int25")); // 加小圖
			} else {
				postMap.remove("giftSelected"); // 加小圖
			}
			postMap.put("highlightlistingSelected", sellItemDataMap
					.get("e_int14")); // 加底色
			postMap.put("boldSelected", sellItemDataMap.get("e_int15")); // 加粗
		}

		postMap.put("intlOK", "1"); // 海外発送にも対応する
		// postMap.put("itemsize", ""); // 包裝後商品大小，選項式 e_varchar15 e_varchar16 e_varchar17
		// postMap.put("itemweight", sellItemDataMap.get("e_varchar18")); // 包裝後商品重量，選項式
		postMap.put("loc_cd", "48"); // 商品所在地 48海外
		postMap.put("minBidRating", "0"); // 買家最低評價限制
		postMap.put("mode", "submit"); // ????????
		postMap.put("numResubmit", sellItemDataMap.get("e_int12")); // 自動上架次數0~3
		postMap.put("oldAID", ""); // 舊商品ID
		postMap.put("aID", ""); // 商品ID

		postMap.put("shipping", sellItemDataMap.get("e_varchar15")); // 運費負擔 buyer seller
		postMap.put("shiptime", "payment"); // 出運時間 payment代金先払い、close代金後払い
		postMap.put("retpolicy", "0"); // 退貨 0-不可退貨，1-可退貨
		postMap.put("retpolicy_comment", ""); // 退貨說明

		postMap.put("aspj1", "yes"); // 銀行振込
		postMap.put("bkname1", ""); // 銀行名稱-1
		postMap.put("bkname2", ""); // 銀行名稱-2
		postMap.put("bkname3", ""); // 銀行名稱-3
		postMap.put("bkname4", ""); // 銀行名稱-4
		postMap.put("bkname5", ""); // 銀行名稱-5
		postMap.put("bkname6", ""); // 銀行名稱-6
		postMap.put("bkname7", ""); // 銀行名稱-7
		postMap.put("bkname8", ""); // 銀行名稱-8
		postMap.put("bkname9", ""); // 銀行名稱-9
		postMap.put("bkname10", ""); // 銀行名稱-10

		postMap.put("paymethod1", ""); // ほかに決済方法があれば入力してください
		postMap.put("paymethod2", ""); // ほかに決済方法があれば入力してください
		postMap.put("paymethod3", ""); // ほかに決済方法があれば入力してください
		postMap.put("paymethod4", ""); // ほかに決済方法があれば入力してください
		postMap.put("paymethod5", ""); // ほかに決済方法があれば入力してください
		postMap.put("paymethod6", ""); // ほかに決済方法があれば入力してください
		postMap.put("paymethod7", ""); // ほかに決済方法があれば入力してください
		postMap.put("paymethod8", ""); // ほかに決済方法があれば入力してください
		postMap.put("paymethod9", ""); // ほかに決済方法があれば入力してください
		postMap.put("paymethod10", ""); // ほかに決済方法があれば入力してください

		postMap.put("shipfee1", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipfee2", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipfee3", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipfee4", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipfee5", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipfee6", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipfee7", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipfee8", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipfee9", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipfee10", ""); // 配送方法（運送会社、サービス名など）を記入してください

		postMap.put("shipname1", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipname2", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipname3", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipname4", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipname5", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipname6", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipname7", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipname8", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipname9", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipname10", ""); // 配送方法（運送会社、サービス名など）を記入してください

		postMap.put("shipratelink1", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipratelink2", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipratelink3", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipratelink4", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipratelink5", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipratelink6", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipratelink7", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipratelink8", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipratelink9", ""); // 配送方法（運送会社、サービス名など）を記入してください
		postMap.put("shipratelink10", ""); // 配送方法（運送会社、サービス名など）を記入してください

		return fixSellItemPostData(postMap);
	}

	/**
	 * 傳送訊息
	 * 
	 * @param bidAccount
	 *            下標帳號
	 * @param itemId
	 *            商品ID
	 * @param sendMethod
	 *            聯絡方法 0-留言版，1-Email，2-揭
	 * @param msgTitle
	 *            訊息標題
	 * @param msgContact
	 *            訊息內容
	 * @param toAddress
	 *            收件人Email
	 * @return
	 * @throws AccountNotExistException
	 */
	public JSONArray sendMsg(String bidAccount, String itemId,
			String sendMethod, String msgTitle, String msgContact,
			String toAddress) throws AccountNotExistException {

		JSONArray jArray = new JSONArray();
		NetAgent nAgent = new NetAgent();
		boolean results = false;
		autoLogin(bidAccount);
		Cookie[] cookies = getLoginSessionCookie(getAppId(), bidAccount);
		nAgent.getState().addCookies(cookies);

		// 開啟訊息頁面
		// nAgent.getDataWithGet(contactUrl.replaceAll("\\$MOGAN_ITEM_ID", itemId));
		try {
			Map postDataMap = new HashMap();
			NodeList nodes;
			HTMLNodeFilter inputNf;
			HTMLNodeFilter nameNf;
			HasParentFilter parnetFilter;
			AndFilter andFilter;
			String postMsgURL = "";
			// 判斷發送訊息的方法
			switch (Integer.valueOf(sendMethod)) {
			case 0:// 留言版
				postDataMap.put("aID", itemId);
				postDataMap.put("subject", msgTitle);
				postDataMap.put("comment", msgContact);
				postDataMap.put("comment", "msgContact");
				nAgent.setPostDataMap(postDataMap);

				nAgent.postMaptoData();
				// System.out.println("[DEBUG] sendMsg.postMsgURL::....test."+((String)postDataMap.get("comment")).length());
				nAgent.getDataWithPost(previewMsgURL);
				this.outputTofile(nAgent.getResponseBody());
				postDataMap = new HashMap();

				inputNf = new HTMLNodeFilter("INPUT");
				nameNf = new HTMLNodeFilter("NAME");
				parnetFilter = new HasParentFilter(new HTMLNodeFilter(
						"contact_submit"));
				andFilter = new AndFilter(inputNf, parnetFilter);
				andFilter = new AndFilter(andFilter, nameNf);

				nodes = nAgent.filterItem(andFilter);
				for (int n = 0; n < nodes.size(); n++) {
					postDataMap.put(nodes.elementAt(n).toHtml()
							.split("NAME=\"")[1].split("\"")[0], nodes
							.elementAt(n).toHtml().split("VALUE=\"")[1]
							.split("\"")[0]);
				}
				postDataMap.put("comment", msgContact);

				andFilter = new AndFilter(new HTMLNodeFilter("METHOD"),
						new HTMLNodeFilter("disabledSubmit"));
				nodes = nAgent.filterItem(andFilter);
				postMsgURL = "";
				for (int n = 0; n < nodes.size(); n++) {
					postMsgURL = "http"
							+ nodes.elementAt(n).toHtml().split("\"http")[1]
									.split("\"")[0];
				}
				this.outputTofile(nAgent.getResponseBody());
				this.outputTofile(msgContact, "sendwonmsg");
				nAgent.setPostDataMap(postDataMap);
				nAgent.postMaptoData();
				
				 
				nAgent.getDataWithPost(postMsgURL, charSet);

				if (nAgent.filterItem(new HTMLNodeFilter(this.CONTACT_FINISH))
						.size() > 0) {
					results = true;
				}
				break;
			case 1:// E-MAIL
				SimpleMailSender sms = new SimpleMailSender();
				System.out.println("[DEBUG] sendMail-1");
				msgContact = "<html><body>"
						+ msgContact.replaceAll("(\r|\n|\r\n)", "<br />")
						+ "</body></html>";
				sms.setMailSubject(msgTitle);
				sms.setMailContent(msgContact);
				sms.setMailServerHost("adsx.mogan.com.tw");
				sms.setMailServerPort("25");
				sms.setAccount("");
				sms.setPwd("");

				sms.setFromName(this.getMailSenderName());
				sms.setFromAddress(this.getMailSenderAddress());

				ArrayList dataList = new ArrayList();
				System.out.println("[DEBUG] sendMail-2");
				sms.setTargetList(dataList);

				/* 寄送mail開始 */
				MailSenderInfo mail = sms.getMailInfo("", toAddress, "", this
						.getMailCC());
				results = sms.sendHtmlMail(mail);
				System.out.println("[DEBUG] sendMail-3" + jArray);
				break;
			case 2:// 揭示版
				postDataMap.put("aID", itemId);
				postDataMap.put("cc", "jp");
				postDataMap.put("showdiscuss", msgTitle);
				postDataMap.put("comment", msgContact);
				nAgent.setPostDataMap(postDataMap);
				nAgent.postMaptoData();
				nAgent.getDataWithPost(discussPreviewURL);

				inputNf = new HTMLNodeFilter("INPUT");
				nameNf = new HTMLNodeFilter("name");
				parnetFilter = new HasParentFilter(new HTMLNodeFilter(
						"ACTION=\"http"));
				andFilter = new AndFilter(inputNf, parnetFilter);
				andFilter = new AndFilter(andFilter, nameNf);

				nodes = nAgent.filterItem(andFilter);
				for (int n = 0; n < nodes.size(); n++) {
					System.out.println("[DEBUG]" + nodes.elementAt(n).toHtml());
					postDataMap.put(
							nodes.elementAt(n).toHtml().split("name=")[1]
									.split(" ")[0], nodes.elementAt(n).toHtml()
									.split("VALUE=\"")[1].split("\"")[0]);
				}
				postDataMap.put("comment", msgContact);
				System.out.println("[DEBUG]" + postDataMap);
				andFilter = new AndFilter(new HTMLNodeFilter("METHOD"),
						new HTMLNodeFilter("discussion_submit"));
				nodes = nAgent.filterItem(andFilter);
				postMsgURL = "";
				for (int n = 0; n < nodes.size(); n++) {
					postMsgURL = "http"
							+ nodes.elementAt(n).toHtml().split("\"http")[1]
									.split("\"")[0];
				}
				nAgent.setPostDataMap(postDataMap);
				nAgent.postMaptoData();
				nAgent.getDataWithPost(postMsgURL, charSet);

				if (nAgent.filterItem(new HTMLNodeFilter(this.DISCUSS_FINISH))
						.size() > 0) {
					results = true;
				}
				System.out.println(postDataMap);
				// 送信完了しました。
				break;
			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (results) {
			jArray.add("1");
		} else {
			jArray.add("0");
		}

		return jArray;
	}

	/**
	 * 把URL中GET的參數變成INPUT HIDDEN類型的參數
	 * 
	 * @param args
	 * @return
	 */
	public String fixGetArgs2PostArgs(String args) {
		// String s=
		// "<INPUT TYPE=\"button\" VALUE=\"免責事項に同意した上で送信\" onClick=\"javascript:jumpPage('orderform','/jp/config/orderform?save=order_form&.crumb=zyj5nCAuUUf')\">"
		// ;
		// s=s.split("javascript:jumpPage\\('orderform','/jp/config/orderform\\?")[1].split("'")[0];
		StringBuffer newArgs = new StringBuffer();
		String[] oldArgs = args.split("&");
		for (int i = 0; i < oldArgs.length; i++) {
			newArgs.append("<INPUT TYPE=\"HIDDEN\" NAME=\""
					+ oldArgs[i].split("=")[0] + "\" VALUE=\""
					+ oldArgs[i].split("=")[1] + "\">\n");
		}
		// System.out.println(newArgs);
		return newArgs.toString();

	}

	/**
	 * 送出order form
	 * 
	 * @param bidAccount
	 * @param postMap
	 * @return
	 * @throws AccountNotExistException
	 */
	public JSONArray postOrderForm(String bidAccount, Map postMap)
			throws AccountNotExistException {
		NetAgent nAgent = new NetAgent();
		autoLogin(bidAccount);
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), bidAccount);
		nAgent.getState().addCookies(cookies);
		JSONArray jArray = new JSONArray();
		String url = this.ORDER_FORM_URL_C + "?save=order_form&.crumb="
				+ postMap.get(".crumb");
		postMap.remove("save");
		postMap.remove("ITEM_ID");
		postMap.remove("APP_ID");
		postMap.remove(".crumb");
		postMap.remove("ACTION");
		postMap.remove("WEB_SITE_ID");
		postMap.remove("MODEL_NAME");
		postMap.remove("UID");

		nAgent.setPostDataMap(postMap);
		nAgent.postMaptoData();
		System.out.println("[DEBUG] postOrderForm::" + nAgent.getPostDataMap());
		nAgent.getDataWithPost(url, charSet);
		// /jp/config/orderform?save=order_form&.crumb=zyj5nCAuUUf
		jArray.add(nAgent.getResponseBody());
		// this.outputTofile(nAgent.getResponseBody());
		return jArray;
	}

	/**
	 * 預覽order form 填寫資料
	 * 
	 * @param postMap
	 * @return
	 * @throws AccountNotExistException
	 */
	public JSONArray getOrderFormPreview(String bidAccount, String itemId,
			Map postMap) throws AccountNotExistException {
		NetAgent nAgent = new NetAgent();
		autoLogin(bidAccount);
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), bidAccount);
		nAgent.getState().addCookies(cookies);
		JSONArray jArray = new JSONArray();
		nAgent.setPostDataMap(postMap);
		nAgent.postMaptoData();
		nAgent.getDataWithPost(this.ORDER_FORM_URL_B, charSet);
		String sb = nAgent.getResponseBody();
		String urlArgs = sb
				.split("onClick=\"javascript:jumpPage\\('orderform','/jp/config/orderform\\?")[1]
				.split("'")[0];
		// save=order_form&.crumb=zyj5nCAuUUf
		sb = sb
				.replaceAll(
						"<INPUT TYPE=\"button\" VALUE=\"修正\" onClick=\"javascript:previewBackFunc\\(\\)\">",
						"<INPUT TYPE=\"button\" VALUE=\"修正\" onClick=\"javascript:history.go(-1)\">\n"
								+ "<INPUT TYPE=\"HIDDEN\" NAME=\"APP_ID\" VALUE=\""
								+ this.getAppId()
								+ "\">\n"
								+ "<INPUT TYPE=\"HIDDEN\" NAME=\"ACTION\" VALUE=\"POST_ORDER_FORM\">\n"
								+ "<INPUT TYPE=\"HIDDEN\" NAME=\"MODEL_NAME\" VALUE=\"ItemOrderFormYJ\">\n"
								+ "<INPUT TYPE=\"HIDDEN\" NAME=\"UID\" VALUE=\""
								+ bidAccount
								+ "\">\n"
								+ "<INPUT TYPE=\"HIDDEN\" NAME=\"ITEM_ID\" VALUE=\""
								+ itemId + "\">\n"
								+ fixGetArgs2PostArgs(urlArgs));
		sb = sb
				.replaceAll(
						"document\\.forms\\[name\\]\\.submit\\(\\)",
						"document.forms[name].action = '/NetPassPort/ProxyProtal' ;\ndocument.forms[name].submit()");

		jArray.add(sb);
		// this.outputTofile(sb);
		return jArray;
	}

	/**
	 * 取得order form頁面
	 * 
	 * @param bidAccount
	 * @param itemId
	 * @return
	 * @throws AccountNotExistException
	 */
	public JSONArray getItemOrderForm(String bidAccount, String itemId,
			String sellerId) throws AccountNotExistException {
		JSONArray jArray = new JSONArray();
		NetAgent nAgent = new NetAgent();

		autoLogin(bidAccount);
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), bidAccount);
		nAgent.getState().addCookies(cookies);
		nAgent.getDataWithGet(this.ORDER_FORM_URL_A.replaceAll(
				"\\$YAHOO_BIDDER_ACCOUNT", bidAccount).replaceAll(
				"\\$YAHOO_ITEM_ID", itemId).replaceAll(
				"\\$YAHOO_SELLER_ACCOUNT", sellerId));

		String sb = nAgent.getResponseBody();

		/*
		 * sb = sb.replaceAll( "<TABLE CELLPADDING=\"4\" CELLSPACING=\"0\" BORDER=\"0\" WIDTH=\"100%\">\\s<TR><FORM METHOD=POST NAME=\"orderForm\">",
		 * "<FORM METHOD=POST NAME=\"orderForm\"><TABLE CELLPADDING=\"4\" CELLSPACING=\"0\" BORDER=\"0\" WIDTH=\"100%\"><TR>"); sb =
		 * sb.replaceAll("</FORM>\\s</TR>\\s</TABLE>","</TR></TABLE></FORM>"); //
		 */

		sb = sb.replaceAll("oForm.submit\\(\\);",
				"	oForm.action = \"/NetPassPort/ProxyProtal\";\n"
						+ "oForm.submit\\(\\);");

		sb = sb
				.replaceAll(
						"<INPUT TYPE=\"HIDDEN\" NAME=\"o_area\">",
						"<INPUT TYPE=\"HIDDEN\" NAME=\"o_area\">\n"
								+ "<INPUT TYPE=\"HIDDEN\" NAME=\"APP_ID\" VALUE=\""
								+ this.getAppId()
								+ "\">\n"
								+ "<INPUT TYPE=\"HIDDEN\" NAME=\"ACTION\" VALUE=\"GET_ORDER_FORM_PREVIEW\">\n"
								+ "<INPUT TYPE=\"HIDDEN\" NAME=\"MODEL_NAME\" VALUE=\"ItemOrderFormYJ\">\n"
								+ "<INPUT TYPE=\"HIDDEN\" NAME=\"UID\" VALUE=\""
								+ bidAccount
								+ "\">\n"
								+ "<INPUT TYPE=\"HIDDEN\" NAME=\"ITEM_ID\" VALUE=\""
								+ itemId + "\">\n");

		jArray.add(sb);
		// this.outputTofile(sb);
		return jArray;
	}

	/**
	 * 直接抓目前資料庫中所儲存的連絡資料
	 * 
	 * @param bidAccount
	 *            下標帳號
	 * @param itemId
	 *            商品ID
	 * @param transactionId
	 *            交易ID
	 * @return
	 */
	public JSONArray getItemContactMsgFromDB(String bidAccount, String itemId,
			String transactionId) {
		JSONArray jArray = new JSONArray();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		// 排序方式
		// 排序欄位
		String orderBy = "msg_date";
		String orderType = "DESC";
		jArray = conn.queryJSONArray("mogan-DB",
				"SELECT * FROM view_item_contact_record_v1 WHERE "
						// + "item_id='"
						// + itemId
						// + "' and bid_account='"
						// + bidAccount
						// + "' and transaction_id='"
						+ " transaction_id='" + transactionId + 
						"' ORDER BY "+ orderBy + //排序欄位
						" "+orderType);//排序方法
		return jArray;
	}

	/**
	 * TODO 未尚完成 讀取商品資料，不寫入資料庫 取得追蹤，瀏覽人數，推薦次數，違規次數 得標資料 結標時間
	 * 
	 * @param bidAccount
	 * @param itemId
	 * @return
	 * @throws AccountNotExistException
	 */
	public JSONArray getItemData(String bidAccount, String itemId)
			throws AccountNotExistException {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		JSONArray jArray = new JSONArray();
		NetAgent nAgent = new NetAgent();
		autoLogin(bidAccount);
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), bidAccount);
		nAgent.getState().addCookies(cookies);
		nAgent.getDataWithGet(ITEM_DATA_URL.replaceAll("\\$YAHOO_ITEM_ID",
				itemId));

		try {
			System.out.println("[DEBUG] COUNT A:"
					+ nAgent.filterItem(
							new HasChildFilter(new HTMLNodeFilter("アクセス総数")))
							.elementAt(0).getParent().toPlainTextString()
							.split("： ")[1]);
			// System.out.println("[DEBUG] COUNT A:"+nAgent.filterItem(new HasChildFilter(new
			// HTMLNodeFilter("アクセス総数"))).elementAt(0).getNextSibling().getNextSibling().toPlainTextString());
			// System.out.println("[DEBUG] COUNT A:"+nAgent.filterItem(new HasChildFilter(new
			// HTMLNodeFilter("アクセス総数"))).elementAt(0).getParent().toHtml());

			System.out.println("[DEBUG] COUNT B:"
					+ nAgent.filterItem(
							new HasChildFilter(new HTMLNodeFilter(
									"ウォッチリストに追加された数"))).toHtml());
			System.out.println("[DEBUG] COUNT C:"
					+ nAgent.filterItem(
							new HasChildFilter(new HTMLNodeFilter(
									"友だちにメールを送られた数"))).toHtml());
			System.out.println("[DEBUG] COUNT D:"
					+ nAgent.filterItem(
							new HasChildFilter(new HTMLNodeFilter(
									"違反商品の申告をされた数"))).toHtml());

			// System.out.println("[DEBUG] COUNT E:"+nAgent.filterItem(new
			// HTMLNodeFilter("a name=\"winnerlist\"")).elementAt(0).getNextSibling().getNextSibling());
			Node n = nAgent.filterItem(
					new HTMLNodeFilter("a name=\"winnerlist\"")).elementAt(0)
					.getNextSibling().getNextSibling();
			// System.out.println("[DEBUG] i:"+n.getChildren().elementAt(1).toHtml());
			// System.out.println("[DEBUG] i:"+n.getChildren().elementAt(1).getChildren().elementAt(5).toHtml());

			n = n.getChildren().elementAt(1);// 取得tbody

			NodeList nodes = n.getChildren();
			for (int i = 5; i < nodes.size(); i = i + 2) {
				Node winnerData = nodes.elementAt(i);
				System.out.println("[DEBUG] i:"
						+ winnerData.getChildren().elementAt(1).toHtml());// 得標者
				System.out.println("[DEBUG] i:"
						+ winnerData.getChildren().elementAt(5).toHtml());// 得標個數
				System.out.println("[DEBUG] i:"
						+ winnerData.getChildren().elementAt(7).toHtml());// 得標價
				System.out.println("[DEBUG]=============================");
				// System.out.println("[DEBUG] i:"+nodes.elementAt(i).getChildren());
			}

		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jArray;
	}

	/**
	 * @param bidAccount
	 * @param itemId
	 * @return
	 */
	public String getItemContactType(String bidAccount, String itemId) {
		String contactType = null;
		String orderId = "";
		String sql = "SELECT * FROM view_item_i";
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		Map conditionMap = new HashMap();
		conditionMap.put("account", bidAccount);
		conditionMap.put("item_id", itemId);
		ArrayList dataList = conn.queryWithMap("mogan-tw",
				"view_item_order_v1", conditionMap);
		orderId = (String) ((Map) dataList.get(0)).get("item_order_id");
		return getItemContactType(orderId);
		// conn.queryWithMap("mogan-tw","view_item_order_v1");
	}

	/**
	 * @param orderId
	 * @return
	 */
	public String getItemContactType(String orderId) {
		String contactType = null;
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		NetAgent nAgent = new NetAgent();
		Map orderMap = getOrderData(orderId);

		String bidAccount = (String) orderMap.get("account");
		String itemId = (String) orderMap.get("item_id");

		try {
			contactType = getItemContactType(bidAccount, itemId, orderId);
		} catch (AccountNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contactType;
	}

	/**
	 * 依訂單編號，取得訂單資料
	 * 
	 * @param orderId
	 * @return
	 */
	public Map getOrderData(String orderId) {
		Map orderMap = new HashMap();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");

		String sql = "SELECT * FROM view_item_order_v1 WHERE item_order_id='"
				+ orderId + "'";

		ArrayList orderList = conn.query("mogan-tw", sql);
		if (orderList.size() > 0) {
			orderMap = (Map) orderList.get(0);
		}
		return orderMap;

	}

	/**
	 * 取得商品聯絡方法
	 * 
	 * @param bidAccount
	 * @param itemId
	 * @param orderId
	 *            -系統id
	 * @return
	 * @throws AccountNotExistException
	 */
	public String getItemContactType(String bidAccount, String itemId,
			String orderId) throws AccountNotExistException {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");

		NetAgent nAgent = new NetAgent();
		
		autoLogin(bidAccount);
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), bidAccount);
		nAgent.getState().addCookies(cookies);
		System.out.println("[DEBUG] getItemContactType::"+ITEM_DATA_URL.replaceAll("\\$YAHOO_ITEM_ID",
				itemId));
		nAgent.getDataWithGet(ITEM_DATA_URL.replaceAll("\\$YAHOO_ITEM_ID",
				itemId));
		
		HTMLNodeFilter hrefNf = new HTMLNodeFilter("href");// href
		HasParentFilter parnetFilter = new HasParentFilter(new HTMLNodeFilter(
				"decBg01 decBg05"));// decBg01 decBg05

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

			// TODO 新版須修改，因資料表結構改變
			Map conditionMap = new HashMap();
			conditionMap.put("no", orderId);
			Map dataMap = new HashMap();
			dataMap.put("contact_type", contactType);
			dataMap.put("order_form_status", hasOrderForm);
			System.out.println("[DEBUG] getItemContactType::" + orderId + " "
					+ dataMap + " " + hasOrderForm);
			// conn.update("mogan-DB","member_message",conditionMap,dataMap);
			conn.update("mogan-tw", "web_won", conditionMap, dataMap);
		} catch (ParserException e) {
			SysLogger4j.error("NetAgent Yahoo JP",e);
		} catch (UnsupportedEncodingException e) {
			SysLogger4j.error("NetAgent Yahoo JP",e);
		} catch (SQLException e) {
			SysLogger4j.error("NetAgent Yahoo JP",e);
		}
		return contactType;
	}

	/**
	 * 取得商品資料，目前主要為取得商品聯絡方式
	 * 
	 * @param bidAccount
	 * @param itemId
	 * @return
	 * @throws AccountNotExistException
	 */
	public JSONArray getItemData(String bidAccount, String itemId, String wonId)
			throws AccountNotExistException {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		JSONArray jArray = new JSONArray();

		String sql = "SELECT id,user_name,item,item_id ,no, end_date,sell_name,tax,costed,locally,remittance,status,jyahooid,contact_type FROM web_won WHERE id="
				+ wonId + "";
		jArray = conn.queryJSONArray("mogan-tw", sql);

		return jArray;
	}

	/**
	 * 讀取下標頁面
	 * 
	 * @param bidAccount
	 * @param itemId
	 * @return
	 * @throws AccountNotExistException
	 */
	public String loadItemPage(String bidAccount, String itemId)
			throws AccountNotExistException {
		NetAgent nAgent = new NetAgent();
		String html = "";
		/* 設定帳號COOKIE */
		autoLogin(bidAccount);

		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), bidAccount);
		nAgent.getState().addCookies(cookies);

		nAgent.getDataWithGet(ITEM_DATA_URL.replaceAll("\\$YAHOO_ITEM_ID",
				itemId));
		html = nAgent.getResponseBody();
		return html;
	}

	/**
	 * 取得聯絡資料新版
	 * 
	 * @version 2.0
	 * @param itemOrderId
	 * @return
	 * @throws Exception
	 */
	public JSONArray getItemContactMsg(JSONArray itemOrderIds) throws Exception {
		JSONArray jArray = new JSONArray();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
		"DBConn");
		SysLogger4j.info(itemOrderIds.toString());
		ArrayList orderList = conn.query(CONN_ALIAS,
				"SELECT * FROM item_order WHERE item_order_id IN ("
						+ itemOrderIds.toString().replaceAll("\"", "'").replaceAll("\\[|\\]", "") + ") AND delete_flag = 1 ");
		
		return jArray;
	}
	
	/**
	 * 取得聯絡資料新版
	 * 
	 * @version 2.0
	 * @param itemOrderId
	 * @return
	 * @throws Exception
	 */
	public JSONArray getItemContactMsg(String itemOrderId) throws Exception {
		JSONArray jArray = new JSONArray();
		//getItemContactType(itemOrderId);
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
		
		ArrayList orderList = conn.query(CONN_ALIAS,
				"SELECT * FROM item_order WHERE item_order_id='"
						+ itemOrderId + "'");
		
		// 沒有訂單可能是因為該訂單為特殊填單
		Map<String, String> orderMap = (Map) orderList.get(0);

		NetAgent nAgent = new NetAgent();
		String bidAccount = orderMap.get("account");
		/** 因舊版資料舊庫yahoo 帳號沒有ID*/
		String bidId = orderMap.get("account");
		String itemId = orderMap.get("item_id");
		String itemServer = orderMap.get("url").split("http://")[1].split("\\.")[0];
		String sellerId = orderMap.get("seller_id");
		String memberAccount = orderMap.get("member_account");
		System.out.println("###[DEBUG]getItemContactMsgxx::"+bidAccount+" "+itemId+" "+itemOrderId);
		autoLogin(bidAccount);
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), bidAccount);
		nAgent.getState().addCookies(cookies);

		try {
			// 取出連絡記錄， 透過下標ID 商品ID 交易ID來辦識不同的資料
			JSONArray jDataArray = getItemContactMsgFromDB(bidAccount, itemId,
					itemOrderId);
			Map contactMap = new HashMap();

			for (int i = 0; i < jDataArray.size(); i++) {
				contactMap.put(jDataArray.getJSONObject(i).get("msg_id"),
						jDataArray.getJSONObject(i));
			}

			nAgent.getDataWithGet(ITEM_DISCUSSION_MSG_URL.replaceAll(
					"\\$YAHOO_ITEM_ID", itemId).replaceAll("\\$YAHOO_PAGE", itemServer));

			NodeList discussionNodes = nAgent.filterItem(new HTMLNodeFilter(
					"id=\"modBlbdForm\""));// 揭示版
			// 當商品未結標，揭示版不會出來需要避過 2010 02 10
			if (discussionNodes.size() > 0 //是否有揭示版
					&& discussionNodes.elementAt(0).getChildren().size() > 5//揭示版是否有內容
					) {
				
				// 子項必需超過5個才可判斷為有內容
				discussionNodes = discussionNodes.elementAt(0).getChildren();
				discussionNodes = discussionNodes.elementAt(5).getChildren();

				Map discussMap = new HashMap();
				discussMap.put("member_account", memberAccount);
				discussMap.put("bid_account", bidAccount);
				discussMap.put("transaction_id", itemOrderId);
				discussMap.put("item_id", itemId);
				discussMap.put("seller_id", sellerId);

				for (int i = 0; i < discussionNodes.size(); i++) {
					if (discussionNodes.elementAt(i).toPlainTextString()
							.matches("^(\\s投稿|\\s答え)(.|\\s)*")) {
						
						// 標題
						String msgId = discussionNodes.elementAt(i)
								.toPlainTextString().split("\\n")[1];
						String msgFrom = discussionNodes.elementAt(i)
								.toPlainTextString().split("\\n")[2]
								.split("(\\(|\\（)")[0];
						System.out.println("[DEBUG] 有揭示版."+msgId);
						for (int d = 3; d < discussionNodes.elementAt(i)
								.toPlainTextString().split("\\n").length; d++) {
							if (discussionNodes.elementAt(i)
									.toPlainTextString().split("\\n")[d]
									.indexOf("月") > 0) {
								String msgDate = discussionNodes.elementAt(i)
										.toPlainTextString().split("\\n")[d];
								msgDate = fixYJDate(msgDate);// 修正留言時間為資料庫可以接受的時間格式
								discussMap.put("msg_date", msgDate);
							}
						}
						discussMap.put("msg_id", msgId);
						discussMap.put("msg_title", msgId);
						discussMap.put("msg_category", "連絡掲示板");
						discussMap.put("msg_from", msgFrom);


					} else if (discussionNodes.elementAt(i).toPlainTextString()
							.matches("^\\s*")) {
						// 空白

					} else {
						// 內文
						discussMap.put("msg_contact", discussionNodes
								.elementAt(i).toHtml());

						

						discussMap.put("member_id", memberAccount);
						/** bid_account 將被修改為 bid_id*/
						discussMap.put("bid_id", bidAccount);
						discussMap.put("item_order_id", itemOrderId);
						discussMap.put("transaction_id", itemOrderId);
						discussMap.put("item_id", itemId);
						
						/**2010 02 10
						 * 修正資料更新方式
						 */
						Map conditionMap = new HashMap();
						conditionMap.put("transaction_id",itemOrderId);
						conditionMap.put("msg_id",discussMap.get("msg_id"));
						
						conn.newData("mogan-DB", "item_contact_record", conditionMap,discussMap);
					}
				}
			}

			nAgent.getDataWithGet(ITEM_CONTACT_MSG_URL.replaceAll(
					"\\$YAHOO_ITEM_ID", itemId));
			HTMLNodeFilter aNf = new HTMLNodeFilter("a");
			HTMLNodeFilter crumbNf = new HTMLNodeFilter(".crumb");// 20091026確定
			AndFilter andFilter = new AndFilter(aNf, crumbNf);

			NodeList nodes = nAgent.filterItem(andFilter);
			String linkURL = "";

			String msgId = ""; // 訊息SEQ
			String msgTitle = ""; // 訊息標題
			String msgFrom = ""; // 訊息由誰發出
			String msgContact = ""; // 訊息內容
			String msgDate = ""; // 訊息發出時間

			for (int i = 0; i < nodes.size(); i++) {

				linkURL = nodes.elementAt(i).getText().split("\"")[1]
						.split("\"")[0];
				msgId = linkURL.split("&no=")[1].split("&")[0].replaceAll(
						"&no=", "");

				if (!contactMap.containsKey(msgId)) {

					// 開啟留言畫面
					nAgent.getDataWithGet(contactDetailUrl + linkURL);//

					// 取得留言內容
					HTMLNodeFilter fromNf = new HTMLNodeFilter(msgFrom_KeyWord);
					HTMLNodeFilter cssNf = new HTMLNodeFilter(
							msgContactCSS_KeyWord);
					NodeList contactNodes = nAgent.filterItem(cssNf);
					msgContact = contactNodes.elementAt(0).toHtml();
					msgContact = msgContact
							.replaceAll("(<small>|</small>)", "");

					// 找到"投稿者："的位置，再找出投稿者與留言時間的資料
					NodeList tableFromNodes = nAgent.filterItem(fromNf)
							.elementAt(0).getParent().getParent().getParent()
							.getChildren();

					msgTitle = tableFromNodes.elementAt(1).getChildren()
							.elementAt(1).toPlainTextString();// 留言標題
					msgFrom = tableFromNodes.elementAt(3).getChildren()
							.elementAt(1).toPlainTextString();// 留言者
					msgDate = tableFromNodes.elementAt(3).getChildren()
							.elementAt(3).toPlainTextString();// 留言時間

					// 修正留言者的留言內容
					msgFrom = msgFrom.replaceAll(msgFrom_KeyWord, "");

					msgDate = fixYJDate(msgDate);// 修正留言時間為資料庫可以接受的時間格式

					Map dataMap = new HashMap();
					dataMap.put("seller_id", sellerId);
					/** member_account 將被修改為 member_id*/
					dataMap.put("member_account", memberAccount);
					dataMap.put("member_id", memberAccount);
					dataMap.put("bid_account", bidAccount);
					/** bid_account 將被修改為 bid_id*/
					dataMap.put("bid_id", bidAccount);
					dataMap.put("item_order_id", itemOrderId);
					dataMap.put("transaction_id", itemOrderId);
					dataMap.put("item_id", itemId);
					dataMap.put("msg_id", msgId);
					dataMap.put("msg_title", msgTitle);
					dataMap.put("msg_from", msgFrom);
					dataMap.put("msg_contact", msgContact);
					dataMap.put("msg_date", msgDate);
					dataMap.put("msg_category", "取引ナビ");
					
					/**2010 02 10
					 * 修正資料更新方式
					 */
					Map conditionMap = new HashMap();
					conditionMap.put("transaction_id",itemOrderId);
					conditionMap.put("msg_id",msgId);
					
					conn.newData("mogan-DB", "item_contact_record", conditionMap,dataMap);
//					conn.newData("mogan-DB", "item_contact_record", dataMap);
				}
			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			this.outputTofile(nAgent.getResponseBody(), "GmailMsg_"
					+ System.currentTimeMillis() + "_" + itemId);
			throw e;
		}
		return jArray;
	}

	/**
	 * 讀取連絡訊息,可分別由資料庫或網頁上讀取
	 * 
	 * @param bidAccount
	 *            下標帳號
	 * @param itemId
	 *            商品ID
	 * @param transactionId
	 *            交易ID
	 * @param sellerId
	 *            賣家ID
	 * @param memberAccount
	 *            會員帳號
	 * @param type
	 *            讀取方式，DB、WEB
	 * @return
	 * @throws Exception
	 */
	public JSONArray getItemContactMsg(String bidAccount, String itemId,
			String transactionId, String sellerId, String memberAccount,
			String dataSource) throws Exception {

		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");

		if (dataSource.equals(this.DATA_SOURCE_WEB)) {
			getItemContactMsg(transactionId);
		}
		
		// 取出連絡記錄， 透過下標ID 商品ID 交易ID來辦識不同的資料
		JSONArray jDataArray = getItemContactMsgFromDB(bidAccount, itemId,
				transactionId);

		return jDataArray;
	}

	/**
	 * @param webSiteName
	 *            - 固定傳入"Yahoo JP"
	 * @param uId
	 *            - 下標帳號
	 * @param pwd
	 *            - 密碼
	 * @param itemURL
	 *            - 商品網址
	 * @param price
	 *            - 下標價
	 * @param qty
	 *            - 下標數量
	 * @return 下標結果，以數字表示 <br /> 0 - 無法判斷<br /> 1 - 下標成功，非最高出價<br /> 2 - 下標成功，已得標<br/> 3 - 下標成功，最高出價<br /> 4 - 無法下標<br /> - 下標失敗，錯誤出價(未完成)<br /> -
	 *         下標失敗，已結標(未完成)<br /> - 下標失敗，無下標連結(未完成)<br /> - 下標失敗，評價不足(未完成)<br /> - 下標失敗，密碼錯誤(未完成)<br /> - 下標失敗，下標價過低(未完成)<br /> - 下標失敗，數量錯誤(未完成)<br
	 *         /> - 下標失敗，無法下標(未完成)<br />
	 * @throws Exception
	 */
	@Override
	public JSONArray bidItem(String uId, String pwd, String itemURL,
			String price, String qty) throws Exception {
		long l0 = System.currentTimeMillis();
		JSONArray jArray = new JSONArray();
		autoLogin(uId);
		long l1 = System.currentTimeMillis();
		String bidItemMsg = "0";
		NetAgent nAgent = new NetAgent();
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), uId);
		nAgent.getState().addCookies(cookies);
		long l11 = System.currentTimeMillis();
		// nAgent.getDataWithPost(itemURL);
		String itemId = itemURL.split("\\/")[itemURL.split("\\/").length - 1];
		// nAgent.getDataWithPost(itemURL);
		nAgent.getDataWithPost("http://127.0.0.1/NetPassPort/index.html");

		// System.out.println("[DEBUG] getHostUrl::"+nAgent.getDataWithPost("http://page.auctions.yahoo.co.jp/jp/show/countdown?aID="+itemId));
		// System.out.println("[DEBUG] getHostUrl::"+"http://page.auctions.yahoo.co.jp/jp/show/countdown?aID="+itemId);
		// System.out.println("[DEBUG] getHostUrl::"+nAgent.getHostUrl());
		// this.printHeaders(nAgent.getResponseHeader());
		// this.outputTofile(nAgent.getResponseBody());
		long l2 = System.currentTimeMillis();
		/*
		 * if (nAgent.getStatusCode() != 200 && nAgent.getStatusCode() != 302) { throw new Exception("HTTP Status Code Error(" +
		 * nAgent.getStatusCode() + ")"); }
		 */
		long l3 = System.currentTimeMillis();
		NodeList nodes;

		Map dataMap = new HashMap();
		try {

			// nodes = nAgent.filterBid0FormItem();
			long l4 = System.currentTimeMillis();
			// nAgent.setParserNodesToPostDataMap(nodes);

			Map tempMap = new HashMap();

			tempMap.put(nAgent.YAHOO_JP_BID, price);// 價格
			tempMap.put("ItemID", itemId);// ItemID
			// tempMap.put(this.YAHOO_JP_PWD, nPwd);// 密碼(MD5編碼後)
			tempMap.put(nAgent.YAHOO_JP_QUANTITY, qty);// 數量
			tempMap.put("bidType", "1000");// bidtype

			tempMap.put("cc", "jp");// cc
			tempMap.put("lastquantity", "1");// cc
			tempMap.put("login", uId);// cc
			tempMap.put("setPrice", price);// cc

			nAgent.putDataInPostDataMap(tempMap);
			System.out.println("[DEBUG] getPostDataMap::"
					+ nAgent.getPostDataMap());

			/** 取得回傳網址 */
			nodes = nAgent.filterItem(new HTMLNodeFilter("id=\"frmbb1\""));
			String bidPreviewUrl = "http://$PAGE.auctions.yahoo.co.jp/jp/show/bid_preview";
			System.out.println("[DEBUG] bidPreviewUrl::" + bidPreviewUrl);

			bidPreviewUrl = bidPreviewUrl.replaceAll("\\$PAGE", "page"
					+ itemURL.split("page")[1].split("\\.")[0]);
			System.out.println("[DEBUG] bidPreviewUrl::" + bidPreviewUrl);

			if (nodes.size() > 0) {
				// bidPreviewUrl=nAgent.getUrl(nodes.elementAt(0).toHtml());
			}
			System.out.println("[DEBUG] bidPreviewUrl::" + bidPreviewUrl);
			nAgent.postMaptoData();
			nAgent.getDataWithPost(bidPreviewUrl);

			long l5 = System.currentTimeMillis();
			nodes = nAgent.filterInputItem();
			nAgent.setParserNodesToPostDataMap(nodes);
			nAgent.postMaptoData();
			// nodes = nAgent.filterFormHttpHref();
			String bidUrl = "http://$BID.auctions.yahoo.co.jp/jp/config/placebid";
			// http://bid10.auctions.yahoo.co.jp/jp/config/placebid
			bidUrl = bidUrl.replaceAll("\\$BID", "bid"
					+ itemURL.split("page")[1].split("\\.")[0]);
			// if (nodes.size() > 0) {
			// bidUrl = nAgent.getUrl(nodes.elementAt(0).getText());
			// }
			System.out.println("[DEBUG] bidUrl::" + bidUrl);
			// outputTofile(nAgent.getResponseBody());
			nAgent.getDataWithPost(bidUrl);
			long l6 = System.currentTimeMillis();
			// outputTofile(nAgent.getResponseBody());
			bidItemMsg = this.checkBidResult(nAgent.getResponseBody());
			long l7 = System.currentTimeMillis();

			System.out.println("[DEBUG] BID ITEM(" + l0 + ")7-0:" + (l7 - l0));
			System.out.println("[DEBUG] BID ITEM(" + l0 + ")7-6:" + (l7 - l6));
			System.out.println("[DEBUG] BID ITEM(" + l0 + ")6-5:" + (l6 - l5));
			System.out.println("[DEBUG] BID ITEM(" + l0 + ")5-4:" + (l5 - l4));
			System.out.println("[DEBUG] BID ITEM(" + l0 + ")4-3:" + (l4 - l3));
			System.out.println("[DEBUG] BID ITEM(" + l0 + ")3-2:" + (l3 - l2));
			System.out.println("[DEBUG] BID ITEM(" + l0 + ")2-1:" + (l2 - l1));
			System.out
					.println("[DEBUG] BID ITEM(" + l0 + ")2-11:" + (l2 - l11));
			System.out
					.println("[DEBUG] BID ITEM(" + l0 + ")11-1:" + (l11 - l1));
			System.out.println("[DEBUG] BID ITEM(" + l0 + ")1-0:" + (l1 - l0));

		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.outputTofile(nAgent.getResponseBody());
			bidItemMsg = "0";
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// this.outputTofile(nAgent.getResponseBody());
			bidItemMsg = "4";
		}
		System.out.println("[DEBUG]bidItem\t" + itemURL + "::" + uId + "::"
				+ price);
		if (bidItemMsg.equals("0") || bidItemMsg.equals("4")) {
			System.out.println("======================");
			this.outputTofile(nAgent.getResponseBody(), l0 + "_" + itemId);
			System.out.println("======================");
		}

		jArray.add(bidItemMsg);
		return jArray;
	}

	/**
	 * @param webSiteName
	 *            - 固定傳入"Yahoo JP"
	 * @param uId
	 *            - 下標帳號
	 * @param pwd
	 *            - 密碼
	 * @param itemURL
	 *            - 商品網址
	 * @param qty
	 *            - 下標數量
	 * @return 下標結果，以數字表示 0 - 下標失敗<br /> 1 - 下標成功，目前最高標<br /> 2 - 下標成功，非最高標<br /> 3 - 下標成功，已得標<br /> 4 - 下標失敗，無法下標<br /> - 下標失敗，錯誤出價(未完成)<br /> -
	 *         下標失敗，已結標(未完成)<br /> - 下標失敗，無下標連結(未完成)<br /> - 下標失敗，評價不足(未完成)<br /> - 下標失敗，密碼錯誤(未完成)<br /> - 下標失敗，下標價過低(未完成)<br /> - 下標失敗，數量錯誤(未完成)<br
	 *         /> - 下標失敗，無法下標(未完成)<br />
	 * @throws AccountNotExistException
	 * @throws Exception
	 */
	@Override
	public JSONArray buyItem(String uId, String pwd, java.lang.String itemURL,
			String qty) throws AccountNotExistException {
		autoLogin(uId);
		NetAgent nAgent = new NetAgent();
		JSONArray jArray = new JSONArray();
		String buyItemMsg = "0";
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), uId);
		nAgent.getState().addCookies(cookies);
		nAgent.getDataWithPost(itemURL);

		NodeList nodes;
		try {
			nodes = nAgent.filterBid0FormItem();
			nAgent.setParserNodesToPostDataMap(nodes);
			nAgent.buyItem(pwd, qty);
			// outputTofile(nAgent.getResponseBody());
			nodes = nAgent.filterInputItem();
			nAgent.setParserNodesToPostDataMap(nodes);
			nAgent.postMaptoData();
			nodes = nAgent.filterFormHttpHref();
			System.out.println("[DEBUG]--3");
			String bidUrl = "";
			if (nodes.size() > 0) {
				bidUrl = nAgent.getUrl(nodes.elementAt(0).getText());
			}
			nAgent.getDataWithPost(bidUrl);
			// outputTofile(nAgent.getResponseBody());
			buyItemMsg = this.checkBidResult(nAgent.getResponseBody());
			System.out.println("[DEBUG]--4");
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			buyItemMsg = "0";
		}
		jArray.add(buyItemMsg);
		return jArray;
	}

	@Override
	public JSONArray checkBidHistory(String webSiteURL, String id,
			String itemURL) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * <p>
	 * 開啟商品頁面判斷狀態
	 * </p>
	 * 
	 * @param webSiteURL
	 * @param uId
	 * @param pwd
	 * @param itemURL
	 * @param bidPrice
	 *            下標價
	 * @return 回傳數字<br /> 0 - 未得標<br /> 1 - 最高出價者<br /> 2 - 已得標<br /> 3 - 出價被取消，未結標(未完成)<br /> 4 - 出價被取消，已結標(未完成)<br /> 5 - 出價被超過(未完成)<br /> 6 -
	 *         流標(未完成)<br />
	 * @throws Exception
	 */
	@Override
	public JSONArray isMyBid(String uId, String itemURL, String price)
			throws Exception {
		JSONArray jArray = new JSONArray();
		String bidItemMsg = "0";
		autoLogin(uId);
		NetAgent nAgent = new NetAgent();
		Cookie[] cookies = getLoginSessionCookie(this.getAppId(), uId);
		nAgent.getState().addCookies(cookies);
		nAgent.getDataWithPost(itemURL);
		// this.printHeaders(nAgent.getResponseHeader());
		bidItemMsg = nAgent.isMyBid(price);
		// outputTofile(nAgent.getResponseBody());
		jArray.add(bidItemMsg);
		return jArray;
	}

	/**
	 * <p>
	 * 090922@Dian
	 * </p>
	 * <p>
	 * 傳入網址、帳號和密碼進行登入動作，checkLoginSession為true時不重覆登入，目前適用Yahoo jp
	 * </p>
	 * 
	 * @param webSiteURL
	 * @param uId
	 * @param pwd
	 * @param checkLoginSession
	 *            是否重覆登入
	 * @return 回傳數字<br /> 0-未登入 <br /> 1-登入成功 <br /> 2-登入失敗 <br /> 3-帳號已登入
	 * @throws Exception
	 */
	@Override
	public JSONArray login(String uId, String pwd) throws Exception {
		JSONArray jArray = new JSONArray();
		NetAgent nAgent = new NetAgent();
		String loginMsg = "0";// 未登入
		NodeList nodes;
		try {
			nAgent.getDataWithGet(this.getLoginURL());// 開啟登入畫面
			nodes = nAgent.filterInputItem();// 取得input項目
			nAgent.setParserNodesToPostDataMap(nodes);// 將nodes設入要post項目
			Map tempMap = new HashMap();// 設定帳號及密碼
			tempMap.put(nAgent.YAHOO_JP_ACCOUNT, uId);
			tempMap.put(nAgent.YAHOO_JP_PWD, pwd);
			nAgent.putDataInPostDataMap(tempMap);// //將Map設入要post項目
			nAgent.postMaptoData();// 將postMap轉成postData

			nodes = nAgent.filterFormLoginHref();// 過濾登入項目
			// System.out.println("[INFO] YAHOO JP LOGIN :::::::::::");
			// System.out.println(nAgent.getResponseBody());
			setWebSiteURL(nodes.elementAt(0).getText());
			setWebSiteURL(nAgent.getUrl(getWebSiteURL()));
			nAgent.getDataWithPost(getWebSiteURL());
			if (nAgent.getResponseCookies().length == 0) {
				loginMsg = "2";// 登入失敗
			} else {
				this.setLoginCookieMap(this.getWebSiteName(), uId,pwd, nAgent
						.getResponseCookies());
				loginMsg = "1";// 登入成功
			}
		} catch (ParserException e) {
			e.printStackTrace();
			loginMsg = "2";// 登入失敗
		}
		System.out.println("[INFO] YAHOO JP LOGIN loginMsg::" + loginMsg
				+ " account:" + uId + ":" + pwd);
		jArray.add(loginMsg);
		return jArray;
	}

	@Override
	public boolean autoLogin(String uId, String pwd) {
		// TODO Auto-generated method stub
		if (this.getLoginSessionCookie(this.getAppId(), uId).length > 0) {
			System.out.println("[DEBUG] CHECK COOKIE:"
					+ this.getLoginSessionCookie(this.getAppId(), uId).length);
			
			NetAgent nAgent = new NetAgent();
			Cookie[] cookies = getLoginSessionCookie(this.getAppId(), uId);
			nAgent.getState().addCookies(cookies);
			nAgent.getDataWithGet("https://lh.login.yahoo.co.jp/");
			this.outputTofile(nAgent.getResponseBody());
			try {
				if (nAgent.filterItem(new HTMLNodeFilter("履歴 - Yahoo! JAPAN")).size()>0){
					return true;
				}else{
					return false;
				}
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		} else {
			try {
				login(uId, pwd);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
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
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		JSONArray jArray = conn.queryJSONArray("mogan-DB",
				"SELECT * FROM system_bid_id WHERE DELETE_FLAG='1' and account='"
						+ uId + "'");
		if (jArray.size() > 0) {
			return autoLogin(jArray.getJSONObject(0).getString("account"),
					jArray.getJSONObject(0).getString("bid_password"));
		} else {
			throw new AccountNotExistException("此帳號不存在(" + uId + ")");
		}
	}

	/**
	 * 根據appId回報目前登入帳號
	 * 
	 * @param appId
	 * @return
	 */
	public JSONArray getLoginList() {
		JSONArray jArray = new JSONArray();
		Map tempMap = this.getLoginCookieMap(this.getAppId());
		JSONObject jObj = JSONObject.fromObject(tempMap);
		if (jObj.size() > 0) {
			for (Iterator it = jObj.keys(); it.hasNext();) {
				String uid = (String) it.next();
				JSONObject accObj = jObj.getJSONObject(uid);
				accObj.remove("COOKIE");
				jArray.add(accObj);
			}
		}
		return jArray;
	}

	/**
	 * 判斷下標結果，判斷是否DIV的ID，yaucBidRcd為非最高出價，yaucBidAct為最高出價，modWlBtnArea結標追縱代表未結標
	 * 
	 * @return 回傳數字<br /> 0 - 無法判斷<br /> 1 - 下標成功，非最高出價<br /> 2 - 下標成功，已得標<br />3 - 下標成功，最高出價<br /> 4 - 無法下標<br />
	 * @throws ParserException
	 */
	private String checkBidResult(String html) throws ParserException {
		try {

			String resultMsg = "0";
			NetAgent nAgent = new NetAgent();
			nAgent.setResponseBody(html);
			// parser.setInputHTML(html);
			HTMLNodeFilter divNf = new HTMLNodeFilter("div");

			boolean flag = true;
			for (int i = 1; i < 5; i++) {
				if (!flag) {
					break;
				}
				nAgent.resetParser();
				// parser.reset();
				switch (i) {
				case 4:
					HTMLNodeFilter cancelNf = new HTMLNodeFilter(
							"この商品への入札は取り消されました。\n今後この商品には入札できません。");
					nAgent.resetParser();

					if (nAgent.filterItem(cancelNf).size() > 0) {
						resultMsg = "4";// 出價被取消無法再出價
						flag = false;
						break;
					}
				case 2:
					HTMLNodeFilter bidActNf = new HTMLNodeFilter(
							"id=\"yaucBidAct\"");// 最高出價
					AndFilter divActFilter = new AndFilter(divNf, bidActNf);
					if (nAgent.filterItem(divActFilter).size() > 0) {
						resultMsg = "2";// 最高出價，已結標
						flag = false;
					}
					nAgent.resetParser();
					HTMLNodeFilter modWlBtnAreaNf = new HTMLNodeFilter(
							"id=\"modWlBtnArea\"");// 結標追蹤
					AndFilter divModFilter = new AndFilter(divNf,
							modWlBtnAreaNf);
					if (nAgent.filterItem(divModFilter).size() > 0) {
						resultMsg = "3";// 最高出價未結標
						flag = false;
						break;
					}
				case 1:
					HTMLNodeFilter bidRcdNf = new HTMLNodeFilter(
							"id=\"yaucBidRcd\"");// 非最高出價
					AndFilter divRcdFilter = new AndFilter(divNf, bidRcdNf);
					if (nAgent.filterItem(divRcdFilter).size() > 0) {
						resultMsg = "1";// 最高出價
						flag = false;
						break;
					}
				}
			}
			return resultMsg;
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
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
		rightNow.set(Calendar.MONTH,
				Integer.parseInt(msgDate.split("月")[0]) - 1);
		rightNow.set(Calendar.DATE, Integer.parseInt(msgDate.split("月")[1]
				.split("日")[0].trim()));
		rightNow.set(Calendar.HOUR_OF_DAY, Integer
				.parseInt(msgDate.split("日")[1].split("時")[0].trim()));
		rightNow.set(Calendar.MINUTE, Integer.parseInt(msgDate.split("時")[1]
				.split("分")[0].trim()));
		SysCalendar calendar = new SysCalendar();
		msgDate = calendar.getFormatDate(rightNow.getTime(),
				SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql);
		calendar = null;
		return msgDate;
	}

	@Override
	public String getBidURL() {
		// TODO Auto-generated method stub
		return bidURL;
	}

	@Override
	public String getLoginURL() {
		// TODO Auto-generated method stub
		return loginURL;
	}

	public void setMailSenderName(String mailSenderName) {
		this.mailSenderName = mailSenderName;
	}

	public String getMailSenderName() {
		return mailSenderName;
	}

	public void setMailSenderAddress(String mailSenderAddress) {
		this.mailSenderAddress = mailSenderAddress;
	}

	public String getMailSenderAddress() {
		return mailSenderAddress;
	}

	public void setMailCC(String mailCC) {
		this.mailCC = mailCC;
	}

	public String getMailCC() {
		return mailCC;
	}

}
