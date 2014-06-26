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
}
