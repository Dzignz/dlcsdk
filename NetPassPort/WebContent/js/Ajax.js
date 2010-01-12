var checkSessionFlag=false;
var waitWindow;
function createHttpRequest(){
	if(window.ActiveXObject){
		try{
			return new ActiveXObject("Msxml2.XMLHTTP");
		}catch(e){
			try{
				return new ActiveXObject("Microsoft.XMLHTTP");
			}catch(e2){
				return null;
			}
		}
	}else if(window.XMLHttpRequest){
		return new XMLHttpRequest();
	}else{
		return null;
	}
}

function chkAjaBrowser()
	{
		var a,ua = navigator.userAgent;
		this.bw= { 
		  safari    : ((a=ua.split('AppleWebKit/')[1])?a.split('(')[0]:0)>=124 ,
		  konqueror : ((a=ua.split('Konqueror/')[1])?a.split(';')[0]:0)>=3.3 ,
		  mozes     : ((a=ua.split('Gecko/')[1])?a.split(" ")[0]:0) >= 20011128 ,
		  opera     : (!!window.opera) && ((typeof XMLHttpRequest)=='function') ,
		  msie      : (!!window.ActiveXObject)?(!!createHttpRequest()):false 
		}
		return (this.bw.safari||this.bw.konqueror||this.bw.mozes||this.bw.opera||this.bw.msie)
	}

function sendRequest(callback,data,method,url,async,sload,user,password)
	{


		var oj = createHttpRequest();
		if( oj == null ) return null;
		var sload = (!!sendRequest.arguments[5])?sload:false;
		if(sload || method.toUpperCase() == 'GET')url += "?";
		if(sload)url=url+"t="+(new Date()).getTime();
		
		var bwoj = new chkAjaBrowser();
		var opera	  = bwoj.bw.opera;
		var safari	  = bwoj.bw.safari;
		var konqueror = bwoj.bw.konqueror;
		var mozes	  = bwoj.bw.mozes ;
				
		if(typeof callback=='object'){
			var callback_onload = callback.onload
			var callback_onbeforsetheader = callback.onbeforsetheader
		} else {
			var callback_onload = callback;
			var callback_onbeforsetheader = null;
		}

		if(opera || safari || mozes){
			oj.onload = function () { callback_onload(oj); }
		} else {
		
			oj.onreadystatechange =function () 
			{
				if ( oj.readyState == 4 ){
					callback_onload(oj);
				}
			}
		}
		data = uriEncode(data,url)
		if(method.toUpperCase() == 'GET') {
			url += data
		}
		oj.open(method,url,async,user,password);

		if(!!callback_onbeforsetheader)callback_onbeforsetheader(oj)
		setEncHeader(oj)
		oj.send(data);
		function setEncHeader(oj){
			var contentTypeUrlenc = 'application/x-www-form-urlencoded; charset=UTF-8';
			
			if(!window.opera){
				oj.setRequestHeader('Content-Type',contentTypeUrlenc);
			} else {
				if((typeof oj.setRequestHeader) == 'function')
					oj.setRequestHeader('Content-Type',contentTypeUrlenc);
			}	
			return oj
		}

		function uriEncode(data,url){
			var encdata =(url.indexOf('?')==-1)?'?dmy':'';
			if(typeof data=='object'){
				for(var i in data)
					encdata+='&'+encodeURIComponent(i)+'='+encodeURIComponent(data[i]);
			} else if(typeof data=='string'){
				if(data=="")return "";
				var encdata = '';
				var datas = data.split('&');
				for(i=1;i<datas.length;i++)
				{
					var dataq = datas[i].split('=');
					encdata += '&'+encodeURIComponent(dataq[0])+'='+encodeURIComponent(dataq[1]);
				}
			} 
			return encdata;
		}

		return oj
	}

