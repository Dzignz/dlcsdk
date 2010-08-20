Ext.namespace("Mogan.delList");
Mogan.delList.createCaseListGridPanel = function() {

	var bbar = new Ext.PagingToolbar({
				pageSize : 50,
				store : Mogan.delList.itemListStore,
				displayInfo : true,
				displayMsg : 'Displaying topics {0} - {1} of {2}',
				emptyMsg : "No topics to display",
				items : ['-',/* {
							store : Mogan.delList.accountListStore,
							displayField : 'diaplay_account',
							model : 'local',
							valueField : 'bid_id',
							id : 'comboAccount',
							triggerAction : 'all',
							editable : false,
							typeAhead : true,
							emptyText : '篩選帳號...',
							xtype : 'combo'
						}, */'-', {
							id : 'comboSearchKey',
							store : Mogan.delList.searchKeyStore,
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
								Mogan.delList.itemListStore.load();
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
							dataIndex : 'msg_status',
							width : 40,
							renderer : Mogan.delList.rendererMsgStatus
						}, {
							header : " ",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'website_id',
							renderer : Mogan.delList.rendererFixWebsiteIcon,
							width : 30
						}, {
							header : " ",
							editor : new Ext.form.TextField({
										readOnly : true
									}),
							dataIndex : 'o_varchar01',
							renderer : Mogan.delList.rendererFixMemberPayStatus,
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
							renderer : Mogan.delList.rendererFixOrderStatus
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
							header : "代標帳號",
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
						},
						// {
						// header : "未付款數量",
						// editor : new Ext.form.TextField(),
						// width : 80,
						// renderer : Mogan.delList.rendererFixNonRemitCount,
						// dataIndex : 'non_pay'
						// }, {
						// header : "同會員未付款",
						// editor : new Ext.form.TextField(),
						// width : 100,
						// renderer : Mogan.delList.rendererFixNonRemitCount,
						// dataIndex : 'member_non_pay'
						// }, {
						// header : "未出貨數量",
						// editor : new Ext.form.TextField(),
						// width : 100,
						// dataIndex : 'total_unship'
						// },
						{
							header : "填寫交易form",
							renderer : Mogan.delList.rendererOrderFormIcon,
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
			// 標示刪除狀態
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
				store : Mogan.delList.itemListStore,
				/*
				 * sm : new Ext.grid.RowSelectionModel({ singleSelect : true,
				 * listeners : { rowselect : function(sm, row, rec) {
				 * Ext.getCmp("itemPanel").getForm() .loadRecord(rec); } } }),
				 */
				cm : cm,
				bbar : bbar,
				viewConfig : gridViewConfig,
				tbar : [{
							xtype : 'button',
							iconCls : 'help',
							// height:24,
							text : '教學文件',
							handler : function() {
								window.open("./guide/delList.htm");
							}
						}]
			});


	return grid;
};