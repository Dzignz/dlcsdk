Ext.namespace("Mogan.sellItem");

var appId = "26b782eb04abbd54efba0dcf854b158d";
var statusCondition = "";

var loadBidItemsParams = {
	APP_ID : appId,
	ACTION : "LOAD_ITEMS",
	DATA_CLASS : "SELL_ITEM",
	MODEL_NAME : "BidManager",
	RETURN_TYPE : "JSON",
	STATUS_CONDITION : statusCondition,
	CONDITION : '',
	ORDER_BY : 'end_date',
	DIR : 'DESC'
};

/**
 * 讀取商品資訊
 * @param {} itemOrderId
 */
getItemData=function (itemOrderId){
		Ext.MessageBox.show({
				msg : '發問...',
				progressText : 'asking...',
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
				Ext.Msg.alert("訊息",json["responseData"][0]);
			}
		},
		failure : function(response) {
			Ext.Msg.alert("訊息","下架失敗");
		},
		params : {
			APP_ID : appId,
			ACTION : "GET_ITEM_DATA",
			BID_ACCOUNT : "omimiside",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "BidManager",
			WEB_SITE_ID:"SWD-2009-0001",
			ITEM_ID : "n83095138"
		}
	});
}


/**
 * 取得出價清單
 * @param {} itemOrderId
 */
getBidList=function (itemOrderId){
		Ext.MessageBox.show({
				msg : '發問...',
				progressText : 'asking...',
				width : 300,
				wait : true,
				waitConfig : {
					interval : 200
				}
//				icon : 'mail' // custom class in msg-box.html
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
				Ext.Msg.alert("訊息",json["responseData"][0]);
			}
		},
		failure : function(response) {
			Ext.Msg.alert("訊息","下架失敗");
		},
		params : {
			APP_ID : appId,
			ACTION : "GET_BID_LIST",
			BID_ACCOUNT : "omimiside",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "BidManager",
			LIST_TYPE:"LOG_LIST",
			PAGE:"1",
			WEB_SITE_ID:"SWD-2009-0001",
			ITEM_ID : "w46063720"
		}
	});
}

getHighPriceAccount=function (itemOrderId){
		Ext.MessageBox.show({
				msg : '發問...',
				progressText : 'asking...',
				width : 300,
				wait : true,
				waitConfig : {
					interval : 200
				}
//				icon : 'mail' // custom class in msg-box.html
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
				Ext.Msg.alert("訊息",json["responseData"][0].toString());
			}
		},
		failure : function(response) {
			Ext.Msg.alert("訊息","下架失敗");
		},
		params : {
			APP_ID : appId,
			ACTION : "GET_HIGH_PRICE_ACCOUNT",
			BID_ACCOUNT : "omimiside",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "BidManager",
			PAGE:"1",
			ITEM_ID : "w46347951",
			WEB_SITE_ID:"SWD-2009-0001"
			
		}
	});
}

/**
 * 發問
 * @param {} itemOrderId
 */
questItem=function (itemOrderId){
		Ext.MessageBox.show({
				msg : '發問...',
				progressText : 'asking...',
				width : 300,
				wait : true,
				waitConfig : {
					interval : 200
				}
//				icon : 'mail' // custom class in msg-box.html
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
				Ext.Msg.alert("訊息",json["responseData"][0]);
			}
		},
		failure : function(response) {
			Ext.Msg.alert("訊息","下架失敗");
		},
		params : {
			APP_ID : appId,
			ACTION : "QUESTION_ITEM",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "BidManager",
			ITEM_ORDER_ID : itemOrderId
		}
	});
}

/**
 * 下架 
 * @param {} itemOrderId
 */
