package cz.muni.fi.cep.core.users.api;

import java.util.List;

import cz.muni.fi.cep.core.users.entities.CepUserEntity;

public interface UserService {

	/**
	 * Create user.
	 * 
	 * @param user
	 *            {@link CepUserEntity}
	 */
	public abstract void createUser(CepUserEntity user);

	/**
	 * Deletes given user.
	 * 
	 * @param user
	 *            {@link CepUserEntity}
	 */
	public abstract void deleteUser(CepUserEntity user);

	/**
	 * Updates given user.
	 * 
	 * @param user
	 *            {@link CepUserEntity}
	 */
	public abstract void updateUser(CepUserEntity user);

	/**
	 * Returns {@link CepUserEntity} with given id
	 * @param id
	 * @return {@link CepUserEntity}
	 */
	public abstract CepUserEntity getCepUserById(Long id);

	/**
	 * 
	 * @return {@link List<CepUserEntity>}
	 */
	public abstract List<CepUserEntity> getAllCepUsers();

}