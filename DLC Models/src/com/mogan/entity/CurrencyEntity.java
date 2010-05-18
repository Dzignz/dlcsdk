package com.mogan.entity;

import java.util.HashMap;
import java.util.Map;

public class CurrencyEntity {
	final static public String CNY="CNY";	//人民幣
	final static public String TWD="TWD";	//新台幣
	final static public String USD="USD";	//美金
	final static public String JPY="JPY";	//日幣
	
	/**
	 *	欄位名稱 
	 */
	final static private Map<String,String> accountColName=new HashMap<String,String>();	
	{
		accountColName.put(CNY, "sum_rmb");
		accountColName.put(TWD, "sum_ntd");
		accountColName.put(USD, "sum_usd");
		accountColName.put(JPY, "sum_yen");
	}
	
	/**
	 *	資料格式 
	 */
	final static private Map<String,String> currencyFormat=new HashMap<String,String>();	
	{
		currencyFormat.put(CNY, "0.00");
		currencyFormat.put(TWD, "0");
		currencyFormat.put(USD, "0.00");
		currencyFormat.put(JPY, "0");
	}
	
	static public String getCurrencyAccountColName(String currency){
		return accountColName.get(currency);
	}
	
	static public String getCurrencyFormat(String currency){
		return currencyFormat.get(currency);
	}
}
