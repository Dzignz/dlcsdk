package com.mogan.exception.schedule;

import com.mogan.exception.MoganException;

public class ScheduleNonInitException extends MoganException {
	public ScheduleNonInitException(){
		super();
	}
	public ScheduleNonInitException(String msg){
		super(msg);
	}
}
