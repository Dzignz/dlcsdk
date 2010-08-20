package com.mogan.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import com.mogan.model.netAgent.NetAgent;
import com.mogan.sys.DBConn;
import com.mogan.sys.SysCalendar;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;

public class WebWalker extends ProtoModel implements ServiceModelFace {
	static NetAgent nAgent = new NetAgent();
	private static Logger logger = Logger.getLogger( WebWalker.class.getName());
	
	private JSONArray fixKeyword() throws UnsupportedEncodingException, SQLException {
		JSONArray jArray = new JSONArray();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String sql = "SELECT * FROM search_keyword WHERE (keyword_tw IS NULL OR keyword_tw='') and (keyword_us IS NULL OR keyword_us='')";
		int count = conn.getQueryDataSize("mogan-DB", sql);
		int pageSize = 1000;
		int fixCount = 0;
		logger.info("[INFO] fixKeyword-1:count="
				+ ((count + pageSize) / pageSize));
		ArrayList keywords;
		Map<String, String> tempMap;
		Map conditionMap;// 條件
		Map dataMap;// 資料

		// ////////////////將日文為英文的資料複制到中文及英文
		for (int i = 0; i < ((count + pageSize * 2) / pageSize); i++) {

			fixCount = 0;
			keywords = conn.queryWithPage("mogan-tw", sql, i * pageSize,
					pageSize);

			for (int j = 0; j < keywords.size(); j++) {
				tempMap = (Map) keywords.get(j);
				if (checkChart(tempMap.get("keyword_jp"))) {
					conditionMap = new HashMap();
					conditionMap.put("id", tempMap.get("id"));
					dataMap = new HashMap();
					dataMap.put("keyword_tw", tempMap.get("keyword_jp"));
					dataMap.put("keyword_us", tempMap.get("keyword_jp"));
					conn.update("mogan-tw", "search_keyword", conditionMap,
							dataMap);
					fixCount++;
				}
			}
			logger.info("[INFO] fixKeyword-1:已處理筆數=" + i * pageSize
					+ ":" + fixCount);
			// break;
		}
		jArray.add(fixCount+"/"+count);
		
		// ////////////////將中文為英數的資料複制到英文
		sql = "SELECT * FROM search_keyword WHERE keyword_tw IS NOT NULL AND  keyword_tw  NOT LIKE ''  AND keyword_jp IS NOT NULL AND keyword_jp NOT LIKE '' AND (keyword_us IS NULL OR keyword_us='')";
		count = conn.getQueryDataSize("mogan-tw", sql);

		logger.info("[INFO] fixKeyword-2:count="
				+ ((count + pageSize) / pageSize));
		
		fixCount=0;//將修正數量歸零
		for (int i = 0; i < ((count + pageSize * 2) / pageSize); i++) {
			fixCount = 0;
			keywords = conn.queryWithPage("mogan-tw", sql, i * pageSize,
					pageSize);

			for (int j = 0; j < keywords.size(); j++) {
				tempMap = (Map) keywords.get(j);
				if (checkChart(tempMap.get("keyword_tw"))) {
					conditionMap = new HashMap();
					conditionMap.put("id", tempMap.get("id"));
					dataMap = new HashMap();
					dataMap.put("keyword_us", tempMap.get("keyword_tw"));
					conn.update("mogan-tw", "search_keyword", conditionMap,
							dataMap);
					fixCount++;
				}
				// logger.info("[DEBUG] fixKeyword:("+j+")="+tempMap.get("id")+":"+tempMap.get("keyword_jp")+":"+tempMap.get("keyword_tw")+":"+
				// tempMap.get("keyword_us"));
			}
			logger.info("[INFO] fixKeyword-2:已處理筆數=" + i * pageSize
					+ ":" + fixCount);
			// break;
		}
		jArray.add(fixCount+"/"+count);
		
		//分類
		sql = "SELECT * FROM api_category WHERE category_jp IS NOT NULL AND  category_jp  NOT LIKE ''  AND (category_tw IS NULL OR category_tw='' OR category_en IS NULL OR category_en='' OR category_cn IS NULL OR category_cn='')";
		count = conn.getQueryDataSize("mogan-DB", sql);

		logger.info("[INFO] fixKeyword-3:count="
				+ ((count + pageSize) / pageSize));
		
		fixCount=0;//將修正數量歸零
		for (int i = 0;  i < ((count + pageSize * 2) / pageSize); i++) {
			fixCount = 0;
			keywords = conn.queryWithPage("mogan-DB", sql, i * pageSize,
					pageSize);

			for (int j = 0; j < keywords.size(); j++) {
				tempMap = (Map) keywords.get(j);
				if (checkChart(tempMap.get("category_jp"))) {
					conditionMap = new HashMap();
					conditionMap.put("id", tempMap.get("id"));
					dataMap = new HashMap();
					dataMap.put("category_tw", tempMap.get("category_jp"));
					dataMap.put("category_en", tempMap.get("category_jp"));
					dataMap.put("category_cn", tempMap.get("category_jp"));
					conn.update("mogan-DB", "api_category", conditionMap,
							dataMap);
					fixCount++;
				}
				// logger.info("[DEBUG] fixKeyword:("+j+")="+tempMap.get("id")+":"+tempMap.get("keyword_jp")+":"+tempMap.get("keyword_tw")+":"+
				// tempMap.get("keyword_us"));
			}
			logger.info("[INFO] fixKeyword-3:已處理筆數=" + i * pageSize
					+ ":" + fixCount);
			// break;
		}
		jArray.add(fixCount+"/"+count);
		
		//分類-中文欄位為純英數
		sql = "SELECT * FROM api_category WHERE category_jp IS NOT NULL AND  category_tw  NOT LIKE ''  AND category_jp IS NOT NULL AND ( category_en IS NULL OR category_en='' )";
		count = conn.getQueryDataSize("mogan-DB", sql);

		logger.info("[INFO] fixKeyword-4:count="
				+ ((count + pageSize) / pageSize));
		
		fixCount=0;//將修正數量歸零
		for (int i = 0; i < ((count + pageSize * 2) / pageSize); i++) {
			fixCount = 0;
			keywords = conn.queryWithPage("mogan-DB", sql, i * pageSize,
					pageSize);
			for (int j = 0; j < keywords.size(); j++) {
				tempMap = (Map) keywords.get(j);
				if (checkChart(tempMap.get("category_tw"))) {
					conn.executSql("mogan-DB", "UPDATE api_category SET category_en='"+tempMap.get("category_tw").replaceAll("'", "''")+"',category_tw='"+tempMap.get("category_tw").replaceAll("'", "''")+"' WHERE category_jp='"+tempMap.get("category_jp")+"' AND (category_en IS NULL OR category_en LIKE '')");
					conn.executSql("mogan-DB", "UPDATE api_category SET category_tw='"+tempMap.get("category_tw").replaceAll("'", "''")+"' WHERE category_jp='"+tempMap.get("category_jp")+"' AND (category_tw IS NULL OR category_tw LIKE '')");
					fixCount++;
				}
			}
			logger.info("[INFO] fixKeyword-4:已處理筆數=" + i * pageSize
					+ ":" + fixCount);
		}
		jArray.add(fixCount+"/"+count);
		
		sql = "SELECT * FROM api_category WHERE category_tw IS NOT NULL AND  category_tw  NOT LIKE ''  AND category_jp IS NOT NULL AND ( category_cn IS NULL OR category_cn='' )";
		count = conn.getQueryDataSize("mogan-DB", sql);

		logger.info("[INFO] fixKeyword-5:count="
				+ ((count + pageSize) / pageSize));
		fixCount=0;//將修正數量歸零
		for (int i = 0; i < ((count + pageSize * 2) / pageSize); i++) {
			fixCount = 0;
			keywords = conn.queryWithPage("mogan-DB", sql, i * pageSize,
					pageSize);
			for (int j = 0; j < keywords.size(); j++) {
				tempMap = (Map) keywords.get(j);
				if (checkChart(tempMap.get("category_tw"))) {
					conn.executSql("mogan-DB", "UPDATE api_category SET category_cn='"+tempMap.get("category_tw").replaceAll("'", "''")+"' WHERE category_jp='"+tempMap.get("category_jp")+"' AND (category_cn IS NULL OR category_cn LIKE '')");
					conn.executSql("mogan-DB", "UPDATE api_category SET category_tw='"+tempMap.get("category_tw").replaceAll("'", "''")+"' WHERE category_jp='"+tempMap.get("category_jp")+"' AND (category_tw IS NULL OR category_tw LIKE '')");
					fixCount++;
				}
			}
			logger.info("[INFO] fixKeyword-5:已處理筆數=" + i * pageSize
					+ ":" + fixCount);
		}
		jArray.add(fixCount+"/"+count);
		
		logger.info("[DEBUG] fixKeyword:END");
		// jArray.add(keywords);
		return jArray;
	}

