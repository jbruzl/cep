import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import cz.muni.fi.cep.activiti.notification.service.NotifyHistoryService;
import cz.muni.fi.cep.activiti.notification.service.NotifyService;
import cz.muni.fi.cep.activiti.notification.tasks.SendSMSTask;
import cz.muni.fi.cep.api.DTO.CepHistoryProcessInstance;
import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.api.DTO.forms.CepFormData;
import cz.muni.fi.cep.api.DTO.forms.CepFormProperty;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.api.services.users.IdentityService;

public class NotifyServiceTest extends ActivitiBasicTest  {
	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private SendSMSTask sendSMSTask;

	@Autowired
	private NotifyService service;

	@Autowired
	private NotifyHistoryService notifyHistoryService;
	
	private String message = "Hello_World!";
	private String receiver = "728484615";
	private String publisherCode;
	private MockRestServiceServer mockServer;
	private Wiser wiser;

	@Test
	public void testStart() {
		FormData startForm = service.getStartForm();
		assertNotNull("Start form data souldn't be null", startForm);
		assertTrue("Start form data should be instance of CepFormData",
				startForm instanceof CepFormData);

		FormProperty messageFP = null;
		for (FormProperty fp : startForm.getFormProperties()) {
			if (fp.getId().equals("message"))
				messageFP = fp;
		}
		assertNotNull("Message not found in form property", messageFP);

		assertTrue("Form property should be instance of CepFormProperty",
				messageFP instanceof CepFormProperty);

		((CepFormProperty) messageFP).setInput(message);

		ProcessInstance pi = service.runProcess((CepFormData) startForm);
		assertNotNull("Process instance is null", pi);
		
		List<WiserMessage> messages = wiser.getMessages();
		assertEquals(2, messages.size());
		
		mockServer.verify();

		CepHistoryProcessInstance chpi = notifyHistoryService
				.getDetail(pi.getId());
		assertNotNull(
				"Historic process instance is null, process does not have recorded progress",
				chpi);
	}
	
	@Before
	public void setUp() {
		assertNotNull("Identity service null", identityService);
		assertNotNull("Subscription service null", subscriptionService);
		
		List<GrantedAuthority> gaList = new ArrayList<>();
        User user = new User("test", "test",
                true, true, true, true, gaList);

	    SecurityContextHolder.getContext().setAuthentication(
	        new UsernamePasswordAuthenticationToken(user, "test")
	    );
	    
	    publisherCode = service.getPublisherCode();

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
		SecurityContextHolder.clearContext();
		if (wiser != null)
			wiser.stop();
	}

}
