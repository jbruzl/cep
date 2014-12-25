/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions.dao;

import org.springframework.data.repository.CrudRepository;

import cz.muni.fi.bruzl.cep.subscriptions.entities.Publisher;

/**
 * @author Jan Bruzl
 *
 */
public interface PublisherDao extends CrudRepository<Publisher, Long> {
	public Publisher findByCode(String code); 
}
