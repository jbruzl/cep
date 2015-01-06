package cz.muni.fi.cep.core.subscriptions.api;

import java.util.List;

import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.core.subscriptions.dao.PublisherDao;
import cz.muni.fi.cep.core.subscriptions.dao.SubscriberDao;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;

public interface SubscriptionService {

	/**
	 * @param publisherDao
	 *            the publisherDao to set
	 */
	public abstract void setPublisherDao(PublisherDao publisherDao);

	/**
	 * @param subscriberDao
	 *            the subscriberDao to set
	 */
	public abstract void setSubscriberDao(SubscriberDao subscriberDao);

	/**
	 * Register event by code. If code is registered, no thing happens.
	 * 
	 * @param code
	 *            of event
	 */
	public void register(String code);

	/**
	 * Unregisters event. If event is not present, nothings happens. Warning:
	 * with unregistering event, all subscriptions will be lost! Unregister only
	 * when undeploying process!
	 * 
	 * @param code
	 *            of event
	 */
	public void unregister(String code);

	/**
	 * Subscribe @{link CepUserEntity} to event defined by code with defined contact
	 * type.
	 * 
	 * @param user
	 *            subscribing to event
	 * @param code
	 *            of event
	 * @param contactType
	 */
	public void subscribeUser(CepUserEntity user, String code, ContactType contactType);

	/**
	 * Subscribe contact to event defined by code with defined contact type.
	 * Used for external notifications.
	 * 
	 * @param contact
	 *            to be notified
	 * @param code
	 *            of event
	 * @param contactType
	 */
	public void subscribe(String contact, String code, ContactType contactType);

	/**
	 * Unsubscribe @{link CepUserEntity} from event defined by code with defined
	 * contact type.
	 * 
	 * @param user
	 *            to be unsubscribed
	 * @param code
	 *            of event, if null user is unsubscribed from all events
	 * @param contactType
	 *            , if null user is unsubscribed from event with all contacts
	 */
	public void unsubscribeUser(CepUserEntity user, String code,
			ContactType contactType);

	/**
	 * Unsubscribe contact from event defined by code with defined contact type.
	 * 
	 * @param contact
	 *            to be unsubscribed
	 * @param code
	 *            of event, if null user is unsubscribed from all events
	 * @param contactType
	 *            , if null user is unsubscribed from event with all contacts
	 */
	public void unsubscribe(String contact, String code, ContactType contactType);

	/**
	 * Returns list of event codes subscribed by given {@link CepUserEntity} and type.
	 * 
	 * @param user
	 *            subscribed by
	 * @param contactType
	 *            , if null all types are returned
	 * @return list of event codes
	 */
	public List<String> getUserSubscriptions(CepUserEntity user,
			ContactType contactType);

	/**
	 * Returns list of event codes subscribed by given contact and type.
	 * 
	 * @param contact
	 *            subscribed by
	 * @param contactType
	 *            , if null all types are returned
	 * @return list of event codes
	 */
	public List<String> getSubscriptions(String contact, ContactType contactType);
	
	/**
	 * Return list of event codes.
	 * @return list of event codes
	 */
	public List<String> getAllPublishers();

	/**
	 * Returns list of external contacts subscribed to event with contact type.
	 * 
	 * @param code
	 *            of event
	 * @param contactType
	 * @return list of contacts
	 */
	public List<String> getSubscribers(String code, ContactType contactType);

	/**
	 * Returns list of {@link CepUser} subscribed to event with contact type.
	 * 
	 * @param code
	 *            of event
	 * @param contactType
	 * @return list of {@link CepUser}
	 */
	public List<CepUserEntity> getUserSubscribers(String code, ContactType contactType);

	/**
	 * Returns list of all contacts for given event code and contact type.
	 * 
	 * @param code
	 *            of event, not null
	 * @param contactType
	 *            , not null
	 * @return list of contact
	 */
	public List<String> getContacts(String code, ContactType contactType);

}