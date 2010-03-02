Ext.onReady(function() {

	var viewport = new Ext.Viewport({
				layout : 'border',
				items : [{
							region : 'center',
							items : Mogan.migration.createTabPanel(),
							split : true,
							collapsible : true,
							layout : 'fit'
						}]
			});

	});