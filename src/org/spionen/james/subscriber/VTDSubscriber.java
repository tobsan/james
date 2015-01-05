package org.spionen.james.subscriber;

/**
 * Captures any behaviour specific to VTD for a subscriber
 * @author Tobias Olausson
 */
public class VTDSubscriber extends SubscriberDecorator {

	public VTDSubscriber(Subscriber s) {
		super(s);
		setDistributor(Distributor.VTD);
	}
	
    public String toString() {
        String name = (getLastName() + " " + getFirstName()).trim();
        String coAddr = getCoAddress();
        if(coAddr == null || coAddr.equalsIgnoreCase("null")) {
        	coAddr = "";
        }
        return getAbNr() + ";" + name + ";" + coAddr + ";" + 
        	   getStreetAddress() + ";" + getZipCode() + ";" + getCity();
    }
}
