package com.mogan.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.NoSuchProviderException;

import com.mogan.exception.netAgent.AccountNotExistException;
import com.mogan.model.SMSModel;
import com.mogan.model.netAgent.NetAgent;
import com.mogan.model.netAgent.NetAgentGoogle;
import com.mogan.model.netAgent.NetAgentYJ;
import com.mogan.sys.DBConn;
import com.mogan.sys.model.ScheduleModelAdapter;

public class GmailTask extends ScheduleModelAdapter {

	final static private String GMAIL_ACCOUNT = "GMAIL_ACCOUNT";
	final static private String GMAIL_PWD = "GMAIL_PWD";

	/**** [買家通知] ****/
	/** 日雅 - 買家通知 - 得標通知 */
	final static private String YAHOO_JP_WON_BID_MAIL = "YAHOO_JP_WON_BID_MAIL";
	/** 日雅 - 買家通知 - 超標通知 */
	final static private String YAHOO_JP_NEW_PRICE_MAIL = "YAHOO_JP_NEW_PRICE_MAIL";
	/** 日雅 - 買家通知 - 下標取消通知 */
	final static private String YAHOO_JP_CANCEL_BID_MAIL = "YAHOO_JP_CANCEL_BID_MAIL";
	/** 日雅 - 買家通知 - 賣家回答 */
	final static private String YAHOO_JP_SELLER_ANS_MAIL = "YAHOO_JP_SELLER_ANS_MAIL";
	/** 日雅 - 買家通知 - 賣家聯絡 */
	final static private String YAHOO_JP_SELLER_CONTACT_MAIL = "YAHOO_JP_SELLER_CONTACT_MAIL";
	/** 日雅 - 買家通知 - 最高標通知 */
	final static private String YAHOO_JP_HIGHEST_BIDDER_MAIL = "YAHOO_JP_HIGHEST_BIDDER_MAIL";
	/** 日雅 - 買家通知 - 揭示版 */
	final static private String YAHOO_JP_BUYER_DISCUSS_MAIL = "YAHOO_JP_BUYER_DISCUSS_MAIL";

	/**** [賣家通知] ****/
	/** 日雅 - 賣家通知 - 上架通知 */
	final static private String YAHOO_JP_POST_ITEM_MAIL = "YAHOO_JP_POST_ITEM_MAIL";
	/** 日雅 - 賣家通知 - 賣出通知 */
	final static private String YAHOO_JP_SOLD_ITEM_MAIL = "YAHOO_JP_SOLD_MAIL";
	/** 日雅 - 賣家通知 - 自動上架通知 */
	final static private String YAHOO_JP_AUTO_POST_ITEM_MAIL = "YAHOO_JP_AUTO_POST_ITEM_MAIL";
	/** 日雅 - 賣家通知 - 買家發問 */
	final static private String YAHOO_JP_BUYER_ASK_MAIL = "YAHOO_JP_BUYER_ASK_MAIL";
	/** 日雅 - 賣家通知 - 買家聯絡 */
	final static private String YAHOO_JP_BUYER_CONTACT_MAIL = "YAHOO_JP_BUYER_CONTACT_MAIL";
	/** 日雅 - 賣家通知 - 揭示版 */
	final static private String YAHOO_JP_SELLER_DISCUSS_MAIL = "YAHOO_JP_SELLER_DISCUSS_MAIL";

	/**** [網站代號] ****/
	/** 日本雅虎代號 */
	static final String YAHOO_JP_WEBSITE_ID = "YAHOO_JP_WEBSITE_ID";
	/** PHP_APP_ID */
	static final String PHP_APP_ID = "PHP_APP_ID";
	/** 呼叫PHP操作URL */
	static final String PHP_COMMON_ALERT_URL = "PHP_COMMON_ALERT_URL";
	/** 商品賣出呼叫URL */
	static final String PHP_SOLD_ALERT_URL = "PHP_SOLD_ALERT_URL";

	static NetAgentGoogle nAgentG;

