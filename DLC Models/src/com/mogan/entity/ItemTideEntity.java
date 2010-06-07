package com.mogan.entity;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.google.gdata.data.ExtensionDescription.Default;
import com.mogan.exception.entity.EntityNotExistException;
import com.mogan.exception.privilege.PrivilegeException;
import com.mogan.log.MoganLogger;
import com.mogan.sys.DBConn;
import com.mogan.sys.SysCalendar;
import com.mogan.sys.SysMath;

public class ItemTideEntity extends EntityService {

	private Logger logger = Logger.getLogger(ItemTideEntity.class.getName());
	private String id;
	private int idType;
	private int ITEM_ALERT = 1;
	private int CONTACT_ALERT = 2;
	private int SHIP_ALERT = 3;
	private int MONEY_ALERT = 4;

	/**
	 * 代表傳入的ID是以item_order_id，來帶出item_tide資料
	 */
	final static public int ITEM_ORDER_ID = 0;

	/**
	 * 代表傳入的ID是以tide_id，來帶出item_tide資料
	 */
	final static public int ITEM_TIDE_ID = 1;
	
	/**
	 * 代標-
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_1_01 = "1-01";
	
	/**
	 * 代標-
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_1_02 = "1-02";
	
	/**
	 * 代標-
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_2_01 = "2-01";
	
	/**
	 * 代標-
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_2_02 = "2-02";
	
	/**
	 * 代標-
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_2_03 = "2-03";
	
	/**
	 * 代標-
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_2_04 = "2-04";
	
	/**
	 * 代標-
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_2_05 = "2-05";
	
	/**
	 * 代標-
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_2_06 = "2-06";
	
	/**
	 * 代標-
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_2_07 = "2-07";
	
	/**
	 * 代標-連絡中
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_3_01 = "3-01";
	/**
	 * 代標-取得連絡
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_3_02 = "3-02";
	
	/**
	 * 代標-待匯款
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_3_03 = "3-03";
	
	/**
	 * 代標-已匯款
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_3_04 = "3-04";
	
	/**
	 * 代標-賣家已發貨
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_3_05 = "3-05";
	
	/**
	 * 代標-購買點已收貨
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_3_06 = "3-06";
	
	/**
	 * 代標-購買點已發貨
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_3_07 = "3-07";
	
	/**
	 * 代標-收貨點已收貨
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_3_08 = "3-08";
	
	/**
	 * 代標-收貨點已發貨
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_3_09 = "3-09";
	
	/**
	 * 代標-會員已收貨
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_3_10 = "3-10";
	
	/**
	 * 代標-結案
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_3_11 = "3-11";
	
	/**
	 * 代標-會員棄標
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_3_12 = "3-12";
	
	/**
	 * 代標-
	 * 代購-
	 * 代刊-
	 */
	final static public String STATUS_4_00 = "4-00";

	/**
	 * @param conn
	 * @param id
	 * @param idType
	 * @throws EntityNotExistException
	 */
	public ItemTideEntity(ServletContext servletContext, HttpSession session,
			String id, int idType) throws EntityNotExistException {
		super(servletContext, session);
		this.id = id;
		this.idType = idType;
		refreashData();
	}

