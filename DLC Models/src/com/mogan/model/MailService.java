package com.mogan.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.mogan.sys.DBConn;
import com.mogan.sys.SysKernel;
import com.mogan.sys.SysMath;
import com.mogan.sys.mail.MailSenderInfo;
import com.mogan.sys.mail.SimpleMailSender;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;

/**
 * Servlet implementation class MailService
 */

public class MailService extends ProtoModel implements ServiceModelFace {
	private static Logger logger = Logger.getLogger(MailService.class.getName() );
	private static final long serialVersionUID = 1L;
	private final String MAIL_SENDER_THREAD = "MAIL_SENDER_THREAD";
	private final String FROM_NAME = "fromName";
	private final String FROM_ADDRESS = "fromAdderss";
	private final String SERVER_HOST = "serverHost";
	private final String SERVER_PORT = "serverPort";
	private final String ACCOUNT = "account";
	private final String PWD = "pwd";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MailService() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * ACTION = GET_MAIL_GROUP</br>
	 * 取得Mail群組
	 * 
	 * @return
	 */
	private JSONArray getMailGroup() {
		JSONArray jArray = new JSONArray();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		ArrayList dataList = new ArrayList();
		

		Map tempMap=new HashMap();
		tempMap.put("type_name", "全體會員");
		tempMap.put("mail_type_id", "member_data.delete_flag");
		dataList.add(tempMap);
		tempMap=new HashMap();
		tempMap.put("type_name", "廣告信會員");
		tempMap.put("mail_type_id", "member_profile_set.auto_mail");
		dataList.add(tempMap);
		jArray.addAll(dataList);
		logger.info("[DEBUG]dataList::" + dataList.size());
		return jArray;
	}

	/**
	 * ACTION = SEND_MAIL</br>
	 * 信箱位址可用;區分，如a@yahoo.com;b@yahoo.com</br>
	 * 回傳格式為TRUE或FALSE
	 * @param toName[TO_NAME] 收件人名稱
	 * @param toAddress[TO_ADDRESS] 收件人信箱
	 * @param ccAddress[CC_ADDRESS] 副本人信箱
	 * @param bccAddress[BCC_ADDRESS] 密件副本信箱
	 * @param mailSubject[MAIL_SUBJECT] 信件主旨
	 * @param mailContent[MAIL_CONTENT] 信件內容
	 * @return
	 */
	public JSONArray sendMail(String toName, String toAddress,String ccAddress,String bccAddress,
			String mailSubject, String mailContent) {
		long l0=System.currentTimeMillis();
		logger.info("[DEBUG] sendMail-0");
		JSONArray jArray = new JSONArray();

		SimpleMailSender sms = new SimpleMailSender();
		logger.info("[DEBUG] sendMail-1");
		long l1=System.currentTimeMillis();
		sms.setMailSubject(mailSubject);
		sms.setMailContent(mailContent);
		sms.setMailServerHost(this.getProperties()
				.getProperty(this.SERVER_HOST));
		sms.setMailServerPort(this.getProperties()
				.getProperty(this.SERVER_PORT));
		sms.setAccount(this.getProperties().getProperty(this.ACCOUNT));
		sms.setPwd(this.getProperties().getProperty(this.PWD));
		sms.setFromName(this.getProperties().getProperty(this.FROM_NAME));
		sms.setFromAddress(this.getProperties().getProperty(this.FROM_ADDRESS));
		long l2=System.currentTimeMillis();
		ArrayList dataList = new ArrayList();
		logger.info("[DEBUG] sendMail-2");
		sms.setTargetList(dataList);
		/* 寄送mail開始 */
		long l3=System.currentTimeMillis();
		logger.info(") addToAddress(String toAddress)::"+toAddress);	
		MailSenderInfo mail = sms.getMailInfo(toName,toAddress,ccAddress,bccAddress);
		jArray.add(sms.sendHtmlMail(mail));
		logger.info("[DEBUG] sendMail-3");
		long l4=System.currentTimeMillis();
		logger.info("[DEBUG] sendMail time="+(l4-l3)+". "+(l3-l2)+". "+(l2-l1)+". "+(l1-l0));
		sms = null;
		return jArray;
	}

