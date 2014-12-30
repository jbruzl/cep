import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import cz.muni.fi.cep.core.App;
import cz.muni.fi.cep.core.users.api.IdentityService;
import cz.muni.fi.cep.core.users.dao.CepGroupDao;
import cz.muni.fi.cep.core.users.dao.CepUserDao;
import cz.muni.fi.cep.core.users.entities.CepGroupEntity;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

@ContextConfiguration(classes = App.class)
public class IdentityServiceTests extends BasicTest {
	@Autowired
	private IdentityService identityService;

	private CepUserDao cepUserDao;
	private CepGroupDao cepGroupDao;

	@Test
	public void setUpTest() {
		assertNotNull(identityService);
	}

	@Before
	public void setUp() {
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
	}

	@Test
	public void createAndUpdateTest() {
		CepUserEntity cepUserEntity = createUser();

		identityService.createUser(cepUserEntity);
		Mockito.verify(cepUserDao).save(Mockito.any(CepUserEntity.class));

		try {
			identityService.createUser(null);
			fail("Null pointer exception should be thrown.");
		} catch (Exception ex) {
			assertThat(ex,
					IsInstanceOf.instanceOf(NullPointerException.class));
		}

		identityService.updateUser(cepUserEntity);
		Mockito.verify(cepUserDao, Mockito.times(2)).save(
				Mockito.any(CepUserEntity.class));

		CepGroupEntity cepGroupEntity = createGroup();

		identityService.createGroup(cepGroupEntity);
		Mockito.verify(cepGroupDao).save(Mockito.any(CepGroupEntity.class));

		try {
			identityService.createGroup(null);
			fail("Null pointer exception should be thrown.");
		} catch (Exception ex) {
			assertThat(ex,
					IsInstanceOf.instanceOf(NullPointerException.class));
		}
		identityService.updateGroup(cepGroupEntity);
		Mockito.verify(cepGroupDao, Mockito.times(2)).save(
				Mockito.any(CepGroupEntity.class));
	}

	@Test
	public void deleteTest() {
		CepUserEntity cepUserEntity = createUser();

		identityService.deleteUser(cepUserEntity);
		Mockito.verify(cepUserDao).delete(Mockito.any(CepUserEntity.class));

		try {
			identityService.deleteUser(null);
			fail("Null pointer exception should be thrown.");
		} catch (Exception ex) {
			assertThat(ex,
					IsInstanceOf.instanceOf(NullPointerException.class));
		}

		CepGroupEntity cepGroupEntity = createGroup();

		identityService.deleteGroup(cepGroupEntity);
		Mockito.verify(cepGroupDao).delete(Mockito.any(CepGroupEntity.class));

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
		CepGroupEntity givenGroup = identityService.getGroupById(1L);
		assertEquals(expectedGroup, givenGroup);

	}

	@Test
	public void getAllTest() {
		CepUserEntity cepUserEntity = createUser();
		CepUserEntity cepUserEntity2 = createUser();
		cepUserEntity2.setId("2");
		CepUserEntity cepUserEntity3 = createUser();
		cepUserEntity3.setId("3");
		List<CepUserEntity> users = identityService.getAllCepUsers();
		assertNotNull(users);
		assertEquals(3, users.size());
		
		List<CepUserEntity> expectedUsers = Arrays.asList(cepUserEntity, cepUserEntity2, cepUserEntity3);
		assertThat(expectedUsers, is(users));
		
		List<CepGroupEntity> groups = identityService.getAllGroups();
		assertNotNull(groups);
		assertEquals(3, groups.size());
		CepGroupEntity cepGroupEntity = createGroup();
		CepGroupEntity cepGroupEntity2 = createGroup();
		cepGroupEntity2.setId("2");
		CepGroupEntity cepGroupEntity3 = createGroup();
		cepGroupEntity3.setId("3");
		List<CepGroupEntity> expectedGroups = Arrays.asList(cepGroupEntity, cepGroupEntity2, cepGroupEntity3);
		assertThat(expectedGroups, is(groups));
	}

	@Test
	public void membershipTest() {
		CepUserEntity cepUserEntity = createUser();
		CepGroupEntity cepGroupEntity = createGroup();
		
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
		cepUserEntity.setId("1");
		return cepUserEntity;
	}

	private CepGroupEntity createGroup() {
		CepGroupEntity cepGroupEntity = new CepGroupEntity();
		cepGroupEntity.setCode("00X");
		cepGroupEntity.setName("Group 1");
		cepGroupEntity.setType("Type 0");
		cepGroupEntity.setId("1");
		return cepGroupEntity;
	}
}
