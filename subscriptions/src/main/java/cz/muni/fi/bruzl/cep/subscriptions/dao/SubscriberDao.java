/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions.dao;

import org.springframework.data.repository.CrudRepository;

import cz.muni.fi.bruzl.cep.subscriptions.entities.Subscriber;

/**
 * @author Jan Bruzl
 *
 */
public interface SubscriberDao extends CrudRepository<Subscriber, Long> {

}
