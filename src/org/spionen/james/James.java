package org.spionen.james;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.spionen.james.MasterFile.State;
import org.spionen.james.importing.Importer;
import org.spionen.james.subscriber.Subscriber;

/**
 * This is the controller class for James
 * @author Tobias Olausson
 *
 */
public class James {
    
	private JamesFrame jf;
	private boolean guitest = true;
	
	private MasterFile master;
	private List<Subscriber> vtdSubscribers;
	private List<Subscriber> tbSubscribers;
	private List<Subscriber> bringSubscribers;
	private List<Subscriber> postenSubscribers;
	
	public James() {
		
		// Create the view
		jf = new JamesFrame();
		addListeners(jf);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		
		// Initialise lists, but when should they be populated?
		vtdSubscribers = new ArrayList<Subscriber>();
		tbSubscribers = new ArrayList<Subscriber>();
		bringSubscribers = new ArrayList<Subscriber>();
		postenSubscribers = new ArrayList<Subscriber>();
	}
	
	/**
	 * Adds all action listeners that James needs
	 * @param jf the view to add the listeners to.
	 */
	private void addListeners(final JamesFrame jf) {
		
		jf.addTBSettingsListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(jf, "TB/FTP settings");
			}
		});
		
		jf.addPreferencesListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(jf, "Preferences");
			}
		});
		
		jf.addCreateIssueListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				jf.lockIssue();
				jf.enableImport();
				master = new MasterFile(year, issue);
			}
		});
		
		jf.addGetIssueListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: Do something
				// Show a file chooser and use that file
			}
		});
		
		// Create new address base
		jf.addAddressListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Only if we have a master and it has not yet been initialized
				// may we initialize it.
				if(jf.isLocked() && master.getState() == State.Init) {
					try {
						JFileChooser jfc = new JFileChooser();
						jfc.setMultiSelectionEnabled(false);
						jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						// Load address base directory
						int result = jfc.showDialog(jf, "Load directory");
						if(result == JFileChooser.APPROVE_OPTION) {
							File f = jfc.getSelectedFile().getAbsoluteFile();
							master.importAll(f);
						}

						// Load filter for VTD
						jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
						result = jfc.showDialog(jf, "Load VTD filter");
						if(result == JFileChooser.APPROVE_OPTION) {
							List<Subscriber> subs = master.runFilter(jfc.getSelectedFile().getAbsolutePath());
							vtdSubscribers.addAll(subs);
						}
						
						if(!guitest) { 
							int year = master.getYear();
							int issue = master.getIssue();
							Importer.importAll(year, issue);
						}
						
						// Update internal state
						master.nextState();
						jf.enableDistribution();
						jf.enableStatistics();
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
				} else {
					System.out.println("Not yet initialized!");
					// State info about having to create a new issue
					// to use this function
				}
			}
		});
		
		// Show statistics
		jf.addStatisticsListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// We have to have gone further than initialisation to use this feature
				if(jf.isLocked() && master.getState() != State.Init) {
					   int vtd      = vtdSubscribers.size();
					   int tb       = tbSubscribers.size();
					   int bring    = bringSubscribers.size();
					   int posten   = postenSubscribers.size();
					   int no       = 0; // TODO
					   int special  = 0; // TODO 

					   JOptionPane.showMessageDialog(null,
							   "Statistik enligt nedan:\n"
							   + "Totalt: " + master.size() + " prenumeranter.\n"
							   + "Varav\n"
							   + "         VTD     : " + vtd + " st\n"
							   + "         TB       : " + tb + " st\n"
							   + "         Bring   : " + bring + " st\n"
							   + "         Posten : " + posten + " st (" + special + " special)\n"
							   + "och " + no + " som inte vill ha tidningen alls.");
				} else {
					// Show something about having to create/load issue before usage
				}
			}
		});
		
		// Export to VTD/TB
		jf.addVTDListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(jf.isLocked() && master.getState().compareTo(State.GotSource) >= 0) {
					int year = master.getYear();
					int issue = master.getIssue();
					try {
						if(!guitest) Exporter.exportToVTDetTB(year, issue);
						master.nextState();
						jf.enableRegistryMaint();
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
				} else {
					//Inform about state needing to be "higher"
				}
			}
		});
		
		jf.addTBListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: How to get user/pass? Dialog or through settings?
				/*
				TBConnection tbc = new TBConnection();
				tbc.connect();
				tbc.sendFile(tbfile);
				*/
			}
		});
		
		jf.addKomplettListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				try {
					if(!guitest) Exporter.exportToVTAB_Complete(year, issue);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		jf.addBringListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				try {
					if(!guitest) Exporter.exportToVTAB_JustBring(year, issue);
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		
		jf.addBringSpecialListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				try {
					if(!guitest) Exporter.exportToVTAB_BringAndSpecials(year, issue);
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		
		jf.addPostenListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				try {
					if(!guitest) Exporter.exportToVTAB_JustPosten(year, issue);
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		
		jf.addVTDAddressListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(jf.isLocked()) {
					int year = master.getYear();
					int issue = master.getIssue();
					try {
						if(!guitest) VTD_NoFind.noFind(year, issue);
						jf.enableVTab();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		jf.addVTDMissListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(jf.isLocked()) {
					int year = master.getYear();
					int issue = master.getIssue();
					try {
						if(!guitest) VTD_NoFind.registerFromList(year, issue);
						jf.enableVTab();
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		});

		jf.addRemoveListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				try {
					if(!guitest) NoThanks.noThanks(year, issue);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
