/**
 * 
 */
package townCrisisPortalServer;

import org.activiti.engine.ManagementService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jan Bruzl
 *
 */
public class StartRadioRelayTest extends AbstractTest {
	@Autowired
	@Rule
	public ActivitiRule activitiRule;
	
	@Autowired
	private ManagementService managementService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/StartRadioRelay.bpmn" })
	public void testRadioRelay() {
		try {
			runtimeService.startProcessInstanceByKey("StartRadioRelay");
		}catch(BpmnError ex) {
			System.err.println("Server not responding! Check that.");
		}
	}
}
