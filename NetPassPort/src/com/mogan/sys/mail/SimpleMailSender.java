package com.mogan.sys.mail;

import java.io.*;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import org.apache.log4j.Logger;
import com.mogan.sys.log.SysLogger4j;


/**
 * 簡單郵件（不帶附件的郵件）發送器 繼承Thread
 * extends Thread
 */
public class SimpleMailSender implements Runnable {
	// private ArrayList<MailSenderInfo> mailList;
	static private Logger logger  =  Logger.getLogger(SimpleMailSender.class.getName());
	private int sendSeq;

	// 辨識暫停狀態 0=未發送，1=發送中，2=暫停，3=中斷 ，4=結束
	private int status = 0;
	private String mailContent;
	private String mailSubject;
	private ArrayList targetList;
	// private Object owner;//TODO 應該使用繼承 改版目標
	private boolean debugModeFlag = false;
	private int intervalTime = 5;
	private String mailServerHost;
	private String mailServerPort;
	private String account;
	private String pwd;
	private String fromName;
	private String fromAddress;
	private StringBuffer messages;

	private final String BCC_ADDRESS="mogan@mogan.com.tw";
	
	public SimpleMailSender() {
		// mailList = new ArrayList<MailSenderInfo>();
		targetList = new ArrayList();
		setSendSeq(0);
		messages=new StringBuffer();
	}

	public void mailToAdmin(String msg){
		MailSenderInfo msi=getMailInfo("系統管理者","elgoogdian@gmail.com");
		msi.setContent(msg);
		sendHtmlMail(msi);
	}
	
	/**
	 * 繼續發送
	 */
	public void unpauseMe() {
		status = 1;
		synchronized (this) {
			this.notify();
		}
	}

	/**
	 * 暫停發送
	 */
	public void pauseMe() {
		status = 2;
	}

	/**
	 * 停止發送
	 */
	public void stopMe() {
		status = 3;
	}


	/**
	 * @param toName 收件人名稱
	 * @param toAddress 收件人address
	 * @param ccAddress 副本address
	 * @param bccAddress 密件副本address
	 * @return MailSenderInfo 
	 */
	public MailSenderInfo getMailInfo( String toName,String toAddress,String ccAddress,String bccAddress) {
		MailSenderInfo msi = new MailSenderInfo();
		msi.setMailServerHost(this.mailServerHost);		// Mail Server
		msi.setMailServerPort(this.mailServerPort);		// port
		msi.setUserName(this.account);					// 發信Mail 帳號
		msi.setPassword(pwd);							// 您的郵箱密碼
		msi.setCode("utf-8"); 							// 編碼
		msi.setFrom(this.fromName); 					// 寄件者
		msi.setFromAddress(this.fromAddress); 			// 寄件者Mail Address
		msi.setToAddress(toAddress); 					// 收件者 Mail Address 必設
		msi.addToAddress(toAddress);					// 收件者 Mail Address
		msi.addCCAddress(ccAddress); 					// 副本address
		msi.addBCCAddress(bccAddress+";"+this.BCC_ADDRESS); // 密件副本address
		msi.setValidate(false); 						// 驗證?
		msi.setSubject(this.mailSubject); 				// Mail Subject
		msi.setContent(this.mailContent); 				// Mail Content
		msi.setToName(toName);
		return msi;
	}
	
	/**
	 * @param toName 收件人名稱
	 * @param toAddress 收件人address
	 * @return MailSenderInfo 
	 */
	public MailSenderInfo getMailInfo( String toName,String toAddress) {
		return getMailInfo(toName,toAddress,"","");
	}
	
	/**
	 * @param toName 收件人名稱
	 * @param toAddress 收件人address
	 * @param ccAddress 副本address
	 * @return MailSenderInfo 
	 */
	public MailSenderInfo getMailInfo( String toName,String toAddress,String ccAddress) {
		return getMailInfo(toName,toAddress,ccAddress,"");
	}

