package com.mogan.serviceProtal;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Cookie;
import org.dom4j.Document;
import org.dom4j.Element;

import com.mogan.sys.ModelFace;
import com.mogan.sys.ModelManager;
import com.mogan.sys.ProtoModel;
import com.mogan.sys.ServiceModelFace;
import com.mogan.sys.SysKernel;
import com.mogan.sys.SysLogger4j;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

/**
 * Servlet implementation class AjaxPortal
 */
public class AjaxPortal extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String responseResult = "";
	private String responseMsg = "";
	private String responseData = "";
	private String responseTime = "na";
	private String responseRecord = "";
	private long time0;
	private long time1;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AjaxPortal() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		time0 = System.currentTimeMillis();
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/xml; charset=UTF-8");
		res.setHeader("Cache-Control", "no-cache");
		responseResult = "failure";
		responseMsg = "";
		responseData = "";
		responseTime = "";
		responseRecord = "";
		Map tempMap = req.getParameterMap();

		SysLogger4j.error("ParameterMap::" + tempMap.size());
		Iterator it = tempMap.keySet().iterator();
		int i = 0;
		for (; it.hasNext();) {
			String key = (String) it.next();
			SysLogger4j.error("params#::" + i + " " + key);
			i++;
		}
		String act = "";
		String returnType = req.getParameter("RETURN_TYPE");

		if (returnType == null) {
			returnType = "";
		}

		if (!SysKernel.checkAppId(req, res)) {
			// 非正確 APP ID，無法進行動作
			SysLogger4j.fatal("[INFO] APP ID 未通過驗證.(AjaxPortal 發出)");
			responseData = "";
			responseRecord = "0";
			responseMsg = "Wrong APP ID not Verified.";
		} else if (req.getParameter("ACTION")==null) {
			responseData = "";
			responseRecord = "0";
			responseMsg = "ACTION not find.";			
		}else{
			loadModel(req, res);
		}

		time1 = System.currentTimeMillis();
		responseTime = String.valueOf(time1 - time0);
		PrintWriter out = res.getWriter();
		StringBuffer stringBuffer = new StringBuffer();

		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("responseData", responseData);
		jsonResponse.put("responseRecords", responseRecord);
		jsonResponse.put("responseTime", responseTime);
		jsonResponse.put("responseResult", responseResult);
		jsonResponse.put("responseMsg", responseMsg);
		jsonResponse.put("id", "jsonResponse");
		if (returnType.equals("JSON")) {
			stringBuffer.append(jsonResponse.toString());
		} else {
			XMLSerializer xs = new XMLSerializer();
			stringBuffer.append(xs.write(jsonResponse));
		}
		out.println(stringBuffer);
		out.flush();
		out.close();
		SysLogger4j.info("[INFO] ajax return ::" + stringBuffer);
	}

	/**
	 * 讀取模組資料
	 * 
	 * @param req
	 * @param res
	 */
	private void loadModel(HttpServletRequest req, HttpServletResponse res) {
		
		String modelName = req.getParameter("MODEL_NAME");
		ModelManager modelManager = new ModelManager();
		ProtoModel serviceModel = modelManager.getServiceModel(modelName);
		
		String act = req.getParameter("ACTION").toUpperCase();
		String appId = req.getParameter("APP_ID").toUpperCase();
		if (serviceModel != null) {
			JSONArray jArray;
			try {
				serviceModel.setProperties(modelManager.getModelProperties(modelName));
				serviceModel.setSessionId(req.getSession().getId());
				serviceModel.setAppId(appId);
				serviceModel.setAct(act);
				serviceModel.setModelServletContext(this.getServletContext());
				Map tempParams = req.getParameterMap();
				Map params=new HashMap();
				Set set = tempParams.entrySet();
				Iterator iter = set.iterator();
				while (iter.hasNext()) {
					Entry n = (Entry) iter.next();
					params.put(n.getKey().toString(), req.getParameter(n.getKey().toString()));
				}
				jArray = ((ServiceModelFace) serviceModel).doAction(params);
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
		System.out.println("[INFO] AjaxPortal loadModel End.");
	}
}