	/**
	 * ACTION = SET_MAIL_THREAD</br>
	 * 初始化Mail寄送程式，包含設定收件人
	 * 
	 * @param sessionId - 執行緒ID 
	 * @param mailSubject - 信件主旨
	 * @param mailContent - 信件內容
	 * @param conditionA - 篩選條件A
	 * @param conditionB - 篩選條件B
	 * 
	 * @return
	 */
	private JSONArray setMailThread(String sessionId, String mailSubject,
			String mailContent, String memberGroup,String mailAddress) {
		logger.info("[DEBUG] setMailThread::");
		JSONArray jArray = new JSONArray();
		JSONObject jObj = new JSONObject();

		JSONArray caJArray = JSONArray.fromObject(memberGroup);
		
		JSONArray maJArray=JSONArray.fromObject(mailAddress);
		ArrayList dataList=new ArrayList();
		if (caJArray.size()>0){
			dataList.addAll( getUserList(caJArray));
		}
		dataList.addAll(maJArray);
		Map threadsMap;
		if (this.getModelServletContext().getAttribute("THEARDS") == null) {
			threadsMap = new HashMap();
		} else {
			threadsMap = (Map) this.getModelServletContext().getAttribute(
					"THEARDS");
		}
		SimpleMailSender sms = new SimpleMailSender();
		sms = new SimpleMailSender();
		sms.setMailSubject(mailSubject);
		sms.setMailContent(mailContent);
		sms.setMailServerHost(this.getProperties()
				.getProperty(this.SERVER_HOST));
		sms.setMailServerPort(this.getProperties()
				.getProperty(this.SERVER_PORT));
		sms.setAccount(this.getProperties().getProperty(this.ACCOUNT));
		sms.setPwd(this.getProperties().getProperty(this.PWD));
		sms.setFromName(this.getProperties().getProperty(this.FROM_NAME));
		sms.setFromAddress(this.getProperties().getProperty(this.FROM_ADDRESS));
		sms.setTargetList(dataList);
		jObj.put("SESSION_ID", sessionId);
		jObj.put("TARGET_LIST", dataList.size());
		threadsMap.put(this.MAIL_SENDER_THREAD + "_" + sessionId, sms);
		SimpleMailSender smsx = (SimpleMailSender) threadsMap
				.get(this.MAIL_SENDER_THREAD + "_" + sessionId);

		jArray.add(jObj);
		this.getModelServletContext().setAttribute("THEARDS", threadsMap);

		return jArray;
	}

	/**
	 * ACTION　＝RUN　PAUSE　STOP<br />
	 * 進行mail發送相關操作。
	 * RUN，執行﹔PAUSE，暫停﹔STOP，停止
	 * @param sessionId　－　執行緒ID
	 * @return
	 */
	private JSONArray setMailThreadAction(String sessionId) {
		JSONArray jArray = new JSONArray();
		long l0=System.currentTimeMillis();
		Map threadsMap;
		if (this.getModelServletContext().getAttribute("THEARDS") == null) {
			threadsMap = new HashMap();
		} else {
			threadsMap = (Map) this.getModelServletContext().getAttribute(
					"THEARDS");
		}
		long l1=System.currentTimeMillis();

		SimpleMailSender sms = (SimpleMailSender) threadsMap
				.get(this.MAIL_SENDER_THREAD + "_" + sessionId);
		long l2=System.currentTimeMillis();
		logger.info("sms::"+sms.getMailServerHost()+" "+sms.getMailServerPort()+" "+sms.getAccount()+" "+sms.getPwd());
		// sms = (SimpleMailSender) threadsMap.get(this.MAIL_SENDER_THREAD + "_" + sessionId);

		if (this.getAct().equals("RUN")) {
			switch (sms.getStatus()) {
			case 0:
				// 未執行
				sms.run();
				break;
			default:
				// 執行過
				sms.unpauseMe();
				break;
			}
			jArray = getMailStatus(sessionId);
		} else if (this.getAct().equals("PAUSE")) {
			sms.pauseMe();
			jArray = getMailStatus(sessionId);
		} else if (this.getAct().equals("STOP")) {
			sms.stopMe();
			jArray = getMailStatus(sessionId);
			sms = null;
			threadsMap.remove(this.MAIL_SENDER_THREAD + "_" + sessionId);
		}
		long l3=System.currentTimeMillis();
		
		logger.info("setMailThreadAction 3-2="+(l3-l2));
		logger.info("setMailThreadAction 2-1="+(l2-l1));
		logger.info("setMailThreadAction 1-0="+(l1-l0));
		
		return jArray;
	}

