package cz.muni.fi.cep.core.users.api;

import cz.muni.fi.cep.core.users.dao.CepGroupDao;
import cz.muni.fi.cep.core.users.dao.CepUserDao;
import cz.muni.fi.cep.core.users.entities.CepGroupEntity;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;

public interface IdentityService extends UserService, GroupService{

	/**
	 * Create membership of given user in given group.
	 * 
	 * @param cepUserEntity {@link CepUserEntity}
	 * @param cepGroupEntity {@link CepGroupEntity}
	 */
	public abstract void createMembership(CepUserEntity cepUserEntity,
			CepGroupEntity cepGroupEntity);

	/**
	 * Delete membership of given user in given group.
	 * 
	 * @param cepUserEntity {@link CepUserEntity}
	 * @param cepGroupEntity {@link CepGroupEntity}
	 */
	public abstract void deleteMembership(CepUserEntity cepUserEntity,
			CepGroupEntity cepGroupEntity);

	public abstract void setCepGroupDao(CepGroupDao cepGroupDao);

	public abstract void setCepUserDao(CepUserDao cepUserDao);

	public abstract void setAuthenticatedUserId(String authenticatedUserId);

}