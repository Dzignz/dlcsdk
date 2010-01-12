<?xml version="1.0" encoding="BIG5" ?>
<%@ page language="java" contentType="text/html; charset=BIG5"
	pageEncoding="BIG5"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5" />
<title>Insert title here</title>

<link rel="stylesheet" type="text/css" href="resources/css/ext-all.css" />
<script type="text/javascript" src="js/ext-base.js"></script>
<script type="text/javascript" src="js/ext-all.js"></script>
<script type="text/javascript" src="js/ext-lang-zh_TW.js"></script>
<script type="text/javascript" src="js/ext-mogan.js"></script>

</head>
<body>
<!-- <script type="text/javascript" src="./js/Ajax.js" /> -->
<table border="1">
	<tr>
		<td>
		<h3>[登入介面]</h3>
		<label>登入網站</label> <select id="webSiteList">
			<option value="http://auctions.yahoo.co.jp/jp/">Yahoo jp</option>
		</select> <br />
		<label>帳號</label> <input id="loginUid" type="text" value="bogjack101" /><br />
		<label>密碼</label> <input id="loginPwd" type="text" value="04100515" /><br />
		<input type="button" onclick="loging()" value="登入" /> <input
			type="button" value="顯示已登入帳號" /><br />
		</td>
		<td>
		<h3>[下標商品]</h3>
		<label>商品網址 </label><input type="text" id="bidItemURL" /><br />
		<label>帳號 </label><select id="bidUidList"></select><br />
		<label>密碼 </label><input type="text" id="bidPwd" /><br />
		<label>出價 </label><input type="text" id="bidPrice" /><br />
		<label>數量 </label><input type="text" id="bidQty" /><br />
		<input type="button" onclick="bidItem()" value="下標" /> <input type="button" onclick="isMyBid()" value="是否得標" /><br />
		</td>
		<td>
		<h3>[直購商品]</h3>
		<label>商品網址 </label><input type="text" id="buyItemURL" /><br />
		<label>帳號 </label> <select id="buyUidList"></select><br />
		<label>密碼 </label><input type="text" id="buyPwd" /><br />
		<label>數量 </label><input type="text" id="buyQty" /><br />
		<input type="button" onclick="buyItem()" value="直購" /><br />
		</td>
	</tr>
	<tr>
		<td colspan="3"><input type="button" value="清除訊息"
			onclick="clearMsg()" /><input type="button" value="test"
			onclick="testExt()" /> <br />
		<textarea rows="20" cols="150" id="textAreaMsg" name="textAreaMsg"></textarea></td>
	</tr>
