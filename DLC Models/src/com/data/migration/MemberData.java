package com.data.migration;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import com.mogan.face.MigrationFace;
import com.mogan.sys.DBConn;
import com.mogan.sys.code.MD5;
import com.mogan.sys.log.SysLogger4j;
import com.mogan.sys.model.ProtoModel;

public class MemberData extends ProtoModel implements MigrationFace{
//	DBConn conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");
	private String MIGR_NO;
	private long dataCount=0;
	private long migrCount=0;
	private DBConn conn;
	
	/**
	 * 
	 * @param servletContext
	 * @param appId
	 * @param migrNo
	 */
	public MemberData(ServletContext servletContext, String appId,String migrNo){
		this.setModelServletContext(servletContext);
		this.setAppId(appId);
		
		this.MIGR_NO=migrNo;
		conn = (DBConn) this.getModelServletContext().getAttribute("DBConn");

	}

	
	@Override
	public int getDataCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMigrCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMigrRate() {
		// TODO Auto-generated method stub
		return 0;
	}

	private String getNewMemberId(String name){
		ArrayList<Map> dataList=conn.query("mogan-DB", "SELECT member_id FROM member_data where name='"+name+"'");
		
		if (dataList.size()==0 || dataList.get(0).get("member_id")==null){
			return null;
		}else{
			return (String) dataList.get(0).get("member_id");
		}
	}
	
	/**
	 * 開始執行資料更新
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 */
	public void doMigr()  {
		boolean flag = true;
		
		while (flag) {
			flag = false;
			ArrayList dataList = conn
					.queryWithPage(
							"mogan-tw",
							"SELECT * FROM web_member WHERE migr_flag IS NULL",
							//"SELECT * FROM web_member WHERE migr_flag IS NULL AND verify=1",
							0, 1000);
			SysLogger4j.info("Migr Start...." + MIGR_NO + " "
					+ dataList.size());
			for (int i = 0; i < dataList.size(); i++) {
				Map tempData = (Map) dataList.get(i);
				Map newData = new HashMap();
				Map checkData = new HashMap();

				// newData.put("member_id",);//系統ID ***自動產生
				newData.put("name", tempData.get("name"));// 帳號
				newData.put("pwd", MD5.getMD5Digest((String) tempData
						.get("password")));// 密碼 轉換為MD5
				newData.put("first_name", ((String) tempData.get("realname"))
						.substring(0, 1));// 姓
				newData.put("last_name", ((String) tempData.get("realname"))
						.substring(1));// 名 ***無對應值
				newData.put("nationality", tempData.get("nationality"));// 國藉
				// newData.put("person_id",);//身份證字號 ***無對應值
				newData.put("sex", tempData.get("sex"));// 性別
				newData.put("birthday", tempData.get("birthday"));// 生日
				newData.put("tel", tempData.get("tel"));// 電話
				newData.put("email", tempData.get("email"));// e-mail
				newData.put("postcode", tempData.get("postcode"));// 郵遞區號
				newData.put("address", tempData.get("address"));// 地址
				newData.put("time_at", tempData.get("time_at"));// 建立時間

				newData.put("sum_ntd", tempData.get("ntd"));// 台幣餘額
				if (tempData.get("debts")==null){
					newData.put("debts_ntd", "0");// 台幣借款
				}else{
					newData.put("debts_ntd", tempData.get("debts"));// 台幣借款
				}
				
				newData.put("sum_usd", tempData.get("usd"));// 美金餘額
				// newData.put("debts_usd",);//美金借款 ***無對應值
				newData.put("sum_rmb", tempData.get("rmb"));// 人民幣餘額
				// newData.put("debts_rmb",);//人民幣借款 ***無對應值
				newData.put("sum_yen", tempData.get("yen"));// 日圓餘額
				// newData.put("debts_yen",);//日圓借款 ***無對應值

				newData.put("dividend", tempData.get("dividend"));// 紅利點數
				newData.put("previsited", tempData.get("previsited"));// 上次登入時間
				newData.put("visited", tempData.get("visited"));// 登入次數

				newData.put("set_language", "MD-04");// 使用語系
				if (tempData.get("nationality").equals("台灣")) {
					newData.put("set_language", "MD-02");// 使用語系
				}

				newData.put("verify", tempData.get("verify"));// 驗證狀況
				// newData.put("recommend",getNewMemberId((String) tempData.get("recommend")));//推薦人
				newData.put("recommend", tempData.get("recommend"));// 推薦人
				newData.put("virtual_account", tempData.get("VirtualAccount"));// 虛擬帳號
				newData.put("note", "1");// 是否為舊會員
				newData.put("delete_flag", "1");// 刪除狀態
				
				checkData.put("name", newData.get("name"));
				//conn.newData("mogan-VMDB", "member_data", newData);
				try {
					conn.newData("mogan-VMDB", "member_data",checkData, newData);
				} catch (UnsupportedEncodingException e) {
					SysLogger4j.error("會員資料轉移",e);
				} catch (SQLException e) {
					SysLogger4j.error("會員資料轉移",e);
				}

				Map conditionMap = new HashMap();
				conditionMap.put("id", tempData.get("id"));
				Map dataMap = new HashMap();
				dataMap.put("migr_no", MIGR_NO);
				dataMap.put("migr_flag", "1");
				try {
					conn.update("mogan-tw", "web_member", conditionMap, dataMap);
				} catch (UnsupportedEncodingException e) {
					SysLogger4j.error("會員資料轉移",e);
				} catch (SQLException e) {
					SysLogger4j.error("會員資料轉移",e);
				}
				//break;
			}
			
			dataList = conn
					.queryWithPage(
							"mogan-tw",
							"SELECT * FROM web_member WHERE migr_flag IS NULL",
							0, 1000);
			
			if (dataList.size() > 0) {
				flag = true;
			}
		}
	}
	
	/**
	 * 初始化資料
	 */
	@Override
	public boolean startMigr() {
		//取得資料總數
		ArrayList<Map> dataList=conn.query("mogan-tw", "SELECT COUNT(*) AS COUNT FROM web_member WHERE migr_flag IS NULL");
		this.dataCount=Long.parseLong((String)dataList.get(0).get("COUNT"));
		this.migrCount=0;
		return true;
	}

	@Override
	public boolean stopMigr() {
		// TODO Auto-generated method stub
		return false;
	}

	
}
