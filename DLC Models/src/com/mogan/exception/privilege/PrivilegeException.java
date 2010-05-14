package com.mogan.exception.privilege;

import com.mogan.exception.MoganException;

public class PrivilegeException extends MoganException {
	public PrivilegeException(){
		super();
	}
	public PrivilegeException(String msg){
		super(msg);
	}
}
