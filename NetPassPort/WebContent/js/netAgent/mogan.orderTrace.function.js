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
	ORDER_BY : 'time_at_03',
	DIR : 'ASC'
};

/**
 * 取得競標商品資料
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
		SEARCH_KEY : Ext.getCmp('comboSearchKey').getValue(),
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

	Mogan.orderTrace.updateBidItemData(grid, rowIndex, e);// 更新商品資料
	// Mogan.orderTrace.setSenderData(r);// 設定訊息發送Tab
	Mogan.orderTrace.loadItemOrderForm();// 設定是否顯示order form

	if (Mogan.orderTrace.templateSatus == 'EDIT') {
		Mogan.orderTrace.changeTemplateMode();
	}

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
 *            record
 */
Mogan.orderTrace.openItemOrderForm = function(record) {
	/**
	 * if 需要填order form{ 讀取網頁上的order form }else{ 顯示不必填寫的畫面或是讓TAB失效 }
	 * 
	 */

	var itemPanel = Ext.getCmp("itemPanel");

	if (false || itemPanel.getForm().getValues()['isOrderForm']) {

	} else {
		Ext.DomHelper.append('iframe-window', {
					tag : 'div',
					id : 'YJ-orderForm'
				});
		var win = new Ext.Window({
			el : 'YJ-orderForm',
			layout : 'fit',

			html : "<iframe src='./ProxyProtal?APP_ID="
					+ appId
					+ "&MODEL_NAME=ItemOrderFormYJ&ACTION=GET_ORDER_FORM&BID_ACCOUNT="
					+ Ext.getCmp("itemPanel").getForm().getValues()['agent_account']
					+ "&"
					+ "ITEM_ID="
					+ Ext.getCmp("itemPanel").getForm().getValues()['item_id']
					+ "&"
					+ "SELLER_ACCOUNT="
					+ Ext.getCmp("itemPanel").getForm().getValues()['sell_name']
					+ "'"
					+ " style='width:100%; height:100%;' frameborder='0' />",
			width : 800,
			height : 600,
			closeAction : 'close',
			autoScroll : true,
			modal : true
		});
		win.show();
	}
}

/**
 * 將指定內容複製到賣方資訊&內容中(Renote)
 * 
 * @param {}
 *            index
 */
Mogan.orderTrace.copyConactDataToRenote = function(index) {

	var strTagStrippedText = Ext.getCmp("msgRecordPanel").store.getAt(index).data.msg_contact;
	if (Ext.getCmp("itemPanel").getForm().getValues()['renote'].length > 0) {
		strTagStrippedText = Ext.getCmp("itemPanel").getForm().getValues()['renote']
				+ '<BR />' + strTagStrippedText;
	}

	Ext.MessageBox.show({
		msg : '資料複製中...',
		progressText : 'Saving...',
		width : 300,
		wait : true,
		waitConfig : {
			interval : 200
		}
			// icon : 'mail' // custom class in msg-box.html
		});
	var updateInfo = new Object();
	updateInfo['renote'] = strTagStrippedText;

	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
					Ext.Msg.alert("訊息", "儲存完成");
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == "failure") {
						Ext.Msg.alert("錯誤", json['responseMsg']);
					} else {
						Ext.getCmp("itemPanel").getForm().setValues({
									renote : strTagStrippedText
								});
					}
				},
				failure : function(response) {
					addMsg("[錯誤]\tajax failure");
				},
				params : {
					APP_ID : appId,
					ACTION : "SAVE_ORDER_INFO",
					RETURN_TYPE : "JSON",
					MODEL_NAME : "BidManager",
					WEB_SITE_ID : "SWD-2009-0001",
					ORDER_ID : Ext.getCmp("itemPanel").getForm().getValues()['id'],
					UPDATE_INFO : Ext.encode(updateInfo)
				}
			});
}

/**
 * 更新聯絡資料
 * 
 * @param {}
 *            grid
 * @param {}
 *            rowIndex
 * @param {}
 *            e
 */
