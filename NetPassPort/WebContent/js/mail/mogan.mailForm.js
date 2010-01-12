Ext.namespace("Mogan.mail");
var statusFlag = false;// 是否正在寄信中
var addresseeStore = new Array();// 收件人列表
var sessinoId = ''; // 本次連線ID
var msgArray = new Array();// 訊息列表
var uploadWindow;// 上傳檔案畫面
var loadTempletWindow;// 讀取範列畫面

// 檢查目前寄信狀態
var checkSenderTask = {
	run : function() {
		Mogan.mail.getSenderStatus();
	},
	interval : 1000
};

//var taskRunner = new Ext.util.TaskRunner();// taskRunner
var statusMsg = new Object();
statusMsg[0] = '未執行';
statusMsg[1] = '發信中';
statusMsg[2] = '暫停';
statusMsg[3] = '停止';
statusMsg[4] = '結束';

Mogan.mail.createMailStatusFrom = function() {// 發信表單
	var simple = new Ext.FormPanel({
				labelWidth : 70,
				frame : true,
				bodyStyle : 'padding:10px 10px 0',
				layout : 'vbox',
				layoutConfig : {
					align : 'stretch',
					pack : 'start'
				},
				items : [{
							id : 'textFieldStatus',
							xtype : 'label',
							style : {
								'font-size' : "22px",
								color : "#FF0000"
							},
							text : '狀態:-',
							width : '75%'
						}, {
							id : 'textAreaStatusMsg',
							xtype : 'textarea',
							layout : 'fit',
							height : 300,
							preventScrollbars : true,
							anchor : '80%'

						}]
			});
	return simple;
}

Mogan.mail.createMailSetupFrom = function() {// 設定表單
	var sendTypeData = [['一般', 'A']];
	var sendTypeStore = new Ext.data.SimpleStore({// 下拉式選單資料
		fields : ['dispalyValue', 'type'],
		data : sendTypeData
	});

	var comboType = new Ext.form.ComboBox({// 發信模式
		// id : 'comboxLoginAccount',
		store : sendTypeStore,
		emptyText : '一般(未完成)',
		mode : 'local',
		triggerAction : 'all',
		valueField : 'type',
		displayField : 'dispalyValue',
		fieldLabel : '發信模式',
		disabled : true,
		editable : false
	});

	var formatData = [['一般', 'A']];
	var formatStore = new Ext.data.SimpleStore({// 下拉式選單資料
		fields : ['dispalyValue', 'type'],
		data : formatData
	});
	var comboFromat = new Ext.form.ComboBox({// 信件內容格式
		// id : 'comboxLoginAccount',
		store : formatStore,
		emptyText : 'HTML(未完成)',
		mode : 'local',
		triggerAction : 'all',
		valueField : 'type',
		displayField : 'dispalyValue',
		fieldLabel : '信件內容格式',
		disabled : true,
		editable : false
	});

	var simple = new Ext.FormPanel({
				labelWidth : 150, // label settings here cascade unless
				frame : true,
				bodyStyle : 'padding:5px 5px 5px',
				defaultType : 'textfield',
				items : [{
							width : 150,
							xtype : 'button',
							text : '儲存鈕',
							// disabled : true,
							handler : function() {
								Mogan.mail.saveProperties();
							},
							id : 'btnSaveSetup'
						}, {
							xtype : 'textfield',
							fieldLabel : '每千封暫停秒數',
							width : 100,
							id : 'textfieldInterval'
						}, comboType, comboFromat, {
							xtype : 'textfield',
							fieldLabel : '寄件者名稱',
							anchor : '80%',
							id : 'textarFromName'
						}, {
							xtype : 'textfield',
							fieldLabel : '寄件者信箱',
							anchor : '80%',
							id : 'textarFromAddress'
						}, {
							xtype : 'textfield',
							fieldLabel : 'Mail Server Host',
							id : 'textarServerHost',
							anchor : '80%'
							// id : 'textarMailContent'
					}	, {
							xtype : 'textfield',
							fieldLabel : 'Mail Server Port',
							id : 'textarServerPort',
							anchor : '80%'
							// id : 'textarMailContent'
					}	, {
							xtype : 'textfield',
							fieldLabel : 'Mail Server Account',
							id : 'textarServerAccount',
							anchor : '80%'
							// id : 'textarMailContent'
					}	, {
							xtype : 'textfield',
							fieldLabel : 'Mail Server Pwd',
							id : 'textarServerPwd',
							anchor : '80%'
							// id : 'textarMailContent'
					}]
			});
	return simple;
}

