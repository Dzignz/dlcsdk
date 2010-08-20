package com.mogan.model;

import java.text.ParseException;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.sys.DBConn;
import com.mogan.sys.SysCalendar;
import com.mogan.sys.SysKernel;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;

/**
 * 負責查詢資料
 * @author Dian
 *
 */
public class DataFinder extends ProtoModel implements ServiceModelFace{
	static private Logger logger=Logger.getLogger(DataFinder.class.getName());
	@Override
	public JSONArray doAction(Map<String, String> arg0) throws Exception {
		// TODO Auto-generated method stub
		JSONArray jArray=new JSONArray();
		if (this.getAct().equals("WON_DATA")){
			jArray=getWonData(JSONObject.fromObject(arg0.get("DATAS")));
		}
		return jArray;
	}
	
	private JSONArray getWonData(JSONObject conditionObj) throws ParseException{
		JSONArray jArray=new JSONArray();
		DBConn conn=SysKernel.getConn();
		String startDate= conditionObj.getString("START_DATE");
		String endDate= conditionObj.getString("END_DATE");
		int sqlType=0;
		if (startDate!=null && startDate.length()>0){
			sqlType+=1;
		}
		if (endDate!=null && endDate.length()>0){
			sqlType+=3;
		}
		
		String sql="SELECT item_order_id,item_id,bid_id,time_at_04 as won_date FROM view_bid_item_order_v1 ";
		switch (sqlType){
		case 1:
		case 3:
			sql+="WHERE time_at_04 LIKE '"+startDate+"%'";
			break;
		case 4:
			sql+="WHERE time_at_04 BETWEEN  '"+startDate+"' AND '"+endDate+"'";
			break;
		}
		
		logger.info(conditionObj);
		jArray=conn.queryJSONArray((String) SysKernel.getApplicationAttr(SysKernel.MAIN_DB), sql);
		return jArray;
	}
}
