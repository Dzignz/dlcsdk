package com.mogan.entity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.google.gdata.data.ExtensionDescription.Default;
import com.mogan.exception.entity.EntityNotExistException;
import com.mogan.exception.privilege.PrivilegeException;
import com.mogan.log.MoganLogger;
import com.mogan.model.BidManagerV2;
import com.mogan.model.netAgent.NetAgentYJV2;
import com.mogan.sys.DBConn;
import com.mogan.sys.SysCalendar;
import com.mogan.sys.SysKernel;
import com.mogan.sys.SysMath;
import com.mogan.sys.SysTool;

public class ItemTideEntity extends EntityService {

	private Logger logger = Logger.getLogger(ItemTideEntity.class.getName());
	private String id;
	private int idType;
	final private static int ITEM_ALERT = 1;
	final private static int CONTACT_ALERT = 2;
	final private static int SHIP_ALERT = 3;
	final private static int MONEY_ALERT = 4;

	final public static int SAME_MEMBER = 1;
	final public static int SAME_SELLER = 2;

	/**
	 * 代表傳入的ID是以item_order_id，來帶出item_tide資料
	 */
	final static public int ITEM_ORDER_ID = 0;

	/**
	 * 代表傳入的ID是以tide_id，來帶出item_tide資料
	 */
	final static public int ITEM_TIDE_ID = 1;

	/**
	 * 代標- 代購- 代刊-
	 */
	final static public String STATUS_1_01 = "1-01";

	/**
	 * 代標- 代購- 代刊-
	 */
	final static public String STATUS_1_02 = "1-02";

	/**
	 * 代標- 代購- 代刊-
	 */
	final static public String STATUS_2_01 = "2-01";

	/**
	 * 代標- 代購- 代刊-
	 */
	final static public String STATUS_2_02 = "2-02";

	/**
	 * 代標- 代購- 代刊-
	 */
	final static public String STATUS_2_03 = "2-03";

	/**
	 * 代標- 代購- 代刊-
	 */
	final static public String STATUS_2_04 = "2-04";

	/**
	 * 代標- 代購- 代刊-
	 */
	final static public String STATUS_2_05 = "2-05";

	/**
	 * 代標- 代購- 代刊-
	 */
	final static public String STATUS_2_06 = "2-06";

	/**
	 * 代標- 代購- 代刊-
	 */
	final static public String STATUS_2_07 = "2-07";

	/**
	 * 代標-連絡中 代購- 代刊-
	 */
	final static public String STATUS_3_01 = "3-01";
	/**
	 * 代標-取得連絡 代購- 代刊-
	 */
	final static public String STATUS_3_02 = "3-02";

	/**
	 * 代標-待匯款 代購- 代刊-
	 */
	final static public String STATUS_3_03 = "3-03";

	/**
	 * 代標-已匯款 代購- 代刊-
	 */
	final static public String STATUS_3_04 = "3-04";

	/**
	 * 代標-賣家已發貨 代購- 代刊-
	 */
	final static public String STATUS_3_05 = "3-05";

	/**
	 * 代標-購買點已收貨 代購- 代刊-
	 */
	final static public String STATUS_3_06 = "3-06";

	/**
	 * 代標-購買點已發貨 代購- 代刊-
	 */
	final static public String STATUS_3_07 = "3-07";

	/**
	 * 代標-收貨點已收貨 代購- 代刊-
	 */
	final static public String STATUS_3_08 = "3-08";

	/**
	 * 代標-收貨點已發貨 代購- 代刊-
	 */
	final static public String STATUS_3_09 = "3-09";

	/**
	 * 代標-會員已收貨 代購- 代刊-
	 */
	final static public String STATUS_3_10 = "3-10";

	/**
	 * 代標-結案 代購- 代刊-
	 */
	final static public String STATUS_3_11 = "3-11";

	/**
	 * 代標-會員棄標 代購- 代刊-
	 */
	final static public String STATUS_3_12 = "3-12";

	/**
	 * 代標- 代購- 代刊-
	 */
	final static public String STATUS_4_00 = "4-00";

