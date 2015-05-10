import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import cz.muni.fi.cep.activiti.notification.tasks.SendSMSTask;
import cz.muni.fi.cep.activiti.staffcall.service.EmergencyStaffCallHistoryService;
import cz.muni.fi.cep.activiti.staffcall.service.EmergencyStaffCallService;
import cz.muni.fi.cep.activiti.staffcall.tasks.CheckSMSResponses;
import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.api.DTO.history.CepHistoryProcessInstance;
import cz.muni.fi.cep.api.form.CepFormData;
import cz.muni.fi.cep.api.form.CepFormProperty;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.api.services.users.IdentityService;

public class EmergencyStaffCallServiceTest extends ActivitiBasicTest {
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
	private String publisherCode = "Svolání krizového štábu";
	private MockRestServiceServer mockServerSMS, mockServerSMSResponse,
			mockServerSMSResponseFail;

	private String message, meetingTimeText, meetingPlaceText;
	private RestTemplate restTemplateResponseFail;

	@Autowired
	private EmergencyStaffCallService service;

	@Autowired
	private EmergencyStaffCallHistoryService historyService;

	@Test
	public void okTest() {
		CepFormData startForm = service.getStartForm();
		assertNotNull(startForm);
		assertNotNull(startForm.getFormProperties());
		assertEquals(2, startForm.getFormProperties().size());

		FormProperty meetingPlace = null;
		FormProperty meetingTime = null;

		for (FormProperty fp : startForm.getFormProperties()) {
			switch (fp.getId()) {
			case "meetingPlace":
				meetingPlace = fp;
				break;
			case "meetingTime":
				meetingTime = fp;
				break;
			default:
				fail("Unexpected form property");
				break;
			}
		}
		assertNotNull("Meeting place not found in form property", meetingPlace);
		assertNotNull("Meeting time not found in form property", meetingTime);

		assertTrue("Form property should be instance of CepFormProperty",
				meetingPlace instanceof CepFormProperty);
		assertTrue("Form property should be instance of CepFormProperty",
				meetingTime instanceof CepFormProperty);

		((CepFormProperty) meetingPlace).setInput(meetingPlaceText);
		((CepFormProperty) meetingTime).setInput(meetingTimeText);

		ProcessInstance pi = service.runProcess((CepFormData) startForm);
		assertNotNull("Process instance is null", pi);

		// because for tests job executioner is stopped
		Job singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());

		List<Task> availableTasks = service.getAvailableTasks(pi.getId());
		assertEquals(1, availableTasks.size());

		Task task = availableTasks.get(0);
		assertEquals("Oznámení výsledků svolání", task.getName());

		TaskFormData taskFormData = formService.getTaskFormData(task.getId());
		assertNotNull(taskFormData);
		assertNotNull(taskFormData.getFormProperties());
		assertEquals(3, taskFormData.getFormProperties().size());

		for (FormProperty fp : taskFormData.getFormProperties()) {
			switch (fp.getName()) {
			case "Odeslaná zpráva":
				assertEquals(message, (String) fp.getValue());
				break;
			case "Nepotvrzená čísla":
				assertEquals("[]", (String) fp.getValue());
				break;
			case "Potvrzená čísla":
				assertEquals("[" + receiver + "]", (String) fp.getValue());
				break;

			default:
				fail("Unknown form property: " + fp.getName());
				break;
			}
		}

		taskService.complete(task.getId());

		CepHistoryProcessInstance chpi = historyService.getDetail(pi.getId());
		assertNotNull(
				"Historic process instance is null, process does not have recorded progress",
				chpi);
	}

	@Test
	public void noReponseTest() {
		checkResponses.setRestTemplate(restTemplateResponseFail);

		CepFormData startForm = service.getStartForm();
		assertNotNull(startForm);
		assertNotNull(startForm.getFormProperties());
		assertEquals(2, startForm.getFormProperties().size());

		FormProperty meetingPlace = null;
		FormProperty meetingTime = null;

		for (FormProperty fp : startForm.getFormProperties()) {
			switch (fp.getId()) {
			case "meetingPlace":
				meetingPlace = fp;
				break;
			case "meetingTime":
				meetingTime = fp;
				break;
			default:
				fail("Unexpected form property");
				break;
			}
		}
		assertNotNull("Meeting place not found in form property", meetingPlace);
		assertNotNull("Meeting time not found in form property", meetingTime);

		assertTrue("Form property should be instance of CepFormProperty",
				meetingPlace instanceof CepFormProperty);
		assertTrue("Form property should be instance of CepFormProperty",
				meetingTime instanceof CepFormProperty);

		((CepFormProperty) meetingPlace).setInput(meetingPlaceText);
		((CepFormProperty) meetingTime).setInput(meetingTimeText);

		ProcessInstance pi = service.runProcess((CepFormData) startForm);
		assertNotNull("Process instance is null", pi);

		// because for tests job executioner is stopped
		Job singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());
		
		singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());
		
		singleResultJob = managementService.createJobQuery()
				.processInstanceId(pi.getId()).singleResult();
		assertNotNull(singleResultJob);
		managementService.executeJob(singleResultJob.getId());

		List<Task> availableTasks = service.getAvailableTasks(pi.getId());
		assertEquals(1, availableTasks.size());

		Task task = availableTasks.get(0);
		assertEquals("Oznámení výsledků svolání", task.getName());

		TaskFormData taskFormData = formService.getTaskFormData(task.getId());
		assertNotNull(taskFormData);
		assertNotNull(taskFormData.getFormProperties());
		assertEquals(3, taskFormData.getFormProperties().size());

		for (FormProperty fp : taskFormData.getFormProperties()) {
			switch (fp.getName()) {
			case "Odeslaná zpráva":
				assertEquals(message, (String) fp.getValue());
				break;
			case "Nepotvrzená čísla":
				assertEquals("[" + receiver + "]", (String) fp.getValue());
				break;
			case "Potvrzená čísla":
				assertEquals("[]", (String) fp.getValue());
				break;

			default:
				fail("Unknown form property: " + fp.getName());
				break;
			}
		}

		taskService.complete(task.getId());

		CepHistoryProcessInstance chpi = historyService.getDetail(pi.getId());
		assertNotNull(
				"Historic process instance is null, process does not have recorded progress",
				chpi);
	}

	@Before
	public void setUp() throws UnsupportedEncodingException {
		assertNotNull("Identity service null", identityService);

		List<GrantedAuthority> gaList = new ArrayList<>();
		gaList.add(new SimpleGrantedAuthority("mayor"));
		User user = new User("test", "test", true, true, true, true, gaList);

		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(user, "test"));

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

		meetingPlaceText = "U kolouška";
		meetingTimeText = "8:00";
		message = "Byl svolán krizový štáb na místo: " + meetingPlaceText
				+ " v " + meetingTimeText;

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
		mockServerSMS
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
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

		restTemplateResponseFail = new RestTemplate();
		mockServerSMSResponseFail = MockRestServiceServer
				.createServer(restTemplateResponseFail);
		mockServerSMSResponseFail
				.expect(MockRestRequestMatchers.requestTo(requestUrlResponse))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess(
								"<result></result>", MediaType.APPLICATION_XML));
		mockServerSMSResponseFail
				.expect(MockRestRequestMatchers.requestTo(requestUrlResponse))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess(
								"<result></result>", MediaType.APPLICATION_XML));
		mockServerSMSResponseFail
				.expect(MockRestRequestMatchers.requestTo(requestUrlResponse))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess(
								"<result></result>", MediaType.APPLICATION_XML));

		checkResponses.setRestTemplate(restTemplateResponse);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
