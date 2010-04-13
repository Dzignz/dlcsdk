package com.mogan.model.netAgent;

import java.util.HashMap;
import java.util.Map;

public class NetAgentYT {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NetAgent nAgent = new NetAgent();
		
			nAgent.getDataWithGet("http://tw.news.yahoo.com/article/url/d/a/091216/5/1x0ff.html");
		Map dataMap=new HashMap();
		dataMap.put("uri", "/d/a/091216/5/1x0ff.html");
		dataMap.put("focus", "-1");
		dataMap.put("ynwsatuv", "4");
		nAgent.putAllPostDataMap(dataMap);
		nAgent.getDataWithPost("http://tw.news.yahoo.com/json_ynwsatu.html");
	}
}
