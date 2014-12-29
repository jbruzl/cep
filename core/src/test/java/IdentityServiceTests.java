import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import cz.muni.fi.cep.core.App;
import cz.muni.fi.cep.core.users.dao.CepGroupDao;
import cz.muni.fi.cep.core.users.dao.CepUserDao;
import cz.muni.fi.cep.core.users.entities.CepGroupEntity;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;
import cz.muni.fi.cep.core.users.services.IdentityService;

@ContextConfiguration(classes=App.class)
public class IdentityServiceTests extends BasicTest {
	@Autowired
	private IdentityService identityService;
	
	private CepUserDao cepUserDao;
	private CepGroupDao cepGroupDao;
	
	@Test
	public void setUpTest() {
		Assert.assertNotNull(identityService);
	}
	
	@Before
	public void setUp() {
		cepUserDao = Mockito.mock(CepUserDao.class);
		//TODO mock cepUserDao
		identityService.setCepUserDao(cepUserDao);
		
		cepGroupDao = Mockito.mock(CepGroupDao.class);
		//TODO mock cepGroupDao
		identityService.setCepGroupDao(cepGroupDao);
	}
	
	@Test
	public void createTest() {
		CepUserEntity cepUserEntity = createUser();
		
		identityService.createUser(cepUserEntity);
		Mockito.verify(cepUserDao).save(Mockito.any(CepUserEntity.class));
		
		try {
			identityService.createUser(null);
			Assert.fail("Null pointer exception should be thrown.");
		}catch(Exception ex) {
			Assert.assertThat(ex, IsInstanceOf.instanceOf(NullPointerException.class));
		}
		
		
		CepGroupEntity cepGroupEntity = createGroup();
		
		identityService.createGroup(cepGroupEntity);
		Mockito.verify(cepGroupDao).save(Mockito.any(CepGroupEntity.class));
		
		try {
			identityService.createGroup(null);
			Assert.fail("Null pointer exception should be thrown.");
		}catch(Exception ex) {
			Assert.assertThat(ex, IsInstanceOf.instanceOf(NullPointerException.class));
		}
	}
	
	@Test
	public void updateTest() {
		CepUserEntity cepUserEntity = createUser();
		
		identityService.updateUser(cepUserEntity);
		Mockito.verify(cepUserDao).save(Mockito.any(CepUserEntity.class));
		
		CepGroupEntity cepGroupEntity = createGroup();
		
		identityService.updateGroup(cepGroupEntity);
		Mockito.verify(cepGroupDao).save(Mockito.any(CepGroupEntity.class));		
	}

	
	
	@Test
	public void deleteTest() {
		
	}
	
	@Test
	public void getByIdTest() {
		
	}
	
	@Test
	public void getAllTest() {
		
	}
	
	@Test
	public void membershipTest() {
		
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
		cepGroupEntity.setCode("00X");
		cepGroupEntity.setName("Group 1");
		cepGroupEntity.setType("Type 0");
		return cepGroupEntity;
	}
}
