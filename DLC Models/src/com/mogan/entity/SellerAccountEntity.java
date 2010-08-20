/**
 * 
 */
package com.mogan.entity;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import com.mogan.exception.entity.EntityNotExistException;
import com.mogan.sys.SysTool;

/**
 * @author Dian
 *
 */
public class SellerAccountEntity extends EntityService {
	private String id;
	static public Map payTypeMap= new HashMap();
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9030626151163175852L;

	public SellerAccountEntity(ServletContext servletContext,
			HttpSession session,String id) throws EntityNotExistException {
		super(servletContext, session);
		// TODO Auto-generated constructor stub
		this.id=id;
		this.refreashData();
	}

	/* (non-Javadoc)
	 * @see com.mogan.entity.EntityService#delEntity(java.lang.String)
	 */
	@Override
	public void delEntity(String msg) throws UnsupportedEncodingException,
			SQLException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.mogan.entity.EntityService#refreashData()
	 */
	@Override
	void refreashData() throws EntityNotExistException {
		// TODO Auto-generated method stub
		payTypeMap=SysTool.JSONArray2Map(conn.queryJSONArray(this.CONN_ALIAS, "SELECT list_key,list_name FROM system_list_value WHERE group_key='pay type' "), "list_key", "list_name");
	}

	/* (non-Javadoc)
	 * @see com.mogan.entity.EntityService#saveEntity(java.lang.String)
	 */
	@Override
	public void saveEntity(String msg) throws UnsupportedEncodingException,
			SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	protected String create() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public EntityService cloneEty() throws UnsupportedEncodingException,
			SQLException, EntityNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

}
