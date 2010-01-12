package com.mogan.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.mail.NoSuchProviderException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jml.message.MsnControlMessage;
import net.sf.jml.message.MsnInstantMessage;
import net.sf.jml.message.MsnDatacastMessage;

import net.sf.jml.Email;
import net.sf.jml.MsnContact;
import net.sf.jml.MsnList;
import net.sf.jml.MsnMessenger;
import net.sf.jml.MsnObject;
import net.sf.jml.MsnProtocol;
import net.sf.jml.MsnSwitchboard;
import net.sf.jml.MsnUserStatus;
import net.sf.jml.event.MsnAdapter;
import net.sf.jml.event.MsnContactListAdapter;
import net.sf.jml.event.MsnMessageAdapter;
import net.sf.jml.event.MsnSwitchboardAdapter;
import net.sf.jml.impl.MsnMessengerFactory;
import net.sf.json.JSONArray;

import com.mogan.model.netAgent.NetAgentGoogle;
import com.mogan.sys.DBConn;
import com.mogan.sys.ProtoModel;
import com.mogan.sys.ServiceModelFace;
import com.mogan.sys.mail.SimpleMailSender;

/**
 * modelName=MsnBot Msn 機器人
 * 
 * @author Dian
 */
public class MsnBot extends ProtoModel implements ServiceModelFace, Runnable {

	final private String welcomMsg = "會員#MOGAN_USER_ID 您好\n歡迎使用 摩根小甜心 MSN 即時服務";
	final private String welcomMsg_1 = "小甜心 不懂你的意思\n建議使用下列指令";
	final private String newUser = "小甜心 還不認識你";
	static NetAgentGoogle netAgentG = null;
	// private MsnMessenger messenger;
	boolean run_flag = true;

	static public void main(String[] args) {

	}

	/**
	 * @return
	 */
	public Map getMsnBotMap() {
		if (this.getModelServletContext().getAttribute("MSNBot") == null) {
			this.getModelServletContext().setAttribute("MSNBot", new HashMap());
		}
		return (Map) this.getModelServletContext().getAttribute("MSNBot");
	}

	/**
	 * @return
	 */
	public void setMsnBotMap(Map botMap) {
		this.getModelServletContext().setAttribute("MSNBot", botMap);
	}

	@Override
	public void run() {
		System.out.println("MsnBot run.");
		sysLogin();
		boolean run_flag = true;
		while (run_flag) {
			System.out.println("[INFO]CHECK checkBid");
			try {
				checkBid();
				Thread.sleep(1000 * 60 * 5);
				sysLogout();
				System.out.println("[INFO] CHECK checkBid logout");
				run_flag=false;
			} catch (InterruptedException e) {
				// TODO 寄信通知管理者
				e.printStackTrace();
				run_flag = false;
				SimpleMailSender sms = new SimpleMailSender();
				sms.mailToAdmin("msn BOT 已停止");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SimpleMailSender sms = new SimpleMailSender();
				sms.mailToAdmin("msn BOT 已停止");
			}
		}
		// TODO Auto-generated method stub
	}

	/**
	 * 傳送競標相關訊息 TODO 過渡時期，由JAVA通知
	 * 
	 * @throws Exception
	 */
	public void checkBid() throws Exception {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String sql = "SELECT * FROM member_message WHERE msn_notify='1' AND title='日雅出價被取消'";
		ArrayList<Map> msgList = conn.query("mogan-DB", sql);
		for (int i = 0; i < msgList.size(); i++) {
			Map msgMap = (Map) msgList.get(i);
			boolean snedFlag = false;
			snedFlag = sendMsg(
					"dianwork@hotmail.com",
					msgMap.get("message_id") + " " + msgMap.get("title") + " "
							+ msgMap.get("contents")).getBoolean(0);

			snedFlag = sendMsg(
					"mimio_omimi@yahoo.com.tw",
					msgMap.get("message_id") + " " + msgMap.get("title") + " "
							+ msgMap.get("contents")).getBoolean(0);
			if (snedFlag) {
				// 更新MSN傳送狀態
				String msnSendSQL = "UPDATE member_message SET msn_notify='2' where message_id='"
						+ msgMap.get("message_id") + "'";
				conn.executSql("mogan-DB", msnSendSQL);
			}
		}
	}

	private void checkSellBid() {
		// TODO
	}

