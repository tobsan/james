package org.spionen.james;

/**
 * There should be a nicer way to list all valid extensions
 * @author Tobias Olausson
 *
 */
public class ImportFileFactory {
	
	public static ImportFile createImportFile(String filename) throws IllegalArgumentException {
		for(String s : ExcelImportFile.excelExt) {
			if(filename.endsWith(s)) {
				return new ExcelImportFile();
			}
		}
		for(String s : CsvImportFile.csvExt) {
			if(filename.endsWith(s)) {
				return new CsvImportFile();
			}
		}
		throw new IllegalArgumentException("Invalid file name extension");
	}
}
