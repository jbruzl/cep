package cz.muni.fi.cep.activiti.warninformcitizens.tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import cz.muni.fi.cep.activiti.notification.messages.FileRadioMessage;
import cz.muni.fi.cep.activiti.notification.messages.RadioMessage;
import cz.muni.fi.cep.activiti.notification.tasks.BroadcastTask;
import cz.muni.fi.cep.activiti.notification.tasks.SendSMSTask;
import cz.muni.fi.cep.activiti.staffcall.tasks.CheckSMSResponses;
import cz.muni.fi.cep.activiti.warninformcitizens.tasks.SirenTask;
import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.api.services.users.IdentityService;

/**
 * Test class for Warn and inform citizens.bpmn process.
 * 
 * @author Jan Bruzl
 *
 */
public class WarnInformCitizensTest extends ActivitiBasicTest {

	@Autowired
	private IdentityService identityService;

	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private SendSMSTask sendSMSTask;

	@Autowired
	private BroadcastTask broadcastTask;

	@Autowired
	private SirenTask sirenTask;

	@Autowired
	private CheckSMSResponses checkSMSResponses;

	private String publisherCode = "001";
	private String smsMessage = "test sms";
	private String testAudioFile = "src/test/resources/test.wav";
	private RadioMessage rm;
	private String receiver = "+420728484615";
	private MockRestServiceServer mockServerSMS, mockServerSMSNok,
			mockServerRadio, mockServerRadioFail, mockServerSMSResponseFail,
			mockServerSMSResponse, mockServerSiren, mockServerSirenFail;
	private RestTemplate restSMSOkTemplate, restSMSNokTemplate,
			restRadioOkTemplate, restTemplateResponse,
			restTemplateResponseFail, restRadioNokTemplate,
			restSirenOkTemplate, restSirenNokTemplate;

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warn and inform citizens.bpmn" })
	public void deploymentTest() {
		assertNotNull("Expecting deployed Warn and inform citizens process",
				repositoryService.createProcessDefinitionQuery()
						.processDefinitionKey("warnInformCitizens").list()
						.size() > 0);
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warn and inform citizens.bpmn" })
	public void OkNoCheckTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);