Mogan.mail.createMailContentFrom = function() {// 信件內容表單
	var simple = new Ext.FormPanel({
		labelWidth : 70, // label settings here cascade unless
		frame : true,
		bodyStyle : 'padding:5px 5px 0',
		defaultType : 'textfield',

		items : [{
					layout : 'column',
					xtype : 'panel',
					items : [{
								layout : 'form',
								xtype : 'panel',
								width : 550,
								defaultType : 'textfield',
								bodyStyle : 'padding:5px 0px 0px',
								items : [{
											fieldLabel : '收件群組',
											disabled : true,
											id : 'textfieldAddressee',
											anchor : '95%',
											disabledClass : ' x-form-text x-form-field'
										}, {
											fieldLabel : '信件ID',
											disabled : true,
											id : 'textfieldTempletId',
											anchor : '95%',
											disabledClass : ' x-form-text x-form-field'
										}, {
											fieldLabel : '信件主題',
											id : 'textfieldMailSubject',
											anchor : '95%'
										}]
							}, {
								layout : 'form',
								width : 150,
								defaultType : 'textfield',
								items : [{
											xtype : 'button',
											id : 'mailGroup',
											arrowAlign : 'right',
											text : '設定收件群組',
											menu : mailGroupMenu
										}]
							}]
				}, {
					xtype : 'textarea',
					fieldLabel : '信件內容',
					layout : 'fit',
					height : 200,
					anchor : '80%',
					id : 'textareaMailContent'
				},/*
					 * { bodyStyle : 'margin: 5px;', xtype : 'button', text :
					 * '預覽', width : 100, scale : 'large' }
					 */{
					layout : 'column',
					xtype : 'panel',
					width : 550,
					items : [{
								xtype : 'button',
								text : '預覽',
								width : 100,
								handler : function() {
									Mogan.mail.previewHTML();
								},
								style : {
									margin : '0px 0px 5px 5px'
								},
								scale : 'large'
							}, {
								xtype : 'button',
								text : '儲存範本',
								width : 100,
								scale : 'large',
								style : {
									margin : '0px 0px 5px 5px'
								},
								handler : function() {
									Mogan.mail.saveTempletMail();
								}
							}]
				}]
	});
	return simple;
};

var mailGroupMenu = new Ext.menu.Menu({
			items : [{
						text : '條件群組',
						id : 'nomMailGroup',
						menu : {
							id : 'typeMailGroup'
						}
					}, '-', {
						id : 'advMailGroup',
						text : '進階設定'
					}]
		});

/**
 * 回Server抓Mail類別
 */
Mogan.mail.loadMailGroup = function() {
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
				},
				success : function(response) {
					var json = parserJSON(response.responseText);

					if (json['responseResult'] == "failure") {
						Mogan.mail.setStatusMsg("[錯誤]\t" + json['responseMsg']
								+ " 耗時:" + json['responseTime'] + "ms");
					} else {
						Mogan.mail.setMailGroup(json);
						Mogan.mail.setStatusMsg("[訊息]\t" + json['responseMsg']
								+ " 耗時:" + json['responseTime'] + "ms");
					}

				},
				failure : function(response) {
					Mogan.mail.setStatusMsg("[錯誤]\tloadMailGroup failure");
				},
				params : {
					APP_ID : "fccc13447039e0ebf289e4227bc8e9e6",
					ACTION : "GET_MAIL_GROUP",
					MODEL_NAME : "MailService",
					RETURN_TYPE : "JSON"
				}
			});
}

/**
 * 依jsonObj設定MAIL類別清單
 * 
 * @param {}
 *            jsonObj
 */
Mogan.mail.setMailGroup = function(jsonObj) {
	var groups = jsonObj['responseData'];
	var typeMenu = Ext.menu.MenuMgr.get("typeMailGroup");
	for (var i = 0; i < groups.length; i++) {
		var data = groups[i];
		var item = new Ext.menu.CheckItem({
					text : data['type_name'],
					data : data['mail_type_id'],
					hideOnClick : false
				});
		typeMenu.addItem(item);
	}
	typeMenu.on('click', function() {
				Mogan.mail.setAddressee();
			});
}

