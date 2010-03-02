<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.mogan.sys.DBConn"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="net.sf.json.JSONArray"%>
<%@ page import="net.sf.json.JSONObject"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="java.util.Properties"%><html>
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
	JSONArray trnsList=new JSONArray ();
	Properties p=new Properties();	
	p.put("trnsCode","$MOGAN_ITEM_ID");
	p.put("trnsData","1");
	trnsList.add(p);
	p=new Properties();	
	p.put("trnsCode","$MOGAN_ITEM_ORDER_ID");
	p.put("trnsData","2");
	trnsList.add(p);
	p=new Properties();	
	p.put("trnsCode","$YAHOO_JP_ITEM_ID");
	p.put("trnsData","3");
	trnsList.add(p);

	JSONObject trnsData=new JSONObject();
	trnsData.put("root",trnsList);
	
%>
var accountJSONData = <% out.println(accountData); %>;
var trnsJSONData = <% out.println(trnsData); %>;
</script>

<script type="text/javascript" src="js/netAgent/mogan.transactionTrace.function.js"></script>
<script type="text/javascript" src="js/netAgent/mogan.transactionTrace.form.js"></script>
<script type="text/javascript" src="js/netAgent/mogan.transactionTrace.init.js"></script>
</head>
<body>
<div id="iframe-window"></div>
<div id="iframe-window-trnsList"></div>
<div id="tab-iframe-window-1" style="width:100%; height:100%;"></div>

</body>
</html>