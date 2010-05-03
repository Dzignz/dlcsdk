Ext.namespace("Mogan.orderTrace");

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
						}, '', {
							id : 'comboSearchKey',
							store : Mogan.orderTrace.searchKeyStore,
							model : 'local',
							valueField : 'key',
							displayField : 'value',
							triggerAction : 'all',
							xtype : 'combo'
						}, {
							id : 'butSearchOrder',
							text : '搜尋 ',
							iconCls : 'search',
							scale : 'medium',
							handler : function(btn, pressed) {
								Mogan.orderTrace.itemListStore.load();
							}
						}, '-', {
							id : 'checkboxSameSeller',
							xtype : 'checkbox',
							boxLabel : '帶出同賣家'
						}, '-', {
							iconCls : 'falg',
							disabled : true
						}, {
							text : '顏色標記',
							xtype : 'label'
						}, '', {
							id : 'radioColorOrder',
							xtype : 'radio',
							name : 'colorgroup',
							boxLabel : '同訂單',
							inputValue : 'orderColor',
							handler : function() {
								Mogan.orderTrace
										.refreshGrid('editorGridItemList');
							}
						}, {
							id : 'radioColorMember',
							xtype : 'radio',
							name : 'colorgroup',
							boxLabel : '同會員',
							inputValue : 'memberColor',
							handler : function() {
								Mogan.orderTrace
										.refreshGrid('editorGridItemList');
							}
						}, {
							id : 'radioColorSeller',
							xtype : 'radio',
							name : 'colorgroup',
							boxLabel : '同賣家',
							inputValue : 'sellerColor',
							handler : function() {
								Mogan.orderTrace
										.refreshGrid('editorGridItemList');
							}
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
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'website_id',
							renderer : Mogan.orderTrace.rendererFixWebsiteIcon,
							width : 30
						}, {
							header : "訂單編號",
							dataIndex : 'tide_id',
							width : 100
						}, {
							header : "訂單狀態",
							dataIndex : 'order_status',
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							width : 100,
							renderer : Mogan.orderTrace.rendererFixOrderStatus
						}, {
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
							header : "得標日",
							dataIndex : 'time_at_03',
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
							header : "未付款數量",
							editor : new Ext.form.TextField(),
							width : 80,
							renderer : Mogan.orderTrace.rendererFixNonRemitCount,
							dataIndex : 'non_pay'
						}, {
							header : "同會員未付款",
							editor : new Ext.form.TextField(),
							width : 100,
							renderer : Mogan.orderTrace.rendererFixNonRemitCount,
							dataIndex : 'member_non_pay'
						}, {
							header : "未出貨數量",
							editor : new Ext.form.TextField(),
							width : 100,
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
							header : "匯款帳戶",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'creator'
						}]
			});

	var gridViewConfig = {
		forcefit : true,
		enableRowBody : true,
		getRowClass : function(record, rowIndex, P, ds) {
			var cls = 'white-row';
			// key=Ext.getCmp('radioColorMember').getGroupValue();
			switch (record.data[Ext.getCmp('radioColorMember').getGroupValue()]) {
				case 1 :
					cls = 'mogan-grid-row-1';
					break;
				case 2 :
					cls = 'mogan-grid-row-2';
					break;
				case 3 :
					cls = 'mogan-grid-row-3';
					break;
				case 4 :
					cls = 'mogan-grid-row-4';
					break;
				case 5 :
					cls = 'mogan-grid-row-5';
					break;
				case 6 :
					cls = 'mogan-grid-row-6';
					break;
				case 7 :
					cls = 'mogan-grid-row-7';
					break;
				case 8 :
					cls = 'mogan-grid-row-8';
					break;
				case 9 :
					cls = 'mogan-grid-row-9';
					break;
			}
			return cls;
		}
	};

	var grid = new Ext.grid.EditorGridPanel({
				id : 'editorGridItemList',
				trackMouseOver : true,
				stripeRows : true,
				disableSelection : true,
				stateful : true,
				loadMask : true,
				// height : 250,
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
				viewConfig : gridViewConfig,
				tbar : [{
							xtype : 'buttongroup',
							// columns : 1,
							id : 'btnGpStstusKey',
							items : [{
										id : 'btnGpBtn_3-01',
										xtype : 'button',
										text : '連絡中',
										pValue : '3-01',
										pressed : true,
										enableToggle : true
									}, {
										id : 'btnGpBtn_3-02',
										xtype : 'button',
										text : '取得連絡',
										pValue : '3-02',
										pressed : true,
										enableToggle : true
									}, {
										id : 'btnGpBtn_3-03',
										xtype : 'button',
										text : '待匯款',
										pValue : '3-03',
										pressed : false,
										enableToggle : true
									}, {
										id : 'btnGpBtn_3-04',
										xtype : 'button',
										text : '已匯款',
										pValue : '3-04',
										pressed : false,
										enableToggle : true
									}, {
										id : 'btnGpBtn_3-05',
										xtype : 'button',
										text : '賣家已發貨',
										pValue : '3-05',
										pressed : false,
										enableToggle : true
									}, {
										id : 'btnGpBtn_3-06',
										xtype : 'button',
										text : '購買點已收貨',
										pValue : '3-06',
										pressed : false,
										enableToggle : true
									}, {
										id : 'btnGpBtn_3-07',
										xtype : 'button',
										text : '購買點已發貨',
										pValue : '3-07',
										pressed : false,
										enableToggle : true
									}, {
										id : 'btnGpBtn_3-08',
										xtype : 'button',
										text : '收貨點已收貨',
										pValue : '3-08',
										pressed : false,
										enableToggle : true
									}, {
										id : 'btnGpBtn_3-09',
										xtype : 'button',
										text : '收貨點已發貨',
										pValue : '3-09',
										pressed : false,
										enableToggle : true
									}, {
										id : 'btnGpBtn_3-10',
										xtype : 'button',
										text : '會員已收貨',
										pValue : '3-10',
										pressed : false,
										enableToggle : true
									}]
						}]
			});

	grid.on('rowclick', Mogan.orderTrace.clickItem);
	grid.on('rowcontextmenu', Mogan.orderTrace.showItemListMenu);
	/**
	 * 右鍵快速選單
	 */
	Mogan.orderTrace.itemListMenu = new Ext.menu.Menu({
		id : 'itemListMenu',
		items : [{
					id : 'itemListMenuSameSeller',
					text : '篩選同賣家',
					icon : './resources/mogan/images/1271740251_user_business.png',
					handler : Mogan.orderTrace.clickItemListmenu
				}, {
					id : 'itemListMenuSameMember',
					text : '篩選同買家',
					icon : './resources/mogan/images/1271740286_user.png',
					handler : Mogan.orderTrace.clickItemListmenu
				}, {
					id : 'itemListMenuSameOrder',
					text : '篩選同訂單',
					icon : './resources/mogan/images/1271740251_user_business.png',
					handler : Mogan.orderTrace.clickItemListmenu
				}, '-', {
					id : 'itemListMenuTransOrder',
					text : '轉換訂單',
					icon : './resources/mogan/images/1271740251_user_business.png',
					menu : {
						id : 'transOderMenu'
					}
				}, {
					id : 'itemListMenuNewOrder',
					text : '獨立訂單',
					icon : './resources/mogan/images/1271740251_user_business.png',
					handler : Mogan.orderTrace.moveItem2NewOrder
				}]
	});

	Ext.getCmp('itemListMenu').on('show', Mogan.orderTrace.getMoveableTideList);

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
				region : 'center',
				// layout : 'fit',
				title : 'item title',
				items : [{
							title : '交易訂單',
							layout : 'fit',
							items : Mogan.orderTrace.createOrderPanel()
						}, {
							title : '賣家資料',
							layout : 'fit',
							items : Mogan.orderTrace.createSellerPanel()
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
	var subPanel = new Ext.Panel({
				id : 'subPanel',
				layout : 'border',
				// disabled : true,
				items : [{
					xtype : 'label',
					text : '商品名稱',
					region : 'north'
						// ,height : 100
					}, detilPanel]
			});
	return subPanel;
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
				}),
		tbar : [{
					xtype : 'label',
					text : '商品篩選'
				}, ' ', {
					id : 'comboOrderCase',
					store : Mogan.orderTrace.orderItemListStore,
					model : 'local',
					valueField : 'item_order_id',
					displayField : 'item_id_name',
					triggerAction : 'all',
					emptyText : '不篩選',
					editable : false,
					minListWidth : 450,
					xtype : 'combo'
				}, {
					xtype : 'button',
					text : '取消篩選',
					handler : function() {
						Ext.getCmp('comboOrderCase').setValue();
					}
				}, {
					xtype : 'button',
					text : '更新資料',
					iconCls : 'refresh',
					handler : Mogan.orderTrace.refreshContactData
				}, {
					xtype : 'label',
					text : '上次更新時間：'
				}, {
					xtype : 'label',
					text : '1999 - 10 - 10 '
				}]

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
 * 取得賣家資料畫面
 * 
 * @return {}
 */
