//Ext.namespace("Mogan.transactionTrace");

// 得標資料
var itemListStore = new Ext.data.JsonStore({
	root : 'responseData[0]["Datas"]',
	totalProperty : 'responseData[0]["Records"]',
	idProperty : 'threadid',
	id : 'itemListStore',
	remoteSort : true,
	fields : ['id', 'user_name', 'item', 'item_id', 'no', 'end_date',
			'sell_name', 'tax', 'costed', 'locally', 'remittance', 'status',
			'jyahooid', 'contact_type', 'title', 'renote'],
	proxy : new Ext.data.HttpProxy({
		url : 'AjaxPortal?APP_ID='
				+ appId
				+ '&ACTION=LOAD_BID_ITEMS&MODEL_NAME=BidManager&RETURN_TYPE=JSON'
	}),
	paramNames : {
		start : 'START_INDEX',
		limit : 'PAGE_SIZE',
		sort : 'ORDER_BY',
		dir : 'DIR'
	}
});

itemListStore.on("beforeload", getloadBidItemsURL);

var transRecord = new Ext.data.Record.create({
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

// 留言資料
var msgRecordStore = new Ext.data.JsonStore({
			root : 'responseData[0]["CONTACT_MSG"]["Datas"]',
			totalProperty : 'responseData[0]["CONTACT_MSG"]["Records"]',
			idProperty : 'threadid',
			remoteSort : true,
			fields : ['contact_id', 'seller_id', 'member_account',
					'bid_account', 'transaction_id', 'item_id', 'msg_id',
					'msg_title', 'msg_from', 'msg_contact', 'msg_date',
					'is_read', 'read_date', 'note']
		});

/**
 * 畫面上方按鈕集
 * 
 * @return {}
 */
createHeadPanel = function() {
	var btns = new Array([

	new Ext.Button({
				scale : 'medium',
				text : '發送MSN',
				id : 'btnUploadFile',
				handler : function() {
					Ext.MessageBox.prompt('Name', 'Please enter your name:', sendMsnMsg);
				}
			}),	new Ext.Button({
				scale : 'medium',
				text : '修正關鍵字',
				id : 'btnFixKeyword',
				handler : function() {
					Ext.MessageBox.prompt('Name', 'Please enter your name:', sendMsnMsg);
				}
			}),	new Ext.Button({
				scale : 'medium',
				text : '測試聯絡',
				id : 'btnTestContact',
				handler : function() {
					testContact();
				}
			}),	new Ext.Button({
				scale : 'medium',
				text : '下標測試',
				id : 'btnBidItem',
				handler : function() {
					bidItem();
				}
			}),	new Ext.Button({
				scale : 'medium',
				text : '得標訊息測試',
				id : 'btnWonItem',
				handler : function() {
					testWonMsg();
				}
			})]);
	return btns;
}

/**
 * 案件列表
 * 
 * @return {}
 */
createCaseListGridPanel = function() {
	var bbar = new Ext.PagingToolbar({
				pageSize : 50,
				store : itemListStore,
				displayInfo : true,
				displayMsg : 'Displaying topics {0} - {1} of {2}',
				emptyMsg : "No topics to display",
				paramNames : {
					start : 'START_INDEX',
					limit : 'PAGE_SIZE',
					sort : 'SORT',
					dir : 'DIR'
				},
				items : ['-', {
							id:'comboSearchKey',
							xtype : 'combo'
						}, {
							text : '搜尋 ',
							iconCls : 'search',
							scale: 'medium',
							handler : function(btn, pressed) {
								itemListStore.load(createLoadBidItemsParams(
										itemListStore, 0, 50, '', '',
										loadBidItemsData));
							}
						}]
			});

	var gridTextEditor = new Ext.form.TextField({
				readOnly : true
			});

	var cm = new Ext.grid.ColumnModel({
				// specify any defaults for each column
				defaults : {
					sortable : true
					// columns are not sortable by default
				},
				columns : [new Ext.grid.RowNumberer(), {
							header : "狀態",
							dataIndex : 'status',
							width : 40
						},/* {
							header : "新訊息",
							dataIndex : 'id',
							width : 20,
						}, */{
							header : "得標者",
							dataIndex : 'user_name',
							editor : new Ext.form.TextField({
										readOnly : true
									})
						}, {
							header : "下標帳號",
							dataIndex : 'jyahooid',
							editor : new Ext.form.TextField({
										readOnly : true
									})
						}, {
							header : "摩根得標編號",
							dataIndex : 'no',
							width : 100,
							editor : new Ext.form.TextField({
										readOnly : true
									})
						}, {
							header : "結標日",
							dataIndex : 'end_date',
							width : 150,
							editor : new Ext.form.TextField({
										readOnly : true
									})
						}, {
							header : "商品ID",
							dataIndex : 'item_id',
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							width : 100
						}, {
							header : "商品名",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'item',
							width : 250
						}, {
							header : "賣家帳號",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'sell_name'
						}, {
							header : "賣家 e-mail",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'creator'
						}, {
							header : "連絡方式",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'contact_type'
						}, {
							header : "e-mail通知",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'creator'
						}, {
							header : "留言版通知",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'creator'
						}, {
							header : "填寫交易form",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'creator'
						}, {
							header : "填寫堨示板",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'creator'
						}, {
							header : "消費稅",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'tax'
						}, {
							header : "落札価格",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'costed'
						}, {
							header : "運費",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'locally'
						}, {
							header : "匯款費用",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'remittance'
						}, {
							header : "合計",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'creator'
						}, {
							header : "匯款帳戶",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'creator'
						}, {
							header : "匯款狀況",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'status'
						}]
			});

	var grid = new Ext.grid.EditorGridPanel({
				id : 'editorGridItemList',
				trackMouseOver : true,
				stripeRows : true,
				disableSelection : true,
				stateful : true,
				loadMask : true,
				height : 250,
				flex : 1,
				store : itemListStore,
				sm : new Ext.grid.RowSelectionModel({
							singleSelect : true,
							listeners : {
								rowselect : function(sm, row, rec) {
									Ext.getCmp("itemPanel").getForm()
											.loadRecord(rec);
								}
							}
						}),
				cm : cm,
				tbar : [{
							xtype : 'buttongroup',
							// columns : 1,
							id : 'btnGpStstusKey',
							items : [{
										xtype : 'button',
										text : '已得標未處理',
										pressed : true,
										enableToggle : true
									}, {
										xtype : 'button',
										text : '已取得賣家連絡資料',
										pressed : true,
										enableToggle : true
									}, {
										xtype : 'button',
										text : '完成匯款',
										pressed : true,
										enableToggle : true
									}, {
										xtype : 'button',
										text : '日本收貨完成',
										pressed : true,
										enableToggle : true
									}, {
										xtype : 'button',
										text : '日本出貨完成',
										enableToggle : true
									}, {
										xtype : 'button',
										text : '台灣收貨完成',
										enableToggle : true
									}, {
										xtype : 'button',
										text : '會員結帳完成 ',
										enableToggle : true
									}, {
										xtype : 'button',
										text : '台灣出貨完成 ',
										enableToggle : true
									}]
						}],
				bbar : bbar
			});

	grid.on('rowclick', clickItem);
	itemListStore.load(createLoadBidItemsParams(
			itemListStore, 0, 50, '', '',
			loadBidItemsData));

	return grid;
};

/**
 * 案件詳細資訊
 */
createDetilPanel = function() {
	var detilPanel = new Ext.TabPanel({
				activeTab : 0,
				items : [{
							title : '商品詳細資訊',
							// html : 'A simple tab'
							layout : 'fit',
							items : createItemPanel()
						}, {
							title : '聯絡紀錄',
							layout : 'fit',
							items : createMsgPanel()
						}, {
							title : '發送訊息',
							layout : 'fit',
							items : createMsgSenderPanel()
						}]
			});
	return detilPanel;
};

createItemDataPanel = function() {

}

createMsgPanel = function() {
	var grid = new Ext.grid.EditorGridPanel({
		id : 'msgRecordPanel',
		title : '連絡記錄',
		store : msgRecordStore,
		trackMouseOver : true,
		disableSelection : false,
		loadMask : true,
		viewConfig : {
			forceFit : true,
			enableRowBody : true,
			showPreview : true,
			getRowClass : function(record, rowIndex, p, store) {
				if (this.showPreview) {
					p.body = '<p><input type="button" value="複製到賣方資訊" onclick="copyConactDataToRenote('
							+ rowIndex
							+ ')"/> <input type="button" value="讀取"/></p>'
							+ '<p>' + record.data.msg_contact + '</p>';
					return 'x-grid3-row-expanded';
				}
				return 'x-grid3-row-collapsed';
			}
		},
		columns : [/*
					 * { header : "", renderer :
					 * rendererReadMsg, dataIndex :
					 * 'is_read', width : 30, sortable : true },
					 */{
					header : "留言帳號",
					dataIndex : 'msg_from',
					width : 100,
					sortable : true
				}, {
					header : "主旨",
					dataIndex : 'msg_title',
					width : 150,
					// hidden : true,
					sortable : true
				}, {
					header : "留言時間",
					dataIndex : 'msg_date',
					width : 70,
					align : 'right',
					sortable : true
				}, {
					header : "讀取時間",
					dataIndex : 'read_date',
					width : 70,
					align : 'right',
					renderer : rendererFixDate,
					sortable : true
				}],
		bbar : new Ext.PagingToolbar({
					pageSize : 25,
					store : msgRecordStore,
					displayInfo : true,
					displayMsg : '留言筆數 {0} - {1} of {2}',
					emptyMsg : "No topics to display",
					items : ['-', {
								pressed : true,
								enableToggle : true,
								text : 'Show Preview',
								cls : 'x-btn-text-icon details',
								toggleHandler : function(btn, pressed) {
									var view = grid.getView();
									view.showPreview = pressed;
									view.refresh();
								}
							}]
				})

	});
	return grid;
}

createMsgSenderPanel = function() {
	var msgCategoryData = [
			[
					'0',
					'留言版',
					[['1', '送付先住所、支払い、発送などについて'], ['2', '支払いが完了しました'],
							['3', '商品を受け取りました'], ['4', 'その他']]],
			['1', 'e-mail', []],
			['2', '揭示版', [['no', '公開しない'], ['yes', '公開する']]]];
	// var msgCategoryData = [['1', '留言板'], ['2', 'e-Mail'], ['3', '揭示板']];
	var msgCategoryStore = new Ext.data.SimpleStore({// 下拉式選單資料
		fields : ['value', 'text', 'data'],
		data : msgCategoryData
	});

	var msgTitleStore = new Ext.data.SimpleStore({// 下拉式選單資料
		fields : ['value', 'text'],
		data : msgCategoryStore.getAt(0).get('data')
	});

	var comboMsgCategory = new Ext.form.ComboBox({
				id : 'comboMsgCategory',
				fieldLabel : '類型',
				store : msgCategoryStore,
				mode : 'local',
				id : 'comboMsgCategory',
				triggerAction : 'all',
				width : 200,
				valueField : 'value',
				hiddenName : "SEND_METHOD",
				displayField : 'text',
				editable : false,
				value : '0',
				readOnly : true
			});

	var comboMsgTitle = new Ext.form.ComboBox({
				id : 'comboMsgTitle',
				fieldLabel : '訊息標題',
				store : msgTitleStore,
				mode : 'local',
				width : 300,
				triggerAction : 'all',
				valueField : 'value',
				hiddenName : "SUBJECT_A",
				lazyRender : true,
				displayField : 'text',
				value : '1',
				readOnly : true,
				editable : false
			});

	comboMsgCategory.on('select', fixComboMsgTitle);

	var simple = new Ext.FormPanel({
				id : 'msgSenderPanel',
				labelWidth : 75,
				defaultType : 'textfield',
				frame : true,
				url : 'AjaxPortal',
				items : [{
							xtype : 'panel',
							layout : 'vbox',
							height : 60,
							// width : 200,
							anchor : '90%',
							layoutConfig : {
								align : 'stretch',
								pack : 'start'
							},

							itemCls : 'mogan-item-id',
							bodyStyle : 'padding:5px 5px 5px 5px',
							items : [{
										xtype : 'label',
										id : 'labelMsgItemId',
										text : '商品ID:-',
										cls : 'mogan-item-id',
										anchor : '100%',
										hideLabel : true
									}, {
										xtype : 'label',
										id : 'labelMsgItemName',
										text : '商品名稱:-',
										cls : 'mogan-item-id',
										anchor : '100%',
										hideLabel : true
									}]
						}, comboMsgCategory, comboMsgTitle, {
							id : 'textfieldMsgTitle',
							name : 'SUBJECT_B',
							width : '350',
							disabled : true,
							fieldLabel : ''
						}, {
							xtype : 'textarea',
							name : "MSG",
							height : 200,
							anchor : '90%-100',
							fieldLabel : '內容'
						}, {
							xtype : 'hidden',
							id : 'hiddenMsgItemId',
							name : 'ITEM_ID',
							value : ''
						}, {
							xtype : 'hidden',
							id : 'hiddenBidAccount',
							name : 'BID_ACCOUNT',
							value : ""
						}, {
							xtype : 'hidden',
							name : 'WEB_SITE_ID',
							value : '1'
						}, {
							xtype : 'hidden',
							name : 'ACTION',
							value : 'SEND_MESSAGE'
						}, {
							xtype : 'hidden',
							name : 'APP_ID',
							value : appId
						}, {
							xtype : 'hidden',
							name : 'MODEL_NAME',
							value : "BidManager"
						}],
				buttons : [{
							text : '送出',
							handler : function() {
								Ext.getCmp("msgSenderPanel").getForm().submit({
											method : 'POST'
										});
							}
						}]
			})

	return simple;
}

/**
 * 主要操作畫面，包含列表及項細資訊
 * 
 * @return {}
 */
createCenterPanel = function() {
	var centerPanel = new Ext.Panel({
				layout : 'border',
				items : [{
							title : '案件列表',
							region : 'north',
							html : 'grid',
							height : 250,
							split : true,
							collapsible : true,
							layout : 'fit',
							items : createCaseListGridPanel()
						}, {
							region : 'center',
							layout : 'fit',
							title : '案件詳細資訊',
							items : createDetilPanel()
						}]
			});

	return centerPanel;
}

/**
 * 
 * @return {}
 */
createItemPanel = function() {
	var panel = new Ext.FormPanel({
		id : 'itemPanel',
		labelWidth : 75, // label settings here cascade unless
		// overridden
		frame : true,
		bodyStyle : 'padding:5px 5px 0',
		// width : 350,
		defaults : {
			width : 230
		},
		// height : 300,
		layout : 'vbox',
		layoutConfig : {
			align : 'stretch',
			pack : 'start'
		},
		buttonAlign : 'left',
		defaultType : 'textfield',
		// http://www.mogan.com.tw/adminv2/bidding_config_handle.php?rid=38658
		items : [{
			xtype : 'button',
			text : '開啟舊版畫面',
			scale : 'medium',
			handler : function() {
				//alert(Ext.getCmp("itemPanel").getForm().getValues()['no']);
				
				window
						.open(
								'http://www.mogan.com.tw/adminv2/bidding_config_handle.php?rid='
										+ Ext.getCmp("itemPanel").getForm().getValues()['no'],
								'height=100, width=400, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=n o, status=no');
			}
		}, {
			xtype : 'panel',
			layout : 'column',
			width : '100%',
			bodyStyle : 'padding:5px 5px 5px 5px',
			items : [{
						width : 650,
						xtype : 'fieldset',
						labelWidth : 75,
						defaultType : 'textfield',
						defaults : {
							readOnly : true
						},
						autoHeight : true,
						border : false,
						title : '商品資料',
						items : [{
									xtype : 'hidden',
									width : 400,
									name : 'id'
								},{
									xtype : 'hidden',
									width : 400,
									name : 'no'
								}, {
									fieldLabel : '商品名稱',
									width : 400,
									name : 'item'
								}, {
									fieldLabel : '商品ID',
									name : 'item_id'
								}, {
									fieldLabel : '下標帳號',
									name : 'jyahooid'
								}, {
									fieldLabel : '會員帳號',
									name : 'user_name'
								}, {
									fieldLabel : '賣家帳號',
									name : 'sell_name'
								}]
					}, {
						xtype : 'fieldset',
						labelWidth : 75,
						// width : 400,
						defaultType : 'textfield',
						autoHeight : true,
						border : false,
						title : '交易明細',
						items : [{
									fieldLabel : '匯款帳號',
									width : 200,
									name : 'title'
								}, {
									xtype : 'textarea',
									fieldLabel : '賣方資訊&內容',
									labelWidth : 100,
									width : 400,
									name : 'renote'
								}]
					}]
		}, {
			xtype : 'panel',
			layout : 'column',
			items : [{
						title : '相關費用',
						xtype : 'fieldset',
						labelWidth : 75,
						bodyStyle : 'margin:5px 5px 5px 5px;',
						style : {
							'margin-right' : '10px'
						},
						defaultType : 'textfield',
						autoHeight : true,
						// border : false,
						items : [{
									fieldLabel : '得標價格(日)',
									name : 'costed'
								}, {
									fieldLabel : '當地運費(日)',
									name : 'locally'
								}, {
									fieldLabel : '匯款費用(日)',
									name : 'remittance'
								}, {
									fieldLabel : '稅金費用(日)',
									name : 'tax'
								}, {
									fieldLabel : '其他費用(日)',
									name : 'other'
								}, {
									fieldLabel : '手續費用(台)',
									name : 'charge'
								}]
					}, {
						title : '匯款確認',
						xtype : 'fieldset',
						disabled : true,
						labelWidth : 75,
						bodyStyle : 'margin:5px 5px 5px 5px',
						style : {
							'margin-right' : '10px'
						},
						defaultType : 'textfield',
						autoHeight : true,
						// border : false,
						items : [{
									fieldLabel : '得標價格(日)',
									name : 'costed'
								}, {
									fieldLabel : '當地運費(日)',
									name : 'locally'
								}, {
									fieldLabel : '匯款費用(日)',
									name : 'remittance'
								}, {
									fieldLabel : '稅金費用(日)',
									name : 'tax'
								}, {
									fieldLabel : '其他費用(日)',
									name : 'other'
								}, {
									fieldLabel : '手續費用(台)',
									name : 'charge'
								}]
					}, {
						title : '匯款',
						xtype : 'fieldset',
						disabled : true,
						labelWidth : 75,
						bodyStyle : 'margin:5px 5px 5px 5px',
						style : {
							'margin-right' : '10px'
						},
						defaultType : 'textfield',
						autoHeight : true,
						// border : false,
						items : [{
									fieldLabel : '得標價格(日)',
									name : 'costed'
								}, {
									fieldLabel : '當地運費(日)',
									name : 'locally'
								}, {
									fieldLabel : '匯款費用(日)',
									name : 'remittance'
								}, {
									fieldLabel : '稅金費用(日)',
									name : 'tax'
								}, {
									fieldLabel : '其他費用(日)',
									name : 'other'
								}, {
									fieldLabel : '手續費用(台)',
									name : 'charge'
								}]
					}, {
						title : '物流同捆',
						xtype : 'fieldset',
						disabled : true,
						labelWidth : 75,
						bodyStyle : 'margin:5px 5px 5px 5px',
						style : {
							'margin-right' : '10px'
						},
						defaultType : 'textfield',
						autoHeight : true,
						// border : false,
						items : [{
									fieldLabel : '得標價格(日)',
									name : 'costed'
								}, {
									fieldLabel : '當地運費(日)',
									name : 'locally'
								}, {
									fieldLabel : '匯款費用(日)',
									name : 'remittance'
								}, {
									fieldLabel : '稅金費用(日)',
									name : 'tax'
								}, {
									fieldLabel : '其他費用(日)',
									name : 'other'
								}, {
									fieldLabel : '手續費用(台)',
									name : 'charge'
								}]
					}]
		}],
		buttons : [{
					text : 'Save'
				}]
	});
	// layout:'form'
	return panel;
}