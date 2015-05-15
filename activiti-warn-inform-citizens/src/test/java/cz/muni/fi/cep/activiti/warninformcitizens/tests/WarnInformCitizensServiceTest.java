package cz.muni.fi.cep.activiti.warninformcitizens.tests;
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

import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
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

import cz.muni.fi.cep.activiti.notification.tasks.BroadcastTask;
import cz.muni.fi.cep.activiti.notification.tasks.SendSMSTask;
import cz.muni.fi.cep.activiti.staffcall.tasks.CheckSMSResponses;
import cz.muni.fi.cep.activiti.warninformcitizens.service.WarnInformCitizensHistoryService;
import cz.muni.fi.cep.activiti.warninformcitizens.service.WarnInformCitizensService;
import cz.muni.fi.cep.activiti.warninformcitizens.tasks.SirenTask;
import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.api.DTO.history.CepHistoryProcessInstance;
import cz.muni.fi.cep.api.form.CepFormData;
import cz.muni.fi.cep.api.form.CepFormProperty;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.api.services.users.IdentityService;
/**
 * {@link WarnInformCitizensService}'s test
 * @author Jan Bruzl
 */
public class WarnInformCitizensServiceTest extends ActivitiBasicTest {
	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private WarnInformCitizensService service;

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

	@Autowired
	private WarnInformCitizensHistoryService historyService;

	private String smsMessage = "test";
	private String receiver = "+420728484615";
	private String publisherCode = "Varování škol, samot a dalších subjektů";
	private MockRestServiceServer mockServerSMS, mockServerSMSNok,
			mockServerRadio, mockServerRadioFail, mockServerSMSResponseFail,
			mockServerSMSResponse, mockServerSiren, mockServerSirenFail;
	private RestTemplate restSMSOkTemplate, restSMSNokTemplate,
			restRadioOkTemplate, restTemplateResponse,
			restTemplateResponseFail, restRadioNokTemplate,
			restSirenOkTemplate, restSirenNokTemplate;

	@Test
	public void okTest() {
		FormData startForm = service.getStartForm();
		assertNotNull("Start form data souldn't be null", startForm);
		assertTrue("Start form data should be instance of CepFormData",
				startForm instanceof CepFormData);
		assertEquals(0, startForm.getFormProperties().size());

		ProcessInstance pi = service.runProcess((CepFormData) startForm);
		assertNotNull("Process instance is null", pi);

		List<Task> availableTasks = service.getAvailableTasks();
		assertEquals(2, availableTasks.size());

		for (Task t : availableTasks) {
			CepFormData taskForm = service.getTaskForm(t.getId());

			for (FormProperty fp : taskForm.getFormProperties()) {
				if (!(fp instanceof CepFormProperty))
					fail("Not used recomended form property class");

				CepFormProperty cfp = (CepFormProperty) fp;
				switch (cfp.getName()) {
				default:
					fail("Unknown form property");
					break;
				case "SMS":
					cfp.setInput(smsMessage);
					break;
				case "Ověřovat odpověď?":
					cfp.setInput(false);
					break;
				case "Rozhlasová zpráva":
					cfp.setInput("src/test/resources/test.wav");
					break;
				}
			}

			service.complete(t.getId(), taskForm);
		}

		availableTasks = service.getAvailableTasks();
		assertEquals(1, availableTasks.size());

		for (Task t : availableTasks) {
			CepFormData taskForm = service.getTaskForm(t.getId());

			for (FormProperty fp : taskForm.getFormProperties()) {
				if (!(fp instanceof CepFormProperty))
					fail("Not used recomended form property class");

				CepFormProperty cfp = (CepFormProperty) fp;
				switch (cfp.getName()) {
				default:
					fail("Unknown form property: " + cfp.getName());
					break;
				case "Potvrzené sms":
				case "Nepotvrzeno":
					break;
				}
			}

			service.complete(t.getId(), taskForm);
		}

		CepHistoryProcessInstance chpi = historyService.getDetail(pi.getId());
		assertNotNull(
				"Historic process instance is null, process does not have recorded progress",
				chpi);
	}

	@Test
	public void failTest() {
		sirenTask.setRestTemplate(restSirenNokTemplate);
		broadcastTask.setRestTemplate(restRadioNokTemplate);
		sendSMSTask.setRestTemplate(restSMSNokTemplate);

		FormData startForm = service.getStartForm();
		assertNotNull("Start form data souldn't be null", startForm);
		assertTrue("Start form data should be instance of CepFormData",
				startForm instanceof CepFormData);
		assertEquals(0, startForm.getFormProperties().size());

		ProcessInstance pi = service.runProcess((CepFormData) startForm);
		assertNotNull("Process instance is null", pi);

		List<Task> availableTasks = service.getAvailableTasks();
		assertEquals(3, availableTasks.size());

		for (Task t : availableTasks) {
			CepFormData taskForm = service.getTaskForm(t.getId());

			for (FormProperty fp : taskForm.getFormProperties()) {
				if (!(fp instanceof CepFormProperty))
					fail("Not used recomended form property class");

				CepFormProperty cfp = (CepFormProperty) fp;
				switch (cfp.getName()) {
				default:
					fail("Unknown form property: " + cfp.getName());
					break;
				case "SMS":
					cfp.setInput(smsMessage);
					break;
				case "Ověřovat odpověď?":
					cfp.setInput(false);
					break;
				case "Rozhlasová zpráva":
					cfp.setInput("src/test/resources/test.wav");
					break;
				case "Chyba sirény":
					break;
				}
			}

			service.complete(t.getId(), taskForm);
		}

		availableTasks = service.getAvailableTasks();
		assertEquals(2, availableTasks.size());

		for (Task t : availableTasks) {
			CepFormData taskForm = service.getTaskForm(t.getId());

			for (FormProperty fp : taskForm.getFormProperties()) {
				if (!(fp instanceof CepFormProperty))
					fail("Not used recomended form property class");

				CepFormProperty cfp = (CepFormProperty) fp;
				switch (cfp.getName()) {
				default:
					fail("Unknown form property: "+ cfp.getName());
					break;
				case "Potvrzené sms":
				case "Nepotvrzeno":
				case "Chyba rozhlasu":
				case "Chyba sirény":
					break;
				}
			}

			service.complete(t.getId(), taskForm);
		}

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

		CepUser user1 = new CepUser();
		user1.setFirstName("Jan");
		user1.setLastName("Bruzl");
		user1.setMail("notify@gmail.com");

		user1.setPhoneNumber(receiver);
		user1.setPassword("01234");

		if (identityService.getAllCepUsers().isEmpty()) {
			identityService.createUser(user1);

			identityService.setAuthenticatedUserId(user1.getMail());
		}

		subscriptionService.register(publisherCode);
		if (subscriptionService.getUserSubscribers(publisherCode, null)
				.isEmpty()) {
			subscriptionService.subscribeUser(user1, publisherCode,
					ContactType.SMS);
		}

		String requestUrl;

		configurationManager.setKey(SendSMSTask.loginKey, "login");
		configurationManager.setKey(SendSMSTask.passwordKey, "password");

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

		broadcastTask.setRestTemplate(restRadioOkTemplate);
		checkSMSResponses.setRestTemplate(restTemplateResponse);

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

		mockServerSMSNok = MockRestServiceServer.createServer(restSMSNokTemplate);
		mockServerSMSNok
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withBadRequest());

		initSMSSendMock();

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

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
