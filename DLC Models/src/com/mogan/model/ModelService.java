package com.mogan.model;

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

import com.mogan.serviceProtal.Schedule;
import com.mogan.sys.ModelFace;
import com.mogan.sys.ModelManager;
import com.mogan.sys.ProtoModel;
import com.mogan.sys.ServiceModelFace;
import com.mogan.sys.model.ScheduleModelAdapter;
import com.mogan.sys.model.ScheduleModelFace;

public class ModelService extends ProtoModel implements ServiceModelFace {
	private static ModelManager modelManager = new ModelManager();

	@Override
	public JSONArray doAction(Map parameterMap) {
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
		Schedule.stopSchedule(scheduleName);
		return jArray;
	}
	
	/**
	 * 
	 * @param scheduleName
	 * @return
	 */
	private JSONArray startSchedule(String scheduleName){
		JSONArray jArray = new JSONArray();
		Schedule.startSchedule(scheduleName,this.getAppId(),this.getModelServletContext());
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
			jObj.put("modelName", e.elementText("scheduleName"));
			jObj.put("modelClass", e.elementText("scheduleClass"));
			jObj.put("modelDescription", e.elementText("scheduleDiscription"));
			jObj.put("creator", e.elementText("creator"));
			jObj.put("category", "SCHEDULE");
			jObj.put("create_Date", e.elementText("create_Date"));
			ScheduleModelAdapter scheduleModel = (ScheduleModelAdapter) modelManager
					.getScheduleModel(e.elementText("scheduleName"));
			jObj.put("status", Schedule.getScheduleStatus(e
					.elementText("scheduleName")));
			if ( scheduleModel.getExecuteDate()!=null){
				
				jObj.put("execute_date", scheduleModel.getExecuteDate().toString());
			}
			jArray.add(jObj);
		}

		return jArray;
	}

}
