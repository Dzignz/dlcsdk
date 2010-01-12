Ext.namespace("Mogan");

// UI介面相關
Mogan.createAccountFrom = function() {// 帳號管理表單
	var webSitedata = [['https://login.yahoo.co.jp/config/login', 'Yahoo JP']];
	var webSiteStore = new Ext.data.SimpleStore({// 下拉式選單資料
		fields : ['value', 'text'],
		data : webSitedata
	});

	var combo = new Ext.form.ComboBox({// 網站清單, 下拉式選單
		id : 'comboxLoginURL',
		store : webSiteStore,
		emptyText : '請選擇',
		mode : 'local',
		triggerAction : 'all',
		valueField : 'value',
		displayField : 'text',
		fieldLabel : '登入網站',
		editable : false,
		typeAhead : true
	});

	var simple = new Ext.FormPanel({
				labelWidth : 75, // label settings here cascade unless
				// overridden
				frame : true,
				title : '帳號管理',
				bodyStyle : 'padding:5px 5px 0',
				width : 350,
				height : 230,
				defaults : {
					width : 230
				},
				defaultType : 'textfield',
				// layout:'fit',
				items : [{
							id : 'textfieldAppId',
							fieldLabel : 'App ID',
							name : 'textfieldAppId',
							allowBlank : false
						}, combo, {
							id : 'textfieldAccount',
							fieldLabel : '帳號',
							name : 'account',
							allowBlank : false
						}, {
							id : 'textfieldPwd',
							fieldLabel : '密碼',
							name : 'pwd',
							allowBlank : false
						}],
				buttons : [{
							id : 'btnLogin',
							handler : Mogan.login,
							text : '登入'

						}, {
							text : '確認本帳號登入狀況'
						}, {
							text : '顯示登入列表',
							handler : Mogan.getLoginList
						}, {
							text : '顯示登入列表(all)',
							handler : Mogan.getLoginListAll
						}]
			});

	return simple;
}

Mogan.createBidFrom = function() {// 下標用表單
	var accountData = [];
	var accountStore = new Ext.data.SimpleStore({// 下拉式選單資料
		fields : ['dispalyValue', 'account', 'pwd', 'sysId'],
		data : accountData
	});
	var combo = new Ext.form.ComboBox({// 已登入帳號清單, 下拉式選單
		id : 'comboxLoginAccount',
		store : accountStore,
		emptyText : '請選擇',
		mode : 'local',
		triggerAction : 'all',
		valueField : 'account',
		displayField : 'dispalyValue',
		fieldLabel : '下標帳號',
		editable : false
	});
	var simple = new Ext.FormPanel({
				labelWidth : 70, // label settings here cascade unless
				frame : true,
				title : '下標工具',
				bodyStyle : 'padding:5px 5px 0',
				width : 350,
				height : 230,
				// defaults : {
				// width : 230
				// },
				defaultType : 'textfield',
				layout : 'column',
				items : [{
							layout : 'form',
							xtype : 'fieldset',
							title : '帳號資料',
							autoHeight : true,
							columnWidth : .5,
							defaultType : 'textfield',
							bodyStyle : 'padding:5px 5px 0',
							items : [combo, {
										fieldLabel : '密碼',
										name : 'bidPwd',
										id : 'bidPwd'
									}]
						}, {
							layout : 'form',
							xtype : 'fieldset',
							title : '商品資料',
							autoHeight : true,
							columnWidth : .5,
							style : 'margin-left:20px;',
							defaultType : 'textfield',
							bodyStyle : 'padding:5px 5px 0',
							items : [{
										fieldLabel : '商品網址',
										name : 'bidItemURL',
										id : 'bidItemURL',
										allowBlank : false
									}, {
										fieldLabel : '出價',
										name : 'price',
										id : 'price',
										emptyText : '直購可不填下標金額'
									}, {
										fieldLabel : '數量',
										allowBlank : false,
										id : 'qty',
										name : 'qty'
									}]
						}],

				buttons : [{

							text : '出價',
							handler : function() {
								Mogan.bidItem('BID_ITEM');
							}
						}, {
							text : '直購',
							handler : function() {
								Mogan.bidItem('BUY_ITEM')
							}
						}, {
							text : '自動出價到最高標'
						}, {
							text : '是否得標',
							handler : Mogan.isMyBid
						}, {
							text : '是否得標(出價履歷)',
							handler : Mogan.checkBidHistory
						}]
			});
	return simple;
};

