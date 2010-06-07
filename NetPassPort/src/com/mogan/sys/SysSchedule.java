package com.mogan.sys;

import java.util.Timer;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.log4j.Logger;
import com.mogan.serviceProtal.ScheduleProtal;

/**
 * Servlet implementation class SysSchedule
 */
public class SysSchedule extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static ServletContext servletContext = null;
	static private Logger logger  =  Logger.getLogger(SysSchedule.class.getName());
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SysSchedule() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		initSysSchedule();
		runSchedule();
	}
	
	/**
	 * 執行排程程式，5秒後執行
	 */
	private void runSchedule(){
		Timer timer = new Timer();
		logger.info("init Schedule start.");
        //timer.schedule(new SysAlert(), 0,1000*60*15);
       timer.schedule(new SysAlert(),5000, 1000*60*15);
	}
	
	/**
	 * 初始化Servlet
	 */
	private void initSysSchedule() {
		this.servletContext = this.getServletContext();
	}
}
