package org.spionen.james;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

public class DBConnection {
	 
	private static Connection c;
	private static String dbfile;
	
	public static void setDBFile(File dbfile) {
		DBConnection.dbfile = dbfile.getAbsolutePath();
	}
	
	/**
	 * TODO: Make sure the database is populated the first time the connection
	 * 		 is needed, or run createDatabase() otherwise
	 * @return
	 * @throws SQLException
	 */
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
		boolean success = false;
		try {
			Connection c = getConnection();
			SQLScriptRunner sr = new SQLScriptRunner(c, false, true);
			try {
				InputStream setupResource = DBConnection.class.getResourceAsStream("/org/spionen/james/resources/database_setup.sql");
				InputStream filterResource = DBConnection.class.getResourceAsStream("/org/spionen/james/resources/setup_filters.sql");
				sr.runScript(new InputStreamReader(setupResource));
				sr.runScript(new InputStreamReader(filterResource));
				success = true;
			} catch(FileNotFoundException fnfe) {
				System.out.println("Database setup files not found: " + fnfe.getMessage());
				fnfe.printStackTrace();
			} catch(IOException ioe) {
				System.out.println("I/O error: " + ioe.getMessage());
				ioe.printStackTrace();
			}
			
		} catch(SQLException sqle) {
			System.out.println("Database error: " + sqle.getMessage());
			sqle.printStackTrace();
		}
		
		return success;
	}
}
	