Mogan.createMsgArea = function() {// 訊息區
	var textArea = new Ext.form.TextArea({
				id : 'textAreaMsg',
				grow : false,
				preventScrollbars : true,
				grow : false
			});
	return textArea;
};

Mogan.createStatusBar = function() {// 底部狀態列
	var panel = new Ext.Panel({
				frame : true,
				region : 'south',
				height : 25,
				// layout : 'fit',
				items : [new Ext.form.Label({
							text : '',
							id : 'statusMsg'

						})]
			});
	return panel;
};

// ==================================//
// 實際操作API
var isLoingProcess = false;
var loginAccount = "";
var loginWebSiteName = "";
var accountRecord = Ext.data.Record.create([{
			name : 'dispalyValue',
			type : 'string'
		}, {
			name : 'account',
			type : 'string'
		}, {
			name : 'pwd',
			type : 'string'
		}, {
			name : 'sysId',
			type : 'string'
		}]);

var loginMsg = new Object();
loginMsg["0"] = "未登入";
loginMsg["1"] = "登入成功";
loginMsg["2"] = "登入失敗";
loginMsg["3"] = "帳號已登入";

var bidMsg = new Object();
bidMsg["0"] = "下標失敗";
bidMsg["1"] = "下標成功，非最高標";
bidMsg["2"] = "下標成功，目前最高標";
bidMsg["3"] = "下標成功，已得標";
bidMsg["4"] = "下標失敗，無法下標";
bidMsg["5"] = "未登入";

var myBidMsg = new Object();
myBidMsg["0"] = "未得標";
myBidMsg["1"] = "最高出價者";
myBidMsg["2"] = "已得標";
myBidMsg["3"] = "出價被取消，未結標";
myBidMsg["4"] = "出價被取消，已結標";
myBidMsg["5"] = "出價被超過，未結標";
myBidMsg["6"] = "出價被超過，已結標";

function showExtAlert(msgTitle, msg) {// 顯示訊息
	Ext.MessageBox.alert(msgTitle, msg);
}

// 清空訊息區
function clearMsg() {
	Ext.getCmp("textAreaMsg").setValue("");
}

// 新增訊息
function addMsg(msg) {
	var content = Ext.getCmp("textAreaMsg").getValue();
	if (content.length > 0) {
		content += '\n';
	}
	content += msg;
	Ext.getCmp("textAreaMsg").setValue(content);
}

// 新增登入帳號
function addLoginAccount(valeuKey, accountKey, pwdKey, sysIdKey) {
	var comboxLoginAccount = Ext.getCmp("comboxLoginAccount");
	var store = comboxLoginAccount.getStore();
	store.add(new accountRecord({
				dispalyValue : valeuKey,
				account : accountKey,
				pwd : pwdKey,
				sysId : sysIdKey
			}));
}

// 清除帳號列表
function clearLoginAccount() {
	var comboxLoginAccount = Ext.getCmp("comboxLoginAccount");
	var store = comboxLoginAccount.getStore();
	store.removeAll();

}

// 設定狀態
function setStatusMsg(msg) {
	Ext.getCmp("statusMsg").setText(msg);
}