/**
 * 設定收件群組
 */
Mogan.mail.setAddressee = function() {
	var typeMenu = Ext.menu.MenuMgr.get("typeMailGroup");
	var items = typeMenu.items;
	var msg = "";
	addresseeStore = new Array();
	for (var i = 0; i < items.length; i++) {
		var item = items.item(i);
		if (item.checked == true) {
			addresseeStore[addresseeStore.length] = item['data'];
			if (msg.length > 0)
				msg += "; ";
			msg += item.text;
		}
	}
	var textfieldAddressee = Ext.getCmp("textfieldAddressee");
	textfieldAddressee.setValue(msg);
}

Mogan.mail.showUploadWindow = function() {
	if (!uploadWindow) {
		/*
		 * var fileuploadfield = Ext.ux.form.FileUploadField({ xtype :
		 * 'fileuploadfield', anchor : '95%', emptyText : 'Select a HTML File',
		 * fieldLabel : '上傳信件範本', name : 'FILE_ITEM_0', id : 'fileuploadfieldx'
		 * });
		 */
		uploadWindow = new Ext.Window({
					el : 'uploadFileWindowDiv',
					id : 'uploadFileWindow',
					layout : 'fit',
					width : 500,
					height : 300,
					closeAction : 'hide',
					modal : true,
					plain : true,
					title : '上傳信件範本',
					items : [new Ext.form.FormPanel({
								bodyStyle : 'padding:5px 0px 0px',
								fileUpload : true,
								autoTabs : true,
								activeTab : 0,
								deferredRender : false,
								border : false,
								items : [{
											xtype : 'textfield',
											fieldLabel : '範本名稱',
											name : 'FILE_NAME_0',
											value : '',
											id : 'textfieldFileName'
										}, {
											xtype : 'fileuploadfield',
											anchor : '95%',
											emptyText : 'Select a HTML File',
											fieldLabel : '上傳信件範本',
											name : 'FILE_ITEM_0',
											id : 'fileuploadfield'
										}, {
											xtype : 'hidden',
											name : 'ACTION',
											value : 'UPLOAD_MAIL_TEMPLET_FILE'
										}, {
											xtype : 'hidden',
											name : 'APP_ID',
											value : 'fccc13447039e0ebf289e4227bc8e9e6'
										}, {
											xtype : 'hidden',
											name : 'MODEL_NAME',
											value : 'FileService'
										}, {
											xtype : 'hidden',
											name : 'RETURN_TYPE',
											value : 'JSON'
										}],
								buttons : [{
											text : '上傳',
											handler : function() {
												Mogan.mail.submitSampleFile();
											}
										}],
								id : 'uploadFilePanel'
							})]
				});
		var uploadItem = Ext.getCmp("fileuploadfield");
		// 更換檔案時，同步修正檔名
		uploadItem.on("fileselected", function() {
					var fileNameItem = Ext.getCmp("textfieldFileName");
					if (this.getValue().lastIndexOf("/") > 0) {
						fileNameItem.setValue(this.getValue().slice(this
								.getValue().lastIndexOf("/")
								+ 1));
					} else if (this.getValue().lastIndexOf("\\") > 0) {
						fileNameItem.setValue(this.getValue().slice(this
								.getValue().lastIndexOf("\\")
								+ 1));
					} else {
						fileNameItem.setValue(this.getValue());
					}

				});
	}
	uploadWindow.show(this);
}

Mogan.mail.submitSampleFile = function() {
	var fp = Ext.getCmp("uploadFilePanel");
	// if (fp.getForm().isValid()) {
	fp.getForm().submit({
		url : 'FilePortal',
		waitMsg : 'Uploading File...',
		success : function(fp, o) {
			var uploadWindow = Ext.getCmp("uploadFileWindow");
			Ext.getCmp("textfieldMailSubject")
					.setValue(o.result.responseData[0]['mailSubject']);
			Mogan.mail.setStatusMsg("[訊息]\tupload file success.");
			Ext.getCmp("textfieldTempletId")
					.setValue(o.result.responseData[0]['id']);
			Mogan.mail.loadTempletFile(o.result.responseData[0]['id']);
			Ext.Msg.alert('訊息', '檔案上傳完成')
			uploadWindow.hide();
			Ext.getCmp('mainTabPanel').setActiveTab('tabMailContent');
			// fp.reset();
		},
		failure : function() {
			// 錯誤情況未完成
		}
	});
};

