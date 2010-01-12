Ext.onReady(function() {
			var bodyPanel = new Ext.Panel({
						title : 'Mail Templet Manage Service',
						height : 580,
						layout : 'border',

						items : [{
							region : 'north',
							leyout : 'fit',
							frame : true,
							html:'測試中...未完成'
								// height:190,
								// items : headPanel
							}, {
							region : 'center',
							frame : true,
							items : Mogan.mail.templetMg.createGridPanel(),
							layout : 'fit'
						}, {
							region : 'south',
							frame : true,
							items : [new Ext.form.Label({
										text : 'init.....',
										id : 'statusMsg'
									})]
						}]
					});
			bodyPanel.render(document.body);
			// dataStore.load(queryParsms);
			dataStore.load(queryParsms);
		});