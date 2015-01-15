/**
*   Copyright 2010-2015 Maxim Fris, Tobias Olausson
*
*   This file is part of James.
*
*   James is free software: you can redistribute it and/or modify
*   it under the terms of the GNU General Public License as published by
*   the Free Software Foundation, either version 3 of the License, or
*   (at your option) any later version.
*
*   James is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.
*
*   You should have received a copy of the GNU General Public License
*   along with James. If not, see <http://www.gnu.org/licenses/>.
*/
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
