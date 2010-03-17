package com.mogan.exception.schedule;

import com.mogan.exception.MoganException;

public class ScheduleIncorrectDateSpecException extends MoganException{
	public ScheduleIncorrectDateSpecException(){
		super();
	}
	public ScheduleIncorrectDateSpecException(String msg){
		super(msg);
	}
}
