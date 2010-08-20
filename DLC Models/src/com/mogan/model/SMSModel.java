package com.mogan.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.mogan.model.netAgent.HTMLNodeFilter;
import com.mogan.model.netAgent.NetAgent;
import com.mogan.sys.model.ProtoModel;
import com.mogan.sys.model.ServiceModelFace;

public class SMSModel extends ProtoModel implements ServiceModelFace {
	private static Logger logger = Logger.getLogger(SMSModel.class.getName() );
	private final static String LOG_SERVER_URL = "LOG_SERVER_URL";
	private final static String SEND_SERVER_URL = "SEND_SERVER_URL";
	private final static String SEND_TEXT = "SEND_TEXT";
	private final static String QUERY_LOG = "QUERY_LOG";
	private final static String ACCOUNT = "ACCOUNT";
	private final static String PWD = "PWD";
	private final static String CLASS_NAME = "SMSModel";

	/*
	public SMSModel(){
		ModelManager.
		serviceModel.setProperties(modelManager.getModelProperties(this.getClass().getName()));
		this.setProperties(p)
	}
	*/
	
	@Override
	public JSONArray doAction(Map parameterMap) throws Exception {
		// TODO Auto-generated method stub
		JSONArray jArray = new JSONArray();
		logger.info("[INFO]SMSModel ACTION start. " + this.getAct());
		if (this.getAct().equals("SEND_TEXT")) {
			JSONArray numberJarray = JSONArray.fromObject(parameterMap
					.get("NUMBER_JARRAY"));
			String msg = (String) parameterMap.get("MSG");
			jArray = sendText(numberJarray, msg);
		} else if (this.getAct().equals("QUERY_LOG")) {
			String dateStr = (String) parameterMap.get("DATE_STR");
			jArray = queryLog(dateStr);
		}
		return jArray;
	}

	public JSONArray sendText(String number,String name,String checkKey,String msg){
		JSONArray numberJarray=new JSONArray();
		JSONObject jObj=new JSONObject();
		jObj.put("NUMBER", number);
		jObj.put("NAME", name);
		jObj.put("CHECK_KEY", checkKey);
		numberJarray.add(jObj);
		return sendText(numberJarray,msg);
	}
	
