package org.spionen.james;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	// Create this filter
	public Filter() {
		this.pref = CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE;
		this.encoding = "ISO-8859-1";
		this.zipCodes = new HashSet<Integer>();
	}
	
	/**
	 * Reads the filter file and populate the set
	 */
	public void readFile(String filename) {
		File file = new File(filename);
		if(file.isFile() && file.exists()) {
			try {
				Reader r = new InputStreamReader(new FileInputStream(file), encoding);
				CsvListReader reader = new CsvListReader(r, pref);
				List<String> row;
				while((row = reader.read()) != null) {
					if(row.size() == 1) {
						Integer i = Integer.parseInt(row.get(0));
						zipCodes.add(i);
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
	* @deprecated
	* Old filter creation code. Does not use CSV, but an own format
	* Format: XXXXX-YYYYY or XXXXX. Lines beginning with > are ignored
    */
    public static ArrayList<String> createFilterArray(File fromFile) 
                                        throws FileNotFoundException, 
                                               IOException {
    
        ArrayList<String> fileRows = new ArrayList<String>();
        ArrayList<String> postNrs  = new ArrayList<String>();
        ListHelpers.copyRows(fromFile, fileRows);
        
        for (int i = 0; i < fileRows.size(); i++) {
            String row = fileRows.get(i).trim();
            if (!(row.startsWith(">"))) {
                if (row.length() == 5) {
                    postNrs.add(row);
                    //System.out.println(row);
                } else {
                    String[] s = row.split("-");
                    int a = Integer.parseInt(s[0]);
                    int o = Integer.parseInt(s[1]);
                    String x;
                    for (int y = a; y <= o; y++) {
                        x = Integer.toString(y); 
                        postNrs.add(x);
                    }
                }
            }
        }
        Collections.sort(postNrs);
        return postNrs;
    }
    
    /**
     * @deprecated
     * Old method to check if zip code is covered in array
     */
    public static boolean checkIfInRange(String postNr, ArrayList<String> inArray) {
        int i = Collections.binarySearch(inArray, postNr);
        return i > 0;
    } 
    
}
