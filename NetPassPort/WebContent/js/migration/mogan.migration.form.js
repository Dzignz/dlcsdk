Ext.namespace("Mogan.migration");


/**
 * 建立主畫面
 * 
 * @return {}
 */
Mogan.migration.createTabPanel= function() {
	// alert('createTabPanel');
	// alert('createTabPanel');
	var tabPanel = new Ext.TabPanel({
				activeTab : 0,
				id : 'DetilPanel',
				items : [{
							title : '會員資料',
							layout : 'fit',
							items : Mogan.migration.createMemberPanel()
						}]
			});
	return tabPanel;
};

/**
 * 建立會員資料整合介面
 */
Mogan.migration.createMemberPanel= function() {
	var panel = new Ext.Panel({
		id : 'itemPanel',
		frame : true,
		buttonAlign : 'left',
		items:[{xtype : 'button',
				text : '開始整合',
				handler : function (){Mogan.migration.startMigr('MEMBER_DATA');}
		},{xtype : 'button',
				text : '開始整合-2',
				handler : function (){Mogan.migration.showPie();}
		},{el:'images',frame:true}]

		/*
		 * Ext.DomHelper.append('images', { tag: 'img', src: data.url,
		 * style:'margin:10px;visibility:hidden;' }, true).show(true).frame();
		 */

	});
	
	
	return panel;
}