package com.mogan.serviceProtal;

import java.util.Map;

import net.sf.json.JSONArray;

import com.mogan.sys.ProtoModel;
import com.mogan.sys.ServiceModelFace;

public class TestClass extends ProtoModel implements ServiceModelFace {

	@Override
	public JSONArray doAction(Map map) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("[DEBUG] TestClass.");
		return null;
	}

}
