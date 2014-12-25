package cz.muni.fi.bruzl.cep.subscriptions.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.muni.fi.bruzl.cep.subscriptions.api.SubscriptionService;
import cz.muni.fi.bruzl.cep.subscriptions.dao.PublisherDao;
import cz.muni.fi.bruzl.cep.subscriptions.dao.SubscriberDao;
import cz.muni.fi.bruzl.cep.subscriptions.entities.ContactType;
import cz.muni.fi.bruzl.cep.subscriptions.entities.OrphanSubscriber;
import cz.muni.fi.bruzl.cep.subscriptions.entities.Publisher;
import cz.muni.fi.bruzl.cep.subscriptions.entities.Subscriber;
import cz.muni.fi.bruzl.cep.subscriptions.entities.UserSubscriber;
import cz.muni.fi.cep.core.entities.CepUser;

@Service
@Transactional
public class DBSubscriptionService implements SubscriptionService {
	@Autowired
	private PublisherDao publisherDao;

	@Autowired
	private SubscriberDao subscriberDao;

	/**
	 * @param publisherDao
	 *            the publisherDao to set
	 */
	public void setPublisherDao(PublisherDao publisherDao) {
		this.publisherDao = publisherDao;
	}

	/**
	 * @param subscriberDao
	 *            the subscriberDao to set
	 */
	public void setSubscriberDao(SubscriberDao subscriberDao) {
		this.subscriberDao = subscriberDao;
	}

	@Override
	public void register(String code) {
		if (code == null)
			throw new IllegalArgumentException("Event code is null.");

		Publisher publisher = new Publisher();
		publisher.setCode(code);

		if (publisherDao.findByCode(code) == null)
			publisherDao.save(publisher);
	}

	@Override
	public void unregister(String code) {
		if (code == null)
			throw new IllegalArgumentException("Event code is null.");

		Publisher publisher = publisherDao.findByCode(code);
		if (publisher != null)
			publisherDao.delete(publisher); // TODO verify cascading

	}

	@Override
	public void subscribeUser(CepUser user, String code, ContactType contactType) {
		if (user == null)
			throw new IllegalArgumentException("User parameter is null.");
		if (code == null)
			throw new IllegalArgumentException("Code parameter is null.");
		if (contactType == null)
			throw new IllegalArgumentException(
					"Contact type parameter is null.");

		Subscriber subscriber = new UserSubscriber(user);
		subscriber.setContactType(contactType);

		Publisher publisher = publisherDao.findByCode(code);
		if (publisher == null)
			throw new IllegalArgumentException("Event with code: " + code
					+ " does not exists.");

		subscriber.setPublisher(publisher);
		subscriberDao.save(subscriber);
	}

	@Override
	public void subscribe(String contact, String code, ContactType contactType) {
		if (contact == null)
			throw new IllegalArgumentException("Contact parameter is null.");
		if (code == null)
			throw new IllegalArgumentException("Code parameter is null.");
		if (contactType == null)
			throw new IllegalArgumentException(
					"Contact type parameter is null.");

		Subscriber subscriber = new OrphanSubscriber(contact);
		subscriber.setContactType(contactType);

		Publisher publisher = publisherDao.findByCode(code);
		if (publisher == null)
			throw new IllegalArgumentException("Event with code: " + code
					+ " does not exists.");

		subscriber.setPublisher(publisher);
		subscriberDao.save(subscriber);
	}

	@Override
	public void unsubscribeUser(CepUser user, String code,
			ContactType contactType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unsubscribe(String contact, String code, ContactType contactType) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getUserSubscriptions(CepUser user,
			ContactType contactType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getSubscriptions(String contact, ContactType contactType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getSubscribers(String code, ContactType contactType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CepUser> getUserSubscribers(String code, ContactType contactType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getContacts(String code, ContactType contactType) {
		// TODO Auto-generated method stub
		return null;
	}

}
