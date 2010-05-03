package com.mogan.sys.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TimerTask;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.mogan.sys.log.SysLogger4j;

/**
 * Servlet implementation class ModelLoader
 */
public class ModelManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String modelFilePath;
	private static ServletContext servletContext;
	private static Document Mdeldocument;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ModelManager() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		System.out.println("[INFO] ModelManager init start.");
		servletContext = this.getServletContext();
		modelFilePath = this.getServletContext().getRealPath("/")
				+ "/WEB-INF/model.xml";
		loadModels();
		servletContext.setAttribute("ModelManager", this);
		System.out.println("[INFO] ModelManager init finish.");
	}

	public ServletContext getModelServletContext() {
		return this.servletContext;
	}

	/**
	 * 新增一個Model
	 * 
	 * @param name
	 *            - modelName
	 * @param classPath
	 *            - modelClass
	 * @param Description
	 *            - modelDiscription
	 * @return false=已經有重複名稱的model name了，true=新增成功
	 */
	public boolean addModel(String name, String classPath, String Description,
			Properties p) {
		if (getModels(name).size() > 0) {
			return false;// 有重覆名稱
		}
		Document document = (Document) this.servletContext
				.getAttribute("MODEL_XML");
		Element root = document.getRootElement();
		Element model = root.addElement("model");
		model.addElement("modelName").setText(name);
		model.addElement("modelClass").setText(classPath);
		model.addElement("modelDiscription").setText(Description);
		Iterator it = p.keySet().iterator();
		for (; it.hasNext();) {
			String key = (String) it.next();
			model.element("model-properties").addAttribute(key,
					p.getProperty(key));
		}
		return true;
	}

	/**
	 * 覆寫一個model，如不存在自動新增
	 * 
	 * @param name
	 * @param classPath
	 * @param Description
	 * @return true=新增成功
	 */
	public boolean setModel(String name, String classPath, String description,
			Properties p) {
		List nodes = getModels(name);
		if (nodes.size() == 0) {
			return addModel(name, classPath, description, p);
		} else {
			for (int i = 0; i < nodes.size(); i++) {
				Element e = (Element) nodes.get(i);
				e.element("modelName").setText(name);
				e.element("modelClass").setText(classPath);
				e.element("modelDiscription").setText(description);
				Iterator it = p.keySet().iterator();
				if (p.size() > 0) {
					List propertiesNodes = e.element("model-properties")
							.elements("property");
					propertiesNodes.removeAll(propertiesNodes);
					for (; it.hasNext();) {
						String key = (String) it.next();

						e.element("model-properties").addElement("property")
								.addAttribute("name", key).addAttribute(
										"value", p.getProperty(key));
					}
				}
			}

		}
		return true;
	}

	/**
	 * 儲存Models
	 */
	public void saveModels() {
		File modelXMLFile = new File(modelFilePath);
		Document document = (Document) this.servletContext
				.getAttribute("MODEL_XML");
		XMLWriter writer;
		try {
			writer = new XMLWriter(new FileWriter(modelXMLFile));
			writer.write(document);
			writer.close();
			System.out.println(document.asXML());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 回傳全部model相關資料
	 * 
	 * @param modelName
	 * @return
	 */
	public List<Element> getModels() {
		Document document = (Document) this.servletContext
				.getAttribute("MODEL_XML");
		List<Element> nodes = document.selectNodes("/models/model");
		return nodes;
	}

	/**
	 * 傳入modelName 回傳modelName相關資料
	 * 
	 * @param modelName
	 * @return
	 */
	public List<Element> getModels(String modelName) {
		Document document = (Document) this.servletContext
				.getAttribute("MODEL_XML");
		List<Element> nodes = document.selectNodes("/models/model[modelName='"
				+ modelName + "']");
		SysLogger4j.debug( "getModels:"+modelName);
		return nodes;
	}
	
	/**
	 * 回傳全部schedule相關資料
	 * 
	 * @return
	 */
	public List<Element> getScheduleModels() {
		Document document = (Document) this.servletContext
				.getAttribute("MODEL_XML");
		List<Element> nodes = document.selectNodes("/models/schedule");
		return nodes;
	}



	public List<Element> getScheduleModels(String modelName) {
		Document document = (Document) this.servletContext
				.getAttribute("MODEL_XML");
		List<Element> nodes = document
				.selectNodes("/models/schedule[scheduleName='" + modelName
						+ "']");
		return nodes;
	}

	/**
	 * 取得驗證模組資訊
	 * @param modelName
	 * @return
	 */
	public List<Element> getAuthModels(String modelName) {
		Document document = (Document) this.servletContext
				.getAttribute("MODEL_XML");
		List<Element> nodes = document
				.selectNodes("/models/auth[authName='" + modelName
						+ "']");
		return nodes;
	}
	
	/**
	 * 取得標準檔案處理Model
	 * 
	 * @param modelName
	 * @return
	 */
	public ProtoModel getFileModel(String modelName) {
		URL url1;
		List<org.dom4j.Element> nodes = getModels(modelName);
		ProtoModel fileModel = null;

		if (nodes.size() > 0) {
			Element e = nodes.get(0);
			try {
				url1 = new URL("file:" + this.servletContext.getRealPath("/")
						+ this.servletContext.getAttribute("MODEL_PATH")
						+ e.elementText("modelJar"));
				URLClassLoader cl = new URLClassLoader(new URL[] { url1 },
						Thread.currentThread().getContextClassLoader());
				Class model = cl.loadClass(e.elementText("modelClass"));
				fileModel = (ProtoModel) model.newInstance();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return fileModel;
	}

	/**
	 * 取得標準的資料處理Model
	 * 
	 * @param modelName
	 * @return
	 */
	public ProtoModel getServiceModel(String modelName) {
		URL url1;
		List<org.dom4j.Element> nodes = getModels(modelName);
		ProtoModel serviceModel = null;
		if (nodes.size() > 0) {
			Element e = nodes.get(0);
			try {
				url1 = new URL("file:" + this.servletContext.getRealPath("/")
						+ this.servletContext.getAttribute("MODEL_PATH")
						+ e.elementText("modelJar"));
				URLClassLoader cl = new URLClassLoader(new URL[] { url1 },
						Thread.currentThread().getContextClassLoader());
				// Class model = cl.loadClass("com.mogan.model.ModelService");
				Class model = cl.loadClass(e.elementText("modelClass"));
				SysLogger4j.info(e.elementText("modelClass"));
				serviceModel = (ProtoModel) model.newInstance();
				serviceModel.setModelClass(e.elementText("modelClass"));
				serviceModel.setModelName(modelName);
				serviceModel.setModelDiscription(e
						.elementText("modelDiscription"));
				serviceModel.setAcceptIds(this.getModelAcceptIds(modelName));
				serviceModel.setDenyIds(this.getModelDenyIds(modelName));
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return serviceModel;
	}

	/**
	 * 取得排程模組
	 * @param ScheduleName
	 * @return
	 */
	public ScheduleModelAdapter getScheduleModel(String ScheduleName) {
		ScheduleModelAdapter scheduleModel = null;
		URL url1;

		List<org.dom4j.Element> nodes = getScheduleModels(ScheduleName);
		if (nodes.size() > 0) {
			Element e = nodes.get(0);
			try {
				url1 = new URL("file:" + this.servletContext.getRealPath("/")
						+ this.servletContext.getAttribute("MODEL_PATH")
						+ e.elementText("scheduleJar"));
				URLClassLoader cl = new URLClassLoader(new URL[] { url1 },
						Thread.currentThread().getContextClassLoader());

				Class model = cl.loadClass(e.elementText("scheduleClass"));

				scheduleModel = (ScheduleModelAdapter) model.newInstance();
				
				scheduleModel.setModelClass(e.elementText("scheduleClass"));
				scheduleModel.setModelName(ScheduleName);
				scheduleModel.setModelDiscription(e
						.elementText("scheduleDiscription"));
				
				if (e.elements("loop").size()>0){
					scheduleModel.setLoop(Boolean.parseBoolean(e.elementText("loop")));	
				}

				if (e.elements("load-on-startup").size()>0){
					scheduleModel.setLOS(e.elementText("load-on-startup"));	
				}
				
				if (e.selectNodes("set-run-time-spec").size()>0){
					scheduleModel.setRunTimeSpec(e.elementText("set-run-time-spec"));	
				}
				
				if (e.selectNodes("remain-time").size()>0){
					scheduleModel.setRemainTime(Integer.parseInt(e.elementText("remain-time")));	
				}
				
				
				scheduleModel.setInterval(e.elementText("interval"));
				scheduleModel.setProperties(this.getScheduleProperties(ScheduleName));
				
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return scheduleModel;
	}
	
	/**
	 * 取得驗證模組
	 * @param ScheduleName
	 * @return
	 */
	public AuthModelAdapter getAuthModel(String modelName) {
		AuthModelAdapter authModel = null;
		URL url1;

		List<org.dom4j.Element> nodes = getScheduleModels(modelName);
		if (nodes.size() > 0) {
			Element e = nodes.get(0);
			try {
				url1 = new URL("file:" + this.servletContext.getRealPath("/")
						+ this.servletContext.getAttribute("MODEL_PATH")
						+ e.elementText("authJar"));
				URLClassLoader cl = new URLClassLoader(new URL[] { url1 },
						Thread.currentThread().getContextClassLoader());

				Class model = cl.loadClass(e.elementText("authClass"));

				authModel = (AuthModelAdapter) model.newInstance();
				
				authModel.setModelClass(e.elementText("authClass"));
				authModel.setModelName(modelName);
				authModel.setModelDiscription(e
						.elementText("authDiscription"));

				authModel.setProperties(this.getScheduleProperties(modelName));
				
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return authModel;
		
	}
	
	/**
	 * 讀取XML，並設定在ServletContext的MODEL_XML中
	 */
	public void loadModels() {
		File modelXMLFile = new File(modelFilePath);
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(modelXMLFile);
			// System.out.println("-------------------"+document.asXML());
			this.servletContext.setAttribute("MODEL_XML", document);
			// getModel("ModelService");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("[INFO] ModelManager loadModel XML OK.");
	}

	/**
	 * 取得排程屬性
	 * 
	 * @param modelName
	 * @param propertyName
	 * @return
	 */
	public Properties getScheduleProperties(String modelName) {
		Document document = (Document) this.servletContext
				.getAttribute("MODEL_XML");
		List<Element> nodes = document
				.selectNodes("/models/schedule[scheduleName='" + modelName
						+ "']/schedule-properties/property");
		String value = null;

		Properties p = new Properties();
		for (int i = 0; i < nodes.size(); i++) {
			Element e = nodes.get(i);
			p
					.put(e.attribute("name").getText(), e.attribute("value")
							.getText());
		}
		
		return p;
	}

	/**
	 * 取得模組屬性
	 * @param modelName
	 * @return
	 */
	public Properties getModelProperties(String modelName) {
		Document document = (Document) this.servletContext
				.getAttribute("MODEL_XML");
		List<Element> nodes = document.selectNodes("/models/model[modelName='"
				+ modelName + "']/model-properties/property");
		Properties p = new Properties();		
		for (int i = 0; i < nodes.size(); i++) {
			Element e = nodes.get(i);
			p
					.put(e.attribute("name").getText(), e.attribute("value")
							.getText());
		}
		return p;
	}
	
	/**
	 * 取得appId白名單
	 * @param modelName
	 * @return
	 */
	public ArrayList getModelAcceptIds(String modelName){
		Document document = (Document) this.servletContext
		.getAttribute("MODEL_XML");
		List<Element> nodes = document.selectNodes("/models/model[modelName='"
				+ modelName + "']/acceptId/appId");
		ArrayList appIds=new ArrayList();
		for (int i = 0; i < nodes.size(); i++) {
			Element e = nodes.get(i);
			appIds.add(e.getText());
		}
		return appIds;
	}
	
	/**
	 * 取得appId黑名單
	 * @param modelName
	 * @return
	 */
	public ArrayList getModelDenyIds(String modelName){
		Document document = (Document) this.servletContext
		.getAttribute("MODEL_XML");
		List<Element> nodes = document.selectNodes("/models/model[modelName='"
				+ modelName + "']/denyId/appId");
		ArrayList denyIds=new ArrayList();
		for (int i = 0; i < nodes.size(); i++) {
			Element e = nodes.get(i);
			denyIds.add(e.getText());
		}
		return denyIds;
	}

	/**
	 * 回傳目前的model文件
	 * 
	 * @return
	 */
	public Document getModelDocument() {
		Document document = (Document) this.servletContext
				.getAttribute("MODEL_XML");
		return document;
	}
}