	/**
	 * 實作執行緒行，run 依targetList內容寄出mail，如status=1時就暫停執行緒，status=2中斷執行緒，
	 */
	public void run() {
		long t1=System.currentTimeMillis();
		status = 1;
		int err=0;
		MailSenderInfo mail=getMailInfo("親愛的摩根會員","");
		messages.append("\n[訊息] 信件發送開始.預計發出"+targetList.size()+" 封.");
		for (int i = 0; i < targetList.size(); i++) {
			try {
				if (status == 2) {
					//暫停寄信
					synchronized (this) {
						this.wait();
					}
				} else if (status == 3) {
					//停止發信
					System.out.println("[INFO] 停止發信.");
					break;
				}
				Map tempMap = (Map) targetList.get(i);
				String email = (String) tempMap.get("email");
				//debugModeFlag=true;
				if (debugModeFlag) {
					email = "ELGOOGDIAN@gmail.com";
				}
				//email="w_"+email+".fixcom";
				//判斷e mail address 是否符合格式，符合才加入寄送資料
				Pattern pattern = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
			    Matcher matcher = pattern.matcher(email);
			    if (matcher.matches()){
			    	mail.addBCCAddress(email);
			    	System.out.println("[INFO] 信件#" + i + " 寄出."+tempMap.get("email")+"["+email+"]" );
			    }
				setSendSeq(i+1);
				//messages.append("\n[訊息] 信件#"+getSendSeq()+")"+" "+(String) tempMap.get("realname") +"("+email+")");

				if (i != 0 && i % 50 == 0 || !(i+1<targetList.size())){
					//Thread.sleep(intervalTime * 1000); 
					if (sendHtmlMail(mail)){
						messages.append("\n[訊息]  寄出成功.");
					}else{
						err++;
						messages.append("\n[訊息]  寄出失敗.");
					}
					mail=null;
					mail=getMailInfo("親愛的摩根會員","");
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				err++;
				e.printStackTrace();
			}
		}/*
		if (sendHtmlMail(mail)){
			messages.append("\n[訊息]  寄出成功.");
		}else{
			err++;
			messages.append("\n[訊息]  寄出失敗.");
		}*/
		long t2=System.currentTimeMillis();
		messages.append("\n");
		messages.append("[訊息] 信件全數發送完成，成功 "+this.getSendSeq()+"封，失敗"+err+"封，共 "+targetList.size()+"封 .耗時 "+(t2-t1)+"ms.");
		// ((MailBoradEx)owner).maillFinish();
		status = 4;
	}
	
	public boolean checkAddressFormat(){
		return true;
	}
	
	/**
	 * 以文本格式發送郵件
	 * 
	 * @param mailInfo
	 *            待發送的郵件的資訊
	 */
	public boolean sendTextMail(MailSenderInfo mailInfo) {
		// 判斷是否需要身份認證
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();

		
		if (mailInfo.isValidate()) {
			// 如果需要身份認證，則創建一個密碼驗證器
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		// 根據郵件會話屬性和密碼驗證器構造一個發送郵件的session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);
		try {
			// 根據session創建一個郵件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 創建郵件發送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress(),
					mailInfo.getFrom(), mailInfo.getCode());
			// 設置郵件消息的發送者

			mailMessage.setFrom(from);
			// 創建郵件的接收者位址，並設置到郵件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			mailMessage.setRecipient(Message.RecipientType.TO, to);

			// 設置郵件消息的主題
			mailMessage.setSubject(MimeUtility.encodeText(
					mailInfo.getSubject(), mailInfo.getCode(), null));
			// 設置郵件消息發送的時間
			mailMessage.setSentDate(new Date());
			// 設置郵件消息的主要內容
			String mailContent = mailInfo.getContent();
			// 建立多內容郵件物件
			MimeMultipart mp = new MimeMultipart();
			// 建立內文ߤ���
			MimeBodyPart mbp1 = new MimeBodyPart();
			mbp1.setText(mailContent, mailInfo.getCode());
			mp.addBodyPart(mbp1);
			mailMessage.setContent(mp);
			// 發送郵件
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * 以HTML格式發送郵件
	 * 
	 * @param mailInfo
	 *            待發送的郵件資訊
	 * @throws MessagingException 
	 */
	public boolean sendHtmlMail(MailSenderInfo mailInfo) {
		// 判斷是否需要身份認證
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		Transport transport;
		// 如果需要身份認證，則創建一個密碼驗證器
		System.out.println("[DEBUG] mailInfo.isValidate()::"+mailInfo.isValidate());
		System.out.println("[DEBUG] mailInfo.getMailServerHost()::"+mailInfo.getMailServerHost());
		System.out.println("[DEBUG] mailInfo.getMailServerPort()::"+mailInfo.getMailServerPort());
		//if (mailInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		//}
		try {
			Provider [] p=Security.getProviders();
			for (int i=0;i<p.length;i++){
				logger.info("Security OK!!"+p[i].getName());
				
			}
		// 根據郵件會話屬性和密碼驗證器構造一個發送郵件的session
		Session sendMailSession = Session
				.getInstance(pro);
		transport=sendMailSession.getTransport("smtp");
			// 根據session創建一個郵件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			
			// 創建郵件發送者地址
			Address from;
		
				from = new InternetAddress(mailInfo.getFromAddress(),
						mailInfo.getFrom(), mailInfo.getCode());

			// 設置郵件消息的發送者
			mailMessage.setFrom(from);
			// 創建郵件的接收者位址，並設置到郵件消息中

			// Message.RecipientType.TO
			
			mailMessage.setRecipients(Message.RecipientType.TO, mailInfo.getToAddressList());
			mailMessage.setRecipients(Message.RecipientType.CC, mailInfo.getCCAddressList());
			mailMessage.setRecipients(Message.RecipientType.BCC, mailInfo.getBCCAddressList());
			// 設置郵件消息的主題
			System.out.println("mailInfo.getSubject()="+mailInfo.getSubject());
			mailMessage.setSubject(MimeUtility.encodeText(
					mailInfo.getSubject(), mailInfo.getCode(), null));
			// 設置郵件消息發送的時間
			mailMessage.setSentDate(new Date());
			// MiniMultipart類是一個容器類，包含MimeBodyPart類型的物件
			Multipart mainPart = new MimeMultipart();
			// 創建一個包含HTML內容的MimeBodyPart
			BodyPart html = new MimeBodyPart();
			// 設置HTML內容
			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);
			// 將MiniMultipart物件設置郵件內容
			mailMessage.setContent(mainPart);
			logger.info("[DEBUG] 收件人:1:"+mailInfo.getToName()+" ADDRESS:"+mailInfo.getToAddress());
			// 發送郵件
			transport.connect();
			logger.info("[DEBUG] 收件人:2:"+mailInfo.getToName()+" ADDRESS:"+mailInfo.getToAddress());
			transport.send(mailMessage, mailMessage.getAllRecipients());
			logger.info("[DEBUG] 收件人:3:"+mailInfo.getToName()+" ADDRESS:"+mailInfo.getToAddress());
			transport.close();
			logger.info("[DEBUG] 收件人:4:"+mailInfo.getToName()+" ADDRESS:"+mailInfo.getToAddress());
			mailMessage=null;
			transport=null;
			sendMailSession=null;
			return true;
		} catch (UnsupportedEncodingException e) {
			SysLogger4j.error("UnsupportedEncodingException",e);
		} catch (MessagingException e) {
			SysLogger4j.error("MessagingException",e);
		}
		return false;
	}

	/**
	 * @param mailContent
	 *            the mailContent to set
	 */
	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	/**
	 * @return the mailContent
	 */
	public String getMailContent() {
		return mailContent;
	}

	/**
	 * @param mailSubject
	 *            the mailSubject to set
	 */
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	/**
	 * @return the mailSubject
	 */
	public String getMailSubject() {
		return mailSubject;
	}

	/**
	 * @param targetList
	 *            the targetList to set
	 */
	public void setTargetList(ArrayList targetList) {
		this.targetList = targetList;
	}

	/**
	 * @return the targetList
	 */
	public ArrayList getTargetList() {
		return targetList;
	}

	/**
	 * @param testModeFlag
	 *            the testModeFlag to set
	 */
	public void setDebugModeFlag(boolean debugModeFlag) {
		this.debugModeFlag = debugModeFlag;
	}

	/**
	 * @return the testModeFlag
	 */
	public boolean isDebugModeFlag() {
		return debugModeFlag;
	}

	/**
	 * @param itime
	 *            the itime to set
	 */
	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
	}

	/**
	 * @return the itime
	 */
	public int getIntervalTime() {
		return intervalTime;
	}

	/**
	 * @param mailServerHost
	 *            the mailServerHost to set
	 */
	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	/**
	 * @return the mailServerHost
	 */
	public String getMailServerHost() {
		return mailServerHost;
	}

	/**
	 * @param mailServerPort
	 *            the mailServerPort to set
	 */
	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	/**
	 * @return the mailServerPort
	 */
	public String getMailServerPort() {
		return mailServerPort;
	}

	/**
	 * @param pwd
	 *            the pwd to set
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	/**
	 * @return the pwd
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * @param fromName
	 *            the fromName to set
	 */
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	/**
	 * @return the fromName
	 */
	public String getFromName() {
		return fromName;
	}

	/**
	 * @param fromAddress
	 *            the fromAddress to set
	 */
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	/**
	 * @return the fromAddress
	 */
	public String getFromAddress() {
		return fromAddress;
	}

	/**
	 * @param account
	 *            the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	public int getStatus() {
		return this.status;
	}

	/**
	 * @return the sendIndex
	 */
	public int getSendSeq() {
		return sendSeq;
	}

	/**
	 * 
	 * @param sendSeq
	 */
	private void setSendSeq(int sendSeq) {
		this.sendSeq = sendSeq;
	}
	
	/**
	 * 讀取訊息並清空
	 * @return
	 */
	synchronized public String getMessagesWithDel(){
		String s=this.messages.toString();
		this.messages.delete(0, this.messages.length());
		return s;
	}
}
