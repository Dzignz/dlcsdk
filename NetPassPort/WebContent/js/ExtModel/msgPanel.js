/**
 * 發送訊息相關設定
 * 
 * @type
 */
msgCategoryData = [
		[
				'0',
				'留言版',
				[['1', '送付先住所、支払い、発送などについて'], ['2', '支払いが完了しました'],
						['3', '商品を受け取りました'], ['4', 'その他']]],
		['1', 'e-mail', []], ['2', '揭示版', [['no', '公開しない'], ['yes', '公開する']]]];

msgCategoryStore = new Ext.data.SimpleStore({// 下拉式選單資料
	fields : ['value', 'text', 'data'],
	data : msgCategoryData
});

SendMsgPanel = Ext.extend(Ext.form.FormPanel, {
			labelWidth : 75,
			defaultType : 'textfield',
			frame : true,
			buttonAlign : 'left',
			initComponent : function() {
				this.items = [
					{
							xtype : 'hidden',
							name : 'ITEM_ORDER_ID',
							value:itemOrderId
						},{
							xtype : 'combo',
							fieldLabel : '類型',
							store : msgCategoryStore,
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
							store : new Ext.data.SimpleStore({// 下拉式選單資料
								fields : ['value', 'text'],
								data : msgCategoryStore.getAt(0).get('data')
							}),
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
						}, {
							id : 'textareaMsgContent',
							xtype : 'textarea',

							name : "MSG",
							height : 200,
							// grow : true,
							// growMax : 200,
							anchor : '90%',
							fieldLabel : '內容'
						}];
				this.buttons = [{
							id : 'msgSendBtn',
							text : '送出',
							handler : sendMsg
						}]
				SendMsgPanel.superclass.initComponent.call(this);
				Ext.getCmp('comboMsgCategory').on('select', function(comboBox) {
					var msgContact = comboBox.getValue();
					var msgTitleData = comboBox.getStore().getAt(msgContact)
							.get('data');

					if (Ext.isEmpty(msgTitleData)) {
						Ext.getCmp("comboMsgTitle").setDisabled(true);
						Ext.getCmp("textfieldMsgTitle").setDisabled(false);
					} else {
						Ext.getCmp("comboMsgTitle").getStore()
								.loadData(msgTitleData);
						Ext.getCmp("comboMsgTitle")
								.setValue(msgTitleData[0][0]);
						Ext.getCmp("textfieldMsgTitle").setDisabled(true);
						Ext.getCmp("comboMsgTitle").setDisabled(false);
					}
				});
			}
		});

sendMsg = function() {
	var textareaMsgContent = Ext.getCmp("textareaMsgContent");// 留言內容
	var textfieldMsgTitle = Ext.getCmp("textfieldMsgTitle");// MAIL TITLE
	var comboMsgTitle = Ext.getCmp("comboMsgTitle");// 標準格式 TITLE
	var comboMsgCategory = Ext.getCmp("comboMsgCategory");// 留言方式
	Ext.getCmp('msgSendBtn').setDisabled(true);
	Ext.Ajax.request({
				url : 'AjaxPortal',
				callback : function() {
					// Ext.Msg.hide();
					Ext.getCmp('msgSendBtn').setDisabled(false);
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

createMsgSenderPanel = function() {

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
						}, {
							xtype : 'compositefield',
							id : 'compsMsgTemplet',
							fieldLabel : '訊息範本',
							items : [{
										xtype : 'combo',
										id : 'comboMsgTemplate',
										fieldLabel : '訊息範本',
										store : Mogan.orderTrace.templateListStore,
										mode : 'local',
										width : 300,
										triggerAction : 'all',
										valueField : 'templateIndex',
										lazyRender : true,
										displayField : 'fileName',
										editable : true
									}, {
										xtype : 'button',
										id : 'msgChangeTemplateMode',
										text : '範本編輯模式',
										handler : function(b, e) {
											Mogan.orderTrace
													.changeTemplateMode();
										}
									}]
						}, {
							xtype : 'buttongroup',
							id : 'compsMsgTempletToolBar',
							fieldLabel : '範本工具列 ',
							items : [{
										id : 'msgSaveBtn',
										text : '儲存',
										disabled : true,
										handler : function() {
											Mogan.orderTrace.saveMsg(0);
										}
									}, {
										id : 'msgSaveAsBtn',
										text : '另儲新範本',
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
									}]
						}, {
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
							id : 'msgSendBtn',
							text : '送出',
							handler : Mogan.orderTrace.sendMsg
						}]
			})
	return simple;
}