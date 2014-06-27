package org.spionen.james;

import java.io.IOException;
import javax.swing.JOptionPane;

public class Main {

	public static void main(String[] args) {
		try {
			Settings s = new Settings();
			new James(s);
		} catch(IOException ioe) {
			JOptionPane.showMessageDialog(null, "Could not initialize: \n\n" + ioe.getMessage());
			System.exit(-1); // Error
		}
	}
}
