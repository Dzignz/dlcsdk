package com.mogan.model;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.exception.schedule.ScheduleIncorrectDateSpecException;
import com.mogan.serviceProtal.ScheduleProtal;
import com.mogan.serviceProtal.ScheduleProtal;
import com.mogan.sys.ModelFace;
import com.mogan.sys.ModelManager;
import com.mogan.sys.ProtoModel;
import com.mogan.sys.ServiceModelFace;
import com.mogan.sys.model.ScheduleModelAdapter;
import com.mogan.sys.model.ScheduleModelFace;

public class ModelService extends ProtoModel implements ServiceModelFace {
	private static ModelManager modelManager = new ModelManager();

	@Override
	public JSONArray doAction(Map parameterMap) throws Exception{
		// TODO Auto-generated method stub
		JSONArray jArray = new JSONArray();
		
		if (this.getAct().equals("LOAD_MODEL_DATA")) {
			jArray = this.loadModelData();
		} else if (this.getAct().equals("RELOAD_MODELS")) {
			jArray = this.reloadModels();
		}else if (this.getAct().equals("START_SCHEDULE")){
			String scheduleName=(String) parameterMap.get("SCHEDULE_NAME");
			jArray = this.startSchedule(scheduleName);
			jArray = this.loadModelData();
		}else if (this.getAct().equals("STOP_SCHEDULE")){
			String scheduleName=(String) parameterMap.get("SCHEDULE_NAME");
			jArray = this.stopSchedule(scheduleName);
			jArray = this.loadModelData();
		}
		return jArray;
	}

	/**
	 * 
	 * @param scheduleName
	 * @return
	 */
	private JSONArray stopSchedule(String scheduleName){
		JSONArray jArray = new JSONArray();
		ScheduleProtal.stopSchedule(scheduleName);
		return jArray;
	}
	
	/**
	 * 
	 * @param scheduleName
	 * @return
	 * @throws ParseException 
	 * @throws ScheduleIncorrectDateSpecException 
	 */
	private JSONArray startSchedule(String scheduleName) throws ScheduleIncorrectDateSpecException, ParseException{
		JSONArray jArray = new JSONArray();
		ScheduleProtal.startSchedule(scheduleName,this.getAppId(),this.getModelServletContext());
		return jArray;
	}
	
	/**
	 * 重新賣取model設定
	 * 
	 * @return
	 */
	public JSONArray reloadModels() {
		ModelManager mm = new ModelManager();
		mm.loadModels();
		return loadModelData();
	}

	/**
	 * 取回Model各資料
	 * 
	 * @return JSONArray
	 */
	public JSONArray loadModelData() {
		JSONArray jArray = new JSONArray();
		List<Element> nodes = modelManager.getModels();

		for (int i = 0; i < nodes.size(); i++) {
			Element e = nodes.get(i);
			JSONObject jObj = new JSONObject();
			jObj.put("modelName", e.elementText("modelName"));
			jObj.put("modelClass", e.elementText("modelClass"));
			jObj.put("modelDescription", e.elementText("modelDiscription"));
			jObj.put("category", "MODLE");
			jObj.put("status", "-");
			jObj.put("creator", e.elementText("creator"));
			jObj.put("create_Date", e.elementText("create_Date"));
			jArray.add(jObj);
		}


		nodes = modelManager.getScheduleModels();
		for (int i = 0; i < nodes.size(); i++) {
			Element e = nodes.get(i);
			JSONObject jObj = new JSONObject();
			String scheduleName=e.elementText("scheduleName");
			jObj.put("modelName", scheduleName);
			jObj.put("modelClass", e.elementText("scheduleClass"));
			jObj.put("modelDescription", e.elementText("scheduleDiscription"));
			jObj.put("creator", e.elementText("creator"));
			jObj.put("category", "SCHEDULE");
			jObj.put("create_Date", e.elementText("create_Date"));
			
			jObj.put("status", ScheduleProtal.getScheduleStatus(e
					.elementText("scheduleName")));
			if(ScheduleModelAdapter.setLastExecuteDate(scheduleName)!=null){
				jObj.put("last_exe_date", ScheduleModelAdapter.setLastExecuteDate(scheduleName).toString());	
			}
			
			if(ScheduleModelAdapter.getStartScheduleDate(scheduleName)!=null){
				jObj.put("start_schedule_date", ScheduleModelAdapter.getStartScheduleDate(scheduleName).toString());	
			}
			if(ScheduleModelAdapter.getNextExeDate(scheduleName)!=null){
				jObj.put("next_exe_date", ScheduleModelAdapter.getNextExeDate(scheduleName).toString());	
			}
			
			
			//jObj.put("next_execute_date", scheduleModel.getExecuteDate().toString());
			jObj.put("run_time_spec", e.elementText("set-run-time-spec"));
			jObj.put("remain_time", ScheduleModelAdapter.getRemainTime(scheduleName));
			if  (e.elementText("loop")!=null && e.elementText("loop").length()>0){
				jObj.put("remain_time","∞");
			}
			jObj.put("interval", e.elementText("interval"));

			
			jArray.add(jObj);
		}

		return jArray;
	}

}
