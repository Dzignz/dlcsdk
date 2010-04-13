package com.mogan.exception.yamoto;

import com.mogan.exception.MoganException;

/**
 * Yamato專用
 * @author Dian
 *
 */
public class YamatoException extends MoganException {
	public YamatoException(){
		super();
	}
	public YamatoException(String msg){
		super(msg);
	}
}