	/**
	 * @param keyword
	 *            關鍵字
	 * @param charSet
	 *            編碼
	 * @param isUseDBWord
	 *            是否使用DB 關鍵字
	 * @param day
	 *            使用多久前的關鍵字
	 * @return
	 * @throws Exception
	 */
	private JSONArray searchKeyword(String keyword,final String charSet,
			boolean isUseDBWord, int day) throws Exception {
		if (isUseDBWord) {
			 keyword=getDBWord(day);
		}
		return searchKeyword(keyword, charSet);
	}
	
	private void searchKeywordByThread(String keyword,final String charSet,
			boolean isUseDBWord, int day) throws Exception {
		if (isUseDBWord) {
			ArrayList dataList=getDBWord(day,3);
			for (int i=0;i<dataList.size();i++){
				Map tempMap = (Map) dataList.get(i);
				final String keyword2=(String) tempMap.get("keyword_jp");
				logger.info(tempMap.get("keyword_jp")+"---xxxxxxxxxxx Start.("+i);
				class keyword extends Thread {
					private String keyword;
					private String charSet;
					public keyword(String keyword,String charSet){
						this.keyword=keyword;
						this.charSet=charSet;
					}
					
					public synchronized  void run (){
						logger.info(keyword+"---Start.");
						try {
							searchKeyword(keyword, charSet);
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
						}
						logger.info(keyword+"---End.");
					}
				}
				Thread t=new keyword((String) tempMap.get("keyword_jp"),charSet);
				t.start();
			}
		}
	}