	/**
	 * 自動建立一個新的Entity
	 * 
	 * @param servletContext
	 * @param session
	 * @throws EntityNotExistException
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	public ItemTideEntity(ServletContext servletContext, HttpSession session)
			throws EntityNotExistException, UnsupportedEncodingException,
			SQLException {
		super(servletContext, session);
		this.id = create();
		this.idType = ItemTideEntity.ITEM_TIDE_ID;
		refreashData();
	}

	/**
	 * 傳入指定item order id 或 item tide id 來讀取tide id
	 * 
	 * @param servletContext
	 * @param session
	 * @param id
	 * @param idType
	 * @throws EntityNotExistException
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	public ItemTideEntity(ServletContext servletContext, HttpSession session,
			String id, int idType) throws EntityNotExistException,
			UnsupportedEncodingException, SQLException {
		super(servletContext, session);
		this.id = id;
		this.idType = idType;
		refreashData();

	}

	/**
	 * 讀取訂單資料
	 * 
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	@Override
	void refreashData() throws EntityNotExistException,
			UnsupportedEncodingException, SQLException {
		JSONArray jArray;
		switch (this.idType) {
		case ITEM_ORDER_ID:
			jArray = conn.queryJSONArray(CONN_ALIAS, "SELECT * FROM view_item_tide_v2 WHERE tide_id in (SELECT tide_id FROM item_order WHERE item_order_id ='"
					+ id + "'  AND  delete_flag=1  )");
			break;
		case ITEM_TIDE_ID:
			jArray = conn.queryJSONArray(CONN_ALIAS, "SELECT * FROM view_item_tide_v2 WHERE tide_id ='"
					+ id + "'");
			break;
		default:
			jArray = new JSONArray();
			break;
		}

		if (jArray.size() == 1) {
			this.setMainObj(jArray.getJSONObject(0));
			if (this.getAttribute("classfly").equals("OC-001")) {
				this.setAttribute("cost_1", this.getAttribute("cost_8"));
			}
			String itmeAlert = this.getMainObj().optString("item_alert", "-"); // 商品備註
			String moneyAlert = this.getMainObj().optString("money_alert", "-"); // 金流備註
			String contactAlert = this.getMainObj().optString("contact_alert", "-"); // 聯絡備註
			String shipAlert = this.getMainObj().optString("ship_alert", "-"); // 物流備註
			this.setAttribute("alert_group", ("<p>商品註釋：" + itmeAlert
					+ "</p><p>聯絡備註：" + contactAlert + "</p><p>金流備註："
					+ moneyAlert + "</p><p>物流備註：" + shipAlert).replaceAll("null", "-").replaceAll("[\\r\\n|\\r|\\n]", "<br />")
					+ "</p>");

			if (this.getAttribute("remit_id") == null) {
				// 檢查有沒有匯款資料
				// 沒有則自動新增
				RemitEntity rmEty = new RemitEntity(this.getModelServletContext(), this.getSession());
				this.setAttribute("remit_id", rmEty.getAttribute("remit_id"));
				this.saveEntity("自動建立remit資料");
			}
			logger.info("同捆訂單讀取完成.(" + getMainObj().getString("tide_id") + ")");
		} else if (jArray.size() > 1) {
			throw new EntityNotExistException("此ID對應多筆資料.(" + id + " " + idType
					+ ")");
		} else if (jArray.size() == 0) {
			throw new EntityNotExistException("沒有此ID資料.(" + id + " " + idType
					+ ")");
		}
	}

	/**
	 * 取得匯款資料
	 * 
	 * @return
	 * @throws EntityNotExistException
	 */
	public RemitEntity getRemitEntity() throws EntityNotExistException {
		RemitEntity rmEty = new RemitEntity(this.getModelServletContext(), this.getSession(), (String) this.getAttribute("remit_id"), RemitEntity.REMIT_ID);
		return rmEty;
	}

	/**
	 * 取得已付商品費用項目商品
	 */
	public JSONArray getPaidItemOrder() {
		JSONArray jArray;
		jArray = conn.queryJSONArray(CONN_ALIAS, "SELECT * FROM view_bid_item_order_v1 WHERE tide_id ='"
				+ this.getMainObj().getString("tide_id")
				+ "'  AND o_varchar01='1'");
		return jArray;
	}

	/**
	 * 取得未付商品費用項目商品
	 * 
	 * @return
	 */
	public JSONArray getNonPaidItemOrder() {
		JSONArray jArray;
		jArray = conn.queryJSONArray(CONN_ALIAS, "SELECT * FROM view_bid_item_order_v1 WHERE tide_id ='"
				+ this.getMainObj().getString("tide_id")
				+ "'  AND o_varchar01='0'");
		return jArray;
	}

	/**
	 * 取得所有商品
	 * 
	 * @return
	 */
	public JSONArray getItemOrders() {
		JSONArray jArray = new JSONArray();
		jArray.addAll(getNonPaidItemOrder());
		jArray.addAll(getPaidItemOrder());
		return jArray;
	}

