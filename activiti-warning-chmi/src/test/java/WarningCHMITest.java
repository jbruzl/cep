import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricVariableUpdate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.subethamail.wiser.Wiser;

import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.core.bpmn.service.api.MessageType;
import cz.muni.fi.cep.core.subscriptions.api.SubscriptionService;
import cz.muni.fi.cep.core.users.api.IdentityService;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;
import cz.muni.fi.cep.warning.chmi.WeatherReportRegister;
import cz.muni.fi.cep.warning.chmi.report.Report;

/**
 * @author Jan Bruzl
 *
 */
@PropertySource("classpath:config/config.properties")
public class WarningCHMITest extends ActivitiBasicTest {
	private Wiser wiser;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	private WeatherReportRegister weatherReportRegistr;
	
	private String publisherCode = "WeatherReport";

	@Test
	@org.activiti.engine.test.Deployment(resources = {
			"diagrams/Warning CHMI.bpmn", "diagrams/Notify.bpmn",
			"diagrams/SendSMS.bpmn" })
	public void startTest() {
		Map<String, String> message = new HashMap<>();
		message.put("message", "hello");
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);
		params.put("templateKey", "default");
		params.put("templateVariable", message);
		params.put("notificationEnabled", "true");
		params.put("messageType", MessageType.NOTIFICATION);

		runtimeService.startProcessInstanceByKey("warningChmi", params);

		List<HistoricDetail> historyVariables = historyService
				.createHistoricDetailQuery().variableUpdates()
				.orderByVariableName().asc().list();

		Report report;
		for (HistoricDetail hd : historyVariables) {
			HistoricVariableUpdate hvu = (HistoricVariableUpdate) hd;
			if (hvu.getVariableTypeName().equals("weatherReport"))
				report = (Report) hvu.getValue();

		}

	}

	@Before
	public void setUp() {
		assertNotNull("Identity service null", identityService);
		assertNotNull("Subscription service null", subscriptionService);

		CepUserEntity userEntity = new CepUserEntity();
		userEntity.setFirstName("Jan");
		userEntity.setLastName("Bruzl");
		userEntity.setEmail("notify@gmail.com");
		userEntity.setPhoneNumber("+420728484615");
		userEntity.setPassword("01234");

		CepUserEntity userEntity2 = new CepUserEntity();
		userEntity2.setFirstName("Karel");
		userEntity2.setLastName("Novak");
		userEntity2.setEmail("novak@novak.cz");
		userEntity2.setPhoneNumber("+420728484615");
		userEntity2.setPassword("01234");

		if (identityService.getAllCepUsers().isEmpty()) {
			identityService.createUser(userEntity);
			identityService.createUser(userEntity2);

			identityService.setAuthenticatedUserId(userEntity.getEmail());
		}

		subscriptionService.register(publisherCode);
		if (subscriptionService.getUserSubscribers(publisherCode, null).isEmpty()) {
			subscriptionService.subscribeUser(userEntity, publisherCode,
					ContactType.EMAIL);
			//subscriptionService.subscribeUser(userEntity, publisherCode,
				//	ContactType.SMS);
			subscriptionService.subscribeUser(userEntity2, publisherCode,
					ContactType.EMAIL);
		}
		wiser = new Wiser();
		wiser.setPort(2025);
		wiser.start();
	}

	@After
	public void tearDown() {
		identityService.setAuthenticatedUserId(null);
		if (wiser != null)
			wiser.stop();
	}
}
