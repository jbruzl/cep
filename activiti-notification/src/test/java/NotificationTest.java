import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import cz.muni.fi.cep.activiti.notification.tasks.SendSMSTask;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.api.services.users.IdentityService;
import cz.muni.fi.cep.core.bpmn.service.api.MessageType;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;

/**
 * Test class for Notify.bpmn process.
 * 
 * @author Jan Bruzl
 *
 */
public class NotificationTest extends ActivitiBasicTest {
	private Wiser wiser;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private SendSMSTask sendSMSTask;

	@Autowired
	private ConfigurationManager configurationManager;

	private String message = "Hello_World!";
	private String receiver = "728484615";
	private String publisherCode = "001";
	private MockRestServiceServer mockServer;

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/SendSMS.bpmn",
			"diagrams/Notify.bpmn" })
	public void deploymentTest() {
		assertNotNull("Expecting deployed Notify process", repositoryService
				.createProcessDefinitionQuery().processDefinitionKey("notify")
				.list().size() > 0);
		assertNotNull("Expecting deployed SendSMS process", repositoryService
				.createProcessDefinitionQuery().processDefinitionKey("sendsms")
				.list().size() > 0);
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/SendSMS.bpmn",
			"diagrams/Notify.bpmn" })
	public void notificationDisabledTest() {
		HashMap<String, Object> params = new HashMap<>();
		HashMap<String, String> message = new HashMap<>();
		message.put("message", this.message);
		params.put("publisherCode", publisherCode);
		params.put("messageType", MessageType.NOTIFICATION);
		params.put("templateKey", "default");
		params.put("templateVariable", message);

		configurationManager.setKey("cep.notify.enabled", "false");

		ProcessInstance pi = runtimeService.startProcessInstanceByKey("notify",
				params);
		assertNotNull(pi);

		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().activityType("endEvent")
				.finished().list();
		assertEquals(1, haiList.size());
		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(0).getActivityId(),
				"notificationdisabled1", haiList.get(0).getActivityId());

		List<WiserMessage> messages = wiser.getMessages();
		assertEquals(0, messages.size());
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/SendSMS.bpmn",
			"diagrams/Notify.bpmn" })
	public void notificationTest() {
		HashMap<String, Object> params = new HashMap<>();
		HashMap<String, String> message = new HashMap<>();
		message.put("message", this.message);
		params.put("publisherCode", publisherCode);
		params.put("messageType", MessageType.NOTIFICATION);
		params.put("templateKey", "default");
		params.put("templateVariable", message);

		configurationManager.setKey("cep.notify.enabled", "true");

		ProcessInstance pi = runtimeService.startProcessInstanceByKey("notify",
				params);
		assertNotNull(pi);

		List<WiserMessage> messages = wiser.getMessages();
		assertEquals(2, messages.size());

		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().activityType("endEvent")
				.finished().orderByHistoricActivityInstanceEndTime().asc()
				.list();
		assertEquals(4, haiList.size());
		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(3).getActivityId(), "endevent3", haiList
						.get(3).getActivityId());

		mockServer.verify();
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/SendSMS.bpmn",
			"diagrams/Notify.bpmn" })
	public void noReceiversTest() {
		HashMap<String, Object> params = new HashMap<>();
		HashMap<String, String> message = new HashMap<>();
		message.put("message", "default");
		params.put("publisherCode", "002");
		params.put("messageType", MessageType.NOTIFICATION);
		params.put("templateKey", "default");
		params.put("templateVariable", message);

		configurationManager.setKey("cep.notify.enabled", "true");

		ProcessInstance pi = runtimeService.startProcessInstanceByKey("notify",
				params);
		assertNotNull(pi);

		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().activityType("endEvent")
				.finished().orderByHistoricActivityInstanceEndTime().asc()
				.list();
		assertEquals(3, haiList.size());
		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(2).getActivityId(), "endevent3", haiList
						.get(2).getActivityId());

		List<WiserMessage> messages = wiser.getMessages();
		assertEquals(0, messages.size());
	}

	@Before
	public void setUp() {
		assertNotNull("Identity service null", identityService);
		assertNotNull("Subscription service null", subscriptionService);

		CepUserEntity userEntity = new CepUserEntity();
		userEntity.setFirstName("Jan");
		userEntity.setLastName("Bruzl");
		userEntity.setEmail("notify@gmail.com");
		userEntity.setPhoneNumber(receiver);
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
		if (subscriptionService.getUserSubscribers(publisherCode, null)
				.isEmpty()) {
			subscriptionService.subscribeUser(userEntity, publisherCode,
					ContactType.EMAIL);
			subscriptionService.subscribeUser(userEntity, publisherCode,
					ContactType.SMS);
			subscriptionService.subscribeUser(userEntity2, publisherCode,
					ContactType.EMAIL);
		}
		wiser = new Wiser();
		wiser.setPort(2025);
		wiser.start();

		configurationManager.setKey("cep.notify.sms.login", "login");
		configurationManager.setKey("cep.notify.sms.password", "password");

		RestTemplate restTemplate = new RestTemplate();
		String requestUrl = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey("cep.notify.sms.login"))
				.append("&password=")
				.append(configurationManager.getKey("cep.notify.sms.password"))
				.append("&action=send_sms").append("&number=").append(receiver)
				.append("&message=").append(message).toString();
		mockServer = MockRestServiceServer.createServer(restTemplate);
		mockServer
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		sendSMSTask.setRestTemplate(restTemplate);
	}

	@After
	public void tearDown() {
		identityService.setAuthenticatedUserId(null);
		if (wiser != null)
			wiser.stop();
	}
}
