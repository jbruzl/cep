/**
 * 
 */
package bpmn;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.junit.Test;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import cz.muni.fi.cep.core.bpmn.service.api.MessageType;


/**
 * @author Jan Bruzl
 *
 */
public class SendEmailTest extends ActivitiBasicTest {
	@Test
	@org.activiti.engine.test.Deployment(resources = { "bpmn/SendEmail.bpmn" })
	public void testEMAILTask() throws IOException, MessagingException {
		Wiser wiser = new Wiser();
		wiser.setPort(2025);	//in case of mail task not responding, check if port is not used by other program
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
		runtimeService.startProcessInstanceByKey("SendEmail", processVariables);	
		
		List<WiserMessage> messages = wiser.getMessages();
		assertEquals(2, messages.size());
		

		wiser.stop();
	}
}
