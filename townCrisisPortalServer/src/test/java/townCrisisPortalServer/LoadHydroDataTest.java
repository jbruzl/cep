package townCrisisPortalServer;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.concurrent.Callable;

import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.JobQuery;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

public class LoadHydroDataTest extends AbstractTest {
	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti-test.cfg.xml");

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/LoadHydroData.bpmn" })
	public void deploymentTest() {
		assertNotNull(activitiRule);

		RepositoryService repositoryService = activitiRule
				.getRepositoryService();
		assertNotNull(repositoryService);

		Deployment deployment = repositoryService.createDeploymentQuery()
				.singleResult();
		assertNotNull(deployment);

		String deploymentID = deployment.getId();
		System.out.println("Found deployment " + deployment.getId()
				+ ", deployed at " + deployment.getDeploymentTime());

		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery().latestVersion().singleResult();
		assertNotNull(processDefinition);

		assertEquals("LoadHydroData1", processDefinition.getKey());
		System.out.println("Found process definition "
				+ processDefinition.getId());
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/LoadHydroData.bpmn" })
	public void startTaskTest() throws Exception {
		ManagementService managementService = activitiRule
				.getManagementService();
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		JobQuery jobQuery = managementService.createJobQuery();
		assertEquals(1, jobQuery.count());

		final ProcessInstanceQuery piq = runtimeService
				.createProcessInstanceQuery().processDefinitionKey(
						"LoadHydroData1");

		moveBySeconds(5);
		
		assertEquals(1, jobQuery.count());
		moveBySeconds(5);
		
		assertEquals(1, jobQuery.count());

	}

	private void moveBySeconds(int seconds) throws Exception {
		ProcessEngineConfiguration processEngineConfiguration = activitiRule
				.getProcessEngine().getProcessEngineConfiguration();
		processEngineConfiguration.getClock().setCurrentTime(
				new Date(processEngineConfiguration.getClock().getCurrentTime()
						.getTime()
						+ ((seconds * 1000) + 5000)));
	}
}
