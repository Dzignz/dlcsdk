package com.mogan.serviceProtal;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	private static final String otherSysLogin="OTHER_SYSTEM_LOGIN";
	
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
		res.setContentType("text/xml; charset=UTF-8");
		res.setHeader("Cache-Control", "no-cache");
		String oauthType=req.getParameter("OAUTH_TYPE");
		System.out.println("[DEBUG] OAUTH LOGIN.-1");
		
		
		if (oauthType.equals(webLogin)){
			System.out.println("[DEBUG] OAUTH LOGIN.-2");
			String userName=req.getParameter("USER_NAME").toUpperCase();
			String pwd=req.getParameter("PWD").toUpperCase();
			if (userName.equals("dian".toUpperCase()) && pwd.equals("mogan".toUpperCase())){
				System.out.println("[DEBUG] OAUTH LOGIN.-3");
				req.getSession().setAttribute("userId", "0001");
				req.getSession().setAttribute("userName", userName);
				req.getSession().setAttribute("loginTime", new Date());
				req.getSession().setAttribute("oauthType",webLogin);
				req.getSession().setAttribute("oauthKey","passed");
				res.sendRedirect((String) req.getSession().getAttribute("originURL"));
			}
		}
		
	}

}
