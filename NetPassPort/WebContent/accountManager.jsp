<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.mogan.sys.SysKernel"%>
<%@page import="com.mogan.sys.DBConn"%>
<%@page import="net.sf.json.JSONArray"%>

<html>
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


<script type="text/javascript"	src="js/account/account.ui.js"></script>
<script type="text/javascript"	src="js/account/account.store.js"></script>
<script type="text/javascript"	src="js/account/account.fucntion.js"></script>

<script type="text/javascript"	src="js/account/account.init.js"></script>

</head>
<body>
<input type="button" value="show" onclick="query()"/>"
<label id="info" > </label>
</body>
</html>