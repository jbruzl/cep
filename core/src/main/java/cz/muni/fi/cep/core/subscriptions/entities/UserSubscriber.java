/**
 * 
 */
package cz.muni.fi.cep.core.subscriptions.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import cz.muni.fi.cep.core.users.entities.CepUserEntity;

/**
 * UserSubscriber entity
 * 
 * @author Jan Bruzl
 */
@Entity
public class UserSubscriber extends Subscriber {
	@ManyToOne
	private CepUserEntity cepUserEntity;

	public UserSubscriber() {
		super();
	}

	public UserSubscriber(CepUserEntity cepUserEntity) {
		super();
		this.cepUserEntity = cepUserEntity;
	}
	
	

	public CepUserEntity getCepUserEntity() {
		return cepUserEntity;
	}

	@Override
	public String getContact() throws Exception {
		String contact;

		switch (getContactType()) {
		case EMAIL:
			contact = cepUserEntity.getEmail();
			break;
		case PHONE:
			contact = cepUserEntity.getPhoneNumber();
			break;
		case SMS:
			contact = cepUserEntity.getPhoneNumber();
			break;
		default:
			throw new RuntimeException("UserSubscriber: Unknown contact type "
					+ getContactType());
		}

		return contact;
	}
}
