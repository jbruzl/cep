#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

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

import cz.muni.fi.cep.api.form.CepFormData;
import ${groupId}.api.DTO.forms.CepFormData;
import ${groupId}.api.DTO.forms.CepFormProperty;
import ${groupId}.core.servicemanager.AbstractCepProcessService;

/**
 * Service class for BPMN diagram.
 * 
 * All interaction with ${processName} process should be done thru this service.
 * 
 * @author Jan Bruzl
 */
@Service
@PropertySource("classpath:config/application-${process}.properties")
public class ${process}Service extends AbstractCepProcessService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public ${process}Service(@Value("${symbol_dollar}{cep.${process}.process.key}") String processKey,
			@Value("${symbol_dollar}{cep.${process}.process.name}") String processName,
			@Value("${symbol_dollar}{cep.${process}.key}") String key,
			@Value("${symbol_dollar}{cep.${process}.name}") String name,
			@Value("${symbol_dollar}{cep.${process}.description}") String description,
			${processName}HistoryService historyService) {
		cepHistoryService = historyService;
		this.processKey = processKey;
		this.processName = processName;
		this.key = key;
		this.name = name;
		this.description = description;
	}

	@PostConstruct
	public void init() {
		logger.info("Initialising ${processName} service");

		processServiceManager.registerService(this);
		
		if (repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult() == null)
			repositoryService.createDeployment()
					.addClasspathResource("diagrams/${processName}.bpmn").deploy();
		logger.debug("Process ${processName} deployed");

		processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult();
		if (processDefinition != null)
			logger.debug("Process definition obtained");
		else
			logger.warn("Process definition could not be obtained");

		logger.info("${processName} service initialised");
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
		logger.info("Process ${processName} started");
		return pi;
	}

	@Override
	public String complete(String taskId, CepFormData data) {
		return null;
	}
}
