package org.spionen.james;

/**
 * This should serve well to be some kind of state class
 * @author Maxim
 */

public class MasterFile implements Comparable<MasterFile> {
    
    // Instance variables
    private int year; 
    private int issue; 
    private String fileName; 
    private State state;
    
    // Constructors
    public MasterFile(String masterFileName) {
        
        year = Integer.parseInt(masterFileName.split("[-_.]")[0]);
        issue = Integer.parseInt(masterFileName.split("[-_.]")[1]);
        fileName = masterFileName; 
    }
    
    public MasterFile(int year, int issue) {
    	this.year = year;
    	this.issue = issue;
    	this.fileName = year + "-" + issue + "_Master.txt"; 
    	this.state = State.Init;
    }
    
    public int getYear() { return year; }
    public int getIssue() { return issue; }    
    public String getFileName() { return fileName; }
    
    public State getState() { return state; }
    public void nextState() {
    	State[] allStates = State.values();
    	for(int i=0; i < allStates.length; i++) {
    		if(state == allStates[i] && i+1 < allStates.length) {
    			state = allStates[i+1];
    		}
    	}
    }
    
    public void prevState() {
    	State[] allStates = State.values();
    	for(int i = allStates.length; i > 0; i++) {
    		if(state == allStates[i] && i-1 > -1) {
    			state = allStates[i-1];
    		}
    	}
    }
    
    public String getDescription() {
        return "Nr. " + issue + " " + year;
    }
    
    public MasterFile nextIssue() {
        if (issue < 8) {
        	return new MasterFile(year, issue+1);
        } else {
        	return new MasterFile(year+1, 1);
        }
    }
    
    public MasterFile previousIssue() {
        if (issue > 1) {
        	return new MasterFile(year, issue-1);
        } else {
        	return new MasterFile(year-1, 8);
        }
    }
    
    @Override 
    public int compareTo(MasterFile mf) {
        return this.getFileName().compareTo(mf.getFileName());
    }
    
    public enum State {
    	Init,
    	GotSource,
    	FirstVTD_TB,
    	GotFirstMiss,
    	SecondVTD_TB,
    	GotSecondMiss,
    	Finalised
    };
    
}
