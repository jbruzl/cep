/**
 * 
 */
package cz.muni.fi.bruzl.cep.subscriptions.tests;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import cz.muni.fi.bruzl.cep.subscriptions.dao.PublisherDao;

/**
 * @author Jan Bruzl
 *
 */
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes = {TestConfig.class, PublisherDao.class})
public abstract class AbstractTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	
}
