package com.mogan.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class CurrencyEntity {
	static public Logger logger  =  Logger.getLogger(CurrencyEntity.class.getName());
	final static public String CNY="CNY";	//人民幣
	final static public String TWD="TWD";	//新台幣
	final static public String USD="USD";	//美金
	final static public String JPY="JPY";	//日幣
	
	/**
	 *	欄位名稱 
	 */
	static private Map<String,String> accountColName=new HashMap<String,String>();	
	static {
		accountColName.put(CNY, "sum_rmb");
		accountColName.put(TWD, "sum_ntd");
		accountColName.put(USD, "sum_usd");
		accountColName.put(JPY, "sum_yen");
	}
	
	/**
	 *	欄位名稱 
	 */
	static private Map<String,String> debtsColName=new HashMap<String,String>();	
	static {
		debtsColName.put(CNY, "debts_rmb");
		debtsColName.put(TWD, "debts_ntd");
		debtsColName.put(USD, "debts_usd");
		debtsColName.put(JPY, "debts_yen");
	}
	
	/**
	 *	資料格式 
	 */
	static private Map<String,String> currencyFormat=new HashMap<String,String>();	
	static {
		currencyFormat.put(CNY, "0.00");
		currencyFormat.put(TWD, "0");
		currencyFormat.put(USD, "0.00");
		currencyFormat.put(JPY, "0");
	}
	
	static private Map<String,Integer> currencyDecimalPlace=new HashMap<String,Integer>();	
	static {
		currencyDecimalPlace.put(CNY, Integer.valueOf(2));
		currencyDecimalPlace.put(TWD, Integer.valueOf(0));
		currencyDecimalPlace.put(USD, Integer.valueOf(2));
		currencyDecimalPlace.put(JPY, Integer.valueOf(0));
	}
	
	/**
	 * 取得帳戶欠款的欄位名稱
	 * @param currency
	 * @return
	 */
	static public String getCurrencyDebtsColName(String currency){
		return debtsColName.get(currency);
	}
	
	/**
	 * 取得帳戶餘額的欄位名稱
	 * @param currency
	 * @return
	 */
	static public String getCurrencyAccountColName(String currency){
		return accountColName.get(currency);
	}
	
	/**
	 * 取得帳戶使用的數字格式
	 * @param currency
	 * @return
	 */
	static public String getCurrencyFormat(String currency){
		return currencyFormat.get(currency);
	}
	
	/**
	 * 取得帳戶小數點後顯示幾位
	 * @param currency
	 * @return
	 */
	static public int getCurrencyDecimalPlace(String currency){
		return currencyDecimalPlace.get(currency);
	}
}
