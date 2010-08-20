package com.mogan.sys.model;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;

import com.mogan.exception.schedule.ScheduleIncorrectDateSpecException;
import com.mogan.exception.schedule.ScheduleNonInitException;
import com.mogan.serviceProtal.ScheduleProtal;
import com.mogan.sys.SysCalendar;

/**
 * 透過排程模組，執行固定程序
 * 
 * @author Dian
 */
public abstract class ScheduleModelAdapter extends TimerTask implements ScheduleModelFace {
	private static Logger logger=Logger.getLogger(ScheduleModelAdapter.class.getName());
	private Properties p =new Properties();
	private String appId = "";
	private String act = "";
	private ServletContext modelServletContext=null;
	private String modelName="";
	private String modelClass="";
	private String modelDiscription="";
	private String sessionId="";
	private String runTimeSpec="";
	
	private static long intervalMinute=-1;
	private static boolean loop=false;
	private static boolean LOS;
	
	//protected Date executeDate;
	/**排程啟動時間*/
	private Date startExeDate;
	private Date lastExeDate;
	private Date nextExeDate;
	private Integer remainTime=-1;
	private int exeTime=0;
	private static Map<String,Map> scheduleStatus=new HashMap<String,Map>();
	private static Map<String,Date> executeDateMap=new HashMap<String,Date>();
	
	
	/**
	 * 自動執行initModel與runModel
	 * 
	 * @see #initSchedule
	 * @see #runSchedule
	 */
	@Override
	final public void run() {
		logger.info("[INFO] SCHEDULE::"+this.getModelName()+" Start."+this.getRemainTime());

		Date d=new Date();
		this.setLastExecuteDate(d);
		
		exeSchedule();
		exeTime++;
		logger.info("[INFO] SCHEDULE::"+this.getModelName()+" end."+this.isLoop());
		if (!this.isLoop()) {
			//是否重覆執行，重覆執行就不自動中斷
			setRemainTime(this.getRemainTime() - 1);
			logger.info("[INFO] SCHEDULE::"+this.getModelName()+" getRemainTime."+(this.getRemainTime() <= 0));
			if (this.getRemainTime() <= 0) {
				resetStatus(this.getModelName());
				ScheduleProtal.stopSchedule(this.getModelName());
					try {
						ScheduleProtal.restartSchedule(this.getModelName(),
								this.getAppId(), this.getModelServletContext());
					} catch (ScheduleIncorrectDateSpecException e) {
						e.printStackTrace();
						logger.error(e.getMessage(),e);
					} catch (ParseException e) {
						e.printStackTrace();
						logger.error(e.getMessage(),e);
					}
			}
		}
		SysCalendar sysCal=new SysCalendar();
		sysCal.addMilliSecond((int) this.getInterval());
		this.setNextExeDate(sysCal.getDate());
		
	}
	
