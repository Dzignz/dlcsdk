package com.mogan.sys;

import java.text.DateFormat;
import java.text.ParseException;
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
		if (dateFormat==null){
			dateFormat=setDateFormat(dateFormat,this.yyyy_MM_dd);
		}
		return dateFormat.format(calendar.getTime());
	}
	
	public String getFormatDate(Object obj) {
		return dateFormat.format(obj);
	}
	
	/**
	 * 傳入日期字串，轉換為取得符合格式的日期字串
	 * @param dateStr
	 * @return
	 * @throws ParseException 
	 */
	static public String getFormatDate(String dateStr,String formatString ) throws ParseException {
		DateFormat dateFormat=new SimpleDateFormat(formatString);
		dateFormat=setDateFormat(dateFormat,formatString);
		return  dateFormat.format(dateFormat.parse(dateStr));
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
