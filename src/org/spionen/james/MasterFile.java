package org.spionen.james;

/**
 * This should serve well to be some kind of state class
 * @author Maxim
 */

public class MasterFile implements Comparable<MasterFile> {
    
    // Instance variables
    private int year; 
    private int number; 
    private String fileName; 
    
    // Constructors
    public MasterFile(String masterFileName) {
        
        year = Integer.parseInt(masterFileName.split("[-_.]")[0]);
        number = Integer.parseInt(masterFileName.split("[-_.]")[1]);
        fileName = masterFileName; 
    }
    
    //Accessors
    public int getYear() {return year;}
    public int getNumber() {return number;}    
    public String getFileName() {return fileName;}
    
    public String getDescription() {
        return "Nr. " + number + " " + year;
    }
    
    public MasterFile nextIssue() {
        int yyyy; //next Issue Year 
        int n; //next Issue number
        
        if (number < 8) {
            yyyy = year;
            n = number + 1; 
        } else {
            yyyy = year + 1;
            n = 1; 
        }
        return new MasterFile(yyyy + "-" + n + "_Master.txt"); 
    }
    
    public MasterFile previousIssue() {
        int yyyy; //previous Issue Year 
        int n; //previous Issue number
        
        if (number > 1) {
            yyyy = year;
            n = number - 1; 
        } else {
            yyyy = year - 1;
            n = 8; 
        }
        return new MasterFile(yyyy + "-" + n + "_Master.txt");
    }
    
    @Override 
    public int compareTo(MasterFile mf) {
        return this.getFileName().compareTo(mf.getFileName());
    }
    
}
