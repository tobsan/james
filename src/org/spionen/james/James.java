package org.spionen.james;

import javax.swing.JFrame;

// TODO: Gör ett flow över states för ett nummer. Nån slags state-klass så att man inte kan göra fel!

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
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
	}
}
