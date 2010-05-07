package com.mogan.sys;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.mogan.sys.log.SysLogger4j;

public class SysCalendar {
	Calendar calendar = Calendar.getInstance();
	DateFormat dateFormat;
	static final public String MM_dd_yyyy="MM/dd/yyyy";
	static final public String yyyy_MM_dd="yyyy/MM/dd";
	/**排程用日期格式，以空格隔開
	 * 年 月 日 時 分
	 * */
	static final public String yyyy_MM_dd_HH_mm_forSchedule="yyyy MM dd HH mm";
	static final public String yyyy_MM_dd_HH_mm_ss_Mysql="yyyy-MM-dd HH:mm:ss";
	static final public String yyyy_MM_dd_Mysql="yyyy-MM-dd";
	
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
		if (d==null){
			return null;
		}
		
		DateFormat dateFormat=new SimpleDateFormat(formatString);
		dateFormat=setDateFormat(dateFormat,formatString);
		return dateFormat.format(d);
	}

	public String getFormatDate(String formatString) {
		dateFormat=new SimpleDateFormat(formatString);
		dateFormat=setDateFormat(dateFormat,formatString);
		return getFormatDate();
	}
	
	public void setStr2Date(String dateStr,String formatString) throws ParseException{
		dateFormat=new SimpleDateFormat(formatString);
		dateFormat=setDateFormat(dateFormat,formatString);
		calendar.setTime(dateFormat.parse(dateStr));
	}
	
	/**
	 * 做時間的運算，可以傳入負數的num做減法
	 * @param timeUnit
	 * @param num
	 * 
	 * @see java.util.Calendar
	 */
	public void add(int timeUnit,int num){
		calendar.add(timeUnit, num);
	}
	
	public void addDay(int day){
		calendar.add(Calendar.DAY_OF_MONTH, day);
	}
	public void addMonth(int month){
		calendar.add(Calendar.MONTH, month);
	}
	public void addHour(int hour){
		calendar.add(Calendar.HOUR_OF_DAY, hour);
	}
	public void addMinute(int minute){
		calendar.add(Calendar.MINUTE, minute);
	}
	public void addSecond(int second){
		calendar.add(Calendar.SECOND, second);
	}
	public void addMilliSecond(int milliSecond){
		calendar.add(Calendar.MILLISECOND, milliSecond);
	}
}
