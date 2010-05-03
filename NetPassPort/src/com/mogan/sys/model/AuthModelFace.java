package com.mogan.sys.model;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 認證專用model
 * @author Dian
 *
 */
public interface AuthModelFace {
	public abstract boolean doAuth(HttpServletRequest req, HttpServletResponse res) throws Exception;
}
