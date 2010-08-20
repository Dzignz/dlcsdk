/**
 * 
 */
package com.mogan.model;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.io.FileIO;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;

/**
 * @author Dian
 */
public class TempletManager extends ProtoModel implements ServiceModelFace {
	static private Logger logger = Logger.getLogger(TempletManager.class.getName());
	private String fileWoner=null;
	private String fileModel=null;
	/*
	 * (non-Javadoc)
	 * @see com.mogan.sys.model.ServiceModelFace#doAction(java.util.Map)
	 */
	@Override
	public JSONArray doAction(Map<String, String> parameterMap)
			throws Exception {
		JSONArray jArray = new JSONArray();
		logger.info("[INFO]TempletManager ACTION start. " + this.getAct());
		fileWoner = (String) parameterMap.get("WONER");
		fileModel = (String) parameterMap.get("TEMPLATE_MODEL");
		if (this.getAct().equals("DEL_TEMPLATE")) {
			String templetName = (String) parameterMap.get("TEMPLATE_NAME");
			jArray = delTemplate( templetName);
		} else if (this.getAct().equals("SAVE_TEMPLATE")) {
			String templateName = (String) parameterMap.get("TEMPLATE_NAME");
			String templateText = (String) parameterMap.get("TEMPLATE_TEXT");
			jArray = saveTemplate( templateName, templateText);
		}else if (this.getAct().equals("SAVE_TRNS_CODE_LIST")){
			String trnsCodeList= (String) parameterMap.get("TRNS_CODE_LIST");
			JSONArray jTrnsCodeList=JSONArray.fromObject(trnsCodeList);
			jArray = saveUserPty(jTrnsCodeList);
		}else if (this.getAct().equals("LOAD_TEMPLATE")){
			String templetName= (String) parameterMap.get("TEMPLATE_NAME");
			jArray=loadTemplate(templetName);
		}
		return jArray;
	}

	/**
	 * 讀取範本內容
	 * @param templetName
	 * @return
	 */
	public JSONArray loadTemplate(String templetName){
		FileIO fio =new FileIO();
		JSONArray jArray=new JSONArray();
		JSONObject jObj=new JSONObject();
		//jObj.put(templetName, fio.loadTxtFile(null, this.getModelName(), templetName));
		jObj.put("fileName", templetName);
		jObj.put("fileContent", fio.loadTxtFile(fileWoner, fileModel, templetName).toString());
		jArray.add(jObj);
		//fio.loadTxtFile(null, this.getModelName(), templetName);
		return jArray;
	}
	
	/**
	 * 將個人設定保存起來
	 * @param jTrnsCodeList
	 * @return
	 */
	public JSONArray saveUserPty(JSONArray jTrnsCodeList){
		FileIO fio =new FileIO();
		JSONArray jArray=new JSONArray();
		Properties p = new Properties();
		p.put("TRNS_CODE_LIST", jTrnsCodeList.toString());
		jArray.add(fio.savePtyFile(fileWoner, fileModel, p, "")) ;
		return jArray;
	}
	
	/**
	 * 儲存範本檔案
	 * 
	 * @param templetName
	 * @param templetText
	 * @return
	 */
	public JSONArray saveTemplate( String templetName,
			String templetText) {
		FileIO fio = new FileIO();
		JSONArray jArray = new JSONArray();
		jArray.add(fio.saveTxtFile(fileWoner, fileModel, templetName, templetText));
		return jArray;
	}

	/**
	 * 刪除指定的範本
	 * 
	 * @param templetName
	 * @return
	 */
	public JSONArray delTemplate( String templetName) {
		FileIO fio = new FileIO();
		JSONArray jArray = new JSONArray();
		jArray.add(fio.delTxtFile(fileWoner, fileModel, templetName));
		return jArray;
	}
}
