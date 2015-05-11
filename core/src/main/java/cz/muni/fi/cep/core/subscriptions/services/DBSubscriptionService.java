package cz.muni.fi.cep.core.subscriptions.services;

import java.util.ArrayList;
import java.util.List;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.core.subscriptions.dao.PublisherDao;
import cz.muni.fi.cep.core.subscriptions.dao.SubscriberDao;
import cz.muni.fi.cep.core.subscriptions.entities.OrphanSubscriber;
import cz.muni.fi.cep.core.subscriptions.entities.Publisher;
import cz.muni.fi.cep.core.subscriptions.entities.Subscriber;
import cz.muni.fi.cep.core.subscriptions.entities.UserSubscriber;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;

@Service("subscriptionService")
@Transactional
public class DBSubscriptionService implements SubscriptionService {
	@Autowired
	private PublisherDao publisherDao;

	@Autowired
	private SubscriberDao subscriberDao;

	@Autowired
	private Mapper mapper;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public void setPublisherDao(PublisherDao publisherDao) {
		this.publisherDao = publisherDao;
	}

	public void setSubscriberDao(SubscriberDao subscriberDao) {
		this.subscriberDao = subscriberDao;
	}

	@Override
	public void register(String code) {
		if (code == null)
			throw new NullPointerException("Event code is null.");

		Publisher publisher = new Publisher();
		publisher.setCode(code);

		if (publisherDao.findByCode(code) == null) {
			logger.debug("Publisher with code {} not found, registering", code);
			publisherDao.save(publisher);
		}

		logger.info("Publisher with code {} registered.", code);
	}

	@Override
	public void unregister(String code) {
		if (code == null)
			throw new NullPointerException("Event code is null.");

		Publisher publisher = publisherDao.findByCode(code);
		if (publisher != null) {
			logger.debug("Publisher with code {} found, deleting", code);
			publisherDao.delete(publisher);
		}
		logger.info("Publisher with code {} unregistered.", code);
	}

	@Override
	public void subscribeUser(CepUser user, String code, ContactType contactType) {
		if (user == null)
			throw new NullPointerException("User parameter is null.");
		if (code == null)
			throw new NullPointerException("Code parameter is null.");
		if (contactType == null)
			throw new NullPointerException("Contact type parameter is null.");

		if (getUserSubscriptions(user, contactType).contains(code)) {
			logger.info("User {} already subscribed to {} with {}", user, code,
					contactType.toString());
			return;
		}

		CepUserEntity cepUserEntity = mapper.map(user, CepUserEntity.class);

		Subscriber subscriber = new UserSubscriber(cepUserEntity);
		subscriber.setContactType(contactType);

		Publisher publisher = publisherDao.findByCode(code);
		if (publisher == null) {
			logger.debug("Publisher with code {} not found", code);
			throw new IllegalArgumentException("Event with code: " + code
					+ " does not exists.");
		}

		subscriber.setPublisher(publisher);
		subscriberDao.save(subscriber);

		logger.info("User {} subscribed to Publisher {} with contact type {}",
				user, code, contactType);
	}

	@Override
	public void subscribe(String contact, String code, ContactType contactType) {
		if (contact == null)
			throw new NullPointerException("Contact parameter is null.");
		if (code == null)
			throw new NullPointerException("Code parameter is null.");
		if (contactType == null)
			throw new NullPointerException("Contact type parameter is null.");

		Subscriber subscriber = new OrphanSubscriber(contact);
		subscriber.setContactType(contactType);

		Publisher publisher = publisherDao.findByCode(code);
		if (publisher == null) {
			logger.debug("Publisher with code {} not found", code);
			throw new IllegalArgumentException("Event with code: " + code
					+ " does not exists.");
		}

		subscriber.setPublisher(publisher);
		subscriberDao.save(subscriber);
		logger.info(
				"Contact {} subscribed to Publisher {} with contact type {}",
				contact, code, contactType);
	}

