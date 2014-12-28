/**
 * 
 */
package cz.muni.fi.cep.core.subscriptions.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import cz.muni.fi.cep.api.DTO.ContactType;

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
	
	@ManyToOne
	private Publisher publisher;

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
	public abstract String getContact() throws Exception;

	/**
	 * 
	 * @return type of subscribed contact
	 */
	public ContactType getContactType() {
		return contactType;
	}

	public void setContactType(ContactType contactType) {
		this.contactType = contactType;
	}

	/**
	 * @return the publisher
	 */
	public Publisher getPublisher() {
		return publisher;
	}

	/**
	 * @param publisher the publisher to set
	 */
	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}


	
	

}