	/**
	 * <p>
	 * <font size=7 color=red>發送簡訊，ACTION = SEND_TEXT</font>
	 * </p>
	 * NUMBER_JARRAY
	 * MSG
	 * numberJarray必須為JSON的格式<BR /> 範例 :
	 * [{"NUMBER":"0988123654","NAME":"dian","CHECK_KEY":"0001"},{"NUMBER":"0999147852","NAME":"mary","CHECK_KEY":"0002"}]<BR /> 三項必須屬性<li>NUMBER-號碼</li>
	 * <li>NAME-接收者名稱</li><li>CHECK_KEY-交易代號</li> <BR /><BR /> 收訊電話號碼 - 國際簡訊規格 : (+或是00)(國碼 )(去0之手機號碼).<BR /> 例 : +8613913560366 or 008613913560366<BR
	 * /> 國際簡訊自動以國際簡訊計費<BR /> 台灣與大陸(不含香港、澳門)之手機門號無論是否帶入國碼都可為有效號碼<BR /> <BR /> 接收者名稱<BR /> (最好有，以便客戶查詢發訊結果時知道收訊人的姓名)<BR /> 如果無，SMSe自動以收訊號碼當作收訊人姓名<BR />
	 * <BR />
	 * 
	 * @param numberJarray
	 *            - 傳送的對向
	 * @param msg
	 *            - 要傳送的訊息
	 * @return
	 */
	public JSONArray sendText(JSONArray numberJarray, String msg) {
		try {
			msg=URLEncoder.encode(msg, "big5");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONArray jArray = new JSONArray();
		NetAgent nAgent = new NetAgent();
		/*
		 * yhy-貴公司的帳號 dc2a-貴公司的密碼 od_sob-資料庫的keyword(20字內)請自行利用 movetel-收訊電話號碼 數字 +號 ;號 name-收訊人姓名 bf-小於SMSe系統時間或無時會立即發送 sb-69個全形字一則，
		 * zchttp-執行完成後要返回的HTTP位置 http_sob-
		 */
		StringBuffer numbers = new StringBuffer();
		StringBuffer names = new StringBuffer();
		StringBuffer checkKey = new StringBuffer();
		for (int i = 0; i < numberJarray.size(); i++) {
			JSONObject jObj = numberJarray.getJSONObject(i);
			if (numbers.length() > 0) {
				numbers.append(";");
			}
			if (jObj.has("NUMBER")) {
				numbers.append(jObj.getString("NUMBER"));
			}

			if (names.length() > 0) {
				names.append(";");
			}
			if (jObj.has("NAME")) {
				try {
					names.append(URLEncoder.encode(jObj.getString("NAME"), "big5"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					names.append(jObj.getString("NAME"));
				}
				
			}

			if (checkKey.length() > 0) {
				checkKey.append(";");
			}
			if (jObj.has("CHECK_KEY")) {
				try {
					checkKey.append(URLEncoder.encode(jObj.getString("CHECK_KEY"), "big5"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					checkKey.append(jObj.getString("CHECK_KEY"));
				}
			}
		}

		String url = this.getProperty(SEND_SERVER_URL)
				+ this.getProperty(SEND_TEXT) + "?yhy="
				+ this.getProperty(ACCOUNT) + "&dc2a=" + this.getProperty(PWD)
				+ "&od_sob=" + checkKey + "&movetel=" + numbers + "&name="
				+ names + "&sb=" + msg ;
		
		logger.info(url);
		nAgent.getDataWithGet(url);
		logger.info(nAgent.getResponseBody());
		jArray.add("1");
		return jArray;
	}

	/**
	 * <p>
	 * <font size=7 color=red>發送簡訊，ACTION = QUERY_LOG</font>
	 * </p>
	 * <li>DELIVRD…….成功</li> <li>UNDELIV…….空號</li> <li>TELERR………號碼錯誤</li> <li>EXPIRED……..失敗</li> <li>ERR……………無法送達</li>
	 * [{"DATE":"2009/12/24 下午 04:09:25","NUMBER":"0910054930","STATUS":"DELIVRD","CHECK_KEY":"0001"},{"DATE":"2009/12/24 下午 04:09:27","NUMBER":
	 * "0999054930"
	 * ,"STATUS":"號碼錯誤","CHECK_KEY":"0002"},{"DATE":"2009/12/24 下午 04:12:41","NUMBER":"0910054930","STATUS":"DELIVRD","CHECK_KEY":"0001"},{
	 * "DATE":"2009/12/24 下午 04:12:41"
	 * ,"NUMBER":"0999054930","STATUS":"號碼錯誤","CHECK_KEY":"0002"},{"DATE":"2009/12/24 下午 04:13:38","NUMBER":"0910054930"
	 * ,"STATUS":"DELIVRD","CHECK_KEY"
	 * :"0001"},{"DATE":"2009/12/24 下午 04:13:38","NUMBER":"0999054930","STATUS":"號碼錯誤","CHECK_KEY":"0002"},{"DATE":"2009/12/24 下午 04:16:02"
	 * ,"NUMBER":"0910054930"
	 * ,"STATUS":"DELIVRD","CHECK_KEY":"0001"},{"DATE":"2009/12/24 下午 04:16:02","NUMBER":"0910054930","STATUS":"DELIVRD","CHECK_KEY"
	 * :"0002"},{"DATE":"2009/12/24 下午 04:52:45"
	 * ,"NUMBER":"0910054930","STATUS":"DELIVRD","CHECK_KEY":"0002"},{"DATE":"2009/12/24 下午 04:52:45","NUMBER"
	 * :"0910054930","STATUS":"DELIVRD","CHECK_KEY":"0002"}] dateStr日期條件格式為2009/12/25
	 * 
	 * @param dateStr
	 *            -日期條件
	 * @return 回傳內容為JSON格式，陣列形態，每個項目由 <li>DATE-發送時間</li> <li>NUMBER-發送號碼</li> <li>STATUS-送送狀態</li> <li>CHECK_KEY-檢查碼</li>
	 */
	public JSONArray queryLog(String dateStr) {
		JSONArray jArray = new JSONArray();
		NetAgent nAgent = new NetAgent();
		String url = this.getProperty(LOG_SERVER_URL)
				+ this.getProperty(QUERY_LOG) + "?yhy="
				+ this.getProperty(ACCOUNT) + "&dc2a=" + this.getProperty(PWD)
				+ "&bf=" + dateStr + "&sms_sb=1";
		logger.info(url);
		nAgent.getDataWithGet(url);

		JSONObject jObj = new JSONObject();
		try {
			nAgent.setResponseBody(new String(nAgent.getResponseBody()
					.getBytes("ISO-8859-1"), "BIG5"));
			logger.info( nAgent.getResponseBody());
			logger.info("===============");
			NodeList nodes = nAgent.filterItem(new HTMLNodeFilter("p"));

			for (int i = 0; i < nodes.size(); i = i + 2) {
				jObj = new JSONObject();
				logger.info(i + ":"
						+ nodes.elementAt(i).toPlainTextString());
				System.out
						.println(i
								+ ":"
								+ nodes.elementAt(i).toPlainTextString().split(
										"\\*").length);
				jObj.put("DATE", nodes.elementAt(i).toPlainTextString().split(
						"\\*")[0]);
				jObj.put("NUMBER", nodes.elementAt(i).toPlainTextString()
						.split("\\*")[1]);
				jObj.put("STATUS", nodes.elementAt(i).toPlainTextString()
						.split("\\*")[2]);
				/*
				jObj.put("CHECK_KEY", nodes.elementAt(i).toPlainTextString()
						.split("\\*")[3]);
						*/
				jArray.add(jObj);
			}

			logger.info(nAgent.getResponseBody());
			logger.info(jArray);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		jArray.add(nAgent.getResponseBody());
		return jArray;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SMSModel sm = new SMSModel();
		Properties p = new Properties();
		p.put(SEND_SERVER_URL, "http://smsmo.smse.com.tw");
		p.put(LOG_SERVER_URL, "http://sms.smse.com.tw");
		p.put(SEND_TEXT, "/STANDARD/sms_fu.asp");
		p.put(QUERY_LOG, "/STANDARD/TVRVRE_FU_B.ASP");
		p.put(ACCOUNT, "MORGAN");
		p.put(PWD, "24266676");
		sm.setProperties(p);

		JSONArray jArray = new JSONArray();
		JSONObject jObj = new JSONObject();
		
		jObj.put("NAME","郭發福");
		jObj.put("NUMBER","0918081826");
		//jArray.add(jObj);
		
		jObj.put("NAME","蘇美秀");
		jObj.put("NUMBER","0930939297");
		jArray.add(jObj);
		
		jObj.put("NAME","樺昌胡醫師");
		jObj.put("NUMBER","0986888011");
		jArray.add(jObj);
		
		jObj.put("NAME","杨金刚");
		jObj.put("NUMBER","13086889300");
		jArray.add(jObj);
		
		jObj.put("NAME","王強");
		jObj.put("NUMBER","0926744256");
		jArray.add(jObj);
		
		jObj.put("NAME","鍾采霏");
		jObj.put("NUMBER","886918700820");
		jArray.add(jObj);
		
		jObj.put("NAME","歐任琦");
		jObj.put("NUMBER","0916890196");
		jArray.add(jObj);
		
		jObj.put("NAME","廖文振");
		jObj.put("NUMBER","0939578338");
		jArray.add(jObj);
		
		jObj.put("NAME","曾婉容");
		jObj.put("NUMBER","0935915271");
		jArray.add(jObj);
		
		jObj.put("NAME","林育德");
		jObj.put("NUMBER","0927639755");
		jArray.add(jObj);
		
		jObj.put("NAME","顏健峯");
		jObj.put("NUMBER","0928340303");
		jArray.add(jObj);
		
		jObj.put("NAME","林雅麗");
		jObj.put("NUMBER","0927338466");
		jArray.add(jObj);
		
		jObj.put("NAME","林錫欽");
		jObj.put("NUMBER","0920966188");
		jArray.add(jObj);
		
		jObj.put("NAME","謝惠貞");
		jObj.put("NUMBER","0912304154");
		jArray.add(jObj);
		
		jObj.put("NAME","柳琤");
		jObj.put("NUMBER","18958185088");
		jArray.add(jObj);
		
		jObj.put("NAME","陈志皇");
		jObj.put("NUMBER","13817718777");
		jArray.add(jObj);
		
		jObj.put("NAME","蕭潤榮");
		jObj.put("NUMBER","0937220961");
		jArray.add(jObj);
		
		jObj.put("NAME","邢開祥");
		jObj.put("NUMBER","0963298836");
		jArray.add(jObj);
		
		jObj.put("NAME","王卫星");
		jObj.put("NUMBER","13901393485");
		jArray.add(jObj);			
			for (int i=0;i<jArray.size();i++){
				JSONArray tempJArray=new JSONArray();
				tempJArray.add(jArray.getJSONObject(i));
				//sm.sendText(tempJArray,"親愛的摩根會員"+jArray.getJSONObject(i).getString("NAME")+"您好,因上週日(02/28)晚間八點到九點 間,伺服器發生網路設備錯誤,影響會員下標,為補償會員損失及表示摩根歉意,將提供一次手續費減免予以受影響會員,摩根會記取此次經驗加強硬體維護,同時希望會員保持對摩根的愛戴.");
				
				//break;
			}
			sm.queryLog("2010/03/02");

		// sm.sendText(jArray,"測試簡訊");

	}

}
