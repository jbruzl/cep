package cz.muni.fi.cep.api.services.users;

import java.util.List;

import cz.muni.fi.cep.api.DTO.CepGroup;
import cz.muni.fi.cep.api.DTO.CepUser;

/**
 * Interface to identity service, that manages user and group relationship.
 * 
 * @author Jan Bruzl
 *
 */
public interface IdentityService extends UserService, GroupService {

	/**
	 * Create membership of given user in given group.
	 * 
	 * @param cepUserEntity
	 *            {@link CepUser}
	 * @param cepGroupEntity
	 *            {@link CepGroup}
	 */
	public abstract void createMembership(CepUser cepUser, CepGroup cepGroup);

	/**
	 * Delete membership of given user in given group.
	 * 
	 * @param cepUserEntity
	 *            {@link CepUser}
	 * @param cepGroupEntity
	 *            {@link CepGroup}
	 */
	public abstract void deleteMembership(CepUser cepUser, CepGroup cepGroup);

	/**
	 * Sets authenticated user to given authenticatedUserId. Used in process
	 * services for setting process user.
	 * 
	 * @param authenticatedUserId
	 */
	public abstract void setAuthenticatedUserId(String authenticatedUserId);

	/**
	 * Returns list of groups which given user has memberships.
	 * 
	 * @param cepUser
	 *            {@link CepUser}
	 * @return List of {@link CepGroup}
	 */
	public abstract List<CepGroup> getMemberships(CepUser cepUser);

	/**
	 * Returns list of members of given group.
	 * 
	 * @param cepGroup
	 *            {@link CepGroup}
	 * @return List of {@link CepUser}
	 */
	public abstract List<CepUser> getMembers(CepGroup cepGroup);

}