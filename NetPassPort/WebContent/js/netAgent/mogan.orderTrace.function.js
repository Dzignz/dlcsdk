Ext.namespace("Mogan.orderTrace");

var appId = "26b782eb04abbd54efba0dcf854b158d";

var loadBidItemsParams = {
	APP_ID : appId,
	ACTION : "LOAD_BID_ITEM_ORDERS",
	MODEL_NAME : "BM2",
	RETURN_TYPE : "JSON",
	STATUS_CONDITION : Mogan.orderTrace.filterStatus,
	SEARCH_KEY : '',
	CONDITION_KEY : '',
	START_INDEX : 0,
	PAGE_SIZE : 50,
	ORDER_BY : 'time_at_04',
	DIR : 'ASC'
};

/**
 * 取得競標商品資料 好像沒有用...
 * 
 * @param {}
 *            store 資料套用範圍
 * @param {}
 *            startIndex 起始資料
 * @param {}
 *            pageSize 每頁數量
 * @param {}
 *            callbackFunction 回呼FUNCTION
 */
Mogan.orderTrace.createLoadBidItemsParams = function(store, startIndex,
		pageSize, orderBy, condition, callbackFunction) {

	var conditObj = {
		DEFAULT_VALUE : '-',
		SEARCH_KEY : Ext.getCmp('comboSearchKey').getValue().trim(),
		ACCOUNT_ID : Ext.getCmp('comboAccount').getValue(),
		ACCOUNT : Ext.getCmp('comboAccount').getRawValue()
	};

	var loadParams = {
		params : {
			APP_ID : appId,
			ACTION : "LOAD_BID_ITEM_ORDERS",
			MODEL_NAME : "BM2",
			RETURN_TYPE : "JSON",
			ORDER_BY : orderBy,
			CHECK_SAME_SELLER : Ext.getCmp('checkboxSameSeller').getValue(),
			STATUS_CONDITION : statusCondition,
			CONDITION_KEY : Ext.encode(conditObj)
		},
		add : false,
		scope : store,
		callback : callbackFunction
	};

	// alert('createLoadBidItemsParams');
	return loadParams;
}

/**
 * 點選列表資料，初始化資料
 * 
 * @param {}
 *            grid
 * @param {}
 *            rowIndex
 * @param {}
 *            e
 */
Mogan.orderTrace.clickItem = function(grid, rowIndex, e) {

	var r = grid.getStore().getAt(rowIndex);
	Mogan.orderTrace.loadOrderData(grid, rowIndex, e);// 將資料顯示基本資料Tab //OK
	if (Mogan.orderTrace.templateSatus == 'EDIT') {
		Mogan.orderTrace.changeTemplateMode();
	}

}

Mogan.orderTrace.openItemPage = function(grid, rowIndex, e) {

	var r = Mogan.orderTrace.orderItemListStore.getAt(rowIndex);

	var url = './ProxyProtal?APP_ID=' + appId
			+ "&MODEL_NAME=ItemOrderFormYJ&ACTION=GET_ITEM_PAGE&BID_ACCOUNT="
			+ r.get('buyer_account') + "&" + "ITEM_ID=" + r.get('item_id');

	window.open(url);
}

/**
 * 讀取商品 order form
 * 
 * @param {}
 *            record
 */
Mogan.orderTrace.loadItemOrderForm = function() {
	var itemPanel = Ext.getCmp("itemPanel");
	Ext.getCmp("tabItemOrderForm").setDisabled(true);
	if (itemPanel.getForm().getValues()['order_form_status'] == 0) {
		Ext.getCmp("DetilPanel").setActiveTab(0);
		Ext.getCmp("tabItemOrderForm").html = itemPanel.getForm().getValues()['order_form_status'];
	} else {

		Ext.DomHelper.overwrite('tab-iframe-window-1', {
			tag : 'iframe',
			src : './ProxyProtal?APP_ID='
					+ appId
					+ "&MODEL_NAME=ItemOrderFormYJ&ACTION=GET_ORDER_FORM&BID_ACCOUNT="
					+ Ext.getCmp("itemPanel").getForm().getValues()['agent_account']
					+ "&"
					+ "ITEM_ID="
					+ Ext.getCmp("itemPanel").getForm().getValues()['item_id']
					+ "&"
					+ "SELLER_ACCOUNT="
					+ Ext.getCmp("itemPanel").getForm().getValues()['sell_name'],
			style : 'width:100%; height:100%;',
			frameborder : '0'
		});
		Ext.getCmp("tabItemOrderForm").setDisabled(false);
	}
}

/**
 * 開啟商品order form
 * 
 * @param {}
 *            dataIndex
 */
Mogan.orderTrace.showItemOrderForm = function(dataIndex) {
	Ext.DomHelper.append('iframe-window', {
				tag : 'div',
				id : 'YJ-orderForm'
			});

	var bidAccount = Mogan.orderTrace.orderItemListStore.getAt(dataIndex).data['buyer_account'];
	var itemId = Mogan.orderTrace.orderItemListStore.getAt(dataIndex).data['item_id'];
	var sellerAccount = Ext.getCmp('orderDataPanel').getForm().getValues()['seller_account'];

	var win = new Ext.Window({
		el : 'YJ-orderForm',
		layout : 'fit',
		html : "<iframe src='./ProxyProtal?APP_ID="
				+ appId
				+ "&MODEL_NAME=ItemOrderFormYJ&ACTION=GET_ORDER_FORM&BID_ACCOUNT="
				+ bidAccount + "&" + "ITEM_ID=" + itemId + "&"
				+ "SELLER_ACCOUNT=" + sellerAccount + "'"
				+ " style='width:100%; height:100%;' frameborder='0' />",
		width : 800,
		height : 600,
		closeAction : 'close',
		autoScroll : true
			// modal : true
	});
	win.show();
}

/**
 * 顯示商品圖片
 * 
 * @param {}
 *            imgUrl 圖片網址
 */
Mogan.orderTrace.showItemImg = function(winId, imgUrl) {

	if ((!Ext.isEmpty(Ext.getCmp('itemImage_win')))
			&& Ext.getCmp('itemImage_win').imgValue == imgUrl) {
		Ext.getCmp('itemImage_win').show();
		return;
	} else {
		if (!Ext.isEmpty(Ext.getCmp('itemImage_win')))
			Ext.getCmp('itemImage_win').close();

		if (!Ext.get('itemImage_div')) {
			Ext.DomHelper.append('iframe-window', {
						tag : 'div',
						id : 'itemImage_div'
					});
		}
		var tempDiv = Ext.DomHelper.createDom({
					tag : 'div'
				});
		var img = Ext.DomHelper.createDom({
					tag : 'img',
					id : 'win_itemImage',
					src : imgUrl,
					width : '100%',
					height : '100%',
					title : '點我關閉',
					onclick : "Mogan.orderTrace.closeItemImg(this);"
				});
		tempDiv.appendChild(img);
		var win = new Ext.Window({
					el : 'itemImage_div',
					id : 'itemImage_win',
					imgValue : imgUrl,
					layout : 'fit',
					html : tempDiv.innerHTML,
					width : 800,
					height : 600,
					// resizable : false,
					hideBorders : true,
					border : false,
					closeAction : 'close'
				});

		win.show();
	}

}

Mogan.orderTrace.closeItemImg = function(obj) {
	Ext.getCmp('itemImage_win').close()
}

/**
 * 更新畫面上的資料
 * 
 * @param {}
 *            response
 * @param {}
 *            checkTideId 是否要檢查tide id
 */
Mogan.orderTrace.refreshContactDataBak = function(response, checkTideId) {
	var json = parserJSON(response.responseText);
	if (json['responseResult'] == "failure") {
		Ext.Msg.alert("錯誤", "請向程式開發者詢問<br />" + json['responseMsg']);
	} else {

		if (Ext.isBoolean(checkTideId) && checkTideId) {
			if (Ext.getCmp('orderDataPanel').getForm().getValues()['tide_id'] != json['responseData'][0]['TideId']) {
				// 回傳的資料與目前檢視的訂單為不同筆，直接取消
				return;
			}
		}

		for (var i = 0; i < json['responseData'][0]['Datas'].length; i++) {
			Mogan.orderTrace.fixItemOrderListData('item_order_id',
					json['responseData'][0]['Datas'][i]['item_order_id'],
					'msg_status',
					json['responseData'][0]['Datas'][i]['msg_status']);
		}

		if (Ext.isBoolean(checkTideId)) {
			Mogan.orderTrace.msgRecordStore.loadData(json);
		} else {
			var tempJsonData = {
				'responseData' : [{
							'Msgs' : json['responseData'][0]['Datas']
						}]
			};
			Mogan.orderTrace.msgRecordStore.loadData(tempJsonData);
		}
		Mogan.orderTrace.upadteLabelMsgUpdateTime();

	}
}

/**
 * 更新訊息最後更新時間
 */
Mogan.orderTrace.upadteLabelMsgUpdateTime = function() {
	var updateTimeDate = null;
	var updateTimeStr = null;
	var itemDesc = '';
	if (Mogan.orderTrace.msgRecordStore.getCount() == 0) {
		Ext.getCmp('labelMsgUpdateTime').setText('-');
		return;
	}

	if (Ext.getCmp('comboItemOrderList').getValue() == '') {
		Mogan.orderTrace.msgRecordStore.each(function(r) {
					if (updateTimeDate == null) {
						updateTimeDate = Date.parseDate(
								r.data['order_time_at_16'], "Y-m-d H:i:s");
						updateTimeStr = r.data['order_time_at_16'];
					}
					if (updateTimeDate < Date.parseDate(
							r.data['order_time_at_16'], "Y-m-d H:i:s")) {
						updateTimeDate = Date.parseDate(
								r.data['order_time_at_16'], "Y-m-d H:i:s");
						updateTimeStr = r.data['order_time_at_16'];
					}
					itemDesc = ' [' + r.data['item_order_id'] + '] '
							+ r.data['item_name'];
				});
	} else {
		updateTimeStr = Mogan.orderTrace.msgRecordStore.getAt(0).data['order_time_at_16'];
		itemDesc = ' ['
				+ Mogan.orderTrace.msgRecordStore.getAt(0).data['item_order_id']
				+ '] '
				+ Mogan.orderTrace.msgRecordStore.getAt(0).data['item_name'];
	}
	Ext.getCmp('labelMsgUpdateTime').setText(updateTimeStr + itemDesc);
}

