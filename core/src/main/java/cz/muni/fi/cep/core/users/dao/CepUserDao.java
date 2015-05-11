package cz.muni.fi.cep.core.users.dao;

import org.springframework.data.repository.CrudRepository;

import cz.muni.fi.cep.core.users.entities.CepUserEntity;

/**
 * Dao for {@link CepUserEntity}. Provides basic CRUD methods.
 * 
 * @author Jan Bruzl
 */
public interface CepUserDao extends CrudRepository<CepUserEntity, Long> {
	
	public CepUserEntity findByEmail(String email);
}
