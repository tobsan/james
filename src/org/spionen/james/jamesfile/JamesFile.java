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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.spionen.james.subscriber.Subscriber;

/**
 * Abstract class to capture common behaviour for source files we want
 * to import into james. Such files are to contain address information
 * 
 * @author Tobias Olausson
 *
 */
public abstract class JamesFile {
	
	public abstract Map<Long,Subscriber> readFile(File file) throws IOException;
	public Map<Long, Subscriber> readFile(String filename) throws IOException, FileNotFoundException {
		File f = new File(filename);
		// Check for common things
		if(!f.isFile()) {
			throw new FileNotFoundException("Input is not a file: " + filename);
		} else if(!f.exists()) {
			throw new FileNotFoundException("The file does not exist: " + filename);
		} else if(!f.canRead()) {
			throw new IOException("Can't read file: " + filename);
		}
		return readFile(f);
	}
	
	public abstract void writeFile(Map<Long, Subscriber> subscribers, File file) throws IOException;
	public void writeFile(Map<Long,Subscriber> subscribers, String filename) throws IOException, FileNotFoundException {
		File f = new File(filename);
		if(!f.exists()) {
			boolean b = f.createNewFile();
			if(!b || !f.canWrite()) {
				throw new IOException("Can't write to file: " + filename);
			}
		}
		writeFile(subscribers, f);
	}
}
