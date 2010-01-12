Ext.onReady(function() {
	
			var accountForm = new Mogan.netAgent.bid.createAccountFrom();
			var bidForm = new Mogan.netAgent.bid.createBidFrom();
			var msgArea = new Mogan.netAgent.bid.createMsgArea();
			var statusBar = new Mogan.netAgent.bid.createStatusBar();
			var tempPanel = new Ext.Panel({
						renderTo : 'mainPanel',
						layout : 'border',
						height : 600,
						// width : 500,
						items : [{
									region : 'north',
									height : 230,
									layout : 'column',
									items : [{
												columnWidth : .5,
												layout : 'fit',
												items : [accountForm]
											}, {
												columnWidth : .5,
												layout : 'fit',
												items : [bidForm]
											}]
								}, {
									region : 'center',
									layout : 'fit',
									// height:300,
									buttons : [{
												text : '清除訊息',
												handler : clearMsg
											}],
									items : [msgArea]
								}, statusBar]
					});
		});