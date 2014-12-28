/**
 * 
 */
package cz.muni.fi.cep.core.subscriptions.dao;

import org.springframework.data.repository.CrudRepository;

import cz.muni.fi.cep.core.subscriptions.entities.Publisher;


/**
 * @author Jan Bruzl
 *
 */
public interface PublisherDao extends CrudRepository<Publisher, Long> {
	/**
	 * Returns {@link Publisher} with given code.
	 * @param code
	 * @return {@link Publisher}
	 */
	public Publisher findByCode(String code); 
}