Mogan.orderTrace.updateBidItemData = function(grid, rowIndex, e) {
	var r = grid.getStore().getAt(rowIndex);
	Ext.Ajax.request({
		url : 'AjaxPortal',
		callback : function(response) {
			// 更新是否有新訊息
			var json = parserJSON(response.responseText);
			if (json['responseResult'] == "failure") {
				Ext.Msg.alert("錯誤", json['responseMsg']);
			} else {
				var r = itemListStore
						.getAt(Ext
								.getCmp("editorGridItemList")
								.getStore()
								.find(
										'id',
										json['responseData'][0]['ITEM_DATA']['Datas'][0]['id']));

				if (!Ext
						.isEmpty(json['responseData'][0]['CONTACT_MSG']['Datas'][0])) {
					if (json['responseData'][0]['CONTACT_MSG']['Datas'][0]['is_read_count'] > 0) {
						r.set("new_msg", "★");
					}
				}
			}
		},
		failure : function(response) {
			Ext.Msg.alert("錯誤", "更新聯絡資料錯誤，請向程式開發者詢問");
		},
		params : {
			APP_ID : appId,
			ACTION : "UPDATE_ITEM_CONTACT_DATA",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "BidManager",
			WEB_SITE_ID : "SWD-2009-0001",
			ITEM_ORDER_ID : r.get("item_order_id")
		}
	});
}

/**
 * BM2
 * 
 * @param {}
 *            response
 */
Mogan.orderTrace.refreshContactData = function() {

	var itemOrderIds = new Array();
	// alert(Ext.getCmp('comboOrderCase').getValue());
	if (Ext.getCmp('comboOrderCase').getValue() == '') {
		Mogan.orderTrace.orderItemListStore.each(function(r) {
					itemOrderIds.push(r.data['item_order_id']);
				});
	} else {
		itemOrderIds.push(Ext.getCmp('comboOrderCase').getValue());
	}
	// alert(itemOrderIds);

	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
					Ext.Msg.hide();
					Ext.getCmp('DetilPanel').setDisabled(false);
				},
				success : Mogan.orderTrace.showOrderData,
				failure : function(response) {
					Ext.Msg.alert("錯誤", "請向程式開發者詢問");
				},
				params : {
					APP_ID : appId,
					ACTION : "REFRESH_CONTACT_DATA",
					RETURN_TYPE : "JSON",
					MODEL_NAME : "BM2",
					ITEM_ORDER_IDS : Ext.encode(itemOrderIds)
				}
			});
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
		Ext.Msg.alert("錯誤", "請向程式開發者詢問<br />"+json['responseMsg']);
	} else {

		// 訂單資料
		Ext.getCmp('labelOrderMemberMobile')
				.setText(json['responseData'][0]['Datas']['tel']);
		Ext.getCmp('labelOrderMemberEMail')
				.setText(json['responseData'][0]['Datas']['email']);
		Ext.getCmp('labelOrderItemCount')
				.setText(json['responseData'][0]['Datas']['items_count']);

		var totalPirce = 0.0;
		totalPirce += (json['responseData'][0]['Datas']['item_total_price'] - 0);
		totalPirce += (json['responseData'][0]['Datas']['cost_3'] - 0);
		totalPirce += (json['responseData'][0]['Datas']['cost_4'] - 0);

		Ext.getCmp('textfieldOrderRemitOut').setText(
				totalPirce + '<br /> ('
						+ json['responseData'][0]['Datas']['item_total_price']
						+ '+' + json['responseData'][0]['Datas']['cost_3']
						+ '+' + json['responseData'][0]['Datas']['cost_4']
						+ ')', false);
						
		Ext.getCmp('labelOrderNote').setText(
				json['responseData'][0]['Datas']['alert_group'], false);

		Ext.getCmp('orderDataPanel').getForm()
				.setValues(json['responseData'][0]['Datas']);

		if (Ext.isEmpty(json['responseData'][0]['Datas']['ship_type'])) {
			Ext.getCmp('radiogroupOrderShipType').setValue('rb_ship_type_0',
					false);
			Ext.getCmp('radiogroupOrderShipType').setValue('rb_ship_type_1',
					false);
		} else {
			switch (Number(json['responseData'][0]['Datas']['ship_type'])) {
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

		if (Ext.isEmpty(Ext.getCmp('comboOrderPayType').getValue())) {
			Ext.getCmp('comboOrderPayType').fireEvent('select',
					Ext.getCmp('comboOrderPayType'), null);
		} else {
			Ext.getCmp('comboOrderPayType').fireEvent(
					'select',
					Ext.getCmp('comboOrderPayType'),
					Mogan.orderTrace.payTypeStore
							.getAt(Mogan.orderTrace.payTypeStore.find(
									'list_key', Ext.getCmp('comboOrderPayType')
											.getValue())));
		}
		
		Ext.getCmp('textfieldOrderServiceCost').setValue(json['responseData'][0]['Datas']['cost_8']);
		//Ext.getCmp('textfieldOrderIShipCost').setValue(json['responseData'][0]['Datas']['cost_9']);
		
		orderItemListJSONData['root'] = json['responseData'][0]['ItemList'];
		Mogan.orderTrace.orderItemListStore.load();
	}
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
			? r.data['seller_account']
			: r.data['seller_account'] + " - " + r.data['seller_name']);// 賣家名稱
	Ext.getCmp('labelOrderWebSite').setText(r.data['website_name']);// 網站名稱
	
	if (r.data['order_status']=='3-01' || r.data['order_status']=='3-02'){
		Ext.getCmp("btnOrderSaveCost").setDisabled(false);
		Ext.getCmp("btnOrderSubmitCost").setDisabled(false);	
	}else{
		Ext.getCmp("btnOrderSaveCost").setDisabled(true);
		Ext.getCmp("btnOrderSubmitCost").setDisabled(true);
	}

	
	// TODO 金流備忘
	// TODO 物流備忘
	// TODO 商品備忘
	// TODO 連絡備忘
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
					Ext.Msg.hide();
					Ext.getCmp('DetilPanel').setDisabled(false);
				},
				success : Mogan.orderTrace.showOrderData,
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
	Mogan.orderTrace.fixFilterStatus();
	loadBidItemsParams.STATUS_CONDITION = Ext
			.encode(Mogan.orderTrace.filterStatus);
	loadBidItemsParams.CONDITION_KEY = Ext.encode({
				SEARCH_KEY : Ext.getCmp('comboSearchKey').getValue(),
				ACCOUNT_ID : Ext.getCmp('comboAccount').getValue(),
				ACCOUNT : Ext.getCmp('comboAccount').getRawValue(),
				DEFAULT_VALUE : '-'
			});
	store.baseParams = loadBidItemsParams;
	if (Mogan.orderTrace.filterStatus.length == 0) {
		Ext.MessageBox.alert("請重新設定訂單狀態", "訂單狀態未選擇.<br />請重新設定訂單狀態   ");
		return false;
	}
	return true;
}

