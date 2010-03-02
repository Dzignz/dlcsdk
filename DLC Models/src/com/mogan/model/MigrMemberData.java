package com.mogan.model;

import java.util.Map;

import net.sf.json.JSONArray;

import com.data.migration.MemberData;
import com.mogan.model.netAgent.NetAgentYJ;
import com.mogan.sys.ProtoModel;
import com.mogan.sys.ServiceModelFace;
import com.mogan.sys.SysCalendar;

public class MigrMemberData extends ProtoModel implements ServiceModelFace{

	/**
	 * 開始Migration 會員資料
	 */
	private JSONArray startMigr(){
		 MemberData md=new  MemberData(this.getModelServletContext(),
					this.getAppId(),new SysCalendar().getFormatDate());
		 md.startMigr();
		 md.doMigr();
		 JSONArray jArray=new JSONArray();
		 jArray.add(md.getDataCount());
		return jArray;
	}
	
	/**
	 * 停止整合會員資料 
	 */
	private JSONArray stopMigr(){
		
		return null;
	}
	
	@Override
	public JSONArray doAction(Map parameterMap) throws Exception {
		// TODO Auto-generated method stub
		JSONArray jArray=new JSONArray();
		if (this.getAct().equals("START")) {
			jArray=startMigr();
		}else if (this.getAct().equals("STOP")){
			
		}
		return jArray;
	}

}
