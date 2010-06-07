package com.mogan.account;

public class AccountMaker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("account::" + getCode(8));
		System.out.println("pwd::" + getCode(8));
		System.out.println("account::" + getCode(8));
		System.out.println("pwd::" + getCode(8));
		System.out.println("account::" + getCode(8));
		System.out.println("pwd::" + getCode(8));
		System.out.println("account::" + getCode(8));
		System.out.println("pwd::" + getCode(8));
		System.out.println("account::" + getCode(8));
		System.out.println("pwd::" + getCode(8));
		System.out.println("account::" + getCode(8));
		System.out.println("pwd::" + getCode(8));
		System.out.println("account::" + getCode(8));
		System.out.println("pwd::" + getCode(8));
		System.out.println("account::" + getCode(8));
		System.out.println("pwd::" + getCode(8));
		System.out.println("account::" + getCode(8));
		System.out.println("pwd::" + getCode(8));
		System.out.println("account::" + getCode(8));
		System.out.println("pwd::" + getCode(8));
		System.out.println("account::" + getCode(8));
		System.out.println("pwd::" + getCode(8));
		System.out.println("account::" + getCode(8));
		System.out.println("pwd::" + getCode(8));
		System.out.println("account::" + getCode(8));
		System.out.println("pwd::" + getCode(8));
	}

	public static String getCode(int codeCount) {
		// 產生亂數密碼
		int[] word = new int[codeCount];
		int mod;
		for (int i = 0; i < codeCount; i++) {
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
		for (int j = 0; j < codeCount; j++) {
			newPassword.append((char) word[j]);
		}
		return newPassword.toString();

	}
}
