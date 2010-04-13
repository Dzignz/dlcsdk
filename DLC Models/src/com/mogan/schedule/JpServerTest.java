package com.mogan.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mogan.model.netAgent.NetAgent;
import com.mogan.sys.DBConn;
import com.mogan.sys.model.ScheduleModelAdapter;

public class JpServerTest extends ScheduleModelAdapter {
	public void exeSchedule() {
		DBConn conn = (DBConn) this.getModelServletContext().getAttribute(
		"DBConn");
		ArrayList<Map> itemList=conn.query("mogan-tw", "select item_id,url from web_track order by id desc limit 1");
		String itemId=(String) itemList.get(0).get("item_id");
		NetAgent netAgent=new NetAgent();

		String t0="";//台灣開啟日本網頁的時間
		String t1="";//台灣呼叫日本server的時間
		String t2="";//日本server開啟網頁的時間
		
		long lA_0=System.currentTimeMillis();
		netAgent.getDataWithGet("http://page.auctions.yahoo.co.jp/jp/auction/"+itemId);
		long lA_1=System.currentTimeMillis();
		t0=String.valueOf(lA_1-lA_0);
		
		long lB_0=System.currentTimeMillis();
		netAgent.getDataWithGet("http://mogan-jp.ws/study.jsp?ITEM_ID="+itemId);
		long lB_1=System.currentTimeMillis();
		t1=String.valueOf(lB_1-lB_0);
		t2=netAgent.getResponseBody();
		t2=t2.split("<body>")[1].split("</body>")[0].replaceAll("\\s", "");
		
		Map dataMap=new HashMap();
		
		dataMap.put("alert", "OPEN_YAHOO_PAGE_TEST");
		dataMap.put("create_date", new Date());
		dataMap.put("info", t0+" ,"+t1+" ,"+t2);
		
		conn.newData("mogan-DB", "system_alert", dataMap );
		netAgent.getDataWithGet("http://192.168.1.37/class/common/test.php?auctionID="+itemId);
	}
}