	public void exeSchedule() {
		try {

			if (nAgentG == null) {
				nAgentG = new NetAgentGoogle(this.getProperty(GMAIL_ACCOUNT),
						this.getProperty(GMAIL_PWD));
			}

			/** 日雅-得標信 */
			ArrayList msgList = nAgentG.getMail(this
					.getProperty(YAHOO_JP_WON_BID_MAIL));
			logAlert(msgList, YAHOO_JP_WON_BID_MAIL, this.getProperty(YAHOO_JP_WEBSITE_ID));
			updateItemContactType(msgList,this.getProperty(YAHOO_JP_WEBSITE_ID));
			msgList = new ArrayList();

			/** 被超標信 */
			msgList = nAgentG
					.getMail(this.getProperty(YAHOO_JP_NEW_PRICE_MAIL));
			logAlert(msgList, YAHOO_JP_NEW_PRICE_MAIL, this
					.getProperty(YAHOO_JP_WEBSITE_ID));
			msgList = new ArrayList();

			/** 被取消出價信 */
			msgList = nAgentG.getMail(this
					.getProperty(YAHOO_JP_CANCEL_BID_MAIL));
			logAlert(msgList, YAHOO_JP_CANCEL_BID_MAIL, this
					.getProperty(YAHOO_JP_WEBSITE_ID));
			msgList = new ArrayList();

			/** 聯絡資料更新 */
			msgList.addAll(nAgentG.getMail(this
					.getProperty(YAHOO_JP_SELLER_ANS_MAIL)));
			msgList.addAll(nAgentG.getMail(this
					.getProperty(YAHOO_JP_SELLER_CONTACT_MAIL)));
			msgList.addAll(nAgentG.getMail(this
					.getProperty(YAHOO_JP_SELLER_DISCUSS_MAIL)));
			msgList.addAll(nAgentG.getMail(this
					.getProperty(YAHOO_JP_BUYER_ASK_MAIL)));
			msgList.addAll(nAgentG.getMail(this
					.getProperty(YAHOO_JP_BUYER_CONTACT_MAIL)));
			msgList.addAll(nAgentG.getMail(this
					.getProperty(YAHOO_JP_BUYER_DISCUSS_MAIL)));
			updateItemContactMsg(msgList, this.getProperty(YAHOO_JP_WEBSITE_ID));
			msgList = new ArrayList();

			/** 未處理 */
			nAgentG.getMail(this.getProperty(YAHOO_JP_AUTO_POST_ITEM_MAIL));

			nAgentG.getMail(this.getProperty(YAHOO_JP_HIGHEST_BIDDER_MAIL));
			nAgentG.getMail(this.getProperty(YAHOO_JP_POST_ITEM_MAIL));
			nAgentG.getMail(this.getProperty(YAHOO_JP_SOLD_ITEM_MAIL));

			nAgentG.close();

			/*
			 * // 更新得標通知 //nAgentG.updateBid2Won(nAgentG.getWonBidMail()); // 更新競標聯絡資訊
			 * nAgentG.updateItemContactMsg(nAgentG.getSellerContactMail());//留言版 nAgentG.updateItemContactMsg(nAgentG.getBuyerDiscussMail());//揭示版
			 * nAgentG.getHighestPriceMail(); nAgentG.callPhpServer(nAgentG.getNewPriceMail()); nAgentG.getSellerAnsMail();
			 * nAgentG.callPhpServer(nAgentG.getCancelMail()); nAgentG.getPostItemMail(); nAgentG.getAutoPostItemMail(); nAgentG.getSoldItemMail();
			 * nAgentG.getBuyerAskMail(); nAgentG.getBuyerContactMail(); nAgentG.getSellerDiscussMail();
			 */
			
			updateGmailStatus("OK");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			updateGmailStatus("NOT_OK");
			e.printStackTrace();
			SMSModel sms=new SMSModel();
			Properties p=new Properties();
			p.setProperty("LOG_SERVER_URL", "http://sms.smse.com.tw");
			p.setProperty("SEND_SERVER_URL", "http://smsmo.smse.com.tw");
			p.setProperty("SEND_TEXT", "/STANDARD/sms_fu.asp");
			p.setProperty("QUERY_LOG", "/STANDARD/TVRVRE_FU_B.ASP");
			p.setProperty("ACCOUNT", "MORGAN");
			p.setProperty("PWD", "24266676");
			sms.setProperties(p);

			//sms.sendText("0910054930", "吳宗翰","ERR_GmailTask","GmailTask 當掉了!!!_"+e.getMessage());
			sms=null;
		}
	}