	@Override
	void refreashData() throws EntityNotExistException {
		JSONArray jArray;
		switch (this.idType) {
		case ITEM_ORDER_ID:
			jArray = conn.queryJSONArray(CONN_ALIAS, "SELECT * FROM item_tide WHERE tide_id in (SELECT tide_id FROM item_order WHERE item_order_id ='"
					+ id + "'  AND  delete_flag=1  )  AND delete_flag=1 ");
			break;
		case ITEM_TIDE_ID:
			jArray = conn.queryJSONArray(CONN_ALIAS, "SELECT * FROM item_tide WHERE tide_id ='"
					+ id + "'  AND  delete_flag=1 ");
			break;
		default:
			jArray = new JSONArray();
			break;
		}

		if (jArray.size() == 1) {
			this.setMainObj(jArray.getJSONObject(0));
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
	 * 取得已付商品費用項目商品
	 */
	public JSONArray getPaidItemOrder() {
		JSONArray jArray;
		jArray = conn.queryJSONArray(CONN_ALIAS, "SELECT * FROM view_bid_item_order_v1 WHERE tide_id ='"
				+ this.getMainObj().getString("tide_id")
				+ "' AND delete_flag=1 AND o_varchar01='1'");
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
				+ "' AND delete_flag=1 AND o_varchar01='0'");
		return jArray;
	}

	/**
	 * 取得所有商品
	 * 
	 * @return
	 */
	public JSONArray getItemOrder() {
		JSONArray jArray = new JSONArray();
		jArray.addAll(getNonPaidItemOrder());
		jArray.addAll(getPaidItemOrder());
		return jArray;
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
		if (!this.getMainObj().getString("tide_status").matches("3-0[123]")) {
			throw new PrivilegeException("訂單已付款，無法刪單或棄標!!");
		}

		logger.info(this.getMainObj().toString());
		MemberEntity memEty = new MemberEntity(this.getModelServletContext(), this.getSession(), this.getMainObj().getString("member_id"), MemberEntity.MEMBER_ID);
		JSONArray paidItemOrder = getPaidItemOrder();
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
			Map userLog_dataMap = MoganLogger.getItemOrderDel4User(log_Id, tempObj.getString("item_order_id"), bakMoney, this.getOpenUser(), memEty.getUserId(), this.getOpenUserIP(), msg);
			this.mLogger.preLog(userLog_dataMap);
			memEty.addMoney(bakMoney, (String) ioEty.getAttribute("pay_currency"), true);
			ioEty.setAttribute("charg_01", "-"
					+ df.format(bd_value.setScale(2, BigDecimal.ROUND_HALF_UP)));
			ioEty.saveEntity(msg);
			ioEty.delEntity(msg);
			logger.info("刪除訂單 退款 金額:$"
					+ df.format(bd_value.setScale(2, BigDecimal.ROUND_HALF_UP))
					+ ". 會員ID:"
					+ memEty.getMemberMainObj().getString("member_id")
					+ ". 得標編號:" + tempObj.getString("item_order_id")
					+ ". 訂單編號:" + this.getMainObj().getString("tide_id"));
			memEty.refreashData();
			userLog_dataMap.put("sum_money", memEty.getMyMoney((String) ioEty.getAttribute("pay_currency")));
			userLog_dataMap.put("debts", memEty.getMyDebts((String) ioEty.getAttribute("pay_currency")));
			this.mLogger.commitLog(userLog_dataMap);
		}

		JSONArray nonPayItemOrder = getNonPaidItemOrder();
		for (int i = 0; i < nonPayItemOrder.size(); i++) {
			JSONObject tempObj = nonPayItemOrder.getJSONObject(i);
			ItemOrderEntity ioEty = new ItemOrderEntity(this.getModelServletContext(), this.getSession(), tempObj.getString("item_order_id"));
			ioEty.delEntity();
		}
		String bakMoney = "0";
		if (this.getMainObj().getString("member_pay_status").equals("2")) {
			bakMoney = this.getMainObj().getString("income");
			memEty.addMoney(this.getMainObj().getString("income"), this.getMainObj().getString("currency"), true);
			this.saveEntity(msg);
		}

		String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
		Map userLog_dataMap = MoganLogger.getItemOrderDel4User(log_Id, this.getMainObj().getString("tide_id"), bakMoney, this.getOpenUser(), memEty.getUserId(), this.getOpenUserIP(), msg);
		this.mLogger.preLog(userLog_dataMap);
		this.addAlert(msg, this.ITEM_ALERT, true);
		this.delEntity(msg);
		this.mLogger.commitLog(userLog_dataMap);
	}

