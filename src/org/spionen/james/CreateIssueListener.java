package org.spionen.james;

import java.awt.event.ActionListener;

/**
 * Interface used to be able to have cyclic dependencies
 * between two ActionListeners. Kind of an ugly hack. 
 * 
 * @author Tobias Olausson
 *
 */
public interface CreateIssueListener extends ActionListener {
	public void setListener(ActionListener getListener);
}