	private void checkWonBid() {
		// TODO
	}

	/**
	 * <P>
	 * <font size=7 color=red>ACTION = SEND_MSG</font>
	 * </P>
	 * 
	 * @param email
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	private JSONArray sendMsg(final String email, final String msg)
			throws Exception {
		// TODO 需針對EMAIL 找到指定的 MSNBot
		JSONArray jArray = new JSONArray();
		final MsnMessenger msnBot = getMsnBot(email);
		boolean sendFlag = true;
		if ((msnBot.getOwner().getStatus() == MsnUserStatus.OFFLINE)) {
			sendFlag = false;
		}
		
		MsnContact[] contacts = msnBot.getContactList().getContactsInList(
				MsnList.AL);

		for (int i = 0; i < contacts.length; i++) {
			// 檢查發送對向上線狀態，不在線上則不發送訊息
			if (contacts[i].getStatus() == MsnUserStatus.OFFLINE
					&& contacts[i].getEmail().getEmailAddress().equals(email)) {
				sendFlag = false;
				break;
			}
		}

		if (sendFlag == false) {
			jArray.add(sendFlag);
			return jArray;
		}

		final MsnInstantMessage message = new MsnInstantMessage();
		message.setBold(false);
		message.setItalic(false);
		message.setFontRGBColor(255 * 102 * 37);
		message.setContent(msg);

		MsnSwitchboard[] switchboards = msnBot.getActiveSwitchboards();
		for (int i = 0; i < switchboards.length; i++) {
			if (switchboards[i].containContact(Email.parseStr(email))
					&& switchboards[i].getAllContacts().length == 1) {
				switchboards[i].sendMessage(message);
			}
		}

		final Object attachment = new Object();
		msnBot.addSwitchboardListener(new MsnSwitchboardAdapter() {
			public void switchboardStarted(MsnSwitchboard switchboard) {
				if (switchboard.getAttachment() == attachment) {

					msnBot.getContactList().getContacts()[0].getEmail()
							.getEmailAddress();
					msnBot.getContactList().getContacts()[0].getStatus();
					switchboard.inviteContact(Email.parseStr(email));
				}
			}

			public void contactJoinSwitchboard(MsnSwitchboard switchboard,
					MsnContact contact) {
				if (switchboard.getAttachment() == attachment
						&& Email.parseStr(email).equals(contact.getEmail())) {
					switchboard.setAttachment(null);
					switchboard.sendMessage(message);
					msnBot.removeSwitchboardListener(this);
				}
			}
		});
		msnBot.newSwitchboard(attachment);

		jArray.add(sendFlag);
		return jArray;
	}

	/**
	 * 使用會員email，取得對應的登入的MSNbot
	 * 
	 * @param email
	 * @return
	 */
	private MsnMessenger getMsnBot(String email) {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String sql = "SELECT sweet_email FROM view_member_msn WHERE member_msn_email='"
				+ email + "' and delete_flag='1'";
		ArrayList<Map> accountList = conn.query("mogan-DB", sql);
		MsnMessenger msnBot = null;
		if (accountList.size() > 0) {
			Map botMap = getMsnBotMap();
			msnBot = (MsnMessenger) botMap.get("MSN_"
					+ accountList.get(0).get("sweet_email"));
		}
		msnBot.login();
		return msnBot;
	}

	/**
	 * 指定帳號密碼登入
	 * 
	 * @param account
	 * @param pwd
	 */
	private MsnMessenger login(String account, String pwd) {
		// create MsnMessenger instance
		System.out.println("MsnBot login." + account);
		MsnMessenger messenger = MsnMessengerFactory.createMsnMessenger(
				account, pwd);

		// MsnMessenger support all protocols by default
		messenger
				.setSupportedProtocol(new MsnProtocol[] { MsnProtocol.MSNP12 });

		// default init status is online,
		// messenger.getOwner().setInitStatus(MsnUserStatus.BUSY);

		// log incoming message
		// messenger.setLogIncoming(true);

		// log outgoing message
		// messenger.setLogOutgoing(true);

		/*
		 * MsnObject displayPicture = MsnObject.getInstance("mogansweet@gmail.com","D:\\20090105102606627.jpg");
		 * displayPicture.setType(MsnObject.TYPE_DISPLAY_PICTURE); messenger.getDisplayPictureDuelManager().setDisplayPicutre(displayPicture);
		 * messenger.getDisplayPictureDuelManager().setDisplayPicutre(displayPicture); messenger.getOwner().setInitDisplayPicture(displayPicture);
		 */
		initMessenger(messenger);
		messenger.login();

		return messenger;

	}

