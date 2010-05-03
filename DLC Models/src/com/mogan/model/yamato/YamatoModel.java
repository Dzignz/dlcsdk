package com.mogan.model.yamato;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.mogan.exception.yamoto.YamatoException;
import com.mogan.sys.DBConn;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;

/**
 * YAMATO_MODEL
 * @author Dian
 *
 */
public class YamatoModel extends ProtoModel implements ServiceModelFace {
	private String yamotoId = "SM-20091223-09";

	@Override
	public JSONArray doAction(Map parameterMap) throws Exception {
		JSONArray jArray = new JSONArray();
		if (this.getAct().equals("SEARCH_ITEM")) {
			// TODO 取得清單
			jArray = searchItem(parameterMap.get("PAGE"), parameterMap
					.get("PAGE_SIZE"), parameterMap.get("KEY_WORD"));
		} else if (this.getAct().equals("CHECK_ITEM")) {
			// TODO 確認商品
			jArray = searchPackageItem(parameterMap.get("ORDER_ID"));
		} else if (this.getAct().equals("KEYIN_ITEM")) {
			// TODO 輸入商品資訊
			jArray = keyInItem(parameterMap.get("ORDER_IDS"), parameterMap
					.get("LENGTH"), parameterMap.get("WIDTH"), parameterMap
					.get("HEIGHT"), parameterMap.get("WEIGHT"), parameterMap
					.get("YAMATO_NO"));
		} else if (this.getAct().equals("HistoryAction")) {
			// TODO 歷史紀錄
		} else {
			throw new Exception("Action (" + this.getAct() + ") not find.");
		}

		return jArray;
	}

	/**
	 * 輸入包裹資料，
	 * @param itemOrderIdS 商品ID 以JSON型態傳入["IO-001","IO-002","IO-005"]
	 * @param itemLength 商品長
	 * @param itemWidth 商品寬
	 * @param itemHeight 商品高
	 * @param itemWeight 商品重
	 * @param yamatoNo yamato NO，請自已產生
	 * @return 建立結果 Result 1=成功，0=失敗
	 * @throws YamatoException
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 */
	private JSONArray keyInItem(Object itemOrderIdS, Object itemLength,
			Object itemWidth, Object itemHeight, Object itemWeight,
			Object yamatoNo) throws YamatoException, UnsupportedEncodingException, SQLException {
		JSONArray jArray = new JSONArray();
		JSONObject jObj = new JSONObject();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");

		// 先將舊資料清空
		JSONArray ioIdArray = JSONArray.fromObject(itemOrderIdS);
		String ioIds = "";
		while (ioIdArray.size() > 0) {
			ioIds += "'" + ioIdArray.getString(0) + "'";
			ioIdArray.remove(0);
			if (ioIdArray.size() > 0) {
				ioIds += ",";
			}
		}
		String tradeIdSql = "SELECT tied_id FROM item_order WHERE item_order_id in ("
				+ ioIds + ") group by tied_id";
		ArrayList<Map> tradeIds = conn.query("mogan-DB", tradeIdSql);
		
		String result="0";	//執行結果
		
		jObj.put("UserName", "");
		jObj.put("Address", "");
		jObj.put("ZIP", "");
		jObj.put("Tel", "");
		
		if (tradeIds.get(0).get("tied_id") == null || tradeIds.size() > 1) {
			throw new YamatoException("order ID Error.");
		} else {

			String tradeOrderId = (String) tradeIds.get(0).get("tied_id");
			String packageId = "";
			ArrayList<Map> packageList = conn.query("mogan-DB",
					"SELECT package_id from item_package where p_varchar01='"
							+ yamatoNo + "'");

			if (packageList.size() > 0
					&& packageList.get(0).get("package_id") != null) {
				packageId = (String) packageList.get(0).get("package_id");
				String clearSql = "UPDATE item_order SET package_id = null WHERE package_id = '"
						+ packageId + "'";
				conn.executSql("mogan-DB", clearSql);
				clearSql = null;
			} else {
				packageId = conn.getAutoNumber("mogan-DB", "IP-ID-01");
			}

			Map conditionMap = new HashMap();
			Map dataMap = new HashMap();
			conditionMap.put("p_varchar01", yamatoNo);
			dataMap.put("package_id", packageId);
			dataMap.put("package_length", "0");
			dataMap.put("package_width", "0");
			dataMap.put("package_height", "0");
			dataMap.put("package_kg", "0");
			dataMap.put("create_date", new Date());
			dataMap.put("p_varchar01", yamatoNo);
			dataMap.put("trade_order_id", tradeOrderId);
			dataMap.put("creator", yamotoId);

			// TODO 檢查資料合理性
			conn.newData("mogan-DB", "item_package", conditionMap, dataMap);

			// TODO 更新同捆商品

			dataMap = new HashMap();
			dataMap.put("package_id", packageId);
			while (ioIdArray.size() > 0) {
				conditionMap = new HashMap();
				conditionMap.put("item_order_id", ioIdArray.getString(0));
				conn.update("mogan-DB", "item_order", conditionMap, dataMap);
				ioIdArray.remove(0);
			}

			String memberData = "SELECT member_id FROM item_order WHERE item_order_id in ("
					+ ioIds + ") GROUP BY member_id";
			ArrayList<Map> memberList = conn.query("mogan-DB", memberData);
			memberData = "SELECT tel AS Tel,postcode AS ZIP,address AS Address,concat(first_name,last_name) AS UserName FROM member_data WHERE member_id='"
					+ memberList.get(0).get("member_id") + "'";
			jObj.putAll(conn.queryJSONArray("mogan-DB", memberData).getJSONObject(0));
			conn.queryJSONArray("mogan-DB", memberData).getJSONObject(0);
			result="1";
		}
		jObj.put("Result", result);
		jArray.add(jObj);
		return jArray;
	}

