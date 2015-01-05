package cz.muni.fi.cep.tests.activiti.notification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;

import org.activiti.engine.form.FormData;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import cz.muni.fi.cep.activiti.notification.service.NotifyService;
import cz.muni.fi.cep.api.DTO.ContactType;
import cz.muni.fi.cep.core.subscriptions.api.SubscriptionService;
import cz.muni.fi.cep.core.users.api.IdentityService;
import cz.muni.fi.cep.core.users.entities.CepUserEntity;
import cz.muni.fi.cep.tests.activiti.ActivitiBasicTest;

/**
 * Test class for {@link NotifyService}
 * 
 * @author Jan Bruzl
 *
 */
public class NotifyServiceTest extends ActivitiBasicTest {
	private Wiser wiser;
	
	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	private NotifyService notifyService;
	
	@Before
	public void setUp() {
		assertNotNull("Identity service null", identityService);
		assertNotNull("Subscription service null", subscriptionService);
		
		CepUserEntity userEntity = new CepUserEntity();
		userEntity.setFirstName("Jan");
		userEntity.setLastName("Bruzl");
		userEntity.setEmail("notify@gmail.com");
		userEntity.setPhoneNumber("123456789");
		userEntity.setPassword("01234");

		CepUserEntity userEntity2 = new CepUserEntity();
		userEntity2.setFirstName("Karel");
		userEntity2.setLastName("Novak");
		userEntity2.setEmail("novak@novak.cz");
		userEntity2.setPhoneNumber("987654321");
		userEntity2.setPassword("01234");	

		identityService.createUser(userEntity);
		identityService.createUser(userEntity2);
		
		identityService.setAuthenticatedUserId(userEntity.getEmail());
		
		String publisherCode = NotifyService.getPublisherCode();
		subscriptionService.subscribeUser(userEntity, publisherCode, ContactType.EMAIL);
		subscriptionService.subscribeUser(userEntity, publisherCode, ContactType.SMS);
		subscriptionService.subscribeUser(userEntity2, publisherCode, ContactType.EMAIL);
		subscriptionService.subscribeUser(userEntity2, publisherCode, ContactType.SMS);
		
		wiser = new Wiser();
		wiser.setPort(2025);
		wiser.start();
	}
	
	@After
	public void tearDown() {
		identityService.setAuthenticatedUserId(null);
		if(wiser != null)
			wiser.stop();
	}
	
	@Test
	@org.activiti.engine.test.Deployment(resources = {"diagrams/SendSMS.bpmn", "diagrams/Notify.bpmn" })
	public void deploymentTest() {
		assertNotNull("Expecting deployed Notify process", repositoryService.createProcessDefinitionQuery().processDefinitionKey("Notify").singleResult());
		assertNotNull("Expecting deployed SendSMS process", repositoryService.createProcessDefinitionQuery().processDefinitionKey("SendSMS").singleResult());
	}
	
	@Test
	@org.activiti.engine.test.Deployment(resources = {"diagrams/SendSMS.bpmn", "diagrams/Notify.bpmn" })
	public void getDiagramTest() {
		notifyService.init();
		assertNotNull(notifyService.getProcessDiagram());
	}
	
	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/SendSMS.bpmn", "diagrams/Notify.bpmn" })
	public void notificationTest() {	
		notifyService.init();
		HashMap<String, String> message = new HashMap<>();
		message.put("message", "default");
		ProcessInstance pi = notifyService.startTask("default", message);

		//TODO test sms implementation
		
		List<WiserMessage> messages = wiser.getMessages();
		assertEquals(2, messages.size());
		
		List<HistoricActivityInstance> haiList = historyService.createHistoricActivityInstanceQuery().activityType("endEvent").activityId("endevent3").finished().list();
		assertEquals(1, haiList.size());
		assertEquals("Process didn't finish in expected end event. Finished in: " + haiList.get(0).getActivityId(), "endevent3",haiList.get(0).getActivityId());
		
		Float processInstanceProgress = notifyService.getProcessInstanceProgress(pi.getId());
		assertEquals(new Float(1), processInstanceProgress);
	}
	
	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/SendSMS.bpmn", "diagrams/Notify.bpmn" })
	public void startFormTest() {
		FormData formData = notifyService.getStartForm();
		assertNotNull(formData);
	}
}
