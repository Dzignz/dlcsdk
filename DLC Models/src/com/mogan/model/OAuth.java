package com.mogan.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;

import org.apache.hadoop.hbase.util.Base64;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.mogan.face.NetAgentModel;
import com.mogan.io.FileIO;
import com.mogan.model.netAgent.HTMLNodeFilter;
import com.mogan.model.netAgent.NetAgent;
import com.mogan.model.netAgent.NetAgentYJ;
import com.mogan.sys.DBConn;
import com.mogan.sys.ProtoModel;
import com.mogan.sys.ServiceModelFace;

public class OAuth extends ProtoModel implements ServiceModelFace{
	NetAgentTool naTool=new NetAgentTool();
	
	
	public static void main (String [] args){
		String baseStr="GET&https%3A%2F%2Fauth.login.yahoo.co.jp%2Foauth%2Fv2%2Fget_request_token&oauth_callback%3Dhttp%253A%252F%252Fap.mogan.com.tw%252FNetPassPort%252Fyahooback.jsp%26oauth_consumer_key%3Ddj0yJmk9SG1QdE12OWNIUUlmJmQ9WVdrOVRERnhjMHhaTjJzbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD04Yg--%26oauth_nonce%3Dgtkt4Frkyy%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1269497173%26oauth_version%3D1.0";
		
		System.out.println("getReqToken1 :: "+baseStr);
	}
	
	private static String getRandStr(){
		int[] word = new int[10];
		int mod;
		for (int i = 0; i < 10; i++) {
			mod = (int) ((Math.random() * 7) % 3);
			if (mod == 1) { // 數字
				word[i] = (int) ((Math.random() * 10) + 48);
			} else if (mod == 2) { // 大寫英文
				word[i] = (char) ((Math.random() * 26) + 65);
			} else { // 小寫英文
				word[i] = (char) ((Math.random() * 26) + 97);
			}
		}
		StringBuffer newPassword = new StringBuffer();
		for (int j = 0; j < 10; j++) {
			newPassword.append((char) word[j]);
		}
		return newPassword.toString();
	}
	
