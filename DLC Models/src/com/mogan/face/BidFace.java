package com.mogan.face;

import net.sf.json.JSONArray;

public interface BidFace {
	
	public boolean autoLogin(
			String uId, String pwd)throws Exception;
	
	public  JSONArray isMyBid( String uId, String itemURL, String price)throws Exception;
	
	public  JSONArray checkBidHistory(String webSiteURL,
			String uId, String itemURL) throws Exception;
	
	public JSONArray login(String uId, String pwd)throws Exception;
	
	public JSONArray bidItem(String uId,String pwd, String itemURL, String price, String qty) throws Exception;
	
	public JSONArray buyItem(String uId,String pwd,String itemURL, String qty) throws Exception;
	
	public String getLoginURL();
	
	public String getBidURL();

}
