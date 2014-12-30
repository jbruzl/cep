package cz.muni.fi.cep.core.subscriptions.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.core.subscriptions.api.SubscriptionService;
import cz.muni.fi.cep.core.subscriptions.dao.PublisherDao;
import cz.muni.fi.cep.core.subscriptions.dao.SubscriberDao;
import cz.muni.fi.cep.core.subscriptions.entities.OrphanSubscriber;
import cz.muni.fi.cep.core.subscriptions.entities.Publisher;
import cz.muni.fi.cep.core.subscriptions.entities.Subscriber;
import cz.muni.fi.cep.core.subscriptions.entities.UserSubscriber;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;

@Service
@Transactional
public class DBSubscriptionService implements SubscriptionService {
	@Autowired
	private PublisherDao publisherDao;

	@Autowired
	private SubscriberDao subscriberDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.muni.fi.cep.core.subscriptions.services.SubscriptionService#
	 * setPublisherDao(cz.muni.fi.cep.core.subscriptions.dao.PublisherDao)
	 */
	@Override
	public void setPublisherDao(PublisherDao publisherDao) {
		this.publisherDao = publisherDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.muni.fi.cep.core.subscriptions.services.SubscriptionService#
	 * setSubscriberDao(cz.muni.fi.cep.core.subscriptions.dao.SubscriberDao)
	 */
	@Override
	public void setSubscriberDao(SubscriberDao subscriberDao) {
		this.subscriberDao = subscriberDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.muni.fi.cep.core.subscriptions.services.SubscriptionService#register
	 * (java.lang.String)
	 */
	@Override
	public void register(String code) {
		if (code == null)
			throw new NullPointerException("Event code is null.");

		Publisher publisher = new Publisher();
		publisher.setCode(code);

		if (publisherDao.findByCode(code) == null)
			publisherDao.save(publisher);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.muni.fi.cep.core.subscriptions.services.SubscriptionService#unregister
	 * (java.lang.String)
	 */
	@Override
	public void unregister(String code) {
		if (code == null)
			throw new NullPointerException("Event code is null.");

		Publisher publisher = publisherDao.findByCode(code);
		if (publisher != null)
			publisherDao.delete(publisher); // TODO verify cascading

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.muni.fi.cep.core.subscriptions.services.SubscriptionService#subscribeUser
	 * (cz.muni.fi.cep.core.users.entities.CepUserEntity, java.lang.String,
	 * cz.muni.fi.cep.api.DTO.ContactType)
	 */
	@Override
	public void subscribeUser(CepUserEntity user, String code,
			ContactType contactType) {
		if (user == null)
			throw new NullPointerException("User parameter is null.");
		if (code == null)
			throw new NullPointerException("Code parameter is null.");
		if (contactType == null)
			throw new NullPointerException(
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.muni.fi.cep.core.subscriptions.services.SubscriptionService#subscribe
	 * (java.lang.String, java.lang.String, cz.muni.fi.cep.api.DTO.ContactType)
	 */
	@Override
	public void subscribe(String contact, String code, ContactType contactType) {
		if (contact == null)
			throw new NullPointerException("Contact parameter is null.");
		if (code == null)
			throw new NullPointerException("Code parameter is null.");
		if (contactType == null)
			throw new NullPointerException(
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.muni.fi.cep.core.subscriptions.services.SubscriptionService#
	 * unsubscribeUser(cz.muni.fi.cep.core.users.entities.CepUserEntity,
	 * java.lang.String, cz.muni.fi.cep.api.DTO.ContactType)
	 */
	@Override
	public void unsubscribeUser(CepUserEntity user, String code,
			ContactType contactType) {
		if (user == null)
			throw new NullPointerException("User parameter is null.");

		Iterable<Subscriber> subscribers = null;
		if (code == null)
			subscribers = subscriberDao.findAllByUser(user);
		else {
			Publisher publisher = publisherDao.findByCode(code);
			if (publisher == null)
				throw new IllegalArgumentException("Event with code " + code
						+ " does not exists.");
			subscribers = subscriberDao.findAllByUserAndPublisher(user,
					publisher);
		}
		for (Subscriber subscriber : subscribers) {
			if (contactType == null
					|| subscriber.getContactType().equals(contactType))
				subscriberDao.delete(subscriber);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.muni.fi.cep.core.subscriptions.services.SubscriptionService#unsubscribe
	 * (java.lang.String, java.lang.String, cz.muni.fi.cep.api.DTO.ContactType)
	 */
	@Override
	public void unsubscribe(String contact, String code, ContactType contactType) {
		if (contact == null)
			throw new NullPointerException("Contact parameter is null.");

		Iterable<Subscriber> subscribers = null;
		if (code == null)
			subscribers = subscriberDao.findAllByContact(contact);
		else {
			Publisher publisher = publisherDao.findByCode(code);
			if (publisher == null)
				throw new IllegalArgumentException("Event with code " + code
						+ " does not exists.");
			subscribers = subscriberDao.findAllByContactAndPublisher(contact,
					publisher);
		}
		for (Subscriber subscriber : subscribers) {
			if (contactType == null
					|| subscriber.getContactType().equals(contactType))
				subscriberDao.delete(subscriber);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.muni.fi.cep.core.subscriptions.services.SubscriptionService#
	 * getUserSubscriptions(cz.muni.fi.cep.core.users.entities.CepUserEntity,
	 * cz.muni.fi.cep.api.DTO.ContactType)
	 */
	@Override
	public List<String> getUserSubscriptions(CepUserEntity user,
			ContactType contactType) {
		if (user == null)
			throw new NullPointerException("User is null.");

		List<String> codes = new ArrayList<String>();
		Iterable<Subscriber> subscribers = null;

		subscribers = subscriberDao.findAllByUser(user);

		for (Subscriber subscriber : subscribers) {
			if (contactType == null
					|| subscriber.getContactType().equals(contactType))
				codes.add(subscriber.getPublisher().getCode());
		}

		return codes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.muni.fi.cep.core.subscriptions.services.SubscriptionService#
	 * getSubscriptions(java.lang.String, cz.muni.fi.cep.api.DTO.ContactType)
	 */
	@Override
	public List<String> getSubscriptions(String contact, ContactType contactType) {
		if (contact == null)
			throw new NullPointerException("Contact is null.");

		List<String> codes = new ArrayList<String>();
		Iterable<Subscriber> subscribers = null;

		subscribers = subscriberDao.findAllByContact(contact);

		for (Subscriber subscriber : subscribers) {
			if (contactType == null
					|| subscriber.getContactType().equals(contactType))
				codes.add(subscriber.getPublisher().getCode());
		}

		return codes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.muni.fi.cep.core.subscriptions.services.SubscriptionService#getSubscribers
	 * (java.lang.String, cz.muni.fi.cep.api.DTO.ContactType)
	 */
	@Override
	public List<String> getSubscribers(String code, ContactType contactType) {
		if (code == null)
			throw new NullPointerException("Code is null.");

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see cz.muni.fi.cep.core.subscriptions.services.SubscriptionService#
	 * getUserSubscribers(java.lang.String, cz.muni.fi.cep.api.DTO.ContactType)
	 */
	@Override
	public List<CepUser> getUserSubscribers(String code, ContactType contactType) {
		if (code == null)
			throw new NullPointerException("Code is null.");

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
					// users.add(((UserSubscriber) subscriber).getId()); TODO
				} catch (Exception e) {
					// TODO log
				}
			}
		}

		return users;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cz.muni.fi.cep.core.subscriptions.services.SubscriptionService#getContacts
	 * (java.lang.String, cz.muni.fi.cep.api.DTO.ContactType)
	 */
	@Override
	public List<String> getContacts(String code, ContactType contactType) {
		if (code == null)
			throw new NullPointerException("Code is null.");
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

	@Override
	public List<String> getAllPublishers() {
		Iterable<Publisher> publishers = null;

		publishers = publisherDao.findAll();
		List<String> subscriberList = new ArrayList<>();
		for(Publisher publisher : publishers) {
			subscriberList.add(publisher.getCode());
		}
		return subscriberList;
	}

}
