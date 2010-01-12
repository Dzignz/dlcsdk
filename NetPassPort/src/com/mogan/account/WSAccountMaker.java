package com.mogan.account;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public class WSAccountMaker {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String account="Mogan";
		String key1=AccountMaker.getCode(13);
		String key2=String.valueOf(System.currentTimeMillis());
	
		System.out.println( System.currentTimeMillis());
		System.out.println("account1::"+getMD5Digest(key1+account+key2));
	}
	
	private static String getMD5Digest(String str) {
		try {
			byte[] buffer = str.getBytes();
			byte[] result = null;
			StringBuffer buf = null;
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			// allocate room for the hash
			result = new byte[md5.getDigestLength()];
			// calculate hash
			md5.reset();
			md5.update(buffer);
			result = md5.digest();
			// create hex string from the 16-byte hash
			buf = new StringBuffer(result.length * 2);
			for (int i = 0; i < result.length; i++) {
				int intVal = result[i] & 0xff;
				if (intVal < 0x10) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(intVal));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Exception caught: " + e);
			e.printStackTrace();

		}
		return null;
	}
}
