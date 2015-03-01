package cz.muni.fi.cep.activiti.radio.service;

import java.io.File;
import java.util.GregorianCalendar;
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

import cz.muni.fi.cep.activiti.radio.messages.FileRadioMessage;
import cz.muni.fi.cep.api.DTO.CepGroup;
import cz.muni.fi.cep.api.DTO.forms.CepFormData;
import cz.muni.fi.cep.api.DTO.forms.CepFormProperty;
import cz.muni.fi.cep.api.services.servicemanager.CepProcessService;
import cz.muni.fi.cep.core.servicemanager.AbstractCepProcessService;

/**
 * Service class for BPMN diagram. Implements {@link CepProcessService}.
 * 
 * All interaction with process should be done thru this service.
 * 
 * @author Jan Bruzl
 */
@Service
@PropertySource("classpath:config/application-radio.properties")
public class BroadcastMessageService extends AbstractCepProcessService {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${cep.radio.broadcast.url}")
	private String broadcastUrl;

	
	
	@Autowired
	public BroadcastMessageService(@Value("${cep.radio.process.key}") String processKey,
			@Value("${cep.radio.process.name}") String processName,
			@Value("${cep.radio.key}") String key,
			@Value("${cep.radio.name}") String name,
			@Value("${cep.radio.description}") String description,
			BroadcastMessageHistoryService broadcastMessageHistoryService) {
		cepHistoryService = broadcastMessageHistoryService;
		this.processKey = processKey;
		this.processName = processName;
		this.key = key;
		this.name = name;
		this.description = description;	
	}

	@PostConstruct
	public void init() {
		logger.info("Initialising Broadcast Message service");

		CepGroup cepGroup = new CepGroup();
		cepGroup.setCode("radio");
		cepGroup.setName("Rozhlas");
		cepGroup.setType("Proces");
		if(identityService.getGroupByCode(cepGroup.getCode())==null)
			identityService.createGroup(cepGroup);
		
		processServiceManager.registerService(this);

		configurationManager.setKey("cep.radio.broadcast.url", broadcastUrl);

		if (repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult() == null)
			repositoryService.createDeployment()
					.addClasspathResource("diagrams/BroadcastMessage.bpmn")
					.deploy();
		logger.debug("Process {} deployed", processKey);

		processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult();
		if (processDefinition != null)
			logger.debug("Process definition obtained");
		else
			logger.warn("Process definition could not be obtained");

		logger.info("Broadcast Message service initialised");
	}
	
	@Override
	public ProcessInstance runProcess(CepFormData data) {
		File f = null;
		for (FormProperty fp : data.getFormProperties()) {
			if (fp instanceof CepFormProperty) {
				switch (fp.getId()) {
				case "radioMessage":
					f = new File((String) ((CepFormProperty) fp).getInput());
					break;
				}
			}
		}

		if (f == null)
			return null;

		if (!f.exists())
			return null;

		final String user = ((User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal()).getUsername();

		FileRadioMessage frm = new FileRadioMessage();
		frm.setAudioFileName(f.getPath());
		frm.setAuthor(user);
		frm.setRecordDate(GregorianCalendar.getInstance());

		HashMap<String, Object> params = new HashMap<>();
		params.put("radioMessage", frm);

		identityService.setAuthenticatedUserId(user);
		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				processDefinition.getKey(), params);
		identityService.setAuthenticatedUserId(null);

		logger.info("Process {} started", processKey);
		return pi;
	}

	@Override
	public void complete(Task task, CepFormData data) {
		//Is not needed in this process
		logger.error("Called not needed method: complete() in {}", getClass());
	}

}
