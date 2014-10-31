/**
 * 
 */
package townCrisisPortalServer;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.JobQuery;
import org.activiti.engine.test.ActivitiRule;
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

	@Test
	@org.activiti.engine.test.Deployment(resources = {
			"diagrams/SendEmail.bpmn", "diagrams/MonthlySirenTest.bpmn" })
	public void testSirenTest() throws Exception {
		Wiser wiser = new Wiser();
		wiser.setPort(1025);
		wiser.start();

		moveBySeconds(5);
		JobQuery jobQuery = managementService.createJobQuery();
		List<Job> jobList = jobQuery.executable().list();
		assertEquals(1, jobList.size());
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
				System.out.println((String)bodyPart.getContent());
			}
		}
		wiser.stop();

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
