Ext.onReady(function() {
			var bodyPanel = new Ext.Panel({
						buttons : [{
									text : 'text'
								}]
					});

			var bodyPanel = new Ext.Panel({
						title : 'Web Site Manager',
						height : 500,
						layout : 'border',
						items : [{
									region : 'north',
									frame : true,
									anchor : '100%',
									layout : 'hbox',
									defaults : {
										margins : '0 5 0 0'
									},
									height : 60,
									items : [new Ext.Button({
														scale : 'large',
														text : 'Save'
													}), new Ext.Button({
														scale : 'large',
														text : 'New'
													}), new Ext.Button({
														scale : 'large',
														text : 'Delete'
													}), new Ext.Button({
														scale : 'large',
														text : 'Reload',
														handler:Mogan.admin.reloadModels
													})]
								}, {
									region : 'center',
									items : Mogan.admin.createModelGrid(),
									layout : 'fit'
								}, {
									region : 'south',
									frame : true,
									height:15,
									items : [new Ext.form.Label({
												text : 'init.....',
												id : 'statusMsg'
												
											})]
								}]
					});
			bodyPanel.render(document.body);
			Mogan.admin.loadModelData();
		});


