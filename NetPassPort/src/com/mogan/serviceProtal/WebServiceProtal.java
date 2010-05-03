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

import com.mogan.sys.SysAlert;
import com.mogan.sys.SysKernel;
import com.mogan.sys.log.SysLogger4j;
import com.mogan.sys.model.ModelManager;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;
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
		SysLogger4j.info("[Info] WebServiceProtal init....Start");
		this.servletContext = this.getServletContext();
		SysLogger4j.info("[Info] WebServiceProtal init....Finish");
	}

	public String callService(String appId, String modelName, String action,
			String args, String returnType) {

		SysLogger4j.info("webService appId::"+appId);
		SysLogger4j.info("webService modelName::"+modelName);
		SysLogger4j.info("webService action::"+action);
		SysLogger4j.info("webService args::"+args);
		
		if (!SysKernel.checkAppId(appId)) {
			action = "NON";// 非正確 APP ID，無法進行動作
			SysLogger4j.info("webService appId 未通過驗證::"+appId);
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
		SysLogger4j.info("webService Result::"+jsonResponse.getString("responseResult"));
		SysLogger4j.debug("webService back::"+stringBuffer);
		new SysAlert().run();
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
				
				if (!serviceModel.verifyAppId(appId)){
					throw new Exception("AppId 驗證失敗");
				}
				
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
			String key=it.next().toString();
			argsMap.put(key,jObj.get(key));
		}
		return argsMap;
	}

}
