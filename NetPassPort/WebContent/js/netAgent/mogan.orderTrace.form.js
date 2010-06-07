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
							header : " ",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'o_varchar01',
							renderer : Mogan.orderTrace.rendererFixMemberPayStatus,
							width : 30
						}, {
							header : "訂單編號",
							dataIndex : 'tide_id',
							width : 100
						}, {
							header : "訂單狀態",
							dataIndex : 'tide_status',
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
							editor : new Ext.form.TextField({
										readOnly : true
									})
						}, {
							header : "得標日",
							dataIndex : 'time_at_04',
							width : 150,
							editor : new Ext.form.TextField({
										readOnly : true
									})
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
							header : "填寫交易form",
							renderer : Mogan.orderTrace.rendererOrderFormIcon,
							dataIndex : 'o_varchar02'
						}, {
							header : "落札価格",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'buy_price'
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
				/*
				 * sm : new Ext.grid.RowSelectionModel({ singleSelect : true,
				 * listeners : { rowselect : function(sm, row, rec) {
				 * Ext.getCmp("itemPanel").getForm() .loadRecord(rec); } } }),
				 */
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
							id : 'orderPanelTab',
							items : Mogan.orderTrace.createOrderPanel()
						}, {
							title : '賣家資料',
							layout : 'fit',
							// id : 'msgSenderPanelTab',
							items : Mogan.orderTrace.createSellerPanel()
						}, {
							title : '聯絡紀錄',
							layout : 'fit',
							id : 'msgRecordPanelTab',
							items : Mogan.orderTrace.createMsgPanel()
						}, {
							title : '發送訊息',
							layout : 'fit',
							id : 'msgSenderPanelTab',
							items : Mogan.orderTrace.createMsgSenderPanel()
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
					p.body = record.data.msg_contact + "<br />";
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
					header : "得標編號",
					dataIndex : 'item_order_id',
					editor : new Ext.form.TextField(),
					width : 150,
					// hidden : true,
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
					id : 'comboItemOrderList',
					store : Mogan.orderTrace.orderItemListStore,
					model : 'local',
					valueField : 'item_order_id',
					displayField : 'item_id_name',
					triggerAction : 'all',
					emptyText : '不篩選',
					editable : false,
					width : 300,
					minListWidth : 450,
					xtype : 'combo'
				}, {
					xtype : 'button',
					text : '取消篩選',
					handler : function() {
						Ext.getCmp('comboItemOrderList').setValue();
						Mogan.orderTrace.filterItemOrderMsg();
					}
				}, {
					xtype : 'button',
					text : '更新資料',
					iconCls : 'refresh',
					handler : Mogan.orderTrace.refreshContactData
				}, {
					xtype : 'label',
					text : '最後更新時間：'
				}, {
					xtype : 'label',
					id : 'labelMsgUpdateTime',
					text : '1999 - 10 - 10 '
				}]
	});

	return grid;
}

