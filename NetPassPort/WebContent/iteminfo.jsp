<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.mogan.sys.SysKernel"%>
<%@page import="com.mogan.sys.DBConn"%>

<%@page import="net.sf.json.JSONArray"%>
<%@page import="net.sf.json.JSONObject"%>

<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" >
<%
if (session.getAttribute("USER_ID")==null){
%>
	location='nonPrivilege.jsp';
<%
}
%>
</script>
<link rel="stylesheet" type="text/css" href="resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="resources/mogan/mogan.css" />
<link rel="stylesheet" type="text/css" href="resources/mogan/grid.css" />

<script type="text/javascript" src="js/ext-base.js"></script>
<script type="text/javascript" src="js/ext-all.js"></script>
<script type="text/javascript" src="js/ext-lang-zh_TW.js"></script>
<script type="text/javascript" src="js/ext-mogan.js"></script>
<script type="text/javascript" src="js/ExtModel/msgPanel.js"></script>
<title>商品資訊</title>

<script type="text/javascript">
	var appId = "26b782eb04abbd54efba0dcf854b158d";
	var itemOrderId='<%=request.getParameter("item_order_id") %>';
	readMsg = function(contactId, itemOrderId) {
		Ext.Ajax.request( {
			url : 'AjaxPortal',
			callback : function() {
				Ext.Msg.hide();
			},
			success : function(response) {
				var json = parserJSON(response.responseText);
				if (json['responseResult'] == "failure") {
					Ext.Msg.alert("錯誤", json['responseMsg']);
				} else {

				}
			},
			failure : function(response) {
				Ext.Msg.alert("錯誤", "請向程式開發者詢問");
			},
			params : {
				APP_ID : appId,
				ACTION : "READ_TRANSACTION_MSG",
				RETURN_TYPE : "JSON",
				MODEL_NAME : "BM2",
				ITEM_ORDER_ID : itemOrderId,
				CONTACT_ID : contactId
			}
		});
	};
	
	Ext.onReady(function() {
		var sendMsgPanel = new SendMsgPanel({
			title:'發送訊息',
			id:'msgSenderPanel',
			renderTo : 'msgPanelDiv',
			height:400,
			width:msgTabel.offsetWidth
		});
		sendMsgPanel.show();
		Ext.getBody().setStyle("margin", "10px");
		Ext.getBody().setStyle("padding", "10px");
		
	});
</script>
<style type="text/css">
#box {
	width: 100px;
	height: 100px;
	background-color: black;
	position: fixed;
	_position: absolute;
	top: 100px;
	left: 100px;
}

#fixedRight {
	position: fixed;
	_position: absolute;
	z-index: 9;
	bottom: 200px;
	width: 100px;
	height: 80px;
	border: 1px solid #CCCCCC;
	background: #EEF7FF;
	right: 5px;
}

</style>

</head>
<body>
<%
	
	DBConn conn = SysKernel.getConn();
	String sql = "";
	
	JSONArray entArray=conn.queryJSONArray((String)SysKernel.getApplicationAttr(SysKernel.MAIN_DB), "SELECT * FROM view_bid_item_order_v1 WHERE item_order_id = '"
			+ request.getParameter("item_order_id") + "' AND delete_flag=1");
 	JSONObject ioEty=entArray.getJSONObject(0);
	JSONArray msgArray = conn.queryJSONArray("mogan-DB", "SELECT * FROM view_item_contact_record_v1 WHERE "
			+ " item_order_id = '" + ioEty.getString("item_order_id") + "'  ORDER BY msg_date desc");
	Map statusMap=new HashMap();
	statusMap.put("1-01","競標中");
	statusMap.put("1-02","訂單已關閉");
	
	statusMap.put("3-01","連絡中");
	statusMap.put("3-02","取得連絡");
	statusMap.put("3-03","待匯款");
	statusMap.put("3-04","已匯款");
	statusMap.put("3-05","賣家已發貨");
	statusMap.put("3-06","購買點已收貨");
	statusMap.put("3-07","購買點已發貨");
	statusMap.put("3-08","收貨點已收貨");
	statusMap.put("3-09","收貨點已發貨");
	statusMap.put("3-10","會員已收貨");
	statusMap.put("3-12","棄標");
	
%>
<div><a id="itemData"></a>
<h3>商品名稱 ：<%=ioEty.getString("item_name")%></h3>
<h3>目前狀態 ：<%=statusMap.get(ioEty.getString("order_status")) %></h3>
<h3>得標金額 ：<%=ioEty.getString("price")%></h3>
<h3>得標數量 ：<%=ioEty.getString("unit")%></h3>
<br />

<p><img src=http://image.mogan.com.tw/images/item_images/<%=ioEty.getString("main_image")%> /> <img
	src=http://image.mogan.com.tw/images/item_images/<%=ioEty.getString("e_varchar02")%> /> <img
	src=http://image.mogan.com.tw/images/item_images/<%=ioEty.getString("e_varchar03")%> /></p>
</div>
<div>
<a id="memberData"></a>
<h4>會員名稱：<%=ioEty.getString("full_name")%></h4>
<h4>完成交易次數：<%="-"%></h4>
</div>
<div id="msgPanelDiv"></div>
<a id="msgData"></a>
<table id="msgTabel" border=1>
	<tr BGCOLOR="#00FFFF">
		<td>#</td>
		<td>留言帳號</td>
		<td>留言時間</td>
		<td>讀取狀態</td>
	</tr>
	<%
		for (int i = 0; i < msgArray.size(); i++) {
			JSONObject obj = msgArray.getJSONObject(i);
	%>
	<tr BGCOLOR="#F88017">
		<td width=18><%=  (i + 1) %></td>
		<td><%= obj.getString("msg_from") %></td>
		<td><%= obj.getString("msg_date") %></td>
		<td>
		<%
			if (obj.getInt("is_read") == 0)
					out.println("<input type='button' value='未讀取' onclick=readMsg('"
							+ obj.getString("contact_id")
							+ "','"
							+ request.getParameter("item_order_id")
							+ "');>");
				else
					out.println("<input type='button' value='已讀取' disabled=true>");
					out.println(obj.getString("read_date"));
		%>
		</td>

	</tr>
	<tr>
		<%="<td COLSPAN=4 BGCOLOR='#FFF8C6'>"
						+ obj.getString("msg_contact") + "</td>"%>
	</tr>
	<%
		}
	%>
</table>
<div id="fixedRight">
<p><a href='#itemData'>▲ 商品資料</a></p>
<p><a href='#memberData'>▲ 會員資料</a></p>
<p><a href='#msgData'>▲ 訊息資料</a></p>
</div>
</body>
</html>