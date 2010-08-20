package com.mogan.sys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class reloadParmeter
 */
public class reloadParmeter extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger= Logger.getLogger(InitSys.class.getName());
    /**
     * @see HttpServlet#HttpServlet()
     */
    public reloadParmeter() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		initSystemParameter();
	}
	
	private void initSystemParameter() {
		File f = new File(this.getServletContext().getRealPath("/")
				+ "WEB-INF/sys.Property");
		FileInputStream fis;
		try {
			if (f.isFile()) {
				if (!f.getParentFile().isDirectory()) {
					f.getParentFile().mkdirs();
				}
			}
			fis = new FileInputStream(f);
			// 指定utf-8編碼
			BufferedReader br;
			br = new BufferedReader(new InputStreamReader(fis, "utf-8"));
			Properties p = new Properties();
			p.load(br);
			//System.setProperties(p);
			Iterator it = p.keySet().iterator();
			for (; it.hasNext();) {
				String key = (String) it.next();
				System.setProperty(key, (String) p.get(key));
				this.getServletContext().setAttribute(key, p.get(key));
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}


	}

}
