package cz.muni.fi.cep.activiti.warninformcitizens.service;

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

import cz.muni.fi.cep.api.form.CepFormData;
import cz.muni.fi.cep.api.form.CepFormProperty;
import cz.muni.fi.cep.core.servicemanager.AbstractCepProcessService;

/**
 * Service class for BPMN diagram.
 * 
 * All interaction with Warn and inform citizens process should be done thru this service.
 * 
 * @author Jan Bruzl
 */
//@Service
@PropertySource("classpath:config/application-warnInformCItizens.properties")
public class warnInformCitizensService extends AbstractCepProcessService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public warnInformCitizensService(@Value("${cep.warnInformCItizens.process.key}") String processKey,
			@Value("${cep.warnInformCItizens.process.name}") String processName,
			@Value("${cep.warnInformCItizens.key}") String key,
			@Value("${cep.warnInformCItizens.name}") String name,
			@Value("${cep.warnInformCItizens.description}") String description,
			warnInformCitizensHistoryService historyService) {
		cepHistoryService = historyService;
		this.processKey = processKey;
		this.processName = processName;
		this.key = key;
		this.name = name;
		this.description = description;
	}

	@PostConstruct
	public void init() {
		logger.info("Initialising Warn and inform citizens service");

		processServiceManager.registerService(this);
		
		if (repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult() == null)
			repositoryService.createDeployment()
					.addClasspathResource("diagrams/Warn and inform citizens.bpmn").deploy();
		logger.debug("Process Warn and inform citizens deployed");

		processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult();
		if (processDefinition != null)
			logger.debug("Process definition obtained");
		else
			logger.warn("Process definition could not be obtained");

		logger.info("Warn and inform citizens service initialised");
	}

	@Override
	public ProcessInstance runProcess(CepFormData data) {
		HashMap<String, Object> params = new HashMap<>();
		
		//Iterate thru input form data
		for (FormProperty fp : data.getFormProperties()) {
			if (fp instanceof CepFormProperty) {
				switch (fp.getId()) {
					default:
						break;
				}
			}
		}
		
		
		final String user = ((User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal()).getUsername();
		identityService.setAuthenticatedUserId(user);
		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				processDefinition.getKey(), params);
		identityService.setAuthenticatedUserId(null);
		logger.info("Process Warn and inform citizens started");
		return pi;
	}

	@Override
	public String complete(String taskId, CepFormData data) {
		return null;
	}
}