/**
 * BM2 更新絡聯內容
 * 
 * @param {}
 *            response
 */
Mogan.orderTrace.refreshContactData = function() {

	var itemOrderIds = new Array();
	// alert(Ext.getCmp('comboOrderCase').getValue());
	if (Ext.getCmp('comboItemOrderList').getValue() == '') {
		Mogan.orderTrace.orderItemListStore.each(function(r) {
					itemOrderIds.push(r.data['item_order_id']);
				});
	} else {
		itemOrderIds.push(Ext.getCmp('comboItemOrderList').getValue());
	}
	Ext.getCmp('labelMsgUpdateTime').setText('更新中....');
	Ext.Ajax.request({
		url : 'AjaxPortal',
		callback : function() {
			Ext.Msg.hide();
			Ext.getCmp('DetilPanel').setDisabled(false);
		},
		success : Mogan.orderTrace.refreshContactDataBak,
		failure : function(response) {
			Ext.Msg.alert("錯誤", "請向程式開發者詢問");
		},
		params : {
			APP_ID : appId,
			ACTION : "REFRESH_CONTACT_DATA",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "BM2",
			TIDE_ID : Ext.getCmp('orderDataPanel').getForm().getValues()['tide_id'],
			ITEM_ORDER_IDS : Ext.encode(itemOrderIds)
		}
	});
}

/**
 * 依案件ID來篩選訊息
 */
Mogan.orderTrace.filterItemOrderMsg = function() {
	Mogan.orderTrace.msgRecordStore.filter([{
		fn : function(record) {
			if (Ext.getCmp('comboItemOrderList').getValue() == '') {
				return true;
			} else {
				return record.get('item_order_id') == Ext
						.getCmp('comboItemOrderList').getValue();
			}
		}
	}]);
	Mogan.orderTrace.upadteLabelMsgUpdateTime();
}

/**
 * 設定發送訊息介面
 */
Mogan.orderTrace.fixMsgSenderPanel = function() {
	if (Ext.getCmp('msgSenderPanelTab')) {
		Ext.getCmp('comboItemOrderList2').setValue(Ext.getCmp('orderDataPanel')
				.getForm().getValues()['item_order_id']);
		Ext.getCmp("textareaMsgContent").setValue('');
		Mogan.orderTrace.fixComboMsgTitle(Ext.getCmp("comboMsgCategory"));
	}
}

/**
 * 更新訂單資料 BM2
 * 
 * @param {}
 *            response
 */
Mogan.orderTrace.showOrderData = function(response) {
	var i = 0;
	var json = parserJSON(response.responseText);
	if (json['responseResult'] == "failure") {
		Ext.Msg.alert("錯誤", "請向程式開發者詢問<br />" + json['responseMsg']);
	} else {
		// 賣家資料畫面
		Mogan.orderTrace.loadSellerData(json['responseData'][0]['SellerData'],
				json['responseData'][0]['SellerAccounts']);

		// 訂單資料
		Mogan.orderTrace.refreshOrderData(json['responseData'][0]['Datas']);

		// 同捆清單
		orderItemListJSONData['root'] = json['responseData'][0]['ItemList'];
		Mogan.orderTrace.orderItemListStore.load();

		// 更新訊息記錄
		Mogan.orderTrace.refreshContactDataBak(response, false);

		// 更新log資料
		Mogan.orderTrace.refreshLogPanel(json['responseData'][0]['Logs']);
		
		// 設定發訊介面
		Mogan.orderTrace.fixMsgSenderPanel();

		// 更新訂單資料title
		Mogan.orderTrace.refreshOrderTitle(json);
	}
}

Mogan.orderTrace.showAlert = function(){
//	Ext.getCmp('labelOrderNote').getText
//	Ext.getCmp('orderDataPanel').getForm().getValues()['alert_group']
	Ext.Msg.alert('備註',Ext.getCmp('orderDataPanel').getForm().getValues()['alert_group']);
}

/**
 * 更新訂單資料title 資訊
 */
Mogan.orderTrace.refreshOrderTitle =function (json){
			var countMsg = " <font color=\"#ae0000\"># ";
		if (json['responseData'][0]['TideCount']['3-01'])
			countMsg += "連絡中 (" + json['responseData'][0]['TideCount']['3-01']
					+ ") ";
		if (json['responseData'][0]['TideCount']['3-02'])
			countMsg += "取得連絡 (" + json['responseData'][0]['TideCount']['3-02']
					+ ") ";
		if (json['responseData'][0]['TideCount']['3-03'])
			countMsg += "待匯款 (" + json['responseData'][0]['TideCount']['3-03']
					+ ") ";
		countMsg += "</font>";
		countMsg += "<font color=\"#330066\" onclick='Mogan.orderTrace.showAlert()' style=\"cursor:pointer\" >";//#330066 CC6633
		if (!Ext.isEmpty(json['responseData'][0]['Datas']['item_alert'])){
			
			countMsg += " [商品備註] <img src='./resources/mogan/images/message03.gif' />";
//			countMsg += "</font>";
		}
		if (!Ext.isEmpty(json['responseData'][0]['Datas']['contact_alert'])){
//			countMsg += "<font color=\"#330066\">";//#330066 CC6633
			countMsg += " [聯絡備註] <img src='./resources/mogan/images/message03.gif'/>";
//			countMsg += "</font>";
		}
		if (!Ext.isEmpty(json['responseData'][0]['Datas']['ship_alert'])){
//			countMsg += "<font color=\"#330066\">";//#330066 CC6633
			countMsg += " [物流備註] <img src='./resources/mogan/images/1271300305_message.png'/>";
//			countMsg += "</font>";
		}
		if (!Ext.isEmpty(json['responseData'][0]['Datas']['money_alert'])){
//			countMsg += "<font color=\"#330066\">";//#330066 CC6633
			countMsg += " [金流備註] <img src='./resources/mogan/images/1271300305_message.png'/>";
//			countMsg += "</font>";
		}
		countMsg += "</font>";
		Ext.getCmp('centerPanel').setTitle("訂單資料 - "
				+ json['responseData'][0]['Datas']['tide_id'] + countMsg);
}

/**
 * 更新log資料
 * @param {} logs
 */
Mogan.orderTrace.refreshLogPanel = function (logs){
	logJSONData['root'] = logs;
	Mogan.orderTrace.logStore.load();
}

/**
 * 如果有被取消的案件，會跳出警告
 * 
 * @param {}
 *            store
 * @param {}
 *            record
 */
Mogan.orderTrace.alertCancelItem = function(store, record) {
	var msg_1 = "";
	var msg_2 = "";
	var mainAlert = "";

	var msg_1_flag = false;
	var msg_2_flag = false;
	for (var i = 0; i < record.length; i++) {
		if (record[i]['data']['flag_01'] == 1) {
			msg_1 = msg_1 + record[i]['data']['item_order_id'] + "\r\n";
		}
		if (record[i]['data']['flag_01'] == 2) {
			msg_2 = msg_2 + record[i]['data']['item_order_id'] + "\r\n";
		}
	}
	if (msg_1.length > 0) {
		mainAlert += "[警告]\r\n ";
		mainAlert += "下列商品在競標時曾被取消，請確認得標狀況\r\n";
		mainAlert += msg_1 + "\r\n";
	}
	if (msg_2.length > 0) {
		mainAlert += "[警告]\r\n ";
		mainAlert += "下列商品在得標後曾被取消，請確認得標狀況\r\n";
		mainAlert += msg_2 + "\r\n";
	}
	if (mainAlert.length > 0) {
		alert(mainAlert);
	}
}

/**
 * BM2 更新訂單資料
 * 
 * @param {}
 *            datas
 */
Mogan.orderTrace.refreshOrderData = function(datas) {
	/**
	 * 訂單資料
	 */
	Ext.getCmp('comboOrderPayType').reset();
	Ext.getCmp('labelOrderMoneyAlert').setText('');

	Ext.getCmp('labelOrderMemberMobile').setText(datas['tel']);
	Ext.getCmp('labelOrderMemberEMail').setText(datas['email']);
	Ext.getCmp('labelOrderItemCount').setText(datas['items_count']);

	// 國際運費
	Ext.getCmp('labelIntShipCost').setText(datas['cost_9']);
	// 匯率
	Ext.getCmp('labelExchange').setText(datas['exchange']);
	Ext.getCmp('labelOrderWebSite').setText(datas['website_name']);// 網站名稱
	Ext.getCmp('labelOrderNote').setText(datas['alert_group'], false); // 備忘

	Ext.getCmp('orderDataPanel').getForm().setValues(datas);

	Mogan.orderTrace.fixTotalPrice(); // 修正支出總額

	// 結清狀態
	if (Ext.isEmpty(datas['ship_type'])) {
		Ext.getCmp('radiogroupOrderShipType').setValue('rb_ship_type_0', false);
		Ext.getCmp('radiogroupOrderShipType').setValue('rb_ship_type_1', false);
	} else {
		switch (Number(datas['ship_type'])) {
			case 0 :
				Ext.getCmp('radiogroupOrderShipType').setValue(
						'rb_ship_type_0', true);
				break;
			case 1 :
				Ext.getCmp('radiogroupOrderShipType').setValue(
						'rb_ship_type_1', true);
				break;
		}
	}

	if (datas['delete_flag'] != '0' && datas['tide_status'] == '3-01'
			|| datas['tide_status'] == '3-02') {
		if (Ext.getCmp("btnOrderSaveCost")) {
			Ext.getCmp("btnOrderSaveCost").setDisabled(false);
		}
		if (Ext.getCmp("btnOrderSubmitCost")) {
			Ext.getCmp("btnOrderSubmitCost").setDisabled(false);
		}
	} else {
		if (Ext.getCmp("btnOrderSaveCost")) {
			// Ext.getCmp("btnOrderSaveCost").setDisabled(true);
		}

		if (Ext.getCmp("btnOrderSubmitCost")) {
			Ext.getCmp("btnOrderSubmitCost").setDisabled(true);
		}
	}
	// Ext.getCmp('textfieldOrderServiceCost').setValue(datas['cost_8']);

	/*
	 * Ext.getCmp('textfieldOrderServiceCost')
	 * .setValue(json['responseData'][0]['Datas']['cost_8']);
	 * 
	 * if (Ext.isEmpty(json['responseData'][0]['Datas']['ship_type'])) {
	 * Ext.getCmp('radiogroupOrderShipType').setValue('rb_ship_type_0', false);
	 * Ext.getCmp('radiogroupOrderShipType').setValue('rb_ship_type_1', false); }
	 * else { switch (Number(json['responseData'][0]['Datas']['ship_type'])) {
	 * case 0 : Ext.getCmp('radiogroupOrderShipType').setValue(
	 * 'rb_ship_type_0', true); break; case 1 :
	 * Ext.getCmp('radiogroupOrderShipType').setValue( 'rb_ship_type_1', true);
	 * break; } }
	 * 
	 * Ext.getCmp('textfieldOrderServiceCost')
	 * .setValue(json['responseData'][0]['Datas']['cost_8']);
	 */
}

