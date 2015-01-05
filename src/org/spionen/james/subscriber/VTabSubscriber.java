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
