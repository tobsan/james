package org.spionen.james;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.spionen.james.importing.ImportFile;
import org.spionen.james.importing.ImportFileFactory;
import org.spionen.james.subscriber.Subscriber;
import org.spionen.james.subscriber.VTDSubscriber;

/**
 * This should serve well to be some kind of state class
 * @author Maxim Fris
 * @author Tobias Olausson
 */

public class MasterFile implements Comparable<MasterFile>, Serializable {
    
	private static final long serialVersionUID = -5687392826237960600L;
    private int year; 
    private int issue; 
    private String fileName; 
    
    // These two contain ALL subscribers together.
    private List<Subscriber> allSubscribers;
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
    	allSubscribers = new ArrayList<Subscriber>();
    	rejects = new ArrayList<Subscriber>();
    }
    
    public void importAll(String directory) {
		File dir = new File(directory);
		importAll(dir);
	}
    
    public void importAll(File directory) {
    	if(directory.isDirectory()) {
			File[] files = directory.listFiles();
			for(File f : files) {
				try {
					ImportFile imp = ImportFileFactory.createImportFile(f.getAbsolutePath());
					System.out.println(f.getAbsolutePath());
					List<Subscriber> subs = imp.readFile(f);
					allSubscribers.addAll(subs);
				} catch(IllegalArgumentException e) {
					// TODO: Do something reasonable here
					// e.printStackTrace();
				}
			}
		}
		Collections.sort(allSubscribers);
		removeBadAddresses();
		removeDuplicates();
    }
    
    /**
	 * Remove addresses that are invalid
	 */
	public void removeBadAddresses() {
		Iterator<Subscriber> it = allSubscribers.iterator();
		while(it.hasNext()) {
			Subscriber s = it.next();
			if(!s.correctAdress()) {
				s.setDistributor("Invalid");
				rejects.add(s);
				it.remove();
			}
		}
	}
	
	/**
	 * Assumes that the list of subscribers is sorted
	 */
	public void removeDuplicates() {
		Iterator<Subscriber> it = allSubscribers.iterator();
		while(it.hasNext()) {
			Subscriber s1 = it.next();
			if(it.hasNext()) {
				Subscriber s2 = it.next();
				if(s1.comparePrenumerant(s2)) {
					s2.setDistributor("Duplicate");
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
				s.setDistributor("NoThanks");
			}
			rejects.addAll(declines);
		} catch(IOException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	public List<Subscriber> runFilter(String filterPath) {
		List<Subscriber> subscribers = new ArrayList<Subscriber>();
		Filter filter = new Filter();
		filter.readFile(filterPath);
		for(Subscriber s : allSubscribers) {
			try {
				int zipcode = Integer.parseInt(s.getZipCode());
				if(filter.matches(zipcode)) {
					subscribers.add(s);
				}
			} catch(NumberFormatException ne) {
				// This is just some random output when there are errors
				VTDSubscriber vs = new VTDSubscriber(s);
				System.out.println(vs.toString());
				System.exit(1);
			}
		}
		return subscribers;
	}
	
    
	public int size() { return allSubscribers.size(); }
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
    	SecondVTD_TB_VTAB,
    	GotSecondMiss,
    	Finalised;
    }
    
}
