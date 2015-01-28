import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cz.muni.fi.cep.activiti.radio.messages.FileRadioMessage;
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
	
	private final String broadcastUrlKey = "activiti.radio.broadcast.url";
	
	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/BroadcastMessage.bpmn"})
	public void deploymentTest() {
		assertNotNull("Expecting deployed MyProcess process", repositoryService
				.createProcessDefinitionQuery().processDefinitionKey("broadcastmessage")
				.list().size() > 0);
		
	}
	
	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/BroadcastMessage.bpmn"})
	public void runTest() {
		configurationManager.setKey(broadcastUrlKey, "localhost");
		
		FileRadioMessage rm = new FileRadioMessage();
		rm.setAudioFileName(testAudioFile);
		rm.setAuthor("test");
		
		Map<String, Object> params = new HashMap<>();
		params.put("radioMessage", rm);
		
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("broadcastmessage", params);
		assertNotNull(pi);

		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().activityType("endEvent")
				.finished().list();
		assertEquals(1, haiList.size());
		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(0).getActivityId(),
				"endok", haiList.get(0).getActivityId());
		
	}

}
