package com.mogan.serviceProtal;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.mogan.exception.schedule.ScheduleIncorrectDateSpecException;
import com.mogan.sys.SysAlert;
import com.mogan.sys.SysCalendar;
import com.mogan.sys.model.ModelManager;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ScheduleModelAdapter;

/**
 * Servlet implementation class Schedule2
 */
public class ScheduleProtal extends HttpServlet {
	private static Logger logger = Logger.getLogger( ScheduleProtal.class.getName());
	private static final long serialVersionUID = 1L;
	final private static String APP_ID = "APP_ID";

	final private static String SCHEDULE_NAME = "scheduleName";

	final private static String INTERVAL = "interval";
	final private static String LOOP = "loop";
	final private static String LOAD_ON_STARTUP = "load-on-startup";

	// final private static String APP_ID="APP_ID";

	// private static Timer timer = new Timer();

	private static Map scheduleMap = new HashMap();

	private static ModelManager modelManager = new ModelManager();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ScheduleProtal() {
		super();

	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// TODO Auto-generated constructor stub
		 try {
			initSchedule();
		} catch (ScheduleIncorrectDateSpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化排程任務
	 * 
	 * @return
	 * @throws ScheduleIncorrectDateSpecException 
	 * @throws ParseException 
	 */
	public String initSchedule() throws ScheduleIncorrectDateSpecException, ParseException {

		List<Element> nodes = modelManager.getScheduleModels();
		for (int i = 0; i < nodes.size(); i++) {
			Element e = nodes.get(i);
			String scheduleName = e.elementText(SCHEDULE_NAME);
			
			Properties p = modelManager.getScheduleProperties(scheduleName);
			logger.debug("[DEBUG] initSchedule ::"
					+ e.elementText(SCHEDULE_NAME)+"::"+Boolean.valueOf(e.elementText(LOAD_ON_STARTUP)));
			if (Boolean.valueOf(e.elementText(LOAD_ON_STARTUP))){
				startSchedule(scheduleName,this.getInitParameter(APP_ID),this.getServletContext());
			}	
		}
		return "";
	}

	/**
	 * 回取排程執行狀況，0=未執行，1執行中
	 * @param scheduleName
	 * @return
	 */
	static public int getScheduleStatus(String scheduleName){
		int statusCode=0;
		if (scheduleMap.get(scheduleName) != null) {
			statusCode=1;
		}else{
			
		}
		return statusCode;
	} 
	
	/**
	 * 修正指定執行時間，解析指定時間格式，取得下次執行時間
	 * 除了年，其他都可以指定下次執行時間
	 * @param dateStr
	 * @return
	 * @throws ScheduleIncorrectDateSpecException 時間格式不正確
	 * @throws Exception 
	 */
	static private String getFixDateStr(String dateStr,String nowDateStr) throws ScheduleIncorrectDateSpecException  {
		String [] dateStrList=dateStr.split("\\s");
		String [] nowDateStrList=nowDateStr.split("\\s");
		StringBuffer fixDateStr=new StringBuffer();
		/**如果已超過目前時間下個單位必需+1
		 * 如 一點要 轉為兩點
		 * */
		boolean isOver=false;
		
		for (int i=dateStrList.length-1;i>=0;i--){
			if (dateStrList[i].matches("^\\d{1,2}$")){
				int num=Integer.parseInt(dateStrList[i]);
				int nowNum=Integer.parseInt(nowDateStrList[i]);
				fixDateStr.insert(0,dateStrList[i]+" ");
				if (num<nowNum){
					isOver=true;
				}else{
					isOver=false;
				}
			}else if (dateStrList[i].matches("^\\*$")){
				if (isOver){
					fixDateStr.insert(0,(Integer.parseInt(nowDateStrList[i])+1)+" ");
					isOver=false;
				}else{
					fixDateStr.insert(0,nowDateStrList[i]+" ");
				}
			}else{
				throw new ScheduleIncorrectDateSpecException("時間格式錯誤，["+dateStr+"]");
			}	
		}
		return fixDateStr.toString();
	}
	
	/**
	 * 執行Schedule工作
	 * 
	 * @param scheduleName
	 * @param appid
	 * @param sc
	 * @throws ScheduleIncorrectDateSpecException
	 * @throws ParseException
	 */
	static public void startSchedule(String scheduleName, String appid,
			ServletContext sc) throws ScheduleIncorrectDateSpecException, ParseException  {
		startSchedule(scheduleName,appid,sc,0);
	}
	
	/**
	 * 
	 * @param scheduleName
	 * @param appid
	 * @param sc
	 * @param delay 
	 * @throws ScheduleIncorrectDateSpecException
	 * @throws ParseException
	 */
	static public void startSchedule(String scheduleName, String appid,
			ServletContext sc,long delay) throws ScheduleIncorrectDateSpecException, ParseException  {
		ScheduleModelAdapter scheduleModel = (ScheduleModelAdapter) modelManager
				.getScheduleModel(scheduleName);
		logger.info("Schedule 啟動開始::"+scheduleName);
		Timer timer;
		if (scheduleMap.get(scheduleName) != null) {
			timer = (Timer) scheduleMap.get(scheduleName);
		} else {
			timer = new Timer();
		}
		
		scheduleModel.setAppId(appid);
		scheduleModel.setModelServletContext(sc);

		
		Date assignDate=null;
		if (scheduleModel.getRunTimeSpec().length()>0) {
			//是否有指定執行時間
			SysCalendar sysCal=new SysCalendar();
			String rtSpec=getFixDateStr(scheduleModel.getRunTimeSpec(),sysCal.getFormatDate(sysCal.yyyy_MM_dd_HH_mm_forSchedule));
			sysCal.setStr2Date(rtSpec, sysCal.yyyy_MM_dd_HH_mm_forSchedule);
			sysCal.addSecond((int) (delay/1000));
			assignDate = sysCal.getDate();
			scheduleModel.setNextExeDate(assignDate);
		}

		if (assignDate == null) {
			// 不指定執行時間，立馬執行
			if (scheduleModel.getInterval() == -1) {
				// 無間隔時間，只執行一次
				timer.schedule(scheduleModel, delay);
			} else {
				// 依間隔時執行
				timer.schedule(scheduleModel, delay,scheduleModel.getInterval());
			}
		} else {
			// 指定執行時間
			if (scheduleModel.getInterval() == -1) {
				// 無間隔時間，只執行一次
				timer.schedule(scheduleModel, assignDate);
			} else {
				// 依間隔時執行
				timer.schedule(scheduleModel, assignDate, scheduleModel
						.getInterval());
			}
		}
		logger.info("[DEBUG] Schedule 啟動時間::"+new Date());
		scheduleModel.setStartScheduleDate(new Date());
		scheduleMap.put(scheduleName, timer);
		logger.info("[DEBUG] Schedule 啟動完成::"+scheduleName + " assignDate:"+assignDate);
	}

	/**
	 * 更新排程相關資訊
	 */
	static private void updateScheduleStatus(ScheduleModelAdapter scheduleModel){
		
	}
	
	/**
	 * 停止Schedule工作
	 * 
	 * @param timer
	 */
	static public void stopSchedule(String scheduleName) {
		int i = (int)(Math.random()*100)+1;
		if (scheduleMap.get(scheduleName) != null) {
			ScheduleModelAdapter.resetStatus(scheduleName);
			Timer timer = (Timer) scheduleMap.get(scheduleName);
			timer.cancel();
			scheduleMap.remove(scheduleName);
		}
	}

	/**
	 * 重新啟動排程
	 * @param scheduleName
	 * @param appid
	 * @param sc
	 * @throws ScheduleIncorrectDateSpecException 
	 * @throws ParseException 
	 */
	static public void restartSchedule(String scheduleName, String appid,
			ServletContext sc) throws ScheduleIncorrectDateSpecException, ParseException {
		//scheduleName
		logger.info("重啟排程...");
		List<Element> nodes=modelManager.getScheduleModels(scheduleName);
		long delay=Long.parseLong(nodes.get(0).elementText("interval"));
		if (nodes.get(0).elementText("interval").length()==0 && nodes.get(0).elementText("set-run-time-spec").length()==0){
			logger.info("失敗.(排程無 interval 及 set-run-time-spec 設定)");
			return;
		}
		stopSchedule(scheduleName);
		startSchedule(scheduleName, appid, sc,delay*1000*60);
		logger.info("重啟排程...成功");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			initSchedule();
		} catch (ScheduleIncorrectDateSpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
