package com.mogan.serviceProtal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class SMSeProtal extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(request, response);
	}
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		req.getParameter("mttel");//回應者之電話號碼
		req.getParameter("mtval");//回應內容
		req.getParameter("od_sob");//送出檢查碼
		
		req.getParameter("smsod");//訊息結果
		req.getParameter("sms_tool");//實際應發送通數
		req.getParameter("http_sob");//回復檢查碼
	}
}
