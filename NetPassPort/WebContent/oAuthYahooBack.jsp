<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
oauth_token:<%=request.getParameter("oauth_token") %>
<br />
oauth_token_secret:<%=request.getParameter("oauth_token_secret") %>
<br />
oauth_expires_in:<%=request.getParameter("oauth_expires_in") %>
<br />
oauth_callback_confirmed:<%=request.getParameter("oauth_callback_confirmed") %>
<br />
xoauth_request_auth_url:<%=request.getParameter("xoauth_request_auth_url") %>
<br />

</body>
</html>