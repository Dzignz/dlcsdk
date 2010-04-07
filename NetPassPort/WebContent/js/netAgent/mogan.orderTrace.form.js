Ext.namespace("Mogan.orderTrace");

/**
 * 畫面上方按鈕集
 * 
 * @return {}
 */
Mogan.orderTrace.createHeadPanel = function() {
	var btns = new Array([

	new Ext.Button({
				scale : 'medium',
				text : '顯示得標清單',
				id : 'btnUploadFile',
				handler : function() {
					// itemListStore.load();
					Mogan.orderTrace.itemListStore.load(Mogan.orderTrace
							.createLoadBidItemsParams(
									Mogan.orderTrace.itemListStore, 0, 50, '',
									'', Mogan.orderTrace.loadBidItemsData));

				}
			}), new Ext.Button({
				scale : 'medium',
				text : '隱藏 / 顯示  案件列表',
				handler : function() {
					Mogan.mail.setMail();
				},
				id : 'btnStartSend'
			}), new Ext.Button({
				scale : 'medium',
				text : '教學影片',
				iconCls : 'video',
				handler : function() {
					window.open('/demo/MailService_demo/MailService_demo.htm');
				}
			})]);
	// alert(btns.length);
	return btns;
}

/**
 * 案件列表
 * 
 * @return {}
 */
Mogan.orderTrace.createCaseListGridPanel = function() {
	var bbar = new Ext.PagingToolbar({
				pageSize : 50,
				store : Mogan.orderTrace.itemListStore,
				displayInfo : true,
				displayMsg : 'Displaying topics {0} - {1} of {2}',
				emptyMsg : "No topics to display",
				items : ['-', {
							store : Mogan.orderTrace.accountListStore,
							displayField : 'diaplay_account',
							model : 'local',
							valueField : 'bid_id',
							id : 'comboAccount',
							triggerAction : 'all',
							editable : false,
							typeAhead : true,
							emptyText : '篩選帳號...',
							xtype : 'combo'
						}, {
							id : 'comboSearchKey',
							xtype : 'combo'
						}, {
							id : 'butSearchOrder',
							text : '搜尋 ',
							iconCls : 'search',
							scale : 'medium',
							handler : function(btn, pressed) {
								Mogan.orderTrace.itemListStore.load();
							}
						}, {
							xtype : 'checkbox',
							boxLabel : '標記同賣家 '
						},' ', {
							xtype : 'checkbox',
							boxLabel : '標記同買家 '
						}]
			});

	var cm = new Ext.grid.ColumnModel({
				// specify any defaults for each column
				defaults : {
					sortable : true
					// columns are not sortable by default
				},
				columns : [new Ext.grid.RowNumberer(), {
							header : " ",
							dataIndex : 'new_msg',
							width : 20
						}, {
							header : " ",
							editor : new Ext.form.TextField({readOnly : true}),
							dataIndex : 'website_id',
							width : 30
						},{
							header : "匯款狀況",
							editor : new Ext.form.TextField(),
							dataIndex : 'status',
							width : 30
						},{
							header : "會員帳號",
							dataIndex : 'name',
							editor : new Ext.form.TextField({
										readOnly : true
									})
						}, {
							header : "會員名稱",
							editor : new Ext.form.TextField(),
							dataIndex : 'full_name'
						}, {
							header : "下標帳號",
							dataIndex : 'buyer_account',
							editor : new Ext.form.TextField()
						}, {
							header : "摩根得標編號",
							dataIndex : 'item_order_id',
							width : 100,
							editor : new Ext.form.TextField()
						}, {
							header : "結標日",
							dataIndex : 'e_date02',
							width : 150,
							editor : new Ext.form.TextField()
						}, {
							header : "商品ID",
							dataIndex : 'item_id',
							editor : new Ext.form.TextField(),
							width : 100
						}, {
							header : "商品名",
							editor : new Ext.form.TextField(),
							dataIndex : 'item_name',
							width : 250
						}, {
							header : "賣家帳號",
							editor : new Ext.form.TextField(),
							dataIndex : 'seller_account'
						}, {
							header : "累計成交數量",
							editor : new Ext.form.TextField(),
							dataIndex : 'total_item'
						}, {
							header : "累計未付款數量",
							editor : new Ext.form.TextField(),
							renderer : Mogan.orderTrace.rendererFixNonRemitCount,
							dataIndex : 'total_unpay'
						}, {
							header : "累計未出貨數量",
							editor : new Ext.form.TextField(),
							dataIndex : 'total_unship'
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
							dataIndex : 'contact_type',
							renderer : Mogan.orderTrace.rendererContactType
						}, {
							header : "填寫交易form",
							renderer : Mogan.orderTrace.rendererOrderForm,
							dataIndex : 'o_varchar02'
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
							dataIndex : 'buy_price'
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
				store : Mogan.orderTrace.itemListStore,
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
				bbar : bbar,
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
						}]
			});
	grid.on('rowclick', Mogan.orderTrace.clickItem);
	Mogan.orderTrace.itemListStore.load();
	return grid;
};

