package cz.muni.fi.cep.activiti.staffcall.service;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import cz.muni.fi.cep.api.DTO.CepGroup;
import cz.muni.fi.cep.api.form.CepFormData;
import cz.muni.fi.cep.api.form.CepFormProperty;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.core.servicemanager.AbstractCepProcessService;

/**
 * Service class for BPMN diagram.
 * 
 * All interaction with Example process should be done thru this service.
 * 
 * @author Jan Bruzl
 */
@Service
@PropertySource("classpath:config/application-emergencyStaffCall.properties")
public class EmergencyStaffCallService extends AbstractCepProcessService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${cep.emergencyStaffCall.publishercode}")
	private String publisherCode;
	
	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	public EmergencyStaffCallService(@Value("${cep.emergencyStaffCall.process.key}") String processKey,
			@Value("${cep.emergencyStaffCall.process.name}") String processName,
			@Value("${cep.emergencyStaffCall.key}") String key,
			@Value("${cep.emergencyStaffCall.name}") String name,
			@Value("${cep.emergencyStaffCall.description}") String description,
			EmergencyStaffCallHistoryService historyService) {
		cepHistoryService = historyService;
		this.processKey = processKey;
		this.processName = processName;
		this.key = key;
		this.name = name;
		this.description = description;
	}

	@PostConstruct
	public void init() {
		logger.info("Initialising EmergencyStaffCall service");

		CepGroup cepGroup = new CepGroup();
		cepGroup.setCode("mayor");
		cepGroup.setName("Starosta");
		cepGroup.setType("Proces");
		if (identityService.getGroupByCode(cepGroup.getCode()) == null)
			identityService.createGroup(cepGroup);
		
		processServiceManager.registerService(this);
		
		subscriptionService.register(publisherCode);
		logger.debug("Publisher {} registered", publisherCode);
		
		if (repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult() == null)
			repositoryService.createDeployment()
					.addClasspathResource("diagrams/EmergencyStaffCall.bpmn").deploy();
		logger.debug("Process EmergencyStaffCall deployed");

		processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult();
		if (processDefinition != null)
			logger.debug("Process definition obtained");
		else
			logger.warn("Process definition could not be obtained");

		logger.info("EmergencyStaffCall service initialised");
	}

	@Override
	public ProcessInstance runProcess(CepFormData data) {
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		if (!canStart(user)) {
			return null;
		}

		HashMap<String, Object> variables = new HashMap<>();
		variables.put("publisherCode", publisherCode);
		identityService.setAuthenticatedUserId(user.getUsername());
		ProcessInstance pi = runtimeService
				.startProcessInstanceByKey(processDefinition.getKey(), variables);
		identityService.setAuthenticatedUserId(null);
		logger.info("Process EmergencyStaffCall started");
		return pi;
	}


	@Override
	public String complete(String taskId, CepFormData data) {
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		if (!canStart(user)) {
			return null;
		}

		HashMap<String, Object> variables = new HashMap<>();

		for (FormProperty fp : data.getFormProperties()) {
			if (fp instanceof CepFormProperty) {
				CepFormProperty cepFormProperty = (CepFormProperty) fp;
				switch (fp.getId()) {

				case "meetingTime":
					String meetingTime = (String) cepFormProperty.getInput();
					variables.put("meetingTime", meetingTime);
					break;
				case "meetingPlace":
					String meetingPlace = (String) ((CepFormProperty) fp).getInput();
					variables.put("meetingPlace", meetingPlace);
					break;

				}
			}
		}
		
		String processInstanceId = taskService.createTaskQuery().taskId(taskId)
				.singleResult().getProcessInstanceId();
		taskService.complete(taskId, variables);

		return processInstanceId;
	}
}
