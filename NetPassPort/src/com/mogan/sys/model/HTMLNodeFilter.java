package com.mogan.sys.model;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;

public class HTMLNodeFilter implements NodeFilter {
	private String keyWord;//篩選關鍵字

	public HTMLNodeFilter(String key) {
		setKeyWord(key);
	}

	@Override
	public boolean accept(Node node) {

		if (node.getText().contains(keyWord)) {// 使用關鍵篩選
			if (keyWord.equals("strong")){
				System.out.println("HTMLNodeFilter : "+node.getText());
			}
			return true;
		} else {
			return false;
		}
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getKeyWord() {
		return keyWord;
	}

}
