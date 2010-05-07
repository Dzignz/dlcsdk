package com.mogan.model;

import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.sys.DBConn;
import com.mogan.sys.log.SysLogger4j;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ScheduleModelAdapter;
import com.mogan.sys.model.ServiceModelFace;

/**
 * igoogle 小工具用，隨時取得gmail讀信狀況
 * @author Dian
 *
 */
public class GmailWatcher extends ProtoModel implements ServiceModelFace{

	@Override
	public JSONArray doAction(Map<String, String> arg0) throws Exception {
		JSONArray jArray= new JSONArray();
		SysLogger4j.info("GmailWatcher"+this.getAct());
		if (this.getAct().equals("GET_INFO")){
			
			jArray=getGmailStatusInfo();
		}
		return jArray;
	}

	private JSONArray getGmailStatusInfo(){
		JSONArray jArray= new JSONArray();
//		JSONObject jObj= new JSONObject();
		
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
//		jObj.put("LAST_HOUR", conn.queryJSONArray("mogan-DB", "SELECT alert,count(alert) from system_alert WHERE create_date > DATE_SUB(NOW(),INTERVAL 1 HOUR ) GROUP BY alert"));
//		jObj.put("TODAY", conn.queryJSONArray("mogan-DB", "SELECT alert,count(alert) from system_alert WHERE create_date > CURDATE() GROUP BY alert"));
		jArray.add(ScheduleModelAdapter.getLastExecuteDate("GmailTask").toString());
		jArray.add(conn.queryJSONArray("mogan-DB", "SELECT alert,count(alert) as alert_count from system_alert WHERE create_date > DATE_SUB(NOW(),INTERVAL 1 HOUR ) GROUP BY alert"));
		jArray.add(conn.queryJSONArray("mogan-DB", "SELECT alert,count(alert) as alert_count from system_alert WHERE create_date > CURDATE() GROUP BY alert"));
		jArray.add(conn.queryJSONArray("mogan-DB", "SELECT alert,count(alert) as alert_count from system_alert WHERE create_date > DATE_SUB(CURDATE(),INTERVAL 1 DAY )  and create_date < CURDATE()GROUP BY alert"));
		
		return jArray;
	}
}

