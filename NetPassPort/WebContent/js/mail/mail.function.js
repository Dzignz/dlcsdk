Ext.namespace("Mogan.mail");
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