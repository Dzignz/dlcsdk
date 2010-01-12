package com.mogan.model;

import net.sf.jml.Email;
import net.sf.jml.MsnContact;
import net.sf.jml.MsnList;
import net.sf.jml.MsnMessenger;
import net.sf.jml.MsnProtocol;
import net.sf.jml.MsnSwitchboard;
import net.sf.jml.MsnUserStatus;
import net.sf.jml.event.MsnContactListAdapter;
import net.sf.jml.event.MsnMessageAdapter;
import net.sf.jml.event.MsnSwitchboardAdapter;
import net.sf.jml.impl.MsnMessengerFactory;
import net.sf.jml.message.MsnControlMessage;
import net.sf.jml.message.MsnDatacastMessage;
import net.sf.jml.message.MsnInstantMessage;
import net.sf.json.JSONArray;

public class MsnBotEx {
	MsnMessenger messenger;

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
				message.setContent("目前版本0.2");
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

	public MsnUserStatus getMsnStatus(){
		return messenger.getOwner().getStatus();
	}
	
	/**
	 * 指定帳號密碼登入
	 * 
	 * @param account
	 * @param pwd
	 */
	public void login(String account, String pwd) {
		// create MsnMessenger instance
		System.out.println("MsnBot login." + account);
		messenger = MsnMessengerFactory.createMsnMessenger(account, pwd);

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
	public boolean sendMsg(final String email, final String msg)
			throws Exception {
		// TODO 需針對EMAIL 找到指定的 MSNBot
		boolean sendFlag = true;
		if ((messenger.getOwner().getStatus() == MsnUserStatus.OFFLINE)) {
			sendFlag = false;
		}

		MsnContact[] contacts = messenger.getContactList().getContactsInList(
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
			//無法送出
			return sendFlag;
		}

		/** 設定訊息 */
		final MsnInstantMessage message = new MsnInstantMessage();
		message.setBold(false);
		message.setItalic(false);
		message.setFontRGBColor(255 * 102 * 37);
		message.setContent(msg);

		/** 判斷指定對話框是否存在 */
		MsnSwitchboard[] switchboards = messenger.getActiveSwitchboards();
		for (int i = 0; i < switchboards.length; i++) {
			if (switchboards[i].containContact(Email.parseStr(email))
					&& switchboards[i].getAllContacts().length == 1) {
				if (switchboards[i].sendMessage(message, true)) {
					switchboards[i].close();
					sendFlag = false;
					break;
				}
			}
		}
		
		if (sendFlag == false) {
			//已送出
			return sendFlag;
		}

		final Object attachment = new Object();
		messenger.addSwitchboardListener(new MsnSwitchboardAdapter() {
			public void switchboardStarted(MsnSwitchboard switchboard) {
				if (switchboard.getAttachment() == attachment) {

					messenger.getContactList().getContacts()[0].getEmail()
							.getEmailAddress();
					messenger.getContactList().getContacts()[0].getStatus();
					switchboard.inviteContact(Email.parseStr(email));
				}
			}

			public void contactJoinSwitchboard(MsnSwitchboard switchboard,
					MsnContact contact) {
				if (switchboard.getAttachment() == attachment
						&& Email.parseStr(email).equals(contact.getEmail())) {
					switchboard.setAttachment(null);
					if (switchboard.sendMessage(message, true)) {
						switchboard.close();
					}
					messenger.removeSwitchboardListener(this);

				}
			}
		});
		messenger.newSwitchboard(attachment);

		return sendFlag;
	}

}