unpostItem=function (itemOrderId){
		Ext.MessageBox.show({
				msg : '下架...',
				progressText : 'unselling...',
				width : 300,
				wait : true,
				waitConfig : {
					interval : 200
				}
//				icon : 'mail' // custom class in msg-box.html
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
				Ext.Msg.alert("訊息",json["responseData"][0]);
			}
		},
		failure : function(response) {
			Ext.Msg.alert("訊息","下架失敗");
		},
		params : {
			APP_ID : appId,
			ACTION : "UNREPOST_ITEM",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "BidManager",
			ITEM_ORDER_ID : itemOrderId
		}
	});
}

/**
 * 重新上刊 
 * @param {} itemOrderId
 */
repostItem=function (itemOrderId){
		Ext.MessageBox.show({
				msg : '重新上刊中...',
				progressText : 'reposting...',
				width : 300,
				wait : true,
				waitConfig : {
					interval : 200
				}
//				icon : 'mail' // custom class in msg-box.html
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
				Ext.Msg.alert("訊息",json["responseData"][0]);
			}
		},
		failure : function(response) {
			Ext.Msg.alert("訊息","上刊失敗");
		},
		params : {
			APP_ID : appId,
			ACTION : "REPOST_ITEM",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "BidManager",
			ITEM_ORDER_ID : itemOrderId
		}
	});
}

/**
 * 上刊商品
 * @param {} itemOrderId 
 */
postItem=function (itemOrderId){
		Ext.MessageBox.show({
				msg : '上刊中...',
				progressText : 'posting...',
				width : 300,
				wait : true,
				waitConfig : {
					interval : 200
				}
//				icon : 'mail' // custom class in msg-box.html
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
				Ext.Msg.alert("訊息",json["responseData"][0]);
			}
		},
		failure : function(response) {
			Ext.Msg.alert("訊息","上刊失敗");
		},
		params : {
			APP_ID : appId,
			ACTION : "POST_ITEM",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "BidManager",
			ITEM_ORDER_ID : itemOrderId
		}
	});
}

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
createLoadBidItemsParams = function(store, startIndex,
		pageSize, orderBy, condition, callbackFunction) {
	
	var loadParams = {
		params : {
			APP_ID : appId,
			ACTION : "LOAD_ITEMS",
			DATA_CLASS : "SELL_ITEM",
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
	loadBidItemsParams.CONDITION = Ext.getCmp('comboSearchKey').getValue();
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
clickItem = function(grid, rowIndex, e) {
	var r = grid.getStore().getAt(rowIndex);
	loadBidItemData(grid, rowIndex, e);
	setSenderData(r);
	// Ext.get(itemFrame).src="http://www.mogan.com.tw/adminv2/bidding_config_handle.php?rid=38282";

}

searchData = function(index) {
		
}

/**
 * 將指定內容複製到賣方資訊&內容中(Renote)
 * @param {} index
 */
copyConactDataToRenote = function(index) {
	
	/*
	var strInputCode = Ext.getCmp("msgRecordPanel").store.getAt(index).data.msg_contact;
	strInputCode=strInputCode.replace(/<br>/g,'\r\n');
	strInputCode = strInputCode.replace(/&(lt|gt);/g, function(strMatch, p1) {
				return (p1 == "lt") ? "<" : ">";
			});
			*/
	//var strTagStrippedText = strInputCode.replace(/<\/?[^>]+(>|$)/g, "");
	var strTagStrippedText = Ext.getCmp("msgRecordPanel").store.getAt(index).data.msg_contact;
	if (Ext.getCmp("itemPanel").getForm().getValues()['renote'].length>0){
		strTagStrippedText=Ext.getCmp("itemPanel").getForm().getValues()['renote']+'<BR />'+strTagStrippedText;	
	}
	
	
	Ext.MessageBox.show({
				msg : '資料複製中...',
				progressText : 'Saving...',
				width : 300,
				wait : true,
				waitConfig : {
					interval : 200
				}
//				icon : 'mail' // custom class in msg-box.html
			});
	var updateInfo=new Object();
	updateInfo['renote']=strTagStrippedText;
	
	Ext.Ajax.request({
		url : 'AjaxPortal',
		callback : function() {
			Ext.Msg.alert("訊息","儲存完成");
		},
		success : function(response) {
			var json = parserJSON(response.responseText);
			if (json['responseResult'] == "failure") {
				Ext.Msg.alert("錯誤", json['responseMsg']);
			} else {
				Ext.getCmp("itemPanel").getForm().setValues({renote:strTagStrippedText});
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
			WEB_SITE_ID : "1",
			ORDER_ID : Ext.getCmp("itemPanel").getForm().getValues()['id'],
			UPDATE_INFO : Ext.encode(updateInfo)
		}
	});
}

loadBidItemData = function(grid, rowIndex, e) {
	var r = grid.getStore().getAt(rowIndex);
	var msg = Ext.Msg.wait("請稍待", "資料讀取中", {
				text : 'wait...',
				animate : true
			});
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
				var msgRecordStore = Ext.getCmp("msgRecordPanel").getStore();
				msgRecordStore.loadData(json);
				if (!Ext.isEmpty(json['responseData'][0]['ITEM_DATA'])) {
					var r = itemListStore
							.getAt(Ext
									.getCmp("editorGridItemList")
									.getStore()
									.find(
											'id',
											json['responseData'][0]['ITEM_DATA']['Datas'][0]['id']));
					r
							.set(
									"contact_type",
									json['responseData'][0]['ITEM_DATA']['Datas'][0]['contact_type']);
					r.commit();
				}
			}
		},
		failure : function(response) {
			addMsg("[錯誤]\tajax failure");
		},
		params : {
			APP_ID : appId,
			ACTION : "LOAD_TRANSACTION_DATA",
			RETURN_TYPE : "JSON",
			MODEL_NAME : "BidManager",
			WEB_SITE_ID : "1",
			BID_ACCOUNT : r.get("jyahooid"),
			ITEM_ID : r.get("item_id"),
			TRANSACTION_ID : '',
			SELLER_ID : r.get("sell_name"),
			WON_ID : r.get("id"),
			CONTACT_TYPE : r.get("contact_type"),
			MEMBER_ACCOUNT : r.get("user_name")
		}
	});
}

