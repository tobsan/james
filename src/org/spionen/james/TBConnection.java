package org.spionen.james;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;

/**
 * This class handles the FTP connection to TidningsBärarna
 * @author Tobias Olausson
 *
 */
public class TBConnection {
	private String hostname = "ftp.tidningsbararna.se"; // Standard URL
	private String username;
	private String password;
	private FTPClient client;
	private String channel;
	
	public TBConnection(String hostname, String username, String password, String channel) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.channel = channel;
		client = new FTPClient();
	}
	
	public TBConnection(String username, String password, String channel) {
		this.username = username;
		this.password = password;
		this.channel = channel;
	}
	
	public void connect() {
		try {
			client.connect(hostname);
			client.login(username, password);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a file to TB using the required FTP protocol defined as:
	 * 
	 * OPEN <UTB_URL>
	 * USER <user> <pwd>
	 * APPEND <utfil> FTPFIN01
	 * QUOTE RCMD "call FTCLIN<kk>"
	 * CLOSE
	 * QUIT
	 * 
	 * Where utfil is the file to be sent, and kk is the channel
	 * given to us by TB.
	 * 
	 * @param filename the file we want to send to TB
	 */
	public void sendFile(String filename) {
		File f = new File(filename);
		if(client.isConnected()) {
			if(f.exists() && f.canRead()) {
				try {
					InputStream is = new FileInputStream(filename);
					client.appendFile("FTPFIN01", is);
					client.sendCommand("QUOT", "RCMD \"call FTCLIN" + channel + "\"");
					client.sendCommand("CLOSE");
					client.logout();
				} catch(IOException e) {
					e.printStackTrace();
					// Do....eh, something?
				}
			}
		}
	}
	
}