	/**
	 * 判斷是否只包含英數
	 * 
	 * @param newKeyword
	 * @return true 只含英數，false 含有雙Btye字串
	 */
	private boolean checkChart(String newKeyword) {
		if (newKeyword == null) {
			return false;
		}
		if (newKeyword.getBytes().length != newKeyword.length()) {
			return false;
		}
		return true;
	}

	/**
	 * @param keyword
	 * @param charSet
	 * @return
	 * @throws Exception
	 */
	private JSONArray searchKeyword(String keyword, String charSet)
			throws Exception {
		
		JSONArray jArray =new JSONArray();

		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		try {
			Connection connection = conn.getConnection("mogan-DB");
			CallableStatement cs = connection
					.prepareCall("{?=call addkeyword(?,?)}");
			cs.registerOutParameter(1, Types.BIT);// 設定輸出子的資料狀態

			JSONObject jObj = new JSONObject();
			JSONArray newWordArray = new JSONArray();
			jObj.put("KEY_WORD", keyword);

			String encodeKeyword;
			logger.info("[DEBUG]searchKeyword:" + keyword);
			encodeKeyword = URLEncoder.encode(keyword, charSet);// 進行資料編碼
			nAgent
					.getDataWithGet("http://search.auctions.yahoo.co.jp/jp/search/auc?p="
							+ encodeKeyword);
			String tempKeyword = "";
			int l = 0;

			NodeList nodes = nAgent.filterReferenceKeyword();
			cs.setString(2, keyword);
			cs.setString(3, keyword);
			cs.execute();// 預先執行，避免查尋結果沒有建議關鍵字造成LOOP

			for (int i = 0; i < nodes.size(); i++) {
				l++;
				Node n = nodes.elementAt(i);
				tempKeyword = n.toHtml();
				tempKeyword = tempKeyword.split("<nobr>")[1].split("</nobr>")[0];// 篩選掉不該出現的HTML符号
				// logger.info(l + ") " + tempKeyword + " (" + keyword + ") "+checkChart(tempKeyword));

				cs.setString(2, keyword);
				cs.setString(3, tempKeyword);

				cs.execute();

				// logger.info("cs.getInt(1)::"+cs.getResultSet().getRow());
				// logger.info("cs.getInt(1)::" + cs.getInt(1));
				if (cs.getInt(1) == 1) {// 取回回傳值(第一個?)0=已資料存在
					tempKeyword = "*" + tempKeyword;// 如果是新字就加上*号
				}
				newWordArray.add(tempKeyword);

			}
			jObj.put("NEW_WORD", newWordArray);
			jArray.add(jObj);
			
			cs.close();
			connection.close();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return jArray;
	}

	/**
	 * 取得DB關鍵字
	 * 
	 * @param day
	 * @return
	 */
	private  String getDBWord(int day) {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String date = "";
		String returnStr = "";
		SysCalendar sysCal = new SysCalendar();
		sysCal.setDateFormat(sysCal.MM_dd_yyyy);
		sysCal.setDateFormat(sysCal.yyyy_MM_dd);
		sysCal.addDay(-day);
		date = sysCal.getFormatDate();
		ArrayList dataList;
		//		 dataList = conn				.query(						"mogan-DB",						"SELECT keyword_jp as keyword FROM api_search_keyword_word WHERE keyword_id = (SELECT MIN(keyword_id) FROM api_search_keyword_word WHERE (last_date < STR_TO_DATE('"							+ date								+ "','%Y/%m/%d') or last_date is null) and keyword_jp is not null )");
		 dataList = conn
		.query(
				"mogan-DB",
				"SELECT diction_translate as keyword FROM api_search_keyword_diction WHERE diction_id = (SELECT MIN(diction_id) FROM api_search_keyword_diction WHERE (time_at < STR_TO_DATE('"
						+ date
						+ "','%Y/%m/%d') or time_at is null) and diction_translate is not null )");
		if (dataList.size() > 0) {
			Map tempMap = (Map) dataList.get(0);
			returnStr = (String) tempMap.get("keyword");
		}
		
		return returnStr;
	}
	
	
	private ArrayList getDBWord(int day,int limit) {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String date = "";
		String returnStr = "";
		SysCalendar sysCal = new SysCalendar();
		sysCal.setDateFormat(sysCal.MM_dd_yyyy);
		sysCal.setDateFormat(sysCal.yyyy_MM_dd);
		sysCal.addDay(-day);
		date = sysCal.getFormatDate();
		ArrayList dataList = conn
				.query(
						"mogan-DB",
						"SELECT keyword_jp FROM api_search_keyword_word WHERE keyword_id in (SELECT keyword_id FROM api_search_keyword_word WHERE (last_date < STR_TO_DATE('"
								+ date
								+ "','%Y/%m/%d') or last_date is null) and keyword_jp is not null ) limit "+limit);


		
		return dataList;
	}

	@Override
	public JSONArray doAction(Map parameterMap) throws Exception {
		JSONArray jArray = new JSONArray();

		if (this.getAct().equals("SEARCH_KEYWORD")) {

			String keyword = (String) parameterMap.get("KEYWORD");
			String charSet = (String) parameterMap.get("CHAR_SET");
			String bankType = (String) parameterMap.get("BANK_TYPE");
			int day = Integer.parseInt((String) parameterMap.get("DAY"));
			boolean isUseDBWord = Boolean.parseBoolean((String) parameterMap
					.get("IS_USE_DB_WORD"));
			jArray=searchKeyword(keyword, charSet, isUseDBWord, day);
			
		} else if (this.getAct().equals("FIX_KEYWORD")) {
			jArray = fixKeyword();
		}
		// TODO Auto-generated method stub
		return jArray;
	}

}
