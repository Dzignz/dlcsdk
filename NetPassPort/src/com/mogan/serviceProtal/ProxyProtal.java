package com.mogan.serviceProtal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.mogan.sys.ModelManager;
import com.mogan.sys.ProtoModel;
import com.mogan.sys.ServiceModelFace;
import com.mogan.sys.model.ProxyModelFace;

/**
 * Servlet implementation class ProxyProtal
 */
public class ProxyProtal extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProxyProtal() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// TODO Auto-generated method stub
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=UTF-8");
		res.setHeader("Cache-Control", "no-cache");
		
		//StringBuffer stringBuffer = loadModel(req, res);
		PrintWriter out = res.getWriter();
		out.println(loadModel(req, res));
		out.flush();
		out.close();
	}
	
	/**
	 * 讀取模組資料
	 * 
	 * @param req
	 * @param res
	 */
	private String loadModel(HttpServletRequest req, HttpServletResponse res) {
		
		String modelName = req.getParameter("MODEL_NAME");
		ModelManager modelManager = new ModelManager();
		ProtoModel serviceModel = modelManager.getServiceModel(modelName);
		String act = req.getParameter("ACTION").toUpperCase();
		String appId = req.getParameter("APP_ID").toUpperCase();
		String htmlStr="";
		if (serviceModel != null) {
			
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
				
				htmlStr = ((ProxyModelFace) serviceModel).doAction(params);

			} catch (Exception e) {
				htmlStr=e.getMessage();
			}finally{
				serviceModel=null;
			}
		} else {
			htmlStr="Model not find.";
		}
		return htmlStr;
	}

}
