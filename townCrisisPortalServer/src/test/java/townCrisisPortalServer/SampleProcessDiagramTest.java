package townCrisisPortalServer;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.JobQuery;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SampleProcessDiagramTest extends AbstractTest {
	@Autowired
	@Rule
	public ActivitiRule activitiRule;
	
	@Autowired
	private ManagementService managementService;
	
	@Autowired
	private RuntimeService runtimeService;
	

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/SampleProcessDiagram.bpmn" })
	public void deploymentTest() {		
		assertNotNull(activitiRule);
		assertNotNull(managementService);
		assertNotNull(runtimeService);

	}


	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/SampleProcessDiagram.bpmn" })
	public void startTaskTest() throws Exception {		
		JobQuery jobQuery = managementService.createJobQuery();
		
		assertEquals(1, jobQuery.count());

		final ProcessInstanceQuery piq = runtimeService
				.createProcessInstanceQuery().processDefinitionKey(
						"mySimpleProcess");

		moveBySeconds(5);
		assertEquals(1, jobQuery.count());
		moveBySeconds(5);

		assertEquals(1, jobQuery.count());
		moveBySeconds(5);

	}

	private void moveBySeconds(int seconds) throws Exception {
		ProcessEngineConfiguration processEngineConfiguration = activitiRule.getProcessEngine().getProcessEngineConfiguration();
		processEngineConfiguration.getClock().setCurrentTime(
				new Date(processEngineConfiguration.getClock().getCurrentTime()
						.getTime()
						+ ((seconds * 1000) + 5000)));
	}
	
	@Test
	@org.activiti.engine.test.Deployment(resources= {"diagrams/SampleProcessDiagram.bpmn"})
	public void manualExecutionTest() throws Exception {
		moveBySeconds(5);
		JobQuery jobQuery = managementService.createJobQuery();
		List<Job> jobList = jobQuery.executable().list();
		assertEquals(1, jobList.size());
		System.out.println(jobList);
		for(Job job : jobList) {
			System.out.println("Executing job: " + job.toString());
			managementService.executeJob(job.getId());
		}
		
		jobQuery = managementService.createJobQuery();
		jobList = jobQuery.executable().list();
		//assertEquals(1, jobList.size());
		System.out.println(jobList);
		for(Job job : jobList) {
			System.out.println("Executing job: " + job.toString());
			managementService.executeJob(job.getId());
		}
		
		jobQuery = managementService.createJobQuery();
		jobList = jobQuery.executable().list();
		//assertEquals(1, jobList.size());
		System.out.println(jobList);
		for(Job job : jobList) {
			System.out.println("Executing job: " + job.toString());
			managementService.executeJob(job.getId());
		}
		
		jobQuery = managementService.createJobQuery();
		jobList = jobQuery.executable().list();
		assertEquals(0, jobList.size());
		
	}
	
	

}
