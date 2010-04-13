/**
 * 
 */
package com.mogan.schedule;

import java.text.ParseException;
import java.util.Date;

import com.mogan.exception.schedule.ScheduleIncorrectDateSpecException;
import com.mogan.serviceProtal.ScheduleProtal;
import com.mogan.sys.model.ScheduleModelAdapter;

/**
 * @author user
 *
 */
public class ScheduleTest extends ScheduleModelAdapter {
	public void exeSchedule(){
		System.out.println("[DEBUG] ScheduleTest--1"+this.getRemainTime());
		System.out.println("[DEBUG] ScheduleTest--2"+new Date());
	}
}
