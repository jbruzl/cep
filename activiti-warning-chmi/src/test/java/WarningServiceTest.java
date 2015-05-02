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

import javax.xml.transform.stream.StreamSource;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
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

import cz.muni.fi.cep.activiti.notification.tasks.SendSMSTask;
import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.api.form.CepFormData;
import cz.muni.fi.cep.api.form.CepFormProperty;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.api.services.users.IdentityService;
import cz.muni.fi.cep.warning.chmi.WeatherReportRegister;
import cz.muni.fi.cep.warning.chmi.report.Country;
import cz.muni.fi.cep.warning.chmi.report.Region;
import cz.muni.fi.cep.warning.chmi.report.Report;
import cz.muni.fi.cep.warning.chmi.service.WarningService;
import cz.muni.fi.cep.warning.chmi.tasks.EvaluateWarningReport;
import cz.muni.fi.cep.warning.chmi.tasks.ObtainWeatherReport;

/**
 * 
 * @author Jan Bruzl
 *
 */
public class WarningServiceTest extends ActivitiBasicTest {
	private Wiser wiser;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private WarningService warningService;

	private final String publisherCode = "Varování";

	private MockRestServiceServer mockServerOk, mockServerNok, mockServerSMSOk,
			mockServerSMSNok, mockServerSMSOkNok;
	private RestTemplate restTemplateOk, restTemplateNok, restTemplateSMSOk,
			restTemplateSMSNok, restTemplateSMSOkNok;

	@Autowired
	private SendSMSTask sendSMSTask;

	@Autowired
	private ObtainWeatherReport obtainWeatherReport;

	@Autowired
	private EvaluateWarningReport evaluateWarningReport;

	@Autowired
	private Jaxb2Marshaller weatherReportMarshaller;

