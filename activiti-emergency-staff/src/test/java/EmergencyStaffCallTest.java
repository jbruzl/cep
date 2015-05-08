import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Job;
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

import cz.muni.fi.cep.activiti.notification.tasks.SendSMSTask;
import cz.muni.fi.cep.activiti.staffcall.tasks.CheckSMSResponses;
import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.api.services.users.IdentityService;

/**
 * Test class for Example.bpmn process.
 * 
 * @author Jan Bruzl
 *
 */
public class EmergencyStaffCallTest extends ActivitiBasicTest {

	@Autowired
	private IdentityService identityService;

	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private SendSMSTask sendSMSTask;

	@Autowired
	private CheckSMSResponses checkResponses;

	private String receiver = "+420728484615";
	private String publisherCode = "001";
	private MockRestServiceServer mockServerSMS, mockServerSMSResponse;

	private final String key = "emergencyStaffCall";
	private String message, meetingTime, meetingPlace;

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/EmergencyStaffCall.bpmn" })
	public void deploymentTest() {
		assertNotNull("Expecting deployed Example process", repositoryService
				.createProcessDefinitionQuery().processDefinitionKey(key)
				.list().size() > 0);
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/EmergencyStaffCall.bpmn" })
	public void okTest() {
		HashMap<String, Object> variables = new HashMap<>();
		variables.put("publisherCode", publisherCode);
		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key,
				variables);
		assertNotNull(pi);

		Task task = taskService.createTaskQuery().processInstanceId(pi.getId())
				.singleResult();
		assertNotNull(task);
		assertEquals("Vyplnění informací", task.getName());

		HashMap<String, Object> vars = new HashMap<>();
		vars.put("meetingTime", meetingTime);
		vars.put("meetingPlace", meetingPlace);

		taskService.complete(task.getId(), vars);

		// job query&execute
		Job singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());

		task = taskService.createTaskQuery().processInstanceId(pi.getId())
				.singleResult();
		assertNotNull(task);
		assertEquals("Oznámení výsledků svolání", task.getName());

		taskService.complete(task.getId());

		List<HistoricProcessInstance> hpi = historyService
				.createHistoricProcessInstanceQuery().finished()
				.processInstanceId(pi.getId()).list();
		assertEquals(1, hpi.size());

	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/EmergencyStaffCall.bpmn" })
	public void repeatTest() throws UnsupportedEncodingException {
		String requestUrlResponse = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey("cep.sms.login"))
				.append("&password=")
				.append(configurationManager.getKey("cep.sms.password"))
				.append("&action=inbox").append("&delete=1").toString();
		RestTemplate restTemplateResponse = new RestTemplate();
		mockServerSMSResponse = MockRestServiceServer
				.createServer(restTemplateResponse);
		mockServerSMSResponse
				.expect(MockRestRequestMatchers.requestTo(requestUrlResponse))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess(
								"<result></result>", MediaType.APPLICATION_XML));

		checkResponses.setRestTemplate(restTemplateResponse);

		HashMap<String, Object> variables = new HashMap<>();
		variables.put("publisherCode", publisherCode);
		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key,
				variables);
		assertNotNull(pi);

		Task task = taskService.createTaskQuery().processInstanceId(pi.getId())
				.singleResult();
		assertNotNull(task);
		assertEquals("Vyplnění informací", task.getName());

		HashMap<String, Object> vars = new HashMap<>();
		vars.put("meetingTime", meetingTime);
		vars.put("meetingPlace", meetingPlace);

		taskService.complete(task.getId(), vars);

		RestTemplate restTemplate = new RestTemplate();
		String requestUrl = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey("cep.sms.login"))
				.append("&password=")
				.append(configurationManager.getKey("cep.sms.password"))
				.append("&action=send_sms").append("&number=").append(receiver)
				.append("&message=")
				.append(URLEncoder.encode(message, "UTF-8")).toString();
		mockServerSMS = MockRestServiceServer.createServer(restTemplate);
		mockServerSMS
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		sendSMSTask.setRestTemplate(restTemplate);

		// job query&execute
		Job singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());

		restTemplateResponse = new RestTemplate();
		mockServerSMSResponse = MockRestServiceServer
				.createServer(restTemplateResponse);
		mockServerSMSResponse
				.expect(MockRestRequestMatchers.requestTo(requestUrlResponse))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess(
								"<result></result>", MediaType.APPLICATION_XML));

		checkResponses.setRestTemplate(restTemplateResponse);

		restTemplate = new RestTemplate();
		mockServerSMS = MockRestServiceServer.createServer(restTemplate);
		mockServerSMS
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		sendSMSTask.setRestTemplate(restTemplate);

		singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());

		restTemplateResponse = new RestTemplate();
		mockServerSMSResponse = MockRestServiceServer
				.createServer(restTemplateResponse);
		mockServerSMSResponse
				.expect(MockRestRequestMatchers.requestTo(requestUrlResponse))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess(
								"<result></result>", MediaType.APPLICATION_XML));

		checkResponses.setRestTemplate(restTemplateResponse);

		restTemplate = new RestTemplate();
		mockServerSMS = MockRestServiceServer.createServer(restTemplate);
		mockServerSMS
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		sendSMSTask.setRestTemplate(restTemplate);

		singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());

		task = taskService.createTaskQuery().processInstanceId(pi.getId())
				.singleResult();
		assertNotNull(task);
		assertEquals("Oznámení výsledků svolání", task.getName());

		taskService.complete(task.getId());

		List<HistoricProcessInstance> hpi = historyService
				.createHistoricProcessInstanceQuery().finished()
				.processInstanceId(pi.getId()).list();
		assertEquals(1, hpi.size());

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

		configurationManager.setKey("cep.sms.login", "login");
		configurationManager.setKey("cep.sms.password", "password");

		meetingPlace = "U kolouška";
		meetingTime = "8:00";
		message = "Byl svolán krizový štáb na místo: " + meetingPlace + " v "
				+ meetingTime;

		RestTemplate restTemplate = new RestTemplate();
		String requestUrl = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey("cep.sms.login"))
				.append("&password=")
				.append(configurationManager.getKey("cep.sms.password"))
				.append("&action=send_sms").append("&number=").append(receiver)
				.append("&message=")
				.append(URLEncoder.encode(message, "UTF-8")).toString();
		mockServerSMS = MockRestServiceServer.createServer(restTemplate);
		mockServerSMS
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		sendSMSTask.setRestTemplate(restTemplate);

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

		String requestUrlResponse = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey("cep.sms.login"))
				.append("&password=")
				.append(configurationManager.getKey("cep.sms.password"))
				.append("&action=inbox").append("&delete=1").toString();
		RestTemplate restTemplateResponse = new RestTemplate();
		mockServerSMSResponse = MockRestServiceServer
				.createServer(restTemplateResponse);
		mockServerSMSResponse
				.expect(MockRestRequestMatchers.requestTo(requestUrlResponse))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess(smsResponseXML,
								MediaType.APPLICATION_XML));
		checkResponses.setRestTemplate(restTemplateResponse);
	}

	@After
	public void tearDown() {
		identityService.setAuthenticatedUserId(null);
	}
}