	/**
	 * ACTION = GET_MAIL_STATUS</br>
	 * 取得Mail 目前寄送狀況
	 * 
	 * @param sessionId - 執行緒ID
	 * @return
	 */
	private JSONArray getMailStatus(String sessionId) {
		JSONArray jArray = new JSONArray();
		JSONObject jObj = new JSONObject();
		Map threadsMap;
		if (this.getModelServletContext().getAttribute("THEARDS") == null) {
			threadsMap = new HashMap();
		} else {
			threadsMap = (Map) this.getModelServletContext().getAttribute("THEARDS");
		}
		SimpleMailSender sms = (SimpleMailSender) threadsMap
				.get(this.MAIL_SENDER_THREAD + "_" + sessionId);
		jObj.put("TOTAL", sms.getTargetList().size());
		jObj.put("STATUS", sms.getStatus());
		jObj.put("CURRENT_SEQ", sms.getSendSeq());
		double d = SysMath.div(sms.getSendSeq(), sms.getTargetList()
				.size(), 4);
		d = d * 100;
		d=SysMath.div(d, 1, 4);
		jObj.put("PERCENTAGE", d);
		jObj.put("MSG", sms.getMessagesWithDel());
		jArray.add(jObj);
		return jArray;
	}

	/**
	 * 取得發送目標清單
	 * 
	 * @param conditionAJSONArray
	 * @param conditionBJSONArray
	 * @return
	 */
	private ArrayList getUserList(JSONArray memberGroup) {
		StringBuffer whereSql=new StringBuffer();
		logger.info("getUserList memberGroup::" + memberGroup);
		for (int i=0;i<memberGroup.size();i++){
			String groupType=memberGroup.getString(i);
			if (whereSql.length()>0)
			whereSql.append(" OR ");
			whereSql.append(groupType+" ='1' ");
		}
		if (whereSql.length()>0)
			whereSql.append(" AND ");
			whereSql.append(" member_data.delete_flag <> '0' ");
		String sql = "SELECT " +
				" member_data.email " +
				" FROM " +
				" member_data " +
				" LEFT Join member_profile_set ON member_data.member_id = member_profile_set.member_id " +
				" WHERE " +whereSql;
				//" member_profile_set.auto_mail =  '1'";
		logger.info("getUserList sql::" + sql);
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		ArrayList dataList = conn.query((String) SysKernel.getApplicationAttr(SysKernel.MAIN_DB), sql);

		return dataList;
	}

	public JSONArray saveProperties(String fromName, String fromAddress,
			String serverHost, String serverPort, String account, String pwd) {
		JSONArray jArray = new JSONArray();
		this.getProperties().put("fromName", fromName);
		this.getProperties().put("fromAdderss", fromAddress);
		this.getProperties().put("serverHost", serverHost);
		this.getProperties().put("serverPort", serverPort);
		this.getProperties().put("account", account);
		this.getProperties().put("pwd", pwd);

		super.saveProperties(this.getModelName(), this.getModelClass(), this
				.getModelDiscription(), this.getProperties());

		jArray.add(true);
		return jArray;
	}

	/**
	 * ACTION = SEARCH_MAIL_TEMPLET<br />
	 * 依ID及信件主旨(檔名)搜尋範本
	 * @param searchKey - 搜尋關鍵字
	 * @return
	 */
	private JSONArray searchMailTemplet(String searchKey) {
		JSONArray jArray = new JSONArray();
		String sql = "SELECT subject,id,CONCAT(CONCAT(subject,'-'),id) as dspValue FROM web_mail where id like '%"
				+ searchKey + "%' or subject like '%" + searchKey + "%'";
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		ArrayList dataList = conn.query("mogan-tw", sql);
		jArray = SysKernel.arrayLIst2JSONArray(dataList);
		return jArray;
	}

