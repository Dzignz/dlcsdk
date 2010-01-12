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

import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.mogan.model.netAgent.NetAgent;
import com.mogan.sys.DBConn;
import com.mogan.sys.ProtoModel;
import com.mogan.sys.ServiceModelFace;
import com.mogan.sys.SysCalendar;

public class WebWalker extends ProtoModel implements ServiceModelFace {
	static NetAgent nAgent = new NetAgent();

	private JSONArray fixKeyword() {
		JSONArray jArray = new JSONArray();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		String sql = "SELECT * FROM search_keyword WHERE (keyword_tw IS NULL OR keyword_tw='') and (keyword_us IS NULL OR keyword_us='')";
		int count = conn.getQueryDataSize("mogan-tw", sql);
		int pageSize = 1000;
		int fixCount = 0;
		System.out.println("[INFO] fixKeyword-1:count="
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
			System.out.println("[INFO] fixKeyword-1:已處理筆數=" + i * pageSize
					+ ":" + fixCount);
			// break;
		}
		jArray.add(fixCount+"/"+count);
		
		// ////////////////將中文為英數的資料複制到英文
		sql = "SELECT * FROM search_keyword WHERE keyword_tw IS NOT NULL AND  keyword_tw  NOT LIKE ''  AND keyword_jp IS NOT NULL AND keyword_jp NOT LIKE '' AND (keyword_us IS NULL OR keyword_us='')";
		count = conn.getQueryDataSize("mogan-tw", sql);

		System.out.println("[INFO] fixKeyword-2:count="
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
				// 4558
				// System.out.println("[DEBUG] fixKeyword:("+j+")="+tempMap.get("id")+":"+tempMap.get("keyword_jp")+":"+tempMap.get("keyword_tw")+":"+
				// tempMap.get("keyword_us"));
			}
			System.out.println("[INFO] fixKeyword-2:已處理筆數=" + i * pageSize
					+ ":" + fixCount);
			// break;
		}
		jArray.add(fixCount+"/"+count);
		System.out.println("[DEBUG] fixKeyword:END");
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
	private JSONArray searchKeyword(String keyword, String charSet,
			boolean isUseDBWord, int day) throws Exception {
		if (isUseDBWord) {
			keyword = getDBWord(day);
		}
		return searchKeyword(keyword, charSet);
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
		JSONArray jArray = new JSONArray();

		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		try {
			Connection connection = conn.getConnection("mogan-tw");
			CallableStatement cs = connection
					.prepareCall("{?=call addkeyword(?,?,?)}");
			cs.registerOutParameter(1, Types.BIT);// 設定輸出子的資料狀態

			JSONObject jObj = new JSONObject();
			JSONArray newWordArray = new JSONArray();
			jObj.put("KEY_WORD", keyword);

			String encodeKeyword;
			System.out.println("[DEBUG]searchKeyword:" + keyword);
			encodeKeyword = URLEncoder.encode(keyword, charSet);// 進行資料編碼
			nAgent
					.getDataWithGet("http://search.auctions.yahoo.co.jp/jp/search/auc?p="
							+ encodeKeyword);
			String tempKeyword = "";
			int l = 0;

			NodeList nodes = nAgent.filterReferenceKeyword();
			cs.setString(2, keyword);
			cs.setString(3, keyword);
			cs.setString(4, "");
			cs.execute();// 預先執行，避免查尋結果沒有建議關鍵字造成LOOP

			for (int i = 0; i < nodes.size(); i++) {
				l++;
				Node n = nodes.elementAt(i);
				tempKeyword = n.toHtml();
				tempKeyword = tempKeyword.split("<nobr>")[1].split("</nobr>")[0];// 篩選掉不該出現的HTML符号
				// System.out.println(l + ") " + tempKeyword + " (" + keyword + ") "+checkChart(tempKeyword));

				cs.setString(2, keyword);
				cs.setString(3, tempKeyword);
				if (checkChart(tempKeyword)) {// 判斷是否含有全形字
					cs.setString(4, "EN");
				} else {
					cs.setString(4, "");
				}
				cs.execute();

				// System.out.println("cs.getInt(1)::"+cs.getResultSet().getRow());
				// System.out.println("cs.getInt(1)::" + cs.getInt(1));
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
	private String getDBWord(int day) {
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
						"mogan-tw",
						"SELECT keyword_jp FROM search_keyword WHERE id = (SELECT MIN(id) FROM search_keyword WHERE (last_date < STR_TO_DATE('"
								+ date
								+ "','%Y/%m/%d') or last_date is null) and keyword_jp is not null )");
		System.out
				.println("SELECT keyword_jp FROM search_keyword WHERE id = (SELECT MIN(id) FROM search_keyword WHERE last_date < STR_TO_DATE('"
						+ date + "','%Y/%m/%d') or last_date is null)");
		if (dataList.size() > 0) {
			Map tempMap = (Map) dataList.get(0);
			returnStr = (String) tempMap.get("keyword_jp");
		}
		return returnStr;
	}

	@Override
	public JSONArray doAction(Map parameterMap) throws Exception {
		JSONArray jArray = new JSONArray();

		if (this.getAct().equals("SEARCH_KEYWORD")) {

			String keyword = (String) parameterMap.get("KEYWORD");
			String charSet = (String) parameterMap.get("CHAR_SET");
			int day = Integer.parseInt((String) parameterMap.get("DAY"));
			boolean isUseDBWord = Boolean.parseBoolean((String) parameterMap
					.get("IS_USE_DB_WORD"));
			jArray = searchKeyword(keyword, charSet, isUseDBWord, day);

		} else if (this.getAct().equals("FIX_KEYWORD")) {
			jArray = fixKeyword();
		}
		// TODO Auto-generated method stub
		return jArray;
	}

}
