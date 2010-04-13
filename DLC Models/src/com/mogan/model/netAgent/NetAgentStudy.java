package com.mogan.model.netAgent;

import com.mogan.face.NetAgentModel;

public class NetAgentStudy extends NetAgentModel{
	
	public static void main(String[] args) {
		NetAgent nAgent = new NetAgent();
		
		nAgent.getDataWithGet("http://auctions.yahooapis.jp/AuctionWebService/V2/json/search?appid=XgYc6pixg67PfQsmDHQZitFdpGJAD.A8zUox2YN40412NHK5Wl7jgBf3OkQLzfo4janN&query=ipod&category=0&page=1&sort=bids&order=a&callback=");
		System.out.println(nAgent.getResponseBody());
	}
}
