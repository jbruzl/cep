package cz.muni.fi.cep.activiti.warning.chmi.tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import cz.muni.fi.cep.activiti.notification.tasks.SendSMSTask;
import cz.muni.fi.cep.activiti.warning.chmi.WeatherReportRegister;
import cz.muni.fi.cep.activiti.warning.chmi.report.Country;
import cz.muni.fi.cep.activiti.warning.chmi.report.Region;
import cz.muni.fi.cep.activiti.warning.chmi.report.Report;
import cz.muni.fi.cep.activiti.warning.chmi.tasks.EvaluateWarningReport;
import cz.muni.fi.cep.activiti.warning.chmi.tasks.ObtainWeatherReport;
import cz.muni.fi.cep.api.DTO.CepUser;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.api.services.users.IdentityService;

/**
 * @author Jan Bruzl
 *
 */
@PropertySource("classpath:config/config.properties")
public class WarningTest extends ActivitiBasicTest {
	private Wiser wiser;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private ConfigurationManager configurationManager;

	private final String key = "warning";
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
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warning.bpmn",
			"diagrams/InformCitizens.bpmn" })
	public void deploymentTest() {
		assertNotNull("Expecting deployed Warning", repositoryService
				.createProcessDefinitionQuery().processDefinitionKey(key)
				.list().size() > 0);
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warning.bpmn",
			"diagrams/InformCitizens.bpmn" })
	public void timerStartTest() {
		WeatherReportRegister wrr = new WeatherReportRegister();

		Report okReport = (Report) weatherReportMarshaller
				.unmarshal(new StreamSource(getClass().getClassLoader()
						.getResourceAsStream("weatherOk.xml")));
		wrr.getReports().add(okReport);

		evaluateWarningReport.setWeatherReportRegistr(wrr);
		obtainWeatherReport.setRestTemplate(restTemplateNok);
		List<Job> jobList = managementService.createJobQuery().executable()
				.list();
		for (Job j : jobList) {
			managementService.executeJob(j.getId());
		}
		List<HistoricProcessInstance> processInstances = historyService
				.createHistoricProcessInstanceQuery().processDefinitionKey(key)
				.list();

		skipToFuture();

		List<Job> futureJobList = managementService.createJobQuery()
				.executable().list();
		for (Job j : futureJobList) {
			managementService.executeJob(j.getId());
		}
		List<HistoricProcessInstance> futureProcessInstances = historyService
				.createHistoricProcessInstanceQuery().processDefinitionKey(key)
				.list();

		assertTrue(processInstances.size() < futureProcessInstances.size());
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warning.bpmn",
			"diagrams/InformCitizens.bpmn" })
	public void manualStartTest() {

		sendSMSTask.setRestTemplate(restTemplateSMSOk);
		WeatherReportRegister wrr = new WeatherReportRegister();

		evaluateWarningReport.setWeatherReportRegistr(wrr);
		obtainWeatherReport.setRestTemplate(restTemplateOk);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key);
		assertNotNull("Expected process instance, got null", pi);

		List<Task> list = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals(1, list.size());

		Task task = list.get(0);

		assertEquals("decideInformCitizens", task.getTaskDefinitionKey());

		Map<String, Object> vars = new HashMap<>();
		vars.put("sendRadio", false);
		vars.put("sendSMS", false);
		vars.put("sendEmail", false);
		vars.put("decideInformCitizens", true);

		taskService.complete(task.getId(), vars);

		mockServerSMSOk.verify();
		mockServerOk.verify();
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warning.bpmn",
			"diagrams/InformCitizens.bpmn" })
	public void sendSMSFailureTest() {
		sendSMSTask.setRestTemplate(restTemplateSMSNok);
		WeatherReportRegister wrr = new WeatherReportRegister();

		evaluateWarningReport.setWeatherReportRegistr(wrr);
		obtainWeatherReport.setRestTemplate(restTemplateOk);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key);
		assertNotNull("Expected process instance, got null", pi);

		List<Task> list = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals(1, list.size());

		Task task = list.get(0);

		assertEquals("decideInformCitizens", task.getTaskDefinitionKey());

		Map<String, Object> vars = new HashMap<>();
		vars.put("decideInformCitizens", true);

		List<WiserMessage> messages = wiser.getMessages();
		assertEquals(2, messages.size());
		mockServerSMSNok.verify();
		mockServerOk.verify();

	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warning.bpmn",
			"diagrams/InformCitizens.bpmn" })
	public void sendSMSDecreaseLevelTest() {
		sendSMSTask.setRestTemplate(restTemplateSMSOkNok);
		WeatherReportRegister wrr = new WeatherReportRegister();

		Report okReport = (Report) weatherReportMarshaller
				.unmarshal(new StreamSource(getClass().getClassLoader()
						.getResourceAsStream("weatherOk.xml")));

		String countryCode = configurationManager.getKey(EvaluateWarningReport.countryCodeKey);
		String regionCode = configurationManager.getKey(EvaluateWarningReport.regionCodeKey);
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
		
		wrr.getReports().add(okReport); 

		evaluateWarningReport.setWeatherReportRegistr(wrr);
		obtainWeatherReport.setRestTemplate(restTemplateOk);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key);
		assertNotNull("Expected process instance, got null", pi);

		List<Task> list = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals(1, list.size());

		Task task = list.get(0);

		assertEquals("decideInformCitizens", task.getTaskDefinitionKey());

		mockServerSMSOkNok.verify();
		mockServerOk.verify();

	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warning.bpmn",
			"diagrams/InformCitizens.bpmn" })
	public void dontInformCitizensTest() {
		sendSMSTask.setRestTemplate(restTemplateSMSOk);
		WeatherReportRegister wrr = new WeatherReportRegister();


		evaluateWarningReport.setWeatherReportRegistr(wrr);
		obtainWeatherReport.setRestTemplate(restTemplateOk);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key);
		assertNotNull("Expected process instance, got null", pi);

		List<Task> list = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals(1, list.size());

		Task task = list.get(0);

		assertEquals("decideInformCitizens", task.getTaskDefinitionKey());

		Map<String, Object> vars = new HashMap<>();
		vars.put("sendRadio", false);
		vars.put("sendSMS", false);
		vars.put("sendEmail", false);
		vars.put("decideInformCitizens", false);

		taskService.complete(task.getId(), vars);

		HistoricActivityInstance endActivity = historyService
				.createHistoricActivityInstanceQuery()
				.processInstanceId(pi.getId()).activityType("endEvent")
				.singleResult();
		assertNotNull(endActivity);
		assertEquals("declineInformCitizens", endActivity.getActivityId());

		mockServerSMSOk.verify();
		mockServerOk.verify();
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warning.bpmn",
			"diagrams/InformCitizens.bpmn" })
	public void noChangeTest() {
		WeatherReportRegister wrr = new WeatherReportRegister();

		Report okReport = (Report) weatherReportMarshaller
				.unmarshal(new StreamSource(getClass().getClassLoader()
						.getResourceAsStream("weatherOk.xml")));
		wrr.getReports().add(okReport);

		evaluateWarningReport.setWeatherReportRegistr(wrr);
		obtainWeatherReport.setRestTemplate(restTemplateOk);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key);
		assertNotNull("Expected process instance, got null", pi);

		HistoricActivityInstance endActivity = historyService
				.createHistoricActivityInstanceQuery()
				.processInstanceId(pi.getId()).activityType("endEvent")
				.singleResult();
		assertNotNull(endActivity);
		assertEquals("warningLevelNotChanged", endActivity.getActivityId());

		mockServerOk.verify();

	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/Warning.bpmn",
			"diagrams/InformCitizens.bpmn" })
	public void badRequestWeatherReportTest() {
		obtainWeatherReport.setRestTemplate(restTemplateNok);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key);
		assertNotNull("Expected process instance, got null", pi);

		mockServerNok.verify();

		List<Task> list = taskService.createTaskQuery()
				.processInstanceId(pi.getId()).list();
		assertEquals(1, list.size());

		Task task = list.get(0);

		assertEquals("informCommunicationFailure", task.getTaskDefinitionKey());

		taskService.complete(task.getId());

		List<HistoricProcessInstance> piList = historyService
				.createHistoricProcessInstanceQuery()
				.processInstanceId(pi.getId()).finished().list();
		assertEquals(1, piList.size());

		final HistoricActivityInstance activity = historyService
				.createHistoricActivityInstanceQuery()
				.processInstanceId(pi.getId()).activityType("endEvent")
				.singleResult();
		assertEquals("Chyba spojení s ÈHMÚ", activity.getActivityName());

		mockServerNok.verify();
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
		configurationManager.setKey(ObtainWeatherReport.chmiUrlKey, requestUrl);

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

	}

	@After
	public void tearDown() {
		identityService.setAuthenticatedUserId(null);
		if (wiser != null)
			wiser.stop();
	}

	/**
	 * Adds 60 mins to process engine
	 */
	private void skipToFuture() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(activitiRule.getProcessEngine()
				.getProcessEngineConfiguration().getClock().getCurrentTime());
		cal.add(Calendar.MINUTE, 60);
		activitiRule.setCurrentTime(cal.getTime());
	}

}
