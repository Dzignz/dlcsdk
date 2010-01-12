Ext.namespace("Mogan");
Mogan.createAcdMenu = function() {
	var acdMenu = new Ext.menu.Menu({
				renderTo : 'loginMenu',
				items : [{
							text : 'menu1'
						}, {
							text : 'menu2'
						}]
			});
	return acdMenu;
};
Mogan.createTreeMenu = function() {
	var treePanel = new Ext.tree.TreePanel({
				el : 'loginMenu'
			});
	var treeRoot = new Ext.tree.TreeNode({
				text : 'ROOT',
				href : 'bidService.html',
				hrefTarget : '_MainFrame'
			});
	treePanel.setRootNode(treeRoot);

	return treePanel;
};
Mogan.creatBidMenu = function() {
	var menuPanel = new Ext.Panel({
				layout : {
					type : 'vbox',
					align : 'stretch'
				},
				height : 900,
				enableToggle : true,
				items : [
						creatMenuBtn('手動下標', 'bidMenu', 'bidService.html'),
						creatMenuBtn('預約下標', 'bidMenu', '', true),
						creatMenuBtn('下標追蹤', 'bidMenu', 'transactionTrace.jsp'),
						creatMenuBtn('代拍清單2', 'sellItem', 'sellItem.jsp')]
			});
	return menuPanel;
};

Mogan.creatMailMenu = function() {
	var menuPanel = new Ext.Panel({
				layout : {
					type : 'vbox',
					align : 'stretch'
				},
				height : 900,
				enableToggle : true,
				items : [
						creatMenuBtn('信件範本管理', 'mailMenu',
								'mailTempletManage.html'),
						creatMenuBtn('發信程式', 'mailMenu', 'mailService.html')]
			});
	return menuPanel;
}

Mogan.creatAccountMenu = function() {
	var menuPanel = new Ext.Panel({
				layout : {
					type : 'vbox',
					align : 'stretch'
				},
				height : 900,
				enableToggle : true,
				items : [creatMenuBtn('產出亂數帳號', 'accountMenu', '', true)]
			});
	return menuPanel;
}

Mogan.creatTraceMenu = function() {
	var menuPanel = new Ext.Panel({
				layout : {
					type : 'vbox',
					align : 'stretch'
				},
				height : 900,
				enableToggle : true,
				items : [creatMenuBtn('商品問與答', 'mailMenu', '', true),
						creatMenuBtn('交易追蹤', 'mailMenu', '', true)]
			});
	return menuPanel;
}

Mogan.creatSysMenu = function() {
	var menuPanel = new Ext.Panel({
				layout : {
					type : 'vbox',
					align : 'stretch'
				},
				height : 900,
				enableToggle : true,
				items : [
						creatMenuBtn('Model Manager', 'modelService',
								'modelService.html'),
						creatMenuBtn('WebSite Manager', 'webSiteManager',
								'webSiteManager.jsp'),
						creatMenuBtn('applcation ID', 'mailMenu'),
						creatMenuBtn('test function', 'Test function','FunctionTest.jsp')]
			});
	return menuPanel;
}

/**
 * 建立關鍵字查詢Menu
 * 
 * @return {}
 */
Mogan.creatKeywordMenu = function() {
	var menuPanel = new Ext.Panel({
				layout : {
					type : 'vbox',
					align : 'stretch'
				},
				height : 900,
				enableToggle : true,
				items : [
						creatMenuBtn('關鍵字查詢', 'keywordWalkerService',
								'keywordWalkerService.html'),
						creatMenuBtn('關鍵字翻譯對照表', 'keywordMapping')]
			});
	return menuPanel;
}

function creatMenuBtn(label, group, url, isDisabled) {
	var btn = new Ext.Button({
				text : label,
				toggleGroup : group,
				enableToggle : true,
				disabled : isDisabled,
				handler : function() {
					openApp(url);
				}
			});
	return btn;
};

function openTab(id, title, url) {
	Ext.getCmp('mainPanel').add(new Ext.ux.IframeComponent({
				id : id,
				title : title,
				closeable : true,
				url : url
			}));

}

function openApp(url) {
	var iframe = Ext.get("_appFrame").dom;
	iframe.src = url;
}