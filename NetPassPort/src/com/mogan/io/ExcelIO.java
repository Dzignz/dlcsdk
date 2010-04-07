package com.mogan.io;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
/**
 * 未完成 2010 03 24
 * @author Dian
 *
 */
public class ExcelIO {

	public static void main (String [] args){
		File f=new File("D:\\temp\\Y-0006.xls");

		try {
			Workbook workbook = Workbook.getWorkbook(f);
			Sheet sheet = workbook.getSheet(0);
			
			Cell a1 = sheet.getCell(0,0);
			Cell b2 = sheet.getCell(1,1);
			Cell c2 = sheet.getCell(2,1);

			String stringa1 = a1.getContents();
			String stringb2 = b2.getContents();
			String stringc2 = c2.getContents();
			System.out.println("sheet.getColumns()="+sheet.getColumns());
			System.out.println("sheet.getRows()="+sheet.getRows());
			
			System.out.println("stringb2="+stringb2);
			System.out.println("stringc2="+stringc2);
			workbook.close();
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