/**
 * 修正支出總額
 */
Mogan.orderTrace.fixTotalPrice = function() {
	var totalPirce = 0.0;
	// 商品總價
	var itemTalPrice = Ext.getCmp('orderDataPanel').getForm().getValues()['item_total_price']
			- 0;
	// 稅金
	var cost3 = Ext.getCmp('orderDataPanel').getForm().getValues()['cost_3']
			- 0;
	// 當地運費
	var cost4 = Ext.getCmp('orderDataPanel').getForm().getValues()['cost_4']
			- 0;
	// totalPirce = itemTalPrice + cost3 + cost4;
	// 其他費用
	var cost6 = Ext.getCmp('orderDataPanel').getForm().getValues()['cost_6']
			- 0;
	totalPirce = itemTalPrice + cost3 + cost4 + cost6;

	Ext.getCmp('textfieldOrderRemitOut').setText(
			totalPirce + '<br /> (' + itemTalPrice + '+' + cost3 + '+' + cost4
					+ '+' + cost6 + ')', false);
}

/**
 * 讀取賣家資料，包含賣家收款方式
 */
Mogan.orderTrace.loadSellerData = function(datas, accounts) {

	Ext.getCmp('labelSellerId').setText(
			Mogan.orderTrace.rendererFixWebsiteIcon(datas['website_id']) + ' '
					+ datas['account'], false); // 賣家帳號

	Ext.getCmp('sellerDataPanel').getForm().setValues(datas);

	sellerPayTypeJSONData['root'] = accounts;
	Mogan.orderTrace.sellerPayType.load();
	Mogan.orderTrace.sellerPayType.filter('is_active', '0');
	if (Mogan.orderTrace.sellerPayType.find('account_id', Ext
					.getCmp('comboOrderPayType').getValue()) < 0) {
		Ext.getCmp('comboOrderPayType').reset();
		Ext.getCmp('labelOrderMoneyAlert').setText('');
	}

	sellerAccountData['root'] = accounts;
	Mogan.orderTrace.sellerAccountListStore.commitChanges();
	Mogan.orderTrace.sellerAccountListStore.load();

}

/**
 * 讀取商品訂單資料 BM2
 * 
 * @param {}
 *            grid
 * @param {}
 *            rowIndex
 * @param {}
 *            e
 */
Mogan.orderTrace.loadOrderData = function(grid, rowIndex, e) {
	var r = grid.getStore().getAt(rowIndex);
	// 先讀取資料庫的資料，再讀取網頁上的資料如有更新就會提示使用者

	Ext.getCmp('orderDataPanel').getForm().loadRecord(r);

	Ext.getCmp('DetilPanel').setDisabled(false);
	Ext.getCmp('labelOrderId').setText(r.data['tide_id']);// 訂單編號
	Ext.getCmp('labelOrderMemberName').setText(r.data['full_name']);// 會員名稱

	Ext.getCmp('labelOrderSellerName').setText((Ext
			.isEmpty(r.data['seller_name']))
			? r.data['seller_account_eval']
			: r.data['seller_account_eval'] + " - " + r.data['seller_name']);// 賣家名稱

	Ext.MessageBox.show({
				msg : '訂單資料讀取中...',
				progressText : 'Loading...',
				width : 300,
				wait : true,
				waitConfig : {
					interval : 200
				}
			});
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == "failure") {
						Ext.Msg.alert("錯誤", json['responseMsg']);
					} else {
						Mogan.orderTrace.showOrderData(response);
						Ext.Msg.hide();
						// 自動跳回第一個tab
						Ext.getCmp("DetilPanel").setActiveTab(0);
						Ext.getCmp('DetilPanel').setDisabled(false);
					}
				},
				failure : function(response) {
					Ext.Msg.alert("錯誤", "請向程式開發者詢問");
				},
				params : {
					APP_ID : appId,
					ACTION : "LOAD_TRADE_ORDER_DATA",
					RETURN_TYPE : "JSON",
					MODEL_NAME : "BM2",
					TIDE_ID : r.data['tide_id']
				}
			});
}

/**
 * BM2 初始化訂單篩選狀態
 */
Mogan.orderTrace.fixFilterStatus = function() {
	if (!Ext.isEmpty(Ext.getCmp("btnGpStstusKey"))) {
		Mogan.orderTrace.filterStatus = new Array();
		for (var i = 0; i < Ext.getCmp("btnGpStstusKey").items.items.length; i++) {
			if (Ext.getCmp("btnGpStstusKey").items.items[i].pressed) {
				Mogan.orderTrace.filterStatus[Mogan.orderTrace.filterStatus.length] = {
					key : Ext.getCmp("btnGpStstusKey").items.items[i].pValue,
					value : Ext.getCmp("btnGpStstusKey").items.items[i].pressed
				};
			}
		}

	}
}

/**
 * 修正資料篩選條件，訂單狀態未選則無法送出查詢 BM2
 * 
 * @param {}
 *            store
 * @param {}
 *            options
 * @return {Boolean}
 */
Mogan.orderTrace.getloadBidItemsURL = function(store, options) {
	if (Ext.getCmp('chkPowerSearch').getValue()
			&& Ext.getCmp('comboSearchKey').getValue().trim() == 0) {
		Ext.MessageBox.alert("請設定關鍵字", "強力搜尋下關鍵字為必填  ");
		return false;
	}
	Mogan.orderTrace.fixFilterStatus();
	if (Mogan.orderTrace.filterStatus.length == 0) {
		Ext.MessageBox.alert("請重新設定訂單狀態", "訂單狀態未選擇.<br />請重新設定訂單狀態   ");
		return false;
	}
	loadBidItemsParams.STATUS_CONDITION = Ext
			.encode(Mogan.orderTrace.filterStatus);
	loadBidItemsParams.CONDITION_KEY = Ext.encode({
				SEARCH_KEY : Ext.getCmp('comboSearchKey').getValue().trim(),
				ACCOUNT_ID : Ext.getCmp('comboAccount').getValue(),
				ACCOUNT : Ext.getCmp('comboAccount').getRawValue(),
				IS_POWER_SEARCH : Ext.getCmp('chkPowerSearch').getValue(),
				DEFAULT_VALUE : '-'
			});

	store.baseParams = loadBidItemsParams;
	options['callback'] = function(r, options, success) {
		if (!success) {
			alert("讀取錯誤，請重新整理畫面或登入");
		}
	}

	return true;
}

/**
 * 發送訊息 BM2
 * 
 * @param {}
 *            value
 */
Mogan.orderTrace.sendMsg = function() {
	var textareaMsgContent = Ext.getCmp("textareaMsgContent");// 留言內容
	var textfieldMsgTitle = Ext.getCmp("textfieldMsgTitle");// MAIL TITLE
	var comboMsgTitle = Ext.getCmp("comboMsgTitle");// 標準格式 TITLE
	var comboMsgCategory = Ext.getCmp("comboMsgCategory");// 留言方式

	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
					// Ext.Msg.hide();
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == "failure") {
						Ext.Msg.alert("錯誤", json['responseMsg']);
					} else {
						if (json['responseData'][0]) {
							Ext.Msg.confirm("請確認", "訊息已發出，是否清空已發出的訊息",
									function(btn, text) {
										if (btn == 'yes') {
											textareaMsgContent.setValue("");
										}
									});
						} else {
							Ext.Msg.alert("錯誤", "訊息未送出");
						}

					}
				},
				failure : function(response) {
					Ext.Msg.alert("錯誤", "請向程式開發者詢問");
				},
				params : {
					APP_ID : appId,
					ACTION : "SEND_MESSAGE",
					RETURN_TYPE : "JSON",
					MODEL_NAME : "BM2",
					MSG_DATAS : Ext.encode(Ext.getCmp('msgSenderPanel')
							.getForm().getValues())
				}
			});
}

/**
 * 將訊息設定為已讀
 * 
 * @param {}
 *            value
 */
Mogan.orderTrace.readMsg = function(value, itemOrderId) {
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
					Ext.Msg.hide();
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == "failure") {
						Ext.Msg.alert("錯誤", json['responseMsg']);
					} else {
						var msgRecordStore = Ext.getCmp("msgRecordPanel")
								.getStore();
						msgRecordStore.find('contact_id', value);
						var record = msgRecordStore.getAt(msgRecordStore.find(
								'contact_id', value));

						record.set('read_date',
								json['responseData'][0]['read_date']);
						record.set('is_read',
								json['responseData'][0]['is_read']);
						record.commit();

					}
				},
				failure : function(response) {
					Ext.Msg.alert("錯誤", "請向程式開發者詢問");
				},
				params : {
					APP_ID : appId,
					ACTION : "READ_TRANSACTION_MSG",
					RETURN_TYPE : "JSON",
					MODEL_NAME : "BM2",
					ITEM_ORDER_ID : itemOrderId,
					CONTACT_ID : value
				}
			});
}

