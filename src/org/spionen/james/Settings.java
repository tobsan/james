package org.spionen.james;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.spionen.james.subscriber.Subscriber.Distributor;

public class Settings implements Serializable {
	private static final long serialVersionUID = -6284149429420472389L;
	private static final String settingsFile = ".jamesrc";
	
	// Where to look for filters, reference files and previous masters
	private File basePath;
	private File filterPath;
	private File referencePath;
	
	// Settings for FTP protocol, as specified by TB
	private String ftpHost;
	private String ftpUser;
	private String ftpPass;
	private String ftpChannel;
	
	// TODO: Decide on using Object or CSV/XML here
	public Settings() throws IOException {
		// Check for settings file and try to load
		File home = new File(System.getenv("HOME"));
		File jamesSettings = new File(home.getPath() + File.pathSeparator + settingsFile);
		if(jamesSettings.exists() && jamesSettings.isFile()) {
			// Read settings object from file, or something
		} else {
			// Prompt for values and create settings file
		}
	}
	
	public Settings(String basePath, String ftpHost, String ftpUser, 
					String ftpPass, String ftpChannel) throws IOException {
		
		this.basePath = new File(basePath);
		this.filterPath = new File(basePath + File.pathSeparator + "filter");
		this.referencePath = new File(basePath + File.pathSeparator + "reference");
		checkPaths();
		
		this.ftpHost = ftpHost;
		this.ftpUser = ftpUser;
		this.ftpPass = ftpPass;
		this.ftpChannel = ftpChannel;
		// TODO: Check connection using supplied values
		
	}
	
	private boolean checkPaths() throws IOException {
		if(!basePath.exists() && !basePath.mkdirs()) {
			throw new IOException("Could not create non-existing base path: " + basePath.getPath());
		} else if(!filterPath.exists() && !filterPath.mkdirs()) {
			throw new IOException("Could not create non-existing filter path: " + filterPath.getPath());
		} else if(!referencePath.exists() && !referencePath.mkdirs()) {
			throw new IOException("Could not create non-existing reference path: " + referencePath.getPath());
		} else {
			return true; // It's all good!
		}
	}
	
	public TBConnection createTBConnection() {
		return new TBConnection(ftpHost, ftpUser, ftpPass, ftpChannel);
	}
	
	/**
	 * Creates a filter, given a Distributor from file:
	 * basePath / filter / Distributor.txt
	 * 
	 * @param d the distributor for which to create the filter
	 * @return
	 */
	public Filter createFilter(Distributor d) throws IOException, NumberFormatException {
		return new Filter(d, filterPath.getPath() + File.pathSeparator + d.toString() + ".txt");
	}
}
