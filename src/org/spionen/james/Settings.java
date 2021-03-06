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
import java.io.IOException;
import java.io.Serializable;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Settings implements Serializable {
	private static final long serialVersionUID = -6284149429420472389L;
	private static final String DB_NAME = "james.db";
	private static final String JAMES_NODE_PATH = "/org/spionen/james";
	
	// File paths
	private File dbPath;
	private File basePath;
	
	public Settings(String basePath) throws IOException {
		setBasePath(new File(basePath));
	}
	
	public void save() throws IOException {
		Preferences prefs = Preferences.userRoot().node(JAMES_NODE_PATH);
		prefs.put("JAMES_PATH", basePath.getAbsolutePath());
	}
	
	public static Settings load() throws IOException {
		Preferences prefs = Preferences.userRoot().node(JAMES_NODE_PATH);
		
		String path = prefs.get("JAMES_PATH", null);
		if(path == null) {
			return null;
		}
		return new Settings(path);
	}
	
	public static Settings loadOrCreate() throws IOException {
		Settings s = load();
		if(s != null) {
			return s;
		}
		
		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = jfc.showDialog(null, "Select James directory");
		if(result == JFileChooser.APPROVE_OPTION) {
			s = new Settings(jfc.getSelectedFile().getPath());
			s.save();
			return s;
		} else {
			return null;
		}
	}

	public File getBasePath() {
		return basePath;
	}

	public void setBasePath(File basePath) throws IOException {
		this.basePath = basePath;
		this.dbPath = new File(basePath.getAbsolutePath() + File.separator + DB_NAME);
		if(!basePath.exists() && !basePath.mkdirs()) {
			throw new IOException("Can't create base path: " + basePath.getAbsolutePath());
		} else if(basePath.exists() && !basePath.isDirectory()) {
			throw new IOException("Base path must be a directory: " + basePath.getAbsolutePath());
		} else if (basePath.exists() && (!basePath.canRead() || !basePath.canWrite()) ) {
			throw new IOException("Base path directory can't be read and/or written");
		} else if(dbPath.exists() && (!dbPath.canWrite() || !dbPath.canRead())) {
			throw new IOException("Database file can't be read and/or written: " + dbPath.getAbsolutePath());
		}
		
		if(!dbPath.exists() || dbPath.length() == 0) {
			JOptionPane.showMessageDialog(null, "Click OK to create database. This may take up to a minute");
			DBConnection.setDBFile(dbPath);
			DBConnection.createDatabase();
		}
	}
	
	public File getDBPath() {
		return dbPath;
	}
}
