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
<title>Insert title here</title>
<%

	DBConn conn = (DBConn)application.getAttribute("DBConn");// 呼叫 Bean 物件的 getConn() 方法，取得已建立完成的資料庫連結
	JSONArray jArray=new JSONArray();
	    switch (Integer.parseInt((String)request.getParameter("type"))){
    	case 1:
    	case 2:
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 0 DAY) AS DATE_S from web_bidding WHERE DATE(end_date)= CURDATE()").getJSONObject(0));//0
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 1 DAY) AS DATE_S from web_bidding WHERE end_date> CURDATE() and end_date < DATE_ADD(CURDATE(),INTERVAL 1 DAY) ").getJSONObject(0));//1
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 2 DAY) AS DATE_S from web_bidding WHERE end_date > DATE_ADD(CURDATE(),INTERVAL 1 DAY) and end_date < DATE_ADD(CURDATE(),INTERVAL 2 DAY) ").getJSONObject(0));//1
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 3 DAY) AS DATE_S from web_bidding WHERE end_date > DATE_ADD(CURDATE(),INTERVAL 2 DAY) and end_date < DATE_ADD(CURDATE(),INTERVAL 3 DAY) ").getJSONObject(0));//1
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 4 DAY) AS DATE_S from web_bidding WHERE end_date > DATE_ADD(CURDATE(),INTERVAL 3 DAY) and end_date < DATE_ADD(CURDATE(),INTERVAL 4 DAY) ").getJSONObject(0));//1
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 5 DAY) AS DATE_S from web_bidding WHERE end_date > DATE_ADD(CURDATE(),INTERVAL 4 DAY) and end_date < DATE_ADD(CURDATE(),INTERVAL 5 DAY) ").getJSONObject(0));//1
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 6 DAY) AS DATE_S from web_bidding WHERE end_date > DATE_ADD(CURDATE(),INTERVAL 5 DAY) and end_date < DATE_ADD(CURDATE(),INTERVAL 6 DAY) ").getJSONObject(0));//1
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 7 DAY) AS DATE_S from web_bidding WHERE end_date > DATE_ADD(CURDATE(),INTERVAL 6 DAY) and end_date < DATE_ADD(CURDATE(),INTERVAL 7 DAY) ").getJSONObject(0));//1
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 8 DAY) AS DATE_S from web_bidding WHERE end_date > DATE_ADD(CURDATE(),INTERVAL 7 DAY) and end_date < DATE_ADD(CURDATE(),INTERVAL 8 DAY) ").getJSONObject(0));//1
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 9 DAY) AS DATE_S from web_bidding WHERE end_date > DATE_ADD(CURDATE(),INTERVAL 8 DAY) and end_date < DATE_ADD(CURDATE(),INTERVAL 9 DAY) ").getJSONObject(0));//1
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 10 DAY) AS DATE_S from web_bidding WHERE end_date > DATE_ADD(CURDATE(),INTERVAL 9 DAY) and end_date < DATE_ADD(CURDATE(),INTERVAL 10 DAY) ").getJSONObject(0));//1
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 11 DAY) AS DATE_S from web_bidding WHERE end_date > DATE_ADD(CURDATE(),INTERVAL 10 DAY) and end_date < DATE_ADD(CURDATE(),INTERVAL 11 DAY) ").getJSONObject(0));//1
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 12 DAY) AS DATE_S from web_bidding WHERE end_date > DATE_ADD(CURDATE(),INTERVAL 11 DAY) and end_date < DATE_ADD(CURDATE(),INTERVAL 12 DAY) ").getJSONObject(0));//1
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 13 DAY) AS DATE_S from web_bidding WHERE end_date > DATE_ADD(CURDATE(),INTERVAL 12 DAY) and end_date < DATE_ADD(CURDATE(),INTERVAL 13 DAY) ").getJSONObject(0));//1
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 14 DAY) AS DATE_S from web_bidding WHERE end_date > DATE_ADD(CURDATE(),INTERVAL 13 DAY) and end_date < DATE_ADD(CURDATE(),INTERVAL 14 DAY) ").getJSONObject(0));//1
	jArray.add(conn.queryJSONArray("mogan-tw","select COUNT(*) AS COUNT ,SUM(costed) AS COST ,DATE_ADD(CURDATE(),INTERVAL 15 DAY) AS DATE_S from web_bidding WHERE end_date > DATE_ADD(CURDATE(),INTERVAL 14 DAY)").getJSONObject(0));//1
