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
        return getAbNr() + ";" + name + ";" + getCoAddress() + ";" 
         	 + getStreetAddress() + ";" + getZipCode() + ";" + getCity();
    }
}
