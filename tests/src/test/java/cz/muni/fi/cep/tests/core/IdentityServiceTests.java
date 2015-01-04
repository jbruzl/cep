package cz.muni.fi.cep.tests.core;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cz.muni.fi.cep.core.users.api.IdentityService;
import cz.muni.fi.cep.core.users.dao.CepGroupDao;
import cz.muni.fi.cep.core.users.dao.CepUserDao;
import cz.muni.fi.cep.core.users.entities.CepGroupEntity;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;
import cz.muni.fi.cep.tests.BasicTest;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


public class IdentityServiceTests extends BasicTest {
	@Autowired
	private IdentityService identityService;

	@Autowired
	private CepUserDao cepUserDao;
	@Autowired
	private CepGroupDao cepGroupDao;

	@Test
	public void setUpTest() {
		assertNotNull(identityService);
		assertNotNull(cepUserDao);
		assertNotNull(cepGroupDao);
	}

	@Before
	public void setUp() {
		/*
		cepUserDao = Mockito.mock(CepUserDao.class);
		Mockito.when(cepUserDao.findOne(Long.parseLong(createUser().getId()))).thenReturn(createUser());
		List<CepUserEntity> users = new ArrayList<>();
		CepUserEntity cepUserEntity = createUser();
		users.add(createUser());
		cepUserEntity.setId("2");
		users.add(cepUserEntity);
		cepUserEntity = createUser();
		cepUserEntity.setId("3");
		users.add(cepUserEntity);
		Mockito.when(cepUserDao.findAll()).thenReturn(users);
		Mockito.when(cepUserDao.save(createUser())).thenReturn(createUser());
		identityService.setCepUserDao(cepUserDao);

		cepGroupDao = Mockito.mock(CepGroupDao.class);
		Mockito.when(cepGroupDao.findOne(Long.parseLong(createGroup().getId()))).thenReturn(createGroup());
		List<CepGroupEntity> groups = new ArrayList<>();
		CepGroupEntity cepGroupEntity = createGroup();
		groups.add(createGroup());
		cepGroupEntity.setId("2");
		groups.add(cepGroupEntity);
		cepGroupEntity = createGroup();
		cepGroupEntity.setId("3");
		groups.add(cepGroupEntity);
		Mockito.when(cepGroupDao.findAll()).thenReturn(groups);
		Mockito.when(cepGroupDao.save(createGroup())).thenReturn(createGroup());
		identityService.setCepGroupDao(cepGroupDao);
		*/
	}

	@Test
	public void createAndUpdateTest() {
		CepUserEntity cepUserEntity = createUser();

		identityService.createUser(cepUserEntity);
		

		try {
			identityService.createUser(null);
			fail("Null pointer exception should be thrown.");
		} catch (Exception ex) {
			assertThat(ex,
					IsInstanceOf.instanceOf(NullPointerException.class));
		}

		identityService.updateUser(cepUserEntity);

		CepGroupEntity cepGroupEntity = createGroup();

		identityService.createGroup(cepGroupEntity);

		try {
			identityService.createGroup(null);
			fail("Null pointer exception should be thrown.");
		} catch (Exception ex) {
			assertThat(ex,
					IsInstanceOf.instanceOf(NullPointerException.class));
		}
		identityService.updateGroup(cepGroupEntity);

	}

	@Test
	public void deleteTest() {
		CepUserEntity cepUserEntity = createUser();

		identityService.deleteUser(cepUserEntity);


		try {
			identityService.deleteUser(null);
			fail("Null pointer exception should be thrown.");
		} catch (Exception ex) {
			assertThat(ex,
					IsInstanceOf.instanceOf(NullPointerException.class));
		}

		CepGroupEntity cepGroupEntity = createGroup();

		identityService.deleteGroup(cepGroupEntity);


		try {
			identityService.deleteGroup(null);
			fail("Null pointer exception should be thrown.");
		} catch (Exception ex) {
			assertThat(ex,
					IsInstanceOf.instanceOf(NullPointerException.class));
		}
	}

