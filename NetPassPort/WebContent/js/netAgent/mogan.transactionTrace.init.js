Ext.onReady(function() {
			var viewport = new Ext.Viewport({
						layout : 'border',
						items : [{
							region : 'center',
							items : Mogan.transactionTrace
									.createCaseListGridPanel(),
							split : true,
							collapsible : true,
							layout : 'fit'

						}, {
							height : 300,
							region : 'south',
							split : true,
							collapsible : true,
							layout : 'fit',
							// frame : true,//
							items : Mogan.transactionTrace.createDetilPanel()
						}]
					});
			var el = Ext.get('comboSearchKey');
			var keyNav = new Ext.KeyNav(el, {
						enter : function(e) {
							itemListStore.load();
						}
					});
		});