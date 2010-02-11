/**
 * 
 */
package com.mogan.schedule;

import com.mogan.sys.model.ScheduleModelAdapter;

/**
 * @author user
 *
 */
public class ScheduleTest extends ScheduleModelAdapter {
	public void run(){
		System.out.println("[DEBUG] ScheduleTest--1");
		try {
			Thread.sleep(1000*60*2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("[DEBUG] ScheduleTest--2");
	}
}
