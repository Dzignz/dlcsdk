package com.mogan.entity;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import com.mogan.exception.entity.EntityNotExistException;
import com.mogan.log.MoganLogger;
import com.mogan.sys.DBConn;
import com.mogan.sys.SysKernel;
import com.mogan.sys.model.ProtoModel;

public abstract class EntityService extends ProtoModel {
	private static final long serialVersionUID = -5784358140034639555L;
	MoganLogger mLogger;
	DBConn conn;
	String CONN_ALIAS;
	String userName;
	String userIp;

	private JSONObject mainObj;

	public EntityService() {
		this.conn = SysKernel.getConn();
		mLogger = new MoganLogger(this.conn);
		CONN_ALIAS =(String) SysKernel.getApplicationAttr("MAIN_DB");
		//CONN_ALIAS = (String) this.getModelServletContext().getAttribute("MAIN_DB");
	};
	
	public EntityService(ServletContext servletContext, HttpSession session) {
		this.setModelServletContext(servletContext);
		this.setSession(session);
		this.conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
		mLogger = new MoganLogger(this.conn);
		CONN_ALIAS = (String) this.getModelServletContext().getAttribute("MAIN_DB");
	};

	/**
	 * 複製一個entity,但會是一個新的ID
	 */
	public abstract EntityService cloneEty()
			throws UnsupportedEncodingException, SQLException,
			EntityNotExistException;

	/**
	 * 建立一個新的entity
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	protected abstract String create() throws UnsupportedEncodingException,
			SQLException;

	/**
	 * 設定主檔指定屬性的資料
	 * 
	 * @param attributeName
	 *            屬性名稱
	 * @param value
	 *            資料
	 */
	public void setAttribute(String attributeName, Object value) {
		this.mainObj.put(attributeName, value);
	}

	/**
	 * 取得主檔指定屬性的資料
	 * 
	 * @param attributeName
	 *            屬性名稱
	 * @return
	 */
	public Object getAttribute(String attributeName) {
		return this.mainObj.get(attributeName);
	}

	/**
	 * 讀取資料
	 * 
	 * @throws EntityNotExistException
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	abstract void refreashData() throws EntityNotExistException,
			UnsupportedEncodingException, SQLException;

	/**
	 * 儲存資料，在log留下msg
	 */
	abstract public void saveEntity(String msg)
			throws UnsupportedEncodingException, SQLException;

	/**
	 * 儲存資料
	 */
	public void saveEntity() throws UnsupportedEncodingException, SQLException {
		saveEntity("");
	}

	/**
	 * 刪除資料，在log留下msg
	 */
	abstract public void delEntity(String msg)
			throws UnsupportedEncodingException, SQLException;

	/**
	 * 刪除資料
	 */
	public void delEntity() throws UnsupportedEncodingException, SQLException {
		delEntity("");
	}

	/**
	 * 取得開啟資料的使用者帳號
	 * 
	 * @return 使用者帳號
	 */
	public String getOpenUserName() {
		if (this.getSession() == null) {
			return "MoganSweet";
		} else {
			return (String) this.getSession().getAttribute("USER_NAME");
		}
	}

	/**
	 * 取得開啟資料的使用者ID
	 * 
	 * @return 系統ID
	 */
	public String getOpenUser() {
		if (this.getSession() == null) {
			return "SM-20100518-01";
		}else{
			return (String) this.getSession().getAttribute("USER_ID");
		}
	}

	/**
	 * 取得開啟資料的使用者IP
	 * 
	 * @return
	 */
	public String getOpenUserIP() {
		if (this.getSession() == null) {
			InetAddress localHost;
			try {
				localHost = InetAddress.getLocalHost();
				return localHost.getHostAddress();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return "";
		}else{
		return (String) this.getSession().getAttribute("CLIENT_IP");
		}
	}

	/**
	 * @param mainObj
	 *            the mainObj to set
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
