package cz.muni.fi.cep.activiti.warninformcitizens.service;

import java.io.File;
import java.util.GregorianCalendar;
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

import cz.muni.fi.cep.activiti.notification.messages.FileRadioMessage;
import cz.muni.fi.cep.activiti.notification.tasks.BroadcastTask;
import cz.muni.fi.cep.activiti.notification.tasks.SendSMSTask;
import cz.muni.fi.cep.api.DTO.CepGroup;
import cz.muni.fi.cep.api.form.CepFormData;
import cz.muni.fi.cep.api.form.CepFormProperty;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.core.servicemanager.AbstractCepProcessService;

/**
 * Service class for Warn and inform citizens BPMN diagram.
 * 
 * All interaction with Warn and inform citizens process should be done thru
 * this service.
 * 
 * @author Jan Bruzl
 */
@Service
@PropertySource("classpath:config/application-warnInformCitizens.properties")
public class WarnInformCitizensService extends AbstractCepProcessService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	@Value("${cep.warnInformCitizens.publishercode}")
	private String publisherCode;
	
	@Value("${cep.radio.broadcast.url}")
	private String broadcastUrl;
	
	@Value("${cep.warnInformCitizens.sms.login}")
	private String login;
	
	@Value("${cep.warnInformCitizens.sms.password}")
	private String password;
	
	

	@Autowired
	public WarnInformCitizensService(
			@Value("${cep.warnInformCitizens.process.key}") String processKey,
			@Value("${cep.warnInformCitizens.process.name}") String processName,
			@Value("${cep.warnInformCitizens.key}") String key,
			@Value("${cep.warnInformCitizens.name}") String name,
			@Value("${cep.warnInformCitizens.description}") String description,
			WarnInformCitizensHistoryService historyService) {
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

		CepGroup cepGroup = new CepGroup();
		cepGroup.setCode("mayor");
		cepGroup.setName("Starosta");
		cepGroup.setType("Proces");
		if (identityService.getGroupByCode(cepGroup.getCode()) == null)
			identityService.createGroup(cepGroup);

		configurationManager.setKey(BroadcastTask.broadcastUrlKey, broadcastUrl);

		configurationManager.setKey(SendSMSTask.loginKey, login);
		configurationManager.setKey(SendSMSTask.passwordKey, password);
		
		subscriptionService.register(publisherCode);
		logger.debug("Publisher {} registered", publisherCode);

		processServiceManager.registerService(this);

		if (repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult() == null)
			repositoryService
					.createDeployment()
					.addClasspathResource(
							"diagrams/Warn and inform citizens.bpmn").deploy();
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
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		if (!canStart(user)) {
			return null;
		}
		
		HashMap<String, Object> params = new HashMap<>();
		params.put("publisherCode", publisherCode);
		
		identityService.setAuthenticatedUserId(user.getUsername());
		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				processDefinition.getKey(), params);
		identityService.setAuthenticatedUserId(null);
		logger.info("Process Warn and inform citizens started");
		return pi;
	}

	@Override
	public String complete(String taskId, CepFormData data) {
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();

		HashMap<String, Object> variables = new HashMap<>();

		for (FormProperty fp : data.getFormProperties()) {
			if (fp instanceof CepFormProperty) {
				CepFormProperty cepFormProperty = (CepFormProperty) fp;
				switch (fp.getId()) {
				case "radioMessage":
					File f = new File((String) cepFormProperty.getInput());

					if (!f.exists())
						continue;

					FileRadioMessage frm = new FileRadioMessage();
					frm.setAudioFileName(f.getPath());
					frm.setAuthor(user.getUsername());
					frm.setRecordDate(GregorianCalendar.getInstance());
					variables.put("radioMessage", frm);
					break;

				case "smsMessage":
					String smsMessage = (String) cepFormProperty.getInput();
					variables.put("smsMessage", smsMessage);
					break;
				case "checkResponse":
					Boolean checkResponse = (boolean) ((CepFormProperty) fp).getInput();
					variables.put("checkResponse", checkResponse);
					break;
				}
			}
		}
		String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
		taskService.complete(taskId, variables);
		
		return processInstanceId;
	}
}
