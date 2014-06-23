package org.spionen.james.subscriber;

import org.spionen.james.FieldType;

public class Subscriber implements Comparable<Subscriber> {
    
    //TODO Make abNr to Long.
    
    private String abNr;
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
    
    public Subscriber() {
    	abNr = firstName = lastName = "";
    	coAddress = streetAddress = zipCode = "";
    	city = country = type = note = "";
    	distributor = null;
    }

    public String getAbNr() {
		return abNr;
	}

	public void setAbNr(String abNr) {
		this.abNr = abNr;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getFullName() {
		return firstName + " " + lastName;
	}

	public String getCoAddress() {
		return coAddress;
	}

	public void setCoAddress(String coAddress) {
		this.coAddress = coAddress;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
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
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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
		case SubscriberID: return getAbNr();
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
		case SubscriberID: setAbNr(data); break;
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
		this.note = note;
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

        if (abNr.equals("000")) {
            note = "Ej VTD/TB adress";
            return false;
        } 
        return true;
    }

    public boolean comparePrenumerant(Subscriber p) {
    	if (abNr.equals(p.getAbNr())) {
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
        return this.getAbNr().compareTo(p.getAbNr());
    }

    public enum Distributor {
    	VTD,TB,Bring,Posten,Duplicate,Invalid,NoThanks;
    }
    
    public enum Type {
    	Student, PhDStudent, Politician, Other
    }
}
