package com.mogan.sys;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Hashtable;

import org.apache.log4j.Logger;



public class SysEnCoding {
	static private Logger logger  =  Logger.getLogger(SysEnCoding.class.getName());
	protected Hashtable s2thash = new Hashtable(), t2shash = new Hashtable();
	private int unsupportedStrategy = UNIESC;

	public final static int DELETE = 0;
	public final static int HTMLESC = 1;
	public final static int UNIESC = 2;
	public final static int QUESTIONMARK = 3;
	public final static int TOTAL = 4;
	public static int GB2312 = 0;
	public static int GBK = 1;
	public static int GB18030 = 2;
	public static int HZ = 3;
	public static int BIG5 = 4;
	public static int CNS11643 = 5;
	public static int UTF8 = 6;
	public static int UTF8T = 7;
	public static int UTF8S = 8;
	public static int UNICODE = 9;
	public static int UNICODET = 10;
	public static int UNICODES = 11;
	public static int ISO2022CN = 12;
	public static int ISO2022CN_CNS = 13;
	public static int ISO2022CN_GB = 14;
	public static int EUC_KR = 15;
	public static int CP949 = 16;
	public static int ISO2022KR = 17;
	public static int JOHAB = 18;
	public static int SJIS = 19;
	public static int EUC_JP = 20;
	public static int ISO2022JP = 21;

	public static int ASCII = 22;
	public static int OTHER = 23;

	public static int TOTALTYPES = 24;

	public final static int SIMP = 0;
	public final static int TRAD = 1;
	// Names of the encodings as understood by Java
	public static String[] javaname;
	// Names of the encodings for human viewing
	public static String[] nicename;
	// Names of charsets as used in charset parameter of HTML Meta tag
	public static String[] htmlname;

	public SysEnCoding() {
		unsupportedStrategy = UNIESC;
		String dataline;

		// Initialize and load in the simplified/traditional character hashses
		s2thash = new Hashtable();
		t2shash = new Hashtable();

		javaname = new String[TOTALTYPES];
		nicename = new String[TOTALTYPES];
		htmlname = new String[TOTALTYPES];

		// Assign encoding names
		javaname[GB2312] = "GB2312";
		javaname[GBK] = "GBK";
		javaname[GB18030] = "GB18030";
		javaname[HZ] = "ASCII"; // What to put here? Sun doesn't support HZ
		javaname[ISO2022CN_GB] = "ISO2022CN_GB";
		javaname[BIG5] = "BIG5";
		javaname[CNS11643] = "EUC-TW";
		javaname[ISO2022CN_CNS] = "ISO2022CN_CNS";
		javaname[ISO2022CN] = "ISO2022CN";
		javaname[UTF8] = "UTF8";
		javaname[UTF8T] = "UTF8";
		javaname[UTF8S] = "UTF8";
		javaname[UNICODE] = "Unicode";
		javaname[UNICODET] = "Unicode";
		javaname[UNICODES] = "Unicode";
		javaname[EUC_KR] = "EUC_KR";
		javaname[CP949] = "MS949";
		javaname[ISO2022KR] = "ISO2022KR";
		javaname[JOHAB] = "Johab";
		javaname[SJIS] = "SJIS";
		javaname[EUC_JP] = "EUC_JP";
		javaname[ISO2022JP] = "ISO2022JP";
		javaname[ASCII] = "ASCII";
		javaname[OTHER] = "ISO8859_1";

		// Assign encoding names
		htmlname[GB2312] = "GB2312";
		htmlname[GBK] = "GBK";
		htmlname[GB18030] = "GB18030";
		htmlname[HZ] = "HZ-GB-2312";
		htmlname[ISO2022CN_GB] = "ISO-2022-CN-EXT";
		htmlname[BIG5] = "BIG5";
		htmlname[CNS11643] = "EUC-TW";
		htmlname[ISO2022CN_CNS] = "ISO-2022-CN-EXT";
		htmlname[ISO2022CN] = "ISO-2022-CN";
		htmlname[UTF8] = "UTF-8";
		htmlname[UTF8T] = "UTF-8";
		htmlname[UTF8S] = "UTF-8";
		htmlname[UNICODE] = "UTF-16";
		htmlname[UNICODET] = "UTF-16";
		htmlname[UNICODES] = "UTF-16";
		htmlname[EUC_KR] = "EUC-KR";
		htmlname[CP949] = "x-windows-949";
		htmlname[ISO2022KR] = "ISO-2022-KR";
		htmlname[JOHAB] = "x-Johab";
		htmlname[SJIS] = "Shift_JIS";
		htmlname[EUC_JP] = "EUC-JP";
		htmlname[ISO2022JP] = "ISO-2022-JP";
		htmlname[ASCII] = "ASCII";
		htmlname[OTHER] = "ISO8859-1";

		// Assign Human readable names
		nicename[GB2312] = "GB-2312";
		nicename[GBK] = "GBK";
		nicename[GB18030] = "GB18030";
		nicename[HZ] = "HZ";
		nicename[ISO2022CN_GB] = "ISO2022CN-GB";
		nicename[BIG5] = "Big5";
		nicename[CNS11643] = "CNS11643";
		nicename[ISO2022CN_CNS] = "ISO2022CN-CNS";
		nicename[ISO2022CN] = "ISO2022 CN";
		nicename[UTF8] = "UTF-8";
		nicename[UTF8T] = "UTF-8 (Trad)";
		nicename[UTF8S] = "UTF-8 (Simp)";
		nicename[UNICODE] = "Unicode";
		nicename[UNICODET] = "Unicode (Trad)";
		nicename[UNICODES] = "Unicode (Simp)";
		nicename[EUC_KR] = "EUC-KR";
		nicename[CP949] = "CP949";
		nicename[ISO2022KR] = "ISO 2022 KR";
		nicename[JOHAB] = "Johab";
		nicename[SJIS] = "Shift-JIS";
		nicename[EUC_JP] = "EUC-JP";
		nicename[ISO2022JP] = "ISO 2022 JP";
		nicename[ASCII] = "ASCII";
		nicename[OTHER] = "OTHER";
	}

