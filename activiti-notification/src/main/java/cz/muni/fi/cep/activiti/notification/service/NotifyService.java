package cz.muni.fi.cep.activiti.notification.service;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import cz.muni.fi.cep.core.subscriptions.api.SubscriptionService;

/**
 * Service class for BPMN diagram Notify.
 * 
 * All interaction with Notify process should be done thru this service.
 * 
 * @author Jan Bruzl
 */

public class NotifyService {
	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	private static final String publisherCode = "Notify";
	
	private ProcessDefinition processDefinition;
	
	public NotifyService() {
		processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("Notify").singleResult();
		registerService();
	}
	
	private void registerService() {
		subscriptionService.register(publisherCode);
	}
	
	public void unregisterService() {
		subscriptionService.unregister(publisherCode);
	}
	

	public ProcessInstance startTask(String templateKey, Map<String, String> message) {
		
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);
		params.put("templateKey", templateKey);
		params.put("templateVariable", message);

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(processDefinition.getKey(), params);
		
		return pi;
	}
	
	public void getStartForm() {
		formService.getStartFormData(processDefinition.getId());		
	}
}
