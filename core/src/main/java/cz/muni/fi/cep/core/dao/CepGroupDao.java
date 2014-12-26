package cz.muni.fi.cep.core.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import cz.muni.fi.cep.core.entities.CepGroup;

public interface CepGroupDao extends PagingAndSortingRepository<CepGroup, Long> {

}