/**
 * 讀取fileId檔案內容
 * 
 * @param {}
 *            fileId
 */
Mogan.mail.loadTempletFile = function(fileId) {
	Ext.Ajax.request({
				url : 'FilePortal',
				callback : function() {
					Ext.WindowMgr.getActive().hide();
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == 'failure') {
						Ext.Msg.alert('讀取失敗', json['responseMsg']);
						return;
					}
					Ext.getCmp("textfieldTempletId") //
							.setValue(json['responseData'][0]['MailId']);
					Ext.getCmp("textfieldMailSubject") //
							.setValue(json['responseData'][0]['MailSubject']);
					Ext.getCmp("textareaMailContent") //
							.setValue(json['responseData'][0]['MailContent']);
				},
				failure : function(response) {
					Mogan.mail.setStatusMsg("[錯誤]\tloadTempletFile failure");
				},
				params : {
					APP_ID : "fccc13447039e0ebf289e4227bc8e9e6",
					ACTION : "LOAD_FILE",
					FILE_ID : fileId,
					MODEL_NAME : "FileService",
					RETURN_TYPE : "JSON"
				}
			});
}

/**
 * 開啟預覽視窗
 */
Mogan.mail.previewHTML = function() {
	var previewWin = window.open('', 'previewWin');
	previewWin.document.write(Ext.getCmp("textareaMailContent").getValue());
}

/**
 * 讀取 Properties
 */
Mogan.mail.loadProperties = function() {
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
				},
				success : function(response) {
					var json = parserJSON(response.responseText);

					Ext.getCmp("textarFromName")
							.setValue(json['responseData'][0]['fromName']);
					Ext.getCmp("textarFromAddress")
							.setValue(json['responseData'][0]['fromAdderss']);
					Ext.getCmp("textarServerHost")
							.setValue(json['responseData'][0]['serverHost']);
					Ext.getCmp("textarServerPort")
							.setValue(json['responseData'][0]['serverPort']);
					Ext.getCmp("textarServerAccount")
							.setValue(json['responseData'][0]['account']);
					Ext.getCmp("textarServerPwd")
							.setValue(json['responseData'][0]['pwd']);
				},
				failure : function(response) {
					Mogan.mail.setStatusMsg("[錯誤]\tloadProperties failure");
				},
				params : {
					APP_ID : "fccc13447039e0ebf289e4227bc8e9e6",
					ACTION : "LOAD_PROPERTIES",
					MODEL_NAME : "MailService",
					RETURN_TYPE : "JSON"
				}
			});
}

/**
 * 初始化Mail相關設定
 */
Mogan.mail.setMail = function() {
	if (addresseeStore.length == 0) {
		Ext.Msg.alert('錯誤', '未設定收件群組!!');
		return;
	}
	Ext.getCmp('textFieldStatus').setText('狀態：初始化中...');
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					sessionId = json['responseData'][0]['SESSION_ID'];
					Mogan.mail
							.sendMailConfirm(json['responseData'][0]['TARGET_LIST']);
					Ext.getCmp('textFieldStatus').setText('狀態：初始化完成.');
				},
				failure : function(response) {
					Mogan.mail.setStatusMsg("[錯誤]\tloadProperties failure");
				},
				params : {
					APP_ID : "fccc13447039e0ebf289e4227bc8e9e6",
					ACTION : "SET_MAIL_THREAD",
					MODEL_NAME : "MailService",
					CONDITION_A : Ext.util.JSON.encode(addresseeStore),// 基本條件
					CONDITION_B : "[]",// 進階條件
					MAIL_SUBJECT : Ext.getCmp("textfieldMailSubject")
							.getValue(),// 信件標題
					MAIL_CONTENT : Ext.getCmp("textareaMailContent").getValue(),// 信件內容
					RETURN_TYPE : "JSON"
				}
			});

}

/**
 * 跳出確認視窗，確認是否開始寄送mail
 * 
 * @param {}
 *            userCount
 */
