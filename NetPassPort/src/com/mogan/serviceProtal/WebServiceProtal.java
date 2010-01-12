package com.mogan.serviceProtal;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import com.mogan.sys.ModelManager;
import com.mogan.sys.ProtoModel;
import com.mogan.sys.ServiceModelFace;
import com.mogan.sys.SysKernel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Servlet implementation class WebServiceProtal
 */
public class WebServiceProtal extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static ServletContext servletContext = null;

	private String responseData = "";
	private String responseRecord = "";
	private String responseTime = "";
	private String responseResult = "";
	private String responseMsg = "";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public WebServiceProtal() {
		super();
		
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		System.out.println("[Info] WebServiceProtal init....Start");
		
		this.servletContext = this.getServletContext();
		System.out.println("[Info] WebServiceProtal init....Finish");
	}

	public String callService(String appId, String modelName, String action,
			String args, String returnType) {
		System.out.println("[DEBUG] webService appId::" + appId);
		System.out.println("[DEBUG] webService modelName::" + modelName);
		System.out.println("[DEBUG] webService action::" + action);
		System.out.println("[DEBUG] webService test::" + args);
		System.out.println("[DEBUG] webService returnType::" + returnType);
		if (!SysKernel.checkAppId(appId)) {
			action = "NON";// 非正確 APP ID，無法進行動作
			System.out.println("[訊息] APP ID 未通過驗證.(AjaxPortal 發出)");
		}
		long time0 = System.currentTimeMillis();

		if (action.equals("NON")) {
			responseData = "";
			responseRecord = "0";
			responseMsg = "APP ID not Verified";
		} else {
			loadModel(appId,modelName, action, args);
		}
		long time1 = System.currentTimeMillis();
		responseTime = String.valueOf(time1 - time0);
		StringBuffer stringBuffer = new StringBuffer();
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("responseData", responseData);
		jsonResponse.put("responseRecords", responseRecord);
		jsonResponse.put("responseTime", responseTime);
		jsonResponse.put("responseResult", responseResult);
		jsonResponse.put("responseMsg", responseMsg);
		jsonResponse.put("id", returnType + "Response");
		if (returnType.equals("JSON")) {
			stringBuffer.append(jsonResponse.toString());
		} else {
			XMLSerializer xs = new XMLSerializer();
			XStream xStream = new XStream(new DomDriver());
			stringBuffer.append(xs.write(jsonResponse));
		}
		System.out.println("[Info]callService..."+stringBuffer);
		return stringBuffer.toString();
	}

	/**
	 * 讀取模組資料
	 * 
	 * @param req
	 * @param res
	 */
	private void loadModel(String appId,String modelName, String action, String args) {
		URL url1;
		ModelManager modelManager = new ModelManager();
		ProtoModel serviceModel = modelManager.getServiceModel(modelName);

		
		Map argsMap = parseArgs(args);
		if (serviceModel != null) {
			JSONArray jArray;
			try {
				serviceModel.setProperties(modelManager
						.getModelProperties(modelName));
				serviceModel.setAct(action);
				serviceModel.setAppId(appId);
				serviceModel.setModelServletContext(this.servletContext);
				jArray = ((ServiceModelFace) serviceModel).doAction(argsMap);
				responseData = jArray.toString();
				responseRecord = jArray.size() + "";
				responseResult = "scuess";
				responseMsg = "scuess";
			} catch (Exception e) {
				e.printStackTrace();
				responseResult = "failure";
				responseMsg = e.getMessage();
				responseRecord = "0";
			}finally{
				serviceModel=null;
			}
		} else {
			responseResult = "failure";
			responseMsg = "Model Name (" + modelName + ") not found.";
			responseRecord = "0";
		}
	}

	/**
	 * 解析傳入參數
	 * @param args
	 * @return
	 */
	private Map parseArgs(String args) {
		JSONObject jObj=new JSONObject().fromObject(args);
		Iterator it=jObj.keys();
		Map argsMap = new HashMap();
		for (;it.hasNext();){
			String key=(String) it.next();
			argsMap.put(key,jObj.get(key));
		}
		return argsMap;
	}

}