	private JSONArray checkItem(Object itemId) {
		JSONArray jArray = new JSONArray();

		return jArray;
	}

	private JSONArray searchPackage(Object page, Object size, Object keyWord) {
		JSONArray jArray = new JSONArray();

		return jArray;
	}

	private JSONArray searchPackageItem(Object itemOrderId) {
		JSONArray jArray = new JSONArray();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");
		JSONObject jObj = new JSONObject();
		String sql = "SELECT view_item_order_v2.item_name AS ItemName, "
				+ "view_item_order_v2.item_id AS ItemId, "
				+ "view_item_order_v2.main_image AS ImageUrl, "
				+ "view_item_order_v2.seller_id AS SellerId, "
				+ "view_item_order_v2.item_url AS ItemUrl, "
				+ "view_item_order_v2.member_id AS MemberId, "
				+ "view_item_order_v2.buy_unit AS Quantity, "
				+ "view_item_order_v2.website_name AS WebSiteName,"
				+ "view_item_order_v2.seller_account AS SellerName, "
				+ "view_item_order_v2.item_order_id AS OrderId, "
				+ "view_item_order_v2.full_name AS UserName, "
				+ "view_item_order_v2.order_status AS OrderStatus "
				+ "FROM view_item_order_v2 "
				+ "WHERE view_item_order_v2.delete_flag =1 AND view_item_order_v2.order_status LIKE '3-04' AND view_item_order_v2.first_tied=(SELECT first_tied FROM item_order WHERE item_order_id ='"
				+ itemOrderId + "')";

		jObj.put("DATA", conn.queryJSONArray("mogan-DB", sql));
		jObj.put("TOTAL_SIZE", conn.getQueryDataSize("mogan-DB", sql));
		jArray.add(jObj);
		return jArray;
	}

	/**
	 * 搜尋商品資料
	 * 
	 * @param page
	 *            頁數
	 * @param size
	 *            每頁筆數
	 * @param keyWord
	 *            搜尋關鍵字
	 * @return JSONArray型態資料
	 */
	private JSONArray searchItem(Object page, Object size, Object keyWord) {
		int pageIndex;
		int pageSize;

		if (page == null || Integer.valueOf((String) page) < 1) {
			pageIndex = 1;
		} else {
			pageIndex = Integer.parseInt((String) page);
		}

		if (size == null || Integer.valueOf((String) size) < 1) {
			pageSize = 30;
		} else if (Integer.valueOf((String) size) > 500) {
			pageSize = 500;
		} else {
			pageSize = Integer.parseInt((String) size);
		}

		JSONArray jArray = new JSONArray();

		JSONObject jObj = new JSONObject();
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
				"DBConn");

		// 可查出所有已匯款商品
		String sql = "SELECT view_item_order_v2.item_name AS ItemName, "
				+ "view_item_order_v2.item_id AS ItemId, "
				+ "view_item_order_v2.main_image AS ImageUrl, "
				+ "view_item_order_v2.seller_id AS SellerId, "
				+ "view_item_order_v2.item_url AS ItemUrl, "
				+ "view_item_order_v2.member_id AS MemberId, "
				+ "view_item_order_v2.buy_unit AS Quantity, "
				+ "view_item_order_v2.website_name AS WebSiteName,"
				+ "view_item_order_v2.seller_account AS SellerName, "
				+ "view_item_order_v2.item_order_id AS OrderId, "
				+ "view_item_order_v2.full_name AS UserName, "
				+ "view_item_order_v2.order_status AS OrderStatus "
				+ "FROM view_item_order_v2 "
				+ "WHERE view_item_order_v2.delete_flag =1 AND view_item_order_v2.order_status LIKE '3-04' ";
		if (keyWord == null || ((String) keyWord).length() == 0) {

		} else {
			sql += " AND (view_item_order_v2.item_name LIKE '%" + keyWord
					+ "%' " + "OR view_item_order_v2.item_id LIKE '%" + keyWord
					+ "%' " + "OR view_item_order_v2.seller_account LIKE '%"
					+ keyWord + "%' "
					+ "OR view_item_order_v2.full_name LIKE '%" + keyWord
					+ "%' " + "OR view_item_order_v2.item_order_id LIKE '%"
					+ keyWord + "%')";
		}

		jObj.put("DATA", conn.queryJSONArrayWithPage("mogan-DB", sql,
				(pageIndex - 1) * 50, pageSize));
		jObj.put("TOTAL_SIZE", conn.getQueryDataSize("mogan-DB", sql));

		jArray.add(jObj);
		return jArray;
	}

}