Mogan.mail.sendMailConfirm = function(userCount) {
	Ext.Msg.show({
				title : '發送確認',
				msg : '預計寄出 ' + userCount + ' 封信件.\n是否要寄出？',
				width : 300,
				buttons : Ext.Msg.YESNO,
				fn : function(btn) {
					var act = '';
					if (btn == 'yes') {
						act = 'RUN';
						Ext.getCmp('textAreaStatusMsg').setValue('');
					} else if (btn == 'no') {
						act = 'STOP';
					}
					Mogan.mail.setMailSenderAct(act);
				}
			});
}

/**
 * 設定發信器的狀態 RUN PAUSE STOP
 * 
 * @param {}
 *            act
 */
Mogan.mail.setMailSenderAct = function(act) {
	Mogan.mail.changeUIStatus(act);
	Mogan.mail.setStatusMsg("[動作]\tMogan.mail.setMailSenderAct." + act);
	Ext.MessageBox.show({
				msg : 'Sending your mail, please wait...',
				progressText : 'Sending...',
				width : 300,
				wait : true,
				waitConfig : {
					interval : 200
				},
				icon : 'mail' // custom class in msg-box.html
			});

	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
						if (act=='RUN'){
							Ext.MessageBox.alert("Message","寄件完成");
						}else if (act=='STOP'){
							Ext.MessageBox.alert("Message","寄件取消");
						}
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					Mogan.mail.changeUIStatus(act);
					Mogan.mail.setSenderStatus(json);
					Mogan.mail.setCheckSenderTaskEnable(true);
				},
				failure : function(response) {
					if (typeof(response.responseText) == 'undefined') {
						// Mogan.mail
						// .appendStatusMsg(response.responseText, false);
					} else {
						Mogan.mail.changeUIStatus('STOP');
						Mogan.mail
								.setStatusMsg("[錯誤]\tRun mail thread failure.");
					}
				},
				params : {
					APP_ID : "fccc13447039e0ebf289e4227bc8e9e6",
					ACTION : act,
					SESSION_ID : sessionId,
					MODEL_NAME : "MailService",
					RETURN_TYPE : "JSON"
				}
			});
}

/**
 * 隨狀態改變目前UI
 * 
 * @param {}
 *            status
 */
Mogan.mail.changeUIStatus = function(status) {
	// alert();
	if (status == 'RUN') {
		Ext.getCmp('btnStartSend').setDisabled(true);
		Ext.getCmp('btnPauseSend').setDisabled(false);
		Ext.getCmp('btnStopSend').setDisabled(false);
		Ext.getCmp('btnStartSend').setHandler(function() {
					Mogan.mail.setMailSenderAct('RUN')
				});
		Ext.getCmp('mainTabPanel').setActiveTab(1);
		Mogan.mail.setCheckSenderTaskEnable(true);

		statusFlag = true;
	} else if (status == 'PAUSE') {
		Mogan.mail.setCheckSenderTaskEnable(false);
		statusFlag = false;
		Ext.getCmp('btnStartSend').setDisabled(false);
		Ext.getCmp('btnPauseSend').setDisabled(true);
		Ext.getCmp('btnStopSend').setDisabled(false);
		Ext.getCmp('btnStartSend').setHandler(function() {
					Mogan.mail.setMailSenderAct('RUN')
				});

	} else if (status == 'STOP') {
		Mogan.mail.setCheckSenderTaskEnable(false);
		statusFlag = false;
		Ext.getCmp('btnStartSend').setDisabled(false);
		Ext.getCmp('btnPauseSend').setDisabled(true);
		Ext.getCmp('btnStopSend').setDisabled(true);
		Ext.getCmp('btnStartSend').setHandler(function() {
					Mogan.mail.setMail()
				});
	}
}

/**
 * 設定是否啟動檢查寄信器的狀態
 * 
 * @param {}
 *            status
 */
Mogan.mail.setCheckSenderTaskEnable = function(flag) {
	if (flag && !statusFlag) {
		//taskRunner.start(checkSenderTask);
	} else if (flag == false) {
		//taskRunner.stop(checkSenderTask);
	}
}