	@Test
	public void testStart() {
		WeatherReportRegister wrr = new WeatherReportRegister();

		Report okReport = (Report) weatherReportMarshaller
				.unmarshal(new StreamSource(getClass().getClassLoader()
						.getResourceAsStream("weatherOk.xml")));

		setAwarenessLevel(okReport);

		wrr.getReports().add(okReport);

		evaluateWarningReport.setWeatherReportRegistr(wrr);
		obtainWeatherReport.setRestTemplate(restTemplateOk);

		assertNotNull(warningService);

		CepFormData startForm = warningService.getStartForm();
		assertNotNull(startForm);
		assertNotNull(startForm.getFormProperties());
		assertEquals(0, startForm.getFormProperties().size());

		ProcessInstance pi = warningService.runProcess(null);
		assertNotNull(pi);

		List<Task> availableTasks = warningService
				.getAvailableTasks(pi.getId());
		assertEquals(1, availableTasks.size());

		Task task = availableTasks.get(0);
		assertEquals("Rozhodnutí o informování obyvatel", task.getName());

		CepFormData taskForm = warningService.getTaskForm(task.getId());
		assertNotNull(taskForm);
		assertNotNull(taskForm.getFormProperties());
		assertEquals(4, taskForm.getFormProperties().size());

		FormProperty checkboxEmail = null;
		FormProperty checkboxSMS = null;
		FormProperty checkboxRadio = null;
		FormProperty checkboxDecide = null;
		for (FormProperty fp : taskForm.getFormProperties()) {
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
			case "decideInformCitizens":
				checkboxDecide = fp;
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
		assertNotNull("Decide checkbox not found in form property",
				checkboxRadio);

		assertTrue("Form property should be instance of CepFormProperty",
				checkboxEmail instanceof CepFormProperty);
		assertTrue("Form property should be instance of CepFormProperty",
				checkboxSMS instanceof CepFormProperty);
		assertTrue("Form property should be instance of CepFormProperty",
				checkboxRadio instanceof CepFormProperty);
		assertTrue("Form property should be instance of CepFormProperty",
				checkboxDecide instanceof CepFormProperty);

		((CepFormProperty) checkboxEmail).setInput(false);
		((CepFormProperty) checkboxSMS).setInput(false);
		((CepFormProperty) checkboxRadio).setInput(false);
		((CepFormProperty) checkboxDecide).setInput(true);

		warningService.complete(task.getId(), taskForm);

		HistoricActivityInstance endActivity = historyService
				.createHistoricActivityInstanceQuery()
				.processInstanceId(pi.getId()).activityType("endEvent")
				.singleResult();
		assertNotNull(endActivity);
		assertEquals("citizensInformed", endActivity.getActivityId());
	}

	private void setAwarenessLevel(Report okReport) {
		String countryCode = configurationManager
				.getKey(EvaluateWarningReport.countryCodeKey);
		String regionCode = configurationManager
				.getKey(EvaluateWarningReport.regionCodeKey);
		
		Country currentCountry = null;
		Region currentRegion = null;

		for (Country c : okReport.getCountries()) {
			if (countryCode.equals(c.getCode()))
				currentCountry = c;
		}

		for (Region r : currentCountry.getRegions()) {
			if (regionCode.equals(r.getCode())) {
				currentRegion = r;
			}
		}

		currentRegion.setAwarenessLevelCode("1");
	}

	@Test
	public void dontInformCitizenTest() {
		WeatherReportRegister wrr = new WeatherReportRegister();

		Report okReport = (Report) weatherReportMarshaller
				.unmarshal(new StreamSource(getClass().getClassLoader()
						.getResourceAsStream("weatherOk.xml")));
		setAwarenessLevel(okReport);
		wrr.getReports().add(okReport);

		evaluateWarningReport.setWeatherReportRegistr(wrr);
		obtainWeatherReport.setRestTemplate(restTemplateOk);

		assertNotNull(warningService);

		CepFormData startForm = warningService.getStartForm();
		assertNotNull(startForm);
		assertNotNull(startForm.getFormProperties());
		assertEquals(0, startForm.getFormProperties().size());

		ProcessInstance pi = warningService.runProcess(null);
		assertNotNull(pi);

		List<Task> availableTasks = warningService
				.getAvailableTasks(pi.getId());
		assertEquals(1, availableTasks.size());

		Task task = availableTasks.get(0);
		assertEquals("Rozhodnutí o informování obyvatel", task.getName());

		CepFormData taskForm = warningService.getTaskForm(task.getId());
		assertNotNull(taskForm);
		assertNotNull(taskForm.getFormProperties());
		assertEquals(4, taskForm.getFormProperties().size());

		FormProperty checkboxEmail = null;
		FormProperty checkboxSMS = null;
		FormProperty checkboxRadio = null;
		FormProperty checkboxDecide = null;
		for (FormProperty fp : taskForm.getFormProperties()) {
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
			case "decideInformCitizens":
				checkboxDecide = fp;
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
		assertNotNull("Decide checkbox not found in form property",
				checkboxRadio);

		assertTrue("Form property should be instance of CepFormProperty",
				checkboxEmail instanceof CepFormProperty);
		assertTrue("Form property should be instance of CepFormProperty",
				checkboxSMS instanceof CepFormProperty);
		assertTrue("Form property should be instance of CepFormProperty",
				checkboxRadio instanceof CepFormProperty);
		assertTrue("Form property should be instance of CepFormProperty",
				checkboxDecide instanceof CepFormProperty);

		((CepFormProperty) checkboxEmail).setInput(false);
		((CepFormProperty) checkboxSMS).setInput(false);
		((CepFormProperty) checkboxRadio).setInput(false);
		((CepFormProperty) checkboxDecide).setInput(false);

		warningService.complete(task.getId(), taskForm);

		HistoricActivityInstance endActivity = historyService
				.createHistoricActivityInstanceQuery()
				.processInstanceId(pi.getId()).activityType("endEvent")
				.singleResult();
		assertNotNull(endActivity);
		assertEquals("declineInformCitizens", endActivity.getActivityId());
	}

	@Test
	public void testCHMUFailure() {
		WeatherReportRegister wrr = new WeatherReportRegister();

		Report okReport = (Report) weatherReportMarshaller
				.unmarshal(new StreamSource(getClass().getClassLoader()
						.getResourceAsStream("weatherOk.xml")));
		wrr.getReports().add(okReport);

		evaluateWarningReport.setWeatherReportRegistr(wrr);
		obtainWeatherReport.setRestTemplate(restTemplateNok);

		assertNotNull(warningService);

		CepFormData startForm = warningService.getStartForm();
		assertNotNull(startForm);
		assertNotNull(startForm.getFormProperties());
		assertEquals(0, startForm.getFormProperties().size());

		ProcessInstance pi = warningService.runProcess(null);
		assertNotNull(pi);

		List<Task> availableTasks = warningService
				.getAvailableTasks(pi.getId());
		assertEquals(1, availableTasks.size());

		Task task = availableTasks.get(0);
		assertEquals("Informování o selhání komunikace", task.getName());

		CepFormData taskForm = warningService.getTaskForm(task.getId());
		assertNotNull(taskForm);
		assertNotNull(taskForm.getFormProperties());
		assertEquals(0, taskForm.getFormProperties().size());

		warningService.complete(task.getId(), taskForm);

		HistoricActivityInstance endActivity = historyService
				.createHistoricActivityInstanceQuery()
				.processInstanceId(pi.getId()).activityType("endEvent")
				.singleResult();
		assertNotNull(endActivity);
		assertEquals("communicationFailure", endActivity.getActivityId());

	}

	@Before
	public void setUp() throws UnsupportedEncodingException {
		assertNotNull("Identity service null", identityService);
		assertNotNull("Subscription service null", subscriptionService);

		CepUser userEntity = new CepUser();
		userEntity.setFirstName("Jan");
		userEntity.setLastName("Bruzl");
		userEntity.setMail("notify@gmail.com");
		userEntity.setPhoneNumber("728484615");
		userEntity.setPassword("01234");

		CepUser userEntity2 = new CepUser();
		userEntity2.setFirstName("Karel");
		userEntity2.setLastName("Novak");
		userEntity2.setMail("novak@novak.cz");
		userEntity2.setPhoneNumber("123456789");
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

		InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream("weatherOk.xml");

		StringBuilder sbXMLWarning = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream, "UTF-8"))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				sbXMLWarning.append(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		String weatherTextXML = sbXMLWarning.toString();
		RestTemplate restTemplate = new RestTemplate();

		String requestUrl = "http://www.chmi.cz/files/portal/docs/meteo/om/zpravy/data/sivs_aktual.xml";
		configurationManager.setKey("cep.warning.chmi.url", requestUrl);

		mockServerOk = MockRestServiceServer.createServer(restTemplate);
		mockServerOk
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess(weatherTextXML,
								MediaType.APPLICATION_XML));
		restTemplateOk = restTemplate;

		RestTemplate restTemplateBadRequest = new RestTemplate();
		mockServerNok = MockRestServiceServer
				.createServer(restTemplateBadRequest);
		mockServerNok.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(MockRestResponseCreators.withBadRequest());
		restTemplateNok = restTemplateBadRequest;

		configurationManager.setKey("cep.sms.login", "login");
		configurationManager.setKey("cep.sms.password", "password");

		String smsOkMessage = "Na Vašem území byla vyhlášena meteo výstraha";
		String smsNokMessage = "Na Vašem území byla snížena meteo výstraha";

		restTemplateSMSOk = new RestTemplate();
		String requestUrlSMSOk = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey("cep.sms.login"))
				.append("&password=")
				.append(configurationManager.getKey("cep.sms.password"))
				.append("&action=send_sms").append("&number=")
				.append(userEntity.getPhoneNumber()).append("&message=")
				.append(URLEncoder.encode(smsOkMessage, "UTF-8")).toString();
		mockServerSMSOk = MockRestServiceServer.createServer(restTemplateSMSOk);
		mockServerSMSOk
				.expect(MockRestRequestMatchers.requestTo(requestUrlSMSOk))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));

		restTemplateSMSOkNok = new RestTemplate();
		String requestUrlSMSOkNok = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey("cep.sms.login"))
				.append("&password=")
				.append(configurationManager.getKey("cep.sms.password"))
				.append("&action=send_sms").append("&number=")
				.append(userEntity.getPhoneNumber()).append("&message=")
				.append(URLEncoder.encode(smsNokMessage, "UTF-8")).toString();
		mockServerSMSOkNok = MockRestServiceServer
				.createServer(restTemplateSMSOkNok);
		mockServerSMSOkNok
				.expect(MockRestRequestMatchers.requestTo(requestUrlSMSOkNok))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));

		restTemplateSMSNok = new RestTemplate();
		String requestUrlSMSNok = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey("cep.sms.login"))
				.append("&password=")
				.append(configurationManager.getKey("cep.sms.password"))
				.append("&action=send_sms").append("&number=")
				.append(userEntity.getPhoneNumber()).append("&message=")
				.append(URLEncoder.encode(smsOkMessage, "UTF-8")).toString();
		mockServerSMSNok = MockRestServiceServer
				.createServer(restTemplateSMSNok);
		mockServerSMSNok
				.expect(MockRestRequestMatchers.requestTo(requestUrlSMSNok))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(MockRestResponseCreators.withBadRequest());

		configurationManager.setKey(EvaluateWarningReport.countryCodeKey, "CZ");
		configurationManager.setKey(EvaluateWarningReport.regionCodeKey, "B");

		List<GrantedAuthority> gaList = new ArrayList<>();
		gaList.add(new SimpleGrantedAuthority("mayor"));
		User user = new User("test", "test", true, true, true, true, gaList);

		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(user, "test"));

	}

	@After
	public void tearDown() {
		identityService.setAuthenticatedUserId(null);
		if (wiser != null)
			wiser.stop();
	}
}
