package com.mogan.sys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class SysKernel
 */
public class SysKernel extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static ServletContext servletContext;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SysKernel() {
        super();
        // TODO Auto-generated constructor stub
    }

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		servletContext = this.getServletContext();
	}
    
	/**
	 * 檢查Appid 是否被接受,回傳APP_ID是否開放使用，無對應APP_ID回傳false
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	static public boolean checkAppId(HttpServletRequest req, HttpServletResponse res) {
		String appId = req.getParameter("APP_ID");
		return checkAppId(appId);
	}
	
	/**
	 * 檢查Appid 是否被接受,回傳APP_ID是否開放使用，無對應APP_ID回傳false
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	static public boolean checkAppId(String appId) {
		Map appIdMap = (Map) servletContext.getAttribute("APP_ID");
	
		Boolean flag = Boolean.parseBoolean((String) appIdMap.get(appId));
		if (flag != null) {
			return flag;
		}
		return false;
	}	

	static public JSONArray arrayLIst2JSONArray(ArrayList arrayList){
		JSONArray jArray = new JSONArray();
		String key;
		JSONObject jObj = new JSONObject();
		for (int i=0;i<arrayList.size();i++){
			Map tempMap=(Map) arrayList.get(i);
			jObj = new JSONObject();
			jObj.putAll(tempMap);
			jArray.add(jObj);
		}
		return jArray;
	}
}
