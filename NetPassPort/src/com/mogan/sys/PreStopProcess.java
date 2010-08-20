package com.mogan.sys;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

public class PreStopProcess extends HttpServlet implements
		ServletContextListener {
	static private Logger logger=Logger.getLogger(PreStopProcess.class.getName());
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("[INFO] SERVER STOP");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		logger.info("[INFO] SERVER START");
	}

}
