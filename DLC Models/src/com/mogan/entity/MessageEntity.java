/**
 * 
 */
package com.mogan.entity;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.mogan.exception.entity.EntityNotExistException;

/**
 * @author Dian
 *
 */
public class MessageEntity extends EntityService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MessageEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public MessageEntity(String msgId) {
		super();
		// TODO Auto-generated constructor stub
		
	}

	public void sendMsg(){
		
	}
	
	/* (non-Javadoc)
	 * @see com.mogan.entity.EntityService#cloneEty()
	 */
	@Override
	@Deprecated
	public EntityService cloneEty() throws UnsupportedEncodingException,
			SQLException, EntityNotExistException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mogan.entity.EntityService#create()
	 */
	@Override
	@Deprecated
	protected String create() throws UnsupportedEncodingException, SQLException {
		// TODO Auto-generated method stub
		return null;
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
	void refreashData() throws EntityNotExistException,
			UnsupportedEncodingException, SQLException {
		// TODO Auto-generated method stub

	}

	/**
	 * 取消儲存功能，訊息送出不能修改
	 */
	/* (non-Javadoc)
	 * @see com.mogan.entity.EntityService#saveEntity(java.lang.String)
	 */
	@Override
	@Deprecated
	public void saveEntity(String msg) throws UnsupportedEncodingException,
			SQLException {
		// TODO Auto-generated method stub

	}

}
