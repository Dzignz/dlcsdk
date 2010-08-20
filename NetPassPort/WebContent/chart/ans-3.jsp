<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="com.mogan.sys.DBConn"%>
<%@ page import="java.sql.CallableStatement"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.DriverManager"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.sql.ResultSetMetaData"%>
<%@ page import="java.sql.SQLException"%>
<%@ page import="java.sql.Statement"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.Comparator"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Properties"%>
<%@ page import="java.util.TreeMap"%>
<%@ page import="java.util.TreeMap"%>
<%@ page import="net.sf.json.JSONArray"%>
<%@ page import="net.sf.json.JSONObject"%>
<%
DBConn conn = (DBConn)application.getAttribute("DBConn");// 呼叫 Bean 物件的 getConn() 方法，取得已建立完成的資料庫連結

Statement stmt = null;
ResultSet rst = null;

Map<String, Integer> cateMap = new HashMap<String, Integer>();
Map<String, Integer> topCateMap = new HashMap<String, Integer>();
Map<String, Integer> topCateMonMap = new HashMap<String, Integer>();
Map<String, Integer> topCateMaxMonMap = new HashMap<String, Integer>();
Map<String, Integer> topCateMinMonMap = new HashMap<String, Integer>();
Map<String, Integer> topCateAvgMonMap = new HashMap<String, Integer>();
Map<String, Map> topCateMemMap = new HashMap<String, Map>();
cateMap.put("UNKNOW", 0);
String startDate="2010-6-01";
String endDate="2010-6-31";
JSONArray jArray= conn.queryJSONArray("mogan-DB","SELECT e_varchar01,item_order_id,member_id,buy_price FROM `item_order` INNER JOIN item_data ON item_data.item_data_id = `item_order`.item_data_id  WHERE `time_at_04` >= '"
		+ startDate + "' AND  `time_at_04` <= '" + endDate + "' ");

for (int i=0;i<jArray.size();i++){
//while (!rst.isLast()) {

	String cateId = jArray.getJSONObject(i).getString("e_varchar01");
	int itemMon = jArray.getJSONObject(i).getInt("buy_price");
	String memId = jArray.getJSONObject(i).getString("member_id");
	
	JSONArray cateArray = conn.queryJSONArray("mogan-DB","SELECT category_id_path FROM api_category WHERE categoryid='"+ cateId + "'");
	if (cateArray.size()== 0) {
		// 沒有對應的category
		cateMap.put("UNKNOW", cateMap.get("UNKNOW") + 1);
	} else {
		
		String fullCate = cateArray.getJSONObject(0).getString("category_id_path");
		String topCate = fullCate.split(",")[1];
		if (topCateMap.containsKey(topCate)){
			topCateMap.put(topCate, topCateMap.get(topCate) + 1);
			topCateMonMap.put(topCate, topCateMonMap.get(topCate) + itemMon);
			topCateMemMap.get(topCate).put(memId,"1");
			if (topCateMaxMonMap.get(topCate)<itemMon) topCateMaxMonMap.put(topCate, itemMon);
			if (topCateMinMonMap.get(topCate)>itemMon) topCateMinMonMap.put(topCate, itemMon);
			topCateAvgMonMap.put(topCate, (topCateAvgMonMap.get(topCate)+itemMon)/2);
		}else{
			topCateMap.put(topCate, 1);
			topCateMonMap.put(topCate, itemMon);
			topCateMemMap.put(topCate, new HashMap());
			topCateMemMap.get(topCate).put(memId,"1");
			topCateMaxMonMap.put(topCate, itemMon);
			topCateMinMonMap.put(topCate, itemMon);
			topCateAvgMonMap.put(topCate, itemMon);
		}
		
		while (fullCate.contains(",")) {
			String keyCate = fullCate.substring(fullCate.lastIndexOf(",") + 1, fullCate.length());
			// String keyCate=fullCate.split(",")[fullCate.split(",").length];
			if (cateMap.containsKey(keyCate)) {
				cateMap.put(keyCate, cateMap.get(keyCate) + 1);
			} else {
				cateMap.put(keyCate, 1);
			}
			fullCate = fullCate.replaceAll("," + keyCate, "");
		}
	}

}
List<Map.Entry<String, Integer>> list_Data = new ArrayList<Map.Entry<String, Integer>>(cateMap.entrySet());

Collections.sort(list_Data, new Comparator<Map.Entry<String, Integer>>() {
	public int compare(Map.Entry<String, Integer> o1,
			Map.Entry<String, Integer> o2) {
		return (o2.getValue() - o1.getValue());
	}
});

//Iterator it = topCateMap.keySet().iterator();


%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<script type="text/javascript" src="http://www.google.com/jsapi"></script>
    <script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Task');
        data.addColumn('number', 'Hours per Day');
        data.addRows(<%=topCateMap.size() %>);
        <%
        	Iterator it = topCateMap.keySet().iterator();
        	
        	for (int i=0; it.hasNext();) {
        		String key = (String) it.next();
        		out.println("data.setValue("+i+", 0, '"+"<PRE>共\\t"+topCateMemMap.get(key).size()+" 人\\r\\n"+
						"總價\\t"+topCateMonMap.get(key)+"日元\\r\\n"+
						"單價(高)\\t"+topCateMaxMonMap.get(key)+"日元\\r\\n"+
						"單價(低)\\t"+topCateMinMonMap.get(key)+"日元\\r\\n"+
						"單價(平均)\\t"+topCateAvgMonMap.get(key)+"日元"+"</PRE>');");
        		out.println("data.setValue("+i+", 1, "+topCateMap.get(key)+");");
        		i++;
	        }
        	
        %>

        var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
        chart.draw(data, {width: 800, height: 600, title: 'My Daily Activities'});
      }
    </script>
    
</head>
<body>
<div id="chart_div"></div>
<div id="chart_div2"></div>
</body>
</html>