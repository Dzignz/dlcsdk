package com.mogan.schedule;

import com.data.migration.MemberData;
import com.mogan.sys.SysCalendar;
import com.mogan.sys.model.ScheduleModelAdapter;

/**
 * 資料整合排程
 * @author Dian
 *
 */
public class MigrationTask extends ScheduleModelAdapter {
	public void exeSchedule(){
		SysCalendar sysCal=new SysCalendar();
		MemberData md=new MemberData(this.getModelServletContext(), this.getAppId(), sysCal.getFormatDate(sysCal.yyyy_MM_dd_HH_mm_forSchedule));
		md.startMigr();
		md.doMigr();
	}
}
