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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
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
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import org.apache.log4j.Logger;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

/**
 * Servlet implementation class InitDBConn
 */
public class DBConn extends HttpServlet {
	static private Logger logger = Logger.getLogger("com.mogan.sys.DBConn");
	private static final long serialVersionUID = 1L;
	private static ServletContext servletContext = null;
	private static int connCount = 0;
/*
	static class JsonValueProcessorImpl implements JsonValueProcessor {

		@Override
		public Object processArrayValue(Object arg0, JsonConfig jsonConfig) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object processObjectValue(String key, Object arg1,
				JsonConfig jsonConfig) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	*/
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
		Connection conn = null;
		try {
			Enumeration enr = this.getServletContext().getAttributeNames();
			String attName = null;
			while (enr.hasMoreElements()) {
				attName = enr.nextElement().toString();
				if (attName.startsWith("DB_Server_")) {
					conn = getConnection(attName.replaceAll("DB_Server_", ""));
					this.closeConnection(conn);
					logger.info(attName.replaceAll("DB_Server_", "")
							+ " Connection success.");
				}
			}
			conn.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		conn = null;
		DBConn dbConn = new DBConn();
		this.getServletContext().setAttribute("DBConn", dbConn);
		this.servletContext = this.getServletContext();
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
		ArrayList<Map> numList = query(connAlias, "SELECT getAutoNumber('"
				+ idName + "') as num");
		String autoNum = (String) numList.get(0).get("num");
		return autoNum;
	}

	/**
	 * 執行SQL語法
	 * 
	 * @param connAlias
	 *            連線名稱
	 * @param sql
	 *            SQL語法
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	public boolean executSql(String connAlias, String sql)
			throws UnsupportedEncodingException, SQLException {
		Connection conn = getConnection(connAlias);
		Statement stmt = null;
		ResultSet rst = null;
		boolean flag = false;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			sql = new String(sql.getBytes("UTF-8"), "UTF-8");
			flag = stmt.execute(sql);
			logger.debug(sql);
		} catch (SQLException e) {
			logger.error(sql, e);
			throw e;
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException", e);
			throw e;
		}
		closeResultSet(rst);
		closeStatement(stmt);
		closeConnection(conn);
		return flag;
	}

	/**
	 * 取得sql資筆數
	 * 
	 * @param connAlias
	 * @param sql
	 * @return
	 */
	public int getQueryDataSize(String connAlias, String sql) {
		// sql = "SELECT count(*) AS COUNT_SIZE FROM (" + sql + ") A";
		sql = sql.replaceAll("(?i)SELECT (.*?)(?i)FROM ", "SELECT COUNT(*) AS COUNT_SIZE FROM ");
		ArrayList dataList = query(connAlias, sql);
		return Integer.parseInt((String) ((Map) dataList.get(0)).get("COUNT_SIZE"));
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
		//JsonConfig cfg=new JsonConfig();
		//cfg.registerDefaultValueProcessor(Object, defaultValueProcessor);
		jArray.addAll(dataList);
		
		/*
		 * for (int i = 0; i < dataList.size(); i++) { // Map tempMap=(Map) dataList.get(i); jArray.add(JSONObject.fromObject(dataList.get(i))); }
		 */
		return jArray;
	}

