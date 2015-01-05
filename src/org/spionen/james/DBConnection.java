package org.spionen.james;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class DBConnection {
	 
	private static Connection c;
	private static String dbfile;
	
	public static void setDBFile(File dbfile) {
		DBConnection.dbfile = dbfile.getAbsolutePath();
	}
	
	public static Connection getConnection() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
			if(c == null || c.isClosed() ) {
				c = DriverManager.getConnection("jdbc:sqlite:" + dbfile);
			}
			return c;
		} catch(ClassNotFoundException cnfe) {
			System.out.println("SQLite database driver not found (this should not happen)");
			return null;
		}
	}
	
	public static boolean createDatabase() {
		try {
			Connection c = getConnection();
			SQLScriptRunner sr = new SQLScriptRunner(c, false, true);
			try {
				sr.runScript(new FileReader(new File("resources/database_setup.sql")));
				sr.runScript(new FileReader(new File("resources/setup_filters.sql")));
				return true;
			} catch(FileNotFoundException fnfe) {
				System.out.println("Database setup files not found: " + fnfe.getMessage());
				fnfe.printStackTrace();
				return false;
			} catch(IOException ioe) {
				System.out.println("I/O error: " + ioe.getMessage());
				ioe.printStackTrace();
				return false;
			}
			
		} catch(SQLException sqle) {
			System.out.println("Database error: " + sqle.getMessage());
			sqle.printStackTrace();
			return false;
		}

	}
}
	
