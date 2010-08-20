<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

test1<br/>
<br/>
<br/>
request.getMethod() = <%=request.getMethod() %><br />
request.getParameterMap() = <%=request.getParameterMap() %>
<%
Integer open=0;



if (application.getAttribute("open")==null){
	
}else{
	open=(Integer)application.getAttribute("open");
}
open++;
application.setAttribute("open",open);

////////////////////
Integer sessionTime=0;
if (application.getAttribute("sessionTime")==null){
	
}else{
	sessionTime=(Integer)application.getAttribute("sessionTime");
}
if (session.isNew()){
	sessionTime++;
}
application.setAttribute("sessionTime",sessionTime);

//////////
Integer sessionTime_u=0;
if (session.getAttribute("sessionTime_u")==null){
	
}else{
	sessionTime_u=(Integer)session.getAttribute("sessionTime_u");
}
sessionTime_u++;
session.setAttribute("sessionTime_u",sessionTime_u);

%>
<br />
頁面開啟次數：<%=application.getAttribute("open") %><br />
頁面開啟人數：<%=application.getAttribute("sessionTime") %><br />
個人頁面開啟：<%=session.getAttribute("sessionTime_u") %>

</body>
</html>