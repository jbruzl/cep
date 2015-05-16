package cz.muni.fi.cep.activiti.warning.chmi.service;

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

import cz.muni.fi.cep.activiti.warning.chmi.tasks.EvaluateWarningReport;
import cz.muni.fi.cep.activiti.warning.chmi.tasks.ObtainWeatherReport;
import cz.muni.fi.cep.api.DTO.CepGroup;
import cz.muni.fi.cep.api.form.CepFormData;
import cz.muni.fi.cep.api.form.CepFormProperty;
import cz.muni.fi.cep.api.services.subscriptions.SubscriptionService;
import cz.muni.fi.cep.core.servicemanager.AbstractCepProcessService;

/**
 * Service class for BPMN diagram Warning.
 * 
 * All interaction with Inform Citizens process should be done thru this
 * service.
 * 
 * @author Jan Bruzl
 */
@Service
@PropertySource("classpath:config/application-warning.properties")
public class WarningService extends AbstractCepProcessService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SubscriptionService subscriptionService;

	@Value("${cep.warning.publishercode}")
	private String publisherCode;

	@Value("${cep.radio.broadcast.url}")
	private String broadcastUrl;

	@Value("${cep.warning.countryCode}")
	private String countryCode;

	@Value("${cep.warning.regionCode}")
	private String regionCode;

	@Value("${cep.warning.chmi.url}")
	private String chmiUrl;

	@Autowired
	public WarningService(
			@Value("${cep.warning.process.key}") String processKey,
			@Value("${cep.warning.process.name}") String processName,
			@Value("${cep.warning.key}") String key,
			@Value("${cep.warning.name}") String name,
			@Value("${cep.warning.description}") String description,
			WarningHistoryService historyService) {
		cepHistoryService = historyService;
		this.processKey = processKey;
		this.processName = processName;
		this.key = key;
		this.name = name;
		this.description = description;
	}

	@PostConstruct
	public void init() {
		logger.info("Initialising Warning service");

		CepGroup cepGroup = new CepGroup();
		cepGroup.setCode("mayor");
		cepGroup.setName("Starosta");
		cepGroup.setType("Proces");
		if (identityService.getGroupByCode(cepGroup.getCode()) == null)
			identityService.createGroup(cepGroup);

		processServiceManager.registerService(this);

		configurationManager.setKey(EvaluateWarningReport.countryCodeKey,
				countryCode);
		configurationManager.setKey(EvaluateWarningReport.regionCodeKey,
				regionCode);
		configurationManager.setKey(ObtainWeatherReport.chmiUrlKey, chmiUrl);

		subscriptionService.register(publisherCode);
		subscriptionService.register("Varování pro obèany");
		logger.debug("Publisher {} registered", publisherCode);

		if (repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult() == null)
			repositoryService.createDeployment()
					.addClasspathResource("diagrams/Warning.bpmn").deploy();
		logger.debug("Process Warning deployed");

		processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processKey).singleResult();
		if (processDefinition != null)
			logger.debug("Process definition obtained");
		else
			logger.warn("Process definition could not be obtained");

		logger.info("Warning service initialised");
	}

	public String getPublisherCode() {
		return publisherCode;
	}

	@Override
	public CepFormData getStartForm() {
		CepFormData startForm = super.getStartForm();
		startForm.getFormProperties().clear();
		return startForm;
	}

	@Override
	public ProcessInstance runProcess(CepFormData data) {
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		if (!canStart(user)) {
			return null;
		}

		identityService.setAuthenticatedUserId(user.getUsername());
		ProcessInstance pi = runtimeService
				.startProcessInstanceByKey(processDefinition.getKey());
		identityService.setAuthenticatedUserId(null);
		logger.info("Process Warning started");
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

				case "decideInformCitizens":
					Boolean decision = (boolean) cepFormProperty.getInput();
					variables.put("decideInformCitizens", decision);
					break;
				case "sendRadio":
					Boolean radio = (boolean) ((CepFormProperty) fp).getInput();
					variables.put("sendRadio", radio);
					break;

				case "sendSMS":
					Boolean sms = (boolean) ((CepFormProperty) fp).getInput();
					variables.put("sendSMS", sms);
					break;

				case "sendEmail":
					Boolean email = (boolean) ((CepFormProperty) fp).getInput();
					variables.put("sendEmail", email);
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
