import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import cz.muni.fi.cep.core.App;


@ContextConfiguration(classes = App.class)
public abstract class BasicTest extends AbstractTransactionalJUnit4SpringContextTests{

}