	/**
	 * 使用預設的帳號登入
	 */
	private void login() {
		MsnMessenger messenger = login(this.getProperties().getProperty(
				"msnAccount"), this.getProperties().getProperty("msnPwd"));
		Map botMap = getMsnBotMap();
		botMap.put("MSN_" + this.getProperties().getProperty("msnAccount"),
				messenger);
		setMsnBotMap(botMap);
	}

	/**
	 * TODO 使用系統設定的帳號登出
	 */
	public void sysLogout() {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String sql = "SELECT * FROM msn_sweet WHERE delete_flag='1'";
		ArrayList accountList = conn.query("mogan-DB", sql);
		Map botMap = getMsnBotMap();
		for (int i = 0; i < accountList.size(); i++) {
			Map accountMap = (Map) accountList.get(i);
			/*
			MsnMessenger messenger = login((String) accountMap.get("email"),
					(String) accountMap.get("pwd"));*/
			MsnMessenger messenger=(MsnMessenger) botMap.get("MSN_" + accountMap.get("email"));
			messenger.logout();
		}
		setMsnBotMap(botMap);
	}
	
	/**
	 * TODO 使用系統設定的帳號登入
	 */
	public void sysLogin() {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String sql = "SELECT * FROM msn_sweet WHERE delete_flag='1'";
		ArrayList accountList = conn.query("mogan-DB", sql);
		Map botMap = getMsnBotMap();
		for (int i = 0; i < accountList.size(); i++) {
			Map accountMap = (Map) accountList.get(i);
			MsnMessenger messenger = login((String) accountMap.get("email"),
					(String) accountMap.get("pwd"));
			botMap.put("MSN_" + accountMap.get("email"), messenger);
		}
		setMsnBotMap(botMap);
	}

	/**
	 * TODO
	 * 
	 * @param msnAccount
	 * @return
	 */
	private JSONArray getUserInfoByMsnAccount(String msnAccount) {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String sql = "SELECT * FROM web_member WHERE msn_account='"
				+ msnAccount + "'";
		return conn.queryJSONArray("mogan-tw", sql);
	}

