Ext.onReady(function() {
			var bodyPanel = new Ext.Panel({
						title : 'Webwalk Service',
						height : 800,
						layout : 'border',
						items : [{
									region : 'center',
									items:createHeadPanel()
									//layout : 'fit'
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