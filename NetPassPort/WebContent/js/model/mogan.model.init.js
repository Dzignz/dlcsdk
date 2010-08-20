Ext.onReady(function() {
			var viewport = new Ext.Viewport({
						layout : 'border',
						title : 'Model Service',
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
														handler:Mogan.model.reloadModels
													}), new Ext.Button({
														scale : 'large',
														text : 'ReloadSysParameter',
														handler:Mogan.model.reloadSysParameter
													})]
								}, {
									region : 'center',
									items : Mogan.model.createModelGrid(),
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
			Mogan.model.loadModelData();
		});

