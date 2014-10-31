/**
 * 
 */
package townCrisisPortalServer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.activiti.engine.ManagementService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import cz.muni.fi.bruzl.ces.entity.MessageType;

/**
 * @author Jan Bruzl
 *
 */
public class SendEmailTest extends AbstractTest {
	@Autowired
	@Rule
	public ActivitiRule activitiRule;
	
	@Autowired
	private ManagementService managementService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/SendEmail.bpmn" })
	public void testEMAILTask() throws IOException, MessagingException {
		Wiser wiser = new Wiser();
		wiser.setPort(1025);
		wiser.start();
		
		Map<String, String> templateVariable = new HashMap<String, String>();
		templateVariable.put("world","hellou");
		
		Map<String,Object> processVariables =
				new HashMap<String,Object>();
		processVariables.put("sender", "sender@localhost.com");
		
		List<String> receivers = new ArrayList<String>();
		receivers.add("email@localhost.com");
		receivers.add("email2@localhost.com");
		processVariables.put("receivers", receivers);
		processVariables.put("templateKey", "testTemplate");
		processVariables.put("messageType", MessageType.NOTIFICATION);
		processVariables.put("templateVariable", templateVariable);
		runtimeService.startProcessInstanceByKey("sendEmail", processVariables);	
		
		List<WiserMessage> messages = wiser.getMessages();
		assertEquals(2, messages.size());
		

		wiser.stop();
	}
}
