package com.mogan.exception;

public class NonPrivilegeException extends MoganException {
	public NonPrivilegeException(){
		super();
	}
	public NonPrivilegeException(String msg){
		super(msg);
	}
}
