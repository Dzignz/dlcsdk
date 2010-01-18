package com.mogan.sys;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

/**
 * Servlet implementation class InitDBConn
 */
public class DBConn extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static ServletContext servletContext = null;
	private static int connCount = 0;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DBConn() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		initDBConn();
	}

	private void initDBConn() {
		try {
			Connection conn = null;
			conn = getConnection("mogan-tw");
			this.closeConnection(conn);
			conn = getConnection("mogan-DB");
			this.closeConnection(conn);
			if (conn != null)
				System.out.println("[Info] Connect Test Success");
			conn.close();
			conn = null;
			DBConn dbConn = new DBConn();
			this.getServletContext().setAttribute("DBConn", dbConn);
			this.servletContext = this.getServletContext();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param fromat
	 *            -已經處理過的字串
	 * @see SimpleDateFormat
	 */
	public String getDate(String fromat) {
		Calendar rightNow = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat(fromat);
		return dateFormat.format(rightNow.getTime());
	}

	synchronized public String getAutoNumber(String connAlias, String idName) {
		ArrayList<Map> numList=query("mogan-DB", "SELECT getAutoNumber('SA-SEQ-01') as num");
		String autoNum=(String) numList.get(0).get("num");
		return autoNum;
	}
	
	/*
	synchronized public String getAutoNumber(String connAlias, String idName) {
		ArrayList dataList = query(connAlias,
				"SELECT * FROM sys_table_id_index where id_name='" + idName
						+ "'");

		if (dataList.size() > 0) {
			Map rowMap = (Map) dataList.get(0);
			String lastValue = (String) rowMap.get("last_value");
			String frontRule = (String) rowMap.get("front_rule");
			String indexRule = (String) rowMap.get("index_rule");
			String newId = frontRule.substring(0, frontRule.lastIndexOf("-"));
			newId += "-"
					+ getDate(frontRule
							.substring(frontRule.lastIndexOf("-") + 1));
			java.text.DecimalFormat df = new java.text.DecimalFormat(indexRule);
			if (lastValue == null)
				lastValue = "";
			if (lastValue.startsWith(newId)) {
				newId += "-"
						+ df
								.format(Integer
										.valueOf(lastValue.substring(lastValue
												.lastIndexOf("-") + 1)) + 1);
			} else {
				newId += "-" + df.format(Integer.valueOf(indexRule) + 1);
			}
			this.executSql(connAlias,
					"UPDATE sys_table_id_index SET last_value='" + newId
							+ "' WHERE id_name='" + idName + "'");
			return newId;
		}
		return String.valueOf(System.currentTimeMillis());
	}*/

	/**
	 * 執行SQL語法
	 * 
	 * @param connAlias
	 *            連線名稱
	 * @param sql
	 *            SQL語法
	 */
	public boolean executSql(String connAlias, String sql) {
		Connection conn = getConnection(connAlias);
		Statement stmt = null;
		ResultSet rst = null;
		boolean flag = false;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			sql = new String(sql.getBytes("UTF-8"), "UTF-8");

			flag = stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println("[ERR] SQLException:"+sql);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeResultSet(rst);
		closeStatement(stmt);
		closeConnection(conn);
		return flag;
	}

	public int getQueryDataSize(String connAlias, String sql) {
		sql = "select count(*) as COUNT from (" + sql + ") A";
		ArrayList dataList = query(connAlias, sql);
		return Integer.parseInt((String) ((Map) dataList.get(0)).get("COUNT"));
	}

	/**
	 * 回傳JSONArray
	 * 
	 * @param connAlias
	 *            連線代號
	 * @param sql
	 *            SQL指令
	 */
	public JSONArray queryJSONArray(String connAlias, String sql) {
		JSONArray jArray = new JSONArray();
		ArrayList dataList = query(connAlias, sql);
		for (int i = 0; i < dataList.size(); i++) {
			// Map tempMap=(Map) dataList.get(i);
			jArray.add(JSONObject.fromObject(dataList.get(i)));
		}
		return jArray;
	}

	/**
	 * 更新資料庫
	 * 
	 * @param connAlias
	 *            連線代號
	 * @param table
	 *            目標table名稱
	 * @param conditionMap
	 *            條件
	 * @param dataMap
	 *            更新資料
	 * @return
	 */
	public JSONArray update(String connAlias, String table, Map conditionMap,
			Map dataMap) {
		JSONArray jArray = new JSONArray();
		// Connection conn = getConnection(connAlias);
		Map colStrctMap = queryTabelStructure(connAlias, table, dataMap);
		colStrctMap.putAll(queryTabelStructure(connAlias, table, conditionMap));

		String sql = "UPDATE " + table + " SET "
				+ getSqlStr(dataMap, colStrctMap) + " WHERE "
				+ getSqlStr(conditionMap, colStrctMap);
		executSql(connAlias, sql);
		return jArray;
	}

	/**
	 * 建立新資料，當conditionMap已經有資料存在，則不新增資料只修改資料
	 * 
	 * @param connAlias
	 * @param table
	 * @param conditionMap
	 * @param dataMap
	 * @return
	 */
	public JSONArray newData(String connAlias, String table, Map conditionMap,
			Map dataMap) {
		JSONArray jArray = new JSONArray();

		if (conditionMap!=null && conditionMap.size()>0 && querySizeWithMap(connAlias, table, conditionMap) > 0) {
			update(connAlias, table, conditionMap, dataMap);
		} else {
			newData(connAlias,table,dataMap);
		}

		return jArray;
	}
	
	/**
	 * 
	 * @param connAlias
	 * @param table
	 * @param dataMap
	 * @return
	 */
	public JSONArray newData(String connAlias, String table,Map<String,String> dataMap) {
		JSONArray jArray = new JSONArray();

		Map colStrctMap = queryTabelStructure(connAlias, table, dataMap);
		StringBuffer columnStr = new StringBuffer();
		StringBuffer valueStr = new StringBuffer();

		// 找出table使用的自動編碼，如果自動編碼欄位未給值則使用自動編碼
		String indexStr = "SELECT id_name,table_name,column_name FROM system_table_id_index WHERE table_name='"
				+ table + "'";
		ArrayList indexList = this.query(connAlias, indexStr);
		for (int i = 0; i < indexList.size(); i++) {
			Map indexMap = (Map) indexList.get(i);
			String columnName = (String) indexMap.get("column_name");
			String value = (String) indexMap.get("id_name");
			//欄位
			if (columnStr.length() > 0) {
				columnStr.append(",");
			}			
			columnStr.append(columnName);
			
			
			if (valueStr.length() > 0) {
				valueStr.append(",");
			}
			if (dataMap.containsKey(columnName)) {
				//傳入的資料有指定值
				value= dataMap.get(columnName);
				dataMap.remove(columnName);
				valueStr.append(" '" + value + "'");	
			}else{
				//傳入資料未指定值，使用自動編號
				valueStr.append(" getAutoNumber('" + value + "')");	
			}

		}
		
		//設定其他欄位內容
		Iterator it = dataMap.keySet().iterator();
		for (; it.hasNext();) {
			String columnName = (String) it.next();
			if (dataMap.get(columnName)==null){
				continue;
			}
			if (columnStr.length() > 0) {
				columnStr.append(",");
			}
			columnStr.append(columnName);

			if (valueStr.length() > 0) {
				valueStr.append(",");
			}
			valueStr.append("'" + fixSqlValue(dataMap.get(columnName)) + "'");
		}
		
		//組合SQL字串
		String insertSql = "INSERT " + table +" ( "+ columnStr+" ) values ("+valueStr+")";
		this.executSql(connAlias,insertSql);
		return jArray;
	}
	
	/**
	 * 修正特殊字元
	 * @param value
	 * @return
	 */
	private Object fixSqlValue(Object value){
		if (value==null){
			return value;
		}
		if (value instanceof String){
			value=((String)value).replaceAll("'", "''");	
		}
		if (value instanceof Date){
			SysCalendar calendar = new SysCalendar();
			value= SysCalendar.getFormatDate((Date) value,
					SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql);
		}
		return value;
	}

	/**
	 * 
	 * @param connAlias
	 * @param table
	 * @param conditionMap
	 * @return
	 */
	public ArrayList queryWithMap(String connAlias, String table, Map conditionMap) {
		String sql = "SELECT * FROM " + table;
		StringBuffer whereStr = new StringBuffer();
		Map colStrctMap = queryTabelStructure(connAlias, table, conditionMap);

		if (conditionMap.size() > 0) {
			sql += " WHERE " + getSqlStr(conditionMap, colStrctMap);
		}
		return query(connAlias,sql);
	}
	
	
	/**
	 * 以conditionMap查詢目前資料筆數
	 * 
	 * @param connAlias
	 * @param table
	 * @param conditionMap
	 *            key=欄位，value=值
	 * @return
	 */
	public int querySizeWithMap(String connAlias, String table, Map conditionMap) {
		String sql = "SELECT COUNT(*) AS COUNT FROM " + table;
		StringBuffer whereStr = new StringBuffer();
		Map colStrctMap = queryTabelStructure(connAlias, table, conditionMap);

		if (conditionMap.size() > 0) {
			sql += " WHERE " + getSqlStr(conditionMap, colStrctMap);
		}
		return getQueryDataSize(connAlias, sql);
	}

	/**
	 * 分頁查詢
	 * 
	 * @param connAlias
	 *            連線代號
	 * @param sql
	 *            SQL指令
	 */
	public JSONArray queryJSONArrayWithPage(String connAlias, String sql,
			int startIndex, int pageSize) {
		String pageSql = fixPageSql(connAlias, sql, startIndex, pageSize);
		return queryJSONArray(connAlias, pageSql);
	}

	/**
	 * TODO 時間格式的欄位尚未完全測試完畢 組合出符合欄位結構的SQL語法
	 * 
	 * @param jData
	 *            - 欄位名稱,值
	 * @param colStrctMap
	 *            - 欄位結構
	 * @return
	 */
	public String getSqlStr(Map jData, Map colStrctMap) {
		StringBuffer dataBuffer = new StringBuffer();
		Iterator itData = jData.keySet().iterator();
		for (; itData.hasNext();) {
			String colName = (String) itData.next();
			String newData = "";
			Map colMap = (Map) colStrctMap.get(colName);
			switch (((Integer) colMap.get("columnType")).intValue()) {
			case -1: // LONGVARCHAR
			case 1: // CHAR
			case 12: // VARCHAR
				newData = colName + "='" + jData.get(colName) + "'";
				break;
			case 8: // DOUBLE
			case 6: // FLOAT
			case 5: // SMALLINT
			case 4: // INTEGER
			case 2: // NUMERIC
			case -5: // BIGINT
			case -6: // TINYINT
				newData = colName + "=" + jData.get(colName) + "";
				break;
			case 91: // DATE
			case 92: // TIME
			case 93: // TIMESTAMP
				SysCalendar calendar = new SysCalendar();
				newData = colName
						+ "='"
						+ calendar.getFormatDate((Date) jData.get(colName),
								SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql) + "'";
				break;
			default:
				newData = colName + "='" + jData.get(colName) + "'";
			}
			if (dataBuffer.length() > 0) {
				dataBuffer.append(",");
			}
			dataBuffer.append(newData);
		}
		return dataBuffer.toString();
	}

	/**
	 * 回傳欄位結構資料
	 * 
	 * @param connAlias
	 * @param tableName
	 * @param jColumn
	 * @return
	 */
	public Map queryTabelStructure(String connAlias, String tableName,
			Map columnMap) {
		Map colStrctMap = new HashMap();
		if (columnMap==null || columnMap.size()==0){
			return colStrctMap;
		}
		Connection conn = getConnection(connAlias);
		Statement stmt = null;
		ResultSet rst = null;

		
		String sql = "SELECT ";

		Iterator it = columnMap.keySet().iterator();

		String columnName = null;
		for (; it.hasNext();) {
			if (columnName != null) {
				sql += " , ";
			}
			columnName = (String) it.next();
			sql += columnName;
		}
		sql += " FROM " + tableName + " WHERE 1=2";
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rst = stmt.executeQuery(sql);
			ResultSetMetaData rsMetaData = rst.getMetaData();
			int numberOfColumns = rsMetaData.getColumnCount();
			for (int i = 1; i < numberOfColumns + 1; i++) {
				Map tempMap = new HashMap();
				tempMap.put("columnName", rsMetaData.getColumnName(i));
				tempMap.put("tableName", rsMetaData.getTableName(i));
				tempMap.put("columnTypeName", rsMetaData.getColumnTypeName(i));
				tempMap.put("columnType", Integer.valueOf(rsMetaData
						.getColumnType(i)));
				colStrctMap.put(rsMetaData.getColumnName(i), tempMap);
				// 記錄欄位型態
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeResultSet(rst);
			closeStatement(stmt);
			closeConnection(conn);
		}
		conn = null;
		stmt = null;

		return colStrctMap;
	}

	/**
	 * 分頁查詢
	 * 
	 * @param connAlias
	 *            連線代號
	 * @param sql
	 *            SQL指令
	 */
	public ArrayList<Map> queryWithPage(String connAlias, String sql,
			int startIndex, int pageSize) {
		// DriverManager MYSQL_5
		String pageSql = fixPageSql(connAlias, sql, startIndex, pageSize);
		return query(connAlias, pageSql);
	}

	/**
	 * 回傳ArrayList
	 * 
	 * @param connAlias
	 *            連線代號
	 * @param sql
	 *            SQL指令
	 */
	public ArrayList<Map> query(String connAlias, String sql) {
		Connection conn = getConnection(connAlias);
		Statement stmt = null;
		ResultSet rst = null;
		ArrayList<Map> dataList = new ArrayList<Map>();
		ArrayList<Map> colList = new ArrayList<Map>();
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rst = stmt.executeQuery(sql);
			ResultSetMetaData rsMetaData = rst.getMetaData();
			int numberOfColumns = rsMetaData.getColumnCount();
			for (int i = 1; i < numberOfColumns + 1; i++) {
				Map tempMap = new HashMap();
				tempMap.put("columnName", rsMetaData.getColumnLabel(i));
				tempMap.put("tableName", rsMetaData.getTableName(i));
				tempMap.put("columnTypeName", rsMetaData.getColumnTypeName(i));
				tempMap.put("columnType", Integer.valueOf(rsMetaData
						.getColumnType(i)));
				colList.add(tempMap);
				// 記錄欄位型態
			}
			// *
			while (rst.next()) {
				Map rowMap = new HashMap();
				for (int i = 0; i < colList.size(); i++) {
					Map clo = (Map) colList.get(i);
					String columnName = (String) clo.get("columnName");
					switch (((Integer) clo.get("columnType")).intValue()) {
					case 93:
						// rowMap.put(columnName, rst.getDate(columnName));
						rowMap.put(columnName, rst.getString(columnName));
						break;
					default:
						rowMap.put(columnName, rst.getString(columnName));
					}

				}
				dataList.add(rowMap);
			}// */
			// conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("[ERR] SQL 語法錯誤:" + sql);
			e.printStackTrace();
		} finally {
			closeResultSet(rst);
			closeStatement(stmt);
			closeConnection(conn);
		}
		conn = null;
		stmt = null;
		return dataList;
	}

	/**
	 * 依據不同的DB Server做不同的語法調整,目前提供MYSQL跟ORACEL版本 </br> -2009/10/21
	 * 
	 * @param connAlias
	 * @param sql
	 * @param startIndex
	 * @param pageSize
	 * @return
	 */
	private String fixPageSql(String connAlias, String sql, int startIndex,
			int pageSize) {
		String pageSql = "";

		if (((String) this.servletContext
				.getAttribute("DB_Server_" + connAlias)).startsWith("MYSQL")) {
			pageSql = sql + " LIMIT " + startIndex + "," + (pageSize);
		} else if (((String) this.servletContext.getAttribute("DB_Server_"
				+ connAlias)).startsWith("ORACLE")) {
			pageSql = " SELECT * FROM (SELECT A.*, rownum r FROM (" + sql
					+ ") A WHERE rownum <= " + (startIndex + pageSize)
					+ ") B  WHERE r > " + startIndex;
		} else {
			pageSql = sql;
		}
		return pageSql;
	}

	public Connection getConnection(String connAlias) {

		Connection conn = null;
		try {
			conn = DriverManager.getConnection("proxool." + connAlias);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 關閉Connection對象
	 */
	public static void closeConnection(Connection conn) {
		try {
			if ((conn != null) && (!conn.isClosed())) {
				conn.close();
			}
		} catch (SQLException e) {
			System.out.println("[錯誤] 關閉數據庫連接發生異常！");
		}
	}

	/**
	 * 關閉PreparedStatement對象
	 */
	public static void closeStatement(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			System.out.println("[錯誤] 關閉Statement發生異常！");
		}
	}

	/**
	 * 關閉ResultSet對象
	 */
	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception ex) {
			System.out.println("[錯誤] 關閉ResultSet發生異常！");
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// super.doPost(request, response);
	}

}
