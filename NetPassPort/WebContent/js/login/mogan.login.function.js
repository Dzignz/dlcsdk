Ext.namespace("Mogan.login");





/**
 * 登入系統
 */
Mogan.login.login = function() {
	Ext.Ajax.request({
				url : 'OauthProtal',
				callback : function() {
					Ext.Msg.alert("訊息", "儲存完成");
				},
				success : function(response) {
					var json = parserJSON(response.responseText);
					alert(json['responseResult']);
				},
				failure : function(response) {
					addMsg("[錯誤]\tajax failure");
				},
				params : {
					OAUTH_TYPE : "WEB_LOGIN",
					RETURN_TYPE : "JSON",
					USER_NAME : Ext.getCmp("unameTextfield").getValue(),
					PWD : Ext.getCmp("pwdTextfield").getValue()
				}
			});
}