Mogan.orderTrace.createMsgSenderPanel = function() {

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

	var simple = new Ext.FormPanel({
				id : 'msgSenderPanel',
				labelWidth : 75,
				defaultType : 'textfield',
				frame : true,
				url : 'AjaxPortal',
				items : [{
							id : 'comboItemOrderList2',
							store : Mogan.orderTrace.orderItemListStore,
							model : 'local',
							valueField : 'item_order_id',
							displayField : 'item_id_name',
							triggerAction : 'all',
							editable : false,
							width : 450,
							// minListWidth : 450,
							fieldLabel : '留言案件',
							hiddenName : "ITEM_ORDER_ID",
							xtype : 'combo'
						}, {
							xtype : 'combo',
							fieldLabel : '類型',
							store : Mogan.orderTrace.msgCategoryStore,
							mode : 'local',
							valueField : 'value',
							displayField : 'text',
							id : 'comboMsgCategory',
							triggerAction : 'all',
							width : 200,
							hiddenName : "SEND_METHOD",
							editable : false,
							value : '0'
						}, {
							xtype : 'combo',
							id : 'comboMsgTitle',
							fieldLabel : '訊息標題',
							store : Mogan.orderTrace.msgTitleStore,
							mode : 'local',
							width : 300,
							triggerAction : 'all',
							valueField : 'value',
							hiddenName : "SUBJECT_A",
							lazyRender : true,
							displayField : 'text',
							value : '1',
							editable : false
						}, {
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

	// Ext.getCmp('comboItemOrderList2').on('render',
	// Mogan.orderTrace.resizeToFitContent ,Ext.getCmp('comboItemOrderList2'));
	// comboMsgTemplate.on('select', Mogan.orderTrace.fixTextareaMsgContent);
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
 * 取得賣家資料畫面 BM2
 * 
 * @return {}
 */
Mogan.orderTrace.createSellerPanel = function() {
	var cm = new Ext.grid.ColumnModel({
				// specify any defaults for each column
				defaults : {
					sortable : true
					// columns are not sortable by default
				},
				columns : [new Ext.grid.RowNumberer(), {
							header : " ",
							dataIndex : 'is_active',
							width : 25,
							renderer : Mogan.orderTrace.rendererRemitStatus
						}, {
							header : "付款方式",
							dataIndex : 'remit_type',
							renderer : Mogan.orderTrace.rendererRemitType,
							width : 100
						}, {
							header : "郵局/銀行名稱",
							dataIndex : 'bank_name',
							editor : new Ext.form.TextField(),
							width : 100
						}, {
							header : "分行名稱",
							dataIndex : 'branch_name',
							editor : new Ext.form.TextField()
						}, {
							header : "帳號",
							editor : new Ext.form.TextField(),
							dataIndex : 'account_no'
						}, {
							header : "帳戶名稱",
							editor : new Ext.form.TextField(),
							dataIndex : 'account_name'
						}, {
							header : "備註",
							dataIndex : 'note',
							editor : new Ext.form.TextField()
						}]
			});

	var toolBar = [{
				id : 'btnAddSellerAccount',
				xtype : 'button',
				handler : function() {
					Mogan.orderTrace.getSellerAccountPanel();
					Mogan.orderTrace.sellerAccountPanel.show();
				},
				text : '新增付款方式'
			}, {
				id : 'btnDelSellerAccount',
				xtype : 'button',
				handler : Mogan.orderTrace.delSellerAccount,
				text : '刪除付款方式'
			}];

	var orderDataPanel = new Ext.FormPanel({
				labelWidth : 75,
				width : 450,
				frame : true,
				id : 'sellerDataPanel',
				defaultType : 'textfield',
				items : [{
							xtype : 'label',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							id : 'labelSellerId',
							fieldLabel : '賣家帳號'
						}, {
							xtype : 'textfield',
							text : '-',
							anchor : '90%',
							id : 'txtfldSellerName',
							name : 'seller_name',
							fieldLabel : '賣家名稱'
						}, {
							xtype : 'textfield',
							anchor : '90%',
							id : 'txtfldSellerEmail',
							name : 'email',
							fieldLabel : 'E-mail'
						}, {
							xtype : 'textfield',
							text : '-',
							anchor : '90%',
							name : 'phone',
							id : 'txtfldSellerPhone',
							fieldLabel : '電話'
						}, {
							xtype : 'textfield',
							anchor : '90%',
							id : 'txtfldSellerAddress',
							name : 'address',
							fieldLabel : '地址'
						}, {
							xtype : 'label',
							style : {
								clear : 'none'
							},
							text : '-',
							width : 'auto',
							id : 'labelSellerTransCount',
							fieldLabel : '成交件數'
						}, {
							xtype : 'textarea',
							value : '-',
							anchor : '90%',
							id : 'txtAraSellerNote',
							name : 'note',
							fieldLabel : '備註'
						}, {
							xtype : 'editorgrid',
							cm : cm,
							store : Mogan.orderTrace.sellerAccountListStore,
							trackMouseOver : true,
							id : 'sellerAccountGrid',
							// layout : 'fit',
							tbar : toolBar,
							frame : true,
							height : 200,
							stripeRows : true,
							stateful : true,
							loadMask : true
						}, {
							xtype : 'button',
							text : '儲存',
							width : 'auto',
							handler : Mogan.orderTrace.saveSellerData,
							id : 'btnSaveSellerData'
						}, {
							xtype : 'hidden',
							name : 'seller_id'
						}]
			});

	return orderDataPanel;
}

/**
 * 建立訂單畫面 BM2
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
				id : 'btnOrderEditNote',
				xtype : 'button',
				text : '編輯備註',
				handler : Mogan.orderTrace.showOrderAlertPanel
			}, '->', {
				id : 'btnDelOrder',
				text : '刪除訂單',
				menu : Mogan.orderTrace.getDelOrderMenu()
			}];

	var cNumberfield = Ext.extend(Ext.form.NumberField, {
				setValue : function(v) {
					cNumberfield.superclass.setValue.apply(this, arguments);
					Mogan.orderTrace.fixTotalPrice();
				}
			});

	var orderDataPanel = new Ext.FormPanel({
				id : 'orderDataPanel',
				region : 'east',
				// collapsible : true,
				split : true,
				labelWidth : 75,
				width : 450,
				// frame : true,
				defaultType : 'textfield',
				labelPad : 10,
				autoScroll : true,
				tbarStyle : 'border-bottom: 1px solid #99BBE8;',
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
						}, new cNumberfield({
							id : 'textfieldOrderShipCost',
							text : '-',
							name : 'cost_4',
							fieldLabel : '當地運費'
						}), {
							xtype : 'compositefield',
							fieldLabel : '稅金',
							items : [new cNumberfield({
//										xtype : 'numberfield',
										minValue : '0',
										id : 'textfieldOrderTaxCost',
										text : '-',
										name : 'cost_3'

									}), {
										xtype : 'button',
										iconCls : 'calculator',
										tooltipType : 'title',
										tooltip : '自動計算稅金 \n商品總費 x 0.05',
										handler : Mogan.orderTrace.calcuTax
									}]
						}, {
							xtype : 'numberfield',
							minValue : '0',
							id : 'textfieldOrderOtherCost',
							text : '-',
							name : 'cost_6',
							fieldLabel : '其他費用'
						}, {
							xtype : 'compositefield',
							fieldLabel : '付款方式',
							items : [{
								id : 'comboOrderPayType',
								store : Mogan.orderTrace.sellerPayType,
								model : 'local',
								valueField : 'account_id',
								displayField : 'remit_value',// remit_value
								// bank_name
								triggerAction : 'all',
								emptyText : '請設定',
								valueNotFoundText : '請設定',
								editable : false,
								allowBlank : false,
								lastQuery : '',
								xtype : 'combo',
								hiddenName : 'remit_to',
								fieldLabel : '付款方式',
								setValue : function(v) {
									this.constructor.prototype.setValue.apply(
											this, arguments);
									var record = this.store.getAt(this.store
											.find('account_id', v));
									this.fireEvent('select', this, record, 0);
								}
							}, {
								xtype : 'button',
								text : '新增',
								handler : function() {
									Mogan.orderTrace.getSellerAccountPanel();
									Mogan.orderTrace.sellerAccountPanel.show();
								}
							}]
						}, {
							id : 'labelOrderMoneyAlert',
							width : 250,
							xtype : 'label',
							style : {
								clear : 'none'
							},
							fieldLabel : '付款方式資訊'
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
						}, {
							xtype : 'hidden',
							name : 'item_alert'
						}, {
							xtype : 'hidden',
							name : 'contact_alert'
						}, {
							xtype : 'hidden',
							name : 'ship_alert'
						}, {
							xtype : 'hidden',
							name : 'money_alert'
						}, {
							xtype : 'hidden',
							name : 'seller_attribute_1'
						}, {
							xtype : 'hidden',
							name : 'seller_account'
						}, {
							xtype : 'hidden',
							name : 'seller_id'
						}, {
							xtype : 'hidden',
							name : 'tide_status'
						}, {
							xtype : 'hidden',
							name : 'website_id'
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
						}, {
							header : "Order Form",
							dataIndex : 'seller_attribute_1',
							width : 150,
							renderer : Mogan.orderTrace.rendererOrderFormBtn
						}, {
							header : "未讀訊息",
							dataIndex : 'seller_attribute_2',
							// dataIndex : 'seller_attribute_1',
							width : 150
							// renderer : Mogan.orderTrace.rendererOrderFormBtn
					}]
			});

	var orderListPanel = new Ext.FormPanel({
				flex : 1,
				title : '訂單內容(同捆清單)',
				layout : 'fit',
				region : 'center',
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
				layout : 'border',
				layoutConfig : {
					align : 'stretch',
					pack : 'start'
				},
				// defaults:{margins:'0 5 0 0'},
				items : [orderListPanel, orderDataPanel]
			});

	return panel;
};

/**
 * 刪除訂單MENU
 * 
 * @return {}
 */
Mogan.orderTrace.getDelOrderMenu = function() {
	if (Ext.isEmpty(Mogan.orderTrace.delOrderMenu)) {
		Mogan.orderTrace.delOrderMenu = new Ext.menu.Menu({
					id : 'delOrderMenu',
					items : [{
								xtype : 'menuitem',
								handler : function() {
									Mogan.orderTrace.delOrder(0)
								},
								text : '刪除訂單'
							}, {
								xtype : 'menuitem',
								handler : function() {
									Mogan.orderTrace.delOrder(1)
								},
								text : '會員棄標'
							}]
				});
	}
	return Mogan.orderTrace.delOrderMenu;

}

/**
 * 建立備忘編輯畫面
 * 
 * @return {}
 */
Mogan.orderTrace.getOrderAlertPanel = function() {
	if (Ext.isEmpty(Mogan.orderTrace.orderAlertPanel)) {
		Ext.DomHelper.append('iframe-window', {
					tag : 'div',
					id : 'orderAlertPanel_div'
				});
		Mogan.orderTrace.orderAlertPanel = new Ext.Window({
					el : 'orderAlertPanel_div',
					closeAction : 'hide',
					autoScroll : true,
					modal : true,
					title : '備忘編輯',
					width : 640,
					height : 400,
					layout : 'fit',
					id : 'orderAlertPanel',

					items : {
						xtype : 'form',
						id : 'orderAlertPanelForm',
						frame : true,
						tbarStyle : 'border-bottom: 1px solid #99BBE8;',
						labelAlign : 'top',
						tbar : [{
									xtype : 'button',
									handler : Mogan.orderTrace.saveAlertData,
									text : '儲存備忘'
								}, {
									xtype : 'button',
									handler : Mogan.orderTrace.confirmAlertPanel,
									text : '關閉'
								}],
						// autoWidth: true,
						items : [{
									id : 'comboxAlertType',
									store : Mogan.orderTrace.alertTypeStore,
									model : 'local',
									valueField : 'list_key',
									displayField : 'list_name',
									triggerAction : 'all',
									value : 'item_alert',
									editable : false,
									allowBlank : false,
									xtype : 'combo',
									hiddenName : 'alert_type',
									fieldLabel : '備忘類型'
								}, {
									id : 'textareaAlertContent',
									xtype : 'textarea',
									anchor : '90% ',
									growMax : 150,
									grow : true,
									name : 'alert_text',
									// width : 550,
									fieldLabel : '新增備忘內容'
								}]
					}
				});
		Ext.getCmp('comboxAlertType').on('select',
				Mogan.orderTrace.fixTextareaAlertContent);
	}

	return Mogan.orderTrace.orderAlertPanel;
}

Mogan.orderTrace.getSellerAccountPanel = function() {
	if (Ext.isEmpty(Mogan.orderTrace.sellerAccountPanel)) {
		Ext.DomHelper.append('iframe-window', {
					tag : 'div',
					id : 'sellerAccountPanel_div'
				});
		Mogan.orderTrace.sellerAccountPanel = new Ext.Window({
					el : 'sellerAccountPanel_div',
					closeAction : 'hide',
					autoScroll : true,
					modal : true,
					title : '賣家收款方式資料',
					width : 640,
					height : 460,
					layout : 'fit',
					id : 'sellerAccountPanel',

					items : {
						xtype : 'form',
						id : 'orderAlertPanelForm',
						bodyStyle : 'padding:5px',
						labelAlign : 'top',
						tbar : [{
									xtype : 'button',
									handler : Mogan.orderTrace.addSellerAccount,
									text : '新增'
								}, {
									xtype : 'button',
									hidden : true,
									handler : Mogan.orderTrace.saveAlertData,
									text : '修改'
								}, {
									xtype : 'button',
									handler : function() {
										Mogan.orderTrace.sellerAccountPanel
												.hide();
									},
									text : '關閉'
								}],
						items : [{
									xtype : 'label',
									id : 'labelSellerAccount',
									style : {
										clear : 'none'
									},
									text : '-',
									width : 'auto',
									fieldLabel : '賣家名稱'
								}, {
									store : Mogan.orderTrace.payTypeStore,
									model : 'local',
									valueField : 'list_key',
									displayField : 'list_name',
									triggerAction : 'all',
									editable : false,
									allowBlank : false,
									xtype : 'combo',
									hiddenName : 'remit_type',
									fieldLabel : '收款方式'
								}, {
									xtype : 'textfield',
									anchor : '90% ',
									name : 'bank_name',
									fieldLabel : '銀行名稱'
								}, {
									xtype : 'textfield',
									anchor : '90% ',
									name : 'branch_name',
									fieldLabel : '分行名稱'
								}, {
									xtype : 'textfield',
									anchor : '90% ',
									name : 'account_no',
									fieldLabel : '戶號'
								}, {
									xtype : 'textfield',
									anchor : '90% ',
									name : 'account_name',
									fieldLabel : '戶名'
								}, {
									xtype : 'textarea',
									anchor : '90% ',
									growMax : 150,
									grow : true,
									name : 'note',
									fieldLabel : '備註'
								}]
					}
				});

		Mogan.orderTrace.sellerAccountPanel.on('beforeShow',
				Mogan.orderTrace.fixSellerAccountPanel);
	}

	return Mogan.orderTrace.sellerAccountPanel;
}
