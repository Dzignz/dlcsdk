package com.mogan.model.netAgent;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.Cookie;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.mogan.face.NetAgentModel;
import com.mogan.sys.SysCalendar;

/**
 * 2010-01-04 第一項商品會掉
 * 
 * @author Dian
 */
public class NetAgentYTW extends NetAgentModel {
	public static void main(String[] args) {
		JSONArray jArray=new JSONArray();
		NetAgentYTW ntw = new NetAgentYTW();
		NetAgent nAgent = new NetAgent();

		nAgent
				.getDataWithGet("http://tw.search.bid.yahoo.com/search/ac?ei=UTF-8&p=%E5%85%94%E6%AF%9B");
		
//		nAgent.getDataWithGet("http://tw.search.bid.yahoo.com/search/ac?p=PS3&ei=UTF-8");
		nAgent.getDataWithGet("http://tw.search.bid.yahoo.com/search/ac?p=%E4%BE%BF%E5%AE%9C&ei=UTF-8");
		nAgent.getDataWithGet("http://tw.search.bid.yahoo.com/search/ac?p=XBOX&ei=UTF-8");

		try {
//			

			/*
			 * Node adsNode=nodes.elementAt(0).getNextSibling().getNextSibling().getChildren().elementAt(12); //System.out.println(adsNode.toHtml());
			 * //System.out.println(adsNode.getChildren());.
			 */
			NodeList nodes = nAgent.filterItem(new HTMLNodeFilter(
					"class=\"datatitle\""));
//			System.out.println(nAgent.getResponseBody());

			nodes = nodes.elementAt(0).getParent().getChildren();
			
			nodes = nodes.elementAt(3).getChildren();
			//nodes = nodes.elementAt(7).getChildren();
			//廣告商品

			for (int i = 12; i < nodes.size(); i++) {
				
				if (!nodes.elementAt(i).toPlainTextString().matches("\\s*")) {
					JSONObject jObj=new JSONObject();
					NodeList itemNode=nodes.elementAt(i).getChildren();
					System.out.println("[DEBUG] itemNode:" + itemNode.size());
					if (itemNode.size()==19){
						NodeList itemNameNode=itemNode.elementAt(1).getChildren().elementAt(1).getChildren().elementAt(1).getChildren().elementAt(0).getChildren().elementAt(0).getChildren();//3
						System.out.println(":::getChildren().size()" + itemNode.elementAt(1).getChildren().size());
						try{
							/*
							System.out.println("-:::" + itemNameNode.toHtml());
							System.out.println("-:::" + itemNameNode.toHtml().split("title=\"")[1].split("\"")[0]);
							System.out.println("0:::" + itemNameNode.elementAt(0).toPlainTextString());
							System.out.println("1:::" + itemNameNode.elementAt(1).toHtml());
							System.out.println("2:::" + itemNameNode.elementAt(2).toHtml());
							*/
						}catch(Exception ex){
							ex.printStackTrace();
						}
//						jObj.put("ITEM_NAME", itemNode.elementAt(1).getChildren().elementAt(0).toPlainTextString());
						jObj.put("ITEM_NAME", itemNameNode.toHtml().split("title=\"")[1].split("\"")[0]);
						jObj.put("PRICE_1", itemNode.elementAt(5).toPlainTextString());
						jObj.put("PRICE_2", itemNode.elementAt(9).toPlainTextString());
						jObj.put("PRICE_3", itemNode.elementAt(11).toPlainTextString());
						jObj.put("DATE", itemNode.elementAt(17).toPlainTextString());
						
					}else if (itemNode.size()==3){
						System.out.println(":::getChildren().size()" + itemNode.size());
						//NodeList itemNameNode=itemNode.elementAt(1).getChildren().elementAt(1).getChildren().elementAt(1).getChildren().elementAt(0).getChildren().elementAt(0).getChildren();//3
						//System.out.println("-:::" + itemNameNode.toHtml());
					}
						jArray.add(jObj);
					
					for (int j=0;j<itemNode.size();j++){
//						System.out.println(itemNode.elementAt(j).toHtml());
						
//						System.out.println("::::::::::::::::::::" + j);
					}
//					System.out.println(nodes.elementAt(i).toHtml(false));
				} else {
//					System.out.println(nodes.elementAt(i).toHtml(false));
				}
				
				System.out.println("==============" + i);
			}
			System.out.println(jArray);
			// System.out.println(nodes.toHtml(false));
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