	/**
	 * 給條件，回傳相同條件的訂單資料，group by tide_status
	 * 
	 * @return
	 * @throws Exception
	 */
	public JSONObject getTideCount(Map conditionMap) throws Exception {

		if (conditionMap == null || conditionMap.size() == 0) {
			throw new Exception("conditionMap can't be null.");
		}
		StringBuffer whereStr = new StringBuffer();
		String sqlStr = "SELECT COUNT(tide_status) AS tide_count,tide_status FROM item_tide ";
		Iterator it = conditionMap.keySet().iterator();
		for (; it.hasNext();) {
			String key = (String) it.next();
			if (whereStr.length() > 0)
				whereStr.append(" AND ");
			whereStr.append(key + " = '" + conditionMap.get(key) + "'");
		}
		if (whereStr.length() > 0)
			whereStr.append(" AND ");
		else
			whereStr.append(" WHERE ");
		whereStr.append(" delete_flag <>0 GROUP BY tide_status ");
		
		return SysTool.JSONArray2JSONObject(conn.queryJSONArray(CONN_ALIAS, sqlStr
				+ " WHERE " + whereStr), "tide_status", "tide_count");
	}

	/**
	 * 刪除訂單,會先進行退款的動作
	 * 
	 * @throws PrivilegeException
	 * @throws EntityNotExistException
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	public void delTide(String msg) throws PrivilegeException,
			EntityNotExistException, UnsupportedEncodingException, SQLException {
		/*
		 * if (!this.getMainObj().getString("tide_status").matches("3-0[123]")) { throw new PrivilegeException("訂單已付款，無法刪單或棄標!!"); }
		 */
		logger.info(this.getMainObj().toString());
		MemberEntity memEty = new MemberEntity(this.getModelServletContext(), this.getSession(), this.getMainObj().getString("member_id"), MemberEntity.MEMBER_ID);
		JSONArray paidItemOrder = getPaidItemOrder();
		JSONArray nonPayItemOrder = getNonPaidItemOrder();
		for (int i = 0; i < paidItemOrder.size(); i++) {
			JSONObject tempObj = paidItemOrder.getJSONObject(i);
			ItemOrderEntity ioEty = new ItemOrderEntity(this.getModelServletContext(), this.getSession(), tempObj.getString("item_order_id"));
			double totalCost = ioEty.getItemTotalCost(); // 商品原幣值
			BigDecimal bd_value = new BigDecimal(SysMath.mul(Double.toString(totalCost), (String) ioEty.getAttribute("exchange"))); // 計算應退金額
			DecimalFormat df = new DecimalFormat(CurrencyEntity.getCurrencyFormat((String) ioEty.getAttribute("pay_currency")));
			logger.info("totalCost=" + totalCost + ". bd_value=" + bd_value
					+ ". format="
					+ df.format(bd_value.setScale(2, BigDecimal.ROUND_HALF_UP)));
			String bakMoney = "0";
			try {
				/**
				 * 判斷傳入金額是否正確,錯誤時訂單不會刪除
				 */
				bakMoney = df.format(bd_value.setScale(2, BigDecimal.ROUND_HALF_UP));
				Double.parseDouble(bakMoney); // 測試是否為數字格式
			} catch (Exception ex) {
				logger.error("退款金額錯誤#" + tempObj.getString("item_order_id")
						+ " #" + this.getMainObj().getString("member_id"));
				logger.error(ex.getMessage(), ex);
				continue;
			}
			String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
			Map userLog_dataMap = MoganLogger.getItemOrderDel4User(log_Id, tempObj.getString("item_order_id"), bakMoney, (String) ioEty.getAttribute("pay_currency"), this.getOpenUser(), memEty.getUserId(), this.getOpenUserIP(), msg);
			this.mLogger.preLog(userLog_dataMap);
			memEty.addMoney(bakMoney, (String) ioEty.getAttribute("pay_currency"), true);
			ioEty.setAttribute("charg_01", "-"
					+ df.format(bd_value.setScale(2, BigDecimal.ROUND_HALF_UP)));
			ioEty.setAttribute("o_varchar01", "0"); // 商品費用未付
			ioEty.saveEntity(msg);
			ioEty.delEntity(msg);
			logger.info("刪除訂單 退款 金額:$"
					+ df.format(bd_value.setScale(2, BigDecimal.ROUND_HALF_UP))
					+ ". 會員ID:" + memEty.getAttribute("member_id") + ". 得標編號:"
					+ tempObj.getString("item_order_id") + ". 訂單編號:"
					+ this.getMainObj().getString("tide_id"));
			memEty.refreashData();
			userLog_dataMap.put("sum_money", memEty.getMyMoney((String) ioEty.getAttribute("pay_currency")));
			userLog_dataMap.put("debts", memEty.getMyDebts((String) ioEty.getAttribute("pay_currency")));
			this.mLogger.commitLog(userLog_dataMap);
		}

