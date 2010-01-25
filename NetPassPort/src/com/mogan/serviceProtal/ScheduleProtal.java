package com.mogan.serviceProtal;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Element;

import com.mogan.sys.ModelManager;
import com.mogan.sys.ProtoModel;
import com.mogan.sys.SysAlert;
import com.mogan.sys.model.ScheduleModelAdapter;

/**
 * Servlet implementation class Schedule2
 */
public class ScheduleProtal extends HttpServlet {
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
		// TODO Auto-generated constructor stub
		// initSchedule();
	}

	/**
	 * 初始化排程任務
	 * 
	 * @return
	 */
	public String initSchedule() {

		List<Element> nodes = modelManager.getScheduleModels();
		for (int i = 0; i < nodes.size(); i++) {
			Element e = nodes.get(i);
			String scheduleName = e.elementText(SCHEDULE_NAME);
			
			Properties p = modelManager.getScheduleProperties(scheduleName);
			System.out.println("[DEBUG] initSchedule ::"
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
	 * 執行Schedule工作
	 * 
	 * @param timer
	 * @param modelName
	 * @param ScheduleName
	 */
	static public void startSchedule(String scheduleName, String appid,
			ServletContext sc) {
		ScheduleModelAdapter scheduleModel = (ScheduleModelAdapter) modelManager
				.getScheduleModel(scheduleName);

		Timer timer;
		if (scheduleMap.get(scheduleName) != null) {
			timer = (Timer) scheduleMap.get(scheduleName);
		} else {
			timer = new Timer();
		}

		scheduleModel.setAppId(appid);
		scheduleModel.setModelServletContext(sc);
		if (scheduleModel.isLoop()) {
			timer.schedule(scheduleModel, 0, scheduleModel.getInterval());
		} else {
			timer.schedule(scheduleModel, 0);
		}
		scheduleMap.put(scheduleName, timer);
	}

	/**
	 * 停止Schedule工作
	 * 
	 * @param timer
	 */
	static public void stopSchedule(String scheduleName) {
		System.out.println("[DEBUG] stopSchedule::"+scheduleName);
		if (scheduleMap.get(scheduleName) != null) {
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
	 */
	static private void restartSchedule(String scheduleName, String appid,
			ServletContext sc) {
		stopSchedule(scheduleName);
		startSchedule(scheduleName, appid, sc);
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
		System.out.println("[DEBUG] Schedule2 start ::");
		initSchedule();
		// super.doPost(request, response);
	}

}
