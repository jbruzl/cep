/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions.entities;

import javax.persistence.Entity;

import cz.muni.fi.cep.core.entities.CepUser;

/**
 * Adapter to {@link CepUser} for contact.
 * 
 * @author Jan Bruzl
 */
@Entity
public class UserSubscriber extends Subscriber {
	//TODO add annotations for relation
	private CepUser user;

	public UserSubscriber() {
		super();
	}

	public UserSubscriber(CepUser user) {
		super();
		this.user = user;
	}

	/**
	 * Returns subscribed {@link CepUser}.
	 * 
	 * @return
	 */
	public CepUser getUser() {
		return user;
	}

	/**
	 * Set subscribed {@link CepUser}
	 * 
	 * @param {@link CepUser}
	 */
	public void setUser(CepUser user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "UserSubscriber [user=" + user + ", getId()=" + getId()
				+ ", getContactType()=" + getContactType() + "]";
	}

	@Override
	public String getContact() throws Exception {
		String contact;
		switch (getContactType()) {
		case EMAIL:
			contact = user.getEmail();
			break;
		case PHONE:
		case SMS:
			contact = user.getPhoneNumber();
			break;
		default:
			throw new Exception("Unknown contact type: " + getContactType());
		}
		return contact;
	}
}