// 登入
Mogan.login = function() {
	if (false && isLoingProcess) {
		showExtAlert("錯誤", "帳號 [" + loginAccount + "]登入中");
		return;
	}
	var comboxLoginURL = Ext.getCmp("comboxLoginURL");
	loginAccount = Ext.getCmp("textfieldAccount").getValue();
	var pwd = Ext.getCmp("textfieldPwd").getValue();
	loginWebSiteName = comboxLoginURL.getRawValue();
	isLoingProcess = true;

	setStatusMsg(loginAccount + " 登入中....");
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
					setStatusMsg(loginAccount + " 登入結束....");
					addMsg('ajax..back....' + r);
					isLoingProcess = false;
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == "failure") {
						addMsg("[錯誤]\t" + json['responseMsg'] + " 耗時:"
								+ json['responseTime'] + "ms");
					} else {
						addMsg("[訊息]\t" + loginMsg[json['responseData'][0]]
								+ " 耗時:" + json['responseTime'] + "ms");
						addLoginAccount(loginAccount + " [" + loginWebSiteName
										+ "]", loginAccount, pwd, "");
					}
				},
				failure : function(response) {
					addMsg("[錯誤]\tajax failure");
				},
				params : {
					APP_ID : Ext.getCmp("textfieldAppId").getValue(),
					ACTION : "LOGIN",
					RETURN_TYPE : "JSON",
					WEB_SITE_URL : comboxLoginURL.getValue(),
					WEB_SITE_NAME : loginWebSiteName,
					UID : loginAccount,
					PWD : pwd
				}
			});
};

// 下標
Mogan.bidItem = function(act) {
	var comboxLoginAccount = Ext.getCmp("comboxLoginAccount");
	var bidAccount = comboxLoginAccount.value;
	addMsg("Mogan.login..." + Ext.getCmp("comboxLoginAccount").getValue()
			+ "::" + Ext.getCmp("bidPwd").getValue());
	var bidAction = act;
	setStatusMsg(bidAccount + " 下標中...." + act);
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
					setStatusMsg(bidAccount + " 下標結束....");
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == "false") {
						addMsg("[錯誤]\tbidItem " + json['responseMsg'] + " 耗時:"
								+ json['responseTime'] + "ms");
					} else {
						addMsg("[訊息]\tbidItem " + bidMsg[json['responseData']]
								+ " 耗時:" + json['responseTime'] + "ms");
					}
				},
				failure : function(response) {
					addMsg("[錯誤]\tbidItem ajax failure" + json['responseMsg']
							+ " 耗時:" + json['responseTime'] + "ms");
				},
				params : {
					APP_ID : Ext.getCmp("textfieldAppId").getValue(),
					ACTION : act,
					RETURN_TYPE : "JSON",
					BID_ITEM_URL : Ext.getCmp("bidItemURL").getValue(),
					UID : bidAccount,
					PWD : Ext.getCmp("bidPwd").getValue(),
					PRICE : Ext.getCmp("price").getValue(),
					QTY : Ext.getCmp("qty").getValue()
				}
			});
};

Mogan.checkBidHistory = function() {
	var comboxLoginAccount = Ext.getCmp("comboxLoginAccount");
	var bidAccount = comboxLoginAccount.value;
	setStatusMsg(bidAccount + " 確認商品出價履歷....");
	Ext.Ajax.request({
		url : 'AjaxPortal',
		callback : function() {
			setStatusMsg(loginAccount + " 確認商品出價履歷完成....");
		},
		success : function(response) {
			var json = parserJSON(response.responseText);
			if (json['responseResult'] == "failure") {
				addMsg("[錯誤]\t" + json['responseMsg'] + " 耗時:"
						+ json['responseTime'] + "ms");
			} else {
				addMsg("[訊息]\t商品出價履歷 耗時:" + json['responseTime'] + "ms");
				addMsg("[訊息]\tAUTO_BID " + json['responseData'][0]['AUTO_BID']);
				addMsg("[訊息]\tBID " + json['responseData'][0]['BID']);
				addMsg("[訊息]\tCANEL_BID "
						+ json['responseData'][0]['CANEL_BID']);
			}
		},
		failure : function(response) {
			addMsg("[錯誤]\tajax failure");
		},
		params : {
			APP_ID : Ext.getCmp("textfieldAppId").getValue(),
			ACTION : "CHECK_BID_HISTORY",
			RETURN_TYPE : "JSON",
			BID_ITEM_URL : Ext.getCmp("bidItemURL").getValue(),
			PRICE : Ext.getCmp("price").getValue(),
			UID : bidAccount
		}
	});
}

