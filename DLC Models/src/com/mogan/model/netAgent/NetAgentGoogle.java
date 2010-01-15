package com.mogan.model.netAgent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;

import net.sf.json.JSONArray;

import org.apache.commons.httpclient.Cookie;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.mogan.exception.NetAgent.AccountNotExistException;
import com.mogan.face.NetAgentModel;
import com.mogan.model.BidManager;
import com.mogan.sys.DBConn;
import com.mogan.sys.ServiceModelFace;

/**
 * @version 0.1
 * @author Dian
 */
public class NetAgentGoogle extends NetAgentModel implements Runnable {
	// 信件分類

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

	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	private Session session;
	private URLName urln;
	private Store store;
	private boolean run_flag = true;

	public NetAgentGoogle() {
		super();
	}

	public NetAgentGoogle(String account, String pwd)
			throws NoSuchProviderException {
		super();
		loginGmail(account, pwd);
	}

	/**
	 * 登入Gmail
	 * 
	 * @param account
	 * @param pwd
	 * @throws NoSuchProviderException
	 */
	public void loginGmail(String account, String pwd)
			throws NoSuchProviderException {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		session = Session.getDefaultInstance(getPropertiesx(), null);
		session.setDebug(true);
		
		// 用pop3協議：new URLName("pop3", "pop.gmail.com", 995, null,"[郵箱帳號]", "[郵箱密碼]");
		// 用IMAP协议
		
		urln = new URLName("imap", "imap.googlemail.com", 995, null, account, pwd);
		try {
			store = session.getStore(urln);
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 信箱連結設定
	 * 
	 * @return
	 */
	private Properties getPropertiesx() {
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", "smtp.gmail.com");
		// Gmail提供的POP3和SMTP是使用安全套接字層SSL的
		props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");

		props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.imap.socketFactory.fallback", "false");
		props.setProperty("mail.imap.port", "993");
		props.setProperty("mail.imap.socketFactory.port", "993");

		props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.pop3.socketFactory.fallback", "false");
		props.setProperty("mail.pop3.port", "995");
		props.setProperty("mail.pop3.socketFactory.port", "995");

		props.put("mail.smtp.auth", "true");
		return props;
	}
	
	/**
	 * 關閉與gmail連線
	 */
	public void close() {
		try {
			store.close();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 開啟指定floder，不存在會自動新增
	 * 
	 * @param folderName
	 * @param openType
	 * @return
	 */
	public Folder getMailFolder(String folderName, int openType) {
		// TODO 直接取得各類狀態mail
		Folder inbox = null;
		try {
			// Store用來收信,Store類實現特定郵件協議上的讀、寫、監視、查找等操作。
			if (!store.isConnected()) {
				System.out.println("[DEBUG] getMailFolder:"+folderName+" "+store.isConnected());
				store.connect();
			}
			inbox = store.getFolder(folderName);// 收件箱
			if (!inbox.exists()) {
				inbox.create(inbox.HOLDS_MESSAGES);
			}
			inbox.open(openType);

		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			System.err.println("[ERR] 登入GMAIL 失敗 Account:"
					+ this.getProperties().getProperty("gmailAccount")
					+ " Pwd:" + this.getProperties().getProperty("gmailPwd"));
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inbox;
	}

	/**
	 * 讀取指定的收件箱類別，回傳的ArrayList中為Map型態，包含CONTENT及SUBJECT
	 * 
	 * @param mailType
	 * @return
	 */
	public ArrayList getMail(String mailType) {
		Folder inbox = null;
		ArrayList msgList = new ArrayList();
		inbox = getMailFolder(mailType, Folder.READ_WRITE);
		// Message[] messages = inbox.getMessages();
		Message[] messages;
		try {
			messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN),
					false));
			FetchProfile profile = new FetchProfile();
			profile.add(FetchProfile.Item.CONTENT_INFO);
			inbox.fetch(messages, profile);
			for (int i = 0; i < messages.length; i++) {
				Map mailMap = new HashMap();
				// messages[i].setFlag(Flags.Flag.SEEN, true);
				String mailSubject = messages[i].getSubject();
				String mailcontent = (String) messages[i].getContent();
				mailMap.put("SUBJECT", mailSubject);
				mailMap.put("CONTENT", mailcontent);
				//mailMap.put("WEBSITE_ID", this.getProperty(YAHOO_JP_WEBSITE_ID));
				mailMap.put("ITEM_ID", mailSubject.split("\\(|\\（")[mailSubject
						.split("\\(|\\（").length - 1].split("\\)")[0]);

				mailcontent = mailcontent.split("様")[0];
				mailMap
						.put("ACCOUNT", mailcontent.split("\r")[mailcontent
								.split("\r").length - 1].replaceAll(
								"(\r|\n|\r\n)", ""));
				msgList.add(mailMap);
				mailSubject = null;
				mailMap = null;
			}
			inbox.close(false);
			return msgList;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msgList;
	}

	/**
	 * 日雅 - 買家通知 - 下標取消通知
	 */
	public ArrayList getCancelMail() {
		ArrayList msgList = getMail(this.getProperty(YAHOO_JP_CANCEL_BID_MAIL));
		for (int i = 0; i < msgList.size(); i++) {
			Map tempMap = (Map) msgList.get(i);
			tempMap.put("SUBJECT", "[出價取消通知] " + tempMap.get("ACCOUNT") + " "
					+ tempMap.get("SUBJECT"));
		}

		if (msgList.size() > 0) {
			logAlert(msgList, YAHOO_JP_CANCEL_BID_MAIL);
		}

		return msgList;
	}

	/**
	 * 日雅 - 買家通知 - 超標通知
	 * 
	 * @return
	 */
	public ArrayList getNewPriceMail() {
		ArrayList msgList = getMail(this.getProperty(YAHOO_JP_NEW_PRICE_MAIL));
		for (int i = 0; i < msgList.size(); i++) {
			Map tempMap = (Map) msgList.get(i);
			tempMap.put("SUBJECT", "[超標通知] " + tempMap.get("SUBJECT"));
		}
		if (msgList.size() > 0) {
			logAlert(msgList, YAHOO_JP_NEW_PRICE_MAIL);
		}
		return msgList;
	}

	/**
	 * 日雅 - 買家通知 - 得標通知
	 * 
	 * @return
	 */
	public ArrayList getWonBidMail() {
		ArrayList msgList = getMail(this.getProperty(YAHOO_JP_WON_BID_MAIL));
		if (msgList.size() > 0) {
			logAlert(msgList, YAHOO_JP_WON_BID_MAIL);
		}
		return msgList;
	}

	/**
	 * 日雅 - 買家通知 - 賣家回答
	 * 
	 * @return
	 */
	public ArrayList getSellerAnsMail() {
		ArrayList msgList = getMail(this.getProperty(YAHOO_JP_SELLER_ANS_MAIL));
		for (int i = 0; i < msgList.size(); i++) {
			Map tempMap = new HashMap();
			tempMap.put("TEXT_NOTIFY", "1");
		}
		return msgList;
	}

	/**
	 * 日雅 - 買家通知 - 賣家聯絡
	 * 
	 * @return
	 */
	public ArrayList getSellerContactMail() {
		ArrayList msgList = getMail(this
				.getProperty(YAHOO_JP_SELLER_CONTACT_MAIL));
		for (int i = 0; i < msgList.size(); i++) {
			Map tempMap = new HashMap();
			tempMap.put("TEXT_NOTIFY", "1");
		}
		return msgList;
	}

	/**
	 * 日雅 - 買家通知 - 最高標通知
	 * 
	 * @return
	 */
	public ArrayList getHighestPriceMail() {

		ArrayList msgList = getMail(this
				.getProperty(YAHOO_JP_HIGHEST_BIDDER_MAIL));
		for (int i = 0; i < msgList.size(); i++) {
			Map tempMap = new HashMap();
			tempMap.put("TEXT_NOTIFY", "1");
		}

		return msgList;
	}

	/**
	 * 日雅 - 買家通知 - 揭示版
	 * 
	 * @return
	 */
	public ArrayList getBuyerDiscussMail() {

		ArrayList msgList = getMail(this
				.getProperty(YAHOO_JP_BUYER_DISCUSS_MAIL));

		return msgList;
	}

	// /////////////////////////////////
	// 賣家通知信
	// /////////////////////////////////

	/**
	 * 日雅 - 賣家通知 - 上架通知
	 * 
	 * @return
	 */
	public ArrayList getPostItemMail() {
		ArrayList msgList = getMail(this.getProperty(YAHOO_JP_POST_ITEM_MAIL));
		for (int i = 0; i < msgList.size(); i++) {
			Map tempMap = new HashMap();
			tempMap.put("TEXT_NOTIFY", "1");
		}
		return msgList;
	}

	/**
	 * 日雅 - 賣家通知 - 自動上架通知
	 * 
	 * @return
	 */
	public ArrayList getAutoPostItemMail() {
		ArrayList msgList = getMail(this
				.getProperty(YAHOO_JP_AUTO_POST_ITEM_MAIL));
		for (int i = 0; i < msgList.size(); i++) {
			Map tempMap = new HashMap();
			tempMap.put("TEXT_NOTIFY", "1");
		}
		return msgList;
	}

	/**
	 * 日雅 - 賣家通知 - 賣出通知
	 * 
	 * @return
	 */
	public ArrayList getSoldItemMail() {
		ArrayList msgList = getMail(this.getProperty(YAHOO_JP_SOLD_ITEM_MAIL));
		for (int i = 0; i < msgList.size(); i++) {
			Map tempMap = new HashMap();
			tempMap.put("TEXT_NOTIFY", "1");
		}
		return msgList;
	}

	/**
	 * 日雅 - 賣家通知 - 買家發問
	 * 
	 * @return
	 */
	public ArrayList getBuyerAskMail() {
		ArrayList msgList = getMail(this.getProperty(YAHOO_JP_BUYER_ASK_MAIL));
		for (int i = 0; i < msgList.size(); i++) {
			Map tempMap = new HashMap();
			tempMap.put("TEXT_NOTIFY", "1");
		}
		return msgList;
	}

	/**
	 * 日雅 - 賣家通知 - 買家聯絡
	 * 
	 * @return
	 */
	public ArrayList getBuyerContactMail() {
		ArrayList msgList = getMail(this
				.getProperty(YAHOO_JP_BUYER_CONTACT_MAIL));
		for (int i = 0; i < msgList.size(); i++) {
			Map tempMap = new HashMap();
			tempMap.put("TEXT_NOTIFY", "1");
		}
		return msgList;
	}

	/**
	 * 日雅 - 賣家通知 - 揭示版
	 * 
	 * @return
	 */
	public ArrayList getSellerDiscussMail() {
		ArrayList msgList = getMail(this
				.getProperty(YAHOO_JP_SELLER_DISCUSS_MAIL));
		for (int i = 0; i < msgList.size(); i++) {
			Map tempMap = new HashMap();
			tempMap.put("TEXT_NOTIFY", "1");
		}
		return msgList;
	}

	@Override
	public void run() {
		try {
			loginGmail(this.getProperties().getProperty("gmailAccount"), this
					.getProperties().getProperty("gmailPwd"));
		} catch (NoSuchProviderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			run_flag = false;
		}
		System.out.println("NetAgentGoogle run.");
		// login();
		// login("mogansweet@gmail.com", "MOGANS4725333");
		while (run_flag) {
			try {
				long l0 = System.currentTimeMillis();
				// [買家通知]
				// 最高標
				// 被超標
				// 被取消的出價
				// 得標通知
				// 賣家回答
				// 賣家聯絡

				// [賣家通知]
				// 上架成功
				// 商品已賣出
				// 系統自動重新上架
				// 買家發問
				// 買家聯絡

				/** 更新得標通知 */
				updateBid2Won(this.getWonBidMail());

				/** 更新競標聯絡資訊 */
				updateItemContactMsg(this.getSellerContactMail());//留言版
				updateItemContactMsg(this.getBuyerDiscussMail());//揭示版

				this.getHighestPriceMail();
				/***/
				callPhpServer(this.getNewPriceMail());

				this.getSellerAnsMail();
				
				
				callPhpServer(this.getCancelMail());
				

				this.getPostItemMail();
				this.getAutoPostItemMail();
				this.getSoldItemMail();
				this.getBuyerAskMail();
				this.getBuyerContactMail();
				this.getSellerDiscussMail();

				// callPhpServer(getNewPriceMail());
				/*
				 * ArrayList wonList = getWonBidMail(); recordMsg(getWonBidMail(), this .getProperty(YAHOO_JP_WON_BID_MAIL));
				 * recordWonBidToOldDB(wonList);
				 */
				
				// sendMsgToPhpServer(getSoldMail(),this.YAHOO_JP_SOLD_MAIL);
				// recordMsg(getSelledMail(),"日雅已賣出");// 檢查被取消的出價
				// 被超價
				long l1 = System.currentTimeMillis();
				System.out.println("[DEBUG] 檢查被取消的出價耗時" + (l1 - l0));

				Thread.sleep(1000 * 60 * 5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// */
		// TODO Auto-generated method stub
	}

	/**
	 * 將動作記錄到system_alert
	 * 
	 * @param dataList
	 * @param action
	 */
	public void logAlert(ArrayList dataList, String action) {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String autoNum = conn.getAutoNumber("mogan-DB", "SA-SEQ-01");
		Map dataMap = new HashMap();
		for (int i = 0; i < dataList.size(); i++) {
			Map<String, String> tempMap = (Map) dataList.get(i);
			ArrayList<Map> itemOrderIdList = getItemOrderIds(tempMap
					.get("WEBSITE_ID"), tempMap.get("ACCOUNT"), tempMap
					.get("ITEM_ID"));
			System.out.println("[DEBUG] logAlert:" + tempMap.get("WEBSITE_ID")
					+ ":" + tempMap.get("ACCOUNT") + ":"
					+ tempMap.get("ITEM_ID"));
			for (int j = 0; j < itemOrderIdList.size(); j++) {
				String itemOrderId = (String) itemOrderIdList.get(j).get(
						"item_order_id");
				dataMap.put("item_order_id", itemOrderId);
				dataMap.put("alert", action);
				dataMap.put("create_date", new Date());
				dataMap.put("seq_no", autoNum);
				conn.newData("mogan-DB", "system_alert", dataMap);
			}
		}
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
		return dataList;
	}

	/**
	 * @param wonList
	 */
	public void updateItemContactMsg(ArrayList<Map> wonList) {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		NetAgentYJ netAgentYJ = new NetAgentYJ(this.getModelServletContext(),
				this.getAppId());

		for (int i = 0; i < wonList.size(); i++) {
			Map<String, String> tempMap = wonList.get(i);
			String itemId = (String) tempMap.get("ITEM_ID");
			ArrayList<Map> itemOrderIdList = getItemOrderIds(tempMap
					.get("WEBSITE_ID"), tempMap.get("ACCOUNT"), itemId);

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
	 * 更新商品為已得標狀態
	 * 
	 * @param wonList
	 */
	public void updateBid2Won(ArrayList<Map> wonList) {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		for (int i = 0; i < wonList.size(); i++) {
			Map tempMap = wonList.get(i);
			String itemId = (String) tempMap.get("ITEM_ID");
			conn.executSql("mogan-tw",
					"update web_bidding set w_ykj=w_ykj+1 where item_id='"
							+ itemId + "'");
		}
	}

	/**
	 * 將訊息傳給PHP SERVER TODO 還沒試過超過商品數量超過50筆的資料
	 * 
	 * @param msgList
	 * @param title
	 */
	private void sendMsgToPhpServer(ArrayList msgList, String Type) {
		NetAgent nAgent = new NetAgent();
		NetAgentYJ nay = new NetAgentYJ(this.getModelServletContext(), this
				.getAppId());
		// 依類型讀取不同的設定值
		String url = this.getProperty(Type);

		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String sql = "";

		JSONArray jArray = new JSONArray();

		for (int i = 0; i < msgList.size(); i++) {
			Map<String, String> infoMap = (Map) msgList.get(i);
			String itemId = (String) infoMap.get("ITEM_ID");
			String html = null;
			NodeList nodes = null;
			String jsonArgs = "";
			String itemOrderId = "";
			try {
				if (infoMap.get("WEBSITE_ID").equals(
						this.getProperty(YAHOO_JP_WEBSITE_ID))) {
					// 取得賣出清單
					jsonArgs = nay.getHighPriceAccount(infoMap.get("ACCOUNT"),
							infoMap.get("ITEM_ID"), "1").toString();
				}
				sql = "SELECT item_order_id FROM view_sell_item_order WHERE item_id ='"
						+ infoMap.get("ITEM_ID")
						+ "' and item_order_classify_flag='IO-05' ";
				itemOrderId = ((Map<String, String>) conn
						.query("mogan-DB", sql).get(0)).get("item_order_id");

				url = url.replaceAll("\\$MOGAN_ITEM_ORDER_ID", itemOrderId);// 摩根訂單ID
				url = url.replaceAll("\\$ARGS", URLEncoder.encode(jsonArgs,
						"UTF-8"));// 賣出價
				System.out.println("[DEBUG] url::" + url);
				nAgent.getDataWithGet(url);
				System.out.println("[DEBUG] sendMsgToPhpServer::"
						+ nAgent.getResponseBody());
			} catch (AccountNotExistException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void callPhpServer(ArrayList infoList) {
		boolean result = false;
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		Map conditionMap = new HashMap();
		Map dataMap = new HashMap();
		for (int i = 0; i < infoList.size(); i++) {
			if (result == false) {
				// 呼叫失敗必需寫入資料庫
				// TODO 新增資料API，直接整合資料模式

				Map infoMap = (Map) infoList.get(i);

				conditionMap = new HashMap();
				infoMap.get("");
				dataMap = new HashMap();
				dataMap.put("from_user", "JAVA-SYSTEM");// 訊息發出者
				dataMap.put("to_user", "");// 訊息接收者
				dataMap.put("classify_flag", "");// 分類

				dataMap.put("title", infoMap.get("SUBJECT")); // 標題
				dataMap.put("contents", infoMap.get("CONTENT")); // 內容
				dataMap.put("time_at", new Date()); // 建立時間，現在時間
				dataMap.put("web_notify", "0"); // 網頁介面顯示狀態 0=不顯示，1=未讀取，2=已讀取
				dataMap.put("msn_notify", "1"); // MSN 通知
				dataMap.put("mail_notify", "0"); // MAIL 通知
				dataMap.put("text_notify", infoMap.get("TEXT_NOTIFY"));// 簡訊
				dataMap.put("is_keep", "0"); // 是否保留 0=不保留 1=保留
				dataMap.put("delete_flag", "1"); // 刪除狀態 1=未刪除 0=已刪除
				conn.newData("mogan-DB", "member_message", conditionMap,
						dataMap);
			}
		}
	}

	/**
	 * 取得被取消出價的通知mail,
	 */
	private void recordMsg(ArrayList msgList, String title) {
		// ArrayList msgList = getCancelMail();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");

		if (true) {
			// TODO 呼叫PHP錯誤時須要寫入MESSAGE BORD
			System.out.println("[DEBUG]msgList::" + msgList.size());
			for (int i = 0; i < msgList.size(); i++) {
				String insertSQL = "INSERT member_message (" + "message_id," + // ID
						// "reply_msg_id," + //回覆的訊息ID
						"from_user," + // 訊息發出者
						"to_user," + // 訊息接收者
						"classify_flag," + // 分類
						// "item_order_id," + //訂單編號
						"title," + // 標題
						"contents," + // 內容
						"time_at," + // 建立時間，現在時間
						"web_notify," + // 網頁介面顯示狀態 0=不顯示，1=未讀取，2=已讀取
						// "read_date," + //read_date 讀取時間
						"msn_notify," + // MSN 通知
						// "msn_notify_date," + // MSN 通知日期
						"mail_notify," + // MAIL 通知
						// "mail_notify_date," + //MAIL 通知日期
						"text_notify," + // 簡訊
						// "text_notify_date," + //簡訊 通知日期
						"is_keep," + // 是否保留 0=不保留 1=保留
						"delete_flag)" + // 刪除狀態 1=未刪除 0=已刪除
						" values (" + "getAutoNumber('MM-ID-01')," + // 自動編號
						"'JAVA-System'," + // 訊息發出者
						"''," + // 訊息接收者
						"''," + // 訂單編號分類
						// "''," + //訂單編號
						"'" + title + "'," + // 標題
						"'" + msgList.get(i) + "'," + // 內容
						"now()," + // 建立時間，現在時間
						"'0'," + // 網頁介面顯示狀態 0=不顯示，1=未讀取，2=已讀取
						// "''," + //read_date 讀取時間
						"'1'," + // MSN 通知
						// "''," + //MSN 通知日期
						"'1'," + // MAIL 通知
						// "''," + //MAIL 通知日期
						"'0'," + // 簡訊
						// "''," + //簡訊 通知日期
						"'0'," + // 是否保留 0=不保留 1=保留
						"'1'" + // 刪除狀態 1=未刪除 0=已刪除
						")"; //
				System.out.println("[DEBUG] insertSQL:" + insertSQL);
				conn.executSql("mogan-DB", insertSQL);
				// sendMsg("dianwork@hotmail.com", (String) msgList.get(0));
				// sendMsg("mimio_omimi@yahoo.com.tw", (String) msgList.get(0));
				// sendMsg("mimio_omimi@yahoo.com.tw", "總共有 "+msgList.size()+"筆被取消.");
			}
		}
	}

	/**
	 * 抽取內容
	 * 
	 * @param part
	 */
	public static void extractPart(MimeBodyPart part) {
		try {
			String disposition = part.getDisposition();

			if (disposition != null
					&& (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition
							.equalsIgnoreCase(Part.INLINE))) {// 附件
				String fileName = decodeText(part.getFileName());
				System.out.println(fileName);
				// saveAttachFile(part);// 保存附件
			} else {// 正文
				if (part.getContent() instanceof String) {// 接收到的純文本
					System.out.println(part.getContent());
				}
				if (part.getContent() instanceof MimeMultipart) {// 接收的郵件有附件時
					BodyPart bodyPart = ((MimeMultipart) part.getContent())
							.getBodyPart(0);
					System.out.println(bodyPart.getContent());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 分析郵件
	 * 
	 * @param content
	 */
	public static void parseMailContent(Object content) {
		try {
			if (content instanceof Multipart) {
				System.out.println("Multipart:Multipart:Multipart");
				Multipart mPart = (MimeMultipart) content;
				for (int i = 0; i < mPart.getCount(); i++) {
					extractPart((MimeBodyPart) mPart.getBodyPart(i));
				}
			} else if (content instanceof Part) {
				System.out.println("Part:Part:Part");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static String decodeText(String text)
			throws UnsupportedEncodingException {
		if (text == null)
			return null;
		if (text.startsWith("=?GB") || text.startsWith("=?gb")) {
			text = MimeUtility.decodeText(text);
		} else {
			text = new String(text.getBytes("ISO8859_1"));
		}
		return text;
	}
}