		sendSMSTask.setRestTemplate(restSMSOkTemplate);
		broadcastTask.setRestTemplate(restRadioOkTemplate);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				"warnInformCitizens", params);
		assertNotNull(pi);

		List<Task> taskList = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals(2, taskList.size());

		HashMap<String, Object> smsTask = new HashMap<>();
		HashMap<String, Object> radioTask = new HashMap<>();

		smsTask.put("smsMessage", smsMessage);
		smsTask.put("checkResponse", false);
		radioTask.put("radioMessage", rm);

		for (Task t : taskList) {
			switch (t.getTaskDefinitionKey()) {
			default:
				fail("unknown task");
				break;
			case "insertMessage":
				taskService.complete(t.getId(), smsTask);
				break;
			case "insertRadioMessage":
				taskService.complete(t.getId(), radioTask);
				break;

			}
		}

		taskList = taskService.createTaskQuery().processInstanceId(pi.getId())
				.list();
		assertEquals(1, taskList.size());

		Task t = taskList.get(0);
		taskService.complete(t.getId());

		HistoricProcessInstance hpi = historyService
				.createHistoricProcessInstanceQuery().finished()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(hpi);

		mockServerRadio.verify();
		mockServerSMS.verify();
		mockServerSiren.verify();
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warn and inform citizens.bpmn" })
	public void OkCheckTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);

		initSMSSendMock();
		broadcastTask.setRestTemplate(restRadioOkTemplate);
		checkSMSResponses.setRestTemplate(restTemplateResponse);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				"warnInformCitizens", params);
		assertNotNull(pi);

		List<Task> taskList = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals(2, taskList.size());

		HashMap<String, Object> smsTask = new HashMap<>();
		HashMap<String, Object> radioTask = new HashMap<>();

		smsTask.put("smsMessage", smsMessage);
		smsTask.put("checkResponse", true);
		radioTask.put("radioMessage", rm);

		for (Task t : taskList) {
			switch (t.getTaskDefinitionKey()) {
			default:
				fail("unknown task");
				break;
			case "insertMessage":
				taskService.complete(t.getId(), smsTask);
				break;
			case "insertRadioMessage":
				taskService.complete(t.getId(), radioTask);
				break;

			}
		}

		taskList = taskService.createTaskQuery().processInstanceId(pi.getId())
				.list();
		assertEquals(0, taskList.size());

		Job singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());
		mockServerSMSResponse.verify();
		mockServerSMS.verify();

		taskList = taskService.createTaskQuery().processInstanceId(pi.getId())
				.list();
		assertEquals(1, taskList.size());

		Task t = taskList.get(0);
		taskService.complete(t.getId());

		HistoricProcessInstance hpi = historyService
				.createHistoricProcessInstanceQuery().finished()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(hpi);

		mockServerRadio.verify();
		mockServerSiren.verify();
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warn and inform citizens.bpmn" })
	public void OkCheckRepeatTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);

		initSMSResponseFailMock();
		broadcastTask.setRestTemplate(restRadioOkTemplate);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				"warnInformCitizens", params);
		assertNotNull(pi);

		List<Task> taskList = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals(2, taskList.size());

		HashMap<String, Object> smsTask = new HashMap<>();
		HashMap<String, Object> radioTask = new HashMap<>();

		smsTask.put("smsMessage", smsMessage);
		smsTask.put("checkResponse", true);
		radioTask.put("radioMessage", rm);

		for (Task t : taskList) {
			switch (t.getTaskDefinitionKey()) {
			default:
				fail("unknown task");
				break;
			case "insertMessage":
				taskService.complete(t.getId(), smsTask);
				break;
			case "insertRadioMessage":
				taskService.complete(t.getId(), radioTask);
				break;

			}
		}

		taskList = taskService.createTaskQuery().processInstanceId(pi.getId())
				.list();
		assertEquals(0, taskList.size());

		initSMSSendMock();

		Job singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());
		mockServerSMSResponseFail.verify();
		mockServerSMS.verify();
		initSMSResponseFailMock();
		initSMSSendMock();

		singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());
		mockServerSMSResponseFail.verify();
		mockServerSMS.verify();
		checkSMSResponses.setRestTemplate(restTemplateResponse);
		initSMSSendMock();

		singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());
		mockServerSMSResponse.verify();

		taskList = taskService.createTaskQuery().processInstanceId(pi.getId())
				.list();
		assertEquals(1, taskList.size());

		Task t = taskList.get(0);
		taskService.complete(t.getId());

		HistoricProcessInstance hpi = historyService
				.createHistoricProcessInstanceQuery().finished()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(hpi);

		mockServerRadio.verify();
		mockServerSiren.verify();
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warn and inform citizens.bpmn" })
	public void OkCheckRepeatFailTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);

		initSMSResponseFailMock();
		broadcastTask.setRestTemplate(restRadioOkTemplate);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				"warnInformCitizens", params);
		assertNotNull(pi);

		List<Task> taskList = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals(2, taskList.size());

		HashMap<String, Object> smsTask = new HashMap<>();
		HashMap<String, Object> radioTask = new HashMap<>();

		smsTask.put("smsMessage", smsMessage);
		smsTask.put("checkResponse", true);
		radioTask.put("radioMessage", rm);

		for (Task t : taskList) {
			switch (t.getTaskDefinitionKey()) {
			default:
				fail("unknown task");
				break;
			case "insertMessage":
				taskService.complete(t.getId(), smsTask);
				break;
			case "insertRadioMessage":
				taskService.complete(t.getId(), radioTask);
				break;

			}
		}

		taskList = taskService.createTaskQuery().processInstanceId(pi.getId())
				.list();
		assertEquals(0, taskList.size());

		initSMSSendMock();

		Job singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());
		mockServerSMSResponseFail.verify();
		mockServerSMS.verify();
		initSMSResponseFailMock();
		initSMSSendMock();

		singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());
		mockServerSMSResponseFail.verify();
		mockServerSMS.verify();
		initSMSResponseFailMock();
		initSMSSendMock();

		singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());
		mockServerSMSResponseFail.verify();

		taskList = taskService.createTaskQuery().processInstanceId(pi.getId())
				.list();
		assertEquals(1, taskList.size());

		Task t = taskList.get(0);
		taskService.complete(t.getId());

		HistoricProcessInstance hpi = historyService
				.createHistoricProcessInstanceQuery().finished()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(hpi);

		mockServerRadio.verify();
		mockServerSiren.verify();
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warn and inform citizens.bpmn" })
	public void OkSMSFailTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);

		sendSMSTask.setRestTemplate(restSMSNokTemplate);
		broadcastTask.setRestTemplate(restRadioOkTemplate);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				"warnInformCitizens", params);
		assertNotNull(pi);

		List<Task> taskList = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals(2, taskList.size());

		HashMap<String, Object> smsTask = new HashMap<>();
		HashMap<String, Object> radioTask = new HashMap<>();

		smsTask.put("smsMessage", smsMessage);
		smsTask.put("checkResponse", false);
		radioTask.put("radioMessage", rm);

		for (Task t : taskList) {
			switch (t.getTaskDefinitionKey()) {
			default:
				fail("unknown task");
				break;
			case "insertMessage":
				taskService.complete(t.getId(), smsTask);
				break;
			case "insertRadioMessage":
				taskService.complete(t.getId(), radioTask);
				break;

			}
		}

		taskList = taskService.createTaskQuery().processInstanceId(pi.getId())
				.list();
		assertEquals(1, taskList.size());

		Task t = taskList.get(0);
		taskService.complete(t.getId());

		HistoricProcessInstance hpi = historyService
				.createHistoricProcessInstanceQuery().finished()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(hpi);

		mockServerRadio.verify();
		mockServerSMSNok.verify();
		mockServerSiren.verify();
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warn and inform citizens.bpmn" })
	public void OkSirenFailTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);

		sendSMSTask.setRestTemplate(restSMSOkTemplate);
		broadcastTask.setRestTemplate(restRadioOkTemplate);
		sirenTask.setRestTemplate(restSirenNokTemplate);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				"warnInformCitizens", params);
		assertNotNull(pi);

		List<Task> taskList = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals(3, taskList.size());

		HashMap<String, Object> smsTask = new HashMap<>();
		HashMap<String, Object> radioTask = new HashMap<>();

		smsTask.put("smsMessage", smsMessage);
		smsTask.put("checkResponse", false);
		radioTask.put("radioMessage", rm);

		for (Task t : taskList) {
			switch (t.getTaskDefinitionKey()) {
			default:
				fail("unknown task");
				break;
			case "insertMessage":
				taskService.complete(t.getId(), smsTask);
				break;
			case "insertRadioMessage":
				taskService.complete(t.getId(), radioTask);
				break;
			case "manualSirenStart":
				taskService.complete(t.getId());
				break;
			}
		}

		taskList = taskService.createTaskQuery().processInstanceId(pi.getId())
				.list();
		assertEquals(1, taskList.size());

		for (Task t : taskList) {
			switch (t.getTaskDefinitionKey()) {
			default:
				fail("unknown task");
				break;
			
			case "informSMSResult":
				taskService.complete(t.getId());
				break;
			}
		}

		HistoricProcessInstance hpi = historyService
				.createHistoricProcessInstanceQuery().finished()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(hpi);

		mockServerRadio.verify();
		mockServerSMS.verify();
		mockServerSirenFail.verify();
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warn and inform citizens.bpmn" })
	public void OkRadioFailTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);

		sendSMSTask.setRestTemplate(restSMSOkTemplate);
		broadcastTask.setRestTemplate(restRadioNokTemplate);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				"warnInformCitizens", params);
		assertNotNull(pi);

		List<Task> taskList = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals(2, taskList.size());

		HashMap<String, Object> smsTask = new HashMap<>();
		HashMap<String, Object> radioTask = new HashMap<>();

		smsTask.put("smsMessage", smsMessage);
		smsTask.put("checkResponse", false);
		radioTask.put("radioMessage", rm);

		for (Task t : taskList) {
			switch (t.getTaskDefinitionKey()) {
			default:
				fail("unknown task");
				break;
			case "insertMessage":
				taskService.complete(t.getId(), smsTask);
				break;
			case "insertRadioMessage":
				taskService.complete(t.getId(), radioTask);
				break;

			}
		}

		taskList = taskService.createTaskQuery().processInstanceId(pi.getId())
				.list();
		assertEquals(2, taskList.size());

		for (Task t : taskList) {
			switch (t.getTaskDefinitionKey()) {
			default:
				fail("unknown task");
				break;
			case "manualRadioMessage":
			case "informSMSResult":
				taskService.complete(t.getId());
				break;
			}
		}

		HistoricProcessInstance hpi = historyService
				.createHistoricProcessInstanceQuery().finished()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(hpi);

		mockServerRadioFail.verify();
		mockServerSMS.verify();
		mockServerSiren.verify();
	}

	@Before
	public void setUp() throws UnsupportedEncodingException {
		FileRadioMessage frm = new FileRadioMessage();
		frm.setAuthor("");
		frm.setRecordDate(Calendar.getInstance());
		frm.setAudioFileName(testAudioFile);
		rm = frm;

		assertNotNull("Identity service null", identityService);
		assertNotNull("Subscription service null", subscriptionService);

		CepUser user = new CepUser();
		user.setFirstName("Jan");
		user.setLastName("Bruzl");
		user.setMail("notify@gmail.com");

		user.setPhoneNumber(receiver);
		user.setPassword("01234");

		if (identityService.getAllCepUsers().isEmpty()) {
			identityService.createUser(user);

			identityService.setAuthenticatedUserId(user.getMail());
		}

		subscriptionService.register(publisherCode);
		if (subscriptionService.getUserSubscribers(publisherCode, null)
				.isEmpty()) {
			subscriptionService.subscribeUser(user, publisherCode,
					ContactType.SMS);
		}

		configurationManager.setKey(SendSMSTask.loginKey, "login");
		configurationManager.setKey(SendSMSTask.passwordKey, "password");

		String requestUrl;
		initSMSSendMock();

		restSMSNokTemplate = new RestTemplate();
		requestUrl = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey(SendSMSTask.loginKey))
				.append("&password=")
				.append(configurationManager.getKey(SendSMSTask.passwordKey))
				.append("&action=send_sms").append("&number=").append(receiver)
				.append("&message=")
				.append(URLEncoder.encode(smsMessage, "UTF-8")).toString();
		mockServerSMSNok = MockRestServiceServer
				.createServer(restSMSNokTemplate);
		mockServerSMSNok.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(MockRestResponseCreators.withBadRequest());

		
		restRadioOkTemplate = new RestTemplate();
		mockServerRadio = MockRestServiceServer
				.createServer(restRadioOkTemplate);
		mockServerRadio
				.expect(MockRestRequestMatchers.requestTo(configurationManager
						.getKey(BroadcastTask.broadcastUrlKey)))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		
		restRadioNokTemplate = new RestTemplate();
		mockServerRadioFail = MockRestServiceServer
				.createServer(restRadioNokTemplate);
		mockServerRadioFail
				.expect(MockRestRequestMatchers.requestTo(configurationManager
						.getKey(BroadcastTask.broadcastUrlKey)))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andRespond(MockRestResponseCreators.withBadRequest());

		
		restSirenOkTemplate = new RestTemplate();
		mockServerSiren = MockRestServiceServer
				.createServer(restSirenOkTemplate);
		mockServerSiren
				.expect(MockRestRequestMatchers.requestTo(configurationManager
						.getKey(BroadcastTask.broadcastUrlKey)))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		sirenTask.setRestTemplate(restSirenOkTemplate);
		sirenTask.setAudioFileName("src/test/resources/zkouska.wav");

		restSirenNokTemplate = new RestTemplate();
		mockServerSirenFail = MockRestServiceServer
				.createServer(restSirenNokTemplate);
		mockServerSirenFail
				.expect(MockRestRequestMatchers.requestTo(configurationManager
						.getKey(BroadcastTask.broadcastUrlKey)))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andRespond(MockRestResponseCreators.withBadRequest());

		
		
		InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream("response.xml");

		StringBuilder sbXMLResponse = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream, "UTF-8"))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				sbXMLResponse.append(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String smsResponseXML = sbXMLResponse.toString();

		requestUrl = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey(CheckSMSResponses.loginKey))
				.append("&password=")
				.append(configurationManager
						.getKey(CheckSMSResponses.passwordKey))
				.append("&action=inbox").append("&delete=1").toString();
		restTemplateResponse = new RestTemplate();
		mockServerSMSResponse = MockRestServiceServer
				.createServer(restTemplateResponse);
		mockServerSMSResponse
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess(smsResponseXML,
								MediaType.APPLICATION_XML));

	}

	protected void initSMSSendMock() {
		restSMSOkTemplate = new RestTemplate();
		String requestUrl = null;
		try {
			requestUrl = new StringBuilder()
					.append("http://api.smsbrana.cz/smsconnect/http.php")
					.append("?login=")
					.append(configurationManager.getKey(SendSMSTask.loginKey))
					.append("&password=")
					.append(configurationManager
							.getKey(SendSMSTask.passwordKey))
					.append("&action=send_sms").append("&number=")
					.append(receiver).append("&message=")
					.append(URLEncoder.encode(smsMessage, "UTF-8")).toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		mockServerSMS = MockRestServiceServer.createServer(restSMSOkTemplate);
		mockServerSMS
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		sendSMSTask.setRestTemplate(restSMSOkTemplate);
	}

	protected void initSMSResponseFailMock() {
		String requestUrl = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey(CheckSMSResponses.loginKey))
				.append("&password=")
				.append(configurationManager
						.getKey(CheckSMSResponses.passwordKey))
				.append("&action=inbox").append("&delete=1").toString();
		restTemplateResponseFail = new RestTemplate();
		mockServerSMSResponseFail = MockRestServiceServer
				.createServer(restTemplateResponseFail);
		mockServerSMSResponseFail
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess(
								"<result></result>", MediaType.APPLICATION_XML));
		checkSMSResponses.setRestTemplate(restTemplateResponseFail);
	}
}
