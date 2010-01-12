package com.mogan.sys.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

import javax.servlet.ServletContext;

import net.sf.json.JSONArray;

import com.mogan.exception.schedule.ScheduleNonInitException;
import com.mogan.serviceProtal.Schedule;
import com.mogan.sys.ModelManager;
import com.mogan.sys.ProtoModel;
import com.mogan.sys.ServiceModelFace;

/**
 * 透過排程模組，執行固定程序
 * 
 * @author Dian
 */
public abstract class ScheduleModelAdapter extends TimerTask implements ScheduleModelFace {
	private Properties p =new Properties();
	private String appId = "";
	private String act = "";
	private ServletContext modelServletContext=null;
	private String modelName="";
	private String modelClass="";
	private String modelDiscription="";
	private String sessionId="";
	private static long intervalMinute;
	private static boolean loop;
	private static boolean LOS;
	protected Date executeDate;
	private static Map<String,Date> executeDateMap=new HashMap<String,Date>();
	
	/**
	 * 自動執行initModel與runModel
	 * 
	 * @see #initSchedule
	 * @see #runSchedule
	 */
	@Override
	public void run() {
		System.out.println("[INFO] SCHEDULE::"+this.getModelName()+" Start.");
		setExecuteDate(new Date());
	}
	
	/** 設定重覆執行間隔時間 */
	@Override
	public void setInterval(Object intervalMinute){
		//以分鐘為單位
		this.intervalMinute=Long.parseLong(intervalMinute.toString())*1000*60;
	}
	/** 取得重覆執行間隔時間 */
	@Override
	public long getInterval(){
		return this.intervalMinute;
	}
	
	/** 設定是否重覆執行 */
	@Override
	public void setLoop(Object loop){
		this.loop=Boolean.parseBoolean(loop.toString());
	}
	/** 是否重覆執行 */
	@Override
	public boolean isLoop(){
		return this.loop;
	}
	
	/** 設定是否自動執行 */
	@Override
	public void setLOS(Object LOS){
		this.LOS=Boolean.parseBoolean(LOS.toString());;
	}
	/** 是否自動執行 */
	@Override
	public boolean isLOS(){
		return this.LOS;
	}
	
	
	
	@Override
	public String getAppId() {
		// TODO Auto-generated method stub
		return this.appId;
	}
	
	@Override
	public String getModelClass() {
		// TODO Auto-generated method stub
		return this.modelClass;
	}
	@Override
	public String getModelDiscription() {
		// TODO Auto-generated method stub
		return this.modelDiscription;
	}


	@Override
	public String getModelName() {
		// TODO Auto-generated method stub
		return this.modelName;
	}


	@Override
	public ServletContext getModelServletContext() {
		// TODO Auto-generated method stub
		return this.modelServletContext;
	}


	@Override
	public Properties getProperties() {
		// TODO Auto-generated method stub
		return this.p;
	}


	@Override
	public String getProperty(String key) {
		// TODO Auto-generated method stub
		return this.p.getProperty(key);
	}


	@Override
	public String getSessionId() {
		// TODO Auto-generated method stub
		return this.sessionId;
	}


	@Override
	public void saveProperties(String name, String classPath,
			String description, Properties p) {
		// TODO Auto-generated method stub
		ModelManager mm=(ModelManager) this.getModelServletContext().getAttribute("ModelManager");
		mm.setModel(name, classPath,description, p);
		mm.saveModels();
	}


	@Override
	public void setAppId(String appId) {
		// TODO Auto-generated method stub
		this.appId=appId;
	}


	@Override
	public void setModelClass(String modelClass) {
		// TODO Auto-generated method stub
		this.modelClass=modelClass;
	}


	@Override
	public void setModelDiscription(String modelDiscription) {
		// TODO Auto-generated method stub
		this.modelDiscription=modelDiscription;
	}


	@Override
	public void setModelName(String modelName) {
		// TODO Auto-generated method stub
		this.modelName=modelName;
	}


	@Override
	public void setModelServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		this.modelServletContext=servletContext;
	}


	@Override
	public void setProperties(Properties p) {
		// TODO Auto-generated method stub
		this.p=p;
	}


	@Override
	public void setSessionId(String sessionId) {
		// TODO Auto-generated method stub
		this.sessionId=sessionId;
	}

	@Override
	public Date getExecuteDate() {
		// TODO Auto-generated method stub
		return ScheduleModelAdapter.executeDateMap.get(this.getModelName());
	}

	@Override
	public void setExecuteDate(Date executeDate) {
		// TODO Auto-generated method stub
		ScheduleModelAdapter.executeDateMap.put(this.getModelName(), executeDate);
	}


}
