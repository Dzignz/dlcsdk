package com.mogan.face;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.log4j.Logger;

import com.mogan.model.netAgent.NetAgent;
import com.mogan.sys.model.ProtoModel;

/**
 * 包含COOKIE的保留及HEADER的印出，及輸出到檔案
 * 
 * @author user
 */
public abstract class NetAgentModel extends ProtoModel {
	static private Logger logger  =  Logger.getLogger(NetAgentModel.class.getName());
	private String webSiteName;
	private String webSiteURL;
	private String version;
	private String appId;

	/**
	 * 回傳指定帳號的COOKIE
	 * 
	 * @param uId
	 * @return
	 */
	public Cookie[] getLoginSessionCookie(String appId, String uId) {

		Map<String, Map> loginCookieMap = (Map) this.getModelServletContext()
				.getAttribute(appId + "_LOGIN_COOKIE_MAP");

		// 判斷有沒有cookie資料
		// COOKIE有效期是否在7天內
		if (loginCookieMap == null) {
			return new NetAgent().getState().getCookies();
		} else if (loginCookieMap.get(uId) == null) {
			return new NetAgent().getState().getCookies();
		} else {
			long loginTime = 0;
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher isNum = pattern.matcher(String.valueOf(loginCookieMap.get(
					uId).get("LOGIN_TIME")));
			if (loginCookieMap.get(uId).get("LOGIN_TIME") != null
					&& !isNum.matches()) {
				loginTime = 0;
			} else {
				loginTime = Long.parseLong((String) loginCookieMap.get(uId)
						.get("LOGIN_TIME"));
			}
			long nowTime = System.currentTimeMillis();
			int loginDay = (int) ((nowTime - loginTime) / (1000 * 60 * 60 * 24));
			logger.info("LOGIN DATA::" +uId+" "+loginCookieMap.get("PWD")+" "+ loginDay);
			if (loginDay > 6) {
				return new NetAgent().getState().getCookies();
			}

		}
		/*
		 * if (this.getModelServletContext().getAttribute(appId + "_LOGIN_COOKIE_MAP") == null || ((Map)
		 * this.getModelServletContext().getAttribute(appId + "_LOGIN_COOKIE_MAP")).get(uId) == null) { return new NetAgent().getState().getCookies();
		 * }
		 */
		return (Cookie[]) ((Map) ((Map) this.getModelServletContext()
				.getAttribute(appId + "_LOGIN_COOKIE_MAP")).get(uId))
				.get("COOKIE");
	}

	/**
	 * 列印 Header資料
	 * 
	 * @param headers
	 */
	public void printHeaders(Header[] headers) {
		logger.info("[訊息] printHeaders#########");
		for (int i = 0; i < headers.length; i++) {
			logger.info("[訊息] #" + i + " " + headers[i].getName() + "::"
					+ headers[i].getValue());
		}
	}

	/**
	 * 列印cookies資料
	 * 
	 * @param cookies
	 */
	public void printCookies(Cookie[] cookies) {
		logger.info("[訊息] printCookies#########");
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			logger.info("Cookie: " + cookie.getName() + ", Value: "
					+ cookie.getValue() + ", IsPersistent?: "
					+ cookie.isPersistent() + ", Expiry Date: "
					+ cookie.getExpiryDate() + ", Comment: "
					+ cookie.getComment());
		}
	}

	/**
	 * 將COOKIE全部回傳，以Map型態回傳，key是uid，value是cookie
	 * 
	 * @return LOGIN_COOKIE_MAP<br /> key - uId <br /> value - Cookie[] <br />
	 */
	public Map getLoginCookieMap(String appId) {
		return (Map) this.getModelServletContext().getAttribute(
				appId + "_LOGIN_COOKIE_MAP");
	}

	/**
	 * 將指定帳號的COOKIE記錄下來
	 * 
	 * @param uId
	 * @param cookies
	 */
	public void setLoginCookieMap(String webSiteName, String uId,String pwd,
			Cookie[] cookies) {
		Map loginMap = (Map) this.getModelServletContext().getAttribute(
				this.getAppId() + "_LOGIN_COOKIE_MAP");
		Map sessionMap = new HashMap();
		if (loginMap == null) {
			loginMap = new HashMap();
		}
		sessionMap.put("WEB_SITE_NAME", webSiteName);
		sessionMap.put("COOKIE", cookies);
		sessionMap.put("LOGIN_TIME", Long.toString(System.currentTimeMillis()));
		sessionMap.put("FROM_IP", "");
		sessionMap.put("PWD", pwd);
		sessionMap.put("ACCOUNT", uId);
		sessionMap.put("APP_ID", this.getAppId());
		loginMap.put(uId, sessionMap);
		this.getModelServletContext().setAttribute(appId + "_LOGIN_COOKIE_MAP",
				loginMap);
	}

	/**
	 * 將字串輸出成為檔案
	 * 
	 * @param fileData
	 */
	public String outputTofile(String fileData) {

		File f = new File(this.getModelServletContext().getRealPath("/")
				+ "outputHtml/outputHtml.htm");
		if (!(f.getParentFile().exists() && f.getParentFile().isDirectory())) {
			/* 判斷路徑是否存在 */
			f.getParentFile().mkdirs();

		}
		outputTofile(fileData, f);
		return this.getModelServletContext().getRealPath("/")
				+ "outputHtml/outputHtml.htm";
	}

	/**
	 * 將字串輸出成為檔案，並指定檔案路徑
	 * 
	 * @param fileData
	 * @param filePath
	 */
	public String outputTofile(String fileData, String fileName) {

		File f = new File(this.getModelServletContext().getRealPath("/")
				+ "outputHtml/"+fileName+".htm");
		if (!(f.getParentFile().exists() && f.getParentFile().isDirectory())) {
			/* 判斷路徑是否存在 */
			f.getParentFile().mkdirs();
		}
		outputTofile(fileData, f);
		return this.getModelServletContext().getRealPath("/")
		+ "outputHtml/"+fileName+".htm";
	}

	/**
	 * 將字串輸出成為檔案，並指定檔案路徑
	 * 
	 * @param fileData
	 * @param filePath
	 */
	protected String outputTofile(String fileData, String filePath,
			String fileName) {
		File f = new File(filePath + fileName);
		if (!(f.getParentFile().exists() && f.getParentFile().isDirectory())) {
			/* 判斷路徑是否存在 */
			f.getParentFile().mkdirs();
		}
		outputTofile(fileData, f);
		return filePath + fileName;
	}

	/**
	 * 將字串輸出成為檔案，並指定檔案路徑
	 * 
	 * @param fileData
	 * @param filePath
	 */
	private void outputTofile(String fileData, File f) {
		try {
			FileWriter fw = new FileWriter(f);

			fw.write(fileData, 0, fileData.length()); // 直接將String寫入檔案
			fw.close(); // 關閉檔案
			f = null;
			fw = null;
		} catch (IOException e) {
			logger.error("[錯誤] 寫檔錯誤============");
			logger.error(e.getMessage(),e);
		}
	}

	public void setWebSiteName(String webSiteName) {
		this.webSiteName = webSiteName;
	}

	public String getWebSiteName() {
		return webSiteName;
	}

	public void setWebSiteURL(String webSiteURL) {
		this.webSiteURL = webSiteURL;
	}

	public String getWebSiteURL() {
		return webSiteURL;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppId() {
		return appId;
	}
}
