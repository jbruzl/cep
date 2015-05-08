import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
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

import cz.muni.fi.cep.activiti.notification.messages.FileRadioMessage;
import cz.muni.fi.cep.activiti.notification.tasks.BroadcastTask;
import cz.muni.fi.cep.activiti.notification.tasks.SendSMSTask;
import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.api.services.users.IdentityService;

/**
 * Test class for InformCitizens.bpmn process.
 * 
 * @author Jan Bruzl
 */
public class InformCitizensTest extends ActivitiBasicTest {
	private Wiser wiser;

	private String testAudioFile = "src/test/resources/test.wav";

	@Autowired
	private IdentityService identityService;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private SendSMSTask sendSMSTask;

	@Autowired
	private BroadcastTask broadcastTask;

	@Autowired
	private ConfigurationManager configurationManager;

	private String message = "Hello_World";
	private String receiver = "728484615";
	private String publisherCode = "001";
	private MockRestServiceServer mockServerSMS, mockServerRadio;

	private final String key = "informCitizens";

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/InformCitizens.bpmn" })
	public void deploymentTest() {
		assertNotNull("Expecting deployed InformCitizens process",
				repositoryService.createProcessDefinitionQuery()
						.processDefinitionKey(key).list().size() > 0);
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/InformCitizens.bpmn" })
	public void radioPathOnlyOkTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);

		boolean radio = true;
		boolean sms = false;
		boolean email = false;

