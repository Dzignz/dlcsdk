<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.net.URLEncoder"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<form id="sendform" action="https://auth.login.yahoo.co.jp/oauth/v2/get_request_token">
<input name="oauth_consumer_key" value="dj0yJmk9V2VWVkRIYVNIOUxBJmQ9WVdrOWMxbGhNV2huTmpRbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD02MA--" />
<input name="oauth_nonce" value="PLAINTEXT" />
<input name="oauth_signature_method" value="PLAINTEXT" />
<input name="oauth_signature" value=<%=URLEncoder.encode("url_encode(3cb3c47286789580b79089c440cb5fbcbeca68b8)&url_encode(abcd)","UTF-8")%> />
<input name="oauth_timestamp" value="<%=System.currentTimeMillis() %>" />
<input name="oauth_version" value="1.0" />
<input name="oauth_callback" value="http://ads.mogan.com.tw/NetPassPort/oAuthYahooBack.jsp" />

<input type="submit" />
 </form>
</body>
</html>