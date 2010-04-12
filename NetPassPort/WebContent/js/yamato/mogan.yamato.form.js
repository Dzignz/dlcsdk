Ext.namespace("Mogan.yamato");

Mogan.yamato.createPanel = function() {
	var actList = [['0', '搜尋', 'Search_Item'], ['1', '同捆查詢', 'CHECK_ITEM'],
			['2', '輸入資訊', 'KeyIn_Item'], ['3', '歷史紀錄', 'HistoryAction']];

	var actStore = new Ext.data.SimpleStore({// 下拉式選單資料
		fields : ['value', 'text', 'data'],
		data : actList
	});

	var comboAction = new Ext.form.ComboBox({
				fieldLabel : 'Action',
				store : actStore,
				mode : 'local',
				id : 'comboAction',
				triggerAction : 'all',
				width : 200,
				valueField : 'data',
				hiddenName : "SEND_METHOD",
				displayField : 'text',
				editable : false,
				readOnly : true
			});
	// comboMsgCategory.on('select', Mogan.transactionTrace.fixComboMsgTitle);

	var simple = new Ext.FormPanel({
				id : 'msgSenderPanel',
				labelWidth : 75,
				defaultType : 'textfield',
				frame : true,
				url : 'AjaxPortal',
				items : [comboAction, {
							fieldLabel : 'args',
							id : 'textfieldArgs',
							name : 'SUBJECT_B',
							width : '350'
						}, {
							xtype : 'button',
							text : '送出',
							fieldLabel : ' ',
							handler:Mogan.yamato.send
						}, {
							id : 'textareaMsgContent',
							xtype : 'textarea',
							name : "MSG",
							// height : 200,
							grow : true,
							growMax : 200,
							anchor : '90% -175',
							fieldLabel : '內容'
						}]
			})

	return simple;
}
