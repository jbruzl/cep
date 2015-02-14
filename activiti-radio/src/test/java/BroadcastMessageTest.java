import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import cz.muni.fi.cep.activiti.radio.messages.FileRadioMessage;
import cz.muni.fi.cep.activiti.radio.tasks.BroadcastTask;
import cz.muni.fi.cep.core.configuration.ConfigurationManager;

/**
 * 
 * 
 * @author Jan Bruzl
 *
 */
public class BroadcastMessageTest extends ActivitiBasicTest {
	private String testAudioFile = "src/test/resources/test.wav";

	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private BroadcastTask broadcastTask;

	private final String broadcastUrlKey = "cep.radio.broadcast.url";

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/BroadcastMessage.bpmn" })
	public void deploymentTest() {
		assertNotNull(
				"Expecting deployed MyProcess process",
				repositoryService.createProcessDefinitionQuery()
						.processDefinitionKey("broadcastmessage").list().size() > 0);

	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/BroadcastMessage.bpmn" })
	public void runTestOK() {
		configurationManager.setKey(broadcastUrlKey,
				"http://localhost:8080/broadcast");

		RestTemplate restTemplate = new RestTemplate();
		MockRestServiceServer mockServer = MockRestServiceServer
				.createServer(restTemplate);
		mockServer
				.expect(MockRestRequestMatchers.requestTo(configurationManager
						.getKey(broadcastUrlKey)))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andRespond(
						MockRestResponseCreators.withSuccess("",
								MediaType.TEXT_PLAIN));
		broadcastTask.setRestTemplate(restTemplate);

		FileRadioMessage rm = new FileRadioMessage();
		rm.setAudioFileName(testAudioFile);
		rm.setAuthor("test");

		Map<String, Object> params = new HashMap<>();
		params.put("radioMessage", rm);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				"broadcastmessage", params);
		assertNotNull(pi);
		
		mockServer.verify();

		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().activityType("endEvent")
				.finished().list();
		assertEquals(1, haiList.size());
		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(0).getActivityId(), "endok",
				haiList.get(0).getActivityId());
	}
	
	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/BroadcastMessage.bpmn" })
	public void runTestFail() {
		configurationManager.setKey(broadcastUrlKey,
				"http://localhost:8080/broadcast");

		RestTemplate restTemplate = new RestTemplate();
		MockRestServiceServer mockServer = MockRestServiceServer
				.createServer(restTemplate);
		mockServer
				.expect(MockRestRequestMatchers.requestTo(configurationManager
						.getKey(broadcastUrlKey)))
				.andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
				.andRespond(
						MockRestResponseCreators.withServerError());
		broadcastTask.setRestTemplate(restTemplate);

		FileRadioMessage rm = new FileRadioMessage();
		rm.setAudioFileName(testAudioFile);
		rm.setAuthor("test");

		Map<String, Object> params = new HashMap<>();
		params.put("radioMessage", rm);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				"broadcastmessage", params);
		assertNotNull(pi);
		
		
		
		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().activityType("endEvent")
				.finished().list();
		assertEquals(1, haiList.size());
		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(0).getActivityId(), "broadcasterror3",
				haiList.get(0).getActivityId());
		
		mockServer.verify();
	}
	
	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/BroadcastMessage.bpmn" })
	public void runTestBadInput() {

		Map<String, Object> params = new HashMap<>();
		params.put("radioMessage", null);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				"broadcastmessage", params);
		assertNotNull(pi);
		
		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().activityType("endEvent")
				.finished().list();
		assertEquals(1, haiList.size());
		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(0).getActivityId(), "badinput1",
				haiList.get(0).getActivityId());

	}
}
