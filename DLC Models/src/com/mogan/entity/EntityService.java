package com.mogan.entity;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import com.mogan.exception.entity.EntityNotExistException;
import com.mogan.log.MoganLogger;
import com.mogan.sys.DBConn;
import com.mogan.sys.model.ProtoModel;

public abstract class EntityService extends ProtoModel{
	private static final long serialVersionUID = -5784358140034639555L;
	MoganLogger mLogger ;
	DBConn conn;
	String CONN_ALIAS;
	private JSONObject mainObj;
	public EntityService(ServletContext servletContext,HttpSession session){
		this.setModelServletContext(servletContext);
		this.setSession(session);
		this.conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
		mLogger = new MoganLogger(this.conn);
		CONN_ALIAS=(String)this.getModelServletContext().getAttribute("MAIN_DB");
	};
	
	abstract String create(JSONObject etyObj) ;
	
	/**
	 * 設定主檔指定屬性的資料
	 * @param attributeName 屬性名稱
	 * @param value 資料
	 */
	public void setAttribute(String attributeName,Object value){
		this.mainObj.put(attributeName, value);
	}
	
	/**
	 * 取得主檔指定屬性的資料
	 * @param attributeName 屬性名稱
	 * @return
	 */
	public Object getAttribute(String attributeName){
		return this.mainObj.get(attributeName);
	}
	
	/**
	 * 讀取資料
	 * @throws EntityNotExistException
	 */
	abstract void refreashData() throws EntityNotExistException;
	
	/**
	 * 儲存資料，在log留下msg
	 */
	abstract public void saveEntity(String msg) throws UnsupportedEncodingException, SQLException;
	
	/**
	 * 儲存資料
	 */
	public void saveEntity() throws UnsupportedEncodingException, SQLException{
		saveEntity("");
	}
	
	/**
	 * 刪除資料，在log留下msg
	 */
	abstract public void delEntity(String msg) throws UnsupportedEncodingException, SQLException ;
	
	/**
	 * 刪除資料
	 */
	public void delEntity() throws UnsupportedEncodingException, SQLException{
		delEntity("");
	}
	
	/**
	 * 取得開啟資料的使用者帳號
	 * @return 使用者帳號
	 */
	public String getOpenUserName(){
		return (String) this.getSession().getAttribute("USER_NAME");
	}
	
	/**
	 * 取得開啟資料的使用者ID
	 * @return 系統ID
	 */
	public String getOpenUser(){
		return (String) this.getSession().getAttribute("USER_ID");
	}
	
	/**
	 * 取得開啟資料的使用者IP
	 * @return
	 */
	public String getOpenUserIP(){
		return (String) this.getSession().getAttribute("CLIENT_IP");
	}


	/**
	 * @param mainObj the mainObj to set
	 */
	public void setMainObj(JSONObject mainObj) {
		this.mainObj = mainObj;
	}


	/**
	 * @return the mainObj
	 */
	public JSONObject getMainObj() {
		return mainObj;
	}
}