	/**
	 * 字符串轉換
	 * 
	 * @param dataline
	 *            轉換的字符串
	 * @param source_encoding
	 *            原字符串編碼
	 * @param target_encoding
	 *            轉換後的編碼
	 */
	public void convertStringBuffer(StringBuffer dataline, int source_encoding,
			int target_encoding) {
		int lineindex;
		String currchar;
		char charvalue;

		if (source_encoding == HZ) {// 如果原字符串編碼是HK
			hz2gbStringBuffer(dataline);
		}

		for (lineindex = 0; lineindex < dataline.length(); lineindex++) {// 循環轉換字符串每個字符的編碼
			charvalue = dataline.charAt(lineindex);
			currchar = "" + charvalue;
			if (((int) charvalue == 0xfeff || (int) charvalue == 0xfffe)
					&& (target_encoding != UNICODE
							&& target_encoding != UNICODES
							&& target_encoding != UNICODET
							&& target_encoding != UTF8
							&& target_encoding != UTF8S && target_encoding != UTF8T)) {
				dataline.deleteCharAt(lineindex);
				continue;
			}

			if ((source_encoding == GB2312 || source_encoding == GBK
					|| source_encoding == ISO2022CN_GB || source_encoding == HZ
					|| source_encoding == GB18030 || source_encoding == UNICODE
					|| source_encoding == UNICODES || source_encoding == UTF8 || source_encoding == UTF8S)
					&& (target_encoding == BIG5 || target_encoding == CNS11643
							|| target_encoding == UNICODET
							|| target_encoding == UTF8T || target_encoding == ISO2022CN_CNS)) {
				if (s2thash.containsKey(currchar) == true) {
					dataline.replace(lineindex, lineindex + 1, (String) s2thash.get(currchar));
				}
			} else if ((source_encoding == BIG5 || source_encoding == CNS11643
					|| source_encoding == UNICODET || source_encoding == UTF8
					|| source_encoding == UTF8T
					|| source_encoding == ISO2022CN_CNS
					|| source_encoding == GBK || source_encoding == GB18030 || source_encoding == UNICODE)
					&& (target_encoding == GB2312
							|| target_encoding == UNICODES
							|| target_encoding == ISO2022CN_GB
							|| target_encoding == UTF8S || target_encoding == HZ)) {
				if (t2shash.containsKey(currchar) == true) {
					dataline.replace(lineindex, lineindex + 1, (String) t2shash.get(currchar));
				}
			}
		}

		if (target_encoding == HZ) {
			// Convert to look like HZ
			gb2hzStringBuffer(dataline);
		}

		Charset charset = Charset.forName(javaname[target_encoding]);
		CharsetEncoder encoder = charset.newEncoder();

		for (int i = 0; i < dataline.length(); i++) {
			if (encoder.canEncode(dataline.subSequence(i, i + 1)) == false) {
				// Replace or delete
				// Delete
				if (unsupportedStrategy == DELETE) {
					dataline.deleteCharAt(i);
					i--;
				} else if (unsupportedStrategy == HTMLESC) {
					// HTML Escape NNNN;
					dataline.replace(i, i + 1, ""
							+ Integer.toHexString((int) dataline.charAt(i))
							+ ";");
				} else if (unsupportedStrategy == UNIESC) {
					// Unicode Escape \\uNNNN
					dataline.replace(i, i + 1, "\\u"
							+ Integer.toHexString((int) dataline.charAt(i)));
				} else if (unsupportedStrategy == QUESTIONMARK) {
					// Unicode Escape \\uNNNN
					dataline.replace(i, i + 1, "?");
				}
			}
		}

	}

