// Ext.namespace("Mogan.orderTrace");
Ext.onReady(function() {
	// 初始化資料 下標帳號清單
	Mogan.orderTrace.accountListStore = new Ext.data.Store({
				reader : new Ext.data.JsonReader({
							root : 'root'
						}, ['bid_id', 'account', 'diaplay_account']),
				proxy : new Ext.data.MemoryProxy(accountJSONData)
			});
	Mogan.orderTrace.accountListStore.load();

	// 得標資料
	Mogan.orderTrace.itemListStore = new Ext.data.JsonStore({
		autoload : true,
		root : 'responseData[0]["Datas"]',
		totalProperty : 'responseData[0]["Records"]',
		idProperty : 'threadid',
		id : 'itemListStore',
		remoteSort : true,
		
		fields : itemOrderCol,
				
		proxy : new Ext.data.HttpProxy({
			url : 'AjaxPortal?APP_ID='
					+ appId
					+ '&ACTION=LOAD_BID_ITEM_ORDERS&MODEL_NAME=BM2&RETURN_TYPE=JSON&START_INDEX=0&PAGE_SIZE=50'
		}),
		paramNames : {
			start : 'START_INDEX',
			limit : 'PAGE_SIZE',
			sort : 'ORDER_BY',
			dir : 'DIR'
		}
	});
	
	Mogan.orderTrace.itemListStore.on("beforeload",
			Mogan.orderTrace.getloadBidItemsURL);

	// 留言資料
	Mogan.orderTrace.msgRecordStore = new Ext.data.JsonStore({
				root : 'responseData[0]["CONTACT_MSG"]["Datas"]',
				totalProperty : 'responseData[0]["CONTACT_MSG"]["Records"]',
				idProperty : 'threadid',
				remoteSort : true,
				fields : ['contact_id', 'seller_id', 'member_account',
						'bid_account', 'transaction_id', 'item_id',
						'msg_category', 'msg_id', 'msg_title', 'msg_from',
						'msg_contact', 'msg_date', 'is_read', 'read_date',
						'note']
			});

	/**
	 * 範本列表專用data store
	 */
	Mogan.orderTrace.templateListStore = new Ext.data.Store({
				id : 'templetListStore',
				reader : new Ext.data.JsonReader({
							root : 'root'
						}, ['templateIndex', 'loadStatus', 'fileContent',
								'fileName']),

				proxy : new Ext.data.MemoryProxy(templateJSONData)
			});
	Mogan.orderTrace.templateListStore.load();

	/**
	 * 名稱對應表專用data store
	 */
	Mogan.orderTrace.trnsListStore = new Ext.data.Store({
				reader : new Ext.data.JsonReader({
							root : 'root'
						}, ['trnsCode', 'trnsData']),
				proxy : new Ext.data.MemoryProxy(trnsJSONData)
			});
	Mogan.orderTrace.trnsListStore.load();

	/**
	 * 欄位列表
	 */
	Mogan.orderTrace.trnsColmListStore = new Ext.data.Store({
			reader : new Ext.data.JsonReader({
						root : 'root'
					}, ['columnName', 'columnDesc']),
			proxy : new Ext.data.MemoryProxy(trnsColmJSONData)
		});
	Mogan.orderTrace.trnsColmListStore.load();

	// 作用不明
	Mogan.orderTrace.templateSatus = 'LOAD';
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

	// 初始化畫面
	var viewport = new Ext.Viewport({
				layout : 'border',
				items : [{
							region : 'center',
							items : Mogan.orderTrace.createCaseListGridPanel(),
							split : true,
							collapsible : true,
							layout : 'fit'
						}, {
							height : 300,
							region : 'south',
							split : true,
							collapsible : true,
							layout : 'fit',
							items : Mogan.orderTrace.createDetilPanel()
						}]
			});
	var el = Ext.get('comboSearchKey');
	var keyNav = new Ext.KeyNav(el, {
				enter : function(e) {
					itemListStore.load();
				}
			});
});