package com.dlc.test;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class logTest
 */
public class logTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public logTest() {
        super();
        // TODO Auto-generated constructor stub
        Properties logp = new Properties();
        try {
          logp.load(logTest.class.getClassLoader()
            .getResourceAsStream( "tw/idv/idealist/Log4j.properties"));
        } catch (IOException e) {
          e.printStackTrace();
        }

 
        logger = Logger.getLogger("R");

        logger.debug("Hello Log4j");
        logger.info("Hi Log4j");

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		super.doGet(request, response);
		response.getOutputStream().println("testBug");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(request, response);
	}

}
