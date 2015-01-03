package services;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;

import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.core.subscriptions.api.SubscriptionService;
import cz.muni.fi.cep.core.users.api.IdentityService;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;

public class SubscriptionServiceTests extends BasicTest {
	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	private IdentityService identityService;

	@Test
	public void setUpTest() {
		assertNotNull(subscriptionService);
	}

	@Before
	public void setUp() {

	}

	@Test
	public void registerUnregisterTest() {
		String code = "001";
		subscriptionService.register(code);
		List<String> codes = subscriptionService.getAllPublishers();
		assertNotNull(codes);
		assertEquals(1, codes.size());
		assertEquals(code, codes.get(0));

		subscriptionService.unregister(code);
		codes = subscriptionService.getAllPublishers();
		assertNotNull(codes);
		assertEquals(0, codes.size());
	}

	@Test
	public void subscribeTest() {
		String code = "001";
		subscriptionService.register(code);

		String contact = "a@b.cz";
		subscriptionService.subscribe(contact, code, ContactType.EMAIL);

		List<String> contactList = subscriptionService.getContacts(code,
				ContactType.SMS);
		assertNotNull(contactList);
		assertEquals(0, contactList.size());

		contactList = subscriptionService.getSubscriptions(contact,
				ContactType.SMS);
		assertNotNull(contactList);
		assertEquals(0, contactList.size());

		contactList = subscriptionService.getContacts(code, ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(1, contactList.size());
		assertEquals(contact, contactList.get(0));

		contactList = subscriptionService.getSubscriptions(contact,
				ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(1, contactList.size());
		assertEquals(code, contactList.get(0));

		contactList = subscriptionService.getSubscriptions(contact, null);
		assertNotNull(contactList);
		assertEquals(1, contactList.size());
		assertEquals(code, contactList.get(0));

		subscriptionService.unsubscribe(contact, code, ContactType.SMS);

		contactList = subscriptionService.getContacts(code, ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(1, contactList.size());

		contactList = subscriptionService.getSubscriptions(contact,
				ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(1, contactList.size());

		subscriptionService.unsubscribe(contact, code, ContactType.EMAIL);

		contactList = subscriptionService.getContacts(code, ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(0, contactList.size());

		contactList = subscriptionService.getSubscriptions(contact,
				ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(0, contactList.size());
		
		subscriptionService.subscribe(contact, code, ContactType.EMAIL);
		
		subscriptionService.unsubscribe(contact, code, null);

		contactList = subscriptionService.getContacts(code, ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(0, contactList.size());

		contactList = subscriptionService.getSubscriptions(contact,
				ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(0, contactList.size());
	}
	
	@Test
	public void subscribeUserTest() {
		String code = "001";
		subscriptionService.register(code);
		
		CepUserEntity userEntity = new CepUserEntity();
		userEntity.setFirstName("Jan");
		userEntity.setLastName("Bruzl");
		userEntity.setEmail("bruzl.jan@gmail.com");
		userEntity.setPhoneNumber("728484615");
		userEntity.setPassword("01234");
		userEntity.setId("1");
		identityService.createUser(userEntity);
		assertNotNull(userEntity.getId());
		
		

		String contact = userEntity.getEmail();
		subscriptionService.subscribeUser(userEntity, code, ContactType.EMAIL);

		List<String> contactList = subscriptionService.getContacts(code,
				ContactType.SMS);
		assertNotNull(contactList);
		assertEquals(0, contactList.size());

		contactList = subscriptionService.getUserSubscriptions(userEntity,
				ContactType.SMS);
		assertNotNull(contactList);
		assertEquals(0, contactList.size());

		contactList = subscriptionService.getContacts(code, ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(1, contactList.size());
		assertEquals(contact, contactList.get(0));

		contactList = subscriptionService.getUserSubscriptions(userEntity,
				ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(1, contactList.size());
		assertEquals(code, contactList.get(0));

		contactList = subscriptionService.getUserSubscriptions(userEntity, null);
		assertNotNull(contactList);
		assertEquals(1, contactList.size());
		assertEquals(code, contactList.get(0));

		subscriptionService.unsubscribeUser(userEntity, code, ContactType.SMS);

		contactList = subscriptionService.getContacts(code, ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(1, contactList.size());

		contactList = subscriptionService.getUserSubscriptions(userEntity,
				ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(1, contactList.size());

		subscriptionService.unsubscribeUser(userEntity, code, ContactType.EMAIL);

		contactList = subscriptionService.getContacts(code, ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(0, contactList.size());

		contactList = subscriptionService.getUserSubscriptions(userEntity,
				ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(0, contactList.size());
		
		subscriptionService.subscribeUser(userEntity, code, ContactType.EMAIL);
		
		subscriptionService.unsubscribeUser(userEntity, code, null);

		contactList = subscriptionService.getContacts(code, ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(0, contactList.size());

		contactList = subscriptionService.getUserSubscriptions(userEntity,
				ContactType.EMAIL);
		assertNotNull(contactList);
		assertEquals(0, contactList.size());
	}

	@Test
	public void failSubscribeTest() {
		try {
			subscriptionService.subscribe("001", null, ContactType.SMS);
			fail("NullPointerException expected.");
		} catch (NullPointerException ex) {
		}
		try {
			subscriptionService.subscribe(null, "001", ContactType.SMS);
			fail("NullPointerException expected.");
		} catch (NullPointerException ex) {
		}
		try {
			subscriptionService.subscribe("001", "001", null);
			fail("NullPointerException expected.");
		} catch (NullPointerException ex) {
		}
		try {
			subscriptionService.subscribe("001", "001", ContactType.SMS);
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException ex) {
		}

		try {
			subscriptionService.subscribeUser(createUserEntity(), null,
					ContactType.SMS);
			fail("NullPointerException expected.");
		} catch (NullPointerException ex) {
		}
		try {
			subscriptionService.subscribeUser(null, "001", ContactType.SMS);
			fail("NullPointerException expected.");
		} catch (NullPointerException ex) {
		}
		try {
			subscriptionService.subscribeUser(createUserEntity(), "001", null);
			fail("NullPointerException expected.");
		} catch (NullPointerException ex) {
		}
		try {
			subscriptionService.subscribeUser(createUserEntity(), "001",
					ContactType.SMS);
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException ex) {
		}
	}

	private CepUserEntity createUserEntity() {
		CepUserEntity cepUserEntity = new CepUserEntity();
		cepUserEntity.setFirstName("Jan");
		cepUserEntity.setLastName("Bruzl");
		cepUserEntity.setEmail("bruzl.jan@gmail.com");
		cepUserEntity.setPhoneNumber("728484615");
		cepUserEntity.setPassword("01234");
		cepUserEntity.setId("1");
		return cepUserEntity;
	}
}