/**
 * 修正留言範本，先詢問使用者是否替換
 * 
 * @param {}
 *            comboBox
 */
Mogan.orderTrace.fixTextareaMsgContent = function(comboBox) {
	var templateIndex = comboBox.getValue();

	if (Mogan.orderTrace.templateSatus == 'EDIT') {
		loadTemplate('yes');
	} else {
		Ext.Msg.confirm("請確認", "是否將訊息內容更換為["
						+ comboBox.getStore().getAt(templateIndex)
								.get('fileName') + "]內容", loadTemplate);
	}
	function loadTemplate(btn, text) {
		if (btn == 'yes') {
			if (Mogan.orderTrace.templateSatus == 'EDIT') {
				Ext.getCmp('msgSaveBtn').setDisabled(false);
			}
			if (Ext.isEmpty(comboBox.getStore().getAt(templateIndex))) {
				Mogan.orderTrace.updateTextareaMsgContent("");
			} else if (comboBox.getStore().getAt(templateIndex)
					.get('loadStatus')) {
				var msgTemplate = comboBox.getStore().getAt(templateIndex)
						.get('fileContent');
				Mogan.orderTrace.updateTextareaMsgContent(msgTemplate);
			} else {
				Mogan.orderTrace.updateTextareaMsgContent('讀取中....');
				Mogan.orderTrace.loadTempletContent(comboBox.getStore()
						.getAt(templateIndex).get('fileName'));
			}
		}
	}

}

/**
 * 更新訊息內容，同時將被mapping的關鍵字取代
 * 
 * @param {}
 *            msg
 */
Mogan.orderTrace.updateTextareaMsgContent = function(msg) {
	if (Mogan.orderTrace.templateSatus == 'EDIT') {

	} else {
		var m = Mogan.orderTrace.trnsListStore.data.items.slice(0);
		Ext.each(m, function(item) {
			var re = new RegExp(item.data['trnsCode'].replace("$", "\\$"), "g");

			var orderData = Mogan.orderTrace.orderItemListStore
					.getAt(Mogan.orderTrace.orderItemListStore.find(
							'item_order_id', Ext.getCmp('comboItemOrderList2')
									.getValue()));
			if (!Ext.isEmpty(orderData.data[item.data['trnsData']])) {
				msg = msg.replace(re, orderData.data[item.data['trnsData']]);
			}

		});
	}
	Ext.getCmp("textareaMsgContent").setValue(msg);
}

/**
 * 
 * @param {}
 *            templetName 範本名稱
 */
Mogan.orderTrace.loadTempletContent = function(templetName) {
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == "failure") {
						Ext.Msg.alert("錯誤", json['responseMsg']);
					} else {
						var msg = json['responseData'][0]['fileContent'];
						if (msg.length == 0) {
							Ext.Msg.alert("通知", templetName + ' 的內容是空的');
						}
						Mogan.orderTrace.updateTextareaMsgContent(msg);
						Ext.getCmp('templateListStore').getAt(templateIndex)
								.set('loadStatus', true);
					}
				},
				failure : function(response) {
					Ext.Msg.alert("錯誤", "請向程式開發者詢問");
				},
				params : {
					APP_ID : appId,
					ACTION : "LOAD_TEMPLATE",
					RETURN_TYPE : "JSON",
					MODEL_NAME : "TEMPLET_MANAGER",
					TEMPLATE_MODEL : "BM2",
					TEMPLATE_NAME : templetName
				}
			});
}

/**
 * 修正留言tilte BM2
 * 
 * @param {}
 *            comboBox
 */
Mogan.orderTrace.fixComboMsgTitle = function(comboBox) {
	var msgContact = comboBox.getValue();
	var msgTitleData = comboBox.getStore().getAt(msgContact).get('data');

	if (Ext.isEmpty(msgTitleData)) {
		Ext.getCmp("comboMsgTitle").setDisabled(true);
		Ext.getCmp("textfieldMsgTitle").setDisabled(false);
	} else {
		Ext.getCmp("comboMsgTitle").getStore().loadData(msgTitleData);
		Ext.getCmp("comboMsgTitle").setValue(msgTitleData[0][0]);
		Ext.getCmp("textfieldMsgTitle").setDisabled(true);
		Ext.getCmp("comboMsgTitle").setDisabled(false);
	}
}

// 修改範本名稱，測試中
Mogan.orderTrace.updateComboMsgTitle = function(comboBox) {
	var msgContact = comboBox.getValue();
}

/**
 * 設定發送參數
 * 
 * @param {}
 *            record
 */
Mogan.orderTrace.setSenderData = function(record) {
	// Ext.getCmp("hiddenMsgItemId").setValue(record.get("item_id"));

	if (record.get("contact_type").indexOf("@") > 0) {
		Ext.getCmp("comboMsgCategory").setValue('1');
	} else {
		Ext.getCmp("comboMsgCategory").setValue('0');
	}
}

/**
 * 改變範本使用模式， 分為套用及編輯
 */
Mogan.orderTrace.changeTemplateMode = function() {
	var modeBtn = Ext.getCmp('msgChangeTemplateMode');
	if (Mogan.orderTrace.templateSatus == 'LOAD') {
		// 進入編輯模式
		Ext.getCmp('compsMsgTempletToolBar').show();
		Ext.Msg.alert('切換', '進入範本編輯模式');

		Mogan.orderTrace.templateSatus = 'EDIT';
		if (Ext.isEmpty(Ext.getCmp('comboMsgTemplate').getValue())) {
			Ext.getCmp('msgSaveBtn').setDisabled(true);
		} else {
			Ext.getCmp('msgSaveBtn').setDisabled(false);
		}
		Ext.getCmp('msgSaveAsBtn').setDisabled(false);
		Ext.getCmp('msgDelBtn').setDisabled(false);
		Ext.getCmp('msgSendBtn').setDisabled(true);
		modeBtn.setText('範本套用模式');

	} else {
		// 進入套用模式
		Ext.getCmp('compsMsgTempletToolBar').hide();
		Ext.Msg.alert('切換', '進入範本套用模式');
		Mogan.orderTrace.templateSatus = 'LOAD';

		Ext.getCmp('msgSaveBtn').setDisabled(true);
		Ext.getCmp('msgSaveAsBtn').setDisabled(true);
		Ext.getCmp('msgDelBtn').setDisabled(true);
		Ext.getCmp('msgSendBtn').setDisabled(false);
		modeBtn.setText('範本編輯模式');

	}
}

/**
 * 刪除範本
 * 
 * @param {}
 *            status-儲存方式 0=儲存 1=另存新檔
 */
Mogan.orderTrace.delMsg = function() {
	var templateName = Ext.getCmp("comboMsgTemplate").getStore().getAt(Ext
			.getCmp("comboMsgTemplate").getValue()).get('fileName');

	Ext.Msg.confirm("刪除", "是否將本範本" + templateName + "刪除", delMsg);
	var isDel = false;
	/**
	 * 儲存範本
	 */
	function delMsg(btn, text) {
		if (btn == 'yes') {
			isDel = true;
		}
		if (isDel) {
			Ext.Ajax.request({
						url : 'AjaxPortal',
						callback : function() {
						},
						success : function(response) {
							var json = parserJSON(response.responseText);
							if (json['responseResult'] == "failure") {
								Ext.Msg.alert("錯誤", json['responseMsg']);
							} else {

								var r = Ext.getCmp("comboMsgTemplate")
										.getStore().getAt(Ext
												.getCmp("comboMsgTemplate")
												.getValue());
								var store = Ext.getCmp("comboMsgTemplate")
										.getStore();
								var selectIndex = store.indexOf(r);
								store.remove(r);
								store.commitChanges();
								if (store.getCount() > 0) {
									r = Ext.getCmp("comboMsgTemplate")
											.getStore().getAt(selectIndex - 1);
									Ext.getCmp("comboMsgTemplate").setValue(r
											.get('templateIndex'));
								} else {
									Ext.getCmp("comboMsgTemplate").setValue("");
								}
								Mogan.orderTrace.fixTextareaMsgContent(Ext
										.getCmp("comboMsgTemplate"));
							}
						},
						failure : function(response) {
							Ext.Msg.alert("錯誤", "請向程式開發者詢問");
						},
						params : {
							APP_ID : appId,
							ACTION : "DEL_TEMPLATE",
							RETURN_TYPE : "JSON",
							MODEL_NAME : "TEMPLET_MANAGER",
							TEMPLATE_MODEL : "BM2",
							TEMPLATE_NAME : templateName
						}
					});
		}
	}

}

/**
 * 儲存範本資訊
 * 
 * @param {}
 *            status-儲存方式 0=儲存 1=另存新檔
 */
