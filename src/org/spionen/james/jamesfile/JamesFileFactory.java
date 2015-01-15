/**
*   Copyright 2010-2015 Maxim Fris, Tobias Olausson
*
*   This file is part of James.
*
*   James is free software: you can redistribute it and/or modify
*   it under the terms of the GNU General Public License as published by
*   the Free Software Foundation, either version 3 of the License, or
*   (at your option) any later version.
*
*   James is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.
*
*   You should have received a copy of the GNU General Public License
*   along with James. If not, see <http://www.gnu.org/licenses/>.
*/
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
