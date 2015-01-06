package org.spionen.james.jamesfile;

/**
 * There should be a nicer way to list all valid extensions
 * @author Tobias Olausson
 *
 */
public class JamesFileFactory {
	
	public static JamesFile createImportFile(String filename) throws IllegalArgumentException {
		for(String s : ExcelJamesFile.excelExt) {
			if(filename.endsWith(s)) {
				return new ExcelJamesFile();
			}
		}
		for(String s : CsvJamesFile.csvExt) {
			if(filename.endsWith(s)) {
				return new CsvJamesFile();
			}
		}
		throw new IllegalArgumentException("Invalid file name extension" + filename);
	}
	
	public static String[] allowedExt() {
		String[] exts = new String[ExcelJamesFile.excelExt.length + CsvJamesFile.csvExt.length];
		int k = 0;
		for(int i = 0; i < ExcelJamesFile.excelExt.length; i++) {
			exts[k++] = ExcelJamesFile.excelExt[i];
		}
		for(int j = 0; j < CsvJamesFile.csvExt.length; j++) {
			exts[k++] = CsvJamesFile.csvExt[j];
		}
		return exts;
	}
}
