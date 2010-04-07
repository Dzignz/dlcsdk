<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.io.InputStream"%>
<%@ page import="java.net.URL"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="javax.crypto.spec.SecretKeySpec"%>
<%@ page import="javax.crypto.Mac"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.net.URLConnection"%>

<%@ page import="org.apache.hadoop.hbase.util.Base64"%>



<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<%

String	oauth_timestamp=String.valueOf(new Date().getTime()/1000);
String	oauth_token=request.getParameter("oauth_token");
String	oauth_verifier=request.getParameter("oauth_verifier");


String  oauth_nonce=(String)session.getAttribute("oauth_nonce");

String urlstring="https://auth.login.yahoo.co.jp/oauth/v2/get_token?";
Map<String,String> yahooResMap=new HashMap();


String httpMethod="GET";
String apiUrl="https://auth.login.yahoo.co.jp/oauth/v2/get_token";
String paramStr="oauth_consumer_key=dj0yJmk9SG1QdE12OWNIUUlmJmQ9WVdrOVRERnhjMHhaTjJzbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD04Yg--&" +
				"oauth_nonce="+oauth_nonce+"&" +
				"oauth_signature_method=HMAC-SHA1&" +
				"oauth_timestamp="+oauth_timestamp+"&" +
				"oauth_token="+URLEncoder.encode((String)session.getAttribute("access_oauth_token"),"UTF-8")+"&"+
				"oauth_verifier="+oauth_verifier+"&"+
				"oauth_version=1.0&"+
				"start=1";

String signBaseStr=httpMethod+"&"+URLEncoder.encode(apiUrl,"UTF-8")+"&"+URLEncoder.encode(paramStr,"UTF-8");

SecretKeySpec signingKey = new SecretKeySpec(new String("cd7c3fdc9a779feaa19100482196e2d00b828f11&").getBytes(), "HmacSHA1");
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


	urlstring="https://auth.login.yahoo.co.jp/oauth/v2/get_token?";
	urlstring+="oauth_consumer_key=dj0yJmk9SG1QdE12OWNIUUlmJmQ9WVdrOVRERnhjMHhaTjJzbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD04Yg--&";
	urlstring+="oauth_token="+oauth_token+"&";
	urlstring+="oauth_signature_method=PLAINTEXT&";
	urlstring+="oauth_signature=cd7c3fdc9a779feaa19100482196e2d00b828f11%26"+session.getAttribute("oauth_token_secret")+"&";//oauth_token
	urlstring+="oauth_timestamp="+oauth_timestamp+"&";
	urlstring+="oauth_nonce="+oauth_nonce+"&";
	urlstring+="oauth_verifier="+oauth_verifier+"&";
	urlstring+="oauth_version=1.0&";


	InputStream in=(new URL(urlstring)).openStream();

	StringBuffer yahooResStrBuf=new StringBuffer();
	int c;
	while ((c = in.read()) != -1)
		yahooResStrBuf.append((char) c);
	        //System.out.print(c);
	in.close(); 

	String [] yahooResStr=yahooResStrBuf.toString().split("&");



	for (int i=0;i<yahooResStr.length;i++){
		 yahooResMap.put(yahooResStr[i].split("=")[0],yahooResStr[i].split("=")[1]);
		 out.println(yahooResStr[i].split("=")[0]+"="+yahooResStr[i].split("=")[1]+"<br />");
	}
	//*/
	session.setAttribute("access_oauth_token",yahooResMap.get("oauth_token"));
	session.setAttribute("access_oauth_token_secret",yahooResMap.get("oauth_token_secret"));
	session.setAttribute("oauth_authorization_expires_in",yahooResMap.get("oauth_authorization_expires_in"));
//}


//	session.getAttribute("access_oauth_token")
out.println("XX"+session.getAttribute("access_oauth_token"));


out.println("====================<br />");


