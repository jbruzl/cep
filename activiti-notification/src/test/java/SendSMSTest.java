import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import cz.muni.fi.cep.activiti.notification.tasks.SendSMSTask;
import cz.muni.fi.cep.api.services.configurationmanager.ConfigurationManager;
import cz.muni.fi.cep.core.bpmn.service.api.MessageType;

public class SendSMSTest extends ActivitiBasicTest {
	@Autowired
	private SendSMSTask sendSMSTask;

	@Autowired
	private ConfigurationManager configurationManager;

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/SendSMS.bpmn" })
	public void testSMSTask() {
		String message = "Hello_World!";
		String receiver = "728484615";
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
		MockRestServiceServer mockServer = MockRestServiceServer
				.createServer(restTemplate);
		mockServer
				.expect(MockRestRequestMatchers.requestTo(requestUrl))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		sendSMSTask.setRestTemplate(restTemplate);

		Map<String, Object> processVariables = new HashMap<>();
		List<String> receivers = new ArrayList<String>();
		receivers.add(receiver);
		processVariables.put("message", message);
		processVariables.put("receivers", receivers);
		processVariables.put("messageType", MessageType.NOTIFICATION);
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("sendsms", processVariables);

		List<HistoricProcessInstance> hpiList = historyService.createHistoricProcessInstanceQuery().processInstanceId(pi.getId()).list();

		assertNotNull(hpiList);
		assertEquals(1, hpiList.size());
		
		mockServer.verify();
	}

}
