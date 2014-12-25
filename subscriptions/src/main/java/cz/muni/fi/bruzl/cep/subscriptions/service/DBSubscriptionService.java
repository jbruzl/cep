package cz.muni.fi.bruzl.cep.subscriptions.service;

import java.util.ArrayList;
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
		if (user == null)
			throw new IllegalArgumentException("User parameter is null.");
		
		Iterable<Subscriber> subscribers = null;
		if (code == null)
			subscribers = subscriberDao.findAllByUser(user);
		else {
			Publisher publisher = publisherDao.findByCode(code);
			if(publisher == null)
				throw new IllegalArgumentException("Event with code "+ code +" does not exists.");
			subscribers = subscriberDao.findAllByUserAndPublisher(user, publisher);
		}
		for (Subscriber subscriber : subscribers) {
			if(contactType != null && subscriber.getContactType().equals(contactType))
				subscriberDao.delete(subscriber);
		}
	}

	@Override
	public void unsubscribe(String contact, String code, ContactType contactType) {
		if (contact == null)
			throw new IllegalArgumentException("Contact parameter is null.");

		Iterable<Subscriber> subscribers = null;
		if (code == null)
			subscribers = subscriberDao.findAllByContact(contact);
		else {
			Publisher publisher = publisherDao.findByCode(code);
			if(publisher == null)
				throw new IllegalArgumentException("Event with code "+ code +" does not exists.");
			subscribers = subscriberDao.findAllByContactAndPublisher(contact, publisher);
		}
		for (Subscriber subscriber : subscribers) {
			if(contactType != null && subscriber.getContactType().equals(contactType))
				subscriberDao.delete(subscriber);
		}
			
	}

	@Override
	public List<String> getUserSubscriptions(CepUser user,
			ContactType contactType) {
		if (user == null)
			throw new IllegalArgumentException("User is null.");

		List<String> codes = new ArrayList<String>();
		Iterable<Subscriber> subscribers = null;
		
		subscribers = subscriberDao.findAllByUser(user);
		
		for (Subscriber subscriber : subscribers) {
			if(contactType != null && subscriber.getContactType().equals(contactType))
				codes.add(subscriber.getPublisher().getCode());
		}

		return codes;
	}

	@Override
	public List<String> getSubscriptions(String contact, ContactType contactType) {
		if (contact == null)
			throw new IllegalArgumentException("Contact is null.");
		
		List<String> codes = new ArrayList<String>();
		Iterable<Subscriber> subscribers = null;
		
		subscribers = subscriberDao.findAllByContact(contact);
		
		for (Subscriber subscriber : subscribers) {
			if(contactType != null && subscriber.getContactType().equals(contactType))
				codes.add(subscriber.getPublisher().getCode());
		}

		return codes;
	}

	@Override
	public List<String> getSubscribers(String code, ContactType contactType) {
		if (code == null)
			throw new IllegalArgumentException("Code is null.");

		Publisher publisher = publisherDao.findByCode(code);

		if (publisher == null)
			throw new IllegalArgumentException("Event type with code " + code
					+ " does not exists.");

		List<String> contacts = new ArrayList<String>();
		for (Subscriber subscriber : publisher.getSubscribers()) {
			if (!(subscriber instanceof OrphanSubscriber))
				continue;
			if (contactType == null
					|| subscriber.getContactType().equals(contactType)) {
				try {
					contacts.add(subscriber.getContact());
				} catch (Exception e) {
					// TODO log
				}
			}
		}

		return contacts;
	}

	@Override
	public List<CepUser> getUserSubscribers(String code, ContactType contactType) {
		if (code == null)
			throw new IllegalArgumentException("Code is null.");

		Publisher publisher = publisherDao.findByCode(code);

		if (publisher == null)
			throw new IllegalArgumentException("Event type with code " + code
					+ " does not exists.");

		List<CepUser> users = new ArrayList<CepUser>();
		for (Subscriber subscriber : publisher.getSubscribers()) {
			if (!(subscriber instanceof UserSubscriber))
				continue;
			if (contactType == null
					|| subscriber.getContactType().equals(contactType)) {
				try {
					users.add(((UserSubscriber) subscriber).getUser());
				} catch (Exception e) {
					// TODO log
				}
			}
		}

		return users;
	}

	@Override
	public List<String> getContacts(String code, ContactType contactType) {
		if (code == null)
			throw new IllegalArgumentException("Code is null.");
		if (contactType == null)
			throw new IllegalArgumentException("Contact type is null.");

		Publisher publisher = publisherDao.findByCode(code);

		if (publisher == null)
			throw new IllegalArgumentException("Event with code: " + code
					+ " does not exists.");

		Iterable<Subscriber> subscribers = subscriberDao
				.findAllByPublisherAndContactType(publisher, contactType);

		List<String> contacts = new ArrayList<>();

		for (Subscriber subscriber : subscribers) {
			try {
				contacts.add(subscriber.getContact());
			} catch (Exception e) {
				// TODO log error
			}
		}

		return contacts;
	}

}
