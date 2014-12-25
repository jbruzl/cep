/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cz.muni.fi.bruzl.cep.subscriptions.entities.Publisher;
import cz.muni.fi.bruzl.cep.subscriptions.entities.Subscriber;

/**
 * @author Jan Bruzl
 *
 */
public interface PublisherDao extends CrudRepository<Publisher, Long> {
	/**
	 * TODO
	 * @param code
	 * @return
	 */
	public Publisher findByCode(String code); 
	

}