/**
 * 案件詳細資訊
 */
Mogan.orderTrace.createDetilPanel = function() {
	var detilPanel = new Ext.TabPanel({
				activeTab : 0,
				id : 'DetilPanel',
				disabled : true,
				// layout : 'fit',
				items : [{
							title : '商品詳細資訊',
							// html : 'A simple tab'
							layout : 'fit',
							items : Mogan.orderTrace.createItemPanel()
						}, {
							title : '聯絡紀錄',
							layout : 'fit',
							items : Mogan.orderTrace.createMsgPanel()
						}, {
							title : '發送訊息',
							layout : 'fit',
							items : Mogan.orderTrace.createMsgSenderPanel()
						}, {
							title : 'order form',
							id : 'tabItemOrderForm',
							contentEl : 'tab-iframe-window-1',
							layout : 'fit'
						}]
			});
	return detilPanel;
};

Mogan.orderTrace.createMsgPanel = function() {
	var grid = new Ext.grid.EditorGridPanel({
		id : 'msgRecordPanel',
		title : '連絡記錄',
		store : Mogan.orderTrace.msgRecordStore,
		trackMouseOver : true,
		disableSelection : false,
		loadMask : true,
		sm : new Ext.grid.RowSelectionModel({
					singleSelect : true
				}),

		viewConfig : {
			autoFill : true,
			// forceFit : true,
			enableRowBody : true,
			showPreview : true,
			editor : new Ext.form.TextField(),
			getRowClass : function(record, rowIndex, p, store) {
				if (this.showPreview) {
					p.body = '<p><input type="button" value="複製到賣方資訊" onclick="Mogan.orderTrace.copyConactDataToRenote('
							+ rowIndex
							+ ')"/></p>'
							+ '<p>'
							+ record.data.msg_contact + '</p>';
					return 'mogan-selectable';
				}
				return 'x-grid3-row-collapsed';
			},
			templates : {
				cell : new Ext.Template(
						'<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} mogan-selectable {css}" style="{style}" tabIndex="0" {cellAttr}>',
						'<div class="x-grid3-cell-inner x-grid3-col-{id}" {attr}>{value}</div>',
						'</td>')
			}
		},
		columns : [{
					header : "訊息來源",
					renderer : Mogan.orderTrace.rendererReadMsg,
					dataIndex : 'is_read',
					width : 30,
					sortable : true
				}, {
					header : "留言帳號",
					dataIndex : 'msg_from',
					editor : new Ext.form.TextField(),
					width : 100,
					sortable : true
				}, {
					header : "主旨",
					dataIndex : 'msg_title',
					editor : new Ext.form.TextField(),
					width : 150,
					// hidden : true,
					sortable : true
				}, {
					header : "留言時間",
					dataIndex : 'msg_date',
					editor : new Ext.form.TextField(),
					width : 70,
					align : 'right',
					sortable : true
				}, {
					header : "讀取時間",
					dataIndex : 'read_date',
					width : 70,
					align : 'right',
					editor : new Ext.form.TextField(),
					renderer : Mogan.orderTrace.rendererFixDate,
					sortable : true
				}],
		bbar : new Ext.PagingToolbar({
					pageSize : 25,
					store : Mogan.orderTrace.msgRecordStore,
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

Mogan.orderTrace.createMsgSenderPanel = function() {
	var msgCategoryData = [
			[
					'0',
					'留言版',
					[['1', '送付先住所、支払い、発送などについて'], ['2', '支払いが完了しました'],
							['3', '商品を受け取りました'], ['4', 'その他']]],
			['1', 'e-mail', []],
			['2', '揭示版', [['no', '公開しない'], ['yes', '公開する']]]];

	var msgCategoryStore = new Ext.data.SimpleStore({// 下拉式選單資料
		fields : ['value', 'text', 'data'],
		data : msgCategoryData
	});

	var msgTitleStore = new Ext.data.SimpleStore({// 下拉式選單資料
		fields : ['value', 'text'],
		data : msgCategoryStore.getAt(0).get('data')
	});

	var comboMsgCategory = new Ext.form.ComboBox({
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
	comboMsgCategory.on('select', Mogan.orderTrace.fixComboMsgTitle);

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

	var comboMsgTemplate = new Ext.form.ComboBox({
				id : 'comboMsgTemplate',
				fieldLabel : '訊息範本',
				store : Mogan.orderTrace.templateListStore,
				mode : 'local',
				width : 300,
				triggerAction : 'all',
				valueField : 'templateIndex',
				lazyRender : true,

				displayField : 'fileName',
				// value : '0',
				// readOnly : true,
				editable : true
			});

	comboMsgTemplate.on('select', Mogan.orderTrace.fixTextareaMsgContent);
	// comboMsgTemplate.on('blur', Mogan.orderTrace.updateComboMsgTitle);

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
						}, comboMsgTemplate, {
							id : 'textareaMsgContent',
							xtype : 'textarea',
							name : "MSG",
							// height : 200,
							grow : true,
							growMax : 200,
							anchor : '90% -175',
							fieldLabel : '內容'
						}, {
							xtype : 'hidden',
							id : 'hiddenItemOrderId',
							name : 'ITEM_ORDER_ID',
							value : ''
						}],
				buttons : [{
							id : 'msgChangeTemplateMode',
							text : '範本編輯模式',
							handler : function(b, e) {
								Mogan.orderTrace.changeTemplateMode();
							}
						}, {
							id : 'msgSaveBtn',
							text : '儲存',
							disabled : true,
							handler : function() {
								Mogan.orderTrace.saveMsg(0);
							}
						}, {
							id : 'msgSaveAsBtn',
							text : '另儲新副本',
							disabled : true,
							handler : function() {
								Mogan.orderTrace.saveMsg(1);
							}
						}, {
							id : 'msgDelBtn',
							text : '刪除副本',
							disabled : true,
							handler : function() {
								Mogan.orderTrace.delMsg();
							}
						}, {
							id : 'msgTrnsListBtn',
							text : '顯示對應列表',
							handler : function() {
								Mogan.orderTrace.showMsgTrnsList();
							}
						}, {
							id : 'msgSendBtn',
							text : '送出',
							handler : function() {
								Mogan.orderTrace.sendMsg();
							}
						}]
			})

	return simple;
}

/**
 * 名稱對應列表
 * 
 * @return {}
 */
Mogan.orderTrace.createTrnsListGird = function() {
	var grid = new Ext.grid.EditorGridPanel({
		store : Mogan.orderTrace.trnsListStore,
		// layout : 'fit',
		// width: 400,
		// height: 360,
		// anchor:'100% 100%',
		columns : [{
					id : 'trnsCode',
					header : "代碼",
					editor : new Ext.form.TextField(),
					dataIndex : 'trnsCode',
					width : 180,
					sortable : true
				}, {
					id : 'trnsData',
					header : "對應資料欄位",
					dataIndex : 'trnsData',
					editor : new Ext.form.ComboBox({
								typeAhead : true,
								triggerAction : 'all',
								store : Mogan.orderTrace.trnsColmListStore,
								mode : 'local',
								valueField : 'columnName',
								lazyRender : true,
								displayField : 'columnDesc',
								readOnly : true,
								editable : false
							}),
					width : 100,
					// sortable : true,

					renderer : function(value) {
						if (Mogan.orderTrace.trnsColmListStore.find(
								'columnName', value) == -1) {
							return '';
						} else {
							return Mogan.orderTrace.trnsColmListStore
									.getAt(Mogan.orderTrace.trnsColmListStore
											.find('columnName', value))
									.get('columnDesc');
						}

						return Mogan.orderTrace.trnsColmListStore
								.getAt(Mogan.orderTrace.trnsColmListStore.find(
										'columnName', value)).get('columnDesc');
					}

				}],
		tbar : new Ext.Toolbar(['-', {
			text : '新增項目',
			handler : function() {
				var defaultData = {
					trnsCode : '',
					trnsData : ''
				};
				var p = new Mogan.orderTrace.trnsListStore.recordType(defaultData);
				Mogan.orderTrace.trnsListStore.insert(0, p);
				grid.startEditing(0, 0);
			}
		}, '-', {
			text : '刪除項目',
			handler : function() {
				var sm = grid.getSelectionModel();
				var cell = sm.getSelectedCell();
				var record = Mogan.orderTrace.trnsListStore.getAt(cell[0]);
				Ext.Msg.confirm('訊息', '確定要刪除選定的資料嗎？ \n('
								+ record['data']['trnsCode'] + ' / '
								+ record['data']['trnsData'] + ')', function(
								btn) {
							if (btn == 'yes') {
								Mogan.orderTrace.trnsListStore.remove(record);
							}
						});
			}
		}, '-', '->', {
			xtype : 'button',
			id : 'trnsListSaveBtn',
			text : '儲存',
			handler : function() {
				Ext.getCmp('trnsListSaveBtn').setText('儲存中.....');
				Mogan.orderTrace.saveTrnsList();
			}
		}]),/*
			 * bbar : [{ xtype : 'button', id : 'trnsListSaveBtn', text : '儲存',
			 * handler : function() { // Mogan.orderTrace.saveMsg(0); } }],//
			 */
		stripeRows : true,
		autoExpandColumn : 'trnsData',
		height : 350,
		width : 600,
		// title : 'Array Grid',
		// config options for stateful behavior
		stateful : true
			// stateId : 'grid'
	});

	return grid;
};

/**
 * 
 * @return {}
 */
Mogan.orderTrace.createItemPanel = function() {
	var panel = new Ext.FormPanel({
		id : 'itemPanel',
		labelWidth : 75, // label settings here cascade unless
		frame : true,
		bodyStyle : 'padding:5px 5px 0',
		defaults : {
			width : 230
		},
		layoutConfig : {
			align : 'stretch',
			pack : 'start'
		},
		buttonAlign : 'left',
		defaultType : 'textfield',
		// *
		items : [{
			xtype : 'panel',
			layout : 'column',
			width : '100%',
			items : [{
				xtype : 'button',
				text : '開啟舊版畫面',
				scale : 'medium',
				handler : function() {
					window
							.open(
									'http://www.mogan.com.tw/adminv2/bidding_config_handle.php?rid='
											+ Ext.getCmp("itemPanel").getForm()
													.getValues()['no'],
									'height=100, width=400, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=n o, status=no');
				}
			}, {
				xtype : 'button',
				text : '開啟オーダーフォーム(Order Form)',
				scale : 'medium',
				style : 'padding:0px 5px 0px',
				handler : function() {
					Mogan.orderTrace.openItemOrderForm();
				}
			}]
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
								}, {
									xtype : 'hidden',
									width : 400,
									name : 'no'
								}, {
									xtype : 'hidden',
									width : 400,
									name : 'order_form_status'
								}, {
									fieldLabel : '商品名稱',
									width : 400,
									name : 'item'
								}, {
									fieldLabel : '商品ID',
									name : 'item_id'
								}, {
									fieldLabel : '下標帳號',
									name : 'agent_account'
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
		}],
		buttons : [{
					text : 'Save'
				}]
			// */
	});
	return panel;
};