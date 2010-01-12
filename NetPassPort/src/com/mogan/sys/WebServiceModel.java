package com.mogan.sys;

import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public abstract class WebServiceModel {
	/**
	 * 修正回傳的資料，修正為json或是xml格式
	 * 
	 * @param responseResult
	 * @param responseMsg
	 * @param responseData
	 * @param responseTime
	 * @param returnType
	 * @return
	 */
	final public String getReturnData(String responseResult, String responseMsg,
			String responseRecords, String responseData, String responseTime,
			String returnType) {

		StringBuffer stringBuffer = new StringBuffer();
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("responseResult", responseResult);
		jsonResponse.put("responseMsg", responseMsg);
		jsonResponse.put("responseData", responseData);
		jsonResponse.put("responseTime", responseTime);
		jsonResponse.put("responseRecords", responseRecords);
		jsonResponse.put("id", "jsonResponse");
		if (returnType.equals("JSON")) {
			stringBuffer.append(jsonResponse.toString());
		} else {
			XMLSerializer xs = new XMLSerializer();
			XStream xStream = new XStream(new DomDriver());
			stringBuffer.append(xs.write(jsonResponse));
		}

		return stringBuffer.toString();
	}

	/**
	 * 檢查Appid 是否被接受,回傳APP_ID是否開放使用，無對應APP_ID回傳false
	 * 
	 * @param appId
	 * @return
	 */
	final public boolean checkAppId(String appId) {
		return SysKernel.checkAppId(appId);
	}
}
