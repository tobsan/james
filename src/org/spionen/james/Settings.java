package org.spionen.james;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.JFileChooser;
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
	
	public static Settings loadOrCreate() throws IOException {
		// Check for settings file and try to load
		File home = new File(System.getenv("HOME"));
		File jamesSettings = new File(home.getPath() + File.pathSeparator + settingsFile);
		if(jamesSettings.exists() && jamesSettings.isFile()) {
			return null;
			// Read settings from the file
		} else {
			// Prompt for values and create settings file
			JFileChooser jfc = new JFileChooser();
			jfc.setMultiSelectionEnabled(false);
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			// Load address base directory
			int result = jfc.showDialog(null, "Select James directory");
			if(result == JFileChooser.APPROVE_OPTION) {
				return new Settings(jfc.getSelectedFile().getPath(), null, null, null, null);
			} else {
				return null;
			}
		}
	}
	
	public Settings(String basePath, String ftpHost, String ftpUser, 
					String ftpPass, String ftpChannel) throws IOException {
		
		this.basePath = new File(basePath);
		this.filterPath = new File(basePath + File.separator + "filter");
		this.referencePath = new File(basePath + File.separator + "reference");
		checkPaths();
		
		this.ftpHost = ftpHost;
		this.ftpUser = ftpUser;
		this.ftpPass = ftpPass;
		this.ftpChannel = ftpChannel;
		// TODO: Check connection using supplied values
		
	}
	
	// TODO: Check so that the paths are in fact directories and not files
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
	
	public FTPConnection createTBConnection() {
		if(ftpHost != null && ftpUser != null && ftpPass != null && ftpChannel != null) {
			return new FTPConnection(ftpHost, ftpUser, ftpPass, ftpChannel);
		} else {
			//TODO: Prompt for these values
			return null;
		}
	}
	
	/**
	 * Creates a filter, given a Distributor from file:
	 * basePath / filter / Distributor.txt
	 * 
	 * If the file does not exist, create it and load an empty filter.
	 * 
	 * @param d the distributor for which to create the filter
	 * @return
	 */
	public Filter createFilter(Distributor d) throws IOException, NumberFormatException {
		String filterFile = filterPath.getPath() + File.separator + d.toString() + ".txt";
		File filterFileFile = new File(filterFile);
		if(!filterFileFile.exists() && !filterFileFile.createNewFile()) {
			throw new IOException("Filter file missing, and could not create empty filter file");
		}
		return new Filter(d, filterFile);
	}
}
