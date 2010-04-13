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
import org.htmlparser.util.SimpleNodeIterator;

import com.mogan.face.NetAgentModel;
import com.mogan.sys.SysCalendar;

/**
 * 2010-01-04 第一項商品會掉
 * 
 * @author Dian
 */
public class NetAgentYTW extends NetAgentModel {
	public static void printNodes(NodeList nodesx,String key,int nodeLevel){
		SimpleNodeIterator iterator = nodesx.elements();
		
		while (iterator.hasMoreNodes()) {
			Node node = iterator.nextNode();
			//得到該節點的子節點列表
			NodeList childList = node.getChildren();
			//孩子節點為空，說明是值節點
			if (null == childList)
			{
				//得到值節點的值
				//若包含關鍵字，則簡單打印出來文本
				if (node.toPlainTextString().equals("價格超低！！YOVICA迷你LED投影機,投射40吋!看電影/電玩超過癮 WII PS3 XBOX ")){
					System.out.println("[info] "+key+" ::"+node.toPlainTextString());
				}else if (node.toPlainTextString().equals("【。西門町。】___【 XBOX 360 HDMI硬碟版+震動手把 】___【門號價下殺 ↘$188元】")){
					System.out.println("[info] "+key+" ::"+node.toPlainTextString());
				}else if (node.toPlainTextString().equals("小李電子王國【P3078】xbox 360 高清音頻RCA線 ")){
					System.out.println("[info] "+key+" ::"+node.toPlainTextString());
				}else{
//					System.out.println("[DEBUG] "+node.toHtml());
				}
				
				
			} //end if
			//孩子節點不為空，繼續迭代該孩子節點
			else 
			{
				key=key+"."+nodeLevel;
				printNodes(childList, key+1,0);
			}//end else
			nodeLevel++;
		}//end wile

	}
	
	public static void main(String[] args) {
		JSONArray jArray=new JSONArray();
		NetAgentYTW ntw = new NetAgentYTW();
		NetAgent nAgent = new NetAgent();
		
		nAgent.getDataWithGet("http://tw.search.bid.yahoo.com/search/ac?p=XBOX&ei=UTF-8");

		try {

			NodeList nodesx = nAgent.getHtmlNodeList();
			printNodes(nodesx,"0",0);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
