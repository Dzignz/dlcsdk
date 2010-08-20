package com.mogan.schedule;

import org.apache.log4j.Logger;

import com.data.migration.MemberData;
import com.mogan.sys.SysCalendar;
import com.mogan.sys.model.ScheduleModelAdapter;

/**
 * 資料整合排程
 * @author Dian
 *
 */
public class MigrationTask extends ScheduleModelAdapter {
	private static Logger logger = Logger.getLogger(MigrationTask.class.getName());
	public void exeSchedule(){
		SysCalendar sysCal=new SysCalendar();
		long l0=System.currentTimeMillis();
		logger.info(" MigrationTask Start ");
		MemberData md=new MemberData(this.getModelServletContext(), this.getAppId(), sysCal.getFormatDate(sysCal.yyyy_MM_dd_HH_mm_forSchedule));
		md.startMigr();
		md.doMigr();
		logger.info("MigrationTask member time : "+(System.currentTimeMillis()-l0));
	}
}
