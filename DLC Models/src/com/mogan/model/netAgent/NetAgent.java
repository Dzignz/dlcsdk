package com.mogan.model.netAgent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * @author Dian 20090914 yahoo japen
 */
public class NetAgent extends HttpClient {
	private StringBuffer responseBody; // 回傳的html body資料
	private Header[] responseHeader;// 回傳 html head資料
	private int statusCode;
	private Cookie[] responseCookies;
	private Parser parser;
	private NameValuePair[] postData;
	private String hostUrl;
	
	/*multipart/form-data 上傳檔案專用參數*/
	private Part[] multipart;
	private Map postFileMap=new HashMap();
	
	private Map postDataMap;
	public static final String YAHOO_JP_ACCOUNT = "login";
	public static final String YAHOO_JP_PWD = "passwd";
	public static final String YAHOO_JP_BID = "Bid";
	public static final String YAHOO_JP_QUANTITY = "Quantity";

	public static final String YAHOO_JP_HISTORY_BID = "入札";
	public static final String YAHOO_JP_HISTORY_AUTO_BID = "自動入札";
	public static final String YAHOO_JP_HISTORY_CANEL_BID = "入札の取り消し";

	public NetAgent() {
		this.getState().clear();
		// this.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		parser = new Parser();
		responseBody = new StringBuffer();
		postDataMap = new HashMap();
	}

	/**
	 * 重設parser
	 */
	public void resetParser(){
		parser.reset();
	}
	
