package org.spionen.james;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;

/**
 * The view class for James
 * @author Tobias Olausson
 * @author Maxim Fris (original design)
 *
 */
public class JamesFrame extends JFrame {

	private static final long serialVersionUID = -5877293779273173169L;
	private JPanel mainPanel;
	
	// Menus
	private JMenuBar menubar;
	private JMenu mainMenu;
	private JMenuItem menuExit;
	private JMenuItem preferences;
	private JMenu specialMenu;
	private JMenuItem loadNoThanks;
	private JMenuItem removeData;
	private JMenuItem removeDB;
	
	// James logotype
	private JPanel logoPanel;
	private JLabel logoLabel;
	private JLabel titleLabel;
	private JLabel taglineLabel;
	
	// Aktuellt nummer
	private JPanel issuePanel;
	private JLabel issueYear;
	private JTextField issueYearField;
	private JLabel issueNumber;
	private JTextField issueNumberField;
	private JButton createIssueButton;
	private JButton getIssueButton;
	
	// Statistik
	private JPanel statsPanel;
	private JButton statsButton;
	
	//Export
	private JPanel exportPanel;
	private JPanel vtdPanel;
	private JButton exportVTDButton;
	private JButton exportTBButton;
	private JButton uploadTBButton;
	private JPanel vtabPanel;
	private JButton komplettButton;
	private JButton bringOnlyButton;
	private JButton bringSpecialButton;
	private JButton postenOnlyButton;
	
	// Registry stuff
	private JPanel registryPanel;
	private JButton nonVTDButton;
	private JButton vtdMissButton;
	private JButton removeButton;
	
	public JamesFrame() {
		super();
		initComponents();
		disableAll();
		setLocationRelativeTo(null);
	}
	