		params.put("sendRadio", radio);
		params.put("sendSMS", sms);
		params.put("sendEmail", email);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key,
				params);
		assertNotNull(pi);

		List<Task> tasks = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals("Expected only one user task, obtained " + tasks.size()
				+ ",  " + tasks, 1, tasks.size());
		Task uploadRadioMessageTask = tasks.get(0);

		assertEquals("Expected user task uploadRadioMessage",
				"uploadRadioMessage",
				uploadRadioMessageTask.getTaskDefinitionKey());

		FileRadioMessage rm = new FileRadioMessage();
		rm.setAudioFileName(testAudioFile);
		rm.setAuthor("test");

		HashMap<String, Object> taskVariables = new HashMap<>();
		taskVariables.put("radioMessage", rm);
		taskService.complete(uploadRadioMessageTask.getId(), taskVariables);

		mockServerRadio.verify();

		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().processInstanceId(pi.getId()).activityType("endEvent")
				.finished().list();
		assertEquals(1, haiList.size());
		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(0).getActivityId(), "citizensInformed",
				haiList.get(0).getActivityId());

	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/InformCitizens.bpmn" })
	public void radioPathOnlyNokTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);

		boolean radio = true;
		boolean sms = false;
		boolean email = false;

		params.put("sendRadio", radio);
		params.put("sendSMS", sms);
		params.put("sendEmail", email);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key,
				params);
		assertNotNull(pi);

		List<Task> tasks = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals("Expected only one user task, obtained " + tasks.size()
				+ ",  " + tasks, 1, tasks.size());
		Task uploadRadioMessageTask = tasks.get(0);

		assertEquals("Expected user task uploadRadioMessage",
				"uploadRadioMessage",
				uploadRadioMessageTask.getTaskDefinitionKey());

		FileRadioMessage rm = new FileRadioMessage();
		rm.setAudioFileName(testAudioFile);
		rm.setAuthor("test");

		taskService.complete(uploadRadioMessageTask.getId());

		tasks = taskService.createTaskQuery().processInstanceId(pi.getId())
				.list();
		assertEquals("Expected only one user task, obtained " + tasks.size()
				+ ",  " + tasks, 1, tasks.size());

		Task manualBroadcastTask = tasks.get(0);
		assertEquals("Expected user task manualRadioBroadcast",
				"manualRadioBroadcast",
				manualBroadcastTask.getTaskDefinitionKey());
		taskService.complete(manualBroadcastTask.getId());

		try {
			mockServerRadio.verify();
			fail("Radio server mock should throw exception");
		} catch (AssertionError ex) {

		}

		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().processInstanceId(pi.getId()).activityType("endEvent")
				.finished().list();
		assertEquals(1, haiList.size());
		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(0).getActivityId(), "citizensInformed",
				haiList.get(0).getActivityId());

	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/InformCitizens.bpmn" })
	public void smsPathOnlyOkTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);

		boolean radio = false;
		boolean sms = true;
		boolean email = false;

		params.put("sendRadio", radio);
		params.put("sendSMS", sms);
		params.put("sendEmail", email);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key,
				params);
		assertNotNull(pi);

		List<Task> tasks = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals("Expected only one user task, obtained " + tasks.size()
				+ ",  " + tasks, 1, tasks.size());
		Task createSMSMessageTask = tasks.get(0);

		assertEquals("Expected user task createSMSMessage", "createSMSMessage",
				createSMSMessageTask.getTaskDefinitionKey());

		String smsMessage = message;

		HashMap<String, Object> taskVariables = new HashMap<>();
		taskVariables.put("smsMessage", smsMessage);
		taskService.complete(createSMSMessageTask.getId(), taskVariables);

		mockServerSMS.verify();

		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().processInstanceId(pi.getId()).activityType("endEvent")
				.finished().list();
		assertEquals(1, haiList.size());
		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(0).getActivityId(), "citizensInformed",
				haiList.get(0).getActivityId());

	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/InformCitizens.bpmn" })
	public void smsPathOnlyNokTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);

		boolean radio = false;
		boolean sms = true;
		boolean email = false;

		params.put("sendRadio", radio);
		params.put("sendSMS", sms);
		params.put("sendEmail", email);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key,
				params);
		assertNotNull(pi);

		List<Task> tasks = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals("Expected only one user task, obtained " + tasks.size()
				+ ",  " + tasks, 1, tasks.size());
		Task createSMSMessageTask = tasks.get(0);

		assertEquals("Expected user task createSMSMessage", "createSMSMessage",
				createSMSMessageTask.getTaskDefinitionKey());

		HashMap<String, Object> taskVariables = new HashMap<>();
		taskService.complete(createSMSMessageTask.getId(), taskVariables);

		tasks = taskService.createTaskQuery().processInstanceId(pi.getId())
				.list();
		assertEquals("Expected only one user task, obtained " + tasks.size()
				+ ",  " + tasks, 1, tasks.size());

		Task manualSMSSendTask = tasks.get(0);
		assertEquals("Expected user task manualSMSSend", "manualSMSSend",
				manualSMSSendTask.getTaskDefinitionKey());

		TaskFormData taskFormData = formService
				.getTaskFormData(manualSMSSendTask.getId());
		List<FormProperty> formProperties = taskFormData.getFormProperties();

		assertEquals("Expected onyl one form property", 1,
				formProperties.size());
		assertEquals("Neodeslané SMS", formProperties.get(0).getName());
		assertTrue(formProperties.get(0).getValue().contains(receiver));

		taskService.complete(manualSMSSendTask.getId());

		try {
			mockServerSMS.verify();
			fail("SMS server mock should throw exception");
		} catch (AssertionError ex) {

		}

		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().processInstanceId(pi.getId()).activityType("endEvent")
				.finished().list();
		assertEquals(1, haiList.size());
		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(0).getActivityId(), "citizensInformed",
				haiList.get(0).getActivityId());

	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/InformCitizens.bpmn" })
	public void emailPathOnlyOkTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);

		boolean radio = false;
		boolean sms = false;
		boolean email = true;

		params.put("sendRadio", radio);
		params.put("sendSMS", sms);
		params.put("sendEmail", email);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key,
				params);
		assertNotNull(pi);

		List<Task> tasks = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals("Expected only one user task, obtained " + tasks.size()
				+ ",  " + tasks, 1, tasks.size());
		Task createSMSMessageTask = tasks.get(0);

		assertEquals("Expected user task createMailMessage",
				"createMailMessage",
				createSMSMessageTask.getTaskDefinitionKey());

		String emailMessage = message;

		HashMap<String, Object> taskVariables = new HashMap<>();
		taskVariables.put("emailMessage", emailMessage);
		taskService.complete(createSMSMessageTask.getId(), taskVariables);
		
		tasks = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals("Expected none user task, obtained " + tasks.size()
				+ ",  " + tasks, 0, tasks.size());

		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().processInstanceId(pi.getId()).activityType("endEvent")
				.finished().list();
		assertEquals(1, haiList.size());
		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(0).getActivityId(), "citizensInformed",
				haiList.get(0).getActivityId());

		List<WiserMessage> messages = wiser.getMessages();
		assertEquals(2, messages.size());

	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/InformCitizens.bpmn" })
	public void emailPathOnlyNokTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);

		boolean radio = false;
		boolean sms = false;
		boolean email = true;

		params.put("sendRadio", radio);
		params.put("sendSMS", sms);
		params.put("sendEmail", email);

		wiser.stop();

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key,
				params);
		assertNotNull(pi);

		List<Task> tasks = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals("Expected only one user task, obtained " + tasks.size()
				+ ",  " + tasks, 1, tasks.size());
		Task createSMSMessageTask = tasks.get(0);
		assertEquals("Expected user task createMailMessage",
				"createMailMessage",
				createSMSMessageTask.getTaskDefinitionKey());

		String emailMessage = message;

		HashMap<String, Object> taskVariables = new HashMap<>();
		taskVariables.put("emailMessage", emailMessage);
		taskService.complete(createSMSMessageTask.getId(), taskVariables);

		tasks = taskService.createTaskQuery().processInstanceId(pi.getId())
				.list();
		assertEquals("Expected only one user task, obtained " + tasks.size()
				+ ",  " + tasks, 1, tasks.size());

		Task manualSendEmailTask = tasks.get(0);
		assertEquals("Expected user task manualSendEmail", "manualSendEmail",
				manualSendEmailTask.getTaskDefinitionKey());

		taskService.complete(manualSendEmailTask.getId());

		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().processInstanceId(pi.getId()).activityType("endEvent")
				.finished().list();
		assertEquals(1, haiList.size());

		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(0).getActivityId(), "citizensInformed",
				haiList.get(0).getActivityId());

		List<WiserMessage> messages = wiser.getMessages();
		assertEquals(0, messages.size());

		wiser.start();

	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/InformCitizens.bpmn" })
	public void allPathOkTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);

		boolean radio = true;
		boolean sms = true;
		boolean email = true;

		params.put("sendRadio", radio);
		params.put("sendSMS", sms);
		params.put("sendEmail", email);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key,
				params);
		assertNotNull(pi);

		List<Task> tasks = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals("Expected three user tasks, obtained " + tasks.size()
				+ ",  " + tasks, 3, tasks.size());

		FileRadioMessage rm = new FileRadioMessage();
		rm.setAudioFileName(testAudioFile);
		rm.setAuthor("test");
		HashMap<String, Object> radioTaskVariables = new HashMap<>();
		radioTaskVariables.put("radioMessage", rm);

		String smsMessage = message;
		HashMap<String, Object> smsTaskVariables = new HashMap<>();
		smsTaskVariables.put("smsMessage", smsMessage);

		String emailMessage = message;
		HashMap<String, Object> emailTaskVariables = new HashMap<>();
		emailTaskVariables.put("emailMessage", emailMessage);

		for (Task t : tasks) {
			switch (t.getTaskDefinitionKey()) {
			default:
				fail("Unexpected task");
				break;
			case "createMailMessage":
				taskService.complete(t.getId(), emailTaskVariables);
				break;
			case "createSMSMessage":
				taskService.complete(t.getId(), smsTaskVariables);
				break;
			case "uploadRadioMessage":
				taskService.complete(t.getId(), radioTaskVariables);
				break;
			}
		}

		tasks = taskService.createTaskQuery().processInstanceId(pi.getId())
				.list();
		assertEquals("Expected 0 user task, obtained " + tasks.size() + ",  "
				+ tasks, 0, tasks.size());

		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().processInstanceId(pi.getId()).activityType("endEvent")
				.finished().list();
		assertEquals(1, haiList.size());
		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(0).getActivityId(), "citizensInformed",
				haiList.get(0).getActivityId());

		mockServerSMS.verify();
		mockServerRadio.verify();
		List<WiserMessage> messages = wiser.getMessages();
		assertEquals(2, messages.size());
	}

	
	@Before
	public void setUp() throws UnsupportedEncodingException {
		assertNotNull("Identity service null", identityService);
		assertNotNull("Subscription service null", subscriptionService);

		CepUser userEntity = new CepUser();
		userEntity.setFirstName("Jan");
		userEntity.setLastName("Bruzl");
		userEntity.setMail("notify@gmail.com");
		userEntity.setPhoneNumber(receiver);
		userEntity.setPassword("01234");

		CepUser userEntity2 = new CepUser();
		userEntity2.setFirstName("Karel");
		userEntity2.setLastName("Novak");
		userEntity2.setMail("novak@novak.cz");
		userEntity2.setPhoneNumber("+420728484615");
		userEntity2.setPassword("01234");

		if (identityService.getAllCepUsers().isEmpty()) {
			identityService.createUser(userEntity);
			identityService.createUser(userEntity2);

			identityService.setAuthenticatedUserId(userEntity.getMail());
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

		configurationManager.setKey("cep.sms.login", "login");
		configurationManager.setKey("cep.sms.password", "password");

		RestTemplate restTemplate = new RestTemplate();
		String requestUrl = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey("cep.sms.login"))
				.append("&password=")
				.append(configurationManager.getKey("cep.sms.password"))
				.append("&action=send_sms").append("&number=").append(receiver)
				.append("&message=").append(URLEncoder.encode(message, "UTF-8")).toString();
		mockServerSMS = MockRestServiceServer.createServer(restTemplate);
		mockServerSMS
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		sendSMSTask.setRestTemplate(restTemplate);

		configurationManager.setKey(BroadcastTask.broadcastUrlKey,
				"http://localhost:8080/broadcast");

		RestTemplate restTemplate2 = new RestTemplate();
		mockServerRadio = MockRestServiceServer.createServer(restTemplate2);
		mockServerRadio
				.expect(MockRestRequestMatchers.requestTo(configurationManager
						.getKey(BroadcastTask.broadcastUrlKey)))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		broadcastTask.setRestTemplate(restTemplate2);
	}

	@After
	public void tearDown() {
		identityService.setAuthenticatedUserId(null);
		if (wiser != null)
			wiser.stop();
	}
}
