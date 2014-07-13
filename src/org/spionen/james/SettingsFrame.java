package org.spionen.james;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A view class to represent the settings/preferences dialog
 * TODO: How to communicate result of this dialog back?
 * 
 * @author Tobias Olausson
 *
 */
public class SettingsFrame extends JFrame {
	
	private JPanel mainPanel;
	private GridLayout grid;
	
	private JLabel basePathLabel;
	private JFileChooser basePathChooser; // TODO: Use some better data type
	private JLabel ftpHostLabel;
	private JTextField ftpHost;
	private JLabel ftpUserLabel;
	private JTextField ftpUser;
	private JLabel ftpPassLabel;
	private JTextField ftpPass;
	private JLabel ftpChannelLabel;
	private JTextField ftpChannel;
	
	private JButton saveButton;
	private JButton cancelButton;
	
	public SettingsFrame() {
		super();
		this.initComponents();
		this.pack();
	}
	
	// Initialize components
	private void initComponents() {
		mainPanel = new JPanel();
		grid = new GridLayout();
		
		basePathLabel = new JLabel("James base path");
		basePathChooser = new JFileChooser();
		
		ftpHostLabel = new JLabel("FTP hostname");
		ftpHost = new JTextField("example.com"); //TODO: Never actually use example.com
		
		ftpUserLabel = new JLabel("FTP username");
		ftpUser = new JTextField();
		
		ftpPassLabel = new JLabel("FTP password");
		ftpPass = new JTextField();
		
		ftpChannelLabel = new JLabel("FTP channel");
		ftpChannel = new JTextField();
		
		saveButton = new JButton("Save");
		cancelButton = new JButton("Cancel");
	}
	
	public void addSaveListener(ActionListener al) {
		saveButton.addActionListener(al);
	}
	
	public void addCancelListener(ActionListener al) {
		cancelButton.addActionListener(al);
	}
	
	public Settings getValues() throws IOException {
		return new Settings(basePathChooser.getSelectedFile().getAbsolutePath(),
							ftpHost.getText(),ftpUser.getText(), ftpPass.getText(),
							ftpChannel.getText());
	}
}