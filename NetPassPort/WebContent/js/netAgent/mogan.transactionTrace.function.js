Ext.namespace("Mogan.transactionTrace");

var appId = "26b782eb04abbd54efba0dcf854b158d";
var statusCondition = "0123";

var loadBidItemsParams = {
	APP_ID : appId,
	ACTION : "LOAD_BID_ITEMS",
	MODEL_NAME : "BidManager",
	RETURN_TYPE : "JSON",
	STATUS_CONDITION : statusCondition,
	CONDITION : '',
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
			START_INDEX : startIndex,
			PAGE_SIZE : pageSize,
			ORDER_BY : orderBy,
			STATUS_CONDITION : statusCondition,
			CONDITION : ''
		},
		add : false,
		scope : store,
		callback : callbackFunction
	};
	var conditionObj = {
		SEARCH_KEY : Ext.getCmp('comboSearchKey').getValue(),
		ACCOUNT_ID : Ext.getCmp('comboAccount').getValue(),
		ACCOUNT : Ext.getCmp('comboAccount').getRawValue()
	};
	loadBidItemsParams.CONDITION = Ext.encode(conditionObj);
	return loadParams;
}

/**
 * 初始化資料
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
	Mogan.transactionTrace.loadBidItemData(grid, rowIndex, e);
	Mogan.transactionTrace.setSenderData(r);
	// Ext.get(itemFrame).src="http://www.mogan.com.tw/adminv2/bidding_config_handle.php?rid=38282";

}

Mogan.transactionTrace.searchData = function(index) {

}

/**
 * 將指定內容複製到賣方資訊&內容中(Renote)
 * 
 * @param {}
 *            index
 */
Mogan.transactionTrace.copyConactDataToRenote = function(index) {

	/*
	 * var strInputCode =
	 * Ext.getCmp("msgRecordPanel").store.getAt(index).data.msg_contact;
	 * strInputCode=strInputCode.replace(/<br>/g,'\r\n'); strInputCode =
	 * strInputCode.replace(/&(lt|gt);/g, function(strMatch, p1) { return (p1 ==
	 * "lt") ? "<" : ">"; });
	 */
	// var strTagStrippedText = strInputCode.replace(/<\/?[^>]+(>|$)/g, "");
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
	/*var msg = Ext.Msg.wait("請稍待", "資料讀取中", {
				text : 'wait...',
				animate : true
			});*/
	// 先讀取資料庫的資料，再讀取網頁上的資料如有更新就會提示使用者
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
					Ext.Msg.hide();
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
					BID_ACCOUNT : r.get("jyahooid"),
					ITEM_ID : r.get("item_id"),
					ITEM_ORDER_ID : r.get("no"),
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
	}
}

Mogan.transactionTrace.getloadBidItemsURL = function(store, options) {
	Mogan.transactionTrace.fixLoadBidItemsParams();
	store.proxy.setUrl('AjaxPortal?' + Ext.urlEncode(loadBidItemsParams));
}

Mogan.transactionTrace.sendMsg = function(value) {

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

	Ext.getCmp("hiddenMsgItemId").setValue(record.get("item_id"));
	Ext.getCmp("hiddenBidAccount").setValue(record.get("jyahooid"));
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
				+ record['data']['contact_id'] + "')\" />"+record['data']['msg_category'];
	} else {
		btnHtml = "<input type='button' value='已讀取' disabled=true />"+record['data']['msg_category'];
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