		for (int i = 0; i < nonPayItemOrder.size(); i++) {
			JSONObject tempObj = nonPayItemOrder.getJSONObject(i);
			ItemOrderEntity ioEty = new ItemOrderEntity(this.getModelServletContext(), this.getSession(), tempObj.getString("item_order_id"));
			ioEty.delEntity();
		}

		String bakMoney = "0";
		if (this.getMainObj().getString("member_pay_status").equals("2")) {
			bakMoney = (String) this.getAttribute("income");
			if (this.getAttribute("classfly").equals("OC-001")) {
				memEty.addMoney((String) this.getAttribute("income"), "TWD", true);
			} else {
				memEty.addMoney((String) this.getAttribute("income"), (String) this.getAttribute("tide_currency"), true);
			}
			this.setAttribute("member_pay_status", "1");
			this.saveEntity(msg);
		}
		String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
		Map userLog_dataMap = MoganLogger.getItemTideDel4User(log_Id, (String) this.getAttribute("tide_id"), bakMoney, (String) this.getAttribute("currency"), this.getOpenUser(), memEty.getUserId(), this.getOpenUserIP(), msg);
		this.mLogger.preLog(userLog_dataMap);
		this.addAlert(msg, this.ITEM_ALERT, true);
		this.delEntity(msg);
		memEty.refreashData();
		if (this.getAttribute("classfly").equals("OC-001")) {
			userLog_dataMap.put("sum_money", memEty.getMyMoney("TWD"));
			userLog_dataMap.put("debts", memEty.getMyDebts("TWD"));
		} else {
			userLog_dataMap.put("sum_money", memEty.getMyMoney((String) this.getAttribute("tide_currency")));
			userLog_dataMap.put("debts", memEty.getMyDebts((String) this.getAttribute("tide_currency")));
		}

