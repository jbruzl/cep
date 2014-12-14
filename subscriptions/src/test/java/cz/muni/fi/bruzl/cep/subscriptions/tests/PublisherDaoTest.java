/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cz.muni.fi.bruzl.cep.subscriptions.dao.PublisherDao;

/**
 * @author Jan Bruzl
 *
 */
public class PublisherDaoTest extends AbstractTest {
	@Test
	public void basicTest() {
		System.out.println("Test's alive!");
		applicationContext.getBean(PublisherDao.class);
	}
}
