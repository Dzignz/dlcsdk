Ext.onReady(function() {
			var viewport = new Ext.Viewport({
						layout : 'border',
						items : [{
							region : 'center',
							items : Mogan.yamato.createPanel(),
							split : true,
							collapsible : true,
							layout : 'fit'

						}]
					});
		});