	protected void initMessenger(MsnMessenger messenger) {
		System.out.println("MsnBot initMessenger.");
		// messenger.addListener(new MsnAdapter(){});
		messenger.addMessageListener(new MsnMessageAdapter() {

			/**
			 * 接收到文字訊息
			 * 
			 * @param switchboard
			 * @param message
			 * @param contact
			 */
			public void instantMessageReceived(MsnSwitchboard switchboard,
					MsnInstantMessage message, MsnContact contact) {
				// text message received
				// System.out.println("[DEBUG] instantMessageReceived 1#"+switchboard.getIncomingMessageChain().iterator().previous().asString());
				// System.out.println("[DEBUG] instantMessageReceived 2#"+switchboard.getIncomingMessageChain().iterator().previous().toString());
				// System.out.println("[DEBUG] instantMessageReceived 3#"+switchboard.getIncomingMessageChain().iterator().previous().getCommand());
				// System.out.println("[DEBUG] instantMessageReceived 4#"+switchboard.getIncomingMessageChain().iterator().previous().getCommand());
				/*
				 * JSONArray userList = getUserInfoByMsnAccount(contact.getEmail() .getEmailAddress()); MsnInstantMessage outMessage = new
				 * MsnInstantMessage(); if (userList.size() > 0) { outMessage.setContent(welcomMsg.replaceAll( "#MOGAN_USER_ID",
				 * userList.getJSONObject(0) .getString("name"))); } else { outMessage.setContent(contact.getEmail().getEmailAddress() + "\n" +
				 * newUser); } System.out.println("[debug]" + contact.getEmail().getEmailAddress()); System.out.println("[debug]" +
				 * message.getContent()); // welcomMsg.replaceAll("#MOGAN_USER_ID", "Dian");
				 */

				switchboard.sendMessage(message);
			}

			/**
			 * 接收到狀態訊息，"正在輸入文字"
			 * 
			 * @param switchboard
			 * @param message
			 * @param contact
			 */
			public void controlMessageReceived(MsnSwitchboard switchboard,
					MsnControlMessage message, MsnContact contact) {
				// such as typing message and recording message
				// switchboard.sendMessage(message);

				System.out.println("[DEBUG] 正在輸入文字::"
						+ message.getRecordingUser());
			}

			/**
			 * 接收到震動
			 * 
			 * @param switchboard
			 * @param message
			 * @param contact
			 */
			public void datacastMessageReceived(MsnSwitchboard switchboard,
					MsnDatacastMessage message, MsnContact contact) {
				// such as Nudge
				System.out.println("[DEBUG] 正在輸入文字::"
						+ message.getContentType());
				// switchboard.sendMessage(message);
			}
		});

		messenger.addContactListListener(new MsnContactListAdapter() {

			public void contactStatusChanged(MsnMessenger messenger,
					MsnContact friend) {
				System.out.println("[DEBUG] friend " + friend.getEmail()
						+ " status changed from " + friend.getOldStatus()
						+ " to " + friend.getStatus());
			}

			public void contactAddedMe(MsnMessenger messenger, MsnContact friend) {
				System.out.println(friend.getEmail() + " add " + messenger);
				messenger.addFriend(friend.getEmail(), "測試加入聯絡人");
				// helloMyFriend();
			}

			/**
			 * 登入完成
			 * 
			 * @param messenger
			 */
			public void loginCompleted(MsnMessenger messenger) {
				// log.info(messenger + " login complete ");
				// messenger.getOwner().getEmail()

				System.out.println(messenger.getOwner().getEmail()
						+ " add loginCompleted");
			}

			/**
			 * 取得完整的連路人清單
			 */
			public void contactListInitCompleted(MsnMessenger messenger) {

				// get contacts in allow list
				MsnContact[] contacts = messenger.getContactList()
						.getContactsInList(MsnList.AL);
				System.out.println("[DEBUG] Allow List." + contacts.length);
				for (int i = 0; i < contacts.length; i++) {
					// don't send message to offline contact
					System.out.println("contacts[i].getEmail()"
							+ contacts[i].getEmail());
					if (contacts[i].getStatus() != MsnUserStatus.OFFLINE) {
						// this is the simplest way to send text
						// messenger.sendText(contacts[i].getEmail(), "hello");
					}
				}
				contacts = messenger.getContactList().getContactsInList(
						MsnList.BL);
				System.out.println("[DEBUG] Block  List." + contacts.length);
				for (int i = 0; i < contacts.length; i++) {
					messenger.addFriend(contacts[i].getEmail(), "測試加入聯絡人");
				}

				contacts = messenger.getContactList().getContactsInList(
						MsnList.FL);
				System.out.println("[DEBUG] Forward  List." + contacts.length);
				for (int i = 0; i < contacts.length; i++) {
					messenger.addFriend(contacts[i].getEmail(), "測試加入聯絡人");
				}

				contacts = messenger.getContactList().getContactsInList(
						MsnList.RL);
				System.out.println("[DEBUG] Reverse List." + contacts.length);
				for (int i = 0; i < contacts.length; i++) {
					messenger.addFriend(contacts[i].getEmail(), "測試加入聯絡人");
				}
			}

		});
	}

	private void logout() {

	}

	private void sendFile() {

	}

	private void helloMyFriend(String email, int st) {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");

		String sql = "SELECT * FROM view_member_msn WHERE delete_flag='1' and member_msn_email='"
				+ email + "'";
		ArrayList memberList = conn.query("mogan-tw", sql);
		if (memberList.size() > 0) {
			Map memberMap = (Map) memberList.get(0);
			if (memberMap.get("classify_flag").equals("MF-01")) {
				// 管理者
			} else if (memberMap.get("classify_flag").equals("MF-02")) {
				// 一般會員
			} else if (memberMap.get("classify_flag").equals("MF-03")) {
				// 進階會員
			}
		}
	}

	@Override
	public JSONArray doAction(Map parameterMap) throws Exception {
		// TODO Auto-generated method stub
		JSONArray jArray = new JSONArray();
		if (this.getAct().equals("SEND_MSG")) {
			String account = (String) parameterMap.get("ACCOUNT");
			String msg = (String) parameterMap.get("MSG");
			jArray = sendMsg(account, msg);
		}
		return jArray;
	}

}