		this.mLogger.commitLog(userLog_dataMap);
	}

	/**
	 * 棄標<br />
	 * 退回訂單內商品費用的84.75%給會員<br />
	 * 並收取訂單的所有費用
	 * 
	 * @throws PrivilegeException
	 * @throws EntityNotExistException
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	public void giveupTide(String msg) throws PrivilegeException,
			EntityNotExistException, UnsupportedEncodingException, SQLException {
		if (!this.getMainObj().getString("tide_status").matches("(1-\\d.|3-0[123])")) {
			throw new PrivilegeException("訂單已付款，無法刪單或棄標!!");
		}

		JSONArray itemOrder = getItemOrders();
		MemberEntity memEty = new MemberEntity(this.getModelServletContext(), this.getSession(), this.getMainObj().getString("member_id"), MemberEntity.MEMBER_ID);
		logger.info("會員餘額... " + memEty.getAttribute("sum_ntd"));
		for (int i = 0; i < itemOrder.size(); i++) {
			JSONObject tempObj = itemOrder.getJSONObject(i);
			ItemOrderEntity ioEty = new ItemOrderEntity(this.getModelServletContext(), this.getSession(), tempObj.getString("item_order_id"));
			double totalCost = ioEty.getItemTotalCost(); // 商品原幣值
			BigDecimal bd_value = new BigDecimal(SysMath.mul(totalCost, 0.8475)); // 退回原商品價的84.75%
			DecimalFormat df = new DecimalFormat(CurrencyEntity.getCurrencyFormat((String) ioEty.getAttribute("currency"))); // 取得商品貨幣格式
			int decimalPlace = CurrencyEntity.getCurrencyDecimalPlace((String) ioEty.getAttribute("currency")); // 取得商品貨幣格式
			BigDecimal bd_value1 = new BigDecimal(SysMath.mul(df.format(bd_value.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)), (String) ioEty.getAttribute("exchange"))); // 計算應退金額= 商品價84.75% x 匯率
			df = new DecimalFormat(CurrencyEntity.getCurrencyFormat((String) ioEty.getAttribute("pay_currency"))); // 取得會員結帳貨幣格式
			decimalPlace = CurrencyEntity.getCurrencyDecimalPlace((String) ioEty.getAttribute("pay_currency")); // 取得會員結帳貨幣格式
			logger.info("退款計算... " + totalCost + " x 0.8475=" + bd_value);
			logger.info("退款計算... " + bd_value + " x "
					+ ioEty.getAttribute("exchange") + "=" + bd_value1);
			String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
			Map userLog_dataMap = MoganLogger.getItemOrderGiveup4User(log_Id, tempObj.getString("item_order_id"), "", (String) ioEty.getAttribute("pay_currency"), this.getOpenUser(), memEty.getUserId(), this.getOpenUserIP(), msg);
			userLog_dataMap.put("money", "0");
			this.mLogger.preLog(userLog_dataMap);
			String bakMoney = "0";
			if (ioEty.getAttribute("o_varchar01").equals("1")) {
				try {
					bakMoney = df.format(bd_value1.setScale(2, BigDecimal.ROUND_HALF_UP));
					Double.parseDouble(bakMoney);
				} catch (Exception ex) {
					logger.error("退款金額錯誤#" + tempObj.getString("item_order_id")
							+ " #" + this.getMainObj().getString("member_id"));
					logger.error(ex.getMessage(), ex);
				}
				userLog_dataMap.put("money", bakMoney);
				this.mLogger.preLog(userLog_dataMap);
				memEty.addMoney(bakMoney, (String) ioEty.getAttribute("pay_currency"), true);
			}
			ioEty.setAttribute("charg_01", "-"
					+ df.format(bd_value1.setScale(2, BigDecimal.ROUND_HALF_UP)));
			ioEty.setAttribute("order_status", "3-12");
			ioEty.saveEntity(msg);
			memEty.refreashData();
			userLog_dataMap.put("sum_money", memEty.getMyMoney((String) ioEty.getAttribute("pay_currency")));
			userLog_dataMap.put("debts", memEty.getMyDebts((String) ioEty.getAttribute("pay_currency")));
			this.mLogger.commitLog(userLog_dataMap);

			logger.info("訂單棄標 是否退款"
					+ ioEty.getAttribute("o_varchar01")
					+ " 退款金額:$"
					+ df.format(bd_value1.setScale(2, BigDecimal.ROUND_HALF_UP))
					+ ". 會員ID:" + memEty.getAttribute("member_id") + ". 得標編號:"
					+ tempObj.getString("item_order_id") + ". 訂單編號:"
					+ this.getMainObj().getString("tide_id"));
		}
		String totalCost = String.valueOf(getToalCost());
		String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
		Map userLog_dataMap = MoganLogger.getItemTideGiveUp4User(log_Id, this.getMainObj().getString("tide_id"), this.getOpenUser(), memEty.getUserId(), this.getOpenUserIP(), msg);
		Map itemTideLog_dataMap = MoganLogger.getItemTideCheckout4User(log_Id, this.getMainObj().getString("tide_id"), totalCost, this.getOpenUser(), memEty.getUserId(), this.getOpenUserIP(), msg);
		this.mLogger.preLog(userLog_dataMap);
		this.mLogger.preLog(itemTideLog_dataMap);
		this.addAlert(msg, ItemTideEntity.ITEM_ALERT, false);
		memEty.addMoney("-" + getToalCost(), "TWD", true);

		// TODO 計算費用
		this.getMainObj().put("income", getToalCost()); // income 收費
		this.getMainObj().put("member_pay_status", "2"); // 已付款，系統自動扣款
		this.getMainObj().put("tide_status", "3-12");
		this.saveEntity(msg);

		memEty.refreashData();
		userLog_dataMap.put("sum_money", memEty.getMyMoney(this.getMainObj().getString("currency")));
		userLog_dataMap.put("debts", memEty.getMyDebts(this.getMainObj().getString("currency")));
		this.mLogger.commitLog(userLog_dataMap);
		this.mLogger.commitLog(itemTideLog_dataMap);
		// TODO 先退84.75%
		// TODO 不刪item order
		// TODO item tide 狀態改為3-012
	}

	/**
	 * 取得此訂單的總費用，不包含商品
	 */
	public double getToalCost() {
		double totalCost = 0;
		if (this.getAttribute("classfly").equals("OC-001")) {
			// 舊版日本yahoo代標
			BigDecimal bd_value = new BigDecimal((String) this.getAttribute("cost_2"));

			BigDecimal jpy = new BigDecimal((String) this.getAttribute("cost_2"));// 匯款費 JPY
			jpy.add(new BigDecimal((String) this.getAttribute("cost_3")));// 稅金 JPY
			jpy.add(new BigDecimal((String) this.getAttribute("cost_4")));// 當地運費 JPY
			jpy.add(new BigDecimal((String) this.getAttribute("cost_6")));// 其他費用 JPY
			BigDecimal twd = new BigDecimal((String) this.getAttribute("cost_8"));// 摩根手續費 TWD
			twd.add(new BigDecimal((String) this.getAttribute("cost_9")));// 國際運費 TWD
			String exchange = (String) this.getAttribute("exchange");
			if (exchange == null)
				exchange = conn.queryJSONArray(this.CONN_ALIAS, "SELECT MIN(exchange) as exchange FROM item_order WHERE tide_id ='"
						+ this.getAttribute("tide_id") + "'").getJSONObject(0).getString("exchange");
			BigDecimal talCost = new BigDecimal(SysMath.mul(jpy.toString(), exchange));
			totalCost = talCost.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		logger.info("getToalCost() .... " + totalCost);
		return totalCost;
	}

	/**
	 * 增加備忘
	 * 
	 * @param msg
	 *            備忘內容
	 * @param alertType
	 *            備忘類型
	 * @param autoSave
	 *            是否自動儲存
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	public void addAlert(String msg, int alertType, boolean autoSave)
			throws UnsupportedEncodingException, SQLException {
		String fixMsg = msg
				+ " #"
				+ new SysCalendar().getFormatDate(SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql)
				+ ". " + this.getOpenUserName() + "\r\n";

		// 再加上原本的備忘
		switch (alertType) {
		case 1:
			fixMsg += this.getMainObj().optString("item_alert");
			this.getMainObj().put("item_alert", fixMsg);
			break;
		case 2:
			fixMsg += this.getMainObj().optString("contact_alert");
			this.getMainObj().put("contact_alert", fixMsg);
			break;
		case 3:
			fixMsg += this.getMainObj().optString("ship_alert");
			this.getMainObj().put("ship_alert", fixMsg);
			break;
		case 4:
			fixMsg += this.getMainObj().optString("money_alert");
			this.getMainObj().put("money_alert", fixMsg);
			break;
		}

		if (autoSave) {
			this.saveEntity(msg);
		}

	}

	@Override
	public void delEntity(String msg) throws UnsupportedEncodingException,
			SQLException {
		String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
		Map log_dataMap = MoganLogger.getItemTideDelete(log_Id, this.getMainObj().getString("tide_id"), this.getOpenUser(), this.getOpenUserIP());
		log_dataMap.put("varchar2", msg);
		mLogger.preLog(log_dataMap);
		Map conditionMap = new HashMap();
		Map dataMap = new HashMap();
		conditionMap.put("tide_id", this.getMainObj().getString("tide_id"));
		dataMap.put("delete_flag", "0");
		conn.update(CONN_ALIAS, "item_tide", conditionMap, dataMap);
		mLogger.commitLog(log_dataMap);
		MoganLogger.logger.info("刪除同捆訂單 [" + this.getAttribute("tide_id") + "]");
	}

	/**
	 * 判斷訂單類型，如果是OC-001代表舊版訂單須要把[cost_1 新手續費(日圓)]轉到 [cost_8舊版手續費(台幣)]
	 * 
	 * @param msg
	 *            log訊息
	 */
	@Override
	public void saveEntity(String msg) throws UnsupportedEncodingException,
			SQLException {

		String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
		Map log_dataMap = MoganLogger.getItemTideChange(log_Id, this.getMainObj().getString("tide_id"), this.getOpenUser(), this.getOpenUserIP());
		log_dataMap.put("varchar2", msg);
		mLogger.preLog(log_dataMap);

		if (this.getAttribute("classfly").equals("OC-001")) {
			this.setAttribute("cost_8", this.getAttribute("cost_1"));
			// dataMap.put("cost_9", dataMap.get("cost_5"));
		}

		conn.queryJSONArray(CONN_ALIAS, "SELECT item_order_id,MIN(time_at_04) FROM item_order WHERE tide_id ='"
				+ this.getAttribute("tide_id") + "' ORDER BY time_at_04 ");
		Map conditionMap = new HashMap();
		conditionMap.put("tide_id", this.getMainObj().getString("tide_id"));
		conn.update(CONN_ALIAS, "item_tide", conditionMap, this.getMainObj());
		conn.executSql(CONN_ALIAS, "UPDATE item_tide SET item_order_id =(SELECT item_order_id FROM item_order WHERE tide_id='"
				+ this.getAttribute("tide_id")
				+ "' AND order_status like '3%' AND delete_flag =1 group by tide_id having MIN(time_at_04) ) WHERE tide_id='"
				+ this.getAttribute("tide_id") + "'");
		mLogger.commitLog(log_dataMap);
	}

	/**
	 * 變更訂單狀態， 如果autoSave=true時 同時會去修改item_order的資料<br />
	 * 
	 * @param status
	 * @param autoSave
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 * @throws EntityNotExistException
	 */
	public void changeStatus(String status, boolean autoSave)
			throws UnsupportedEncodingException, SQLException,
			EntityNotExistException {
		this.setAttribute("tide_status", status);
		if (autoSave) {
			JSONArray itemOrder = getItemOrders();
			for (int i = 0; i < itemOrder.size(); i++) {
				JSONObject tempObj = itemOrder.getJSONObject(i);
				ItemOrderEntity ioEty = new ItemOrderEntity(this.getModelServletContext(), this.getSession(), tempObj.getString("item_order_id"));
				ioEty.changeStatus(status, autoSave);
			}
			this.saveEntity();
			logger.info("修改訂單狀態. " + this.getAttribute("tide_id") + " to "
					+ status);
		}
	}

	/**
	 * 建立新entity
	 */
	@Override
	protected String create() throws UnsupportedEncodingException, SQLException {
		String id = conn.getAutoNumber(CONN_ALIAS, "IT-ID-01");
		Map etyObj = new HashMap();
		etyObj.put("tide_id", id);
		etyObj.put("date_1", new Date());
		etyObj.put("delete_flag", "1");
		String log_Id = mLogger.getNewLogId();
		Map log_dataMap = MoganLogger.getItemTideCreate(log_Id, id, this.getOpenUser(), this.getOpenUserIP());
		mLogger.preLog(log_dataMap);
		conn.newData(CONN_ALIAS, "item_tide", etyObj);
		mLogger.commitLog(log_dataMap);
		return id;
	}

	/**
	 * 複製entity 不包含 tide_id,date_1, delete_flag,remit_id
	 */
	@Override
	public ItemTideEntity cloneEty() throws UnsupportedEncodingException,
			SQLException, EntityNotExistException {
		logger.info("ITEM TIDE 複製訂單");
		String id = conn.getAutoNumber(CONN_ALIAS, "IT-ID-01");
		Map etyObj = new HashMap();
		id = fixTideId(id);
		etyObj.putAll(getMainObj());
		etyObj.put("tide_id", id);
		etyObj.put("date_1", new Date());
		etyObj.put("delete_flag", "1");
		etyObj.put("item_alert", "由["
				+ (String) this.getSession().getAttribute("USER_NAME")
				+ "]建立新訂單，舊訂單為"
				+ this.getAttribute("tide_id")
				+ " - "
				+ new SysCalendar().getFormatDate(SysCalendar.yyyy_MM_dd_HH_mm_ss_Mysql));
		String log_Id = mLogger.getNewLogId();
		Map log_dataMap = MoganLogger.getItemTideCreate(log_Id, id, this.getOpenUser(), this.getOpenUserIP());
		mLogger.preLog(log_dataMap);
		etyObj.remove("remit_id"); // 移除匯款需求單號
		conn.newData(CONN_ALIAS, "item_tide", etyObj);
		mLogger.commitLog(log_dataMap);
		ItemTideEntity itEty = new ItemTideEntity(this.getModelServletContext(), this.getSession(), id, ItemTideEntity.ITEM_TIDE_ID);
		itEty.saveEntity("由 [" + this.getAttribute("tide_id") + "] 複製");
		return itEty;
	}

	/**
	 * 更新訂單內所有商品的訊息及相關資料
	 */
	public void updateIoMsg(){
		logger.info("ITEM TIDE 更新商品資料"+this.getAttribute("tide_id"));
		JSONArray jArray;
		jArray=conn.queryJSONArray((String) SysKernel.getApplicationAttr(SysKernel.MAIN_DB), "SELECT item_order_id FROM item_order WHERE tide_id='"+this.getAttribute("TIDE_ID")+"' AND delete_flag = 1");
		for (int i=0;i<jArray.size();i++){
			ItemOrderEntity ioEty;
			try {
				ioEty = new ItemOrderEntity(this.getModelServletContext(), this.getSession(), jArray.getJSONObject(i).getString("item_order_id"));
				ioEty.updateItemContactMsg();
				ioEty.updateItemData();	
			} catch (EntityNotExistException e) {
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			}
			
		}
		
	}
	
	/**
	 * 取得此訂單下所有商品聯絡訊息
	 * 
	 * @return
	 */
	public JSONArray getItemOrdersMsg() {
		NetAgentYJV2 na = new NetAgentYJV2(this.getModelServletContext(), this.getAppId());
		// JSONArray ioIds = new JSONArray();
		StringBuffer ioIdStr = new StringBuffer();
		JSONArray ios = this.getItemOrders();
		for (int i = 0; i < ios.size(); i++) {
			if (ioIdStr.length() > 0) {
				ioIdStr.append(",");
			}
			ioIdStr.append("'" + ios.getJSONObject(i).get("item_order_id")
					+ "'");
		}
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
		String orderBy = "msg_date"; // 排序欄位
		String orderType = "DESC"; // 排序方式
		JSONArray jArray = conn.queryJSONArray("mogan-DB", "SELECT * FROM view_item_contact_record_v1 WHERE "
				+ " item_order_id IN (" + ioIdStr + ")  ORDER BY " + orderBy + // 排序欄位
				" " + orderType);// 排序方法
		return jArray;
	}

	/**
	 * 取得此訂單指定商品ID的聯絡訊息
	 * 
	 * @return
	 */
	@Deprecated
	public JSONArray getItemOrdersMsg(JSONArray ioIds) {
		// TODO 應該由itemorderentity帶出
		NetAgentYJV2 na = new NetAgentYJV2(this.getModelServletContext(), this.getAppId());

		return na.getItemContactMsg(ioIds, NetAgentYJV2.MSG_SOURCE_DB);
	}

	/**
	 * 更新訊息記錄
	 */
	public void refreashItemOrdersMsg() {
		NetAgentYJV2 na = new NetAgentYJV2(this.getModelServletContext(), this.getAppId());
		JSONArray ioIds = new JSONArray();
		JSONArray ios = this.getItemOrders();
		for (int i = 0; i < ios.size(); i++) {
			try {
				ItemOrderEntity ioEty = new ItemOrderEntity(this.getModelServletContext(), this.getSession(), (String) ios.getJSONObject(i).get("item_order_id"));
				ioEty.updateItemContactMsg();
			} catch (EntityNotExistException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 取得尚未關閉的相關訂單，condition可由標準條件組成SAME_MEMBER(同會員),SAME_SELLER(同賣家)
	 * 
	 * @param condition
	 * @return
	 * @see SAME_MEMBER
	 * @see SAME_SELLER
	 */
	public JSONArray getUnCloseTide(int condition) {
		JSONArray jArray = new JSONArray();
		String sql = "SELECT full_name,items_count,tide_id FROM view_item_tide_v2 WHERE tide_id not like '"
				+ this.getAttribute("tide_id")
				+ "' AND tide_status in ('3-01','3-02') AND delete_flag = 1";
		switch (condition) {
		case (SAME_MEMBER + SAME_SELLER):
			sql += " AND seller_id='" + this.getAttribute("seller_id")
					+ "' AND member_id='" + this.getAttribute("member_id")
					+ "'";
			break;
		case SAME_MEMBER:
			sql += " AND member_id='" + this.getAttribute("member_id") + "'";
			break;
		case SAME_SELLER:
			sql += " AND seller_id='" + this.getAttribute("seller_id") + "'";
			break;
		}

		jArray = conn.queryJSONArray(CONN_ALIAS, sql);
		return jArray;
	}

	/**
	 * 依指定的level取回系統log
	 * @param level
	 * @throws UnsupportedEncodingException 
	 * @throws SQLException 
	 */
	public JSONArray getlogs(int level) throws UnsupportedEncodingException, SQLException {

		JSONArray jArray=conn.queryJSONArray(CONN_ALIAS, "SELECT lr.*,CONCAT(member_data.first_name,member_data.last_name) AS member_name ,system_member.system_name,view_bid_item_order_v1.item_name,view_bid_item_order_v1.item_id , (SELECT list_name FROM system_list_value WHERE list_key=lr.log_status ) as status_name " +
				" FROM log_record lr " +
				" INNER JOIN  (SELECT '"+this.getAttribute("tide_id")+"' AS item_order_id UNION SELECT item_order_id from item_order WHERE tide_id='"+this.getAttribute("tide_id")+"') tmp ON lr.item_order_id=tmp.item_order_id  " +
				" LEFT JOIN view_bid_item_order_v1 ON view_bid_item_order_v1.item_order_id=lr.item_order_id " +
				" LEFT JOIN member_data ON member_data.member_id=lr.user_name " +
				" LEFT JOIN system_member ON system_member.system_member_id =lr.admin_name WHERE lr.log_status NOT IN ('LR-9299') ORDER BY time_at");

		return jArray;
	}

	/**
	 * 修正tide id 格式
	 * 
	 * @param tideId
	 * @return
	 */
	private String fixTideId(String tideId) {
		tideId = tideId.split("IT-")[1];
		String yy = tideId.substring(0, 2);
		String mm = tideId.substring(2).split("-")[0];
		switch (Integer.parseInt(mm)) {
		case 1:
			mm = "A";
			break;
		case 2:
			mm = "B";
			break;
		case 3:
			mm = "C";
			break;
		case 4:
			mm = "D";
			break;
		case 5:
			mm = "E";
			break;
		case 6:
			mm = "F";
			break;
		case 7:
			mm = "G";
			break;
		case 8:
			mm = "H";
			break;
		case 9:
			mm = "I";
			break;
		case 10:
			mm = "J";
			break;
		case 11:
			mm = "K";
			break;
		case 12:
			mm = "L";
			break;
		}
		String ddCode = tideId.split("-")[1];
		ddCode = Integer.toHexString(Integer.parseInt(ddCode)).toUpperCase();
		return yy + mm + "-" + ddCode;
	}

}
