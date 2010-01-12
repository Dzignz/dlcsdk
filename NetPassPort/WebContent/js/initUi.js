Ext.onReady(function() {
			var viewport = new Ext.Viewport({
						layout : 'border',
						items : [{
									region : 'north',
									html : '我是title',
									height : 40
								}, {
									region : 'west',
									split : true,
									html : 'menu',
									width : 150,
									minSize : 30,
									maxSize : 150,
									collapsible : true,
									title : 'Menu',
									layout : 'accordion',
									layoutConfig : {
										titleCollapse : true,
										animate : true,
										activeOnTop : false
									},
									form : true,
									items : [{
												title : '代標(拍)管理',
												items : [Mogan.creatBidMenu()]
											}, {
												title : '廣告信管理',
												items : [Mogan.creatMailMenu()]
											}, {
												title : '關鍵字查尋',
												items : [Mogan.creatKeywordMenu()]
											}, {
												title : '帳號申請',
												items : [Mogan
														.creatAccountMenu()]
											}, {
												title : '拍賣追蹤',
												items : [Mogan.creatTraceMenu()]
											}, {
												title : '資料管理',
												html : 'data status'
											}, {
												title : '管理報表',
												html : 'data status'
											}, {
												title : '管理者',
												items : [Mogan.creatSysMenu()]
											}]
								}, {
									region : 'center',
									contentEl : '_appFrame'
									//xtype : 'tabpanel',
									//activeTab : 0,
									//id : 'mainPanel'
								}, {
									region : 'south',
									contentEl : 'foot'
								}]
					});
		});
