package org.spionen.james;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Maxim
 */
public class Filter {

    private static final String filterPath = GetFile.jamesFilterPath;
    
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
    
    public static boolean checkIfInRange(String postNr, ArrayList<String> inArray) {
        int i = Collections.binarySearch(inArray, postNr);
        return i > 0;
    } 
    
}
