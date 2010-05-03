package com.mogan.sys.model;


import java.util.List;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

/**
 * 此類別為專門處理檔用Model，透過doFile來進行檔案上傳處理
 * @author user
 *
 */
public interface FileModelFace {
	public abstract JSONArray doFile(HttpServletRequest req, HttpServletResponse res,List fileItems) throws Exception;
}
