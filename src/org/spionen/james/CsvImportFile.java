package org.spionen.james;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;


/**
 * TODO: Make preferences available in the constructor, such as
 * 		 what char should be separator etc. 
 * @author marvin
 *
 */
public class CsvImportFile extends ImportFile {

	@Override
	public ArrayList<Subscriber> readFile(File file) {
		try {
			ArrayList<Subscriber> subscribers = new ArrayList<Subscriber>();
			CsvListReader reader = new CsvListReader(new FileReader(file), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
			
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
	public void writeFile(ArrayList<Subscriber> subscribers, File file) {
		try {
			CsvListWriter writer = new CsvListWriter(new FileWriter(file), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
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
			ArrayList<Subscriber> subs = c.readFile(input);
			c.writeFile(subs, output);
			for(Subscriber s : subs) {
				System.out.println(s.vtdFormat());
			}
		} catch(IOException e) {
			// DO Nothing
		}
	}

}
