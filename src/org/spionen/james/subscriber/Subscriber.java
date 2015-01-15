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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.spionen.james.jamesfile.FieldType;

public class Subscriber implements Comparable<Subscriber> {
    
    private long abNr;
    private String firstName;
    private String lastName;
    private String coAddress;
    private String streetAddress;
    private String zipCode;
    private String city;
    private String country;
    private Distributor distributor;
    private String type;
    private String note;
    
    public static Subscriber getFromDB(ResultSet rs) throws SQLException {
    	Subscriber s = new Subscriber();
		s.setAbNr(rs.getLong("SubscriberID"));
		s.setFirstName(rs.getString("FirstName"));
		s.setLastName(rs.getString("LastName"));
		s.setCoAddress(rs.getString("CoAddress"));
		s.setStreetAddress(rs.getString("Address"));
		s.setZipCode(rs.getString("ZipCode"));
		s.setCity(rs.getString("City"));
		s.setCountry(rs.getString("Country"));
		s.setNote(rs.getString("Note"));
		return s;
    }
    
    public Subscriber() {
    	abNr = 0; 
    	firstName = lastName = "";
    	coAddress = streetAddress = zipCode = "";
    	city = country = type = note = "";
    	distributor = null;
    }
    
    private String cleanString(String data) {
    	if(data != null) {
    		return data.replaceAll("\n|\r", "");
    	} else {
    		return null;
    	}
    }

    public long getAbNr() {
		return abNr;
	}

	public void setAbNr(long abNr) {
		this.abNr = abNr;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = cleanString(firstName);
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = cleanString(lastName);
	}
	
	public String getFullName() {
		return firstName + " " + lastName;
	}

	public String getCoAddress() {
		return coAddress;
	}

	public void setCoAddress(String coAddress) {
		this.coAddress = cleanString(coAddress);
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = cleanString(streetAddress);
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		if(zipCode != null) {
			this.zipCode = zipCode.replaceAll(" ", "");
		}
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = cleanString(city);
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = cleanString(country);
	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}
	
	public void setDistributor(String distributor) {
		if(distributor == null) {
			this.distributor = Distributor.valueOf(distributor);
		}
	}
	
	public String getByField(FieldType ft) {
		switch(ft) {
		case SubscriberID: return String.valueOf(getAbNr());
		case FirstName: return getFirstName();
		case LastName: return getLastName();
		case CoAddress: return getCoAddress();
		case Address: return getStreetAddress();
		case ZipCode: return getZipCode();
		case City: return getCity();
		case Country: return getCountry();
		case Category: return getType();
		case Distributor: 
			if(getDistributor() != null) return getDistributor().toString();
			return null;
		case Note: return getNote();
		default: return null;
		}
	}
	
	public void setByField(FieldType ft, String data) {
		switch(ft) {
		case SubscriberID: 
			try{
				long val = Long.parseLong(cleanID(data));
				setAbNr(val);
			} catch(NumberFormatException e) {}
			break;
		case FirstName: setFirstName(data); break;
		case LastName: setLastName(data); break;
		case CoAddress: setCoAddress(data); break;
		case Address: setStreetAddress(data); break;
		case ZipCode: setZipCode(data); break;
		case City: setCity(data); break;
		case Country: setCountry(data);
		case Category: setType(data); break;
		case Distributor: setDistributor(data); break;
		case Note: setNote(data); break;
		default: return; // Discard anything else
		}		
	}
	
	private static String cleanID(String data) {
		data = data.replaceAll("-", "");
		data = data.replaceAll("T", "0");
		data = data.replaceAll("P", "0");
		data = data.replaceAll("R", "0");
		return data;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = cleanString(note);
	}

	/**
	 * Previously known as masterFormat()
	 */
	public String toString() {
		return abNr + ";" + 
				firstName + ";" +
				lastName + ";" +
				coAddress + ";" +
				streetAddress + ";" +
				zipCode + ";" +
				city + ";" +
				country  + ";" +
				distributor + ";" +
				type  + ";" +
				note;
    }

    public boolean correctAdress() {
    	if(zipCode != null) {
	        if (zipCode.equals("0") || zipCode.startsWith("0")) {
	        	return false;
	        } else if (zipCode.equals("40530") && streetAddress.length() == 0) {
	            return false;
	        } else if (zipCode.length() > 6 || zipCode.length() < 5) {
	            note = "Felaktigt Postnummer";
	            return false;
	        } else if (country == null || country.equals("")) {
                return true;
            } else if (country.equals("SVERIGE") || country.equals("SWEDEN")) {
                return true;
            } else if (streetAddress.length() == 0) {
            	note = "Adress saknas";
            	return false;
            } else {
                note = "Icke Svensk adress";
                return false;
            }
        } else {
        	note = "Postnummer saknas";
        	return false;
        }
    }
    
    public boolean isOKforPaperRoute() {
    
        if (streetAddress.startsWith("BOX ") || streetAddress.startsWith("PL ")) {
            note = "Ej VTD/TB adress";
            return false;  
        } 
        
        if (streetAddress.length() > 35) {
            note = "För lång gatuadress för VTD/TB";
            return false;
        }

        if (streetAddress.length() < 3) {
            note = "För kort adress för VTD/TB";
            return false;
        }

        if (coAddress.length() > 30) {
            note = "För lång COAdress för VTD/TB";
            return false;
        }

        if ((firstName.length() + lastName.length()) > 30) {
            note = "För långt namn för VTD/TB";
            return false;
        }

        if (abNr == 0) {
            note = "Ej VTD/TB adress";
            return false;
        } 
        return true;
    }

    @Override
    public boolean equals(Object o) {
    	Subscriber p = (Subscriber)o;
    	if (abNr == p.getAbNr()) {
    		if (zipCode.equals(p.getZipCode())) {
    			if (streetAddress.equals(p.getStreetAddress())) {
    				return true;
    			}
    		}
    	}
    	return false;
    }

    @Override
    public int compareTo(Subscriber p) {
    	long a = this.getAbNr();
    	long b = p.getAbNr();
    	return a > b ? 1 : a < b ? -1 : 0;
    }

    public enum Distributor {
    	VTD,TB,BRING,POSTEN,INVALID,NONE;
    }
    
    public enum Type {
    	Student, PhDStudent, Staff, Politician, Other
    }
}