function callfunction(callback){
	//sendRequest(callback,'&CALL_TYPE=AJAX','GET','./SupplierServlet',true,true);
	//sendRequest(callback,'&CALL_TYPE=AJAX','GET','http://127.0.0.1:8083/SupplierServlet',true,true);
	var sParams = "";
	sParams = addPostParam(sParams, "CALL_TYPE", "AJAX");
    sParams = addPostParam(sParams, "GetUploadedPercentage", "YES");
	sendRequest(callback,sParams,'POST','http://127.0.0.1:8083/FileUploadServlet',true,true);
}

function addPostParam(sParams, sParamName, sParamValue) {
	if(sParams.length == 0) {
		sParams += "&";
	}
	
	return sParams + encodeURIComponent(sParamName) + "=" + encodeURIComponent(sParamValue)+"&";
}


function getMessage(obj){
	var msgMap=new Map();
	try{
	if(obj.readyState==4 && obj.status==200){
		var msg=document.getElementById('msg');
		var response = obj.responseXML.documentElement;
		var records = response.getElementsByTagName('info');
		var result = null;
		if(records[0].getElementsByTagName("result")[0].childNodes[0]!=null){
			result=records[0].getElementsByTagName("result")[0].childNodes[0].nodeValue;
		}
		
		var session_out = null;
		if(records[0].getElementsByTagName("session_out")[0] != null){
			
			if(records[0].getElementsByTagName("session_out")[0].childNodes.length>0){
				session_out=records[0].getElementsByTagName("session_out")[0].childNodes[0].nodeValue;
				
				if(session_out=='true'){
					if(checkSessionFlag==false){
						var d=new Date();
						var windowId=d.getTime()+"-"+Math.random()*1000;					
						var	returnObject;
						checkSessionFlag=true;
						if(location.href.indexOf('SupplierServlet')>0){
							returnObject=window.showModalDialog("./TraceServlet?PlugInCode=F-2008-07-00009&LoginRole=Supplier&FrameType=LoginDialog&windowId="+windowId+"&","Login",'dialogHeight:200px;dialogWidth=300px;status=no ' );
						}else{
							returnObject=window.showModalDialog("./TraceServlet?PlugInCode=F-2008-07-00009&FrameType=LoginDialog&windowId="+windowId+"&","Login",'dialogHeight:200px;dialogWidth=300px;status=no ' );
						}
						checkSessionFlag=false;
						if(returnObject==null){
							return;
						}else if(returnObject['result']!='0'){
							alert(returnObject['message']);	
						}else{
							alert(returnObject['message']);	
						}
						return;
					}else{
						return;
					}
				}
			}
		}
		msgMap.put('result',result);
		var message = null;
		if(records[0].getElementsByTagName("message")[0].childNodes[0]!=null){
			message=records[0].getElementsByTagName("message")[0].childNodes[0].nodeValue;
		}
		
		msgMap.put('message',message);
		
		var functionId = null;
		if(records[0].getElementsByTagName("function")[0] != null){
			if(records[0].getElementsByTagName("function")[0].childNodes.length>0){
				functionId=records[0].getElementsByTagName("function")[0].childNodes[0].nodeValue;
			}
		}
		
		msgMap.put('function',functionId);
		/*for(var i=0;i<records[0].childNodes.length;i++){
			alert('obj....'+records[0].childNodes[i].nodeName);
		}*/
		var returnObject = new Object();
		//alert(records[0].getElementsByTagName("return_object")[0].childNodes.length);
		
		if(records[0].getElementsByTagName("return_object").length>0){
		if(records[0].getElementsByTagName("return_object")[0] != null){
		if(records[0].getElementsByTagName("return_object")[0].childNodes.length>0){
			//alert(records[0].getElementsByTagName("return_object")[0].childNodes.length);
			for(var i=0;i<records[0].getElementsByTagName("return_object")[0].childNodes.length;i++){
				
				if(records[0].getElementsByTagName("return_object")[0].childNodes[i].childNodes.length>0){
					var return_key=records[0].getElementsByTagName("return_object")[0].childNodes[i].nodeName;
					var return_value=records[0].getElementsByTagName("return_object")[0].childNodes[i].childNodes[0].nodeValue;
					
					//alert(return_key+'...'+return_value);
					returnObject[return_key]=return_value;
				}
			}
		}
		}
		}
		
		msgMap.put('return_object',returnObject);
		var response_data = null;
		if(records[0].getElementsByTagName("response_data").length>0){
			if(records[0].getElementsByTagName("response_data")[0].childNodes[0]!=null){
				if(response_data=records[0].getElementsByTagName("response_data")[0].childNodes[0].nodeValue==null){
					response_data=records[0].getElementsByTagName("response_data")[0].childNodes[0];
					//var xmlString = response_data.xml;
					//var xmlString = response_data.textContent;
					var oBrowser = new detectBrowser();
					if(oBrowser.isIE==true){
						msgMap.put('response_xml',response_data.xml);
					}else{
						
						var xmlString = (new XMLSerializer()).serializeToString(response_data);
						msgMap.put('response_xml',xmlString);
					}				
					
				}
				else
					response_data=records[0].getElementsByTagName("response_data")[0].childNodes[0].nodeValue;
			}
		}
		msgMap.put('response_data',response_data);
		
		if(records[0].getElementsByTagName("response_xml").length>0){
			if(records[0].getElementsByTagName("response_xml")[0]!=null){
				var subElement=records[0].getElementsByTagName("response_xml")[0];
				var subMsgMap=new Map();
				msgMap.put('response_xml',subMsgMap);
				processSubMsg(subMsgMap,subElement);
		
			}
		}

	}else{
			msgMap.put('message','Connect failed!');
	}
	}catch(error){
		
	}

	return msgMap;
}

