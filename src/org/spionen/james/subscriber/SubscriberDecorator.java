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

import org.spionen.james.jamesfile.FieldType;

/**
 * Decorator class for Subscribers. Primary use is to implement various
 * distributor-defined formats for output.
 * 
 * @author Tobias Olausson
 *
 */
public class SubscriberDecorator extends Subscriber {

	private Subscriber sub;
	public SubscriberDecorator(Subscriber s) {
		sub = s;
	}
	
	public long getAbNr() {
		return sub.getAbNr();
	}
	public String getFirstName() {
		return sub.getFirstName();
	}
	public String getLastName() {
		return sub.getLastName();
	}
	public String getFullName() {
		return sub.getFullName();
	}
	public String getCoAddress() {
		return sub.getCoAddress();
	}
	public String getStreetAddress() {
		return sub.getStreetAddress();
	}
	public String getZipCode() {
		return sub.getZipCode();
	}
	public String getCity() {
		return sub.getCity();
	}
	public String getCountry() {
		return sub.getCountry();
	}
	public Distributor getDistributor() {
		return sub.getDistributor();
	}
	public String getByField(FieldType ft) {
		return sub.getByField(ft);
	}
	public String getType() {
		return sub.getType();
	}
	public String getNote() {
		return sub.getNote();
	}
	public boolean correctAdress() {
		return sub.correctAdress();
	}
	public boolean isOKforPaperRoute() {
		return sub.isOKforPaperRoute();
	}
	public boolean equals(Object p) {
		return sub.equals(p);
	}
	public int compareTo(Subscriber p) {
		return sub.compareTo(p);
	}
	public int hashCode() {
		return sub.hashCode();
	}
	public void setAbNr(long abNr) {
		sub.setAbNr(abNr);
	}
	public void setFirstName(String firstName) {
		sub.setFirstName(firstName);
	}
	public void setLastName(String lastName) {
		sub.setLastName(lastName);
	}
	public void setCoAddress(String coAddress) {
		sub.setCoAddress(coAddress);
	}
	public void setStreetAddress(String streetAddress) {
		sub.setStreetAddress(streetAddress);
	}
	public void setZipCode(String zipCode) {
		sub.setZipCode(zipCode);
	}
	public void setCity(String city) {
		sub.setCity(city);
	}
	public void setCountry(String country) {
		sub.setCountry(country);
	}
	public void setDistributor(Distributor distributor) {
		sub.setDistributor(distributor);
	}
	public void setDistributor(String distributor) {
		sub.setDistributor(distributor);
	}
	public void setByField(FieldType ft, String data) {
		sub.setByField(ft, data);
	}
	public void setType(String type) {
		sub.setType(type);
	}
	public void setNote(String note) {
		sub.setNote(note);
	}
	public String toString() {
		return sub.toString();
	}
	
	
}
