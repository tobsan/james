package org.spionen.james;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.spionen.james.jamesfile.JamesFile;
import org.spionen.james.jamesfile.JamesFileFactory;
import org.spionen.james.subscriber.Subscriber;
import org.spionen.james.subscriber.Subscriber.Distributor;

/**
 * This should serve well to be some kind of state class
 * @author Maxim Fris
 * @author Tobias Olausson
 */

public class MasterFile implements Serializable {
    
	private static final long serialVersionUID = -5687392826237960600L;
    private int year; 
    private int issue; 
    
    // These two contain ALL subscribers together.
    private Map<Long,Subscriber> subscribers;
    private State state;
    
    public MasterFile(int year, int issue) {
    	this.year = year;
    	this.issue = issue;
    	this.state = State.Init;
    	subscribers = new TreeMap<Long,Subscriber>();
    }
    
    public void importAll(String directory) {
		File dir = new File(directory);
		importAll(dir);
	}
    
    /**
     * This follows the same structure as Importer.importAll:
     * 	1) Read all files and merge lists of subscribers
     *  2) Remove all malformed addresses
     *  3) (Remove duplicates) - a Map cannot have duplicates
     *  4) Missing: Remove all subscribers that have declined to have the paper
     * @param directory
     */
    public void importAll(File directory) {
    	if(directory.isDirectory()) {
			File[] files = directory.listFiles();
			for(File f : files) {
				try {
					JamesFile imp = JamesFileFactory.createImportFile(f.getAbsolutePath());
					System.out.println(f.getAbsolutePath());
					Map<Long,Subscriber> subs = imp.readFile(f);
					subscribers.putAll(subs);
					System.out.println("Master size: " + subscribers.size());
				} catch(IllegalArgumentException e) {
					// This is thrown if files that do not match extensions 
					// are found in the directory, so it is not really a problem
				}
			}
		}
		removeBadAddresses();
    }
    
    /**
	 * Remove addresses that are invalid
	 */
	public Map<Long,Subscriber> removeBadAddresses() {
		Map<Long,Subscriber> result = new TreeMap<Long,Subscriber>();
		Iterator<Map.Entry<Long,Subscriber>> iter = subscribers.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<Long, Subscriber> entry = iter.next();
			Subscriber s = entry.getValue();
			if(!s.correctAdress()) {
				s.setDistributor(Distributor.Invalid);
				result.put(entry.getKey(), s);
				iter.remove();
			}
		}
		System.out.println("Invalid addresses: " + result.size());
		return result;
	}
	
	/**
	 * Mark all subscribers that do not want to get the paper.
	 * This differs from a filter in that it contains individual subscribers
	 * and not just zip codes.
	 */
	public Map<Long,Subscriber> removeDeclines(Map<Long,Subscriber> declines) {
		Map<Long,Subscriber> result = new TreeMap<Long,Subscriber>();
		Iterator<Map.Entry<Long,Subscriber>> it = declines.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Long,Subscriber> entry = it.next();
			Subscriber s = entry.getValue();
			s.setDistributor(Distributor.NoThanks);
			result.put(entry.getKey(), s);
			it.remove();
		}
		return result;
	}
	
	public Map<Long,Subscriber> runFilter(Filter f) {
		Map<Long,Subscriber> subs = new TreeMap<Long,Subscriber>();
		for(long abNr : subscribers.keySet()) {
			Subscriber s = subscribers.get(abNr);
			if(f.apply(s)) {
				subs.put(abNr, s);
			}
		}
		return subs;
	}
	
	/**
	 * This method checks this masterfile against another one, generating a 
	 * list of all stops and all starts (diffs of any kind).
	 *  
	 * @param mf the MasterFile to compare with
	 * @return a Pair with all that are in this but not the other masterfile
	 * 		   as its first part, and all that are in the other masterfile but
	 * 		   not in this one as its second part.
	 */
	public Pair<Map<Long,Subscriber>, Map<Long,Subscriber>> checkAgainst(MasterFile mf) {
		return checkAgainst(mf,true);
	}
	
	private Pair<Map<Long,Subscriber>, Map<Long,Subscriber>> checkAgainst(MasterFile mf, boolean crossCheck) {
		Map<Long,Subscriber> result = new TreeMap<Long,Subscriber>();
		for(long abNr : subscribers.keySet()) {
			if(mf.contains(abNr)) {
				result.put(abNr, subscribers.get(abNr));
			}
		}
		if(crossCheck) { // To prevent infinite recursion
			Pair<Map<Long,Subscriber>, Map<Long,Subscriber>> tmp = mf.checkAgainst(mf, false);
			return new Pair<Map<Long,Subscriber>, Map<Long,Subscriber>>(result, tmp.first());
		} else {
			return new Pair<Map<Long,Subscriber>, Map<Long,Subscriber>>(result, null);
		}
	}
	
	/**
	 * Delegate to the map of subscribers
	 */
	public boolean contains(long abNr) {
		return subscribers.containsKey(abNr);
	}
	
	public Map<Long,Subscriber> exportByDistributor(Distributor d) {
		Map<Long,Subscriber> result = new TreeMap<Long,Subscriber>();
		for(Map.Entry<Long,Subscriber> entry : subscribers.entrySet()) {
			Subscriber s = entry.getValue();
			if(s.getDistributor() == d) {
				result.put(entry.getKey(), s);
			}
		}
		return result;
	}
	
	public int size() { return subscribers.size(); }
    public int getYear() { return year; }
    public int getIssue() { return issue; }    
    
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
