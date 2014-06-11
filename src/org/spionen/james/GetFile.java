package org.spionen.james;

/**
 *
 * @author Maxim
 */
public class GetFile {
    
    // The base folder path for the Current system users home folder. 
    public static final String libraryPath = System.getenv("HOME");

        // The path where James puts all files he needs to save between missions.
    public static final String jamesLibraryPath = libraryPath + "/Documents/James/"; 

    // Where James put things he's asked to deliver. Like Export lists and stuff.
    public static final String jamesExportPath = jamesLibraryPath + "/From James with Love/"; 
    
    // Where you put address files you want James to read and import.
    public static final String jamesImportPath = jamesLibraryPath + "/To James/";
    
    // Where you put the list of subscribers VTD cannot find. 
    // TODO Integrate this with the "To James" folder.
    public static final String jamesVTDRejectsPath = jamesLibraryPath + "/VTD Rejects/";

    // Where James puts all reference files. IE the files that contains 
    // non Issue-specific data. Such as people that doesent want the paper.
    public static final String jamesReferencePath = jamesLibraryPath + "/reference/";

    // Where James puts all settings files.
    public static final String jamesSettingsPath = jamesLibraryPath + "/settings/";
   
    // Where James puts all filter files.
    public static final String jamesFilterPath = jamesLibraryPath + "/filter/";
   
    // James Files paths
        
    // File containing subscribers in MasterFormat that excplicitly have asked
    // to not get the paper delivered to them.
    // This file gets updated every time someone asks to be excluded. 
    public static final String noThanksFilePath = jamesReferencePath + "NoThanks.txt";
    
    // File containing subscribers in Masterformat that VTD have not been able
    // to find or deliver to. These addresses are relayed to Bring or Posten.
    // This file gets updated whenever VTD sends lists of addresses they cannot 
    // find or deliver to. 
    public static final String notForVTDFilePath = jamesReferencePath + "VTD-BOM-List.txt";

    
    public static String currentMaster(int year, int issue) {        
        String path = jamesLibraryPath + year + "-" + issue + "_Master.txt";
        return path;
    }
    
    public static String previousMaster(int year, int issue) {
        String path = jamesLibraryPath + Helpers.previousIssue(year, issue) + "_Master.txt";
        return path;
    }
    
    public static String rejects(int year, int issue) {
        String path = jamesReferencePath + year + "-" + issue + "_Rejects.txt";
        return path;
    }
        
    public static String vTabExportFile(int year, int issue, String kind) {
        String path = jamesExportPath + "VTab-Spionen-" + year + "-" + issue + "-" + kind + ".txt";
        return path;
    }

}
