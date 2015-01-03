package bpmn;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricVariableUpdate;
import org.junit.Test;
import cz.muni.fi.cep.core.bpmn.service.api.MessageType;

public class SendSMSTest extends ActivitiBasicTest{
	
	@SuppressWarnings("unchecked")
	@Test
	@org.activiti.engine.test.Deployment(resources = { "bpmn/SendSMS.bpmn" })
	public void testSMSTask() {
		Map<String, Object> processVariables = new HashMap<>();
		List<String> receivers = new ArrayList<String>();
		receivers.add("All");
		processVariables.put("message", "Hello World!");		
		processVariables.put("receivers", receivers);
		processVariables.put("messageType", MessageType.NOTIFICATION);
		runtimeService.startProcessInstanceByKey("SendSMS", processVariables);
		
		List<HistoricDetail> historyVariables = historyService.
				createHistoricDetailQuery().
				variableUpdates().
				orderByVariableName().
				asc().
				list();
		
		assertNotNull(historyVariables);
		assertEquals(3, historyVariables.size());
		
		for(HistoricDetail hd : historyVariables) {
			HistoricVariableUpdate hvu = (HistoricVariableUpdate)hd;
			if(hvu.getVariableName().equals("message")) {
				assertEquals("Hello World!", (String)hvu.getValue());
				System.out.println(hvu.getValue());
			}
			if(hvu.getVariableName().equals("receivers")) {
				List<String> hvuList = null;
				if(hvu.getValue() instanceof List)
					hvuList = (List<String>)hvu.getValue();
				assertEquals(receivers, hvuList);
				System.out.println(hvu.getValue());
			}
			if(hvu.getVariableName().equals("messageType")) {
				assertEquals(MessageType.NOTIFICATION, hvu.getValue());
				System.out.println(hvu.getValue());
			}
		}
	}

}