</table>
<script type="text/javascript">
	function testExt() {
		Ext.MessageBox.alert('hello world', 'dian');
	}
	function showExtMsg(msgTitle, msg) {
		Ext.MessageBox.alert(msgTitle, msg);
	}
	var loginAccount = "";
	var loginWebSiteName = "";
	var isLoingProcess = false;
	var loginMsg=new Object();
	loginMsg["0"]="未登入";
	loginMsg["1"]="登入成功";
	loginMsg["2"]="登入失敗";
	loginMsg["3"]="帳號已登入";
	
	var bidMsg=new Object();
	bidMsg["0"]="下標失敗";
	bidMsg["1"]="下標成功，非最高標";
	bidMsg["2"]="下標成功，目前最高標";
	bidMsg["3"]="下標成功，已得標";
	bidMsg["4"]="未登入";
	bidMsg["5"]="未登入";

	var myBidMsg=new Object();
	myBidMsg["0"]="未得標";
	myBidMsg["1"]="最高出價者";
	myBidMsg["2"]="已得標";
	
	function isMyBid(){
		Ext.Ajax.request( {
			url :'AjaxPortal',
			success : function(response) {
				var rs = parserXML(response.responseText);
				var r = rs.records[0];
				if (r.data['responseResult'] == "false") {
					addMsg("[錯誤] " + r.data['responseMsg'] + " 耗時:"
							+ r.data['responseTime'] + "ms");
				} else {
					addMsg("[訊息] " + myBidMsg[r.data['responseData']] + " 耗時:"
							+ r.data['responseTime'] + "ms");
				}
			},
			failure : function(response) {
				addMsg("[錯誤] ajax failure");
				isLoingProcess = false;
			},
			params : {
				ACTION :"IS_MY_BID",
				RETURN_TYPE :"XML",
				BID_ITEM_URL :document.getElementById("bidItemURL").value,
				UID :document.getElementById("bidUidList").value,
				PWD :document.getElementById("bidPwd").value
			}
		});
	}
	
	function loging() {
		if (isLoingProcess) {
			showExtMsg("錯誤", "帳號 [" + loginAccount + "]登入中");
			return;
		}
		var selectObj = document.getElementById("webSiteList");
		isLoingProcess = true;
		loginAccount = document.getElementById("loginUid").value;
		loginWebSiteName = selectObj.options[selectObj.selectedIndex].text;
		Ext.Ajax.request( {
			url :'AjaxPortal',
			success : function(response) {
				var rs = parserXML(response.responseText);
				var r = rs.records[0];
				if (r.data['responseResult'] == "false") {
					addMsg("[錯誤] " + r.data['responseMsg'] + " 耗時:"
							+ r.data['responseTime'] + "ms");
				} else {
					addMsg("[訊息] " + loginMsg[r.data['responseData']] + " 耗時:"
							+ r.data['responseTime'] + "ms");
					addLoginAccount("bidUidList", loginAccount + " ["
							+ loginWebSiteName + "]", loginAccount);
					addLoginAccount("buyUidList", loginAccount + " ["
							+ loginWebSiteName + "]", loginAccount);
				}
				isLoingProcess = false;
			},
			failure : function(response) {
				addMsg("[錯誤] ajax failure");
				isLoingProcess = false;
			},
			params : {
				ACTION :"LOGIN",
				RETURN_TYPE :"XML",
				WEB_SITE_URL :selectObj.value,
				UID :loginAccount,
				PWD :document.getElementById("loginPwd").value
			}
		});
	}

	function bidItem() {
		Ext.Ajax.request( {
			url :'AjaxPortal',
			success : function(response) {
				var rs = parserXML(response.responseText);
				var r = rs.records[0];
				if (r.data['responseResult'] == "false") {
					addMsg("[錯誤] bidItem "+r.data['responseMsg'] + " 耗時:"
							+ r.data['responseTime'] + "ms");
				} else {
					addMsg("[訊息] bidItem "+bidMsg[r.data['responseData']] + " 耗時:"
							+ r.data['responseTime'] + "ms");
				}
			},
			failure : function(response) {
				addMsg("[錯誤] bidItem ajax failure"+r.data['responseMsg'] + " 耗時:"
						+ r.data['responseTime'] + "ms");
			},
			params : {
				ACTION :"BID_ITEM",
				RETURN_TYPE :"XML",
				BID_ITEM_URL :document.getElementById("bidItemURL").value,
				UID :document.getElementById("bidUidList").value,
				PWD :document.getElementById("bidPwd").value,
				PRICE :document.getElementById("bidPrice").value,
				QTY :document.getElementById("bidQty").value
			}
		});
	}

	function buyItem() {
		Ext.Ajax.request( {
			url :'AjaxPortal',
			success : function(response) {
				var rs = parserXML(response.responseText);
				var r = rs.records[0];
				addMsg("[訊息] buyItem ajax success" + " 耗時:"
						+ r.data['responseTime'] + "ms");
			},
			failure : function(response) {
				addMsg("[錯誤] buyItem ajax failure" + " 耗時:"
						+ r.data['responseTime'] + "ms");
			},
			params : {
				ACTION :"BUY_ITEM",
				RETURN_TYPE :"XML",
				BID_ITEM_URL :document.getElementById("buyItemURL").value,
				UID :document.getElementById("buyUidList").value,
				PWD :document.getElementById("buyPwd").value,
				QTY :document.getElementById("buyQty").value
			}
		});
	}

	function logout() {

	}

	function addLoginAccount(selectId, itemName, value) {
		var tempSelect = document.getElementById(selectId);
		tempSelect.options[tempSelect.options.length] = new Option(itemName);
		tempSelect.options[tempSelect.options.length - 1].value = value;
	}

	function addMsg(msg) {
		if (document.getElementById("textAreaMsg").value.length > 0) {
			document.getElementById("textAreaMsg").value += "\n";
		}
		document.getElementById("textAreaMsg").value += msg;
	}

	function clearMsg() {
		document.getElementById("textAreaMsg").value = "";
	}
</script>
</body>
</html>