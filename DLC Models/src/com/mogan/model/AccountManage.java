package com.mogan.model;

import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.model.netAgent.NetAgentYJV2;
import com.mogan.sys.DBConn;
import com.mogan.sys.SysKernel;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;

public class AccountManage extends ProtoModel implements ServiceModelFace{

	@Override
	public JSONArray doAction(Map<String, String> parameterMap) throws Exception {
		// TODO Auto-generated method stub
		JSONArray jArray=new JSONArray();
		if (this.getAct().equals("GET_ACCOUNT_STATUS")){
			jArray=getAccountStatus();
		}
		return jArray;
	}
	
	/**
	 * 取得所有的登入資訊
	 * @return
	 */
	private JSONArray getAccountStatus(){
		JSONArray jArray=new JSONArray();
		DBConn conn=SysKernel.getConn();
		Map loginMap = (Map) SysKernel.getApplicationAttr(this.getAppId() + "_LOGIN_COOKIE_MAP");
		Iterator it=loginMap.keySet().iterator();
		for (;it.hasNext();){
			Map tempMap=(Map) loginMap.get(it.next());
			jArray.add(tempMap);
		}
		return jArray;
	}

}
