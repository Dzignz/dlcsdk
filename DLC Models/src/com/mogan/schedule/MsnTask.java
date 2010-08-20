package com.mogan.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import net.sf.jml.MsnMessenger;
import net.sf.jml.MsnProtocol;
import net.sf.jml.MsnUserStatus;
import net.sf.jml.impl.MsnMessengerFactory;
import net.sf.json.JSONArray;

import com.mogan.model.MsnBot;
import com.mogan.model.MsnBotEx;
import com.mogan.sys.DBConn;

import com.mogan.sys.model.ModelManager;
import com.mogan.sys.model.ScheduleModelAdapter;

/**
 * @author Dian
 */
public class MsnTask extends ScheduleModelAdapter {
	private static Logger logger = Logger.getLogger(MsnTask.class.getName() );
	
	static ModelManager modelManager = new ModelManager();
//	static Map botMap=new HashMap();
	
	@Override
	public void exeSchedule() {
		sysLogin();//檢查登入狀態
		try {
			Thread.sleep(1000*30);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sysLogin();//檢查登入狀態
		//檢查商品超標狀態
		
		logger.info("[INFO] SCHEDULE MsnTask RUN.");
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
			MsnBotEx msnBotEx;
			if (botMap.get("MSN_" + accountMap.get("email"))!=null){
				logger.info(botMap);
				logger.info(" msn "+(botMap.get("MSN_" + accountMap.get("email")) instanceof MsnBotEx));
				msnBotEx=(MsnBotEx) botMap.get("MSN_" + accountMap.get("email"));	
			}else{
				msnBotEx=new MsnBotEx();
			}
			
			if (msnBotEx.getMsnStatus()!= MsnUserStatus.OFFLINE){
				//在線上就跳過
				logger.info(accountMap.get("email")+" already login.");
				continue;
			}
			
			msnBotEx.login((String) accountMap.get("email"),
					(String) accountMap.get("pwd"));
			botMap.put("MSN_" + accountMap.get("email"), msnBotEx);
		}
		setMsnBotMap(botMap);
	}
	
	/**
	 * @return
	 */
	public Map<String,MsnBotEx> getMsnBotMap() {
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
	
	/**
	 * 指定帳號密碼登入
	 * 
	 * @param account
	 * @param pwd
	 */
	private MsnMessenger login(String account, String pwd) {
		// create MsnMessenger instance
		logger.info("MsnBot login." + account);
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
		//initMessenger(messenger);
		messenger.login();

		return messenger;

	}

}
