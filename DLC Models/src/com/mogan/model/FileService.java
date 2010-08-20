/**
 * 
 */
package com.mogan.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.mogan.sys.DBConn;
import com.mogan.sys.log.SysLogger4j;
import com.mogan.sys.model.FileModelFace;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;

/**
 * @author Dian
 */
public class FileService extends ProtoModel implements ServiceModelFace,
		FileModelFace {
	Logger logger = Logger.getLogger(FileService.class.getName());

	public JSONArray doFile(HttpServletRequest req, HttpServletResponse res,
			List fileItems) {
		JSONArray jArray = new JSONArray();
		SysLogger4j.info("FileService start.");
		if (this.getAct().equals("UPLOAD_MAIL_TEMPLET_FILE")) {
			jArray = uploadFileMailTemplet(req, res, fileItems);
		}
		return jArray;
	}

	public JSONArray doAction(Map parameterMap) throws Exception {
		JSONArray jArray = new JSONArray();
		if (this.getAct().equals("LOAD_FILE")) {
			String fileid = (String) parameterMap.get("FILE_ID");
			jArray = loadMailTempLetFile(fileid);
		} else if (this.getAct().equals("SAVE_MAIL_TEMPLET")) {
			String mailId = (String) parameterMap.get("MAIL_ID");
			String mailSubject = (String) parameterMap.get("MAIL_SUBJECT");
			String mailContent = (String) parameterMap.get("MAIL_CONTENT");
			jArray = saveMailTempLetFile(mailId, mailSubject, mailContent);
		}
		return jArray;
	}

	private JSONArray saveMailTempLetFile(String mailId, String mailSubject,
			String mailContent) throws IOException {
		JSONArray jArray = new JSONArray();
		String errMsg = "";
		boolean errFlag = false;
		JSONObject jObj = new JSONObject();
		try {
			String filePath = "/mail_templet/";
			DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
			/* 更新資料庫資料 */
			String sql = "UPDATE web_mail SET subject='" + mailSubject
					+ "'  where id='" + mailId + "'";
			if (conn.executSql("mogan-tw", sql)) {
				// TODO 錯誤處理
				errMsg = "更新資料錯誤. ";
				errFlag = true;
			}

			/* 更新檔案內容 */
			FileWriter fw;

			fw = new FileWriter(this.getModelServletContext().getRealPath("/")
					+ this.getProperties().getProperty("uploadPath") + filePath
					+ mailId);

			BufferedWriter bfw = new BufferedWriter(fw);
			bfw.write(mailContent);
			bfw.flush();
			fw.close();

			if (errFlag) {
				jObj.put("", "");
			} else {
				jObj.put("", "");
			}

		} catch (IOException e) {
			
			logger.error("File 上傳", e);
			errMsg += "更新檔案錯誤.";
			errFlag = true;
			throw e;
		} catch (SQLException e) {
			logger.error("File 上傳", e);
		}

		jArray.add(jObj);
		return jArray;
	}

	/**
	 * 讀取信件範本
	 * 
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	private JSONArray loadMailTempLetFile(String fileid) throws Exception {
		JSONArray jArray = new JSONArray();


		String filePath = "/mail_templet/" + fileid;
		File f = new File(this.getModelServletContext().getRealPath("/")
				+ this.getProperties().getProperty("uploadPath") + filePath);

		/*
		 * File f = new File(this.getModelServletContext().getRealPath("/") + filePath);
		 */
		try {
			FileInputStream fis = new FileInputStream(f);
			// 指定utf-8編碼
			BufferedReader br;

			br = new BufferedReader(new InputStreamReader(fis, "utf-8"));

			String file_str_append = "";
			StringBuffer file_str = new StringBuffer();
			while ((file_str_append = br.readLine()) != null) {
				// file_str += file_str_append;
				file_str.append(file_str_append);
				file_str.append("\r\n");
			}
			file_str.delete(file_str.lastIndexOf("\r\n"), file_str.length());

			br.close();
			fis.close();
			// 將utf-8所有檔案內容輸出
			JSONObject jObj = new JSONObject();
			jObj.put("mailId", fileid);
			jObj.put("mailContent", file_str.toString());
			jArray.add(jObj);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage(),e);
			throw e;
		}

		return jArray;
	}

	private JSONArray uploadFileMailTemplet(HttpServletRequest req,
			HttpServletResponse res, List fileItems) {
		JSONArray jArray = new JSONArray();
		Iterator i = fileItems.iterator();
		JSONObject jObj = new JSONObject();
		String filePath = "/mail_templet/";
		String id = System.currentTimeMillis()+"";
		try {

			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				if (fi.getFieldName().startsWith("FILE_ITEM")) {
					jObj.put("filePath", fi.getName());
					File f = new File(this.getModelServletContext().getRealPath("/")
							+ this.getProperties().getProperty("uploadPath")
							+ filePath + id);
					try {
						if (!f.getParentFile().exists()) {
							logger.info("f.getParentFile().exists()... "+f.getParentFile().exists());
							f.getParentFile().mkdirs();
						}
						if (!f.getParentFile().isDirectory()) {
							logger.info("isDirectory().exists()... "+f.getParentFile().isDirectory());
							f.getParentFile().mkdirs();
						}
						f.createNewFile();
						fi.write(f); // 將檔案寫到磁碟
						logger.info(f.getAbsolutePath() + " 上傳完成");
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
						
					}
					jObj.put("mailId", id);
				} else if (fi.getFieldName().startsWith("FILE_NAME")) {
					jObj.put("mailSubject", fi.getString());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}
		jArray.add(jObj);
		return jArray;
	}

	// 將路徑過濾掉，只傳回檔名
	public String getFilename(String fullname) {
		String filename = null;
		fullname = fullname.replace('\\', '/');
		StringTokenizer token = new StringTokenizer(fullname, "/");
		while (token.hasMoreTokens()) {
			filename = token.nextToken();
		}
		return filename;
	}

}