	/**
	 * 使用map查詢
	 * 
	 * @param connAlias
	 *            - 連線名稱
	 * @param table
	 *            - table 名稱
	 * @param conditionMap
	 *            - 查詢條件
	 * @return
	 */
	public JSONArray queryJSONArray(String connAlias, String table,
			Map conditionMap) {
		JSONArray jArray = new JSONArray();
		ArrayList dataList = query(connAlias, table, conditionMap);
		jArray.addAll(dataList);
		/*
		 * for (int i = 0; i < dataList.size(); i++) { // Map tempMap=(Map) dataList.get(i); jArray.add(JSONObject.fromObject(dataList.get(i))); }
		 */
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
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	public JSONArray update(String connAlias, String table, Map conditionMap,
			Map dataMap) throws UnsupportedEncodingException, SQLException {
		JSONArray jArray = new JSONArray();
		// Connection conn = getConnection(connAlias);
		Map colStrctMap = queryTabelStructure(connAlias, table, dataMap);
		colStrctMap.putAll(queryTabelStructure(connAlias, table, conditionMap));

		String sql = "UPDATE " + table + " SET "
				+ getSqlStr(dataMap, colStrctMap) + " WHERE "
				+ getSqlWhereStr(conditionMap, colStrctMap);
		jArray.add(executSql(connAlias, sql));
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
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	public JSONArray newData(String connAlias, String table, Map conditionMap,
			Map dataMap) throws UnsupportedEncodingException, SQLException {
		JSONArray jArray = new JSONArray();

		if (conditionMap != null && conditionMap.size() > 0
				&& querySizeWithMap(connAlias, table, conditionMap) > 0) {
			update(connAlias, table, conditionMap, dataMap);
		} else {
			newData(connAlias, table, dataMap);
		}

		return jArray;
	}

	/**
	 * @param connAlias
	 * @param table
	 * @param dataMap
	 * @return
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	public JSONArray newData(String connAlias, String table,
			Map<String, String> dataMap) throws UnsupportedEncodingException,
			SQLException {
		JSONArray jArray = new JSONArray();

		Map colStrctMap = queryTabelStructure(connAlias, table, dataMap);
		StringBuffer columnStr = new StringBuffer();
		StringBuffer valueStr = new StringBuffer();
		Map<String, String> copyMap = (Map) ((HashMap) dataMap).clone();
		if (Boolean.getBoolean("DB_HAS_AUTO_NUM_TABLE")) {
			// 找出table使用的自動編碼，如果自動編碼欄位未給值則使用自動編碼
			String indexStr = "SELECT id_name,table_name,column_name FROM system_table_id_index WHERE table_name='"
					+ table + "'";
			ArrayList indexList = this.query(connAlias, indexStr);
			for (int i = 0; i < indexList.size(); i++) {
				Map indexMap = (Map) indexList.get(i);
				String columnName = (String) indexMap.get("column_name");
				String value = (String) indexMap.get("id_name");
				// 欄位
				if (columnStr.length() > 0) {
					columnStr.append(",");
				}
				columnStr.append(columnName);

				if (valueStr.length() > 0) {
					valueStr.append(",");
				}
				if (copyMap.containsKey(columnName)) {
					// 傳入的資料有指定值
					value = copyMap.get(columnName);
					copyMap.remove(columnName);
					valueStr.append(" '" + value + "'");
				} else {
					// 傳入資料未指定值，使用自動編號
					valueStr.append(" getAutoNumber('" + value + "')");
				}
			}
		}
		// 設定其他欄位內容
		Iterator it = copyMap.keySet().iterator();
		for (; it.hasNext();) {
			String columnName = (String) it.next();
			if (copyMap.get(columnName) == null) {
				continue;
			}
			if (columnStr.length() > 0) {
				columnStr.append(",");
			}
			columnStr.append(columnName);

			if (valueStr.length() > 0) {
				valueStr.append(",");
			}
			valueStr.append("'" + fixSqlValue(copyMap.get(columnName)) + "'");
		}

		// 組合SQL字串
		String insertSql = "INSERT " + table + " ( " + columnStr
				+ " ) values (" + valueStr + ")";
		this.executSql(connAlias, insertSql);
		return jArray;
	}

	/**
	 * 修正特殊字元
	 * 
	 * @param value
	 * @return
	 */
	private Object fixSqlValue(Object value) {
		if (value == null) {
			return value;
		}
		if (value instanceof String) {
			value = ((String) value).replaceAll("'", "''");
		}
		if (value instanceof Date) {
			SysCalendar calendar = new SysCalendar();
			value = SysCalendar.getFormatDate((Date) value, SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql);
		}
		return value;
	}

	/**
	 * @param connAlias
	 * @param table
	 * @param conditionMap
	 * @return
	 */
	public ArrayList queryWithMap(String connAlias, String table,
			Map conditionMap) {
		String sql = "SELECT * FROM " + table;
		StringBuffer whereStr = new StringBuffer();
		Map colStrctMap = queryTabelStructure(connAlias, table, conditionMap);

		if (conditionMap.size() > 0) {
			sql += " WHERE " + getSqlWhereStr(conditionMap, colStrctMap);
		}
		return query(connAlias, sql);
	}

	/**
	 * 使用Map 查詢
	 * 
	 * @param connAlias
	 * @param table
	 * @param conditionMap
	 * @return
	 */
	public ArrayList query(String connAlias, String table, Map conditionMap) {
		String sql = "SELECT * FROM " + table;
		StringBuffer whereStr = new StringBuffer();
		Map colStrctMap = queryTabelStructure(connAlias, table, conditionMap);

		if (conditionMap.size() > 0) {
			sql += " WHERE " + getSqlWhereStr(conditionMap, colStrctMap);
		}
		return query(connAlias, sql);
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
		String sql = "SELECT * FROM " + table;
		StringBuffer whereStr = new StringBuffer();
		Map colStrctMap = queryTabelStructure(connAlias, table, conditionMap);

		if (conditionMap.size() > 0) {
			sql += " WHERE " + getSqlWhereStr(conditionMap, colStrctMap);
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
	 * @param jData
	 * @param colStrctMap
	 * @return
	 */
	public String getSqlWhereStr(Map jData, Map colStrctMap) {
		StringBuffer dataBuffer = new StringBuffer();
		Iterator itData = jData.keySet().iterator();
		for (; itData.hasNext();) {
			String colName = (String) itData.next();
			String newData = "";
			Map colMap = (Map) colStrctMap.get(colName);

			if (colStrctMap.get(colName) == null) {
				/** 如果沒有這個欄位就pass */
				continue;
			}
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
				if (jData.get(colName) instanceof String) {
					try {
						SysCalendar.getFormatDate((String) jData.get(colName), SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql);
						newData = colName
								+ "='"
								+ SysCalendar.getFormatDate((String) jData.get(colName), SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql)
								+ "'";
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else if (jData.get(colName) instanceof Date) {
					newData = colName
							+ "='"
							+ calendar.getFormatDate((Date) jData.get(colName), SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql)
							+ "'";
				}
				break;
			default:
				newData = colName + "='" + jData.get(colName) + "'";
			}
			if (dataBuffer.length() > 0) {
				dataBuffer.append(" AND ");
			}
			dataBuffer.append(newData);
		}
		return dataBuffer.toString();
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

		SysCalendar calendar = new SysCalendar();
		int i = 0;
		for (; itData.hasNext();) {
			String colName = (String) itData.next();
			String newData = "";
			Map colMap = (Map) colStrctMap.get(colName);

			if (colStrctMap.get(colName) == null) {
				/** 如果沒有這個欄位就pass */
				continue;
			}
			switch (((Integer) colMap.get("columnType")).intValue()) {
			case -1: // LONGVARCHAR
			case 1: // CHAR
			case 12: // VARCHAR
				if (jData.get(colName) != null)
					newData = colName + "='" + jData.get(colName) + "'";
				break;
			case 8: // DOUBLE
			case 7: // FLOAT
			case 6: // FLOAT
			case 5: // SMALLINT
			case 4: // INTEGER
			case 2: // NUMERIC
			case -5: // BIGINT
			case -6: // TINYINT
				if (jData.get(colName) != null)
					newData = colName + "=" + jData.get(colName) + "";
				break;
			case 91: // DATE
				if (jData.get(colName) != null)
					if (jData.get(colName) instanceof String) {
						try {
							SysCalendar.getFormatDate((String) jData.get(colName), SysCalendar.yyyy_MM_dd_Mysql);
							newData = colName
									+ "='"
									+ SysCalendar.getFormatDate((String) jData.get(colName), SysCalendar.yyyy_MM_dd_Mysql)
									+ "'";
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else if (jData.get(colName) instanceof Date) {
						newData = colName
								+ "='"
								+ calendar.getFormatDate((Date) jData.get(colName), SysCalendar.yyyy_MM_dd_Mysql)
								+ "'";
					}
				break;
			case 92: // TIME
			case 93: // TIMESTAMP
				if (jData.get(colName) != null)
					if (jData.get(colName) instanceof String) {
						try {
							SysCalendar.getFormatDate((String) jData.get(colName), SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql);
							newData = colName
									+ "='"
									+ SysCalendar.getFormatDate((String) jData.get(colName), SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql)
									+ "'";
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else if (jData.get(colName) instanceof Date) {
						newData = colName
								+ "='"
								+ calendar.getFormatDate((Date) jData.get(colName), SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql)
								+ "'";
					}

				break;
			default:

				if (jData.get(colName) != null)
					newData = colName + "='" + jData.get(colName) + "'";
			}
			if (newData.length() > 0) {

				if (dataBuffer.length() > 0) {
					dataBuffer.append(",");
				}
				dataBuffer.append(newData);
			}

		}

		return dataBuffer.toString();
	}

	/**
	 * 回傳欄位結構資料
	 * 
	 * @param connAlias
	 * @param tableName
	 * @return
	 */
	public Map queryTabelStructure(String connAlias, String tableName) {
		Map colStrctMap = new HashMap();

		Connection conn = getConnection(connAlias);
		Statement stmt = null;
		ResultSet rst = null;

		String sql = "SELECT * FROM " + tableName + " WHERE 1=2";
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rst = stmt.executeQuery(sql);
			ResultSetMetaData rsMetaData = rst.getMetaData();
			int numberOfColumns = rsMetaData.getColumnCount();
			for (int i = 1; i < numberOfColumns + 1; i++) {
				Map tempMap = new HashMap();
				tempMap.put("columnName", rsMetaData.getColumnLabel(i));
				tempMap.put("tableName", rsMetaData.getTableName(i));
				tempMap.put("columnTypeName", rsMetaData.getColumnTypeName(i));
				tempMap.put("columnType", Integer.valueOf(rsMetaData.getColumnType(i)));
				colStrctMap.put(rsMetaData.getColumnLabel(i), tempMap);
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
		if (columnMap == null || columnMap.size() == 0) {
			return colStrctMap;
		}
		Connection conn = getConnection(connAlias);
		Statement stmt = null;
		ResultSet rst = null;

		String sql = "SELECT * FROM " + tableName + " WHERE 1=2";
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rst = stmt.executeQuery(sql);
			ResultSetMetaData rsMetaData = rst.getMetaData();
			int numberOfColumns = rsMetaData.getColumnCount();
			for (int i = 1; i < numberOfColumns + 1; i++) {
				Map tempMap = new HashMap();
				tempMap.put("columnName", rsMetaData.getColumnLabel(i));
				tempMap.put("tableName", rsMetaData.getTableName(i));
				tempMap.put("columnTypeName", rsMetaData.getColumnTypeName(i));
				tempMap.put("columnType", Integer.valueOf(rsMetaData.getColumnType(i)));
				colStrctMap.put(rsMetaData.getColumnLabel(i), tempMap);
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
		logger.debug("SQL 語法:" + sql);
		Connection conn = getConnection(connAlias);
		Statement stmt = null;
		ResultSet rst = null;
		ArrayList<Map> dataList = new ArrayList<Map>();
		ArrayList<Map> colList = new ArrayList<Map>();
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rst = stmt.executeQuery(sql);
			ResultSetMetaData rsMetaData = rst.getMetaData();
			int numberOfColumns = rsMetaData.getColumnCount();
			for (int i = 1; i < numberOfColumns + 1; i++) {
				Map tempMap = new HashMap();
				tempMap.put("columnName", rsMetaData.getColumnLabel(i));
				tempMap.put("tableName", rsMetaData.getTableName(i));
				tempMap.put("columnTypeName", rsMetaData.getColumnTypeName(i));
				tempMap.put("columnType", Integer.valueOf(rsMetaData.getColumnType(i)));
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
						rowMap.put(columnName, SysCalendar.getFormatDate(rst.getTimestamp(columnName), SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql));
						break;
					default:
						if (rst.getObject(columnName)!=null){
							rowMap.put(columnName, rst.getString(columnName));
						}
					}

				}
				dataList.add(rowMap);
			}// */
			// conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("SQL 語法錯誤:" + sql, e);
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

		if (((String) this.servletContext.getAttribute("DB_Server_" + connAlias)).startsWith("MYSQL")) {
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
			logger.error("[錯誤] 關閉數據庫連接發生異常！");
			logger.error(e.getMessage(),e);
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
			logger.error("[錯誤] 關閉Statement發生異常！");
			logger.error(e.getMessage(),e);
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
		} catch (Exception e) {
			logger.error("[錯誤] 關閉ResultSet發生異常！");
			logger.error(e.getMessage(),e);
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
