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
import java.util.Properties;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.DiskFileUpload;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
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
public class FileService extends ProtoModel implements ServiceModelFace,FileModelFace {

	public JSONArray doFile(HttpServletRequest req, HttpServletResponse res,
			List fileItems) {
		JSONArray jArray = new JSONArray();
		SysLogger4j.info("FileService start.");
		if (this.getAct().equals("UPLOAD_MAIL_TEMPLET_FILE")) {
			jArray = uploadFileMailTemplet(req, res, fileItems);
		}
		return jArray;
	}

	public JSONArray doAction(Map parameterMap)
			throws Exception {
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
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
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
		

			fw = new FileWriter(this.getModelServletContext().getRealPath("/")+this.getProperties().getProperty("uploadPath")
					+ filePath + mailId);
			
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
			// TODO Auto-generated catch block
			SysLogger4j.error("File 上傳",e);
			errMsg += "更新檔案錯誤.";
			errFlag = true;
			throw e;
		} catch (SQLException e) {
			SysLogger4j.error("File 上傳",e);
			// TODO Auto-generated catch block
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
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");

		ArrayList dataList = conn.query("mogan-tw",
				"SELECT file_Path,subject from web_mail where id='" + fileid
						+ "'");
		if (dataList.size() > 0) {
			Map tempMap = (Map) dataList.get(0);
			String filePath = (String) tempMap.get("file_Path");
			String subject = (String) tempMap.get("subject");
			File f = new File(this.getModelServletContext().getRealPath("/")+this.getProperties().getProperty("uploadPath")
					+ filePath);

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
				file_str
						.delete(file_str.lastIndexOf("\r\n"), file_str.length());

				br.close();
				fis.close();
				// 將utf-8所有檔案內容輸出
				JSONObject jObj = new JSONObject();
				jObj.put("MailId", fileid);
				jObj.put("MailSubject", subject);
				jObj.put("MailContent", file_str.toString());
				jArray.add(jObj);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw e;
			}
		}
		return jArray;
	}

	private JSONArray uploadFileMailTemplet(HttpServletRequest req,
			HttpServletResponse res, List fileItems) {
		JSONArray jArray = new JSONArray();
		Iterator i = fileItems.iterator();
		JSONObject jObj = new JSONObject();
		String filePath = "/mail_templet/";
		String id;
		try {

			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				if (fi.getFieldName().startsWith("FILE_ITEM")) {
					String fileName = getFilename(fi.getName()); // fi.getName()得到的是包含路徑的檔名
					id = addTempletMailRecord("test", (String) jObj
							.get("mailSubject"), "", filePath, fileName);
					jObj.put("id", id);

					File f = new File(this.getModelServletContext().getRealPath("/")+this.getProperties().getProperty(
							"uploadPath")
							+ filePath + id);
					
					try {
						
						if (!f.getParentFile().exists()) {
							f.getParentFile().mkdirs();
						}
						if (!f.getParentFile().isDirectory()) {
							f.getParentFile().mkdirs();
						}
						fi.write(f); // 將檔案寫到磁碟
						SysLogger4j.info(f.getAbsolutePath()+" 上傳完成");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
				} else if (fi.getFieldName().startsWith("FILE_NAME")) {
					jObj.put("mailSubject", fi.getString());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jArray.add(jObj);
		return jArray;
	}

	/**
	 * 新增一筆資料到web_mail
	 * 
	 * @param mailClassId
	 * @param subject
	 * @param content
	 * @param filePath
	 * @return 資料ID
	 * @throws Exception
	 */
	private String addTempletMailRecord(String mailClassId, String subject,
			String content, String filePath, String fileName) throws Exception {
		// LAST_INSERT_ID()
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String id = "";
		try {
			id = conn.getAutoNumber("mogan-tw", "WM-ID-01");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		String sql = "INSERT INTO web_mail (id,mail_class_id,subject,content,file_Path,file_name,create_date,creator) VALUES ('"
				+ id
				+ "', '"
				+ mailClassId
				+ "', '"
				+ subject
				+ "', '"
				+ content
				+ "','"
				+ filePath
				+ id
				+ "','"
				+ fileName
				+ "',now(),'Dian')";
		if (conn.executSql("mogan-tw", sql)) {
			throw new Exception("[錯誤] 無法新增資料");
		}
		return id;
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
