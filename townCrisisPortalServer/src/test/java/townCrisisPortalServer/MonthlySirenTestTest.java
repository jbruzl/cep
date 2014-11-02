/**
 * 
 */
package townCrisisPortalServer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.activiti.engine.FormService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.JobQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

/**
 * @author Jan Bruzl
 *
 */
public class MonthlySirenTestTest extends AbstractTest {
	@Autowired
	@Rule
	public ActivitiRule activitiRule;

	@Autowired
	private ManagementService managementService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private FormService formService;

	@Autowired
	private TaskService taskService;

	private Wiser wiser;

	@org.junit.Before
	public void setUp() {
		wiser = new Wiser();
		wiser.setPort(1025);
		wiser.start();
	}

	@After
	public void tearDown() {
		if (wiser != null) {
			wiser.stop();
		}
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = {
			"diagrams/SendEmail.bpmn", "diagrams/MonthlySirenTest.bpmn",
			"diagrams/SendSMS.bpmn", "diagrams/SoundSiren.bpmn" })
	public void testSirenTestCancel() throws Exception {
		JobQuery jobQuery;
		List<Job> jobList;
		startProcess();

		List<Task> taskList = taskService.createTaskQuery().list();
		Task verifyCancelation = taskList.get(0);
		assertNotNull(verifyCancelation);

		taskService.claim(verifyCancelation.getId(), "kermit");

		Map<String, Object> formProperties = new HashMap<String, Object>();
		formProperties.put("canceltest", true);

		taskService.complete(verifyCancelation.getId(), formProperties);

		jobQuery = managementService.createJobQuery().processInstanceId(
				verifyCancelation.getProcessInstanceId());
		jobList = jobQuery.executable().list();
		assertEquals("Expecting 0 jobs.", 0, jobList.size());

	}

	@Test
	@org.activiti.engine.test.Deployment(resources = {
			"diagrams/SendEmail.bpmn", "diagrams/MonthlySirenTest.bpmn",
			"diagrams/SendSMS.bpmn", "diagrams/SoundSiren.bpmn" })
	public void testSirenTestTimeout() throws Exception {
		JobQuery jobQuery;
		List<Job> jobList;
		startProcess();

		List<Task> taskList = taskService.createTaskQuery().list();
		Task verifyCancelation = taskList.get(0);
		assertNotNull(verifyCancelation);

		taskService.claim(verifyCancelation.getId(), "kermit");

		moveBySeconds(9 * 60);

		jobQuery = managementService.createJobQuery().processInstanceId(
				verifyCancelation.getProcessInstanceId());
		jobList = jobQuery.executable().list();
		assertEquals("Expecting 1 job.", 1, jobList.size());
		for (Job job : jobList) {
			System.out.println("Executing job: " + job.toString());
			System.out.println("Due date: " + job.getDuedate());
			System.out.println("Instance: " + job.getProcessInstanceId());
			System.out.println("Definition: " + job.getProcessDefinitionId());
			System.out.println("Execution: " + job.getExecutionId());

			managementService.executeJob(job.getId());
		}

		moveBySeconds(60);

		jobQuery = managementService.createJobQuery().processInstanceId(
				verifyCancelation.getProcessInstanceId());
		jobList = jobQuery.executable().list();
		assertEquals("Expecting 1 job.", 1, jobList.size());
		for (Job job : jobList) {
			System.out.println("Executing job: " + job.toString());
			System.out.println("Due date: " + job.getDuedate());
			System.out.println("Instance: " + job.getProcessInstanceId());
			System.out.println("Definition: " + job.getProcessDefinitionId());
			System.out.println("Execution: " + job.getExecutionId());

			managementService.executeJob(job.getId());
		}

	}

	@Test
	@org.activiti.engine.test.Deployment(resources = {
			"diagrams/SendEmail.bpmn", "diagrams/MonthlySirenTest.bpmn",
			"diagrams/SendSMS.bpmn", "diagrams/SoundSiren.bpmn" })
	public void testSirenTest() throws Exception {
		JobQuery jobQuery;
		List<Job> jobList;
		startProcess();

		List<Task> taskList = taskService.createTaskQuery().list();
		Task verifyCancelation = taskList.get(0);
		assertNotNull(verifyCancelation);

		taskService.claim(verifyCancelation.getId(), "kermit");

		jobQuery = managementService.createJobQuery().processInstanceId(
				verifyCancelation.getProcessInstanceId());
		jobList = jobQuery.executable().list();
		assertEquals("Expecting 0 job.", 0, jobList.size());

		taskService.claim(verifyCancelation.getId(), "kermit");

		Map<String, Object> formProperties = new HashMap<String, Object>();
		formProperties.put("canceltest", false);

		taskService.complete(verifyCancelation.getId(), formProperties);

		moveBySeconds(10 * 60);
		jobQuery = managementService.createJobQuery().processInstanceId(
				verifyCancelation.getProcessInstanceId());
		jobList = jobQuery.executable().list();

		assertEquals("Expecting 1 jobs.", 1, jobList.size());
		for (Job job : jobList) {
			System.out.println("Executing job: " + job.toString());
			System.out.println("Due date: " + job.getDuedate());
			System.out.println("Instance: " + job.getProcessInstanceId());
			System.out.println("Definition: " + job.getProcessDefinitionId());
			System.out.println("Execution: " + job.getExecutionId());

			managementService.executeJob(job.getId());
		}
	}

	/**
	 * @throws Exception
	 * @throws MessagingException
	 * @throws IOException
	 */
	private void startProcess() throws Exception, MessagingException,
			IOException {
		setEngineTime("2014-11-05 11:50:00");
		JobQuery jobQuery = managementService.createJobQuery();
		List<Job> jobList = jobQuery.executable().list();
		assertEquals("Process job should be waiting for execution.", 1,
				jobList.size());
		System.out.println(jobList);
		for (Job job : jobList) {
			System.out.println("Executing job: " + job.toString());
			managementService.executeJob(job.getId());
		}

		List<WiserMessage> messages = wiser.getMessages();
		assertEquals(1, messages.size());
		for (WiserMessage wm : messages) {
			MimeMessage mm = wm.getMimeMessage();
			System.out.println(mm.getContentType());
			MimeMultipart multipart = (MimeMultipart) mm.getContent();
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				System.out.println(bodyPart.getContentType());
				System.out.println((String) bodyPart.getContent());
			}
		}
	}

	private void moveBySeconds(int seconds) throws Exception {
		ProcessEngineConfiguration processEngineConfiguration = activitiRule
				.getProcessEngine().getProcessEngineConfiguration();
		processEngineConfiguration.getClock().setCurrentTime(
				new Date(processEngineConfiguration.getClock().getCurrentTime()
						.getTime()
						+ ((seconds * 1000) + 5000)));
		System.out.println("Setting time to: "
				+ processEngineConfiguration.getClock().getCurrentTime());
	}

	private void setEngineTime(String datetime) throws ParseException {
		ProcessEngineConfiguration processEngineConfiguration = activitiRule
				.getProcessEngine().getProcessEngineConfiguration();

		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);
		activitiRule.setCurrentTime(date);
		System.out.println("Setting time to: "
				+ processEngineConfiguration.getClock().getCurrentTime());
	}
}
