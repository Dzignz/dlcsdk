package com.mogan.model.netAgent;

import net.sf.json.JSONArray;

import com.mogan.face.BidFace;

public class NetAgentRu implements BidFace {

	@Override
	public boolean autoLogin(String id, String pwd) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JSONArray bidItem(String id, String pwd, String itemURL,
			String price, String qty) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONArray buyItem(String id, String pwd, String itemURL, String qty)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONArray checkBidHistory(String webSiteURL, String id,
			String itemURL) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBidURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLoginURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONArray isMyBid(String id, String itemURL, String price)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONArray login(String id, String pwd) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
