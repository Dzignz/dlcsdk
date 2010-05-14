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
Mogan.orderTrace.statusNameMap['1-01'] = '競標中';
Mogan.orderTrace.statusNameMap['1-02'] = '競標結束';

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

/**
 * 發送訊息相關設定
 * @type 
 */
Mogan.orderTrace.msgCategoryData = [
		[
				'0',
				'留言版',
				[['1', '送付先住所、支払い、発送などについて'], ['2', '支払いが完了しました'],
						['3', '商品を受け取りました'], ['4', 'その他']]],
		['1', 'e-mail', []], ['2', '揭示版', [['no', '公開しない'], ['yes', '公開する']]]];

Ext.onReady(function() {
	
	/**
	 * 留言版類型，留言版，e-mail，揭示版
	 */
	Mogan.orderTrace.msgCategoryStore = new Ext.data.SimpleStore({// 下拉式選單資料
		fields : ['value', 'text', 'data'],
		data : Mogan.orderTrace.msgCategoryData
	});
	
	/**
	 * 留言版標題
	 */
	Mogan.orderTrace.msgTitleStore = new Ext.data.SimpleStore({// 下拉式選單資料
		fields : ['value', 'text'],
		data : Mogan.orderTrace.msgCategoryStore.getAt(0).get('data')
	});
	
	
	// 備忘類型清單
	Mogan.orderTrace.alertTypeStore = new Ext.data.Store({
				reader : new Ext.data.JsonReader({
							root : 'root'
						}, ['list_key', 'list_name']),
				proxy : new Ext.data.MemoryProxy(alertTypeJSONData)
			});
	Mogan.orderTrace.alertTypeStore.load();

	// 付款方式清單
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
						'buy_unit', 'time_at_03', 'bid_account', 'item_id_name','seller_attribute_1','o_varchar01']),
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
				root : 'responseData[0]["Msgs"]',
				totalProperty : 'responseData[0]["MsgRecords"]',
				idProperty : 'threadid',
				remoteSort : true,
				fields : ['contact_id', 'seller_id', 'member_account',
						'item_order_id', 'item_id', 'msg_category', 'msg_id',
						'msg_title', 'msg_from', 'msg_contact', 'msg_date',
						'is_read', 'read_date', 'note', 'order_time_at_16',
						'item_name']
			});
	// Mogan.orderTrace.msgRecordStore.on('load',Mogan.orderTrace.filterItemOrderMsg);

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
							title : '待處理列表',
							region : 'north',
							items : Mogan.orderTrace.createCaseListGridPanel(),
							split : true,
							collapsible : true,
							height : 300,
							layout : 'fit'
						}, {
							title : '訂單資料',
							region : 'center',
							split : true,
							collapsible : true,
							layout : 'fit',
							items : Mogan.orderTrace.createDetilPanel()
						}]
			});
			
	/**
	 * 修正權限
	 */
	//"add","up","del","view"
	var pNum=parseInt(pkey,2);			
	if ( (pNum & parseInt('000010000000000000000000',2)) < 1 ){ //聯絡賣家
		Ext.getCmp('DetilPanel').remove(Ext.getCmp('msgSenderPanelTab'),true);
	}
	
	if ( (pNum & parseInt('000000000100000000000000',2)) <1){ //訂單管理  更新 
		Ext.destroy(Ext.getCmp('btnOrderSubmitCost'));	//訂單態狀
	}
	
	if ( (pNum & parseInt('000000000010000000000000',2)) <1){ //訂單狀態管理  刪除
		Ext.destroy(Ext.getCmp('itemListMenuTransOrder'));	//商品訂單移動
		Ext.destroy(Ext.getCmp('itemListMenuNewOrder'));	//商品訂單移動
		Ext.destroy(Ext.getCmp('btnDelOrder'));				//刪除訂單下方按鈕
//		Ext.destroy(Ext.getCmp('itemListMenuDelOrder'));	//刪除訂單上方列表menu
	}
	
	if ( (pNum & parseInt('000000000000000001000000',2)) <1){ //訂單備忘  維護
		Ext.destroy(Ext.getCmp('btnOrderEditNote'));
	}
		

	
	
			
			
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

	/***************************************************************************
	 * Tooltip 專區
	 */
	Ext.QuickTips.init();

	new Ext.ToolTip({
				target : 'rb_ship_type_0',
				html : '商品費用已結清，收貨時不用付錢，請選我'
			});

	new Ext.ToolTip({
				target : 'rb_ship_type_1',
				html : '收貨時需支付運費或商品費用時，請選我'
			});

});