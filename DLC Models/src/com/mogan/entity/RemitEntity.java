/**
 * 
 */
package com.mogan.entity;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.exception.entity.EntityNotExistException;
import com.mogan.sys.DBConn;

/**
 * @author Dian
 */
public class RemitEntity extends EntityService {
	private Logger logger = Logger.getLogger(RemitEntity.class.getName());
	/**
	 * remit_id 付款ID
	 */
	final public static int REMIT_ID = 0;

	/**
	 * tide_id 訂單ID
	 */
	final public static int TIDE_ID = 1;
	private String id;
	private int idType;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3512438691918081278L;

	public RemitEntity(ServletContext servletContext, HttpSession session) throws EntityNotExistException, UnsupportedEncodingException, SQLException {
		super(servletContext, session);
		this.id=create();
		this.idType=RemitEntity.REMIT_ID;
		this.refreashData();
	}
	
	/**
	 * 讀取remit實体，
	 * 
	 * @param servletContext
	 * @param session
	 * @param id
	 *            可傳入多種ID
	 * @param idType
	 *            需告知ID類型
	 * @throws EntityNotExistException
	 */
	public RemitEntity(ServletContext servletContext, HttpSession session,
			String id, int idType) throws EntityNotExistException {
		super(servletContext, session);
		this.id = id;
		this.idType = idType;
		this.refreashData();
	}

	/*
	 * (non-Javadoc)
	 * @see com.mogan.entity.EntityService#delEntity(java.lang.String)
	 */
	@Override
	public void delEntity(String msg) throws UnsupportedEncodingException,
			SQLException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see com.mogan.entity.EntityService#refreashData()
	 */
	@Override
	void refreashData() throws EntityNotExistException {
		JSONArray jArray = new JSONArray();
		switch (this.idType) {
		case 0:
			// remit_id 付款ID
			jArray = conn.queryJSONArray(this.CONN_ALIAS, "SELECT * FROM view_remit_list_v1 WHERE remit_id='"
					+ this.id + "'");
			break;
		case 1:
			// tide_id 訂單ID
			jArray = conn.queryJSONArray(this.CONN_ALIAS, "SELECT * FROM view_remit_list_v1 WHERE remit_id in (SELECT remit_id FROM item_tide WHERE tide_id='"
					+ this.id + "')");
			break;
		}
		if (jArray.size()==1){
			setMainObj(jArray.getJSONObject(0));
		}else if (jArray.size()>1){
			throw new EntityNotExistException("此ID對應多筆資料.("+id+")");
		}else if (jArray.size()==0){
			throw new EntityNotExistException("沒有此ID資料.("+id+")");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.mogan.entity.EntityService#saveEntity(java.lang.String)
	 */
	@Override
	public void saveEntity(String msg) throws UnsupportedEncodingException,
			SQLException {
		Map conditionMap=new HashMap();
		conditionMap.put("remit_id", this.getAttribute("remit_id"));
		conn.update(this.CONN_ALIAS, "remit_list", conditionMap, this.getMainObj());
		logger.debug("付款資料 修改. "+this.getAttribute("remit_id"));
	}

	/**
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	@Override
	protected String create() throws UnsupportedEncodingException, SQLException {
		Map etyObj=new HashMap();
		String id = conn.getAutoNumber(CONN_ALIAS, "RL-ID-01");
		
		etyObj.put("remit_id", id);
		etyObj.put("create_date", new Date());
		etyObj.put("delete_flag", "1");
		etyObj.put("remit_classify", "RL-901");
		etyObj.put("creator", (String) this.getSession().getAttribute("USER_ID"));
		conn.newData(CONN_ALIAS, "remit_list", etyObj);
		return id;
	}


	@Override
	public EntityService cloneEty() throws UnsupportedEncodingException,
			SQLException, EntityNotExistException {
		// TODO Auto-generated method stub
		return null;
	}



}
