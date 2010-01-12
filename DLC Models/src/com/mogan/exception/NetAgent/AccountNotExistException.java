package com.mogan.exception.NetAgent;

import com.mogan.exception.MoganException;

/**
 * 帳號不存在專用
 * @author Dian
 *
 */
public class AccountNotExistException extends MoganException {
	public AccountNotExistException(){
		super();
	}
	public AccountNotExistException(String msg){
		super(msg);
	}
}
