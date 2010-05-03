Ext.namespace("Mogan.orderTrace");

/**
 * 篩選關鍵字
 * 
 * @type String
 */
Mogan.orderTrace.filterKeyWord = "";

/**
 * 篩選訂單狀態
 * 
 * @type String
 */
Mogan.orderTrace.filterStatus = new Array();

/**
 * 狀態名稱對應表
 */
Mogan.orderTrace.statusNameMap = new Object();
Mogan.orderTrace.statusNameMap['3-01'] = '連絡中';
Mogan.orderTrace.statusNameMap['3-02'] = '取得連絡';
Mogan.orderTrace.statusNameMap['3-03'] = '待匯款';
Mogan.orderTrace.statusNameMap['3-04'] = '已匯款';
Mogan.orderTrace.statusNameMap['3-05'] = '賣家已發貨';
Mogan.orderTrace.statusNameMap['3-06'] = '購買點已收貨';
Mogan.orderTrace.statusNameMap['3-07'] = '購買點已發貨';
Mogan.orderTrace.statusNameMap['3-08'] = '收貨點已收貨';
Mogan.orderTrace.statusNameMap['3-09'] = '收貨點已發貨';
Mogan.orderTrace.statusNameMap['3-10'] = '會員已收貨';

Ext.onReady(function() {

	Mogan.orderTrace.payTypeStore = new Ext.data.Store({
				reader : new Ext.data.JsonReader({
							root : 'root'
						}, ['list_key', 'list_name']),
				proxy : new Ext.data.MemoryProxy(payTypeJSONData)
			});
	Mogan.orderTrace.payTypeStore.load();
			
	// 初始化資料 下標帳號清單
	Mogan.orderTrace.accountListStore = new Ext.data.Store({
				reader : new Ext.data.JsonReader({
							root : 'root'
						}, ['bid_id', 'account', 'diaplay_account']),
				proxy : new Ext.data.MemoryProxy(accountJSONData)
			});
	Mogan.orderTrace.accountListStore.load();

	/**
	 * 同捆得標清單，同賣家得標清單
	 */
	Mogan.orderTrace.orderItemListStore = new Ext.data.Store({
		reader : new Ext.data.JsonReader(
				{
					root : 'root'
				},
				['item_order_id', 'item_id', 'item_name', 'buy_price',
						'buy_unit', 'time_at_03', 'bid_account', 'item_id_name']),
		proxy : new Ext.data.MemoryProxy(orderItemListJSONData)
	});
	Mogan.orderTrace.orderItemListStore.load();

	// 完整得標清單
	Mogan.orderTrace.itemListStore = new Ext.data.JsonStore({
				autoload : true,
				root : 'responseData[0]["Datas"]',
				totalProperty : 'responseData[0]["Records"]',
				idProperty : 'threadid',
				id : 'itemListStore',
				remoteSort : true,
				fields : itemOrderCol,
				proxy : new Ext.data.HttpProxy({
							url : 'AjaxPortal',
							method : 'POST'
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
	 * 搜尋關鍵字
	 */
	Mogan.orderTrace.searchKeyStore = new Ext.data.Store({
				id : 'searchKeyStore',
				reader : new Ext.data.JsonReader({
							root : 'root'
						}, ['value', 'key']),

				proxy : new Ext.data.MemoryProxy({
							"root" : [{
										key : ' ',
										value : ' '
									}]
						})
			});

	Mogan.orderTrace.searchKeyStore.load();

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
							region : 'north',
							items : Mogan.orderTrace.createCaseListGridPanel(),
							split : true,
							collapsible : true,
							height : 300,
							layout : 'fit'
						}, {
							region : 'center',
							split : true,
							collapsible : true,
							layout : 'fit',
							items : Mogan.orderTrace.createDetilPanel()
						}]
			});
			
	/**
	 * 資料讀取
	 */
	Mogan.orderTrace.fixFilterStatus();
	Mogan.orderTrace.itemListStore.load();
	
	/**
	 * 鍵盤綁定
	 */
	var el = Ext.get('comboSearchKey');
	var keyNav = new Ext.KeyNav(el, {
				enter : function(e) {
					itemListStore.load();
				}
			});
			
	/***
	 * Tooltip 專區
	 */
	Ext.QuickTips.init();
	
    new Ext.ToolTip({
        target: 'rb-ship_type_1',
        html: '商品費用已結清，收貨時不用付錢，請選我'
    });
    
	new Ext.ToolTip({
        target: 'rb-ship_type_0',
        html: '收貨時需支付運費或商品費用時，請選我'
    });
			
});