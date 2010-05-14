<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.mogan.sys.DBConn"%>
<%@ page import="com.mogan.io.FileIO"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.io.File"%>
<%@ page import="net.sf.json.JSONArray"%>
<%@ page import="net.sf.json.JSONObject"%>
<%@page import="java.util.Properties"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>聯絡追蹤 V2</title>

<link rel="stylesheet" type="text/css" href="resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="resources/mogan/mogan.css" />
<link rel="stylesheet" type="text/css" href="resources/mogan/grid.css" />

<script type="text/javascript" src="js/ext-base.js"></script>

<script type="text/javascript" src="js/ext-all.js"></script>

<!-- 
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/ext-core/3.1.0/ext-core.js"></script>
  -->
<script type="text/javascript"
	src="js/mogan/mogan.template.selectedCell.js"></script>

<script type="text/javascript" src="js/ext-lang-zh_TW.js"></script>
<script type="text/javascript" src="js/ext-mogan.js"></script>

<script type="text/javascript"
	src="js/netAgent/mogan.orderTrace.function.js"></script>
<script type="text/javascript"
	src="js/netAgent/mogan.orderTrace.form.js"></script>
<script type="text/javascript"
	src="js/netAgent/mogan.orderTrace.init.js"></script>

<script type="text/javascript">

<%
	final String modelName="BidManager";

	String [] pName={"add","up","del","view"}; //權限名稱
	StringBuffer pKey=new StringBuffer("111111010000");	//權限字串
	//*
	//SF-201005-07 費用維護
	//SF-201004-16 聯絡賣家
	//SF-201004-17 訂單狀態管理
	//SF-201005-08 訂單備忘維護
	//SF-201005-09 賣家資料維護
	pKey=new StringBuffer();
	JSONObject pObj=(JSONObject)session.getAttribute("USER_PRIVILEGE");
	JSONObject tempObj=	pObj.getJSONObject("SF-201004-03");//代標聯絡處理 主畫面
	for (int i=0;i<pName.length;i++){
		if (tempObj.getBoolean(pName[i])){
			pKey.append("1");
		}else{
			pKey.append("0");
		}
	}
	tempObj=	pObj.getJSONObject("SF-201004-16");//聯絡賣家 
	for (int i=0;i<pName.length;i++){
		if (tempObj.getBoolean(pName[i])){
			pKey.append("1");
		}else{
			pKey.append("0");
		}
	}
	tempObj=	pObj.getJSONObject("SF-201004-17");//訂單狀態管理 
	for (int i=0;i<pName.length;i++){
		if (tempObj.getBoolean(pName[i])){
			pKey.append("1");
		}else{
			pKey.append("0");
		}
	}
	tempObj=	pObj.getJSONObject("SF-201005-07");//費用維護 
	for (int i=0;i<pName.length;i++){
		if (tempObj.getBoolean(pName[i])){
			pKey.append("1");
		}else{
			pKey.append("0");
		}
	}
	tempObj=	pObj.getJSONObject("SF-201005-08");//訂單備忘維護 
	for (int i=0;i<pName.length;i++){
		if (tempObj.getBoolean(pName[i])){
			pKey.append("1");
		}else{
			pKey.append("0");
		}
	}
	tempObj=	pObj.getJSONObject("SF-201005-09");//賣家資料維護 
	for (int i=0;i<pName.length;i++){
		if (tempObj.getBoolean(pName[i])){
			pKey.append("1");
		}else{
			pKey.append("0");
		}
	}
	//*/

	

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
	tempObj=new JSONObject();
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
	System.out.println("[DEBUG] ot-3");
	JSONArray trnsColmList=JSONArray.fromObject(p.get("TRNS_COLM_LIST"));	
	JSONObject trnsColmData=new JSONObject();
	trnsColmData.put("root",trnsColmList);
	
	//範本列表
	File [] f=fio.getTxtFileList(null,modelName);
	JSONArray templateFileList=new JSONArray();
	//JSONObject tempFileList=new JSONObject();
	JSONObject templateData=new JSONObject();
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
	
	//代標訂單
	Map colMap=conn.queryTabelStructure("mogan-DB","view_bid_item_order_v2");
	Iterator it=colMap.keySet().iterator();
	JSONArray colList=new JSONArray();
	for (;it.hasNext();){
		colList.add(it.next());
	}
	colList.add("memberColor");
	colList.add("sellerColor");
	colList.add("orderColor");
	JSONObject orderItemListData=new JSONObject();
	orderItemListData.put("root",new JSONArray());
	
	JSONObject payTypeData=new JSONObject();
	JSONArray typeList=conn.queryJSONArray("mogan-DB","SELECT list_key,list_name FROM system_list_value WHERE group_key='pay type' ");
	payTypeData.put("root",typeList);
	
	JSONObject alertTypeData=new JSONObject();
	alertTypeData.put("root",conn.queryJSONArray("mogan-DB","SELECT list_key,list_name FROM system_list_value WHERE group_key='alert type' "));
%>
var pkey='<%=pKey %>';
var itemOrderCol=<%=colList %>;
var accountJSONData = <%=accountData %>;
var trnsJSONData = <%=trnsData %>;
var trnsColmJSONData = <%=trnsColmData %>;
var templateJSONData = <%=templateData %>;
var orderItemListJSONData = <%=orderItemListData %>;
var payTypeJSONData = <%=payTypeData %>;
var alertTypeJSONData = <%=alertTypeData %>;

</script>
</head>
<body>
<div id="iframe-window"></div>


</body>
</html>