Mogan.orderTrace.saveMsg = function(status) {

	var templateName = '';
	var templateText = '';
	var isSave = false;
	// 顯示對話視窗請使用者確是否要儲存
	switch (status) {
		case 0 :
			templateName = Ext.getCmp("comboMsgTemplate").getStore().getAt(Ext
					.getCmp("comboMsgTemplate").getValue()).get('fileName');
			Ext.Msg.confirm("儲存", "是否將訊息儲存至[" + templateName + "]範本", saveMsg);
			break;
		case 1 :
			Ext.Msg.prompt("另存新範本", "請輸入範本名稱", saveMsg);
			break;
	}

	/**
	 * 儲存範本
	 */
	function saveMsg(btn, text) {
		if (btn == 'yes') {
			isSave = true;
		} else if (btn == 'ok') {
			templateName = text;
			isSave = true;
		}
		if (isSave) {
			templateText = Ext.getCmp("textareaMsgContent").getValue();
			Ext.Ajax.request({
						url : 'AjaxPortal',
						callback : function() {
						},
						success : function(response) {
							var json = parserJSON(response.responseText);
							if (json['responseResult'] == "failure") {
								Ext.Msg.alert("錯誤", json['responseMsg']);
							} else {
								Ext.Msg.alert("儲存功成", "範本[" + templateName
												+ "]儲存完成");
								switch (status) {
									case 0 :
										break;
									case 1 :
										var store = Ext
												.getCmp("comboMsgTemplate")
												.getStore();
										var defaultData = {
											templateIndex : store.getCount(),
											fileName : templateName,
											fileContent : templateText,
											loadStatus : true
										};
										// create new record
										var p = new store.recordType(defaultData);
										store.add(p);
										Ext.getCmp("comboMsgTemplate")
												.setValue(templateName);
										break;
								}
							}
						},
						failure : function(response) {
							Ext.Msg.alert("錯誤", "請向程式開發者詢問");
						},
						params : {
							APP_ID : appId,
							ACTION : "SAVE_TEMPLATE",
							RETURN_TYPE : "JSON",
							MODEL_NAME : "TEMPLET_MANAGER",
							TEMPLATE_MODEL : "BM2",
							TEMPLATE_TEXT : templateText,
							TEMPLATE_NAME : templateName
						}
					});
		}
	}
}

/**
 * 商品列表右鍵快速選單 BM2
 * 
 * @param {}
 *            grid
 * @param {}
 *            rowIndex
 * @param {}
 *            e
 */
Mogan.orderTrace.showItemListMenu = function(grid, rowIndex, e) {
	e.preventDefault();// 打斷瀏覽器右鍵
	var r = grid.getStore().getAt(rowIndex);
	Ext.getCmp('itemListMenuSameSeller').setText('篩選同賣家 - ['
			+ r.data['seller_account'] + ']');
	Ext.getCmp('itemListMenuSameMember').setText('篩選同買家 - ['
			+ r.data['full_name'] + ']');
	Ext.getCmp('itemListMenuSameSeller').pValue = {
		value : r.data['seller_account'],
		key : r.data['seller_account']
	};
	Ext.getCmp('itemListMenuSameMember').pValue = {
		value : r.data['full_name'],
		key : r.data['full_name']
	};

	if (Ext.getCmp('itemListMenuTransOrder')) {
		Ext.getCmp('itemListMenuTransOrder').pValue = {
			tideId : r.data['tide_id'],
			sellerId : r.data['seller_id'],
			memberId : r.data['member_id'],
			itemOrderId : r.data['item_order_id']
		};
	}

	Mogan.orderTrace.itemListMenu.showAt(e.getXY());
}

/**
 * 取回可移動訂單 show BM2
 */
Mogan.orderTrace.getMoveableTideList = function() {
	var menuItem = Ext.getCmp('itemListMenuTransOrder');
	var index = Mogan.orderTrace.itemListStore.find('item_order_id',
			menuItem.pValue['itemOrderId']);
	/**
	 * 2010/8/18 解除移單限制 
	var regex = new RegExp('(1-02|3-0[123])');
	if (!regex
			.test(Mogan.orderTrace.itemListStore.getAt(index).data['tide_status'])) {
				
		return;
	}
	*/
	/*
	 * if (Mogan.orderTrace.itemListStore.getAt(index).data['tide_status']){
	 * return ; }
	 */
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
				},
				success : Mogan.orderTrace.setMoveableTideList,
				failure : function(response) {
					Ext.Msg.alert("錯誤", "請向程式開發者詢問");
				},
				params : {
					APP_ID : appId,
					ACTION : "LOAD_MOVEABLE_ORDER",
					RETURN_TYPE : "JSON",
					MODEL_NAME : "BM2",
					MEMBER_ID : menuItem.pValue['memberId'],
					SELLER_ID : menuItem.pValue['sellerId'],
					TIDE_ID : menuItem.pValue['tideId']
				}
			});
}

/**
 * 設定可移動的訂單範圍 BM2
 * 
 * @param {}
 *            response
 */
Mogan.orderTrace.setMoveableTideList = function(response) {
	var json = parserJSON(response.responseText);
	if (json['responseResult'] == "failure") {
		Ext.Msg.alert("錯誤", json['responseMsg']);
	} else {
		var transOderMenu = Ext.menu.MenuMgr.get("transOderMenu");
		var combineOderMenu = Ext.menu.MenuMgr.get("combineOderMenu");
		transOderMenu.removeAll();
		combineOderMenu.removeAll();
		for (var i = 0; i < json['responseData'].length; i++) {
			var item = new Ext.menu.Item({
						text : json['responseData'][i]['tide_id'] + ' ('
								+ json['responseData'][i]['items_count'] + ')',
						handler : Mogan.orderTrace.moveItem2Order,
						pValue : json['responseData'][i]['tide_id']
					});
			combineOderMenu.addItem(item.cloneConfig());
			transOderMenu.addItem(item.cloneConfig());
		}
		if (combineOderMenu.items.length == 0) {
			Ext.getCmp('itemListMenuCombineOrder').setDisabled(true);
			Ext.getCmp('itemListMenuTransOrder').setDisabled(true);
		} else {
			Ext.getCmp('itemListMenuCombineOrder').setDisabled(false);
			Ext.getCmp('itemListMenuTransOrder').setDisabled(false);
		}
	}
}

/**
 * 轉換訂單
 * 
 * @param {}
 *            menuItem
 */
Mogan.orderTrace.moveItem2Order = function(menuItem) {

	Ext.Ajax.request({
		url : 'AjaxPortal',
		callback : function() {
			Mogan.orderTrace.itemListStore.reload();
		},
		success : function(response) {
			var json = parserJSON(response.responseText);
			if (json['responseResult'] == "failure") {
				Ext.Msg.alert("錯誤", json['responseMsg']);
			}
		},
		failure : function(response) {
			Ext.Msg.alert("錯誤", "請向程式開發者詢問");
		},
		params : {
			APP_ID : appId,
			ACTION : "MOVE_ITEM_2_ORDER",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "BM2",
			ITEM_ORDER_ID : Ext.getCmp('itemListMenuTransOrder')['pValue']['itemOrderId'],
			FROM_TIDE_ID : Ext.getCmp('itemListMenuTransOrder')['pValue']['tideId'],
			TO_TIDE_ID : menuItem.pValue,
			TYPE : menuItem.parentMenu.xValue
		}
	});
}

/**
 * 獨立新訂單
 * 
 * @param {}
 *            menuItem
 */
Mogan.orderTrace.moveItem2NewOrder = function(menuItem) {
	Ext.Ajax.request({
		url : 'AjaxPortal',
		callback : function() {
			Mogan.orderTrace.itemListStore.reload();
		},
		success : function(response) {
			var json = parserJSON(response.responseText);
			if (json['responseResult'] == "failure") {
				Ext.Msg.alert("錯誤", json['responseMsg']);
			} else {
			}
		},
		failure : function(response) {
			Ext.Msg.alert("錯誤", "請向程式開發者詢問");
		},
		params : {
			APP_ID : appId,
			ACTION : "MOVE_ITEM_2_NEW_ORDER",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "BM2",
			ITEM_ORDER_ID : Ext.getCmp('itemListMenuTransOrder')['pValue']['itemOrderId'],
			FROM_TIDE_ID : Ext.getCmp('itemListMenuTransOrder')['pValue']['tideId']
		}
	});
}

/**
 * 點選商品列表右鍵快速選單
 * 
 * @param {}
 *            item
 * @param {}
 *            event
 */
Mogan.orderTrace.clickItemListmenu = function(item, event) {

	// var store = Ext.getCmp('comboSearchKey').getStore();
	// var keyIndex = store.find('value', item.pValue['key']);
	// if (keyIndex >= 0) {
	// } else {
	// var p = new store.recordType(item.pValue);
	// store.add(p);
	// }
	Ext.getCmp('comboSearchKey').setValue(item.pValue['key']);
	Mogan.orderTrace.itemListStore.load();
}

/**
 * 顯示對應名稱列表
 */
var editMsgWindow;
Mogan.orderTrace.showEditTemplate = function() {
	if (trnsWindow == null) {
		Ext.DomHelper.append('iframe-window', {
					tag : 'div',
					id : 'msgTemplatePanel'
				});
		editMsgWindow = new Ext.Window({
					el : 'msgTemplatePanel',
					layout : 'fit',
					title : "代碼對應列表",
					items : [Mogan.orderTrace.createEditTemplatePanel()],
					width : 400,
					height : 360,
					closeAction : 'hide',
					autoScroll : true,
					modal : true
				});
	}
	trnsWindow.show();
}

/**
 * 顯示對應名稱列表
 */
var trnsWindow;
Mogan.orderTrace.showMsgTrnsList = function() {
	if (trnsWindow == null) {
		Ext.DomHelper.append('iframe-window', {
					tag : 'div',
					id : 'trns-list'
				});
		trnsWindow = new Ext.Window({
					el : 'trns-list',
					layout : 'fit',
					title : "代碼對應列表",
					items : [Mogan.orderTrace.createTrnsListGird()],
					width : 480,
					height : 360,
					closeAction : 'hide',
					autoScroll : true,
					modal : true
				});
	}
	trnsWindow.show();
}

/**
 * 名稱對應表專用儲存對話框
 */
Mogan.orderTrace.saveTrnsList = function() {

	var m = Mogan.orderTrace.trnsListStore.data.items.slice(0);
	var jsonArray = [];
	Ext.each(m, function(item) {
				jsonArray.push(item.data)
			});

	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == "failure") {
						Ext.Msg.alert("錯誤", json['responseMsg']);
					} else {
						// Ext.Msg.alert("通知", "對應表儲存成功.");
						Ext.getCmp('trnsListSaveBtn').setText('儲存完成');
					}
				},
				failure : function(response) {
					Ext.Msg.alert("錯誤", "請向程式開發者詢問");
				},
				params : {
					APP_ID : appId,
					ACTION : "SAVE_TRNS_CODE_LIST",
					RETURN_TYPE : "JSON",
					MODEL_NAME : "TEMPLET_MANAGER",
					TEMPLATE_MODEL : "BM2",
					TRNS_CODE_LIST : Ext.encode(jsonArray)
				}
			});
}

