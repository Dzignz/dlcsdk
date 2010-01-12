Ext.onReady(function() {
			var bodyPanel = new Ext.Panel({
						title : 'Webwalk Service',
						height : 800,
						layout : 'border',
						items : [{
									region : 'north',
									leyout : 'fit',
									height : 45,
									frame : true,
									layout : 'hbox',
									defaults : {
										margins : '0 5 0 0'
									},
									items : createHeadPanel()
								}, {
									region : 'center',
									items: createCenterPanel(),
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