Mogan.mail.getSenderStatus = function() {
	var conn = createXhrObject();
	conn.open("POST", "AjaxPortal", false);
	var params = "";
	params += encodeURIComponent("APP_ID") + "="
			+ encodeURIComponent("fccc13447039e0ebf289e4227bc8e9e6");
	params += "&" + encodeURIComponent("ACTION") + "="
			+ encodeURIComponent("GET_MAIL_STATUS");
	params += "&" + encodeURIComponent("SESSION_ID") + "="
			+ encodeURIComponent(sessionId);
	params += "&" + encodeURIComponent("MODEL_NAME") + "="
			+ encodeURI("MailService");
	params += "&" + encodeURIComponent("RETURN_TYPE") + "="
			+ encodeURIComponent("JSON");
	conn.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	conn.setRequestHeader("Content-length", params.length);
	conn.setRequestHeader("Connection", "close");
	conn.send(params);
	var json = parserJSON(conn.responseText);
	Mogan.mail.setSenderStatus(json);
}

/**
 * 設定寄件狀態
 * 
 * @param {}
 *            json
 */
Mogan.mail.setSenderStatus = function(json) {
	// 判斷是否成功
	if (json['responseResult'] == 'failure') {
		Mogan.mail.setCheckSenderTaskEnable(false);
		Mogan.mail.changeUIStatus('STOP');
	}
	// 判斷目前的寄信器的狀態
	if (json['responseData'][0]['STATUS'] == 4) {
		Mogan.mail.setCheckSenderTaskEnable(false);
		Mogan.mail.changeUIStatus('STOP');
	}

	Mogan.mail.appendStatusMsg(json['responseData'][0]['MSG'], false);
	Ext.getCmp('textFieldStatus').setText('狀態：'
			+ statusMsg[json['responseData'][0]['STATUS']] + ' [進度: '
			+ json['responseData'][0]['PERCENTAGE'] + '% ] ('
			+ json['responseData'][0]['CURRENT_SEQ'] + '/'
			+ json['responseData'][0]['TOTAL'] + ')');
}

/**
 * 新增狀態區訊息
 * 
 * @param {}
 *            msg
 * @param {}
 *            isNewLine
 */
Mogan.mail.appendStatusMsg = function(msg, isNewLine) {
	if (Ext.getCmp('textAreaStatusMsg').getValue().length > 0 && isNewLine) {
		msg = Ext.getCmp('textAreaStatusMsg').getValue() + '\n' + msg;
	} else {
		msg = Ext.getCmp('textAreaStatusMsg').getValue() + msg;
	}
	while (msg.split('\n').length > 500) {
		msg = msg.replace(msg.split('\n')[0] + '\n', '');
	}
	Ext.getCmp('textAreaStatusMsg').setValue(msg);
	Ext.getDom('textAreaStatusMsg').scrollTop = Ext.getDom('textAreaStatusMsg').scrollHeight;
}

/**
 * 儲存設定档
 */
Mogan.mail.saveProperties = function() {
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
				},
				success : function(response) {
					Ext.Msg.alert('儲存成功', '設定檔儲存成功');
					Mogan.mail.setStatusMsg("[成功]\tSave Properties success.");
				},
				failure : function(response) {
					Mogan.mail.setStatusMsg("[錯誤]\tSave Properties failure.");
				},
				params : {
					APP_ID : "fccc13447039e0ebf289e4227bc8e9e6",
					ACTION : "SAVE_PROPERTIES",
					MODEL_NAME : "MailService",

					FROM_NAME : Ext.getCmp("textarFromName").getValue(),
					FROM_ADDRESS : Ext.getCmp("textarFromAddress").getValue(),
					SERVER_HOST : Ext.getCmp("textarServerHost").getValue(),
					SERVER_PORT : Ext.getCmp("textarServerPort").getValue(),
					ACCOUNT : Ext.getCmp("textarServerAccount").getValue(),
					PWD : Ext.getCmp("textarServerPwd").getValue(),

					RETURN_TYPE : "JSON"
				}
			});
}

/**
 * 儲存範本档
 */
