package com.mogan.model.netAgent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URI;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import com.mogan.face.NetAgentModel;
import com.mogan.sys.model.ServiceModelFace;


public class NetAgentManager extends NetAgentModel implements ServiceModelFace {
	
	public NetAgentManager() {
		super();
	}

	public String logout(String webSiteURL, String uId) {
		return "logout response";
	}


	/**
	 * 取回全部已登入的帳號
	 * 
	 * @return
	 */
	public JSONArray getLoginListAll() {
		JSONArray jArray = new JSONArray();
		Map<String, Boolean> appidMap = (Map) this.getModelServletContext()
				.getAttribute("APP_ID");
		System.out.println("[DEBUG] getLoginListAll::" + appidMap.size());
		for (Map.Entry<String, Boolean> appId : appidMap.entrySet()) {
			System.out.println("[DEBUG] getLoginListAll::" + appId.getKey()
					+ ":" + appId.getValue());
			if (appId.getValue()) {
				jArray.addAll(getLoginList(appId.getKey()));
			}
		}
		return jArray;
	}

	/**
	 * 根據appId回報目前登入帳號
	 * 
	 * @param appId
	 * @return
	 */
	public JSONArray getLoginList(String appId) {
		JSONArray jArray = new JSONArray();
		Map tempMap = this.getLoginCookieMap(appId);
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
	 * @return 回傳數字<br /> 0 - 未得標<br /> 1 - 最高出價者<br /> 2 - 已得標<br /> 3 -
	 *         出價被取消，未結標(未完成)<br /> 4 - 出價被取消，已結標(未完成)<br /> 5 - 出價被超過(未完成)<br
	 *         /> 6 - 流標(未完成)<br />
	 * 
	 * @throws Exception
	 */
	public JSONArray isMyBid(String appId, String webSiteURL, String uId,
			 String itemURL, String price) throws Exception {
		JSONArray jArray = new JSONArray();
		String bidItemMsg = "0";
		NetAgent nAgent = new NetAgent();
		
		Cookie[] cookies = getLoginSessionCookie(appId, uId);
		nAgent.getState().addCookies(cookies);
		nAgent.getDataWithPost(itemURL);
//		this.printHeaders(nAgent.getResponseHeader());
		 outputTofile(nAgent.getResponseBody());
		bidItemMsg = nAgent.isMyBid(price);
		// outputTofile(nAgent.getResponseBody());
		jArray.add(bidItemMsg);
		return jArray;
	}

	/**
	 * 
	 * @param appId
	 *            APP ID
	 * @param webSiteURL
	 *            登入的網站
	 * @param uId
	 *            登入的ID
	 * @param itemURL
	 *            商品URL
	 * @return 回傳資料為AUTO_BID(自動出價)，BID(手動出價)，CANEL_BID(出價被取消)，三種狀態的數量
	 */
	public JSONArray checkBidHistory(String appId, String webSiteURL,
			String uId, String itemURL) {
		JSONArray jArray = new JSONArray();
		NetAgent nAgent = new NetAgent();
		Cookie[] cookies = getLoginSessionCookie(appId, uId);
		nAgent.getState().addCookies(cookies);
		String[] itemId = itemURL.split("/");
		itemURL = itemId[itemId.length - 1];
		itemURL = itemURL.split("\\?")[0];
		itemURL = "http://page17.auctions.yahoo.co.jp/jp/show/bid_hist?aID="
				+ itemURL + "&typ=log";
		nAgent.getDataWithGet(itemURL);
		JSONObject jObj = new JSONObject();
		jObj.put("AUTO_BID", nAgent.checkBidHistory(uId,
				nAgent.YAHOO_JP_HISTORY_AUTO_BID));
		jObj.put("BID", nAgent
				.checkBidHistory(uId, nAgent.YAHOO_JP_HISTORY_BID));
		jObj.put("CANEL_BID", nAgent.checkBidHistory(uId,
				nAgent.YAHOO_JP_HISTORY_CANEL_BID));
		jArray.add(jObj);
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
	public JSONArray login(String appId, String webSiteName, String webSiteURL,
			String uId, String pwd) throws Exception {
		JSONArray jArray = new JSONArray();
		NetAgent nAgent = new NetAgent();
		String loginMsg = "0";// 未登入
		NodeList nodes;
		try {
			nAgent.getDataWithGet(webSiteURL);// 開啟登入畫面
			nodes = nAgent.filterInputItem();// 取得input項目
			nAgent.setParserNodesToPostDataMap(nodes);// 將nodes設入要post項目
			Map tempMap = new HashMap();// 設定帳號及密碼
			tempMap.put(nAgent.YAHOO_JP_ACCOUNT, uId);
			tempMap.put(nAgent.YAHOO_JP_PWD, pwd);
			nAgent.putDataInPostDataMap(tempMap);// //將Map設入要post項目
			nAgent.postMaptoData();// 將postMap轉成postData
			nodes = nAgent.filterFormLoginHref();// 過濾登入項目
			webSiteURL = nodes.elementAt(0).getText();
			webSiteURL = nAgent.getUrl(webSiteURL);
			nAgent.getDataWithPost(webSiteURL);
			if (nAgent.getResponseCookies().length == 0) {
				loginMsg = "2";// 登入失敗
				outputTofile(nAgent.getResponseBody());
			} else {
				this.setLoginCookieMap( webSiteName, uId,pwd, nAgent
						.getResponseCookies());
				loginMsg = "1";// 登入成功
			}
		} catch (ParserException e) {
			e.printStackTrace();
			loginMsg = "2";// 登入失敗
		}
		jArray.add(loginMsg);
		return jArray;
	}

	/**
	 * 透過cookies長度，判斷是否有登入
	 * @param appId
	 * @param webSiteName
	 * @param uId
	 * @return
	 */
	public JSONArray isLogin(String appId,java.lang.String webSiteName, java.lang.String uId) {
		JSONArray jArray = new JSONArray();
		Cookie[] cookies = getLoginSessionCookie(appId, uId);
		if (cookies.length==0){
			jArray.add(false);
		}else{
			jArray.add(true);
		}
		return jArray;
	}

	/**
	 * 
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
	 * @return 下標結果，以數字表示 <br /> 0 - 無法判斷<br /> 1 - 下標成功，非最高出價<br /> 2 -
	 *         下標成功，已得標<br/> 3 - 下標成功，最高出價<br /> 4 - 無法下標<br /> -
	 *         下標失敗，錯誤出價(未完成)<br /> - 下標失敗，已結標(未完成)<br /> - 下標失敗，無下標連結(未完成)<br
	 *         /> - 下標失敗，評價不足(未完成)<br /> - 下標失敗，密碼錯誤(未完成)<br /> -
	 *         下標失敗，下標價過低(未完成)<br /> - 下標失敗，數量錯誤(未完成)<br /> - 下標失敗，無法下標(未完成)<br
	 *         />
	 * @throws Exception
	 */
	public JSONArray bidItem(String appId, String webSiteName, String uId,
			String pwd, String itemURL, String price, String qty)
			throws Exception {
		JSONArray jArray = new JSONArray();
		String bidItemMsg = "0";
		NetAgent nAgent = new NetAgent();
		Cookie[] cookies = getLoginSessionCookie(appId, uId);
		nAgent.getState().addCookies(cookies);
		nAgent.getDataWithPost(itemURL);
		// outputTofile(nAgent.getResponseBody());
		if (nAgent.getStatusCode() != 200 && nAgent.getStatusCode() != 302) {
			throw new Exception("HTTP Status Code Error("
					+ nAgent.getStatusCode() + ")");
		}
		NodeList nodes;
		try {
			nodes = nAgent.filterBid0FormItem();
			nAgent.setParserNodesToPostDataMap(nodes);
			//outputTofile(nAgent.getResponseBody());
			nAgent.bidItem(pwd, price, qty);
			// outputTofile(nAgent.getResponseBody());
			nodes = nAgent.filterInputItem();
			nAgent.setParserNodesToPostDataMap(nodes);
			nAgent.postMaptoData();
			nodes = nAgent.filterFormHttpHref();
			String bidUrl = "";
			if (nodes.size() > 0) {
				bidUrl = nAgent.getUrl(nodes.elementAt(0).getText());
			}
			//outputTofile(nAgent.getResponseBody());
			nAgent.getDataWithPost(bidUrl);
			// outputTofile(nAgent.getResponseBody());
			//bidItemMsg = nAgent.checkBidResult();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bidItemMsg = "0";
		}
		jArray.add(bidItemMsg);
		return jArray;
	}

	/**
	 * 
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
	 * @return 下標結果，以數字表示 0 - 下標失敗<br /> 1 - 下標成功，目前最高標<br /> 2 - 下標成功，非最高標<br
	 *         /> 3 - 下標成功，已得標<br /> 4 - 下標失敗，無法下標<br /> - 下標失敗，錯誤出價(未完成)<br />
	 *         - 下標失敗，已結標(未完成)<br /> - 下標失敗，無下標連結(未完成)<br /> - 下標失敗，評價不足(未完成)<br
	 *         /> - 下標失敗，密碼錯誤(未完成)<br /> - 下標失敗，下標價過低(未完成)<br /> -
	 *         下標失敗，數量錯誤(未完成)<br /> - 下標失敗，無法下標(未完成)<br />
	 * @throws Exception
	 */
	public JSONArray buyItem(String appId, java.lang.String webSiteName,
			java.lang.String uId, java.lang.String pwd,
			java.lang.String itemURL, String qty) {
		NetAgent nAgent = new NetAgent();
		JSONArray jArray = new JSONArray();
		String buyItemMsg = "0";
		Cookie[] cookies = getLoginSessionCookie(appId, uId);
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
			//buyItemMsg = nAgent.checkBidResult();
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
	/*
	 * 繼承ServiceModel
	 */
	public JSONArray doAction(Map parameterMap) throws Exception {
		JSONArray jArray = new JSONArray();
		try {
			if (this.getAct().equals("LOGIN")) {
				String webSiteURL = (String) parameterMap.get("WEB_SITE_URL");
				String uId = (String) parameterMap.get("UID");
				String pwd = (String) parameterMap.get("PWD");
				String webSiteName = (String) parameterMap.get("WEB_SITE_NAME");
				String appId = (String) parameterMap.get("APP_ID");
				jArray = this.login(appId, webSiteName, webSiteURL, uId, pwd);
			} else if (this.getAct().equals("BID_ITEM")) {
				String bitItemURL = (String) parameterMap.get("BID_ITEM_URL");
				String uId = (String) parameterMap.get("UID");
				String pwd =(String) parameterMap.get("PWD");
				String price = (String) parameterMap.get("PRICE");
				String qty = (String) parameterMap.get("QTY");
				String appId = (String) parameterMap.get("APP_ID");
				jArray = this.bidItem(appId, "", uId, pwd, bitItemURL, price,
						qty);
			} else if (this.getAct().equals("BUY_ITEM")) {
				String bitItemURL = (String) parameterMap.get("BID_ITEM_URL");
				String uId = (String) parameterMap.get("UID");
				String pwd = (String) parameterMap.get("PWD");
				String qty = (String) parameterMap.get("QTY");
				String appId = (String) parameterMap.get("APP_ID");
				jArray = this.buyItem(appId, "", uId, pwd, bitItemURL, qty);
			} else if (this.getAct().equals("IS_MY_BID")) {
				String bitItemURL = (String) parameterMap.get("BID_ITEM_URL");
				String uId = (String) parameterMap.get("UID");
				String appId = (String) parameterMap.get("APP_ID");
				String price = (String) parameterMap.get("PRICE");
				jArray = this.isMyBid(appId, "", uId, bitItemURL, price);
			} else if (this.getAct().equals("GET_LOGIN_LIST")) {
				String appId = (String) parameterMap.get("APP_ID");
				jArray = this.getLoginList(appId);
			} else if (this.getAct().equals("GET_LOGIN_LIST_ALL")) {
				jArray = this.getLoginListAll();
			} else if (this.getAct().equals("CHECK_BID_HISTORY")) {
				String bitItemURL = (String) parameterMap.get("BID_ITEM_URL");
				String uId = (String) parameterMap.get("UID");
				String pwd = (String) parameterMap.get("PWD");
				String appId = (String) parameterMap.get("APP_ID");
				String price = (String) parameterMap.get("PRICE");
				jArray = this.checkBidHistory(appId, "", uId, bitItemURL);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return jArray;
	}

}