/**
 * 顯示備忘編輯畫面 BM2
 */
Mogan.orderTrace.showOrderAlertPanel = function() {
	Mogan.orderTrace.getOrderAlertPanel();
	Ext.getCmp('textareaAlertContent').setValue('');
	Mogan.orderTrace.fixTextareaAlertContent();
	Mogan.orderTrace.orderAlertPanel.show();
}

/**
 * 選擇備忘類型時同時更改textarea區塊的內容 BM2
 */
Mogan.orderTrace.fixTextareaAlertContent = function() {
	var orderData = Ext.getCmp('orderDataPanel').getForm().getValues();
	Ext.getCmp('orderAlertPanelForm').getForm().loadRecord({
				data : {
					alert_text : orderData[Ext.getCmp('comboxAlertType')
							.getValue()]
				}
			});
}
/**
 * BM2 儲存賣家資料
 */
Mogan.orderTrace.saveSellerData = function() {

	var accountData = new Array();
	var r = Mogan.orderTrace.sellerAccountListStore.getModifiedRecords();
	for (var i = 0; i < r.length; i++) {
		accountData.push(r[i]['data']);
	}
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == "failure") {
						Ext.Msg.alert("錯誤", json['responseMsg']);
					} else {
						Mogan.orderTrace.loadSellerData(
								json['responseData'][1][0],
								json['responseData'][1][1]);
						var warnTides = json['responseData'][0];
						var msg = "請注意下列被影響訂單\n";
						var msgFlag = false;
						for (key in warnTides) {
							var tempMsg = "";
							if (warnTides[key].length > 0) {
								tempMsg += key + "\n";
							}
							for (var i = 0; i < warnTides[key].length; i++) {
								tempMsg += "★ " + warnTides[key][i]['tide_id']
										+ "\n";
								msgFlag = true;
							}
							tempMsg += "==\n";
							msg += tempMsg;
						}
						if (msgFlag) {
							alert(msg);
						}

					}
				},
				failure : function(response) {
					Ext.Msg.alert("錯誤", "請向程式開發者詢問");
				},
				params : {
					APP_ID : appId,
					ACTION : "SAVE_SELLER_DATA",
					RETURN_TYPE : "JSON",
					MODEL_NAME : "BM2",
					SELLER_DATA : Ext.encode(Ext.getCmp('sellerDataPanel')
							.getForm().getValues()),
					ACCOUNT_DATA : Ext.encode(accountData)
				}
			});
}

/**
 * BM2 儲存備忘資料
 */
Mogan.orderTrace.saveAlertData = function() {
	var alertData = Ext.getCmp('orderAlertPanelForm').getForm().getValues();
	Ext.MessageBox.show({
				msg : 'Saving...',
				progressText : 'Saving...',
				width : 300,
				wait : true,
				waitConfig : {
					interval : 200
				}
			});
	Ext.Ajax.request({
		url : 'AjaxPortal',
		callback : function() {
		},
		success : function(response) {
			var json = parserJSON(response.responseText);
			if (json['responseResult'] == "failure") {
				Ext.Msg.alert("錯誤", json['responseMsg']);
			} else {
				Ext.getCmp('orderDataPanel').getForm()
						.setValues(json['responseData'][0]);
				Ext.Msg.alert("通知", "儲存成功.");
				Mogan.orderTrace.refreshOrderData(json['responseData'][0]);
				Mogan.orderTrace.fixTextareaAlertContent();
			}
		},
		failure : function(response) {
			Ext.Msg.alert("錯誤", "請向程式開發者詢問");
		},
		params : {
			APP_ID : appId,
			ACTION : "SAVE_ALERT_DATA",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "BM2",
			ALERT_DATA : Ext.encode(alertData),
			TIDE_ID : Ext.getCmp('orderDataPanel').getForm().getValues()['tide_id']
		}
	});
}

/**
 * 判斷資料是否未儲存。 BM2
 */
Mogan.orderTrace.confirmAlertPanel = function() {
	// if (Ext.getCmp('orderAlertPanelForm').getForm().isDirty()) {
	if (Ext.getCmp('textareaAlertContent').isDirty()) {
		Ext.MessageBox.confirm('將關閉備忘編輯畫面', '資料尚未儲存，請是否確定關閉備忘編輯畫面?', function(
						btn) {
					if (btn == 'yes') {
						Ext.getCmp('orderAlertPanel').hide();
					}
				})
	} else {
		Ext.getCmp('orderAlertPanel').hide();
	}
}

/**
 * 確認訂單費用 BM2
 */
Mogan.orderTrace.submitOrderCostMoney = function() {

	var validateMsg = "";
	// 先檢查資料是否正確及完整
	if (!Ext.getCmp('radiogroupOrderShipType').validate()) {
		validateMsg += "費用是否已結清 - 未填.<br />";
	}
	if (!Ext.getCmp('comboOrderPayType').validate()) {
		validateMsg += "費用支付方式 - 未填.<br />";
	}
	if (!Ext.getCmp('textfieldOrderServiceCost').validate()) {
		validateMsg += "手續費 - 未填或格式錯誤 <br />";
	}
	if (!Ext.getCmp('textfieldOrderRemitCost').validate()) {
		validateMsg += "匯款費 - 未填或格式錯誤 <br />";
	}
	if (!Ext.getCmp('textfieldOrderRemitCost').validate()) {
		validateMsg += "其他費用 - 未填或格式錯誤 <br />";
	}
	if (!Ext.getCmp('textfieldOrderTaxCost').validate()) {
		validateMsg += "稅金 - 未填或格式錯誤 <br />";
	}
	if (!Ext.getCmp('orderDataPanel').getForm().isValid()) {
		Ext.Msg.alert("請檢查資料", "請檢查下列欄位<br />" + validateMsg);
		return;
	}

	var orderData = Ext.getCmp('orderDataPanel').getForm().getValues();
	var totalPirce = 0;
	totalPirce += (orderData['item_total_price'] - 0);
	totalPirce += (orderData['cost_3'] - 0);
	totalPirce += (orderData['cost_4'] - 0);
	totalPirce += (orderData['cost_6'] - 0);

	var computeStr = "";
	computeStr = "請確認下列資料是否正確<br />";
	computeStr += "===================<br />";

	if (Mogan.orderTrace.sellerAccountListStore
			.getAt(Mogan.orderTrace.sellerAccountListStore.find('account_id',
					orderData['remit_to'])).get('remit_type') == 'RL-802') {
		computeStr += "付款方式：<label style='font-size:medium;color:#F30;'>"
				+ Ext.getCmp('comboOrderPayType').getRawValue()
				+ "<br /><img src='./resources/mogan/images/1278041790_Warning.png' />訂單狀態會直接改為已付款</label><br />";
	} else {
		computeStr += "付款方式：" + Ext.getCmp('comboOrderPayType').getRawValue()
				+ "<br />";
	}
	computeStr += "支付總額：" + totalPirce + "<br />";
	computeStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
			+ orderData['item_total_price'] + " (商品總價)<br />";
	computeStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
			+ orderData['cost_3'] + " (稅金)<br />";
	computeStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
			+ orderData['cost_4'] + " (當地運費)<br />";
	computeStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
			+ orderData['cost_6'] + " (其他費用)<br />";
	computeStr += "";

	Ext.MessageBox.prompt('請確認訂單支出費用', computeStr, function(btn, money) {

		if (btn != 'ok') {
			return;
		}

		Ext.Ajax.request({
			url : 'AjaxPortal',
			callback : function() {
			},
			success : function(response) {
				var json = parserJSON(response.responseText);
				if (json['responseResult'] == "failure") {
					Ext.Msg.alert("錯誤", json['responseMsg']);
				} else {
					Ext.Msg.alert("訊息", "匯款要求已送出.");
					Mogan.orderTrace.itemListStore.each(function(record) {
						if (record['data']['tide_id'] == orderData['tide_id']) {
							if (Mogan.orderTrace.sellerAccountListStore
									.getAt(Mogan.orderTrace.sellerAccountListStore
											.find('account_id',
													orderData['remit_to']))
									.get('remit_type') == 'RL-802') {

								record.set("tide_status", "3-04");
							} else {
								record.set("tide_status", "3-03");
							}
						}
					});
					Ext.getCmp('editorGridItemList').getView().refresh();
				}
			},
			failure : function(response) {
				Ext.Msg.alert("錯誤", "請向程式開發者詢問");
			},
			params : {
				APP_ID : appId,
				ACTION : "SUBMIT_TRADE_ORDER_MONEY",
				RETURN_TYPE : "JSON",
				MODEL_NAME : "BM2",
				COMMIT_MONEY : money,
				ORDER_DATA : Ext.encode(orderData)
			}
		});
	}, null, false, totalPirce);
}

/**
 * 儲存訂單費用 BM2
 */
Mogan.orderTrace.saveOrderCostMoney = function() {
	var orderData = Ext.getCmp("orderDataPanel").getForm().getValues();
	delete orderData['labelOrderNote'];
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == "failure") {
						Ext.Msg.alert("錯誤", json['responseMsg']);
					} else {
						Ext.Msg.alert("訊息", "訂單費用儲存成功.");
						Mogan.orderTrace.showOrderData(response);
					}
				},
				failure : function(response) {
					Ext.Msg.alert("錯誤", "請向程式開發者詢問");
				},
				params : {
					APP_ID : appId,
					ACTION : "SAVE_TRADE_ORDER_DATA",
					RETURN_TYPE : "JSON",
					MODEL_NAME : "BM2",
					ORDER_DATA : Ext.encode(orderData)

				}
			});
}

/**
 * 自動計算稅金，商品總費 x 0.05 BM2
 */
