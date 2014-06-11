package org.spionen.james;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
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

	private JPanel mainPanel;
	
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
	private JButton createIssue;
	private JButton getIssue;
	// Import
	private JPanel importPanel;
	private JButton addressBaseButton;
	// Statistik
	private JPanel statsPanel;
	private JButton statsButton;
	//Export
	private JPanel exportPanel;
	private JPanel vtdPanel;
	private JButton exportButton;
	private JPanel vtabPanel;
	private JButton komplettButton;
	private JButton bringOnlyButton;
	private JButton bringSpecialButton;
	private JButton postenOnlyButton;
	// Registerunderhåll
	private JPanel registryPanel;
	private JButton nonVTDButton;
	private JButton vtdMissButton;
	private JButton removeButton;
	
	public JamesFrame() {
		super();
		initComponents();
	}
	
	/**
	 * Initialize all graphical components
	 */
	private void initComponents() {
		GridBagConstraints c = new GridBagConstraints();
		c.ipadx = 5;
		c.ipady = 5;
		c.insets = new Insets(10,10,10,10);
		c.fill = GridBagConstraints.BOTH;
		// The "James" panel
		logoPanel = new JPanel();
		logoPanel.setLayout(new GridBagLayout());
		// TODO: Better path
		logoLabel = new JLabel(new ImageIcon("/home/gargravarr/workspace/James/bin/james/resources/JamesMD.png"));
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
		issueYear = new JLabel ("År (YYYY)");
		issueYearField = new JTextField("  2014  ");
		issueYearField.setHorizontalAlignment(JTextField.CENTER);
		issueNumber = new JLabel ("Nummer (N)");
		issueNumberField = new JTextField("  1  ");
		issueNumberField.setHorizontalAlignment(JTextField.CENTER);
		createIssue = new JButton("Skapa angivet nummer");
		getIssue = new JButton("Hämta angivet nummer");
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
		issuePanel.add(createIssue,c);
		c.gridx = 2;
		issuePanel.add(getIssue, c);
		c.gridwidth = 1;
		// The import panel
		importPanel = new JPanel();
		importPanel.setBorder(BorderFactory.createTitledBorder("Import"));
		addressBaseButton = new JButton("Skapa ny adressbas");
		importPanel.add(addressBaseButton);
		// The statistics panel
		statsPanel = new JPanel();
		statsPanel.setBorder(BorderFactory.createTitledBorder("Statistik"));
		statsButton = new JButton("Se statistik");
		statsPanel.add(statsButton);
		// The export panel
		exportPanel = new JPanel();
		exportPanel.setBorder(BorderFactory.createTitledBorder("Export"));
		exportPanel.setLayout(new GridBagLayout());
		// The VTD/TB panel
		vtdPanel = new JPanel();
		vtdPanel.setBorder(BorderFactory.createTitledBorder("VTD & TB"));
		vtdPanel.setLayout(new GridBagLayout());
		exportButton = new JButton("Exportera");
		c.gridx = 0;
		c.gridy = 0;
		vtdPanel.add(exportButton, c);
		c.gridheight = 1;
		// The V-TAB panel
		vtabPanel = new JPanel();
		vtabPanel.setBorder(BorderFactory.createTitledBorder("V-Tab"));
		vtabPanel.setLayout(new GridBagLayout());
		komplettButton = new JButton("Komplett");
		bringOnlyButton = new JButton("Bara Bring");
		bringSpecialButton = new JButton("Bring + Special");
		postenOnlyButton = new JButton("Bara Posten");
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
		registryPanel.setBorder(BorderFactory.createTitledBorder("Registerunderhåll"));
		registryPanel.setLayout(new GridBagLayout());
		nonVTDButton = new JButton("Reg. ej funnen VTD-adress");
		vtdMissButton = new JButton("Reg. VTD misslista");
		removeButton = new JButton("Ta bort prenumerant");
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
		mainPanel.add(importPanel,c);
		c.gridx = 2;
		mainPanel.add(statsPanel,c);
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		mainPanel.add(exportPanel,c);
		c.gridy = 2;
		mainPanel.add(registryPanel,c);
		
		// Internal stuff
		setContentPane(mainPanel);
		pack();
	}
	
	public void addAddressListener(ActionListener al) {
		addressBaseButton.addActionListener(al);
	}
	
	public void addStatisticsListener(ActionListener al) {
		statsButton.addActionListener(al);
	}
	
	public void addVTDListener(ActionListener al) {
		exportButton.addActionListener(al);
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
