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
