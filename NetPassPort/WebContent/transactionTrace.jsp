<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.mogan.sys.DBConn"%>
<%@ page import="com.mogan.io.FileIO"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.io.File"%>
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
	final String modelName="BidManager";

	DBConn conn = (DBConn)application.getAttribute("DBConn");// 呼叫 Bean 物件的 getConn() 方法，取得已建立完成的資料庫連結
	JSONArray accountList=conn.queryJSONArray("mogan-DB","SELECT bid_id,CONCAT(account,CONCAT('-',website_name)) as diaplay_account,account FROM view_system_bid_id");
	JSONObject accountData=new JSONObject();//增加一個空白選項
	JSONObject emptyAccount=new JSONObject();//增加一個空白選項
	emptyAccount.put("bid_id","");
	emptyAccount.put("account","");
	emptyAccount.put("diaplay_account","-");
	accountList.add(0,emptyAccount);
	accountData.put("root",accountList);
	
	//對應列表
	FileIO fio =new FileIO();
	Properties p=fio.loadPtyFile(null,modelName);
	JSONArray trnsList=JSONArray.fromObject(p.get("TRNS_CODE_LIST"));	
	JSONObject trnsData=new JSONObject();
	trnsData.put("root",trnsList);
	
	//欄位清單
	JSONArray tempTrnsColmList=new JSONArray();
	JSONObject tempObj=new JSONObject();
	tempObj.put("columnName","item_id");
	tempObj.put("columnDesc","日雅商品ID");
	tempTrnsColmList.add(tempObj);
	tempObj.put("columnName","sell_name");
	tempObj.put("columnDesc","日雅賣家ID");
	tempTrnsColmList.add(tempObj);
	tempObj.put("columnName","item_order_id");
	tempObj.put("columnDesc","摩根訂單編號");
	tempTrnsColmList.add(tempObj);
	p.put("TRNS_COLM_LIST",tempTrnsColmList);
	JSONArray trnsColmList=JSONArray.fromObject(p.get("TRNS_COLM_LIST"));	
	JSONObject trnsColmData=new JSONObject();
	trnsColmData.put("root",trnsColmList);
	
	//範本列表
	File [] f=fio.getTxtFileList(null,modelName);
	JSONArray templateFileList=new JSONArray();
	//JSONObject tempFileList=new JSONObject();
	JSONObject templateData=new JSONObject();
	System.out.println("[DEBUG] JSP::"+f);
	if (f!=null){
	for (int i=0;i<f.length;i++){
		JSONObject templateFile=new JSONObject();
		templateFile.put("templateIndex",i);
		templateFile.put("loadStatus",false);
		templateFile.put("fileContent","");
		templateFile.put("fileName",f[i].getName().replaceAll("\\.(t|T)(x|X)(t|T)$",""));
		templateFileList.add(templateFile);
	}}
	templateData.put("root",templateFileList);
	
%>

var accountJSONData = <% out.println(accountData); %>;
var trnsJSONData = <% out.println(trnsData); %>;
var trnsColmJSONData = <% out.println(trnsColmData); %>;
var templateJSONData = <% out.println(templateData); %>;

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