package com.mogan.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.exception.netAgent.AccountNotExistException;
import com.mogan.model.netAgent.NetAgentYJ;
import com.mogan.model.netAgent.NetAgentYJV2;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;

/**
 * 下標代理程式
 * <FONT SIZE=2>MODEL NAME=BidAgentV2</FONT>
 * @author Dian
 *
 */
public class BidAgentV2 extends ProtoModel implements ServiceModelFace {
	static final String YAHOO_JP_WEBSITE_ID = "SWD-2009-0001";
	private static Logger logger = Logger.getLogger(BidManagerV2.class.getName());
	
	@Override
	public JSONArray doAction(Map<String, String> parameterMap) throws Exception {
		JSONArray jArray=new JSONArray();
		if (this.getAct().equals("IS_MY_BID")){
			//我的競標狀況
			String webSiteId=parameterMap.get("WEBSITE_ID");
			String bidAccount=parameterMap.get("BID_ACCOUNT");
			String itemURL=parameterMap.get("ITEM_URL");
			jArray=this.isMyBid(webSiteId, bidAccount, itemURL);
		}else if (this.getAct().equals("BID_ITEM")){
			//下標
			String webSiteId=parameterMap.get("WEBSITE_ID");
			String bidAccount=parameterMap.get("BID_ACCOUNT");
			String pwd=parameterMap.get("PWD");
			String itemURL=parameterMap.get("ITEM_URL");
			String price=parameterMap.get("PRICE");
			String qty=parameterMap.get("QTY");
			jArray=bidItem( webSiteId,  bidAccount,  pwd, itemURL,  price,  qty);
		}else if (this.getAct().equals("GET_HIGH_PRICE_ACCOUNT")){
			//取得最高出價者帳號
			String webSiteId=parameterMap.get("WEBSITE_ID");
			String bidAccount=parameterMap.get("BID_ACCOUNT");
			String itemId=parameterMap.get("ITEM_ID");
			String page=parameterMap.get("PAGE");
			jArray=getHighPriceAccount( webSiteId,  bidAccount, itemId,  page);
		}else if (this.getAct().equals("SEND_WON_MSG")){
			String webSiteId=parameterMap.get("WEBSITE_ID");
			String bidAccount=parameterMap.get("BID_ACCOUNT");
			String itemId=parameterMap.get("ITEM_ID");
			String subject=parameterMap.get("SUBJECT");
			String msg=parameterMap.get("MSG");
			jArray=sendWonMsg(webSiteId, bidAccount,
					itemId,  subject,  msg);
		}
		return jArray;
	}
	
	/**
	 * 
	 * <font size=2>判斷競標狀況，ACTION = IS_MY_BID</font>
	 * @param webSiteId 		網站ID[WEBSITE_ID]
	 * @param bidAccount		下標帳號[BID_ACCOUNT]
	 * @param itemURL			商品URL[ITEM_URL]
	 * @return 回傳數字<br /> 0 - 未得標<br /> 1 - 最高出價者<br /> 2 - 已得標<br />
	 * @throws AccountNotExistException 
	 */
	private JSONArray isMyBid(String webSiteId, String bidAccount, String itemURL) throws AccountNotExistException{
		JSONArray jArray = new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJV2 agentYJ = new NetAgentYJV2(this.getModelServletContext(),
					this.getAppId());
			jArray = agentYJ.isMyBid(bidAccount, itemURL);
		}
		return jArray;	
	}

	/**
	 * <font size=2>判斷競標狀況，ACTION = BID_ITEM</font>
	 * 
	 * @param webSiteId		網站ID[WEBSITE_ID]
	 * @param bidAccount	下標帳號[BID_ACCOUNT]
	 * @param pwd			帳號密碼[PWD]
	 * @param itemURL		商品URL[ITEM_URL]
	 * @param price			出價金額[PRICE]
	 * @param qty			出價數量[QTY]
	 * @return 回傳數字<br /> 0 - 無法判斷<br /> 1 - 下標成功，非最高出價<br /> 2 - 下標成功，已得標<br />3 - 下標成功，最高出價<br /> 4 - 無法下標<br />
	 * @throws AccountNotExistException
	 */
	private JSONArray bidItem(String webSiteId, String uId, String pwd,
			String itemURL, String price, String qty) throws AccountNotExistException {
		JSONArray jArray=new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJV2 agentYJ = new NetAgentYJV2(this.getModelServletContext(),
					this.getAppId());
			jArray = agentYJ.bidItem(uId, pwd, itemURL, price, qty);
		}
		return jArray;	
	}
	
	/**
	 * 取得最高出價者的帳號
	 * <b>判斷競標狀況，ACTION = GET_HIGH_PRICE_ACCOUNT</b>
	 * @param webSiteId		網站ID[WEBSITE_ID]
	 * @param bidAccount	下標帳號[BID_ACCOUNT]
	 * @param itemId		商品id[ITEM_ID]
	 * @param page			頁數[PAGE]
	 * @return
	 * @throws AccountNotExistException 
	 */
	private JSONArray getHighPriceAccount(String webSiteId, String bidAccount,
			String itemId, String page) throws AccountNotExistException{
		JSONArray jArray=new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJV2 agentYJ = new NetAgentYJV2(this.getModelServletContext(),
					this.getAppId());
			jArray = agentYJ.getHighPriceAccount(bidAccount, itemId, page);
		}
		return jArray;	
	}
	
	/**
	 * 送出訊息，不指定方法，自動判斷
	 * <b>判斷競標狀況，ACTION = SEND_WON_MSG</b>
	 * @param webSiteId		網站ID[WEBSITE_ID]
	 * @param bidAccount	下標帳號[BID_ACCOUNT]
	 * @param itemId		商品id[ITEM_ID]
	 * @param subject		訊息標題[SUBJECT]
	 * @param msg			訊息內容[MSG]
	 * @return
	 * @throws AccountNotExistException
	 */
	private JSONArray sendWonMsg(String webSiteId, String bidAccount,
			String itemId, String subject, String msg)
			throws AccountNotExistException {
		JSONArray jArray = new JSONArray();
		JSONObject jObj = new JSONObject();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			int sendMethod = 2;
			String contactType = "";
			NetAgentYJV2 na = new NetAgentYJV2(this.getModelServletContext(), this
					.getAppId());
			// 取得連絡方法
			contactType = na.getItemContactType(bidAccount, itemId);

			if (contactType
					.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")) {
				itemId=contactType;
				sendMethod = 1;
			} else {
				if (contactType.equals(NetAgentYJV2.CONTACT_BOARD)) {
					sendMethod = 0;
					subject = "1";
				} else {
					sendMethod = 2;
					subject = "no";
				}
			}
			jObj.put("CONTACT_TYPE", sendMethod);
			jObj.put("CONTACT_RESULTS", na.sendMsg(bidAccount, itemId,sendMethod, subject, msg));
		}
		// 讀取商品頁面
		jArray.add(jObj);
		return jArray;
	}
	
	/**
	 * 
	 * @param bidAccount	下標帳號[BID_ACCOUNT]
	 * @param itemId		商品id[ITEM_ID]或Mail Address
	 * @param sendMethod	發送方法
	 * @param msgTitle		訊息標題
	 * @param msgContact	訊息內容
	 * @return
	 */
	private JSONArray sendMsg(String bidAccount, String itemId,
			String sendMethod, String msgTitle, String msgContact){
		JSONArray jArray = new JSONArray();
		
		return jArray;
	}
	
}
