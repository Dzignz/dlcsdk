package com.mogan.serviceProtal;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import org.apache.tomcat.util.http.fileupload.DiskFileUpload;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;

import com.mogan.sys.FileModelFace;
import com.mogan.sys.ModelFace;
import com.mogan.sys.ModelManager;
import com.mogan.sys.ProtoModel;
import com.mogan.sys.ServiceModelFace;
import com.mogan.sys.SysKernel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Servlet implementation class FilePortal
 */
public class FilePortal extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String responseResult = "";
	private String responseMsg = "";
	private String responseData = "";
	private String responseTime = "na";
	private String responseRecord = "";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FilePortal() {
		super();
		System.out.println("[DEBUG]FilePortal start.");
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().println("[DEBUG]FilePortal start.");
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=UTF-8");
		res.setHeader("Cache-Control", "no-cache");

		System.out.println("[DEBUG]FilePortal start.");
		long time0 = System.currentTimeMillis();
		DiskFileUpload fu = new DiskFileUpload();
		List fileItems = null;
		responseResult = "failure";
		responseMsg = "";
		responseData = "";
		responseTime = "";
		responseRecord = "";
		String returnType = "XML";
		String act = null;
		String appId=null;
		String modelName = null;
		boolean isUploadFile = true;
			System.out.println("[DEBUG]FilePortal ."+req.getContentType());
		try {
			/* 上傳檔案 */
			if (req.getContentType().indexOf("multipart/form-data") > -1) {
				isUploadFile = true;
				fileItems = fu.parseRequest(req);
				Iterator i = fileItems.iterator();
				int index = 0;
				act = null;
				modelName = null;
				while (i.hasNext()) {
					FileItem fi = (FileItem) i.next();
					//System.out.println("[DEBUG] fi.getFieldName()"+fi.getFieldName()+" fi.getName()"+fi.getName()+" fi.getString()"+fi.getString());
					if (fi.getFieldName().equals("ACTION")) {
						act = fi.getString();
					} else if (fi.getFieldName().equals("APP_ID")) {
						appId = fi.getString();
					} else if (fi.getFieldName().equals("MODEL_NAME")) {
						modelName = fi.getString();
					} else if (fi.getFieldName().equals("RETURN_TYPE")) {
						returnType = fi.getString();
					}
				}
				/* 一般POST資料 */
			} else if (req.getContentType().indexOf("application") > -1) {
				act = req.getParameter("ACTION").toUpperCase();
				appId = req.getParameter("APP_ID").toUpperCase();
				returnType = req.getParameter("RETURN_TYPE").toUpperCase();
				modelName = req.getParameter("MODEL_NAME");
				isUploadFile = false;
			}

			if (act.equals("NON")) {
				responseData = "";
				responseRecord = "0";
				responseMsg = "APP ID not Verified";
			} else {
				loadModel(req, res, fileItems, modelName, act,appId, isUploadFile);
			}

		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long time1 = System.currentTimeMillis();
		StringBuffer stringBuffer = new StringBuffer();
		String jsonString = "{success:true}";
		JSONObject jsonResponse = new JSONObject().fromObject(jsonString);
		JSONObject jsonResponse2 = new JSONObject().fromObject(jsonString);
		jsonResponse.put("responseData", responseData);
		jsonResponse.put("responseRecords", responseRecord);
		jsonResponse.put("responseTime", responseTime);
		jsonResponse.put("responseResult", responseResult);
		jsonResponse.put("responseMsg", responseMsg);
		jsonResponse.put("success", true);
		jsonResponse.put("id", "jsonResponse");
		if (returnType.equals("JSON")) {
			stringBuffer.append(jsonResponse.toString());
		} else {
			XMLSerializer xs = new XMLSerializer();
			XStream xStream = new XStream(new DomDriver());
			stringBuffer.append(xs.write(jsonResponse));
		}
		PrintWriter out = res.getWriter();
		String s = stringBuffer.toString();
		s = s.replaceAll("\\/", "/");
		out.println(s);
		out.flush();
		out.close();
	}

	/**
	 * 讀取模組資料
	 * 
	 * @param req
	 * @param res
	 */
	private void loadModel(HttpServletRequest req, HttpServletResponse res,
			List fileItems, String modelName, String act,String appId, boolean isUploadFile) {
		JSONArray jArray;

		ModelManager modelManager = new ModelManager();
		
		ProtoModel fileModel = modelManager.getFileModel(modelName);
		fileModel.setProperties(modelManager.getModelProperties(modelName));

		fileModel.setAppId(appId);
		fileModel.setAct(act);
		fileModel.setSessionId(req.getSession().getId());
		fileModel.setModelServletContext(this.getServletContext());
		if (fileModel != null) {
			try {
				if (isUploadFile) {
					jArray = ((FileModelFace)fileModel).doFile(req, res, fileItems);
				} else {
					Map tempParams = req.getParameterMap();
					Map params=new HashMap();
					Set set = tempParams.entrySet();
					Iterator iter = set.iterator();
					while (iter.hasNext()) {
						Entry n = (Entry) iter.next();
						params.put(n.getKey().toString(), req.getParameter(n.getKey().toString()));
					}
					jArray = ((ServiceModelFace) fileModel).doAction(params);
				}
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
				fileModel=null;
			}
		} else {
			responseResult = "failure";
			responseMsg = "Model Name (" + modelName + ") not found.";
			responseRecord = "0";
		}

		System.out.println("[DEBUG]FilePortal loadModel finish.");
	}
}
