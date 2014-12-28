package cz.muni.fi.cep.core.users.dao;

import org.springframework.data.repository.CrudRepository;

import cz.muni.fi.cep.core.users.entities.CepUserEntity;

/**
 * 
 * @author Jan Bruzl
 */
public interface CepUserDao extends CrudRepository<CepUserEntity, Long> {

}
