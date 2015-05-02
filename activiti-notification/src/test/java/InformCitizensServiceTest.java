import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import cz.muni.fi.cep.activiti.notification.service.InformCitizensHistoryService;
import cz.muni.fi.cep.activiti.notification.service.InformCitizensService;
import cz.muni.fi.cep.activiti.notification.tasks.BroadcastTask;
import cz.muni.fi.cep.activiti.notification.tasks.SendSMSTask;
import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.api.DTO.history.CepHistoryProcessInstance;
import cz.muni.fi.cep.api.form.CepFormData;
import cz.muni.fi.cep.api.form.CepFormProperty;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.api.services.users.IdentityService;

public class InformCitizensServiceTest extends ActivitiBasicTest {
	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private SendSMSTask sendSMSTask;

	@Autowired
	private InformCitizensService service;

	@Autowired
	private BroadcastTask broadcastTask;

	@Autowired
	private InformCitizensHistoryService informCitizensHistoryService;

	private final String broadcastUrlKey = "cep.radio.broadcast.url";
	private String message = "Hello_World";
	private String receiver = "728484615";
	private String publisherCode = "Informace";
	private MockRestServiceServer mockServerSMS, mockServerRadio;
	private Wiser wiser;

	@Test
	public void testStart() {
		FormData startForm = service.getStartForm();
		assertNotNull("Start form data souldn't be null", startForm);
		assertTrue("Start form data should be instance of CepFormData",
				startForm instanceof CepFormData);

		FormProperty checkboxEmail = null;
		FormProperty checkboxSMS = null;
		FormProperty checkboxRadio = null;
		for (FormProperty fp : startForm.getFormProperties()) {
			switch (fp.getId()) {
			case "sendRadio":
				checkboxRadio = fp;
				break;
			case "sendSMS":
				checkboxSMS = fp;
				break;
			case "sendEmail":
				checkboxEmail = fp;
				break;
			default:
				fail("Unexpected form property");
				break;
			}
		}
		assertNotNull("Email checkbox not found in form property",
				checkboxEmail);
		assertNotNull("SMS checkbox not found in form property", checkboxSMS);
		assertNotNull("Radio checkbox not found in form property",
				checkboxRadio);

		assertTrue("Form property should be instance of CepFormProperty",
				checkboxEmail instanceof CepFormProperty);
		assertTrue("Form property should be instance of CepFormProperty",
				checkboxSMS instanceof CepFormProperty);
		assertTrue("Form property should be instance of CepFormProperty",
				checkboxRadio instanceof CepFormProperty);

		((CepFormProperty) checkboxEmail).setInput(true);
		((CepFormProperty) checkboxSMS).setInput(true);
		((CepFormProperty) checkboxRadio).setInput(true);

		ProcessInstance pi = service.runProcess((CepFormData) startForm);
		assertNotNull("Process instance is null", pi);

		List<Task> tasks = service.getTasks(pi);
		assertEquals(3, tasks.size());

		for (Task t : tasks) {
			CepFormData taskForm = service.getTaskForm(t.getId());

			List<FormProperty> formProperties = taskForm.getFormProperties();
			assertEquals(1, formProperties.size());
			FormProperty formProperty = formProperties.get(0);

			switch (formProperty.getId()) {
			case "radioMessage":
				((CepFormProperty) formProperty)
						.setInput("src/test/resources/test.wav");
				break;
			case "smsMessage":
			case "emailMessage":
				((CepFormProperty) formProperty).setInput(message);
				break;
			}

			service.complete(t.getId(), taskForm);
		}

		tasks = service.getTasks(pi);
		assertEquals(0, tasks.size());
		
		CepHistoryProcessInstance detail = informCitizensHistoryService.getDetail(pi.getId());
		assertNotNull(detail);
		
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

		configurationManager.setKey(broadcastUrlKey,
				"http://localhost:8080/broadcast");

		RestTemplate restTemplate2 = new RestTemplate();
		mockServerRadio = MockRestServiceServer.createServer(restTemplate2);
		mockServerRadio
				.expect(MockRestRequestMatchers.requestTo(configurationManager
						.getKey(broadcastUrlKey)))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		broadcastTask.setRestTemplate(restTemplate2);

		List<GrantedAuthority> gaList = new ArrayList<>();
		gaList.add(new SimpleGrantedAuthority("mayor"));
		User user = new User("test", "test", true, true, true, true, gaList);

		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(user, "test"));
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
		if (wiser != null)
			wiser.stop();

	}

}
