Ext.namespace("Mogan.mail.templetMg");

var mailContentRecord = new Ext.data.Record.create({
			name : 'id',
			type : 'string'
		}, {
			name : 'subject',
			type : 'string'
		}, {
			name : 'mail_content',
			type : 'string'
		}, {
			name : 'update',
			type : 'string'
		});

var mailContentStore = new Ext.data.JsonStore({
			fields : ['id', 'subject', 'mail_content', 'update']
		});

var dataStore = new Ext.data.JsonStore({
			root : 'responseData',
			totalProperty : 'responseTotalRecords',
			idProperty : 'threadid',
			remoteSort : true,
			fields : ['id', 'mail_class_id', 'subject', 'create_date',
					'creator'],
			proxy : new Ext.data.HttpProxy({
						url : 'AjaxPortal'
					})
		});

Mogan.mail.templetMg.setMailContent = function(text) {
	Ext.getCmp("textareaMailContent").setValue(text);
}

Mogan.mail.templetMg.loadMailContent = function(mailId) {
	var index = mailContentStore.find('id', mailId);

	if (index == -1) {
		Ext.Ajax.request({
			url : 'FilePortal',
			callback : function() {
				Mogan.mail.templetMg.loadMailContent(mailId);
			},
			success : function(response) {
				var json = parserJSON(response.responseText);
				if (json['responseResult'] == 'failure') {
					Ext.Msg.alert('讀取失敗', json['responseMsg']);
					mailContentStore.add(new mailContentRecord({
								id : mailId,
								mail_content : json['responseMsg']
							}));
					return;
				}
				mailContentStore.add(new mailContentRecord({
							id : mailId,
							subject : json['responseData'][0]['MailSubject'],
							mail_content : json['responseData'][0]['MailContent']
						}));
			},
			failure : function(response) {
				mailContentStore.add(new mailContentRecord({
							id : mailId,
							mail_content : '讀取錯誤'
						}));
				Ext.Msg.alert('讀取失敗', '讀取失敗，請檢查網路');
			},
			params : {
				APP_ID : "fccc13447039e0ebf289e4227bc8e9e6",
				ACTION : "LOAD_FILE",
				FILE_ID : mailId,
				MODEL_NAME : "FileService",
				RETURN_TYPE : "JSON"
			}
		});
	} else {
		Mogan.mail.templetMg.setMailContent(mailContentStore.getAt(index)
				.get('mail_content'));
	}
}