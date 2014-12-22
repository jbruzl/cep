/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Abstract super type for subscription to {@link Publisher}.
 * 
 * @author Jan Bruzl
 */
@Entity
public abstract class Subscriber {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private ContactType contactType;

	/**
	 * 
	 */
	public Subscriber() {
		super();
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * 
	 * @return subscribed contact
	 * @throws Exception Unknown contact type
	 */
	abstract String getContact() throws Exception;

	/**
	 * 
	 * @return type of subscribed contact
	 */
	ContactType getContactType() {
		return contactType;
	}

	public void setContactType(ContactType contactType) {
		this.contactType = contactType;
	}

}