/**
 * 發送訊息
 * 
 * @param {}
 *            value
 */
Mogan.orderTrace.sendMsg = function() {

	var textareaMsgContent = Ext.getCmp("textareaMsgContent");// 留言內容
	var textfieldMsgTitle = Ext.getCmp("textfieldMsgTitle");// MAIL TITLE
	var comboMsgTitle = Ext.getCmp("comboMsgTitle");// 標準格式 TITLE
	var comboMsgCategory = Ext.getCmp("comboMsgCategory");// 留言方式
	var hiddenItemOrderId = Ext.getCmp("hiddenItemOrderId");

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
						Ext.Msg.confirm("請確認", "訊息已發出，是否將清空已發出的訊息", function(
										btn, text) {
									if (btn == 'yes') {
										textareaMsgContent.setValue("");
									}
								});
					}
				},
				failure : function(response) {
					Ext.Msg.alert("錯誤", "請向程式開發者詢問");
				},
				params : {
					APP_ID : appId,
					ACTION : "SEND_MESSAGE",
					RETURN_TYPE : "JSON",
					MODEL_NAME : "BidManager",
					ITEM_ORDER_ID : hiddenItemOrderId.getValue(),
					WEB_SITE_ID : "SWD-2009-0001",
					MSG : textareaMsgContent.getValue(),
					SEND_METHOD : comboMsgCategory.getValue(),
					SUBJECT_B : textfieldMsgTitle.getValue(),
					SUBJECT_A : comboMsgTitle.getValue()
				}
			});
}

/**
 * 將訊息設定為已讀
 * 
 * @param {}
 *            value
 */
