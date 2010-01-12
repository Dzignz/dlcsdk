Ext.namespace("Mogan.keyword");
var dataStroe = new Object();
var keepSearch = false; // 是否繼續搜尋

Mogan.keyword.fixKeyword = function(keyword, isUseDBWord, day) {
		Ext.Ajax.request({
		url : 'AjaxPortal',
		callback : function() {
		},
		success : function(response) {
			var json = parserJSON(response.responseText);
			if (json['responseResult'] == 'failure') {
				Ext.Msg.alert('搜尋失敗', json['responseMsg']);
				return;
			}
			Ext.Msg.alert('搜尋成功', json['responseData'][0]+" 日文英數=>中、英關鍵字<br /> "+json['responseData'][1]+" 中文英數=>英關鍵字");
		},
		failure : function(response) {
			
			// Mogan.mail.setStatusMsg("[錯誤]\tSave Properties
			// failure.");
		},
		params : {
			APP_ID : "fccc13447039e0ebf289e4227bc8e9e6",
			ACTION : "FIX_KEYWORD",
			MODEL_NAME : "WebWalkService",
			RETURN_TYPE : "JSON"
		}
	});
}

Mogan.keyword.searchKeyword = function(keyword, isUseDBWord, day) {
	keepSearch = isUseDBWord;
	Ext.Ajax.request({
		url : 'AjaxPortal',
		callback : function() {
		},
		success : function(response) {
			var json = parserJSON(response.responseText);
			if (json['responseResult'] == 'failure') {
				Ext.Msg.alert('搜尋失敗', json['responseMsg']);
				Ext.getCmp("btnSearch").setDisabled(false);
				Ext.getCmp("btnStop").setDisabled(true);
				return;
			}
			keyword = json['responseData'][0]['KEY_WORD'];

			var msg = "使用 " + keyword + " 搜尋到下列關鍵字 耗時:" + json['responseTime']
					+ "ms";
			for (var i = 0; i < json['responseData'][0]['NEW_WORD'].length; i++) {
				msg += "\n\t" + json['responseData'][0]['NEW_WORD'][i];
			}

			Mogan.keyword.appendWalkMsg(msg);
			if (keepSearch) {
				var delay = new Ext.util.DelayedTask(function() {
							Mogan.keyword.searchKeyword('', isUseDBWord, day);
						});
				delay.delay(Ext.getCmp('spinnerfieldDelaySecend').getValue()
						* 1000);
			} else {
				Ext.getCmp("btnSearch").setDisabled(false);
				Ext.getCmp("btnStop").setDisabled(true);
			}
		},
		failure : function(response) {
			// Mogan.mail.setStatusMsg("[錯誤]\tSave Properties
			// failure.");
		},
		params : {
			APP_ID : "fccc13447039e0ebf289e4227bc8e9e6",
			ACTION : "SEARCH_KEYWORD",
			MODEL_NAME : "WebWalkService",
			KEYWORD : keyword,
			IS_USE_DB_WORD : isUseDBWord,
			DAY : day,
			CHAR_SET : "EUC-JP",
			RETURN_TYPE : "JSON"
		}
	});
	Ext.getCmp("btnSearch").setDisabled(true);
	Ext.getCmp("btnStop").setDisabled(false);
}

Mogan.keyword.appendWalkMsg = function(msg) {
	if (Ext.getCmp('textareaWalkMsg').getValue().length > 0) {
		msg = Ext.getCmp('textareaWalkMsg').getValue() + '\n' + msg;
	} else {
		msg = Ext.getCmp('textareaWalkMsg').getValue() + msg;
	}

	/*最大行數設定，上限好像是1000*/
	while (msg.split('\n').length > Ext.getCmp('spinnerfieldRowCount')
			.getValue()) {
		msg = msg.replace(msg.split('\n')[0] + '\n', '');
	}
	Ext.getCmp('textareaWalkMsg').setValue(msg);
	Ext.getDom('textareaWalkMsg').scrollTop = Ext.getDom('textareaWalkMsg').scrollHeight;
}
