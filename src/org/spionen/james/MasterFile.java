package org.spionen.james;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.spionen.james.jamesfile.JamesFile;
import org.spionen.james.jamesfile.JamesFileFactory;
import org.spionen.james.subscriber.Subscriber;
import org.spionen.james.subscriber.TBSubscriber;
import org.spionen.james.subscriber.VTDSubscriber;
import org.spionen.james.subscriber.VTabSubscriber;
import org.spionen.james.subscriber.Subscriber.Distributor;

/**
 * This is the model class for James. It handles communication with the
 * database and holds the year and number for the issue we're working on.
 * 
 * TODO: Enforce the use of createIssue() && save() as one atomic operation
 * 
 * @author Maxim Fris
 * @author Tobias Olausson
 */

public class MasterFile implements Serializable {
    
	private static final long serialVersionUID = -5687392826237960600L;
    private int year; 
    private int issue; 
    
    public MasterFile(int year, int issue) {
    	this.year = year;
    	this.issue = issue;
    }
    
    public Map<Long, Subscriber> importAll(String directory) {
		File dir = new File(directory);
		if(dir.isDirectory()) {
			return importAll(dir);
		} else {
			return null;
		}
	}
    
    /**
     * This follows the same structure as Importer.importAll:
     * 	1) Read all files and merge lists of subscribers
     *  2) Remove all malformed addresses
     *  3) (Remove duplicates) - a Map cannot have duplicates
     *  4) Missing: Remove all subscribers that have declined to have the paper
     * @param directory
     */
    private Map<Long,Subscriber> importAll(File directory) {
		Map<Long, Subscriber> subscribers = new TreeMap<Long, Subscriber>();
		File[] files = directory.listFiles();
		int totalNum = 0;
		for(File f : files) {
			try {
				JamesFile imp = JamesFileFactory.createImportFile(f.getAbsolutePath());
				System.out.println(f.getAbsolutePath());
				Map<Long,Subscriber> subs = imp.readFile(f);
				totalNum += subs.size();
				subscribers.putAll(subs);
				System.out.println("Total imported: " + totalNum + ", Master size: " + subscribers.size());
			} catch(IllegalArgumentException e) {
				// This is thrown by the JamesFileFactory if files that do not
				// match extensions are found in the directory, so it is not 
				// really a problem
			} catch(IOException ioe) {
				// If there was an error reading a file, or if a file was badly formatted.
			}
		}
		System.out.println("ImportAll, total imported: " + totalNum + ", subscribers: " + subscribers.size());
		
		if(subscribers.size() == 0) {
			return null;
		} else {
			removeBadAddresses(subscribers);
			return subscribers;
		}
    }
    
    /**
     * This method fetches an issue from the database, and reports on whether
     * or not it exists. 
     * 
     * @return true if the issue exists in the database, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean load() throws SQLException {
		String sql = "SELECT * FROM DistributedTo NATURAL JOIN Subscribers " 
				   + "WHERE DistributedTo.IssueYear = '" + year + "' "
				   + "AND DistributedTo.IssueNumber = '" + issue + "' ";
		Connection c = DBConnection.getConnection();
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(sql);
		
		if(rs.isAfterLast()) {
			return false;
		}
		return true;
    }
    
    /**
     * Creates an issue in the database, if it doesn't already exist.
     * @return true if the operation was successful, false if the issue already existed
     * @throws SQLException if a database error occurs
     */
    public boolean createIssue() throws SQLException {
    	String check = "SELECT * FROM Issues WHERE IssueYear = '"+year+"' AND IssueNumber='"+issue+"'";
		String sql = "INSERT INTO Issues VALUES('" + year + "','" + issue +"')";
		Connection c = DBConnection.getConnection();
		Statement st = c.createStatement();
		ResultSet rs = st.executeQuery(check);
		if(rs.isAfterLast()) {
			int rows = st.executeUpdate(sql);
			if(rows == 0) {
				return false;
			}
			return true;
		} else {
			return false;
		}
    }
    
