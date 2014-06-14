package org.spionen.james;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;


/**
 * This class handles reading and writing CSV files for address data to use
 * with james. This does NOT handle output to any distributor, but only the 
 * source data for addresses.
 * 
 * @author Tobias Olausson
 *
 */
public class CsvImportFile extends ImportFile {

	private CsvPreference pref;
	private String encoding;
	
	/**
	 * Creates a new instance of this class, with given formatting options
	 * @param preference the kind of CSV file this is
	 * @param encoding the encoding used for the file
	 */
	public CsvImportFile(CsvPreference preference, String encoding) {
		this.pref = preference;
		this.encoding = encoding;
	}
	
	/**
	 * Creates a new instance of this class, with the north european
	 * excel preference for CSV style, and ISO-8859-1 as encoding.
	 */
	public CsvImportFile() {
		this.pref = CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE;
		this.encoding = "ISO-8859-1";
	}
	
	@Override
	public List<Subscriber> readFile(File file) {
		try {
			List<Subscriber> subscribers = new ArrayList<Subscriber>();
			Reader r = new InputStreamReader(new FileInputStream(file), encoding);
			CsvListReader reader = new CsvListReader(r, pref);
			
			// Handle the header
			String[] header = reader.getHeader(true);
			FieldType[] order = new FieldType[header.length];
			for(int i = 0; i < header.length; i++) {
				order[i] = getFieldType(header[i]);
			}
			
			// Then iterate through the rest of the lines
			List<String> row;
			while((row = reader.read()) != null) {
				Subscriber sub = new Subscriber();
				for(int i = 0; i < row.size(); i++) {
					setByField(order[i], sub, row.get(i));
				}
				subscribers.add(sub);
			}

			reader.close();
			return subscribers;
		} catch(IOException e) {
			return new ArrayList<Subscriber>();
			//TODO: Handle this in a better way
		}
	}

	@Override
	public void writeFile(List<Subscriber> subscribers, File file) {
		try {
			Writer w = new PrintWriter(file, encoding);
			CsvListWriter writer = new CsvListWriter(w, pref);
			// First, write a header
			List<FieldType> order = Arrays.asList(standardOrder());
			writer.write(order);
			// And then the rest of the columns
			for(Subscriber s : subscribers) {
				List<String> values = new ArrayList<String>();
				for(FieldType ft : order) {
					values.add(getByField(ft, s));
				}
				writer.write(values);
			}
			writer.close();
			
		} catch(IOException e) {
			//TODO: Do something here?
		}
	}
	
	// Simple testing code
	public static void main(String[] args) {
		String input = "/home/marvin/workspace/doktorandtest.csv";
		String output = "/home/marvin/workspace/doktorander.csv";
		CsvImportFile c = new CsvImportFile();
		try {
			List<Subscriber> subs = c.readFile(input);
			c.writeFile(subs, output);
			for(Subscriber s : subs) {
				System.out.println(s.vtdFormat());
			}
		} catch(IOException e) {
			// DO Nothing
		}
	}

}