	@Test
	public void getByIdTest() {
		CepUserEntity cepUserEntity = createUser();
		identityService.createUser(cepUserEntity);
		try {
			identityService.getCepUserById(null);
			fail("Null pointer exception should be thrown.");
		} catch (Exception ex) {
			assertThat(ex,
					IsInstanceOf.instanceOf(NullPointerException.class));
		}
		
		CepUserEntity given = identityService.getCepUserById(Long.parseLong(cepUserEntity.getId()));
		assertEquals(cepUserEntity, given);
		
		try {
			identityService.getGroupById(null);
			fail("Null pointer exception should be thrown.");
		} catch (Exception ex) {
			assertThat(ex,
					IsInstanceOf.instanceOf(NullPointerException.class));
		}
		
		CepGroupEntity expectedGroup = createGroup();
		identityService.createGroup(expectedGroup);
		CepGroupEntity givenGroup = identityService.getGroupById(Long.parseLong(expectedGroup.getId()));
		assertEquals(expectedGroup, givenGroup);

	}

	@Test
	public void getAllTest() {
		CepUserEntity cepUserEntity = createUser();
		CepUserEntity cepUserEntity2 = createUser();
		cepUserEntity2.setEmail("email2@gmail.com");
		CepUserEntity cepUserEntity3 = createUser();
		cepUserEntity3.setEmail("email3@gmail.com");
		identityService.createUser(cepUserEntity);
		identityService.createUser(cepUserEntity2);
		identityService.createUser(cepUserEntity3);
		List<CepUserEntity> users = identityService.getAllCepUsers();
		assertNotNull(users);
		assertEquals(3, users.size());
		
		List<CepUserEntity> expectedUsers = Arrays.asList(cepUserEntity, cepUserEntity2, cepUserEntity3);
		assertThat(expectedUsers, is(users));
		
		
		CepGroupEntity cepGroupEntity = createGroup();
		CepGroupEntity cepGroupEntity2 = createGroup();
		cepGroupEntity2.setCode("002");
		CepGroupEntity cepGroupEntity3 = createGroup();
		cepGroupEntity3.setCode("003");
		identityService.createGroup(cepGroupEntity);
		identityService.createGroup(cepGroupEntity2);
		identityService.createGroup(cepGroupEntity3);
		
		List<CepGroupEntity> groups = identityService.getAllGroups();
		assertNotNull(groups);
		assertEquals(3, groups.size());
		
		List<CepGroupEntity> expectedGroups = Arrays.asList(cepGroupEntity, cepGroupEntity2, cepGroupEntity3);
		assertThat(expectedGroups, is(groups));
	}

	@Test
	public void membershipTest() {
		CepUserEntity cepUserEntity = createUser();
		CepGroupEntity cepGroupEntity = createGroup();
		
		identityService.createUser(cepUserEntity);
		identityService.createGroup(cepGroupEntity);
		identityService.createMembership(cepUserEntity, cepGroupEntity);
		
		assertTrue(cepUserEntity.getGroups().contains(cepGroupEntity));
		assertTrue(cepGroupEntity.getUsers().contains(cepUserEntity));
		
		identityService.deleteMembership(cepUserEntity, cepGroupEntity);
		
		assertFalse(cepUserEntity.getGroups().contains(cepGroupEntity));
		assertFalse(cepGroupEntity.getUsers().contains(cepUserEntity));
	}

	private CepUserEntity createUser() {
		CepUserEntity cepUserEntity = new CepUserEntity();
		cepUserEntity.setFirstName("Jan");
		cepUserEntity.setLastName("Bruzl");
		cepUserEntity.setEmail("bruzl.jan@gmail.com");
		cepUserEntity.setPhoneNumber("728484615");
		cepUserEntity.setPassword("01234");
		return cepUserEntity;
	}

	private CepGroupEntity createGroup() {
		CepGroupEntity cepGroupEntity = new CepGroupEntity();
		cepGroupEntity.setCode("001");
		cepGroupEntity.setName("Group 1");
		cepGroupEntity.setType("Type 0");
		return cepGroupEntity;
	}
}
