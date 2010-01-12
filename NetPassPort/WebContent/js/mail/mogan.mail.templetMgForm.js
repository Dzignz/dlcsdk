Ext.namespace("Mogan.mail.templetMg");
var queryParsms = {
	params : {
		APP_ID : "fccc13447039e0ebf289e4227bc8e9e6",
		RETURN_TYPE : "JSON",
		MODEL_NAME : "MailService",
		ACTION : "LOAD_MAIL_TEMPLET_LIST",
		SEARCH_KEY : "",
		SHORT_FIELD : "",
		START_INDEX : 0,
		PAGE_SIZE : 25
	}
};

Mogan.mail.templetMg.createGridPanel = function() {

	var grid = new Ext.grid.GridPanel({
				trackMouseOver : true,
				stripeRows : true,
				disableSelection : true,
				stateful : true,
				loadMask : true,
				height : 500,
				flex : 1,
				store : dataStore,
				columns : [{
							header : "信件ID",
							dataIndex : 'id',
							width : 100

						}, {
							header : "信件類別",
							dataIndex : 'mail_class_id',
							width : 180
						}, {
							header : "信件標題",
							dataIndex : 'subject',
							width : 180
						}, {
							header : "最後修改日期",
							dataIndex : 'create_date',
							width : 100
						}, {
							header : "建立帳號",
							dataIndex : 'creator'
						}],
				bbar : new Ext.PagingToolbar({
							pageSize : 25,
							store : dataStore,
							displayInfo : true,
							displayMsg : 'Displaying topics {0} - {1} of {2}',
							emptyMsg : "No topics to display",
							items : ['-', {
										pressed : true,
										enableToggle : true,
										text : 'Show Preview',
										cls : 'x-btn-text-icon details',
										toggleHandler : function(btn, pressed) {
											var view = grid.getView();
											view.showPreview = pressed;
											view.refresh();
										}
									}]
						})
			});
			
	var editArea = new Ext.Panel({
				flex : 1,
				layout : 'fit',
				buttonAlign : 'left',
				items : [{
							xtype : 'textarea',
							fieldLabel : '信件內容',
							layout : 'fit',
							id : 'textareaMailContent'
						}],
				buttons : [{
							text : 'test'
						}]
			});
			
	var panel = new Ext.Panel({
				layout : 'hbox',
				layoutConfig : {
					align : 'stretch',
					pack : 'start'
				},
				items : [grid, editArea]

			});
			
	grid.on('rowclick', function(grid,rowIndex,event) {
				var record=grid.getStore().getAt(rowIndex);
				Mogan.mail.templetMg.loadMailContent(record.get("id"));
			});
	return panel;
}