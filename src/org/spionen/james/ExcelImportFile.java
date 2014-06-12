package org.spionen.james;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelImportFile extends ImportFile {

	@Override
	public ArrayList<Subscriber> readFile(File file) { 
		return null;
		//TODO: Implement this
	}

	@Override
	public void writeFile(ArrayList<Subscriber> subscribers, File file) {
		Workbook wb;
		try {
			wb = WorkbookFactory.create(file);
		} catch(InvalidFormatException | IOException ife) {
			if(file.getName().endsWith(".xlsx")) {
				wb = new XSSFWorkbook();
			} else {
				wb = new HSSFWorkbook();
			}
		}
		
		// First, remove all existing sheets
		int sheets = wb.getNumberOfSheets();
		for(int i = 0; i < sheets; i++) {
			wb.removeSheetAt(i);
		}
		Sheet s = wb.createSheet();
		// TODO: create top row of sheet with header data
		Row top = s.createRow(0);
		
		// And then, iterate through all subscibers
		for(int i = 0; i < subscribers.size(); i++) {
			int j = i+1;
			Row r = s.createRow(j);
			// TODO: Create columns for each field in the subscriber
		}
	}

}
