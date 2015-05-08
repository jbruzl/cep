import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.history.HistoricProcessInstance;
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
public class warnInformCitizensTest extends ActivitiBasicTest {

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
	
	private String publisherCode = "001";
	private String smsMessage = "test sms";
	private String testAudioFile = "src/test/resources/test.wav";
	private RadioMessage rm;
	private String receiver = "728484615";
	private MockRestServiceServer mockServerSMS, mockServerSMSNok, mockServerRadio;
	private RestTemplate restSMSOkTemplate, restSMSNokTemplate, restRadioOkTemplate;

	@Test
	@org.activiti.engine.test.Deployment(resources = {
			"diagrams/Warn and inform citizens.bpmn" })
	public void deploymentTest() {
		assertNotNull("Expecting deployed Warn and inform citizens process", repositoryService
				.createProcessDefinitionQuery().processDefinitionKey("warnInformCitizens")
				.list().size() > 0);
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { 
			"diagrams/Warn and inform citizens.bpmn" })
	public void OkNoCheckTest() {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);
		
		sendSMSTask.setRestTemplate(restSMSOkTemplate);
		broadcastTask.setRestTemplate(restRadioOkTemplate);
		
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("warnInformCitizens",
				params);
		assertNotNull(pi);
		
		List<Task> taskList = taskService.createTaskQuery().processInstanceId(pi.getId()).list();
		assertEquals(2, taskList.size());
		
		
		HashMap<String, Object> smsTask = new HashMap<>();
		HashMap<String, Object> radioTask = new HashMap<>();
		
		smsTask.put("smsMessage", smsMessage);
		smsTask.put("checkResponse", false);
		radioTask.put("radioMessage", rm);
		
		
		for(Task t : taskList){
			switch(t.getTaskDefinitionKey()){
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
		
		taskList = taskService.createTaskQuery().processInstanceId(pi.getId()).list();
		assertEquals(1, taskList.size());
		
		Task t = taskList.get(0);
		taskService.complete(t.getId());
		
		HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery().finished().processInstanceId(pi.getId()).singleResult();
		assertNotNull(hpi);
		
		mockServerRadio.verify();
		mockServerSMS.verify();
	}
	
	@Before
	public void setUp() throws UnsupportedEncodingException{
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
		
		
		restSMSOkTemplate = new RestTemplate();
		String requestUrl = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey("cep.sms.login"))
				.append("&password=")
				.append(configurationManager.getKey("cep.sms.password"))
				.append("&action=send_sms").append("&number=").append(receiver)
				.append("&message=")
				.append(URLEncoder.encode(smsMessage, "UTF-8")).toString();
		mockServerSMS = MockRestServiceServer.createServer(restSMSOkTemplate);
		mockServerSMS
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		
		restSMSNokTemplate = new RestTemplate();
		requestUrl = new StringBuilder()
				.append("http://api.smsbrana.cz/smsconnect/http.php")
				.append("?login=")
				.append(configurationManager.getKey("cep.sms.login"))
				.append("&password=")
				.append(configurationManager.getKey("cep.sms.password"))
				.append("&action=send_sms").append("&number=").append(receiver)
				.append("&message=")
				.append(URLEncoder.encode(smsMessage, "UTF-8")).toString();
		mockServerSMSNok = MockRestServiceServer.createServer(restSMSNokTemplate);
		mockServerSMSNok
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withBadRequest());
		
		restRadioOkTemplate = new RestTemplate();
		mockServerRadio = MockRestServiceServer.createServer(restRadioOkTemplate);
		mockServerRadio
				.expect(MockRestRequestMatchers.requestTo(configurationManager
						.getKey(BroadcastTask.broadcastUrlKey)))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));

		
	}
}