Mogan.orderTrace.readMsg = function(value) {
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
					MODEL_NAME : "BidManager",
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
		var m = trnsListStore.data.items.slice(0);
		Ext.each(m, function(item) {
					var re = new RegExp(item.data['trnsCode'], "g");
					var orderData = itemListStore.getAt(Ext
							.getCmp("editorGridItemList").getStore().find(
									'id',
									Ext.getCmp('itemPanel').getForm()
											.getValues()['id']));
					msg = msg
							.replace(re, orderData.data[item.data['trnsData']]);
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
					MODEL_NAME : "BidManager",
					TEMPLATE_NAME : templetName
				}
			});
}

/**
 * 修正留言tilte
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
	Ext.getCmp("labelMsgItemId").setText("商品ID: " + record.get("item_id"));
	Ext.getCmp("labelMsgItemName").setText("商品名稱: " + record.get("item"));
	// Ext.getCmp("hiddenMsgItemId").setValue(record.get("item_id"));
	Ext.getCmp("hiddenItemOrderId").setValue(record.get("item_order_id"));

	if (record.get("contact_type").indexOf("@") > 0) {
		Ext.getCmp("comboMsgCategory").setValue('1');
	} else {
		Ext.getCmp("comboMsgCategory").setValue('0');
	}
	Mogan.orderTrace.fixComboMsgTitle(Ext.getCmp("comboMsgCategory"));
	Ext.getCmp("textareaMsgContent").setValue('');
	// Ext.getCmp("hiddenBidAccount").setValue(record.get("jyahooid"));
}

/**
 * 改變範本使用模式， 分為套用及編輯
 */
Mogan.orderTrace.changeTemplateMode = function() {
	var modeBtn = Ext.getCmp('msgChangeTemplateMode');
	if (Mogan.orderTrace.templateSatus == 'LOAD') {
		// 進入編輯模式
		Ext.Msg.alert('切換', '進入範本編輯模式');
		Mogan.orderTrace.templateSatus = 'EDIT';
		Ext.getCmp('msgSaveBtn').setDisabled(false);
		Ext.getCmp('msgSaveAsBtn').setDisabled(false);
		Ext.getCmp('msgDelBtn').setDisabled(false);
		Ext.getCmp('msgSendBtn').setDisabled(true);
		modeBtn.setText('範本套用模式');

	} else {
		// 進入套用模式
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
							MODEL_NAME : "BidManager",
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
							MODEL_NAME : "BidManager",
							TEMPLATE_TEXT : templateText,
							TEMPLATE_NAME : templateName
						}
					});
		}
	}
}

/**
 * 商品列表右鍵快速選單
 * BM2
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
	Ext.getCmp('itemListMenuTransOrder').pValue = {
		tideId : r.data['tide_id'],
		sellerId : r.data['seller_id'],
		memberId : r.data['member_id'],
		itemOrderId : r.data['item_order_id']
	};
	Mogan.orderTrace.itemListMenu.showAt(e.getXY());
}

/**
 * 取回可移動訂單 show
 * BM2
 */
