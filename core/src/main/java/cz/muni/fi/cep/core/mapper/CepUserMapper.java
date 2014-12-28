package cz.muni.fi.cep.core.mapper;

import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.UserEntity;

import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;

/**
 * Custom DTO mapper for {@link CepUser}
 *  
 * @author Jan Bruzl
 */
public class CepUserMapper {
	private CepUser cepUser;
	private User userEntity;
	private CepUserEntity cepUserEntity;

	

	public CepUserMapper(CepUser cepUser) {
		this.cepUser = cepUser;
		map();
	}

	public CepUserMapper(User activitiUser, CepUserEntity cepUserEntity) {
		this.userEntity = activitiUser;
		this.cepUserEntity = cepUserEntity;
		map();
	}

	/**
	 * @return the cepUser
	 */
	public CepUser getCepUser() {
		return cepUser;
	}


	/**
	 * @return the cepUserEntity
	 */
	public CepUserEntity getCepUserEntity() {
		return cepUserEntity;
	}

	public CepUserMapper map() {
		// map:  cepUserEntity ---> cepUser
		if (cepUser == null &&  cepUserEntity != null) {
			cepUser = new CepUser();
			cepUser.setId(Long.parseLong(cepUserEntity.getId()));
			cepUser.setFirstName(cepUserEntity.getFirstName());
			cepUser.setLastName(cepUserEntity.getLastName());
			cepUser.setMail(cepUserEntity.getEmail());
			cepUser.setPassword(cepUserEntity.getPassword());
			cepUser.setPhoneNumber(cepUserEntity.getPhoneNumber());
		}

		// map:  cepUserEntity <--- cepUser
		if (cepUser != null &&  cepUserEntity == null) {
			cepUserEntity = new CepUserEntity();
			cepUserEntity.setId(Long.toString(cepUser.getId()));
			cepUserEntity.setPhoneNumber(cepUser.getPhoneNumber());
			cepUserEntity.setEmail(cepUser.getMail());
			cepUserEntity.setFirstName(cepUser.getFirstName());
			cepUserEntity.setId(Long.toString(cepUser.getId()));
			cepUserEntity.setLastName(cepUser.getLastName());
			cepUserEntity.setPassword(cepUser.getPassword());
		}

		return this;
	}

	/**
	 * @return the userEntity
	 */
	public User getUserEntity() {
		return userEntity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CepUserMapper [cepUser=" + cepUser + ", userEntity="
				+ userEntity + ", cepUserEntity=" + cepUserEntity + "]";
	}
	
	

}
