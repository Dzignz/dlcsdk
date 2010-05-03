Ext.namespace("Mogan.yamato");

var appId = "26b782eb04abbd54efba0dcf854b158d";

Mogan.yamato.send = function() {
	var comboAction = Ext.getCmp('comboAction');
	var textfieldArgs = Ext.getCmp('textfieldArgs');
	var textareaMsgContent = Ext.getCmp('textareaMsgContent');
	var initParams = {
		APP_ID : appId,
		ACTION : comboAction.getValue(),
		RETURN_TYPE : "JSON",
		MODEL_NAME : "YAMATO_MODEL",
		WEB_SITE_ID : "SWD-2009-0001"
	};
	var args=textfieldArgs.getValue().split("&");
	for (var i=0;i<args.length;i++){
		initParams[args[i].split("=")[0]]=args[i].split("=")[1];
	}
	Ext.Ajax.request({
				url : 'AjaxPortal',
				success : function(response) {
					var json = parserJSON(response.responseText);
					if (json['responseResult'] == "failure") {
						Ext.Msg.alert("錯誤", json['responseMsg']);
						textareaMsgContent.setValue(json['responseMsg']);
					} else {
						textareaMsgContent.setValue(response.responseText);
					}
				},
				failure : function(response) {
					Ext.Msg.alert("錯誤", "請向程式開發者詢問");
				},
				params : initParams
			});

}