function processSubMsg(msgMap,element){
	if(element==null)
		return;
	if(element.childNodes==null)
		return;
	for(var j=0;j<element.childNodes.length;j++){
		var subElement=element.childNodes[j];
		if(subElement.childNodes[0]!=null && subElement.childNodes[0].nodeValue==null && subElement.childNodes.length>0){
			var subMsgMap=new Map();
			msgMap.put(element.tagName,subMsgMap);
			processSubMsg(subMsgMap,subElement);
		}else{
			if(subElement.childNodes[0]!=null){
				msgMap.put(subElement.tagName,subElement.childNodes[0].nodeValue);
			}else{
				msgMap.put(subElement.tagName,null);
			}
		}
	}
}

function processMsg(obj){
	if(obj.readyState==4 && obj.status==200){
		window.focus();
		alert("The Msg is " + obj.responseText+"/Status:"+obj.readyState);    
	}
}


function encode(data){
	var encdata="";
	if(typeof data=='object'){
		for(var i in data)
			encdata+='&'+encodeURIComponent(i)+'='+encodeURIComponent(data[i]);
	} else if(typeof data=='string'){
		if(data=="")return "";
		var encdata = '';
		var datas = data.split('&');
		for(i=1;i<datas.length;i++)
		{
			var dataq = datas[i].split('=');
			encdata += '&'+encodeURIComponent(dataq[0])+'='+encodeURIComponent(dataq[1]);
		}
	} 
	return encdata;
}

function detectBrowser()//§PÂ_¬O§_¥ÎIEÂsÄý¾¹
 { 
  var sAgent = navigator.userAgent.toLowerCase();
  this.isIE = (sAgent.indexOf('msie')!=-1); //IE6.0-7
  this.isFF = (sAgent.indexOf('firefox')!=-1);//firefox
  this.isSa = (sAgent.indexOf('safari')!=-1);//safari
  this.isOp = (sAgent.indexOf('opera')!=-1);//opera
  this.isNN = (sAgent.indexOf('netscape')!=-1);//netscape
  this.isMa = this.isIE;//marthon
  this.isOther = (!this.isIE && !this.isFF && !this.isSa && !this.isOp && !this.isNN && !this.isSa);//unknown Browser
 }