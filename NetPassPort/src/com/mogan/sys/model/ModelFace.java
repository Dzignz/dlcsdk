package com.mogan.sys.model;

import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

public interface ModelFace {
	
	public JSONArray doAction(HttpServletRequest req, HttpServletResponse res) throws Exception;
	public JSONArray doFile(HttpServletRequest req, HttpServletResponse res,List fileItems);
	public void setAct(String act);
	public String getAct();
	public void setServletContext(ServletContext servletContext);
	public ServletContext getServletContext();
	public void setProperties(Properties p);
}
