package org.spionen.james;

import java.io.IOException;
import javax.swing.JOptionPane;

public class Main {

	public static void main(String[] args) {
		try {
			Settings s = Settings.loadOrCreate();
			if(s != null) {
				new James(s);
			} else {
				JOptionPane.showMessageDialog(null, "James needs settings to operate, exiting");
				System.exit(0); // Normal exit
			}
		} catch(IOException ioe) {
			JOptionPane.showMessageDialog(null, "Could not initialize: \n\n" + ioe.getMessage());
			System.exit(-1); // Error
		}
	}
}