    private static String getHmacSha1(byte[]data, byte[] key) {
    	System.out.println("[DEBUG] getHmacSha1");
        SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        byte[] rawHmac = mac.doFinal(data);
        String oauth_signature=Base64.encodeBytes(rawHmac);
        return oauth_signature;
    }
    

    
    /**
     * 取得登入驗證碼
     * @return
     */
	private JSONArray getReqToken(){
		JSONArray jArray=new JSONArray();
		String oauth_nonce=getRandStr();
		this.getModelServletContext().setAttribute("oauth_nonce", oauth_nonce);
		String	oauth_timestamp=String.valueOf(new Date().getTime()/1000);
		NetAgent na=new NetAgent();
		try {
		String httpMethod="GET";
		String apiUrl="https://auth.login.yahoo.co.jp/oauth/v2/get_request_token";
		String paramStr="oauth_callback="+URLEncoder.encode("http://ap.mogan.com.tw/NetPassPort/AjaxPortal?MODEL_NAME=oAuth&APP_ID=26b782eb04abbd54efba0dcf854b158d&RETURN_TYPE=JSON&ACTION=YAHOO_CALLBACK&x=","UTF-8")+"&" +
							"oauth_consumer_key=dj0yJmk9SG1QdE12OWNIUUlmJmQ9WVdrOVRERnhjMHhaTjJzbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD04Yg--&" +
							"oauth_nonce="+oauth_nonce+"&" +
							"oauth_signature_method=HMAC-SHA1&" +
							"oauth_timestamp="+oauth_timestamp+"&" +
							"oauth_version=1.0";
		
		
			String signBaseStr=httpMethod+"&"+URLEncoder.encode(apiUrl,"UTF-8")+"&"+URLEncoder.encode(paramStr,"UTF-8");
			
			//System.out.println("[DEBUG] getHmacSha1::"+getHmacSha1(signBaseStr.getBytes(),"cd7c3fdc9a779feaa19100482196e2d00b828f11".getBytes()));
			
			String url="https://auth.login.yahoo.co.jp/oauth/v2/get_request_token?" +
				"oauth_callback=http://ap.mogan.com.tw/NetPassPort/studyoAuth.jsp&"+
				"oauth_consumer_key=dj0yJmk9SG1QdE12OWNIUUlmJmQ9WVdrOVRERnhjMHhaTjJzbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD04Yg--&" +
				"oauth_nonce="+oauth_nonce+"&" +
				"oauth_signature_method=HMAC-SHA1&" +
				"oauth_signature="+getHmacSha1(signBaseStr.getBytes(),"cd7c3fdc9a779feaa19100482196e2d00b828f11&".getBytes())+"&"+
				"oauth_timestamp="+oauth_timestamp+"&" +
				"oauth_version=1.0";
				
				
			
		na.getDataWithGet(url);
		String [] backpram=na.getResponseBody().split("&");
		Map backMap=new HashMap();
		
		for (int i=0;i<backpram.length;i++){
			backMap.put(backpram[i].split("=")[0],URLDecoder.decode( backpram[i].split("=")[1], "UTF-8"));
		}
		System.out.println("[DEBUG] getReqToken backMap:: "+backMap);
		naTool.printHeaders(na.getRequestHeaders());
		
		jArray.add(JSONObject.fromObject(backMap));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jArray;
	}
	
	/**
	 * Oauth 登入
	 * @param url
	 * @return
	 */
	private JSONArray loginYj(String url){
		JSONArray jArray=new JSONArray();
		NetAgent na=new NetAgent();
		NodeList nodes;
		try {
			System.out.println("[DEBUG] loginYj URL:: "+url);
			//*
			na.getDataWithGet(url);
			nodes = na.filterInputItem();
			// 取得input項目
			na.setParserNodesToPostDataMap(nodes);// 將nodes設入要post項目
			Map tempMap = new HashMap();// 設定帳號及密碼
			tempMap.put(na.YAHOO_JP_ACCOUNT, "wsoop08");
			tempMap.put(na.YAHOO_JP_PWD, "a12345678");
			na.putDataInPostDataMap(tempMap);// //將Map設入要post項目
			na.postMaptoData();// 將postMap轉成postData
			nodes = na.filterFormLoginHref();// 過濾登入項目
			naTool.setWebSiteURL(nodes.elementAt(0).getText());
			naTool.setWebSiteURL(na.getUrl(naTool.getWebSiteURL()));
			na.getDataWithPost(naTool.getWebSiteURL());
			//*/
			//na.setResponseBody(htmlcode);
			HTMLNodeFilter urlNf = new HTMLNodeFilter("atallow=1");
			//找出確認連結
			nodes=na.filterItem(urlNf);
			System.out.println("[DEBUG] nodes.size()::"+nodes.size());
			System.out.println("[DEBUG] nodes.getText()::"+nodes.elementAt(0).getText());
			System.out.println("[DEBUG] nodes.asString()::"+nodes.asString());
			String baseURL="https://auth.login.yahoo.co.jp/oauth/v2/";
			String allowURL=nodes.elementAt(0).getText().split("\"")[1];
			allowURL=allowURL.replaceFirst("\\./", baseURL);
			System.out.println("[DEBUG] loginYj allow:: "+allowURL);
			naTool.outputTofile(na.getResponseBody());
			na.getDataWithGet(allowURL);	
			naTool.outputTofile(na.getResponseBody());
			//na.getResponseHeader();
			System.out.println("[DEBUG] loginYj getHostUrl:: "+na.getHostConfiguration().getLocalAddress());
			naTool.printHeaders(na.getResponseHeader());
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return jArray;
	}
	
	/**
	 * YAHOO CALLBACK
	 * @param parameterMap
	 * @return
	 */
	private JSONArray yahooCallback(Map parameterMap){
		JSONArray jArray=new JSONArray();
		System.out.println("[DEBUG]  yahooCallback::"+parameterMap);
		return jArray;
	}
	
	@Override
	public JSONArray doAction(Map parameterMap) throws Exception {
		// TODO Auto-generated method stub		
		JSONArray jArray=new JSONArray();
		naTool.setModelServletContext(this.getModelServletContext());
		if(this.getAct().equals("GET_REQ_TOKEN")){
			jArray=getReqToken();
		}else if (this.getAct().equals("LOGIN_YAHOO_JP")){
			String loginURL=(String) parameterMap.get("LOG_IN_URL");
			jArray=loginYj(loginURL);
		}else if (this.getAct().equals("YAHOO_CALLBACK")){
			
		}
		return jArray;
	}
	
}