break;
    	case 3:
    		System.out.println("getParameter::"+Integer.parseInt((String)request.getParameter("type")));
    		//jArray=conn.queryJSONArray("mogan-tw","select user_name,count(*)as count , sum(costed) as cost ,(select realname from web_member where name=user_name) as realname FROM web_won where `show`='1' AND end_date > DATE('2009-12-31') group by user_name");//0
    		jArray=conn.queryJSONArray("mogan-tw","select user_name,count(*)as count , sum(costed) as cost ,(select realname from web_member where name=user_name) as realname FROM web_won where `show`='1' group by user_name");//0
    		break;
	    }
	
%>
<script type="text/javascript" src="http://www.google.com/jsapi"></script>
    <script type="text/javascript">
    <%
    switch (Integer.parseInt((String)request.getParameter("type"))){
    	case 1:
    		out.println("google.load('visualization', '1', {packages:['columnchart']});");
    		break;
    	case 2:
    		out.println("google.load('visualization', '1', {'packages':['motionchart']});");
    		break;
    	case 3:
    		out.println("google.load('visualization', '1', {'packages':['motionchart']});");
    		break;
    }
    %>
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = new google.visualization.DataTable();
        <%
        out.println("data.addRows("+jArray.size()+");");
        switch (Integer.parseInt((String)request.getParameter("type"))){
        	case 1:
        		out.println("data.addColumn('string', '日期');");
        		out.println("data.addColumn('number', '預計結標數量');");
        		
                for (int i=0;i<jArray.size();i++){
                	out.println("data.setValue("+i+", 0, '"+i+"');");
                	out.println("data.setValue("+i+", 1, "+jArray.getJSONObject(i).getInt("COUNT")+");");
                }
                out.println("var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));");
        		break;
        	case 2:
        		out.println("data.addColumn('string', 'DAY');");
        		out.println("data.addColumn('date', 'Date');");
        		out.println("data.addColumn('number', '結標總數');");
        		out.println("data.addColumn('number', '結標總價');");
                for (int i=0;i<jArray.size();i++){
                	String dateStr=jArray.getJSONObject(i).getString("DATE_S");
                	out.println("data.setValue("+i+", 0, '"+dateStr+"');");
                	out.println("data.setValue("+i+", 1, new Date ("+dateStr.split("-")[0]+","+dateStr.split("-")[1]+",1));");
                	out.println("data.setValue("+i+", 2, "+jArray.getJSONObject(i).getInt("COUNT")+");");
                	out.println("data.setValue("+i+", 3, "+jArray.getJSONObject(i).optInt("COST",0)+");");
                }
                out.println("var chart = new google.visualization.MotionChart(document.getElementById('chart_div'));");
        		break;
        	case 3:
        		
        		out.println("data.addColumn('string', 'name');");
        		out.println("data.addColumn('date', 'Date');");
        		out.println("data.addColumn('number', '結標總數');");
        		out.println("data.addColumn('number', '結標總價');");
                for (int i=0;i<jArray.size();i++){
                	
                	out.println("data.setValue("+i+", 0, '"+jArray.getJSONObject(i).getString("user_name")+"');");
                	out.println("data.setValue("+i+", 1, new Date ());");
                	out.println("data.setValue("+i+", 2, "+jArray.getJSONObject(i).getInt("count")+");");
                	out.println("data.setValue("+i+", 3, "+jArray.getJSONObject(i).optInt("cost",0)+");");
                }
                out.println("var chart = new google.visualization.MotionChart(document.getElementById('chart_div'));");
        		break;
        }
        %>

        chart.draw(data, {width: 1200, height: 750});
      }

    </script>



</head>
<body>
<ul>
	<li><a href="statistics.jsp?type=1">未來兩週結標數量統計</a></li>
	<li><a href="statistics.jsp?type=2">未來兩週結標數量/金額統計</a></li>
	<li><a href="statistics.jsp?type=3">會員購買數量金額統計</a></li>
	<li><a href="statistics.jsp?type=4">會員購買金額百分比統計</a></li>
</ul>
<div id="chart_div">未來兩週結標數量計算</div>

</body>
</html>