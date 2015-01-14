package org.spionen.james;

import org.spionen.james.subscriber.Subscriber;

/**
 * Interface to ease customization of printing Subscribers. Sometimes, there
 * is more information needed than that held by the Subscriber itself, such
 * as the distribution date. By implementing this interface, such info
 * can be incorporated.  
 * 
 * @author Tobias Olausson
 *
 */
public interface SubscriberPrinter {
	public String print(Subscriber s);
}
