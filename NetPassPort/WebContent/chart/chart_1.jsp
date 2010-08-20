<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>



<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.mogan.sys.SysKernel"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>得標統計</title>
<jsp:useBean id="stBean" class="com.mogan.bean.Statistics" scope="session" />


    <script type="text/javascript" src="http://www.google.com/jsapi"></script>
    <script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'date');
        data.addColumn('number', '得標件數');
        data.addColumn('number', '平均得標件數');
        <%
        stBean.setDataType(stBean.DATA_TYPE_WON_BID );
        stBean.setRange(stBean.DAY );
        stBean.setRangeValue(7);
        stBean.loadData();
        %>
        data.addRows(<%=stBean.getDataSize() %>);
        <%
        	out.println(stBean.printData());        
        %>

        var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
        chart.draw(data, {width: 800, height: 480, title: '最近七天資料'});

        var data = new google.visualization.DataTable();
        data.addColumn('string', 'date');
        data.addColumn('number', '得標件數');
        data.addColumn('number', '平均得標件數');
        <%
        stBean.setDataType(stBean.DATA_TYPE_WON_BID );
        stBean.setRange(stBean.DAY );
        stBean.setRangeValue(30);
        stBean.loadData();
        %>
        data.addRows(<%=stBean.getDataSize() %>);
        <%
        	out.println(stBean.printData());        
        %>
        var chart = new google.visualization.LineChart(document.getElementById('chart_div2'));
        chart.draw(data, {width: 800, height: 480, title: '最近30天資料'});

        var data = new google.visualization.DataTable();
        data.addColumn('string', 'date');
        data.addColumn('number', '得標件數');
        data.addColumn('number', '平均得標件數');
        <%
        stBean.setDataType(stBean.DATA_TYPE_WON_BID );
        stBean.setRange(stBean.MONTH );
        stBean.setRangeValue(3);
        stBean.loadData();
        %>
        data.addRows(<%=stBean.getDataSize() %>);
        <%
        	out.println(stBean.printData());        
        %>
        var chart = new google.visualization.LineChart(document.getElementById('chart_div3'));
        chart.draw(data, {width: 800, height: 480, title: '最近3個月資料'});
      }
    </script>
  
</head>
<body>
<div id="chart_div"></div>
<div id="chart_div2"></div>
<div id="chart_div3"></div>
</body>
</html>