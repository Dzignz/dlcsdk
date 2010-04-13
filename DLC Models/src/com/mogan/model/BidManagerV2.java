package com.mogan.model;

import java.util.ArrayList;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.sys.DBConn;
import com.mogan.sys.ProtoModel;
import com.mogan.sys.ServiceModelFace;
import com.mogan.sys.SysLogger4j;

/**
 * 訂單管理第二版
 * 20100401 開始
 * @author Dian
 *
 */
public class BidManagerV2 extends ProtoModel implements ServiceModelFace {
	
	/**
	 * 程式進入點
	 */
	@Override
	public JSONArray doAction(Map parameterMap) throws Exception {
		// TODO Auto-generated method stub
		JSONArray jArray=new JSONArray();
		if (this.getAct().equals("LOAD_BID_ITEM_ORDERS")){
			/**
			 * 讀取代標清單 
			 */
			jArray=loadBitIOs();
		}
		return jArray;
	}

	/**
	 * 讀取代標清單
	 * @return
	 */
	private JSONArray loadBitIOs(){
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
		"DBConn");
		JSONArray jArray=new JSONArray();
		JSONObject jObj = new JSONObject();
		
		
		String sql="SELECT * FROM view_bid_item_order_v1";
		if (sql.endsWith("view_bid_item_order_v1")){
			sql+=" WHERE ";	
		}
		sql+=" delete_flag = 1 ";
		//sql+=" ORDER BY item_order_id ";
		
		//conn.queryTabelStructure("mogan-VMDB","view_bid_item_order_v1");
		SysLogger4j.warn(sql);
		jObj.put("Datas", conn.queryJSONArrayWithPage("mogan-VMDB", sql,0,10));
		jObj.put("Records", conn.getQueryDataSize("mogan-VMDB", sql));
		jObj.put("Fileds", conn.queryTabelStructure("mogan-VMDB","view_bid_item_order_v1"));
		jArray.add(jObj);
		return jArray;
	}
}
