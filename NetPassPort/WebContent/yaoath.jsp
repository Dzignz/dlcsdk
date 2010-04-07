<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ page import="java.util.Date"%>
<%@ page import="java.io.InputStream"%>
<%@ page import="java.net.URL"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="javax.crypto.spec.SecretKeySpec"%>
<%@ page import="javax.crypto.Mac"%>
<%@ page import="org.apache.hadoop.hbase.util.Base64"%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<%

String  oauth_nonce="test";
String	oauth_timestamp=String.valueOf(new Date().getTime()/1000);

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
	oauth_nonce=newPassword.toString();
	session.setAttribute("oauth_nonce",oauth_nonce);
	
	//*
	//HMAC-SHA1
	String httpMethod="GET";
	String apiUrl="https://auth.login.yahoo.co.jp/oauth/v2/get_request_token";
	//apiUrl="https://auth.login.yahoo.co.jp/oauth/v2/get_request_token";
	String paramStr="oauth_callback="+URLEncoder.encode("http://ap.mogan.com.tw/NetPassPort/yahooback.jsp","UTF-8")+"&" +
	"oauth_consumer_key=dj0yJmk9SG1QdE12OWNIUUlmJmQ9WVdrOVRERnhjMHhaTjJzbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD04Yg--&" +
	"oauth_nonce="+oauth_nonce+"&" +
	"oauth_signature_method=HMAC-SHA1&" +
	"oauth_timestamp="+oauth_timestamp+"&" +
	"oauth_version=1.0";

	String signBaseStr=httpMethod+"&"+URLEncoder.encode(apiUrl,"UTF-8")+"&"+URLEncoder.encode(paramStr,"UTF-8");
    SecretKeySpec signingKey = new SecretKeySpec("cd7c3fdc9a779feaa19100482196e2d00b828f11&".getBytes(), "HmacSHA1");
    Mac mac = null;
    try {
        mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
    }
    catch(Exception e) {
        throw new RuntimeException(e);
    }
    byte[] rawHmac = mac.doFinal(signBaseStr.getBytes());
    String oauth_signature=Base64.encodeBytes(rawHmac);

	
	String urlstring="https://auth.login.yahoo.co.jp/oauth/v2/get_request_token?";
	urlstring+="oauth_callback=http://ap.mogan.com.tw/NetPassPort/yahooback.jsp&";
	urlstring+="oauth_consumer_key=dj0yJmk9SG1QdE12OWNIUUlmJmQ9WVdrOVRERnhjMHhaTjJzbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD04Yg--&";
	urlstring+="oauth_nonce="+oauth_nonce+"&";
	urlstring+="oauth_signature_method=HMAC-SHA1&";
	urlstring+="oauth_signature="+oauth_signature+"&";
	urlstring+="oauth_timestamp="+oauth_timestamp+"&";
	urlstring+="oauth_version=1.0";
	
	//*/
	
	/*
	//PLAINTEXT
	String urlstring="https://auth.login.yahoo.co.jp/oauth/v2/get_request_token?";
	urlstring+="oauth_consumer_key=dj0yJmk9SG1QdE12OWNIUUlmJmQ9WVdrOVRERnhjMHhaTjJzbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD04Yg--&";
	urlstring+="oauth_nonce="+oauth_nonce+"&";
	urlstring+="oauth_signature_method=PLAINTEXT&";
	urlstring+="oauth_signature=cd7c3fdc9a779feaa19100482196e2d00b828f11%26&";
	urlstring+="oauth_timestamp="+oauth_timestamp+"&";
	urlstring+="oauth_version=1.0&";
	urlstring+="oauth_callback=http://ap.mogan.com.tw/NetPassPort/yahooback.jsp&";
	*/
	/*
	InputStream in=(new URL(urlstring)).openStream();
	
	StringBuffer yahooResStrBuf=new StringBuffer();
    int c;
    while ((c = in.read()) != -1)
    	yahooResStrBuf.append((char) c);
            //System.out.print(c);
    in.close(); 
    
    String [] yahooResStr=yahooResStrBuf.toString().split("&");
  	*/  
%>

<body>

====================<br />
rawHmac=<%//new String(rawHmac) %><br /><br />
signBaseStr=<%=signBaseStr %><br /><br />
oauth_signature=<%=oauth_signature %><br /><br />
oauth_nonce=<%=oauth_nonce %><br /><br />
oauth_timestamp=<%=oauth_timestamp %><br /><br />
yahooResStr=<%="" %><br /><br />
urlstring=<%=urlstring %><br />

====================<br />
<%

	Map<String,String> yahooResMap=new HashMap();
/*
 for (int i=0;i<yahooResStr.length;i++){
	 yahooResMap.put(yahooResStr[i].split("=")[0],yahooResStr[i].split("=")[1]);
	 out.println(yahooResStr[i].split("=")[0]+"="+yahooResStr[i].split("=")[1]+"<br /><br />");
 }
 session.setAttribute("oauth_token_secret",yahooResMap.get("oauth_token_secret"));
 
 //*/
 //window.location = "<%=URLDecoder.decode(yahooResMap.get("xoauth_request_auth_url"),"UTF-8")%>
%>

<input value='open japen yahoo link' type='button' onclick='openyahoo()' / >


<script type="text/javascript" >
function openyahoo(){
	window.location = "<%//=URLDecoder.decode(yahooResMap.get("xoauth_request_auth_url"),"UTF-8") %>"
	
}
</script>

</body>
</html>