	/**
	 * 取得已執行次數
	 */
	public int getExeTime(){
		return exeTime;
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
	

	
	/** 設定是否指定第一次執行時間 */
	@Override
	public void setRunTimeSpec(String dateStr){
		runTimeSpec=dateStr;
	}
	
	/** 取得第一次執行時間 */
	@Override
	public String getRunTimeSpec(){
		return runTimeSpec;
	}
	
	/**
	 * 設定剩餘次數
	 */
	public void setRemainTime(int i){
		Map tempScheduleStatus=getMyStatus(this.getModelName());
		remainTime=i;
		tempScheduleStatus.put("remain_time", remainTime);
	}
	
	/**
	 * 取得剩餘次數
	 */
	public Integer getRemainTime(){
		return getRemainTime(this.getModelName());
	}

	/**
	 * 取得剩餘次數
	 * @param modelName
	 * @return
	 */
	static public Integer  getRemainTime(String modelName){
		Map tempScheduleStatus=getMyStatus(modelName);
		return (Integer) tempScheduleStatus.get("remain_time");
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
		return this.appId;
	}
	
	@Override
	public String getModelClass() {
		return this.modelClass;
	}
	@Override
	public String getModelDiscription() {
		return this.modelDiscription;
	}


	@Override
	public String getModelName() {
		return this.modelName;
	}


	@Override
	public ServletContext getModelServletContext() {
		return this.modelServletContext;
	}


	@Override
	public Properties getProperties() {
		return this.p;
	}


	@Override
	public String getProperty(String key) {
		return this.p.getProperty(key);
	}


	@Override
	public String getSessionId() {
		return this.sessionId;
	}


	@Override
	public void saveProperties(String name, String classPath,
			String description, Properties p) {
		ModelManager mm=(ModelManager) this.getModelServletContext().getAttribute("ModelManager");
		mm.setModel(name, classPath,description, p);
		mm.saveModels();
	}


	@Override
	public void setAppId(String appId) {
		this.appId=appId;
	}


	@Override
	public void setModelClass(String modelClass) {
		this.modelClass=modelClass;
	}


	@Override
	public void setModelDiscription(String modelDiscription) {
		this.modelDiscription=modelDiscription;
	}


	@Override
	public void setModelName(String modelName) {
		this.modelName=modelName;
	}


	@Override
	public void setModelServletContext(ServletContext servletContext) {
		this.modelServletContext=servletContext;
	}


	@Override
	public void setProperties(Properties p) {
		this.p=p;
	}


	@Override
	public void setSessionId(String sessionId) {
		this.sessionId=sessionId;
	}

	@Override
	final public Date getLastExecuteDate() {
		Map tempScheduleStatus=getMyStatus(this.getModelName());
		return (Date) tempScheduleStatus.get("execute_date");
	}
	
	/**
	 * 設定最後執行時間
	 * @param modelName
	 * @return
	 */
	final static public Date getLastExecuteDate(String modelName){
		Map tempScheduleStatus=getMyStatus(modelName);
		return (Date) tempScheduleStatus.get("execute_date");
	}

	
	/**
	 * 設定最後執行時間
	 */
	@Override
	final public void setLastExecuteDate(Date executeDate) {
		this.lastExeDate=executeDate;
		Map tempScheduleStatus=getMyStatus(this.getModelName());
		tempScheduleStatus.put("execute_date", executeDate);
		ScheduleModelAdapter.executeDateMap.put(this.getModelName(), executeDate);
	}
	
	/**
	 * 取得排程啟動時間
	 * @param modelName
	 * @return
	 */
	final static public Date getStartScheduleDate(String modelName){
		Map tempScheduleStatus=getMyStatus(modelName);
		return (Date) tempScheduleStatus.get("start_schedule_date");
	}
	
	/**
	 * 取得排程啟動時間
	 * @return 
	 */
	@Override
	final public Date getStartScheduleDate(){
		return getStartScheduleDate(this.getModelName());
	}
	
	/**
	 * 設定排程啟動時間
	 * @return 
	 */
	@Override
	final public void setStartScheduleDate(Date startDate){
		Map tempScheduleStatus=getMyStatus(this.getModelName());
		tempScheduleStatus.put("start_schedule_date", startDate);
	}
	
	/**
	 * 
	 * @param modelName
	 * @return
	 */
	final static public Date getNextExeDate(String modelName){
		Map tempScheduleStatus=getMyStatus(modelName);
		return (Date) tempScheduleStatus.get("next_exe_date");
	}
	
	/**
	 * 更新預計下次執行時間
	 * @return
	 */
	final public void setNextExeDate(Date nextDate){
		Map tempScheduleStatus=getMyStatus(this.getModelName());
		tempScheduleStatus.put("next_exe_date", nextDate);
	}
	
	/**
	 * 取得下次執行時間
	 */
	@Override
	final public Date  getNextExeDate(){
		return getNextExeDate(this.getModelName());
	}
	
	
	final static private Map getMyStatus(String modelName){
		Map tempScheduleStatus=new HashMap();
		if (scheduleStatus.get(modelName)==null){
			scheduleStatus.put(modelName, new HashMap());
		}
		tempScheduleStatus=scheduleStatus.get(modelName);
		
		return tempScheduleStatus;
	}
	
	
	final public static void resetStatus(String modelName){
		Map tempScheduleStatus=getMyStatus(modelName);
		tempScheduleStatus.remove("next_exe_date");
		tempScheduleStatus.remove("start_schedule_date");
	}
	


}
