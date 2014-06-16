package org.spionen.james;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This should serve well to be some kind of state class
 * @author Maxim Fris
 * @author Tobias Olausson
 */

public class MasterFile implements Comparable<MasterFile> {
    
    // Instance variables
    private int year; 
    private int issue; 
    private String fileName; 
    private List<Subscriber> subscribers;
	private List<Subscriber> rejects;
    private State state;
    
    /**
     * @deprecated
     * @param masterFileName
     */
    public MasterFile(String masterFileName) {
        
        year = Integer.parseInt(masterFileName.split("[-_.]")[0]);
        issue = Integer.parseInt(masterFileName.split("[-_.]")[1]);
        fileName = masterFileName; 
    }
    
    public MasterFile(int year, int issue) {
    	this.year = year;
    	this.issue = issue;
    	this.state = State.Init;
    	this.fileName = year + "-" + issue + "_Master.txt"; 
    	subscribers = new ArrayList<Subscriber>();
    	rejects = new ArrayList<Subscriber>();
    }
    
    public void importAll(String directory) {
		File dir = new File(directory);
		if(dir.isDirectory()) {
			String[] files = dir.list();
			for(String f : files) {
				try {
					ImportFile imp = ImportFileFactory.createImportFile(f);
					List<Subscriber> subs = imp.readFile(f);
					subscribers.addAll(subs);
				} catch(IllegalArgumentException | IOException e) {
					// TODO: Do something reasonable here
					e.printStackTrace();
				}
			}
		}
		Collections.sort(subscribers);
	}
    
    /**
	 * Remove addresses that are invalid
	 */
	public void removeBadAddresses() {
		Iterator<Subscriber> it = subscribers.iterator();
		while(it.hasNext()) {
			Subscriber s = it.next();
			if(!s.correctAdress()) {
				s.setDistributor("I"); // I for invalid?
				rejects.add(s);
				it.remove();
			}
		}
	}
	
	/**
	 * Assumes that the list of subscribers is sorted
	 */
	public void removeDuplicates() {
		Iterator<Subscriber> it = subscribers.iterator();
		while(it.hasNext()) {
			Subscriber s1 = it.next();
			if(it.hasNext()) {
				Subscriber s2 = it.next();
				if(s1.comparePrenumerant(s2)) {
					s2.setDistributor("D"); // D for duplicate?
					rejects.add(s2);
					it.remove();
					
				}
			}
		}
	}
	
	/**
	 * Removes all Subscribers that do not want to get the paper
	 * TODO: Make a nicer flow of data here. valid/invalid extension
	 */
	public void removeDeclines(String declineFilePath) {
		try {
			List<Subscriber> declines;
			ImportFile imp = ImportFileFactory.createImportFile(declineFilePath);		
			declines = imp.readFile(declineFilePath);
			for(Subscriber s : declines) {
				s.setDistributor("N"); // N for NoThanks
			}
			rejects.addAll(declines);
		} catch(IOException | IllegalArgumentException e) {
			e.printStackTrace();
		}
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
    
    /**
     * @deprecated
     * @return a string description of this issue
     */
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
