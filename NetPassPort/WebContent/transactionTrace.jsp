<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.mogan.sys.DBConn"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="net.sf.json.JSONArray"%>
<%@ page import="net.sf.json.JSONObject"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>聯絡追蹤</title>
<link rel="stylesheet" type="text/css" href="resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="resources/mogan/mogan.css" />

<script type="text/javascript" src="js/ext-base.js"></script>
<script type="text/javascript" src="js/ext-all.js"></script>

<script type="text/javascript" src="js/mogan/mogan.template.selectedCell.js"></script>

<script type="text/javascript" src="js/ext-lang-zh_TW.js"></script>
<script type="text/javascript" src="js/ext-mogan.js"></script>

<jsp:useBean id="ConDbBean" scope="session" class="com.mogan.sys.DBConn"/>
<script type="text/javascript" >
<%

	DBConn conn = (DBConn)application.getAttribute("DBConn");// 呼叫 Bean 物件的 getConn() 方法，取得已建立完成的資料庫連結
	JSONArray accountList=conn.queryJSONArray("mogan-DB","SELECT bid_id,CONCAT(account,CONCAT('-',website_name)) as diaplay_account,account FROM view_system_bid_id");
	JSONObject accountData=new JSONObject();//增加一個空白選項
	JSONObject emptyAccount=new JSONObject();//增加一個空白選項
	emptyAccount.put("bid_id","");
	emptyAccount.put("account","");
	emptyAccount.put("diaplay_account","-");
	accountList.add(0,emptyAccount);
	accountData.put("root",accountList);
	//accountData.put("account","-");
	
%>
var accountJSONData = <% out.println(accountData); %>;
</script>

<script type="text/javascript" src="js/netAgent/mogan.transactionTrace.function.js"></script>
<script type="text/javascript" src="js/netAgent/mogan.transactionTrace.form.js"></script>
<script type="text/javascript" src="js/netAgent/mogan.transactionTrace.init.js"></script>
</head>
<body>
<div id="window-itemOrderForm-YAHOOJP"></div>


</body>
</html>