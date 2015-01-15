/**
*   Copyright 2010-2015 Maxim Fris, Tobias Olausson
*
*   This file is part of James.
*
*   James is free software: you can redistribute it and/or modify
*   it under the terms of the GNU General Public License as published by
*   the Free Software Foundation, either version 3 of the License, or
*   (at your option) any later version.
*
*   James is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.
*
*   You should have received a copy of the GNU General Public License
*   along with James. If not, see <http://www.gnu.org/licenses/>.
*/
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
	
