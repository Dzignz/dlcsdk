package com.mogan.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.entity.ItemOrderEntity;
import com.mogan.entity.ItemTideEntity;
import com.mogan.exception.entity.EntityNotExistException;
import com.mogan.exception.netAgent.AccountNotExistException;
import com.mogan.model.netAgent.NetAgentYJ;
import com.mogan.model.netAgent.NetAgentYJV2;
import com.mogan.sys.DBConn;
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
	private static Logger logger = Logger.getLogger(BidAgentV2.class.getName());
	
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
		}else if (this.getAct().equals("ASK_QUESTION")){
			String webSiteId=parameterMap.get("WEBSITE_ID");
			String bidAccount=parameterMap.get("BID_ACCOUNT");
			String itemId=parameterMap.get("ITEM_ID");
			String question=parameterMap.get("QUESTION");
			jArray=askQusetion(webSiteId, bidAccount,itemId,  question);
		}else if (this.getAct().equals("UPDATE_TIDE_MSG")){
			String tideId=parameterMap.get("TIDE_ID");
			jArray=upTideMsg(tideId);
		}
		return jArray;
	}
	
	/**
	 * <font size=2>對商品發問，ACTION = UPDATE_TIDE_MSG</font>
	 * 更新訂單內所有商品的訊息及相關資料
	 * @param tideId 商品ID[TIDE_ID]
	 * @return 0 未執行<br /> 1 更新成功 <br />2 tideId 不存在<br />3 更新失敗
	 */
	private JSONArray upTideMsg(String tideId){
		JSONArray jArray=new JSONArray();
		int flag=0;
		ItemTideEntity itEty;
		try {
			itEty = new ItemTideEntity(this.getModelServletContext(), this.getSession(),tideId,ItemTideEntity.ITEM_TIDE_ID);
			itEty.updateIoMsg();
			flag=1;
		} catch (EntityNotExistException e) {
			flag=2;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			flag=3;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} catch (SQLException e) {
			flag=3;
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		
		jArray.add(flag);
		return jArray;
	}
	
	/**
	 * <font size=2>對商品發問，ACTION = ASK_QUESTION</font>
	 * @param webSiteId		網站ID[WEBSITE_ID]
	 * @param bidAccount	下標帳號[BID_ACCOUNT]
	 * @param itemId		商品ID[ITEM_ID]
	 * @param question		發問內容[QUESTION]
	 * @return 1-成功，0-失敗
	 * @throws Exception
	 */
	private JSONArray askQusetion(String webSiteId, String bidAccount,
			String itemId, String question) throws Exception {
		// TODO 商品發問
		JSONArray jArray = new JSONArray();
		if (webSiteId.equals(YAHOO_JP_WEBSITE_ID)) {
			NetAgentYJV2 agentYJ = new NetAgentYJV2(this.getModelServletContext(),
					this.getAppId());
			jArray = agentYJ.askQuestion(bidAccount, itemId, question);
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
				try {
					
					DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
					conn.executSql("mogan-DB", "UPDATE item_order SET flag_02='"+contactType+"' WHERE item_data_id in (SELECT item_data_id FROM item_data WHERE item_id='"+itemId+"')");
					
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage(),e);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				itemId=contactType;
				sendMethod = 1;	//email
			} else {
				if (contactType.equals(NetAgentYJV2.CONTACT_BOARD)) {
					sendMethod = 0;
					subject = "1";	// 留言版
				} else {
					sendMethod = 2;
					subject = "no";	//揭示版
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