fixLoadBidItemsParams = function() {
	if (!Ext.isEmpty(Ext.getCmp("btnGpPostStstusKey"))) {
		// 可以取得btnGpStstusKey時才去判斷btnGpStstusKey的內容
		statusCondition = "";
		var statusKeyCollection = Ext.getCmp("btnGpPostStstusKey").items;
		for (var i = 0; i < statusKeyCollection.getCount(); i++) {
			if (statusKeyCollection.get(i).pressed) {
				statusCondition += i + "";
			}
		}
		loadBidItemsParams.STATUS_CONDITION = statusCondition;
	}
}

getloadBidItemsURL = function(store, options) {
	fixLoadBidItemsParams();
	store.proxy.setUrl('AjaxPortal?' + Ext.urlEncode(loadBidItemsParams));
}

sendMsg = function(value) {

}

readMsg = function(value) {

}

fixComboMsgTitle = function(comboBox) {
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
setSenderData = function(record) {
	Ext.getCmp("labelMsgItemId").setText("商品ID: " + record.get("item_id"));
	Ext.getCmp("labelMsgItemName").setText("商品名稱: " + record.get("item"));

	Ext.getCmp("hiddenMsgItemId").setValue(record.get("item_id"));
	Ext.getCmp("hiddenBidAccount").setValue(record.get("jyahooid"));
}

loadBidItemsData=function (){
	
}

/**
 * 修正讀取按鈕
 * 
 * @param {}
 *            value
 */
rendererReadMsg = function(value) {
	var btnHtml = "<input type='button' value='讀取' />";
	if (value == 'true') {
		var btnHtml = "<input type='button' value='讀取' disabled=true onclick=readMsg/>";
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
rendererFixDate = function(value) {
	if (value == null) {
		return "未讀取";
	} else {
		return value;
	}
}