Mogan.mail.saveTempletMail = function() {

	var fileName = Ext.getCmp("textfieldMailSubject").getValue() + ' ('
			+ Ext.getCmp("textfieldTempletId").getValue() + ')';
	Ext.Msg.show({
		title : '儲存確認',
		msg : fileName + '\n內容將被儲存.\n是否要儲存？',
		width : 300,
		buttons : Ext.Msg.YESNO,
		fn : function(btn) {
			if (btn != 'yes') {
				return;
			}
			Ext.Ajax.request({
				url : 'FilePortal',
				callback : function() {
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == 'failure') {
						Ext.Msg.alert('儲存失敗', json['responseMsg']);
						return;
					}
					Ext.Msg.alert('儲存成功', fileName + '\n儲存成功.');
					Mogan.mail
							.setStatusMsg("[成功]\tSave Save Templet Mail success.");
				},
				failure : function(response) {
					Mogan.mail.setStatusMsg("[錯誤]\tSave Properties failure.");
				},
				params : {
					APP_ID : "fccc13447039e0ebf289e4227bc8e9e6",
					ACTION : "SAVE_MAIL_TEMPLET",
					MODEL_NAME : "FileService",
					MAIL_ID : Ext.getCmp("textfieldTempletId").getValue(),
					MAIL_SUBJECT : Ext.getCmp("textfieldMailSubject")
							.getValue(),
					MAIL_CONTENT : Ext.getCmp("textareaMailContent").getValue(),
					RETURN_TYPE : "JSON"
				}
			});
		}
	});
}

/**
 * 開啟讀取範本資料
 */
Mogan.mail.openTempletLoader = function() {
	if (!loadTempletWindow) {
		loadTempletWindow = new Ext.Window({
			el : 'loadTempletWindowDiv',
			id : 'loadTempletWindow',
			layout : 'fit',
			width : 500,
			height : 300,
			closeAction : 'hide',
			modal : true,
			plain : true,
			title : '讀取信件範本',

			items : [new Ext.form.FormPanel({
						bodyStyle : 'padding:5px 5px 5px 5px',
						autoTabs : true,
						activeTab : 0,
						deferredRender : false,
						border : false,
						labelAlign : 'top',
						items : [{
									xtype : 'textfield',
									fieldLabel : '請輸入信件主題或信件ID',
									name : 'SEARCH_KEY',
									anchor : '80%',
									value : '',
									id : 'textfieldSearchKey'
								}, {
									xtype : 'button',
									text : '搜尋',
									id : 'btnSearch',
									scale : 'large',
									width : 80,
									handler : function() {
										Mogan.mail.searchTemplet(Ext
												.getCmp("textfieldSearchKey")
												.getValue());
									}
								}, {
									xtype : 'combo',
									editable : false,
									anchor : '80%',
									emptyText : '-',
									fieldLabel : '請挑選一個信件範本',
									name : 'TEMPLET_LIST',
									displayField : 'dspValue',
									valueField : 'id',
									mode : 'local',
									triggerAction : 'all',
									store : new Ext.data.JsonStore({
												autoDestroy : true,
												root : 'responseData',
												storeId : 'jStore',
												fields : [{
															name : 'id'
														}, {
															name : 'subject'
														}, 'dspValue']
											}),
									id : 'comboTempletList'
								}, {
									text : '讀取',
									xtype : 'button',
									// id : 'btnSearch',
									scale : 'large',
									width : 80,
									handler : function() {
										Mogan.mail
												.loadTempletFile(Ext
														.getCmp("comboTempletList").value);
									}
								}],

						id : 'loadTempletPanel'
					})]
		});
	}
	loadTempletWindow.show(this);
}

/**
 * 搜尋範本資料
 * 
 * @param {}
 *            searchKey
 */
Mogan.mail.searchTemplet = function(searchKey) {
	if (searchKey == null || searchKey.trim().length == 0) {
		Ext.Msg.alert('錯誤', '搜尋關鍵字為空值，請重新設定.');
		return;
	}

	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == 'failure') {
						Ext.Msg.alert('儲存失敗', json['responseMsg']);
						return;
					}

					var comboTempletList = Ext.getCmp("comboTempletList");
					var store = comboTempletList.store;
					store.removeAll();
					store.loadData(json);
					Ext.Msg.alert('搜尋結果', '有' + store.getCount() + '筆資料符合條件');

					comboTempletList.reset();
					Mogan.mail
							.setStatusMsg("[成功]\tSearch Templet Mail success.");
				},
				failure : function(response) {
					Mogan.mail.setStatusMsg("[錯誤]\tSave Properties failure.");
				},
				params : {
					APP_ID : "fccc13447039e0ebf289e4227bc8e9e6",
					ACTION : "SEARCH_MAIL_TEMPLET",
					MODEL_NAME : "MailService",
					SEARCH_KEY : searchKey,
					RETURN_TYPE : "JSON"
				}
			});
}