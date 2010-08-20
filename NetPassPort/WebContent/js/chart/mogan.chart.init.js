Ext.namespace("Mogan.chart");
Ext.onReady(function() {
			var viewport = new Ext.Viewport({
						layout : 'border',
						items : [{
									title : '待處理列表',
									region : 'west',
									html : '待處理列表',
									//items : Mogan.orderTrace.createCaseListGridPanel(),
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
									id : 'centerPanel',
									html : '訂單資料'//,
									//items : Mogan.orderTrace.createDetilPanel()
								}]
					});
		})