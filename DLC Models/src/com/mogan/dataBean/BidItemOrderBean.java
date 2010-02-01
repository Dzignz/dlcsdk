package com.mogan.dataBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mogan.sys.DBConn;

public class BidItemOrderBean {
	private Map dataMap=new HashMap();
	public BidItemOrderBean(DBConn conn,String itemOrderId){
		ArrayList dataList=conn.query("mogan-tw", "SELECT * FROM view_bid_item_order_v1 WHERE item_order_id = '"+itemOrderId+"'");
		if (dataList.size()>0){
			dataMap=(Map) dataList.get(0);
		}
	}
	public Map getDataMap(){
		return dataMap;
	}
}
