<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.mogan.sys.SysKernel"%>
<%@page import="com.mogan.sys.DBConn"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="net.sf.json.JSONObject"%>
<%long l0=System.currentTimeMillis(); %>

<%@page import="com.mogan.sys.SysCalendar"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>得標資料</title>
<link rel="stylesheet" type="text/css" href="resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="resources/mogan/mogan.css" />
<link rel="stylesheet" type="text/css" href="resources/mogan/grid.css" />


<script type="text/javascript" src="js/ext-base.js"></script>
<script type="text/javascript" src="js/ext-all.js"></script>
<script type="text/javascript" src="js/ext-lang-zh_TW.js"></script>
<script type="text/javascript" src="js/ext-mogan.js"></script>



<script type="text/javascript"	src="js/netAgent/bidmsg.ui.js"></script>
<script type="text/javascript"	src="js/netAgent/bidmsg.store.js"></script>
<script type="text/javascript"	src="js/netAgent/bidmsg.fucntion.js"></script>

<script type="text/javascript"	src="js/netAgent/bidmsg.init.js"></script>



</head>
<body>
<label id="info" >
</label>
<%
DBConn conn=SysKernel.getConn();
JSONArray jArray;
int dataCount;
int dataTCount=0;
SysCalendar sc=new SysCalendar();
for (int d=1;d>=0;d--){
	if (true)
	break;
	jArray=conn.queryJSONArray("mogan-DB","SELECT * FROM view_bid_item_order_v1 WHERE time_at_04 >= DATE_ADD(CURDATE(),INTERVAL -"+d+" DAY) AND time_at_04 < DATE_ADD(CURDATE(),INTERVAL -"+(d-1)+" DAY) ORDER BY time_at_04 ");
	dataCount=jArray.size(); 
	dataTCount+=dataCount;
	//<a id="an_anchor">我是一個錨點</a>
	out.println("<p><br><a id='day_"+d+"'>"+d+"天前結標</a>......");
	sc.addDay(-5);
	out.println("<a href='#day_5'>5天前結標("+sc.getFormatDate("MM/dd") +")</a> ");
	sc.addDay(1);
	out.println("<a href='#day_4'>4天前結標("+sc.getFormatDate("MM/dd") +")</a> ");
	sc.addDay(1);
	out.println("<a href='#day_3'>3天前結標("+sc.getFormatDate("MM/dd") +")</a> ");
	sc.addDay(1);
	out.println("<a href='#day_2'>2天前結標("+sc.getFormatDate("MM/dd") +")</a> ");
	sc.addDay(1);
	out.println("<a href='#day_1'>1天前結標("+sc.getFormatDate("MM/dd") +")</a> ");
	sc.addDay(1);
	 out.println("<br>");
	 out.println("<table border='1px'>");
	 out.println("<tr>");
	 out.println("<th>序號</th>");
	 out.println("<th>摩根得標ID</th>");
	 out.println("<th>日雅ID</th>");
	 out.println("<th>商品名稱</th>");
	 out.println("<th>下標帳號</th>");
	 out.println("<th>得標日</th>");
	 out.println("</tr>");
	 for (int i=0;i<jArray.size();i++){
		 JSONObject tempObj=jArray.getJSONObject(i);
		 out.println("<tr>");
		 out.println("<td>");
		 out.println(dataCount-i);
		 out.println("</td>");
		 out.println("<td>");
		 out.println(tempObj.getString("item_order_id"));
		 out.println("</td>");
		 out.println("<td>");
		 //out.println("<a target='new' href='http://page15.auctions.yahoo.co.jp/jp/auction/"+tempObj.getString("item_id")+"'>");
		 out.println(tempObj.getString("item_id"));
		// out.println("</a>");
		 out.println("</td>");
		 out.println("<td>");
		 
//		 out.println("<a target='new' href='http://ap.mogan.com.tw/NetPassPort/ProxyProtal?APP_ID=26b782eb04abbd54efba0dcf854b158d&MODEL_NAME=ItemOrderFormYJ&ACTION=GET_ITEM_PAGE&BID_ACCOUNT="+tempObj.getString("buyer_account")+"&ITEM_ID="+tempObj.getString("item_id")+"'>");
out.println("<a target='new' href='http://page15.auctions.yahoo.co.jp/jp/auction/"+tempObj.getString("item_id")+"'>");
		 out.println(tempObj.getString("item_name"));
	//	 out.println("</a>");
		 out.println("</td>");
		 out.println("<td>");
		 out.println(tempObj.getString("buyer_account"));
		 out.println("</td>");
		 out.println("<td>");
		 out.println(tempObj.getString("time_at_04"));
		 out.println("</td>");
		 out.println("</tr>");
	 }
	 out.println("</table>");
	 out.println("<p />");
}
long l1=System.currentTimeMillis(); 
%>
<script type="text/javascript">
info.innerHTML+="本次執行時間："+<%=l1-l0 %>;
info.innerHTML+="資料筆數："+<%=dataTCount %>;
</script>
</body>
</html>