Mogan.orderTrace.createSellerPanel = function() {
	var orderDataPanel = new Ext.FormPanel({
				labelWidth : 75,
				width : 450,
				frame : true,
				defaultType : 'textfield',
				items : [{
							xtype : 'label',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							fieldLabel : '賣家帳號'
						}, {
							xtype : 'label',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							fieldLabel : '賣家名稱'
						}, {
							xtype : 'label',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							fieldLabel : 'E-mail'
						}, {
							xtype : 'label',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							fieldLabel : '電話'
						}, {
							xtype : 'label',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							fieldLabel : '成交件數'
						}, {
							xtype : 'label',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							fieldLabel : '備註'
						}]
			});
	return orderDataPanel;
}

/**
 * 建立訂單畫面
 * 
 * @return {}
 */
Mogan.orderTrace.createOrderPanel = function() {
	var toolBar = [{
				id : 'btnOrderSaveCost',
				xtype : 'button',
				handler : Mogan.orderTrace.saveOrderCostMoney,
				text : '儲存費用'
			}, {
				id : 'btnOrderSubmitCost',
				xtype : 'button',
				handler : Mogan.orderTrace.submitOrderCostMoney,
				text : '移入待匯款清單'
			}, {
				// id :'btnOrderEditNote',
				xtype : 'button',
				text : '編輯備註'
			}];
	var orderDataPanel = new Ext.FormPanel({
				id : 'orderDataPanel',
				labelWidth : 75,
				width : 450,
				// frame : true,
				defaultType : 'textfield',
				labelPad : 10,
				autoScroll : true,
				tbarStyle : 'border-bottom: 1px solid #99BBE8;',
				/*
				 * tbar : new Ext.Toolbar({ items : [{ xtype : 'button', text :
				 * '儲存訂單費用' }, { xtype : 'button', text : '確認訂單費用' }] }),
				 */
				padding : 5,
				tbar : toolBar,
				// fbar : toolBar,
				items : [{
							xtype : 'label',
							id : 'labelOrderId',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							fieldLabel : '訂單編號'
						}, {
							xtype : 'label',
							id : 'labelOrderMemberName',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							fieldLabel : '會員名稱'
						}, {
							xtype : 'label',
							id : 'labelOrderMemberMobile',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							fieldLabel : '會員手機'
						}, {
							xtype : 'label',
							id : 'labelOrderMemberEMail',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							fieldLabel : '會員E-mail'
						}, {
							xtype : 'label',
							id : 'labelOrderSellerName',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							fieldLabel : '賣家名稱'
						}, {
							xtype : 'label',
							id : 'labelOrderWebSite',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							fieldLabel : '交易網站'
						}, {
							xtype : 'label',
							id : 'labelOrderItemCount',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							fieldLabel : '商品項數'
						}, {
							xtype : 'label',
							id : 'labelOrderPackageCount',
							fieldLabel : '包裹總數',
							style : {
								clear : 'none'
							},
							width : 'auto',
							text : '未知'
						}, {
							xtype : 'numberfield',
							minValue : '0',
							id : 'textfieldOrderServiceCost',
							text : '-',
							name : 'cost_1',
							fieldLabel : '手續費'
						}, {
							xtype : 'numberfield',
							minValue : '0',
							id : 'textfieldOrderRemitCost',
							text : '-',
							name : 'cost_2',
							fieldLabel : '匯款費'
						},/*
							 * { id : 'textfieldOrderIShipCost', text : '-',
							 * name : 'cost_5', fieldLabel : '國際運費' },
							 */{
							xtype : 'numberfield',
							minValue : '0',
							id : 'textfieldOrderOtherCost',
							text : '-',
							name : 'cost_6',
							fieldLabel : '其他費用'
						}, {

							xtype : 'compositefield',
							fieldLabel : '稅金',
							items : [{
										xtype : 'numberfield',
										minValue : '0',
										id : 'textfieldOrderTaxCost',
										text : '-',
										name : 'cost_3'

									}, {
										xtype : 'button',
										iconCls : 'calculator',
										tooltipType : 'title',
										tooltip : '自動計算稅金 \n商品總費 x 0.05',
										handler : Mogan.orderTrace.calcuTax
									}]
						}, {
							xtype : 'numberfield',
							id : 'textfieldOrderShipCost',
							text : '-',
							name : 'cost_4',
							fieldLabel : '當地運費'
						}, {
							id : 'comboOrderPayType',
							store : Mogan.orderTrace.payTypeStore,
							model : 'local',
							valueField : 'list_key',
							displayField : 'list_name',
							triggerAction : 'all',
							emptyText : '請設定',
							editable : false,
							allowBlank : false,
							xtype : 'combo',
							hiddenName : 'remit_to',
							fieldLabel : '付款方式'
						}, {
							id : 'textfieldOrderMoneyAlert',
							text : '-',
							width : 250,
							name : 'money_alert',
							fieldLabel : '賣家帳戶'
						}, {
							id : 'textfieldOrderRemitOut',
							xtype : 'label',
							style : {
								clear : 'none'
							},
							text : '-',
							fieldLabel : '支出總計'
						}, {
							id : 'radiogroupOrderShipType',
							xtype : 'radiogroup',
							name : 'ship_type_group',
							tabTip : 'Tickets tabtip',
							allowBlank : false,
							items : [{
										id : 'rb_ship_type_1',
										title : 'Tickets tabtip',
										inputValue : '1',
										boxLabel : '已結清',
										name : 'ship_type'
									}, {
										id : 'rb_ship_type_0',
										boxLabel : '未結清',
										inputValue : '0',
										name : 'ship_type'
									}],
							fieldLabel : '付款狀態'
						}, {
							id : 'labelOrderNote',
							xtype : 'label',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							fieldLabel : '備忘'
						}, {
							xtype : 'hidden',
							name : 'tide_id'
						}, {
							xtype : 'hidden',
							name : 'classfly'
						}, {
							xtype : 'hidden',
							name : 'remit_id'
						}, {
							xtype : 'hidden',
							name : 'item_total_price'
						}, {
							xtype : 'hidden',
							name : 'total_cost'
						}]
			});

	var cm = new Ext.grid.ColumnModel({
				// specify any defaults for each column
				defaults : {
					sortable : true
					// columns are not sortable by default
				},
				columns : [new Ext.grid.RowNumberer(), {
							header : "得標編號",
							dataIndex : 'item_order_id',
							width : 100
						}, {
							header : "商品編號",
							dataIndex : 'item_id',
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							width : 100
						}, {
							header : "商品名稱",
							dataIndex : 'item_name',
							editor : new Ext.form.TextField({
										readOnly : true
									})
						}, {
							header : "得標價",
							editor : new Ext.form.TextField(),
							dataIndex : 'buy_price'
						}, {
							header : "得標數量",
							editor : new Ext.form.TextField(),
							dataIndex : 'buy_unit'
						}, {
							header : "下標帳號",
							dataIndex : 'bid_account',
							editor : new Ext.form.TextField()
						}, {
							header : "得標日",
							dataIndex : 'time_at_03',
							width : 150,
							editor : new Ext.form.TextField()
						}]
			});

	var orderListPanel = new Ext.FormPanel({
				flex : 1,
				title : '訂單內容(同捆清單)',
				layout : 'fit',

				items : new Ext.grid.GridPanel({
							cm : cm,
							frame : true,
							store : Mogan.orderTrace.orderItemListStore,
							trackMouseOver : true,
							id : 'orderListGrid',
							layout : 'fit',
							stripeRows : true,
							disableSelection : true,
							stateful : true,
							loadMask : true
						})
			});

	var panel = new Ext.Panel({
				id : 'orderPanel',
				frame : true,
				layout : 'hbox',
				layoutConfig : {
					align : 'stretch',
					pack : 'start'
				},
				// defaults:{margins:'0 5 0 0'},
				items : [orderDataPanel, orderListPanel]
			});

	Ext.getCmp('comboOrderPayType').on('select',
			Mogan.orderTrace.comboOrderPayTypeSelect);

	return panel;
}

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