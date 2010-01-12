Ext.onReady(function() {
			var headPanel = Mogan.keyword.createSetupPanel();
			var bodyPanel = new Ext.Panel({
						title : 'Webwalk Service',
						height : 580,
						layout : 'border',
						items : [{
									region : 'north',
									leyout:'fit',
									height:190,
									items : headPanel
								}, {
									region : 'center',
									items:Mogan.keyword.createMsgPanel(),
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
		});