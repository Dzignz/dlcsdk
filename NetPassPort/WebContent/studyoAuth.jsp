<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="resources/css/ext-all.css" />

<script type="text/javascript" src="js/ext-base.js"></script>
<script type="text/javascript" src="js/ext-all.js"></script>
<script type="text/javascript" src="js/ext-lang-zh_TW.js"></script>
<script type="text/javascript" src="js/ext-mogan.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<p>
<input type="button" value="取得Request Token" onclick="getReqToken()"/> 
<input type="button" value="開啟End User登入畫面" onclick="loginYahooJp()"/> 
<input type="button" value="取得Access Token" /> 
<input type="button" value="取得競標中列表" /> 
</p>
<p>
oauth_nonce=<input type="text" value="" disabled=true/><br />
oauth_token=<input type="text" value="" disabled=true/><br />
oauth_token_secret=<input type="text" value="" disabled=true/><br />
xoauth_request_auth_url=<input type="text" value="" disabled=true/><br />
request Token=<input type="text" value="" disabled=true/><br />
access Token=<input type="text" value="" disabled=true/><br />
</p>
============<br /><br />
session.getId=<%=session.getId() %><br />
session.getLastAccessedTime=<%=session.getLastAccessedTime() %><br />
session.getCreationTime=<%=session.getCreationTime() %><br />
<%
if (session.getAttribute("user")==null){
		session.setAttribute("user","dian");
		session.setMaxInactiveInterval(60);
}
%>
session.getMaxInactiveInterval=<%=session.getMaxInactiveInterval() %><br />
<TEXTAREA NAME="comments" COLS=40 ROWS=6> </TEXTAREA>

<script type="text/javascript" language="javascript">
var appId = "26b782eb04abbd54efba0dcf854b158d";
var loginURL="";
function getReqToken(){
	var params=	 {
			APP_ID : appId,
			ACTION : "GET_REQ_TOKEN",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "oAuth"
		};
	callServer(params,getReqTokenBack);
}
function getReqTokenBack(json){
	//loginYahooJp(json['responseData'][0]['xoauth_request_auth_url']);
	loginURL=json['responseData'][0]['xoauth_request_auth_url'];
	alert(json['responseData'][0]['xoauth_request_auth_url']);
}

function loginYahooJp(){
	//window.open(loginURL);
	var params=	 {
			APP_ID : appId,
			ACTION : "LOGIN_YAHOO_JP",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "oAuth",
			LOG_IN_URL : loginURL
		};
	callServer(params,loginYahooJpBack);	
}

function loginYahooJpBack(json){
	alert(json['responseData'][0]);
}
/*
function getReqToken(){

}
*/
function callServer(params,callback){
	Ext.Ajax.request({
		url : 'AjaxPortal',
		callback : function() {
		},
		success : function (response){
			var json = parserJSON(response.responseText);
			callback(json);
			//alert(json['responseData']);
		},
		failure : function(response) {
			Ext.Msg.alert("錯誤", "錯誤!錯誤!!");
		},
		params :params
	});
}
</script>

</body>
</html>