Mogan.isMyBid = function() {
	var comboxLoginAccount = Ext.getCmp("comboxLoginAccount");
	var bidAccount = comboxLoginAccount.value;
	setStatusMsg(bidAccount + " 確認商品狀態....");
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
					setStatusMsg(loginAccount + " 確認商品狀態完成....");
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					addMsg(response.responseText);
					if (json['responseResult'] == "failure") {
						addMsg("[錯誤]\t" + json['responseMsg'] + " 耗時:"
								+ json['responseTime'] + "ms");
					} else {
						addMsg("[訊息]\t" + myBidMsg[json['responseData'][0]]
								+ " 耗時:" + json['responseTime'] + "ms");
					}
				},
				failure : function(response) {
					addMsg("[錯誤]\tajax failure");
				},
				params : {
					APP_ID : Ext.getCmp("textfieldAppId").getValue(),
					ACTION : "IS_MY_BID",
					RETURN_TYPE : "JSON",
					BID_ITEM_URL : Ext.getCmp("bidItemURL").getValue(),
					PRICE : Ext.getCmp("price").getValue(),
					UID : bidAccount
				}
			});
}

// 取得已登入列表，指定APP_ID
Mogan.getLoginList = function() {
	setStatusMsg(" 取得登入帳號清單中....");
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
					setStatusMsg("帳號清單取回完成....");
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json.responseResult == "failure") {
						addMsg("[錯誤]\t" + json.responseMsg + " 耗時:"
								+ json.responseTime + "ms");
						setStatusMsg("帳號清單取回解析錯誤....");
						return;
					}
					addMsg("[訊息]\tappId取回已登入下列帳號(共 " + json.responseRecords
							+ " 組) 耗時:" + json.responseTime + "ms");
					var jsonRes = json.responseData;
					clearLoginAccount();
					for (var i = 0; i < jsonRes.length; i++) {
						var data = jsonRes[i];
						addLoginAccount(data['ACCOUNT'] + "["
										+ data['WEB_SITE_NAME'] + "]",
								data['ACCOUNT'], "", "");
						addMsg("\t" + (i + 1) + ") " + data['ACCOUNT']
								+ " 登入時間:" + data['LOGIN_TIME']);
					}
					return;
				},
				failure : function(response) {
					addMsg("[錯誤]\tajax failure");
				},
				params : {
					ACTION : "GET_LOGIN_LIST",
					RETURN_TYPE : "JSON",
					APP_ID : Ext.getCmp("textfieldAppId").getValue()
				}
			});
};

// 取得已登入列表，不指定APP_ID
Mogan.getLoginListAll = function() {
	setStatusMsg(" 取得登入帳號清單中....");
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
					setStatusMsg("帳號清單取回完成....");
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json.responseResult == "failure") {
						addMsg("[錯誤]\t" + json.responseMsg + " 耗時:"
								+ json.responseTime + "ms");
						setStatusMsg("帳號清單取回解析錯誤....");
						return;
					}
					addMsg("[訊息]\tappId取回已登入下列帳號(共 " + json.responseRecords
							+ " 組) 耗時:" + json.responseTime + "ms");
					var jsonRes = json.responseData;
					clearLoginAccount();
					for (var i = 0; i < jsonRes.length; i++) {
						var data = jsonRes[i];
						addLoginAccount(data['ACCOUNT'] + "["
										+ data['WEB_SITE_NAME'] + "]",
								data['ACCOUNT'], "", "");
						addMsg("\t" + (i + 1) + ") " + data['ACCOUNT']
								+ " 登入時間:" + data['LOGIN_TIME']);
					}
					return;
				},
				failure : function(response) {
					addMsg("[錯誤]\tajax failure");
				},
				params : {
					ACTION : "GET_LOGIN_LIST_ALL",
					APP_ID : Ext.getCmp("textfieldAppId").getValue(),
					RETURN_TYPE : "JSON"
				}
			});
};