Ext.onReady(function() {
			var win = new Ext.Window({
						applyTo : 'login-win',
						layout : 'form',
						width : 300,
						height : 150,
						closeAction : 'hide',
						closable : false,
						bodyStyle : 'padding:5px 5px 0',
						// margins:'3 3 3 0',
						labelWidth : 75,
						title : 'Login....',
						// plain : true,
						id : 'login-win',
						items : [{
									xtype : 'textfield',
									id : 'unameTextfield',
									fieldLabel : 'User Name'
								}, {
									xtype : 'textfield',
									id : 'pwdTextfield',
									fieldLabel : 'Password',
									inputType : 'password'
								}],
						buttons : [{
									text : '登入',
									handler :function (){Mogan.login.login();}
								}]
					});
			win.show(this);
		});