package cz.muni.fi.cep.activiti.notification.service;


import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.api.DTO.CepGroup;
import cz.muni.fi.cep.api.DTO.MessageType;
import cz.muni.fi.cep.api.DTO.forms.CepFormData;
import cz.muni.fi.cep.api.DTO.forms.CepFormProperty;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.core.servicemanager.AbstractCepProcessService;

/**
 * Service class for BPMN diagram Notify.
 * 
 * All interaction with Notify process should be done thru this service.
 * 
 * @author Jan Bruzl
 */
@Service
@PropertySource("classpath:config/application-notify.properties")
public class NotifyService extends AbstractCepProcessService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SubscriptionService subscriptionService;

	@Value("${cep.notify.publishercode}")
	private String publisherCode;
	
	@Value("${cep.notify.enabled}")
	private String defNotificationEnabled;

	@Autowired
	public NotifyService(@Value("${cep.notify.process.key}") String processKey,
			@Value("${cep.notify.process.name}") String processName,
			@Value("${cep.notify.key}") String key,
			@Value("${cep.notify.name}") String name,
			@Value("${cep.notify.description}") String description,
			NotifyHistoryService notifyHistoryService) {
		cepHistoryService = notifyHistoryService;
		this.processKey = processKey;
		this.processName = processName;
		this.key = key;
		this.name = name;
		this.description = description;
	}

	@PostConstruct
	public void init() {
		logger.info("Initialising Notify service");

		CepGroup cepGroup = new CepGroup();
		cepGroup.setCode("notify");
		cepGroup.setName("Notifikace");
		cepGroup.setType("Proces");
		if(identityService.getGroupByCode(cepGroup.getCode())==null)
			identityService.createGroup(cepGroup);
		
		processServiceManager.registerService(this);
		
		subscriptionService.register(publisherCode);
		logger.debug("Publisher {} registered", publisherCode);
		
		configurationManager.setKey("cep.notify.enabled", defNotificationEnabled);

		if (repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult() == null)
			repositoryService.createDeployment()
					.addClasspathResource("diagrams/SendSMS.bpmn")
					.addClasspathResource("diagrams/Notify.bpmn").deploy();
		logger.debug("Process Notify deployed");
		logger.debug("Process SendSMS deployed");

		processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult();
		if (processDefinition != null)
			logger.debug("Process definition obtained");
		else
			logger.warn("Process definition could not be obtained");

		logger.info("Notify service initialised");
	}

	public String getPublisherCode() {
		return publisherCode;
	}

	@Override
	public ProcessInstance runProcess(CepFormData data) {
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);
		params.put("messageType", MessageType.NOTIFICATION);
		params.put("templateKey", "default");
		HashMap<String, String> templateVars = new HashMap<>();
		params.put("templateVariable", templateVars);
		
		String message = null;
		
		for (FormProperty fp : data.getFormProperties()) {
			if (fp instanceof CepFormProperty) {
				switch (fp.getId()) {
				case "message":
					message = (String) ((CepFormProperty) fp).getInput();
					break;
				}
			}
		}
		
		if(message == null)
			return null;
	
		templateVars.put("message", message);

		final String user = ((User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal()).getUsername();
		identityService.setAuthenticatedUserId(user);
		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				processDefinition.getKey(), params);
		identityService.setAuthenticatedUserId(null);
		logger.info("Process Notify started");
		return pi;
	}

	@Override
	public void complete(Task task, CepFormData data) {
		// Is not needed in this process
		logger.error("Called not needed method: complete() in {}", getClass());
	}
}