    public void save(Map<Long, Subscriber> subscribers) throws SQLException {
		Iterator<Map.Entry<Long,Subscriber>> iter = subscribers.entrySet().iterator();
		Connection c = DBConnection.getConnection();
		
		c.setAutoCommit(false);
		PreparedStatement stmt = c.prepareStatement("INSERT OR REPLACE INTO Subscribers VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
		PreparedStatement stmt2 = c.prepareStatement("INSERT OR REPLACE INTO DistributedTo VALUES(?, ?, ?)");
		while(iter.hasNext()) {
    		Map.Entry<Long,Subscriber> entry = iter.next();
    		Subscriber s = entry.getValue();
    		if(s.correctAdress()) {
    			if (s.getCity() == null) {
    				System.out.println(s.toString());
    			}
	    		stmt.setString(1, String.valueOf(s.getAbNr()));
	    		stmt.setString(2, s.getFirstName());
	    		stmt.setString(3, s.getLastName());
	    		stmt.setString(4, s.getCoAddress());
	    		stmt.setString(5, s.getStreetAddress());
	    		stmt.setString(6, s.getZipCode());
	    		stmt.setString(7, s.getCity());
	    		stmt.setString(8, s.getCountry());
	    		stmt.setString(9, s.getNote());
	    		stmt.addBatch();
	    		
	    		stmt2.setString(1, String.valueOf(s.getAbNr()));
	    		stmt2.setString(2, String.valueOf(year));
	    		stmt2.setString(3, String.valueOf(issue));
	    		stmt2.addBatch();
    		}
    	}
		stmt.executeBatch();
		stmt2.executeBatch();
    	c.commit();
    	c.setAutoCommit(true);
    }
    
    /**
	 * Remove addresses that are invalid
	 */
	private Map<Long,Subscriber> removeBadAddresses(Map<Long,Subscriber> subscribers) {
		Map<Long,Subscriber> result = new TreeMap<Long,Subscriber>();
		Iterator<Map.Entry<Long,Subscriber>> iter = subscribers.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<Long, Subscriber> entry = iter.next();
			Subscriber s = entry.getValue();
			if(!s.correctAdress()) {
				s.setDistributor(Distributor.INVALID);
				result.put(entry.getKey(), s);
				iter.remove();
			}
		}
		System.out.println("Invalid addresses: " + result.size());
		return result;
	}
	
	/**
	 * Runs filters for new subscribers. In fact this can be run any time
	 * since only subscribers that are not already present in the DistributedBy
	 * table will be added, and only if they match a filter. 
	 *   
	 * @throws SQLException if a database error occurs
	 */
	public void runFilters() throws SQLException {
		Connection c = DBConnection.getConnection();
		c.setAutoCommit(false);
		
		String joinFilter = "INSERT INTO DistributedBy (SubscriberID, Distributor) "
		  + "SELECT SubscriberID, ? FROM Subscribers NATURAL JOIN DistributedTo "
		  + "NATURAL JOIN Filter WHERE Filter.Distributor = ? AND "
		  + "DistributedTo.IssueYear = "+year+" AND DistributedTo.IssueNumber = "+issue+" AND "
		  + "Subscribers.SubscriberID NOT IN (SELECT SubscriberID from DistributedBy)";

		PreparedStatement ps = c.prepareStatement(joinFilter);
		
		ps.setString(1, "VTD");
		ps.setString(2, "VTD");
		ps.addBatch();
		
		ps.setString(1, "TB");
		ps.setString(2, "TB");
		ps.addBatch();
		
		ps.setString(1, "BRING");
		ps.setString(2, "BRING");
		ps.addBatch();
		
		ps.executeBatch();
		
		// Set posten as distributor for anyone that does not match the current filters
		String postenFilter = 
				"INSERT INTO DistributedBy (SubscriberID, Distributor) "
			  + "SELECT SubscriberID, 'POSTEN' FROM Subscribers NATURAL JOIN DistributedTo "
			  + "WHERE IssueYear = " + year + " AND IssueNumber = " + issue + " AND "
			  + "SubscriberID NOT IN (SELECT SubscriberID FROM DistributedBy)";
		Statement st = c.createStatement();
		st.executeUpdate(postenFilter);
		
		c.commit();
		c.setAutoCommit(true);
	}
	
	public ArrayList<Subscriber> getStart(Distributor d) throws SQLException {
		MasterFile prev = previousIssue();
		if (prev != null && prev.load()) {
			String subPrevious = 
						 "SELECT SubscriberID FROM DistributedTo WHERE " 
					   + "IssueYear = " + prev.getYear() 
					   + " AND IssueNumber = " + prev.getIssue();
			String sql = "SELECT * FROM Subscribers NATURAL JOIN DistributedTo NATURAL JOIN DistributedBy WHERE "
					   + "IssueYear = " + year + " AND IssueNumber = " + issue + " AND "
					   + "Distributor = '" + d.name() + "' AND "
					   + "Subscribers.SubscriberID NOT IN (" + subPrevious + ")";
			
			Connection c = DBConnection.getConnection();
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(sql);
			ArrayList<Subscriber> subs = new ArrayList<Subscriber>();
			while(rs.next()) {
				Subscriber s = Subscriber.getFromDB(rs);
				subs.add(s);
			}
			return subs;
		} else {
			return null; 
		}
	}
	
