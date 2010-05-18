package com.mogan.exception.entity;

import com.mogan.exception.MoganException;

/**
 * 無對應ENTITY 資料
 * @author Dian
 *
 */
public class EntityNotExistException extends MoganException {
	public EntityNotExistException(){
		super();
	}
	public EntityNotExistException(String msg){
		super(msg);
	}
}