	/**
	 * Initialise all graphical components
	 */
	private void initComponents() {
		// First, the menus
		menubar = new JMenuBar();
		mainMenu = new JMenu("Menu");
		preferences = new JMenuItem("Preferences");
		menuExit = new JMenuItem("Exit");
		mainMenu.add(preferences);
		mainMenu.add(menuExit);
		specialMenu = new JMenu("Special");
		loadNoThanks = new JMenuItem("Load NoThanks-list");
		removeData = new JMenuItem("Remove all Issues and Subscribers from DB");
		removeDB = new JMenuItem("Delete the Database");
		specialMenu.add(loadNoThanks);
		specialMenu.addSeparator();
		specialMenu.add(removeData);
		specialMenu.add(removeDB);
		
		menubar.add(mainMenu);
		menubar.add(specialMenu);
		
		this.setJMenuBar(menubar);
		
		// Then, all panels
		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 5;
		c.ipady = 5;
		c.insets = new Insets(10,10,10,10);
		c.fill = GridBagConstraints.BOTH;
		// The "James" panel
		logoPanel = new JPanel();
		logoPanel.setLayout(new GridBagLayout());
		logoLabel = new JLabel(new ImageIcon(getClass().getResource("/org/spionen/james/resources/JamesMD.png")));
		titleLabel = new JLabel("James");
		float titleSize = 25;
		titleLabel.setFont(titleLabel.getFont().deriveFont(titleSize));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		taglineLabel = new JLabel("\"At your service!\"");
		taglineLabel.setHorizontalAlignment(SwingConstants.CENTER);
		c.gridx = 0;
		c.gridy = 0;
		logoPanel.add(logoLabel, c);
		c.gridy = 1;
		c.ipady = 0;
		logoPanel.add(titleLabel, c);
		c.gridy = 2;
		logoPanel.add(taglineLabel, c);
		c.ipady = 5;
		// The panel for "Aktuellt nummer"
		issuePanel = new JPanel();
		issuePanel.setLayout(new GridBagLayout());
		issuePanel.setBorder(BorderFactory.createTitledBorder("Aktuellt Nummer"));
		issueYear = new JLabel ("Year (YYYY)");
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		year = month == Calendar.DECEMBER ? year++ : year;
		issueYearField = new JTextField(String.valueOf(year));
		issueYearField.setHorizontalAlignment(JTextField.CENTER);
		issueNumber = new JLabel ("Issue (N)");
		
		// Summer months suggests issue number 5. Autumn = Month-2
		month = month > 4 && month < 8 ? 5 : month < 5 ? month+1 : month == Calendar.DECEMBER ? 1 : month-2;
		issueNumberField = new JTextField(String.valueOf(month));
		issueNumberField.setHorizontalAlignment(JTextField.CENTER);
		createIssueButton = new JButton("Create issue");
		getIssueButton = new JButton("Load issue");
		c.gridx = 0;
		c.gridy = 0;
		issuePanel.add(issueYear, c);
		c.gridx = 1;
		issuePanel.add(issueYearField, c);
		c.gridx = 2;
		issuePanel.add(issueNumber, c);
		c.gridx = 3;
		issuePanel.add(issueNumberField, c);
		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 2;
		issuePanel.add(createIssueButton,c);
		c.gridx = 2;
		issuePanel.add(getIssueButton, c);
		c.gridwidth = 1;
		// The statistics panel
		statsPanel = new JPanel();
		statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
		statsButton = new JButton("View statistics");
		statsPanel.add(statsButton);
		// The export panel
		exportPanel = new JPanel();
		exportPanel.setBorder(BorderFactory.createTitledBorder("Export"));
		exportPanel.setLayout(new GridBagLayout());
		// The VTD/TB panel
		vtdPanel = new JPanel();
		vtdPanel.setBorder(BorderFactory.createTitledBorder("Distribution"));
		vtdPanel.setLayout(new GridBagLayout());
		exportVTDButton = new JButton("Export to VTD");
		exportTBButton = new JButton("Export to TB");
		uploadTBButton = new JButton("Upload to TB");
		c.gridx = 0;
		c.gridy = 0;
		vtdPanel.add(exportVTDButton, c);
		c.gridy = 1;
		vtdPanel.add(exportTBButton, c);
		c.gridy = 2;
		vtdPanel.add(uploadTBButton, c);
		c.gridheight = 1;
		// The V-TAB panel
		vtabPanel = new JPanel();
		vtabPanel.setBorder(BorderFactory.createTitledBorder("Printing"));
		vtabPanel.setLayout(new GridBagLayout());
		komplettButton = new JButton("Complete");
		bringOnlyButton = new JButton("Only Bring");
		bringSpecialButton = new JButton("Bring + VIP");
		postenOnlyButton = new JButton("Only VIP");
		c.gridx = 0;
		c.gridy = 0;
		vtabPanel.add(komplettButton, c);
		c.gridy = 1;
		vtabPanel.add(bringOnlyButton, c);
		c.gridy = 2;
		vtabPanel.add(bringSpecialButton, c);
		c.gridy = 3;
		vtabPanel.add(postenOnlyButton, c);
		// Add both sub-panels to export panel
		c.gridx = 0;
		c.gridy = 0;
		exportPanel.add(vtdPanel, c);
		c.gridx = 1;
		exportPanel.add(vtabPanel, c);
		// The panel for registry maintenance
		registryPanel = new JPanel();
		registryPanel.setBorder(BorderFactory.createTitledBorder("Registry maintenance"));
		registryPanel.setLayout(new GridBagLayout());
		nonVTDButton = new JButton("Register address as non-VTD");
		vtdMissButton = new JButton("Register VTD miss-list");
		removeButton = new JButton("Remove subscriber");
		c.gridx = 0;
		c.gridy = 0;
		registryPanel.add(nonVTDButton, c);
		c.gridx = 1;
		registryPanel.add(vtdMissButton, c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		registryPanel.add(removeButton, c);
		
		// Put it all together, shall we?
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		c.gridwidth = 1;
		c.gridheight = 2;
		c.gridx = 0;
		c.gridy = 0;
		mainPanel.add(logoPanel, c);
		c.gridheight = 1;
		c.gridy = 2;
		mainPanel.add(issuePanel, c);
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		mainPanel.add(statsPanel,c);
		c.gridx = 1;
		c.gridy = 1;
		mainPanel.add(exportPanel,c);
		c.gridy = 2;
		mainPanel.add(registryPanel,c);
		
		// Internal stuff
		setContentPane(mainPanel);
		pack();
	}
	
	public int getYear() {
		return Integer.parseInt(issueYearField.getText().trim());
	}
	
	public int getIssue() {
		return Integer.parseInt(issueNumberField.getText().trim());
	}
	
	public void lockIssue() {
		issueNumberField.setEnabled(false);
		issueYearField.setEnabled(false);
		createIssueButton.setEnabled(false);
		getIssueButton.setEnabled(false);
	}
	
	public void unlockIssue() {
		issueNumberField.setEnabled(true);
		issueYearField.setEnabled(true);
		createIssueButton.setEnabled(true);
		getIssueButton.setEnabled(true);
	}
	
	/**
	 * Disable all buttons except create/load issue 
	 */
	public void disableAll() {
		unlockIssue();
		bringOnlyButton.setEnabled(false);
		bringSpecialButton.setEnabled(false);
		exportVTDButton.setEnabled(false);
		exportTBButton.setEnabled(false);
		uploadTBButton.setEnabled(false);
		komplettButton.setEnabled(false);
		postenOnlyButton.setEnabled(false);
		statsButton.setEnabled(false);
	}
	
	public void enableAll() {
		lockIssue();
		bringOnlyButton.setEnabled(true);
		bringSpecialButton.setEnabled(true);
		exportVTDButton.setEnabled(true);
		exportTBButton.setEnabled(true);
		// uploadTBButton.setEnabled(true);
		komplettButton.setEnabled(true);
		postenOnlyButton.setEnabled(true);
		statsButton.setEnabled(true);
	}
	
	/*
	 * Only listeners below
	 */
	
	public void addJamesLogoListener(MouseListener ml) {
		logoLabel.addMouseListener(ml);
	}
	
	public void addPreferencesListener(ActionListener al) {
		preferences.addActionListener(al);
	}
	
	public void addMenuExitListener(ActionListener al) {
		menuExit.addActionListener(al);
	}
	
	public void addReadNoThanksListener(ActionListener al) {
		loadNoThanks.addActionListener(al);
	}
	
	public void addRemoveDataListener(ActionListener al) {
		removeData.addActionListener(al);
	}
	
	public void addRemoveDBListener(ActionListener al) {
		removeDB.addActionListener(al);
	}
	
	public void addCreateIssueListener(ActionListener al) {
		createIssueButton.addActionListener(al);
	}
	
	public void addGetIssueListener(ActionListener al) {
		getIssueButton.addActionListener(al);
	}
	
	public void addStatisticsListener(ActionListener al) {
		statsButton.addActionListener(al);
	}
	
	public void addVTDListener(ActionListener al) {
		exportVTDButton.addActionListener(al);
	}
	
	public void addTBListener(ActionListener al) {
		exportTBButton.addActionListener(al);
	}
	
	public void addKomplettListener(ActionListener al) {
		komplettButton.addActionListener(al);
	}
 	
	public void addBringListener(ActionListener al) {
		bringOnlyButton.addActionListener(al);
	}
	
	public void addBringSpecialListener(ActionListener al) {
		bringSpecialButton.addActionListener(al);
	}
	
	public void addPostenListener(ActionListener al) {
		postenOnlyButton.addActionListener(al);
	}
	
	public void addVTDAddressListener(ActionListener al) {
		nonVTDButton.addActionListener(al);
	}
	
	public void addVTDMissListener(ActionListener al) {
		vtdMissButton.addActionListener(al);
	}
	
	public void addRemoveListener(ActionListener al) {
		removeButton.addActionListener(al);
	}
}
