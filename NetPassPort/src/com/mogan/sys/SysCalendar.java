package com.mogan.sys;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SysCalendar {
	Calendar calendar = Calendar.getInstance();
	DateFormat dateFormat;
	static final public String MM_dd_yyyy="MM/dd/yyyy";
	static final public String yyyy_MM_dd="yyyy/MM/dd";
	static final public String yyyy_MM_dd_HH_mm_ss_Mysql="yyyy-MM-dd HH:mm:ss";
	
	public SysCalendar(){

	}

	public DateFormat setDateFormat(String formatString) {
		dateFormat = new SimpleDateFormat(formatString);
		return dateFormat;
	}
	
	static public DateFormat setDateFormat(DateFormat dateFormat,String formatString) {
		dateFormat = new SimpleDateFormat(formatString);
		return dateFormat;
	}

	public Date getDate() {
		return calendar.getTime();
	}

	public String getFormatDate() {
		return dateFormat.format(calendar.getTime());
	}
	
	
	public String getFormatDate(Date d) {
		return dateFormat.format(d);
	}
	
	
	static public String getFormatDate(Date d, String formatString) {
		DateFormat dateFormat=new SimpleDateFormat(formatString);
		dateFormat=setDateFormat(dateFormat,formatString);
		return dateFormat.format(d);
	}

	public String getFormatDate(String formatString) {
		DateFormat dateFormat=new SimpleDateFormat(formatString);
		dateFormat=setDateFormat(dateFormat,formatString);
		return getFormatDate();
	}
	
	public void addDay(int day){
		calendar.add(Calendar.DAY_OF_MONTH, day);
	}
	public void addMonth(int month){
		
	}
	public void addYear(int year){
		
	}
}
