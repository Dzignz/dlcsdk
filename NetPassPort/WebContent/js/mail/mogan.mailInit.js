Ext.onReady(function() {

	var headPanel = new Ext.Panel({
				buttons : [{
							text : 'text'
						}]
			});
	var bodyPanel = new Ext.Panel({
				buttons : [{
							text : 'text'
						}]
			});
	var tabs = new Ext.TabPanel({
				id : 'mainTabPanel',
				renderTo : Ext.getBody(),
				activeTab : 0,
				items : [{
							id : 'tabMailContent',
							title : '信件內容',
							items : [Mogan.mail.createMailContentFrom()],
							layout : 'fit'
						}, {
							id : 'tabSenderStatus',
							title : '發信狀態',
							items : [Mogan.mail.createMailStatusFrom()],
							layout : 'fit'
						}, {
							id : 'tabSetup',
							title : '設定',
							items : [Mogan.mail.createMailSetupFrom()],
							layout : 'fit'
						}]
			});
	var bodyPanel = new Ext.Panel({
		title : 'Mail Service',
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
					// bodyStyle : 'padding:5px 5px 5px 5px',
					items : [new Ext.Button({
										scale : 'large',
										text : '上傳HTML原始檔',
										id : 'btnUploadFile',
										handler : function() {
											Mogan.mail.showUploadWindow();
										}
									}), new Ext.Button({
										scale : 'large',
										text : '讀取範本',
										handler : function() {
											Mogan.mail.openTempletLoader();
										}
									}), new Ext.Button({
										scale : 'large',
										text : '寄送信件',
										handler : function() {
											Mogan.mail.setMail();
										},
										id : 'btnStartSend'
									}), new Ext.Button({
										scale : 'large',
										text : '暫停寄送',
										disabled : true,
										handler : function() {
											Mogan.mail
													.setMailSenderAct('PAUSE');
										},
										id : 'btnPauseSend'
									}), new Ext.Button({
										scale : 'large',
										text : '停止寄送',
										disabled : true,
										handler : function() {
											Mogan.mail.setMailSenderAct('STOP');
										},
										id : 'btnStopSend'
									}), new Ext.Button({
										scale : 'large',
										text : '教學影片',
										iconCls:'video',
										handler : function() {
											window.open('/demo/MailService_demo/MailService_demo.htm');
										}
									})]
				}, {
					region : 'center',
					items : tabs,
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
	Mogan.mail.loadMailGroup();
	Mogan.mail.loadProperties();
});
/**
 * 設定狀態列的訊息
 * 
 * @param {}
 *            msg 狀態訊息
 */
Mogan.mail.setStatusMsg = function(msg) {
	if (statusFlag) {
		var obj = new Ext.getCmp("statusMsg");
		obj.setText(msg);
	}
}