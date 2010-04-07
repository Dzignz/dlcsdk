package com.mogan.sys;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class SysLogger4j {
	static  Logger logger  =  Logger.getLogger(SysLogger4j.class );
	protected SysLogger4j(){
		//PropertyConfigurator.configure("D:\\Server\\tomcat-6.0.14\\lib\\log4j.properties" );  // Second step
	}
	static public void info(Object message){
		logger.info(message);
	}
	
	static public void warn(Object message){
		logger.warn(message);
	}
	
	static public void error(Object message){
		logger.error(message);
	}
	
	static public void fatal(Object message){
		logger.fatal(message);
	}
	static public void debug(Object message){
		logger.debug(message);
	}	
	

}
