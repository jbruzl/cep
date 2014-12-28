package cz.muni.fi.cep.core.users.dao;

import org.springframework.data.repository.CrudRepository;

import cz.muni.fi.cep.core.users.entities.CepGroupEntity;

/**
 * Dao for {@link CepGroupEntity}. Provides basic CRUD methods.
 * 
 * @author Jan Bruzl
 */
public interface CepGroupDao extends CrudRepository<CepGroupEntity, Long> {

}
