package com.mogan.serviceProtal;

import java.io.IOException;
import java.util.Date;
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

import com.mogan.sys.model.AuthModelAdapter;
import com.mogan.sys.model.ModelManager;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;

/**
 * Servlet implementation class OauthProtal
 */
public class OauthProtal extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       /**網頁登入*/ 
	private static final String webLogin="WEB_LOGIN";
	/**
	 * 其他系統登入 
	 */
	private static final String modelLogin="MODEL_LOGIN";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OauthProtal() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(req, res);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// TODO Auto-generated method stub
		req.setCharacterEncoding("UTF-8");
		res.setContentType("text/xml; charset=UTF-8");
		res.setHeader("Cache-Control", "no-cache");
		String oauthType=req.getParameter("OAUTH_TYPE");
		
		
		if (oauthType.equals(webLogin)){
			String userName=req.getParameter("USER_NAME").toUpperCase();
			String pwd=req.getParameter("PWD").toUpperCase();
			if (userName.equals("dian".toUpperCase()) && pwd.equals("mogan".toUpperCase())){
				req.getSession().setAttribute("userId", "0001");
				req.getSession().setAttribute("userName", userName);
				req.getSession().setAttribute("loginTime", new Date());
				req.getSession().setAttribute("oauthType",webLogin);
				req.getSession().setAttribute("oauthKey","passed");
				res.sendRedirect((String) req.getSession().getAttribute("originURL"));
			}
		}else if (oauthType.equals(modelLogin)){
			loadModel(req,res);
		}
	}
	
	/**
	 * 
	 */
	private void loadModel(HttpServletRequest req, HttpServletResponse res){
		String modelName = req.getParameter("MODEL_NAME");
		ModelManager modelManager = new ModelManager();
		ProtoModel authModel = modelManager.getAuthModel(modelName);
		
		if (authModel != null) {
			try {
				authModel.setProperties(modelManager.getModelProperties(modelName));
				authModel.setSessionId(req.getSession().getId());
				authModel.setModelServletContext(this.getServletContext());
				 ((AuthModelAdapter) authModel).doAuth(req,res);
				 authModel.setSession(req.getSession());
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				authModel=null;
			}
		} else {

		}
	}

}