	/**
	 * 棄標
	 * 
	 * @throws PrivilegeException
	 * @throws EntityNotExistException
	 * @throws SQLException
	 * @throws UnsupportedEncodingException
	 */
	public void giveupTide(String msg) throws PrivilegeException,
			EntityNotExistException, UnsupportedEncodingException, SQLException {
		if (!this.getMainObj().getString("tide_status").matches("3-0[123]")) {
			throw new PrivilegeException("訂單已付款，無法刪單或棄標!!");
		}

		JSONArray itemOrder = getItemOrder();
		MemberEntity memEty = new MemberEntity(this.getModelServletContext(), this.getSession(), this.getMainObj().getString("member_id"), MemberEntity.MEMBER_ID);
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

			String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
			Map userLog_dataMap = MoganLogger.getItemOrderGiveup4User(log_Id, tempObj.getString("item_order_id"), "", this.getOpenUser(), memEty.getUserId(), this.getOpenUserIP(), msg);
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
					+ ". 會員ID:"
					+ memEty.getMemberMainObj().getString("member_id")
					+ ". 得標編號:" + tempObj.getString("item_order_id")
					+ ". 訂單編號:" + this.getMainObj().getString("tide_id"));
		}

		String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
		Map userLog_dataMap = MoganLogger.getItemTideGiveUp4User(log_Id, this.getMainObj().getString("tide_id"), this.getOpenUser(), memEty.getUserId(), this.getOpenUserIP(), msg);
		this.mLogger.preLog(userLog_dataMap);

		this.addAlert(msg, this.ITEM_ALERT, false);
		this.getMainObj().put("tide_status", "3-12");
		this.saveEntity(msg);

		memEty.refreashData();
		userLog_dataMap.put("sum_money", memEty.getMyMoney(this.getMainObj().getString("currency")));
		userLog_dataMap.put("debts", memEty.getMyDebts(this.getMainObj().getString("currency")));
		this.mLogger.commitLog(userLog_dataMap);
		// TODO 先退84.75%
		// TODO 不刪item order
		// TODO item tide 狀態改為3-012
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
			fixMsg += this.getMainObj().getString("item_alert");
			this.getMainObj().put("item_alert", fixMsg);
			break;
		case 2:
			fixMsg += this.getMainObj().getString("contact_alert");
			this.getMainObj().put("contact_alert", fixMsg);
			break;
		case 3:
			fixMsg += this.getMainObj().getString("ship_alert");
			this.getMainObj().put("ship_alert", fixMsg);
			break;
		case 4:
			fixMsg += this.getMainObj().getString("money_alert");
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
	}

	@Override
	public void saveEntity(String msg) throws UnsupportedEncodingException,
			SQLException {

		String log_Id = conn.getAutoNumber(CONN_ALIAS, "LR-ID-01");
		Map log_dataMap = MoganLogger.getItemTideChange(log_Id, this.getMainObj().getString("tide_id"), this.getOpenUser(), this.getOpenUserIP());
		log_dataMap.put("varchar2", msg);
		mLogger.preLog(log_dataMap);

		Map conditionMap = new HashMap();
		Map dataMap = new HashMap();
		conditionMap.put("tide_id", this.getMainObj().getString("tide_id"));
		dataMap.putAll(this.getMainObj());
		conn.update(CONN_ALIAS, "item_tide", conditionMap, dataMap);

		mLogger.commitLog(log_dataMap);
	}

	/**
	 * 變更訂單狀態，
	 * 如果autoSave=true時 同時會去修改item_order的資料<br />
	 * 
	 * @param status
	 * @param autoSave
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 * @throws EntityNotExistException 
	 */
	public void changeStatus(String status, boolean autoSave) throws UnsupportedEncodingException, SQLException, EntityNotExistException {
		this.setAttribute("tide_status", status);
		if (autoSave){
			JSONArray itemOrder = getItemOrder();
			for (int i = 0; i < itemOrder.size(); i++) {
				JSONObject tempObj = itemOrder.getJSONObject(i);
				ItemOrderEntity ioEty = new ItemOrderEntity(this.getModelServletContext(), this.getSession(), tempObj.getString("item_order_id"));
				ioEty.changeStatus(status, autoSave);
			}
			this.saveEntity();
			logger.info("修改訂單狀態. "+this.getAttribute("tide_id")+" to "+status);
		}
	}

	@Override
	String create(JSONObject etyObj) {
		// TODO Auto-generated method stub
		return null;
	}

}
