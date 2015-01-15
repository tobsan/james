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
package org.spionen.james.subscriber;

public class VTabSubscriber extends SubscriberDecorator {

	public VTabSubscriber(Subscriber s) {
		super(s);
	}
	
	/**
	 * @override
	 */
    public String toString() {
        String name = (getLastName() + " " + getFirstName()).trim();
        String coAddr = getCoAddress() == null ? "" : getCoAddress();
        return getAbNr() + ";" + name + ";" + coAddr + ";" 
         	 + getStreetAddress() + ";" + getZipCode() + ";" + getCity();
    }
}
