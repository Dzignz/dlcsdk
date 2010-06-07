package com.mogan.sys.mail;

/**
 * 發送郵件需要使用的基本資訊
 */
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class MailSenderInfo {
	// 發送郵件的伺服器的IP和埠
	private String mailServerHost;
	private String mailServerPort = "25";
	// 郵件編碼格式
	private String code = "utf-8";
	// 郵件發送者名稱
	private String from;
	// 郵件發送者的地址
	private String fromAddress;
	// 郵件接收者名稱
	private String toName;
	// 郵件接收者的地址
	private String toAddress;
	// 登陸郵件發送伺服器的用戶名和密碼
	private String userName;
	private String password;
	// 是否需要身份驗證
	private boolean validate = false;
	// 郵件主題
	private String subject;
	// 郵件的文本內容
	private String content;
	// 郵件附件的檔案名
	private String[] attachFileNames;

	private ArrayList bccAddressList = new ArrayList();
	private ArrayList ccAddressList = new ArrayList();
	private ArrayList toAddressList = new ArrayList();

	/**
	 * 獲得郵件會話屬性
	 */
	public Properties getProperties() {
		Properties p = new Properties();
		p.put("mail.smtp.host", this.mailServerHost);
		p.put("mail.smtp.port", this.mailServerPort);
		p.put("mail.smtp.auth", "false");
		return p;
	}

	public void addBCCAddress(String bccAddress) {
		if (bccAddress != null && bccAddress.length() > 0) {
			for (int i = 0; i < bccAddress.split(";").length; i++) {
				if (!bccAddressList.contains(bccAddress.split(";")[i]) && bccAddress.split(";")[i].length()>0) {
					bccAddressList.add(bccAddress.split(";")[i]);
				}
			}
		}
	}

	public void addCCAddress(String ccAddress) {
		if (ccAddress != null && ccAddress.length() > 0) {
			for (int i = 0; i < ccAddress.split(";").length; i++) {
				if (!ccAddressList.contains(ccAddress.split(";")[i])) {
					ccAddressList.add(ccAddress.split(";")[i]);
				}
			}
		}
	}

	public void addToAddress(String toAddress) {
		if (toAddress != null && toAddress.length() > 0) {
			for (int i = 0; i < toAddress.split(";").length; i++) {
				if (!toAddressList.contains(toAddress.split(";")[i])) {
					toAddressList.add(toAddress.split(";")[i]);
				}
			}
		}
	}

	public Address[] getToAddressList() {
		Address[] to = new InternetAddress[toAddressList.size()];
		
		for (int i = 0; i < to.length; i++) {
			try {
				to[i] = new InternetAddress((String) toAddressList.get(i));
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return to;
	}

	public Address[] getCCAddressList() {
		Address[] cc = new InternetAddress[ccAddressList.size()];

		for (int i = 0; i < cc.length; i++) {
			try {
				cc[i] = new InternetAddress((String) ccAddressList.get(i));
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return cc;
	}

	public Address[] getBCCAddressList() {
		Address[] bcc = new InternetAddress[bccAddressList.size()];		
		for (int i = 0; i < bcc.length; i++) {
			try {
				if (((String)bccAddressList.get(i)).length()>0){
					bcc[i] = new InternetAddress((String) bccAddressList.get(i));
				}
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bcc;
	}

	public String getMailServerHost() {
		return mailServerHost;
	}

	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	public String getMailServerPort() {
		return mailServerPort;
	}

	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String[] getAttachFileNames() {
		return attachFileNames;
	}

	public void setAttachFileNames(String[] fileNames) {
		this.attachFileNames = fileNames;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String textContent) {
		this.content = textContent;
	}

	/**
	 * @param toName
	 *            the toName to set
	 */
	public void setToName(String toName) {
		this.toName = toName;
	}

	/**
	 * @return the toName
	 */
	public String getToName() {
		return toName;
	}

}
