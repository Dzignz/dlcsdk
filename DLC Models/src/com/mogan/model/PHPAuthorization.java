package com.mogan.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.model.netAgent.NetAgent;
import com.mogan.sys.DBConn;
import com.mogan.sys.log.SysLogger4j;
import com.mogan.sys.model.AuthModelAdapter;

/**
 * 如果使用者透過PHP連入AP系統，需透過此Model進行登入驗證，
 * 
 * @author Dian
 *
 */
public class PHPAuthorization extends AuthModelAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean doAuth(HttpServletRequest req, HttpServletResponse res) throws Exception {
		DBConn conn=(DBConn) req.getSession().getServletContext().getAttribute("DBConn");
		
		RequestDispatcher rd; //
		String userId=req.getParameter("USER_ID");
		String modelId=req.getParameter("MODEL_ID");
		// TODO 先確認USER在PHP SERVER上是否登入
		NetAgent nAgent=new NetAgent();
		Map postData=new HashMap();
		postData=new HashMap();
		postData.put("Action", "GetPrivilege");
		postData.put("User_Id", userId);
		nAgent.putAllPostDataMap(postData);
		nAgent.postMaptoData();
		nAgent.getDataWithPost("http://web.mogan.com.tw/adminv3/website/RedirectionSystem.php?MODEL_ID="+modelId);
		JSONObject privilegeObj=JSONObject.fromObject(nAgent.getResponseBody());
		JSONArray users=conn.queryJSONArray("mogan-DB", "SELECT system_name FROM system_member WHERE system_member_id='"+userId+"'");
		this.getSession().setAttribute("USER_PRIVILEGE", privilegeObj);
		this.getSession().setAttribute("USER_ID", userId);
		this.getSession().setAttribute("USER_NAME", users.getJSONObject(0).getString("system_name"));
		this.getSession().setAttribute("LOGIN_TIME", new Date());
		System.out.println(privilegeObj);
		if (privilegeObj.getJSONObject(modelId).getBoolean("view")){
			rd = req.getRequestDispatcher((String)this.getModelServletContext().getAttribute("PHP_MODEL_ID_"+modelId));
		}else{
			rd = req.getRequestDispatcher("nonPrivilege.jsp");
		}		
		rd.forward(req, res);
//		
		return true;
	}

}