oauth_timestamp=String.valueOf(new Date().getTime()/1000);

 httpMethod="GET";
 apiUrl="http://auctions.yahooapis.jp/AuctionWebService/V2/myBidList";
 paramStr="oauth_consumer_key=dj0yJmk9SG1QdE12OWNIUUlmJmQ9WVdrOVRERnhjMHhaTjJzbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD04Yg--&" +
				"OAuth realm=yahooapis.jp&"+
				"oauth_nonce="+oauth_nonce+"&" +
				"oauth_signature_method=HMAC-SHA1&" +
				"oauth_timestamp="+oauth_timestamp+"&" +
				"oauth_token="+URLEncoder.encode((String)session.getAttribute("access_oauth_token"),"UTF-8")+"&"+
				"oauth_version=1.0&"+
				"start=1";
 


//paramStr="oauth_callback=http%3A%2F%2Fwww.example.com%2F&oauth_consumer_key=test_consumer_key&oauth_nonce=3a1f92580ea537395b42d2d1cdf5fe29&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1258019462&oauth_version=1.0";
 signBaseStr=httpMethod+"&"+URLEncoder.encode(apiUrl,"UTF-8")+"&"+URLEncoder.encode(paramStr,"UTF-8");
//signBaseStr="GET&http%3A%2F%2Fphotos.example.net%2Fphotos&file%3Dvacation.jpg%26oauth_consumer_key%3Ddpf43f3p2l4k3l03%26oauth_nonce%3Dkllo9940pd9333jh%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1191242096%26oauth_token%3Dnnch734d00sl2jdk%26oauth_version%3D1.0%26size%3Doriginal";
 signingKey = new SecretKeySpec(new String("cd7c3fdc9a779feaa19100482196e2d00b828f11&").getBytes(), "HmacSHA1");
 mac = null;
try {
    mac = Mac.getInstance("HmacSHA1");
    mac.init(signingKey);
}
catch(Exception e) {
    throw new RuntimeException(e);
}
byte[] rawHmac2 = mac.doFinal(signBaseStr.getBytes());
 oauth_signature=Base64.encodeBytes(rawHmac2);


urlstring="http://auctions.yahooapis.jp/AuctionWebService/V2/myBidList?";
urlstring+="OAuth realm=yahooapis.jp"+"&";
urlstring+="oauth_consumer_key=dj0yJmk9SG1QdE12OWNIUUlmJmQ9WVdrOVRERnhjMHhaTjJzbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD04Yg--&";
urlstring+="oauth_nonce="+oauth_nonce+"&";
urlstring+="oauth_signature_method=HMAC-SHA1&";
urlstring+="oauth_timestamp="+oauth_timestamp+"&";
urlstring+="oauth_token="+session.getAttribute("access_oauth_token")+"&";
urlstring+="oauth_version=1.0&";
urlstring+="start=1&";
urlstring+="oauth_signature="+oauth_signature+"";




//urlstring+="realm=yahooapis.jp&";






//=============

/*
 in=(new URL(urlstring)).openStream();

 yahooResStrBuf=new StringBuffer();

while ((c = in.read()) != -1)
	yahooResStrBuf.append((char) c);
        //System.out.print(c);
in.close(); 

String [] yahooResStr2=yahooResStrBuf.toString().split("&");

Map<String,String> yahooResMap2=new HashMap();

for (int i=0;i<yahooResStr2.length;i++){
	 yahooResMap2.put(yahooResStr2[i].split("=")[0],yahooResStr2[i].split("=")[1]);
	 out.println(yahooResStr2[i].split("=")[0]+"="+yahooResStr2[i].split("=")[1]+"<br />");
}
*/
%>
<br />

#yahooResStrBuf=<%=yahooResStrBuf %><br /><br />

#urlstring=<%=urlstring %><br /><br />
#signBaseStr=<%=signBaseStr %><br /><br />
#key=<%="cd7c3fdc9a779feaa19100482196e2d00b828f11&"+yahooResMap.get("oauth_token_secret") %><br /><br />

====================<br />
<input value='open japen yahoo link' type='button' onclick='openyahoo()' / ><br />


====================<br />
<script type="text/javascript" >
function openyahoo(){
	window.location = "<%=urlstring%>" ;
}
</script>

</body>
</html>