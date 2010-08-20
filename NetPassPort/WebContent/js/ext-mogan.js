/**
 * 20090918 ver 0.1 �إ�mogan ext�M�Ϊ�xml�ѪRjs lib �^�Ǫ�xml�T�w��
 * responseResult-���\�Υ���, responseMsg-��~�T��, responseData-�^�ǭ�,
 * responseTime-����ɶ�
 */

var xr = new Ext.data.XmlReader({
			totalRecords : 'responseRecords',
			id : 'id',
			record : 'responseData'
		}, ['data']);

// Mogan.getXmlStore = function(fieldsKey) {
var xstore = new Ext.data.XmlStore({
	autoDestroy : true,
	storeId : 'xStore',
	record : 'responseData',
	totalRecords : 'responseRecords'
		// ields : ['responseResult', 'responseMsg', 'responseData',
		// / 'responseTime']
	});
// }

/* Ext store 專門用在data set上，複雜xml構合無法使用 */
function parserXML(xmlStr) {
	var xmlDom;
	xmlStr = xmlStr.replace(/^\s+|\s+$/g, "");
	// 1.先把接收的xml(文字格式)前後去空格
	if (window.ActiveXObject) {
		// 2.先檢查是否是ie，window.ActiveXObject是ie獨有的物件,
		// 在把接收的文字格式轉xml物件
		xmlDom = CreateXMLDOM();
		xmlDom.async = "false";
		xmlDom.loadXML(xmlStr);
	} else {
		// 3.不是ie我暫時認定是firefox，就把接收的文字格式轉xml物件
		parser = new DOMParser();
		xmlDom = parser.parseFromString(xmlStr, "text/xml");
	}
	/*
	 * var proxy = new Ext.data.MemoryProxy(xmlDom); var xr = new
	 * Ext.data.XmlReader({ //totalRecords : 'responseRecords', //id : 'id',
	 * record : 'records' },
	 * ['responseRecords','responseResult','responseMsg','responseTime','responseData']);
	 * var ds = new Ext.data.Store({ proxy : proxy, reader : xr }); //
	 * xstore.loadData(xmlDom) var xobj = xr.readRecords(xmlDom);
	 * ds.loadData(xmlDom);
	 */
	return xmlDom;
}

function CreateXMLDOM() {// window平台專用XML解析物件
	var ActiveX = new Array("MSXML2.DOMDocument.5.0", "MSXML2.DOMDocument.4.0",
			"MSXML2.DOMDocument.3.0", "MSXML2.DOMDocument", "Microsoft.XMLDOM",
			"MSXML.DOMDocument");
	for (var i = 0; i < ActiveX.length; i++) {
		try {
			return new ActiveXObject(ActiveX[i]);
		} catch (e) {
		}
	}
	return null;
}

function parserJSON(jsonStr) {
	// var rs = jr.readRecords(Ext.util.JSON.decode(jsonStr));
	try {
		return Ext.util.JSON.decode(jsonStr);
	} catch (e) {
		return {};
	}
}

function createXhrObject() {
	var http;
	var activeX = ['MSXML2.XMLHTTP.3.0', 'MSXML2.XMLHTTP', 'Microsoft.XMLHTTP'];
	try {
		http = new XMLHttpRequest();
	} catch (e) {
		for (var i = 0; i < activeX.length; ++i) {
			try {
				http = new ActiveXObject(activeX[i]);
				break;
			} catch (e) {
			}
		}
	} finally {
		return http;
	}
};