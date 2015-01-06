package org.spionen.james;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A view class to represent the settings/preferences dialog
 * 
 * @author Tobias Olausson
 *
 */
public class SettingsDialog extends JDialog {
	
	private static final long serialVersionUID = -6342091137452560157L;
	private JPanel mainPanel;
	private GridLayout grid;
	
	private JLabel basePathLabel;
	private JPanel basePathPanel;
	private JTextField basePathField;
	private JButton basePathChooserBtn;
	
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
	
	public SettingsDialog(Settings current) {
		super();
		this.setResizable(false);
		this.initComponents();
		basePathField.setText(current.getBasePath().getAbsolutePath());
		
		this.pack();
		this.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
		this.setLocationRelativeTo(null);
	}
	
	// Initialize components
	// TODO: Use a better layout manager
	private void initComponents() {
		mainPanel = new JPanel();
		grid = new GridLayout(0,2);
		mainPanel.setLayout(grid);
		
		basePathLabel = new JLabel("James base path");
		basePathPanel = new JPanel();
		basePathField = new JTextField(20);
		basePathChooserBtn = new JButton("Select");
		basePathChooserBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser("Choose base path directory");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fc.showDialog(mainPanel, "Select directory");
				if(result == JFileChooser.APPROVE_OPTION) {
					basePathField.setText(fc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		basePathPanel.add(basePathField);
		basePathPanel.add(basePathChooserBtn);
		
		ftpHostLabel = new JLabel("FTP hostname");
		ftpHost = new JTextField(30);
		
		ftpUserLabel = new JLabel("FTP username");
		ftpUser = new JTextField(30);
		
		ftpPassLabel = new JLabel("FTP password");
		ftpPass = new JTextField(30);
		
		ftpChannelLabel = new JLabel("FTP channel");
		ftpChannel = new JTextField(30);
		
		saveButton = new JButton("Save");
		cancelButton = new JButton("Cancel");
		
		mainPanel.add(basePathLabel);
		mainPanel.add(basePathPanel);
		mainPanel.add(ftpHostLabel);
		mainPanel.add(ftpHost);
		mainPanel.add(ftpUserLabel);
		mainPanel.add(ftpUser);
		mainPanel.add(ftpPassLabel);
		mainPanel.add(ftpPass);
		mainPanel.add(ftpChannelLabel);
		mainPanel.add(ftpChannel);
		mainPanel.add(saveButton);
		mainPanel.add(cancelButton);
		
		this.setContentPane(mainPanel);
	}
	
	public Settings getSettings() throws IOException {
		return new Settings(basePathField.getText());
	}
	
	public void addSaveListener(ActionListener al) {
		saveButton.addActionListener(al);
	}
	
	public void addCancelListener(ActionListener al) {
		cancelButton.addActionListener(al);
	}
}