Mogan.orderTrace.getMoveableTideList = function() {
	var menuItem = Ext.getCmp('itemListMenuTransOrder');
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
 * 設定可移動的訂單範圍
 * BM2
 * @param {}
 *            response
 */
Mogan.orderTrace.setMoveableTideList = function(response) {
	var json = parserJSON(response.responseText);
	if (json['responseResult'] == "failure") {
		Ext.Msg.alert("錯誤", json['responseMsg']);
	} else {
		var transOderMenu = Ext.menu.MenuMgr.get("transOderMenu");
		transOderMenu.removeAll();
		for (var i = 0; i < json['responseData'].length; i++) {

			var item = new Ext.menu.Item({
						text : json['responseData'][i]['tide_id'] + ' ('
								+ json['responseData'][i]['items_count'] + ')',
						handler : Mogan.orderTrace.moveItem2Order,
						pValue : json['responseData'][i]['tide_id']
					});
			transOderMenu.addItem(item);
		}
		if (transOderMenu.items.length == 0) {
			Ext.getCmp('itemListMenuTransOrder').setDisabled(true);
			// Ext.getCmp('itemListMenuNewOrder').setDisabled(true);
		} else {
			Ext.getCmp('itemListMenuTransOrder').setDisabled(false);
			// Ext.getCmp('itemListMenuNewOrder').setDisabled(false);
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
			Mogan.orderTrace.itemListStore.load();
		},
		success : function() {
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
			TO_TIDE_ID : menuItem.pValue
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
			Mogan.orderTrace.itemListStore.load();
		},
		success : function() {
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

	var store = Ext.getCmp('comboSearchKey').getStore();
	var keyIndex = store.find('value', item.pValue['key']);
	if (keyIndex >= 0) {
	} else {
		var p = new store.recordType(item.pValue);
		store.add(p);
	}
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
 * 名稱對應表專用儲存對話框
 */
Mogan.orderTrace.saveTrnsList = function() {

	var m = trnsListStore.data.items.slice(0);
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
					MODEL_NAME : "BidManager",
					TRNS_CODE_LIST : Ext.encode(jsonArray)

				}
			});
}

/**
 * 確認訂單費用 BM2
 */
Mogan.orderTrace.submitOrderCostMoney = function() {

	var validateMsg="";
	// 先檢查資料是否正確及完整
	if (!Ext.getCmp('radiogroupOrderShipType').validate()){
		validateMsg+="費用是否已結清 - 未填.<br />";
	}
	if (!Ext.getCmp('comboOrderPayType').validate()){
		validateMsg+="費用支付方式 - 未填.<br />";
	}
	if (!Ext.getCmp('textfieldOrderServiceCost').validate()){
		validateMsg+="手續費 - 未填或格式錯誤 <br />";
	}
	if (!Ext.getCmp('textfieldOrderRemitCost').validate()){
		validateMsg+="匯款費 - 未填或格式錯誤 <br />";
	}
	if (!Ext.getCmp('textfieldOrderRemitCost').validate()){
		validateMsg+="其他費用 - 未填或格式錯誤 <br />";
	}
	if (!Ext.getCmp('textfieldOrderTaxCost').validate()){
		validateMsg+="稅金 - 未填或格式錯誤 <br />";
	}
		if(!Ext.getCmp('textfieldOrderOtherCost').getForm().isValid()){
		Ext.Msg.alert("請檢查資料","請檢查下列欄位<br />"+validateMsg);
		return ;
	}
	
	var orderData = Ext.getCmp('orderDataPanel').getForm().getValues();
	var totalPirce = 0;
	totalPirce += (orderData['item_total_price'] - 0);
	totalPirce += (orderData['cost_3'] - 0);
	totalPirce += (orderData['cost_4'] - 0);
	

	var computeStr = "";
	computeStr = "請確認下列資料是否正確<br />";
	computeStr += "===================<br />";
	computeStr += "付款方式：" + Ext.getCmp('comboOrderPayType').getRawValue()
			+ "<br />";
	computeStr += "支付總額：" + totalPirce + "<br />";
	computeStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
			+ orderData['item_total_price'] + " (商品總價)<br />";
	computeStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
			+ orderData['cost_3'] + " (稅金)<br />";
	computeStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
			+ orderData['cost_4'] + " (當地運費)<br />";
	computeStr += "";
	
	
	Ext.MessageBox.confirm('請確認訂單支出費用', computeStr, function(btn, text) {

		if (btn != 'yes') {
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
					//Mogan.orderTrace.showOrderData(response);
					Mogan.orderTrace.itemListStore.each(function(record) {
								if (record['data']['tide_id'] == orderData['tide_id']) {
									record.set("order_status", "3-03");
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

// ////////////////////////////
// 事件區 EVENT //
// ///////////////////////////
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

	if (record['data']['list_key'] == 'RL-802') {
		Mogan.orderTrace.FixRadiogroupOrderShipType();
		radiogroupOrderShipType.setValue('rb_ship_type_0', true);
		Ext.getCmp('rb_ship_type_1').setDisabled(true);
		Ext.getCmp('textfieldOrderRemitCost').setValue("0");
	} else {
		Ext.getCmp('rb_ship_type_1').setDisabled(false);
	}
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
 * 修正 OrderForm按鈕
 */
Mogan.orderTrace.rendererOrderForm = function(value) {
	var html = "";
	if (value == "1") {
		html = "<img src='./resources/mogan/images/form_edit.png' />";
	} else {
		html = "<img src='./resources/mogan/images/form_edit_pale.png' />";
	}
	return html;
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
 * 修正網站顯示ICON
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
 * 修正訂單狀態
 * 
 * @param {}
 *            value
 * @return {String}
 */
Mogan.orderTrace.rendererFixOrderStatus = function(value) {
	return Mogan.orderTrace.statusNameMap[value];
};
