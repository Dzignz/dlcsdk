package com.mogan.model;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import com.mogan.model.netAgent.NetAgent;
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
		// TODO Auto-generated method stub
		String userId=req.getParameter("USER_ID");
		String modelId=req.getParameter("MODEL_ID");
		// TODO 先確認USER在PHP SERVER上是否登入
		NetAgent nAgent=new NetAgent();
		nAgent.getDataWithGet("");
		
		// TODO 再確認USER在MODEL上的權限
		nAgent.getDataWithGet("");
		
		res.sendRedirect("");
		return false;
	}

}
