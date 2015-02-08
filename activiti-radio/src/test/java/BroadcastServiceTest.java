import org.springframework.beans.factory.annotation.Autowired;

import cz.muni.fi.cep.activiti.radio.service.BroadcastMessageService;
import cz.muni.fi.cep.activiti.radio.tasks.BroadcastTask;
import cz.muni.fi.cep.core.configuration.ConfigurationManager;


public class BroadcastServiceTest extends ActivitiBasicTest {
	private String testAudioFile = "src/test/resources/test.wav";

	@Autowired
	private ConfigurationManager configurationManager;

	@Autowired
	private BroadcastTask broadcastTask;

	private final String broadcastUrlKey = "activiti.radio.broadcast.url";
	
	@Autowired
	private BroadcastMessageService service;
	
	
	
}
