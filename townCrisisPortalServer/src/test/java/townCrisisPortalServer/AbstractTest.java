package townCrisisPortalServer;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:activiti-test.cfg.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true)
public abstract class AbstractTest{

}