	/**
	 * 
	 * @param status
	 */
	public void updateGmailStatus(String status){
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
		"DBConn");
		Map conditionMap=new HashMap();
		Map dataMap=new HashMap();
		dataMap.put("config_value", status);
		conditionMap.put("config_id", "SC-1002-00001");
		conditionMap.put("config_key", "GMAIL_STATUS");
		conn.update("mogan-DB", "system_config", conditionMap, dataMap);
	}
	
	/**
	 * 更新商品聯絡方式
	 * 
	 * @param dataList
	 * @param websiteId
	 */
	public void updateItemContactType(ArrayList<Map> dataList, String websiteId) {
		if (dataList == null || dataList.size() == 0) {
			return;
		}

		NetAgentYJ netAgentYJ = new NetAgentYJ(this.getModelServletContext(),
				this.getAppId());
		
		for (int i = 0; i < dataList.size(); i++) {
			Map<String, String> tempMap = dataList.get(i);
			String itemId = (String) tempMap.get("ITEM_ID");
			ArrayList<Map> itemOrderIdList = getItemOrderIds(websiteId, tempMap
					.get("ACCOUNT"), itemId);
			
			for (int j = 0; j < itemOrderIdList.size(); j++) {
				String itemOrderId = (String) itemOrderIdList.get(j).get(
						"item_order_id");
				
				netAgentYJ.getItemContactType(itemOrderId);
			}
		}
	}

	/**
	 * 更新商品聯絡資料
	 * 
	 * @param dataList
	 * @param websiteId
	 * @throws Exception 
	 */
	public void updateItemContactMsg(ArrayList<Map> dataList, String websiteId) throws Exception {
		if (dataList == null || dataList.size() == 0) {
			return;
		}
		NetAgentYJ netAgentYJ = new NetAgentYJ(this.getModelServletContext(),
				this.getAppId());

		for (int i = 0; i < dataList.size(); i++) {
			Map<String, String> tempMap = dataList.get(i);
			String itemId = (String) tempMap.get("ITEM_ID");
			ArrayList<Map> itemOrderIdList = getItemOrderIds(websiteId, tempMap
					.get("ACCOUNT"), itemId);

			for (int j = 0; j < itemOrderIdList.size(); j++) {
				String itemOrderId = (String) itemOrderIdList.get(j).get(
						"item_order_id");
				try {
					netAgentYJ.getItemContactMsg(itemOrderId);
				} catch (AccountNotExistException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 將動作記錄到system_alert
	 * 
	 * @param dataList
	 * @param action
	 */
	public void logAlert(ArrayList dataList, String action, String websiteId) {
		if (dataList == null || dataList.size() == 0) {
			return;
		}

		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String autoNum = conn.getAutoNumber("mogan-DB", "SA-SEQ-01");
		Map dataMap = new HashMap();
		for (int i = 0; i < dataList.size(); i++) {
			Map<String, String> tempMap = (Map) dataList.get(i);
			ArrayList<Map> itemOrderIdList = getItemOrderIds(websiteId, tempMap
					.get("ACCOUNT"), tempMap.get("ITEM_ID"));
			if (itemOrderIdList.size()>0){
				for (int j = 0; j < itemOrderIdList.size(); j++) {
					String itemOrderId = (String) itemOrderIdList.get(j).get(
							"item_order_id");
					dataMap.put("item_order_id", itemOrderId);
					dataMap.put("alert", action);
					dataMap.put("create_date", new Date());
					dataMap.put("seq_no", autoNum);
					conn.newData("mogan-DB", "system_alert", dataMap);
				}
			}else{
				dataMap.put("alert", "YAHOO_UNDEFINED");
				dataMap.put("create_date", new Date());
				dataMap.put("info", "YAHOO ACCOUNT:"+tempMap.get("ACCOUNT")+", ITEM ID:"+ tempMap.get("ITEM_ID"));
				dataMap.put("seq_no", autoNum);
				conn.newData("mogan-DB", "system_alert", dataMap);
			}
		}
		
		NetAgent nAgent = new NetAgent();
		
		System.out.println("[INFO] logAlert URL::"+this.getProperty(PHP_COMMON_ALERT_URL)
				+ "?appId=" + this.getProperty(PHP_APP_ID) + "&action="
				+ action + "&seq_no=" + autoNum);
		
		nAgent.getDataWithGet(this.getProperty(PHP_COMMON_ALERT_URL)
				+ "?appId=" + this.getProperty(PHP_APP_ID) + "&action="
				+ action + "&seq_no=" + autoNum);

		 
	}

	/**
	 * 複數商品可能有多個得標者
	 * 
	 * @param webSiteId
	 * @param account
	 * @param itemId
	 * @return
	 */
	private ArrayList getItemOrderIds(String webSiteId, String account,
			String itemId) {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		ArrayList<Map> dataList = conn.query("mogan-tw",
				"SELECT item_order_id FROM view_bid_item_order WHERE website_id='"
						+ webSiteId + "' AND account='" + account
						+ "' AND item_id='" + itemId + "'");
		System.out.println("[DEBUG] getItemOrderIds::"+"SELECT item_order_id FROM view_bid_item_order WHERE website_id='"
				+ webSiteId + "' AND account='" + account
				+ "' AND item_id='" + itemId + "'");
		return dataList;
	}

}
