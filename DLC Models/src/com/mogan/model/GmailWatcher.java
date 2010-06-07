package com.mogan.model;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Map;

import javax.mail.NoSuchProviderException;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.model.netAgent.NetAgentGoogle;
import com.mogan.model.netAgent.NetAgentYJ;
import com.mogan.sys.DBConn;
import com.mogan.sys.log.SysLogger4j;
import com.mogan.sys.mail.MailSenderInfo;
import com.mogan.sys.mail.SimpleMailSender;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ScheduleModelAdapter;
import com.mogan.sys.model.ServiceModelFace;


/**
 * igoogle 小工具用，隨時取得gmail讀信狀況
 * @author Dian
 *
 */
public class GmailWatcher extends ProtoModel implements ServiceModelFace{
	static private Logger logger  =  Logger.getLogger(GmailWatcher.class.getName());
	@Override
	public JSONArray doAction(Map<String, String> arg0) throws Exception {
		JSONArray jArray= new JSONArray();
		SysLogger4j.info("GmailWatcher"+this.getAct());
		if (this.getAct().equals("GET_INFO")){
			jArray=getGmailStatusInfo();
		}else if (this.getAct().equals("LOGIN_TEST")){
			logger.info("LOGIN_TEST!!");
			loginTest();
		}
		return jArray;
	}

	private void loginTest() throws NoSuchProviderException{
		
		NetAgentGoogle nag=new NetAgentGoogle("elgoogdian@gmail.com","vfbyfnfvygo");
		logger.info("LOGIN OK!!");
		ArrayList list=nag.getMail("CASE");
		logger.info("mail list!!"+list.size());
		//nag.close();
		Provider [] p=Security.getProviders();
		for (int i=0;i<p.length;i++){
			logger.info("Security OK!!"+p[i].getName());
			
		}
		
		SimpleMailSender sms = new SimpleMailSender();
		logger.info("[DEBUG] sendMail-1");
		String msgContact = "<html><body> 測試信</body></html>";
		sms.setMailSubject("程式寄信");
		sms.setMailContent(msgContact);
		sms.setMailServerHost("mail.mogan.com.tw");
		sms.setMailServerPort("25");
		sms.setAccount("");
		sms.setPwd("");
		sms.setFromName("good post mail");
		sms.setFromAddress("ads@mogan.com.tw");
		ArrayList dataList = new ArrayList();
		logger.info("[DEBUG] sendMail-2. ");
		sms.setTargetList(dataList);
		MailSenderInfo mail = sms.getMailInfo("", "elgoogdian@gmail.com", "","ads@mogan.com.tw");
		boolean results = sms.sendHtmlMail(mail);
		logger.info("[DEBUG] sendMail-3. "+results);
		
		

		
	}
	
	private JSONArray getGmailStatusInfo(){
		JSONArray jArray= new JSONArray();
//		JSONObject jObj= new JSONObject();
		
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
//		jObj.put("LAST_HOUR", conn.queryJSONArray("mogan-DB", "SELECT alert,count(alert) from system_alert WHERE create_date > DATE_SUB(NOW(),INTERVAL 1 HOUR ) GROUP BY alert"));
//		jObj.put("TODAY", conn.queryJSONArray("mogan-DB", "SELECT alert,count(alert) from system_alert WHERE create_date > CURDATE() GROUP BY alert"));
		jArray.add(ScheduleModelAdapter.getLastExecuteDate("GmailTask").toString());
		jArray.add(conn.queryJSONArray("mogan-DB", "SELECT alert,count(alert) as alert_count from system_alert WHERE create_date > DATE_SUB(NOW(),INTERVAL 1 HOUR ) GROUP BY alert"));
		jArray.add(conn.queryJSONArray("mogan-DB", "SELECT alert,count(alert) as alert_count from system_alert WHERE create_date > CURDATE() GROUP BY alert"));
		jArray.add(conn.queryJSONArray("mogan-DB", "SELECT alert,count(alert) as alert_count from system_alert WHERE create_date > DATE_SUB(CURDATE(),INTERVAL 1 DAY )  and create_date < CURDATE()GROUP BY alert"));
		
		return jArray;
	}
}

