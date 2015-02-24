#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.HashMap;
import java.util.List;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ${groupId}.core.configuration.ConfigurationManager;
import ${groupId}.core.users.api.IdentityService;


/**
 * Test class for ${processName}.bpmn process.
 * 
 * @author Jan Bruzl
 *
 */
public class ${processName}Test extends ActivitiBasicTest {

	@Autowired
	private IdentityService identityService;

	@Autowired
	private ConfigurationManager configurationManager;

	@Test
	@org.activiti.engine.test.Deployment(resources = {
			"diagrams/${processName}.bpmn" })
	public void deploymentTest() {
		assertNotNull("Expecting deployed ${processName} process", repositoryService
				.createProcessDefinitionQuery().processDefinitionKey("${process}")
				.list().size() > 0);
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { 
			"diagrams/${processName}.bpmn" })
	public void ${process}Test() {
		HashMap<String, Object> params = new HashMap<>();

		ProcessInstance pi = runtimeService.startProcessInstanceByKey("${process}",
				params);
		assertNotNull(pi);

		List<HistoricActivityInstance> haiList = historyService
				.createHistoricActivityInstanceQuery().activityType("endEvent")
				.finished().list();

		assertEquals(
				"Process didn't finish in expected end event. Finished in: "
						+ haiList.get(0).getActivityId(),
				"endevent", haiList.get(0).getActivityId());
	}
}
