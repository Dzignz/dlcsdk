Ext.namespace("Mogan.keyword");

Mogan.keyword.createSetupPanel = function() {
	var simple = new Ext.FormPanel({
				defaultType : 'textfield',
				labelWidth : 220, // label settings here cascade unless
				frame : true,
				height : 190,
				// bodyStyle : 'padding:5px 5px 5px 5px',
				items : [{
							xtype : 'checkbox',
							fieldLabel : '使用資料庫關鍵字',
							text : '使用資料庫關鍵字',
							checked : true,
							id : 'checkboxIsUseDBKeyword',
							width : 100,
							handler : function(checkbox, checked) {
								// checkbox.setDisabled(!checked);
								Ext.getCmp("spinnerfieldDay")
										.setDisabled(!checked);
								Ext.getCmp("textfieldKeyword")
										.setDisabled(checked);

							}
						}, {
							xtype : 'spinnerfield',
							fieldLabel : '使用超過(5-500)天未搜尋的關鍵字',
							width : 100,
							text : '超過(5-500)天未搜尋',
							value : 5,
							minValue : 5,
							maxValue : 500,
							incrementValue : 5,// 每次增額
							accelerate : true,
							id : 'spinnerfieldDay'
						}, {
							fieldLabel : '關鍵字',
							id : 'textfieldKeyword',
							disabled : true,
							width : 100
						}, {
							fieldLabel : '每次搜尋中斷時間(秒)',
							xtype : 'spinnerfield',
							width : 100,
							value : 0,
							minValue : 0,
							maxValue : 9999,
							incrementValue : 1,// 每次增額
							id : 'spinnerfieldDelaySecend',
							accelerate : true
						}, {
							xtype : 'spinnerfield',
							fieldLabel : '畫面顯示記錄筆數(50-1000)',
							width : 100,
							value : 50,
							minValue : 50,
							maxValue : 5000,
							incrementValue : 10,// 每次增額
							id : 'spinnerfieldRowCount',
							accelerate : true
						}, {
							layout : 'column',
							xtype : 'panel',
							items : [{
								style : {
									margin : '5px 5px 5px 5px'
								},
								xtype : 'button',
								text : '開始搜尋',
								width : 100,
								id : 'btnSearch',
								handler : function() {
									Mogan.keyword
											.searchKeyword(
													Ext
															.getCmp("textfieldKeyword")
															.getValue(),
													Ext
															.getCmp("checkboxIsUseDBKeyword")
															.getValue(),
													Ext
															.getCmp("spinnerfieldDay")
															.getValue());
								}
							}, {
								style : {
									margin : '5px 5px 5px 5px'
								},
								xtype : 'button',
								text : '停止',
								id : 'btnStop',
								disabled : true,
								handler : function() {
									keepSearch = false;
									Ext.getCmp("btnSearch").setDisabled(false);
									Ext.getCmp("btnStop").setDisabled(true);
								},
								width : 100
							}, {
								style : {
									margin : '5px 5px 5px 5px'
								},
								xtype : 'button',
								text : '修正關鍵字',
								width : 100,
								id : 'btnFixKeyword',
								handler : function() {
									Mogan.keyword.fixKeyword();
								}
							}]
						}]
			});
	return simple;
};

Mogan.keyword.createMsgPanel = function() {
	var simple = new Ext.FormPanel({
				defaultType : 'textfield',
				labelWidth : 70, // label settings here cascade unless
				frame : true,
				labelAlign : 'top',
				items : [{
							style : {
								margin : '5px 5px 5px 5px'
							},
							handler : function() {
								Ext.getCmp("textareaWalkMsg").setValue("");
							},
							xtype : 'button',
							text : '清除訊息區',
							width : 100
						}, {
							fieldLabel : '執行結果',
							xtype : 'textarea',
							layout : 'fit',
							height : 200,
							anchor : '80%',
							id : 'textareaWalkMsg'
						}]
			});
	return simple;
};