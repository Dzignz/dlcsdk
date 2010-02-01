package com.mogan.sys.model;

import org.apache.commons.httpclient.methods.PostMethod;

public class EncodePostMethod extends PostMethod {
	private String charset="utf-8";
    public EncodePostMethod(String url,String charset){
        super(url);
        this.charset=charset;
    }
    
    public EncodePostMethod(String url){
        super(url);
    }
    
    @Override
    public String getRequestCharSet(){
        return charset;
    }
} 