	@Override
	public void unsubscribeUser(CepUser user, String code,
			ContactType contactType) {
		if (user == null)
			throw new NullPointerException("User parameter is null.");

		Iterable<Subscriber> subscribers = null;
		CepUserEntity cepUserEntity = mapper.map(user, CepUserEntity.class);
		if (code == null)
			subscribers = subscriberDao.findAllByUser(cepUserEntity);
		else {
			Publisher publisher = publisherDao.findByCode(code);
			if (publisher == null)
				throw new IllegalArgumentException("Event with code " + code
						+ " does not exists.");
			subscribers = subscriberDao.findAllByUserAndPublisher(
					cepUserEntity, publisher);
		}
		for (Subscriber subscriber : subscribers) {
			if (contactType == null
					|| subscriber.getContactType().equals(contactType)) {
				logger.debug(
						"Deleting subscription for {} to {} with contact type {}",
						user, code, subscriber.getContactType());
				subscriberDao.delete(subscriber);
			}
		}
		logger.info(
				"User {} unsubscribed from Publisher {} with contact type {}",
				user, code, contactType);
	}

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
					|| subscriber.getContactType().equals(contactType)) {
				logger.debug(
						"Deleting subscription for {} to {} with contact type {}",
						contact, code, subscriber.getContactType());
				subscriberDao.delete(subscriber);
			}
		}
		logger.info(
				"Contact {} unsubscribed from Publisher {} with contact type {}",
				contact, code, contactType);
	}

	@Override
	public List<String> getUserSubscriptions(CepUser user,
			ContactType contactType) {
		if (user == null)
			throw new NullPointerException("User is null.");

		List<String> codes = new ArrayList<String>();
		Iterable<Subscriber> subscribers = null;

		CepUserEntity cepUserEntity = mapper.map(user, CepUserEntity.class);
		subscribers = subscriberDao.findAllByUser(cepUserEntity);

		for (Subscriber subscriber : subscribers) {
			if (contactType == null
					|| subscriber.getContactType().equals(contactType))
				codes.add(subscriber.getPublisher().getCode());
		}
		logger.info(
				"Returning user {} subscription with contact type {}. Size: {} ",
				user, contactType, codes.size());
		return codes;
	}

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
		logger.info(
				"Returning contact {} subscription with contact type {}. Size: {} ",
				contact, contactType, codes.size());
		return codes;
	}

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
					logger.error(
							"Tryied to obtain contact of type {} from {} but failed.",
							contactType, subscriber);
				}
			}
		}
		logger.info(
				"Returning publishers {} subscribed contacts with contact type {}. Size: {} ",
				publisher, contactType, contacts.size());
		return contacts;
	}

	@Override
	public List<CepUser> getUserSubscribers(String code, ContactType contactType) {
		if (code == null)
			throw new NullPointerException("Code is null.");

		Publisher publisher = publisherDao.findByCode(code);

		if (publisher == null)
			throw new IllegalArgumentException("Event type with code " + code
					+ " does not exists.");

		List<CepUser> users = new ArrayList<>();
		for (Subscriber subscriber : publisher.getSubscribers()) {
			if (!(subscriber instanceof UserSubscriber))
				continue;
			if (contactType == null
					|| subscriber.getContactType().equals(contactType)) {
				try {
					users.add(mapper.map(
							((UserSubscriber) subscriber).getCepUserEntity(),
							CepUser.class));
				} catch (Exception e) {
					logger.error(
							"Tryied to obtain contact of type {} from {} but failed.",
							contactType, subscriber);
				}
			}
		}
		logger.info(
				"Returning publishers {} subscribed users with contact type {}. Size: {} ",
				publisher, contactType, users.size());
		return users;
	}

	@Override
	public List<String> getContacts(String code, ContactType contactType) {
		if (code == null)
			throw new NullPointerException("Code is null.");
		if (contactType == null)
			throw new IllegalArgumentException("Contact type is null.");

		Publisher publisher = publisherDao.findByCode(code);

		if (publisher == null) {
			logger.error("Event with code: {} does not exists.", code);
			return new ArrayList<>();
		}

		Iterable<Subscriber> subscribers = subscriberDao
				.findAllByPublisherAndContactType(publisher, contactType);

		List<String> contacts = new ArrayList<>();

		for (Subscriber subscriber : subscribers) {
			try {
				contacts.add(subscriber.getContact());
			} catch (Exception e) {
				logger.error(
						"Tryied to obtain contact of type {} from {} but failed.",
						contactType, subscriber);
			}
		}
		logger.info("Returning contacts for {} of type {}", code, contactType);
		return contacts;
	}

	@Override
	public List<String> getAllPublishers() {
		Iterable<Publisher> publishers = null;

		publishers = publisherDao.findAll();
		List<String> publisherList = new ArrayList<>();
		for (Publisher publisher : publishers) {
			publisherList.add(publisher.getCode());
		}
		logger.info("Returning list of publisher codes. Size: {}",
				publisherList.size());
		return publisherList;
	}

}
