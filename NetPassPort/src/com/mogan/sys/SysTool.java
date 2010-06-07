package com.mogan.sys;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SysTool {
	static public Map JSONArray2Map(JSONArray jArray,String key,String value){
		Map tempMap=new HashMap();
		for (int i=0;i<jArray.size();i++){
			JSONObject jObj=jArray.getJSONObject(i);
			tempMap.put(jObj.get(key), jObj.get(value));
		}
		return tempMap;
	}
	static public JSONObject JSONArray2JSONObject(JSONArray jArray,String key,String value){
		JSONObject tempObj=new JSONObject();
		for (int i=0;i<jArray.size();i++){
			JSONObject jObj=jArray.getJSONObject(i);
			tempObj.put(jObj.get(key), jObj.get(value));
		}
		return tempObj;
	}
}
