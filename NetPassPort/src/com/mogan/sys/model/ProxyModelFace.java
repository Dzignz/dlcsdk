package com.mogan.sys.model;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;


/**
 * 此類別為專門處理資料用Model，透過doAction來進行資料處理及回傳所需資料
 * @author user
 *
 */
public interface ProxyModelFace  {
	public abstract String doAction(Map map) throws Exception;
}
