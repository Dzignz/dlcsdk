package com.mogan.model;

import java.util.Map;

import net.sf.json.JSONArray;

import com.mogan.sys.ProtoModel;
import com.mogan.sys.ServiceModelFace;

public class DataMigrate extends ProtoModel implements ServiceModelFace{
	/** 會員資料 */
	private final static String MIGRATE_MEMBER="MIGRATE_MEMBER";
	/** 競標資料 */
	private final static String MIGRATE_BID_TRACE="MIGRATE_BID_ORDER";
	/** 代購資料 */
	private final static String MIGRATE_BUY_ORDER="MIGRATE_BUY_ORDER";
	/** 代拍資料 */
	private final static String MIGRATE_SELL_TRACE="MIGRATE_SELL_TRACE";
	/** 物流資料 */
	private final static String MIGRATE_SHIP_DATA="MIGRATE_SHIP_DATA";
	/** 即時入帳資料 */
	private final static String MIGRATE_MONEY_TRANSFER="MIGRATE_MONEY_TRANSFER";

	
	@Override
	public JSONArray doAction(Map arg0) throws Exception {
		// TODO Auto-generated method stub
		if (this.getAct().equals(MIGRATE_MEMBER)){
			
		}
		return null;
	}

}
