package com.mogan.exception;

public abstract class MoganException extends Exception {
	public MoganException(){
		super();
	}
	public MoganException(String msg){
		super(msg);
	}
}