	public void showNodesText(NodeList nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			Node tempNode = nodes.elementAt(i);
			System.out.println("[INFO] showNodesText#" + i + " "
					+ tempNode.getText());
		}

	}

	/**
	 * 提供網址，使用get進行連線
	 * 
	 * @param urlString
	 * @return 連線結果
	 */
	public int getDataWithGet(String urlString) {
		GetMethod getMethod = new GetMethod(urlString);
		statusCode = 0;
		try {
			getMethod
					.setRequestHeader(
							"User-Agent",
							"Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-TW; rv:1.9.1.3) Gecko/20090824 Firefox/3.5.3 GTB6 (.NET CLR 3.5.30729)");
			
			statusCode = this.executeMethod(getMethod);
			
			
			this.setResponseBody(getMethod.getResponseBodyAsStream(), getMethod
					.getResponseCharSet());
			this.setResponseHeader(getMethod.getResponseHeaders());
			this.setResponseCookies(this.getState().getCookies());
			
			this.setHostUrl(getMethod.getURI().getHost());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// getMethod.releaseConnection();
			// 必須釋放連線資源
		}

		return statusCode;
	}

	/**
	 * 提供網址及參數，使用post連線
	 * 
	 * @param urlString
	 *            目標網址
	 * @param postData
	 *            傳入參數
	 * @return 回傳連線狀態
	 */
	private int getDataWithPost(EncodePostMethod postMethod) {
		if (this.postData != null) {// 避免postData是空值
			postMethod.setRequestBody(this.postData);
		}
		statusCode = 0;
		try {
			postMethod
					.setRequestHeader(
							"User-Agent",
							"Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-TW; rv:1.9.1.3) Gecko/20090824 Firefox/3.5.3 GTB6 (.NET CLR 3.5.30729)");
			
			statusCode = this.executeMethod(postMethod);
			this.setResponseBody(postMethod.getResponseBodyAsStream(),
					postMethod.getResponseCharSet());
			this.setResponseHeader(postMethod.getResponseHeaders());
			this.setResponseCookies(this.getState().getCookies());
			this.setHostUrl(postMethod.getURI().getHost());
			
			if (statusCode == 302) {
				String newUrl = getRedirectLocation();
				System.out.println("[DEBUG] 302 NEW URL::" + newUrl);
				statusCode = getDataWithPost(newUrl);
			}
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return statusCode;
	}


	/**
	 * 上傳檔案專用POST
	 * @param urlString
	 * @return
	 */
	private int getDateWithPostFile(EncodePostMethod postMethod) {
		//EncodePostMethod postMethod = new EncodePostMethod(urlString,"euc-jp");
		postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE,false);
		statusCode = 0;
		
		try {
			postMaptoDataForMultipart();

			postMethod.setRequestEntity(new MultipartRequestEntity(multipart,postMethod.getParams()));

			postMethod
					.setRequestHeader(
							"User-Agent",
							"Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-TW; rv:1.9.1.3) Gecko/20090824 Firefox/3.5.3 GTB6 (.NET CLR 3.5.30729)");
			statusCode = this.executeMethod(postMethod);
			this.setResponseBody(postMethod.getResponseBodyAsStream(),
					postMethod.getResponseCharSet());
			this.setResponseHeader(postMethod.getResponseHeaders());
			this.setResponseCookies(this.getState().getCookies());
			this.setHostUrl(this.getHostConfiguration().getHostURL());
			
			if (statusCode == 302) {
				String newUrl = getRedirectLocation();
				System.out.println("[DEBUG] 302 NEW URL::" + newUrl);
				statusCode = getDateWithPostFile(newUrl);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return statusCode;
	}
	
	/**
	 * 上傳檔案專用POST
	 * @param urlString 網址
	 * @return
	 */
	public int getDateWithPostFile(String urlString) {
		EncodePostMethod postMethod = new EncodePostMethod(urlString);
		return getDateWithPostFile(postMethod);
	}
	
	/**
	 * 上傳檔案專用POST
	 * @param urlString 網址
	 * @param charset 編碼
	 * @return
	 */
	public int getDateWithPostFile(String urlString, String charset) {
		EncodePostMethod postMethod = new EncodePostMethod(urlString, charset);
		return getDateWithPostFile(postMethod);
	}

	/**
	 * 呼叫前必須設定POST資料，呼叫postMaptoData()或setPostData()
	 * 
	 * @param urlString
	 *            網址
	 * @param charset
	 *            編碼
	 * @return
	 */
	public int getDataWithPost(String urlString, String charset) {
		EncodePostMethod postMethod = new EncodePostMethod(urlString, charset);
		return getDataWithPost(postMethod);
	}

	/**
	 * 呼叫前必須設定POST資料，呼叫postMaptoData()或setPostData()
	 * 
	 * @param urlString
	 *            網址
	 * @return
	 */
	public int getDataWithPost(String urlString) {
		EncodePostMethod postMethod = new EncodePostMethod(urlString);
		return getDataWithPost(postMethod);
	}

	/**
	 * 提供網址及參數，使用post連線
	 * 
	 * @param urlString
	 *            目標網址
	 * @param postData
	 *            傳入參數
	 * @return 回傳連線狀態
	 */
	public int getDataWithPostAt(String urlString, NameValuePair[] postData) {
		this.setPostData(postData);
		statusCode = getDataWithPost(urlString);
		return statusCode;
	}

	/**
	 * 20090918 yahoo.jp篩選直購專用
	 * 
	 * @return 直購價
	 */
	public String getBuyPrice() {
		String buyPrice = "";
		try {
			parser.setInputHTML(this.getResponseBody());
			HTMLNodeFilter nf = new HTMLNodeFilter("bidnum");
			NodeList nodes;
			nodes = parser.extractAllNodesThatMatch(nf);
			if (nodes.size() > 0) {
				buyPrice = nodes.elementAt(0).getText().split("(?i)(value)=\"")[1]
						.split("\"")[0];
				Pattern pattern = Pattern.compile("[^0-9]");
				Matcher matcher = pattern.matcher(buyPrice);
				// 替換第一個符合正則的數據
				buyPrice = matcher.replaceAll("");
			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buyPrice;
	}

	/**
	 * 直購商品，預設數量1
	 * 
	 * @param pwd
	 *            密碼
	 * @return
	 * @throws ParserException
	 *             URL 解析錯誤
	 */
	public int buyItem(String pwd) throws ParserException {
		return buyItem(pwd, "1");
	}

	/**
	 * 直購商品,自動找出直購價，執行下標程式下標
	 * 
	 * @param pwd
	 *            密碼
	 * @param qty
	 *            數量
	 * @return
	 * @throws ParserException
	 *             URL 解析錯誤
	 */
	public int buyItem(String pwd, String qty) throws ParserException {
		String price = getBuyPrice();
		return bidItem(pwd, price, qty);
	}

	/**
	 * 不指定數量下標，預設數量為1
	 * 
	 * @param pwd
	 *            密碼
	 * @param price
	 *            下標價
	 * @return int
	 * @throws ParserException
	 *             URL 解析錯誤
	 */
	public int bidItem(String pwd, String price) throws ParserException {
		return bidItem(pwd, price, "1");
	}

	/**
	 * 對指定商品下標，
	 * 
	 * @param pwd
	 *            密碼
	 * @param price
	 *            下標價
	 * @param qty
	 *            購買數量
	 * @return int
	 * @throws ParserException
	 *             url 解析錯誤
	 */
	public int bidItem(String pwd, String price, String qty)
			throws ParserException {
		String challenge = this.getChallengeYJ();
		String nPwd = this.getMD5Digest(pwd);
		nPwd = this.getMD5Digest(nPwd + challenge);
		String bidActURL;
		try {
			bidActURL = this.getBibActionURL();
			Map tempMap = new HashMap();
			tempMap.put(this.YAHOO_JP_BID, price);// 價格
//			tempMap.put(this.YAHOO_JP_PWD, nPwd);// 密碼(MD5編碼後)
			tempMap.put(this.YAHOO_JP_QUANTITY, qty);// 數量

			tempMap.put("md5", "1");
			this.putDataInPostDataMap(tempMap);
			this.postMaptoData();
			statusCode = 0;
			for (int i = 0; i < this.postData.length; i++) {
				NameValuePair tempNV = this.postData[i];
			}
			statusCode = this.getDataWithPost(bidActURL);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return statusCode;
		// TODO 執行下標動作

	}

	public NodeList filterItem(NodeFilter filter) throws ParserException {
		NodeList nodes;
		try {
			parser.setInputHTML(this.getResponseBody());
			nodes = parser.extractAllNodesThatMatch(filter);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return nodes;
	}

	public NodeList filterBid0FormItem() throws ParserException {
		// TODO 解析商品頁面的FROM
		try {
			parser.setInputHTML(this.getResponseBody());
			HTMLNodeFilter nf = new HTMLNodeFilter("input");
			HasParentFilter parnetFilter = new HasParentFilter(
					new HTMLNodeFilter("frmbb1"));
			AndFilter andFilter = new AndFilter(nf, parnetFilter);
			NodeList nodes;
			nodes = parser.extractAllNodesThatMatch(andFilter);
			return nodes;
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * ver 1.0 20090913 日本YAHOO 專用， 取得商品頁面中下標的網址
	 * 
	 * @return
	 * @throws ParserException
	 */
	public String getBibActionURL() throws ParserException {
		String actURL = "";
		try {
			parser.setInputHTML(this.getResponseBody());
			HTMLNodeFilter nf = new HTMLNodeFilter("frmbb1");
			NodeList nodes;
			nodes = parser.extractAllNodesThatMatch(nf);
			if (nodes.size() == 0) {
				throw new ParserException("HTML 解析錯誤 frmbb1不存在.");
			}
			if (nodes.size() > 0) {
				actURL = nodes.elementAt(0).getText().split("(?i)(action)=\"")[1]
						.split("\"")[0];
				actURL = this.getUrl(actURL);
			}
		} catch (ParserException e) {
			throw e;
		}

		return actURL;
	}

	/**
	 * ver 1.0 20090913 日本YAHOO 專用，取得商品頁面在frmbb1 from中的challenge資料
	 * 
	 * @return challengeStr
	 */
	public String getChallengeYJ() {
		String challengeStr = "";
		try {
			parser.setInputHTML(this.getResponseBody());
			HTMLNodeFilter nf = new HTMLNodeFilter(".challenge");
			HTMLNodeFilter nf2 = new HTMLNodeFilter("input");
			HasParentFilter parnetFilter = new HasParentFilter(
					new HTMLNodeFilter("frmbb1"));
			AndFilter andFilter = new AndFilter(nf2, new AndFilter(nf,
					parnetFilter));
			NodeList nodes;
			nodes = parser.extractAllNodesThatMatch(andFilter);
			if (nodes.size() > 0) {
				challengeStr = nodes.elementAt(0).getText().split("(?i)(value)=\"")[1]
						.split("\"")[0];
			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return challengeStr;
	}

	/**
	 * 取得目前網頁重新導向的Location
	 * 
	 * @return String
	 */
	public String getRedirectLocation() {
		for (int i = 0; i < this.responseHeader.length; i++) {
			Header tempH = this.responseHeader[i];
			if (tempH.getName().equals("Location")) {
				return tempH.getValue();
			}
		}
		return "";
	}

	/**
	 * 轉換postFileMap及postDataMap的內容，轉換為可以被multipart/form-data使用的參數
	 * 
	 */
	private void postMaptoDataForMultipart() {
		Map tempMap=new HashMap();
		tempMap.putAll(postDataMap);
		tempMap.putAll(postFileMap);
		
		multipart=new Part[tempMap.size()];
		Iterator it = tempMap.keySet().iterator();
		
		for (int i = 0; it.hasNext(); i++) {
			String key = (String) it.next();
			
			//判斷Map中的value是屬於那種型態的資料
			if (tempMap.get(key) instanceof java.io.File){
				System.out.println("[DEBUG]postMaptoDataForMultipart file."+key+"#"+((File)tempMap.get(key)).length());
				File f=(File) tempMap.get(key);
				try {
					multipart[i]=new FilePart(key,f.getName(),f);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println("[ERR] postMaptoDataForMultipart,檔案傳換錯誤.");
					e.printStackTrace();
				}
			}else if (tempMap.get(key) instanceof java.lang.String){
				multipart[i]=new StringPart(key,(String) tempMap.get(key));
				System.out.println("[DEBUG]postMaptoDataForMultipart string."+key+"#"+tempMap.get(key));
			}

			//multipart[i] = new NameValuePair(key, (String) tempMap.get(key));
		}
		tempMap=null;
	}

	
	/**
	 * 將postDataMap中的資料轉換為postMethod接受的NameValuePair資料
	 */
	public void postMaptoData() {
		postData = new NameValuePair[this.postDataMap.size()];
		Iterator it = postDataMap.keySet().iterator();
		for (int i = 0; it.hasNext(); i++) {
			String key = (String) it.next();
			// System.out.println("postMaptoData#" + key + ":" +
			// postDataMap.get(key));
			postData[i] = new NameValuePair(key, (String) postDataMap.get(key));
		}
	}

	/**
	 * 直接存入直接的Map資料
	 * 
	 * @param dataMap
	 */
	public void putDataInPostDataMap(Map dataMap) {
		this.postDataMap.putAll(dataMap);
	}

	/**
	 * 將傳入的NodeList轉成postDataMap(未轉入postData)，轉入時針對有name跟value的項目轉換， name轉換成key，value轉換成value
	 * 
	 * @param nodes
	 */
	public void setParserNodesToPostDataMap(NodeList nodes) {
		this.postDataMap.clear();
		for (int i = 0; i < nodes.size(); i++) {
			Node e = nodes.elementAt(i);
			String tagText = e.getText();
			if (!(tagText.split("(?i)(name)=").length > 1
					&& tagText.split("(?i)(value)=").length > 1)) {
//				System.out.println(tagText);
				//沒有name 跟value其中之一的就出局
				continue;
			}
			String key = tagText.split("(?i)(name)=")[1].split(" ")[0];
			String value = "";

			if (tagText.contains("(?i)(value)=\"\"")) {

			} else if (tagText.split("(?i)(value)=").length > 1) {
				if (tagText.split("(?i)(value)=\"")[1].split("\"").length==0){
					value="";
				}else{
					value = tagText.split("(?i)(value)=\"")[1].split("\"")[0];
				}
			}

			key = key.replaceAll("\"", "");
			value = value.replaceAll("\"", "");
			this.postDataMap.put(key, value);
		}
	}

	/**
	 * 取得Form中有"http"關鍵字的鏈結
	 * 
	 * @return
	 * @throws ParserException
	 */
	public NodeList filterFormHttpHref() throws ParserException {
		try {
			parser.setInputHTML(this.getResponseBody());
			HTMLNodeFilter nf = new HTMLNodeFilter("http");
			HTMLNodeFilter nf2 = new HTMLNodeFilter("form ");
			AndFilter andFilter = new AndFilter(nf, nf2);
			NodeList nodes = parser.extractAllNodesThatMatch(andFilter);
			return nodes;
		} catch (ParserException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 篩選有login關鍵字的from
	 * 
	 * @return
	 * @throws ParserException
	 */
	public NodeList filterFormLoginHref() throws ParserException {
		try {
			parser.setInputHTML(this.getResponseBody());
			HTMLNodeFilter nf = new HTMLNodeFilter("login");
			HTMLNodeFilter nf2 = new HTMLNodeFilter("form ");
			HTMLNodeFilter nf3 = new HTMLNodeFilter("http");
			AndFilter andFilter = new AndFilter(nf, nf2);
			AndFilter andFilter2 = new AndFilter(andFilter, nf3);
			NodeList nodes = parser.extractAllNodesThatMatch(andFilter2);
			return nodes;
		} catch (ParserException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public NodeList filterReferenceKeyword() throws ParserException {
		parser.setInputHTML(this.getResponseBody());
		HTMLNodeFilter nobrNf = new HTMLNodeFilter("a");
		HTMLNodeFilter searchNf = new HTMLNodeFilter("href=\"/search?");
		AndFilter andFilter = new AndFilter(searchNf, nobrNf);
		NodeList nodes = parser.extractAllNodesThatMatch(andFilter);
		return nodes;
	}

	/**
	 * 篩選擁有login關鍵字的節點
	 * 
	 * @return NodeList 擁有login關鍵字的節點
	 */
	public NodeList filterLoginHref() throws ParserException {
		try {
			parser.setInputHTML(this.getResponseBody());
			HTMLNodeFilter nf = new HTMLNodeFilter("login");
			NodeList nodes = parser.extractAllNodesThatMatch(nf);
			return nodes;
		} catch (ParserException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 將字串的的url取出，只回傳第一組結果
	 * 
	 * @param urlString
	 *            需要修正的url
	 *@return 回傳修正後的url
	 */
	public String getUrl(String urlString) {
		Pattern pattern = Pattern
				.compile("https?://([-\\w\\.]+)+(:\\d+)?(/([\\w/_\\.]*(\\?\\S+)?)?)?");
		Matcher matcher = pattern.matcher(urlString);
		Boolean b = matcher.find();
		if (b) {
			urlString = matcher.group(0);
		}
		urlString = urlString.replaceAll("\"", "");
		return urlString;
	}

	// 評価制限
	// このオークションは出品者により入札者評価制限が設定されています。あなたの評価は出品者の要求する評価を満たしていません。
	/**
	 * 
	 */
	public String checkAppraise() {
		String resultMsg = "0";
		try {
			parser.setInputHTML(this.getResponseBody());
			HTMLNodeFilter appraiseNf = new HTMLNodeFilter("評価制限");
			if (parser.extractAllNodesThatMatch(appraiseNf).size() > 0) {
				resultMsg = "1";
			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultMsg;
	}

	/**
	 * 090923<br /> 判斷網頁中是否有id=modTradeStep的div標籤，有代表已得標
	 * 
	 * @return 回傳數字<br /> 0 - 未得標<br /> 1 - 最高出價者<br /> 2 - 已得標<br /> 3 - 出價被取消，未結標(未完成)<br /> 4 - 出價被取消，已結標(未完成)<br /> 5 - 出價被超過，未結標(未完成)<br/> 6 -
	 *         出價被超過，已結標(流標)(未完成)<br />
	 */
	public String isMyBid(String bidPrice) {
		String resultMsg = "0";
		try {
			parser.setInputHTML(this.getResponseBody());
			HTMLNodeFilter divNf = new HTMLNodeFilter("div");
			boolean flag = false;
			for (int i = 1; i <= 3; i++) {
				if (flag) {
					break;
				}
				parser.reset();
				switch (i) {
				case 1:// 最高出價者
					HTMLNodeFilter msgNf = new HTMLNodeFilter(
							"id=\"modMsgBox\"");// 目前為最高出價者
					if (parser.extractAllNodesThatMatch(msgNf).size() > 0) {
						resultMsg = "1";
						flag = true;
						break;
					}
					continue;
				case 2:// 已得標
					HTMLNodeFilter tradeNf = new HTMLNodeFilter(
							"id=\"modTradeStep\"");// 已得標
					AndFilter divTradeFilter = new AndFilter(divNf, tradeNf);
					if (parser.extractAllNodesThatMatch(divTradeFilter).size() > 0) {
						resultMsg = "2";
						flag = true;
						break;
					}
					continue;
				case 3:
					resultMsg = "3";
					flag = true;
					break;
				}

			}
			System.out.println("[DEBUG] isMyBid resultMsg::" + resultMsg);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultMsg;
	}

	public String checkBidHistory(String uId, String actType) {
		String resultMsg = "0";
		try {
			HTMLNodeFilter uIdNf = new HTMLNodeFilter(" " + uId.toLowerCase()
					+ " ");// 帳號
			HTMLNodeFilter actionNf = new HTMLNodeFilter(" " + actType);// 帳號
			AndFilter andFilter = new AndFilter(uIdNf, actionNf);
			parser.setInputHTML(this.getResponseBody());
			NodeList nodes = parser.extractAllNodesThatMatch(andFilter);
			resultMsg = nodes.size() + "";
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultMsg;
	}



	/**
	 * 篩選所有input tag 內容
	 * 
	 * @return NodeList
	 */
	public NodeList filterInputItem() throws ParserException {
		try {
			parser.setInputHTML(this.getResponseBody());
			HTMLNodeFilter nf = new HTMLNodeFilter("input");
			NodeList nodes = parser.extractAllNodesThatMatch(nf);
			return nodes;
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 傳入密碼，challenge及submit的目標URL，透過MD5轉換取得對應變數
	 * 
	 * @param pwd
	 *            正常的pwd
	 * @param challenge
	 *            每次改變的challenge
	 * @param login_url
	 *            submit的目標URL
	 */
	public Map yahooJPPwdMD5(String pwd, String challenge, String login_url) {
		login_url += "?";
		return new HashMap();
	}

	/**
	 * 將傳入字串轉換為MD5
	 * 
	 * @param String
	 *            -傳入字串
	 * @return String-MD5
	 */
	private static String getMD5Digest(String str) {
		try {
			byte[] buffer = str.getBytes();
			byte[] result = null;
			StringBuffer buf = null;
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			// allocate room for the hash
			result = new byte[md5.getDigestLength()];
			// calculate hash
			md5.reset();
			md5.update(buffer);

			result = md5.digest();
			// create hex string from the 16-byte hash
			buf = new StringBuffer(result.length * 2);
			for (int i = 0; i < result.length; i++) {
				int intVal = result[i] & 0xff;
				if (intVal < 0x10) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(intVal));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Exception caught: " + e);
			e.printStackTrace();

		}
		return null;
	}

	public void setResponseBody(String responseBody) {
		if (this.responseBody.length() > 0) {
			this.responseBody.delete(0, this.responseBody.length());
		}
		this.responseBody.append(responseBody);
	}

	/**
	 * 將html內容存入
	 * @param responseBody
	 *            the responseBody to set
	 */
	public void setResponseBody(InputStream is, String charSet) {
		if (is == null) {
			return;
		}
		BufferedReader in;
		try {
			
			in = new BufferedReader(new InputStreamReader(is, charSet));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			in = new BufferedReader(new InputStreamReader(is));
		}
		String s = "";
		if (this.responseBody.length() > 0) {
			this.responseBody.delete(0, this.responseBody.length());
		}
		while (true) {
			try {
				s = in.readLine();
				if (s == null) {
					break;
				}
				this.responseBody.append(s + "\n");
			} catch (IOException e) {
				// 讀取內容發生錯誤
				s = null;
			}
		}
	}

	/**
	 * @return the responseBody
	 */
	public String getResponseBody() {
		return responseBody.toString();
	}

	private void parserHeaderCookie(Header[] responseHeader) {
		Map cookieMap;
		ArrayList cookieList = new ArrayList();
		String headerName;
		String headerValue;
		String cookieName;
		String cookieValue;
		// String cookiePersistent;
		String cookieExpiryDate;
		// String cookieComment;
		String[] cookieElement;
		for (int i = 0; i < responseHeader.length; i++) {
			headerName = responseHeader[i].getName();
			if (headerName.equals("Set-Cookie")) {
				headerValue = responseHeader[i].getValue();
				cookieElement = headerValue.split(";");
				cookieMap = new HashMap();
				for (int j = 0; j < cookieElement.length; j++) {
					if (cookieElement[j].startsWith(" expires")) {
						cookieExpiryDate = cookieElement[j].split("=")[1];
						cookieMap.put("COOKIE_EXPIRESDATE", cookieExpiryDate);
					} else if (cookieElement[j].startsWith(" domain")) {

					} else if (cookieElement[j].startsWith(" path")) {
					} else {
						String tempCookieStr = cookieElement[j].replaceFirst(
								"=", "_COOKIE_");
						cookieName = tempCookieStr.split("_COOKIE_")[0];
						cookieMap.put("COOKIE_NAME", cookieName);
						cookieValue = tempCookieStr.split("_COOKIE_")[1];
						cookieMap.put("COOKIE_VALUE", cookieValue);
					}
				}
				cookieList.add(cookieMap);
			}
		}
		Cookie[] cookies = new Cookie[cookieList.size()];
		for (int i = 0; i < cookieList.size(); i++) {
			cookieMap = (Map) cookieList.get(i);
			cookies[i] = new Cookie();
			cookies[i].setName((String) cookieMap.get("COOKIE_NAME"));
			cookies[i].setValue((String) cookieMap.get("COOKIE_VALUE"));
		}
		this.setResponseCookies(cookies);
	}
	
	/**
	 * 取得http字串
	 * @param str
	 * @return
	 */
	public String getHttp(String str){
		return "http"+(str.split("(?i)http")[1].split("(\"|')")[0]);
	}

	/**
	 * @param responseHeader
	 *            the responseHeader to set
	 */
	public void setResponseHeader(Header[] responseHeader) {
		this.responseHeader = responseHeader;
	}

	/**
	 * @return the responseHeader
	 */
	public Header[] getResponseHeader() {
		return responseHeader;
	}

	public void setPostData(NameValuePair[] postData) {
		this.postData = postData;
	}

	public NameValuePair[] getPostData() {
		return postData;
	}

	public void putAllPostDataMap(Map postDataMap) {
		this.postDataMap.putAll(postDataMap);
	}

	public void setPostDataMap(Map postDataMap) {
		this.postDataMap = postDataMap;
	}

	public Map getPostDataMap() {
		return postDataMap;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setResponseCookies(Cookie[] cookies) {
		this.responseCookies = cookies;
	}

	public Cookie[] getResponseCookies() {
		return responseCookies;
	}

	public void addPostFileItem(String key,File f) {
		postFileMap.put(key, f);
	}

	public Map getFileMap() {
		return postFileMap;
	}

	/**
	 * @param hostUrl the hostUrl to set
	 */
	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}

	/**
	 * @return the hostUrl
	 */
	public String getHostUrl() {
		return hostUrl;
	}

}
