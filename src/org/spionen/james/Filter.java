package org.spionen.james;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.spionen.james.subscriber.Subscriber;
import org.spionen.james.subscriber.Subscriber.Distributor;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

/**
 * A class representing a filter for zip codes for a distributor, so
 * that any papers within such a zip code is distributed by them. 
 * 
 * @author Maxim Fris
 * @author Tobias Olausson 
 */
public class Filter {

	private Set<Integer> zipCodes;
	private CsvPreference pref; 
	private String encoding;
	private Distributor dist;
	private String filepath;
	
	// Create this filter
	public Filter(Distributor dist, String filepath) {
		this.dist = dist;
		this.filepath = filepath;
		this.pref = CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE;
		this.encoding = "ISO-8859-1";
		this.zipCodes = new HashSet<Integer>();
		readFile();
	}
	
	/**
	 * Reads the filter file and populate the set
	 */
	private void readFile() {
		File file = new File(filepath);
		if(file.isFile() && file.exists()) {
			try {
				Reader r = new InputStreamReader(new FileInputStream(file), encoding);
				CsvListReader reader = new CsvListReader(r, pref);
				List<String> row;
				while((row = reader.read()) != null) {
					// A single number
					if(row.size() == 1) {
						Integer i = Integer.parseInt(row.get(0));
						zipCodes.add(i);
					// A span of numbers
					} else if(row.size() == 2) {
						Integer i = Integer.parseInt(row.get(0));
						Integer j = Integer.parseInt(row.get(1));
						for(int k = i; k <= j; k++) {
							zipCodes.add(k);
						}
					} else {
						// Malformed entry, discard
					}
				}
				reader.close();
			} catch(IOException | NumberFormatException e) {
				// TODO: Well, this is a bummer
			}
		}
	}
	
	/**
	 * Check if a zipCode matches this filter.
	 * @param zipCode the zipCode to check for
	 * @return true if the zipCode matches, false otherwise
	 */
	public boolean matches(int zipCode) {
		return zipCodes.contains(zipCode);
	}
	
	/**
	 * Checks if a Subscriber matches this filter, and if so, sets its
	 * distributor to the supplied value.
	 * 
	 * @param s the subscriber 
	 * @return true if the filter matched, false otherwise
	 */
	public boolean apply(Subscriber s) {
		try {
			int zip = Integer.parseInt(s.getZipCode());
			if(zipCodes.contains(zip)) {
				s.setDistributor(dist);
				return true;
			}
			return false;
		} catch(NumberFormatException e) {
			return false;
		}
	}
    
}