Mogan.orderTrace.calcuTax = function() {

	var datas = Ext.getCmp('orderDataPanel').getForm().getValues();
	var itemToalPirce = (datas['item_total_price'] - 0);
	Ext.getCmp('textfieldOrderTaxCost').setValue((itemToalPirce * 0.05)
			.toFixed());
}

/**
 * 變換付清狀態,及修正可選類型
 */
Mogan.orderTrace.FixRadiogroupOrderShipType = function() {
	var radiogroupOrderShipType = Ext.getCmp('radiogroupOrderShipType');
	radiogroupOrderShipType.setValue('rb_ship_type_1', true);
}

/**
 * 
 * @param {}
 *            tideId 訂單ID
 * @param {}
 *            type 0- 刪除訂單. 1- 訂單棄標
 */
Mogan.orderTrace.delOrder = function(type) {
	/*
	 * var regex = new RegExp('3-0[123]'); if (!regex
	 * .test(Ext.getCmp('orderDataPanel').getForm().getValues()['tide_status'])) {
	 * Ext.Msg.alert("錯誤", "訂單已付款無法進行 刪單或棄標"); return; }
	 */
	var tideId = Ext.getCmp('orderDataPanel').getForm().getValues()['tide_id'];
	var msg = "";
	var msg2 = "";
	var status = '';
	switch (type) {
		case 0 :
			msg = "刪單";
			/** 已付費用 */
			var count = Mogan.orderTrace.orderItemListStore.getTotalCount();
			var itemIncome = 0;
			var Expend = 0;
			/*
			 * for (var i = 0; i < count; i++) { if
			 * (Mogan.orderTrace.orderItemListStore.getAt(i) .get('o_varchar01') ==
			 * 1) { // 如果已結商品費用，須退費 itemIncome +=
			 * ((Mogan.orderTrace.orderItemListStore .getAt(i).get('buy_price')
			 * Mogan.orderTrace.orderItemListStore.getAt(i) .get('buy_unit') *
			 * 100) / 100); //
			 * Expend+=((Mogan.orderTrace.orderItemListStore.getAt(i).get('buy_price')*Mogan.orderTrace.orderItemListStore.getAt(i).get('buy_unit')*100)/100); } }
			 * msg2 = "刪單時將改收費用退回給會員.<br />已收費用 =====<br />商品費用 --- " +
			 * itemIncome;
			 */
			status = Ext.getCmp('orderDataPanel').getForm().getValues()['tide_status'];
			break;
		case 1 :
			msg = "棄標";
			var itemIncome = 0;
			var expend = 0;
			/*
			 * for (var i = 0; i < count; i++) { if
			 * (Mogan.orderTrace.orderItemListStore.getAt(i) .get('o_varchar01') ==
			 * 1) { // 如果已結商品費用，須退費 itemIncome +=
			 * ((Mogan.orderTrace.orderItemListStore .getAt(i).get('buy_price')
			 * Mogan.orderTrace.orderItemListStore.getAt(i) .get('buy_unit') *
			 * 100) / 100); //
			 * Expend+=((Mogan.orderTrace.orderItemListStore.getAt(i).get('buy_price')*Mogan.orderTrace.orderItemListStore.getAt(i).get('buy_unit')*100)/100); } }
			 */
			// expend=(itemIncome* 0.8475 * 1000)/1000;
			// msg2 = "棄標時將只退回84.75%的商品費用，並收取所有訂單費用.<br />已收費用 =====<br />商品費用
			// --- "+itemIncome+"<br />退回費用 =====<br />商品費用84.75% --- "+expend;
			status = '3-12';
			break;
	}
	// TODO 刪掉調整

	Ext.MessageBox.prompt("警告", "請確認是否 <font color='red' size='6'>[" + msg
					+ "]</font> 並請輸入訂單 <font color='red' size='6'>[" + msg
					+ "]</font> 原因\r\n<br /><br />", function(btn, text) {
				if (btn != 'ok') {
					return;
				}

				if (text.trim() == '') {
					alert("錯誤，請輸入 [" + msg + "] 原因");
					Mogan.orderTrace.delOrder(type);
					return;
				}

				Ext.MessageBox.show({
					msg : '資料處理中...',
					progressText : 'Processing...',
					width : 300,
					wait : true,
					waitConfig : {
						interval : 200
					}
						// icon : 'mail' // custom class in msg-box.html
					});

				Ext.Ajax.request({
					url : 'AjaxPortal',
					callback : function() {
					},
					success : function(response) {
						var json = parserJSON(response.responseText);
						if (json['responseResult'] == "failure") {
							Ext.Msg.alert("錯誤", json['responseMsg']);
						} else {
							Ext.Msg
									.alert(
											"訊息",
											"訂單"
													+ msg
													+ "("
													+ Ext
															.getCmp('orderDataPanel')
															.getForm()
															.getValues()['tide_id']
													+ ")成功.");

							Mogan.orderTrace.fixItemOrderListData("tide_id",
									tideId, "tide_status", status);

							if (type == '0') {
								Mogan.orderTrace.fixItemOrderListData(
										"tide_id", tideId, "delete_flag", "0");
							}
							Mogan.orderTrace.showOrderData(response);
						}
					},
					failure : function(response) {
						Ext.Msg.alert("錯誤", "請向程式開發者詢問");
					},
					params : {
						APP_ID : appId,
						ACTION : "DEL_TIDE",
						RETURN_TYPE : "JSON",
						MODEL_NAME : "BM2",
						DEL_TYPE : type,
						TIDE_ID : Ext.getCmp('orderDataPanel').getForm()
								.getValues()['tide_id'],
						MSG : text.trim()
					}
				});
			}, this, true);
}

/**
 * 修正訂單資料
 * 
 * @param {}
 *            conditField 條件欄位
 * @param {}
 *            conditValue 條件值
 * @param {}
 *            changeField 變更欄位
 * @param {}
 *            newValue 變更值
 */
Mogan.orderTrace.fixItemOrderListData = function(conditField, conditValue,
		changeField, newValue) {
	Mogan.orderTrace.itemListStore.each(function(record) {
				if (record['data'][conditField] == conditValue) {
					record.set(changeField, newValue);
				}
			});
	Ext.getCmp('editorGridItemList').getView().refresh();
}

/**
 * 新增付款方式 BM2
 */
Mogan.orderTrace.addSellerAccount = function() {
	var orderData = Ext.getCmp("sellerAccountPanelForm").getForm().getValues();
	orderData['seller_id'] = Ext.getCmp('orderDataPanel').getForm().getValues()["seller_id"];
	var remit_value = Ext.getCmp("sellerAccountPanelForm").getForm().getValues()
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == "failure") {
						Ext.Msg.alert("錯誤", json['responseMsg']);
					} else {
						if (json['responseData'][0] == "duplicate") {
							Ext.Msg.alert("訊息", "賣家收款方式重複.");
						} else {
							Mogan.orderTrace.loadSellerData(
									json['responseData'][0],
									json['responseData'][1]);
							Ext.Msg.alert("訊息", "賣家收款方式新增成功.");
						}
					}
				},
				failure : function(response) {
					Ext.Msg.alert("錯誤", "請向程式開發者詢問");
				},
				params : {
					APP_ID : appId,
					ACTION : "ADD_SELLER_ACCOUNT",
					RETURN_TYPE : "JSON",
					MODEL_NAME : "BM2",
					ACCOUNT_DATA : Ext.encode(orderData)
				}
			});
}

/**
 * 分析備註欄的賣家收款方式資料
 * 
 * @param {}
 *            str
 * @return {Boolean} true=分析成功，flase=分析失敗，可能是格式錯誤
 */
Mogan.orderTrace.processSellerAccount = function(str) {
	var datas = str.split(/\n/);
	var dataObj = new Object();
	if (datas.length >= 3) {
		// 需多於三行
		if (datas[0].replace(/\s/g, '').search('ぱるる') == 0) {
			dataObj['remit_type'] = 'RL-805'; // 付款方式
			dataObj['bank_name'] = '郵局'; // 銀行名稱，自動帶入
			dataObj['branch_name'] = datas[1].replace(/\s/g, ''); // 分行名稱，第二行
			dataObj['account_no'] = datas[2].replace(/\s/g, ''); // 帳戶碼號，第三行
			dataObj['account_name'] = datas[3].replace(/\s/g, ''); // 帳戶名稱，第四行
		} else if (datas[0].replace(/\s/g, '').search('銀行') == 0) {
			dataObj['remit_type'] = 'RL-806'; // 付款方式
			dataObj['bank_name'] = datas[1].replace(/\s/g, ''); // 銀行名稱，第二行
			dataObj['branch_name'] = datas[2].replace(/\s/g, ''); // 分行名稱，第三行
			dataObj['account_no'] = datas[3].replace(/\s/g, ''); // 帳戶碼號，第四行
			dataObj['account_name'] = datas[4].replace(/\s/g, ''); // 帳戶名稱，第五行
		} else {
			return false;
		}
		Ext.getCmp('sellerAccountPanelForm').getForm().setValues(dataObj);
	}
	return true;

	//
	// 第二行是銀行名稱
	// 第三行是郵局
}

// ////////////////////////////
// 事件區 EVENT //
// ///////////////////////////

/**
 * 
 * @param {}
 *            grid 來源表格
 * @param {}
 *            rowIndex 來源資料列
 * @param {}
 *            columnIndex 來源資料欄位
 * @param {}
 *            e
 */
Mogan.orderTrace.cellclick = function(grid, rowIndex, columnIndex, e) {

	if (grid.getId() == "sellerAccountGrid") {
		// 賣家收款方式列表

	}
	alert("grid:" + grid);
	alert("rowIndex:" + rowIndex);
	alert("columnIndex:" + columnIndex);
	alert("e:" + e);
}

/**
 * 賣家收款方式列表欄位被click
 * 
 * @param {}
 *            grid
 * @param {}
 *            rowIndex
 * @param {}
 *            columnIndex
 * @param {}
 *            e
 */
