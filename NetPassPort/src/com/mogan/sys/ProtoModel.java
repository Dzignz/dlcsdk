package com.mogan.sys;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public abstract class ProtoModel extends HttpServlet {
	private Properties p;
	private String appId = "";
	private String act = "";
	private ServletContext servletContext;
	private String modelName="";
	private String modelClass="";
	private String modelDiscription="";
	private String sessionId="";
	/**白名單**/
	private List<String> acceptIds;
	/**黑名單**/
	private List<String> denyIds;
	public ProtoModel(){
		
	}
	
	/**
	 * 將傳入的AppId與黑名單跟
	 * @param appId
	 * @return
	 */
	final public boolean verifyAppId(String appId){
		boolean result=true;
		if(acceptIds!=null){
			result=false;
			for (String s:acceptIds){
				if (s.equals(appId)){
					result=true;
				}
			}
			return result;
		}
		
		if(denyIds!=null){
			result=true;
			for (String s:denyIds){
				if (s.equals(appId)){
					result=false;
				}
			}
			return result;
		}
		
		return result;
	}
	
	/**
	 * 更新AcceptIDs,如傳入null或是空的ArrayList則不更換
	 * @param ids
	 */
	final protected void setAcceptIds(ArrayList ids){
		if (ids==null || ids.size()==0){
			
		}else{
			acceptIds=ids;
		}
	}
	
	/**
	 * 更新DenyIDs,如傳入null或是空的ArrayList則不更換
	 * @param ids
	 */
	final protected void setDenyIds(ArrayList ids){
		if (ids==null || ids.size()==0){
			
		}else{
			denyIds=ids;
		}
	}
	
	/**
	 * @param p the p to set
	 */
	final public void setProperties(Properties p) {
		this.p = p;
	}

	/**
	 * @return the p
	 */
	final public Properties getProperties() {
		return p;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	final public String getProperty(String key) {
		return this.getProperties().getProperty(key);
	}
	
	/**
	 * 回傳JSONArray 型態的Properties
	 * @return
	 */
	final public JSONArray getPropertiesWithJSONArray() {
		Iterator it = p.keySet().iterator();
		JSONArray jArray = new JSONArray();
		JSONObject jObj = new JSONObject();
		for (; it.hasNext();) {
			String key = (String) it.next();
			jObj.put(key, p.get(key));
		}
		jArray.add(jObj);
		return jArray;
	}
	
	final public void saveProperties(String name,String classPath,String description,Properties p){
		ModelManager mm=(ModelManager) this.getModelServletContext().getAttribute("ModelManager");
		mm.setModel(name, classPath,description, p);
		mm.saveModels();
	}
	

	
	/**
	 * 取得指定的動作
	 * @return
	 */
	final public String getAct() {
		// TODO Auto-generated method stub
		return this.act;
	}

	/**
	 * 設定要執行的動作
	 * @param act
	 */
	final public void setAct(String act) {
		// TODO Auto-generated method stub
		this.act = act;
	}
	
	/**
	 * 設定共用變數區，servletContext
	 * @param servletContext
	 */
	final public void setModelServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		this.servletContext = servletContext;
	}
	
	final public ServletContext getModelServletContext() {
		// TODO Auto-generated method stub
		return this.servletContext;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelClass(String modelClass) {
		this.modelClass = modelClass;
	}

	public String getModelClass() {
		return modelClass;
	}

	public void setModelDiscription(String modelDiscription) {
		this.modelDiscription = modelDiscription;
	}

	public String getModelDiscription() {
		return modelDiscription;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppId() {
		return appId;
	}
}
