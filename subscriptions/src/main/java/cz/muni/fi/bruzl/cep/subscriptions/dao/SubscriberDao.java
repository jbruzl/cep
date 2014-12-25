/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import cz.muni.fi.bruzl.cep.subscriptions.entities.ContactType;
import cz.muni.fi.bruzl.cep.subscriptions.entities.Publisher;
import cz.muni.fi.bruzl.cep.subscriptions.entities.Subscriber;
import cz.muni.fi.cep.core.entities.CepUser;

/**
 * @author Jan Bruzl
 *
 */
public interface SubscriberDao extends CrudRepository<Subscriber, Long> {
	/**
	 * TODO
	 * 
	 * @param publisher
	 * @return
	 */
	public Iterable<Subscriber> findAllByPublisherAndContactType(
			Publisher publisher, ContactType contactType);

	@Query("select u from #{#entityName} u where u.contact = :contact")
	public Iterable<Subscriber> findAllByContact(@Param("contact") String contact);
	
	@Query("select u from #{#entityName} u where u.contact = :contact and u.publisher = :publisher")
	public Iterable<Subscriber> findAllByContactAndPublisher(@Param("contact")String contact, @Param("publisher")Publisher publisher);
	
	@Query("select u from #{#entityName} u where u.user = :user")
	public Iterable<Subscriber> findAllByUser(@Param("user") CepUser user);
	
	@Query("select u from #{#entityName} u where u.user = :user")
	public Iterable<Subscriber> findAllByUserAndPublisher(@Param("user") CepUser user, @Param("publisher")Publisher publisher);
}