	/**
	 * 將傳入的字符串參數編碼由HZ轉換為GB2312
	 * 
	 * @param hzstring
	 * @return
	 */
	public String hz2gb(String hzstring) {
		StringBuffer gbstring = new StringBuffer(hzstring);
		hz2gbStringBuffer(gbstring);
		return gbstring.toString();
	}

	/**
	 * 將傳入的StringBuffer參數編碼由HZ轉換為GB2312
	 * 
	 * @param hzstring
	 * @return
	 */
	public void hz2gbStringBuffer(StringBuffer hzstring) {
		// byte[] hzbytes; // = new byte[2];
		byte[] gbchar = new byte[2];
		int i = 0;
		// StringBuffer gbstring = new StringBuffer("");

		// Convert to look like equivalent Unicode of GB
		for (i = 0; i < hzstring.length(); i++) {
			if (hzstring.charAt(i) == '~') {
				if (hzstring.charAt(i + 1) == '{') {
					hzstring.delete(i, i + 2);
					while (i < hzstring.length()) {
						if (hzstring.charAt(i) == '~'
								&& hzstring.charAt(i + 1) == '}') {
							hzstring.delete(i, i + 2);
							i--;
							break;
						} else if (hzstring.charAt(i) == '\r'
								|| hzstring.charAt(i) == '\n') {
							break;
						}
						gbchar[0] = (byte) (hzstring.charAt(i) + 0x80);
						gbchar[1] = (byte) (hzstring.charAt(i + 1) + 0x80);
						try {
							hzstring.replace(i, i + 2, new String(gbchar, "GB2312"));
						} catch (Exception usee) {
							System.err.println(usee.toString());
						}
						i++;
					}
				} else if (hzstring.charAt(i + 1) == '~') { // ~~ becomes ~
					hzstring.replace(i, i + 2, "~");
				}
			}
		}

	}

	/**
	 * 將傳入的字符串參數編碼由GB2312轉換為HZ
	 * 
	 * @param gbstring
	 * @return
	 */
	public String gb2hz(String gbstring) {
		StringBuffer hzbuffer = new StringBuffer(gbstring);
		gb2hzStringBuffer(hzbuffer);
		return hzbuffer.toString();
	}

	/**
	 * 將傳入的StringBuffer參數編碼由GB2312轉換為HZ
	 * 
	 * @param gbstring
	 * @return
	 */
	public void gb2hzStringBuffer(StringBuffer gbstring) {
		byte[] gbbytes = new byte[2];
		int i;
		boolean terminated = false;

		for (i = 0; i < gbstring.length(); i++) {
			if ((int) gbstring.charAt(i) > 0x7f) {
				gbstring.insert(i, "~{");
				terminated = false;
				while (i < gbstring.length()) {
					if (gbstring.charAt(i) == '\r'
							|| gbstring.charAt(i) == '\n') {
						gbstring.insert(i, "~}");
						i += 2;
						terminated = true;
						break;
					} else if ((int) gbstring.charAt(i) <= 0x7f) {
						gbstring.insert(i, "~}");
						i += 2;
						terminated = true;
						break;
					}
					try {
						gbbytes = gbstring.substring(i, i + 1).getBytes("GB2312");
					} catch (UnsupportedEncodingException uee) {
						logger.error(uee.getMessage(),uee);
					}
					gbstring.delete(i, i + 1);
					gbstring.insert(i, (char) (gbbytes[0] + 256 - 0x80));
					gbstring.insert(i + 1, (char) (gbbytes[1] + 256 - 0x80));
					i += 2;
				}
				if (terminated == false) {
					gbstring.insert(i, "~}");
					i += 2;
				}
			} else {
				if (gbstring.charAt(i) == '~') {
					gbstring.replace(i, i + 1, "~~");
					i++;
				}
			}
		}

	}
}
