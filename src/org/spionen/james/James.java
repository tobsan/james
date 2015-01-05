package org.spionen.james;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.spionen.james.jamesfile.JamesFile;
import org.spionen.james.jamesfile.JamesFileFactory;
import org.spionen.james.subscriber.Subscriber;
import org.spionen.james.subscriber.Subscriber.Distributor;

/**
 * This is the controller class for James
 * @author Tobias Olausson
 *
 */
public class James {
    
	private JamesFrame jf;
	private MasterFile master;
	private Settings settings;
			
	public James(Settings settings) {
		this.settings = settings;
		DBConnection.setDBFile(settings.getDBPath());
		
		// Initialize data structures
		master = null;
		
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
		
		jf.addPreferencesListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final SettingsDialog sf = new SettingsDialog();
				sf.addCancelListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sf.setVisible(false);
						sf.dispose();
					}
				});
				sf.addSaveListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JOptionPane.showMessageDialog(jf, "Settings saved (not really)");
						try {
							Settings s = sf.getSettings();
							settings.setBasePath(s.getBasePath());
							
							sf.setVisible(false);
							sf.dispose();
						} catch(IOException ioe) {
							JOptionPane.showMessageDialog(jf, "Error: " + ioe.getMessage() + "\n Try again.");
						}
					}
				});
				sf.setVisible(true);
			}
		});
		
		jf.addMenuExitListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Quit the application
				jf.dispose();
				System.exit(0);
			}
		});
		
		jf.addReadNoThanksListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setMultiSelectionEnabled(false);
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if(jfc.showOpenDialog(jf) == JFileChooser.APPROVE_OPTION) {
					File f = jfc.getSelectedFile();
					JamesFile imp = JamesFileFactory.createImportFile(f.getAbsolutePath());
					Map<Long,Subscriber> noThanks = imp.readFile(f);
					try {
						Connection c = DBConnection.getConnection();
						c.setAutoCommit(false);
						for(long id : noThanks.keySet()) {
							Statement st = c.createStatement();
							st.executeUpdate("UPDATE DistributedBy SET Distributor='NONE' WHERE SubscriberID='"+id+"'");
						}
						c.commit();
						c.setAutoCommit(true);
						JOptionPane.showConfirmDialog(jf, "NoThanks list read and subscribers updated");
					} catch(SQLException sqle) {
						
					}
				}
			}
		});
		
		jf.addRemoveDataListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sure = JOptionPane.showConfirmDialog(jf, "Are you sure?", "Delete data?", JOptionPane.YES_NO_OPTION);
				if(sure == JOptionPane.OK_OPTION) {
					try {
						Connection c = DBConnection.getConnection();
						Statement s = c.createStatement();
						c.setAutoCommit(false);
						s.executeUpdate("DELETE FROM DistributedBy");
						s.executeUpdate("DELETE FROM DistributedTo");
						s.executeUpdate("DELETE FROM Subscribers");
						s.executeUpdate("DELETE FROM Issues");
						c.commit();
						c.setAutoCommit(true);
						jf.disableAll();
						master = null;
					} catch(SQLException sqle) {
						System.out.println("Database error: " + sqle.getMessage());
						sqle.printStackTrace();
					}
				}
			}
		});
		
		jf.addRemoveDBListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sure = JOptionPane.showConfirmDialog(jf, "Are you sure?", "Delete data?", JOptionPane.YES_NO_OPTION);
				if(sure == JOptionPane.OK_OPTION) {
					try {
						Connection c = DBConnection.getConnection();
						c.close();
					} catch(SQLException sqle) {
						System.out.println("Database error: " + sqle.getMessage());
						sqle.printStackTrace();
					} finally {
						File db = settings.getDBPath();
						if(db.delete() && DBConnection.createDatabase()) {
							jf.disableAll();
							master = null;
							JOptionPane.showMessageDialog(jf, "Successfully deleted and recreated the database");
						} else {
							JOptionPane.showMessageDialog(jf, "Could not delete database", "Delete failed", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});
		
		// Double-clicking on the logo resets the program state
		jf.addJamesLogoListener(new MouseListener() {
			public void mouseClicked(MouseEvent me) {
				if(me.getClickCount() == 2) {
					jf.disableAll();
					master = null;
				}
				
			}

			public void mouseEntered(MouseEvent arg0) {	}
			public void mouseExited(MouseEvent arg0) { }
			public void mousePressed(MouseEvent arg0) { }
			public void mouseReleased(MouseEvent arg0) { }
			
		});

		final CreateIssueListener createListener = new CreateIssueListener() {
			private ActionListener getListener = null;
			public void setListener(ActionListener getListener) {
				this.getListener = getListener;
			}
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				master = new MasterFile(year, issue);
				
				try {
					if(master.createIssue()) {
						// And load directory with addresses
						JFileChooser jfc = new JFileChooser();
						jfc.setMultiSelectionEnabled(false);
						jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						// Load address base directory
						int result = jfc.showDialog(jf, "Load directory");
						if(result == JFileChooser.APPROVE_OPTION) {
							String dir = jfc.getSelectedFile().getAbsolutePath();
							
							// Load and dump into the database
							Map<Long, Subscriber> subs = master.importAll(dir);
							if(subs != null) {
								master.save(subs);
								master.runFilters();
								
								// Update internal state
								jf.enableAll();
							} else {
								JOptionPane.showMessageDialog(jf, "Selected file is not a directory, or did not contain any subscribers");
							}
						}
					} else {
						String text = "That issue already exists.\nWould you like to load it?";
						int val = JOptionPane.showConfirmDialog(jf, text, "Issue already exists", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
						if(val == JOptionPane.YES_OPTION) {
							getListener.actionPerformed(null);
						}
					}
				} catch(SQLException sqle) {
					System.out.println("Database error: " + sqle.getMessage());
					sqle.printStackTrace();
				}
			}
		};
		
		final ActionListener getListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				try {
					master = new MasterFile(year, issue);
					if(master.load()) {
						jf.enableAll();
					} else {
						String text = "James could not find that issue.\nWould you like to create it?";
						int val = JOptionPane.showConfirmDialog(jf, text, "Issue not found", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
						if(val == JOptionPane.YES_OPTION) {
							createListener.actionPerformed(null);
						}
					}
				} catch(SQLException sqle) {
					System.out.println("Database error: " + sqle.getMessage());
					sqle.printStackTrace();
				}
			}
		};
		
		jf.addCreateIssueListener(createListener);
		jf.addGetIssueListener(getListener);
		createListener.setListener(getListener);
		
		// Show statistics
		jf.addStatisticsListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				int year = jf.getYear();
				int issue = jf.getIssue();
				String sql = "SELECT Distributor, COUNT(Distributor) AS count FROM DistributedBy "
						   + "NATURAL JOIN DistributedTo WHERE "
						   + "IssueYear='" + year + "' AND IssueNumber='" + issue + "' "
						   + "GROUP BY Distributor";
				String specialsql = "SELECT 'VIP', COUNT(*) as count FROM VIP";
				try {
					Connection c = DBConnection.getConnection();
					Statement st = c.createStatement();
					ResultSet rs = st.executeQuery(sql);
					
					int vtd, tb, bring, posten, nothanks;
					vtd = tb = bring = posten = nothanks = 0;
					while(rs.next()) {
						switch(rs.getString("Distributor")) {
						case "VTD": vtd = rs.getInt("count"); break;
						case "TB": tb = rs.getInt("count"); break;
						case "BRING": bring = rs.getInt("count"); break;
						case "POSTEN": posten = rs.getInt("count"); break;
						case "NONE": nothanks = rs.getInt("count"); break;
						}
					}
					int sum = vtd + tb + bring + posten + nothanks;
					
					ResultSet specrs = c.createStatement().executeQuery(specialsql);
					int special = 0;
					if(specrs.next()) {
						special = specrs.getInt("count");
					}
					
					JOptionPane.showMessageDialog(null,
							   "Statistik enligt nedan:\n"
							   + "Totalt: " + sum + " prenumeranter.\n"
							   + "Varav\n"
							   + "         VTD     : " + vtd + " st\n"
							   + "         TB      : " + tb + " st\n"
							   + "         Bring   : " + bring + " st\n"
							   + "         Posten  : " + posten + " st (" + special + " special)\n"
							   + "och " + nothanks + " som inte vill ha tidningen alls.");
					
				} catch(SQLException sqle) {
					System.out.println("Database error: " + sqle.getMessage());
					sqle.printStackTrace();
				}	
		}
		});
		
		// Export to VTD/TB
		jf.addVTDListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	try {
		    		List<Subscriber> start = master.getStart(Distributor.VTD);
		    		List<Subscriber> stop = master.getStop(Distributor.VTD);
			    	
		    		if(start == null || stop == null) {
		    			JOptionPane.showMessageDialog(jf, "Could not find the previous issue to get start/stop list", "Previous issue missing", JOptionPane.ERROR_MESSAGE);
		    		} else {
		    			JOptionPane.showMessageDialog(jf, "Size of start: " + start.size() + "\nSize of stop: " + stop.size());
		    		}
		    	} catch(SQLException sqle) {
		    		System.out.println("Database error: " + sqle.getMessage());
					sqle.printStackTrace();
				}
			}
		});
		
		// TODO: Delivery date 
		jf.addTBListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
		    		List<Subscriber> start = master.getStart(Distributor.TB);
		    		List<Subscriber> stop = master.getStop(Distributor.TB);
			    	
		    		if(start == null || stop == null) {
		    			JOptionPane.showMessageDialog(jf, "Could not find the previous issue to get start/stop list", "Previous issue missing", JOptionPane.ERROR_MESSAGE);
		    		} else {
		    			JOptionPane.showMessageDialog(jf, "Size of start: " + start.size() + "\nSize of stop: " + stop.size());
		    		}
		    	} catch(SQLException sqle) {
		    		System.out.println("Database error: " + sqle.getMessage());
					sqle.printStackTrace();
				}
			}
		});
		
		jf.addKomplettListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					List<Subscriber> subs = master.getVTABByDistributor(Distributor.VTD);
					List<Subscriber> brings = master.getVTABByDistributor(Distributor.BRING);
					List<Subscriber> tbs = master.getVTABByDistributor(Distributor.TB);
					List<Subscriber> vips = master.getVTABVIP();
					subs.addAll(brings);
					subs.addAll(tbs);
					subs.addAll(vips);
					exportSubscribers(subs);
				} catch(SQLException sqle) {
					System.out.println("Database error: " + sqle.getMessage());
					sqle.printStackTrace();
				} catch(IOException ioe) {
					System.out.println("I/O error: " + ioe.getMessage());
					ioe.printStackTrace();
				}
			}
		});
		
		jf.addBringListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					List<Subscriber> subs = master.getVTABByDistributor(Distributor.BRING);
					exportSubscribers(subs);
				} catch(SQLException sqle) {
					System.out.println("Database error: " + sqle.getMessage());
					sqle.printStackTrace();
				} catch(IOException ioe) {
					System.out.println("I/O error: " + ioe.getMessage());
					ioe.printStackTrace();
				}
			}
		});
		
		jf.addBringSpecialListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					List<Subscriber> bringSubs = master.getVTABByDistributor(Distributor.BRING);
					List<Subscriber> vipSubs = master.getVTABVIP();
					bringSubs.addAll(vipSubs);
					exportSubscribers(bringSubs);
				} catch(SQLException sqle) {
					System.out.println("Database error: " + sqle.getMessage());
					sqle.printStackTrace();
				} catch(IOException ioe) {
					System.out.println("I/O error: " + ioe.getMessage());
					ioe.printStackTrace();
				}
			}
		});
		
		jf.addPostenListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					List<Subscriber> vipSubs = master.getVTABVIP();
					exportSubscribers(vipSubs);
				} catch(SQLException sqle) {
					System.out.println("Database error: " + sqle.getMessage());
				} catch(IOException ioe) {
					System.out.println("I/O error: " + ioe.getMessage());
				}
			}
		});
		
		jf.addRemoveListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = "Input SubscriberID.\nThis is either 10 digits (for LADOK entries)" 
							+ " or 5 digits for (VIP entries).";
				String val = JOptionPane.showInputDialog(jf, text);
				if(val != null && !val.equalsIgnoreCase("")) {
					try {
						long subid = Long.parseLong(val);
						Connection c = DBConnection.getConnection();
						Statement st = c.createStatement();
						String sql = "INSERT OR REPLACE INTO DistributedBy VALUES (" + subid + ", 'NONE')";
						int rows = st.executeUpdate(sql);
						JOptionPane.showMessageDialog(jf, "OK, updated " + rows + " entries", "Success", JOptionPane.INFORMATION_MESSAGE);
					} catch(SQLException sqle) {
						// TODO: Handle this
					} catch(NumberFormatException nfe) {
						JOptionPane.showMessageDialog(jf, "Only numerical SubscriberIDs are valid", "Invalid ID", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		jf.addVTDMissListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					// This is independent on issue
					/*
					try {
						if(!guitest) VTD_NoFind.registerFromList(year, issue);
						jf.enableVTab();
					} catch(IOException ioe) {
						ioe.printStackTrace();
					}
					*/
			}
		});
		
		jf.addVTDAddressListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = "Input SubscriberID.\nThis is either 10 digits (for LADOK entries)" 
							+ " or 5 digits for (VIP entries)";
				String val = JOptionPane.showInputDialog(jf, text);
				if(val != null && !val.equalsIgnoreCase("")) {
					try {
						long subid = Long.parseLong(val);
						Connection c = DBConnection.getConnection();
						Statement st = c.createStatement();
						String order = "CASE Distributor WHEN 'VTD' THEN 1 WHEN 'TB' THEN 2 "
									 + "WHEN 'BRING' THEN 3 WHEN 'POSTEN' THEN 4 WHEN 'NONE' THEN 5 END";
						
						String sql = "INSERT OR REPLACE INTO DistributedBy (SubscriberID, Distributor) "
								   + "SELECT SubscriberID, Distributor FROM Subscribers NATURAL JOIN Filter "
								   + "WHERE SubscriberID = '" + subid + "' AND Distributor IS NOT 'VTD' "
								   + "ORDER BY " + order + " LIMIT 1";
						
						int rows = st.executeUpdate(sql);
						if(rows == 0) { // Then we need to ship this with Posten
							String postsql = "INSERT OR REPLACE INTO DistributedBy(SubscriberID, Distributor) "
										   + "VALUES(" + subid + ", 'POSTEN')";
							Statement pst = c.createStatement();
							rows = pst.executeUpdate(postsql);
						}
						JOptionPane.showMessageDialog(jf, "OK, updated " + rows + " entries", "Success", JOptionPane.INFORMATION_MESSAGE);
					} catch(SQLException sqle) {
						// TODO: Handle this
					} catch(NumberFormatException nfe) {
						JOptionPane.showMessageDialog(jf, "Only numerical SubscriberIDs are valid", "Invalid ID", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
	}
	
	// TODO: Check for overwriting of file
	// See: http://stackoverflow.com/questions/13738625/jfilechooser-custom-file-name-create-new-file
	public void exportSubscribers(List<Subscriber> subs) throws IOException {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = jfc.showSaveDialog(jf);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	File file = jfc.getSelectedFile();
            if(!file.exists() && !file.createNewFile() ) {
            	JOptionPane.showMessageDialog(jf,"Can't create new file, try again");
            } else if(!file.canWrite()) {
            	JOptionPane.showMessageDialog(jf,"Unable to write to that file");
            } else {
                PrintWriter outFile = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                for(Subscriber s : subs) {
                	outFile.println(s.toString());
                }
                outFile.flush();
                outFile.close();
            	JOptionPane.showMessageDialog(jf,"Exported " + subs.size() + " subscribers to file successful!");
            }
        }
	}
}
