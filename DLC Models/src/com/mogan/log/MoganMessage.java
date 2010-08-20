package com.mogan.log;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mogan.sys.DBConn;
import com.mogan.sys.SysKernel;

public class MoganMessage {
	final static String SYSMSG="ICR-801";
	
	/**
	 * 發送訊息
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void sendMsg(String type,String formUser,String toUser,boolean isGroup,String title,String msg,String itemOrderId) throws UnsupportedEncodingException, SQLException{
		DBConn conn=SysKernel.getConn();
		Map dataMap=new HashMap();
		dataMap.put("classify_flag", type);
		dataMap.put("item_order_id", itemOrderId);
		dataMap.put("contents", msg);
		dataMap.put("title", title);
		dataMap.put("from_user", formUser);
		dataMap.put("to_user", toUser);
		dataMap.put("time_at", new Date());
		
		conn.newData((String) SysKernel.getApplicationAttr(SysKernel.MAIN_DB), "member_message", dataMap);
		//ICR-503
	}
	
	/**
	 * 
	 * @param type
	 * @param formUser
	 * @param toUser
	 * @param isGroup
	 * @param title
	 * @param msg
	 * @throws UnsupportedEncodingException
	 * @throws SQLException
	 */
	public static void sendMsg(String type,String formUser,String toUser,boolean isGroup,String title,String msg) throws UnsupportedEncodingException, SQLException{
		MoganMessage.sendMsg(type, formUser, toUser, isGroup, title, msg, "");
	}
	
	/**
	 * 回覆訊息
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void replyMsg(String replyMsg,String formUser,String msg) throws UnsupportedEncodingException, SQLException{
		DBConn conn=SysKernel.getConn();
		Map dataMap=new HashMap();
		dataMap.put("reply_msg_id", replyMsg);
		/*
		dataMap.put("classify_flag", type);
		dataMap.put("item_order_id", type);
		dataMap.put("contents", type);
		dataMap.put("from_user", type);
		dataMap.put("to_user", type);
		*/
		conn.newData((String) SysKernel.getApplicationAttr(SysKernel.MAIN_DB), "member_message", dataMap);
	}
	
}
