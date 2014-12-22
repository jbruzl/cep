package cz.muni.fi.bruzl.cep.subscriptions.entities;

import javax.persistence.Entity;

/**
 * Storage entity for subscription to {@link Publisher} without 
 * having system account.
 *  
 * @author Jan Bruzl
 */
@Entity
public class OrphanSubscriber extends Subscriber {

	String contact;
	
	public OrphanSubscriber() {
		super();
	}

	public OrphanSubscriber(String contact) {
		super();
		this.contact = contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}


	@Override
	public String toString() {
		return "OrphanSubscriber [contact=" + contact + ", getId()=" + getId()
				+ ", getContactType()=" + getContactType() + "]";
	}

	@Override
	public String getContact() {
		return contact;
	}


	
	
}
