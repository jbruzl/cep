package cz.muni.fi.cep.activiti.notification.service;

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
import cz.muni.fi.cep.api.DTO.CepGroup;
import cz.muni.fi.cep.api.form.CepFormData;
import cz.muni.fi.cep.api.form.CepFormProperty;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.core.servicemanager.AbstractCepProcessService;

/**
 * Service class for BPMN diagram Inform Citizens.
 * 
 * All interaction with Inform Citizens process should be done thru this service.
 * 
 * @author Jan Bruzl
 */
@Service
@PropertySource("classpath:config/application-notify.properties")
public class InformCitizensService extends AbstractCepProcessService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SubscriptionService subscriptionService;

	@Value("${cep.informcitizens.publishercode}")
	private String publisherCode;
	
	@Value("${cep.radio.broadcast.url}")
	private String broadcastUrl;
	
	private final String broadcastUrlKey = "cep.radio.broadcast.url";

	@Autowired
	public InformCitizensService(
			@Value("${cep.informcitizens.process.key}") String processKey,
			@Value("${cep.informcitizens.process.name}") String processName,
			@Value("${cep.informcitizens.key}") String key,
			@Value("${cep.informcitizens.name}") String name,
			@Value("${cep.informcitizens.description}") String description,
			InformCitizensHistoryService historyService) {
		cepHistoryService = historyService;
		this.processKey = processKey;
		this.processName = processName;
		this.key = key;
		this.name = name;
		this.description = description;
	}

	@PostConstruct
	public void init() {
		logger.info("Initialising InformCitizens service");

		CepGroup cepGroup = new CepGroup();
		cepGroup.setCode("mayor");
		cepGroup.setName("Starosta");
		cepGroup.setType("Proces");
		if (identityService.getGroupByCode(cepGroup.getCode()) == null)
			identityService.createGroup(cepGroup);

		processServiceManager.registerService(this);
		
		configurationManager.setKey(broadcastUrlKey, broadcastUrl);

		subscriptionService.register(publisherCode);
		logger.debug("Publisher {} registered", publisherCode);

		if (repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult() == null)
			repositoryService.createDeployment()
					.addClasspathResource("diagrams/InformCitizens.bpmn")
					.deploy();
		logger.debug("Process InformCitizens deployed");

		processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult();
		if (processDefinition != null)
			logger.debug("Process definition obtained");
		else
			logger.warn("Process definition could not be obtained");

		logger.info("InformCitizens service initialised");
	}

	public String getPublisherCode() {
		return publisherCode;
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

		boolean radio = false;
		boolean sms = false;
		boolean email = false;

		for (FormProperty fp : data.getFormProperties()) {
			if (fp instanceof CepFormProperty) {
				switch (fp.getId()) {
				case "sendRadio":
					radio = (boolean) ((CepFormProperty) fp).getInput();
					break;

				case "sendSMS":
					sms = (boolean) ((CepFormProperty) fp).getInput();
					break;

				case "sendEmail":
					email = (boolean) ((CepFormProperty) fp).getInput();
					break;

				}
			}
		}

		params.put("sendRadio", radio);
		params.put("sendSMS", sms);
		params.put("sendEmail", email);

		identityService.setAuthenticatedUserId(user.getUsername());
		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				processDefinition.getKey(), params);
		identityService.setAuthenticatedUserId(null);
		logger.info("Process InformCitizens started");
		return pi;
	}

	@Override
	public String complete(String taskId, CepFormData data) {
		// ověřit, taskId patří této servise
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
				case "emailMessage":
					String emailMessage = (String) cepFormProperty.getInput();
					variables.put("emailMessage", emailMessage);
					break;
				}
			}
		}
		String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
		taskService.complete(taskId, variables);
		
		return processInstanceId;
	}
}
