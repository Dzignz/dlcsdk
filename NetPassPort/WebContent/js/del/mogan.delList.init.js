Ext.namespace("Mogan.delList");

Mogan.orderTrace.orderItemListStore = new Ext.data.Store({
			reader : new Ext.data.JsonReader({
						root : 'root'
					}, ['item_order_id', 'item_id', 'item_name', 'buy_price',
							'buy_unit', 'time_at_03', 'buyer_account',
							'item_id_name', 'seller_attribute_1',
							'o_varchar01', 'msg_status']),
			proxy : new Ext.data.MemoryProxy(orderItemListJSONData)
		});
Mogan.orderTrace.orderItemListStore.load();

Ext.onReady(function() {
			alert('onReady-1');

			var viewport = new Ext.Viewport({

						layout : 'border',
						// layout : 'fit',
						items : [{
							title : '所有訂單列表',
							region : 'center',
							items : Mogan.delList.createCaseListGridPanel()
								// split : true,
								// collapsible : false,
								// height : 300,
								// layout : 'fit'
							}],
						renderTo : Ext.getBody()
					});

			alert('onReady-2');
		})