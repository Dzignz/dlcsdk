package com.mogan.sys.model;

import java.util.Properties;

import javax.servlet.ServletContext;

import net.sf.json.JSONArray;

public interface ProtoModelFace {
	 	 
	 public void setProperties(Properties p);
	 public Properties getProperties();
	 public String getProperty(String key);
	 public void saveProperties(String name,String classPath,String description,Properties p);
	 public void setModelServletContext(ServletContext servletContext);
	 public ServletContext getModelServletContext();
	 public void setModelName(String modelName);
	 public String getModelName();
	 public void setModelClass(String modelClass);
	 public String getModelClass() ;
	 public void setModelDiscription(String modelDiscription) ;
	 public String getModelDiscription() ;
	 public void setSessionId(String sessionId) ;
	 public String getSessionId() ;
	 public void setAppId(String appId) ;
	 public String getAppId() ;
	 
}
