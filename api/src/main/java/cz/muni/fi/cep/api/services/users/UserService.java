package cz.muni.fi.cep.api.services.users;

import java.util.List;

import cz.muni.fi.cep.api.DTO.CepUser;

/**
 * Interface for user service, that manages all users.
 * 
 * @author Jan Bruzl
 *
 */
public interface UserService {

	/**
	 * Create user.
	 * 
	 * @param user
	 *            {@link CepUser}
	 */
	public abstract void createUser(CepUser user);

	/**
	 * Deletes given user.
	 * 
	 * @param user
	 *            {@link CepUser}
	 */
	public abstract void deleteUser(CepUser user);

	/**
	 * Updates given user.
	 * 
	 * @param user
	 *            {@link CepUser}
	 */
	public abstract void updateUser(CepUser user);

	/**
	 * Returns {@link CepUser} with given id
	 * @param id
	 * @return {@link CepUser}
	 */
	public abstract CepUser getCepUserById(Long id);

	/**
	 * Returns all {@link CepUser}
	 * @return {@link List<CepUser>}
	 */
	public abstract List<CepUser> getAllCepUsers();

	/**
	 * Returns {@link CepUser} with given email
	 * @param email
	 * @return {@link CepUser}
	 */
	public abstract CepUser getCepUserByEmail(String email);

}