	public ArrayList<Subscriber> getStop(Distributor d) throws SQLException {
		MasterFile prev = previousIssue();
		if (prev != null && prev.load()) {
			
			String subCurrent = 
					 "SELECT SubscriberID FROM DistributedTo WHERE " 
				   + "IssueYear=" + year + " AND IssueNumber=" + issue;
			String sql = "SELECT * FROM Subscribers NATURAL JOIN DistributedTo NATURAL JOIN DistributedBy WHERE "
					   + "IssueYear = " + prev.getYear() + " AND " 
					   + "IssueNumber = " + prev.getIssue() + " AND "
					   + "Distributor = '" + d.name() + "' AND "
					   + "Subscribers.SubscriberID NOT IN (" + subCurrent + ")";
			Connection c = DBConnection.getConnection();
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(sql);
			ArrayList<Subscriber> subs = new ArrayList<Subscriber>();
			while(rs.next()) {
				Subscriber s = Subscriber.getFromDB(rs);
				switch(d) {
				// These two require special formatting
				case VTD: subs.add(new VTDSubscriber(s)); break;
				case TB: subs.add(new TBSubscriber(s)); break;
				default: subs.add(s);
				}
			}
			return subs;
		} else {
			return null; 
		}
	}
	
	public List<Subscriber> getVTABByDistributor(Distributor d) throws SQLException {
		List<Subscriber> subs = new ArrayList<Subscriber>();
		Connection c = DBConnection.getConnection();
		Statement st = c.createStatement();
		String sql = "SELECT * FROM Subscribers NATURAL JOIN DistributedTo "
				   + "NATURAL JOIN DistributedBy WHERE IssueYear = " + year + " AND "
				   + "IssueNumber = " + issue + " AND Distributor = '" + d.name() + "'";
		ResultSet rs = st.executeQuery(sql);
		
		// This is a general thing for every export to VTAB
		while(rs.next()) {
			Subscriber s = Subscriber.getFromDB(rs);
			subs.add(new VTabSubscriber(s));
		}
		return subs;
	}
	
	public List<Subscriber> getVTABVIP() throws SQLException {
		List<Subscriber> subs = new ArrayList<Subscriber>();
		Connection c = DBConnection.getConnection();
		Statement st = c.createStatement();
		String sql = "SELECT * FROM VIP WHERE IssueYear = " + year + " AND IssueNumber = " + issue;
		ResultSet rs = st.executeQuery(sql);
		while(rs.next()) {
			Subscriber s = Subscriber.getFromDB(rs);
			subs.add(new VTabSubscriber(s));
		}
		return subs;
	}
	
	public void runGeographicalfilter() throws SQLException {
		int minZip = 40; // 40xxx zipcodes
		int maxZip = 54; // 54xxx zipcodes
		Connection c = DBConnection.getConnection();
		Statement st = c.createStatement();
		// All non-VIPs that live outside minZip* through maxZip* zipCodes are filtered
		// out of James. This is done for each issue.
		String sql = "INSERT OR REPLACE INTO DistributedBy (SubscriberID, Distributor) "
				   + "SELECT SubscriberID, 'NONE' FROM Subscribers NATURAL JOIN DistributedTo WHERE "
				   + "IssueYear = " + year + " AND IssueNumber = " + issue + " AND "
				   + "length(SubscriberID) > 5 AND "
				   + "(Substr(ZipCode,1,2) < '"+minZip+"' OR Substr(ZipCode,1,2) > '"+maxZip+"')";
		st.executeUpdate(sql);
	}

    public int getYear() { return year; }
    public int getIssue() { return issue; }
    
    public MasterFile previousIssue() throws SQLException {
		Connection c = DBConnection.getConnection();
		Statement st = c.createStatement();
		String sql = "SELECT * FROM Issues ORDER BY IssueYear, IssueNumber DESC";
		ResultSet rs = st.executeQuery(sql);
		while(rs.next()) {
			if(rs.getInt("IssueYear") == year && rs.getInt("IssueNumber") == issue) {
				if(rs.next()) {
					int pYear = rs.getInt("IssueYear");
					int pIssue = rs.getInt("IssueNumber");
					return new MasterFile(pYear, pIssue);
				}
				return null;
			}
		}
		return null;
    }
    
    public boolean equals(Object anotherObject) {
    	if(anotherObject != null && anotherObject instanceof MasterFile) {
    		MasterFile m = (MasterFile)anotherObject;
    		return m.getYear() == year && m.getIssue() == issue;
    	}
    	return false;
    }
    
}
