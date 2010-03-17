package com.mogan.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import com.mogan.sys.SysCalendar;

public class FileIO {
	public FileIO() {

	}

	private void checkFileExists(String filePath){
		File f=new File(filePath);
		if (!(f.getParentFile().exists() && f.getParentFile().isDirectory())) {
			/* 判斷路徑是否存在 */
			f.getParentFile().mkdirs();
		}
		System.out.println("[DEBUG] checkFileExists::"+f.getAbsolutePath());
	}
	
	/**
	 * 寫入Property 檔案，並加增寫入時間說明
	 * 
	 * @return
	 */
	public boolean savePtyFile(String userId, String serviceCate, Properties p,
			String comments) {
		FileOutputStream fos;
		boolean saveFlag=false;
		Properties srcPty=new Properties();
		try {
			String ptyPath="";
			if (userId == null) {
				ptyPath=System.getProperty("catalina.base")+"/userFile/default/" + serviceCate
				+ "/" + serviceCate + ".properties";
			} else {
				ptyPath=System.getProperty("catalina.base")+"/userFile/" + userId + "/"
				+ serviceCate + "/" + serviceCate + ".properties";
			}
			checkFileExists(ptyPath);
			if(new File(ptyPath).exists()){
				srcPty.load(new FileInputStream(ptyPath));
			}
			srcPty.putAll(p);
			
			fos = new FileOutputStream(ptyPath);
			srcPty.store(fos, " Properties Save Date:"
					+ new SysCalendar().getFormatDate()+"\n"+comments);
			saveFlag=true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return saveFlag;
	}

	/**
	 * 讀取Property 檔案
	 * 
	 * @return
	 */
	public Properties loadPtyFile(String userId, String serviceCate) {
		Properties p = new Properties();
		try {
			if (userId == null) {
				p.load(new FileInputStream(System.getProperty("catalina.base")+"/userFile/default/" + serviceCate
						+ "/" + serviceCate + ".properties"));
			} else {
				p.load(new FileInputStream(System.getProperty("catalina.base")+"/userFile/" + userId + "/"
						+ serviceCate + "/" + serviceCate + ".properties"));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}

	/**
	 * 標準的寫入檔案 User ID=Null 存檔位置為$類型/default/檔名 User ID!=Null存檔位置為$類型/UserId/檔名
	 * 
	 * @param userId
	 *            使用者ID,
	 * @param serviceCate
	 *            應用類型
	 * @param fileName
	 *            檔名
	 * @param fileData
	 *            檔案內容
	 * @return 成功=true,失敗=false
	 */
	public boolean saveTxtFile(String userId, String serviceCate,
			String fileName, String fileData) {
		try {
			// 未登入狀態存檔位置為$類型/default/檔名
			// 登入狀態存檔位置為$類型/UserId/檔名
			File f;
			if (userId == null) {
				f = new File(System.getProperty("catalina.base")+"/userFile/default/" + serviceCate + "/" + fileName
						+ ".txt");
			} else {
				f = new File(System.getProperty("catalina.base")+"/userFile/" + userId + "/" + serviceCate + "/"
						+ fileName + ".txt");
			}
			if (!(f.getParentFile().exists() && f.getParentFile().isDirectory())) {
				/* 判斷路徑是否存在 */
				f.getParentFile().mkdirs();
			}
			FileWriter fw = new FileWriter(f);
			System.out.println("[DEBUG] saveTxtFile::"+fileData);
			fw.write(fileData, 0, fileData.length()); // 直接將String寫入檔案
			fw.close(); // 關閉檔案
			f = null;
			fw = null;
		} catch (IOException e) {
			System.out.println("[錯誤] 寫檔錯誤============");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @param userId
	 * @param serviceCate
	 * @return
	 */
	public File[] getTxtFileList(String userId, String serviceCate) {
		File f;
		if (userId == null) {
			f = new File(System.getProperty("catalina.base")+"/userFile/default/" + serviceCate);
		} else {
			f = new File(System.getProperty("catalina.base")+"/userFile/" + userId + "/" + serviceCate);
		}
		
	    FileFilter fileFilter=new FileFilter(){
	        public boolean accept(File pathname) {
	            String tmp=pathname.getName().toLowerCase();
	            if(tmp.endsWith(".txt")){
	                return true;
	            }
	            return false;
	        }
	    };
		
		return f.listFiles(fileFilter);
	}

	/**
	 * 標準的讀取檔案
	 * 
	 * @param userId
	 *            使用者ID
	 * @param serviceCate
	 *            應用類型
	 * @param fileName
	 *            檔名
	 * @return 檔案內容
	 */
	public StringBuffer loadTxtFile(String userId, String serviceCate,
			String fileName) {

		File f;
		StringBuffer file_str = new StringBuffer();
		if (userId == null) {
			f = new File(System.getProperty("catalina.base")+"/userFile/default/" + serviceCate + "/" + fileName
					+ ".txt");
		} else {
			f = new File(System.getProperty("catalina.base")+"/userFile/" + userId + "/" + serviceCate + "/"
					+ fileName + ".txt");
		}
		checkFileExists(f.getAbsolutePath());
		try {
			FileInputStream fis = new FileInputStream(f);
			// 指定utf-8編碼
			BufferedReader br;

			br = new BufferedReader(new InputStreamReader(fis, "utf-8"));

			String file_str_append = "";

			while ((file_str_append = br.readLine()) != null) {
				file_str.append(file_str_append);
				file_str.append("\r\n");
			}
			file_str.delete(file_str.lastIndexOf("\r\n"), file_str.length());

			br.close();
			fis.close();
			return file_str;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file_str;
	}
}