	/**
	 * ACTION = LOAD_MAIL_TEMPLET_LIST<br />
	 * 取讀信件範本列表
	 * 
	 * @param startIndex - 開始項目
	 * @param pageSize - 取回數量
	 * @param searchKey - 搜尋關鍵字
	 * @param shortField - 搜尋欄位
	 * @return
	 */
	private JSONArray loadMailTempletList(int startIndex, int pageSize,
			String searchKey, String shortField) {
		JSONArray jArray = new JSONArray();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String sql = "SELECT id,mail_class_id,subject,create_date,creator FROM web_mail ";

		if (searchKey != null && searchKey.length() > 0) {
			// 是否增加條件
			sql += " WHERE id like '" + searchKey + "' or mail_class_id like '"
					+ searchKey + "' or subject like '%" + searchKey + "%' ";
		}

		if (shortField != null && shortField.length() > 0) {
			// 是否要排序
			sql += " ORDER BY " + shortField + "";
		}

		ArrayList dataList = conn.query("mogan-tw", sql);
		for (int i = 0; i < dataList.size(); i++) {
			// Map tempMap=(Map) dataList.get(i);
			logger.info(dataList.get(i));
			jArray.add(JSONObject.fromObject(dataList.get(i)));
		}
		return jArray;
	}

	@Override
	public JSONArray doAction(Map parameterMap) throws Exception {
		// TODO Auto-generated method stub
		JSONArray jArray = new JSONArray();

		if (this.getAct().equals("GET_MAIL_GROUP")) {
			jArray = getMailGroup();
		} else if (this.getAct().equals("SEND_MAIL")) {
			String toName = (String) parameterMap.get("TO_NAME");
			String toAddress = (String) parameterMap.get("TO_ADDRESS");
			String ccAddress = (String) parameterMap.get("CC_ADDRESS");
			String bccAddress = (String) parameterMap.get("BCC_ADDRESS");
			String mailSubject = (String) parameterMap.get("MAIL_SUBJECT");
			String mailContent = (String) parameterMap.get("MAIL_CONTENT");
			logger.info("[DEBUG] MailService doAction");
			jArray=sendMail(toName, toAddress,ccAddress,bccAddress, mailSubject, mailContent);
			logger.info("[DEBUG]");
		} else if (this.getAct().equals("LOAD_PROPERTIES")) {
			jArray = this.getPropertiesWithJSONArray();
		} else if (this.getAct().equals("SAVE_PROPERTIES")) {
			String fromName = (String) parameterMap.get("FROM_NAME");
			String fromAddress = (String) parameterMap.get("FROM_ADDRESS");
			String serverHost = (String) parameterMap.get("SERVER_HOST");
			String serverPort = (String) parameterMap.get("SERVER_PORT");
			String account = (String) parameterMap.get("ACCOUNT");
			String pwd = (String) parameterMap.get("PWD");
			jArray = this.saveProperties(fromName, fromAddress, serverHost,
					serverPort, account, pwd);
		} else if (this.getAct().equals("SET_MAIL_THREAD")) {
			String mailSubject = (String) parameterMap.get("MAIL_SUBJECT");
			String mailcontent = (String) parameterMap.get("MAIL_CONTENT");
			String mailAddress = (String) parameterMap.get("MAIL_ADDRESS");// 群組條件
			String memberGroup = (String) parameterMap.get("MEMBER_GROUP");// 群組條件
			jArray = setMailThread(this.getSessionId(), mailSubject,
					mailcontent, memberGroup,mailAddress);
		} else if (this.getAct().equals("GET_MAIL_STATUS")) {
			String sessionId = (String) parameterMap.get("SESSION_ID");
			jArray = getMailStatus(sessionId);
		} else if (this.getAct().equals("LOAD_MAIL_TEMPLET_LIST")) {
			/* 讀取信件範本列表 */
			String searchKey = (String) parameterMap.get("SEARCH_KEY");
			String shortField = (String) parameterMap.get("SHORT_FIELD");
			int startIndex = Integer.parseInt((String) parameterMap
					.get("START_INDEX"));
			int pageSize = Integer.parseInt((String) parameterMap
					.get("PAGE_SIZE"));
			jArray = loadMailTempletList(startIndex, pageSize, searchKey,
					shortField);
		} else if (this.getAct().equals("SEARCH_MAIL_TEMPLET")) {
			String searchKey = (String) parameterMap.get("SEARCH_KEY");
			jArray = searchMailTemplet(searchKey);
		} else if (this.getAct().equals("RUN") || this.getAct().equals("PAUSE")
				|| this.getAct().equals("STOP")) {
			String sessionId = (String) parameterMap.get("SESSION_ID");
			jArray = setMailThreadAction(sessionId);
		}

		return jArray;
	}

}
