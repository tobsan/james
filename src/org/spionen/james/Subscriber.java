package org.spionen.james;

import java.util.*;

public class Subscriber implements Comparable<Subscriber>
{
    
    //TODO Make abNr to Long.
    
    private String abNr;
    private String firstName;
    private String lastName;
    private String coAddress;
    private String streetAddress;
    private String zipCode;
    private String city;
    private String country;
    private String distributor;
    private String type;
    private String note;

    // Do not use
    public Subscriber(String masterFileRow) {

        Scanner s = new Scanner(masterFileRow);
        Scanner sc = s.useDelimiter(";");

        abNr            = sc.next();
        firstName           = sc.next().trim();
        lastName           = sc.next().trim();
        coAddress        = sc.next().trim();
        streetAddress      = sc.next().trim();
        zipCode          = sc.next().trim();
        city         = sc.next().trim();
        country            = sc.next().trim();
        distributor     = sc.next().trim();
        type             = sc.next();
        note            = sc.next();
        
        sc.close();
        s.close();
    }
    
    public Subscriber() {
    	// Empty
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
		this.zipCode = zipCode;
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

	public String getDistributor() {
		return distributor;
	}

	public void setDistributor(String distributor) {
		this.distributor = distributor;
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

	public String masterFormat() {
        String row = abNr + ";" + 
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
        return row;
    }

    public String vtdFormat() {
        String namn = lastName + " " + firstName;
        namn = namn.trim();

        String row = abNr + ";" + namn + ";" + coAddress + ";" + streetAddress + ";" + zipCode + ";" + city;
        return row;
    }

    public String vTabFormat() {
        String namn = firstName + " " + lastName;
        namn = namn.trim();

        String row = namn + ";" + coAddress + ";" + streetAddress + ";" + zipCode + ";" + city;
        return row;
    }

    public boolean correctAdress() {

        if (zipCode.equals("0")) {return false;}
        if (zipCode.startsWith("0")) {return false;}
        if (zipCode.equals("40530")) {
            if (streetAddress.length() == 0) {
                return false;
            }
        }
        if (zipCode.length() != 5) {
            note = "Felaktigt Postnummer";
            return false;
        } else {
            if (country.equals("")) {
                    return true;
            } else {
                if (country.equals("SVERIGE") || country.equals("SWEDEN")) {
                    return true;
                }
                else {
                    note = "Icke Svensk adress";
                    return false;
                }
            }
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

    public String tbFormat()
    {
        //int abb = Integer.parseInt(abNr);
        int pn = Integer.parseInt(zipCode);
        //Double prenNr = Double.parseDouble(abNr);
        Long prenNr = Long.parseLong(abNr);
        
        //System.out.println(prenNr);
        
        String out1 = String.format("%7s%010d%05d%-20s%09d", "L ",prenNr,pn,city,0);
        String out2 = ListHelpers.toTB(streetAddress);
        String out3 = String.format("%1s%14s%-30s%-30s%-60s%06d", "","", lastName + " " + firstName, coAddress, "", 1);
        
        //System.err.print(out1);
        //System.err.print(ListHelpers.toTB(gatuAdress));
        //System.err.print(out3 + "\n");
        
        String line = out1 + out2 + out3;
        
        return line;
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

}
