package org.spionen.james.importing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.spionen.james.subscriber.Subscriber;

/**
 * Abstract class to capture common behaviour for source files we want
 * to import into james. Such files are to contain address information
 * 
 * @author Tobias Olausson
 *
 */
public abstract class ImportFile {
	
	public abstract List<Subscriber> readFile(File file);
	public List<Subscriber> readFile(String filename) throws IOException, FileNotFoundException {
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
	
	public abstract void writeFile(List<Subscriber> subscribers, File file);
	public void writeFile(List<Subscriber> subscribers, String filename) throws IOException, FileNotFoundException {
		File f = new File(filename);
		if(!f.exists()) {
			boolean b = f.createNewFile();
			if(!b || !f.canWrite()) {
				throw new IOException("Can't write to file: " + filename);
			}
		}
		System.out.println("Writing subscribers to " + filename);
		writeFile(subscribers, f);
	}
}
