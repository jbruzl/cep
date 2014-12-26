/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import cz.muni.fi.bruzl.cep.subscriptions.api.ContactType;
import cz.muni.fi.bruzl.cep.subscriptions.entities.Publisher;
import cz.muni.fi.bruzl.cep.subscriptions.entities.Subscriber;
import cz.muni.fi.cep.core.entities.CepUser;

/**
 * @author Jan Bruzl
 *
 */
public interface SubscriberDao extends CrudRepository<Subscriber, Long> {
	/**
	 * Returns iterable of {@link Subscriber} with given {@link Publisher} and contact type.
	 * 
	 * @param publisher
	 * @param contactType
	 * @return Iterable<{@link Subscriber}>
	 */
	public Iterable<Subscriber> findAllByPublisherAndContactType(
			Publisher publisher, ContactType contactType);

	/**
	 * Returns iterable of {@link Subscriber} with given contact.
	 * 
	 * @param contact
	 * @return Iterable<{@link Subscriber}>
	 */
	@Query("select u from #{#entityName} u where u.contact = :contact")
	public Iterable<Subscriber> findAllByContact(@Param("contact") String contact);
	
	/**
	 * Returns iterable of {@link Subscriber} with given contact and {@link Publisher}.
	 * 
	 * @param contact
	 * @param publisher
	 * @return Iterable<{@link Subscriber}>
	 */
	@Query("select u from #{#entityName} u where u.contact = :contact and u.publisher = :publisher")
	public Iterable<Subscriber> findAllByContactAndPublisher(@Param("contact")String contact, @Param("publisher")Publisher publisher);
	
	/**
	 * Returns iterable of {@link Subscriber} with given {@link CepUser}.
	 * 
	 * @param user
	 * @return Iterable<{@link Subscriber}>
	 */
	@Query("select u from #{#entityName} u where u.user = :user")
	public Iterable<Subscriber> findAllByUser(@Param("user") CepUser user);
	
	/**
	 * Returns iterable of {@link Subscriber} with given {@link CepUser} and {@link Publisher}.
	 * 
	 * @param user
	 * @param publisher
	 * @return Iterable<{@link Subscriber}>
	 */
	@Query("select u from #{#entityName} u where u.user = :user and u.publisher = :publisher")
	public Iterable<Subscriber> findAllByUserAndPublisher(@Param("user") CepUser user, @Param("publisher")Publisher publisher);
}
