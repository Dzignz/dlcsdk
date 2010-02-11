Ext.namespace("Mogan.transactionTrace");

var appId = "26b782eb04abbd54efba0dcf854b158d";
var statusCondition = "0123";

var loadBidItemsParams = {
	APP_ID : appId,
	ACTION : "LOAD_BID_ITEMS",
	MODEL_NAME : "BidManager",
	RETURN_TYPE : "JSON",
	STATUS_CONDITION : statusCondition,
	SEARCH_KEY:'',
	CONDITION_KEY : '',
	START_INDEX : 0,
	PAGE_SIZE : 50,
	ORDER_BY : 'end_date',
	DIR : 'DESC'
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
Mogan.transactionTrace.createLoadBidItemsParams = function(store, startIndex,
		pageSize, orderBy, condition, callbackFunction) {
	
	var loadParams = {
		params : {
			APP_ID : appId,
			ACTION : "LOAD_BID_ITEMS",
			MODEL_NAME : "BidManager",
			RETURN_TYPE : "JSON",
//			START_INDEX : startIndex,
//			PAGE_SIZE : pageSize,
			ORDER_BY : orderBy,
			STATUS_CONDITION : statusCondition,
			CONDITION_KEY : Ext.encode({
						SEARCH_KEY : Ext.getCmp('comboSearchKey').getValue(),
						ACCOUNT_ID : Ext.getCmp('comboAccount').getValue(),
						ACCOUNT : Ext.getCmp('comboAccount').getRawValue()
					})
		},
		add : false,
		scope : store,
		callback : callbackFunction
	};
//	alert('createLoadBidItemsParams');
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
Mogan.transactionTrace.clickItem = function(grid, rowIndex, e) {

	var r = grid.getStore().getAt(rowIndex);
	Mogan.transactionTrace.loadBidItemData(grid, rowIndex, e);// 將資料顯示基本資料Tab
	Mogan.transactionTrace.updateBidItemData(grid, rowIndex, e);// 更新商品資料
	Mogan.transactionTrace.setSenderData(r);// 設定訊息發送Tab
	Mogan.transactionTrace.loadItemOrderForm();// 設定是否顯示order form

	// Mogan.transactionTrace.getItemOrderForm(r);// 設定訊息發送Tab
	// Ext.get(itemFrame).src="http://www.mogan.com.tw/adminv2/bidding_config_handle.php?rid=38282";

}

/**
 * 讀取商品 order form
 * 
 * @param {}
 *            record
 */
Mogan.transactionTrace.loadItemOrderForm = function() {
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
Mogan.transactionTrace.openItemOrderForm = function(record) {
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
Mogan.transactionTrace.copyConactDataToRenote = function(index) {

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
Mogan.transactionTrace.updateBidItemData = function(grid, rowIndex, e) {
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
 * 
 * @param {}
 *            response
 */
Mogan.transactionTrace.showBidItemData = function(response) {
	var i = 0;
	var json = parserJSON(response.responseText);
	if (json['responseResult'] == "failure") {
		Ext.Msg.alert("錯誤", json['responseMsg']);
	} else {
		var msgRecordStore = Ext.getCmp("msgRecordPanel").getStore();
		msgRecordStore.loadData(json);
		var r = itemListStore
				.getAt(Ext.getCmp("editorGridItemList").getStore().find('id',
						json['responseData'][0]['ITEM_DATA']['Datas'][0]['id']));

		if (!Ext.isEmpty(json['responseData'][0]['CONTACT_MSG']['Datas'][0])) {
			if (json['responseData'][0]['CONTACT_MSG']['Datas'][0]['is_read_count'] > 0) {
				r.set("new_msg", "★");
			}
		}
		if (!Ext.isEmpty(json['responseData'][0]['ITEM_DATA'])) {
			r
					.set(
							"contact_type",
							json['responseData'][0]['ITEM_DATA']['Datas'][0]['contact_type']);

			r.commit();
		}
		Ext.Msg.hide();
	}
}

/**
 * 讀取商品的聯絡記錄，同時發出請求確認是否有新的訊息
 * 
 * @param {}
 *            grid
 * @param {}
 *            rowIndex
 * @param {}
 *            e
 */
Mogan.transactionTrace.loadBidItemData = function(grid, rowIndex, e) {
	var r = grid.getStore().getAt(rowIndex);
	// 先讀取資料庫的資料，再讀取網頁上的資料如有更新就會提示使用者
	// DetilPanel
	Ext.getCmp('DetilPanel').setDisabled(true);
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
					Ext.Msg.hide();
					Ext.getCmp('DetilPanel').setDisabled(false);
				},
				success : Mogan.transactionTrace.showBidItemData,
				failure : function(response) {
					Ext.Msg.alert("錯誤", "請向程式開發者詢問");
				},
				params : {
					APP_ID : appId,
					ACTION : "LOAD_TRANSACTION_DATA",
					RETURN_TYPE : "JSON",
					MODEL_NAME : "BidManager",
					WEB_SITE_ID : "SWD-2009-0001",
					BID_ACCOUNT : r.get("agent_account"),
					ITEM_ID : r.get("item_id"),
					ITEM_ORDER_ID : r.get("item_order_id"),
					SELLER_ID : r.get("sell_name"),
					WON_ID : r.get("id"),
					CONTACT_TYPE : r.get("contact_type"),
					MEMBER_ACCOUNT : r.get("user_name"),
					DATA_SOURCE : 'DB'
				}
			});
}


Mogan.transactionTrace.fixLoadBidItemsParams = function() {
	if (!Ext.isEmpty(Ext.getCmp("btnGpStstusKey"))) {
		// 可以取得btnGpStstusKey時才去判斷btnGpStstusKey的內容
		statusCondition = "";
		var statusKeyCollection = Ext.getCmp("btnGpStstusKey").items;
		for (var i = 0; i < statusKeyCollection.getCount(); i++) {
			if (statusKeyCollection.get(i).pressed) {
				statusCondition += i + "";
			}
		}
		loadBidItemsParams.STATUS_CONDITION = statusCondition;
			loadBidItemsParams.CONDITION_KEY = Ext.encode({
						SEARCH_KEY : Ext.getCmp('comboSearchKey').getValue(),
						ACCOUNT_ID : Ext.getCmp('comboAccount').getValue(),
						ACCOUNT : Ext.getCmp('comboAccount').getRawValue()
					})
	}
}


Mogan.transactionTrace.getloadBidItemsURL = function(store, options) {
	Mogan.transactionTrace.fixLoadBidItemsParams();
	store.proxy.setUrl('AjaxPortal?' + Ext.urlEncode(loadBidItemsParams));
//	alert(store.proxy.getUrl());
}


/**
 * 發送訊息
 * 
 * @param {}
 *            value
 */
Mogan.transactionTrace.sendMsg = function() {

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
Mogan.transactionTrace.readMsg = function(value) {
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
Mogan.transactionTrace.fixTextareaMsgContent = function(comboBox) {
	var msgContact = comboBox.getValue();
	var msgTemplate = comboBox.getStore().getAt(msgContact).get('text');

	Ext.Msg.confirm("請確認", "是否將訊息內容更換為["+comboBox.getStore().getAt(msgContact).get('title')+"]內容", function(btn, text) {
				if (btn == 'yes') {
					msgTemplate=msgTemplate.replace(/\$MOGAN_ITEM_ORDER_ID/g,"9999");
					msgTemplate=msgTemplate.replace(/\$MOGAN_MONEY/g,"88888");
					Ext.getCmp("textareaMsgContent").setValue(msgTemplate);
				}
			});
}

/**
 * 修正留言tilte
 * 
 * @param {}
 *            comboBox
 */
Mogan.transactionTrace.fixComboMsgTitle = function(comboBox) {
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

/**
 * 設定發送參數
 * 
 * @param {}
 *            record
 */
Mogan.transactionTrace.setSenderData = function(record) {
	Ext.getCmp("labelMsgItemId").setText("商品ID: " + record.get("item_id"));
	Ext.getCmp("labelMsgItemName").setText("商品名稱: " + record.get("item"));
	// Ext.getCmp("hiddenMsgItemId").setValue(record.get("item_id"));
	Ext.getCmp("hiddenItemOrderId").setValue(record.get("item_order_id"));

	if (record.get("contact_type").indexOf("@") > 0) {
		Ext.getCmp("comboMsgCategory").setValue('1');
	} else {
		Ext.getCmp("comboMsgCategory").setValue('0');
	}
	Mogan.transactionTrace.fixComboMsgTitle(Ext.getCmp("comboMsgCategory"));
	Ext.getCmp("textareaMsgContent").setValue('');
	// Ext.getCmp("hiddenBidAccount").setValue(record.get("jyahooid"));
}

/**
 * 修正 OrderForm按鈕
 */
Mogan.transactionTrace.rendererOrderForm = function(value) {
	var html = "";
	if (value == "1") {
		html = "<img src='./resources/mogan/images/form_edit.png' />";
	} else {
		html = "<img src='./resources/mogan/images/form_edit_pale.png' />";
	}
	return html;
}

/**
 * 修正讀取按鈕
 * 
 * @param {}
 *            value
 */
Mogan.transactionTrace.rendererReadMsg = function(value, metaData, record,
		rowIndex, colIndex, store) {
	var btnHtml = "<input type='button' value='讀取 " + value + "' />";

	if (value == 0) {
		btnHtml = "<input type='button' value='讀取' onclick=\"Mogan.transactionTrace.readMsg('"
				+ record['data']['contact_id']
				+ "')\" />"
				+ record['data']['msg_category'];
	} else {
		btnHtml = "<input type='button' value='已讀取' disabled=true />"
				+ record['data']['msg_category'];
	}
	return btnHtml;
}

/**
 * 修正讀取時間，如果沒有時間，顯示為未讀取
 * 
 * @param {}
 *            value
 * @return {String}
 */
Mogan.transactionTrace.rendererFixDate = function(value) {
	if (value == null) {
		return "未讀取";
	} else {
		return value;
	}
}