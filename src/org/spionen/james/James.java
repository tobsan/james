package org.spionen.james;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;

/**
 * This is the controller class for James
 * @author Tobias Olausson
 *
 */
public class James {
    
	private JamesFrame jf;
	public James() {
		
		// Create the view
		jf = new JamesFrame();
		addListeners(jf);
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
	
	/**
	 * Adds all action listeners that James needs
	 * @param jf the view to add the listeners to.
	 */
	private void addListeners(final JamesFrame jf) {
		
		jf.addCreateIssueListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: Do something
			}
		});
		
		jf.addGetIssueListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: Do something
			}
		});
		
		// Create new address base
		jf.addAddressListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				try {
					Importer.importAll(year, issue);
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		
		// Show statistics
		jf.addStatisticsListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				try {
					ListHelpers.getAllDistributorStatistic(year, issue);
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		
		// Export to VTD/TB
		jf.addVTDListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				try {
					Exporter.exportToVTDetTB(year, issue);
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		
		jf.addKomplettListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				try {
					Exporter.exportToVTAB_Complete(year, issue);
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
					Exporter.exportToVTAB_JustBring(year, issue);
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
					Exporter.exportToVTAB_BringAndSpecials(year, issue);
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
					Exporter.exportToVTAB_JustPosten(year, issue);
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
		
		jf.addVTDAddressListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				try {
					VTD_NoFind.noFind(year, issue);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		jf.addVTDMissListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				try {
					VTD_NoFind.registerFromList(year, issue);
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});

		jf.addRemoveListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				try {
					NoThanks.noThanks(year, issue);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
