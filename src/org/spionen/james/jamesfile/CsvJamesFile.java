package org.spionen.james.jamesfile;

import org.spionen.james.subscriber.Subscriber;

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
import java.util.Map;
import java.util.TreeMap;

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
public class CsvJamesFile extends JamesFile {

	public static final String[] csvExt = {".csv", ".txt"};
	private CsvPreference pref;
	private String encoding;
	
	/**
	 * Creates a new instance of this class, with given formatting options
	 * @param preference the kind of CSV file this is
	 * @param encoding the encoding used for the file
	 */
	public CsvJamesFile(CsvPreference preference, String encoding) {
		this.pref = preference;
		this.encoding = encoding;
	}
	
	/**
	 * Creates a new instance of this class, with the north european
	 * excel preference for CSV style, and ISO-8859-1 as encoding.
	 */
	public CsvJamesFile() {
		this.pref = CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE;
		this.encoding = "ISO-8859-1";
	}
	
	@Override
	public Map<Long, Subscriber> readFile(File file) {
		try {
			Map<Long, Subscriber> subscribers = new TreeMap<Long, Subscriber>();
			Reader r = new InputStreamReader(new FileInputStream(file), encoding);
			CsvListReader reader = new CsvListReader(r, pref);
			
			// Handle the header
			String[] header = reader.getHeader(true);
			FieldType[] order = new FieldType[header.length];
			for(int i = 0; i < header.length; i++) {
				order[i] = FieldType.getFieldType(header[i]);
			}
			
			// Then iterate through the rest of the lines
			List<String> row;
			while((row = reader.read()) != null) {
				Subscriber sub = new Subscriber();
				for(int i = 0; i < row.size(); i++) {
					sub.setByField(order[i], row.get(i));
				}
				subscribers.put(sub.getAbNr(), sub);
			}
			
			reader.close();
			r.close();
			return subscribers;
		} catch(IOException e) {
			return null;
			//TODO: Handle this in a better way
		}
	}

	@Override
	/**
	 * TODO: Should the map be converted to a sorted list first, perhaps?
	 */
	public void writeFile(Map<Long,Subscriber> subscribers, File file) {
		try {
			Writer w = new PrintWriter(file, encoding);
			CsvListWriter writer = new CsvListWriter(w, pref);
			// First, write a header
			List<FieldType> order = Arrays.asList(FieldType.standardOrder());
			writer.write(order);
			// And then the rest of the columns
			for(long abNr : subscribers.keySet()) {
				List<String> values = new ArrayList<String>();
				Subscriber s = subscribers.get(abNr);
				for(FieldType ft : order) {
					values.add(s.getByField(ft));
				}
				writer.write(values);
			}
			writer.close();
			
		} catch(IOException e) {
			//TODO: Do something here?
			e.printStackTrace();
		}
	}

}