Mogan.orderTrace.saGridCellClick = function(grid, rowIndex, columnIndex, e) {
	if (columnIndex == "1") {
		if (Mogan.orderTrace.sellerAccountListStore.getAt(rowIndex)
				.get("is_active") == "1") {
			Mogan.orderTrace.sellerAccountListStore.getAt(rowIndex).set(
					"is_active", "0");
		} else {
			Mogan.orderTrace.sellerAccountListStore.getAt(rowIndex).set(
					"is_active", "1");
		}
		Mogan.orderTrace.saveSellerData();
	}

}

/**
 * 付款方式維護介面
 */
Mogan.orderTrace.fixSellerAccountPanel = function() {
	var name = '';
	switch (Ext.getCmp('orderDataPanel').getForm().getValues()['website_id']) {
		case 'SWD-2009-0001' :
			name = "<img src='./resources/mogan/images/1271299827_yahoo_protocol.png' /> ";
			break;
	}
	name += Ext.getCmp('orderDataPanel').getForm().getValues()["seller_account"];
	Ext.getCmp('labelSellerAccount').setText(name, false);

}

/**
 * 選擇付款方式時觸發 RL-802 = 代金引換 , 自動將付款狀態改為未付 BM2
 * 
 * @param {}
 *            combo
 * @param {}
 *            record
 * @param {}
 *            index
 */
Mogan.orderTrace.comboOrderPayTypeSelect = function(combo, record, index) {
	var radiogroupOrderShipType = Ext.getCmp('radiogroupOrderShipType');

	if (record == null) {
		Ext.getCmp('rb_ship_type_1').setDisabled(false);
		return;
	}

	if (record.get('remit_type') == 'RL-802') {
		Mogan.orderTrace.FixRadiogroupOrderShipType();
		radiogroupOrderShipType.setValue('rb_ship_type_0', true);
		Ext.getCmp('rb_ship_type_1').setDisabled(true);
		Ext.getCmp('textfieldOrderRemitCost').setValue("0");
	} else {
		Ext.getCmp('rb_ship_type_1').setDisabled(false);
	}

	Ext.getCmp('labelOrderMoneyAlert').setText(record.get('bank_name') + ' '
			+ record.get('branch_name') + ' ' + record.get('account_no'));
}

/**
 * 刷新表格畫面
 * 
 * @param {}
 *            gridId 表格ID
 */

Mogan.orderTrace.refreshGrid = function(gridId) {
	Ext.getCmp(gridId).view.refresh();
}

/**
 * 修正 聯絡方法顯示值
 */
Mogan.orderTrace.rendererContactType = function(value) {
	var html = "";
	if (value == "0") {
		html = "留言板";
	} else if (value == "1") {
		html = "e-mail";
	} else if (value == "2") {
		html = "揭示版";
	}
	return html;
}

/**
 * 修正 icon圖示
 */
Mogan.orderTrace.rendererOrderFormIcon = function(value, metaData, record) {
	var icons = "";
	if (value == "2" || value == "3") {
		icons = "<img src='./resources/mogan/images/form_edit.png' title='需填寫Order Form'/>";
	} else {
		// html = "<img src='./resources/mogan/images/form_edit_pale.gif' />";
	}
	if (record.get('contact_type') == 1) {
		icons += " <img src='./resources/mogan/images/1278577528_Mail.png' title='以mail聯絡'/>";
	}
	return icons;
}

/**
 * 修正未匯款數量 大於0用紅色字體
 * 
 * @param {}
 *            value
 * @return {String}
 */
Mogan.orderTrace.rendererFixNonRemitCount = function(value) {
	if (value > 0) {
		return "<font color='red'>" + value + "</font>";
	} else {
		return value;
	}
}

/**
 * 修正讀取按鈕
 * 
 * @param {}
 *            value
 */
Mogan.orderTrace.rendererReadMsg = function(value, metaData, record, rowIndex,
		colIndex, store) {
	var btnHtml = "<input type='button' value='讀取 " + value + "' />";

	if (value == 0) {
		btnHtml = "<input type='button' value='讀取' onclick=\"Mogan.orderTrace.readMsg('"
				+ record['data']['contact_id']
				+ "','"
				+ record['data']['item_order_id']
				+ "')\" />"
				+ record['data']['msg_category'];
	} else {
		btnHtml = "<input type='button' value='已讀取' disabled=true />"
				+ record['data']['msg_category'];
	}
	return btnHtml;
};

/**
 * 修正讀取時間，如果沒有時間，顯示為未讀取
 * 
 * @param {}
 *            value
 * @return {String}
 */
Mogan.orderTrace.rendererFixDate = function(value) {
	if (value == null) {
		return "未讀取";
	} else {
		return value;
	}
};

/**
 * 修正網站顯示ICON BM2
 * 
 * @param {}
 *            value 網站ID
 * @return {}
 */
Mogan.orderTrace.rendererFixWebsiteIcon = function(value) {
	var icon = "";
	if (value == 'SWD-2009-0001') {
		icon = "<img src='./resources/mogan/images/1271299827_yahoo_protocol.png' />"
	}
	return icon;
};

/**
 * 顯示是否有被取消的記錄BM2
 * 
 * @param {}
 *            value 網站ID
 * @return {}
 */
Mogan.orderTrace.rendererFixCancelAlert = function(value) {
	var icon = "";
	if (value == '2') {
		// 得標後被取消
		icon = "<img src='./resources/mogan/images/1277874677_alert.png' title='得標後被取消'/>"
	} else if (value == '1') {
		// 下標時被取消
		icon = "<img src='./resources/mogan/images/1277874689_preferences-desktop-notification.png' title='下標時被取消'/>"
	}
	return icon;
};

/**
 * 會員商品費用支付狀態
 * 
 * @param {}
 *            value 0- 未結商品費用. 1- 已結商品費用
 * @return {}
 */
Mogan.orderTrace.rendererFixMemberPayStatus = function(value) {
	var icon = "";
	if (value == '0') { // 0- 未結商品費用
		icon = "<img src='./resources/mogan/images/1273741011_currency_dollar red.png' title='未結商品費用'/>"
	} else if (value == '1') { // 1- 已結商品費用
		icon = "<img src='./resources/mogan/images/1273740966_currency_dollar green.png' title='已結商品費用'/>"
	}
	return icon;
};

/**
 * 修正訂單狀態 BM2
 * 
 * @param {}
 *            value
 * @return {String}
 */
Mogan.orderTrace.rendererFixOrderStatus = function(value, metaData, record) {
	var value = Mogan.orderTrace.statusNameMap[value];
	if (record.get('delete_flag') == '0') {
		value += "<img src='./resources/mogan/images/1273740478_trash_16x16.gif' />"
	}
	return value;
};

/**
 * orderform 填寫按鈕
 * 
 * @param {}
 *            value
 * @return {}
 */
Mogan.orderTrace.rendererOrderFormBtn = function(value, metaData, record,
		rowIndex) {

	var btn = '<input type="button" value="不用填寫" disabled=true />';
	switch (value - 0) {
		case 1 :
			btn = '<input type="button" value="不用填寫" onclick="Mogan.orderTrace.showItemOrderForm('
					+ rowIndex + ')" disabled=true/>';
			break;
		case 2 :
			btn = '<input type="button" value="尚未填寫" onclick="Mogan.orderTrace.showItemOrderForm('
					+ rowIndex + ')" />';
			break;
		case 3 :
			btn = '<input type="button" value="填寫完成" onclick="Mogan.orderTrace.showItemOrderForm('
					+ rowIndex + ')" />';
			break;
	}
	return btn;
}

/**
 * 
 * @param {}
 *            value
 * @param {}
 *            metaData
 * @param {}
 *            record
 * @param {}
 *            rowIndex
 * @return {}
 */
Mogan.orderTrace.rendererMsgStatus = function(value, metaData, record, rowIndex) {

	switch (value.split('/')[0] - 0) {
		case 0 :
			return value;
			break;
		default :
			return "<span style='color:red'>" + value + "</span>";
			break;
	}
	return value;
}

/**
 * 
 * @param {}
 *            e
 */
Mogan.orderTrace.focusItemImg = function(imgId) {
	// alert(imgId);
	Ext.getDom(imgId).setAttribute('style',
			'margin:5px;border:3px solid #3799FF');
	Mogan.orderTrace.showItemImg('win_' + imgId, Ext.getDom(imgId).src);
}

/**
 * 
 * @param {}
 *            e
 */
Mogan.orderTrace.mouseoutItemImg = function(imgId) {
	// alert(imgId);
	Ext.getDom(imgId).setAttribute('style',
			'margin:5px;border:3px solid #CCCCCC');
}

/**
 * 付款方式
 * 
 * @param {}
 *            value
 * @param {}
 *            metaData
 * @param {}
 *            record
 * @param {}
 *            rowIndex
 * @return {}
 */
Mogan.orderTrace.rendererRemitType = function(value, metaData, record, rowIndex) {
	return payTypeMap[value];
}
/**
 * 修正顯示名稱，有系統帳號就顯示系統帳號
 * @param {} value
 * @param {} metaData
 * @param {} record
 * @param {} rowIndex
 */
Mogan.orderTrace.rendererLogUserName = function (value, metaData, record, rowIndex){
	if (Ext.isEmpty(value)){
		return record.get('system_name');
	}else{
		return record.get('member_name');
	}
}

Mogan.orderTrace.rendererLogItemId =function (value, metaData, record, rowIndex){
	if (Ext.isEmpty(record.get('item_id'))){
		return value;
	}else{
		return value+" / "+record.get('item_id');
	}
	
}
/**
 * 
 * @param {}
 *            value
 * @param {}
 *            metaData
 * @param {}
 *            record
 * @param {}
 *            rowIndex
 * @return {}
 */
Mogan.orderTrace.rendererRemitStatus = function(value, metaData, record,
		rowIndex) {
	var icon = "";
	if (value == "1") {
		icon = "<img src='./resources/mogan/images/stop-icone-9406-16.png' />";
	} else {

	}
	return icon;
}