package org.spionen.james.importing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.spionen.james.FieldType;
import org.spionen.james.subscriber.Subscriber;
import org.spionen.james.subscriber.VTDSubscriber;

public class ExcelImportFile extends ImportFile {
	
	public static final String[] excelExt = {".xls", ".xlsx"};
	
	public List<Subscriber> readFile(File file) { 
		List<Subscriber> subscribers = new ArrayList<Subscriber>();
		try {
			Workbook wb = WorkbookFactory.create(file);
			Sheet s = wb.getSheetAt(0);
			
			// Take first row, use to check order of fields
			Row firstRow = s.getRow(s.getFirstRowNum());
			FieldType[] order = new FieldType[firstRow.getLastCellNum() - firstRow.getFirstCellNum()];
			int j = 0; 
			for(int i = firstRow.getFirstCellNum(); i < firstRow.getLastCellNum(); i++, j++) {
				Cell c = firstRow.getCell(i);
				if(c != null) {
					order[j] = FieldType.getFieldType(c.getStringCellValue());
				}
			}
			// Then iterate through the rest of the rows
			if(s.getLastRowNum() > 0) {
				// LastRowNum is 0-indexed, so add 1
				for(int i = s.getFirstRowNum()+1; i < s.getLastRowNum()+1; i++) {
					Row r = s.getRow(i);
					Subscriber sub = new Subscriber();
					j = 0;
					// LastCellNum is also 0-indexed
					for(int k = r.getFirstCellNum(); k < r.getLastCellNum()+1; k++, j++) {
						Cell c = r.getCell(k);
						if(c != null) {
							if(c.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								String val = new Integer(new Double(c.getNumericCellValue()).intValue()).toString();
								sub.setByField(order[j], val);
							} else {
								sub.setByField(order[j], c.getStringCellValue());
							}
						}
					}
					subscribers.add(sub);
				}
			}
			return subscribers;
		} catch(InvalidFormatException | IOException ioe) {
			return subscribers; // TODO: This is probably empty. Throw exception instead?
		}
	}

	public void writeFile(List<Subscriber> subscribers, File file) {
		// Create the workbook from file first
		Workbook wb;
		try {
			wb = WorkbookFactory.create(file);
		} catch(InvalidFormatException | IOException ife) {
			// If it fails, just create a blank one
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
		// Create top row of sheet with header data
		Row top = s.createRow(0);
		FieldType[] fields = FieldType.standardOrder();
		for(int i = 0; i < fields.length; i++) {
			Cell c = top.createCell(i);
			c.setCellValue(fields[i].getDesc());
		}
		
		// And then, iterate through all subscribers
		for(int i = 0; i < subscribers.size(); i++) {
			Subscriber sub = subscribers.get(i); 
			Row r = s.createRow(i+1);
			for(int k = 0; k < fields.length; k++) {
				Cell c = r.createCell(k,Cell.CELL_TYPE_STRING);
				String str = sub.getByField(fields[k]);
				if(str != null) {
					c.setCellValue(str);
				} else {
					c.setCellType(Cell.CELL_TYPE_BLANK);
				}
			}
		}
		
		// Write out to file
		try {
			wb.write(new FileOutputStream(file));
		} catch (IOException e) {
			// TODO: Handle this...
			e.printStackTrace();
		}
	}

	// "Testing" code
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String input = "/home/gargravarr/James/Registerfiler/Register Spionen nr 3 2013/Register Spionen (kopia).xls";
		String output = "/home/gargravarr/James/Registerfiler/Register Spionen nr 3 2013/test.xls";
		ExcelImportFile im = new ExcelImportFile();
		List<Subscriber> ps = im.readFile(input);
		for(Subscriber s : ps) {
			VTDSubscriber vs = new VTDSubscriber(s);
			System.out.println(vs.toString());
		}
		im.writeFile(ps, output);
	}
}
