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
		<h3>[�n�J����]</h3>
		<label>�n�J����</label> <select id="webSiteList">
			<option value="http://auctions.yahoo.co.jp/jp/">Yahoo jp</option>
		</select> <br />
		<label>�b��</label> <input id="loginUid" type="text" value="bogjack101" /><br />
		<label>�K�X</label> <input id="loginPwd" type="text" value="04100515" /><br />
		<input type="button" onclick="loging()" value="�n�J" /> <input
			type="button" value="��ܤw�n�J�b��" /><br />
		</td>
		<td>
		<h3>[�U�аӫ~]</h3>
		<label>�ӫ~���} </label><input type="text" id="bidItemURL" /><br />
		<label>�b�� </label><select id="bidUidList"></select><br />
		<label>�K�X </label><input type="text" id="bidPwd" /><br />
		<label>�X�� </label><input type="text" id="bidPrice" /><br />
		<label>�ƶq </label><input type="text" id="bidQty" /><br />
		<input type="button" onclick="bidItem()" value="�U��" /> <input type="button" onclick="isMyBid()" value="�O�_�o��" /><br />
		</td>
		<td>
		<h3>[���ʰӫ~]</h3>
		<label>�ӫ~���} </label><input type="text" id="buyItemURL" /><br />
		<label>�b�� </label> <select id="buyUidList"></select><br />
		<label>�K�X </label><input type="text" id="buyPwd" /><br />
		<label>�ƶq </label><input type="text" id="buyQty" /><br />
		<input type="button" onclick="buyItem()" value="����" /><br />
		</td>
	</tr>
	<tr>
		<td colspan="3"><input type="button" value="�M���T��"
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
	loginMsg["0"]="���n�J";
	loginMsg["1"]="�n�J���\";
	loginMsg["2"]="�n�J����";
	loginMsg["3"]="�b���w�n�J";
	
	var bidMsg=new Object();
	bidMsg["0"]="�U�Х���";
	bidMsg["1"]="�U�Ц��\�A�D�̰���";
	bidMsg["2"]="�U�Ц��\�A�ثe�̰���";
	bidMsg["3"]="�U�Ц��\�A�w�o��";
	bidMsg["4"]="���n�J";
	bidMsg["5"]="���n�J";

	var myBidMsg=new Object();
	myBidMsg["0"]="���o��";
	myBidMsg["1"]="�̰��X����";
	myBidMsg["2"]="�w�o��";
	
	function isMyBid(){
		Ext.Ajax.request( {
			url :'AjaxPortal',
			success : function(response) {
				var rs = parserXML(response.responseText);
				var r = rs.records[0];
				if (r.data['responseResult'] == "false") {
					addMsg("[���~] " + r.data['responseMsg'] + " �Ӯ�:"
							+ r.data['responseTime'] + "ms");
				} else {
					addMsg("[�T��] " + myBidMsg[r.data['responseData']] + " �Ӯ�:"
							+ r.data['responseTime'] + "ms");
				}
			},
			failure : function(response) {
				addMsg("[���~] ajax failure");
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
			showExtMsg("���~", "�b�� [" + loginAccount + "]�n�J��");
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
					addMsg("[���~] " + r.data['responseMsg'] + " �Ӯ�:"
							+ r.data['responseTime'] + "ms");
				} else {
					addMsg("[�T��] " + loginMsg[r.data['responseData']] + " �Ӯ�:"
							+ r.data['responseTime'] + "ms");
					addLoginAccount("bidUidList", loginAccount + " ["
							+ loginWebSiteName + "]", loginAccount);
					addLoginAccount("buyUidList", loginAccount + " ["
							+ loginWebSiteName + "]", loginAccount);
				}
				isLoingProcess = false;
			},
			failure : function(response) {
				addMsg("[���~] ajax failure");
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
					addMsg("[���~] bidItem "+r.data['responseMsg'] + " �Ӯ�:"
							+ r.data['responseTime'] + "ms");
				} else {
					addMsg("[�T��] bidItem "+bidMsg[r.data['responseData']] + " �Ӯ�:"
							+ r.data['responseTime'] + "ms");
				}
			},
			failure : function(response) {
				addMsg("[���~] bidItem ajax failure"+r.data['responseMsg'] + " �Ӯ�:"
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
				addMsg("[�T��] buyItem ajax success" + " �Ӯ�:"
						+ r.data['responseTime'] + "ms");
			},
			failure : function(response) {
				addMsg("[���~] buyItem ajax failure" + " �Ӯ�:"
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