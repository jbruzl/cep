package cz.muni.fi.cep.core.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import cz.muni.fi.cep.core.entities.CepUser;

/**
 * 
 * @author Jan Bruzl
 */
public interface CepUserDao extends PagingAndSortingRepository<CepUser, Long> {

}
