#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import ${groupId}.api.DTO.ContactType;
import ${groupId}.core.bpmn.service.api.MessageType;
import ${groupId}.core.subscriptions.api.SubscriptionService;
import ${groupId}.core.users.api.IdentityService;
import ${groupId}.core.users.entities.CepUserEntity;

/**
 * 
 * 
 * @author Jan Bruzl
 *
 */
public class DefaultTest extends ActivitiBasicTest {
	
	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/MyProcess.bpmn"})
	public void deploymentTest() {
		assertNotNull("Expecting deployed MyProcess process", repositoryService
				.createProcessDefinitionQuery().processDefinitionKey("MyProcess")
				.list().size() > 0);
		
	}

	@Test
	@org.activiti.engine.test.Deployment(resources = { "diagrams/MyProcess.bpmn"})
	public void test() {
		//TODO add tests
	}

}
